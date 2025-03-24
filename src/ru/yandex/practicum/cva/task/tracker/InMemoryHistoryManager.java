package ru.yandex.practicum.cva.task.tracker;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final HashMap<Integer, Integer> map;
    private final Node first = new Node();
    private final Node last = new Node();
    private int size;


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

            if (map.containsKey(taskId)) {
                int idxInList = map.get(taskId);
                remove(idxInList);
            }
            map.put(taskId, size);
            linkLast(task.clone());
        }
    }

    @Override
    public void remove(int id) {

        if (map.containsKey(id)) {
            int idx = map.get(id);
            map.remove(id);
            Node node = find(idx);
            removeNode(node);

        }
    }

    private Node find(int index) {
        Node currNode = first;
        while (index >= 0 && currNode != null) {
            currNode = currNode.next;
            index--;

        }
        return currNode;
    }

    private void linkLast(Task value) {
        Node newNode = new Node();
        newNode.value = value;
        Node prevLast = last.prev;
        prevLast.next = newNode;
        newNode.prev = prevLast;
        newNode.next = last;
        last.prev = newNode;
        size++;
    }

    private void removeNode(Node node) {
        if (node != null) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
            size--;
        }
    }

    private List<Task> getTasks() {
        Node currNode = first;
        List<Task> res = new ArrayList<>();
        for (int i = 0; i < size; i++) {
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