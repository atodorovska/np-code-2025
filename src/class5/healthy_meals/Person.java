package class5.healthy_meals;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Person implements Comparable<Person> {

    private long id;
    private List<String> meals;
    private int count;

    public Person(long id, List<String> meals) {
        this.id = id;
        this.meals = meals;
        this.count = 0;
    }

    public static Person create(String line) {
        String[] tokens = line.split("\\s+");
        long id = Long.parseLong(tokens[0]);
        List<String> meals = new ArrayList<>();

        for (int i = 1; i < tokens.length; i++) {
            meals.add(tokens[i]);
        }
        return new Person(id, meals);
    }

    public long getId() {
        return id;
    }

    public List<String> getMeals() {
        return meals;
    }

    public int getCount() {
        return count;
    }

    public Person countHealthyMeals(List<String> healthyMeals) {
        count = (int) meals.stream().filter(healthyMeals::contains).distinct().count();
        return this;
    }

    public int countHealthyMeals2(List<String> healthyMeals) {
        return (int) meals.stream().filter(healthyMeals::contains).distinct().count();
    }

    @Override
    public String toString() {
        return String.format("Person ID: %d (healthy meals: %d)", id, count);
    }

    @Override
    public int compareTo(Person other) {
        return Comparator.comparing(Person::getCount).reversed().thenComparing(Person::getId).compare(this, other);
    }
}
