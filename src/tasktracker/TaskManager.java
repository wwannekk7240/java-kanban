package tasktracker;

import java.util.ArrayList;

public interface TaskManager {

    Task createTask(Task task);

    Epic createEpic(Epic epic);

    void createSubtask(Subtask subtask);

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpicProgress(Epic epic);

    void updateEpic(Epic epic);

    ArrayList<Task> getAllTasks();

    ArrayList<Epic> getAllEpics();

    ArrayList<Subtask> getAllSubtasks();

    Epic getEpic(int id, HistoryManager historyManager);

    Task getTask(int id, HistoryManager historyManager);

    Subtask getSubtask(int id, HistoryManager historyManager);

    Epic removeEpic(int epicId);

    Task removeTask(int id);

    Subtask removeSubtask(int subtaskId);

    Epic clearEpic();

    Task clearTask();

    Subtask clearSubtask();

    ArrayList<Subtask> getAllSubtasksForEpic(int epicId);

}
