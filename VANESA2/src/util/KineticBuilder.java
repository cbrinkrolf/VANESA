package util;

import java.util.Collection;
import java.util.Iterator;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import graph.GraphInstance;
import graph.gui.Parameter;

public class KineticBuilder {

	public static String createConvenienceKinetic(BiologicalNodeAbstract bna) {
		StringBuilder sb = new StringBuilder();
		Pathway pw = GraphInstance.getPathwayStatic();

		Collection<BiologicalEdgeAbstract> substrateEdges = pw.getGraph().getJungGraph().getInEdges(bna);

		Collection<BiologicalEdgeAbstract> productEdges = pw.getGraph().getJungGraph().getOutEdges(bna);

		BiologicalEdgeAbstract bea;

		// numerator
		sb.append(bna.getName() + " * ( v_f");
		if (bna.getParameter("v_f") == null) {
			bna.getParameters().add(new Parameter("v_f", 1, "mmol/s"));
		}
		Iterator<BiologicalEdgeAbstract> itBea = substrateEdges.iterator();
		int weight;
		int substrates = 0;
		int products = 0;
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
			if (bna.getParameter("km_" + bea.getFrom().getName()) == null) {
				bna.getParameters().add(new Parameter("km_" + bea.getFrom().getName(), 1, "mmol"));
			}
			if (weight > 1) {
				sb.append(")^" + weight);
			}
		}
		sb.append(" - v_r ");
		if (bna.getParameter("v_r") == null) {
			bna.getParameters().add(new Parameter("v_r", 1, "mmol/s"));
		}
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
			if (bna.getParameter("km_" + bea.getTo().getName()) == null) {
				bna.getParameters().add(new Parameter("km_" + bea.getTo().getName(), 1, "mmol"));
			}
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
			sb.append(")");
		}
		return sb.toString();
	}
}
