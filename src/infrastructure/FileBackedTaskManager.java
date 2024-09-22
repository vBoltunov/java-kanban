package infrastructure;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    public FileBackedTaskManager(HistoryManager historyManager) {
        super(historyManager);
    }
}
