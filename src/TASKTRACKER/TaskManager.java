package TASKTRACKER;

import java.util.ArrayList;
import  java.util.HashMap;

public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int nextId = 1;

    public Task createTask(String name, String description) {
        Task task = new Task(name, description);
        task.setId(nextId++);
        tasks.put(task.getId(), task);
        return task;
    }

    public Epic createEpic(String name, String description) {
        Epic epic = new Epic(name, description);
        epic.setId(nextId++);
        epics.put(epic.getId(), epic);
        return epic;
    }

    public Subtask createSubtask(String name, String description, int epicId) {
        if (!epics.containsKey(epicId)) return null;
        Subtask subtask = new Subtask(name, description, epicId);
        subtask.setId(nextId++);
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(epicId);
        epic.getSubtaskId().add(subtask.getId());
        updateEpicProgress(epicId);
        return subtask;
    }

    public void updateTaskProgress(int taskId, TaskProgress.Progress progress) {
        Task task = tasks.get(taskId);
        if (task != null) {
            task.setProgress(progress);
        }
    }

    public void updateSubtaskProgress(int subtaskId, TaskProgress.Progress progress) {
        Subtask subtask = subtasks.get(subtaskId);
        if (subtask != null) {
            subtask.setProgress(progress);
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

        for (int subtaskId : epic.getSubtaskId()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask == null) continue;
            if (subtask.getProgress() != TaskProgress.Progress.DONE) allDone = false;
            break;
        }
        if (!hasSubtasks) {
            epic.setProgress(TaskProgress.Progress.NEW);
        } else if (allDone) {
            epic.setProgress(TaskProgress.Progress.DONE);
        } else {
            epic.setProgress(TaskProgress.Progress.IN_PROGRESS);
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
}
