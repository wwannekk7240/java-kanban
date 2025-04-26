package tasktracker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
}