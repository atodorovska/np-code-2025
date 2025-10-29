package class3.queue;

public class DrawingItem implements Drawable {

    private String item;

    public DrawingItem(String item) {
        this.item = item;
    }

    @Override
    public String draw() {
        return item;
    }

    @Override
    public String toString() {
        return item;
    }
}
