package class3.exam;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

class Student implements Comparable<Student> {
    String id;
    List<Integer> grades;

    public Student(String id, List<Integer> grades) {
        this.id = id;
        this.grades = grades;
    }

    public double average() {
        return grades.stream().mapToDouble(i -> i).average().getAsDouble();
    }

    public int getYear() {
        return (24 - Integer.parseInt(id.substring(0, 2)));
    }

    public int totalCourses() {
        return Math.min(getYear() * 10, 40);
    }

    public double labAssistantPoints() {
        return average() * ((double) grades.size() / totalCourses()) * (0.8 + ((getYear() - 1) * 0.2) / 3.0);
    }

    //TODO: implement function
    public static List<Integer> mapGrades(List<Integer> grades) {
        return grades.stream().map(i -> 11 - i).collect(Collectors.toList());
    }

    @Override
    public int compareTo(Student o) {
        return Comparator.comparing(Student::labAssistantPoints)
                         .thenComparing(Student::average)
                         .compare(this, o);
    }

    @Override
    public String toString() {
        return String.format(
                "Student %s (%d year) - %d/%d passed exam, average grade %.2f.\nLab assistant points: %.2f",
                id,
                getYear(),
                grades.size(),
                totalCourses(),
                average(),
                labAssistantPoints()
        );
    }
}

class ExecuteAndSort {

    public static <E extends Comparable<E>> List<E> execute(List<E> elements, Function<E, E> function) {
        return elements.stream()
                       .map(function)
                       .sorted(Comparator.reverseOrder())
                       .collect(Collectors.toList());
    }
}

public class ExecuteAndSortTest {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int testCase = Integer.parseInt(sc.nextLine());
        int n = Integer.parseInt(sc.nextLine());

        if (testCase == 1) { // students
            int studentScenario = Integer.parseInt(sc.nextLine());
            List<Student> students = new ArrayList<>();
            while (n > 0) {
                String line = sc.nextLine();
                String[] parts = line.split("\\s+");
                String id = parts[0];
                List<Integer> grades = Arrays.stream(parts).skip(1).map(Integer::parseInt).collect(Collectors.toList());
                students.add(new Student(id, grades));
                --n;
            }

            if (studentScenario == 1) {
                //TODO: transform all students such that their id is converted into a new format adding the suffix
                // "_FCSE" after each id number

                students = ExecuteAndSort.execute(
                        students, (a) -> {
                            a.id = a.id + "_FCSE";
                            return a;
                        }
                );

                System.out.println(students);

            } else {
                //TODO: transform all students such that their grades are mapped into a new system as follows:
                // 10 -> 1
                // 9 -> 2
                // 8 -> 3
                // 7 -> 4
                // 6 -> 5

                students = ExecuteAndSort.execute(
                        students, (a) -> {
                            a.grades = Student.mapGrades(a.grades);
                            return a;
                        }
                );

                System.out.println(students);

            }
        } else { //integers
            List<Integer> integers = new ArrayList<>();
            while (n > 0) {
                integers.add(Integer.parseInt(sc.nextLine()));
                --n;
            }

            //TODO: transform all integers to be 10 times greater than their original value if their original value
            // was less than 100 or 2 times greater otherwise

            integers = ExecuteAndSort.execute(
                    integers, (a) -> {
                        if (a < 100)
                            return 10 * a;
                        return 2 * a;
                    }
            );
            System.out.println(integers);
        }
    }
}
