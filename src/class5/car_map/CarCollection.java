package class5.car_map;

import java.util.*;
import java.util.stream.Collectors;

class CarPriceAndPowerComparator implements Comparator<Car> {

    @Override
    public int compare(Car o1, Car o2) {
        if (o1.getPrice() == o2.getPrice())
            return Float.compare(o1.getPower(), o2.getPower());
        return Integer.compare(o1.getPrice(), o2.getPrice());
    }
}

class CarModelComparator implements Comparator<Car> {

    @Override
    public int compare(Car o1, Car o2) {
        return o1.getModel().compareTo(o2.getModel());
    }
}

public class CarCollection {

    private List<Car> cars;

    public CarCollection() {
        cars = new ArrayList<>();
    }

    public void addCar(Car car) {
        cars.add(car);
    }

    public List<Car> getList() {
        return cars;
    }

    public void sortByPrice(boolean ascending) {
        if (ascending)
            cars = cars.stream().sorted(new CarPriceAndPowerComparator()).collect(Collectors.toList());
        else
            cars = cars.stream().sorted(new CarPriceAndPowerComparator().reversed()).collect(Collectors.toList());
    }

    public List<Car> filterByManufacturer(String manufacturer) {
        return cars.stream()
                   .filter(car -> car.getManufacturer().equalsIgnoreCase(manufacturer))
                   .sorted(new CarModelComparator())
                   .collect(Collectors.toList());
    }

    public Map<String, List<Car>> groupByManufacturer() {
        Map<String, List<Car>> map = new HashMap<>();
        for (Car car : cars) {
            map.computeIfAbsent(car.getManufacturer(), k -> new ArrayList<>()).add(car);
        }
        return map;
    }

    public Map<String, List<Car>> groupByManufacturerAdvanced() {
        return cars.stream().collect(Collectors.groupingBy(Car::getManufacturer, Collectors.toList()));
    }
}
