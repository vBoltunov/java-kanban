package managers;

public class Managers {

    private static TaskManager taskManager;

    private Managers() {
        throw new IllegalStateException("Утилитарный класс");
    }

    public static TaskManager getDefaultManager() {
        if (taskManager == null) {
            taskManager = new InMemoryTaskManager();
        }
        return taskManager;
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
