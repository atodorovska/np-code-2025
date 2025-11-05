package class4.temperatures;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DailyTemperatures {

    private List<DailyMeasurement> measurements;

    public DailyTemperatures() {
        this.measurements = new ArrayList<>();
    }

    public void readTemperatures(InputStream in) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        measurements = reader.lines()
                             .filter(Objects::nonNull)
                             .map(DailyMeasurement::createMeasurement)
                             .collect(Collectors.toList());
    }

    public void writeDailyStats(PrintStream out, char scale) {
        PrintWriter writer = new PrintWriter(out);
        measurements.stream().sorted().forEach(m -> writer.println(DailyMeasurement.getMeasurementStats(m, scale)));
        writer.flush();
    }
}
