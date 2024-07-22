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
        makeArtifact(WsArtName, "Env.sampleWsServer", [], WsArtId);
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