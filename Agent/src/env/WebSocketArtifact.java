package Env;

import cartago.*;
import java.net.InetSocketAddress;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketArtifact extends Artifact {
    private MyWebSocketServer server;
    private Map<String, WebSocket> connections = new ConcurrentHashMap<>();

    @OPERATION
    public void init(int port) {
        server = new MyWebSocketServer(new InetSocketAddress(port));
        server.start();
        log("WebSocket server started on port " + port);
    }

    @OPERATION
    public void broadcast(String message) {
        if (server != null) {
            server.broadcast(message);
            log("Broadcasted message: " + message);
        }
    }

    @OPERATION
    public void send(String address, String message) {
        WebSocket conn = connections.get(address);
        if (conn != null && conn.isOpen()) {
            conn.send(message);
            log("Sent message to " + address + ": " + message);
        } else {
            failed("Connection not found or closed: " + address);
        }
    }

    @OPERATION
    public void shutdown() {
        if (server != null) {
            try {
                server.stop();
                log("WebSocket server stopped");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class MyWebSocketServer extends WebSocketServer {
        public MyWebSocketServer(InetSocketAddress address) {
            super(address);
        }

        @Override
        public void onOpen(WebSocket conn, ClientHandshake handshake) {
            String address = conn.getRemoteSocketAddress().toString();
            connections.put(address, conn);
            signal("clientConnected", address);
        }

        @Override
        public void onClose(WebSocket conn, int code, String reason, boolean remote) {
            String address = conn.getRemoteSocketAddress().toString();
            connections.remove(address);
            signal("clientDisconnected", address);
        }

        @Override
        public void onMessage(WebSocket conn, String message) {
            String address = conn.getRemoteSocketAddress().toString();
            signal("messageReceived", address, message);
        }

        @Override
        public void onError(WebSocket conn, Exception ex) {
            signal("error", ex.getMessage());
        }

        @Override
        public void onStart() {
            signal("serverStarted");
        }
    }
}