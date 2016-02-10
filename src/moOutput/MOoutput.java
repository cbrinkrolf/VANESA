package moOutput;

import graph.GraphInstance;
import graph.gui.Parameter;
import gui.MainWindow;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import petriNet.ContinuousTransition;
import petriNet.DiscreteTransition;
import petriNet.PNEdge;
import petriNet.Place;
import petriNet.StochasticTransition;
import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;

/**
 * @author Rafael, cbrinkro
 */
public class MOoutput {

	private static final boolean debug = MainWindow.developer;
	private static final String indentation = "  ";
	private static final String endl = System.getProperty("line.separator");

	private OutputStream os = null;
	private String modelName = null;
	private Pathway pw = null;

	private String places = "";
	private String edgesString = "";
	private double xmin = Double.MAX_VALUE, xmax = Double.MIN_VALUE, ymin = Double.MAX_VALUE, ymax = Double.MIN_VALUE;
	private final Hashtable<String, Integer> numInEdges = new Hashtable<String, Integer>();
	private final Hashtable<String, Integer> numOutEdges = new Hashtable<String, Integer>();
	private final Hashtable<String, Integer> actualInEdges = new Hashtable<String, Integer>();
	private final Hashtable<String, Integer> actualOutEdges = new Hashtable<String, Integer>();
	private final Hashtable<BiologicalNodeAbstract, String> nodeType = new Hashtable<BiologicalNodeAbstract, String>();
	private final HashMap<BiologicalNodeAbstract, String> vertex2name = new HashMap<BiologicalNodeAbstract, String>();
	private HashMap<String, String> inWeights = new HashMap<String, String>();
	private HashMap<String, String> outWeights = new HashMap<String, String>();

	private HashMap<BiologicalEdgeAbstract, String> bea2resultkey = new HashMap<BiologicalEdgeAbstract, String>();

	private String packageInfo = null;

	private boolean noIdent = false;

	double minX = Double.MAX_VALUE;
	double minY = Double.MAX_VALUE;
	double maxX = Double.MIN_VALUE;
	double maxY = Double.MIN_VALUE;

	double factor = -1;

	public MOoutput(OutputStream os, Pathway pathway) {
		this(os, pathway, null);
	}

	public MOoutput(OutputStream os, Pathway pathway, String packageInfo) {
		this.packageInfo = packageInfo;
		if (debug)
			System.out.println();
		if (debug)
			System.out.println("MOoutput(File " + pathway.getName() + " Pathway " + pathway + ")");

		this.os = os;
		this.modelName = pathway.getName();// .substring(0,
		// this.modelName = this.modelName.replace(".", "_");
		// pathway.getName().lastIndexOf("."));
		if (debug)
			System.out.println("Model Name = '" + modelName + "'");
		this.pw = pathway;

		try {
			write();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void write() throws IOException {

		prepare();

		buildConnections();
		buildNodes();

		// if (debug)
		// System.out.println(properties+places+transitions+edgesString);
		StringBuilder sb = new StringBuilder();
		sb.append("model '" + modelName + "'" + this.endl);
		;
		// os.write(new String("model '" + modelName + "'"'" + this.endl").getBytes());

		if (packageInfo != null) {
			// os.write(new String(this.indentation + this.packageInfo +
			// this.endl).getBytes());
			sb.append(this.indentation + this.packageInfo + this.endl);
			// os.write(new
			// String(this.indentation + "import PNlib = ConPNlib;" + this.endl).getBytes());
		}

		// if (this.packageInfo == null) {
		// sb.append(this.indentation + "inner PNlib.Settings settings1();" + this.endl);
		sb.append(this.indentation + "inner PNlib.Settings settings(showTokenFlow = true);" + this.endl);
		// }

		sb.append(places);
		sb.append("equation" + this.endl);
		sb.append(edgesString);
		sb.append(this.indentation + "annotation(Icon(coordinateSystem(extent={{"+(minX-50)+","+(minY-50)+"},{"+(maxX+50)+","+(maxY+50)+"}})), Diagram(coordinateSystem(extent={{"+(minX-50)+","+(minY-50)+"},{"+(maxX+50)+","+(maxY+50)+"}})));" + this.endl);
		sb.append("end '" + modelName + "';" + this.endl);

		String data = sb.toString();
		if (noIdent) {
			// System.out.println("replace");
			// remove "'"
			data = data.replaceAll("'", "");

			// remove "+" in names
			// data= data.replaceAll("(\\S)\\+", "$1_plus");

			// remove "-" in names
			// data= data.replaceAll("(\\S)\\-", "$1_");
		}
		os.write(data.getBytes());
		os.close();
	}

	private void prepare() {
		Iterator<BiologicalNodeAbstract> it = pw.getAllGraphNodes().iterator();
		BiologicalNodeAbstract bna;
		while (it.hasNext()) {
			bna = it.next();
			if (!bna.hasRef()) {
				Point2D p = pw.getGraph().getVertexLocation(bna);
				String biologicalElement = bna.getBiologicalElement();
				String name = "";
				if (biologicalElement.equals(biologicalElements.Elementdeclerations.place)
						|| biologicalElement.equals(biologicalElements.Elementdeclerations.s_place))
					name = bna.getName();
				else
					name = bna.getName();

				this.vertex2name.put(bna, name);
				// nodePositions.put(name, p);
				nodeType.put(bna, biologicalElement);
				// bioName.put(name, bna.getLabel());
				// bioObject.put(name, bna);

				if (xmin > p.getX())
					xmin = p.getX();
				if (xmax < p.getX())
					xmax = p.getX();
				if (ymin > p.getY())
					ymin = p.getY();
				if (ymax < p.getY())
					ymax = p.getY();
			}
		}
	}

	private void buildNodes() {
		for (int i = 1; i <= inhibitCount; i++)
			places += "PNlib.IA inhibitorArc" + i + ";" + this.endl;
		BiologicalNodeAbstract bna;
		ArrayList<String> names = new ArrayList<String>();
		Iterator<BiologicalNodeAbstract> it = pw.getAllGraphNodes().iterator();
		while (it.hasNext()) {
			// System.out.println("knoten");
			bna = it.next();
			if (!bna.hasRef()) {
				for (int i = 0; i < bna.getParameters().size(); i++) {
					// params += this.indentation + "parameter Real "+bna.getParameters().get(i).getName()+" = "+bna.getParameters().get(i).getValue()+";" + this.endl;
					places = places.concat(this.indentation + "parameter Real '_" + bna.getName() + "_" + bna.getParameters().get(i).getName() + "'");
					if (bna.getParameters().get(i).getUnit().length() > 0) {
						places = places.concat("(final unit=\"" + bna.getParameters().get(i).getUnit() + "\")");
					}
					places = places.concat(" = " + bna.getParameters().get(i).getValue() + ";" + this.endl);
					// System.out.println("drin");
				}
			}
		}

		it = pw.getAllGraphNodes().iterator();
		while (it.hasNext()) {
			bna = it.next();
			if (!bna.hasRef()) {
				String biologicalElement = bna.getBiologicalElement();
				// double km = Double.NaN, kcat = Double.NaN;
				// String ec = "";

				int in = pw.getGraph().getJungGraph().getInEdges(bna).size();
				int out = pw.getGraph().getJungGraph().getOutEdges(bna).size();

				if (bna.getRefs().size() > 0) {
					Iterator<BiologicalNodeAbstract> refIt = bna.getRefs().iterator();
					BiologicalNodeAbstract node;
					while (refIt.hasNext()) {
						node = refIt.next();
						in += pw.getGraph().getJungGraph().getInEdges(node).size();
						out += pw.getGraph().getJungGraph().getOutEdges(node).size();
					}

				}
				names.add(bna.getName());
				if (biologicalElement.equals(Elementdeclerations.place)) {

					Place place = (Place) bna;

					String atr = "startTokens=" + (int) place.getTokenStart() + ",minTokens=" + (int) place.getTokenMin() + ",maxTokens="
							+ (int) place.getTokenMax();
					places = places.concat(getPlaceString(place.getModellicaString(), bna, atr, in, out));

				} else if (biologicalElement.equals(Elementdeclerations.s_place)) {

					Place place = (Place) bna;
					String atr = "startMarks=" + place.getTokenStart() + ",minMarks=" + place.getTokenMin() + ",maxMarks=" + place.getTokenMax()
							+ ",t(final unit=\"mmol\")";
					places = places.concat(getPlaceString(place.getModellicaString(), bna, atr, in, out));

				} else if (biologicalElement.equals(Elementdeclerations.stochasticTransition)) {

					StochasticTransition t = (StochasticTransition) bna;
					// String atr = "h=" + t.getDistribution();
					String atr = "h=1.0";
					places = places.concat(getTransitionString(bna, t.getModellicaString(), bna.getName(), atr, in, out));

				} else if (biologicalElement.equals(Elementdeclerations.discreteTransition)) {

					DiscreteTransition t = (DiscreteTransition) bna;
					String atr = "delay=" + t.getDelay();
					places = places.concat(getTransitionString(bna, t.getModellicaString(), bna.getName(), atr, in, out));

				} else if (biologicalElement.equals(Elementdeclerations.continuousTransition)) {

					ContinuousTransition t = (ContinuousTransition) bna;
					// String atr = "maximumSpeed="+t.getMaximumSpeed();
					String atr;
					if (t.isKnockedOut()) {
						atr = "maximumSpeed(final unit=\"mmol/min\")=0";
					} else {
						atr = "maximumSpeed(final unit=\"mmol/min\")=" + this.replace(t.getMaximumSpeed(), t.getParameters(), t);
					}
					// System.out.println("atr");
					places = places.concat(getTransitionString(bna, t.getModellicaString(), bna.getName(), atr, in, out));
				}
			}
		}

	}

	private int inhibitCount = 0;

	private void buildConnections() {

		Iterator<BiologicalEdgeAbstract> it = pw.getAllEdges().iterator();

		BiologicalEdgeAbstract bea;
		PNEdge e;
		while (it.hasNext()) {

			bea = it.next();
			String fromString;
			String toString;
			String fromType;

			if (bea.getFrom().hasRef()) {
				fromString = vertex2name.get(bea.getFrom().getRef());
				fromType = nodeType.get(bea.getFrom().getRef());
			} else {
				fromString = vertex2name.get(bea.getFrom());
				fromType = nodeType.get(bea.getFrom());
			}
			if (bea.getTo().hasRef()) {
				toString = vertex2name.get(bea.getTo().getRef());
			} else {
				toString = vertex2name.get(bea.getTo());
			}
			// String fromString = vertex2name.get(bea.getFrom());
			// String toString = vertex2name.get(bea.getTo());
			// String fromType = nodeType.get(fromString);
			// String toType = nodeType.get(toString);

			// TODO funktionen werden zulassen
			if (bea instanceof PNEdge) {
				e = (PNEdge) bea;
				if (this.inWeights.containsKey(toString)) {
					this.inWeights.put(toString, this.inWeights.get(toString).concat("," + e.getModellicaFunction()));
				} else {
					this.inWeights.put(toString, e.getModellicaFunction() + "");
				}
				if (this.outWeights.containsKey(fromString)) {
					this.outWeights.put(fromString, this.outWeights.get(fromString).concat("," + e.getModellicaFunction()));
				} else {
					this.outWeights.put(fromString, e.getModellicaFunction() + "");
				}
			}

			if (numInEdges.containsKey(toString))
				numInEdges.put(toString, numInEdges.get(toString) + 1);
			else {
				numInEdges.put(toString, 1);
				actualInEdges.put(toString, 0);
			}

			if (numOutEdges.containsKey(fromString))
				numOutEdges.put(fromString, numOutEdges.get(fromString) + 1);
			else {
				numOutEdges.put(fromString, 1);
				actualOutEdges.put(fromString, 0);
			}
			if (bea instanceof PNEdge && bea.getBiologicalElement().equals(biologicalElements.Elementdeclerations.pnInhibitionEdge)) {
				inhibitCount++;
				if (fromType.equals(Elementdeclerations.s_place) || fromType.equals(Elementdeclerations.place)) {
					edgesString += this.indentation + "connect('" + fromString + "'.outTransition[" + (actualOutEdges.get(fromString) + 1) + "],"
							+ "inhibitorArc" + inhibitCount + ".inPlace);" + this.endl;
					edgesString += this.indentation + "connect(" + "inhibitorArc" + inhibitCount + ".outTransition,'" + toString + "'.inPlaces["
							+ (actualInEdges.get(toString) + 1) + "]) " + this.getFromToAnnotation(bea.getFrom(), bea.getTo()) + ";" + this.endl;
					actualOutEdges.put(fromString, actualOutEdges.get(fromString) + 1);
					actualInEdges.put(toString, actualInEdges.get(toString) + 1);
				}

			} else if (fromType.equals(Elementdeclerations.s_place)) {
				edgesString = edgesString.concat(getConnectionStringPT(fromString, toString, bea));
			} else if (fromType.equals(Elementdeclerations.place)) {
				edgesString = edgesString.concat(getConnectionStringPT(fromString, toString, bea));
			} else {
				edgesString = edgesString.concat(getConnectionStringTP(fromString, toString, bea));
			}

		}
	}

	private String getPlaceString(String element, BiologicalNodeAbstract bna, String atr, int inEdges, int outEdges) {

		for (int i = 0; i < bna.getParameters().size(); i++) {
			// params += this.indentation + "parameter Real "+bna.getParameters().get(i).getName()+" = "+bna.getParameters().get(i).getValue()+";" + this.endl;
		}
		return this.indentation + element + " '" +

		// falls die anzahlen nicht stimmen
		// +numInEdges.get(bna.getVertex().toString())
		// +numOutEdges.get(bna.getVertex().toString())
		//
				bna.getName() + "'(nIn=" + inEdges + ",nOut=" + outEdges + "," + atr + ") "
				// +(bioName.containsKey(name)?"(biologicalName = \""+bioName.get(name)+"\")":"")
				// +" annotation(Placement(transformation(x = "+Math.floor(scale*(p.getX()+xshift))+", y = "+Math.floor(scale*(-(p.getY()+yshift)))+", scale = 0.84), "
				// +"iconTransformation(x = "
				// +Math.floor(scale*(p.getX()+xshift))+", y = "+Math.floor(scale*(-(p.getY()+yshift)))+", scale = 0.84)))"
				+  getPlacementAnnotation(bna) + ";" + this.endl;
	}

	private String getTransitionString(BiologicalNodeAbstract bna, String element, String name, String atr, int inEdges, int outEdges) {
		for (int i = 0; i < bna.getParameters().size(); i++) {
			// params += this.indentation + "parameter Real "+bna.getParameters().get(i).getName()+" = "+bna.getParameters().get(i).getValue()+";" + this.endl;
		}

		// String inNumbers = "";
		String inNumbers = "";
		String outNumbers = "";

		if (this.inWeights.containsKey(name)) {
			inNumbers = this.inWeights.get(name);
		}
		if (this.outWeights.containsKey(name)) {
			outNumbers = this.outWeights.get(name);
		}

		String in = "";
		String out = "";

		in = "{" + inNumbers + "}";
		out = "{" + outNumbers + "}";
		// System.out.println("name: "+bna.getName());
		if (in.length() == 2) {
			in = "fill(1,'" + bna.getName() + "'.nIn)";
		}
		if (out.length() == 2) {
			out = "fill(1,'" + bna.getName() + "'.nOut)";
		}
		// System.out.println("inPropper: " + in);
		// System.out.println("outPropper: " + out);

		return this.indentation + element + " '" + bna.getName() + "'(nIn=" + inEdges + ",nOut=" + outEdges + "," + atr + ",arcWeightIn=" + in + ",arcWeightOut="
				+ out + ") " + getPlacementAnnotation(bna) + ";" + this.endl;
	}

	private String getConnectionStringTP(String from, String to, BiologicalEdgeAbstract bea) {
		// String from = bea.getFrom().getName();
		// String to = bea.getTo().getName();
		String result = this.indentation + "connect('" + from + "'.outPlaces[" + (actualOutEdges.get(from) + 1) + "],'" + to + "'.inTransition["
				+ (actualInEdges.get(to) + 1) + "]) " + this.getFromToAnnotation(bea.getFrom(), bea.getTo())+";"
				// +" annotation(Line(points = {{"
				// +Math.floor(scale*(fromPoint.getX()+xshift)+10)+","
				// +Math.floor(scale*(-(fromPoint.getY()+yshift))+((numOutEdges.get(from)-1)*pinabstand/2-(actualOutEdges.get(from))*pinabstand))+"},{"
				// +Math.floor(scale*(toPoint. getX()+xshift)-19)+","
				// +Math.floor(scale*(-(toPoint.
				// getY()+yshift))+((numInEdges.get(to)-1)*pinabstand/2-(actualInEdges.get(to))*pinabstand))+"}}));"
				+ this.endl;
		// System.out.println(to+".tSumIn_["+(actualInEdges.get(to) + 1)+"]");
		this.bea2resultkey.put(bea, "'" + to + "'.tokenFlow.inflow[" + (actualInEdges.get(to) + 1) + "]");

		actualInEdges.put(to, actualInEdges.get(to) + 1);
		actualOutEdges.put(from, actualOutEdges.get(from) + 1);
		return result;
	}

	private String getConnectionStringPT(String from, String to, BiologicalEdgeAbstract bea) {
		// String from = bea.getFrom().getName();
		// String to = bea.getTo().getName();
		String result = this.indentation + "connect('" + from + "'.outTransition[" + (actualOutEdges.get(from) + 1) + "],'" + to + "'.inPlaces["
				+ (actualInEdges.get(to) + 1) + "]) " + this.getFromToAnnotation(bea.getFrom(), bea.getTo()) + ";"
				// +" annotation(Line(points = {{"
				// +Math.floor(scale*(fromPoint.getX()+xshift)+19)+","
				// +Math.floor(scale*(-(fromPoint.getY()+yshift))+((numOutEdges.get(from)-1)*pinabstand/2-(actualOutEdges.get(from))*pinabstand))+"},{"
				// +Math.floor(scale*(toPoint. getX()+xshift)-10)+","
				// +Math.floor(scale*(-(toPoint.
				// getY()+yshift))+((numInEdges.get(to)-1)*pinabstand/2-(actualInEdges.get(to))*pinabstand))+"}}));"
				+ this.endl;
		// System.out.println(from+".tSumOut_["+(actualOutEdges.get(from) +
		// 1)+"]");
		this.bea2resultkey.put(bea, "'" + from + "'.tokenFlow.outflow[" + (actualOutEdges.get(from) + 1) + "]");

		actualInEdges.put(to, actualInEdges.get(to) + 1);
		actualOutEdges.put(from, actualOutEdges.get(from) + 1);
		return result;
	}

	public HashMap<BiologicalEdgeAbstract, String> getBea2resultkey() {
		return bea2resultkey;
	}

	private String replace(String function, ArrayList<Parameter> params, BiologicalNodeAbstract node) {
		StringBuilder mFunction = new StringBuilder(function);
		Set<Character> chars = new HashSet<Character>();
		chars.add('*');
		chars.add('+');
		chars.add('/');
		chars.add('-');
		chars.add('^');
		chars.add('(');
		chars.add(')');
		chars.add(' ');

		// replace parameters
		ArrayList<String> paramNames = new ArrayList<String>();

		for (int i = 0; i < params.size(); i++) {
			paramNames.add(params.get(i).getName());
		}

		Collections.sort(paramNames, new StringLengthComparator());

		String name = "";
		// System.out.println("drin");
		// Character c;

		// Character l;
		Character r;
		boolean check;
		int index = 0;
		int idxNew = 0;
		String insert;
		for (int i = 0; i < paramNames.size(); i++) {
			index = 0;
			name = paramNames.get(i);

			// System.out.println("name: "+name );
			// System.out.println("fkt: "+mFunction);

			while (mFunction.indexOf(name, index) >= 0) {
				check = false;
				idxNew = mFunction.indexOf(name, index);
				// System.out.println("index: "+index);
				// System.out.println("idxNew: "+idxNew);
				if (mFunction.length() >= idxNew + name.length()) {
					// System.out.println("groesser gleich");
					if (mFunction.length() > idxNew + name.length()) {
						// System.out.println("groesser");
						r = mFunction.charAt(idxNew + name.length());
					} else {
						// System.out.println("else");
						// Parameter is last term of function
						r = ' ';
					}
					if (idxNew == 0) {
						check = true;
					} else {
						check = chars.contains(mFunction.charAt(idxNew - 1));
					}
					// System.out.println("l: "+r+" r: "+r);
					// if (!Character.isDigit(c) && !Character.isAlphabetic(c))
					// {
					if (check && chars.contains(r)) {
						// mFunction = mFunction.replaceFirst(name, mNames
						// .get(name));
						insert = "'_" + node.getName() + "_" + name + "'";
						mFunction.replace(idxNew, idxNew + name.length(), insert);
						// mFunction.insert(idxNew, "_" + node.getName() + "_");
						// index = idxNew + name.length() + 2
						// + node.getName().length();
						index = idxNew + insert.length();
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
		// System.out.println("2: "+mFunction);
		// replace places
		GraphInstance graphInstance = new GraphInstance();
		Pathway pw = graphInstance.getPathway();
		Iterator<BiologicalNodeAbstract> it = pw.getAllGraphNodes().iterator();
		ArrayList<String> names = new ArrayList<String>();
		BiologicalNodeAbstract bna;
		Place p;
		HashMap<String, String> referenceMapping = new HashMap<String, String>();

		while (it.hasNext()) {
			bna = it.next();
			if (bna instanceof Place) {
				p = (Place) bna;
				// names.add("P"+p.getID());
				names.add(p.getName());
				if (p.hasRef()) {
					referenceMapping.put(p.getName(), p.getRef().getName());
				} else {
					referenceMapping.put(p.getName(), p.getName());
				}
				// mNames.put("P"+p.getID(), "P"+p.getID() + ".t");
			}
		}

		Collections.sort(names, new StringLengthComparator());
		// Character c;
		// System.out.println("drin");
		index = 0;
		idxNew = 0;

		for (int i = 0; i < names.size(); i++) {
			// check = false;
			index = 0;
			name = names.get(i);

			// System.out.println("name: "+name );
			// System.out.println("fkt: "+mFunction);
			// System.out.println("n: "+name);
			while (mFunction.indexOf(name, index) >= 0) {
				check = false;
				idxNew = mFunction.indexOf(name, index);
				// System.out.println("index: "+index);
				// System.out.println("idxNew: "+idxNew);
				if (mFunction.length() >= idxNew + name.length()) {
					// System.out.println("groesser gleich");
					if (mFunction.length() > idxNew + name.length()) {
						// System.out.println("groesser");
						r = mFunction.charAt(idxNew + name.length());
					} else {
						// System.out.println("else");
						r = ' ';
					}
					// System.out.println("c: "+c);
					// System.out.println(mFunction.charAt(idxNew));

					if (idxNew == 0) {
						check = true;
					} else {
						check = chars.contains(mFunction.charAt(idxNew - 1));
					}

					/*
					 * if (idxNew > 0) { if
					 * (chars.contains(mFunction.charAt(idxNew - 1))) { check =
					 * true; } } else { check = true; }
					 */
					// System.out.println("r: "+r);
					if (check && chars.contains(r)) {
						// mFunction = mFunction.replaceFirst(name, mNames
						// .get(name));
						insert = "'" + referenceMapping.get(name) + "'.t";
						mFunction.replace(idxNew, idxNew + name.length(), insert);

						// mFunction.insert(idxNew + name.length(), ".t");
						index = idxNew + insert.length();
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

	class StringLengthComparator implements Comparator<String> {

		// compares descending
		public int compare(String s1, String s2) {
			int i = s2.length() - s1.length();
			return i;
		}
	}

	private String getPlacementAnnotation(BiologicalNodeAbstract bna) {

		double x = pw.getGraph().getVertexLocation(bna).getX();
		double y = pw.getGraph().getVertexLocation(bna).getY()*factor;
		if (x < minX) {
			minX = x;
		}
		if (x > maxX) {
			maxX = x;
		}
		if (y < minY) {
			minY = y;
		}
		if (y > maxY) {
			maxY = y;
		}

		return "annotation(Placement(visible=true, transformation(origin={" + x + "," + y + "}, extent={{-20,-20}, {20,20}}, rotation=0)))";
	}

	private String getFromToAnnotation(BiologicalNodeAbstract from, BiologicalNodeAbstract to){

		Point2D p1;
		Point2D p2;
		String color;

		if(from.hasRef()){
			p1 = pw.getGraph().getVertexLocation(from.getRef());
		}else{
			p1 = pw.getGraph().getVertexLocation(from);
		}
		if(to.hasRef()){
			p2 = pw.getGraph().getVertexLocation(to.getRef());
		}else{
			p2 = pw.getGraph().getVertexLocation(to);
		}

		if(from.hasRef() || to.hasRef()){
			color = "{180, 180, 180}";
		}else{
			color = "{0, 0, 0}";
		}

		return "annotation(Line(color=" + color + ", points={{"+p1.getX()+","+p1.getY()*factor+"}, {"+p2.getX()+","+p2.getY()*factor+"}}))";
	}
}
