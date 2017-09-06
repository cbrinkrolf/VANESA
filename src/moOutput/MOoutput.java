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

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.edges.petriNet.PNEdge;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.petriNet.ContinuousTransition;
import biologicalObjects.nodes.petriNet.DiscreteTransition;
import biologicalObjects.nodes.petriNet.PNNode;
import biologicalObjects.nodes.petriNet.Place;
import biologicalObjects.nodes.petriNet.StochasticTransition;
import biologicalObjects.nodes.petriNet.Transition;

/**
 * @author Rafael, cbrinkro
 */
public class MOoutput {

	private static final boolean debug = MainWindow.developer;
	private static final String INDENT = "  ";
	private static final String ENDL = System.getProperty("line.separator");

	private OutputStream os = null;
	private String modelName = null;
	private Pathway pw = null;

	private String places = "";
	private String edgesString = "";
	// string for inhibitory arcs
	private String IAString = "";
	// string for test arcs
	private String TAString = "";

	private final Hashtable<String, ArrayList<BiologicalNodeAbstract>> actualInEdges = new Hashtable<String, ArrayList<BiologicalNodeAbstract>>();
	private final Hashtable<String, ArrayList<BiologicalNodeAbstract>> actualOutEdges = new Hashtable<String, ArrayList<BiologicalNodeAbstract>>();
	private final Hashtable<BiologicalNodeAbstract, String> nodeType = new Hashtable<BiologicalNodeAbstract, String>();
	private final HashMap<BiologicalNodeAbstract, String> vertex2name = new HashMap<BiologicalNodeAbstract, String>();
	private HashMap<String, String> inWeights = new HashMap<String, String>();
	private HashMap<String, String> outWeights = new HashMap<String, String>();

	private HashMap<BiologicalEdgeAbstract, String> bea2resultkey = new HashMap<BiologicalEdgeAbstract, String>();

	private Set<BiologicalNodeAbstract> marked;

	private String packageInfo = null;

	private boolean colored = false;

	private boolean noIdent = false;

	double minX = Double.MAX_VALUE;
	double minY = Double.MAX_VALUE;
	double maxX = -Double.MAX_VALUE;
	double maxY = -Double.MAX_VALUE;

	private Set<Character> chars = new HashSet<Character>();

	public MOoutput(OutputStream os, Pathway pathway, boolean colored) {
		this(os, pathway, null, colored);
	}

	public MOoutput(OutputStream os, Pathway pathway, String packageInfo, boolean colored) {
		this.init();
		this.packageInfo = packageInfo;
		this.colored = colored;
		if (debug) {
			System.out.println("MOoutput(File " + pathway.getName() + " Pathway " + pathway + ")");
		}
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

	private void init() {
		marked = GraphInstance.getMyGraph().getVisualizationViewer().getPickedVertexState().getPicked();
		System.out.println(marked.size());
		chars.add('*');
		chars.add('+');
		chars.add('/');
		chars.add('-');
		chars.add('^');
		chars.add('(');
		chars.add(')');
		chars.add(' ');
	}

	private void write() throws IOException {

		prepare();

		buildConnections();
		buildNodes();

		// if (debug)
		// System.out.println(properties+places+transitions+edgesString);
		StringBuilder sb = new StringBuilder();
		sb.append("model '" + modelName + "'" + ENDL);
		if (colored) {
			sb.append(this.getgFunctions());
		}
		// os.write(new String("model '" + modelName + "'"'" +
		// this.endl").getBytes());

		if (packageInfo != null) {
			// os.write(new String(this.indentation + this.packageInfo +
			// this.endl).getBytes());
			sb.append(INDENT + this.packageInfo + ENDL);
			// os.write(new
			// String(this.indentation + "import PNlib = ConPNlib;" +
			// this.endl).getBytes());
		}

		// if (this.packageInfo == null) {
		// sb.append(this.indentation + "inner PNlib.Settings settings1();" +
		// this.endl);
		sb.append(INDENT + "inner PNlib.Settings settings(showTokenFlow = true) annotation(Placement(visible=true, transformation(origin={"
				+ (minX - 30) + "," + (maxY + 30) + "}, extent={{-20,-20}, {20,20}}, rotation=0)));" + ENDL);
		// }

		sb.append(places);
		sb.append("equation" + ENDL);
		sb.append(edgesString);
		sb.append(INDENT + "annotation(Icon(coordinateSystem(extent={{" + (minX - 50) + "," + (minY - 50) + "},{" + (maxX + 50) + "," + (maxY + 50)
				+ "}})), Diagram(coordinateSystem(extent={{" + (minX - 50) + "," + (minY - 50) + "},{" + (maxX + 50) + "," + (maxY + 50) + "}})));"
				+ ENDL);
		sb.append("end '" + modelName + "';" + ENDL);

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
		Iterator<BiologicalNodeAbstract> it = pw.getAllGraphNodesSortedAlphabetically().iterator();
		BiologicalNodeAbstract bna;
		while (it.hasNext()) {
			bna = it.next();
			if (!bna.hasRef()) {
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
			}
		}
	}

	private void buildNodes() {

		places += IAString;
		places += TAString;

		// for (int i = 1; i <= inhibitCount; i++)

		BiologicalNodeAbstract bna;
		ArrayList<String> names = new ArrayList<String>();
		Iterator<BiologicalNodeAbstract> it = pw.getAllGraphNodesSortedAlphabetically().iterator();
		while (it.hasNext()) {
			// System.out.println("knoten");
			bna = it.next();
			if (!bna.hasRef()) {
				for (int i = 0; i < bna.getParameters().size(); i++) {
					// params += this.indentation + "parameter Real
					// "+bna.getParameters().get(i).getName()+" =
					// "+bna.getParameters().get(i).getValue()+";" + this.endl;
					places = places.concat(INDENT + "parameter Real '_" + bna.getName() + "_" + bna.getParameters().get(i).getName() + "'");
					if (bna.getParameters().get(i).getUnit().length() > 0) {
						places = places.concat("(final unit=\"" + bna.getParameters().get(i).getUnit() + "\")");
					}
					places = places.concat(" = " + bna.getParameters().get(i).getValue() + ";" + ENDL);
					// System.out.println("drin");
				}
			}
		}

		it = pw.getAllGraphNodesSortedAlphabetically().iterator();
		while (it.hasNext()) {
			bna = it.next();
			if (!bna.hasRef() && bna instanceof PNNode) {
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
				String atr = "";
				if (biologicalElement.equals(Elementdeclerations.place)) {

					Place place = (Place) bna;

					atr = "startTokens=" + (int) place.getTokenStart() + ",minTokens=" + (int) place.getTokenMin() + ",maxTokens="
							+ (int) place.getTokenMax();
					// places =
					// places.concat(getPlaceString(getModelicaString(place),
					// bna, atr, in, out));

				} else if (biologicalElement.equals(Elementdeclerations.s_place)) {

					Place place = (Place) bna;
					String start = "";
					String min = "";
					String max = "";

					if (colored) {
						start = "{0.0, " + place.getTokenStart() + "}";
						min = "{" + place.getTokenMin() + ", " + place.getTokenMin() + "}";
						max = "{" + place.getTokenMax() + ", " + place.getTokenMax() + "}";
					} else {
						start = place.getTokenStart() + "";
						min = place.getTokenMin() + "";
						max = place.getTokenMax() + "";
					}

					atr = "startMarks=" + start + ",minMarks=" + min + ",maxMarks=" + max + ",t(final unit=\"mmol\")";
					// places =
					// places.concat(getPlaceString(getModelicaString(place),
					// bna, atr, in, out));

				} else if (biologicalElement.equals(Elementdeclerations.stochasticTransition)) {

					StochasticTransition t = (StochasticTransition) bna;
					// String atr = "h=" + t.getDistribution();
					atr = "h=1.0";
					// places = places.concat(getTransitionString(bna,
					// getModelicaString(t), bna.getName(), atr, in, out));

				} else if (biologicalElement.equals(Elementdeclerations.discreteTransition)) {

					DiscreteTransition t = (DiscreteTransition) bna;
					atr = "delay=" + t.getDelay();
					// places = places.concat(getTransitionString(bna,
					// getModelicaString(t), bna.getName(), atr, in, out));

				} else if (biologicalElement.equals(Elementdeclerations.continuousTransition)) {

					ContinuousTransition t = (ContinuousTransition) bna;
					// String atr = "maximumSpeed="+t.getMaximumSpeed();
					String speed = this.replaceAll(t.getMaximumSpeed(), t.getParameters(), t);
					if (t.isKnockedOut()) {
						atr = "maximumSpeed(final unit=\"mmol/min\")=0/*" + speed + "*/";
					} else {
						atr = "maximumSpeed(final unit=\"mmol/min\")=" + speed;
					}

					// System.out.println("atr");
					// places = places.concat(getTransitionString(bna,
					// getModelicaString(t), bna.getName(), atr, in, out));
				}

				if (bna instanceof Place) {

				}

				if (bna instanceof Transition) {
					Transition t = (Transition) bna;
					if (t.getFiringCondition().length() > 0) {
						atr += ", firingCon=" + t.getFiringCondition();
					}
				}
				places = places.concat(getTransitionString(bna, getModelicaString(bna), bna.getName(), atr, in, out));
			}
		}
	}

	private void buildConnections() {

		Iterator<BiologicalEdgeAbstract> it = pw.getAllEdgesSorted().iterator();

		BiologicalEdgeAbstract bea;
		PNEdge e;
		Set<BiologicalNodeAbstract> markedOut;

		int inhibitCount = 0;
		int testArcCount = 0;

		while (it.hasNext()) {

			bea = it.next();
			String fromString;
			String toString;
			// String fromType;

			if (bea.getFrom().hasRef()) {
				fromString = vertex2name.get(bea.getFrom().getRef());
				// fromType = nodeType.get(bea.getFrom().getRef());
			} else {
				fromString = vertex2name.get(bea.getFrom());
				// fromType = nodeType.get(bea.getFrom());
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
			String weight;
			if (bea instanceof PNEdge) {
				e = (PNEdge) bea;
				// System.out.println("edge");
				// Edge Place -> Transition
				if (e.getFrom() instanceof Place) {

					weight = "";
					if (colored) {
						if (marked.contains(e.getFrom())) {
							weight += "g1('" + resolveReference(e.getFrom()).getName() + "'.color)";
						} else {
							weight += "{0," + this.getModelicaEdgeFunction(e) + "}/*" + fromString + "*/";
						}

					} else {
						weight += this.getModelicaEdgeFunction(e);
					}

					if (this.inWeights.containsKey(toString)) {
						this.inWeights.put(toString, inWeights.get(toString) + "," + weight);
					} else {
						this.inWeights.put(toString, weight);
					}

					// Edge Transition -> Place
				} else {
					// System.out.println("kante");
					weight = "";
					if (colored) {
						markedOut = this.getMarkedNeighborsIn(e.getFrom());
						String tmp = "g";
						if (markedOut.size() == 0) {
							tmp = "{0," + this.getModelicaEdgeFunction(e) + "}/*" + toString + "*/";
						} else {
							String nodes = "";
							Iterator<BiologicalNodeAbstract> itBNA = markedOut.iterator();
							BiologicalNodeAbstract node;
							while (itBNA.hasNext()) {
								node = itBNA.next();
								nodes += "'" + resolveReference(node).getName() + "'.color,";
							}
							// ={g2('DHAP'.color, 'GAP'.color)}
							tmp = "g" + markedOut.size() + "(" + nodes.substring(0, nodes.length() - 1) + ")";
							// System.out.println(tmp);
						}

						if (marked.contains(e.getTo())) {
							weight = tmp;
						} else {
							weight = "{0," + this.getModelicaEdgeFunction(e) + "}/*" + fromString + "*/";
						}
						// System.out.println("weight:"+weight);
					} else {
						weight += this.getModelicaEdgeFunction(e) + "";
					}

					if (this.outWeights.containsKey(fromString)) {
						this.outWeights.put(fromString, outWeights.get(fromString) + "," + weight);
					} else {
						this.outWeights.put(fromString, weight);
					}
				}

				if (!actualInEdges.containsKey(toString)) {
					actualInEdges.put(toString, new ArrayList<BiologicalNodeAbstract>());
				}
				actualInEdges.get(toString).add(e.getFrom());

				if (!actualOutEdges.containsKey(fromString)) {
					actualOutEdges.put(fromString, new ArrayList<BiologicalNodeAbstract>());
				}

				actualOutEdges.get(fromString).add(e.getTo());

				if (e.getBiologicalElement().equals(biologicalElements.Elementdeclerations.pnInhibitionEdge)) {
					if (e.getFrom() instanceof Place) {
						inhibitCount++;
						IAString += INDENT + "PNlib.IA inhibitorArc" + inhibitCount + "(testValue=" + this.getModelicaEdgeFunction(e) + ");" + ENDL;
						edgesString += INDENT + "connect('" + fromString + "'.outTransition["
								+ (actualOutEdges.get(fromString).indexOf(e.getTo()) + 1) + "]," + "inhibitorArc" + inhibitCount + ".inPlace);"
								+ ENDL;
						edgesString += INDENT + "connect(" + "inhibitorArc" + inhibitCount + ".outTransition,'" + toString + "'.inPlaces["
								+ (actualInEdges.get(toString).indexOf(e.getFrom()) + 1) + "]) " + this.getFromToAnnotation(e.getFrom(), e.getTo())
								+ ";" + ENDL;
					}
				} else if (e.getBiologicalElement().equals(biologicalElements.Elementdeclerations.pnTestEdge)) {
					if (e.getFrom() instanceof Place) {
						testArcCount++;
						TAString += INDENT + "PNlib.TA testArc" + testArcCount + "(testValue=" + this.getModelicaEdgeFunction(e) + ");" + ENDL;
						edgesString += INDENT + "connect('" + fromString + "'.outTransition["
								+ (actualOutEdges.get(fromString).indexOf(e.getTo()) + 1) + "]," + "testArc" + testArcCount + ".inPlace);" + ENDL;
						edgesString += INDENT + "connect(" + "testArc" + testArcCount + ".outTransition,'" + toString + "'.inPlaces["
								+ (actualInEdges.get(toString).indexOf(e.getFrom()) + 1) + "]) " + this.getFromToAnnotation(e.getFrom(), e.getTo())
								+ ";" + ENDL;
					}
				} else if (e.getFrom() instanceof Place) {
					edgesString = edgesString.concat(getConnectionStringPT(fromString, toString, e));
				} else {
					edgesString = edgesString.concat(getConnectionStringTP(fromString, toString, e));
				}
			}
		}
	}

	private String getTransitionString(BiologicalNodeAbstract bna, String element, String name, String atr, int inEdges, int outEdges) {
		for (int i = 0; i < bna.getParameters().size(); i++) {
			// params += this.indentation + "parameter Real
			// "+bna.getParameters().get(i).getName()+" =
			// "+bna.getParameters().get(i).getValue()+";" + this.endl;
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

		in = ",arcWeightIn={" + inNumbers + "}";
		out = ",arcWeightOut={" + outNumbers + "}";
		// System.out.println("name: "+bna.getName());
		if (inNumbers.length() == 0) {
			in = "";
		}
		if (outNumbers.length() == 0) {
			out = "";
		}
		// System.out.println("inPropper: " + in);
		// System.out.println("outPropper: " + out);

		return INDENT + element + " '" + bna.getName() + "'(nIn=" + inEdges + ",nOut=" + outEdges + "," + atr + in + out + ") "
				+ getPlacementAnnotation(bna) + ";" + ENDL;
	}

	private String getConnectionStringTP(String from, String to, BiologicalEdgeAbstract bea) {
		// String from = bea.getFrom().getName();
		// String to = bea.getTo().getName();
		String result = INDENT + "connect('" + from + "'.outPlaces[" + (actualOutEdges.get(from).indexOf(bea.getTo()) + 1) + "],'" + to
				+ "'.inTransition[" + (actualInEdges.get(to).indexOf(bea.getFrom()) + 1) + "]) "
				+ this.getFromToAnnotation(bea.getFrom(), bea.getTo()) + ";"
				// +" annotation(Line(points = {{"
				// +Math.floor(scale*(fromPoint.getX()+xshift)+10)+","
				// +Math.floor(scale*(-(fromPoint.getY()+yshift))+((numOutEdges.get(from)-1)*pinabstand/2-(actualOutEdges.get(from))*pinabstand))+"},{"
				// +Math.floor(scale*(toPoint. getX()+xshift)-19)+","
				// +Math.floor(scale*(-(toPoint.
				// getY()+yshift))+((numInEdges.get(to)-1)*pinabstand/2-(actualInEdges.get(to))*pinabstand))+"}}));"
				+ ENDL;
		// System.out.println(to+".tSumIn_["+(actualInEdges.get(to) + 1)+"]");
		this.bea2resultkey.put(bea, "'" + to + "'.tokenFlow.inflow[" + (actualInEdges.get(to).indexOf(bea.getFrom()) + 1) + "]");

		// actualInEdges.get(to).add(bea.getTo());
		// actualOutEdges.get(from).add(bea.getFrom());
		return result;
	}

	private String getConnectionStringPT(String from, String to, BiologicalEdgeAbstract bea) {
		// String from = bea.getFrom().getName();
		// String to = bea.getTo().getName();
		String result = INDENT + "connect('" + from + "'.outTransition[" + (actualOutEdges.get(from).indexOf(bea.getTo()) + 1) + "],'" + to
				+ "'.inPlaces[" + (actualInEdges.get(to).indexOf(bea.getFrom()) + 1) + "]) " + this.getFromToAnnotation(bea.getFrom(), bea.getTo())
				+ ";"
				// +" annotation(Line(points = {{"
				// +Math.floor(scale*(fromPoint.getX()+xshift)+19)+","
				// +Math.floor(scale*(-(fromPoint.getY()+yshift))+((numOutEdges.get(from)-1)*pinabstand/2-(actualOutEdges.get(from))*pinabstand))+"},{"
				// +Math.floor(scale*(toPoint. getX()+xshift)-10)+","
				// +Math.floor(scale*(-(toPoint.
				// getY()+yshift))+((numInEdges.get(to)-1)*pinabstand/2-(actualInEdges.get(to))*pinabstand))+"}}));"
				+ ENDL;
		// System.out.println(from+".tSumOut_["+(actualOutEdges.get(from) +
		// 1)+"]");
		this.bea2resultkey.put(bea, "'" + from + "'.tokenFlow.outflow[" + (actualOutEdges.get(from).indexOf(bea.getTo()) + 1) + "]");

		// actualInEdges.get(to).add(bea.getTo());
		// actualOutEdges.get(from).add(bea.getFrom());

		return result;
	}

	public HashMap<BiologicalEdgeAbstract, String> getBea2resultkey() {
		return bea2resultkey;
	}

	private String replaceAll(String function, ArrayList<Parameter> params, BiologicalNodeAbstract node) {

		String mFunction = this.replaceParameters(function, params, node);
		mFunction = this.replaceNames(mFunction);
		return mFunction;
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
		double y = -pw.getGraph().getVertexLocation(bna).getY();
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

	private String getFromToAnnotation(BiologicalNodeAbstract from, BiologicalNodeAbstract to) {

		Point2D p1;
		Point2D p2;
		String color;
		double shiftFrom;
		double shiftTo;

		if (from.hasRef()) {
			p1 = pw.getGraph().getVertexLocation(from.getRef());
		} else {
			p1 = pw.getGraph().getVertexLocation(from);
		}
		if (to.hasRef()) {
			p2 = pw.getGraph().getVertexLocation(to.getRef());
		} else {
			p2 = pw.getGraph().getVertexLocation(to);
		}

		if (from.hasRef() || to.hasRef()) {
			color = "{180, 180, 180}";
		} else {
			color = "{0, 0, 0}";
		}

		if (from.getBiologicalElement().equals(Elementdeclerations.s_place)) {
			shiftFrom = 25;
		} else {
			shiftFrom = 10;
		}

		if (to.getBiologicalElement().equals(Elementdeclerations.s_place)) {
			shiftTo = -25;
		} else {
			shiftTo = -10;
		}

		return "annotation(Line(color=" + color + ", points={{" + (p1.getX() + shiftFrom) + "," + (-p1.getY()) + "}, {" + (p2.getX() + shiftTo) + ","
				+ (-p2.getY()) + "}}))";
	}

	private String getModelicaString(BiologicalNodeAbstract bna) {

		if (bna instanceof ContinuousTransition) {
			if (colored) {
				return "PNlib.Examples.Models.BicoloredPlaces.CTC";
			}
			return "PNlib.TC";
		} else if (bna instanceof DiscreteTransition) {
			return "PNlib.TD";
		} else if (bna instanceof StochasticTransition) {
			return "PNlib.TS";
		} else if (bna instanceof Place) {
			if (((Place) bna).isDiscrete()) {
				return "PNlib.PD";
			} else {
				if (colored) {
					return "PNlib.Examples.Models.BicoloredPlaces.CPC";
				}
				return "PNlib.PC";
			}
		}
		return null;
	}

	private String getModelicaEdgeFunction(PNEdge bea) {

		if (bea.getFrom().isConstant() || bea.getTo().isConstant()) {
			return "0";
		}
		if (bea.getFrom().hasRef() && bea.getFrom().getRef().isConstant()) {
			return "0";
		}
		if (bea.getTo().hasRef() && bea.getTo().getRef().isConstant()) {
			return "0";
		}
		return replaceNames(bea.getFunction());

	}

	private String replaceNames(String function) {
		StringBuilder mFunction = new StringBuilder(function);
		String name = "";
		// System.out.println("drin");
		// Character c;

		// Character l;
		Character r;
		boolean check;
		int index = 0;
		int idxNew = 0;
		String insert;

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
						// System.out.println(name+" ersetzt durch:
						// "+mNames.get(name));
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

	private String replaceParameters(String function, ArrayList<Parameter> params, BiologicalNodeAbstract node) {
		StringBuilder mFunction = new StringBuilder(function);

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
						// System.out.println(name+" ersetzt durch:
						// "+mNames.get(name));
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
		return mFunction.toString();
	}

	private Set<BiologicalNodeAbstract> getMarkedNeighborsIn(BiologicalNodeAbstract bna) {
		HashSet<BiologicalNodeAbstract> set = new HashSet<BiologicalNodeAbstract>();
		Iterator<BiologicalEdgeAbstract> it = GraphInstance.getMyGraph().getJungGraph().getInEdges(bna).iterator();
		BiologicalEdgeAbstract bea;
		while (it.hasNext()) {
			bea = it.next();
			if (marked.contains(bea.getFrom())) {
				set.add(bea.getFrom());
			}
		}
		return set;
	}

	private BiologicalNodeAbstract resolveReference(BiologicalNodeAbstract bna) {
		if (bna.hasRef()) {
			return this.resolveReference(bna.getRef());
		}
		return bna;
	}

	private String getgFunctions() {
		String f = "function g1" + ENDL + "    input Real[2] inColors;" + ENDL + "    output Real[2] outWeights;" + ENDL + "  algorithm" + ENDL
				+ "    if sum(inColors) < 1e-12 then" + ENDL + "      outWeights := fill(1, 2);" + ENDL + "    else" + ENDL
				+ "      outWeights[1] := inColors[1] / sum(inColors);" + ENDL + "      outWeights[2] := inColors[2] / sum(inColors);" + ENDL
				+ "    end if;" + ENDL + "  end g1;" + ENDL + "  function g2" + ENDL + "    input Real[2] inColors1;" + ENDL
				+ "    input Real[2] inColors2;" + ENDL + "    output Real[2] outWeights;" + ENDL + "  algorithm" + ENDL
				+ "    if sum(inColors1) < 1e-12 then" + ENDL + "      outWeights := fill(0.5, 2);" + ENDL + "    else" + ENDL
				+ "      outWeights[1] := inColors1[1] / sum(inColors1) / 2;" + ENDL + "      outWeights[2] := inColors1[2] / sum(inColors1) / 2;"
				+ ENDL + "    end if;" + ENDL + "" + ENDL + "    if sum(inColors2) < 1e-12 then" + ENDL
				+ "      outWeights[1] := outWeights[1] + 0.5;" + ENDL + "      outWeights[2] := outWeights[2] + 0.5;" + ENDL + "    else" + ENDL
				+ "      outWeights[1] := outWeights[1] + inColors2[1] / sum(inColors2) / 2;" + ENDL
				+ "      outWeights[2] := outWeights[2] + inColors2[2] / sum(inColors2) / 2;" + ENDL + "    end if;" + ENDL + "  end g2;" + ENDL;
		return f;
	}
}
