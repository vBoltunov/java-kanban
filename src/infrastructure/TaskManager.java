package infrastructure;

import model.Epic;
import model.Subtask;
import model.Task;
import model.enums.Status;

import java.util.List;

public interface TaskManager {

    // методы для генерации идентификаторов
    int generateTaskId();

    int generateEpicId();

    int generateSubtaskId();

    // Получение списка всех задач
    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<Subtask> getAllSubtasks();

    List<Subtask> getSubtaskList(Epic epic);

    // Удаление всех задач
    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubtasks();

    // Получение по идентификатору
    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    // Создание задач
    Task createTask(Task task);

    Epic createEpic(Epic epic);

    Subtask createSubtask(Subtask subtask);

    // Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    // Удаление по идентификатору
    void deleteTaskById(int id);

    void deleteEpicById(int id);

    void deleteSubtaskById(int id);

    // пересчёт статуса эпика
    Status calculateStatus(Epic epic);

    // Получение истории задач
    List<Task> getHistory();
}
