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
import simulation.ConflictHandling;

public abstract class Place extends PNNode {
	private double token = 0;
	private double tokenMin = 0;
	private double tokenMax = Double.MAX_VALUE;
	private double tokenStart = 0;
	private ConflictHandling conflictStrategy = ConflictHandling.Probability;

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

	public ConflictHandling getConflictStrategy() {
		return conflictStrategy;
	}

	public void setConflictStrategy(final ConflictHandling conflictStrategy) {
		this.conflictStrategy = conflictStrategy;
	}

	public Set<PNArc> getConflictingInEdges() {
		final Collection<BiologicalEdgeAbstract> edges = GraphInstance.getMyGraph().getJungGraph().getInEdges(this);
		final Set<PNArc> result = new HashSet<>();
		if (edges != null) {
			for (final BiologicalEdgeAbstract bea : edges) {
				if (bea instanceof PNArc) {
					final PNArc arc = (PNArc) bea;
					if (arc.isRegularArc() && !(bea.getFrom() instanceof ContinuousTransition)) {
						result.add((PNArc) bea);
					}
				}
			}
		}
		return result;
	}

	public Set<PNArc> getConflictingOutEdges() {
		final Collection<BiologicalEdgeAbstract> edges = GraphInstance.getMyGraph().getJungGraph().getOutEdges(this);
		final Set<PNArc> result = new HashSet<>();
		if (edges != null) {
			for (final BiologicalEdgeAbstract bea : edges) {
				if (bea instanceof PNArc) {
					final PNArc arc = (PNArc) bea;
					if (arc.isRegularArc() && !(bea.getTo() instanceof ContinuousTransition)) {
						result.add((PNArc) bea);
					}
				}
			}
		}
		return result;
	}

	public boolean hasConflictProperties() {
		if (conflictStrategy == ConflictHandling.Priority) {
			return hasPriorityConflict(getConflictingInEdges()) || hasPriorityConflict(getConflictingOutEdges());
		}
		if (conflictStrategy == ConflictHandling.Probability) {
			return hasProbabilityConflict(getConflictingInEdges()) || hasProbabilityConflict(
					getConflictingOutEdges());
		}
		return false;
	}

	public static boolean hasPriorityConflict(final Collection<PNArc> edges) {
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

	public static boolean hasProbabilityConflict(final Collection<PNArc> edges) {
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
		return (sum - 1.0) > 0.000001;
	}

	public void solveConflictProperties() {
		final Collection<PNArc> inEdges = getConflictingInEdges();
		final Collection<PNArc> outEdges = getConflictingOutEdges();
		if (conflictStrategy == ConflictHandling.Priority) {
			if (hasPriorityConflict(inEdges)) {
				solvePriorityConflict(inEdges);
			}
			if (hasPriorityConflict(outEdges)) {
				solvePriorityConflict(outEdges);
			}
		} else if (conflictStrategy == ConflictHandling.Probability) {
			if (hasProbabilityConflict(inEdges)) {
				solveProbabilityConflict(inEdges);
			}
			if (hasProbabilityConflict(outEdges)) {
				solveProbabilityConflict(outEdges);
			}
		}
	}

	public static void solvePriorityConflict(final Collection<PNArc> edges) {
		if (edges.size() > 1) {
			final Set<Integer> goodSet = new HashSet<>();
			for (int i = 1; i <= edges.size(); i++) {
				goodSet.add(i);
			}
			final Set<PNArc> set = new HashSet<>();
			for (final PNArc bea : edges) {
				if (goodSet.contains(bea.getPriority())) {
					goodSet.remove(bea.getPriority());
				} else {
					set.add(bea);
				}
			}
			if (set.size() == goodSet.size()) {
				for (final PNArc bea : set) {
					final int priority = goodSet.iterator().next();
					bea.setPriority(priority);
					goodSet.remove(priority);
				}
			}
		}
	}

	public static void solveProbabilityConflict(final Collection<PNArc> edges) {
		if (edges.size() > 1) {
			double sum = 0;
			for (final PNArc bea : edges) {
				if (bea.getProbability() < 0) {
					bea.setProbability(bea.getProbability() * -1);
				}
				sum += bea.getProbability();
			}
			if (sum != 1.0) {
				for (final PNArc bea : edges) {
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
