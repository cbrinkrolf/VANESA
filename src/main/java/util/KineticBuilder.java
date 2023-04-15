package util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import biologicalElements.Pathway;
import biologicalObjects.edges.Activation;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.edges.Inhibition;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import graph.GraphInstance;
import graph.gui.Parameter;
import gui.PopUpDialog;

public class KineticBuilder {
	public static String createConvenienceKineticReversible(BiologicalNodeAbstract bna, Pathway pw) {
		StringBuilder sb = new StringBuilder();
		if (pw == null) {
			pw = GraphInstance.getPathway();
		}
		if (!pw.containsVertex(bna)) {
			return "";
		}
		Collection<BiologicalEdgeAbstract> inEdges = getDirectedInEdges(bna, pw, true);
		Collection<BiologicalEdgeAbstract> outEdges = getDirectedOutEdges(bna, pw, true);

		Set<BiologicalEdgeAbstract> substrateEdges = new HashSet<>();
		Set<BiologicalEdgeAbstract> activatorEdges = new HashSet<>();
		Set<BiologicalEdgeAbstract> inhibRelativeEdges = new HashSet<>();
		Set<BiologicalEdgeAbstract> productEdges = new HashSet<>();

		// manage inEdges
		if (inEdges != null) {
			for (BiologicalEdgeAbstract bea : inEdges) {
				if (bea instanceof Activation) {
					activatorEdges.add(bea);
				} else if (bea instanceof Inhibition) {
					if (!((Inhibition) bea).isAbsoluteInhibition()) {
						inhibRelativeEdges.add(bea);
					}
				} else {
					substrateEdges.add(bea);
				}
			}
		}
		// manage outEdges
		if (outEdges != null) {
			for (BiologicalEdgeAbstract bea : outEdges) {
				productEdges.add(bea);
			}
		}
		// reaction concentration;
		sb.append(bna.getName()).append(" ");
		// activators
		String name;
		for (BiologicalEdgeAbstract bea : activatorEdges) {
			name = getBNARef(bea.getFrom()).getName();
			sb.append("* (1 + (").append(name).append(" / kA_").append(name);
			sb.append("))");
			addParameter(bna, "kA_" + name, 1, "mmol/l");
		}
		// relative inhibitors
		for (BiologicalEdgeAbstract bea : inhibRelativeEdges) {
			name = getBNARef(bea.getFrom()).getName();
			sb.append("* (kI_").append(name).append(" / (kI_").append(name);
			sb.append(" + ").append(name).append("))");
			addParameter(bna, "kI_" + name, 1, "mmol/l");
		}
		// numerator
		sb.append(" * ( v_f");
		addParameter(bna, "v_f", 1, "1/s");
		int substrates = 0;
		int products = 0;
		for (BiologicalEdgeAbstract bea : substrateEdges) {
			int weight = 1;
			substrates++;
			if (bea.getLabel().length() > 0) {
				try {
					weight = Integer.parseInt(bea.getLabel());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			sb.append(" * ");
			if (weight > 1) {
				sb.append("(");
			}
			name = getBNARef(bea.getFrom()).getName();
			sb.append(name).append(" / km_").append(name);
			addParameter(bna, "km_" + name, 1, "mmol/l");
			if (weight > 1) {
				sb.append(")^").append(weight);
			}
		}
		sb.append(" - v_r ");
		addParameter(bna, "v_r", 0.1, "1/s");
		for (BiologicalEdgeAbstract bea : productEdges) {
			int weight = 1;
			products++;
			if (bea.getLabel().length() > 0) {
				try {
					weight = Integer.parseInt(bea.getLabel());
				} catch (Exception e) {
					e.printStackTrace();
					PopUpDialog.getInstance().show("Parsing Error",
							"This label cannot be parsed as an integer: " + bea.getLabel() + "\r\n");
				}
			}
			sb.append(" * ");
			if (weight > 1) {
				sb.append("(");
			}
			name = getBNARef(bea.getTo()).getName();
			sb.append(name).append(" / km_").append(name);
			addParameter(bna, "km_" + name, 1, "mmol/l");
			if (weight > 1) {
				sb.append(")^").append(weight);
			}
		}
		sb.append(") ");
		if (substrates > 0 || products > 0) {
			sb.append("/ (");
			// dominator
			boolean isFirstSubstrateEdge = true;
			for (BiologicalEdgeAbstract bea : substrateEdges) {
				if (!isFirstSubstrateEdge) {
					sb.append(" * ");
				}
				isFirstSubstrateEdge = false;
				int weight = 1;
				if (bea.getLabel().length() > 0) {
					try {
						weight = Integer.parseInt(bea.getLabel());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				name = getBNARef(bea.getFrom()).getName();
				sb.append("(");
				for (int i = 0; i <= weight; i++) {
					if (i == 0) {
						sb.append(" 1 ");
					} else if (i == 1) {
						sb.append(" + ").append(name).append(" / km_");
						sb.append(name);
					} else {
						sb.append(" + (").append(name).append(" / km_");
						sb.append(name).append(")^").append(i);
					}
				}
				sb.append(")");
			}
			if (substrates > 0 && products > 0) {
				sb.append(" + ");
			}
			boolean isFirstProductEdge = true;
			for (BiologicalEdgeAbstract bea : productEdges) {
				if (!isFirstProductEdge) {
					sb.append(" * ");
				}
				isFirstProductEdge = false;
				int weight = 1;
				if (bea.getLabel().length() > 0) {
					try {
						weight = Integer.parseInt(bea.getLabel());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				name = getBNARef(bea.getTo()).getName();
				sb.append("(");
				for (int i = 0; i <= weight; i++) {
					if (i == 0) {
						sb.append(" 1 ");
					} else if (i == 1) {
						sb.append(" + ").append(name).append(" / km_").append(name);
					} else {
						sb.append(" + (").append(name).append(" / km_").append(name);
						sb.append(")^").append(i);
					}
				}
				sb.append(")");
			}
			sb.append(" - 1)");
		}
		return sb.toString();
	}

	public static String createConvenienceKineticIrreversible(BiologicalNodeAbstract bna, Pathway pw) {
		StringBuilder sb = new StringBuilder();
		if (pw == null) {
			pw = GraphInstance.getPathway();
		}
		if (!pw.containsVertex(bna)) {
			return "";
		}
		Collection<BiologicalEdgeAbstract> inEdges = getDirectedInEdges(bna, pw, true);

		Set<BiologicalEdgeAbstract> substrateEdges = new HashSet<>();
		Set<BiologicalEdgeAbstract> activatorEdges = new HashSet<>();
		Set<BiologicalEdgeAbstract> inhibRelativeEdges = new HashSet<>();

		// manage inEdges
		if (inEdges != null) {
			for (BiologicalEdgeAbstract bea : inEdges) {
				if (bea instanceof Activation) {
					activatorEdges.add(bea);
				} else if (bea instanceof Inhibition) {
					if (!((Inhibition) bea).isAbsoluteInhibition()) {
						inhibRelativeEdges.add(bea);
					}
				} else {
					substrateEdges.add(bea);
				}
			}
		}
		// reaction concentration;
		sb.append(bna.getName()).append(" ");
		// activators
		String name;
		for (BiologicalEdgeAbstract bea : activatorEdges) {
			name = getBNARef(bea.getFrom()).getName();
			sb.append("* (1 + (").append(name).append(" / kA_").append(name);
			sb.append("))");
			addParameter(bna, "kA_" + name, 1, "mmol/l");
		}
		// relative inhibitors
		for (BiologicalEdgeAbstract bea : inhibRelativeEdges) {
			name = getBNARef(bea.getFrom()).getName();
			sb.append("* (kI_").append(name).append(" / (kI_").append(name);
			sb.append(" + ").append(name).append("))");
			addParameter(bna, "kI_" + name, 1, "mmol/l");
		}
		// numerator
		sb.append(" * ( v_f");
		addParameter(bna, "v_f", 1, "1/s");
		int substrates = 0;
		for (BiologicalEdgeAbstract bea : substrateEdges) {
			int weight = 1;
			substrates++;
			if (bea.getLabel().length() > 0) {
				try {
					weight = Integer.parseInt(bea.getLabel());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			sb.append(" * ");
			if (weight > 1) {
				sb.append("(");
			}
			name = getBNARef(bea.getFrom()).getName();
			sb.append(name).append(" / km_").append(name);
			addParameter(bna, "km_" + name, 1, "mmol/l");
			if (weight > 1) {
				sb.append(")^").append(weight);
			}
		}
		sb.append(") ");
		if (substrates > 0) {
			sb.append("/ (");
			// dominator
			boolean isFirstSubstrateEdge = true;
			for (BiologicalEdgeAbstract bea : substrateEdges) {
				if (!isFirstSubstrateEdge) {
					sb.append(" * ");
				}
				isFirstSubstrateEdge = false;
				int weight = 1;
				if (bea.getLabel().length() > 0) {
					try {
						weight = Integer.parseInt(bea.getLabel());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				name = getBNARef(bea.getFrom()).getName();
				sb.append("(");
				for (int i = 0; i <= weight; i++) {
					if (i == 0) {
						sb.append(" 1 ");
					} else if (i == 1) {
						sb.append(" + ").append(name).append(" / km_");
						sb.append(name);
					} else {
						sb.append(" + (").append(name).append(" / km_");
						sb.append(name).append(")^").append(i);
					}
				}
				sb.append(")");
			}
			sb.append(")");
		}
		return sb.toString();
	}

	public static String createLawOfMassActionKineticReversible(BiologicalNodeAbstract bna, Pathway pw) {

		StringBuilder sb = new StringBuilder();
		if (pw == null) {
			pw = GraphInstance.getPathway();
		}
		if (!pw.containsVertex(bna)) {
			return "";
		}
		Collection<BiologicalEdgeAbstract> substrateEdges = getDirectedInEdges(bna, pw, true);
		Collection<BiologicalEdgeAbstract> productEdges = getDirectedOutEdges(bna, pw, true);

		sb.append("v_f");
		addParameter(bna, "v_f", 1, "1/s");
		for (BiologicalEdgeAbstract bea : substrateEdges) {
			int weight = 1;
			if (bea.getLabel().length() > 0) {
				try {
					weight = Integer.parseInt(bea.getLabel());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			sb.append(" * ");
			
			sb.append(getBNARef(bea.getFrom()).getName());
			if (weight > 1) {
				sb.append("^").append(weight);
			}
		}
		sb.append(" - v_r ");
		addParameter(bna, "v_r", 0.1, "1/s");
		for (BiologicalEdgeAbstract bea : productEdges) {
			int weight = 1;
			if (bea.getLabel().length() > 0) {
				try {
					weight = Integer.parseInt(bea.getLabel());
				} catch (Exception e) {
					e.printStackTrace();
					PopUpDialog.getInstance().show("Parsing Error",
							"This label cannot be parsed as an integer: " + bea.getLabel() + "\r\n");
				}
			}
			sb.append(" * ");
			sb.append(getBNARef(bea.getTo()).getName());
			if (weight > 1) {
				sb.append("^").append(weight);
			}
		}
		return sb.toString();
	}
	
	public static String createLawOfMassActionKineticIrreversible(BiologicalNodeAbstract bna, Pathway pw) {

		StringBuilder sb = new StringBuilder();
		if (pw == null) {
			pw = GraphInstance.getPathway();
		}
		if (!pw.containsVertex(bna)) {
			return "";
		}
		Collection<BiologicalEdgeAbstract> substrateEdges = getDirectedInEdges(bna, pw, true);

		sb.append("v_f");
		addParameter(bna, "v_f", 1, "1/s");
		for (BiologicalEdgeAbstract bea : substrateEdges) {
			int weight = 1;
			if (bea.getLabel().length() > 0) {
				try {
					weight = Integer.parseInt(bea.getLabel());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			sb.append(" * ");
			
			sb.append(getBNARef(bea.getFrom()).getName());
			if (weight > 1) {
				sb.append("^").append(weight);
			}
		}
		return sb.toString();
	}

	private static void addParameter(BiologicalNodeAbstract bna, String name, double value, String unit) {
		if (bna.getParameter(name) == null) {
			bna.getParameters().add(new Parameter(name, value, unit));
		}
	}

	private static Collection<BiologicalEdgeAbstract> getDirectedInEdges(BiologicalNodeAbstract bna, Pathway pw,
			boolean integerCheck) {
		Collection<BiologicalEdgeAbstract> inEdges = pw.getGraph().getJungGraph().getInEdges(bna);
		return getFilteredEdges(inEdges, true, true);
	}

	private static Collection<BiologicalEdgeAbstract> getDirectedOutEdges(BiologicalNodeAbstract bna, Pathway pw,
			boolean integerCheck) {
		Collection<BiologicalEdgeAbstract> outEdges = pw.getGraph().getJungGraph().getOutEdges(bna);
		return getFilteredEdges(outEdges, true, true);
	}

	private static Collection<BiologicalEdgeAbstract> getFilteredEdges(Collection<BiologicalEdgeAbstract> edges,
			boolean directedCheck, boolean integerCheck) {

		Set<BiologicalEdgeAbstract> filteredEdges = new HashSet<>();

		for (BiologicalEdgeAbstract bea : edges) {
			if (directedCheck) {
				if (!bea.isDirected()) {
					continue;
				}
			}
			if (integerCheck) {
				try {
					Integer.parseInt(bea.getLabel());
				} catch (Exception e) {
					e.printStackTrace();
					PopUpDialog.getInstance().show("Parsing Error",
							"Edge label cannot be parsed as an integer: " + bea.getLabel() + "\r\n" + "Edge from: "
									+ getBNARef(bea.getFrom()).getName() + " to " + getBNARef(bea.getTo()).getName() + "\\r\\n");
					continue;
				}
			}
			filteredEdges.add(bea);
		}
		return filteredEdges;
	}
	
	private static BiologicalNodeAbstract getBNARef(BiologicalNodeAbstract bna) {
		if (bna.isLogical()) {
			return bna.getLogicalReference();
		}
		return bna;
	}
}
