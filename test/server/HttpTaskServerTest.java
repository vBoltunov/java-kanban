package server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import managers.InMemoryTaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import managers.Managers;
import managers.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static model.enums.Status.IN_PROGRESS;
import static model.enums.Status.NEW;
import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {

    TaskManager taskManager;
    HttpTaskServer taskServer;

    Gson gson = Managers.getGson();

    Task task;
    Epic epic;
    Subtask subtask;
    HttpClient client;

    @BeforeEach
    void beforeEach() throws IOException {
        taskManager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(taskManager);
        taskServer.start();
        client = HttpClient.newHttpClient();
        LocalDateTime startTime1 = LocalDateTime.of(2024, 10, 23, 18, 0);
        task = taskManager.createTask(new Task("Задача 1", "Описание задачи 1", NEW,
                startTime1, Duration.ofMinutes(9)));
        epic = taskManager.createEpic(new Epic("Эпик 1", "Описание эпика 1"));
        subtask = taskManager.createSubtask(new Subtask("Подзадача 1", "Описание подзадачи 1", NEW,
                2, startTime1.plusMinutes(20), Duration.ofMinutes(6)));
        taskManager.getTaskById(1);
    }

    @AfterEach
    void afterEach() {
        taskServer.stop();
    }

    @Test
    void shouldReturnHistory() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/history/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Integer> taskIds = gson.fromJson(response.body(), new TypeToken<ArrayList<Integer>>(){}.getType());

        assertEquals(200, response.statusCode(), "Должен вернуться код 200");
        assertNotNull(taskIds, "Список id не должен возвращать null");
        assertFalse(taskIds.isEmpty(), "Список id не должен быть пустым");

        int expectedTaskId = task.getId();
        assertTrue(taskIds.contains(expectedTaskId),
                "В списке id, сохранённом в истории, должен содержаться id Задачи 1");
        assertEquals(1, expectedTaskId,
                "id, сохранённый в истории, должен быть равен 1");
    }

    @Test
    void shouldRemoveTaskById() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(),"Должен вернуться код 200");
        assertNull(taskManager.getTaskById(1), "Запрос удалённой задачи должен возвращать null");
    }

    @Test
    void shouldRemoveEpicById() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/epics/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(),"Должен вернуться код 200");
        assertNull(taskManager.getEpicById(2), "Запрос удалённого эпика должен возвращать null");
    }

    @Test
    void shouldRemoveSubtaskById() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/subtasks/3");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(),"Должен вернуться код 200");
        assertNull(taskManager.getSubtaskById(3), "Запрос удалённой подзадачи должен возвращать null");
    }

    @Test
    void shouldUpdateTaskById() throws IOException, InterruptedException {
        LocalDateTime startTime2 = LocalDateTime.of(2024, 10, 23, 18, 10);
        Task alteredTask = new Task(1, "Изменённая задача 1", "Описание изменённой задачи 1",
                IN_PROGRESS, startTime2, Duration.ofMinutes(5));
        String json = gson.toJson(alteredTask);

        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(json)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(),"Должен вернуться код 201");
        assertEquals(alteredTask, taskManager.getTaskById(1),
                "Новые поля задачи не были записаны на сервер");
    }

    @Test
    void shouldUpdateSubtaskById() throws IOException, InterruptedException {
        LocalDateTime startTime2 = LocalDateTime.of(2024, 10, 23, 18, 10);
        Subtask alteredSubtask = new Subtask(3, "Изменённая подзадача 1",
                "Описание изменённой подзадачи 1", IN_PROGRESS, 2, startTime2, Duration.ofMinutes(5));
        String json = gson.toJson(alteredSubtask);

        URI url = URI.create("http://localhost:8080/subtasks/3");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(json)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(),"Должен вернуться код 201");
        assertEquals(alteredSubtask, taskManager.getSubtaskById(3),
                "Новые поля подзадачи не были записаны на сервер");
    }

    @Test
    void shouldCreateNewTask() throws IOException, InterruptedException {
        LocalDateTime startTime2 = LocalDateTime.of(2024, 10, 23, 18, 10);
        Task newTask = new Task("Задача 2", "Описание задачи 2", NEW, startTime2, Duration.ofMinutes(5));
        String json = gson.toJson(newTask);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(json)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(),"Должен вернуться код 201");
        assertEquals(4, taskManager.getTaskById(4).getId(), "Записанная задача должна иметь id=4");
        assertEquals("Задача 2", taskManager.getTaskById(4).getName(),
                "Название записанной задачи должно соответствовать строке 'Задача 2'");

        String jsonObject = response.body();
        Task receivedTask = gson.fromJson(jsonObject, Task.class);

        assertEquals(taskManager.getTaskById(4), receivedTask,
                "Отправленная и полученная задачи должны быть идентичными");
    }

    @Test
    void shouldCreateNewEpic() throws IOException, InterruptedException {
        Epic newEpic = new Epic("Эпик 2", "Описание эпика 2");
        String json = gson.toJson(newEpic);

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(json)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(),"Должен вернуться код 201");
        assertEquals(4, taskManager.getEpicById(4).getId(), "Записанный эпик должен иметь id=4");
        assertEquals("Эпик 2", taskManager.getEpicById(4).getName(),
                "Название записанного эпика должно соответствовать строке 'Эпик 2'");

        String jsonObject = response.body();
        Epic receivedEpic = gson.fromJson(jsonObject, Epic.class);

        assertEquals(taskManager.getEpicById(4), receivedEpic,
                "Отправленный и полученный эпики должны быть идентичными");
    }

    @Test
    void shouldCreateNewSubtask() throws IOException, InterruptedException {
        LocalDateTime startTime2 = LocalDateTime.of(2024, 10, 23, 18, 10);
        Subtask newSubtask = new Subtask("Подзадача 2", "Описание подзадачи 2", NEW,
                2, startTime2, Duration.ofMinutes(5));
        String json = gson.toJson(newSubtask);

        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(json)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(),"Должен вернуться код 201");
        assertEquals(4, taskManager.getSubtaskById(4).getId(),
                "В памяти должна быть подзадача с id=4");
        assertEquals("Подзадача 2", taskManager.getSubtaskById(4).getName(),
                "Название записанной подзадачи должно соответствовать строке 'Подзадача 2'");

        String jsonObject = response.body();
        Subtask receivedSubtask = gson.fromJson(jsonObject, Subtask.class);

        assertEquals(taskManager.getSubtaskById(4), receivedSubtask,
                "Отправленная и полученная подзадачи должны быть идентичными");
    }

    @Test
    void shouldReceiveTaskById() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(),"Должен вернуться код 200");

        String jsonObject = response.body();
        Task receivedTask = gson.fromJson(jsonObject, Task.class);

        assertEquals(taskManager.getTaskById(1), receivedTask,
                "Отправленная и полученная задачи должны быть идентичными");
    }

    @Test
    void shouldReceiveEpicById() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/epics/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(),"Должен вернуться код 200");

        String jsonObject = response.body();
        Epic receivedEpic = gson.fromJson(jsonObject, Epic.class);

        assertEquals(taskManager.getEpicById(2), receivedEpic,
                "Отправленный и полученный эпики должны быть идентичными");
    }

    @Test
    void shouldReceiveSubtaskById() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/subtasks/3");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(),"Должен вернуться код 200");

        String jsonObject = response.body();
        Subtask receivedSubtask = gson.fromJson(jsonObject, Subtask.class);

        assertEquals(taskManager.getSubtaskById(3), receivedSubtask,
                "Отправленная и полученная подзадачи должны быть идентичными");
    }

    @Test
    void shouldReceiveAllTasks() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(),"Должен вернуться код 200");
        List<Task> list = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>(){}.getType());

        assertEquals(list.getFirst(), taskManager.getAllTasks().getFirst(),
                "Задача должна быть первой в списке переданных и полученных задач");
        assertEquals(list.size(), taskManager.getAllTasks().size(),
                "Размер списка переданных задач должен быть равен размеру списка полученных");
    }

    @Test
    void shouldReceiveAllEpics() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(),"Должен вернуться код 200");
        List<Epic> list = gson.fromJson(response.body(), new TypeToken<ArrayList<Epic>>(){}.getType());

        assertEquals(list.getFirst(), taskManager.getAllEpics().getFirst(),
                "Эпик должен быть первым в списке переданных и полученных эпиков");
        assertEquals(list.size(), taskManager.getAllEpics().size(),
                "Размер списка переданных эпиков должен быть равен размеру списка полученных");
    }

    @Test
    void shouldReceiveAllSubtasks() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(),"Должен вернуться код 200");
        List<Subtask> list = gson.fromJson(response.body(), new TypeToken<ArrayList<Subtask>>(){}.getType());

        assertEquals(list.getFirst(), taskManager.getAllSubtasks().getFirst(),
                "Подзадача должна быть первой в списке переданных и полученных подзадач");
        assertEquals(list.size(), taskManager.getAllSubtasks().size(),
                "Размер списка переданных подзадач должен быть равен размеру списка полученных");
    }
}
