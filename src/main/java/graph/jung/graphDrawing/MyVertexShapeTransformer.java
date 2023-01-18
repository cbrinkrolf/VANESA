package graph.jung.graphDrawing;

import java.awt.Shape;
import java.awt.geom.AffineTransform;

import com.google.common.base.Function;

import biologicalObjects.nodes.BiologicalNodeAbstract;

public class MyVertexShapeTransformer implements Function<BiologicalNodeAbstract, Shape>{

	@Override
	public Shape apply(BiologicalNodeAbstract bna) {
		return AffineTransform.getScaleInstance(bna.getNodesize(), bna.getNodesize()).createTransformedShape(bna.getShape());
		//return bna.getShape();
	}

}
