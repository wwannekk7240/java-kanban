package tasktracker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasktracker.managers.HistoryManager;
import tasktracker.managers.Managers;
import tasktracker.managers.TaskManager;
import tasktracker.tasks.Epic;
import tasktracker.tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private TaskManager taskManager;
    private HistoryManager historyManager;
    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    void setUp() {
        historyManager = Managers.getDefaultHistory();
        taskManager = Managers.getDefault();
        task1 = new Task("Task 1", "Description 1");
        task1.setId(1);
        task2 = new Task("Task 2", "Description 2");
        task2.setId(2);
        task3 = new Task("Task 3", "Description 3");
        task3.setId(3);
    }

    @Test
    void managerShouldAddAndFindTasksById() {

        Task task = new Task("Task", "Description");
        Task createdTask = taskManager.createTask(task);
        int taskId = createdTask.getId();

        Epic epic = new Epic("Epic", "Description");
        Epic createdEpic = taskManager.createEpic(epic);
        int epicId = createdEpic.getId();

        assertNotNull(taskManager.getTask(taskId), "Должна находиться задача");
        assertNotNull(taskManager.getEpic(epicId), "Должен находиться эпик");
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

    @Test
    void testAddTaskToHistory() {
        historyManager.add(task1);
        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size(), "История должна содержать 1 задачу");
        assertEquals(task1.getId(), history.get(0).getId(), "ID задачи должен совпадать");
    }

    @Test
    void testRemoveFromBeginning() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task1.getId());
        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size(), "После удаления должно остаться 2 задачи");
        assertEquals(task2.getId(), history.get(0).getId(), "Первой должна быть task2");
        assertEquals(task3.getId(), history.get(1).getId(), "Второй должна быть task3");
    }

    @Test
    void testRemoveFromMiddle() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task2.getId());
        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size(), "После удаления должно остаться 2 задачи");
        assertEquals(task1.getId(), history.get(0).getId(), "Первой должна быть task1");
        assertEquals(task3.getId(), history.get(1).getId(), "Второй должна быть task3");
    }

    @Test
    void testRemoveFromEnd() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task3.getId());
        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size(), "После удаления должно остаться 2 задачи");
        assertEquals(task1.getId(), history.get(0).getId(), "Первой должна быть task1");
        assertEquals(task2.getId(), history.get(1).getId(), "Второй должна быть task2");
    }

    @Test
    void testEmptyHistory() {
        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty(), "История должна быть пустой при инициализации");
    }



}