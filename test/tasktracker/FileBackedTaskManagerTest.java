package tasktracker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import tasktracker.managers.FileBackedTaskManager;
import java.io.File;
import java.io.IOException;
import tasktracker.tasks.Epic;
import tasktracker.tasks.Subtask;
import tasktracker.tasks.Task;
import java.util.List;

public class FileBackedTaskManagerTest {
    private File tempFile;
    private FileBackedTaskManager manager;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("tasks", ".csv");
        manager = new FileBackedTaskManager(tempFile);
    }

    @Test
    void saveAndLoadEmptyManager() {
        manager.save();
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertTrue(loadedManager.getAllTasks().isEmpty(), "Задачи должны быть пустыми");
        assertTrue(loadedManager.getAllEpics().isEmpty(), "Эпики должны быть пустыми");
        assertTrue(loadedManager.getAllSubtasks().isEmpty(), "Подзадачи должны быть пустыми");
    }

    @Test
    void saveAndLoadMultipleTasks() {
        Task task1 = new Task("T 1", "D 1", null, null);
        Task task2 = new Task("T 2", "D 2", null, null);
        Epic epic1 = new Epic("E 1", "Epic D 1", null, null);
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic1);
        Subtask subtask1 = new Subtask("S 1", "Sub D 1", null, null, epic1.getId());
        Subtask subtask2 = new Subtask("S 2", "Sub D 2", null, null, epic1.getId());
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.save();

        assertTrue(tempFile.exists(), "Файл должен существовать");
        assertTrue(tempFile.length() > 0, "Файл не должен быть пустым");

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        List<Task> loadedTasks = loadedManager.getAllTasks();
        List<Epic> loadedEpics = loadedManager.getAllEpics();
        List<Subtask> loadedSubtasks = loadedManager.getAllSubtasks();

        assertEquals(2, loadedTasks.size(), "Должно быть 2 задачи");
        assertEquals(1, loadedEpics.size(), "Должен быть 1 эпик");
        assertEquals(2, loadedSubtasks.size(), "Должно быть 2 подзадачи");

        Task loadedTask = loadedTasks.get(0);
        assertEquals("T 1", loadedTask.getName(), "Название задачи должно совпадать");
        assertEquals("D 1", loadedTask.getDescription(), "Описание задачи должно совпадать");
        Epic loadedEpic = loadedManager.getEpic(epic1.getId());
        assertNotNull(loadedEpic, "Эпик должен быть найден");
        assertEquals(2, loadedEpic.getSubtaskId().size(), "У эпика должно быть 2 подзадачи");
    }
}
