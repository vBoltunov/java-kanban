package model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subtasks = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(int id, String name, String description) {
        super(id, name, description);
    }

    public List<Integer> getSubtasks() {
        return subtasks;
    }

    public void addSubtask(Integer subtaskId) {
        subtasks.add(subtaskId);
    }

    public void deleteSubtask(Integer subtaskId) {
        subtasks.remove(subtaskId);
    }

    public void removeSubtasks() {
        subtasks.clear();
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtasks=" + getSubtasks() +
                ", id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", taskStatus=" + getStatus() +
                '}';
    }
}
