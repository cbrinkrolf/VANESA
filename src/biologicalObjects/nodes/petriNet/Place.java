package biologicalObjects.nodes.petriNet;

import java.awt.Color;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import biologicalElements.Elementdeclerations;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.edges.petriNet.PNEdge;
//import edu.uci.ics.jung.graph.Vertex;
import graph.GraphInstance;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Place extends PNNode {

	private double token = 0;
	@Getter(AccessLevel.NONE)
	private double tokenMin = 0.0;
	@Getter(AccessLevel.NONE)
	private double tokenMax = Double.MAX_VALUE;
	@Setter(AccessLevel.NONE)
	private double tokenStart = 0;

	public static final int CONFLICTHANDLING_NONE = 0;
	public static final int CONFLICTHANDLING_PRIO = 1;
	public static final int CONFLICTHANDLING_PROB = 2;

	private int conflictStrategy = 0;

	public Place(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.place);
		if (label.equals(""))
			setLabel(name);
		if (name.equals(""))
			setName(label);

		this.setDefaultNodesize(2);
		setDefaultColor(Color.WHITE);
	}

	public double getTokenMin() {
		if (this.isConstant()) {
			return 0;
		}
		return tokenMin;
	}

	public double getTokenMax() {
		if (this.isConstant()) {
			return Double.MAX_VALUE;
		}
		return tokenMax;
	}

	public void setTokenStart(double tokenStart) {
		this.tokenStart = tokenStart;
		if (!new GraphInstance().getPathway().getPetriPropertiesNet().isPetriNetSimulation())
			token = tokenStart;
	}

	public Set<PNEdge> getConflictingOutEdges() {
		Collection<BiologicalEdgeAbstract> coll = GraphInstance.getMyGraph().getJungGraph().getOutEdges(this);
		Set<PNEdge> result = new HashSet<PNEdge>();
		if (coll != null && coll.size() > 0) {
			Iterator<BiologicalEdgeAbstract> it = coll.iterator();
			BiologicalEdgeAbstract bea;
			while (it.hasNext()) {
				bea = it.next();
				if (bea instanceof PNEdge && !(bea.getTo() instanceof ContinuousTransition)) {
					result.add((PNEdge) bea);
				}
			}
		}
		return result;
	}

	public boolean hasConflictProperties() {
		if (this.conflictStrategy == DiscretePlace.CONFLICTHANDLING_PRIO) {
			return this.hasPriorityConflicts();
		} else if (this.conflictStrategy == DiscretePlace.CONFLICTHANDLING_PROB) {
			return this.hasProbabilityConflicts();
		}
		return false;
	}

	public boolean hasPriorityConflicts() {
		Collection<PNEdge> edges = this.getConflictingOutEdges();
		if (edges.size() > 1) {
			Iterator<PNEdge> it = edges.iterator();
			PNEdge bea;
			Set<Integer> set = new HashSet<Integer>();
			while (it.hasNext()) {
				bea = it.next();
				if (set.contains(bea.getPriority())) {
					return true;
				} else {
					if (bea.getPriority() > 0 && bea.getPriority() <= edges.size()) {
						set.add(bea.getPriority());
					} else {
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean hasProbabilityConflicts() {
		Collection<PNEdge> edges = this.getConflictingOutEdges();
		if (edges.size() > 1) {
			double sum = 0;
			Iterator<PNEdge> it = edges.iterator();
			PNEdge bea;
			while (it.hasNext()) {
				bea = it.next();
				sum += bea.getProbability();
			}
			if (sum != 1.0) {
				return true;
			}
		}
		return false;
	}

	public void solveConflictProperties() {
		if (this.conflictStrategy == DiscretePlace.CONFLICTHANDLING_PRIO && this.hasPriorityConflicts()) {
			this.solvePriorityConflicts();
		} else if (this.conflictStrategy == DiscretePlace.CONFLICTHANDLING_PROB && hasProbabilityConflicts()) {
			this.solveProbabilityConflicts();
		}
	}

	public void solvePriorityConflicts() {
		Collection<PNEdge> edges = this.getConflictingOutEdges();
		if (edges.size() > 1) {
			Iterator<PNEdge> it = edges.iterator();
			PNEdge bea;

			Set<Integer> goodSet = new HashSet<Integer>();
			for (int i = 1; i <= edges.size(); i++) {
				goodSet.add(i);
			}

			Set<PNEdge> set = new HashSet<PNEdge>();
			while (it.hasNext()) {
				bea = it.next();
				if (goodSet.contains(bea.getPriority())) {
					goodSet.remove(bea.getPriority());
				} else {
					set.add(bea);
				}
			}
			it = set.iterator();
			if (set.size() == goodSet.size()) {
				int prio = 1;
				while (it.hasNext()) {
					bea = it.next();
					prio = goodSet.iterator().next();
					bea.setPriority(prio);
					goodSet.remove(prio);
				}
			}
		}
	}

	public void solveProbabilityConflicts() {
		Collection<PNEdge> edges = this.getConflictingOutEdges();
		if (edges.size() > 1) {
			double sum = 0;
			Iterator<PNEdge> it = edges.iterator();
			PNEdge bea;
			while (it.hasNext()) {
				bea = it.next();
				sum += bea.getProbability();
			}
			if (sum != 1.0) {
				it = edges.iterator();
				while (it.hasNext()) {
					bea = it.next();
					bea.setProbability(bea.getProbability() / sum);
				}
			}
		}
	}
}
