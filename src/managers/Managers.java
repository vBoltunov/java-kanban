package managers;

public class Managers {

    private Managers() {
        throw new IllegalStateException("Утилитарный класс");
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
