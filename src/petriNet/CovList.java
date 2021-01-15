package petriNet;

import java.util.ArrayList;

public class CovList implements Cloneable {

	private double[] tokens;

	public CovList(int number) {
		this.tokens = new double[number];
	}

	public void addTokens(double[] newTokens) {
		if (this.tokens.length == newTokens.length) {
			for (int i = 0; i < this.tokens.length; i++) {
				if (this.tokens[i] != -1) {
					this.tokens[i] = this.tokens[i] + newTokens[i];
				}
			}
		}
	}

	/*
	 * public boolean isGreater(CovList cl) { int[] clTokens = cl.getElements();
	 * 
	 * if (this.tokens.length == clTokens.length) {
	 * 
	 * } return false; }
	 */

	public ArrayList<Integer> getGreaterIndexs(CovList cl) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		double[] clTokens = cl.getElements();

		if (this.tokens.length == clTokens.length) {
			for (int i = 0; i < this.tokens.length; i++) {
				if ((this.tokens[i] == -1.0 && clTokens[i] != -1.0)
						|| (this.tokens[i] > clTokens[i])) {
					list.add(new Integer(i));
				} else if ((clTokens[i] == -1.0 && this.tokens[i] != -1.0)
						|| (this.tokens[i] < clTokens[i])) {
					return new ArrayList<Integer>();
				}
			}
		}
		// System.out.println("groessere Indexe:");
		// System.out.println(list);
		return list;
	}

	public boolean isGreaterEqualCol(double[] values) {
		boolean greater = false;
		for (int i = 0; i < this.tokens.length; i++) {
			if ((this.tokens[i] + values[i] >= 0 || this.tokens[i] == -1)) {
				greater = true;
			} else if (this.tokens[i] + values[i] < 0) {
				return false;
			}
		}
		return greater;
	}
	
	public boolean isGreaterEqual(double[] values){
		boolean ge = false;
		for (int i = 0; i < this.tokens.length; i++) {
			if ((this.tokens[i] >= values[i] || this.tokens[i] == -1)) {
				ge = true;
			} else if (this.tokens[i] < values[i]) {
				return false;
			}
		}
		return ge;
		
	}

	public boolean isGreater(double[] values) {
		boolean greater = false;
		for (int i = 0; i < this.tokens.length; i++) {
			// System.out.println(this.tokens[i]+ ", " +values[i]);
			if ((this.tokens[i] == -1.0 && values[i] != -1.0)
					|| (this.tokens[i] > values[i] && values[i] != -1.0)) {
				greater = true;
				// System.out.println("groesser");
			} else if ((values[i] == -1.0 && this.tokens[i] != -1.0)
					|| this.tokens[i] < values[i]) {
				// System.out.println("kleiner");
				return false;
			}
		}
		return greater;
	}

	public boolean isEqual(double[] values) {
		// boolean equal = false;
		if (values.length == this.tokens.length) {
			for (int i = 0; i < this.tokens.length; i++) {
				if (this.tokens[i] != values[i]) {
					// System.out.println(this.tokens[i] +"!=" + values[i]);
					return false;
				}
			}
		} else {
			// System.out.println("laenge unterschiedlich");
			return false;
		}
		return true;
	}

	public double getElementAt(int index) {
		return this.tokens[index];
	}

	public void setElementAt(int index, double value) {
		this.tokens[index] = value;
	}

	public double[] getElements() {
		return this.tokens;
	}

	public void setElements(double[] newElements) {
		if (this.tokens.length == newElements.length) {
			this.tokens = newElements;
		}
	}

	public int getSize() {
		return this.tokens.length;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[" + this.tokens[0]);
		for (int i = 1; i < this.tokens.length; i++) {
			sb.append(", " + this.tokens[i]);
		}
		sb.append("]");
		return sb.toString();
	}

	public CovList clone() {
		try {
			CovList cl = (CovList) super.clone();
			cl.setElements(this.tokens.clone());
			return cl;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
