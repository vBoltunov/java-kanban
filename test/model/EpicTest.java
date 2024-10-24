package model;

import managers.InMemoryTaskManager;
import managers.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static model.enums.Status.*;
import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    private TaskManager taskManager;
    Epic epic;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
        epic = taskManager.createEpic(new Epic(1,"Эпик 1", "Описание эпика 1", NEW));
    }

    @Test
    void epicHasNoDeletedSubtasks() {
        LocalDateTime startTime1 = LocalDateTime.of(2024, 11, 5, 1, 0);
        LocalDateTime startTime2 = LocalDateTime.of(2024, 11, 5, 1, 20);
        taskManager.createSubtask(new Subtask(2,"Подзадача 1", "Описание подзадачи 1", NEW,
                1, startTime1, Duration.ofMinutes(10)));
        taskManager.createSubtask(new Subtask(3,"Подзадача 2", "Описание подзадачи 2", NEW,
                1, startTime2, Duration.ofMinutes(20)));
        taskManager.deleteSubtaskById(2);
        assertEquals(1, epic.getEpicSubtasks().size());
        assertEquals(3, epic.getEpicSubtasks().getFirst());
        assertEquals(3, epic.getEpicSubtasks().getLast());
    }

    @Test
    void emptyEpicHasNewStatus() {
        assertEquals(NEW, epic.getStatus(), "Статус генерируется неправильно");
    }

    @Test
    void newEpicHasNewStatus() {
        LocalDateTime startTime = LocalDateTime.of(2024, 10, 5, 20, 0);
        taskManager.createSubtask(new Subtask("Подзадача 1", "Описание подзадачи 1", NEW, 1,
                LocalDateTime.now(), Duration.ofDays(2)));
        taskManager.createSubtask(new Subtask("Подзадача 2", "Описание подзадачи 2", NEW, 1,
                startTime, Duration.ofMinutes(35)));
        assertEquals(NEW, epic.getStatus(), "Статус рассчитывается неправильно");
    }

    @Test
    void doneEpicHasDoneStatus() {
        LocalDateTime startTime1 = LocalDateTime.of(2024, 11, 5, 1, 0);
        LocalDateTime startTime2 = LocalDateTime.of(2024, 11, 5, 1, 20);
        taskManager.createSubtask(new Subtask(2, "Подзадача 1", "Описание подзадачи 1", NEW,
                1, startTime1, Duration.ofMinutes(10)));
        taskManager.createSubtask(new Subtask(3,"Подзадача 2", "Описание подзадачи 2", NEW,
                1, startTime2, Duration.ofMinutes(20)));
        taskManager.updateSubtask(new Subtask(2, "Подзадача 1", "Описание подзадачи 1", DONE,
                1, startTime1, Duration.ofMinutes(10)));
        taskManager.updateSubtask(new Subtask(3, "Подзадача 2", "Описание подзадачи 2", DONE,
                1, startTime2, Duration.ofMinutes(20)));
        assertEquals(DONE, epic.getStatus(), "Статус рассчитывается неправильно");
    }

    @Test
    void epicInProgressWhenSubtasksPartiallyDone() {
        LocalDateTime startTime1 = LocalDateTime.of(2024, 11, 5, 1, 0);
        LocalDateTime startTime2 = LocalDateTime.of(2024, 11, 5, 1, 20);
        taskManager.createSubtask(new Subtask(2, "Подзадача 1", "Описание подзадачи 1", NEW,
                1, startTime1, Duration.ofMinutes(10)));
        taskManager.createSubtask(new Subtask(3,"Подзадача 2", "Описание подзадачи 2", NEW,
                1, startTime2, Duration.ofMinutes(20)));
        taskManager.updateSubtask(new Subtask(2, "Подзадача 1", "Описание подзадачи 1", DONE,
                1, startTime1, Duration.ofMinutes(10)));
        taskManager.updateSubtask(new Subtask(3, "Подзадача 2", "Описание подзадачи 2", NEW,
                1, startTime2, Duration.ofMinutes(20)));
        assertEquals(IN_PROGRESS, epic.getStatus(), "Статус рассчитывается неправильно");
    }

    @Test
    void epicInProgressWhenSubtasksInProgress() {
        LocalDateTime startTime1 = LocalDateTime.of(2024, 11, 5, 1, 0);
        LocalDateTime startTime2 = LocalDateTime.of(2024, 11, 5, 1, 20);
        taskManager.createSubtask(new Subtask("Подзадача 1", "Описание подзадачи 1", NEW,
                1, startTime1, Duration.ofMinutes(10)));
        taskManager.createSubtask(new Subtask("Подзадача 2", "Описание подзадачи 2", NEW,
                1, startTime2, Duration.ofMinutes(20)));
        taskManager.updateSubtask(new Subtask(2, "Подзадача 1", "Описание подзадачи 1", IN_PROGRESS,
                1, startTime1, Duration.ofMinutes(10)));
        taskManager.updateSubtask(new Subtask(3, "Подзадача 2", "Описание подзадачи 2", IN_PROGRESS,
                1, startTime2, Duration.ofMinutes(20)));
        assertEquals(IN_PROGRESS, epic.getStatus(), "Статус рассчитывается неправильно");
    }
}