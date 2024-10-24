package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubtaskTest {

    @Test
    void shouldReturnEpicId() {
        Subtask subtask = new Subtask(2, "Подзадача 1", "Описание подзадачи 1", 1);

        assertEquals(1, subtask.getEpicId());
    }

    @Test
    void shouldSetEpicId() {
        Epic epic = new Epic(2, "Эпик 1", "Описание эпика 1");
        Subtask subtask = new Subtask(2, "Подзадача 1", "Описание подзадачи 1", 1);

        subtask.setEpicId(epic);

        assertEquals(2, subtask.getEpicId());
    }

    @Test
    void subtaskShouldBeEqualsSubtask() {
        Subtask subtask1 = new Subtask(1, "Подзадача 1", "Описание подзадачи 1");
        Subtask subtask2 = new Subtask(1, "Подзадача 1", "Описание подзадачи 1");

        assertEquals(subtask1, subtask2);
    }
}