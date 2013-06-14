package graph.jung.graphDrawing;

import java.awt.BasicStroke;
import java.awt.Stroke;

import org.apache.commons.collections15.Transformer;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.visualization.PluggableRenderContext;
import edu.uci.ics.jung.visualization.picking.PickedState;

public class MyEdgeStrokeHighlighting implements
		Transformer<BiologicalEdgeAbstract, Stroke> {

	protected static final Stroke basic = new BasicStroke(1);

	protected static final Stroke heavy = new BasicStroke(2);

	// protected static final Stroke dotted = new SloppyStroke(2.0f, 3.0f);
	protected static final Stroke dotted = PluggableRenderContext.DOTTED;

	protected PickedState<BiologicalNodeAbstract> psV;
	protected PickedState<BiologicalEdgeAbstract> psE;

	protected boolean graphTheory = false;

	public MyEdgeStrokeHighlighting(PickedState<BiologicalNodeAbstract> psV,
			PickedState<BiologicalEdgeAbstract> psE) {
		this.psV = psV;
		this.psE = psE;
	}

	private Stroke getStrokeWithoutGraphTheory(BiologicalEdgeAbstract bea) {

		if (psV.getPicked().isEmpty()) {
			if (psE.getPicked().isEmpty()) {
				return basic;
			} else {
				if (psE.isPicked(bea)) {
					return heavy;
				} else {
					return dotted;
				}
			}
		} else {

			if (psV.isPicked(bea.getFrom()) || psV.isPicked(bea.getTo())) {
				return heavy;
			} else {
				return dotted;
			}
		}
	}

	private Stroke getStrokeWithGraphTheory(BiologicalEdgeAbstract bea) {

		if (psV.isPicked(bea.getFrom()) == true
				&& psV.isPicked(bea.getTo()) == true) {
			return heavy;
		} else {
			return dotted;
		}
	}

	public void setGraphTheory(boolean graphTheory) {
		this.graphTheory = graphTheory;
	}

	@Override
	public Stroke transform(BiologicalEdgeAbstract bea) {
		if (!graphTheory)
			return getStrokeWithoutGraphTheory(bea);
		else
			return getStrokeWithGraphTheory(bea);
	}
}
