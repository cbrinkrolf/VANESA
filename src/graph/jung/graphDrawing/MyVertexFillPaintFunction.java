package graph.jung.graphDrawing;

import java.awt.Color;
import java.awt.Paint;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.collections15.Transformer;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import configurations.NetworkSettings;
import configurations.NetworkSettingsSingelton;
import edu.uci.ics.jung.visualization.picking.PickedState;

public class MyVertexFillPaintFunction implements
		Transformer<BiologicalNodeAbstract, Paint> {

	protected PickedState<BiologicalNodeAbstract> psV;
	protected PickedState<BiologicalEdgeAbstract> psE;

	private Pathway pw;
	protected boolean graphTheory = false;
	NetworkSettings settings = NetworkSettingsSingelton.getInstance();

	public MyVertexFillPaintFunction(PickedState<BiologicalNodeAbstract> psV,
			PickedState<BiologicalEdgeAbstract> psE, Pathway pw) {
		this.psV = psV;
		this.psE = psE;
		this.pw = pw;
	}

	private Paint getFillPaintWithoutGraphTheory(BiologicalNodeAbstract v) {

		if (psV.getPicked().isEmpty()) {
			if (psE.getPicked().isEmpty()) {
				return v.getColor();
			} else {
				// Set set = ps.getPickedEdges();
				BiologicalEdgeAbstract bea;
				for (Iterator<BiologicalEdgeAbstract> it = psE.getPicked()
						.iterator(); it.hasNext();) {

					bea = it.next();

					if (v == bea.getFrom() || v == bea.getTo()) {
						return v.getColor().brighter().brighter();
					}
				}

				return Color.LIGHT_GRAY.brighter();
			}

		} else {
			if (psV.isPicked(v))
				return v.getColor();
			else {
				BiologicalNodeAbstract w;
				Iterator<BiologicalNodeAbstract> iter = pw.getGraph().getJungGraph().getNeighbors(v).iterator();
				while(iter.hasNext()) {
					w = iter.next();
					if (psV.isPicked(w))
						return v.getColor().brighter().brighter();
				}

				Set<BiologicalEdgeAbstract> set = psE.getPicked();
				BiologicalEdgeAbstract bea;
				for (Iterator<BiologicalEdgeAbstract> it = set.iterator(); it
						.hasNext();) {

					bea = it.next();
					if (v == bea.getFrom() || v == bea.getTo())
						return v.getColor().brighter().brighter();
				}

				return Color.LIGHT_GRAY.brighter();
			}
		}
	}

	private Paint getFillPaintWithGraphTheory(BiologicalNodeAbstract v) {
		if (psV.isPicked(v)) {
			return Color.YELLOW;
		} else {
			return Color.white;
		}

	}

	public void setGraphTheory(boolean graphTheory) {
		this.graphTheory = graphTheory;
	}

	/*public Paint getNewLoadedDrawPaint(Vertex v) {
		return Color.red;
	}*/

	@Override
	public Paint transform(BiologicalNodeAbstract bna) {
		if (!graphTheory) {
			return getFillPaintWithoutGraphTheory(bna);
		} else {
			return getFillPaintWithGraphTheory(bna);
		}
	}
}
