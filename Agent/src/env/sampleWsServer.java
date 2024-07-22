package Env;

// jacamo imports
import cartago.*;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.parser.ParseException;
import jason.asSyntax.Literal;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.Structure;

// Connection imports
import Websocket.WsServer;
import Websocket.WsServerMsgHandler;
import org.java_websocket.WebSocket;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;

public class sampleWsServer extends Artifact implements WsServerMsgHandler {
    
    private final int port = 9080; 				// Websocket Server port
	private final String host = "localhost";	// Websocket Server Address
	private WsServer conn; 						// Websocket Client->Server connection

    // init creates the connection between each agent and the server
    @OPERATION
    public void init() throws URISyntaxException {
        conn = new WsServer( new InetSocketAddress( "ws://" + host, port ) );
        // With the line below we set the handler of the income messages with an OPERATION function
        // This way, we can interact with the agent directly via websocket
        conn.setMsgHandler( this::handleMsg ); 
        // conn.connect();
        // lock is true when the environment is updating the map beliefs
    }
    
    // @OPERATION
    // public void send( String msg ) {
    //     conn.send( msg );
    // }

    @Override
	public void handleMsg( WebSocket conn, String msg ) {
        System.out.println( "Received: " + msg );
    }
}
