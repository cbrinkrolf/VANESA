package graph.jung.graphDrawing;

import java.awt.Font;

import com.google.common.base.Function;

import biologicalObjects.nodes.BiologicalNodeAbstract;
import configurations.GraphSettings;

public class MyVertexFontTransformer implements Function<BiologicalNodeAbstract, Font> {

	private final GraphSettings settings = GraphSettings.getInstance();

	@Override
	public Font apply(BiologicalNodeAbstract bna) {
		if (settings.getVertexFont() != null) {
			return settings.getVertexFont();
		}
		// apply default font
		return null;
	}

}
