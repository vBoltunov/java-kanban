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

    // Проверьте, что экземпляры класса Task равны друг другу, если равен их id
    // Не уверен, что я правильно понял идею проверки
    @Test
    public void taskInstancesAreEqualWhenTheirIdsEqual() {
        Task task = new Task(1,"Задача 1", "Описание задачи 1", NEW);

        taskManager.createTask(task);

        Task savedTask = taskManager.getTaskById(1);

        assertEquals(task, savedTask, "Задачи не совпадают");
        assertEquals(task.hashCode(), savedTask.hashCode(), "hashCode() задач не совпадает");
        assertEquals(task.toString(), savedTask.toString(), "toString() задач не совпадает");
    }

    // Проверьте, что наследники класса Task равны друг другу, если равен их id
    // Не уверен, что я правильно понял идею проверки
    @Test
    public void epicInstancesAreEqualWhenTheirIdsEqual() {
        Epic epic = new Epic(1,"Эпик 1", "Описание эпика 1");
        Subtask subtask = new Subtask(1,"Позадача 1", "Описание подзадачи 1", NEW, 1);

        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);

        Epic savedEpic = taskManager.getEpicById(1);
        Subtask savedSubtask = taskManager.getSubtaskById(1);

        assertEquals(epic, savedEpic, "Эпики не совпадают");
        assertEquals(epic.hashCode(), savedEpic.hashCode(), "hashCode() эпиков не совпадает");
        assertEquals(epic.toString(), savedEpic.toString(), "toString() эпиков не совпадает");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают");
        assertEquals(subtask.hashCode(), savedSubtask.hashCode(), "hashCode() подзадач не совпадает");
        assertEquals(subtask.toString(), savedSubtask.toString(), "toString() подзадач не совпадает");
    }

    // Проверьте, что объект Epic нельзя добавить в самого себя в виде подзадачи
    // проверьте, что объект Subtask нельзя сделать своим же эпиком
    /* Такие проверки невозможно выполнить, т.к. методы по созданию
    задач/подзадач/эпиков принимают объекты определённого типа */

    // Убедитесь, что утилитарный класс всегда возвращает
    // проинициализированные и готовые к работе экземпляры менеджеров;
    @Test
    public void shouldReturnInMemoryTaskManagerByDefault() {
        TaskManager manager = Managers.getDefault();

        assertNotNull(manager, "Task Manager не был создан");
        assertInstanceOf(InMemoryTaskManager.class, manager,
                "Менеджер возвращает неправильный класс");
    }

    @Test
    public void shouldReturnInMemoryHistoryManagerByDefault() {
        HistoryManager manager = Managers.getDefaultHistory();

        assertNotNull(manager, "History manager не был создан");
        assertInstanceOf(InMemoryHistoryManager.class, manager,
                "Менеджер возвращает неправильный класс");
    }

    // проверьте, что InMemoryTaskManager действительно добавляет задачи разного типа и может найти их по id
    @Test
    public void taskManagerWorksWithVariousTaskTypesAndAbleToFindThemById() {
        Task task = new Task(1, "Задача 1", "Описание задачи 1");
        Epic epic = new Epic(1, "Эпик 1", "Описание эпика 1");
        Subtask subtask = new Subtask(1, "Позадача 1", "Описание подзадачи 1", NEW, 1);

        taskManager.createTask(task);
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);

        assertEquals(Task.class, taskManager.getTaskById(1).getClass(),
                "Тип исходной задачи и сохранённой в менеджере не совпадают");
        assertEquals(Epic.class, taskManager.getEpicById(1).getClass(),
                "Тип исходной задачи и сохранённой в менеджере не совпадают");
        assertEquals(Subtask.class, taskManager.getSubtaskById(1).getClass(),
                "Тип исходной задачи и сохранённой в менеджере не совпадают");
        assertEquals(task.getId(), taskManager.getAllTasks().get(0).getId(),
                "Id исходной задачи и сохранённой в менеджере не совпадают");
        assertEquals(epic.getId(), taskManager.getAllEpics().get(0).getId(),
                "Id исходного эпика и сохранённого в менеджере не совпадают");
        assertEquals(subtask.getId(), taskManager.getAllSubtasks().get(0).getId(),
                "Id исходной подзадачи и сохранённой в менеджере не совпадают");
    }

    // проверьте, что задачи с заданным id и сгенерированным id не конфликтуют внутри менеджера
    @Test
    public void taskManagerShouldWorkWithIncomingIdsAndGeneratedIds() {
        Task task1 = new Task( "Задача 1", "Описание задачи 1");
        Task task2 = new Task(2, "Задача 2", "Описание задачи 2");

        taskManager.createTask(task1);
        taskManager.createTask(task2);

        assertEquals(task1, taskManager.getTaskById(1), "Первая задача должна иметь идентификатор 1");
        assertEquals(task2, taskManager.getTaskById(2), "Вторая задача должна иметь идентификатор 2");
    }

    // создайте тест, в котором проверяется неизменность задачи (по всем полям) при добавлении задачи в менеджер
    @Test
    public void taskManagerShouldStoreTaskFieldsUnchanged() {
        Task task = new Task(1,"Задача 1", "Описание задачи 1", NEW);

        taskManager.createTask(task);

        assertEquals(task.getName(), taskManager.getTaskById(1).getName(),
                "Неправильно сохраняется имя задачи");
        assertEquals(task.getDescription(), taskManager.getTaskById(1).getDescription(),
                "Неправильно сохраняется описание задачи");
        assertEquals(task.getStatus(), taskManager.getTaskById(1).getStatus(),
                "Неправильно сохраняется статус задачи");
    }

    // убедитесь, что задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных.
    @Test
    public void historyManagerStoresPreviousVersionsOfTasks() {
        Task task = new Task("Задача 1", "Описание задачи 1", NEW);

        taskManager.createTask(task);

        taskManager.getTaskById(1);

        Task task1 = new Task(1,"Задача 1", "Описание задачи 1", IN_PROGRESS);

        taskManager.updateTask(task1);

        taskManager.getTaskById(1);

        List<Task> list = taskManager.getHistory();

        assertEquals(NEW, list.getFirst().getStatus(), "Статус задачи 1 должен быть NEW");
        assertEquals(IN_PROGRESS, list.getLast().getStatus(), "Статус задачи 1 должен быть IN_PROGRESS");
    }

    // More Tests :)
    @Test
    void getAllTasks() {
        Task task1 = new Task("Задача 1", "Описание задачи 1", NEW);
        Task task2 = new Task("Задача 2", "Описание задачи 2", NEW);

        taskManager.createTask(task1);
        taskManager.createTask(task2);

        List<Task> taskList = taskManager.getAllTasks();

        assertEquals(task1, taskList.get(0), "Первая задача должна быть первой в списке");
        assertEquals(task2, taskList.get(1), "Вторая задача должна быть второй в списке");
        assertEquals(2, taskList.size(), "Список задач должен содержать 2 элемента");
    }

    @Test
    void getAllEpics() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");

        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        List<Epic> epicList = taskManager.getAllEpics();

        assertEquals(epic1, epicList.get(0), "Первый эпик должен быть первым в списке");
        assertEquals(epic2, epicList.get(1), "Второй эпик должен быть вторым в списке");
        assertEquals(2, epicList.size(), "Список эпиков должен содержать 2 элемента");
    }

    @Test
    void getAllSubtasks() {
        taskManager.createEpic(new Epic("Эпик 1", "Описание эпика 1"));
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", NEW, 1);
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", NEW, 1);

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

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
        Task task1 = new Task("Задача 1", "Описание задачи 1", NEW);
        Task task2 = new Task(1, "Задача 2", "Описание задачи 2", null);

        taskManager.createTask(task1);
        taskManager.updateTask(task2);

        assertEquals(NEW, taskManager.getTaskById(1).getStatus(),
                "Если передан статус null, статус задачи не должен меняться");
    }

    @Test
    void updateTaskWithNull() {
        Task task = new Task("Задача 1", "Описание задачи 1", NEW);

        taskManager.createTask(task);
        taskManager.updateTask(null);

        assertEquals(task, taskManager.getTaskById(1), "Если передан null, задача не должна обновляться");
    }

    @Test
    void updateTaskWithWrongId() {
        Task task1 = new Task("Задача 1", "Описание задачи 1", NEW);
        Task task2 = new Task(2, "Задача 2", "Описание задачи 2", IN_PROGRESS);

        taskManager.createTask(task1);
        taskManager.updateTask(task2);

        assertEquals(task1, taskManager.getTaskById(1), "Задача не должна изменяться");
        assertNull(taskManager.getTaskById(2), "Вторая задача не должна быть записана");
    }

    @Test
    void updateEpicWithNextEpic() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");

        taskManager.createEpic(epic1);
        taskManager.updateEpic(epic2);

        assertEquals("Эпик 1", taskManager.getEpicById(1).getName(),
                "Имя эпика не должно измениться");
        assertEquals("Описание эпика 1", taskManager.getEpicById(1).getDescription(),
                "Описание эпика не должно измениться");
    }

    @Test
    void updateEpicWithNull() {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");

        taskManager.createEpic(epic);
        taskManager.updateEpic(null);

        assertEquals(epic, taskManager.getEpicById(1), "Если передан null, эпик не должен обновляться");
    }

    @Test
    void updateSubtaskWithNull() {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        Subtask subTask = new Subtask("Подзадача 1", "Описание подзадачи 1", NEW, 1);

        taskManager.createEpic(epic);
        taskManager.createSubtask(subTask);
        taskManager.updateSubtask(null);

        assertEquals(subTask, taskManager.getSubtaskById(1),
                "Если передан null, подзадача не должна обновляться");
    }

    @Test
    void updateSubtaskWithWrongId() {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", NEW, 1);
        Subtask subtask2 = new Subtask(2, "Подзадача 2",
                "Описание подзадачи 2", IN_PROGRESS, 1);

        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1);
        taskManager.updateSubtask(subtask2);

        assertEquals(subtask1, taskManager.getSubtaskById(1), "Подзадача не должна изменяться");
        assertNull(taskManager.getSubtaskById(2), "Вторая подзадача не должна быть записана");
    }

    @Test
    void deleteTaskById() {
        Task task = new Task("Задача 1", "Описание задачи 1", NEW);

        taskManager.createTask(task);
        taskManager.deleteTaskById(1);

        assertNull(taskManager.getTaskById(1), "Задача не была удалена");
    }

    @Test
    void deleteEpicById() {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");

        taskManager.createEpic(epic);
        taskManager.deleteEpicById(1);

        assertNull(taskManager.getEpicById(1), "Эпик не был удалён");
    }

    @Test
    void deleteSubtaskById() {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        Subtask subtask = new Subtask("Подзадача 1", "Описание подзадачи 1", NEW, 1);

        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        taskManager.deleteSubtaskById(1);

        assertNull(taskManager.getSubtaskById(1), "Подзадача не была удалена");
        assertEquals(0, taskManager.getEpicById(1).getSubtasks().size(),
                "Список подзадач эпика должен быть пуст");
    }

    @Test
    void deleteTaskByWrongId() {
        Task task = new Task("Задача 1", "Описание задачи 1", NEW);
        taskManager.createTask(task);
        taskManager.deleteTaskById(2);

        assertNull(taskManager.getTaskById(2), "Должен возвращаться null, если id не существует");
        assertEquals(task, taskManager.getTaskById(1), "Задача не должна измениться");
    }

    @Test
    void deleteEpicByWrongId() {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");

        taskManager.createEpic(epic);
        taskManager.deleteEpicById(2);

        assertNull(taskManager.getEpicById(2), "Должен возвращаться null, если id не существует");
        assertEquals(epic, taskManager.getEpicById(1), "Эпик не должен измениться");
    }

    @Test
    void deleteSubtaskByWrongId() {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        Subtask subtask = new Subtask("Подзадача 1", "Описание подзадачи 1", NEW, 1);

        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        taskManager.deleteSubtaskById(3);

        assertNull(taskManager.getSubtaskById(3), "Должен возвращаться null, если id не существует");
        assertEquals(subtask, taskManager.getSubtaskById(1), "Подзадача не должна измениться");
    }

    @Test
    void deleteAllTasks() {
        Task task1 = new Task("Задача 1", "Описание задачи 1", NEW);
        Task task2 = new Task("Задача 2", "Описание задачи 2", NEW);

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.deleteAllTasks();

        assertNull(taskManager.getTaskById(1), "Should be deleted");
        assertNull(taskManager.getTaskById(1), "Should be deleted");
        assertEquals(0, taskManager.getAllTasks().size(), "Длина списка задач должна быть равна 0");
    }

    @Test
    void deleteAllEpics() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", NEW, 1);
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", NEW, 2);

        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
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
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", NEW, 1);
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", NEW, 2);

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.deleteAllSubtasks();

        assertNull(taskManager.getSubtaskById(1), "Подзадача должна быть удалена");
        assertNull(taskManager.getSubtaskById(2), "Подзадача должна быть удалена");
        assertEquals(0, taskManager.getAllSubtasks().size(),
                "Длина списка подзадач должна быть равна 0");
    }

    @Test
    void getSubtaskList() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", NEW, 1);
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 1", NEW, 1);

        taskManager.createEpic(epic1);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        List<Subtask> list = taskManager.getSubtaskList(epic1);
        assertEquals(2, list.size(), "Список подзадач должен содержать 2 элемента");
    }

    @Test
    void getSubtaskListFromEpicWithWrongId() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", NEW, 1);
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", NEW, 1);

        taskManager.createEpic(epic1);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        assertNull(taskManager.getSubtaskList(epic2), "Должен возвращаться null");
    }

    @Test
    void getHistory() {
        Task task = taskManager.createTask(new Task("Задача 1", "Описание задачи 1", NEW));
        Epic epic = taskManager.createEpic(new Epic("Эпик 1", "Описание эпика 1"));
        Subtask subtask = taskManager.createSubtask(
                new Subtask("Подзадача 1", "Описание подзадачи 1", NEW, 1));

        taskManager.getTaskById(1);
        taskManager.getEpicById(1);
        taskManager.getSubtaskById(1);
        taskManager.getTaskById(1);
        taskManager.getEpicById(1);
        taskManager.getSubtaskById(1);
        taskManager.getTaskById(1);
        taskManager.getEpicById(1);
        taskManager.getSubtaskById(1);
        taskManager.getTaskById(1);

        List<Task> list = taskManager.getHistory();

        assertEquals(10, list.size(), "Длина списка должна быть равна 10");
        assertEquals(task, list.getFirst(), "Задача 1 должна быть первой в списке");
        assertEquals(task, list.getLast(), "Задача 1 должна быть последней в списке");

        taskManager.getSubtaskById(1);

        assertEquals(epic, list.getFirst(), "Эпик 1 должен быть первым в списке");
        assertEquals(subtask, list.getLast(), "Подзадача 1 должна быть последней в списке");
    }
}