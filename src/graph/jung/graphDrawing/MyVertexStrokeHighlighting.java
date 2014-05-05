package graph.jung.graphDrawing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.util.Iterator;

import org.apache.commons.collections15.Transformer;

import petriNet.ContinuousTransition;
import petriNet.Place;
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.visualization.picking.PickedState;
import graph.GraphInstance;

public class MyVertexStrokeHighlighting implements
		Transformer<BiologicalNodeAbstract, Stroke> {

	protected Stroke pn_heavy = new BasicStroke(5);

	protected Stroke pn_medium = new BasicStroke(4);

	protected Stroke pn_light = new BasicStroke(3);

	protected Stroke heavy = new BasicStroke(2);

	protected Stroke superHeavy = new BasicStroke(5);

	protected Stroke medium = new BasicStroke(1);

	protected Stroke light = new BasicStroke(0);

	protected PickedState<BiologicalNodeAbstract> psV;

	protected PickedState<BiologicalEdgeAbstract> psE;

	protected boolean graphTheory = false;

	private Pathway pw;

	public MyVertexStrokeHighlighting(PickedState<BiologicalNodeAbstract> psV,
			PickedState<BiologicalEdgeAbstract> psE, Pathway pw) {
		this.psV = psV;
		this.psE = psE;
		this.pw = pw;
	}

	public Stroke withGraphTheory(BiologicalNodeAbstract v) {
		if (new GraphInstance().getPathway().isPetriNet())
			if (psV.isPicked(v))
				return pn_heavy;
			else
				return pn_light;

		else if (psV.isPicked(v))
			return heavy;
		else
			return light;

	}

	public Stroke withoutGraphTheory(BiologicalNodeAbstract bna) {

		boolean petriNet = new GraphInstance().getPathway().isPetriNet();
		boolean isContPlace = false;
		
		// mark Environment nodes in hierarchical Nodes.
		if(pw instanceof BiologicalNodeAbstract){
			BiologicalNodeAbstract b = (BiologicalNodeAbstract) pw;
			if(b.getParentNode().getEnvironment().contains(bna)){
				float dashPhase = 0f;
                float dash[] = {5.0f,5.0f};
				return new BasicStroke(
                        1f,
                        BasicStroke.CAP_ROUND,
                        BasicStroke.JOIN_MITER,
                        1.5f, //miter limit
                        dash,
                        dashPhase
                        );
			}
		}

		if (((bna instanceof Place && !((Place) bna).isDiscrete()) || bna instanceof ContinuousTransition)) {
			isContPlace = true;
		}

		if (psV.getPicked().isEmpty()) {
			if (psE.getPicked().isEmpty()) {
				if (petriNet && !isContPlace) {
					return pn_medium;
				} else {
					return medium;
				}
			} else {
				Iterator<BiologicalEdgeAbstract> it = psE.getPicked()
						.iterator();
				BiologicalEdgeAbstract bea;
				while (it.hasNext()) {

					bea = it.next();

					if (bna == bea.getFrom() || bna == bea.getTo())
						if (petriNet && !isContPlace)
							return pn_medium;
						else
							return medium;
				}
				if (petriNet && !isContPlace) {
					return pn_medium;
				} else {
					return light;
				}
			}

		} else {
			if (psV.isPicked(bna)) {
				if (petriNet && !isContPlace) {
					return pn_heavy;
				} else {
					return heavy;
				}
			} else {
				Iterator<BiologicalNodeAbstract> iter = pw.getGraph()
						.getJungGraph().getNeighbors(bna).iterator();
				BiologicalNodeAbstract w;
				while (iter.hasNext()) {
					w = iter.next();
					if (psV.isPicked(w)) {
						if (petriNet && !isContPlace) {
							return pn_medium;
						} else {
							return medium;
						}
					}
				}

				Iterator<BiologicalEdgeAbstract> it = psE.getPicked()
						.iterator();
				BiologicalEdgeAbstract bea;
				while (it.hasNext()) {

					bea = it.next();

					if (bna == bea.getFrom() || bna == bea.getTo()) {
						if (petriNet && !isContPlace) {
							return pn_medium;
						} else {
							return medium;
						}
					}
				}

				if (petriNet && !isContPlace) {
					return pn_light;
				} else {
					return light;
				}
			}
		}
	}

	/*
	 * public Stroke getCollectorStroke(Vertex v) { return superHeavy; }
	 */

	public boolean isGraphTheory() {
		return graphTheory;
	}

	public void setGraphTheory(boolean graphTheory) {
		this.graphTheory = graphTheory;
	}

	@Override
	public Stroke transform(BiologicalNodeAbstract bna) {

		if (bna.hasRef()) {

			float dash1[] = { 3.0f };

			return new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
					BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f);

		} else {

			if (!graphTheory) {
				return withoutGraphTheory(bna);
			} else {
				return withGraphTheory(bna);
			}
		}
	}
}
