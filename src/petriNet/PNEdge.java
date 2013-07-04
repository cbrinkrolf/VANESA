package petriNet;

import graph.GraphInstance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
//import edu.uci.ics.jung.graph.Edge;
//import edu.uci.ics.jung.graph.decorators.EdgeShape;

public class PNEdge extends BiologicalEdgeAbstract {

	// Wahrscheinlichkeit, dass diese Kante aktiviert wird
	private double activationProbability;
	// Anzahl an Tokens, die "wandern"
	// private double passingTokens;

	private double lowerBoundary;
	private double upperBoundary;

	// hier gibt es bestimmt einen besseren Datentyp. Dieser ist erstmal ein
	// Platzhalter!
	// TODO vllt besser boolean, inhibition true/false?
	private String condition;
	private String type;
	private String function;

	private boolean wasUndirected = false;

	public boolean wasUndirected() {
		return wasUndirected;
	}

	public void wasUndirected(boolean wasUndirected) {
		this.wasUndirected = wasUndirected;
	}

	private FunctionParser fp = new FunctionParser();

	public PNEdge(BiologicalNodeAbstract from, BiologicalNodeAbstract to, String label, String name, String type,
			String edgeFunction) {
		super(edgeFunction, name, from, to);
		super.setDirected(true);
		this.type = type;
		if (type.equals(biologicalElements.Elementdeclerations.inhibitionEdge)
				|| type.equals(biologicalElements.Elementdeclerations.inhibitor))
			setBiologicalElement(biologicalElements.Elementdeclerations.pnInhibitionEdge);
		else if (type
				.equals(biologicalElements.Elementdeclerations.pnDiscreteEdge)
				|| type.equals(biologicalElements.Elementdeclerations.pnContinuousEdge)
				|| type.equals(biologicalElements.Elementdeclerations.pnInhibitionEdge))
			setBiologicalElement(type);
		else
			setBiologicalElement(biologicalElements.Elementdeclerations.pnDiscreteEdge);

		this.function = edgeFunction;
		this.setAbstract(false);
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
		super.setLabel(this.function);
		//this.validateFunction();
	}

	// public PNEdge(Edge edge, String label, String name, String type, double
	// passingTokens){
	// this(edge, label, name, type);
	// this.passingTokens = passingTokens;
	// }

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public double getActivationProbability() {
		return activationProbability;
	}

	public void setActivationProbability(double activationProbability) {
		this.activationProbability = activationProbability;
	}

	public double getPassingTokens() {
		// return passingTokens;
		return fp.parse(this.function);
	}

	/*
	 * public void setPassingTokens(double passingTokens) { this.passingTokens =
	 * passingTokens; super.setLabel(passingTokens+""); }
	 */

	public double getLowerBoundary() {
		return lowerBoundary;
	}

	public void setLowerBoundary(double lowerBoundary) {
		this.lowerBoundary = lowerBoundary;
	}

	public double getUpperBoundary() {
		return upperBoundary;
	}

	public void setUpperBoundary(double upperBoundary) {
		this.upperBoundary = upperBoundary;
	}

	/*
	 * public String getCondition() { return condition; }
	 * 
	 * public void setCondition(String condition) { this.condition = condition;
	 * }
	 */

	public boolean isConditionFulfilled() {
		return true;
	}

	private boolean validateFunction() {
		GraphInstance graphInstance = new GraphInstance();
		Pathway pw = graphInstance.getPathway();
		HashSet<BiologicalNodeAbstract> hs = pw.getAllNodes();
		// System.out.println("nodes: " + hs.size());
		Iterator it = hs.iterator();
		ArrayList<String> names = new ArrayList<String>();

		// HashMap<String, Double> name2token = new HashMap<String, Double>();

		HashMap<String, Double> name2token = new HashMap<String, Double>();

		// ArrayList<String> names = new ArrayList<String>();
		BiologicalNodeAbstract bna;
		Place p;
		while (it.hasNext()) {
			bna = (BiologicalNodeAbstract) it.next();
			if (bna instanceof Place) {
				p = (Place) bna;
				names.add(p.getName());
				name2token.put(p.getName(), p.getToken());
				// System.out.println("hinzugefuegt");
			}
		}

		Collections.sort(names, new StringLengthComparator());
		// System.out.println("nodes: "+name2token.size());
		// System.out.println("keyset:");
		// System.out.println(name2token.keySet());
		// //Object[] k =name2token.keySet().toArray();
		Character c;
		// Object o;
		String name = "";
		String fctCopy = this.function;
		for (int i = 0; i < names.size(); i++) {
			name = names.get(i);
			// System.out.println(name);
			// System.out.println("node: "+o);
			// System.out.println("place: "+s);
			while (fctCopy.indexOf(name) >= 0) {
				if (fctCopy.length() >= fctCopy.indexOf(name) + name.length()) {
					if (fctCopy.length() > fctCopy.indexOf(name)
							+ name.length()) {
						c = fctCopy.charAt(fctCopy.indexOf(name)
								+ name.length());
					} else {
						c = 'a';
					}

					// System.out.println("nachfolgendes c: "+c);
					if (!Character.isDigit(c)) {
						// System.out.println(o + " :" +
						// t.indexOf(o.toString()));
						fctCopy = fctCopy.replaceFirst(name,
								name2token.get(name) + "");
						// System.out.println(function);
					} else {
						System.out.println("Error");
						return false;
					}
				} else {
					break;
				}
			}
		}
		// this.getModellicaFunction();
		try {
		//	System.out.println("funktion: " + this.function);
		//	System.out.println("funktion ausgewertet: " + fp.parse(fctCopy));
			this.getModellicaFunction();
			// System.out.println("mFkt");

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("not valid");
			return false;
		}
		// System.out.println("valid");
		return true;
	}

	public String getModellicaFunction() {		
		StringBuilder mFunction = new StringBuilder(this.function);
		GraphInstance graphInstance = new GraphInstance();
		Pathway pw = graphInstance.getPathway();
		HashSet<BiologicalNodeAbstract> hs = pw.getAllNodes();
		Iterator it = hs.iterator();
		ArrayList<String> names = new ArrayList<String>();
		HashMap<String, String> mNames = new HashMap<String, String>();
		BiologicalNodeAbstract bna;
		Place p;
		while (it.hasNext()) {
			bna = (BiologicalNodeAbstract) it.next();
			if (bna instanceof Place) {
				p = (Place) bna;
				names.add("P"+p.getID());
				mNames.put("P"+p.getID(), "P"+p.getID() + ".t");
			}
		}

		Collections.sort(names, new StringLengthComparator());
		Character c;
		String name = "";
		// System.out.println("drin");
		int index = 0;
		int idxNew = 0;
		for (int i = 0; i < names.size(); i++) {
			index = 0;
			name = names.get(i);
			// System.out.println("name: "+name );
			// System.out.println("fkt: "+mFunction);

			while (mFunction.indexOf(name, index) >= 0) {
				idxNew = mFunction.indexOf(name, index);
				// System.out.println("index: "+index);
				// System.out.println("idxNew: "+idxNew);
				if (mFunction.length() >= idxNew + name.length()) {
					// System.out.println("groesser gleich");
					if (mFunction.length() > idxNew + name.length()) {
						// System.out.println("groesser");
						c = mFunction.charAt(idxNew + name.length());
					} else {
						// System.out.println("else");
						c = 'a';
					}
					// System.out.println("c: "+c);
					if (!Character.isDigit(c) && c != '.') {
						// mFunction = mFunction.replaceFirst(name, mNames
						// .get(name));
						mFunction.insert(idxNew + name.length(), ".t");
						index = idxNew + name.length() + 2;
						// System.out.println(name+" ersetzt durch: "+mNames.get(name));
					} else {
						index = idxNew + name.length();
						// System.out.println("Error");
						// break;
					}
				} else {
					// /System.out.println("break");
					break;
				}
			}
		}
		// System.out.println("druch");
		// System.out.println("mFkt: " + mFunction);
		return mFunction.toString();
	}
}

class StringLengthComparator implements Comparator<String> {

	// compares descending
	public int compare(String s1, String s2) {
		int i = s2.length() - s1.length();
		return i;
	}

}
