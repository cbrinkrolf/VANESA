package petriNet.verification;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.edges.petriNet.PNArc;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.petriNet.ContinuousTransition;
import biologicalObjects.nodes.petriNet.DiscreteTransition;
import biologicalObjects.nodes.petriNet.Place;
import biologicalObjects.nodes.petriNet.Transition;
import com.ezylang.evalex.parser.ParseException;
import graph.gui.Parameter;
import simulation.ConflictHandling;
import simulation.VanesaExpression;

import java.util.*;

public class PNVerifier {
	private final Pathway pathway;
	private final List<VerificationIssue> issues = new ArrayList<>();

	public PNVerifier(final Pathway pathway) {
		this.pathway = pathway;
	}

	public boolean verify() {
		final Map<String, Object> placeFunctionParameters = new HashMap<>();
		final Set<String> placeNames = new HashSet<>();
		for (final BiologicalNodeAbstract node : pathway.getAllGraphNodes()) {
			if (node instanceof Place) {
				final Place place = (Place) node;
				placeNames.add(place.getName());
				// Register the place name with a dummy value of 1 to make it available as parameter
				placeFunctionParameters.put(place.getName(), 1);
			}
		}

		for (final BiologicalNodeAbstract node : pathway.getAllGraphNodes()) {
			if (node instanceof Place) {
				final Place place = (Place) node;
				for (final Parameter parameter : place.getParameters()) {
					if (placeNames.contains(parameter.getName())) {
						issues.add(
								new ParameterOverlapsPlaceNameVerificationIssue(pathway, place, parameter.getName()));
					}
				}
				if (place.getConflictStrategy() == ConflictHandling.Priority) {
					if (Place.hasPriorityConflict(place.getConflictingInEdges())) {
						issues.add(new InputConflictPriorityVerificationIssue(pathway, place));
					}
					if (Place.hasPriorityConflict(place.getConflictingOutEdges())) {
						issues.add(new OutputConflictPriorityVerificationIssue(pathway, place));
					}
				} else if (place.getConflictStrategy() == ConflictHandling.Probability) {
					if (Place.hasProbabilityConflict(place.getConflictingInEdges())) {
						issues.add(new InputConflictProbabilityVerificationIssue(pathway, place));
					}
					if (Place.hasProbabilityConflict(place.getConflictingOutEdges())) {
						issues.add(new OutputConflictProbabilityVerificationIssue(pathway, place));
					}
				}
			} else if (node instanceof Transition) {
				final Transition transition = (Transition) node;
				for (final Parameter parameter : transition.getParameters()) {
					if (placeNames.contains(parameter.getName())) {
						issues.add(new ParameterOverlapsPlaceNameVerificationIssue(pathway, transition,
								parameter.getName()));
					}
				}
				verifyTransitionFunction(transition, placeFunctionParameters, transition.getFiringCondition(),
						"firingCondition");
				if (node instanceof DiscreteTransition) {
					verifyTransitionFunction(transition, placeFunctionParameters,
							((DiscreteTransition) node).getDelay(), "delay");
				} else if (node instanceof ContinuousTransition) {
					verifyTransitionFunction(transition, placeFunctionParameters,
							((ContinuousTransition) node).getMaximalSpeed(), "maximalSpeed");
				}
			}
		}
		for (final BiologicalEdgeAbstract edge : pathway.getAllEdges()) {
			if (edge instanceof PNArc) {
				final PNArc arc = (PNArc) edge;
				for (final Parameter parameter : arc.getParameters()) {
					if (placeNames.contains(parameter.getName())) {
						issues.add(new ParameterOverlapsPlaceNameVerificationIssue(pathway, arc, parameter.getName()));
					}
				}
				final var expression = new VanesaExpression(arc.getFunction()).with(arc.getParameters()).withValues(
						placeFunctionParameters);
				try {
					if (!expression.getUndefinedVariables().isEmpty()) {
						issues.add(new FormulaMissingVariablesVerificationIssue(pathway, arc, "function",
								arc.getFunction(), expression.getUndefinedVariables().toArray(new String[0])));
					}
				} catch (ParseException e) {
					issues.add(new FormulaParseVerificationIssue(pathway, arc, "function", arc.getFunction(),
							e.getMessage()));
				}
			}
		}
		return issues.isEmpty();
	}

	private void verifyTransitionFunction(final Transition t, final Map<String, Object> placeFunctionParameters,
			final String function, final String functionName) {
		final var expression = new VanesaExpression(function).with(t.getParameters()).withValues(
				placeFunctionParameters);
		try {
			if (!expression.getUndefinedVariables().isEmpty()) {
				issues.add(new FormulaMissingVariablesVerificationIssue(pathway, t, functionName, function,
						expression.getUndefinedVariables().toArray(new String[0])));
			}
		} catch (ParseException e) {
			issues.add(new FormulaParseVerificationIssue(pathway, t, functionName, function, e.getMessage()));
		}
	}

	public Pathway getPathway() {
		return pathway;
	}

	public VerificationIssue[] getIssues() {
		return issues.toArray(new VerificationIssue[0]);
	}
}
