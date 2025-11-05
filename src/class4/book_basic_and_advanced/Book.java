package class4.book_basic_and_advanced;

public class Book {

    private String title;
    private String category;
    private float price;

    public Book(String title, String category, float price) {
        this.title = title;
        this.category = category;
        this.price = price;
    }

//    public static Book createBook(String line) {
//        String[] parts = line.split("\\s+");
//        String title = parts[0];
//        String category = parts[1];
//        float price = Float.parseFloat(parts[2]);
//        return new Book(title, category, price);
//    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public float getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return String.format("%s (%s) %.2f", title, category, price);
    }
}
