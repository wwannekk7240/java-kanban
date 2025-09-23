package tasktracker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        Task taskWithId = new Task("Task", "Мимоза");
        taskWithId.setId(100);
        taskManager.createTask(taskWithId);

        Task autoIdTask = new Task("Auto", "Оливье");
        Task createdTask = taskManager.createTask(autoIdTask);
        int autoId = createdTask.getId();

        assertNotEquals(100, autoId, "Сгенерированный id не должен конфликтовать с заданным");
        assertNotNull(taskManager.getTask(100));
        assertNotNull(taskManager.getTask(autoId));
    }

    @Test
    void taskShouldRemainUnchangedAfterAdding() {
        Task original = new Task("Настоящий", "Описание");
        original.setId(1);
        String originalName = original.getName();
        String originalDesc = original.getDescription();

        taskManager.createTask(original);
        Task fromManager = taskManager.getTask(1);

        assertEquals(originalName, fromManager.getName());
        assertEquals(originalDesc, fromManager.getDescription());
        assertEquals(original.getProgress(), fromManager.getProgress());
    }

}