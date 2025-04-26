package tasktracker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private TaskManager taskManager;
    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = Managers.getDefaultHistory();
        taskManager = Managers.getDefault();
    }

    @Test
    void managerShouldAddAndFindTasksById() {

        Task task = new Task("Task", "Description");
        Task createdTask = taskManager.createTask(task);
        int taskId = createdTask.getId();

        Epic epic = new Epic("Epic", "Description");
        Epic createdEpic = taskManager.createEpic(epic);
        int epicId = createdEpic.getId();

        assertNotNull(taskManager.getTask(taskId, historyManager), "Должна находиться задача");
        assertNotNull(taskManager.getEpic(epicId, historyManager), "Должен находиться эпик");
    }

    @Test
    void historyManagerShouldPreserveTaskState() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        Task task = new Task("Task", "Что - то");
        task.setId(1);

        historyManager.add(task);
        task.setDescription("Кто - то");

        Task fromHistory = historyManager.getHistory().getFirst();
        assertEquals("Что - то", fromHistory.getDescription(),
                "История должна хранить первоначальное состояние");
    }
}