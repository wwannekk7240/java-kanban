package tasktracker.managers;

import tasktracker.tasks.Epic;
import tasktracker.tasks.Progress;
import tasktracker.tasks.Subtask;
import tasktracker.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
            copy = new Epic(epic.getName(), epic.getDescription(), epic.getDuration(), epic.getStartTime());
            Epic epicCopy = (Epic) copy;
            List<Integer> subtaskIdCopy = new ArrayList<>(epic.getSubtaskId());
            for (Integer subtaskId : subtaskIdCopy) {
                epicCopy.addSubtaskId(subtaskId);
            }
        } else if (original instanceof Subtask) {
            Subtask subtask = (Subtask) original;
            copy = new Subtask(subtask.getName(), subtask.getDescription(),
                    subtask.getDuration(), subtask.getStartTime(), subtask.getEpicId());
        } else {
            copy = new Task(original.getName(), original.getDescription(), original.getDuration(),
                    original.getStartTime());
        }
        copy.setId(original.getId());
        copy.setProgress(original.getProgress());
        copy.setDuration(original.getDuration());
        copy.setStartTime(original.getStartTime());
        return copy;
    }

    @Override
    public Task createTask(Task task) throws ManagerValidateException {
        if (task.getStartTime() != null && hasTimeOverlap(task)) {
            throw new ManagerValidateException("Задача пересекается с существующей по времени");
        }
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
        addToPrioritized(taskCopy);
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
    public void createSubtask(Subtask subtask) throws ManagerValidateException {
        if (subtask.getStartTime() != null && hasTimeOverlap(subtask)) {
            throw new ManagerValidateException("Подзадача пересекается с существующей по времени");
        }
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
        updateEpicTiming(epicId);
        addToPrioritized(subtaskCopy);
    }

    @Override
    public void updateTask(Task task) throws ManagerValidateException {
        if (task != null && tasks.containsKey(task.getId())) {
            Task oldTask = tasks.get(task.getId());
            removeFromPrioritized(oldTask);
            if (task.getStartTime() != null && hasTimeOverlap(task)) {
                if (oldTask.getStartTime() != null) {
                    addToPrioritized(oldTask);
                }
            throw new ManagerValidateException("Задача пересекается с существующей по времени");
            }
            Task taskCopy = deepCopyTask(task);
            tasks.put(taskCopy.getId(), taskCopy);
            addToPrioritized(taskCopy);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) throws ManagerValidateException {
        if (subtask != null && subtasks.containsKey(subtask.getId())) {
            Subtask oldSubtask = subtasks.get(subtask.getId());
            removeFromPrioritized(oldSubtask);
            if (subtask.getStartTime() != null && hasTimeOverlap(subtask)) {
                if (oldSubtask.getStartTime() != null) {
                    addToPrioritized(oldSubtask);
                }
            throw new ManagerValidateException("Подзадача пересекается с существующей по времени");
            }
            Subtask subtaskCopy = (Subtask) deepCopyTask(subtask);
            subtasks.put(subtaskCopy.getId(), subtaskCopy);
            Epic epic = epics.get(subtaskCopy.getEpicId());
            updateEpicProgress(epic);
            updateEpicTiming(epic.getId());
            addToPrioritized(subtaskCopy);
        }
    }

    @Override
    public void updateEpicProgress(Epic epic) {
        if (epic == null && !epics.containsKey(epic.getId())) {
            return;
        }

        List<Subtask> epicSubtasks = epic.getSubtaskId().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (epicSubtasks.isEmpty()) {
            epic.setProgress(Progress.NEW);
            return;
        }
        boolean allDone = epicSubtasks.stream()
                .allMatch(subtask -> subtask.getProgress() == Progress.DONE);
        boolean allNew = epicSubtasks.stream()
                .allMatch(subtask -> subtask.getProgress() == Progress.NEW);

        if (allDone) {
            epic.setProgress(Progress.DONE);
        } else if (allNew) {
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
        updateEpicTiming(epicCopy.getId());
        epics.put(epicCopy.getId(), epicCopy);
    }

    private void updateEpicTiming(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            epic.updateEpicTiming(this);
        }
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
            Subtask subtask = subtasks.remove(subtaskId);
            if (subtask != null) {
                removeFromPrioritized(subtask);
            }
        }
        epics.remove(epicId);
        historyManager.remove(epicId);
        return null;
    }

    @Override
    public Task removeTask(int id) {
        Task task = tasks.remove(id);
        if (task != null) {
            removeFromPrioritized(task);
            historyManager.remove(id);
        }
        return null;
    }

    @Override
    public Subtask removeSubtask(int subtaskId) {
        Subtask subtask = subtasks.remove(subtaskId);
        if (subtask != null) {
            removeFromPrioritized(subtask);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.getSubtaskId().remove((Integer) subtaskId);
                updateEpicProgress(epic);
                updateEpicTiming(epic.getId());
            }
        }
        historyManager.remove(subtaskId);
        return null;
    }

    @Override
    public Epic clearEpic() {
        for (Subtask subtask : subtasks.values()) {
            removeFromPrioritized(subtask);
        }
        epics.clear();
        subtasks.clear();
        return null;
    }

    @Override
    public Task clearTask() {
        for (Task task : tasks.values()) {
            removeFromPrioritized(task);
        }
        tasks.clear();
        return null;
    }

    @Override
    public Subtask clearSubtask() {
        for (Subtask subtask : subtasks.values()) {
            removeFromPrioritized(subtask);
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtaskId().clear();
            updateEpicProgress(epic);
            updateEpicTiming(epic.getId());
        }
        return null;
    }

    @Override
    public List<Subtask> getAllSubtasksForEpic(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return new ArrayList<>();
        }
        return epic.getSubtaskId().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    protected final Set<Task> prioritizedTasks = new TreeSet<>(
            Comparator.comparing(Task::getStartTime)
                    .thenComparing(Task::getId)
    );

    @Override
    public List<Task> getPrioritizedTasks() {
        return prioritizedTasks.stream()
                .filter(task -> task.getStartTime() != null)
                .collect(Collectors.toList());
    }

    private void addToPrioritized(Task task) {
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    private void removeFromPrioritized(Task task) {
        prioritizedTasks.remove(task);
    }

    private boolean isTimeOverlap(Task task1, Task task2) {
        if (task1.getStartTime() == null || task2.getStartTime() == null) return false;
        if (task1.equals(task2)) return false;

        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime end1 = task1.getEndTime();
        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end2 = task2.getEndTime();

        return !end1.isBefore(start2) && !end2.isBefore(start1);
    }

    private boolean hasTimeOverlap(Task task) {
        return prioritizedTasks.stream()
                .filter(existingTask -> existingTask.getStartTime() != null)
                .anyMatch(existingTask -> isTimeOverlap(task, existingTask));
    }
}

