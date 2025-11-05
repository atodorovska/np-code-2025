package class2;

import java.util.function.*;

public class FunctionalInterfacesTest {


    public static void main(String[] args) {
        //1. Function
        Function<String, Integer> function = str -> str.length();
        System.out.println(function.apply("Stefan"));

        BiFunction<Integer, Integer, Integer> bifunction = (a,b) -> a+b;
        System.out.println(bifunction.apply(5,10));

        //2. Predicate (condition)
        Predicate<Integer> isEven = number -> number % 2 == 0;
        System.out.println(isEven.test(5));
        System.out.println(isEven.test(6));

        //3. Supplier
        Supplier<Long> currentTimeInMs = () -> System.currentTimeMillis();
        System.out.println(currentTimeInMs.get());

        //4. Consumer
        Consumer<String> printer = str -> System.out.println(str);
        printer.accept("NP");
    }
}
