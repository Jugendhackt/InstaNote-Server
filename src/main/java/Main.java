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

            httpServer.createContext("/getData", new GetDataHandler());

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
	
    private class GetDataHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            HashMap<String, String> query = queryToMap(exchange.getRequestURI().getQuery());


        }
    }

    private HashMap<String, String> queryToMap(String query) {
        LinkedHashMap<String, String> result = new LinkedHashMap<>();

        for(String arg : query.split("&")){
            String[] keyAndValue = arg.split("=");
            result.put(keyAndValue[0], keyAndValue[1]);
        }
        return result;
    }
    


    void write(String text, int rCode, HttpExchange exchange) throws IOException {
        Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.add("Content-Type", "application/json");

        exchange.sendResponseHeaders(rCode, text.length());
        exchange.getResponseBody().write(text.getBytes());
        exchange.getResponseBody().close();
    }


}

