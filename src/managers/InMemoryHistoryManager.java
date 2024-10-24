package managers;

import model.Node;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node<Task>> taskHistoryMap = new HashMap<>();
    private Node<Task> head;
    private Node<Task> tail;

    public List<Task> getTasks() {
        List<Task> taskList = new ArrayList<>();
        Node<Task> current = head;
        while (current != null) {
            taskList.add(current.getTask());
            current = current.getNext();
        }
        return taskList;
    }

    public void linkLast(Task task) {
        Node<Task> oldTail = tail;
        Node<Task> newNode = new Node<>(oldTail, task, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.setNext(newNode);
        }
        int id = task.getId();
        taskHistoryMap.put(id, newNode);
    }

    public void removeNode(Node<Task> node) {
        Node<Task> next = node.getNext();
        Node<Task> prev = node.getPrev();
        if (prev == null) {
            head = next;
        } else {
            prev.setNext(next);
            node.setNext(null);
        }
        if (next == null) {
            tail = prev;
        } else {
            next.setPrev(prev);
            node.setNext(null);
        }
        node.setTask(null);
    }

    @Override
    public void add(Task task) {
        int id = task.getId();
        if (taskHistoryMap.containsKey(id)) {
            Node<Task> node = taskHistoryMap.get(id);
            linkLast(task);
            removeNode(node);
        } else {
            linkLast(task);
        }
    }

    @Override
    public void remove(int id) {
        Node<Task> node = taskHistoryMap.get(id);
        if (node != null) {
            removeNode(node);
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }
}
