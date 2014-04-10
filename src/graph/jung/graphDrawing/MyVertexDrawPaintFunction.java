package graph.jung.graphDrawing;

import java.awt.Color;
import java.awt.Paint;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.collections15.Transformer;

import petriNet.Transition;
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import configurations.NetworkSettings;
import configurations.NetworkSettingsSingelton;
import edu.uci.ics.jung.visualization.picking.PickedState;

public class MyVertexDrawPaintFunction implements
		Transformer<BiologicalNodeAbstract, Paint> {

	protected PickedState<BiologicalNodeAbstract> psV;
	protected PickedState<BiologicalEdgeAbstract> psE;

	private Pathway pw;
	protected boolean graphTheory = false;
	NetworkSettings settings = NetworkSettingsSingelton.getInstance();

	public MyVertexDrawPaintFunction(PickedState<BiologicalNodeAbstract> psV,
			PickedState<BiologicalEdgeAbstract> psE, Pathway pw) {
		this.psV = psV;
		this.psE = psE;
		this.pw = pw;
	}

	private Paint withGraphTheory(BiologicalNodeAbstract v) {

		if (psV.isPicked(v)) {
			if (settings.isBackgroundColor())
				return Color.WHITE;
			else
				// return Color.BLACK;
				return Color.DARK_GRAY;

		} else {
			return Color.LIGHT_GRAY;
		}
	}

	private Paint withoutGraphTheory(BiologicalNodeAbstract v) {
		boolean medium_check = false;

		if (v instanceof Transition) {
			if (((Transition) v).isSimulationActive()) {
				return Color.red;
			}
		}

		if (psV.getPicked().isEmpty()) {
			if (psE.getPicked().isEmpty()) {

				if (v.isReference() || v.isHidden())
					return Color.LIGHT_GRAY;
				else if (v.hasBrendaNode() || v.hasKEGGNode())
					return Color.RED;
				else

				if (settings.isBackgroundColor())
					return Color.WHITE;
				else
					// return Color.BLACK;
					return Color.DARK_GRAY;

			} else {
				Set<BiologicalEdgeAbstract> set = psE.getPicked();
				BiologicalEdgeAbstract e;
				for (Iterator<BiologicalEdgeAbstract> it = set.iterator(); it
						.hasNext();) {

					e = it.next();
					// Pair points = e.getEndpoints();
					if (v == e.getFrom() || v == e.getTo())
						medium_check = true;
				}
				if (medium_check) {
					if (settings.isBackgroundColor()) {
						return Color.WHITE;
					} else {
						// return Color.BLACK;
						return Color.DARK_GRAY;
					}
				} else {
					return Color.LIGHT_GRAY;
				}
			}

		} else {
			if (psV.isPicked(v))
				if (settings.isBackgroundColor())
					return Color.WHITE;
				else
					// return Color.BLACK;
					// return Color.DARK_GRAY;
					return Color.BLUE;

			else {
				BiologicalNodeAbstract w;
				Iterator<BiologicalNodeAbstract> iter = pw.getGraph()
						.getJungGraph().getNeighbors(v).iterator();
				while (iter.hasNext()) {
					w = iter.next();
					if (psV.isPicked(w))
						medium_check = true;
				}

				Set<BiologicalEdgeAbstract> set = psE.getPicked();
				BiologicalEdgeAbstract bea;
				for (Iterator<BiologicalEdgeAbstract> it = set.iterator(); it
						.hasNext();) {

					bea = it.next();

					if (v == bea.getFrom() || v == bea.getTo())
						medium_check = true;
				}

				if (medium_check)
					if (settings.isBackgroundColor())
						return Color.WHITE;
					else
						// return Color.BLACK;
						return Color.DARK_GRAY;

				else
					return Color.LIGHT_GRAY;
			}
		}
	}

	public void setGraphTheory(boolean graphTheory) {
		this.graphTheory = graphTheory;
	}

	/*
	 * public Paint getNewLoadedDrawPaint(Vertex v) { return Color.red;
	 * 
	 * }
	 */

	@Override
	public Paint transform(BiologicalNodeAbstract v) {

		HashSet<BiologicalNodeAbstract> set = pw.getNewLoadedNodes();
		if (set.contains(v)) {
			return Color.red;
		} else {
			if (!graphTheory)
				return withoutGraphTheory(v);
			else
				return withGraphTheory(v);
		}
	}
}
