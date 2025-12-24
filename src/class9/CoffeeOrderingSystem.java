package class9;


import java.util.ArrayList;
import java.util.List;

interface Beverage { //Component participant
    double getPrice();
    String receipt();
}

class Espresso implements Beverage {

    @Override
    public double getPrice() {
        return 1.2;
    }

    @Override
    public String receipt() {
        return "Espresso:\n";
    }
}

class Matcha implements Beverage {
    @Override
    public double getPrice() {
        return 1.8;
    }

    @Override
    public String receipt() {
        return "Matcha:\n";
    }
}

class DecafEspresso implements Beverage {
    @Override
    public double getPrice() {
        return 1.1;
    }

    @Override
    public String receipt() {
        return "Decaf Espresso:\n";
    }
}

abstract class BeverageDecorator implements Beverage {
    Beverage beverage;

    public BeverageDecorator(Beverage beverage) {
        this.beverage = beverage;
    }
}

class AlmondMilkDecorator extends BeverageDecorator {

    public AlmondMilkDecorator(Beverage beverage) {
        super(beverage);
    }

    @Override
    public double getPrice() {
        return beverage.getPrice() + 1;
    }

    @Override
    public String receipt() {
        return beverage.receipt() + "- almond milk\n";
    }
}

class RegularMilkDecorator extends BeverageDecorator {
    public RegularMilkDecorator(Beverage beverage) {
        super(beverage);
    }

    @Override
    public double getPrice() {
        return beverage.getPrice() + 0.5;
    }

    @Override
    public String receipt() {
        return beverage.receipt() + "- regular milk\n";
    }
}

class PumpkinSpiceDecorator extends BeverageDecorator {
    public PumpkinSpiceDecorator(Beverage beverage) {
        super(beverage);
    }

    @Override
    public double getPrice() {
        return beverage.getPrice() + 1.1;
    }

    @Override
    public String receipt() {
        return beverage.receipt() + "- pumpkin spice\n";
    }
}



class Order {
    String base;
    List<String> additions;

    public Order(String base) {
        this.base = base;
        additions = new ArrayList<>();
    }

    public void addAddition(String addition) {
        additions.add(addition);
    }

    double price() {
        double price = 0;
        switch (base) {
            case "ESPRESSO":
                price = 1.2;
                break;
            case "DECAF_ESPRESSO":
                price = 1.25;
                break;
            case "BREWED_COFFEE":
                price = 1.0;
                break;
        }

        for (String addition : additions) {
            switch (addition) {
                case "ALMOND_MILK":
                    price = price + 1.5;
                    break;
                case "REGULAR_MILK":
                case "PUMPKIN_SPICE":
                    price = price + 1.0;
                    break;
            }
        }

        return price;
    }

    @Override
    public String toString() {
        return "Order{" +
                "base='" + base + '\'' +
                ", additions=" + additions +
                '}';
    }
}

public class CoffeeOrderingSystem {
    public static void main(String[] args) {
//        Order order = new Order("ESPRESSO");
//        order.addAddition("ALMOND_MILK");
//        order.addAddition("PUMPKIN_SPICE");
//
//        System.out.println(order.price());
//        System.out.println(order.toString());

        Beverage beverage = new DecafEspresso();
        beverage = new AlmondMilkDecorator(beverage);
        beverage = new PumpkinSpiceDecorator(beverage);

        System.out.println(beverage.receipt());
        System.out.println(beverage.getPrice());


    }
}
