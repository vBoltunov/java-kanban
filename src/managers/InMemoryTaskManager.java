package managers;

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

    private static final Comparator<Task> taskComparator = Comparator.comparing(
            Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(Task::getId);

    Logger inMemoryLogger = Logger.getLogger(getClass().getName());

    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, Epic> epics = new HashMap<>();
    protected HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected Set<Task> prioritizedTasks = new TreeSet<>(taskComparator);
    protected HistoryManager historyManager = Managers.getDefaultHistory();

    private int currentId = 0;

    public int generateId() {
        return ++currentId;
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
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
            inMemoryLogger.info("Эпик не существует");
            return Collections.emptyList();
        }
        List<Subtask> list = new ArrayList<>();
        for (Integer subtaskId : epic.getEpicSubtasks()) {
            list.add(subtasks.get(subtaskId));
        }
        return list;
    }

    @Override
    public void deleteAllTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
            prioritizedTasks.remove(task);
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
            prioritizedTasks.remove(subtask);
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
        if (epic != null) {
            historyManager.add(epic);
        }

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
        prioritizedTasks.add(task);
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
            inMemoryLogger.info("Некорректная подзадача: передана пустая подзадача");
            return null;
        }

        if (!epics.containsKey(subtask.getEpicId())) {
            inMemoryLogger.info("Некорректная подзадача: отсутствует эпик для переданной подзадачи");
            return null;
        }

        if (!isValid(subtask)) {
            inMemoryLogger.info("Подзадача пересекается с другими задачами");
            return null;
        }

        subtask.setId(generateId());
        subtasks.put(subtask.getId(), subtask);

        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtask(subtask.getId());
        epic.setStatus(calculateStatus(epic));
        createEpicDateTime(epic);

        prioritizedTasks.add(subtask);

        return subtask;
    }

    @Override
    public void updateTask(Task task) {
        if (task == null || !tasks.containsKey(task.getId())) {
            return;
        }

        Task existingTask = tasks.get(task.getId());

        if (task.getStatus() == null) {
            task.setStatus(existingTask.getStatus());
        }

        if (isValid(task)) {
            tasks.put(task.getId(), task);
        } else {
            throw new IllegalArgumentException(
                    "Задача пересекается с другими задачами или передана некорректная задача");
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic == null || !epics.containsKey(epic.getId())) {
            inMemoryLogger.info("Передан пустой эпик или некорректный id эпика");
            return;
        }

        Epic savedEpic = epics.get(epic.getId());
        savedEpic.setName(epic.getName());
        savedEpic.setDescription(epic.getDescription());
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask == null || !subtasks.containsKey(subtask.getId())) {
            inMemoryLogger.info("Передана пустая подзадача или некорректный id подзадачи");
            return;
        }

        int epicId = subtask.getEpicId();
        if (!epics.containsKey(epicId)) {
            inMemoryLogger.info("Некорректный эпик для подзадачи");
            return;
        }
        List<Integer> epicSubtaskList = epics.get(epicId).getEpicSubtasks();
        if (!epicSubtaskList.contains(subtask.getId())) {
            inMemoryLogger.info("Неправильно указан эпик в подзадаче");
            return;
        }

        Subtask savedSubtask = subtasks.get(subtask.getId());
        prioritizedTasks.remove(savedSubtask);
        if (!isValid(subtask)) {
            inMemoryLogger.info("Подзадача пересекается с другими задачами");
            prioritizedTasks.add(savedSubtask);
            return;
        }

        if (subtask.getStatus() == null) {
            subtask.setStatus(Status.NEW);
        }
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(epicId);
        epic.setStatus(calculateStatus(epic));
        createEpicDateTime(epic);
        prioritizedTasks.add(subtask);
    }

    @Override
    public void deleteTaskById(int id) {
        if (!tasks.containsKey(id)) {
            inMemoryLogger.info("Задача не найдена");
            return;
        }
        prioritizedTasks.remove(tasks.get(id));
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        if (!epics.containsKey(id)) {
            inMemoryLogger.info("Эпик не найден");
            return;
        }
        Epic savedEpic = epics.get(id);

        for (Integer subtaskId : savedEpic.getEpicSubtasks()) {
            historyManager.remove(subtaskId);
            subtasks.remove(subtaskId);
        }
        epics.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteSubtaskById(int id) {
        if (!subtasks.containsKey(id)) {
            inMemoryLogger.info("Подзадача не найдена");
            return;
        }
        Subtask subtask = subtasks.get(id);
        int savedEpicId = subtask.getEpicId();
        Epic savedEpic = epics.get(savedEpicId);
        prioritizedTasks.remove(subtask);
        subtasks.remove(id);
        savedEpic.deleteSubtask(id);
        historyManager.remove(id);

        savedEpic.setStatus(calculateStatus(savedEpic));
    }

    @Override
    public Status calculateStatus(Epic epic) {
        List<Integer> subtaskList = epic.getEpicSubtasks();
        if (subtaskList.isEmpty()) return Status.NEW;

        int newStatus = 0;
        int doneStatus = 0;
        for (Integer subtaskId : subtaskList) {
            Status status = subtasks.get(subtaskId).getStatus();
            if (status.equals(Status.NEW)) newStatus++;
            if (status.equals(Status.DONE)) doneStatus++;
        }

        if (newStatus == subtaskList.size()) return Status.NEW;
        if (doneStatus == subtaskList.size()) return Status.DONE;
        return Status.IN_PROGRESS;
    }

    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        Epic epic = getEpicById(epicId);
        if (epic == null) {
            return Collections.emptyList();
        }

        List<Subtask> epicSubtasks = new ArrayList<>();

        for (Integer subtaskId : epic.getEpicSubtasks()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask != null) {
                epicSubtasks.add(subtask);
            }
        }

        return epicSubtasks;
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
        List<Integer> subtaskList = epic.getEpicSubtasks();
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

    public boolean isValid(Task task) {
        for (Task existingTask : tasks.values()) {
            if (existingTask.getId() != task.getId() &&
                    (task.getStartTime().isBefore(existingTask.getEndTime()) &&
                            task.getEndTime().isAfter(existingTask.getStartTime()))) {
                return false;
            }
        }
        return true;
    }
}