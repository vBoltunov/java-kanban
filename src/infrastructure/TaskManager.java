package infrastructure;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.HashMap;

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
}
