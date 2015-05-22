package graph.jung.graphDrawing;

import java.awt.Color;
import java.awt.Paint;

import org.apache.commons.collections15.Transformer;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import configurations.NetworkSettings;
import configurations.NetworkSettingsSingelton;
import edu.uci.ics.jung.visualization.picking.PickedState;

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
			return null;
	}
}
