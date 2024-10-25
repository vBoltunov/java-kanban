package handlers;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.google.gson.Gson;
import managers.Managers;
import managers.TaskManager;
import model.Task;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {

    protected final Gson gson = Managers.getGson();
    private final TaskManager manager;

    public PrioritizedHandler(TaskManager manager) {
        this.manager = manager;
    }

    /* Метод handle() - это переопределение абстрактного метода public abstract void handle()
    интерфейса HttpHandler */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            List<Task> prioritizedTasks = manager.getPrioritizedTasks();
            List<Integer> prioritizedTaskIds = prioritizedTasks.stream().map(Task::getId).toList();
            String jsonResponse = gson.toJson(prioritizedTaskIds);
            sendText(exchange, jsonResponse, 200);
        } else {
            sendNotFound(exchange);
        }
    }
}