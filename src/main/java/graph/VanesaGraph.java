package graph;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import graph.annotations.VanesaAnnotation;
import graph.operations.layout.*;
import graph.operations.layout.gem.GEMLayoutOperation;
import graph.operations.layout.hct.HCTLayoutOperation;
import graph.operations.layout.heb.HEBLayoutOperation;

import java.awt.geom.Point2D;
import java.util.Map;

public class VanesaGraph extends Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract, VanesaAnnotation> {
	private LayoutOperation layout;

	public LayoutOperation getLayout() {
		return layout;
	}

	public void applyLayout(final LayoutOperation layout) {
		this.layout = layout;
		if (layout != null) {
			apply(layout);
			// TODO: zoom and center
		}
	}

	public void updateLayout() {
		if (layout != null) {
			apply(layout);
			// TODO: zoom and center
		}
	}

	public void changeToCircleLayout() {
		applyLayout(new CircleLayoutOperation());
		apply(new StaticLayoutOperation());
	}

	public void changeToStaticLayout() {
		applyLayout(new StaticLayoutOperation());
	}

	public void changeToGEMLayout() {
		applyLayout(new GEMLayoutOperation());
		apply(new StaticLayoutOperation());
		/* TODO
		Collection<BiologicalNodeAbstract> nodes = getVisualizationViewer().getPickedVertexState().getPicked();
		if (nodes.size() > 0) {
			Map<BiologicalNodeAbstract, Point2D> map = new HashMap<>();
			// put unpicked nodes to static
			for (BiologicalNodeAbstract n : g.getVertices()) {
				if (!nodes.contains(n)) {
					map.put(n, getVertexLocation(n));
				}
			}
			changeToLayout(new GEMLayout(g, map));
			PopUpDialog.getInstance().show("GEMLayout", "GEMLayout was applied on picked nodes only!");
		} else {
			changeToLayout(new GEMLayout(g));
		}
		*/
	}

	public void changeToGEMLayout(final Map<BiologicalNodeAbstract, Point2D> mapOfStaticNodes) {
		applyLayout(new GEMLayoutOperation());
		apply(new StaticLayoutOperation());
	}

	public void changeToHEBLayout() {
		/* TODO
		if (layout instanceof HEBLayout && !((HEBLayout) layout).getConfig().resetLayout()) {
			if (((HEBLayout) layout).getConfig().getAutoRelayout()) {
				changeToLayout(new HEBLayout(g, ((HEBLayout) layout).getOrder()));
			} else {
				((HEBLayout) layout).saveCurrentOrder();
			}
			return;
		}
		*/
		applyLayout(new HEBLayoutOperation());
		apply(new StaticLayoutOperation());
	}

	public void changeToHCTLayout() {
		applyLayout(new HCTLayoutOperation()); // TODO: pathway.getRootNode()
		apply(new StaticLayoutOperation());
	}

	public void changeToISOMLayout() {
		applyLayout(new ISOMLayoutOperation());
		apply(new StaticLayoutOperation());
	}

	public void changeToFRLayout() {
		applyLayout(new FRLayoutOperation());
		apply(new StaticLayoutOperation());
	}

	public void changeToKKLayout() {
		applyLayout(new KKLayoutOperation());
		apply(new StaticLayoutOperation());
	}

	public void changeToSpringLayout() {
		applyLayout(new SpringLayoutOperation());
		apply(new StaticLayoutOperation());
	}

	public void enableGraphTheory() {
		// TODO
	}

	public void disableGraphTheory() {
		// TODO
	}
}
