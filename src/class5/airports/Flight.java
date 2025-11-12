package class5.airports;

class Flight implements Comparable<Flight> {

    String from;
    String to;
    int time;
    int duration;

    public Flight(String from, String to, int time, int duration) {
        this.from = from;
        this.to = to;
        this.time = time;
        this.duration = duration;
    }

    @Override
    public int compareTo(Flight o) {
        int x = Integer.compare(this.time, o.time);
        if (x == 0) {
            return this.from.compareTo(o.from);
        }
        return x;
    }

    @Override
    public String toString() {
        int end = time + duration;
        int plus = end / (24 * 60);
        end %= (24 * 60);
        return String.format(
                "%s-%s %02d:%02d-%02d:%02d%s %dh%02dm", from, to, time / 60, time % 60,
                end / 60, end % 60, plus > 0 ? " +1d" : "", duration / 60, duration % 60
        );
    }
}
