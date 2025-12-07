package class7;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

class Person {
    private final int id;
    private final List<String> meals;

    public Person(int id, List<String> meals) {
        this.id = id;
        this.meals = meals;
    }

    public Person() {
        this.id = 0;
        this.meals = new ArrayList<>();
    }

    public Person addMeals(List<String> meals) {
        this.meals.addAll(meals);
        return this;
    }

    public static Person create(String line) {
        String[] parts = line.trim().split("\\s+");
        int id = Integer.parseInt(parts[0]);

//        List<String> out = new ArrayList<>();
//        IntStream.range(1, parts.length).forEach(i -> out.add(parts[i]));

        List<String> meals = Arrays.stream(parts)
                                   .skip(1)
                                   .collect(Collectors.toList());
        return new Person(id, meals);
    }

    public int getId() {
        return id;
    }

    public List<String> getMeals() {
        return meals;
    }

    public long countHealthyMeals(List<String> healthyMeals) {
        return meals.stream()
                    .filter(healthyMeals::contains)
                    .count();
    }

    public long countHealthyMeals2(List<String> healthyMeals) {
//        return meals.stream()
//                    .map(m -> healthyMeals.contains(m) ? 1 : 0)
//                    .reduce(0, (res, curr) -> {
//                        res += curr;
//                        return res;
//                    });

        return meals.stream()
                    .map(m -> healthyMeals.contains(m) ? 1 : 0)
                    .reduce(0, Integer::sum);
    }

    @Override
    public String toString() {
        return String.format("Person{id=%d, meals=%s}", id, meals);
    }
}

public class HealthyMealsSolution {

    private List<String> healthyMeals;

    public HealthyMealsSolution() {
        healthyMeals = new ArrayList<>();
    }

    private List<Person> readPersons(BufferedReader br) {
        return br.lines()
                 .filter(line -> !line.isBlank())
                 .map(Person::create)
                 .collect(Collectors.toList());
    }

    public void evaluate1(InputStream is, OutputStream os) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        PrintWriter pw = new PrintWriter(os);

        healthyMeals = Arrays.stream(br.readLine().split("\\s+")).toList();

        List<Person> persons = readPersons(br);

        persons.stream()
               .sorted(Comparator
                               .comparing((Person p) -> p.countHealthyMeals(healthyMeals))
                               .thenComparing(Person::getId))
               .forEach(p -> {
                   long healthyCount = p.countHealthyMeals(healthyMeals);
                   pw.printf("%d -> %d healthy meals%n", p.getId(), healthyCount);
               });

        pw.flush();
    }

    public void evaluate2(InputStream is, OutputStream os) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        PrintWriter pw = new PrintWriter(os);

        healthyMeals = Arrays.stream(br.readLine().split("\\s+"))
                             .collect(Collectors.toList());

        List<Person> persons = readPersons(br);

        persons.stream()
               .map(p -> p.countHealthyMeals(healthyMeals))
               .sorted()
               .forEach(pw::println);

        pw.flush();
    }

    public void evaluate3(InputStream is, OutputStream os) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        PrintWriter pw = new PrintWriter(os);

        healthyMeals = Arrays.stream(br.readLine().split("\\s+")).toList();

        List<Person> persons = readPersons(br);

        persons.stream()
               .sorted(Comparator
                               .comparing((Person p) -> p.countHealthyMeals(healthyMeals))
                               .thenComparing(Person::getId))
               .forEach(p -> {
                   long healthyCount = p.countHealthyMeals2(healthyMeals);
                   pw.printf(
                           "Person ID: %d (healthy meals: %d)%n",
                           p.getId(), healthyCount
                   );
               });

        pw.flush();
    }

    public void evaluate4_groupByHealthyCount(InputStream is, OutputStream os) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        PrintWriter pw = new PrintWriter(os);

        healthyMeals = Arrays.stream(br.readLine().split("\\s+")).toList();
        List<Person> persons = readPersons(br);

        Map<Long, Long> grouped = persons.stream()
                                         .collect(Collectors.groupingBy(
                                                 p -> p.countHealthyMeals(healthyMeals),
                                                 Collectors.counting()
                                         ));

        grouped.entrySet().stream()
               .sorted(Map.Entry.<Long, Long>comparingByKey().reversed())
               .forEach(e -> pw.printf(
                       "%d healthy meals: %d persons%n",
                       e.getKey(), e.getValue()
               ));

        pw.flush();
    }

    public void evaluate5_mostPopularHealthyMeal(InputStream is, OutputStream os) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        PrintWriter pw = new PrintWriter(os);

        healthyMeals = Arrays.stream(br.readLine().split("\\s+")).toList();
        List<Person> persons = readPersons(br);

        Map<String, Long> mealCounts =
                persons.stream()
                       .flatMap(p -> p.getMeals().stream())
                       .filter(healthyMeals::contains)
                       .collect(Collectors.groupingBy(
                               m -> m,
                               Collectors.counting()
                       ));

        Optional<Map.Entry<String, Long>> mostPopular =
                mealCounts.entrySet().stream()
                          .max(Map.Entry.comparingByValue());

        if (mostPopular.isPresent()) {
            Map.Entry<String, Long> e = mostPopular.get();
            pw.printf(
                    "Most popular healthy meal is %s (eaten %d times)%n",
                    e.getKey(), e.getValue()
            );
        } else {
            pw.println("No healthy meals were eaten.");
        }

        pw.flush();
    }

    public void evaluate6_statistics(InputStream is, OutputStream os) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        PrintWriter pw = new PrintWriter(os);

        healthyMeals = Arrays.stream(br.readLine().split("\\s+")).toList();
        List<Person> persons = readPersons(br);

//        persons.stream()
//               .reduce(
//                       new Person(), (res, curr) -> {
//                           res.addMeals(curr.getMeals());
//                           return res;
//                       }
//               );

        long countPersons = persons.size();

        int totalMeals = persons.stream()
                                .mapToInt(p -> p.getMeals().size())
                                .sum();

        int totalHealthy1 = persons.stream()
                                   .mapToInt(p -> (int) p.countHealthyMeals(healthyMeals))
                                   .sum();

        int totalHealthy2 = persons.stream()
                                   .map(p -> (int) p.countHealthyMeals(healthyMeals))
                                   .reduce(0, Integer::sum);

        double averageHealthy = countPersons == 0
                ? 0.0
                : (double) totalHealthy1 / countPersons;

        pw.printf("Persons: %d%n", countPersons);
        pw.printf("Total meals: %d%n", totalMeals);
        pw.printf("Total healthy meals: %d%n", totalHealthy1);
        pw.printf("Average healthy meals per person: %.2f%n", averageHealthy);

        pw.flush();
    }

    public void evaluate7_partitionByHealthy(InputStream is, OutputStream os) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        PrintWriter pw = new PrintWriter(os);

        healthyMeals = Arrays.stream(br.readLine().split("\\s+")).toList();
        List<Person> persons = readPersons(br);

        Map<Boolean, List<Person>> partitioned =
                persons.stream()
                       .collect(Collectors.partitioningBy(
                               p -> p.countHealthyMeals(healthyMeals) > 0
                       ));

        partitioned.entrySet().stream()
                   .filter(e -> e.getKey() == true)
                   .flatMap(e -> e.getValue().stream())
                   .forEach(System.out::println);

        long withHealthy = partitioned.getOrDefault(true, List.of()).size();
        long withoutHealthy = partitioned.getOrDefault(false, List.of()).size();

        pw.printf("With healthy meals: %d%n", withHealthy);
        pw.printf("Without healthy meals: %d%n", withoutHealthy);

        pw.flush();
    }

    public static void main(String[] args) {
        HealthyMealsSolution healthyMeals = new HealthyMealsSolution();
        try {
            healthyMeals.evaluate1(System.in, System.out);
            healthyMeals.evaluate2(System.in, System.out);
            healthyMeals.evaluate3(System.in, System.out);
            healthyMeals.evaluate4_groupByHealthyCount(System.in, System.out);
            healthyMeals.evaluate5_mostPopularHealthyMeal(System.in, System.out);
            healthyMeals.evaluate6_statistics(System.in, System.out);
            healthyMeals.evaluate7_partitionByHealthy(System.in, System.out);
        } catch (IOException e) {
            System.out.println("error");
        }
    }
}

