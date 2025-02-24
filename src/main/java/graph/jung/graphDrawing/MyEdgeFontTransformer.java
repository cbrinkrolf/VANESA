package graph.jung.graphDrawing;

import java.awt.Font;

import com.google.common.base.Function;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import configurations.GraphSettings;

public class MyEdgeFontTransformer implements Function<BiologicalEdgeAbstract, Font> {
	private final GraphSettings settings = GraphSettings.getInstance();

	@Override
	public Font apply(BiologicalEdgeAbstract bea) {
		if (settings.getEdgeFont() != null) {
			return settings.getEdgeFont();
		}
		return null;
	}
}
