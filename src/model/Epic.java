package model;

import model.enums.Status;
import model.enums.TaskType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private final List<Integer> subtasks;

    public Epic() {
        this.subtasks = new ArrayList<>();
    }

    public Epic(String name, String description) {
        super(name, description);
        this.subtasks = new ArrayList<>();
    }

    public Epic(int id, String name, String description) {
        super(id, name, description);
        this.subtasks = new ArrayList<>();
    }

    public Epic(int id, String name, String description, Status status) {
        super(id, name, description, status);
        this.subtasks = new ArrayList<>();
    }

    public List<Integer> getEpicSubtasks() {
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
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtasks, epic.subtasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasks);
    }

    @Override
    public String toString() {
        return getId() +
                ",EPIC," +
                getName() + "," +
                getStatus() + "," +
                getDescription() + ", ," +
                getStartTime() + "," +
                getDuration() + "," +
                getEndTime();
    }
}
