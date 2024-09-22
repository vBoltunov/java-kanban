package infrastructure;

import model.Epic;
import model.enums.Status;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    protected static HashMap<Integer, Task> tasks;
    protected static HashMap<Integer, Epic> epics;
    protected static HashMap<Integer, Subtask> subtasks;
    protected static HistoryManager historyManager;

    protected int id = 0;

    public InMemoryTaskManager(HistoryManager historyManager) {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        InMemoryTaskManager.historyManager = historyManager;
    }

    // Изменил логику генерации id: теперь нумерация "сквозная"
    public int generateId() {
        return ++id;
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
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
        }
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
        }
        epics.clear();

        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
        }
        subtasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        for (Task subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.removeSubtasks();
            epic.setStatus(calculateStatus(epic));
        }
    }

    // Получение по идентификатору
    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task == null) {
            return null;
        }
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            return null;
        }
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            return null;
        }
        historyManager.add(subtask);
        return subtask;
    }

    // Создание задач
    @Override
    public Task createTask(Task task) {
        if (task == null) {
            return null;
        }
        task.setId(generateId());
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        if (epic == null) {
            return null;
        }
        epic.setId(generateId());
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
            System.out.println("Не передан номер эпика для записи подзадачи");
            return null;
        }
        subtask.setId(generateId());
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

    // Удаление по идентификатору - теперь задачи удаляются и из истории просмотров
    @Override
    public void deleteTaskById(int id) {
        if (!tasks.containsKey(id)) {
            System.out.println("Задача с переданным id отсутствует в списке задач");
            return;
        }
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        if (!epics.containsKey(id)) {
            System.out.println("Эпик с переданным id отсутствует в списке эпиков");
            return;
        }
        Epic saved = epics.get(id);

        for (Integer subtaskId : saved.getSubtasks()) {
            historyManager.remove(subtaskId);
            subtasks.remove(subtaskId);
        }
        epics.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteSubtaskById(int id) {
        if (!subtasks.containsKey(id)) {
            System.out.println("Подзадача с переданным id отсутствует в списке подзадач");
            return;
        }
        Subtask subtask = subtasks.get(id);

        int savedEpicId = subtask.getEpicId();
        Epic savedEpic = epics.get(savedEpicId);
        subtasks.remove(id);
        savedEpic.deleteSubtask(id);
        historyManager.remove(id);

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

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
