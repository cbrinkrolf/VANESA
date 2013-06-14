package graph.jung.graphDrawing;

import java.awt.Color;
import java.awt.Paint;

import org.apache.commons.collections15.Transformer;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import configurations.NetworkSettings;
import configurations.NetworkSettingsSingelton;
import edu.uci.ics.jung.visualization.picking.PickedState;
import graph.algorithms.alignment.AlignmentEdge;

public class MyEdgeFillPaintFunction implements Transformer<BiologicalEdgeAbstract, Paint> {

	protected PickedState<BiologicalNodeAbstract> psV;
	protected PickedState<BiologicalEdgeAbstract> psE;
	
	Color dotted = Color.LIGHT_GRAY;
	Color dotted_black = Color.BLACK.brighter();
	NetworkSettings settings = NetworkSettingsSingelton.getInstance();

	protected boolean graphTheory = false;

	public MyEdgeFillPaintFunction(PickedState<BiologicalNodeAbstract> psV, PickedState<BiologicalEdgeAbstract> psE) {
		this.psV = psV;
		this.psE= psE; 
	}


	public void setGraphTheory(boolean graphTheory) {
		this.graphTheory = graphTheory;
	}

	@Override
	public Paint transform(BiologicalEdgeAbstract bea) {
		if (bea instanceof AlignmentEdge) {
			// TODO val fuer ali-Edge setzen
			double val = 1;//((Double) e.getUserDatum("alignment")).doubleValue();
			int r = (int) (255 * val);
			int b = (int) (255 * (1 - val));
			return new Color(r, 0, b);
		} else{
			return null;
		}
	}
}
