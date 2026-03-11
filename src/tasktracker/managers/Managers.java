package tasktracker.managers;

public final class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static UserManager getUserDefault() { return new InMemoryUserManager(); }
}
