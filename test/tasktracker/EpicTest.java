package tasktracker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
class EpicTest {

    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
    }

    @Test
    void epicCantBeAddedAsItsOwnSubtask() {
        Epic epic = new Epic("Epic", "Уборка");
        Epic createdEpic = taskManager.createEpic(epic);
        int epicId = createdEpic.getId();

        Subtask subtask = new Subtask("Invalid subtask", "Грязь", epicId);
        subtask.setId(epicId);

        assertThrows(IllegalArgumentException.class, () -> {
            taskManager.createSubtask(subtask);
        }, "Эпик не должен быть добавлен как собственная подзадача");
    }

    @Test
    void testEpicContainsCorrectSubtaskIds() {
        Epic epic = new Epic("Epic", "Description");
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", epic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", epic.getId());

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        Epic savedEpic = taskManager.getEpic(epic.getId());
        List<Integer> subtaskId = savedEpic.getSubtaskId();

        assertEquals(2, subtaskId.size(), "Эпик должен содержать 2 подзадачи");
        assertTrue(subtaskId.contains(subtask1.getId()), "Эпик должен содержать ID первой подзадачи");
        assertTrue(subtaskId.contains(subtask2.getId()), "Эпик должен содержать ID второй подзадачи");
    }

    @Test
    void testRemoveSubtaskUpdatesEpic() {
        Epic epic = new Epic("Epic", "Description");
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", epic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", epic.getId());

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.removeSubtask(subtask1.getId());

        Epic savedEpic = taskManager.getEpic(epic.getId());
        List<Integer> subtaskIds = savedEpic.getSubtaskId();

        assertEquals(1, subtaskIds.size(), "Эпик должен содержать 1 подзадачу после удаления");
        assertFalse(subtaskIds.contains(subtask1.getId()), "Эпик не должен содержать ID удаленной подзадачи");
        assertTrue(subtaskIds.contains(subtask2.getId()), "Эпик должен содержать ID оставшейся подзадачи");
    }

    @Test
    void testRemoveEpicRemovesSubtasks() {
        Epic epic = new Epic("Epic", "Description");
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", epic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", epic.getId());

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.removeEpic(epic.getId());

        assertNull(taskManager.getSubtask(subtask1.getId()), "Подзадача 1 должна быть удалена");
        assertNull(taskManager.getSubtask(subtask2.getId()), "Подзадача 2 должна быть удалена");
    }
}