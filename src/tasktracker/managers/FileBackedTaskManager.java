package tasktracker.managers;

import tasktracker.tasks.TaskType;
import tasktracker.tasks.Epic;
import tasktracker.tasks.Progress;
import tasktracker.tasks.Subtask;
import tasktracker.tasks.Task;

import java.util.List;
import java.util.ArrayList;
import java.io.*;
import java.nio.file.Files;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public void save() throws ManagerSaveException {
        try {
            List<String> lines = new ArrayList<>();
            for (Task task : getAllTasks()) {
                lines.add(toString(task));
            }
            for (Epic epic : getAllEpics()) {
                lines.add(toString(epic));
            }
            for (Subtask subtask : getAllSubtasks()) {
                lines.add(toString(subtask));
            }
            Files.write(file.toPath(), lines);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения в файл", e);
        }
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public Task createTask(Task task) {
        Task result = super.createTask(task);
        save();
        return result;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic result = super.createEpic(epic);
        save();
        return result;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateEpicProgress(Epic epic) {
        super.updateEpicProgress(epic);
        save();
    }

    @Override
    public Task removeTask(int id) {
        Task result = super.removeTask(id);
        save();
        return result;
    }

    @Override
    public Epic removeEpic(int epicId) {
        Epic result = super.removeEpic(epicId);
        save();
        return result;
    }

    @Override
    public Subtask removeSubtask(int subtaskId) {
        Subtask result = super.removeSubtask(subtaskId);
        save();
        return result;
    }

    @Override
    public Epic clearEpic() {
        Epic result = super.clearEpic();
        save();
        return result;
    }

    @Override
    public Task clearTask() {
        Task result = super.clearTask();
        save();
        return result;
    }

    @Override
    public Subtask clearSubtask() {
        Subtask result = super.clearSubtask();
        save();
        return result;
    }

    private String toString(Task task) {
        String epicId = "";
        if (task.getType() == TaskType.SUBTASK) {
            epicId = String.valueOf(((Subtask) task).getEpicId());
        }
        return String.format("%d,%s,%s,%s,%s,%s",
                task.getId(),
                task.getType(),
                task.getName(),
                task.getProgress(),
                task.getDescription(),
                epicId);
    }

    private static Task fromString(String value) throws IllegalArgumentException {
        String[] parts = value.split(",");
        int id = Integer.parseInt(parts[0]);
        TaskType type = TaskType.valueOf(parts[1]);
        String name = parts[2];
        Progress status = Progress.valueOf(parts[3]);
        String description = parts[4];
        Task task = switch (type) {
            case TASK -> new Task(name, description, null, null);
            case EPIC -> new Epic(name, description, null, null);
            case SUBTASK -> {
                int epicId = Integer.parseInt(parts[5]);
                yield new Subtask(name, description, null, null, epicId);
            }
            default -> throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        };
        task.setId(id);
        task.setProgress(status);
        return task;
    }

    public static FileBackedTaskManager loadFromFile(File file) throws ManagerSaveException {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try {
            if (!file.exists()) {
                return manager;
            }
            List<String> lines = Files.readAllLines(file.toPath());
            if (lines.isEmpty()) {
                return manager;
            }
            List<Subtask> subtaskToProcess = new ArrayList<>();
            for (String line : lines) {
                if (!line.isEmpty()) {
                    Task task = fromString(line);
                    if (task.getType() == TaskType.EPIC) {
                        manager.epics.put(task.getId(), (Epic) task);
                    } else if (task.getType() == TaskType.SUBTASK) {
                        subtaskToProcess.add((Subtask) task);
                        manager.subtasks.put(task.getId(), (Subtask) task);
                    } else {
                        manager.tasks.put(task.getId(), task);
                    }
                    if (task.getId() > manager.nextId) {
                        manager.nextId = task.getId();
                    }
                }
            }
            for (Subtask subtask : subtaskToProcess) {
                Epic epic = manager.epics.get(subtask.getEpicId());
                if (epic != null) {
                    epic.addSubtaskId(subtask.getId());
                }
            }
            for (Epic epic : manager.epics.values()) {
                manager.updateEpicProgress(epic);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки из файла", e);
        }
        return manager;
    }

}
