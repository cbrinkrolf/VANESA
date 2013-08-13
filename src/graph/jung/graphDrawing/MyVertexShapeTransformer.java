package graph.jung.graphDrawing;

import java.awt.Shape;
import java.awt.geom.AffineTransform;

import org.apache.commons.collections15.Transformer;

import biologicalObjects.nodes.BiologicalNodeAbstract;

public class MyVertexShapeTransformer implements Transformer<BiologicalNodeAbstract, Shape>{

	@Override
	public Shape transform(BiologicalNodeAbstract bna) {
		return AffineTransform.getScaleInstance(bna.getNodesize(), bna.getNodesize()).createTransformedShape(bna.getShape());
		//return bna.getShape();
	}

}
