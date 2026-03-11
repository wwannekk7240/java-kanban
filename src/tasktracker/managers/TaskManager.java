package tasktracker.managers;


import tasktracker.tasks.Epic;
import tasktracker.tasks.Subtask;
import tasktracker.tasks.Task;
import java.util.List;

public interface TaskManager {

    Task createTask(Task task);

    Epic createEpic(Epic epic);

    void createSubtask(Subtask subtask);

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpicProgress(Epic epic);

    void updateEpic(Epic epic);

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<Subtask> getAllSubtasks();

    Epic getEpic(int id);

    Task getTask(int id);

    Subtask getSubtask(int id);

    Epic removeEpic(int epicId);

    Task removeTask(int id);

    Subtask removeSubtask(int subtaskId);

    Epic clearEpic();

    Task clearTask();

    Subtask clearSubtask();

    List<Subtask> getAllSubtasksForEpic(int epicId);

    List<Task> getPrioritizedTasks();

}
