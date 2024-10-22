package graph.jung.graphDrawing;

import java.awt.Color;
import java.awt.Paint;

import com.google.common.base.Function;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.petriNet.Transition;
import configurations.GraphSettings;
import edu.uci.ics.jung.visualization.picking.PickedState;

public class MyVertexDrawPaintFunction implements Function<BiologicalNodeAbstract, Paint> {
	private final PickedState<BiologicalNodeAbstract> psV;
	private final PickedState<BiologicalEdgeAbstract> psE;
	private final Pathway pw;
	private boolean graphTheory = false;
	private final GraphSettings settings = GraphSettings.getInstance();

	// nice orange color
	private static Color orangeColer = new Color(255, 100, 0);

	public MyVertexDrawPaintFunction(PickedState<BiologicalNodeAbstract> psV, PickedState<BiologicalEdgeAbstract> psE,
			Pathway pw) {
		this.psV = psV;
		this.psE = psE;
		this.pw = pw;
	}

	private Paint withGraphTheory(BiologicalNodeAbstract v) {
		if (psV.isPicked(v)) {
			if (settings.isBackgroundColor()) {
				return Color.WHITE;
			}
			return Color.DARK_GRAY;
		}
		return Color.LIGHT_GRAY;
	}

	private Paint withoutGraphTheory(BiologicalNodeAbstract v) {
		boolean medium_check = false;
		if (v instanceof Transition) {
			Transition t = (Transition) v;
			if (t.isSimulationFire()) {
				return Color.RED;
			}
			if (t.isSimulationActive()) {

				return orangeColer;
			}
		}

		if (psV.getPicked().isEmpty()) {
			if (psE.getPicked().isEmpty()) {
				if (v.hasBrendaNode() || v.hasKEGGNode())
					return Color.RED;
				if (settings.isBackgroundColor())
					return Color.DARK_GRAY.brighter();
				return Color.DARK_GRAY;
			} else {
				for (BiologicalEdgeAbstract e : psE.getPicked()) {
					if (v == e.getFrom() || v == e.getTo()) {
						medium_check = true;
						break;
					}
				}
				if (medium_check) {
					if (settings.isBackgroundColor()) {
						return Color.WHITE;
					}
					return Color.DARK_GRAY;
				}
				return Color.LIGHT_GRAY;
			}
		} else {
			if (psV.isPicked(v)) {
				if (settings.isBackgroundColor()) {
					return Color.WHITE;
				}
				return Color.BLUE;
			} else {
				for (BiologicalNodeAbstract w : pw.getGraph().getJungGraph().getNeighbors(v)) {
					if (psV.isPicked(w))
						medium_check = true;
				}
				for (BiologicalEdgeAbstract bea : psE.getPicked()) {
					if (v == bea.getFrom() || v == bea.getTo()) {
						medium_check = true;
						break;
					}
				}
				if (medium_check) {
					if (settings.isBackgroundColor()) {
						return Color.WHITE;
					}
					return Color.DARK_GRAY;
				}
				return Color.LIGHT_GRAY;
			}
		}
	}

	public void setGraphTheory(boolean graphTheory) {
		this.graphTheory = graphTheory;
	}

	@Override
	public Paint apply(BiologicalNodeAbstract v) {
		if (graphTheory) {
			return withGraphTheory(v);
		}
		return withoutGraphTheory(v);
	}
}
