package tasktracker;

import java.util.ArrayList;

public class InMemoryHistoryManager implements  HistoryManager{

    private final ArrayList<Task> history = new ArrayList<>(10);

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(history);
    }

    @Override
    public void add(Task task) {
        if (history.size() >= 10) {
            history.removeFirst();
        }
        history.add(copyTask(task));
    }

    private Task copyTask(Task original) {
        Task copy;
        if (original instanceof Epic) {
            Epic epic = (Epic) original;
            copy = new Epic(epic.getName(), epic.getDescription());
        } else if (original instanceof Subtask) {
            Subtask subtask = (Subtask) original;
            copy = new Subtask(subtask.getName(), subtask.getDescription(), subtask.getEpicId());
        } else {
            copy = new Task(original.getName(), original.getDescription());
        }
        copy.setId(original.getId());
        copy.setProgress(original.getProgress());
        return copy;
    }
}
