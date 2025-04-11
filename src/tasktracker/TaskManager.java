package tasktracker;

import java.util.ArrayList;
import java.util.Collections;
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

    public Subtask createSubtask(Subtask subtask) {
        int epicId = subtask.getEpicId();
        if (!epics.containsKey(epicId)) return null;
        subtask.setId(nextId++);
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(epicId);
        epic.getSubtaskId().add(subtask.getId());
        updateEpicProgress(epicId);
        return subtask;
    }

    public void updateTaskProgress(Task task) {
        if (task != null && tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    public void updateSubtaskProgress(Subtask subtask) {
        if (subtask != null && subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            updateEpicProgress(subtask.getEpicId());
        }
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

    private void updateEpicProgress(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return;

        boolean hasSubtasks = !epic.getSubtaskId().isEmpty();
        boolean allDone = true;
        boolean allNew = true;

        for (int subtaskId : epic.getSubtaskId()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask == null) continue;
            if (subtask.getProgress() != Progress.DONE) allDone = false;
            break;
        }

        for (int subtaskId : epic.getSubtaskId()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask == null) continue;
            if (subtask.getProgress() != Progress.NEW) allNew = false;
            break;
        }

        if (!hasSubtasks || allNew) {
            epic.setProgress(Progress.NEW);
        } else if (allDone) {
            epic.setProgress(Progress.DONE);
        } else {
            epic.setProgress(Progress.IN_PROGRESS);
        }
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

    public Epic removeEpic(int id) {
        epics.remove(id);
        return null;
    }

    public Task removeTask(int id) {
        tasks.remove(id);
        return null;
    }

    public Subtask removeSubtask(int id) {
        subtasks.remove(id);
        return null;
    }

    public Epic clearEpic() {
        epics.clear();
        return null;
    }

    public Task clearTask() {
        tasks.clear();
        return null;
    }

    public Subtask clearSubtask() {
        subtasks.clear();
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
