package infrastructure;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        Task task = new Task(1,"Задача 1", "Описание задачи 1", NEW);
        Epic epic = new Epic(2,"Эпик 1", "Описание эпика 1");
        Subtask subtask = new Subtask(3,"Подзадача 1", "Описание подзадачи 1", IN_PROGRESS, 1);

        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);

        List<Task> list = historyManager.getHistory();

        assertEquals(task, list.get(0), "Задача 1 дожна быть первой в списке");
        assertEquals(epic, list.get(1), "Эпик 1 должен быть вторым в списке");
        assertEquals(subtask, list.get(2), "Подзадача 1 должна быть третьей в списке");
    }

    @Test
    void getHistory() {
        Task task = new Task(1,"Задача 1", "Описание задачи 1", NEW);
        Epic epic = new Epic(2,"Эпик 1", "Описание эпика 1");
        Subtask subtask = new Subtask(3,"Подзадача 1", "Описание подзадачи 1", IN_PROGRESS, 1);

        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);

        List<Task> list = historyManager.getHistory();

        assertEquals(3, list.size(), "Длина списка должна быть равна 3");
    }

    @Test
    void historyIsEmpty() {
        assertEquals(0, historyManager.getHistory().size(), "Длина списка должна быть равна 0");
    }
}