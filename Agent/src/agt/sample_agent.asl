+!start_ws_client
    :   true
    <-  .print("Starting WebSocket connection");
        .my_name(Me);
        .concat(Me, "ws", WsArtName);
        makeArtifact(WsArtName, "Env.sampleWsClient", [], WsArtId);
        focus(WsArtId);
        .wait(2000);
        send("Hello world!").

+!start_ws_server
    :   true
    <-  .print("Starting WebSocket connection");
        .my_name(Me);
        .concat(Me, "ws", WsArtName);
        makeArtifact(WsArtName, "Env.WebSocketArtifact", [8080], WsArtId);
        focus(WsArtId);
        .wait(2000).

+!start_http_client
    :   true
    <-  .print("Starting HTTP connection");
        .my_name(Me);
        .concat(Me, "http", HttpArtName);
        makeArtifact(HttpArtName, "Env.sampleHttpClient", [], HttpArtId);
        focus(HttpArtId);
        .wait(2000);
        send("Hello world!").

+!start_websocket_server
   <- makeArtifact("websocket","WebSocketArtifact",[],ArtId);
      focus(ArtId);
      init(8080).

+!send_to_client(Address, Msg)
   <- send(Address, Msg).

+clientConnected(Address)
   <- .print("New client connected: ", Address);
      send(Address, "Welcome to the server!").

+messageReceived(Address, Message)
   <- .print("Received message from ", Address, ": ", Message);
        .concat("Message received: ", Message, Msg);
      send(Address, Msg).