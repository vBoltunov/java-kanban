package infrastructure;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static model.enums.Status.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class FileBackedTaskManagerTest {

    HistoryManager historyManager;
    File tempFile;
    FileBackedTaskManager saveManager;
    FileBackedTaskManager loadManager;

    // Для тестирования методов использовать функцию создания временных файлов File.createTempFile(…)
    @BeforeEach
    void beforeEach() throws IOException {
        historyManager = Managers.getDefaultHistory();
        tempFile = File.createTempFile("tasks", "csv");
        saveManager = new FileBackedTaskManager(historyManager, tempFile);
        loadManager = FileBackedTaskManager.loadFromFile(tempFile);
    }

    // Проверить сохранение и загрузку пустого файла
    @Test
    public void shouldSaveAndLoadFromEmptyFile() {
        saveManager.save();

        assertTrue(loadManager.getAllTasks().isEmpty());
        assertTrue(loadManager.getAllEpics().isEmpty());
        assertTrue(loadManager.getAllSubtasks().isEmpty());
    }

    // Проверить сохранение нескольких задач, загрузку нескольких задач
    @Test
    public void shouldSaveAndLoadFromNonEmptyFile() {
        saveManager.createTask(new Task(1,"Задача 1", "Описание задачи 1", NEW));
        saveManager.createTask(new Task(2,"Задача 2", "Описание задачи 2", NEW));
        saveManager.createEpic(new Epic(3,"Эпик 1", "Описание эпика 1", NEW));
        saveManager.createSubtask(new Subtask(4,"Подзадача 1", "Описание подзадачи 1", NEW,3));

        assertEquals(saveManager.getAllTasks(), loadManager.getAllTasks());
        assertEquals(saveManager.getAllEpics(), loadManager.getAllEpics());
        assertEquals(saveManager.getAllSubtasks(), loadManager.getAllSubtasks());
    }
}
