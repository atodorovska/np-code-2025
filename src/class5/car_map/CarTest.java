package class5.car_map;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class CarTest {

    public static void main(String[] args) {
        CarCollection carCollection = new CarCollection();
        String manufacturer = fillCollection(carCollection);
        carCollection.sortByPrice(true);
        System.out.println("=== Sorted By Price ASC ===");
        print(carCollection.getList());
        carCollection.sortByPrice(false);
        System.out.println("=== Sorted By Price DESC ===");
        print(carCollection.getList());
        System.out.printf("=== Filtered By Manufacturer: %s ===\n", manufacturer);
        List<Car> result = carCollection.filterByManufacturer(manufacturer);
        print(result);
        System.out.println("=== Grouped By Manufacturer: %s ===\n");
        Map<String, List<Car>> carsByManufacturer = carCollection.groupByManufacturer();
        carsByManufacturer.entrySet().forEach(System.out::println);
        System.out.println("=== Grouped By Manufacturer Advanced: %s ===\n");
        Map<String, List<Car>> carsByManufacturerAdv = carCollection.groupByManufacturer();
        carsByManufacturerAdv.entrySet().forEach(System.out::println);
    }

    static void print(List<Car> cars) {
        for (Car c : cars) {
            System.out.println(c);
        }
    }

    static String fillCollection(CarCollection cc) {
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] parts = line.split("\\s+");

            if (parts.length == 1)
                return line;

            String manufacturer = parts[0];
            String model = parts[1];
            int price = Integer.parseInt(parts[2]);
            float power = Float.parseFloat(parts[3]);
            cc.addCar(new Car(manufacturer, model, price, power));
        }
        return "";
    }
}