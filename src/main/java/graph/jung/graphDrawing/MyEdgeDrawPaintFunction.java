package graph.jung.graphDrawing;

import java.awt.Color;
import java.awt.Paint;
import java.util.Iterator;

import com.google.common.base.Function;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import configurations.NetworkSettings;
import edu.uci.ics.jung.visualization.picking.PickedState;

public class MyEdgeDrawPaintFunction implements Function<BiologicalEdgeAbstract, Paint> {

	protected PickedState<BiologicalNodeAbstract> psV;
	protected PickedState<BiologicalEdgeAbstract> psE;

	Color dotted = Color.LIGHT_GRAY;
	Color dotted_black = Color.BLACK.brighter();
	NetworkSettings settings = NetworkSettings.getInstance();
	Color alphaEdge = new Color(dotted.darker().getRed(), dotted.darker().getGreen(), dotted.darker().getBlue(), settings.getEdgeOpacity());

	protected boolean graphTheory = false;

	public MyEdgeDrawPaintFunction(PickedState<BiologicalNodeAbstract> psV, PickedState<BiologicalEdgeAbstract> psE) {
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
		// uncomment for edges
		if (settings.getDrawEdges()) {

			if (psV.getPicked().isEmpty()) {
				if (psE.getPicked().isEmpty()) {

					if (settings.isBackgroundColor())
						return dotted_black;
					else
						return bea.getColor();
				} else {
					if (psE.isPicked(bea))
						return bea.getColor();
					else if (settings.isBackgroundColor())
						return dotted_black;
					else {
						return bea.getColor();
					}
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
					return bea.getColor();
				}
			}
		} else
			return null;
	}

	public void setGraphTheory(boolean graphTheory) {
		this.graphTheory = graphTheory;
	}

	@Override
	public Paint apply(BiologicalEdgeAbstract bea) {
		if (!graphTheory)
			return getDrawPaintWithoutGraphTheory(bea);
		else
			return getDrawPaintWithGraphTheory(bea);
	}

	public void updateEdgeAlphaValue() {
		alphaEdge = new Color(dotted.getRed(), dotted.getGreen(), dotted.getBlue(), settings.getEdgeOpacity());
	}
}
