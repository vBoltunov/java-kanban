package infrastructure;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static model.enums.Status.IN_PROGRESS;
import static model.enums.Status.NEW;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HistoryManagerTest {

    HistoryManager historyManager;

    @BeforeEach
    void beforeEach() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void add() {
        LocalDateTime startTime1 = LocalDateTime.of(2024, 11, 5, 1, 0);
        LocalDateTime startTime2 = LocalDateTime.of(2024, 11, 5, 1, 20);
        Task task = new Task(1,"Задача 1", "Описание задачи 1", NEW,
                startTime1, Duration.ofMinutes(10));
        Epic epic = new Epic(2,"Эпик 1", "Описание эпика 1");
        Subtask subtask = new Subtask(3,"Подзадача 1", "Описание подзадачи 1", IN_PROGRESS,
                1, startTime2, Duration.ofMinutes(20));

        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);

        List<Task> list = historyManager.getHistory();

        assertEquals(task, list.get(0), "Задача 1 должна быть первой в списке");
        assertEquals(epic, list.get(1), "Эпик 1 должен быть вторым в списке");
        assertEquals(subtask, list.get(2), "Подзадача 1 должна быть третьей в списке");
    }

    // Дублирование
    @Test
    void addTasksTwice() {
        LocalDateTime startTime1 = LocalDateTime.of(2024, 11, 5, 1, 0);
        LocalDateTime startTime2 = LocalDateTime.of(2024, 11, 5, 1, 20);

        Task task = new Task(1, "Задача 1", "Описание задачи 1", NEW, startTime1, Duration.ofMinutes(10));
        Epic epic = new Epic(2, "Эпик 1", "Описание эпика 1");
        Subtask subtask = new Subtask(3, "Подзадача 1", "Описание подзадачи 1", NEW,
                2, startTime2, Duration.ofMinutes(20));

        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);
        historyManager.add(epic);
        historyManager.add(task);

        List<Task> list = historyManager.getHistory();

        assertEquals(3, list.size(), "Длина списка должна быть равна 3");
        assertEquals(subtask, list.get(0), "Подзадача 1 должна быть первой в списке");
        assertEquals(epic, list.get(1), "Эпик 1 должен быть вторым в списке");
        assertEquals(task, list.get(2), "Задача 1 должна быть третьей в списке");
    }

    // Удаление из начала истории
    @Test
    void removeFirstElement() {
        LocalDateTime startTime1 = LocalDateTime.of(2024, 11, 5, 1, 0);
        LocalDateTime startTime2 = LocalDateTime.of(2024, 11, 5, 1, 20);

        Task task = new Task(1, "Задача 1", "Описание задачи 1", NEW,
                startTime1, Duration.ofMinutes(10));
        Epic epic = new Epic(2, "Эпик 1", "Описание эпика 1");
        Subtask subtask = new Subtask(3, "Подзадача 1", "Описание подзадачи 1", NEW,
                2, startTime2, Duration.ofMinutes(20));

        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);
        historyManager.remove(1);

        List<Task> list = historyManager.getHistory();

        assertEquals(2, list.size(), "Длина списка должна быть равна 2");
        assertEquals(epic, list.get(0), "Эпик должен быть элементом с индексом 0");
        assertEquals(subtask, list.get(1), "Подзадача должна быть элементом с индексом 1");
    }

    // Удаление из конца истории
    @Test
    void removeLastElement() {
        LocalDateTime startTime1 = LocalDateTime.of(2024, 11, 5, 1, 0);
        LocalDateTime startTime2 = LocalDateTime.of(2024, 11, 5, 1, 20);

        Task task1 = new Task(1, "Задача 1", "Описание задачи 1", NEW,
                startTime1, Duration.ofMinutes(10));
        Epic epic2 = new Epic(2, "Эпик 1", "Описание эпика 1");
        Subtask subtask3 = new Subtask(3, "Подзадача 1", "Описание подзадачи 1", NEW,
                2, startTime2, Duration.ofMinutes(20));

        historyManager.add(task1);
        historyManager.add(epic2);
        historyManager.add(subtask3);
        historyManager.remove(3);

        List<Task> list = historyManager.getHistory();

        assertEquals(2, list.size(), "Длина списка должна быть равна 2");
        assertEquals(task1, list.get(0), "Задача 1 должна быть первой в списке");
        assertEquals(epic2, list.get(1), "Эпик 1 должен быть вторым в списке");
    }

    // Удаление из середины истории
    @Test
    void removeMiddleElement() {
        LocalDateTime startTime1 = LocalDateTime.of(2024, 11, 5, 1, 0);
        LocalDateTime startTime2 = LocalDateTime.of(2024, 11, 5, 1, 20);

        Task task1 = new Task(1, "Задача 1", "Описание задачи 1", NEW,
                startTime1, Duration.ofMinutes(10));
        Epic epic2 = new Epic(2, "Эпик 1", "Описание эпика 1");
        Subtask subtask3 = new Subtask(3, "Подзадача 1", "Описание подзадачи 1", NEW,
                2, startTime2, Duration.ofMinutes(20));

        historyManager.add(task1);
        historyManager.add(epic2);
        historyManager.add(subtask3);
        historyManager.remove(2);

        List<Task> list = historyManager.getHistory();

        assertEquals(2, list.size(), "Длина списка должна быть равна 2");
        assertEquals(task1, list.get(0), "Задача 1 должна быть первой в списке");
        assertEquals(subtask3, list.get(1), "Подзадача 1 должна быть третьей в списке");
    }

    @Test
    void getHistory() {
        LocalDateTime startTime1 = LocalDateTime.of(2024, 11, 5, 1, 0);
        LocalDateTime startTime2 = LocalDateTime.of(2024, 11, 5, 1, 20);

        Task task = new Task(1,"Задача 1", "Описание задачи 1", NEW,
                startTime1, Duration.ofMinutes(10));
        Epic epic = new Epic(2,"Эпик 1", "Описание эпика 1");
        Subtask subtask = new Subtask(3,"Подзадача 1", "Описание подзадачи 1", IN_PROGRESS,
                1, startTime2, Duration.ofMinutes(20));

        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);

        List<Task> list = historyManager.getHistory();

        assertEquals(3, list.size(), "Длина списка должна быть равна 3");
    }

    // Пустая история задач
    @Test
    void historyIsEmpty() {
        assertEquals(0, historyManager.getHistory().size(), "Длина списка должна быть равна 0");
    }
}