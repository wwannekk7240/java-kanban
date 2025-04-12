package tasktracker;

import java.util.ArrayList;
import  java.util.HashMap;

public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int nextId = 1;

    public Task createTask(Task task) {
        task.setId(nextId++);
        tasks.put(task.getId(), task);
        return task;
    }

    public Epic createEpic(Epic epic) {
        epic.setId(nextId++);
        epics.put(epic.getId(), epic);
        return epic;
    }

    public void createSubtask(Subtask subtask) {
        int epicId = subtask.getEpicId();
        if (!epics.containsKey(epicId)) return;
        subtask.setId(nextId++);
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(epicId);
        epic.getSubtaskId().add(subtask.getId());
        updateEpic(epic);
    }

    public void updateTask(Task task) {
        if (task != null && tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    public void updateSubtask(Subtask subtask) {
        if (subtask != null && subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            updateEpic(epic);
        }
    }

    private void updateEpicProgress(Epic epic) {
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

    public void updateEpic(Epic epic) {
        if (epic == null || !epics.containsKey(epic.getId())) return;
        updateEpicProgress(epic);
        epics.put(epic.getId(), epic);
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    public Task getTask(int id) {
        return tasks.get(id);
    }

    public Subtask getSubtask(int id) {
        return subtasks.get(id);
    }

    public Epic removeEpic(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null)  return null;
        for (Integer subtaskId : epic.getSubtaskId()) {
            subtasks.remove(subtaskId);
        }
        epics.remove(epicId);
        return null;
    }

    public Task removeTask(int id) {
        tasks.remove(id);
        return null;
    }

    public Subtask removeSubtask(int subtaskId) {
        Subtask subtask = subtasks.remove(subtaskId);
        if (subtask == null) return null;
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.getSubtaskId().remove((Integer) subtaskId);
            updateEpic(epic);
        }
        return null;
    }

    public Epic clearEpic() {
        epics.clear();
        subtasks.clear();
        return null;
    }

    public Task clearTask() {
        tasks.clear();
        return null;
    }

    public Subtask clearSubtask() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtaskId().clear();
            updateEpic(epic);
        }
        return null;
    }

    public ArrayList<Subtask> getAllSubtasksForEpic(int epicId) {
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
