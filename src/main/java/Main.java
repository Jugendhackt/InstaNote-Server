import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Main {
    private Main() {
        try {
            HttpServer httpServer = HttpServer.create(new InetSocketAddress(1337), 0);
            httpServer.createContext("/search", new searchHandler());
            httpServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Log.status("starting http-server...");
        new Main();
        Log.success("started http-server");
    }
    
    
    private class searchHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            HashMap<String, String> query = queryToMap(exchange.getRequestURI().getQuery());
    
            String keyword = query.get("key");
            String language = query.get("lang");
            
            Log.warning("+++++ Received a request for "+keyword.toUpperCase()+" +++++");
            if (keyword == null || keyword.toLowerCase().contentEquals("null")) {
                Log.critical("No ID found".toUpperCase());
                Log.status("Aborted request.");
                write("{\"error\": 400}", 400, exchange);
            } else {
                try {
                    String presentationData = DataHandler.getPresentationData(keyword, language);
                    if (presentationData != null) {
                        write(presentationData, 200, exchange);
                        Log.success("+++++ "+keyword.toUpperCase()+" request handeled +++++".toUpperCase() );
                    } else {
                        Log.critical("Missing Data".toUpperCase());
                        write("{\"error\": 400}", 400, exchange);
                        Log.status("Aborted request.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private HashMap<String, String> queryToMap(String query) {
        LinkedHashMap<String, String> result = new LinkedHashMap<>();

        for (String arg : query.split("&")) {
            String[] keyAndValue = arg.split("=");
            result.put(keyAndValue[0], keyAndValue[1]);
        }
        return result;
    }

    private void write(String text, int rCode, HttpExchange exchange) throws IOException {
        Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.add("Content-Type", "application/json");
        responseHeaders.add("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(rCode, text.getBytes().length);
        exchange.getResponseBody().write(text.getBytes());
        exchange.getResponseBody().close();
    }
}

