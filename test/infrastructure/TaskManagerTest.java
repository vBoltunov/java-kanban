package infrastructure;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static model.enums.Status.IN_PROGRESS;
import static model.enums.Status.NEW;
import static org.junit.jupiter.api.Assertions.*;

class TaskManagerTest {

    protected TaskManager taskManager;

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    public void taskInstancesAreEqualWhenTheirIdsEqual() {
        Task task = taskManager.createTask(new Task(1,"Задача 1", "Описание задачи 1", NEW));

        Task savedTask = taskManager.getAllTasks().getFirst();

        // Добавил сравнение id
        assertEquals(task.getId(), savedTask.getId(), "Id исходной и записанной задач не совпадают");
        assertEquals(task, savedTask, "Исходная и записанная задачи не совпадают");
        assertEquals(task.hashCode(), savedTask.hashCode(),
                "hashCode() исходной и записанной задач не совпадает");
        assertEquals(task.toString(), savedTask.toString(),
                "toString() исходной и записанной задач не совпадает");
    }

    @Test
    public void epicInstancesAreEqualWhenTheirIdsEqual() {
        Epic epic = taskManager.createEpic(new Epic(1,"Эпик 1", "Описание эпика 1"));
        Subtask subtask = taskManager.createSubtask(
                new Subtask(1,"Подзадача 1", "Описание подзадачи 1", NEW, 1));

        Epic savedEpic = taskManager.getAllEpics().getFirst();
        Subtask savedSubtask = taskManager.getAllSubtasks().getFirst();

        // Добавил сравнение id
        assertEquals(epic.getId(), savedEpic.getId(), "Id исходного и записанного эпиков не совпадают");
        assertEquals(epic, savedEpic, "Исходный и записанный эпики не совпадают");
        assertEquals(epic.hashCode(), savedEpic.hashCode(),
                "hashCode() исходного и записанного эпиков не совпадает");
        assertEquals(epic.toString(), savedEpic.toString(),
                "toString() исходного и записанного эпиков не совпадает");

        // Добавил сравнение id
        assertEquals(subtask.getId(), savedSubtask.getId(), "Id исходной и записанной подзадач не совпадают");
        assertEquals(subtask, savedSubtask, "Исходная и записанная подзадачи не совпадают");
        assertEquals(subtask.hashCode(), savedSubtask.hashCode(),
                "hashCode() исходной и записанной подзадач не совпадает");
        assertEquals(subtask.toString(), savedSubtask.toString(),
                "toString() исходной и записанной подзадач не совпадает");
    }

    @Test
    public void shouldReturnInMemoryTaskManagerByDefault() {

        assertNotNull(taskManager, "Task Manager не был создан");
        assertInstanceOf(InMemoryTaskManager.class, taskManager,
                "Менеджер возвращает неправильный класс");
    }

    @Test
    public void shouldReturnInMemoryHistoryManagerByDefault() {
        HistoryManager historyManager = Managers.getDefaultHistory();

        assertNotNull(historyManager, "History manager не был создан");
        assertInstanceOf(InMemoryHistoryManager.class, historyManager,
                "Менеджер возвращает неправильный класс");
    }

    @Test
    public void taskManagerWorksWithVariousTaskTypesAndAbleToFindThemById() {
        Task task = taskManager.createTask(new Task(1, "Задача 1", "Описание задачи 1"));
        Epic epic = taskManager.createEpic(new Epic(2, "Эпик 1", "Описание эпика 1"));
        Subtask subtask = taskManager.createSubtask(
                new Subtask(3, "Подзадача 1", "Описание подзадачи 1", NEW, 2));

        assertEquals(Task.class, taskManager.getTaskById(1).getClass(),
                "Тип исходной задачи и сохранённой в менеджере не совпадают");
        assertEquals(Epic.class, taskManager.getEpicById(2).getClass(),
                "Тип исходной задачи и сохранённой в менеджере не совпадают");
        assertEquals(Subtask.class, taskManager.getSubtaskById(3).getClass(),
                "Тип исходной задачи и сохранённой в менеджере не совпадают");
        assertEquals(task.getId(), taskManager.getAllTasks().getFirst().getId(),
                "Id исходной задачи и сохранённой в менеджере не совпадают");
        assertEquals(epic.getId(), taskManager.getAllEpics().getFirst().getId(),
                "Id исходного эпика и сохранённого в менеджере не совпадают");
        assertEquals(subtask.getId(), taskManager.getAllSubtasks().getFirst().getId(),
                "Id исходной подзадачи и сохранённой в менеджере не совпадают");
    }

    @Test
    public void taskManagerShouldWorkWithIncomingIdsAndGeneratedIds() {
        Task task1 = taskManager.createTask(new Task("Задача 1", "Описание задачи 1"));
        Task task2 = taskManager.createTask(new Task(2, "Задача 2", "Описание задачи 2"));

        assertEquals(task1, taskManager.getTaskById(1), "Первая задача должна иметь идентификатор 1");
        assertEquals(task2, taskManager.getTaskById(2), "Вторая задача должна иметь идентификатор 2");
    }

    @Test
    public void taskManagerShouldStoreTaskFieldsUnchanged() {
        taskManager.createTask(new Task(1,"Задача 1", "Описание задачи 1", NEW));

        assertEquals("Задача 1", taskManager.getTaskById(1).getName(),
                "Неправильно сохраняется имя задачи");
        assertEquals("Описание задачи 1", taskManager.getTaskById(1).getDescription(),
                "Неправильно сохраняется описание задачи");
        assertEquals(NEW, taskManager.getTaskById(1).getStatus(),
                "Неправильно сохраняется статус задачи");
    }

    // Убедитесь, что задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных.
    /* Данный тест удалён, так как в ФЗ 6 появилась новая задача:
    Избавиться от повторных просмотров в истории. Если какую-либо задачу посещали несколько раз,
    то в истории должен остаться только её последний просмотр. Предыдущий должен быть удалён. */

    @Test
    void getAllTasks() {
        Task task1 = taskManager.createTask(new Task("Задача 1", "Описание задачи 1", NEW));
        Task task2 = taskManager.createTask(new Task("Задача 2", "Описание задачи 2", NEW));

        List<Task> taskList = taskManager.getAllTasks();

        assertEquals(task1, taskList.get(0), "Первая задача должна быть первой в списке");
        assertEquals(task2, taskList.get(1), "Вторая задача должна быть второй в списке");
        assertEquals(2, taskList.size(), "Список задач должен содержать 2 элемента");
    }

    @Test
    void getAllEpics() {
        Epic epic1 = taskManager.createEpic(new Epic("Эпик 1", "Описание эпика 1"));
        Epic epic2 = taskManager.createEpic(new Epic("Эпик 2", "Описание эпика 2"));

        List<Epic> epicList = taskManager.getAllEpics();

        assertEquals(epic1, epicList.get(0), "Первый эпик должен быть первым в списке");
        assertEquals(epic2, epicList.get(1), "Второй эпик должен быть вторым в списке");
        assertEquals(2, epicList.size(), "Список эпиков должен содержать 2 элемента");
    }

    @Test
    void getAllSubtasks() {
        taskManager.createEpic(new Epic("Эпик 1", "Описание эпика 1"));
        Subtask subtask1 = taskManager.createSubtask(
                new Subtask("Подзадача 1", "Описание подзадачи 1", NEW, 1));
        Subtask subtask2 = taskManager.createSubtask(
                new Subtask("Подзадача 2", "Описание подзадачи 2", NEW, 1));

        List<Subtask> subtaskList = taskManager.getAllSubtasks();

        assertEquals(subtask1, subtaskList.get(0), "Первая подзадача должна быть первой в списке");
        assertEquals(subtask2, subtaskList.get(1), "Вторая подзадача должна быть второй в списке");
        assertEquals(2, subtaskList.size(), "Список подзадач должен содержать 2 элемента");
    }

    @Test
    void createTaskWithNull() {
        Task result = taskManager.createTask(null);

        assertNull(result, "Пустая задача должна возвращать null");
    }

    @Test
    void createEpicWithNull() {
        Epic result = taskManager.createEpic(null);

        assertNull(result, "Пустой эпик должен возвращать null");
    }

    @Test
    void createSubtaskWithNull() {
        Subtask result = taskManager.createSubtask(null);

        assertNull(result, "Пустая подзадача должна возвращать null");
    }

    @Test
    void updateTaskWithNextTaskWithNullStatus() {
        taskManager.createTask(new Task("Задача 1", "Описание задачи 1", NEW));
        Task task2 = new Task(1, "Задача 2", "Описание задачи 2", null);

        taskManager.updateTask(task2);

        assertEquals(NEW, taskManager.getTaskById(1).getStatus(),
                "Если передан статус null, статус задачи не должен меняться");
    }

    @Test
    void updateTaskWithNull() {
        Task task = taskManager.createTask(new Task("Задача 1", "Описание задачи 1", NEW));

        taskManager.updateTask(null);

        assertEquals(task, taskManager.getTaskById(1), "Если передан null, задача не должна обновляться");
    }

    @Test
    void updateTaskWithWrongId() {
        Task task1 = taskManager.createTask(new Task("Задача 1", "Описание задачи 1", NEW));
        Task task2 = new Task(2, "Задача 2", "Описание задачи 2", IN_PROGRESS);

        taskManager.updateTask(task2);

        assertEquals(task1, taskManager.getTaskById(1), "Задача не должна изменяться");
        assertNull(taskManager.getTaskById(2), "Вторая задача не должна быть записана");
    }

    @Test
    void updateEpicWithNextEpic() {
        taskManager.createEpic(new Epic("Эпик 1", "Описание эпика 1"));
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");

        taskManager.updateEpic(epic2);

        assertEquals("Эпик 1", taskManager.getEpicById(1).getName(),
                "Имя эпика не должно измениться");
        assertEquals("Описание эпика 1", taskManager.getEpicById(1).getDescription(),
                "Описание эпика не должно измениться");
    }

    @Test
    void updateEpicWithNull() {
        Epic epic = taskManager.createEpic(new Epic("Эпик 1", "Описание эпика 1"));

        taskManager.updateEpic(null);

        assertEquals(epic, taskManager.getEpicById(1), "Если передан null, эпик не должен обновляться");
    }

    @Test
    void updateSubtaskWithNull() {
        taskManager.createEpic(new Epic(1,"Эпик 1", "Описание эпика 1"));
        Subtask subTask = taskManager.createSubtask(
                new Subtask(2,"Подзадача 1", "Описание подзадачи 1", NEW, 1));

        taskManager.updateSubtask(null);

        assertEquals(subTask, taskManager.getSubtaskById(2),
                "Если передан null, подзадача не должна обновляться");
    }

    @Test
    void updateSubtaskWithWrongId() {
        taskManager.createEpic(new Epic(1,"Эпик 1", "Описание эпика 1"));
        Subtask subtask1 = taskManager.createSubtask(
                new Subtask(2,"Подзадача 1", "Описание подзадачи 1", NEW, 1));
        Subtask subtask2 = new Subtask(3, "Подзадача 2",
                "Описание подзадачи 2", IN_PROGRESS, 1);

        taskManager.updateSubtask(subtask2);

        assertEquals(subtask1, taskManager.getSubtaskById(2), "Подзадача не должна изменяться");
        assertNull(taskManager.getSubtaskById(3), "Вторая подзадача не должна быть записана");
    }

    @Test
    void deleteTaskById() {
        taskManager.createTask(new Task("Задача 1", "Описание задачи 1", NEW));

        taskManager.deleteTaskById(1);

        assertNull(taskManager.getTaskById(1), "Задача не была удалена");
    }

    @Test
    void deleteEpicById() {
        taskManager.createEpic(new Epic("Эпик 1", "Описание эпика 1"));

        taskManager.deleteEpicById(1);

        assertNull(taskManager.getEpicById(1), "Эпик не был удалён");
    }

    @Test
    void deleteSubtaskById() {
        taskManager.createEpic(new Epic(1,"Эпик 1", "Описание эпика 1"));
        taskManager.createSubtask(new Subtask(
                2,"Подзадача 1", "Описание подзадачи 1", NEW, 1));

        taskManager.deleteSubtaskById(2);

        assertNull(taskManager.getSubtaskById(2), "Подзадача не была удалена");
        assertEquals(0, taskManager.getEpicById(1).getSubtasks().size(),
                "Список подзадач эпика должен быть пуст");
    }

    @Test
    void deleteTaskByWrongId() {
        Task task = taskManager.createTask(new Task("Задача 1", "Описание задачи 1", NEW));

        taskManager.deleteTaskById(2);

        assertNull(taskManager.getTaskById(2), "Должен возвращаться null, если id не существует");
        assertEquals(task, taskManager.getTaskById(1), "Задача не должна измениться");
    }

    @Test
    void deleteEpicByWrongId() {
        Epic epic = taskManager.createEpic(new Epic("Эпик 1", "Описание эпика 1"));

        taskManager.deleteEpicById(2);

        assertNull(taskManager.getEpicById(2), "Должен возвращаться null, если id не существует");
        assertEquals(epic, taskManager.getEpicById(1), "Эпик не должен измениться");
    }

    @Test
    void deleteSubtaskByWrongId() {
        taskManager.createEpic(new Epic(1,"Эпик 1", "Описание эпика 1"));
        Subtask subtask = taskManager.createSubtask(
                new Subtask(2,"Подзадача 1", "Описание подзадачи 1", NEW, 1));

        taskManager.deleteSubtaskById(3);

        assertNull(taskManager.getSubtaskById(3), "Должен возвращаться null, если id не существует");
        assertEquals(subtask, taskManager.getSubtaskById(2), "Подзадача не должна измениться");
    }

    @Test
    void deleteAllTasks() {
        taskManager.createTask(new Task("Задача 1", "Описание задачи 1", NEW));
        taskManager.createTask(new Task("Задача 2", "Описание задачи 2", NEW));

        taskManager.deleteAllTasks();

        assertNull(taskManager.getTaskById(1), "Should be deleted");
        assertNull(taskManager.getTaskById(1), "Should be deleted");
        assertEquals(0, taskManager.getAllTasks().size(), "Длина списка задач должна быть равна 0");
    }

    @Test
    void deleteAllEpics() {
        taskManager.createEpic(new Epic("Эпик 1", "Описание эпика 1"));
        taskManager.createEpic(new Epic("Эпик 2", "Описание эпика 2"));
        taskManager.createSubtask(
                new Subtask("Подзадача 1", "Описание подзадачи 1", NEW, 1));
        taskManager.createSubtask(
                new Subtask("Подзадача 2", "Описание подзадачи 2", NEW, 2));

        taskManager.deleteAllEpics();

        assertNull(taskManager.getEpicById(1), "Эпик должен быть удалён");
        assertNull(taskManager.getEpicById(2), "Эпик должен быть удалён");
        assertEquals(0, taskManager.getAllEpics().size(), "Длина списка эпиков должна быть равна 0");

        assertNull(taskManager.getSubtaskById(3), "Подзадача должна быть удалена");
        assertNull(taskManager.getSubtaskById(4), "Подзадача должна быть удалена");
        assertEquals(0, taskManager.getAllSubtasks().size(),
                "Длина списка подзадач должна быть равна 0");
    }

    @Test
    void deleteAllSubtasks() {
        taskManager.createSubtask(new Subtask("Подзадача 1", "Описание подзадачи 1", NEW, 1));
        taskManager.createSubtask(new Subtask("Подзадача 2", "Описание подзадачи 2", NEW, 2));

        taskManager.deleteAllSubtasks();

        assertNull(taskManager.getSubtaskById(1), "Подзадача должна быть удалена");
        assertNull(taskManager.getSubtaskById(2), "Подзадача должна быть удалена");
        assertEquals(0, taskManager.getAllSubtasks().size(),
                "Длина списка подзадач должна быть равна 0");
    }

    @Test
    void getSubtaskList() {
        Epic epic1 = taskManager.createEpic(new Epic("Эпик 1", "Описание эпика 1"));
        taskManager.createSubtask(new Subtask("Подзадача 1", "Описание подзадачи 1", NEW, 1));
        taskManager.createSubtask(new Subtask("Подзадача 2", "Описание подзадачи 1", NEW, 1));

        List<Subtask> list = taskManager.getSubtaskList(epic1);
        assertEquals(2, list.size(), "Список подзадач должен содержать 2 элемента");
    }

    @Test
    void getSubtaskListFromEpicWithWrongId() {
        taskManager.createEpic(new Epic("Эпик 1", "Описание эпика 1"));
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");
        taskManager.createSubtask(
                new Subtask("Подзадача 1", "Описание подзадачи 1", NEW, 1));
        taskManager.createSubtask(new Subtask("Подзадача 2", "Описание подзадачи 2", NEW, 1));

        assertNull(taskManager.getSubtaskList(epic2), "Должен возвращаться null");
    }

    @Test
    void getHistory() {
        Task task = taskManager.createTask(new Task("Задача 1", "Описание задачи 1", NEW));
        Epic epic = taskManager.createEpic(new Epic("Эпик 1", "Описание эпика 1"));
        Subtask subtask = taskManager.createSubtask(
                new Subtask("Подзадача 1", "Описание подзадачи 1", NEW, 2));

        taskManager.getTaskById(1);
        taskManager.getEpicById(2);
        taskManager.getSubtaskById(3);
        taskManager.getTaskById(1);
        taskManager.getEpicById(2);
        taskManager.getSubtaskById(3);
        taskManager.getTaskById(1);
        taskManager.getEpicById(2);
        taskManager.getSubtaskById(3);
        taskManager.getTaskById(1);

        List<Task> list = taskManager.getHistory();

        assertEquals(3, list.size(), "Длина списка должна быть равна 3");
        assertEquals(epic, list.getFirst(), "Эпик 1 должен быть первым в списке");
        assertEquals(task, list.getLast(), "Задача 1 должна быть последней в списке");

        taskManager.getSubtaskById(3);

        List<Task> list1 = taskManager.getHistory();

        assertEquals(epic, list1.getFirst(), "Эпик 1 должен быть первым в списке");
        assertEquals(subtask, list1.getLast(), "Подзадача 1 должна быть последней в списке");
    }
}