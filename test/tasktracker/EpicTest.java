package tasktracker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasktracker.managers.*;
import tasktracker.tasks.Epic;
import tasktracker.tasks.Progress;
import tasktracker.tasks.Subtask;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
class EpicTest {

    private TaskManager taskManager;
    private InMemoryHistoryManager inMemoryHistoryManager;
    private Epic epic;
    private int epicId;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
        inMemoryHistoryManager = new InMemoryHistoryManager();
        Epic newEpic = new Epic("Test Epic", "Test Description", null, null);
        Epic createdEpic = taskManager.createEpic(newEpic);
        assertNotNull(createdEpic, "Эпик должен быть создан успешно");
        this.epicId = createdEpic.getId();
        this.epic = taskManager.getEpic(epicId);
        assertNotNull(this.epic, "Эпик должен быть получен из менеджера");
    }

    @Test
    void epicCantBeAddedAsItsOwnSubtask() {
        Epic epic = new Epic("Epic", "Уборка", null, null);
        Epic createdEpic = taskManager.createEpic(epic);
        int epicId = createdEpic.getId();

        Subtask subtask = new Subtask("Invalid subtask", "Грязь", null, null, epicId);
        subtask.setId(epicId);

        assertThrows(IllegalArgumentException.class, () -> {
            taskManager.createSubtask(subtask);
        }, "Эпик не должен быть добавлен как собственная подзадача");
    }

    @Test
    void testEpicContainsCorrectSubtaskIds() {
        Epic epic = new Epic("Epic", "Description", null, null);
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", null, null, epic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", null, null, epic.getId());

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
        Epic epic = new Epic("Epic", "Description", null, null);
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", null, null, epic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", null, null, epic.getId());

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
        Epic epic = new Epic("Epic", "Description", null, null);
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", null, null, epic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", null, null, epic.getId());

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.removeEpic(epic.getId());

        assertNull(taskManager.getSubtask(subtask1.getId()), "Подзадача 1 должна быть удалена");
        assertNull(taskManager.getSubtask(subtask2.getId()), "Подзадача 2 должна быть удалена");
    }

    @Test
    void epicTiming_shouldCalculateCorrectlyWithMixedTimeSubtasks() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic("Epic", "Desc", null, null);
        Epic createdEpic = manager.createEpic(epic);

        Subtask sub1 = new Subtask("Sub1", "Desc", Duration.ofMinutes(30),
                LocalDateTime.of(2023, 1, 1, 10, 0), createdEpic.getId());
        Subtask sub2 = new Subtask("Sub2", "Desc", Duration.ofMinutes(45),
                LocalDateTime.of(2023, 1, 1, 9, 0), createdEpic.getId());
        Subtask sub3 = new Subtask("Sub3", "Desc", null, null, createdEpic.getId());
        manager.createSubtask(sub1);
        manager.createSubtask(sub2);
        manager.createSubtask(sub3);
        Epic updatedEpic = manager.getEpic(createdEpic.getId());

        assertEquals(Duration.ofMinutes(75), updatedEpic.getDuration());
        assertEquals(LocalDateTime.of(2023, 1, 1, 9, 0), updatedEpic.getStartTime());
        assertEquals(LocalDateTime.of(2023, 1, 1, 10, 30), updatedEpic.getEndTime());
    }

    @Test
    void epicStatus_shouldBeNew_WhenAllSubtasksNew() {
        Subtask subtask1 = new Subtask("Subtask 1", "Description", Duration.ofMinutes(30),
                LocalDateTime.now(), epic.getId());
        subtask1.setProgress(Progress.NEW);
        Subtask subtask2 = new Subtask("Subtask 2", "Description", Duration.ofMinutes(45),
                LocalDateTime.now().plusHours(1), epic.getId());
        subtask2.setProgress(Progress.NEW);

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.updateEpicProgress(epic);
        assertEquals(Progress.NEW, epic.getProgress(),
                "Статус эпика должен быть NEW, когда все подзадачи NEW");
    }

    @Test
    void epicStatus_shouldBeDone_WhenAllSubtasksDone() {
        Subtask subtask1 = new Subtask("Subtask 1", "Description", Duration.ofMinutes(30),
                LocalDateTime.now(), epic.getId());
        subtask1.setProgress(Progress.DONE);
        Subtask subtask2 = new Subtask("Subtask 2", "Description", Duration.ofMinutes(45),
                LocalDateTime.now().plusHours(1), epic.getId());
        subtask2.setProgress(Progress.DONE);

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        Epic updatedEpic = taskManager.getEpic(epicId);
        assertNotNull(updatedEpic, "Эпик не должен быть null");
        assertEquals(Progress.DONE, updatedEpic.getProgress(),
                "Статус эпика должен быть DONE, когда все подзадачи DONE");
    }

    @Test
    void epicStatus_shouldBeInProgress_WhenSubtasksNewAndDone() {
        Subtask subtask1 = new Subtask("Subtask 1", "Description", Duration.ofMinutes(30),
                LocalDateTime.now(), epic.getId());
        subtask1.setProgress(Progress.NEW);
        Subtask subtask2 = new Subtask("Subtask 2", "Description", Duration.ofMinutes(45),
                LocalDateTime.now().plusHours(1), epic.getId());
        subtask2.setProgress(Progress.DONE);

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        Epic updatedEpic = taskManager.getEpic(epicId);
        assertNotNull(updatedEpic, "Эпик не должен быть null");
        assertEquals(Progress.IN_PROGRESS, updatedEpic.getProgress(),
                "Статус эпика должен быть IN_PROGRESS, когда есть подзадачи NEW и DONE");
    }

    @Test
    void epicStatus_shouldBeInProgress_WhenAnySubtaskInProgress() {
        Subtask subtask1 = new Subtask("Subtask 1", "Description", Duration.ofMinutes(30),
                LocalDateTime.now(), epic.getId());
        subtask1.setProgress(Progress.IN_PROGRESS);
        Subtask subtask2 = new Subtask("Subtask 2", "Description", Duration.ofMinutes(45),
                LocalDateTime.now().plusHours(1), epic.getId());
        subtask2.setProgress(Progress.NEW);

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        Epic updatedEpic = taskManager.getEpic(epicId);
        assertNotNull(updatedEpic, "Эпик не должен быть null");
        assertEquals(Progress.IN_PROGRESS, updatedEpic.getProgress(),
                "Статус эпика должен быть IN_PROGRESS, когда любая подзадача IN_PROGRESS");
    }
}