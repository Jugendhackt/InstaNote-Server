import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {
	private Main() {
		try {
			HttpServer httpServer = HttpServer.create(new InetSocketAddress(2000), 0);


			httpServer.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new Main();
	}


	void write(String text, int rCode, HttpExchange exchange) throws IOException {
		Headers responseHeaders = exchange.getResponseHeaders();
		responseHeaders.add("Content-Type", "application/json");

		exchange.sendResponseHeaders(rCode, text.length());
		exchange.getResponseBody().write(text.getBytes());
		exchange.getResponseBody().close();
	}


}

