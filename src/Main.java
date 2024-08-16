import infrastructure.InMemoryTaskManager;
import model.Epic;
import model.enums.Status;
import model.Subtask;
import model.Task;

public class Main {

    public static void main(String[] args) {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        // Создайте две задачи
        taskManager.createTask(new Task("Помыть пол", "Задача 1", Status.NEW));
        taskManager.createTask(new Task("Приготовить еду", "Задача 2", Status.NEW));

        // Создайте эпик с двумя подзадачами
        taskManager.createEpic(new Epic("Выполнить ФЗ №4", "Эпик 1"));
        taskManager.createSubtask(new Subtask("Написать код", "Подзадача 1", Status.NEW, 1));
        taskManager.createSubtask(new Subtask("Отправить код на проверку", "Подзадача 2", Status.NEW, 1));

        // Создайте эпик с одной подзадачей
        taskManager.createEpic(new Epic("Важный эпик 2", "Эпик 2"));
        taskManager.createSubtask(new Subtask("Важное дело", "Подзадача 1", Status.NEW, 2));

        // Распечатайте списки эпиков, задач и подзадач через System.out.println(..)
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubtasks());
        System.out.println(taskManager.getAllEpics());

        // Измените статусы созданных объектов, распечатайте их.
        taskManager.getTaskById(1).setStatus(Status.IN_PROGRESS);
        taskManager.getTaskById(2).setStatus(Status.DONE);

        taskManager.getSubtaskById(1).setStatus(Status.IN_PROGRESS);
        Epic epic1 = taskManager.getEpicById(1);
        epic1.setStatus(taskManager.calculateStatus(epic1));

        taskManager.getSubtaskById(3).setStatus(Status.DONE);
        Epic epic2 = taskManager.getEpicById(2);
        epic2.setStatus(taskManager.calculateStatus(epic2));

        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubtasks());
        System.out.println(taskManager.getAllEpics());
    }
}
