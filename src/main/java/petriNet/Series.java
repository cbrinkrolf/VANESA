package petriNet;

import java.util.ArrayList;
import java.util.List;

public class Series {
	private final List<Double> values = new ArrayList<>();

	public List<Double> getAll() {
		return this.values;
	}

	public Double get(int pos) {
		if (pos < values.size()) {
			return values.get(pos);
		}
		return null;
	}

	public void add(Double d) {
		values.add(d);
	}

	public int size() {
		return values.size();
	}

	public void setValue(int index, Double d) {
		values.set(index, d);
	}
}
