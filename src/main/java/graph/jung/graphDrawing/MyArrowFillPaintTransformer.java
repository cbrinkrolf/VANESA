package graph.jung.graphDrawing;

import java.awt.Color;
import java.awt.Paint;

import com.google.common.base.Function;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import configurations.GraphSettings;

public class MyArrowFillPaintTransformer implements Function<BiologicalEdgeAbstract, Paint> {

	private final GraphSettings settings = GraphSettings.getInstance();
	private Color blackOp = new Color(0, 0, 0, 255);

	@Override
	public Paint apply(BiologicalEdgeAbstract bea) {

		if (settings.getEdgeOpacity() != blackOp.getAlpha()) {
			blackOp = new Color(0, 0, 0, settings.getEdgeOpacity());
		}
		return blackOp;
	}
}
