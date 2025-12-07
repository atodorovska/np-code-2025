package class4.book_basic_and_advanced;

import java.util.*;
import java.util.stream.Collectors;

class BookTitleAndPriceComparator implements Comparator<Book> {

    @Override
    public int compare(Book o1, Book o2) {
        if (o1.getTitle().compareToIgnoreCase(o2.getTitle()) == 0) {
            return Float.compare(o1.getPrice(), o2.getPrice());
        } else
            return o1.getTitle().compareToIgnoreCase(o2.getTitle());
    }
}

class BookPriceAndTitleComparator implements Comparator<Book> {

    @Override
    public int compare(Book o1, Book o2) {
        if (Float.compare(o1.getPrice(), o2.getPrice()) == 0) {
            return o1.getTitle().compareToIgnoreCase(o2.getTitle());
        } else
            return Float.compare(o1.getPrice(), o2.getPrice());
    }
}

public class BookCollection {

    private List<Book> books;

    public BookCollection() {
        this.books = new ArrayList<Book>();
    }

    public void addBook(Book book) {
        books.add(book);
    }

    public void printByCategory(String category) {
        List<Book> filteredBooks = new ArrayList<>();

        for (Book book : books) {
            if (book.getCategory().compareToIgnoreCase(category) == 0) {
                filteredBooks.add(book);
            }
        }
        filteredBooks.sort(new BookTitleAndPriceComparator());

        for (Book book : filteredBooks) {
            System.out.println(book);
        }
    }

    public List<Book> getCheapestN(int n) {
        List<Book> cheapestBooks = new ArrayList<>();
        books.sort(new BookPriceAndTitleComparator());

        for (int i = 0; i < n; i++) {
            cheapestBooks.add(books.get(i));
        }
        return cheapestBooks;
    }

    public void printByCategoryWithStreams(String category) {
        books.stream().filter(book -> book.getCategory().equals(category))
             .sorted(Comparator.comparing(Book::getTitle).thenComparing(Book::getPrice))
             .forEach(System.out::println);
    }

    public List<Book> getCheapestNWithStreams(int n) {
        return books.stream()
                    .sorted(Comparator.comparing(Book::getPrice).thenComparing(Book::getTitle))
                    .limit(n)
                    .collect(Collectors.toList());
    }

}
