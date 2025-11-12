package class5.generic;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

interface Quantifiable {
    double amount();
}

final class License implements Quantifiable, Comparable<License> {
    private final String title;
    private final String category;
    private final String region;
    private final int units;
    private final double ratePerUnit;

    public License(String title, String category, String region, int units, double ratePerUnit) {
        if (title == null || title.isBlank())
            throw new IllegalArgumentException("title required");
        if (category == null || category.isBlank())
            throw new IllegalArgumentException("category required");
        if (region == null || region.isBlank())
            throw new IllegalArgumentException("region required");
        if (units < 0)
            throw new IllegalArgumentException("units < 0");
        if (ratePerUnit < 0)
            throw new IllegalArgumentException("ratePerUnit < 0");
        this.title = title;
        this.category = category;
        this.region = region;
        this.units = units;
        this.ratePerUnit = ratePerUnit;
    }

    public String title() {
        return title;
    }

    public String category() {
        return category;
    }

    public String region() {
        return region;
    }

    public int units() {
        return units;
    }

    public double ratePerUnit() {
        return ratePerUnit;
    }

    @Override
    public double amount() {
        return units * ratePerUnit;
    }

    @Override
    public int compareTo(License other) {

        return Comparator.comparing(License::amount)
                         .reversed()
                         .thenComparing(License::category)
                         .thenComparing(License::title)
                         .compare(this, other);
    }

    @Override
    public String toString() {
        return "%s [%s|%s] units=%d rp=%.2f total=%.2f"
                .formatted(title, category, region, units, ratePerUnit, amount());
    }
}

class Ledger<T extends Quantifiable & Comparable<? super T>> implements Comparable<Ledger<T>> {

    private final List<T> items = new ArrayList<>();

    public Ledger() {
    }

    public void put(T item) {
        items.add(Objects.requireNonNull(item));
    }

    public <R> Set<R> project(Function<? super T, ? extends R> mapper) {
        return items.stream().map(mapper)
                    .collect(Collectors.toCollection(HashSet::new));
    }

    public void forEachIf(Predicate<? super T> condition, Consumer<? super T> action) {
        items.stream().filter(condition).forEach(action);
    }

    public double sum() {
        return items.stream().mapToDouble(Quantifiable::amount).sum();
    }

    @Override
    public int compareTo(Ledger<T> other) {
        return Double.compare(other.sum(), this.sum());
    }
}

public class MediaLicensesDemo {
    public static void main(String[] args) {
        Ledger<License> ledger = new Ledger<>();
        ledger.put(new License("Lo-Fi Beats", "music", "EU", 120_000, 0.0012));
        ledger.put(new License("Cooking B-Roll", "video", "US", 18_000, 0.02));
        ledger.put(new License("City Skyline", "photo", "EU", 7000, 0.15));
        ledger.put(new License("Nature Ambience", "music", "APAC", 90_000, 0.0014));
        ledger.put(new License("Interview Pack", "video", "EU", 9_500, 0.03));
        ledger.put(new License("Retro Poster", "photo", "US", 1500, 0.40));

        Set<String> categories = ledger.project(License::category);
        System.out.println("CATEGORIES: " + categories);

        System.out.println("\nMARK HIGH-VALUE (> 100.00):");
        ledger.forEachIf(l -> l.amount() > 100.0, l -> System.out.println("★ " + l.title()));
    }
}
