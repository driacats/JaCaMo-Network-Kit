package Websocket;

// Websockets
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public class WsServer extends WebSocketServer{

	private WsServerMsgHandler msgHandler;

    public WsServer(InetSocketAddress address){
        super(address);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake){
        System.out.println("new connectio to: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote){
        System.out.println("closed " + conn.getRemoteSocketAddress() + " with exit code " + code + " additional info " + reason);
    }

	public void setMsgHandler(WsServerMsgHandler handler){
		this.msgHandler = handler;
	}

    @Override
	public void onMessage(WebSocket conn, String message) {
		if (msgHandler != null){
			msgHandler.handleMsg(conn, message);
		}
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		System.err.println("an error occurred on connection " + conn.getRemoteSocketAddress()  + ":" + ex);
	}
	
	@Override
	public void onStart() {
		System.out.println("server started successfully");
	}

}