package class3.queue;

import java.util.Set;
import java.util.TreeSet;

public class PriorityQueue<E extends Drawable> {

    private Set<Item<E>> items;

    public PriorityQueue() {
        items = new TreeSet<>();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void add(E e, int priority) {
        items.add(new Item<>(e, priority));
    }

    public Item<E> remove() {
        Item<E> item = items.iterator().next();
        items.remove(item);
        return item;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Item<E> item : items) {
            sb.append(item.toString()).append("\n");
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        PriorityQueue<DrawingItem> queue = new PriorityQueue<>();
        queue.add(new DrawingItem("test1"), 303);
        queue.add(new DrawingItem("test2"), 2);
        queue.add(new DrawingItem("test3"), 393300);

        while (!queue.isEmpty()) {
            System.out.println(queue.remove());
        }
    }
}
