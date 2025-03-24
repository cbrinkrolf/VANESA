package simulation;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.edges.petriNet.PNArc;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.petriNet.DiscretePlace;
import biologicalObjects.nodes.petriNet.DiscreteTransition;
import biologicalObjects.nodes.petriNet.Transition;
import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.parser.ParseException;
import graph.gui.Parameter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class DiscreteSimulator {
	private final List<DiscretePlace> places = new ArrayList<>();
	private final List<DiscreteTransition> transitions = new ArrayList<>();
	private final Map<DiscreteTransition, List<PNArc>> transitionSources = new HashMap<>();
	private final Map<DiscreteTransition, List<PNArc>> transitionTargets = new HashMap<>();
	private final Random random = new Random();
	private final long seed;
	private final boolean allowBranching;
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
			final Collection<BiologicalEdgeAbstract> edges, final long seed, final boolean allowBranching)
			throws SimulationException {
		this.seed = seed;
		this.allowBranching = allowBranching;
		// Collect places and transitions
		for (final var node : nodes) {
			if (node instanceof DiscretePlace) {
				places.add((DiscretePlace) node);
			} else if (node instanceof DiscreteTransition) {
				final DiscreteTransition t = (DiscreteTransition) node;
				transitions.add(t);
				transitionSources.put(t, new ArrayList<>());
				transitionTargets.put(t, new ArrayList<>());
			}
		}
		// Collect all transition source and target places
		for (final var edge : edges) {
			if (edge instanceof PNArc) {
				if (edge.getFrom() instanceof DiscretePlace && edge.getTo() instanceof DiscreteTransition) {
					transitionSources.get((DiscreteTransition) edge.getTo()).add((PNArc) edge);
				} else if (edge.getFrom() instanceof DiscreteTransition && edge.getTo() instanceof DiscretePlace) {
					transitionTargets.get((DiscreteTransition) edge.getFrom()).add((PNArc) edge);
				}
			}
		}
		initialize();
	}

	private void initialize() throws SimulationException {
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
		final var startMarking = new Marking(BigDecimal.valueOf(0), placeTokens, determineConcession(placeTokens));
		markings.add(startMarking);
		openMarkings.add(startMarking);
	}

	private Concession[] determineConcession(final BigInteger[] placeTokens) throws SimulationException {
		final List<Concession> concessions = new ArrayList<>();
		for (final var transition : transitions) {
			if (transition.isKnockedOut()) {
				continue;
			}
			final BigInteger[] putativeTokens = new BigInteger[placeTokens.length];
			System.arraycopy(placeTokens, 0, putativeTokens, 0, placeTokens.length);
			boolean valid = true;
			// TODO: probability and priority for arcs
			// TODO: firingCondition for transition
			// Validate pre-conditions (test and inhibition arcs)
			for (final var arc : transitionSources.get(transition)) {
				if (arc.isRegularArc()) {
					continue;
				}
				final DiscretePlace place = (DiscretePlace) arc.getFrom();
				final int placeIndex = places.indexOf(place);
				final var requestedTokens = evaluateFunction(placeTokens, arc, arc.getFunction(), arc.getParameters());
				if (putativeTokens[placeIndex].compareTo(requestedTokens) >= 0) {
					if (arc.isInhibitorArc()) {
						valid = false;
						break;
					}
				} else {
					if (arc.isTestArc()) {
						valid = false;
						break;
					}
				}
			}
			// Validate normal arcs
			for (final var arc : transitionSources.get(transition)) {
				if (!arc.isRegularArc()) {
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
				for (final var arc : transitionTargets.get(transition)) {
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
					final var expression = createExpression(placeTokens, transition.getDelay(),
							transition.getParameters());
					try {
						final var result = expression.evaluate();
						final BigDecimal delay = result.getNumberValue();
						concessions.add(new Concession(transition, delay));
					} catch (EvaluationException | ParseException e) {
						throw new SimulationException(
								String.format("Failed to evaluate delay function for transition '%s' (%s): %s",
										transition.getName(), transition.getLabel(), transition.getDelay()), e);
					}
				}
			}
		}
		return concessions.stream().sorted(Comparator.comparing(o -> o.delay)).toArray(Concession[]::new);
	}

	private Expression createExpression(final BigInteger[] placeTokens, final String function,
			final List<Parameter> parameters) {
		final var expression = new Expression(function, VanesaExpressionConfiguration.EXPRESSION_CONFIGURATION);
		if (parameters != null) {
			for (final var parameter : parameters) {
				expression.with(parameter.getName(), BigDecimal.valueOf(parameter.getValue()));
			}
		}
		for (int i = 0; i < placeTokens.length; i++) {
			expression.with(places.get(i).getName(), placeTokens[i]);
		}
		return expression;
	}

	private BigInteger evaluateFunction(final BigInteger[] placeTokens, final PNArc arc, final String function,
			final List<Parameter> parameters) throws SimulationException {
		final var expression = createExpression(placeTokens, function, parameters);
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

	public void step() throws SimulationException {
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

	private void fireTransition(final Marking marking, final DiscreteTransition transition, final BigDecimal delay)
			throws SimulationException {
		final BigInteger[] placeTokens = new BigInteger[places.size()];
		System.arraycopy(marking.placeTokens, 0, placeTokens, 0, placeTokens.length);
		for (final var arc : transitionSources.get(transition)) {
			if (!arc.isRegularArc()) {
				continue;
			}
			final DiscretePlace place = (DiscretePlace) arc.getFrom();
			final int placeIndex = places.indexOf(place);
			final var requestedTokens = evaluateFunction(marking.placeTokens, arc, arc.getFunction(),
					arc.getParameters());
			placeTokens[placeIndex] = placeTokens[placeIndex].subtract(requestedTokens);
		}
		for (final var arc : transitionTargets.get(transition)) {
			if (!arc.isRegularArc()) {
				continue;
			}
			final DiscretePlace place = (DiscretePlace) arc.getTo();
			final int placeIndex = places.indexOf(place);
			final var producedTokens = evaluateFunction(marking.placeTokens, arc, arc.getFunction(),
					arc.getParameters());
			placeTokens[placeIndex] = placeTokens[placeIndex].add(producedTokens);
		}
		// Determine new markings concessions and delays considering previous concession delays
		final Concession[] newConcessions = determineConcession(placeTokens);
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
		final var newMarking = new Marking(marking.time.add(delay), placeTokens,
				concessions.stream().sorted(Comparator.comparing(o -> o.delay)).toArray(Concession[]::new));
		markings.add(newMarking);
		openMarkings.add(newMarking);
		firingEdges.add(new FiringEdge(marking, newMarking, transition));
	}

	public Collection<DiscretePlace> getPlaces() {
		return places;
	}

	public Collection<DiscreteTransition> getTransitions() {
		return transitions;
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
		public final Transition transition;

		public FiringEdge(final Marking from, final Marking marking, final Transition transition) {
			this.from = from;
			to = marking;
			this.transition = transition;
		}
	}

	public static class Concession {
		public final DiscreteTransition transition;
		public final BigDecimal delay;

		public Concession(final DiscreteTransition transition, final BigDecimal delay) {
			this.transition = transition;
			this.delay = delay;
		}

		public Concession retain(final BigDecimal elapsedDelay) {
			return new Concession(transition, delay.subtract(elapsedDelay));
		}
	}
}
