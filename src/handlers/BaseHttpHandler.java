package handlers;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler {

    protected void sendText(HttpExchange exchange, String text, int statusCode) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(statusCode, response.length);
        exchange.getResponseBody().write(response);
        exchange.close();
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        String responseText = "{\"Ошибка\":\"Страница не найдена\"}";
        sendText(exchange, responseText, 404);
    }

    protected void sendHasIntersections(HttpExchange exchange) throws IOException {
        String responseText = "{\"Ошибка\":\"Задача пересекается по времени с другими задачами\"}";
        sendText(exchange, responseText, 406);
    }
}
