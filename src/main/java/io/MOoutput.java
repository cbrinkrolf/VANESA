package io;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.edges.petriNet.PNArc;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.petriNet.ContinuousTransition;
import biologicalObjects.nodes.petriNet.DiscretePlace;
import biologicalObjects.nodes.petriNet.DiscreteTransition;
import biologicalObjects.nodes.petriNet.PNNode;
import biologicalObjects.nodes.petriNet.Place;
import biologicalObjects.nodes.petriNet.StochasticTransition;
import biologicalObjects.nodes.petriNet.Transition;
import configurations.Workspace;
import graph.GraphInstance;
import graph.gui.Parameter;
import util.StochasticDistribution;
import util.StringLengthComparator;
import util.VanesaUtility;

/**
 * @author Rafael, cbrinkro
 */
public class MOoutput extends BaseWriter<Pathway> {
	private static final String INDENT = "  ";
	private static final String ENDL = System.lineSeparator();
	private static final Set<Character> CHARS = Set.of('*', '+', '/', '-', '^', '(', ')', ',', ' ');

	// Stringbuilder for test arcs, inhibitory arcs, places, and transitions
	private final StringBuilder componentsSB = new StringBuilder();
	private final StringBuilder parametersSB = new StringBuilder();
	private final StringBuilder edgesSB = new StringBuilder();

	private final Map<String, ArrayList<BiologicalNodeAbstract>> actualInEdges = new HashMap<>();
	private final Map<String, ArrayList<BiologicalNodeAbstract>> actualOutEdges = new HashMap<>();
	private final Map<BiologicalNodeAbstract, String> nodeType = new HashMap<>();
	private final Map<BiologicalNodeAbstract, String> vertex2name = new HashMap<>();
	private final Map<String, String> inWeights = new HashMap<>();
	private final Map<String, String> outWeights = new HashMap<>();
	private final Map<String, String> outPrio = new HashMap<>();
	private final Map<String, String> outProb = new HashMap<>();

	private final Map<BiologicalEdgeAbstract, String> bea2resultkey = new HashMap<>();

	private Set<BiologicalNodeAbstract> marked;

	private String modelName;
	private final String packageInfo;
	private final boolean colored;
	private final boolean noIdent = false;
	private int seed;

	double minX = Double.MAX_VALUE;
	double minY = Double.MAX_VALUE;
	double maxX = -Double.MAX_VALUE;
	double maxY = -Double.MAX_VALUE;

	private String PNlibSettings = "";
	private String PNlibPlaceDisc = "";
	private String PNlibPlaceCont = "";
	private String PNlibPlaceBiColor = "";
	private String PNlibTransitionDisc = "";
	private String PNlibTransitionCont = "";
	private String PNlibtransitionStoch = "";
	private String PNlibTransitionBiColor = "";
	private String PNlibIA = "";
	private String PNlibTA = "";
	private String distrPackage = "PNlib.Types.DistributionType.";

	private int inhibitCount = 0;
	private int testArcCount = 0;

	public MOoutput(File file, boolean colored) {
		this(file, "m", null, 42, colored);
	}

	public MOoutput(File file, String modelName, String packageInfo, int seed, boolean colored) {
		super(file);
		this.seed = seed;
		init();
		this.modelName = modelName;
		this.packageInfo = packageInfo;
		this.colored = colored;
	}

	private void init() {
		marked = GraphInstance.getMyGraph().getVisualizationViewer().getPickedVertexState().getPicked();

		// set correct PNlib names, they might change from OM version
		Map<String, String> env = System.getenv();
		int version = 13;
		if (env.containsKey("OPENMODELICAHOME") && new File(env.get("OPENMODELICAHOME")).isDirectory()) {
			String pathCompiler = env.get("OPENMODELICAHOME");
			if (pathCompiler.contains(".12.")) {
				version = 12;
			}
		}

		switch (version) {
		case 12:
			PNlibSettings = "PNlib.Settings";
			PNlibPlaceDisc = "PNlib.PD";
			PNlibPlaceCont = "PNlib.PC";
			PNlibPlaceBiColor = "PNlib.Examples.Models.BicoloredPlaces.CPC";
			PNlibTransitionDisc = "PNlib.TD";
			PNlibTransitionCont = "PNlib.TC";
			PNlibtransitionStoch = "PNlib.TDS";
			PNlibTransitionBiColor = "PNlib.Examples.Models.BicoloredPlaces.CTC";
			PNlibIA = "PNlib.IA";
			PNlibTA = "PNlib.TA";
			break;
		case 13:
			PNlibSettings = "PNlib.Components.Settings";
			PNlibPlaceDisc = "PNlib.Components.PD";
			PNlibPlaceCont = "PNlib.Components.PC";
			PNlibPlaceBiColor = "";
			PNlibTransitionDisc = "PNlib.Components.TD";
			PNlibTransitionCont = "PNlib.Components.TC";
			PNlibtransitionStoch = "PNlib.Components.TDS";
			PNlibTransitionBiColor = "";
			PNlibIA = "PNlib.Components.IA";
			PNlibTA = "PNlib.Components.TA";
			break;
		}
	}

	@Override
	protected void internalWrite(OutputStream outputStream, Pathway pathway) throws Exception {
		if (Workspace.getCurrentSettings().isDeveloperMode()) {
			System.out.println("MOoutput(File " + pathway.getName() + " Pathway " + pathway + ")");
		}
		if (Workspace.getCurrentSettings().isDeveloperMode())
			System.out.println("Model Name = " + modelName);
		prepare(pathway);
		buildConnections(pathway);
		buildParameters(pathway);
		buildAllNodes(pathway);
		StringBuilder sb = new StringBuilder();
		sb.append("model ").append(modelName).append(ENDL);
		if (colored) {
			sb.append(this.getgFunctions());
		}
		if (packageInfo != null && !packageInfo.isEmpty() && !packageInfo.isBlank()) {
			sb.append(INDENT).append(this.packageInfo).append(ENDL);
		}

		// if (this.packageInfo == null) {
		// sb.append(indentation + "inner PNlib.Settings settings1();" + ENDL);

		// globalSeed influences stochastic transitions and conflict solving strategy:
		// probability

		String omitOpenModelicaAnnimations = "animateHazardFunc = false, animateMarking = false, animatePlace = false, animatePutFireTime = false, animateSpeed = false, animateTIarc = false, animateTransition = false, animateWeightTIarc = false,";

		sb.append("parameter Integer seed = " + seed + ";").append(ENDL);
		sb.append(INDENT).append("inner ").append(PNlibSettings).append(" settings(")
				.append(omitOpenModelicaAnnimations).append("showTokenFlow = true, globalSeed=seed)").append(ENDL)
				.append("annotation(Placement(visible=true, transformation(origin={").append(minX - 30).append(", ")
				.append(maxY + 30).append("}, extent={{-20, -20}, {20, 20}}, rotation=0)));").append(ENDL);
		// }
		sb.append(parametersSB);
		sb.append(componentsSB);
		sb.append("equation").append(ENDL);
		sb.append(edgesSB);
		sb.append(INDENT).append("annotation(Icon(coordinateSystem(extent={{").append(minX - 50).append(",")
				.append(minY - 50).append("},{").append(maxX + 50).append(",").append(maxY + 50)
				.append("}})), Diagram(coordinateSystem(extent={{").append(minX - 50).append(",").append(minY - 50)
				.append("},{").append(maxX + 50).append(",").append(maxY + 50).append("}})));").append(ENDL);
		sb.append("end ").append(modelName).append(";").append(ENDL);
		String data = sb.toString();
		if (noIdent) {
			// remove "'"
			data = data.replaceAll("'", "");
			// remove "+" in names
			// data= data.replaceAll("(\\S)\\+", "$1_plus");
			// remove "-" in names
			// data= data.replaceAll("(\\S)\\-", "$1_");
		}
		outputStream.write(data.getBytes());
	}

	private void prepare(Pathway pw) {
		for (BiologicalNodeAbstract bna : pw.getAllGraphNodesSortedAlphabetically()) {
			if (!bna.isLogical()) {
				/*
				 * if (biologicalElement.equals(biologicalElements.Elementdeclerations.place) ||
				 * biologicalElement.equals(biologicalElements.Elementdeclerations.s_place))
				 * name = bna.getName(); else name = bna.getName();
				 */

				this.vertex2name.put(bna, bna.getName());
				// nodePositions.put(name, p);
				nodeType.put(bna, bna.getBiologicalElement());
				// bioName.put(name, bna.getLabel());
				// bioObject.put(name, bna);
			}
		}
	}

	private void buildParameters(Pathway pw) {
		for (BiologicalNodeAbstract bna : pw.getAllGraphNodesSortedAlphabetically()) {
			if (!bna.isLogical()) {
				for (Parameter p : bna.getParametersSortedAlphabetically()) {
					// params += this.indentation + "parameter Real
					// "+bna.getParameters().get(i).getName()+" =
					// "+bna.getParameters().get(i).getValue()+";" + this.endl;
					parametersSB.append(INDENT).append("parameter Real '_").append(bna.getName()).append("_")
							.append(p.getName()).append("'");
					if (p.getUnit().length() > 0) {
						parametersSB.append("(final unit=\"").append(p.getUnit()).append("\")");
					}
					parametersSB.append(" = ").append(p.getValue().toPlainString()).append(";").append(ENDL);
				}
				// functions and parameters at arcs belong to the corresponding transition
				if (bna instanceof Transition) {
					for (BiologicalEdgeAbstract bea : VanesaUtility
							.getEdgesSortedByID(pw.getGraph().getJungGraph().getIncidentEdges(bna))) {
						for (Parameter p : bea.getParametersSortedAlphabetically()) {

							// params += this.indentation + "parameter Real
							// "+bna.getParameters().get(i).getName()+" =
							// "+bna.getParameters().get(i).getValue()+";" + this.endl;
							parametersSB.append(INDENT).append("parameter Real '__").append(bna.getName()).append("_")
									.append(p.getName()).append("'");
							if (p.getUnit().length() > 0) {
								parametersSB.append("(final unit=\"").append(p.getUnit()).append("\")");
							}
							parametersSB.append(" = ").append(p.getValue().toPlainString()).append(";").append(ENDL);
						}
					}

				}
			}
		}
	}

	private void buildAllNodes(Pathway pw) {
		List<BiologicalNodeAbstract> places = new ArrayList<>();
		List<BiologicalNodeAbstract> transitions = new ArrayList<>();
		for (BiologicalNodeAbstract bna : pw.getAllGraphNodesSortedAlphabetically()) {
			if (bna instanceof Place) {
				places.add(bna);
			} else if (bna instanceof Transition) {
				transitions.add(bna);
			}
		}
		buildNodes(pw, places);
		buildNodes(pw, transitions);
	}

	private void buildNodes(Pathway pw, List<BiologicalNodeAbstract> nodes) {
		String biologicalElement;
		int in;
		int out;
		Iterator<BiologicalNodeAbstract> refIt;
		BiologicalNodeAbstract node;
		// String attr;
		Place place;
		String start;
		String min;
		String max;
		StochasticTransition st;
		List<Integer> events;
		List<Double> probs;
		DiscreteTransition dt;
		ContinuousTransition ct;
		String speed;
		String delay;
		Transition t;
		StringBuilder attr = new StringBuilder();
		for (BiologicalNodeAbstract bna : nodes) {
			attr.setLength(0);

			if (!bna.isLogical() && bna instanceof PNNode) {
				biologicalElement = bna.getBiologicalElement();
				// double km = Double.NaN, kcat = Double.NaN;
				// String ec = "";

				in = pw.getGraph().getJungGraph().getInEdges(bna).size();
				out = pw.getGraph().getJungGraph().getOutEdges(bna).size();

				if (bna.getRefs().size() > 0) {
					refIt = bna.getRefs().iterator();
					while (refIt.hasNext()) {
						node = refIt.next();
						in += pw.getGraph().getJungGraph().getInEdges(node).size();
						out += pw.getGraph().getJungGraph().getOutEdges(node).size();
					}

				}
				if (biologicalElement.equals(Elementdeclerations.discretePlace)) {
					place = (Place) bna;
					attr.append("startTokens=" + (int) place.getTokenStart() + ", minTokens="
							+ (int) place.getTokenMin() + ", maxTokens=" + (int) place.getTokenMax());
					// places = places.concat(getPlaceString(getModelicaString(place), bna, atr, in,
					// out));
				} else if (biologicalElement.equals(Elementdeclerations.continuousPlace)) {
					place = (Place) bna;
					if (colored) {
						start = "{0.0, " + place.getTokenStart() + "}";
						min = "{" + place.getTokenMin() + ", " + place.getTokenMin() + "}";
						max = "{" + place.getTokenMax() + ", " + place.getTokenMax() + "}";
					} else {
						start = String.valueOf(place.getTokenStart());
						min = String.valueOf(place.getTokenMin());
						max = String.valueOf(place.getTokenMax());
					}

					// CHRIS units
					attr.append("startMarks(final unit=\"mmol\")=" + start + " ,minMarks(final unit=\"mmol\")=" + min
							+ " ,maxMarks(final unit=\"mmol\")=" + max + " ,t(final unit=\"mmol\")");
					if (place.getConflictingOutEdges().size() > 1) {

						// priority is default
						if (place.getConflictStrategy() == Place.CONFLICTHANDLING_PROB) {
							attr.append(", enablingType=PNlib.Types.EnablingType.Probability");
							attr.append(", enablingProbOut={" + this.outProb.get(place.getName()) + "}");
						} else if (place.getConflictStrategy() == Place.CONFLICTHANDLING_PRIO) {
							attr.append(", enablingPrioOut={" + this.outPrio.get(place.getName()) + "}");
						}
						// System.out.println(atr);
					}
					// places =
					// places.concat(getPlaceString(getModelicaString(place),
					// bna, atr, in, out));
					// System.out.println(place.getName() + " conflicting edges:
					// " + place.getConflictingOutEdges().size());
				} else if (biologicalElement.equals(Elementdeclerations.stochasticTransition)) {

					st = (StochasticTransition) bna;
					attr.append("distributionType = " + distrPackage);
					switch (st.getDistribution()) {
					case StochasticDistribution.distributionExponential:
						attr.append("Exponential");
						break;
					case StochasticDistribution.distributionTriangular:
						attr.append("Triangular");
						break;
					case StochasticDistribution.distributionTruncatedNormal:
						attr.append("TruncatedNormal");
						break;
					case StochasticDistribution.distributionUniform:
						attr.append("Uniform");
						break;
					case StochasticDistribution.distributionDiscreteProbability:
						attr.append("Discrete");
						break;
					}
					attr.append(", h = " + st.getH() + ", a = " + st.getA() + ", b = " + st.getB() + ", c = "
							+ st.getC() + ", mu = " + st.getMu() + ", sigma = " + st.getSigma() + ", ");
					attr.append("E = {");
					events = st.getEvents();
					for (int i = 0; i < events.size(); i++) {
						if (i > 0) {
							attr.append(", ");
						}
						attr.append(events.get(i));
					}
					attr.append("}, P = {");
					probs = st.getProbabilities();
					for (int i = 0; i < probs.size(); i++) {
						if (i > 0) {
							attr.append(", ");
						}
						attr.append(probs.get(i));
					}
					attr.append("}");

					// places = places.concat(getTransitionString(bna,
					// getModelicaString(t), bna.getName(), atr, in, out));

				} else if (biologicalElement.equals(Elementdeclerations.discreteTransition)) {

					dt = (DiscreteTransition) bna;
					delay = this.replaceAll(dt.getDelay(), dt.getParameters(), dt.getName(), false);
					attr.append("delay=" + delay);
					// places = places.concat(getTransitionString(bna,
					// getModelicaString(t), bna.getName(), atr, in, out));

				} else if (biologicalElement.equals(Elementdeclerations.continuousTransition)) {
					ct = (ContinuousTransition) bna;
					// System.out.println(ct.getMaximalSpeed());
					speed = this.replaceAll(ct.getMaximalSpeed(), ct.getParameters(), ct.getName(), false);
					// System.out.println(speed);
					// if (ct.isKnockedOut()) {
					// attr.append("maximumSpeed(final unit=\"mmol/min\")=0/*" + speed + "*/");
					// } else {
					attr.append("maximumSpeed=" + speed);
					// }

					// System.out.println("atr");
					// places = places.concat(getTransitionString(bna,
					// getModelicaString(t), bna.getName(), atr, in, out));
				}

				if (bna instanceof Place) {

				} else if (bna instanceof Transition) {
					t = (Transition) bna;
					String firingCondition = t.getFiringCondition();
					if (t.isKnockedOut()) {
						firingCondition = "false";
					}
					if (t.getFiringCondition().length() > 0) {
						attr.append(", firingCon=" + firingCondition);
					}
				}
				componentsSB.append(getTransitionString(pw, bna, getModelicaString(bna), bna.getName(), attr, in, out));
			}
		}
	}

	private void buildConnections(Pathway pw) {
		inhibitCount = 0;
		testArcCount = 0;
		StringBuilder weight = new StringBuilder();
		for (BiologicalEdgeAbstract bea : pw.getAllEdgesSortedByID()) {
			weight.setLength(0);
			String fromString = vertex2name.get(this.resolveReference(bea.getFrom()));
			String toString = vertex2name.get(this.resolveReference(bea.getTo()));
			if (bea instanceof PNArc) {
				PNArc e = (PNArc) bea;
				// Edge Place -> Transition
				if (e.getFrom() instanceof Place) {
					if (colored) {
						if (marked.contains(e.getFrom())) {
							weight.append("g1('" + resolveReference(e.getFrom()).getName() + "'.color)");
						} else {
							weight.append("{0, " + this.getModelicaEdgeFunction(e) + "}/*" + fromString + "*/");
						}
					} else {
						weight.append(this.getModelicaEdgeFunction(e));
					}

					if (this.inWeights.containsKey(toString)) {
						this.inWeights.put(toString, inWeights.get(toString) + ", " + weight);
					} else {
						this.inWeights.put(toString, weight.toString());
					}

					if (!(e.getTo() instanceof ContinuousTransition)) {
						int prio = e.getPriority();
						if (this.outPrio.containsKey(fromString)) {
							this.outPrio.put(fromString, outPrio.get(fromString) + ", " + prio);
						} else {
							this.outPrio.put(fromString, String.valueOf(prio));
						}
						double prob = e.getProbability();
						if (this.outProb.containsKey(fromString)) {
							this.outProb.put(fromString, outProb.get(fromString) + ", " + prob);
						} else {
							this.outProb.put(fromString, String.valueOf(prob));
						}
					}

					// Edge Transition -> Place
				} else {
					if (colored) {
						Set<BiologicalNodeAbstract> markedOut = getMarkedNeighborsIn(e.getFrom());
						final String tmp;
						if (markedOut.isEmpty()) {
							tmp = "{0, " + getModelicaEdgeFunction(e) + "}/*" + toString + "*/";
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
						}

						if (marked.contains(e.getTo())) {
							weight.append(tmp);
						} else {
							weight.append("{0, " + getModelicaEdgeFunction(e) + "}/*" + fromString + "*/");
						}
					} else {
						weight.append(getModelicaEdgeFunction(e));
					}

					if (this.outWeights.containsKey(fromString)) {
						this.outWeights.put(fromString, outWeights.get(fromString) + ", " + weight);
					} else {
						this.outWeights.put(fromString, weight.toString());
					}
				}

				if (!actualInEdges.containsKey(toString)) {
					actualInEdges.put(toString, new ArrayList<>());
				}
				actualInEdges.get(toString).add(e.getFrom());

				if (!actualOutEdges.containsKey(fromString)) {
					actualOutEdges.put(fromString, new ArrayList<>());
				}

				actualOutEdges.get(fromString).add(e.getTo());

				if (e.getBiologicalElement().equals(Elementdeclerations.pnInhibitorArc)) {
					if (e.getFrom() instanceof Place) {
						componentsSB.append(this.createInhibitoryArc(pw, fromString, toString, e));
					}
				} else if (e.getBiologicalElement().equals(Elementdeclerations.pnTestArc)) {
					if (e.getFrom() instanceof Place) {
						componentsSB.append(this.createTestArc(pw, fromString, toString, e));
					}
				} else if (e.getFrom() instanceof Place) {
					if (e.getFrom().isConstant()) {
						componentsSB.append(this.createTestArc(pw, fromString, toString, e));
					} else {
						edgesSB.append(getConnectionStringPT(pw, fromString, toString, e));
					}
				} else {
					edgesSB.append(getConnectionStringTP(pw, fromString, toString, e));
				}
			}
		}
	}

	private String createInhibitoryArc(Pathway pw, String fromString, String toString, PNArc e) {
		inhibitCount++;
		String result = INDENT + PNlibIA + " inhibitorArc" + inhibitCount + "(testValue="
				+ this.getModelicaEdgeFunction(e) + ");" + ENDL;
		edgesSB.append(INDENT + "connect('" + fromString + "'.outTransition["
				+ (actualOutEdges.get(fromString).indexOf(e.getTo()) + 1) + "]," + "inhibitorArc" + inhibitCount
				+ ".inPlace);" + ENDL);
		edgesSB.append(INDENT + "connect(" + "inhibitorArc" + inhibitCount + ".outTransition,'" + toString
				+ "'.inPlaces[" + (actualInEdges.get(toString).indexOf(e.getFrom()) + 1) + "]) "
				+ this.getFromToAnnotation(pw, e.getFrom(), e.getTo()) + ";" + ENDL);
		return result;
	}

	private String createTestArc(Pathway pw, String fromString, String toString, PNArc e) {
		testArcCount++;
		String result = INDENT + PNlibTA + " testArc" + testArcCount + "(testValue=" + this.getModelicaEdgeFunction(e)
				+ ");" + ENDL;
		edgesSB.append(INDENT + "connect('" + fromString + "'.outTransition["
				+ (actualOutEdges.get(fromString).indexOf(e.getTo()) + 1) + "]," + "testArc" + testArcCount
				+ ".inPlace);" + ENDL);
		edgesSB.append(INDENT + "connect(" + "testArc" + testArcCount + ".outTransition,'" + toString + "'.inPlaces["
				+ (actualInEdges.get(toString).indexOf(e.getFrom()) + 1) + "]) "
				+ this.getFromToAnnotation(pw, e.getFrom(), e.getTo()) + ";" + ENDL);
		return result;
	}

	private String getTransitionString(Pathway pw, BiologicalNodeAbstract bna, String element, String name,
			StringBuilder attr, int inEdges, int outEdges) {
		// for (int i = 0; i < bna.getParameters().size(); i++) {
		// params += this.indentation + "parameter Real
		// "+bna.getParameters().get(i).getName()+" =
		// "+bna.getParameters().get(i).getValue()+";" + this.endl;
		// }
		String inNumbers = "";
		if (this.inWeights.containsKey(name)) {
			inNumbers = this.inWeights.get(name);
		}
		String outNumbers = "";
		if (this.outWeights.containsKey(name)) {
			outNumbers = this.outWeights.get(name);
		}
		String in = ",arcWeightIn={" + inNumbers + "}";
		String out = ",arcWeightOut={" + outNumbers + "}";
		if (inNumbers.length() == 0) {
			in = "";
		}
		if (outNumbers.length() == 0) {
			out = "";
		}
		return INDENT + element + " '" + bna.getName() + "'(nIn=" + inEdges + ", nOut=" + outEdges + ", "
				+ attr.toString() + in + out + ") " + getPlacementAnnotation(pw, bna) + ";" + ENDL;
	}

	private String getConnectionStringTP(Pathway pw, String from, String to, BiologicalEdgeAbstract bea) {
		// String from = bea.getFrom().getName();
		// String to = bea.getTo().getName();
		String result = INDENT + "connect('" + from + "'.outPlaces["
				+ (actualOutEdges.get(from).indexOf(bea.getTo()) + 1) + "], '" + to + "'.inTransition["
				+ (actualInEdges.get(to).indexOf(bea.getFrom()) + 1) + "]) "
				+ this.getFromToAnnotation(pw, bea.getFrom(), bea.getTo()) + ";"
				// +" annotation(Line(points = {{"
				// +Math.floor(scale*(fromPoint.getX()+xshift)+10)+","
				// +Math.floor(scale*(-(fromPoint.getY()+yshift))+((numOutEdges.get(from)-1)*pinabstand/2-(actualOutEdges.get(from))*pinabstand))+"},{"
				// +Math.floor(scale*(toPoint. getX()+xshift)-19)+","
				// +Math.floor(scale*(-(toPoint.
				// getY()+yshift))+((numInEdges.get(to)-1)*pinabstand/2-(actualInEdges.get(to))*pinabstand))+"}}));"
				+ ENDL;
		this.bea2resultkey.put(bea,
				"'" + to + "'.tokenFlow.inflow[" + (actualInEdges.get(to).indexOf(bea.getFrom()) + 1) + "]");

		// actualInEdges.get(to).add(bea.getTo());
		// actualOutEdges.get(from).add(bea.getFrom());
		return result;
	}

	private String getConnectionStringPT(Pathway pw, String from, String to, BiologicalEdgeAbstract bea) {
		// String from = bea.getFrom().getName();
		// String to = bea.getTo().getName();
		String result = INDENT + "connect('" + from + "'.outTransition["
				+ (actualOutEdges.get(from).indexOf(bea.getTo()) + 1) + "], '" + to + "'.inPlaces["
				+ (actualInEdges.get(to).indexOf(bea.getFrom()) + 1) + "]) "
				+ this.getFromToAnnotation(pw, bea.getFrom(), bea.getTo()) + ";"
				// +" annotation(Line(points = {{"
				// +Math.floor(scale*(fromPoint.getX()+xshift)+19)+","
				// +Math.floor(scale*(-(fromPoint.getY()+yshift))+((numOutEdges.get(from)-1)*pinabstand/2-(actualOutEdges.get(from))*pinabstand))+"},{"
				// +Math.floor(scale*(toPoint. getX()+xshift)-10)+","
				// +Math.floor(scale*(-(toPoint.
				// getY()+yshift))+((numInEdges.get(to)-1)*pinabstand/2-(actualInEdges.get(to))*pinabstand))+"}}));"
				+ ENDL;
		this.bea2resultkey.put(bea,
				"'" + from + "'.tokenFlow.outflow[" + (actualOutEdges.get(from).indexOf(bea.getTo()) + 1) + "]");
		// actualInEdges.get(to).add(bea.getTo());
		// actualOutEdges.get(from).add(bea.getFrom());

		return result;
	}

	public Map<BiologicalEdgeAbstract, String> getBea2resultkey() {
		return bea2resultkey;
	}

	private String replaceAll(String function, List<Parameter> params, String nodePrefix, boolean isEdge) {

		String mFunction = this.replaceParameters(function, params, nodePrefix, isEdge);
		mFunction = this.replaceNames(mFunction);
		return mFunction;
	}

	private String getPlacementAnnotation(Pathway pw, BiologicalNodeAbstract bna) {
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

		return "annotation(Placement(visible=true, transformation(origin={" + x + "," + y
				+ "}, extent={{-20, -20}, {20, 20}}, rotation=0)))";
	}

	private String getFromToAnnotation(Pathway pw, BiologicalNodeAbstract from, BiologicalNodeAbstract to) {
		Point2D p1 = pw.getGraph().getVertexLocation(this.resolveReference(from));
		Point2D p2 = pw.getGraph().getVertexLocation(this.resolveReference(to));
		String color = "{0, 0, 0}";
		if (from.isLogical() || to.isLogical()) {
			color = "{180, 180, 180}";
		}
		double shiftFrom = 10;
		if (from.getBiologicalElement().equals(Elementdeclerations.continuousPlace)) {
			shiftFrom = 25;
		}
		double shiftTo = -10;
		if (to.getBiologicalElement().equals(Elementdeclerations.continuousPlace)) {
			shiftTo = -25;
		}
		return "annotation(Line(color=" + color + ", points={{" + (p1.getX() + shiftFrom) + ", " + (-p1.getY()) + "}, {"
				+ (p2.getX() + shiftTo) + ", " + (-p2.getY()) + "}}))";
	}

	private String getModelicaString(BiologicalNodeAbstract bna) {
		if (bna instanceof ContinuousTransition) {
			if (colored) {
				return PNlibTransitionBiColor; // "PNlib.Examples.Models.BicoloredPlaces.CTC";
			}
			return PNlibTransitionCont; // "PNlib.Components.TC";
		} else if (bna instanceof DiscreteTransition) {
			return PNlibTransitionDisc; // "PNlib.Components.TD";
		} else if (bna instanceof StochasticTransition) {
			return PNlibtransitionStoch; // "PNlib.Components.TDS";
		} else if (bna instanceof Place) {
			if (bna.isDiscrete()) {
				return PNlibPlaceDisc; // "PNlib.Components.PD";
			} else {
				if (colored) {
					return PNlibPlaceBiColor; // "PNlib.Examples.Models.BicoloredPlaces.CPC";
				}
				return PNlibPlaceCont; // "PNlib.Components.PC";
			}
		}
		return null;
	}

	private String getModelicaEdgeFunction(PNArc bea) {
		if (bea.isRegularArc()) {
			if (bea.getTo().isConstant() && bea.getTo() instanceof Place) {
				return "0";
			}
			if (bea.getTo().isLogical() && bea.getTo().getLogicalReference().isConstant()
					&& bea.getTo().getLogicalReference() instanceof Place) {
				return "0";
			}

			if (bea.getFrom().isConstant() && bea.getFrom() instanceof Place) {
				if (bea.getFrom() instanceof DiscretePlace) {
					return "max(0," + replaceNames(bea.getFunction() + "-1)");
				} else {
					return "0";
				}
			}

			if (bea.getFrom().isLogical() && bea.getFrom().getLogicalReference().isConstant()
					&& bea.getFrom().getLogicalReference() instanceof Place) {
				if (bea.getFrom().getLogicalReference() instanceof DiscretePlace) {
					return "max(0," + replaceNames(bea.getFunction() + "-1)");
				} else {
					return "0";
				}
			}
		}
		if (bea.getTo() instanceof Transition) {
			return replaceAll(bea.getFunction(), bea.getParameters(), bea.getTo().getName(), true);
		} else {
			return replaceAll(bea.getFunction(), bea.getParameters(), bea.getFrom().getName(), true);
		}
		// return replaceNames(bea.getFunction());
	}

	private String replaceNames(String function) {
		StringBuilder mFunction = new StringBuilder(function);
		String name = "";
		// Character c;
		// Character l;
		Character r;
		boolean check;
		int index = 0;
		int idxNew = 0;
		String insert;

		// replace places
		Pathway pw = GraphInstance.getPathway();
		if (!pw.isPetriNet() && pw.getTransformationInformation() != null
				&& pw.getTransformationInformation().getPetriNet() != null) {
			pw = pw.getTransformationInformation().getPetriNet();
		}
		ArrayList<String> names = new ArrayList<>();
		Place p;
		HashMap<String, String> referenceMapping = new HashMap<>();
		for (BiologicalNodeAbstract bna : pw.getAllGraphNodes()) {
			if (bna instanceof Place) {
				p = (Place) bna;
				// names.add("P"+p.getID());
				names.add(p.getName());
				referenceMapping.put(p.getName(), this.resolveReference(p).getName());
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
			while (mFunction.indexOf(name, index) >= 0) {
				check = false;
				idxNew = mFunction.indexOf(name, index);
				if (mFunction.length() >= idxNew + name.length()) {
					if (mFunction.length() > idxNew + name.length()) {
						r = mFunction.charAt(idxNew + name.length());
					} else {
						r = ' ';
					}
					if (idxNew == 0) {
						check = true;
					} else {
						check = CHARS.contains(mFunction.charAt(idxNew - 1));
					}

					/*
					 * if (idxNew > 0) { if (chars.contains(mFunction.charAt(idxNew - 1))) { check =
					 * true; } } else { check = true; }
					 */
					if (check && CHARS.contains(r)) {
						// mFunction = mFunction.replaceFirst(name, mNames.get(name));
						insert = "'" + referenceMapping.get(name) + "'.t";
						mFunction.replace(idxNew, idxNew + name.length(), insert);

						// mFunction.insert(idxNew + name.length(), ".t");
						index = idxNew + insert.length();
					} else {
						index = idxNew + name.length();
						// break;
					}
				} else {
					break;
				}
			}
		}
		return mFunction.toString();
	}

	private String replaceParameters(String function, List<Parameter> params, String nodePrefix, boolean isEdge) {
		StringBuilder mFunction = new StringBuilder(function);

		// replace parameters
		ArrayList<String> paramNames = new ArrayList<>();

		for (int i = 0; i < params.size(); i++) {
			paramNames.add(params.get(i).getName());
		}

		Collections.sort(paramNames, new StringLengthComparator());

		String name = "";
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
			while (mFunction.indexOf(name, index) >= 0) {
				check = false;
				idxNew = mFunction.indexOf(name, index);
				if (mFunction.length() >= idxNew + name.length()) {
					if (mFunction.length() > idxNew + name.length()) {
						r = mFunction.charAt(idxNew + name.length());
					} else {
						// Parameter is last term of function
						r = ' ';
					}
					if (idxNew == 0) {
						check = true;
					} else {
						check = CHARS.contains(mFunction.charAt(idxNew - 1));
					}
					// if (!Character.isDigit(c) && !Character.isAlphabetic(c))
					// {
					if (check && CHARS.contains(r)) {
						// mFunction = mFunction.replaceFirst(name, mNames.get(name));
						if (isEdge) {
							// parameters of arcs (assigned to transitions), start with double underscores
							// to avoid naming conflicts between regular transition parameters and arc
							// parameters
							insert = "'__" + nodePrefix + "_" + name + "'";
						} else {
							insert = "'_" + nodePrefix + "_" + name + "'";
						}

						mFunction.replace(idxNew, idxNew + name.length(), insert);
						// mFunction.insert(idxNew, "_" + node.getName() + "_");
						// index = idxNew + name.length() + 2
						// + node.getName().length();
						index = idxNew + insert.length();
					} else {
						index = idxNew + name.length();
						// break;
					}
				} else {
					break;
				}
			}
		}
		return mFunction.toString();
	}

	private Set<BiologicalNodeAbstract> getMarkedNeighborsIn(BiologicalNodeAbstract bna) {
		HashSet<BiologicalNodeAbstract> set = new HashSet<>();
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
		if (bna.isLogical()) {
			return this.resolveReference(bna.getLogicalReference());
		}
		return bna;
	}

	private String getgFunctions() {
		String f = "function g1" + ENDL + "    input Real[2] inColors;" + ENDL + "    output Real[2] outWeights;" + ENDL
				+ "  algorithm" + ENDL + "    if sum(inColors) < 1e-12 then" + ENDL + "      outWeights := fill(1, 2);"
				+ ENDL + "    else" + ENDL + "      outWeights[1] := inColors[1] / sum(inColors);" + ENDL
				+ "      outWeights[2] := inColors[2] / sum(inColors);" + ENDL + "    end if;" + ENDL + "  end g1;"
				+ ENDL + "  function g2" + ENDL + "    input Real[2] inColors1;" + ENDL + "    input Real[2] inColors2;"
				+ ENDL + "    output Real[2] outWeights;" + ENDL + "  algorithm" + ENDL
				+ "    if sum(inColors1) < 1e-12 then" + ENDL + "      outWeights := fill(0.5, 2);" + ENDL + "    else"
				+ ENDL + "      outWeights[1] := inColors1[1] / sum(inColors1) / 2;" + ENDL
				+ "      outWeights[2] := inColors1[2] / sum(inColors1) / 2;" + ENDL + "    end if;" + ENDL + "" + ENDL
				+ "    if sum(inColors2) < 1e-12 then" + ENDL + "      outWeights[1] := outWeights[1] + 0.5;" + ENDL
				+ "      outWeights[2] := outWeights[2] + 0.5;" + ENDL + "    else" + ENDL
				+ "      outWeights[1] := outWeights[1] + inColors2[1] / sum(inColors2) / 2;" + ENDL
				+ "      outWeights[2] := outWeights[2] + inColors2[2] / sum(inColors2) / 2;" + ENDL + "    end if;"
				+ ENDL + "  end g2;" + ENDL;
		return f;
	}
}
