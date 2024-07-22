package Env;

// jacamo imports
import cartago.*;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.parser.ParseException;
import jason.asSyntax.Literal;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.Structure;

import HTTP.HttpClient;

public class sampleHttpClient extends Artifact {
    private final int port = 8080; 				// HTTP Server port
    private final String host = "localhost";	// HTTP Server Address
    private HttpClient conn; 					// HTTP Client->Server connection

    // init creates the connection between each agent and the server
    @OPERATION
    public void init() throws Exception {
        conn = new HttpClient( "http://" + host + ":" + port, "POST" );
        // With the line below we set the handler of the income messages with an OPERATION function
        // This way, we can interact with the agent directly via HTTP
        conn.setMsgHandler( this::handleMsg );
    }

    @OPERATION
    public void send( String msg ) throws Exception {
        conn.send( msg );
    }

    public void handleMsg( String msg ) {
        System.out.println( "Received: " + msg );
    }
}
