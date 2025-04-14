package petriNet.verification;

import biologicalElements.Pathway;
import biologicalObjects.nodes.petriNet.Place;

public class InputConflictProbabilityVerificationIssue extends VerificationIssue {
	private final Place place;

	public InputConflictProbabilityVerificationIssue(final Pathway pathway, final Place place) {
		super(pathway);
		this.place = place;
	}

	public Place getPlace() {
		return place;
	}

	@Override
	public String getDescription() {
		return "The conflict handling probabilities of the incoming arcs of place '" + place.getName()
				+ "' are not normalized";
	}

	@Override
	public boolean isAutoSolvable() {
		return true;
	}

	@Override
	public void solve() {
		Place.solveProbabilityConflict(place.getConflictingInEdges());
	}
}
