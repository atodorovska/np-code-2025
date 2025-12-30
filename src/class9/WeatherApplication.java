package class9;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

interface WeatherSubscriber {
    void update (float temperature, float humidity, float pressure);
    void display();
}

class CurrentConditionsDisplay implements WeatherSubscriber {

    private float temperature;
    private float humidity;

    public CurrentConditionsDisplay(WeatherDispatcher weatherDispatcher) {
        weatherDispatcher.register(this);
    }

    @Override
    public void update(float temperature, float humidity, float pressure) {
        this.temperature = temperature;
        this.humidity = humidity;

        display();
    }

    @Override
    public void display() {
        System.out.println(String.format("Temperature: %.1fF\nHumidity: %.1f%%",  temperature, humidity));
    }
}

class ForecastDisplay implements WeatherSubscriber {

    float previousPressure;
    float currentPressure;

    public ForecastDisplay(WeatherDispatcher weatherDispatcher) {
        weatherDispatcher.register(this);
    }

    @Override
    public void update(float temperature, float humidity, float pressure) {
        previousPressure = currentPressure;
        currentPressure = pressure;

        display();
    }

    @Override
    public void display() {
        System.out.print("Forecast: ");
        if (currentPressure == previousPressure){
            System.out.println("Same");
        } else if (currentPressure > previousPressure){
            System.out.println("Improving");
        } else {
            System.out.println("Cooler");
        }
    }
}



class WeatherDispatcher {
    Set<WeatherSubscriber> subscribers = new HashSet<WeatherSubscriber>();

    public void setMeasurements(float temperature, float humidity, float pressure) {
        for (WeatherSubscriber subscriber : subscribers) {
            subscriber.update(temperature, humidity, pressure);
        }


    }

    public void register(WeatherSubscriber subscriber) {
        subscribers.add(subscriber);
    }

    public void remove(WeatherSubscriber subscriber) {
        subscribers.remove(subscriber);
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