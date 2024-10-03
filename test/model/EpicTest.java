package model;

import infrastructure.Managers;
import infrastructure.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static model.enums.Status.*;
import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    TaskManager taskManager;
    Epic epic;

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();
        epic = taskManager.createEpic(new Epic(1,"Эпик 1", "Описание эпика 1", NEW));
    }

    @Test
    void epicHasNoDeletedSubtasks() {
        taskManager.createSubtask(new Subtask("Подзадача 1", "Описание подзадачи 1", NEW, 1));
        taskManager.createSubtask(new Subtask("Подзадача 2", "Описание подзадачи 2", NEW, 1));
        taskManager.deleteSubtaskById(2);
        assertEquals(1, epic.getSubtasks().size());
        assertEquals(3, epic.getSubtasks().getFirst());
        assertEquals(3, epic.getSubtasks().getLast());
    }

    @Test
    void emptyEpicHasNewStatus() {
        assertEquals(NEW, epic.getStatus(), "Статус генерируется неправильно");
    }

    @Test
    void newEpicHasNewStatus() {
        taskManager.createSubtask(new Subtask("Подзадача 1", "Описание подзадачи 1", NEW, 1));
        taskManager.createSubtask(new Subtask("Подзадача 2", "Описание подзадачи 2", NEW, 1));
        assertEquals(NEW, epic.getStatus(), "Статус рассчитывается неправильно");
    }

    @Test
    void doneEpicHasDoneStatus() {
        taskManager.createSubtask(new Subtask(2,"Подзадача 1", "Описание подзадачи 1", NEW, 1));
        taskManager.createSubtask(new Subtask(3,"Подзадача 2", "Описание подзадачи 2", NEW, 1));
        taskManager.updateSubtask(new Subtask(2, "Подзадача 1", "Описание подзадачи 1", DONE, 1));
        taskManager.updateSubtask(new Subtask(3, "Подзадача 2", "Описание подзадачи 2", DONE, 1));
        assertEquals(DONE, epic.getStatus(), "Статус рассчитывается неправильно");
    }

    @Test
    void epicInProgressWhenSubtasksPartiallyDone() {
        taskManager.createSubtask(new Subtask(2,"Подзадача 1", "Описание подзадачи 1", NEW, 1));
        taskManager.createSubtask(new Subtask(3,"Подзадача 2", "Описание подзадачи 2", NEW, 1));
        taskManager.updateSubtask(new Subtask(2, "Подзадача 1", "Описание подзадачи 1", DONE, 1));
        taskManager.updateSubtask(new Subtask(3, "Подзадача 2", "Описание подзадачи 2", NEW, 1));
        assertEquals(IN_PROGRESS, epic.getStatus(), "Статус рассчитывается неправильно");
    }

    @Test
    void epicInProgressWhenSubtasksInProgress() {
        taskManager.createSubtask(new Subtask("Подзадача 1", "Описание подзадачи 1", NEW, 1));
        taskManager.createSubtask(new Subtask("Подзадача 2", "Описание подзадачи 2", NEW, 1));
        taskManager.updateSubtask(new Subtask(1, "Подзадача 1", "Описание подзадачи 1", IN_PROGRESS, 1));
        taskManager.updateSubtask(new Subtask(2, "Подзадача 2", "Описание подзадачи 2", IN_PROGRESS, 1));
        assertEquals(IN_PROGRESS, epic.getStatus(), "Статус рассчитывается неправильно");
    }
}