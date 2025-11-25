package class5.airports;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

class Airport {

    String name;
    String country;
    String code;
    int passengers;
    Map<String, Set<Flight>> flights;

    public Airport(String name, String country, String code, int passengers) {
        this.name = name;
        this.country = country;
        this.code = code;
        this.passengers = passengers;
        flights = new TreeMap<>();
    }

    public void addFlight(String from, String to, int time, int duration) {
        flights.computeIfAbsent(to, k -> new TreeSet<>())
               .add(new Flight(from, to, time, duration));
    }

    @Override
    public String toString() {
        return String.format("%s (%s)\n%s\n%d", name, code, country, passengers);
    }
}