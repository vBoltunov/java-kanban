package infrastructure;

public class Managers {

    public static TaskManager getDefault() {
        return null;
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
