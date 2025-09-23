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
    void testTaskModificationThroughSettersDoesNotAffectManager() {
        Task task = new Task("OName", "ODescription");
        task.setProgress(Progress.NEW);
        Task createdTask = taskManager.createTask(task);
        int taskId = createdTask.getId();

        task.setName("MName");
        task.setDescription("MDescription");
        task.setProgress(Progress.DONE);
        Task savedTask = taskManager.getTask(taskId);

        assertEquals("OName", savedTask.getName(), "Имя задачи не должно меняться");
        assertEquals("ODescription", savedTask.getDescription(), "Описание задачи не должно меняться");
        assertEquals(Progress.NEW, savedTask.getProgress(), "Статус задачи не должен меняться");
    }
}