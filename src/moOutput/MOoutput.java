package moOutput;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import petriNet.ContinuousTransition;
import petriNet.DiscreteTransition;
import petriNet.PNEdge;
import petriNet.Place;
import petriNet.StochasticTransition;
import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;

/**
 * @author Rafael, cbrinkro
 */
public class MOoutput {

	private static final boolean debug = !false;

	private File file = null;
	private String modelName = null;
	private Pathway pw = null;
	private FileWriter fwriter;
	@SuppressWarnings("unchecked")
	Hashtable speciesTypeID = new Hashtable();
	@SuppressWarnings("unchecked")
	Hashtable compartments = new Hashtable();

	private String places = "";
	private String transitions = "";
	private String properties = "";
	private String edgesString = "";
	private double xshift = 0, yshift = 0;
	private double xmin = 1000, xmax = -1000, ymin = 1000, ymax = -1000;
	private final double scale = 2;
	private final int pinabstand = 4;
	private final int addedNodes = 0;
	private final Hashtable<String, Integer> numInEdges = new Hashtable<String, Integer>();
	private final Hashtable<String, Integer> numOutEdges = new Hashtable<String, Integer>();
	private final Hashtable<String, Integer> actualInEdges = new Hashtable<String, Integer>();
	private final Hashtable<String, Integer> actualOutEdges = new Hashtable<String, Integer>();
	private final Hashtable<String, Point2D> nodePositions = new Hashtable<String, Point2D>();
	private final Hashtable<String, String> nodeType = new Hashtable<String, String>();
	private final Hashtable<String, String> bioName = new Hashtable<String, String>();
	private final Hashtable<String, Object> bioObject = new Hashtable<String, Object>();
	// private HashMap<String, Double> edgeToWeight = new HashMap<String,
	private final HashMap <String, String> vertex2name = new HashMap<String, String>();
	// Double>();
	private HashMap<String, String> inWeights = new HashMap<String, String>();
	private HashMap<String, String> outWeights = new HashMap<String, String>();

	private final Vector<Edge> edges = new Vector<Edge>();

	public MOoutput(File file, Pathway pathway) {

		if (debug)
			System.out.println();
		if (debug)
			System.out.println("MOoutput(File " + file + " Pathway " + pathway
					+ ")");

		this.file = file;
		this.modelName = file.getName().substring(0,
				file.getName().lastIndexOf("."));
		if (debug)
			System.out.println("Model Name = " + modelName);
		this.pw = pathway;

		try {
			write();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void write() throws IOException {

		fwriter = new FileWriter(file);

		prepare();
		// buildProperties();

		buildConnections();
		buildNodes();

		// if (debug)
		// System.out.println(properties+places+transitions+edgesString);

		fwriter.write("model " + modelName + "\r\n");
		// fwriter.write(properties);
		fwriter.write(places);
		// fwriter.write(transitions);
		fwriter.write("\requation\r\n");
		fwriter.write(edgesString);
		fwriter.write("end " + modelName + ";");
		fwriter.close();

	}

	@SuppressWarnings("unchecked")
	private void prepare() {
		Iterator it = pw.getAllNodes().iterator();
		while (it.hasNext()) {
			BiologicalNodeAbstract bna = (BiologicalNodeAbstract) it.next();
			Point2D p = pw.getGraph().getClusteringLayout().getLocation(
					bna.getVertex());
			String biologicalElement = bna.getBiologicalElement();
			String name ="";
			if (biologicalElement.equals(biologicalElements.Elementdeclerations.place) || biologicalElement.equals(biologicalElements.Elementdeclerations.s_place))
			name = "P"+bna.getID();
			else name="T"+ bna.getID();
				
			this.vertex2name.put(bna.getVertex().toString(),name);
			nodePositions.put(name, p);
			nodeType.put(name, biologicalElement);
			bioName.put(name, bna.getLabel());
			bioObject.put(name, bna);

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

	private void buildProperties() {
		xshift = -(xmin + xmax) / 2;
		yshift = -(ymin + ymax) / 2;

		if (debug)
			System.out.println("shift=" + xshift + " " + yshift);

		properties = properties
				.concat("  annotation(Diagram(coordinateSystem(extent = {"
						+ "{" + (int) Math.floor(scale * (xmin + xshift - 20))
						+ "," + (int) Math.floor(scale * (ymin + yshift - 20))
						+ "},{"
						+ (int) Math.floor(scale * (xmax + xshift + 20)) + ","
						+ (int) Math.floor(scale * (ymax + yshift + 20)) + "}"
						+ "})));\r\n");
	}

	private String getWeightedEdges() {

		String weightedEdges = "";

		return weightedEdges;
	}

	@SuppressWarnings("unchecked")
	private void buildNodes() {
		for (int i=1; i<=inhibitCount; i++) places+="PNlib.IA inhibitorArc"+i+";\r\n";		
		Iterator it = pw.getAllNodes().iterator();
		while (it.hasNext()) {
			BiologicalNodeAbstract bna = (BiologicalNodeAbstract) it.next();
			Point2D p = pw.getGraph().getClusteringLayout().getLocation(
					bna.getVertex());
			String biologicalElement = bna.getBiologicalElement();
			double km = Double.NaN, kcat = Double.NaN;
			String ec = "";

			int in = bna.getVertex().getInEdges().size();
			int out = bna.getVertex().getOutEdges().size();
			if (biologicalElement.equals("Enzyme"))
				if (in == 1 && out == 1) // falls ein ein- und aus-gang: MM
					transitions = transitions.concat(getMmString("T"+bna.getID(), p, km, kcat, ec));
				else
					// sonst normale transition
					transitions = transitions.concat(getTransitionStringOld("T"+bna.getID(), in, out, p));
			else if (biologicalElement.equals(Elementdeclerations.place)) {

				Place place = (Place) bna;

				String atr = "startTokens=" + (int)place.getTokenStart()
						+ ",minTokens=" + (int)place.getTokenMin() + ",maxTokens="
						+ (int)place.getTokenMax();
				places = places.concat(getPlaceString(place
						.getModellicaString(), "P"+bna.getID(), atr,
						in, out, p));

			} else if (biologicalElement.equals(Elementdeclerations.s_place)) {

				Place place = (Place) bna;
				String atr = "startMarks=" + place.getTokenStart()
						+ ",minMarks=" + place.getTokenMin() + ",maxMarks="
						+ place.getTokenMax();
				places = places.concat(getPlaceString(place
						.getModellicaString(), "P"+bna.getID(), atr,
						in, out, p));

			} else if (biologicalElement
					.equals(Elementdeclerations.stochasticTransition)) {

				StochasticTransition t = (StochasticTransition) bna;
				//String atr = "h=" + t.getDistribution();
				String atr = "h=1.0";
				places = places.concat(getTransitionString(bna, t
						.getModellicaString(), "T"+bna.getID(), atr,
						in, out, p));

			} else if (biologicalElement
					.equals(Elementdeclerations.discreteTransition)) {

				DiscreteTransition t = (DiscreteTransition) bna;
				String atr = "delay=" + t.getDelay();
				places = places.concat(getTransitionString(bna, t
						.getModellicaString(), "T"+bna.getID(), atr,
						in, out, p));

			} else if (biologicalElement
					.equals(Elementdeclerations.contoniousTransition)) {

				ContinuousTransition t = (ContinuousTransition) bna;
				String atr = "maximumSpeed=1.0";
				places = places.concat(getTransitionString(bna, t
						.getModellicaString(), "T"+bna.getID(), atr,
						in, out, p));
			}
		}
	}

	private int inhibitCount=0;
	
	@SuppressWarnings("unchecked")
	private void buildConnections() {

		Iterator it = pw.getAllEdges().iterator();

		while (it.hasNext()) {

			BiologicalEdgeAbstract bna = (BiologicalEdgeAbstract) it.next();
			String fromString = vertex2name.get(bna.getEdge().getEndpoints().getFirst()
					.toString());
			String toString = vertex2name.get(bna.getEdge().getEndpoints().getSecond()
					.toString());
			String fromType = nodeType.get(fromString);
			String toType = nodeType.get(toString);

			//TODO funktionen werden zulassen
			if (bna instanceof PNEdge) {
				PNEdge e = (PNEdge) bna;
				if (this.inWeights.containsKey(toString)) {
					this.inWeights.put(toString, this.inWeights.get(toString)
							.concat("," + e.getModellicaFunction()));
				} else {
					this.inWeights.put(toString, e.getModellicaFunction() + "");
				}
				if (this.outWeights.containsKey(fromString)) {
					this.outWeights.put(fromString, this.outWeights.get(
							fromString).concat("," + e.getModellicaFunction()));
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
if (bna instanceof PNEdge && bna.getBiologicalElement().equals(biologicalElements.Elementdeclerations.pnInhibitionEdge) ){
	inhibitCount++;
	if (fromType.equals(Elementdeclerations.s_place) || fromType.equals(Elementdeclerations.place)) {
		edgesString = edgesString+"  connect(" + fromString+ ".outTransition["+ (actualOutEdges.get(fromString) + 1) + "]," 
		+"inhibitorArc"+inhibitCount+".inPlace);"+ "\r\n";		
		edgesString = edgesString+"  connect(" +"inhibitorArc"+inhibitCount+ ".outTransition,"
		+ toString + ".inPlaces["+ (actualInEdges.get(toString) + 1) + "]);"+"\r\n";
		actualOutEdges.put(fromString, actualOutEdges.get(fromString) + 1);
		actualInEdges.put(toString, actualInEdges.get(toString) + 1);	
	}

}
else if (fromType.equals(Elementdeclerations.s_place)) {
				edgesString = edgesString.concat(getConnectionStringPT(
						fromString, toString));
			} else if (fromType.equals(Elementdeclerations.place)) {
				edgesString = edgesString.concat(getConnectionStringPT(
						fromString, toString));
			} else {
				edgesString = edgesString.concat(getConnectionStringTP(
						fromString, toString));
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

	private String getPlaceString(String element, String name, String atr,
			int inEdges, int outEdges, Point2D p) {
		return ""

				// falls die anzahlen nicht stimmen
				// +numInEdges.get(bna.getVertex().toString())
				// +numOutEdges.get(bna.getVertex().toString())
				//
				+ element + " " + name + "(nIn=" + inEdges + ",nOut="
				+ outEdges + "," + atr + ")"
				// +(bioName.containsKey(name)?"(biologicalName = \""+bioName.get(name)+"\")":"")
				// +" annotation(Placement(transformation(x = "+Math.floor(scale*(p.getX()+xshift))+", y = "+Math.floor(scale*(-(p.getY()+yshift)))+", scale = 0.84), "
				// +"iconTransformation(x = "
				// +Math.floor(scale*(p.getX()+xshift))+", y = "+Math.floor(scale*(-(p.getY()+yshift)))+", scale = 0.84)))"
				+ ";\r\n";
	}

	private String getTransitionString(BiologicalNodeAbstract bna,
			String element, String name, String atr, int inEdges, int outEdges,
			Point2D p) {
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
		//System.out.println("name: "+bna.getName());
		if(in.length() == 2){
			in = "fill(1,T"+bna.getID()+".nIn)";
		}
		if(out.length() == 2){
			out = "fill(1,T"+bna.getID()+".nOut)";
		}
		//System.out.println("inPropper: " + in);
		//System.out.println("outPropper: " + out);

		return "" + element + " " + name + "(nIn=" + inEdges + ",nOut="
				+ outEdges + "," + atr + ",arcWeightIn=" + in
				+ ",arcWeightOut=" + out + ")" + ";\r\n";
	}

	private String getTransitionStringOld(String name, int inEdges,
			int outEdges, Point2D p) {
		return "  PetriNetsBioChem.Continuous.TC"

				+ inEdges
				+ outEdges
				+ " "
				+ name
				+ (bioName.containsKey(name) ? "(biologicalName = \""
						+ bioName.get(name) + "\")" : "")
				+ " annotation(Placement(transformation(x = "
				+ Math.floor(scale * (p.getX() + xshift)) + ", y = "
				+ Math.floor(scale * (-(p.getY() + yshift)))
				+ ", scale = 0.84), " + "iconTransformation(x = "
				+ Math.floor(scale * (p.getX() + xshift)) + ", y = "
				+ Math.floor(scale * (-(p.getY() + yshift)))
				+ ", scale = 0.84)))" + ";\r\n";
	}

	private String getMmString(String name, Point2D p, double km, double kcat,
			String ec) {
		return "  PetriNetsBioChem.Continuous.Re_MM_11 "
				+ name
				+ "("
				+ (Double.isNaN(kcat) ? "" : "kcat = " + kcat + ", ")
				+ (Double.isNaN(km) ? "" : "Km = " + km + ", ")
				+ (ec.length() > 0 ? "ec_number = \"" + ec + "\", " : "")
				+ (bioName.containsKey(name) ? "biologicalName = \""
						+ bioName.get(name) + "\"" : "")
				+ ") "
				// kann man unter annotation-bedingung stellen
				+ " annotation(Placement(transformation(x = "
				+ Math.floor(scale * (p.getX() + xshift)) + ", y = "
				+ Math.floor(scale * (-(p.getY() + yshift)))
				+ ", scale = 0.84, aspectRatio = 1.6), "
				+ "iconTransformation(x = "
				+ Math.floor(scale * (p.getX() + xshift)) + ", y = "
				+ Math.floor(scale * (-(p.getY() + yshift)))
				+ ", scale = 0.84, aspectRatio = 1.6)))"
				// /
				+ ";\r\n";
	}

	private String getConnectionStringTP(String from, String to) {
		String result = "  connect(" + from + ".outPlaces["
				+ (actualOutEdges.get(from) + 1) + "]," + to + ".inTransition["
				+ (actualInEdges.get(to) + 1) + "]);"
				// +" annotation(Line(points = {{"
				// +Math.floor(scale*(fromPoint.getX()+xshift)+10)+","
				// +Math.floor(scale*(-(fromPoint.getY()+yshift))+((numOutEdges.get(from)-1)*pinabstand/2-(actualOutEdges.get(from))*pinabstand))+"},{"
				// +Math.floor(scale*(toPoint. getX()+xshift)-19)+","
				// +Math.floor(scale*(-(toPoint.
				// getY()+yshift))+((numInEdges.get(to)-1)*pinabstand/2-(actualInEdges.get(to))*pinabstand))+"}}));"
				+ "\r\n";
		actualInEdges.put(to, actualInEdges.get(to) + 1);
		actualOutEdges.put(from, actualOutEdges.get(from) + 1);
		return result;
	}

	private String getConnectionStringPT(String from, String to) {
		String result = "  connect(" + from + ".outTransition["
				+ (actualOutEdges.get(from) + 1) + "]," + to + ".inPlaces["
				+ (actualInEdges.get(to) + 1) + "]);"
				// +" annotation(Line(points = {{"
				// +Math.floor(scale*(fromPoint.getX()+xshift)+19)+","
				// +Math.floor(scale*(-(fromPoint.getY()+yshift))+((numOutEdges.get(from)-1)*pinabstand/2-(actualOutEdges.get(from))*pinabstand))+"},{"
				// +Math.floor(scale*(toPoint. getX()+xshift)-10)+","
				// +Math.floor(scale*(-(toPoint.
				// getY()+yshift))+((numInEdges.get(to)-1)*pinabstand/2-(actualInEdges.get(to))*pinabstand))+"}}));"
				+ "\r\n";
		actualInEdges.put(to, actualInEdges.get(to) + 1);
		actualOutEdges.put(from, actualOutEdges.get(from) + 1);
		return result;
	}
}
