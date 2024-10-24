package managers;

import exceptions.FileLoadException;
import exceptions.ManagerSaveException;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static model.enums.Status.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class FileBackedTaskManagerTest {

    private FileBackedTaskManager taskManager;
    private File testFile;

    @BeforeEach
    void setUp() throws IOException {
        testFile = File.createTempFile("testTasks", ".csv");
        taskManager = new FileBackedTaskManager(testFile);
    }

    @Test
    void testSaveAndLoadEmptyFile() throws FileLoadException {
        taskManager.save();

        FileBackedTaskManager fileManager = new FileBackedTaskManager(testFile);
        FileBackedTaskManager loadedManager = fileManager.loadFromFile(testFile);

        List<Task> loadedTasks = loadedManager.getAllTasks();
        assertEquals(0, loadedTasks.size());
    }

    @Test
    void testSaveAndLoadTasks() throws ManagerSaveException, FileLoadException {
        LocalDateTime startTime1 = LocalDateTime.of(2024, 11, 5, 1, 0);
        LocalDateTime startTime2 = LocalDateTime.of(2024, 11, 5, 1, 20);
        LocalDateTime startTime3 = LocalDateTime.of(2024, 11, 5, 1, 45);
        taskManager.createTask(new Task("Задача 1", "Описание задачи 1", NEW,
                startTime1, Duration.ofMinutes(10)));
        taskManager.createTask(new Task("Задача 2", "Описание задачи 2", NEW,
                startTime2, Duration.ofMinutes(20)));
        taskManager.createEpic(new Epic("Эпик 1", "Описание эпика 1"));
        taskManager.createSubtask(new Subtask("Подзадача 1", "Описание подзадачи 1", NEW,
                3, startTime3, Duration.ofMinutes(10)));

        taskManager.save();

        FileBackedTaskManager fileManager = new FileBackedTaskManager(testFile);
        FileBackedTaskManager loadedManager = fileManager.loadFromFile(testFile);

        assertEquals(taskManager.getAllTasks(), loadedManager.getAllTasks());
        assertEquals(taskManager.getAllEpics(), loadedManager.getAllEpics());
        assertEquals(taskManager.getAllSubtasks(), loadedManager.getAllSubtasks());
    }

    @Test
    void testLoadFromFile_FileNotFound() {
        assertThrows(FileLoadException.class, () -> {
            FileBackedTaskManager fileManager = new FileBackedTaskManager(testFile);
            fileManager.loadFromFile(new File("non-existent-file.csv"));
        });
    }

    @Test
    void testLoadFromFile_IOError() {
        File inaccessibleFile = new File("inaccessible-file.csv");
        assertThrows(FileLoadException.class, () -> {

            FileBackedTaskManager fileManager = new FileBackedTaskManager(testFile);
            fileManager.loadFromFile(inaccessibleFile);
        });
    }

    @Test
    void testSave_Success() {
        assertDoesNotThrow(() -> {
            FileBackedTaskManager fileManager = new FileBackedTaskManager(testFile);
            FileBackedTaskManager loadedManager = fileManager.loadFromFile(testFile);
            loadedManager.createTask(new Task(1, "Задача 1", "Описание задачи 1"));
            loadedManager.save();
        });
    }
}

