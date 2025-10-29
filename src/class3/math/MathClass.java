package class3.math;

import java.util.ArrayList;

public class MathClass {

    public static <E extends Number> double standardDeviation(ArrayList<E> array) {
        double sum = 0;
        for (Number n : array) {
            sum += n.doubleValue();
        }
        double avg = sum / array.size();
        sum = 0;
        for (Number n : array) {
            sum += (avg - n.doubleValue()) * (avg - n.doubleValue());
        }
        return Math.sqrt(sum / array.size());
    }

    public static void main(String[] args) {
        ArrayList<Integer> ints = new ArrayList<>();
        ints.add(1);
        ints.add(10);
        ints.add(20);
        ints.add(30);
        ints.add(40);
        ints.add(50);
        System.out.println(String.format(
                "STD: %.2f",
                MathClass.standardDeviation(ints)
        ));
        ArrayList<Double> doubles = new ArrayList<>();
        doubles.add(3.4);
        System.out.println(String.format(
                "STD: %.2f",
                MathClass.standardDeviation(doubles)
        ));
    }
}
