package class4.car_basic;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

class CarPriceAndPowerComparator implements Comparator<Car> {

    @Override
    public int compare(Car o1, Car o2) {
        if (o1.getPrice() == o2.getPrice())
            return Float.compare(o1.getPower(), o2.getPower());
        return Integer.compare(o1.getPrice(), o2.getPrice());
    }
}

class ReversedCarPriceAndPowerComparator implements Comparator<Car> {

    @Override
    public int compare(Car o1, Car o2) {
        if (o1.getPrice() == o2.getPrice())
            return Float.compare(o2.getPower(), o1.getPower());
        return Integer.compare(o2.getPrice(), o1.getPrice());
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
            cars.sort(new CarPriceAndPowerComparator());
        else
            cars.sort(new ReversedCarPriceAndPowerComparator());
    }

    public List<Car> filterByManufacturer(String manufacturer) {
        List<Car> filteredCars = new ArrayList<>();

        for (Car car : cars) {
            if (car.getManufacturer().equalsIgnoreCase(manufacturer))
                filteredCars.add(car);
        }

        filteredCars.sort(new CarModelComparator());
        return filteredCars;
    }
}
