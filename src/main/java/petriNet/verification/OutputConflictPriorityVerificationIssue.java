package petriNet.verification;

import biologicalElements.Pathway;
import biologicalObjects.nodes.petriNet.Place;

public class OutputConflictPriorityVerificationIssue extends VerificationIssue {
	private final Place place;

	public OutputConflictPriorityVerificationIssue(final Pathway pathway, final Place place) {
		super(pathway);
		this.place = place;
	}

	public Place getPlace() {
		return place;
	}

	@Override
	public String getDescription() {
		return "The conflict handling priorities of the outgoing arcs of place '" + place.getName() + "' are invalid";
	}

	@Override
	public boolean isAutoSolvable() {
		return true;
	}

	@Override
	public void solve() {
		Place.solvePriorityConflict(place.getConflictingOutEdges());
	}
}
