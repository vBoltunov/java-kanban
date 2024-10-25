package handlers;

import com.sun.net.httpserver.HttpExchange;
import model.Subtask;
import managers.TaskManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SubtaskHandler extends TaskHandler {

    public SubtaskHandler(TaskManager manager) {
        super(manager);
    }

    /* Метод handle() - это переопределение абстрактного метода public abstract void handle()
    интерфейса HttpHandler */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            Pattern pattern = Pattern.compile("/subtasks/(\\d+)");
            Matcher matcher = pattern.matcher(path);

            switch (exchange.getRequestMethod()) {
                case "GET":
                    if (matcher.matches()) {
                        int id = Integer.parseInt(matcher.group(1));
                        handleGetSubtaskById(exchange, id);
                    } else {
                        handleGetSubtasks(exchange);
                    }
                    break;
                case "POST":
                    handlePostSubtask(exchange);
                    break;
                case "DELETE":
                    if (matcher.matches()) {
                        int id = Integer.parseInt(matcher.group(1));
                        handleDeleteSubtask(exchange, id);
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

    private void handleGetSubtasks(HttpExchange exchange) throws IOException {
        List<Subtask> subtasks = manager.getAllSubtasks();
        String jsonResponse = gson.toJson(subtasks);
        sendText(exchange, jsonResponse, 200);
    }

    private void handleGetSubtaskById(HttpExchange exchange, int id) throws IOException {
        Subtask subtask = manager.getSubtaskById(id);
        if (subtask != null) {
            String jsonResponse = gson.toJson(subtask);
            sendText(exchange, jsonResponse, 200);
        } else {
            sendNotFound(exchange);
        }
    }

    private void handlePostSubtask(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        Subtask subtask = gson.fromJson(new InputStreamReader(inputStream), Subtask.class);
        if (subtask.getId() == 0) {
            manager.createSubtask(subtask);
            sendText(exchange, gson.toJson(subtask), 201);
        } else {
            try {
                manager.updateSubtask(subtask);
                sendText(exchange, gson.toJson(subtask), 201);
            } catch (Exception e) {
                sendHasIntersections(exchange);
            }
        }
    }

    private void handleDeleteSubtask(HttpExchange exchange, int id) throws IOException {
        Subtask subtask = manager.getSubtaskById(id);
        if (subtask != null) {
            manager.deleteSubtaskById(id);
            sendText(exchange, "{\"Статус\":\"Подзадача удалена\"}", 200);
        } else {
            sendNotFound(exchange);
        }
    }
}