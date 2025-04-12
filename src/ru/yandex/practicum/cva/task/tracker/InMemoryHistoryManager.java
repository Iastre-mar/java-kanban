package ru.yandex.practicum.cva.task.tracker;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final HashMap<Integer, Node> map;
    private final Node first = new Node();
    private final Node last = new Node();


    public InMemoryHistoryManager() {
        this.map = new HashMap<>();

        first.next = last;
        last.prev = first;
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            int taskId = task.getId();

            linkLast(task.clone());
            map.put(taskId, last.prev);
        }
    }

    @Override
    public void remove(int id) {

        if (map.containsKey(id)) {
            Node node = map.get(id);
            map.remove(id);
            removeNode(node);

        }
    }

    private void linkLast(Task value) {
        Node newNode = new Node();
        newNode.value = value;
        Node prevLast = last.prev;
        prevLast.next = newNode;
        newNode.prev = prevLast;
        newNode.next = last;
        last.prev = newNode;
    }

    private void removeNode(Node node) {
        if (node != null) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
    }

    private List<Task> getTasks() {
        Node currNode = first;
        List<Task> res = new ArrayList<>();
        for (int i = 0; i < map.size(); i++) {
            res.add(currNode.next.value);
            currNode = currNode.next;
        }
        return res;
    }

    private class Node {
        private Node prev;
        private Task value;
        private Node next;

    }


}