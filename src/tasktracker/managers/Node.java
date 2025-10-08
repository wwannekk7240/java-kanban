package tasktracker.managers;

import tasktracker.tasks.Task;

public class Node {
    Task task;
    Node prev;
    Node next;

    Node(Task task) {
        this.task = task;
    }
}
