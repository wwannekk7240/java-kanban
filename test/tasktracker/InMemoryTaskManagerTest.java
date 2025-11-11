package tasktracker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasktracker.managers.HistoryManager;
import tasktracker.managers.Managers;
import tasktracker.managers.TaskManager;
import tasktracker.tasks.Epic;
import tasktracker.tasks.Subtask;
import tasktracker.tasks.Task;
import java.time.Duration;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private TaskManager taskManager;
    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void generatedAndManualIdsShouldNotConflict() {
        Task taskWithId = new Task("Task", "Мимоза", null, null);
        taskWithId.setId(100);
        taskManager.createTask(taskWithId);

        Task autoIdTask = new Task("Auto", "Оливье", null, null);
        Task createdTask = taskManager.createTask(autoIdTask);
        int autoId = createdTask.getId();

        assertNotEquals(100, autoId, "Сгенерированный id не должен конфликтовать с заданным");
        assertNotNull(taskManager.getTask(100));
        assertNotNull(taskManager.getTask(autoId));
    }

    @Test
    void taskShouldRemainUnchangedAfterAdding() {
        Task original = new Task("Настоящий", "Описание", null, null);
        original.setId(1);
        String originalName = original.getName();
        String originalDesc = original.getDescription();

        taskManager.createTask(original);
        Task fromManager = taskManager.getTask(1);

        assertEquals(originalName, fromManager.getName());
        assertEquals(originalDesc, fromManager.getDescription());
        assertEquals(original.getProgress(), fromManager.getProgress());
    }

    @Test
    void shouldHandleMultipleTaskTypes() {
        Task task = new Task("Task", "Desc",
                Duration.ofMinutes(30), LocalDateTime.now());
        Epic epic = new Epic("Epic", "Desc", null, null);
        Subtask subtask = new Subtask("Subtask", "Desc", Duration.ofMinutes(15),
                LocalDateTime.now().plusHours(1), 2);
        Task createdTask = taskManager.createTask(task);
        Epic createdEpic = taskManager.createEpic(epic);
        subtask.setEpicId(createdEpic.getId());
        taskManager.createSubtask(subtask);

        assertEquals(1, taskManager.getAllTasks().size());
        assertEquals(1, taskManager.getAllEpics().size());
        assertEquals(1, taskManager.getAllSubtasks().size());
    }

}