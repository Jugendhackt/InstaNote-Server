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

            HttpServer httpServer = HttpServer.create(new InetSocketAddress(2000), 0);

            httpServer.createContext("/search", new searchHandler());

            httpServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Log.status("starting	http-server");
        new Main();
        Log.success("started		http-server");
    }

    private class searchHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            HashMap<String, String> query = queryToMap(exchange.getRequestURI().getQuery());

            Log.status("Received a request for a search");
            write(DataHandler.convertJSON(DataHandler.queryCall(query.get("searchword"), query.get("lang"))).toString(), 200, exchange);

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

