package model;

import infrastructure.HistoryManager;
import infrastructure.Managers;
import infrastructure.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static model.enums.Status.NEW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TaskTest {

    TaskManager taskManager;
    HistoryManager historyManager;
    Task task;

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
        task = new Task("Задача 1", "Описание задачи 1", NEW);
    }

    @Test
    void addNewTask() {
        taskManager.createTask(task);

        Task savedTask = taskManager.getTaskById(1);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void addNewHistoryRecord() {
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        assertNotNull(history, "В истории просмотров пусто.");
        assertEquals(1, history.size(), "В истории просмотров пусто.");
    }
}