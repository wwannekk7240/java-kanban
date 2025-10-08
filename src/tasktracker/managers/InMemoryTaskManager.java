package tasktracker.managers;

import tasktracker.tasks.Epic;
import tasktracker.tasks.Progress;
import tasktracker.tasks.Subtask;
import tasktracker.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected int nextId = 1;
    protected final HistoryManager historyManager = Managers.getDefaultHistory();

    private Task deepCopyTask(Task original) {
        if (original == null) {
            return null;
        }
        Task copy;
        if (original instanceof Epic) {
            Epic epic = (Epic) original;
            copy = new Epic(epic.getName(), epic.getDescription());
            Epic epicCopy = (Epic) copy;
            List<Integer> subtaskIdCopy = new ArrayList<>(epic.getSubtaskId());
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

    @Override
    public Task createTask(Task task) {
        if (task.getId() == 0) {
            task.setId(nextId++);
        } else {
            if (tasks.containsKey(task.getId())) {
                throw new IllegalArgumentException("Задача с ID " + task.getId() + " уже существует");
            }
            if (task.getId() >= nextId) {
                nextId = task.getId() + 1;
            }
        }
        Task taskCopy = deepCopyTask(task);
        tasks.put(taskCopy.getId(), taskCopy);
        return deepCopyTask(taskCopy);
    }

    @Override
    public Epic createEpic(Epic epic) {
        if (epic.getId() == 0) {
            epic.setId(nextId++);
        } else {
            if (epics.containsKey(epic.getId())) {
                throw new IllegalArgumentException("Эпик с ID " + epic.getId() + " уже существует");
            }
            if (epic.getId() >= nextId) {
                nextId = epic.getId() + 1;
            }
        }
        Epic epicCopy = (Epic) deepCopyTask(epic);
        epics.put(epicCopy.getId(), epicCopy);
        return (Epic) deepCopyTask(epicCopy);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        int epicId = subtask.getEpicId();
        if (subtask.getEpicId() == subtask.getId()) {
            throw new IllegalArgumentException("Эпик не может быть собственной подзадачей");
        }
        if (subtask.getId() == subtask.getEpicId()) {
            throw new IllegalArgumentException("Подзадача не может быть своим же эпиком");
        }
        if (!epics.containsKey(epicId)) return;
        if (subtask.getId() == 0) {
            subtask.setId(nextId++);
        } else {
            if (subtasks.containsKey(subtask.getId())) {
                throw new IllegalArgumentException("Подзадача с ID " + subtask.getId() + " уже существует");
            }
            if (subtask.getId() >= nextId) {
                nextId = subtask.getId() + 1;
            }
        }
        Subtask subtaskCopy = (Subtask) deepCopyTask(subtask);
        subtasks.put(subtaskCopy.getId(), subtaskCopy);
        Epic epic = epics.get(epicId);
        epic.getSubtaskId().add(subtaskCopy.getId());
        updateEpicProgress(epic);
    }

    @Override
    public void updateTask(Task task) {
        if (task != null && tasks.containsKey(task.getId())) {
            Task taskCopy = deepCopyTask(task);
            tasks.put(taskCopy.getId(), taskCopy);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask != null && subtasks.containsKey(subtask.getId())) {
            Subtask subtaskCopy = (Subtask) deepCopyTask(subtask);
            subtasks.put(subtaskCopy.getId(), subtaskCopy);
            Epic epic = epics.get(subtaskCopy.getEpicId());
            updateEpicProgress(epic);
        }
    }

    @Override
    public void updateEpicProgress(Epic epic) {
        if (epic == null && !epics.containsKey(epic.getId())) {
            return;
        }

        boolean hasSubtasks = !epic.getSubtaskId().isEmpty();
        boolean allDone = true;
        boolean allNew = true;

        for (Integer subtaskId : epic.getSubtaskId()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask == null) continue;
            if (subtask.getProgress() != Progress.NEW) allNew = false;
            if (subtask.getProgress() != Progress.DONE) allDone = false;
            break;
        }

        if (allDone) {
            epic.setProgress(Progress.DONE);
        } else if (!hasSubtasks || allNew) {
            epic.setProgress(Progress.NEW);
        } else {
            epic.setProgress(Progress.IN_PROGRESS);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic == null || !epics.containsKey(epic.getId())) return;
        Epic epicCopy = (Epic) deepCopyTask(epic);
        updateEpicProgress(epicCopy);
        epics.put(epicCopy.getId(), epicCopy);
    }

    @Override
    public List<Task> getAllTasks() {
        List<Task> copies = new ArrayList<>();
        for (Task task : tasks.values()) {
            copies.add(deepCopyTask(task));
        }
        return copies;
    }

    @Override
    public List<Epic> getAllEpics() {
        List<Epic> copies = new ArrayList<>();
        for (Epic epic : epics.values()) {
            copies.add((Epic) deepCopyTask(epic));
        }
        return copies;
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        List<Subtask> copies = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            copies.add((Subtask) deepCopyTask(subtask));
        }
        return copies;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            Epic epicCopy = (Epic) deepCopyTask(epic);
            historyManager.add(epicCopy);
        }
        return epic;
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            Task taskCopy = deepCopyTask(task);
            historyManager.add(taskCopy);
        }
        return task;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            Subtask subtaskCopy = (Subtask) deepCopyTask(subtask);
            historyManager.add(subtaskCopy);
        }
        return subtask;
    }

    @Override
    public Epic removeEpic(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null)  return null;
        for (Integer subtaskId : epic.getSubtaskId()) {
            subtasks.remove(subtaskId);
        }
        epics.remove(epicId);
        historyManager.remove(epicId);
        return null;
    }

    @Override
    public Task removeTask(int id) {
        tasks.remove(id);
        historyManager.remove(id);
        return null;
    }

    @Override
    public Subtask removeSubtask(int subtaskId) {
        Subtask subtask = subtasks.remove(subtaskId);
        if (subtask == null) return null;
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.getSubtaskId().remove((Integer) subtaskId);
            updateEpicProgress(epic);
        }
        historyManager.remove(subtaskId);
        return null;
    }

    @Override
    public Epic clearEpic() {
        epics.clear();
        subtasks.clear();
        return null;
    }

    @Override
    public Task clearTask() {
        tasks.clear();
        return null;
    }

    @Override
    public Subtask clearSubtask() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtaskId().clear();
            updateEpicProgress(epic);
        }
        return null;
    }

    @Override
    public List<Subtask> getAllSubtasksForEpic(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return new ArrayList<>();
        ArrayList<Subtask> result = new ArrayList<>();
        for (Integer subtaskId : epic.getSubtaskId()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask != null) {
                result.add(subtask);
            }
        }
        return result;
    }
}

