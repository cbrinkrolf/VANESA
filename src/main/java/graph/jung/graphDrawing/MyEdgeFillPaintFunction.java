package graph.jung.graphDrawing;

import java.awt.Color;
import java.awt.Paint;

import com.google.common.base.Function;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import configurations.NetworkSettings;
import configurations.NetworkSettingsSingelton;
import edu.uci.ics.jung.visualization.picking.PickedState;

public class MyEdgeFillPaintFunction implements Function<BiologicalEdgeAbstract, Paint> {

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
	public Paint apply(BiologicalEdgeAbstract bea) {
			return null;
	}
}
