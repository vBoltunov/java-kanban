package handlers;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.google.gson.Gson;
import managers.HistoryManager;
import managers.Managers;
import model.Task;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {

    protected final Gson gson = Managers.getGson();
    private final HistoryManager manager;

    public HistoryHandler(HistoryManager manager) {
        this.manager = manager;
    }

    /* Метод handle() - это переопределение абстрактного метода public abstract void handle()
    интерфейса HttpHandler */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            List<Task> history = manager.getHistory();
            List<Integer> historyIds = history.stream().map(Task::getId).toList();
            String jsonResponse = gson.toJson(historyIds);
            sendText(exchange, jsonResponse, 200);
        } else {
            sendNotFound(exchange);
        }
    }
}