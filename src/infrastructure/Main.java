package infrastructure;

import model.Epic;
import model.Subtask;
import model.Task;

import static model.enums.Status.*;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        // Создайте две задачи, эпик с тремя подзадачами и эпик без подзадач.
        System.out.println("Создаём две задачи, эпик с тремя подзадачами и эпик без подзадач...");
        taskManager.createTask(new Task(1, "Помыть пол", "Задача 1", NEW));
        taskManager.createTask(new Task(2, "Приготовить еду", "Задача 2", NEW));

        taskManager.createEpic(new Epic(3, "Выполнить ФЗ №6", "Эпик 1", NEW));
        taskManager.createSubtask(new Subtask(4, "Написать код", "Подзадача 1", NEW, 3));
        taskManager.createSubtask(new Subtask(
                5, "Отправить код на проверку", "Подзадача 2", NEW, 3));
        taskManager.createSubtask(new Subtask(
                6, "Доработать код согласно ревью", "Подзадача 3", NEW, 3));

        taskManager.createEpic(new Epic(7, "Эпичный эпик", "Эпик 2", NEW));

        // Запросите созданные задачи несколько раз в разном порядке.
        // После каждого запроса выведите историю и убедитесь, что в ней нет повторов.
        System.out.println("Запрашиваем созданные задачи несколько раз в разном порядке " +
                "и выводим историю после каждого запроса...");
        taskManager.getTaskById(1);
        System.out.println(taskManager.getHistory());
        taskManager.getTaskById(2);
        System.out.println(taskManager.getHistory());
        taskManager.getEpicById(3);
        System.out.println(taskManager.getHistory());
        taskManager.getEpicById(7);
        System.out.println(taskManager.getHistory());
        System.out.println();

        // Удалите задачу, которая есть в истории, и проверьте, что при печати она не будет выводиться.
        System.out.println("Удаляем одну из задач и проверяем, что она пропала из истории...");
        taskManager.deleteTaskById(1);
        System.out.println(taskManager.getHistory());
        System.out.println();

        // Удалите эпик с тремя подзадачами и убедитесь, что из истории удалился как сам эпик, так и все его подзадачи.
        System.out.println("Удаляем эпик с тремя подзадачами и проверяем, что он и его подзадачи пропали из истории...");
        taskManager.deleteEpicById(3);
        System.out.println(taskManager.getHistory());
        System.out.println();

        // С помощью сеттеров экземпляры задач позволяют изменить любое своё поле,
        // но это может повлиять на данные внутри менеджера. Протестируйте эти кейсы и
        // подумайте над возможными вариантами решения проблемы.
        System.out.println("Тестируем сеттеры и проверяем, что параметры задачи изменились в истории...");
        taskManager.getEpicById(7).setStatus(DONE);
        taskManager.getEpicById(7).setId(8);
        System.out.println(taskManager.getHistory());
    }
}