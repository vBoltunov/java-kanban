package infrastructure;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManager {
    protected HashMap<Integer, Task> tasks;
    protected HashMap<Integer, Epic> epics;
    protected HashMap<Integer, Subtask> subtasks;

    protected int taskId = 0;
    protected int epicId = 0;
    protected int subtaskId = 0;


    public TaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
    }

    private int generateTaskId() {
        return ++taskId;
    }

    private int generateEpicId() {
        return ++epicId;
    }

    private int generateSubtaskId() {
        return ++subtaskId;
    }

    // Получение списка всех задач
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

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
    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void deleteAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.removeSubtasks();
            epic.setStatus(calculateStatus(epic));
        }
    }

    // пересчёт статуса эпика
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
