package HTTP;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.function.Consumer;

public class HttpClient {
    private Consumer<String> msgHandler;
    private HttpURLConnection http_conn;
    private URL url;

    public HttpClient( String urlString, String request_method ) throws Exception {
        URI uri = URI.create( urlString );
        url = uri.toURL();
        http_conn = ( HttpURLConnection ) url.openConnection();
        http_conn.setRequestProperty( "Connection", "keep-alive" );
        http_conn.setRequestMethod(request_method);
    }

    public void closePersistentConnection() {
        if ( http_conn != null ) {
            http_conn.disconnect();
            http_conn = null;
            url = null;
        }
    }

    public void send( String msg ) throws Exception {
        if (msg != null && !msg.isEmpty()) {
            http_conn.setDoOutput(true);
            http_conn.setRequestProperty("Content-Type", "text/plain");
            try(OutputStream os = http_conn.getOutputStream()) {
                byte[] input = msg.getBytes("utf-8");
                os.write(input, 0, input.length);           
            }
        }

        int responseCode = http_conn.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(http_conn.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        if (msgHandler != null) {
            msgHandler.accept(response.toString());
        }

    }

    public void setMsgHandler(Consumer<String> handler) {
        this.msgHandler = handler;
    }
}