package biologicalObjects.nodes.petriNet;

import java.awt.Color;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.edges.petriNet.PNArc;
import graph.GraphInstance;
import gui.PopUpDialog;

public abstract class Place extends PNNode {
	public static final int CONFLICT_HANDLING_NONE = 0;
	public static final int CONFLICT_HANDLING_PRIO = 1;
	public static final int CONFLICT_HANDLING_PROB = 2;

	private double token = 0;
	private double tokenMin = 0;
	private double tokenMax = Double.MAX_VALUE;
	private double tokenStart = 0;
	private int conflictStrategy = 0;

	protected Place(final String label, final String name, final String biologicalElement, final Pathway parent,
			final boolean isDiscrete) {
		super(label, name, biologicalElement, parent);
		setDiscrete(isDiscrete);
		if (label.isEmpty())
			setLabel(name);
		if (name.isEmpty())
			setName(label);
		setDefaultNodeSize(2);
		setDefaultColor(Color.WHITE);
	}

	public double getToken() {
		return token;
	}

	public void setToken(double token) {
		if (token < 0) {
			return;
		}
		this.token = token;
	}

	public double getTokenMin() {
		return this.isConstant() ? 0 : tokenMin;
	}

	public void setTokenMin(double tokenMin) {
		if (tokenMin < 0) {
			return;
		}
		this.tokenMin = tokenMin;
	}

	public double getTokenMax() {
		return this.isConstant() ? Double.MAX_VALUE : tokenMax;
	}

	public void setTokenMax(double tokenMax) {
		if (tokenMax < 0) {
			return;
		}
		this.tokenMax = tokenMax;
	}

	public double getTokenStart() {
		return tokenStart;
	}

	public void setTokenStart(double tokenStart) {
		if (tokenStart < 0) {
			return;
		}
		this.tokenStart = tokenStart;
		token = tokenStart;
	}

	public int getConflictStrategy() {
		return conflictStrategy;
	}

	public void setConflictStrategy(int conflictStrategy) {
		this.conflictStrategy = conflictStrategy;
	}

	public Set<PNArc> getConflictingOutEdges() {
		Collection<BiologicalEdgeAbstract> edges = GraphInstance.getMyGraph().getJungGraph().getOutEdges(this);
		Set<PNArc> result = new HashSet<>();
		if (edges != null) {
			for (BiologicalEdgeAbstract bea : edges) {
				if (bea instanceof PNArc) {
					PNArc arc = (PNArc) bea;
					if (arc.isRegularArc() && !(bea.getTo() instanceof ContinuousTransition)) {
						result.add((PNArc) bea);
					}
				}
			}
		}
		return result;
	}

	public boolean hasConflictProperties() {
		if (conflictStrategy == DiscretePlace.CONFLICT_HANDLING_PRIO) {
			return hasPriorityConflicts();
		} else if (conflictStrategy == DiscretePlace.CONFLICT_HANDLING_PROB) {
			return hasProbabilityConflicts();
		}
		return false;
	}

	public boolean hasPriorityConflicts() {
		Collection<PNArc> edges = getConflictingOutEdges();
		if (edges.size() > 1) {
			Set<Integer> set = new HashSet<>();
			for (PNArc bea : edges) {
				if (set.contains(bea.getPriority())) {
					return true;
				}
				if (bea.getPriority() > 0 && bea.getPriority() <= edges.size()) {
					set.add(bea.getPriority());
				} else {
					return true;
				}
			}
		}
		return false;
	}

	public boolean hasProbabilityConflicts() {
		Collection<PNArc> edges = getConflictingOutEdges();
		if (edges.size() <= 1) {
			return false;
		}
		double sum = 0;
		for (PNArc bea : edges) {
			if (bea.getProbability() < 0) {
				PopUpDialog.getInstance().show("Probability error",
						"Negative probability detected: arc connecting " + bea.getFrom().getName() + " -> "
								+ bea.getTo().getName());
				return true;
			}
			sum += bea.getProbability();
		}
		return sum != 1.0;
	}

	public void solveConflictProperties() {
		if (conflictStrategy == DiscretePlace.CONFLICT_HANDLING_PRIO && hasPriorityConflicts()) {
			solvePriorityConflicts();
		} else if (conflictStrategy == DiscretePlace.CONFLICT_HANDLING_PROB && hasProbabilityConflicts()) {
			solveProbabilityConflicts();
		}
	}

	public void solvePriorityConflicts() {
		Collection<PNArc> edges = getConflictingOutEdges();
		if (edges.size() > 1) {
			Set<Integer> goodSet = new HashSet<>();
			for (int i = 1; i <= edges.size(); i++) {
				goodSet.add(i);
			}
			Set<PNArc> set = new HashSet<>();
			for (PNArc bea : edges) {
				if (goodSet.contains(bea.getPriority())) {
					goodSet.remove(bea.getPriority());
				} else {
					set.add(bea);
				}
			}
			if (set.size() == goodSet.size()) {
				for (PNArc bea : set) {
					int priority = goodSet.iterator().next();
					bea.setPriority(priority);
					goodSet.remove(priority);
				}
			}
		}
	}

	public void solveProbabilityConflicts() {
		Collection<PNArc> edges = getConflictingOutEdges();
		if (edges.size() > 1) {
			double sum = 0;
			for (PNArc bea : edges) {
				if (bea.getProbability() < 0) {
					bea.setProbability(bea.getProbability() * -1);
				}
				sum += bea.getProbability();
			}
			if (sum != 1.0) {
				for (PNArc bea : edges) {
					bea.setProbability(bea.getProbability() / sum);
				}
			}
		}
	}

	@Override
	public List<String> getTransformationParameters() {
		List<String> list = super.getTransformationParameters();
		list.add("tokenStart");
		list.add("tokenMin");
		list.add("tokenMax");
		list.add("isConstant");
		return list;
	}
}
