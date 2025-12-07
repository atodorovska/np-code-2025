package class7;

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

    // Get titles
    public List<String> getBookTitles() {
        return books.stream().map(Book::getTitle).collect(Collectors.toList());
    }

    // Get distinct categories
    public List<String> getBookCategoriesDistinct() {
        return books.stream().map(Book::getCategory).distinct().collect(Collectors.toList());
    }

    // Search by substring
    public Book getBookByTitleSubstring(String search) {
        return books
                .stream()
                .filter(i -> i.getTitle().toLowerCase().contains(search.toLowerCase()))
                .findFirst()
                .orElseGet(Book::new);
    }

    // Average book price
    public double averageBookPrice() {
        return books.stream()
                    .mapToDouble(Book::getPrice)
                    .average()
                    .orElse(0);
    }

    // Price statistics
    public DoubleSummaryStatistics bookSummaryStatistics() {
        return books.stream().mapToDouble(Book::getPrice).summaryStatistics();
    }

    // Group books by category
    public Map<String, List<Book>> booksByCategory() {
        return books.stream().
                    collect(Collectors.groupingBy(
                            Book::getCategory,
                            TreeMap::new,
                            Collectors.toList()
                    ));
    }

    // Count how many books per category
    public Map<String, Long> countBooksByCategory() {
        return books.stream().
                    collect(Collectors.groupingBy(
                            Book::getCategory,
                            Collectors.counting()
                    ));
    }

    // Average price per category
    public Map<String, Double> averageBookPriceByCategory() {
        return books.stream().
                    collect(Collectors.groupingBy(
                            Book::getCategory,
                            Collectors.averagingDouble(Book::getPrice)
                    ));
    }

    // Cheapest book per category
    public Map<String, Float> cheapestBookPriceByCategory() {
//        Map<String, Optional<Book>> tmp = books.stream()
//                                               .collect(Collectors.groupingBy(
//                                                       Book::getCategory,
//                                                       Collectors.minBy(Comparator.comparing(Book::getPrice))
//                                               ));
//        return tmp.entrySet()
//                  .stream()
//                  .collect(Collectors.toMap(
//                          Map.Entry::getKey, i -> {
//                              return i.getValue().isPresent() ? i.getValue().get().getPrice() : 0;
//                          }
//                  ));


        return books.stream()
                    .collect(Collectors.groupingBy(
                            Book::getCategory,
                            Collectors.collectingAndThen(
                                    Collectors.minBy(Comparator.comparing(Book::getPrice)),
                                    opt -> opt.map(Book::getPrice).orElse(0F)
                            )
                    ));
    }

    // Concatenate all titles into one string
    public String concatenateTitlesJoining() {
        return books.stream().map(Book::getTitle).collect(Collectors.joining(", "));
    }

    public String concatenateTitlesReduce1() {
        return books.stream().map(Book::getTitle).reduce(
                "", (res, curr) -> {
                    res += curr;
                    // res += "**";
                    return res;
                }
        );
    }

    public String concatenateTitlesReduce2() {
        return books.stream().map(Book::getTitle).reduce("", String::concat);
    }
}
