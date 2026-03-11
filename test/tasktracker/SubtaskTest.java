package tasktracker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasktracker.managers.Managers;
import tasktracker.managers.TaskManager;
import tasktracker.tasks.Epic;
import tasktracker.tasks.Subtask;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
    }

    @Test
    void subtasksWithSameIdShouldBeEqual() {
        Subtask subtask1 = new Subtask("Sub 1", "АБВ", null, null, 1);
        subtask1.setId(2);
        Subtask subtask2 = new Subtask("Sub 2", "ВБА", null, null, 1);
        subtask2.setId(2);
        assertEquals(subtask1, subtask2);
    }

    @Test
    void subtaskCantBeItsOwnEpic() {
        Epic epic = new Epic("Test Epic", "Что - то", null, null);
        Epic createdEpic = taskManager.createEpic(epic);
        int epicId = createdEpic.getId();

        Subtask subtask = new Subtask("Invalid Subtask", "Кто - то", null, null, epicId);
        subtask.setId(epicId);

        assertThrows(IllegalArgumentException.class, () -> {
            taskManager.createSubtask(subtask);
        }, "Подзадача не должна быть своим же эпиком");
    }


}