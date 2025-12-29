package class9;

import java.util.*;

interface Subscriber {
    void update(float temperature, float humidity, float pressure);
}

interface Publisher {
    void register (Subscriber subscriber);
    void remove (Subscriber subscriber);
    void notifySubscriber (float temperature, float humidity, float pressure);
}

class WeatherDispatcher implements Publisher {

    Set<Subscriber> subscribers;

    WeatherDispatcher() {
        subscribers = new HashSet<>();
    }

    @Override
    public void register(Subscriber subscriber) {
        subscribers.add(subscriber);
    }

    @Override
    public void remove(Subscriber subscriber) {
        subscribers.remove(subscriber);
    }

    @Override
    public void notifySubscriber(float temperature, float humidity, float pressure) {
        for (Subscriber subscriber : subscribers) {
            subscriber.update(temperature, humidity, pressure);
        }
    }

    public void setMeasurements(float temperature, float humidity, float pressure) {
        notifySubscriber(temperature, humidity, pressure);
        System.out.println();
    }
}

class CurrentConditionsDisplay implements Subscriber {

    CurrentConditionsDisplay(Publisher publisher) {
        publisher.register(this);
    }

    @Override
    public void update(float temperature, float humidity, float pressure) {
        System.out.println(String.format("Temperature: %.1fF\nHumidity: %.1f%%", temperature, humidity));
    }
}

class ForecastDisplay implements Subscriber {

    float previousPressure;

    public ForecastDisplay(Publisher publisher) {
        previousPressure = 0;
        publisher.register(this);
    }



    @Override
    public void update(float temperature, float humidity, float pressure) {
        if (pressure > previousPressure) {
            System.out.println("Forecast: Improving");
        } else if (pressure == previousPressure) {
            System.out.println("Forecast: Same");
        } else { //pressure<previousPressure
            System.out.println("Forecast: Cooler");
        }

        previousPressure = pressure;
    }
}


public class WeatherApplication {

    public static void main(String[] args) {
        WeatherDispatcher weatherDispatcher = new WeatherDispatcher();

        CurrentConditionsDisplay currentConditions = new CurrentConditionsDisplay(weatherDispatcher);
        ForecastDisplay forecastDisplay = new ForecastDisplay(weatherDispatcher);

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            String[] parts = line.split("\\s+");
            weatherDispatcher.setMeasurements(Float.parseFloat(parts[0]), Float.parseFloat(parts[1]), Float.parseFloat(parts[2]));
            if(parts.length > 3) {
                int operation = Integer.parseInt(parts[3]);
                if(operation==1) {
                    weatherDispatcher.remove(forecastDisplay);
                }
                if(operation==2) {
                    weatherDispatcher.remove(currentConditions);
                }
                if(operation==3) {
                    weatherDispatcher.register(forecastDisplay);
                }
                if(operation==4) {
                    weatherDispatcher.register(currentConditions);
                }

            }
        }
    }
}