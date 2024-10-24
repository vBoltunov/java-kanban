package handlers;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.google.gson.Gson;
import managers.Managers;
import model.Task;
import managers.TaskManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    protected final Gson gson = Managers.getGson();
    protected final TaskManager manager;

    public TaskHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            Pattern pattern = Pattern.compile("/tasks/(\\d+)");
            Matcher matcher = pattern.matcher(path);

            switch (exchange.getRequestMethod()) {
                case "GET":
                    if (matcher.matches()) {
                        int id = Integer.parseInt(matcher.group(1));
                        handleGetTaskById(exchange, id);
                    } else {
                        handleGetTasks(exchange);
                    }
                    break;
                case "POST":
                    handlePostTask(exchange);
                    break;
                case "DELETE":
                    if (matcher.matches()) {
                        int id = Integer.parseInt(matcher.group(1));
                        handleDeleteTask(exchange, id);
                    } else {
                        sendNotFound(exchange);
                    }
                    break;
                default:
                    sendNotFound(exchange);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendNotFound(exchange);
        }
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        List<Task> tasks = manager.getAllTasks();
        String jsonResponse = gson.toJson(tasks);
        sendText(exchange, jsonResponse, 200);
    }

    private void handleGetTaskById(HttpExchange exchange, int id) throws IOException {
        Task task = manager.getTaskById(id);
        if (task != null) {
            String jsonResponse = gson.toJson(task);
            sendText(exchange, jsonResponse, 200);
        } else {
            sendNotFound(exchange);
        }
    }

    private void handlePostTask(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        Task task = gson.fromJson(new InputStreamReader(inputStream), Task.class);
        if (task.getId() == 0) {
            manager.createTask(task);
            sendText(exchange, gson.toJson(task), 201);
        } else {
            try {
                manager.updateTask(task);
                sendText(exchange, gson.toJson(task), 201);
            } catch (Exception e) {
                sendHasIntersections(exchange);
            }
        }
    }

    private void handleDeleteTask(HttpExchange exchange, int id) throws IOException {
        Task task = manager.getTaskById(id);
        if (task != null) {
            manager.deleteTaskById(id);
            sendText(exchange, "{\"Статус\":\"Задача удалена\"}", 200);
        } else {
            sendNotFound(exchange);
        }
    }
}