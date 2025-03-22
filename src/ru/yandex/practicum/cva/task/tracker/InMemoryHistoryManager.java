package ru.yandex.practicum.cva.task.tracker;


import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final MyLinkedHashSet<Task> tasks;


    public InMemoryHistoryManager() {
        this.tasks = new MyLinkedHashSet<>();
    }

    @Override
    public List<Task> getHistory() {
        return tasks.getTasks();
    }

    @Override
    public void add(Task task) {
        if (task != null){
            tasks.add(task.clone());
        }

    }

    @Override
    public void remove(int id) {
        Task formalTask = new Task("Technical");
        formalTask.setId(id);
        tasks.remove(formalTask);
    }

    /**
     * Нам неявно предлагают самим реализовать LinkedHashSet.
     * <p>Можно ли оставить в таком виде чтобы не засорять код? </p>
     */
    private static class MyLinkedHashSet<T extends Task>
            extends AbstractSet<T> {
        private final HashMap<Integer, Integer> map;
        private final MyLinkedList<T> list;


        private MyLinkedHashSet() {
            map = new HashMap<>();
            list = new MyLinkedList<>();
        }

        public List<T> getTasks() {
            return list.getTasks();
        }

        @Override
        public Iterator<T> iterator() {
            return list.iterator();
        }

        @Override
        public int size() {
            return list.size();
        }

        @Override
        public boolean add(T t) {
            int oldSize = size();
            int taskId = t.getId();
            if (map.containsKey(taskId)) {
                int idxInList = map.get(taskId);
                list.remove(idxInList);
            }
            map.put(taskId, size());
            list.linkLast(t);

            return size() > oldSize;
        }

        @Override
        public boolean remove(Object o) {
            int oldSize = size();
            if (!(o instanceof Task)) {
                throw new IllegalArgumentException();
            }
            Task task = (Task) o;

            int taskId = task.getId();

            if (map.containsKey(taskId)) {
                int idx = map.get(taskId);
                map.remove(taskId);
                list.remove(idx);
            }

            return size() < oldSize;
        }

        private static class MyLinkedList<E> extends AbstractList<E> {
            // Начало и конец являются лишь указателями и
            //  не имеют собственного значения.
            private final Node first = new Node();
            private final Node last = new Node();
            private int size;

            private MyLinkedList() {
                first.next = last;
                last.prev = first;
            }

            public void linkLast(E value) {
                Node newNode = new Node();
                newNode.value = value;
                Node prevLast = last.prev;
                prevLast.next = newNode;
                newNode.prev = prevLast;
                newNode.next = last;
                last.prev = newNode;
                size++;
            }

            public E get(int index) {
                Node resFind = find(index);
                return (resFind == null) ? null : resFind.value;
            }

            // В случае поступления индекса меньше нуля
            //  выкидывается NullPointerException (Так и предполагалась).
            // Стоит ли добавлять секцию throws?

            /**
             * @throws NullPointerException in case of negative index
             */
            @Override
            public E remove(int index) throws NullPointerException {
                E res = null;
                Node node = find(index);
                if (node != null) {
                    node.prev.next = node.next;
                    node.next.prev = node.prev;
                    res = node.value;
                    size--;
                }
                return res;
            }

            public List<E> getTasks() {
                return new ArrayList<>(this);
            }

            @Override
            public int size() {
                return this.size;
            }

            private Node find(int index) {
                Node currNode = first;
                while (index >= 0 && currNode != null) {
                    currNode = currNode.next;
                    index--;

                }
                return currNode;
            }

            private class Node {
                private Node prev;
                private E value;
                private Node next;
            }


        }
    }


}
