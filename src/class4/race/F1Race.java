package class4.race;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class F1Race {

    private List<Driver> drivers;

    public void readResults(InputStream in) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        drivers = reader.lines()
                        .filter(Objects::nonNull)
                        .map(Driver::createDriver)
                        .collect(Collectors.toList());
    }

    public void printSorted(PrintStream out) {
        PrintWriter writer = new PrintWriter(out);
        Collections.sort(drivers);
        int c = 1;

        for (Driver driver : drivers) {
            writer.println(c + ". " + driver.toString());
            c++;
        }
        writer.flush();
        writer.close();
    }

    public void printSortedWithStreams(PrintStream out) {
        PrintWriter writer = new PrintWriter(out);
        Collections.sort(drivers);
        IntStream.range(0, drivers.size())
                 .forEach(i -> writer.println(i + ". " + drivers.get(i).toString()));

        writer.flush();
        writer.close();
    }
}
