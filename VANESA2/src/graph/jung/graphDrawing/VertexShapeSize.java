package graph.jung.graphDrawing;

/*import java.awt.Shape;

import org.apache.commons.collections15.Transformer;

import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;
/*import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.AbstractVertexShapeFunction;
import edu.uci.ics.jung.graph.decorators.NumberVertexValue;
import edu.uci.ics.jung.graph.decorators.VertexAspectRatioFunction;
import edu.uci.ics.jung.graph.decorators.VertexSizeFunction;*/

/*public class VertexShapeSize  implements Transformer {

	GraphInstance graphInstance = new GraphInstance(); 

	//private NumberVertexValue voltages;
	private VertexShapes shapes = new VertexShapes();
	private Pathway pw;
	
	public VertexShapeSize(Pathway pw) {
		this.pw=pw;
	}

	public int getSize(Vertex arg0) {
		return 0;
	}

	public float getAspectRatio(Vertex arg0) {
		return 1.0f;
	}

	public Shape getShape(Vertex v) {
		BiologicalNodeAbstract ba = (BiologicalNodeAbstract)pw.getElement(v);
		
		if (ba != null) {
			
			return ba.getShape();

		} else
						
			return shapes.getRegularPolygon(v, 5);
	}

	@Override
	public Object transform(Object arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}*/
