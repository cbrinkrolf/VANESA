package moOutput;

import graph.GraphInstance;
import graph.gui.Parameter;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileWriter;
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

//import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;

/**
 * @author Rafael, cbrinkro
 */
public class MOoutput {

	private static final boolean debug = !false;

	private OutputStream os = null;
	private String modelName = null;
	private Pathway pw = null;
	// private FileWriter fwriter;

	private String places = "";
	private String edgesString = "";
	private double xshift = 0, yshift = 0;
	private double xmin = Double.MAX_VALUE, xmax = Double.MIN_VALUE, ymin = Double.MAX_VALUE, ymax = Double.MIN_VALUE;
	private final double scale = 2;
	private final Hashtable<String, Integer> numInEdges = new Hashtable<String, Integer>();
	private final Hashtable<String, Integer> numOutEdges = new Hashtable<String, Integer>();
	private final Hashtable<String, Integer> actualInEdges = new Hashtable<String, Integer>();
	private final Hashtable<String, Integer> actualOutEdges = new Hashtable<String, Integer>();
	// private final Hashtable<String, Point2D> nodePositions = new
	// Hashtable<String, Point2D>();
	private final Hashtable<BiologicalNodeAbstract, String> nodeType = new Hashtable<BiologicalNodeAbstract, String>();
	private final Hashtable<String, String> bioName = new Hashtable<String, String>();
	// private final Hashtable<String, Object> bioObject = new Hashtable<String,
	// Object>();
	// private HashMap<String, Double> edgeToWeight = new HashMap<String,
	private final HashMap<BiologicalNodeAbstract, String> vertex2name = new HashMap<BiologicalNodeAbstract, String>();
	// Double>();
	private HashMap<String, String> inWeights = new HashMap<String, String>();
	private HashMap<String, String> outWeights = new HashMap<String, String>();

	private HashMap<BiologicalEdgeAbstract, String> bea2resultkey = new HashMap<BiologicalEdgeAbstract, String>();

	private String packageInfo = null;

	private boolean noIdent = false;

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

		// fwriter = new FileWriter(file);

		prepare();
		// buildProperties();

		buildConnections();
		buildNodes();

		// if (debug)
		// System.out.println(properties+places+transitions+edgesString);
		StringBuilder sb = new StringBuilder();
		sb.append("model '" + modelName + "'\r\n");
		;
		// os.write(new String("model '" + modelName + "'\r\n").getBytes());

		if (packageInfo != null) {
			// os.write(new String("\t" + this.packageInfo +
			// "\r\n").getBytes());
			sb.append("\t" + this.packageInfo + "\r\n");
			// os.write(new
			// String("\timport PNlib = ConPNlib;\r\n").getBytes());
		}

		// fwriter.write(properties);
		// if (this.packageInfo == null) {
		// sb.append("\tinner PNlib.Settings settings1();\r\n");
		sb.append("\tinner PNlib.Settings settings(showTokenFlow = true);\r\n");
		// }

		// os.write(places.getBytes());
		sb.append(places);
		// fwriter.write(transitions);
		// os.write(new String("equation\r\n").getBytes());
		sb.append("equation\r\n");
		// os.write(edgesString.getBytes());
		sb.append(edgesString);
		// os.write(new String("end '" + modelName + "';").getBytes());
		sb.append("end '" + modelName + "';");

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

	/*
	 * private void buildProperties() { xshift = -(xmin + xmax) / 2; yshift =
	 * -(ymin + ymax) / 2;
	 * 
	 * if (debug) System.out.println("shift=" + xshift + " " + yshift);
	 * 
	 * properties = properties
	 * .concat("  annotation(Diagram(coordinateSystem(extent = {" + "{" + (int)
	 * Math.floor(scale * (xmin + xshift - 20)) + "," + (int) Math.floor(scale *
	 * (ymin + yshift - 20)) + "},{" + (int) Math.floor(scale * (xmax + xshift +
	 * 20)) + "," + (int) Math.floor(scale * (ymax + yshift + 20)) + "}" +
	 * "})));\r\n"); }
	 */

	/*
	 * private String getWeightedEdges() {
	 * 
	 * String weightedEdges = "";
	 * 
	 * return weightedEdges; }
	 */

	private void buildNodes() {
		for (int i = 1; i <= inhibitCount; i++)
			places += "PNlib.IA inhibitorArc" + i + ";\r\n";
		BiologicalNodeAbstract bna;
		ArrayList<String> names = new ArrayList<String>();
		Iterator<BiologicalNodeAbstract> it = pw.getAllGraphNodes().iterator();
		while (it.hasNext()) {
			// System.out.println("knoten");
			bna = it.next();
			if (!bna.hasRef()) {
				for (int i = 0; i < bna.getParameters().size(); i++) {
					// params+="\t\tparameter Real "+bna.getParameters().get(i).getName()+" = "+bna.getParameters().get(i).getValue()+";\r\n";
					places = places.concat("\tparameter Real '_" + bna.getName() + "_" + bna.getParameters().get(i).getName() + "'");
					if (bna.getParameters().get(i).getUnit().length() > 0) {
						places = places.concat("(final unit=\"" + bna.getParameters().get(i).getUnit() + "\")");
					}
					places = places.concat(" = " + bna.getParameters().get(i).getValue() + ";\r\n");
					// System.out.println("drin");
				}
			}
		}

		it = pw.getAllGraphNodes().iterator();
		while (it.hasNext()) {
			bna = it.next();
			if (!bna.hasRef()) {
				Point2D p = pw.getGraph().getVertexLocation(bna);
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
					places = places.concat(getPlaceString(place.getModellicaString(), bna, atr, in, out, p));

				} else if (biologicalElement.equals(Elementdeclerations.s_place)) {

					Place place = (Place) bna;
					String atr = "startMarks=" + place.getTokenStart() + ",minMarks=" + place.getTokenMin() + ",maxMarks=" + place.getTokenMax()
							+ ",t(final unit=\"mmol\")";
					places = places.concat(getPlaceString(place.getModellicaString(), bna, atr, in, out, p));

				} else if (biologicalElement.equals(Elementdeclerations.stochasticTransition)) {

					StochasticTransition t = (StochasticTransition) bna;
					// String atr = "h=" + t.getDistribution();
					String atr = "h=1.0";
					places = places.concat(getTransitionString(bna, t.getModellicaString(), bna.getName(), atr, in, out, p));

				} else if (biologicalElement.equals(Elementdeclerations.discreteTransition)) {

					DiscreteTransition t = (DiscreteTransition) bna;
					String atr = "delay=" + t.getDelay();
					places = places.concat(getTransitionString(bna, t.getModellicaString(), bna.getName(), atr, in, out, p));

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
					places = places.concat(getTransitionString(bna, t.getModellicaString(), bna.getName(), atr, in, out, p));
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
					edgesString = edgesString + "\tconnect('" + fromString + "'.outTransition[" + (actualOutEdges.get(fromString) + 1) + "],"
							+ "inhibitorArc" + inhibitCount + ".inPlace);" + "\r\n";
					edgesString = edgesString + "\tconnect(" + "inhibitorArc" + inhibitCount + ".outTransition,'" + toString + "'.inPlaces["
							+ (actualInEdges.get(toString) + 1) + "]);" + "\r\n";
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

			// BiologicalEdgeAbstract bna = (BiologicalEdgeAbstract) it.next();
			// if (debug)
			// System.out.println("getEdge "+bna.getEdge()+" "+(bna.isDirected()?"directed":"undirected"));
			//
			// //get name and position and type
			// String
			// fromString=bna.getEdge().getEndpoints().getFirst().toString();
			// String
			// toString=bna.getEdge().getEndpoints().getSecond().toString();
			// //Point2D fromPosition = nodePositions.get(fromString);
			// //Point2D toPosition = nodePositions.get(toString);
			// String fromType = nodeType.get(fromString);
			// String toType = nodeType.get(toString);
			// // boolean fromIsEnzyme = fromType.equals("Enzyme");
			// // boolean toIsEnzyme = toType.equals("Enzyme");
			//
			// if (numInEdges.containsKey(toString))
			// numInEdges.put(toString, numInEdges.get(toString)+1);
			// else {
			// numInEdges.put(toString, 1);
			// actualInEdges.put(toString, 0);
			// }
			// if (numOutEdges.containsKey(fromString))
			// numOutEdges.put(fromString, numOutEdges.get(fromString)+1);
			// else {
			// numOutEdges.put(fromString, 1);
			// actualOutEdges.put(fromString, 0);
			// }
			//
			//
			//
			//
			// // if (debug)
			// System.out.println("Typen: "+fromString+" "+fromType+"    "+toString+" "+toType);
			//
			// // if (fromIsEnzyme ^ toIsEnzyme) {
			// //add edges to counter
			// // if (numInEdges.containsKey(toString))
			// // numInEdges.put(toString, numInEdges.get(toString)+1);
			// // else {
			// // numInEdges.put(toString, 1);
			// // actualInEdges.put(toString, 0);
			// // }
			// // if (numOutEdges.containsKey(fromString))
			// // numOutEdges.put(fromString, numOutEdges.get(fromString)+1);
			// // else {
			// // numOutEdges.put(fromString, 1);
			// // actualOutEdges.put(fromString, 0);
			// // }
			// //
			// // //write
			// // if (fromIsEnzyme)
			// //// edges=edges.concat(getConnectionStringTP(fromString,
			// toString, fromPosition, toPosition));
			// // edges.add(new Edge(true, fromString, toString, fromPosition,
			// toPosition));
			// // else
			// //// edges=edges.concat(getConnectionStringPT(fromString,
			// toString, fromPosition, toPosition));
			// // edges.add(new Edge(false, fromString, toString, fromPosition,
			// toPosition));
			// //
			// // //backward direction
			// // if (!bna.isDirected()){
			// // numInEdges.put(fromString, numInEdges.get(fromString)+1);
			// // numOutEdges.put(toString, numOutEdges.get(toString)+1);
			// //
			// // //write backwards
			// // if (fromIsEnzyme)
			// //// edges=edges.concat(getConnectionStringPT(toString,
			// fromString, toPosition, fromPosition));
			// // edges.add(new Edge(false, toString, fromString, toPosition,
			// fromPosition));
			// // else
			// //// edges=edges.concat(getConnectionStringTP(toString,
			// fromString, toPosition, fromPosition));
			// // edges.add(new Edge(true, toString, fromString, toPosition,
			// fromPosition));
			// // }
			// // } else {
			// //
			// // //calculate positions of additional elements
			// // Point2D m1=(Point2D) fromPosition.clone(), m2=(Point2D)
			// toPosition.clone();
			// // double xM=(fromPosition.getX()+toPosition.getX())/2;
			// // double yM=(fromPosition.getY()+toPosition.getY())/2;
			// // double xDif=fromPosition.getX()-toPosition.getX();
			// // double yDif=fromPosition.getY()-toPosition.getY();
			// //
			// // m1.setLocation(xM+yDif/8, yM-xDif/8);
			// // if (!bna.isDirected()) m2.setLocation(xM-yDif/8, yM+xDif/8);
			// //
			// // //both are same: add place
			// // if (fromIsEnzyme == toIsEnzyme) {
			// // //add edges to counter
			// // if (numInEdges.containsKey(toString))
			// // numInEdges.put(toString, numInEdges.get(toString)+1);
			// // else {
			// // numInEdges.put(toString, 1);
			// // actualInEdges.put(toString, 0);
			// // }
			// // if (numOutEdges.containsKey(fromString))
			// // numOutEdges.put(fromString, numOutEdges.get(fromString)+1);
			// // else {
			// // numOutEdges.put(fromString, 1);
			// // actualOutEdges.put(fromString, 0);
			// // }
			// //
			// // if (fromIsEnzyme){ //write place
			// // ++addedNodes;
			// // bioName.put("A"+addedNodes, "added Place");
			// // places=places.concat(getPlaceString("sds","A"+addedNodes,"",
			// 1, 1, m1));
			// // }else{
			// // ++addedNodes;
			// // bioName.put("A"+addedNodes, "added Transition");
			// //
			// transitions=transitions.concat(getTransitionString("A"+addedNodes,
			// 1, 1, m1));
			// // }
			// // numInEdges.put("A"+addedNodes, 1);
			// // numOutEdges.put("A"+addedNodes, 1);
			// //
			// // //write edges
			// // if (fromIsEnzyme){
			// //// edges=edges.concat(getConnectionStringTP(fromString,
			// "A"+addedNodes, fromPosition, m1));
			// //// edges=edges.concat(getConnectionStringPT("A"+addedNodes,
			// toString, m1, toPosition));
			// // edges.add(new Edge(true, fromString, "A"+addedNodes,
			// fromPosition, m1));
			// // edges.add(new Edge(false, "A"+addedNodes, toString, m1,
			// toPosition));
			// // } else {
			// //// edges=edges.concat(getConnectionStringPT(fromString,
			// "A"+addedNodes, fromPosition, m1));
			// //// edges=edges.concat(getConnectionStringTP("A"+addedNodes,
			// toString, m1, toPosition));
			// // edges.add(new Edge(false, fromString, "A"+addedNodes,
			// fromPosition, m1));
			// // edges.add(new Edge(true, "A"+addedNodes, toString, m1,
			// toPosition));
			// // }
			// //
			// // if (!bna.isDirected()){ //create second place
			// // //add edges to counter
			// // if (numInEdges.containsKey(fromString))
			// // numInEdges.put(fromString, numInEdges.get(fromString)+1);
			// // else {
			// // numInEdges.put(fromString, 1);
			// // actualInEdges.put(fromString, 0);
			// // }
			// // if (numOutEdges.containsKey(toString))
			// // numOutEdges.put(toString, numOutEdges.get(toString)+1);
			// // else {
			// // numOutEdges.put(toString, 1);
			// // actualOutEdges.put(toString, 0);
			// // }
			// //
			// // if (fromIsEnzyme) { //write place
			// // ++addedNodes;
			// // bioName.put("A"+addedNodes, "added Place");
			// // places=places.concat(getPlaceString("dsda","A"+addedNodes,"",
			// 1, 1, m2));
			// // } else { //write transition
			// // ++addedNodes;
			// // bioName.put("A"+addedNodes, "added Transition");
			// //
			// transitions=transitions.concat(getTransitionString("A"+addedNodes,
			// 1, 1, m2));
			// // }
			// //
			// // //write edges
			// // if (fromIsEnzyme){
			// //// edges=edges.concat(getConnectionStringTP(toString,
			// "A"+addedNodes, toPosition, m2));
			// //// edges=edges.concat(getConnectionStringPT("A"+addedNodes,
			// fromString, m2, fromPosition));
			// // edges.add(new Edge(true, toString, "A"+addedNodes, toPosition,
			// m2));
			// // edges.add(new Edge(false, "A"+addedNodes, fromString, m2,
			// fromPosition));
			// // } else {
			// //// edges=edges.concat(getConnectionStringPT(toString,
			// "A"+addedNodes, toPosition, m2));
			// //// edges=edges.concat(getConnectionStringTP("A"+addedNodes,
			// fromString, m2, fromPosition));
			// // edges.add(new Edge(false, toString, "A"+addedNodes,
			// toPosition, m2));
			// // edges.add(new Edge(true, "A"+addedNodes, fromString, m2,
			// fromPosition));
			// // }
			// // }
			// // }
			// // }
			// }
			// //edges are first collected and made to strings here
			// //because while collecting the number of mayimal in/out is not
			// known
			// //so the positions are correct AFTER collecting all edges
			// for (int i=0; i<edges.size();++i){
			// Edge edge=edges.get(i);
			// //System.out.println(i);
			// if (edge.firstIsTransition)
			// edgesString=edgesString.concat(getConnectionStringTP(edge.from,
			// edge.to, edge.frompoint, edge.topoint));
			// else
			// edgesString=edgesString.concat(getConnectionStringPT(edge.from,
			// edge.to, edge.frompoint, edge.topoint));
			// //System.out.println(getConnectionStringTP(edge.from, edge.to,
			// edge.frompoint, edge.topoint));
		}
	}

	private String getPlaceString(String element, BiologicalNodeAbstract bna, String atr, int inEdges, int outEdges, Point2D p) {

		for (int i = 0; i < bna.getParameters().size(); i++) {
			// params+="\t\tparameter Real "+bna.getParameters().get(i).getName()+" = "+bna.getParameters().get(i).getValue()+";\r\n";
		}
		return "\t" + element + " '" +

		// falls die anzahlen nicht stimmen
		// +numInEdges.get(bna.getVertex().toString())
		// +numOutEdges.get(bna.getVertex().toString())
		//
				bna.getName() + "'(nIn=" + inEdges + ",nOut=" + outEdges + "," + atr + ")"
				// +(bioName.containsKey(name)?"(biologicalName = \""+bioName.get(name)+"\")":"")
				// +" annotation(Placement(transformation(x = "+Math.floor(scale*(p.getX()+xshift))+", y = "+Math.floor(scale*(-(p.getY()+yshift)))+", scale = 0.84), "
				// +"iconTransformation(x = "
				// +Math.floor(scale*(p.getX()+xshift))+", y = "+Math.floor(scale*(-(p.getY()+yshift)))+", scale = 0.84)))"
				+ ";\r\n";
	}

	private String getTransitionString(BiologicalNodeAbstract bna, String element, String name, String atr, int inEdges, int outEdges, Point2D p) {
		for (int i = 0; i < bna.getParameters().size(); i++) {
			// params+="\t\tparameter Real "+bna.getParameters().get(i).getName()+" = "+bna.getParameters().get(i).getValue()+";\r\n";
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

		return "\t" + element + " '" + bna.getName() + "'(nIn=" + inEdges + ",nOut=" + outEdges + "," + atr + ",arcWeightIn=" + in + ",arcWeightOut="
				+ out + ")" + ";\r\n";
	}

	private String getTransitionStringOld(String name, int inEdges, int outEdges, Point2D p) {
		return "  PetriNetsBioChem.Continuous.TC"

		+ inEdges + outEdges + " " + name + (bioName.containsKey(name) ? "(biologicalName = \"" + bioName.get(name) + "\")" : "")
				+ " annotation(Placement(transformation(x = " + Math.floor(scale * (p.getX() + xshift)) + ", y = "
				+ Math.floor(scale * (-(p.getY() + yshift))) + ", scale = 0.84), " + "iconTransformation(x = "
				+ Math.floor(scale * (p.getX() + xshift)) + ", y = " + Math.floor(scale * (-(p.getY() + yshift))) + ", scale = 0.84)))" + ";\r\n";
	}

	private String getMmString(String name, Point2D p, double km, double kcat, String ec) {
		return "  PetriNetsBioChem.Continuous.Re_MM_11 " + name + "(" + (Double.isNaN(kcat) ? "" : "kcat = " + kcat + ", ")
				+ (Double.isNaN(km) ? "" : "Km = " + km + ", ")
				+ (ec.length() > 0 ? "ec_number = \"" + ec + "\", " : "")
				+ (bioName.containsKey(name) ? "biologicalName = \"" + bioName.get(name) + "\"" : "")
				+ ") "
				// kann man unter annotation-bedingung stellen
				+ " annotation(Placement(transformation(x = " + Math.floor(scale * (p.getX() + xshift)) + ", y = "
				+ Math.floor(scale * (-(p.getY() + yshift))) + ", scale = 0.84, aspectRatio = 1.6), " + "iconTransformation(x = "
				+ Math.floor(scale * (p.getX() + xshift)) + ", y = " + Math.floor(scale * (-(p.getY() + yshift)))
				+ ", scale = 0.84, aspectRatio = 1.6)))"
				// /
				+ ";\r\n";
	}

	private String getConnectionStringTP(String from, String to, BiologicalEdgeAbstract bea) {
		// String from = bea.getFrom().getName();
		// String to = bea.getTo().getName();
		String result = "\tconnect('" + from + "'.outPlaces[" + (actualOutEdges.get(from) + 1) + "],'" + to + "'.inTransition["
				+ (actualInEdges.get(to) + 1) + "]);"
				// +" annotation(Line(points = {{"
				// +Math.floor(scale*(fromPoint.getX()+xshift)+10)+","
				// +Math.floor(scale*(-(fromPoint.getY()+yshift))+((numOutEdges.get(from)-1)*pinabstand/2-(actualOutEdges.get(from))*pinabstand))+"},{"
				// +Math.floor(scale*(toPoint. getX()+xshift)-19)+","
				// +Math.floor(scale*(-(toPoint.
				// getY()+yshift))+((numInEdges.get(to)-1)*pinabstand/2-(actualInEdges.get(to))*pinabstand))+"}}));"
				+ "\r\n";
		// System.out.println(to+".tSumIn_["+(actualInEdges.get(to) + 1)+"]");
		this.bea2resultkey.put(bea, "'" + to + "'.tokenFlow.inflow[" + (actualInEdges.get(to) + 1) + "]");

		actualInEdges.put(to, actualInEdges.get(to) + 1);
		actualOutEdges.put(from, actualOutEdges.get(from) + 1);
		return result;
	}

	private String getConnectionStringPT(String from, String to, BiologicalEdgeAbstract bea) {
		// String from = bea.getFrom().getName();
		// String to = bea.getTo().getName();
		String result = "\tconnect('" + from + "'.outTransition[" + (actualOutEdges.get(from) + 1) + "],'" + to + "'.inPlaces["
				+ (actualInEdges.get(to) + 1) + "]);"
				// +" annotation(Line(points = {{"
				// +Math.floor(scale*(fromPoint.getX()+xshift)+19)+","
				// +Math.floor(scale*(-(fromPoint.getY()+yshift))+((numOutEdges.get(from)-1)*pinabstand/2-(actualOutEdges.get(from))*pinabstand))+"},{"
				// +Math.floor(scale*(toPoint. getX()+xshift)-10)+","
				// +Math.floor(scale*(-(toPoint.
				// getY()+yshift))+((numInEdges.get(to)-1)*pinabstand/2-(actualInEdges.get(to))*pinabstand))+"}}));"
				+ "\r\n";
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
}
