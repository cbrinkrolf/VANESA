package petriNet.verification;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.edges.petriNet.PNArc;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.petriNet.Place;
import biologicalObjects.nodes.petriNet.Transition;
import simulation.ConflictHandling;

import java.util.ArrayList;
import java.util.List;

public class PNVerifier {
	private final Pathway pathway;
	private final List<VerificationIssue> issues = new ArrayList<>();

	public PNVerifier(final Pathway pathway) {
		this.pathway = pathway;
	}

	public boolean verify() {
		for (final BiologicalNodeAbstract node : pathway.getAllGraphNodes()) {
			if (node instanceof Place) {
				final Place place = (Place) node;
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
				// TODO: formula missing parameters/places
			}
		}
		for (final BiologicalEdgeAbstract edge : pathway.getAllEdges()) {
			if (edge instanceof PNArc) {
				final PNArc arc = (PNArc) edge;
				// TODO: formula missing parameters/places
			}
		}
		return issues.isEmpty();
	}

	public Pathway getPathway() {
		return pathway;
	}

	public VerificationIssue[] getIssues() {
		return issues.toArray(new VerificationIssue[0]);
	}
}
