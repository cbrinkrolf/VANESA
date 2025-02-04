package petriNet;

import java.util.ArrayList;

public class CovList implements Cloneable {
	private final double[] tokens;

	public CovList(int count) {
		tokens = new double[count];
	}

	public void addTokens(double[] newTokens) {
		if (tokens.length == newTokens.length) {
			for (int i = 0; i < tokens.length; i++) {
				if (tokens[i] != -1) {
					tokens[i] = tokens[i] + newTokens[i];
				}
			}
		}
	}

	public ArrayList<Integer> getGreaterIndices(CovList cl) {
		final ArrayList<Integer> list = new ArrayList<>();
		final double[] clTokens = cl.getElements();
		if (tokens.length == clTokens.length) {
			for (int i = 0; i < tokens.length; i++) {
				if ((tokens[i] == -1.0 && clTokens[i] != -1.0) || (tokens[i] > clTokens[i])) {
					list.add(i);
				} else if ((clTokens[i] == -1.0 && tokens[i] != -1.0) || (tokens[i] < clTokens[i])) {
					return new ArrayList<>();
				}
			}
		}
		return list;
	}

	public boolean isGreaterEqualCol(double[] values) {
		boolean greater = false;
		for (int i = 0; i < tokens.length; i++) {
			if ((tokens[i] + values[i] >= 0 || tokens[i] == -1)) {
				greater = true;
			} else if (tokens[i] + values[i] < 0) {
				return false;
			}
		}
		return greater;
	}

	public boolean isGreaterEqual(double[] values) {
		boolean ge = false;
		for (int i = 0; i < tokens.length; i++) {
			if ((tokens[i] >= values[i] || tokens[i] == -1)) {
				ge = true;
			} else if (tokens[i] < values[i]) {
				return false;
			}
		}
		return ge;
	}

	public boolean isGreater(double[] values) {
		boolean greater = false;
		for (int i = 0; i < tokens.length; i++) {
			if ((tokens[i] == -1.0 && values[i] != -1.0) || (tokens[i] > values[i] && values[i] != -1.0)) {
				greater = true;
			} else if ((values[i] == -1.0 && tokens[i] != -1.0) || tokens[i] < values[i]) {
				return false;
			}
		}
		return greater;
	}

	public boolean isEqual(double[] values) {
		if (values.length != tokens.length) {
			return false;
		}
		for (int i = 0; i < tokens.length; i++) {
			if (tokens[i] != values[i]) {
				return false;
			}
		}
		return true;
	}

	public double getElementAt(int index) {
		return tokens[index];
	}

	public void setElementAt(int index, double value) {
		tokens[index] = value;
	}

	public double[] getElements() {
		return tokens;
	}

	public void setElements(double[] newElements) {
		if (tokens.length == newElements.length) {
			System.arraycopy(newElements, 0, tokens, 0, tokens.length);
		}
	}

	public int getSize() {
		return tokens.length;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[").append(tokens[0]);
		for (int i = 1; i < tokens.length; i++) {
			sb.append(", ").append(tokens[i]);
		}
		sb.append("]");
		return sb.toString();
	}

	public CovList clone() {
		try {
			final CovList cl = (CovList) super.clone();
			cl.setElements(tokens.clone());
			return cl;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
