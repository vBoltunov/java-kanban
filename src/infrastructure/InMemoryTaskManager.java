package infrastructure;

import model.Epic;
import model.enums.Status;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    protected HashMap<Integer, Task> tasks;
    protected HashMap<Integer, Epic> epics;
    protected HashMap<Integer, Subtask> subtasks;

    protected int taskId = 0;
    protected int epicId = 0;
    protected int subtaskId = 0;


    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
    }

    // Получение списка всех задач
    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Subtask> getSubtaskList(Epic epic) {
        if (!epics.containsValue(epic)) {
            System.out.println("Такого эпика не существует");
            return null;
        }
        List<Subtask> list = new ArrayList<>();
        for (Integer subtaskId : epic.getSubtasks()) {
            list.add(subtasks.get(subtaskId));
        }
        return list;
    }

    // Удаление всех задач
    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.removeSubtasks();
            epic.setStatus(calculateStatus(epic));
        }
    }

    // Получение по идентификатору
    @Override
    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    // Создание задач
    @Override
    public Task createTask(Task task) {
        if (task == null) {
            return null;
        }
        task.setId(generateTaskId());
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        if (epic == null) {
            return null;
        }
        epic.setId(generateEpicId());
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        if (subtask == null) {
            return null;
        }
        int saved = subtask.getEpicId();

        if (!epics.containsKey(saved)) {
            System.out.println("Такого эпика не существует");
            return null;
        }
        subtask.setId(generateSubtaskId());
        subtasks.put(subtask.getId(), subtask);

        Epic epic = epics.get(saved);

        epic.addSubtask(subtask.getId());

        epic.setStatus(calculateStatus(epic));
        return subtask;
    }

    // Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    @Override
    public void updateTask(Task task) {
        if (task == null) {
            System.out.println("Передана пустая задача");
            return;
        }
        if (!tasks.containsKey(task.getId())) {
            System.out.println("Некорректный номер задачи");
            return;
        }
        if (task.getStatus() == null) {
            task.setStatus(Status.NEW);
        }
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic == null) {
            System.out.println("Передан пустой эпик");
            return;
        }
        if (!epics.containsKey(epic.getId())) {
            System.out.println("Некорректный номер эпика");
            return;
        }
        Epic saved = epics.get(epic.getId());
        saved.setName(epic.getName());
        saved.setDescription(epic.getDescription());
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask == null) {
            System.out.println("Передана пустая подзадача");
            return;
        }
        if (!subtasks.containsKey(subtask.getId())) {
            System.out.println("Некорректный номер подзадачи");
            return;
        }
        int epicId = subtask.getEpicId();
        if (!epics.containsKey(epicId)) {
            System.out.println("Подзадача имеет некорректный номер эпика");
            return;
        }
        List<Integer> epicSubtaskList = epics.get(epicId).getSubtasks();
        if (!epicSubtaskList.contains(subtask.getId())) {
            System.out.println("Неправильно указан эпик в подзадаче");
            return;
        }

        if (subtask.getStatus() == null) {
            subtask.setStatus(Status.NEW);
        }
        subtasks.put(subtask.getId(), subtask);

        Epic epic = epics.get(epicId);
        epic.setStatus(calculateStatus(epic));
    }

    // Удаление по идентификатору
    @Override
    public void deleteTaskById(int id) {
        if (!tasks.containsKey(id)) {
            System.out.println("Задачи с таким id не существует");
            return;
        }
        tasks.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        if (!epics.containsKey(id)) {
            System.out.println("Эпика с таким id не существует");
            return;
        }
        Epic saved = epics.get(id);

        for (Integer subTaskId : saved.getSubtasks()) {
            subtasks.remove(subTaskId);
        }
        epics.remove(id);
    }

    @Override
    public void deleteSubtaskById(int id) {
        if (!subtasks.containsKey(id)) {
            System.out.println("Подзадачи с таким id не существует");
            return;
        }
        Subtask subtask = subtasks.get(id);

        int savedEpicId = subtask.getEpicId();
        Epic savedEpic = epics.get(savedEpicId);
        subtasks.remove(id);
        savedEpic.deleteSubtask(id);

        savedEpic.setStatus(calculateStatus(savedEpic));
    }

    // пересчёт статуса эпика
    @Override
    public Status calculateStatus(Epic epic) {
        List<Integer> subtaskList = epic.getSubtasks();
        if (subtaskList.isEmpty()) {
            return Status.NEW;
        }
        int newStatus = 0;
        int doneStatus = 0;
        for (Integer subtaskId : subtaskList) {
            if (subtasks.get(subtaskId).getStatus().equals(Status.NEW)) {
                newStatus++;
            }
            if (subtasks.get(subtaskId).getStatus().equals(Status.DONE)) {
                doneStatus++;
            }
        }
        if (newStatus == subtaskList.size()) {
            return Status.NEW;
        }
        if (doneStatus == subtaskList.size()) {
            return Status.DONE;
        }
        return Status.IN_PROGRESS;
    }
}
