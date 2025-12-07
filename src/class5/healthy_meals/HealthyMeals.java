package class5.healthy_meals;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class HealthyMeals {

    private List<String> healthyMeals;

    public HealthyMeals() {
        healthyMeals = new ArrayList<>();
    }

    public void evaluate1(InputStream is, OutputStream os) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        PrintWriter pw = new PrintWriter(os);
        healthyMeals = Arrays.stream(br.readLine().split("\\s+")).toList();

        br.lines()
          .map(Person::create)
          .sorted(Comparator.comparing((Person p) -> p.countHealthyMeals(healthyMeals))
                            .thenComparing(Person::getId))
          .forEach(pw::println);

        pw.flush();
    }

    public void evaluate2(InputStream is, OutputStream os) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        PrintWriter pw = new PrintWriter(os);
        healthyMeals = Arrays.stream(br.readLine().split("\\s+")).collect(Collectors.toList());

        br.lines()
          .map(Person::create)
          .map(p -> p.countHealthyMeals(healthyMeals))
          .sorted().forEach(pw::println);

        pw.flush();
    }

    public void evaluate3(InputStream is, OutputStream os) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        PrintWriter pw = new PrintWriter(os);
        healthyMeals = Arrays.stream(br.readLine().split("\\s+")).toList();

        br.lines()
          .map(Person::create)
          .sorted(Comparator.comparing((Person p) -> p.countHealthyMeals(healthyMeals))
                            .thenComparing(Person::getId))
          .forEach(p -> {
              pw.printf("Person ID: %d (healthy meals: %d)", p.getId(), p.countHealthyMeals2(healthyMeals));
          });

        pw.flush();
    }

    public static void main(String[] args) {
        HealthyMeals healthyMeals = new HealthyMeals();
        try {
            healthyMeals.evaluate2(System.in, System.out);
        } catch (IOException e) {
            System.out.println("error");
        }
    }
}
