package graph.jung.graphDrawing;

import javax.swing.Icon;

import org.apache.commons.collections15.Transformer;

import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.petriNet.Place;

public class MyVertexIconTransformer implements Transformer<BiologicalNodeAbstract, Icon> {

	@Override
	public Icon transform(BiologicalNodeAbstract bna) {
		if (bna instanceof Place) {
			//Icon icon = new DynamicIcon((Place) bna);

			return null;//icon;
		} else {
			return null;
		}
	}
}
