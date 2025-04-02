package io;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
import org.apache.commons.lang3.StringUtils;
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
	private final Map<String, String> inWeights = new HashMap<>();
	private final Map<String, String> outWeights = new HashMap<>();
	private final Map<String, String> outPrio = new HashMap<>();
	private final Map<String, String> outProb = new HashMap<>();

	private final Map<BiologicalEdgeAbstract, String> bea2resultkey = new HashMap<>();

	private Set<BiologicalNodeAbstract> marked;

	private final String modelName;
	private final String packageInfo;
	private final boolean colored;
	private final boolean noIdent = false;
	private final int seed;

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
	private static final String distrPackage = "PNlib.Types.DistributionType.";

	private int inhibitCount = 0;
	private int testArcCount = 0;

	public MOoutput(File file, boolean colored) {
		this(file, "m", null, 42, colored);
	}

	public MOoutput(File file, String modelName, String packageInfo, int seed, boolean colored) {
		super(file);
		this.seed = seed;
		this.modelName = modelName;
		this.packageInfo = packageInfo;
		this.colored = colored;
		init();
	}

	private void init() {
		marked = GraphInstance.getMyGraph().getVisualizationViewer().getPickedVertexState().getPicked();

		// set correct PNlib names, they might change from OM version
		final Map<String, String> env = System.getenv();
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
	protected void internalWrite(final OutputStream outputStream, final Pathway pathway) throws Exception {
		if (Workspace.getCurrentSettings().isDeveloperMode()) {
			System.out.println(
					"MOoutput(File: " + pathway.getName() + ", Pathway: " + pathway + ", Model Name: " + modelName
							+ ")");
		}
		buildConnections(pathway);
		buildParameters(pathway);
		buildAllNodes(pathway);
		final StringBuilder sb = new StringBuilder();
		sb.append("model ").append(modelName).append(ENDL);
		if (colored) {
			sb.append(getgFunctions());
		}
		if (StringUtils.isNotBlank(packageInfo)) {
			sb.append(INDENT).append(packageInfo).append(ENDL);
		}
		// globalSeed influences stochastic transitions and conflict solving strategy: probability
		sb.append("parameter Integer seed=").append(seed).append(";").append(ENDL);
		sb.append(INDENT).append("inner ").append(PNlibSettings).append(" settings(");
		sb.append("animateHazardFunc=false");
		sb.append(", animateMarking=false");
		sb.append(", animatePlace=false");
		sb.append(", animatePutFireTime=false");
		sb.append(", animateSpeed=false");
		sb.append(", animateTIarc=false");
		sb.append(", animateTransition=false");
		sb.append(", animateWeightTIarc=false");
		sb.append(", showTokenFlow=true");
		sb.append(", globalSeed=seed");
		sb.append(')').append(ENDL);
		sb.append("annotation(Placement(visible=true, transformation(origin={").append(minX - 30).append(", ").append(
				maxY + 30).append("}, extent={{-20, -20}, {20, 20}}, rotation=0)));").append(ENDL);
		sb.append(parametersSB);
		sb.append(componentsSB);
		sb.append("equation").append(ENDL);
		sb.append(edgesSB);
		sb.append(INDENT).append("annotation(Icon(coordinateSystem(extent={{").append(minX - 50).append(",").append(
				minY - 50).append("},{").append(maxX + 50).append(",").append(maxY + 50).append(
				"}})), Diagram(coordinateSystem(extent={{").append(minX - 50).append(",").append(minY - 50).append(
				"},{").append(maxX + 50).append(",").append(maxY + 50).append("}})));").append(ENDL);
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
		final StringBuilder attr = new StringBuilder();
		for (final BiologicalNodeAbstract bna : nodes) {
			attr.setLength(0);
			if (!bna.isLogical() && bna instanceof PNNode) {
				final String biologicalElement = bna.getBiologicalElement();
				// double km = Double.NaN, kcat = Double.NaN;
				// String ec = "";
				int in = pw.getGraph().getJungGraph().getInEdges(bna).size();
				int out = pw.getGraph().getJungGraph().getOutEdges(bna).size();
				if (bna.getRefs().size() > 0) {
					for (final BiologicalNodeAbstract node : bna.getRefs()) {
						in += pw.getGraph().getJungGraph().getInEdges(node).size();
						out += pw.getGraph().getJungGraph().getOutEdges(node).size();
					}
				}
				if (biologicalElement.equals(Elementdeclerations.discretePlace)) {
					final Place place = (Place) bna;
					attr.append("startTokens=").append((int) place.getTokenStart()).append(", minTokens=").append(
							(int) place.getTokenMin()).append(", maxTokens=").append((int) place.getTokenMax());
					// places = places.concat(getPlaceString(getModelicaString(place), bna, atr, in,
					// out));
				} else if (biologicalElement.equals(Elementdeclerations.continuousPlace)) {
					final Place place = (Place) bna;
					final String start;
					final String min;
					final String max;
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
					attr.append("startMarks(final unit=\"mmol\")=").append(start);
					attr.append(" ,minMarks(final unit=\"mmol\")=").append(min);
					attr.append(" ,maxMarks(final unit=\"mmol\")=").append(max);
					attr.append(" ,t(final unit=\"mmol\")");
					if (place.getConflictingOutEdges().size() > 1) {
						// priority is default
						if (place.getConflictStrategy() == Place.CONFLICTHANDLING_PROB) {
							attr.append(", enablingType=PNlib.Types.EnablingType.Probability");
							attr.append(", enablingProbOut={").append(outProb.get(place.getName())).append("}");
						} else if (place.getConflictStrategy() == Place.CONFLICTHANDLING_PRIO) {
							attr.append(", enablingPrioOut={").append(outPrio.get(place.getName())).append("}");
						}
					}
					// places =
					// places.concat(getPlaceString(getModelicaString(place),
					// bna, atr, in, out));
					// System.out.println(place.getName() + " conflicting edges:
					// " + place.getConflictingOutEdges().size());
				} else if (biologicalElement.equals(Elementdeclerations.stochasticTransition)) {
					final StochasticTransition st = (StochasticTransition) bna;
					attr.append("distributionType = ").append(distrPackage);
					switch (st.getDistribution()) {
					case Exponential:
						attr.append("Exponential");
						break;
					case Triangular:
						attr.append("Triangular");
						break;
					case TruncatedNormal:
						attr.append("TruncatedNormal");
						break;
					case Uniform:
						attr.append("Uniform");
						break;
					case DiscreteProbability:
						attr.append("Discrete");
						break;
					}
					attr.append(", h = ").append(st.getH());
					attr.append(", a = ").append(st.getA());
					attr.append(", b = ").append(st.getB());
					attr.append(", c = ").append(st.getC());
					attr.append(", mu = ").append(st.getMu());
					attr.append(", sigma = ").append(st.getSigma());
					attr.append(", E = {");
					attr.append(st.getEvents().stream().map(String::valueOf).collect(Collectors.joining(", ")));
					attr.append("}, P = {");
					attr.append(st.getProbabilities().stream().map(String::valueOf).collect(Collectors.joining(", ")));
					attr.append("}");
					// places = places.concat(getTransitionString(bna,
					// getModelicaString(t), bna.getName(), atr, in, out));
				} else if (biologicalElement.equals(Elementdeclerations.discreteTransition)) {
					final DiscreteTransition dt = (DiscreteTransition) bna;
					final String delay = replaceAll(dt.getDelay(), dt.getParameters(), dt.getName(), false);
					attr.append("delay=").append(delay);
					// places = places.concat(getTransitionString(bna,
					// getModelicaString(t), bna.getName(), atr, in, out));
				} else if (biologicalElement.equals(Elementdeclerations.continuousTransition)) {
					final ContinuousTransition ct = (ContinuousTransition) bna;
					final String speed = replaceAll(ct.getMaximalSpeed(), ct.getParameters(), ct.getName(), false);
					// if (ct.isKnockedOut()) {
					// attr.append("maximumSpeed(final unit=\"mmol/min\")=0/*" + speed + "*/");
					// } else {
					attr.append("maximumSpeed=").append(speed);
					// }
					// places = places.concat(getTransitionString(bna,
					// getModelicaString(t), bna.getName(), atr, in, out));
				}

				if (bna instanceof Place) {
				} else if (bna instanceof Transition) {
					final Transition t = (Transition) bna;
					String firingCondition = t.getFiringCondition();
					if (t.isKnockedOut()) {
						firingCondition = "false";
					}
					if (t.getFiringCondition().length() > 0) {
						attr.append(", firingCon=").append(firingCondition);
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
			if (bea instanceof PNArc) {
				final String fromName = resolveReference(bea.getFrom()).getName();
				final String toName = resolveReference(bea.getTo()).getName();
				final PNArc e = (PNArc) bea;
				// Edge Place -> Transition
				if (e.getFrom() instanceof Place) {
					if (colored) {
						if (marked.contains(e.getFrom())) {
							weight.append("g1('").append(resolveReference(e.getFrom()).getName()).append("'.color)");
						} else {
							weight.append("{0, ").append(getModelicaEdgeFunction(e)).append("}/*").append(fromName)
									.append("*/");
						}
					} else {
						weight.append(getModelicaEdgeFunction(e));
					}

					if (inWeights.containsKey(toName)) {
						inWeights.put(toName, inWeights.get(toName) + ", " + weight);
					} else {
						inWeights.put(toName, weight.toString());
					}

					if (!(e.getTo() instanceof ContinuousTransition)) {
						int prio = e.getPriority();
						if (outPrio.containsKey(fromName)) {
							outPrio.put(fromName, outPrio.get(fromName) + ", " + prio);
						} else {
							outPrio.put(fromName, String.valueOf(prio));
						}
						double prob = e.getProbability();
						if (outProb.containsKey(fromName)) {
							outProb.put(fromName, outProb.get(fromName) + ", " + prob);
						} else {
							outProb.put(fromName, String.valueOf(prob));
						}
					}

					// Edge Transition -> Place
				} else {
					if (colored) {
						Set<BiologicalNodeAbstract> markedOut = getMarkedNeighborsIn(e.getFrom());
						final String tmp;
						if (markedOut.isEmpty()) {
							tmp = "{0, " + getModelicaEdgeFunction(e) + "}/*" + toName + "*/";
						} else {
							String nodes = "";
							for (final BiologicalNodeAbstract node : markedOut) {
								nodes += "'" + resolveReference(node).getName() + "'.color,";
							}
							// ={g2('DHAP'.color, 'GAP'.color)}
							tmp = "g" + markedOut.size() + "(" + nodes.substring(0, nodes.length() - 1) + ")";
						}

						if (marked.contains(e.getTo())) {
							weight.append(tmp);
						} else {
							weight.append("{0, ").append(getModelicaEdgeFunction(e)).append("}/*").append(fromName)
									.append("*/");
						}
					} else {
						weight.append(getModelicaEdgeFunction(e));
					}

					if (outWeights.containsKey(fromName)) {
						outWeights.put(fromName, outWeights.get(fromName) + ", " + weight);
					} else {
						outWeights.put(fromName, weight.toString());
					}
				}

				if (!actualInEdges.containsKey(toName)) {
					actualInEdges.put(toName, new ArrayList<>());
				}
				actualInEdges.get(toName).add(e.getFrom());

				if (!actualOutEdges.containsKey(fromName)) {
					actualOutEdges.put(fromName, new ArrayList<>());
				}

				actualOutEdges.get(fromName).add(e.getTo());

				if (e.getBiologicalElement().equals(Elementdeclerations.pnInhibitorArc)) {
					if (e.getFrom() instanceof Place) {
						componentsSB.append(this.createInhibitoryArc(pw, fromName, toName, e));
					}
				} else if (e.getBiologicalElement().equals(Elementdeclerations.pnTestArc)) {
					if (e.getFrom() instanceof Place) {
						componentsSB.append(this.createTestArc(pw, fromName, toName, e));
					}
				} else if (e.getFrom() instanceof Place) {
					if (e.getFrom().isConstant()) {
						componentsSB.append(this.createTestArc(pw, fromName, toName, e));
					} else {
						edgesSB.append(getConnectionStringPT(pw, fromName, toName, e));
					}
				} else {
					edgesSB.append(getConnectionStringTP(pw, fromName, toName, e));
				}
			}
		}
	}

	private String createInhibitoryArc(Pathway pw, String fromString, String toString, PNArc e) {
		inhibitCount++;
		String result = INDENT + PNlibIA + " inhibitorArc" + inhibitCount + "(testValue=" + getModelicaEdgeFunction(e)
				+ ");" + ENDL;
		edgesSB.append(INDENT + "connect('").append(fromString).append("'.outTransition[").append(
				actualOutEdges.get(fromString).indexOf(e.getTo()) + 1).append("],").append("inhibitorArc").append(
				inhibitCount).append(".inPlace);").append(ENDL);
		edgesSB.append(INDENT + "connect(" + "inhibitorArc").append(inhibitCount).append(".outTransition,'").append(
				toString).append("'.inPlaces[").append(actualInEdges.get(toString).indexOf(e.getFrom()) + 1).append(
				"]) ").append(getFromToAnnotation(pw, e.getFrom(), e.getTo())).append(";").append(ENDL);
		return result;
	}

	private String createTestArc(Pathway pw, String fromString, String toString, PNArc e) {
		testArcCount++;
		String result = INDENT + PNlibTA + " testArc" + testArcCount + "(testValue=" + getModelicaEdgeFunction(e) + ");"
				+ ENDL;
		edgesSB.append(INDENT + "connect('").append(fromString).append("'.outTransition[").append(
				actualOutEdges.get(fromString).indexOf(e.getTo()) + 1).append("],").append("testArc").append(
				testArcCount).append(".inPlace);").append(ENDL);
		edgesSB.append(INDENT + "connect(" + "testArc").append(testArcCount).append(".outTransition,'").append(toString)
				.append("'.inPlaces[").append(actualInEdges.get(toString).indexOf(e.getFrom()) + 1).append("]) ")
				.append(getFromToAnnotation(pw, e.getFrom(), e.getTo())).append(";").append(ENDL);
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
		final String in = inNumbers.isEmpty() ? "" : ",arcWeightIn={" + inNumbers + "}";
		final String out = outNumbers.isEmpty() ? "" : ",arcWeightOut={" + outNumbers + "}";
		return INDENT + element + " '" + bna.getName() + "'(nIn=" + inEdges + ", nOut=" + outEdges + ", "
				+ attr.toString() + in + out + ") " + getPlacementAnnotation(pw, bna) + ";" + ENDL;
	}

	private String getConnectionStringTP(Pathway pw, String from, String to, BiologicalEdgeAbstract bea) {
		// String from = bea.getFrom().getName();
		// String to = bea.getTo().getName();
		String result = INDENT + "connect('" + from + "'.outPlaces["
				+ (actualOutEdges.get(from).indexOf(bea.getTo()) + 1) + "], '" + to + "'.inTransition["
				+ (actualInEdges.get(to).indexOf(bea.getFrom()) + 1) + "]) "
				+ getFromToAnnotation(pw, bea.getFrom(), bea.getTo()) + ";"
				// +" annotation(Line(points = {{"
				// +Math.floor(scale*(fromPoint.getX()+xshift)+10)+","
				// +Math.floor(scale*(-(fromPoint.getY()+yshift))+((numOutEdges.get(from)-1)*pinabstand/2-(actualOutEdges.get(from))*pinabstand))+"},{"
				// +Math.floor(scale*(toPoint. getX()+xshift)-19)+","
				// +Math.floor(scale*(-(toPoint.
				// getY()+yshift))+((numInEdges.get(to)-1)*pinabstand/2-(actualInEdges.get(to))*pinabstand))+"}}));"
				+ ENDL;
		bea2resultkey.put(bea,
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
				+ getFromToAnnotation(pw, bea.getFrom(), bea.getTo()) + ";"
				// +" annotation(Line(points = {{"
				// +Math.floor(scale*(fromPoint.getX()+xshift)+19)+","
				// +Math.floor(scale*(-(fromPoint.getY()+yshift))+((numOutEdges.get(from)-1)*pinabstand/2-(actualOutEdges.get(from))*pinabstand))+"},{"
				// +Math.floor(scale*(toPoint. getX()+xshift)-10)+","
				// +Math.floor(scale*(-(toPoint.
				// getY()+yshift))+((numInEdges.get(to)-1)*pinabstand/2-(actualInEdges.get(to))*pinabstand))+"}}));"
				+ ENDL;
		bea2resultkey.put(bea,
				"'" + from + "'.tokenFlow.outflow[" + (actualOutEdges.get(from).indexOf(bea.getTo()) + 1) + "]");
		// actualInEdges.get(to).add(bea.getTo());
		// actualOutEdges.get(from).add(bea.getFrom());

		return result;
	}

	public Map<BiologicalEdgeAbstract, String> getBea2resultkey() {
		return bea2resultkey;
	}

	private String replaceAll(String function, List<Parameter> params, String nodePrefix, boolean isEdge) {
		String mFunction = replaceParameters(function, params, nodePrefix, isEdge);
		mFunction = replaceNames(mFunction);
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
		final StringBuilder mFunction = new StringBuilder(function);
		// Character c;
		// Character l;
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

		names.sort(new StringLengthComparator());
		for (int i = 0; i < names.size(); i++) {
			// check = false;
			int index = 0;
			String name = names.get(i);
			while (mFunction.indexOf(name, index) >= 0) {
				int idxNew = mFunction.indexOf(name, index);
				if (mFunction.length() >= idxNew + name.length()) {
					Character r;
					if (mFunction.length() > idxNew + name.length()) {
						r = mFunction.charAt(idxNew + name.length());
					} else {
						r = ' ';
					}
					final boolean check = idxNew == 0 || CHARS.contains(mFunction.charAt(idxNew - 1));
					/*
					 * if (idxNew > 0) { if (chars.contains(mFunction.charAt(idxNew - 1))) { check =
					 * true; } } else { check = true; }
					 */
					if (check && CHARS.contains(r)) {
						// mFunction = mFunction.replaceFirst(name, mNames.get(name));
						String insert = "'" + referenceMapping.get(name) + "'.t";
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
		List<String> paramNames = params.stream().map(Parameter::getName).sorted(new StringLengthComparator())
				.collect(Collectors.toList());
		// Character c;
		// Character l;
		for (int i = 0; i < paramNames.size(); i++) {
			int index = 0;
			String name = paramNames.get(i);
			while (mFunction.indexOf(name, index) >= 0) {
				int idxNew = mFunction.indexOf(name, index);
				if (mFunction.length() >= idxNew + name.length()) {
					char r;
					if (mFunction.length() > idxNew + name.length()) {
						r = mFunction.charAt(idxNew + name.length());
					} else {
						// Parameter is last term of function
						r = ' ';
					}
					final boolean check = idxNew == 0 || CHARS.contains(mFunction.charAt(idxNew - 1));
					// if (!Character.isDigit(c) && !Character.isAlphabetic(c))
					// {
					if (check && CHARS.contains(r)) {
						String insert;
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
			return resolveReference(bna.getLogicalReference());
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
