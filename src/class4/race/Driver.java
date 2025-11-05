package class4.race;

import java.util.ArrayList;
import java.util.List;

public class Driver implements Comparable<Driver> {

    private String name;
    private List<String> laps;

    public Driver(String name, String lap1, String lap2, String lap3) {
        this.name = name;
        this.laps = new ArrayList<>();
        laps.add(lap1);
        laps.add(lap2);
        laps.add(lap3);
    }

    public static Driver createDriver(String line) {
        String[] parts = line.split("\\s+");
        String name = parts[0];
        String lap1 = parts[1];
        String lap2 = parts[2];
        String lap3 = parts[3];
        return new Driver(name, lap1, lap2, lap3);
    }

    public String getName() {
        return name;
    }

    private static int getLapMillis(String lap) {
        String[] laps = lap.split(":");
        return Integer.parseInt(laps[0]) * 1000 * 60 +
                Integer.parseInt(laps[1]) * 1000 +
                Integer.parseInt(laps[2]);
    }

    public String getBestLap() {
        int minLap = laps.stream().mapToInt(Driver::getLapMillis).min().orElse(0);

        return laps.stream()
                   .filter(lap -> getLapMillis(lap) == minLap)
                   .findFirst()
                   .orElseGet(() -> laps.getLast());
    }

    @Override
    public int compareTo(Driver other) {
        return getLapMillis(this.getBestLap()) - getLapMillis(other.getBestLap());
    }

    @Override
    public String toString() {
        return String.format("%-10s%10s", getName(), getBestLap());
    }
}
