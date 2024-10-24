package handlers;

import com.sun.net.httpserver.HttpExchange;
import model.Epic;
import managers.TaskManager;
import model.Subtask;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EpicHandler extends TaskHandler {

    public EpicHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            Pattern pattern = Pattern.compile("/epics/(\\d+)(/subtasks)?");
            Matcher matcher = pattern.matcher(path);

            if ("GET".equals(exchange.getRequestMethod())) {
                if (matcher.matches()) {
                    int id = Integer.parseInt(matcher.group(1));
                    if (matcher.group(2) != null) {
                        handleGetEpicSubtasks(exchange, id);
                    } else {
                        handleGetEpicById(exchange, id);
                    }
                } else {
                    handleGetEpics(exchange);
                }
            } else if ("POST".equals(exchange.getRequestMethod())) {
                handlePostEpic(exchange);
            } else if ("DELETE".equals(exchange.getRequestMethod())) {
                if (matcher.matches()) {
                    int id = Integer.parseInt(matcher.group(1));
                    handleDeleteEpic(exchange, id);
                } else {
                    sendNotFound(exchange);
                }
            } else {
                sendNotFound(exchange);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendNotFound(exchange);
        }
    }

    private void handleGetEpics(HttpExchange exchange) throws IOException {
        List<Epic> epics = manager.getAllEpics();
        String jsonResponse = gson.toJson(epics);
        sendText(exchange, jsonResponse, 200);
    }

    private void handleGetEpicById(HttpExchange exchange, int id) throws IOException {
        Epic epic = manager.getEpicById(id);
        if (epic != null) {
            String jsonResponse = gson.toJson(epic);
            sendText(exchange, jsonResponse, 200);
        } else {
            sendNotFound(exchange);
        }
    }

    private void handleGetEpicSubtasks(HttpExchange exchange, int id) throws IOException {
        Epic epic = manager.getEpicById(id);
        if (epic == null) {
            sendNotFound(exchange);
            return;
        }

        List<Subtask> subtasks = manager.getEpicSubtasks(id);
        List<Integer> subtaskIds = subtasks.stream()
                .map(Subtask::getId)
                .toList();
        String jsonResponse = gson.toJson(subtaskIds);
        sendText(exchange, jsonResponse, 200);
    }

    private void handlePostEpic(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        Epic epic = gson.fromJson(new InputStreamReader(inputStream), Epic.class);
        if (epic.getId() == 0) {
            manager.createEpic(epic);
            sendText(exchange, gson.toJson(epic), 201);
        } else {
            try {
                manager.updateEpic(epic);
                sendText(exchange, gson.toJson(epic), 201);
            } catch (Exception e) {
                sendHasIntersections(exchange);
            }
        }
    }

    private void handleDeleteEpic(HttpExchange exchange, int id) throws IOException {
        Epic epic = manager.getEpicById(id);
        if (epic != null) {
            manager.deleteEpicById(id);
            sendText(exchange, "{\"Статус\":\"Эпик удалён\"}", 200);
        } else {
            sendNotFound(exchange);
        }
    }
}