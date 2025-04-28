package io;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
import simulation.ConflictHandling;
import util.StringLengthComparator;
import util.VanesaUtility;

public class MOoutput extends BaseWriter<Pathway> {
	private static final String INDENT = "  ";
	private static final String ENDL = System.lineSeparator();
	private static final Set<Character> CHARS = Set.of('*', '+', '/', '-', '^', '(', ')', ',', ' ');
	private static final String PNlibSettings = "PNlib.Components.Settings";
	private static final String PNlibPlaceDisc = "PNlib.Components.PD";
	private static final String PNlibPlaceCont = "PNlib.Components.PC";
	private static final String PNlibPlaceBiColor = "PNlib.Examples.Models.BicoloredPlaces.CPC";
	private static final String PNlibTransitionDisc = "PNlib.Components.TD";
	private static final String PNlibTransitionCont = "PNlib.Components.TC";
	private static final String PNlibtransitionStoch = "PNlib.Components.TDS";
	private static final String PNlibTransitionBiColor = "PNlib.Examples.Models.BicoloredPlaces.CTC";
	private static final String PNlibIA = "PNlib.Components.IA";
	private static final String PNlibTA = "PNlib.Components.TA";
	private static final String distrPackage = "PNlib.Types.DistributionType.";

	// String builder for test arcs, inhibitory arcs, places, and transitions
	private final StringBuilder componentsSB = new StringBuilder();
	private final StringBuilder parametersSB = new StringBuilder();
	private final StringBuilder edgesSB = new StringBuilder();

	private final Map<String, ArrayList<BiologicalNodeAbstract>> actualInEdges = new HashMap<>();
	private final Map<String, ArrayList<BiologicalNodeAbstract>> actualOutEdges = new HashMap<>();
	private final Map<String, String> inWeights = new HashMap<>();
	private final Map<String, String> outWeights = new HashMap<>();
	private final Map<String, String> inPrio = new HashMap<>();
	private final Map<String, String> inProb = new HashMap<>();
	private final Map<String, String> outPrio = new HashMap<>();
	private final Map<String, String> outProb = new HashMap<>();

	private final Map<BiologicalEdgeAbstract, String> bea2resultkey = new HashMap<>();

	private final Set<BiologicalNodeAbstract> marked;

	private final String modelName;
	private final String packageInfo;
	private final boolean colored;
	private final boolean noIdent = false;
	private final int seed;
	private final int localSeed;

	private double minX = Double.MAX_VALUE;
	private double minY = Double.MAX_VALUE;
	private double maxX = -Double.MAX_VALUE;
	private double maxY = -Double.MAX_VALUE;

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
		localSeed = generateLocalSeed(seed);
		marked = GraphInstance.getMyGraph().getVisualizationViewer().getPickedVertexState().getPicked();
	}

	@Override
	protected void internalWrite(final OutputStream outputStream, final Pathway pathway) throws Exception {
		if (Workspace.getCurrentSettings().isDeveloperMode()) {
			System.out.println("MOoutput(File: " + pathway.getName() + ", Model Name: " + modelName + ")");
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
		sb.append("parameter Integer placeLocalSeed=").append(localSeed).append(";").append(ENDL);
		sb.append("parameter Integer transitionLocalSeed=").append(localSeed).append(";").append(ENDL);
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

	private void buildParameters(final Pathway pw) {
		for (BiologicalNodeAbstract bna : pw.getAllGraphNodesSortedAlphabetically()) {
			if (!bna.isLogical()) {
				buildParametersForNode(pw, bna);
			}
		}
	}

	private void buildParametersForNode(final Pathway pw, final BiologicalNodeAbstract bna) {
		for (final Parameter p : bna.getParametersSortedAlphabetically()) {
			buildParameterForNode("_", bna.getName(), p);
		}
		// functions and parameters at arcs belong to the corresponding transition
		if (bna instanceof Transition) {
			for (final BiologicalEdgeAbstract bea : VanesaUtility.getEdgesSortedByID(
					pw.getGraph().getJungGraph().getIncidentEdges(bna))) {
				for (final Parameter p : bea.getParametersSortedAlphabetically()) {
					buildParameterForNode("__", bna.getName(), p);
				}
			}
		}
	}

	private void buildParameterForNode(final String prefix, final String name, final Parameter p) {
		parametersSB.append(INDENT).append("parameter Real '").append(prefix).append(name).append("_").append(
				p.getName()).append("'");
		if (StringUtils.isNotEmpty(p.getUnit())) {
			parametersSB.append("(final unit=\"").append(p.getUnit()).append("\")");
		}
		parametersSB.append(" = ").append(p.getValue().toPlainString()).append(";").append(ENDL);
	}

	private void buildAllNodes(final Pathway pw) {
		final List<BiologicalNodeAbstract> places = new ArrayList<>();
		final List<BiologicalNodeAbstract> transitions = new ArrayList<>();
		for (final BiologicalNodeAbstract bna : pw.getAllGraphNodesSortedAlphabetically()) {
			if (bna instanceof Place) {
				places.add(bna);
			} else if (bna instanceof Transition) {
				transitions.add(bna);
			}
		}
		buildNodes(pw, places);
		buildNodes(pw, transitions);
	}

	private void buildNodes(final Pathway pw, final List<BiologicalNodeAbstract> nodes) {
		final StringBuilder attr = new StringBuilder();
		for (final BiologicalNodeAbstract bna : nodes) {
			attr.setLength(0);
			if (!bna.isLogical() && bna instanceof PNNode) {
				final String biologicalElement = bna.getBiologicalElement();
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
					attr.append("startTokens=").append((int) place.getTokenStart());
					attr.append(", minTokens=").append((int) place.getTokenMin());
					attr.append(", maxTokens=").append((int) place.getTokenMax());
					if (place.getConflictStrategy() == ConflictHandling.Priority) {
						attr.append(", enablingType=PNlib.Types.EnablingType.Priority");
					} else if (place.getConflictStrategy() == ConflictHandling.Probability) {
						// priority is default in PNLib, therefore, we set probability with default uniform
						// distribution to use random conflict resolution backed by the placeLocalSeed parameter
						attr.append(", enablingType=PNlib.Types.EnablingType.Probability");
					}
					if (place.getConflictingInEdges().size() > 1) {
						if (place.getConflictStrategy() == ConflictHandling.Probability) {
							attr.append(", enablingProbIn={").append(inProb.get(place.getName())).append("}");
						} else if (place.getConflictStrategy() == ConflictHandling.Priority) {
							attr.append(", enablingPrioIn={").append(inPrio.get(place.getName())).append("}");
						}
					}
					if (place.getConflictingOutEdges().size() > 1) {
						if (place.getConflictStrategy() == ConflictHandling.Probability) {
							attr.append(", enablingProbOut={").append(outProb.get(place.getName())).append("}");
						} else if (place.getConflictStrategy() == ConflictHandling.Priority) {
							attr.append(", enablingPrioOut={").append(outPrio.get(place.getName())).append("}");
						}
					}
					// Set a defined seed to ensure deterministic behaviour
					attr.append(", localSeedIn=placeLocalSeed");
					attr.append(", localSeedOut=placeLocalSeed");
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
					attr.append(", minMarks(final unit=\"mmol\")=").append(min);
					attr.append(", maxMarks(final unit=\"mmol\")=").append(max);
					attr.append(", t(final unit=\"mmol\")");
					if (place.getConflictStrategy() == ConflictHandling.Priority) {
						attr.append(", enablingType=PNlib.Types.EnablingType.Priority");
					} else if (place.getConflictStrategy() == ConflictHandling.Probability) {
						// priority is default in PNLib, therefore, we set probability with default uniform
						// distribution to use random conflict resolution backed by the placeLocalSeed parameter
						attr.append(", enablingType=PNlib.Types.EnablingType.Probability");
					}
					if (place.getConflictingInEdges().size() > 1) {
						if (place.getConflictStrategy() == ConflictHandling.Probability) {
							attr.append(", enablingProbIn={").append(inProb.get(place.getName())).append("}");
						} else if (place.getConflictStrategy() == ConflictHandling.Priority) {
							attr.append(", enablingPrioIn={").append(inPrio.get(place.getName())).append("}");
						}
					}
					if (place.getConflictingOutEdges().size() > 1) {
						if (place.getConflictStrategy() == ConflictHandling.Probability) {
							attr.append(", enablingProbOut={").append(outProb.get(place.getName())).append("}");
						} else if (place.getConflictStrategy() == ConflictHandling.Priority) {
							attr.append(", enablingPrioOut={").append(outPrio.get(place.getName())).append("}");
						}
					}
					// Set a defined seed to ensure deterministic behaviour
					attr.append(", localSeedIn=placeLocalSeed");
					attr.append(", localSeedOut=placeLocalSeed");
				} else if (biologicalElement.equals(Elementdeclerations.stochasticTransition)) {
					final StochasticTransition st = (StochasticTransition) bna;
					attr.append("distributionType=").append(distrPackage);
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
					attr.append(", h=").append(st.getH());
					attr.append(", a=").append(st.getA());
					attr.append(", b=").append(st.getB());
					attr.append(", c=").append(st.getC());
					attr.append(", mu=").append(st.getMu());
					attr.append(", sigma=").append(st.getSigma());
					attr.append(", E={");
					attr.append(st.getEvents().stream().map(String::valueOf).collect(Collectors.joining(", ")));
					attr.append("}, P={");
					attr.append(st.getProbabilities().stream().map(String::valueOf).collect(Collectors.joining(", ")));
					attr.append("}");
					attr.append(", localSeed=transitionLocalSeed");
				} else if (biologicalElement.equals(Elementdeclerations.discreteTransition)) {
					final DiscreteTransition dt = (DiscreteTransition) bna;
					final String delay = replaceAll(dt.getDelay(), dt.getParameters(), dt.getName(), false);
					attr.append("delay=").append(delay);
				} else if (biologicalElement.equals(Elementdeclerations.continuousTransition)) {
					final ContinuousTransition ct = (ContinuousTransition) bna;
					final String speed = replaceAll(ct.getMaximalSpeed(), ct.getParameters(), ct.getName(), false);
					attr.append("maximumSpeed=").append(speed);
				}
				// Add firing condition for transitions
				if (bna instanceof Transition) {
					final Transition t = (Transition) bna;
					if (t.isKnockedOut()) {
						attr.append(", firingCon=false");
					} else {
						final String firingCondition = t.getFiringCondition();
						if (firingCondition.length() > 0) {
							attr.append(", firingCon=").append(firingCondition);
						}
					}
				}
				componentsSB.append(getTransitionString(pw, bna, getModelicaString(bna), attr, in, out));
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
						final int prio = e.getPriority();
						if (outPrio.containsKey(fromName)) {
							outPrio.put(fromName, outPrio.get(fromName) + ", " + prio);
						} else {
							outPrio.put(fromName, String.valueOf(prio));
						}
						final double prob = e.getProbability();
						if (outProb.containsKey(fromName)) {
							outProb.put(fromName, outProb.get(fromName) + ", " + prob);
						} else {
							outProb.put(fromName, String.valueOf(prob));
						}
					}

					// Edge Transition -> Place
				} else {
					if (!(e.getFrom() instanceof ContinuousTransition)) {
						final int prio = e.getPriority();
						if (inPrio.containsKey(toName)) {
							inPrio.put(toName, inPrio.get(toName) + ", " + prio);
						} else {
							inPrio.put(toName, String.valueOf(prio));
						}
						final double prob = e.getProbability();
						if (inProb.containsKey(toName)) {
							inProb.put(toName, inProb.get(toName) + ", " + prob);
						} else {
							inProb.put(toName, String.valueOf(prob));
						}
					}

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
						componentsSB.append(createInhibitoryArc(pw, fromName, toName, e));
					}
				} else if (e.getBiologicalElement().equals(Elementdeclerations.pnTestArc)) {
					if (e.getFrom() instanceof Place) {
						componentsSB.append(createTestArc(pw, fromName, toName, e));
					}
				} else if (e.getFrom() instanceof Place) {
					if (e.getFrom().isConstant()) {
						componentsSB.append(createTestArc(pw, fromName, toName, e));
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
		edgesSB.append(INDENT + "connect(inhibitorArc").append(inhibitCount).append(".outTransition,'").append(toString)
				.append("'.inPlaces[").append(actualInEdges.get(toString).indexOf(e.getFrom()) + 1).append("]) ")
				.append(getFromToAnnotation(pw, e.getFrom(), e.getTo())).append(";").append(ENDL);
		return result;
	}

	private String createTestArc(Pathway pw, String fromString, String toString, PNArc e) {
		testArcCount++;
		String result = INDENT + PNlibTA + " testArc" + testArcCount + "(testValue=" + getModelicaEdgeFunction(e) + ");"
				+ ENDL;
		edgesSB.append(INDENT + "connect('").append(fromString).append("'.outTransition[").append(
				actualOutEdges.get(fromString).indexOf(e.getTo()) + 1).append("],").append("testArc").append(
				testArcCount).append(".inPlace);").append(ENDL);
		edgesSB.append(INDENT + "connect(testArc").append(testArcCount).append(".outTransition,'").append(toString)
				.append("'.inPlaces[").append(actualInEdges.get(toString).indexOf(e.getFrom()) + 1).append("]) ")
				.append(getFromToAnnotation(pw, e.getFrom(), e.getTo())).append(";").append(ENDL);
		return result;
	}

	private String getTransitionString(final Pathway pw, final BiologicalNodeAbstract bna, final String element,
			final StringBuilder attr, final int inEdges, final int outEdges) {
		final StringBuilder result = new StringBuilder();
		result.append(INDENT).append(element).append(" '").append(bna.getName()).append("'(");
		result.append("nIn=").append(inEdges);
		result.append(", nOut=").append(outEdges);
		result.append(", ").append(attr);
		final String inNumbers = inWeights.get(bna.getName());
		if (inNumbers != null) {
			result.append(", arcWeightIn={").append(inNumbers).append("}");
		}
		final String outNumbers = outWeights.get(bna.getName());
		if (outNumbers != null) {
			result.append(", arcWeightOut={").append(outNumbers).append("}");
		}
		result.append(") ").append(getPlacementAnnotation(pw, bna)).append(";").append(ENDL);
		return result.toString();
	}

	private String getConnectionStringTP(Pathway pw, String from, String to, BiologicalEdgeAbstract bea) {
		String result = INDENT + "connect('" + from + "'.outPlaces[" + (actualOutEdges.get(from).indexOf(bea.getTo())
				+ 1) + "], '" + to + "'.inTransition[" + (actualInEdges.get(to).indexOf(bea.getFrom()) + 1) + "]) "
				+ getFromToAnnotation(pw, bea.getFrom(), bea.getTo()) + ";" + ENDL;
		bea2resultkey.put(bea, "'" + to + "'.tokenFlow.inflow[" + (actualInEdges.get(to).indexOf(bea.getFrom()) + 1)
				+ "]");
		return result;
	}

	private String getConnectionStringPT(Pathway pw, String from, String to, BiologicalEdgeAbstract bea) {
		String result = INDENT + "connect('" + from + "'.outTransition[" + (actualOutEdges.get(from).indexOf(
				bea.getTo()) + 1) + "], '" + to + "'.inPlaces[" + (actualInEdges.get(to).indexOf(bea.getFrom()) + 1)
				+ "]) " + getFromToAnnotation(pw, bea.getFrom(), bea.getTo()) + ";" + ENDL;
		bea2resultkey.put(bea, "'" + from + "'.tokenFlow.outflow[" + (actualOutEdges.get(from).indexOf(bea.getTo()) + 1)
				+ "]");
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

	private String getPlacementAnnotation(final Pathway pw, final BiologicalNodeAbstract bna) {
		final double x = pw.getGraph().getVertexLocation(bna).getX();
		final double y = -pw.getGraph().getVertexLocation(bna).getY();
		minX = Math.min(minX, x);
		maxX = Math.max(maxX, x);
		minY = Math.min(minY, y);
		maxY = Math.max(maxY, y);
		return "annotation(Placement(visible=true, transformation(origin={" + x + "," + y
				+ "}, extent={{-20, -20}, {20, 20}}, rotation=0)))";
	}

	private String getFromToAnnotation(Pathway pw, BiologicalNodeAbstract from, BiologicalNodeAbstract to) {
		final Point2D p1 = pw.getGraph().getVertexLocation(resolveReference(from));
		final Point2D p2 = pw.getGraph().getVertexLocation(resolveReference(to));
		String color = from.isLogical() || to.isLogical() ? "{180, 180, 180}" : "{0, 0, 0}";
		double shiftFrom = 10;
		if (Elementdeclerations.continuousPlace.equals(from.getBiologicalElement())) {
			shiftFrom = 25;
		}
		double shiftTo = -10;
		if (Elementdeclerations.continuousPlace.equals(to.getBiologicalElement())) {
			shiftTo = -25;
		}
		return "annotation(Line(color=" + color + ", points={{" + (p1.getX() + shiftFrom) + ", " + (-p1.getY()) + "}, {"
				+ (p2.getX() + shiftTo) + ", " + (-p2.getY()) + "}}))";
	}

	private String getModelicaString(BiologicalNodeAbstract bna) {
		if (bna instanceof ContinuousTransition) {
			if (colored) {
				return PNlibTransitionBiColor;
			}
			return PNlibTransitionCont;
		}
		if (bna instanceof DiscreteTransition) {
			return PNlibTransitionDisc;
		}
		if (bna instanceof StochasticTransition) {
			return PNlibtransitionStoch;
		}
		if (bna instanceof Place) {
			if (bna.isDiscrete()) {
				return PNlibPlaceDisc;
			}
			if (colored) {
				return PNlibPlaceBiColor;
			}
			return PNlibPlaceCont;
		}
		return null;
	}

	private String getModelicaEdgeFunction(PNArc bea) {
		if (bea.isRegularArc()) {
			if (bea.getTo().isConstant() && bea.getTo() instanceof Place) {
				return "0";
			}
			if (bea.getTo().isLogical() && bea.getTo().getLogicalReference().isConstant() && bea.getTo()
					.getLogicalReference() instanceof Place) {
				return "0";
			}
			if (bea.getFrom().isConstant() && bea.getFrom() instanceof Place) {
				if (bea.getFrom() instanceof DiscretePlace) {
					return "max(0," + replaceNames(bea.getFunction() + "-1)");
				}
				return "0";
			}
			if (bea.getFrom().isLogical() && bea.getFrom().getLogicalReference().isConstant() && bea.getFrom()
					.getLogicalReference() instanceof Place) {
				if (bea.getFrom().getLogicalReference() instanceof DiscretePlace) {
					return "max(0," + replaceNames(bea.getFunction() + "-1)");
				}
				return "0";
			}
		}
		if (bea.getTo() instanceof Transition) {
			return replaceAll(bea.getFunction(), bea.getParameters(), bea.getTo().getName(), true);
		}
		return replaceAll(bea.getFunction(), bea.getParameters(), bea.getFrom().getName(), true);
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
					char r;
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
		List<String> paramNames = params.stream().map(Parameter::getName).sorted(new StringLengthComparator()).collect(
				Collectors.toList());
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
		final Set<BiologicalNodeAbstract> set = new HashSet<>();
		for (final BiologicalEdgeAbstract bea : GraphInstance.getMyGraph().getJungGraph().getInEdges(bna)) {
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
		final StringBuilder builder = new StringBuilder();
		builder.append("function g1").append(ENDL);
		builder.append("    input Real[2] inColors;").append(ENDL);
		builder.append("    output Real[2] outWeights;").append(ENDL);
		builder.append("  algorithm").append(ENDL);
		builder.append("    if sum(inColors) < 1e-12 then").append(ENDL);
		builder.append("      outWeights := fill(1, 2);").append(ENDL);
		builder.append("    else").append(ENDL);
		builder.append("      outWeights[1] := inColors[1] / sum(inColors);").append(ENDL);
		builder.append("      outWeights[2] := inColors[2] / sum(inColors);").append(ENDL);
		builder.append("    end if;").append(ENDL);
		builder.append("  end g1;").append(ENDL);
		builder.append("function g2").append(ENDL);
		builder.append("    input Real[2] inColors1;").append(ENDL);
		builder.append("    input Real[2] inColors2;").append(ENDL);
		builder.append("    output Real[2] outWeights;").append(ENDL);
		builder.append("  algorithm").append(ENDL);
		builder.append("    if sum(inColors1) < 1e-12 then").append(ENDL);
		builder.append("      outWeights := fill(0.5, 2);").append(ENDL);
		builder.append("    else").append(ENDL);
		builder.append("      outWeights[1] := inColors1[1] / sum(inColors1) / 2;").append(ENDL);
		builder.append("      outWeights[2] := inColors1[2] / sum(inColors1) / 2;").append(ENDL);
		builder.append("    end if;").append(ENDL);
		builder.append(ENDL);
		builder.append("    if sum(inColors2) < 1e-12 then").append(ENDL);
		builder.append("      outWeights[1] := outWeights[1] + 0.5;").append(ENDL);
		builder.append("      outWeights[2] := outWeights[2] + 0.5;").append(ENDL);
		builder.append("    else").append(ENDL);
		builder.append("      outWeights[1] := outWeights[1] + inColors2[1] / sum(inColors2) / 2;").append(ENDL);
		builder.append("      outWeights[2] := outWeights[2] + inColors2[2] / sum(inColors2) / 2;").append(ENDL);
		builder.append("    end if;").append(ENDL);
		builder.append("  end g2;").append(ENDL);
		return builder.toString();
	}

	public static int generateLocalSeed(final int seed) {
		return (int) ((((long) Integer.MAX_VALUE) - seed) % 45849);
	}
}
