package class2;

import java.util.Scanner;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

class Student {
    String id;
    String name;
    int grade;
    int attendance;

    public Student(String id, String name, int grade, int attendance) {
        this.id = id;
        this.name = name;
        this.grade = grade;
        this.attendance = attendance;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", grade=" + grade +
                ", attendance=" + attendance +
                '}';
    }
}

class Course {
    String name;
    Student [] students;

    int size;

    public Course(String name, int capacity) {
        this.name = name;
        students = new Student[capacity];
        size = 0;
    }

    void addStudent (Student s) {
        if (size<students.length){
            students[size++]=s;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Course: ").append(name).append("\n");
        for (int i = 0; i < size; i++) {
            sb.append(i+1).append(". ").append(students[i]).append("\n");
        }
        return sb.toString();
    }

    void enroll (Supplier<Student> supplier){
        addStudent(supplier.get());
    }

    void forEach (Consumer<Student> consumer){
        for (int i = 0; i < size; i++) {
            consumer.accept(students[i]);
        }
    }

    void conditionalForEach (Predicate<Student> predicate, Consumer<Student> consumer){
        for (int i = 0; i < size; i++) {
            if (predicate.test(students[i])){
                consumer.accept(students[i]);
            }
        }
    }

    int count (Predicate<Student> condition){
        int counter = 0;
        for (int i = 0; i < size; i++) {
            if (condition.test(students[i])){
                ++counter;
            }
        }
        return counter;
    }

    String [] map (Function<Student, String> mapper){
        String [] result = new String[size];
        for (int i = 0; i < size; i++) {
            result[i] = mapper.apply(students[i]);
        }
        return result;
    }

}


public class CourseDemo {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Course course = new Course("NP", 100);

        int n=3;

        Supplier<Student> reader = () -> {
            //1 Stefan 6 88
            String index = sc.next();
            String name = sc.next();
            int grade = sc.nextInt();
            int attendance = sc.nextInt();
//            sc.next();
            return new Student(index, name, grade, attendance);
        };

        for (int i=0;i<n;i++){
            course.enroll(reader);
        }

        System.out.println(course);

        Consumer<Student> increaseGrade = student -> student.grade++;

//        course.forEach(increaseGrade);

//        System.out.println(course);


        Predicate<Student> highAttendance = s -> s.attendance>=80;
        Predicate<Student> veryHighAttendance = s -> s.attendance>=90;
        Predicate<Student> passGrade = s -> s.grade>5;
        Predicate<Student> bareMinimumGrade = s -> s.grade==6 || s.grade==7;
        Predicate<Student> freshman = s -> s.id.startsWith("25");
        Predicate<Student> highGrade = s -> s.grade>=9;

        System.out.println(course.count(highAttendance));
        System.out.println(course.count(veryHighAttendance));
        System.out.println(course.count(passGrade));
        System.out.println(course.count(bareMinimumGrade));

        course.conditionalForEach(
                bareMinimumGrade.and(veryHighAttendance),
                increaseGrade
        );

        System.out.println(course);

        Function<Student, String> function = student -> String.format("Freshman: %s High grade: %s.", freshman.test(student), highGrade.test(student));

        for (String s : course.map(function)) {
            System.out.println(s);
        }


    }
}
