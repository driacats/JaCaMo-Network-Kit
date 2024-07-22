# JaCaMo Network Kit

This repo aims to create a basic connection kit for the JaCaMo framework.
Protols implemented:
 - WebSocket
 - HTTP

## Basic Concepts
A JaCaMo agent or environment can be extended using Artifacts.
You can think of an artifact as a screwdriver, without it you can't tighten a screw, you hold it in your hand but it is not part of you or your mind.
Similarly in the `Artifacts` folder you can find two tools that the agent can “hold” to communicate with the outside world.

The repo is divided as follows:

 - Artifacts: contains all the Artifacts implementations
    - HTTP: contains the HTTP artifact;
    - WebSocket: contains the WebSocket artifact;

 - Agent: contains an example agent that can use both artifacts;
 - Instrumentations: here you can find the other hand connections for:
    - Python (HTTP/WebSocket)
    - Unity (HTTP/WebSocket)
    - Rasa (WebSocket)

Now we will present the content of the Artifacts folder and then the connections that are already done. Fill free to collaborate!

## Artifacts

### Websocket Connection

This folder contains the artifacts that are needed to create a WebSocket connection for a JaCaMo agent.

The artifact is written using the library `org.java-websocket`  from the Maven Central Repository. First of all, you have to add it to the `build.gradle` in the `dependencies` section:
```gradle
dependencies {
    // https://mvnrepository.com/artifact/org.java-websocket/Java-WebSocket
    implementation group: 'org.java-websocket', name: 'Java-WebSocket', version: '1.5.6'
}
```

In the folder there are all the artifacts for creating both a client and a server connection.
To use the artifacts copy the WebSocket folder in the in your project where you want and add the path in the `gradle.build` file. Normally the Artifacts are inside the `env` folder, following the structure:

```bash
AgentFolder
|-- src/
|   |-- agt/
|   |-- env/
|   |   |-- Websocket/
```

*A technical important detail:* Java classes cannot extend multiple classes. The WebSocket Client and Server classes need to extend the `org.gava-websocket` class, while the artifacts must extend `Artifact`. To put things together, we had to make a bridge. You can send messages with the `send` method of the websocket connection object, while for receiving messages the websocket connection object does not implement an handler function. The handler function is only an interface and it is implemented in the Agent artifact that extends `Artifact`.

#### Client Connection

To create a client connection with your artifact you should import the `WsClient` and `WsClientMsgHandler`. `WsClient` creates the connection with which you can send and receive messages, `WsClientMsgHandler` is a method to handle the incoming messages. Having it inside the artifact we can use the incoming messages to add beliefs to the agent or make it react in some way: the `handleMsg` method can call `@OPERATION` methods.

```java
import cartago.*;

import Websocket.WsClient;
import Websocket.WsClientMsgHandler;
import java.net.URI;
import java.net.URISyntaxException;

public class ARTIFACT_NAME extends Artifact implements WsClientMsgHandler {

  private int port = YOUR_PORT; // Websocket Server port
  private String host = YOUR_HOST; // Websocket Server Address
  private WsClient conn; // Websocket Client->Server connection

  @OPERATION
  public void init() throws URISyntaxException {
    conn = new WsClient( new URI( "ws://" + host + ":" + port ) );
    conn.setMsgHandler( this::handleMsg );
    conn.connect();
  }

  @Override
  public synchronized void handleMsg( String msg ) {
  	// Handle the message as you want
  }
}
```
This way, the artifact used by the agent creates a new connection ad `init()` and it handles the messages with its own function, making the messages received open to the agent.
To send a message the agent can

```java
conn.send(YOUR_MESSAGE)
```
`YOUR_MESSAGE` should be a string.
And you are done! Now your agent can receive and send messages as a Websocket client!

#### Server Connection

To create a client connection with your artifact you should import the `WsServer` and `WsServerMsgHandler`. `WsServer` creates the connection with which you can send and receive messages, `WsServerMsgHandler` is a method to handle the incoming messages. Having it inside the artifact we can use the incoming messages to add beliefs to the agent or make it react in some way: the `handleMsg` method can call `@OPERATION` methods.

```java
import cartago.*;

import Websocket.WsServer;
import Websocket.WsServerMsgHandler;
import org.java_websocket.WebSocket;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;

public class sampleWsServer extends Artifact implements WsServerMsgHandler {

  private final int port = 9080; // Websocket Server port
  private final String host = "localhost"; // Websocket Server Address
  private WsServer conn; // Websocket Server connection

  // init creates the connection between each agent and the server
  @OPERATION
  public void init() throws URISyntaxException {
    conn = new WsServer( new InetSocketAddress( "ws://" + host, port ) );
    conn.setMsgHandler( this::handleMsg ); 
  }

  @Override
  public synchronized void handleMsg( WebSocket conn, String msg ) {
  	// Handle the message as you want
  }
}

```

**TODO:** for the moment the Server is able to receive but has no broadcast or send methods extended.

And you are done! Now your agent can receive and send messages as a Websocket server!

### HTTP Connection

