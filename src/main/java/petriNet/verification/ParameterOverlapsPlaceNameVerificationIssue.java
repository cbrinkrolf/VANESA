package petriNet.verification;

import biologicalElements.Pathway;
import biologicalObjects.edges.petriNet.PNArc;
import biologicalObjects.nodes.petriNet.Place;
import biologicalObjects.nodes.petriNet.Transition;

public class ParameterOverlapsPlaceNameVerificationIssue extends VerificationIssue {
	private final String entityType;
	private final String entityName;
	private final String parameterName;

	public ParameterOverlapsPlaceNameVerificationIssue(final Pathway pathway, final Transition transition,
			final String parameterName) {
		super(pathway);
		entityType = "transition";
		this.entityName = transition.getName();
		this.parameterName = parameterName;
	}

	public ParameterOverlapsPlaceNameVerificationIssue(final Pathway pathway, final Place place,
			final String parameterName) {
		super(pathway);
		entityType = "place";
		this.entityName = place.getName();
		this.parameterName = parameterName;
	}

	public ParameterOverlapsPlaceNameVerificationIssue(final Pathway pathway, final PNArc arc,
			final String parameterName) {
		super(pathway);
		entityType = "arc";
		this.entityName = arc.getName();
		this.parameterName = parameterName;
	}

	@Override
	public String getDescription() {
		return "The parameter '" + parameterName + "' of " + entityType + " '" + entityName
				+ "' overlaps the name of a place in the network.<br>Parameters are not allowed to have the same "
				+ "name as a place in the network as they won't be distinguishable.";
	}

	@Override
	public boolean isAutoSolvable() {
		return false;
	}

	@Override
	public void solve() {
	}
}
