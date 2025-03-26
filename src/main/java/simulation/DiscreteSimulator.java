package simulation;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.edges.petriNet.PNArc;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.petriNet.DiscretePlace;
import biologicalObjects.nodes.petriNet.DiscreteTransition;
import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.parser.ParseException;
import graph.gui.Parameter;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * Simulator for extended, timed, functional, discrete Petri Nets with capacities.
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
 *     <li>Find all transitions that have concession in the current marking with the same, smallest delay</li>
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
 *     <li>Stochastic transitions</li>
 * </ul>
 */
public class DiscreteSimulator extends Simulator {
	private final List<DiscretePlace> places = new ArrayList<>();
	private final Map<DiscreteTransition, TransitionDetails> transitions = new HashMap<>();
	private final List<Marking> markings = new ArrayList<>();
	private final List<Marking> openMarkings = new ArrayList<>();
	private final List<FiringEdge> firingEdges = new ArrayList<>();

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
				places.add((DiscretePlace) node);
			} else if (node instanceof DiscreteTransition) {
				final DiscreteTransition t = (DiscreteTransition) node;
				transitions.put(t, new TransitionDetails(t));
			}
		}
		// Collect all transition source and target places
		for (final var edge : edges) {
			if (edge instanceof PNArc) {
				if (edge.getFrom() instanceof DiscretePlace && edge.getTo() instanceof DiscreteTransition) {
					transitions.get((DiscreteTransition) edge.getTo()).sources.add((PNArc) edge);
				} else if (edge.getFrom() instanceof DiscreteTransition && edge.getTo() instanceof DiscretePlace) {
					transitions.get((DiscreteTransition) edge.getFrom()).targets.add((PNArc) edge);
				}
			}
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
		for (int i = 0; i < places.size(); i++) {
			final var place = places.get(i);
			placeTokens[i] = BigInteger.valueOf((long) place.getTokenStart());
		}
		final var startMarking = new Marking(startTime, placeTokens, determineConcession(startTime, placeTokens));
		markings.add(startMarking);
		openMarkings.add(startMarking);
	}

	private Concession[] determineConcession(final BigDecimal time, final BigInteger[] placeTokens)
			throws SimulationException {
		final List<Concession> concessions = new ArrayList<>();
		for (final var transition : transitions.values()) {
			if (transition.transition.isKnockedOut()) {
				continue;
			}
			if (StringUtils.isNotBlank(transition.firingCondition)) {
				if ("false".equalsIgnoreCase(transition.firingCondition)) {
					continue;
				} else if (!"true".equalsIgnoreCase(transition.firingCondition)) {
					final var firingConditionExpression = createExpression(places, placeTokens,
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
			final BigInteger[] putativeTokens = new BigInteger[placeTokens.length];
			System.arraycopy(placeTokens, 0, putativeTokens, 0, placeTokens.length);
			boolean valid = true;
			// Validate pre-conditions (test and inhibition arcs or constant places)
			for (final var arc : transition.sources) {
				if (arc.isRegularArc() && !arc.getFrom().isConstant()) {
					continue;
				}
				final DiscretePlace place = (DiscretePlace) arc.getFrom();
				final int placeIndex = places.indexOf(place);
				final var requestedTokens = evaluateFunction(placeTokens, arc, arc.getFunction(), arc.getParameters());
				if (putativeTokens[placeIndex].compareTo(requestedTokens) >= 0) {
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
				if (!arc.isRegularArc() || arc.getTo().isConstant()) {
					continue;
				}
				final DiscretePlace place = (DiscretePlace) arc.getFrom();
				final int placeIndex = places.indexOf(place);
				final var requestedTokens = evaluateFunction(placeTokens, arc, arc.getFunction(), arc.getParameters());
				final BigInteger newTokens = putativeTokens[placeIndex].subtract(requestedTokens);
				final BigInteger minTokens = BigInteger.valueOf((long) place.getTokenMin());
				final BigInteger maxTokens = BigInteger.valueOf((long) place.getTokenMax());
				if (newTokens.compareTo(minTokens) >= 0 && newTokens.compareTo(maxTokens) <= 0) {
					putativeTokens[placeIndex] = newTokens;
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
					final int placeIndex = places.indexOf(place);
					final var producedTokens = evaluateFunction(placeTokens, arc, arc.getFunction(),
							arc.getParameters());
					final BigInteger newTokens = putativeTokens[placeIndex].add(producedTokens);
					final BigInteger minTokens = BigInteger.valueOf((long) place.getTokenMin());
					final BigInteger maxTokens = BigInteger.valueOf((long) place.getTokenMax());
					if (newTokens.compareTo(minTokens) >= 0 && newTokens.compareTo(maxTokens) <= 0) {
						putativeTokens[placeIndex] = newTokens;
					} else {
						valid = false;
						break;
					}
				}
				if (valid) {
					// If all validations succeeded, evaluate the transition's delay and store the concession
					final BigDecimal delay = transition.getDelay(places, placeTokens);
					concessions.add(new Concession(transition, delay));
				}
			}
		}
		return concessions.stream().sorted(Comparator.comparing(o -> o.delay)).toArray(Concession[]::new);
	}

	private static VanesaExpression createExpression(final List<DiscretePlace> places, final BigInteger[] placeTokens,
			final String function, final List<Parameter> parameters) {
		final var expression = new VanesaExpression(function).with(parameters);
		for (int i = 0; i < placeTokens.length; i++) {
			expression.with(places.get(i).getName(), placeTokens[i]);
		}
		return expression;
	}

	private BigInteger evaluateFunction(final BigInteger[] placeTokens, final PNArc arc, final String function,
			final List<Parameter> parameters) throws SimulationException {
		final var expression = createExpression(places, placeTokens, function, parameters);
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
		while (!isDead() && endTime.compareTo(getMaxTime()) > 0) {
			step();
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
			if (allowBranching) {
				// Explore all possible branches
				for (int i = 0; i < maxFireIndex; i++) {
					fireTransition(marking, marking.concessionsOrderedByDelay[i].transition,
							marking.concessionsOrderedByDelay[i].delay);
				}
			} else {
				// Explore only one random branch
				final int branchIndex = random.nextInt(maxFireIndex + 1);
				fireTransition(marking, marking.concessionsOrderedByDelay[branchIndex].transition,
						marking.concessionsOrderedByDelay[branchIndex].delay);
			}
		}
	}

	private void fireTransition(final Marking marking, final TransitionDetails transition, final BigDecimal delay)
			throws SimulationException {
		final BigDecimal newTime = marking.time.add(delay);
		final BigInteger[] placeTokens = new BigInteger[places.size()];
		System.arraycopy(marking.placeTokens, 0, placeTokens, 0, placeTokens.length);
		for (final var arc : transition.sources) {
			// Test and inhibition arcs as well as constant places don't destroy tokens
			if (!arc.isRegularArc() || arc.getTo().isConstant()) {
				continue;
			}
			final DiscretePlace place = (DiscretePlace) arc.getFrom();
			final int placeIndex = places.indexOf(place);
			final var requestedTokens = evaluateFunction(marking.placeTokens, arc, arc.getFunction(),
					arc.getParameters());
			placeTokens[placeIndex] = placeTokens[placeIndex].subtract(requestedTokens);
		}
		for (final var arc : transition.targets) {
			// In the malformed case of non-regular arcs and constant places don't produce tokens
			if (!arc.isRegularArc() || arc.getTo().isConstant()) {
				continue;
			}
			final DiscretePlace place = (DiscretePlace) arc.getTo();
			final int placeIndex = places.indexOf(place);
			final var producedTokens = evaluateFunction(marking.placeTokens, arc, arc.getFunction(),
					arc.getParameters());
			placeTokens[placeIndex] = placeTokens[placeIndex].add(producedTokens);
		}
		// Determine new markings concessions and delays considering previous concession delays
		final Concession[] newConcessions = determineConcession(newTime, placeTokens);
		final List<Concession> concessions = new ArrayList<>();
		// First, retain all transitions that still have concession and reduce their delay
		for (final var concession : marking.concessionsOrderedByDelay) {
			if (concession.transition != transition) {
				for (final var checkConcession : newConcessions) {
					if (concession.transition.equals(checkConcession.transition)) {
						concessions.add(concession.retain(delay));
						break;
					}
				}
			}
		}
		// Second, add all new concessions
		for (final var concession : newConcessions) {
			boolean alreadyPresent = false;
			for (final var checkConcession : concessions) {
				if (concession.transition.equals(checkConcession.transition)) {
					alreadyPresent = true;
					break;
				}
			}
			if (!alreadyPresent) {
				concessions.add(concession);
			}
		}
		// Create the new marking
		final var newMarking = new Marking(newTime, placeTokens,
				concessions.stream().sorted(Comparator.comparing(o -> o.delay)).toArray(Concession[]::new));
		markings.add(newMarking);
		if (!newMarking.isDead()) {
			openMarkings.add(newMarking);
		}
		firingEdges.add(new FiringEdge(marking, newMarking, transition));
	}

	public Collection<DiscretePlace> getPlaces() {
		return places;
	}

	public Collection<DiscreteTransition> getTransitions() {
		return transitions.keySet();
	}

	public Collection<Marking> getMarkings() {
		return markings;
	}

	public Marking getStartMarking() {
		return markings.get(0);
	}

	public BigInteger getTokens(final Marking marking, final DiscretePlace place) {
		return marking.placeTokens[places.indexOf(place)];
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

	public static class TransitionDetails {
		/**
		 * All arcs from places to this transition
		 */
		final List<PNArc> sources = new ArrayList<>();
		/**
		 * All arcs from this transition to places
		 */
		final List<PNArc> targets = new ArrayList<>();
		final DiscreteTransition transition;
		final String firingCondition;
		private BigDecimal fixedDelay;

		private TransitionDetails(final DiscreteTransition transition) {
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
			// If the delay function has no token dependencies, evaluate it and cache the result
			try {
				final var delayExpression = new VanesaExpression(transition.getDelay());
				delayExpression.with(transition.getParameters());
				if (delayExpression.getUndefinedVariables().isEmpty()) {
					fixedDelay = delayExpression.evaluate().getNumberValue();
				}
			} catch (EvaluationException | ParseException ignored) {
				fixedDelay = null;
			}
		}

		public BigDecimal getDelay(final List<DiscretePlace> places, final BigInteger[] placeTokens)
				throws SimulationException {
			if (fixedDelay != null) {
				return fixedDelay;
			}
			final var expression = createExpression(places, placeTokens, transition.getDelay(),
					transition.getParameters());
			try {
				return expression.evaluate().getNumberValue();
			} catch (EvaluationException | ParseException e) {
				throw new SimulationException(
						String.format("Failed to evaluate delay function for transition '%s' (%s): %s",
								transition.getName(), transition.getLabel(), transition.getDelay()), e);
			}
		}
	}

	public static class Concession {
		public final TransitionDetails transition;
		public final BigDecimal delay;

		public Concession(final TransitionDetails transition, final BigDecimal delay) {
			this.transition = transition;
			this.delay = delay;
		}

		public Concession retain(final BigDecimal elapsedDelay) {
			return new Concession(transition, delay.subtract(elapsedDelay));
		}
	}
}
