package tasktracker;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements  HistoryManager{

    private final ArrayList<Task> history = new ArrayList<>(10);
    private Node first;
    private Node last;

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }

    @Override
    public void add(Task task) {
        if (history.size() >= 10) {
            history.removeFirst();
        }
        history.add(copyTask(task));
    }

    @Override
    public void remove(int id) {
        for (int i = history.size() - 1; i >= 0; i--) {
            if (history.get(i).getId() == id) {
                history.remove(i);
            }
        }
    }

    private Task copyTask(Task original) {
        Task copy;
        if (original instanceof Epic) {
            Epic epic = (Epic) original;
            copy = new Epic(epic.getName(), epic.getDescription());
        } else if (original instanceof Subtask) {
            Subtask subtask = (Subtask) original;
            copy = new Subtask(subtask.getName(), subtask.getDescription(), subtask.getEpicId());
        } else {
            copy = new Task(original.getName(), original.getDescription());
        }
        copy.setId(original.getId());
        copy.setProgress(original.getProgress());
        return copy;
    }

    public void linkLast(Task task) {
        Node newNode = new Node(task);
        if(last == null) {
            first = newNode;
        } else {
            last.next = newNode;
            newNode.prev = last;
        }
        newNode = last;
    }

    public List<Task> getTasks() {
        List<Task> tasksNode = new ArrayList<>();
        Node current = first;
        while(current != null) {
            tasksNode.add(current.task);
            current = current.next;
        }
        return tasksNode;
    }

    public void removeNode(Node node) {
        if(node == null) {
            return;
        }
        Node prevNode = node.prev;
        Node nextNode = node.next;
        if(prevNode == null) {
            first = nextNode;
        } else {
            prevNode.next = nextNode;
            node.prev = null;
        }
        if(nextNode == null) {
            last = prevNode;
        } else {
            nextNode.prev = prevNode;
            node.next = null;
        }
    }
}
