package managers;

import exceptions.FileLoadException;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static model.enums.Status.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


class FileBackedTaskManagerTest {

    HistoryManager historyManager;
    File tempFile;
    FileBackedTaskManager saveManager;
    FileBackedTaskManager loadManager;

    // Для тестирования методов использовать функцию создания временных файлов File.createTempFile(…)
    @BeforeEach
    void beforeEach() throws IOException, FileLoadException {
        historyManager = Managers.getDefaultHistory();
        tempFile = File.createTempFile("tasks", ".csv");
        saveManager = new FileBackedTaskManager(historyManager, tempFile);
        loadManager = FileBackedTaskManager.loadFromFile(tempFile);
    }

    // Проверить сохранение и загрузку пустого файла
    @Test
    void saveAndLoadFromEmptyFile() {
        saveManager.save();

        assertTrue(loadManager.getAllTasks().isEmpty());
        assertTrue(loadManager.getAllEpics().isEmpty());
        assertTrue(loadManager.getAllSubtasks().isEmpty());
    }

    // Проверить сохранение нескольких задач, загрузку нескольких задач
    @Test
    void saveAndLoadFromNonEmptyFile() {
        LocalDateTime startTime1 = LocalDateTime.of(2024, 11, 5, 1, 0);
        LocalDateTime startTime2 = LocalDateTime.of(2024, 11, 5, 1, 20);
        LocalDateTime startTime3 = LocalDateTime.of(2024, 11, 5, 1, 45);
        saveManager.createTask(new Task(1,"Задача 1", "Описание задачи 1", NEW,
                startTime1, Duration.ofMinutes(10)));
        saveManager.createTask(new Task(2,"Задача 2", "Описание задачи 2", NEW,
                startTime2, Duration.ofMinutes(20)));
        saveManager.createEpic(new Epic(3,"Эпик 1", "Описание эпика 1", NEW));
        saveManager.createSubtask(new Subtask(4,"Подзадача 1", "Описание подзадачи 1", NEW,
                3, startTime3, Duration.ofMinutes(10)));

        assertEquals(saveManager.getAllTasks(), loadManager.getAllTasks());
        assertEquals(saveManager.getAllEpics(), loadManager.getAllEpics());
        assertEquals(saveManager.getAllSubtasks(), loadManager.getAllSubtasks());
    }

    // Проверяем загрузку из несуществующего файла
    @Test
    void testLoadFromFile_FileNotFound() {
        assertThrows(FileLoadException.class, () -> {
            FileBackedTaskManager.loadFromFile(new File("non-existent-file.csv"));
        });
    }

    // Проверяем загрузку из файла с недоступными правами для записи
    @Test
    void testLoadFromFile_IOError() {
        File inaccessibleFile = new File("inaccessible-file.csv");
        assertThrows(FileLoadException.class, () -> {
            FileBackedTaskManager.loadFromFile(inaccessibleFile);
        });
    }

    // Проверяем, что сохранение не выбрасывает исключений при корректной настройке
    @Test
    void testSave_Success() {
        assertDoesNotThrow(() -> {
            loadManager.createTask(new Task(1, "Задача 1", "Описание задачи 1"));
            loadManager.save();
        });
    }
}
