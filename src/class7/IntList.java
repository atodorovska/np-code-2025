package class7;

import java.util.*;
import java.util.stream.Collectors;

public class IntList {

    private List<Integer> data;

    public IntList(List<Integer> data) {
        this.data = data;
    }

    public double sumData() {
        return data.stream().mapToInt(Integer::intValue).sum();
    }

    public double averageData() {
        return data.stream().mapToInt(Integer::intValue).average().orElse(0);
    }

    public Map<Integer, Long> rangingMap() {
        return data.stream()
                   .collect(Collectors.groupingBy(
                           Integer::intValue,
                           Collectors.counting()
                   ));
    }

    public Map<Integer, Long> changedRangingMap() {
        return rangingMap()
                .entrySet()
                .stream()
                .filter(i -> i.getKey() > 10)
                .collect(Collectors.toMap(Map.Entry::getKey, i -> i.getValue() * 20));
    }

    public int sumEven() {
        return data.stream().filter(i -> i % 2 == 0).mapToInt(Integer::intValue).sum();
    }

    public static void main(String[] args) {
        List<Integer> data = new ArrayList<>();
        data.add(1);
        data.add(10);
        data.add(100);
        data.add(56);
        data.add(77);
        data.add(33);
        data.add(8900);
        data.add(8900);
        data.add(8900);
        data.add(8900);
        data.add(8900);

        IntList list = new IntList(data);

        System.out.println(list.sumData());
        System.out.println(list.averageData());
        System.out.println(list.rangingMap());
        System.out.println(list.sumEven());
    }
}
