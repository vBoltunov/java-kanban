package infrastructure;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> taskHistory = new ArrayList<>(10);

    @Override
    public void add(Task task) {}

    @Override
    public List<Task> getHistory() {
        return taskHistory;
    }
}
