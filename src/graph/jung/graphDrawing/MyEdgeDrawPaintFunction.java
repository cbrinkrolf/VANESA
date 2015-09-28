package graph.jung.graphDrawing;

import java.awt.Color;
import java.awt.Paint;
import java.util.Iterator;

import org.apache.commons.collections15.Transformer;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import configurations.NetworkSettings;
import configurations.NetworkSettingsSingelton;
import edu.uci.ics.jung.visualization.picking.PickedState;
/*import edu.uci.ics.jung.graph.Edge;
 import edu.uci.ics.jung.graph.Vertex;
 import edu.uci.ics.jung.graph.decorators.EdgePaintFunction;
 import edu.uci.ics.jung.utils.Pair;
 import edu.uci.ics.jung.visualization.PickedState;*/

public class MyEdgeDrawPaintFunction implements
		Transformer<BiologicalEdgeAbstract, Paint> {

	protected PickedState<BiologicalNodeAbstract> psV;
	protected PickedState<BiologicalEdgeAbstract> psE;

	Color dotted = Color.LIGHT_GRAY;
	Color dotted_black = Color.BLACK.brighter();
	NetworkSettings settings = NetworkSettingsSingelton.getInstance();

	protected boolean graphTheory = false;

	public MyEdgeDrawPaintFunction(PickedState<BiologicalNodeAbstract> psV,
			PickedState<BiologicalEdgeAbstract> psE) {
		this.psV = psV;
		this.psE = psE;
	}

	public Paint getDrawPaintWithGraphTheory(BiologicalEdgeAbstract bea) {

		if (psV.isPicked(bea.getFrom()) && psV.isPicked(bea.getTo())) {
			return Color.BLUE;
		} else {
			if (settings.isBackgroundColor())
				return dotted_black;
			else
				return dotted;
		}
	}

	public Paint getDrawPaintWithoutGraphTheory(BiologicalEdgeAbstract bea) {

		if (psV.getPicked().isEmpty()) {
			if (psE.getPicked().isEmpty()) {

				BiologicalNodeAbstract a = bea.getFrom();
				BiologicalNodeAbstract b = bea.getTo();

				if (a.isHidden() || b.isHidden()) {
					if (settings.isBackgroundColor())
						return dotted_black;
					else
						return dotted;
				} else {
					return bea.getColor().darker();
				}
			} else {
				if (psE.isPicked(bea))
					return bea.getColor().darker();
				else if (settings.isBackgroundColor())
					return dotted_black;
				else
					return dotted;
			}
		} else {

			Iterator<BiologicalNodeAbstract> it = psV.getPicked().iterator();
			BiologicalNodeAbstract bna;

			while (it.hasNext()) {
				bna = it.next();
				if (bna == bea.getFrom() || bna == bea.getTo())
					return bea.getColor();
			}

			if (psE.isPicked(bea)) {
				return bea.getColor();
			}

			if (settings.isBackgroundColor()) {
				return dotted_black;
			} else {
				return dotted;
			}
		}
		//return null;
	}

	public void setGraphTheory(boolean graphTheory) {
		this.graphTheory = graphTheory;
	}

	@Override
	public Paint transform(BiologicalEdgeAbstract bea) {
		if (!graphTheory)
			return getDrawPaintWithoutGraphTheory(bea);
		else
			return getDrawPaintWithGraphTheory(bea);
	}

}
