package class5.dates_and_times;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;

public final class DateTimes {

    private DateTimes() {
    }

    /**
     Inclusive day count for [start, end].
     Example: 2025-11-01..2025-11-03 -> 3 days.
     */
    public static long daysInclusive(LocalDate start, LocalDate end) {
        Objects.requireNonNull(start);
        Objects.requireNonNull(end);
        return ChronoUnit.DAYS.between(start, end) + 1;
    }

    /**
     Is date in [start, end]?
     */
    public static boolean isWithin(LocalDate date, LocalDate start, LocalDate end, boolean inclusive) {
        Objects.requireNonNull(date);
        Objects.requireNonNull(start);
        Objects.requireNonNull(end);
        return inclusive
                ? !date.isBefore(start) && !date.isAfter(end)
                : date.isAfter(start) && date.isBefore(end);
    }

    /**
     Do two date ranges intersect?
     */
    public static boolean overlap(LocalDate s1, LocalDate e1, LocalDate s2, LocalDate e2, boolean inclusive) {
        Objects.requireNonNull(s1);
        Objects.requireNonNull(e1);
        Objects.requireNonNull(s2);
        Objects.requireNonNull(e2);
        if (inclusive) {
            return !e1.isBefore(s2) && !e2.isBefore(s1);
        } else {
            return e1.isAfter(s2) && e2.isAfter(s1) && s1.isBefore(e2) && s2.isBefore(e1);
        }
    }

    /**
     Earlier of two dates.
     */
    public static LocalDate min(LocalDate a, LocalDate b) {
        return a.isBefore(b) ? a : b;
    }

    /**
     Later of two dates.
     */
    public static LocalDate max(LocalDate a, LocalDate b) {
        return a.isAfter(b) ? a : b;
    }

    /**
     Minimal closed range.
     */
    public record LocalDateRange(LocalDate start, LocalDate end) {
        public LocalDateRange {
            Objects.requireNonNull(start);
            Objects.requireNonNull(end);
            if (end.isBefore(start))
                throw new IllegalArgumentException("end < start");
        }

        public long daysInclusive() {
            return DateTimes.daysInclusive(start, end);
        }

        public boolean contains(LocalDate d, boolean inclusive) {
            return DateTimes.isWithin(d, start, end, inclusive);
        }
    }

    /**
     Intersection of two closed ranges (inclusive). Empty if no overlap.
     */
    public static Optional<LocalDateRange> intersect(LocalDateRange a, LocalDateRange b) {
        if (!overlap(a.start(), a.end(), b.start(), b.end(), true))
            return Optional.empty();
        return Optional.of(new LocalDateRange(max(a.start(), b.start()), min(a.end(), b.end())));
    }

    /**
     Extracts date from a timestamp.
     */
    public static LocalDate extractDate(LocalDateTime dateTime) {
        Objects.requireNonNull(dateTime);
        return dateTime.toLocalDate();
    }

    public static void main(String[] args) {
        LocalDate firstDate = LocalDate.of(2025, 11, 10);
        LocalDate secondDate = LocalDate.of(2025, 11, 16);
        long days = daysInclusive(firstDate, secondDate);
        System.out.println("Days inclusive: " + days);

        LocalDate date = LocalDate.of(2025, 11, 12);
        boolean isWithin = isWithin(date, firstDate, secondDate, true);
        System.out.println("isWithin: " + isWithin);
    }
}
