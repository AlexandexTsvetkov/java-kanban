package ru.yandex.javacource.tsvetkov.javacanban.manager;

import ru.yandex.javacource.tsvetkov.javacanban.task.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private Node<Task> head;
    private Node<Task> tail;
    private final HashMap<Integer, Node<Task>> history;

    public InMemoryHistoryManager() {
        this.history = new HashMap<>();
    }

    @Override
    public void add(Task task) {

        if (task == null) {
            return;
        }
        final int id = task.getId();
        remove(id);
        linkLast(task);
        history.put(id, tail);
    }

    @Override
    public List<Task> getHistory() {

        ArrayList<Task> taskHistoty = new ArrayList<>();

        Node<Task> element = head;

        while (element != null) {
            taskHistoty.add(element.data);
            element = element.next;
        }

        return taskHistoty;
    }

    @Override
    public void remove(int id) {

        final Node<Task> node = history.remove(id);

        if (node == null) {
            return;
        }
        removeNode(node);
    }

    private void linkLast(Task task) {

        Node<Task> newNode = new Node<>(task);

        if (history.isEmpty()) {
            head = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
        }

        tail = newNode;
    }


    private void removeNode(Node<Task> pastNode) {

        if (history.isEmpty()) {
            head = null;
            tail = null;
        } else {

            Node<Task> prevOfpastNode = pastNode.prev;
            Node<Task> nextOfpastNode = pastNode.next;

            if (prevOfpastNode != null) {
                prevOfpastNode.next = nextOfpastNode;
            }

            if (nextOfpastNode != null) {
                nextOfpastNode.prev = prevOfpastNode;
            }

            if (pastNode.equals(head)) {
                head = nextOfpastNode;
            }

            if (pastNode.equals(tail)) {
                tail = prevOfpastNode;
            }
        }
    }

    static final class Node<T> {

        private final T data;
        private Node<T> next;
        private Node<T> prev;

        public Node(T data) {
            this.data = data;
            this.next = null;
            this.prev = null;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node<?> node = (Node<?>) o;
            return Objects.equals(data, node.data);
        }

        @Override
        public int hashCode() {
            return Objects.hash(data);
        }
    }

}
