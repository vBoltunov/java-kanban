package model;

public class Subtask extends Task {
    private final int epic;

    public Subtask(String name, String description, Status status, int epic) {
        super(name, description, status);
        this.epic = epic;
    }

    public int getEpic() {
        return epic;
    }
}
