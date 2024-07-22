package Websocket;

import org.java_websocket.WebSocket;

public interface WsServerMsgHandler {
    
    void handleMsg(WebSocket conn, String msg);

}