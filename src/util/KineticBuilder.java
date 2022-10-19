package util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import biologicalElements.Pathway;
import biologicalObjects.edges.Activation;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.edges.Inhibition;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import graph.GraphInstance;
import graph.gui.Parameter;

public class KineticBuilder {

	public static String createConvenienceKinetic(BiologicalNodeAbstract bna) {
		StringBuilder sb = new StringBuilder();
		Pathway pw = GraphInstance.getPathwayStatic();

		Collection<BiologicalEdgeAbstract> inEdges = pw.getGraph().getJungGraph().getInEdges(bna);

		Collection<BiologicalEdgeAbstract> outEdges = pw.getGraph().getJungGraph().getOutEdges(bna);

		Set<BiologicalEdgeAbstract> substrateEdges = new HashSet<BiologicalEdgeAbstract>();
		Set<BiologicalEdgeAbstract> activatorEdges = new HashSet<BiologicalEdgeAbstract>();
		Set<BiologicalEdgeAbstract> inhibRelativeEdges = new HashSet<BiologicalEdgeAbstract>();
		Set<BiologicalEdgeAbstract> productEdges = new HashSet<BiologicalEdgeAbstract>();

		BiologicalEdgeAbstract bea;
		Iterator<BiologicalEdgeAbstract> itBea = null;
		// manage inEdges
		if (inEdges != null) {
			itBea = inEdges.iterator();
			while (itBea.hasNext()) {
				bea = itBea.next();

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
			itBea = outEdges.iterator();
			while (itBea.hasNext()) {
				bea = itBea.next();
				productEdges.add(bea);
			}
		}

		// reaction concentration;
		sb.append(bna.getName() + " ");
		// activators
		itBea = activatorEdges.iterator();
		while (itBea.hasNext()) {
			bea = itBea.next();
			sb.append("* (1 + (" + bea.getFrom().getName() + " / kA_" + bea.getFrom().getName() + "))");
			addParameter(bna, "kA_" + bea.getFrom().getName(), 1, "mmol/l");
		}

		// relative inhibitors
		itBea = inhibRelativeEdges.iterator();
		while (itBea.hasNext()) {
			bea = itBea.next();
			sb.append("* (kI_" + bea.getFrom().getName() + " / (kI_" + bea.getFrom().getName() + " + " + bea.getFrom().getName() + "))");
			addParameter(bna, "kI_" + bea.getFrom().getName(), 1, "mmol/l");
		}

		// numerator
		sb.append(" * ( v_f");
		addParameter(bna, "v_f", 1, "1/s");
		int weight;
		int substrates = 0;
		int products = 0;
		itBea = substrateEdges.iterator();

		while (itBea.hasNext()) {
			weight = 1;
			substrates++;
			bea = itBea.next();
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
			sb.append(bea.getFrom().getName() + " / km_" + bea.getFrom().getName());
			addParameter(bna, "km_" + bea.getFrom().getName(), 1, "mmol/l");
			if (weight > 1) {
				sb.append(")^" + weight);
			}
		}
		sb.append(" - v_r ");
		addParameter(bna, "v_r", 0.1, "1/s");

		itBea = productEdges.iterator();
		while (itBea.hasNext()) {
			weight = 1;
			products++;
			bea = itBea.next();
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
			sb.append(bea.getTo().getName() + " / km_" + bea.getTo().getName());
			addParameter(bna, "km_" + bea.getTo().getName(), 1, "mmol/l");
			if (weight > 1) {
				sb.append(")^" + weight);
			}
		}
		sb.append(") ");

		if (substrates > 0 || products > 0) {
			sb.append("/ (");

			// dominator
			itBea = substrateEdges.iterator();
			while (itBea.hasNext()) {
				weight = 1;
				bea = itBea.next();
				if (bea.getLabel().length() > 0) {
					try {
						weight = Integer.parseInt(bea.getLabel());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				sb.append("(");
				for (int i = 0; i <= weight; i++) {
					if (i == 0) {
						sb.append(" 1 ");
					} else if (i == 1) {
						sb.append(" + " + bea.getFrom().getName() + " / km_" + bea.getFrom().getName());
					} else {
						sb.append(" + (" + bea.getFrom().getName() + " / km_" + bea.getFrom().getName() + ")^" + weight);
					}
				}
				sb.append(")");
				if (itBea.hasNext()) {
					sb.append(" * ");
				}
			}
			if (substrates > 0 && products > 0) {
				sb.append(" + ");
			}

			itBea = productEdges.iterator();
			while (itBea.hasNext()) {
				weight = 1;
				bea = itBea.next();
				if (bea.getLabel().length() > 0) {
					try {
						weight = Integer.parseInt(bea.getLabel());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				sb.append("(");
				for (int i = 0; i <= weight; i++) {
					if (i == 0) {
						sb.append(" 1 ");
					} else if (i == 1) {
						sb.append(" + " + bea.getTo().getName() + " / km_" + bea.getTo().getName());
					} else {
						sb.append(" + (" + bea.getTo().getName() + " / km_" + bea.getTo().getName() + ")^" + weight);
					}
				}
				sb.append(")");
				if (itBea.hasNext()) {
					sb.append(" * ");
				}
			}
			sb.append(" - 1)");
		}
		return sb.toString();
	}

	private static void addParameter(BiologicalNodeAbstract bna, String name, double value, String unit) {
		if (bna.getParameter(name) == null) {
			bna.getParameters().add(new Parameter(name, value, unit));
		}
	}
}
