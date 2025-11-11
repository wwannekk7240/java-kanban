package tasktracker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasktracker.managers.ManagerValidateException;
import tasktracker.managers.TaskManager;
import tasktracker.tasks.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    protected abstract T createTaskManager();

    @BeforeEach
    void setUp() {
        taskManager = createTaskManager();
    }

    @Test
    void createTask_shouldCreateTaskWithValidData() {
        Task task = new Task("Test Task", "Description",
                Duration.ofMinutes(30), LocalDateTime.now());
        Task created = taskManager.createTask(task);
        assertNotNull(created);
        assertEquals(task.getName(), created.getName());
        assertEquals(task.getDescription(), created.getDescription());
    }

    @Test
    void getTask_shouldReturnTaskWhenExists() {
        Task task = new Task("Test", "Desc",
                Duration.ofMinutes(30), LocalDateTime.now());
        Task created = taskManager.createTask(task);
        Task found = taskManager.getTask(created.getId());
        assertNotNull(found);
        assertEquals(created.getId(), found.getId());
    }

    @Test
    void getTask_shouldReturnNullWhenNotExists() {
        Task found = taskManager.getTask(999);
        assertNull(found);
    }

    @Test
    void updateTask_shouldUpdateExistingTask() {
        Task task = new Task("Original", "Desc",
                Duration.ofMinutes(30), LocalDateTime.now());
        Task created = taskManager.createTask(task);
        created.setName("Updated");
        created.setDescription("Updated Desc");
        taskManager.updateTask(created);
        Task updated = taskManager.getTask(created.getId());
        assertEquals("Updated", updated.getName());
        assertEquals("Updated Desc", updated.getDescription());
    }

    @Test
    void removeTask_shouldRemoveExistingTask() {
        Task task = new Task("Test", "Desc",
                Duration.ofMinutes(30), LocalDateTime.now());
        Task created = taskManager.createTask(task);
        taskManager.removeTask(created.getId());
        assertNull(taskManager.getTask(created.getId()));
    }

    @Test
    void epicStatus_shouldBeNewWhenNoSubtasks() {
        Epic epic = new Epic("Epic", "Desc", null, null);
        Epic created = taskManager.createEpic(epic);
        assertEquals(Progress.NEW, created.getProgress());
    }

    @Test
    void epicStatus_shouldBeNewWhenAllSubtasksNew() {
        Epic epic = new Epic("Epic", "Desc", null, null);
        Epic createdEpic = taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Sub1", "Desc", Duration.ofMinutes(30),
                LocalDateTime.now(), createdEpic.getId());
        Subtask subtask2 = new Subtask("Sub2", "Desc", Duration.ofMinutes(45),
                LocalDateTime.now().plusHours(1), createdEpic.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        assertEquals(Progress.NEW, createdEpic.getProgress());
    }

    @Test
    void epicStatus_shouldBeDoneWhenAllSubtasksDone() {
        Epic epic = new Epic("Epic", "Desc", null, null);
        Epic createdEpic = taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Sub1", "Desc", Duration.ofMinutes(30),
                LocalDateTime.now(), createdEpic.getId());
        subtask1.setProgress(Progress.DONE);
        Subtask subtask2 = new Subtask("Sub2", "Desc", Duration.ofMinutes(45),
                LocalDateTime.now().plusHours(1), createdEpic.getId());
        subtask2.setProgress(Progress.DONE);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.updateEpicProgress(createdEpic);
        assertEquals(Progress.DONE, createdEpic.getProgress());
    }

    @Test
    void epicStatus_shouldBeInProgressWhenSubtasksNewAndDone() {
        Epic epic = new Epic("Epic", "Desc", null, null);
        Epic createdEpic = taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Sub1", "Desc", Duration.ofMinutes(30),
                LocalDateTime.now(), createdEpic.getId());
        subtask1.setProgress(Progress.NEW);
        Subtask subtask2 = new Subtask("Sub2", "Desc", Duration.ofMinutes(45),
                LocalDateTime.now().plusHours(1), createdEpic.getId());
        subtask2.setProgress(Progress.DONE);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.updateEpicProgress(createdEpic);
        assertEquals(Progress.IN_PROGRESS, createdEpic.getProgress());
    }

    @Test
    void epicStatus_shouldBeInProgressWhenAnySubtaskInProgress() {
        Epic epic = new Epic("Epic", "Desc", null, null);
        Epic createdEpic = taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Sub1", "Desc", Duration.ofMinutes(30),
                LocalDateTime.now(), createdEpic.getId());
        subtask1.setProgress(Progress.IN_PROGRESS);
        Subtask subtask2 = new Subtask("Sub2", "Desc", Duration.ofMinutes(45),
                LocalDateTime.now().plusHours(1), createdEpic.getId());
        subtask2.setProgress(Progress.NEW);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.updateEpicProgress(createdEpic);
        assertEquals(Progress.IN_PROGRESS, createdEpic.getProgress());
    }

    @Test
    void shouldDetectTimeOverlap() {
        LocalDateTime baseTime = LocalDateTime.now();
        Task task1 = new Task("Task1", "Desc",
                Duration.ofMinutes(60), baseTime);
        Task task2 = new Task("Task2", "Desc",
                Duration.ofMinutes(30), baseTime.plusMinutes(30));
        taskManager.createTask(task1);
        assertThrows(ManagerValidateException.class, () -> {
            taskManager.createTask(task2);
        }, "Должно выбрасываться исключение при пересечении временных интервалов");
    }

    @Test
    void shouldNotDetectTimeOverlapForNonOverlappingTasks() {
        LocalDateTime baseTime = LocalDateTime.now();
        Task task1 = new Task("Task1", "Desc",
                Duration.ofMinutes(30), baseTime);
        Task task2 = new Task("Task2", "Desc",
                Duration.ofMinutes(30), baseTime.plusHours(1));
        taskManager.createTask(task1);
        assertDoesNotThrow(() -> {
            taskManager.createTask(task2);
        }, "Не должно быть исключения для непересекающихся задач");
    }

    @Test
    void shouldAllowTasksWithoutStartTime() {
        Task task = new Task("Task", "Desc", null, null);
        assertDoesNotThrow(() -> {
            taskManager.createTask(task);
        }, "Задачи без времени начала должны создаваться без ошибок");
    }

    @Test
    void getPrioritizedTasks_shouldReturnEmptyListWhenNoTasks() {
        List<Task> prioritized = taskManager.getPrioritizedTasks();
        assertTrue(prioritized.isEmpty());
    }

    @Test
    void getPrioritizedTasks_shouldReturnTasksInCorrectOrder() {
        LocalDateTime now = LocalDateTime.now();
        Task task1 = new Task("Task1", "Desc",
                Duration.ofMinutes(30), now.plusHours(2));
        Task task2 = new Task("Task2", "Desc",
                Duration.ofMinutes(30), now.plusHours(1));
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        List<Task> prioritized = taskManager.getPrioritizedTasks();
        assertEquals(2, prioritized.size());
        assertEquals(task2.getId(), prioritized.get(0).getId());
        assertEquals(task1.getId(), prioritized.get(1).getId());
    }

    @Test
    void getPrioritizedTasks_shouldNotIncludeTasksWithoutStartTime() {
        Task taskWithTime = new Task("With Time", "Desc",
                Duration.ofMinutes(30), LocalDateTime.now());
        Task taskWithoutTime = new Task("Without Time", "Desc", null, null);
        taskManager.createTask(taskWithTime);
        taskManager.createTask(taskWithoutTime);
        List<Task> prioritized = taskManager.getPrioritizedTasks();
        assertEquals(1, prioritized.size());
        assertEquals(taskWithTime.getId(), prioritized.get(0).getId());
    }

    @Test
    void history_shouldAddTaskToHistory() {
        Task task = new Task("Test", "Desc",
                Duration.ofMinutes(30), LocalDateTime.now());
        Task created = taskManager.createTask(task);
        Task fromHistory = taskManager.getTask(created.getId());
        List<Task> history = taskManager.getPrioritizedTasks();
        assertEquals(1, history.size());
        assertEquals(created.getId(), history.get(0).getId());
    }

    @Test
    void history_shouldNotContainDuplicates() {
        Task task = new Task("Test", "Desc",
                Duration.ofMinutes(30), LocalDateTime.now());
        Task created = taskManager.createTask(task);
        taskManager.getTask(created.getId());
        taskManager.getTask(created.getId());
        taskManager.getTask(created.getId());
        List<Task> history = taskManager.getPrioritizedTasks();
        assertEquals(1, history.size(), "История не должна содержать дубликатов");
    }

    @Test
    void history_shouldBeEmptyAfterRemovingTask() {
        Task task = new Task("Test", "Desc",
                Duration.ofMinutes(30), LocalDateTime.now());
        Task created = taskManager.createTask(task);
        taskManager.getTask(created.getId());
        taskManager.removeTask(created.getId());
        List<Task> history = taskManager.getPrioritizedTasks();
        assertTrue(history.isEmpty());
    }
}
