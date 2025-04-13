package simulation;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.edges.petriNet.PNArc;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.petriNet.DiscretePlace;
import biologicalObjects.nodes.petriNet.DiscreteTransition;
import biologicalObjects.nodes.petriNet.StochasticTransition;
import biologicalObjects.nodes.petriNet.Transition;
import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.parser.ParseException;
import graph.gui.Parameter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simulator for extended, timed, functional, stochastic, discrete Petri Nets with capacities.
 * <hr>
 * For each marking of the net, all transitions are evaluated for concession, by...
 * <ul>
 *     <li>...checking if the transition is not knocked out</li>
 *     <li>...checking if the transition's firingCondition evaluates to true</li>
 *     <li>...checking if places connected via test arcs hold equal or more tokens than evaluated by the arc's function</li>
 *     <li>...checking if places connected via inhibitor arcs hold less tokens than evaluated by the arc's function</li>
 *     <li>
 *         ...checking if source places connected via regular arcs hold equal or more tokens than evaluated by the arc's
 *         function and that the removal of tokens doesn't violate the place's <em>[minTokens, maxTokens]</em> range,
 *         if the place is not constant.
 *     </li>
 *     <li>
 *         ...checking if the addition of tokens evaluated by the arc's function to target places don't violate the
 *         place's <em>[minTokens, maxTokens]</em>, if the place is not constant.
 *     </li>
 * </ul>
 * <hr>
 * Functions for arc weights, delays, and firingConditions are evaluated with the following parameters:
 * <ul>
 *     <li>User-defined parameters of the arc/transition in the case of arc weights and delays</li>
 *     <li>The <em>time</em> parameter for transition firingConditions</li>
 *     <li>The number of tokens for each place in the current marking using the place's name as parameter key</li>
 * </ul>
 * <hr>
 * The process of simulation is implemented as follows:
 * <ul>
 *     <li>Initialize start marking using tokenStart property of places and the defined start time (default: 0)</li>
 *     <li>
 *         Find all transitions that have concession in the current marking with the same, smallest delay. The delay is
 *         determined either by the delay function of discrete transitions or the delay distribution of stochastic
 *         transitions.
 *     </li>
 *     <li>
 *         Select one of these transitions at random and fire it. When branching is allowed, each of them is fired
 *         creating branching markings.
 *     </li>
 *     <li>
 *         Firing a transition removes the requested tokens from the source places that are not constant and connected
 *         via regular arcs and produces tokens in the target places that are not constant and connected via regular
 *         arcs.
 *     </li>
 *     <li>
 *         After a transition fired, the new marking's time is advanced by the transition's delay. All previous
 *         concessions are retained if they still have concession and their delays are advanced as well. Those that
 *         don't have concession anymore are removed. Transitions that newly gained concession are added.
 *     </li>
 * </ul>
 * <hr>
 * General notes:
 * <ul>
 *     <li>Transitions without any arcs still receive concession and fire</li>
 * </ul>
 * <hr>
 * TODO:
 * <ul>
 *     <li>Evaluate time constraints of firingConditions and prevent jumping over emerging concessions based on time</li>
 *     <li>Handle conflict resolution strategy of places and arc priorities/probabilities</li>
 * </ul>
 */
public class DiscreteSimulator extends Simulator {
	private final Map<DiscretePlace, Integer> placesOrder = new HashMap<>();
	private final Map<DiscretePlace, PlaceDetails> places = new HashMap<>();
	private final Map<Transition, TransitionDetails> transitions = new HashMap<>();
	private final List<Marking> markings = Collections.synchronizedList(new ArrayList<>());
	private final List<Marking> openMarkings = new ArrayList<>();
	private final List<FiringEdge> firingEdges = Collections.synchronizedList(new ArrayList<>());
	private final Map<Marking, List<FiringEdge>> outEdges = new ConcurrentHashMap<>();
	private final Map<Marking, List<FiringEdge>> inEdges = new ConcurrentHashMap<>();

	public DiscreteSimulator(final Pathway pathway) throws SimulationException {
		this(pathway, 42, false);
	}

	public DiscreteSimulator(final Pathway pathway, final long seed) throws SimulationException {
		this(pathway, seed, false);
	}

	public DiscreteSimulator(final Pathway pathway, final long seed, final boolean allowBranching)
			throws SimulationException {
		this(pathway.getAllGraphNodes(), pathway.getAllEdges(), seed, allowBranching);
	}

	public DiscreteSimulator(final Collection<BiologicalNodeAbstract> nodes,
			final Collection<BiologicalEdgeAbstract> edges) throws SimulationException {
		this(nodes, edges, 42, false);
	}

	public DiscreteSimulator(final Collection<BiologicalNodeAbstract> nodes,
			final Collection<BiologicalEdgeAbstract> edges, final long seed) throws SimulationException {
		this(nodes, edges, seed, false);
	}

	public DiscreteSimulator(final Collection<BiologicalNodeAbstract> nodes,
			final Collection<BiologicalEdgeAbstract> edges, final long seed, final boolean allowBranching)
			throws SimulationException {
		super(seed, allowBranching);
		// Collect places and transitions
		for (final var node : nodes) {
			if (node instanceof DiscretePlace) {
				final DiscretePlace p = (DiscretePlace) node;
				places.put(p, new PlaceDetails(p));
				placesOrder.put(p, placesOrder.size());
			} else if (node instanceof DiscreteTransition) {
				final DiscreteTransition t = (DiscreteTransition) node;
				transitions.put(t, new TransitionDetails(t, random));
			} else if (node instanceof StochasticTransition) {
				final StochasticTransition t = (StochasticTransition) node;
				transitions.put(t, new TransitionDetails(t, random));
			} else {
				throw new SimulationException(String.format("Petri net is not fully discrete. Found node of type '%s'",
						node.getClass().getSimpleName()));
			}
		}
		// Collect all transition source and target places
		for (final var edge : edges) {
			if (edge instanceof PNArc) {
				final PNArc arc = (PNArc) edge;
				if (arc.getFrom() instanceof DiscretePlace && (arc.getTo() instanceof DiscreteTransition
						|| edge.getTo() instanceof StochasticTransition)) {
					transitions.get((Transition) arc.getTo()).sources.add(arc);
					if (arc.isRegularArc()) {
						places.get((DiscretePlace) arc.getFrom()).outputProbabilitiesNormalized.put(arc,
								BigDecimal.valueOf(arc.getProbability()).max(BigDecimal.ZERO));
					}
				} else if (
						(arc.getFrom() instanceof DiscreteTransition || arc.getFrom() instanceof StochasticTransition)
								&& arc.getTo() instanceof DiscretePlace) {
					transitions.get((Transition) arc.getFrom()).targets.add(arc);
					if (arc.isRegularArc()) {
						places.get((DiscretePlace) arc.getTo()).inputProbabilitiesNormalized.put(arc,
								BigDecimal.valueOf(arc.getProbability()).max(BigDecimal.ZERO));
					}
				}
			} else {
				throw new SimulationException(String.format("Petri net is not fully discrete. Found edge of type '%s'",
						edge.getClass().getSimpleName()));
			}
		}
		// Normalize place probabilities
		for (final var place : places.values()) {
			place.normalizeProbabilities();
		}
		initialize(BigDecimal.ZERO);
	}

	private void initialize(final BigDecimal startTime) throws SimulationException {
		random.setSeed(seed);
		markings.clear();
		openMarkings.clear();
		firingEdges.clear();
		// Create the initial marking and store it in the graph as well as the open list
		final var placeTokens = new BigInteger[places.size()];
		for (final var place : places.keySet()) {
			final int placeIndex = placesOrder.get(place);
			placeTokens[placeIndex] = BigInteger.valueOf((long) place.getTokenStart());
		}
		final var startMarking = new Marking(startTime, placeTokens,
				determineConcession(startTime, placeTokens, new HashMap<>()));
		markings.add(startMarking);
		openMarkings.add(startMarking);
	}

	private Concession[] determineConcession(final BigDecimal time, final BigInteger[] placeTokens,
			final Map<PNArc, BigInteger> fixedArcWeights) throws SimulationException {
		final List<Concession> concessions = new ArrayList<>();
		for (final var transition : transitions.values()) {
			if (transition.transition.isKnockedOut()) {
				continue;
			}
			if (StringUtils.isNotBlank(transition.firingCondition)) {
				if ("false".equalsIgnoreCase(transition.firingCondition)) {
					continue;
				} else if (!"true".equalsIgnoreCase(transition.firingCondition)) {
					final var firingConditionExpression = createExpression(placesOrder, placeTokens,
							transition.firingCondition, transition.transition.getParameters());
					firingConditionExpression.with("time", time);
					try {
						if (!firingConditionExpression.evaluate().getBooleanValue()) {
							continue;
						}
					} catch (EvaluationException | ParseException e) {
						throw new SimulationException(String.format(
								"Failed to evaluate firingCondition function for transition '%s' (%s): %s",
								transition.transition.getName(), transition.transition.getLabel(),
								transition.firingCondition), e);
					}
				}
			}
			// Collect the subset of connected places so we don't copy the whole net's marking each time
			final Map<DiscretePlace, BigInteger> putativeTokensMap = new HashMap<>();
			for (final var arc : transition.sources) {
				final DiscretePlace place = (DiscretePlace) arc.getFrom();
				final int placeIndex = placesOrder.get(place);
				putativeTokensMap.put(place, placeTokens[placeIndex]);
			}
			for (final var arc : transition.targets) {
				final DiscretePlace place = (DiscretePlace) arc.getTo();
				final int placeIndex = placesOrder.get(place);
				putativeTokensMap.put(place, placeTokens[placeIndex]);
			}
			boolean valid = true;
			// Validate pre-conditions (test and inhibition arcs or constant places)
			for (final var arc : transition.sources) {
				if (arc.isRegularArc() && !arc.getFrom().isConstant()) {
					continue;
				}
				final DiscretePlace place = (DiscretePlace) arc.getFrom();
				BigInteger requestedTokens = fixedArcWeights.get(arc);
				if (requestedTokens == null) {
					requestedTokens = evaluateFunction(placeTokens, arc, arc.getFunction(), arc.getParameters());
				}
				if (requestedTokens.signum() < 0) {
					valid = false;
					break;
				}
				if (putativeTokensMap.get(place).compareTo(requestedTokens) >= 0) {
					// If enough tokens are available and the arc is an inhibitor arc, validation fails
					if (arc.isInhibitorArc()) {
						valid = false;
						break;
					}
				} else {
					// If not enough tokens are available and the arc is either a test arc or a regular arc and the
					// connected place constant, validation fails
					if (arc.isTestArc() || (arc.isRegularArc() && arc.getFrom().isConstant())) {
						valid = false;
						break;
					}
				}
			}
			// Validate normal incoming arcs
			for (final var arc : transition.sources) {
				if (!arc.isRegularArc() || arc.getFrom().isConstant()) {
					continue;
				}
				final DiscretePlace place = (DiscretePlace) arc.getFrom();
				BigInteger requestedTokens = fixedArcWeights.get(arc);
				if (requestedTokens == null) {
					requestedTokens = evaluateFunction(placeTokens, arc, arc.getFunction(), arc.getParameters());
				}
				if (requestedTokens.signum() < 0) {
					valid = false;
					break;
				}
				final BigInteger newTokens = putativeTokensMap.get(place).subtract(requestedTokens);
				final BigInteger minTokens = BigInteger.valueOf((long) place.getTokenMin());
				final BigInteger maxTokens = BigInteger.valueOf((long) place.getTokenMax());
				if (newTokens.compareTo(minTokens) >= 0 && newTokens.compareTo(maxTokens) <= 0) {
					putativeTokensMap.put(place, newTokens);
				} else {
					valid = false;
					break;
				}
			}
			if (valid) {
				// Validate normal outgoing arcs
				for (final var arc : transition.targets) {
					if (!arc.isRegularArc() || arc.getTo().isConstant()) {
						continue;
					}
					final DiscretePlace place = (DiscretePlace) arc.getTo();
					BigInteger producedTokens = fixedArcWeights.get(arc);
					if (producedTokens == null) {
						producedTokens = evaluateFunction(placeTokens, arc, arc.getFunction(), arc.getParameters());
					}
					if (producedTokens.signum() < 0) {
						valid = false;
						break;
					}
					final BigInteger newTokens = putativeTokensMap.get(place).add(producedTokens);
					final BigInteger minTokens = BigInteger.valueOf((long) place.getTokenMin());
					final BigInteger maxTokens = BigInteger.valueOf((long) place.getTokenMax());
					if (newTokens.compareTo(minTokens) >= 0 && newTokens.compareTo(maxTokens) <= 0) {
						putativeTokensMap.put(place, newTokens);
					} else {
						valid = false;
						break;
					}
				}
				if (valid) {
					// If all validations succeeded, evaluate the transition's delay and store the concession if the
					// delay is non-negative
					final BigDecimal delay = transition.getDelay(placesOrder, placeTokens);
					if (delay.signum() >= 0) {
						concessions.add(new Concession(transition, delay));
					}
				}
			}
		}
		return concessions.stream().sorted(Comparator.comparing(o -> o.delay)).toArray(Concession[]::new);
	}

	private static VanesaExpression createExpression(final Map<DiscretePlace, Integer> placesOrder,
			final BigInteger[] placeTokens, final String function, final List<Parameter> parameters) {
		final var expression = new VanesaExpression(function).with(parameters);
		for (final var place : placesOrder.keySet()) {
			expression.with(place.getName(), placeTokens[placesOrder.get(place)]);
		}
		return expression;
	}

	private BigInteger evaluateFunction(final BigInteger[] placeTokens, final PNArc arc, final String function,
			final List<Parameter> parameters) throws SimulationException {
		final var expression = createExpression(placesOrder, placeTokens, function, parameters);
		try {
			final var result = expression.evaluate();
			final BigDecimal producedTokens = result.getNumberValue();
			// Truncate non-integer result
			return producedTokens.toBigInteger();
		} catch (EvaluationException | ParseException e) {
			throw new SimulationException(
					String.format("Failed to evaluate function for arc '%s' (%s) from '%s' (%s) to '%s' (%s): %s",
							arc.getName(), arc.getLabel(), arc.getFrom().getName(), arc.getFrom().getLabel(),
							arc.getTo().getName(), arc.getTo().getLabel(), function), e);
		}
	}

	public void simulateUntil(final BigDecimal endTime) throws SimulationException {
		while (!isDead()) {
			step(endTime);
		}
	}

	public BigDecimal getMaxTime() {
		BigDecimal result = markings.get(0).time;
		for (final var marking : markings) {
			if (marking.time.compareTo(result) > 0) {
				result = marking.time;
			}
		}
		return result;
	}

	public void step() throws SimulationException {
		step(null);
	}

	public void step(final BigDecimal endTime) throws SimulationException {
		if (openMarkings.isEmpty()) {
			return;
		}
		final var markingsToProcess = new ArrayList<>(openMarkings);
		openMarkings.clear();
		for (final var marking : markingsToProcess) {
			if (marking.isDead()) {
				continue;
			}
			// Determine which transitions should fire next based on the smallest delay
			final BigDecimal minDelay = marking.concessionsOrderedByDelay[0].delay;
			int maxFireIndex = 0;
			for (int i = 1; i < marking.concessionsOrderedByDelay.length; i++) {
				if (marking.concessionsOrderedByDelay[i].delay.compareTo(minDelay) > 0) {
					break;
				}
				maxFireIndex = i;
			}
			if (minDelay.compareTo(BigDecimal.ZERO) > 0) {
				// Fix arc weights for next batch of transitions, so that all transitions that fire in parallel
				// (but sequential in this implementation) reference the correct place token counts of the current
				// marking.
				for (int i = 0; i < maxFireIndex + 1; i++) {
					final var concession = marking.concessionsOrderedByDelay[i];
					for (final var arc : concession.transition.sources) {
						final var tokens = evaluateFunction(marking.placeTokens, arc, arc.getFunction(),
								arc.getParameters());
						concession.fixedArcWeights.put(arc, tokens);
					}
					for (final var arc : concession.transition.targets) {
						final var tokens = evaluateFunction(marking.placeTokens, arc, arc.getFunction(),
								arc.getParameters());
						concession.fixedArcWeights.put(arc, tokens);
					}
				}
			}

			if (allowBranching) {
				// Explore all possible branches
				// TODO parallelize: IntStream.range(0, maxFireIndex + 1).parallel().forEach(i -> { ... });
				for (int i = 0; i < maxFireIndex + 1; i++) {
					final var concession = marking.concessionsOrderedByDelay[i];
					if (endTime == null || marking.time.add(concession.delay).compareTo(endTime) <= 0) {
						fireTransition(marking, concession, concession.delay);
					}
				}
			} else {
				// TODO: determine output- and input-conflicts and fast-forward all allowed transitions based on
				//  priority or probability
				final Map<DiscretePlace, Set<Pair<PNArc, Concession>>> outputConflicts = new HashMap<>();
				final Map<DiscretePlace, Set<Pair<PNArc, Concession>>> inputConflicts = new HashMap<>();
				for (int i = 0; i < maxFireIndex + 1; i++) {
					final var concession = marking.concessionsOrderedByDelay[i];
					for (final var edge : concession.transition.sources) {
						if (edge.isRegularArc() && !edge.getFrom().isConstant()) {
							outputConflicts.computeIfAbsent((DiscretePlace) edge.getFrom(), p -> new HashSet<>()).add(
									new ImmutablePair<>(edge, concession));
						}
					}
					for (final var edge : concession.transition.targets) {
						if (edge.isRegularArc() && !edge.getTo().isConstant()) {
							inputConflicts.computeIfAbsent((DiscretePlace) edge.getTo(), p -> new HashSet<>()).add(
									new ImmutablePair<>(edge, concession));
						}
					}
				}

				final Set<DiscretePlace> conflictedOutputPlaces = new HashSet<>();
				final Set<DiscretePlace> conflictedInputPlaces = new HashSet<>();
				for (final var place : outputConflicts.keySet()) {
					final var concessions = outputConflicts.get(place);
					if (concessions.size() > 1) {
						final int placeIndex = placesOrder.get(place);
						final BigInteger minTokens = BigInteger.valueOf((long) place.getTokenMin());
						BigInteger putativeTokens = marking.placeTokens[placeIndex];
						for (final var concession : concessions) {
							final var arc = concession.getLeft();
							final var requestedTokens = concession.getRight().fixedArcWeights.get(arc);
							putativeTokens = putativeTokens.subtract(requestedTokens);
							if (putativeTokens.compareTo(minTokens) < 0) {
								conflictedOutputPlaces.add(place);
								break;
							}
						}
					}
				}
				for (final var place : inputConflicts.keySet()) {
					final var concessions = inputConflicts.get(place);
					if (concessions.size() > 1) {
						final int placeIndex = placesOrder.get(place);
						final BigInteger maxTokens = BigInteger.valueOf((long) place.getTokenMax());
						BigInteger putativeTokens = marking.placeTokens[placeIndex];
						for (final var concession : concessions) {
							final var arc = concession.getLeft();
							final var requestedTokens = concession.getRight().fixedArcWeights.get(arc);
							putativeTokens = putativeTokens.add(requestedTokens);
							if (putativeTokens.compareTo(maxTokens) > 0) {
								conflictedInputPlaces.add(place);
								break;
							}
						}
					}
				}
				// if (conflictedOutputPlaces.size() > 0) {
				// 	System.out.println(conflictedOutputPlaces.size() + " place(s) with output conflicts");
				// }
				// if (conflictedInputPlaces.size() > 0) {
				// 	System.out.println(conflictedInputPlaces.size() + " place(s) with input conflicts");
				// }

				// Explore only one random branch
				final int branchIndex = random.nextInt(maxFireIndex + 1);
				final var concession = marking.concessionsOrderedByDelay[branchIndex];
				if (endTime == null || marking.time.add(concession.delay).compareTo(endTime) <= 0) {
					fireTransition(marking, concession, concession.delay);
				}
			}
		}
	}

	private Marking fireTransition(final Marking marking, final Concession concession, final BigDecimal delay)
			throws SimulationException {
		final TransitionDetails transition = concession.transition;
		final BigDecimal newTime = marking.time.add(delay);
		final BigInteger[] placeTokens = new BigInteger[places.size()];
		System.arraycopy(marking.placeTokens, 0, placeTokens, 0, placeTokens.length);
		for (final var arc : transition.sources) {
			// Test and inhibition arcs as well as constant places don't destroy tokens
			if (!arc.isRegularArc() || arc.getFrom().isConstant()) {
				continue;
			}
			final DiscretePlace place = (DiscretePlace) arc.getFrom();
			final int placeIndex = placesOrder.get(place);
			final var requestedTokens = concession.fixedArcWeights.get(arc);
			placeTokens[placeIndex] = placeTokens[placeIndex].subtract(requestedTokens);
		}
		for (final var arc : transition.targets) {
			// In the malformed case of non-regular arcs and constant places don't produce tokens
			if (!arc.isRegularArc() || arc.getTo().isConstant()) {
				continue;
			}
			final DiscretePlace place = (DiscretePlace) arc.getTo();
			final int placeIndex = placesOrder.get(place);
			final var producedTokens = concession.fixedArcWeights.get(arc);
			placeTokens[placeIndex] = placeTokens[placeIndex].add(producedTokens);
		}
		// Determine new markings concessions and delays considering previous concession delays
		final Map<PNArc, BigInteger> fixedArcWeights = new HashMap<>();
		for (final var nextConcession : marking.concessionsOrderedByDelay) {
			if (nextConcession.transition != transition) {
				fixedArcWeights.putAll(nextConcession.fixedArcWeights);
			}
		}
		final Concession[] newConcessions = determineConcession(newTime, placeTokens, fixedArcWeights);
		final List<Concession> concessions = new ArrayList<>();
		// First, retain all transitions that still have concession and reduce their delay
		for (final var nextConcession : marking.concessionsOrderedByDelay) {
			if (nextConcession.transition != transition) {
				for (final var checkConcession : newConcessions) {
					if (nextConcession.transition.equals(checkConcession.transition)) {
						concessions.add(nextConcession.retain(delay));
						break;
					}
				}
			}
		}
		// Second, add all new concessions
		for (final var nextConcession : newConcessions) {
			boolean alreadyPresent = false;
			for (final var checkConcession : concessions) {
				if (nextConcession.transition.equals(checkConcession.transition)) {
					alreadyPresent = true;
					break;
				}
			}
			if (!alreadyPresent) {
				concessions.add(nextConcession);
			}
		}
		// Create the new marking
		final var newMarking = new Marking(newTime, placeTokens,
				concessions.stream().sorted(Comparator.comparing(o -> o.delay)).toArray(Concession[]::new));
		markings.add(newMarking);
		if (!newMarking.isDead()) {
			openMarkings.add(newMarking);
		}
		final var edge = new FiringEdge(marking, newMarking, transition);
		firingEdges.add(edge);
		outEdges.computeIfAbsent(marking, m -> Collections.synchronizedList(new ArrayList<>())).add(edge);
		inEdges.computeIfAbsent(newMarking, m -> Collections.synchronizedList(new ArrayList<>())).add(edge);
		return newMarking;
	}

	public Collection<DiscretePlace> getPlaces() {
		return places.keySet();
	}

	public Collection<Transition> getTransitions() {
		return transitions.keySet();
	}

	public Collection<Marking> getMarkings() {
		return markings;
	}

	public Marking getStartMarking() {
		return markings.get(0);
	}

	public BigInteger getTokens(final Marking marking, final DiscretePlace place) {
		return marking.placeTokens[placesOrder.get(place)];
	}

	public Collection<FiringEdge> getEdges() {
		return firingEdges;
	}

	public boolean isDead() {
		return openMarkings.isEmpty();
	}

	public long getSeed() {
		return seed;
	}

	public boolean isAllowBranching() {
		return allowBranching;
	}

	/**
	 * Return the primary marking timeline without branches (if activated)
	 */
	public Marking[] getMarkingTimeline() {
		final List<Marking> markings = new ArrayList<>();
		markings.add(getStartMarking());
		boolean foundNext = true;
		while (foundNext) {
			final var edges = outEdges.get(markings.get(markings.size() - 1));
			foundNext = edges != null;
			if (edges != null) {
				markings.add(edges.get(0).to);
			}
		}
		return markings.toArray(new Marking[0]);
	}

	public List<Marking[]> getAllMarkingTimelines() {
		final List<Marking> endMarkings = new ArrayList<>();
		for (final var marking : markings) {
			if (outEdges.get(marking) == null) {
				endMarkings.add(marking);
			}
		}
		final List<Marking[]> result = new ArrayList<>();
		for (final var endMarking : endMarkings) {
			final List<Marking> timeline = new ArrayList<>();
			timeline.add(endMarking);
			boolean foundNext = true;
			while (foundNext) {
				final var edges = inEdges.get(timeline.get(0));
				foundNext = edges != null;
				if (edges != null) {
					timeline.add(0, edges.get(0).from);
				}
			}
			result.add(timeline.toArray(new Marking[0]));
		}
		return result;
	}

	public static class Marking {
		public final BigDecimal time;
		public final BigInteger[] placeTokens;
		public final Concession[] concessionsOrderedByDelay;

		private Marking(final BigDecimal time, final BigInteger[] placeTokens,
				final Concession[] concessionsOrderedByDelay) {
			this.time = time;
			this.placeTokens = placeTokens;
			this.concessionsOrderedByDelay = concessionsOrderedByDelay;
		}

		public boolean isDead() {
			return concessionsOrderedByDelay.length == 0;
		}

		public boolean hasEqualTokens(final Marking other) {
			if (other != null && other.placeTokens.length == placeTokens.length) {
				for (int i = 0; i < placeTokens.length; i++) {
					if (placeTokens[i].compareTo(other.placeTokens[i]) != 0) {
						return false;
					}
				}
			}
			return true;
		}

		public boolean hasEqualTokensAndConcessions(final Marking other) {
			if (other == null || other.placeTokens.length != placeTokens.length
					|| other.concessionsOrderedByDelay.length != concessionsOrderedByDelay.length) {
				return false;
			}
			if (!hasEqualTokens(other)) {
				return false;
			}
			final Map<TransitionDetails, BigDecimal> delayMap = new HashMap<>();
			for (final var concession : concessionsOrderedByDelay) {
				delayMap.put(concession.transition, concession.delay);
			}
			for (final var concession : other.concessionsOrderedByDelay) {
				final BigDecimal delayA = delayMap.get(concession.transition);
				if (delayA == null || delayA.compareTo(concession.delay) != 0) {
					return false;
				}
			}
			return true;
		}
	}

	public static class FiringEdge {
		public final Marking from;
		public final Marking to;
		public final TransitionDetails transition;

		public FiringEdge(final Marking from, final Marking marking, final TransitionDetails transition) {
			this.from = from;
			to = marking;
			this.transition = transition;
		}
	}

	public static class PlaceDetails {
		final DiscretePlace place;
		final Map<PNArc, BigDecimal> outputProbabilitiesNormalized = new HashMap<>();
		final Map<PNArc, BigDecimal> inputProbabilitiesNormalized = new HashMap<>();

		public PlaceDetails(final DiscretePlace place) {
			this.place = place;
		}

		void normalizeProbabilities() {
			if (!outputProbabilitiesNormalized.isEmpty()) {
				BigDecimal sum = BigDecimal.ZERO;
				for (final var probability : outputProbabilitiesNormalized.values()) {
					sum = sum.add(probability);
				}
				if (sum.compareTo(BigDecimal.ZERO) == 0) {
					throw new SimulationException("Output probabilities of place '" + place + "' are all zero");
				}
				for (final var edge : outputProbabilitiesNormalized.keySet()) {
					outputProbabilitiesNormalized.put(edge, outputProbabilitiesNormalized.get(edge)
							.divide(sum, 24, RoundingMode.HALF_UP).stripTrailingZeros());
				}
			}
			if (!inputProbabilitiesNormalized.isEmpty()) {
				BigDecimal sum = BigDecimal.ZERO;
				for (final var probability : inputProbabilitiesNormalized.values()) {
					sum = sum.add(probability);
				}
				if (sum.compareTo(BigDecimal.ZERO) == 0) {
					throw new SimulationException("Input probabilities of place '" + place + "' are all zero");
				}
				for (final var edge : inputProbabilitiesNormalized.keySet()) {
					inputProbabilitiesNormalized.put(edge, inputProbabilitiesNormalized.get(edge)
							.divide(sum, 24, RoundingMode.HALF_UP).stripTrailingZeros());
				}
			}
		}
	}

	public static class TransitionDetails {
		/**
		 * All arcs from places to this transition
		 */
		final List<PNArc> sources = new ArrayList<>();
		/**
		 * All arcs from this transition to places
		 */
		final List<PNArc> targets = new ArrayList<>();
		final Transition transition;
		final String firingCondition;
		private BigDecimal fixedDelay;
		private String delayFunction;
		private StochasticSampler delaySampler;

		private TransitionDetails(final Transition transition, final Random random) {
			this.transition = transition;
			// If possible, reduce the firingCondition function for faster subsequent calculations
			String reducedFiringCondition;
			try {
				reducedFiringCondition = new VanesaExpression(transition.getFiringCondition()).reduce(
						transition.getParameters());
			} catch (ParseException e) {
				reducedFiringCondition = transition.getFiringCondition();
			}
			firingCondition = reducedFiringCondition;
			if (transition instanceof DiscreteTransition) {
				delayFunction = ((DiscreteTransition) transition).getDelay();
				// If the delay function has no token dependencies, evaluate it and cache the result
				try {
					final var delayExpression = new VanesaExpression(delayFunction);
					delayExpression.with(transition.getParameters());
					if (delayExpression.getUndefinedVariables().isEmpty()) {
						fixedDelay = delayExpression.evaluate().getNumberValue();
					}
				} catch (EvaluationException | ParseException ignored) {
					fixedDelay = null;
				}
			} else if (transition instanceof StochasticTransition) {
				delaySampler = ((StochasticTransition) transition).getDistributionSampler(random);
			}
		}

		public BigDecimal getDelay(final Map<DiscretePlace, Integer> placesOrder, final BigInteger[] placeTokens)
				throws SimulationException {
			if (fixedDelay != null) {
				return fixedDelay;
			}
			if (delayFunction != null) {
				final var expression = createExpression(placesOrder, placeTokens, delayFunction,
						transition.getParameters());
				try {
					return expression.evaluate().getNumberValue();
				} catch (EvaluationException | ParseException e) {
					throw new SimulationException(
							String.format("Failed to evaluate delay function for transition '%s' (%s): %s",
									transition.getName(), transition.getLabel(), delayFunction), e);
				}
			}
			if (delaySampler != null) {
				final var delay = delaySampler.sample();
				if (delay.signum() < 0) {
					throw new SimulationException(String.format(
							"Stochastic delay distribution returned negative value for transition '%s' (%s): %s",
							transition.getName(), transition.getLabel(), delayFunction));
				}
				return delay;
			}
			throw new SimulationException(
					String.format("Failed to determine delay for transition '%s' (%s)", transition.getName(),
							transition.getLabel()));
		}
	}

	public static class Concession {
		public final TransitionDetails transition;
		public final BigDecimal delay;
		public final Map<PNArc, BigInteger> fixedArcWeights = new HashMap<>();

		public Concession(final TransitionDetails transition, final BigDecimal delay) {
			this.transition = transition;
			this.delay = delay;
		}

		public Concession retain(final BigDecimal elapsedDelay) {
			final var result = new Concession(transition, delay.subtract(elapsedDelay));
			result.fixedArcWeights.putAll(fixedArcWeights);
			return result;
		}
	}
}
