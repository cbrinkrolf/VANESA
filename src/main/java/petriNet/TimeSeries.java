package petriNet;


public class TimeSeries {
    private final Series time = new Series();
    private final Series values = new Series();

    public Series getTime() {
        return time;
    }

    public Series getValues() {
        return values;
    }

    public void add(double t, double v) {
        time.add(t);
        values.add(v);
    }

    public int size() {
        return time.size();
    }
}
