package graph.jung.graphDrawing;

import javax.swing.Icon;

import com.google.common.base.Function;

import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.petriNet.Place;

public class MyVertexIconTransformer implements Function<BiologicalNodeAbstract, Icon> {

	@Override
	public Icon apply(BiologicalNodeAbstract bna) {
		if (bna instanceof Place) {
			//Icon icon = new DynamicIcon((Place) bna);

			return null;//icon;
		} else {
			return null;
		}
	}
}
