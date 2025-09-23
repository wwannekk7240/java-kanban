package tasktracker;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class ManagersTest {

    @Test
    void managersShouldReturnInitializedInstances() {
        TaskManager manager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();

        assertNotNull(manager, "Менеджер задач не должен быть null");
        assertNotNull(historyManager, "Менеджер истории не должен быть null");
    }
  
}