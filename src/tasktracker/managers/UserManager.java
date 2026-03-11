package tasktracker.managers;

import tasktracker.tasks.Task;
import tasktracker.user.User;
import java.util.List;
import java.util.ArrayList;


public interface UserManager {

    int add(User user);

    void update(User user);

    User getById(int id);

    List<User> getAll();

    List<Task> getUserTask(int id);

    void delete(int id);

    TaskManager getTaskManager();
}
