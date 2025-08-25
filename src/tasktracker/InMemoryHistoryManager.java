package tasktracker;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements  HistoryManager{

    private final ArrayList<Task> history = new ArrayList<>(10);

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }

    @Override
    public void add(Task task) {
        if (history.size() >= 10) {
            history.removeFirst();
        }
        history.add(copyTask(task));
    }

    @Override
    public void remove(int id) {
        for (int i = history.size() - 1; i >= 0; i--) {
            if (history.get(i).getId() == id) {
                history.remove(i);
            }
        }
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
