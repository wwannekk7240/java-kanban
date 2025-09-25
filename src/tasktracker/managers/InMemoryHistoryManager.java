package tasktracker.managers;

import tasktracker.tasks.Subtask;
import tasktracker.tasks.Task;
import tasktracker.tasks.Epic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private Node first;
    private Node last;
    private final Map<Integer, Node> nodeHistory = new HashMap<>();

    private static class Node {
        Task task;
        Node prev;
        Node next;

        Node(Task task) {
            this.task = task;
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        int taskId = task.getId();
        if (nodeHistory.containsKey(taskId)) {
            removeNode(nodeHistory.get(taskId));
        }
        Task taskCopy = task.copy();
        linkLast(taskCopy);
        nodeHistory.put(taskId, last);
    }

    @Override
    public void remove(int id) {
        if (nodeHistory.containsKey(id)) {
            removeNode(nodeHistory.get(id));
        }
    }


    public void linkLast(Task task) {
        Node newNode = new Node(task);
        if (last == null) {
            first = newNode;
        } else {
            last.next = newNode;
            newNode.prev = last;
        }
        last = newNode;
    }

    public List<Task> getTasks() {
        List<Task> tasksNode = new ArrayList<>();
        Node current = first;
        while (current != null) {
            tasksNode.add(current.task);
            current = current.next;
        }
        return tasksNode;
    }

    public void removeNode(Node node) {
        if (node == null) {
            return;
        }
        nodeHistory.remove(node.task.getId());
        Node prevNode = node.prev;
        Node nextNode = node.next;
        if (prevNode == null) {
            first = nextNode;
        } else {
            prevNode.next = nextNode;
            node.prev = null;
        }
        if (nextNode == null) {
            last = prevNode;
        } else {
            nextNode.prev = prevNode;
            node.next = null;
        }
    }
}
