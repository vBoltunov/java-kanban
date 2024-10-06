package infrastructure;

import model.Epic;
import model.enums.Status;
import model.Subtask;
import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

public class InMemoryTaskManager implements TaskManager {

    Logger logger = Logger.getLogger(getClass().getName());

    protected static HashMap<Integer, Task> tasks;
    protected static HashMap<Integer, Epic> epics;
    protected static HashMap<Integer, Subtask> subtasks;
    protected static HistoryManager historyManager;
    protected static Set<Task> prioritizedTasks;

    protected int id = 0;

    private static final Comparator<Task> taskComparator = Comparator.comparing(
            Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(Task::getId);

    public InMemoryTaskManager(HistoryManager historyManager) {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        InMemoryTaskManager.historyManager = historyManager;
        prioritizedTasks = new TreeSet<>(taskComparator);
    }

    public int generateId() {
        return ++id;
    }

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
            logger.info("Такого эпика не существует");
            return Collections.emptyList();
        }
        List<Subtask> list = new ArrayList<>();
        for (Integer subtaskId : epic.getSubtasks()) {
            list.add(subtasks.get(subtaskId));
        }
        return list;
    }

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
            logger.info("Не передан номер эпика для записи подзадачи");
            return null;
        }
        subtask.setId(generateId());
        subtasks.put(subtask.getId(), subtask);

        Epic epic = epics.get(saved);

        epic.addSubtask(subtask.getId());

        epic.setStatus(calculateStatus(epic));
        return subtask;
    }

    @Override
    public void updateTask(Task task) {
        if (task == null) {
            logger.info("Передана пустая задача");
            return;
        }
        if (!tasks.containsKey(task.getId())) {
            logger.info("Некорректный номер задачи");
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
            logger.info("Передан пустой эпик");
            return;
        }
        if (!epics.containsKey(epic.getId())) {
            logger.info("Некорректный номер эпика");
            return;
        }
        Epic saved = epics.get(epic.getId());
        saved.setName(epic.getName());
        saved.setDescription(epic.getDescription());
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask == null) {
            logger.info("Передана пустая подзадача");
            return;
        }
        if (!subtasks.containsKey(subtask.getId())) {
            logger.info("Некорректный номер подзадачи");
            return;
        }
        int epicId = subtask.getEpicId();
        if (!epics.containsKey(epicId)) {
            logger.info("Подзадача имеет некорректный номер эпика");
            return;
        }
        List<Integer> epicSubtaskList = epics.get(epicId).getSubtasks();
        if (!epicSubtaskList.contains(subtask.getId())) {
            logger.info("Неправильно указан эпик в подзадаче");
            return;
        }

        if (subtask.getStatus() == null) {
            subtask.setStatus(Status.NEW);
        }
        subtasks.put(subtask.getId(), subtask);

        Epic epic = epics.get(epicId);
        epic.setStatus(calculateStatus(epic));
    }

    @Override
    public void deleteTaskById(int id) {
        if (!tasks.containsKey(id)) {
            logger.info("Задача с переданным id отсутствует в списке задач");
            return;
        }
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        if (!epics.containsKey(id)) {
            logger.info("Эпик с переданным id отсутствует в списке эпиков");
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
            logger.info("Подзадача с переданным id отсутствует в списке подзадач");
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

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private void createEpicDateTime(Epic epic) {
        List<Integer> subtaskList = epic.getSubtasks();
        if (subtaskList.isEmpty()) {
            epic.setDuration(null);
            epic.setStartTime(null);
            epic.setEndTime(null);
            return;
        }

        for (Integer subtaskId : subtaskList) {
            updateEpicWithSubtask(epic, subtasks.get(subtaskId));
        }
    }

    private void updateEpicWithSubtask(Epic epic, Subtask subtask) {
        LocalDateTime subtaskStartTime = subtask.getStartTime();
        LocalDateTime subtaskEndTime = subtask.getEndTime();
        Duration subtaskDuration = subtask.getDuration();

        if (epic.getStartTime() == null || epic.getStartTime().isAfter(subtaskStartTime)) {
            epic.setStartTime(subtaskStartTime);
        }

        if (epic.getEndTime() == null || subtaskEndTime.isAfter(epic.getEndTime())) {
            epic.setEndTime(subtaskEndTime);
        }

        if (epic.getDuration() == null) {
            epic.setDuration(subtaskDuration);
        } else {
            epic.setDuration(epic.getDuration().plus(subtaskDuration));
        }
    }

    private boolean isValid(Task task) {
        if (task.getStartTime() == null || task.getEndTime() == null) return false;
        LocalDateTime startOfReceived = task.getStartTime();
        LocalDateTime endOfReceived = task.getEndTime();

        return prioritizedTasks.stream().noneMatch(savedTask -> {
            if (savedTask.getStartTime() == null || savedTask.getEndTime() == null) return false;
            LocalDateTime startOfSaved = savedTask.getStartTime();
            LocalDateTime endOfSaved = savedTask.getEndTime();
            return (startOfReceived.isBefore(endOfSaved) && endOfReceived.isAfter(startOfSaved)) ||
                    (startOfReceived.isEqual(startOfSaved) || endOfReceived.isEqual(endOfSaved));
        });
    }
}
