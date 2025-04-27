package tasktracker;

import java.util.ArrayList;
import  java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int nextId = 1;
    private final HistoryManager historyManager = Managers.getDefaultHistory();

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
        tasks.put(task.getId(), task);
        return task;
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
        epics.put(epic.getId(), epic);
        return epic;
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
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(epicId);
        epic.getSubtaskId().add(subtask.getId());
        updateEpicProgress(epic);
    }

    @Override
    public void updateTask(Task task) {
        if (task != null && tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask != null && subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
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
        updateEpicProgress(epic);
        epics.put(epic.getId(), epic);
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
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
        return null;
    }

    @Override
    public Task removeTask(int id) {
        tasks.remove(id);
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

