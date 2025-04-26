package tasktracker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    private TaskManager taskManager;
    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void tasksWithSameIdShouldBeEqual() {
        Task task1 = new Task("Task 1", "Бад");
        task1.setId(1);
        Task task2 = new Task("Task 2", "Дэб");
        task2.setId(1);

        assertEquals(task1, task2, "Задачи с одинаковым id должны быть равны");
    }

    @Test
    void generatedAndManualIdsShouldNotConflict() {
        Task taskWithId = new Task("Task", "Мимоза");
        taskWithId.setId(100);
        taskManager.createTask(taskWithId);

        Task autoIdTask = new Task("Auto", "Оливье");
        Task createdTask = taskManager.createTask(autoIdTask);
        int autoId = createdTask.getId();

        assertNotEquals(100, autoId, "Сгенерированный id не должен конфликтовать с заданным");
        assertNotNull(taskManager.getTask(100, historyManager));
        assertNotNull(taskManager.getTask(autoId, historyManager));
    }

    @Test
    void taskShouldRemainUnchangedAfterAdding() {
        Task original = new Task("Настоящий", "Описание");
        original.setId(1);
        String originalName = original.getName();
        String originalDesc = original.getDescription();

        taskManager.createTask(original);
        Task fromManager = taskManager.getTask(1, historyManager);

        assertEquals(originalName, fromManager.getName());
        assertEquals(originalDesc, fromManager.getDescription());
        assertEquals(original.getProgress(), fromManager.getProgress());
    }
}