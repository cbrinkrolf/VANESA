package graph.jung.graphDrawing;

import java.awt.BasicStroke;
import java.awt.Stroke;
import java.util.Iterator;

import com.google.common.base.Function;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.petriNet.ContinuousTransition;
import biologicalObjects.nodes.petriNet.Place;
import edu.uci.ics.jung.visualization.picking.PickedState;

public class MyVertexStrokeHighlighting implements
		Function<BiologicalNodeAbstract, Stroke> {

	protected Stroke pn_heavy = new BasicStroke(5);

	protected Stroke pn_medium = new BasicStroke(4);

	protected Stroke pn_light = new BasicStroke(2);

	protected Stroke heavy = new BasicStroke(3);

	protected Stroke superHeavy = new BasicStroke(6);

	protected Stroke medium = new BasicStroke(2);

	protected Stroke light = new BasicStroke(1);

	protected PickedState<BiologicalNodeAbstract> psV;

	protected PickedState<BiologicalEdgeAbstract> psE;

	protected boolean graphTheory = false;

	private Pathway pw;

	private final float dashPhase = 0f;
    private final float dash[] = {5.0f,5.0f};
    private final BasicStroke basicstroke = new BasicStroke(
            1f,
            BasicStroke.CAP_ROUND,
            BasicStroke.JOIN_MITER,
            1.5f, //miter limit
            dash,
            dashPhase
            );
    

	private final float dash1[] = { 6.0f, 3.0f };
	private final BasicStroke refstroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT,
			BasicStroke.JOIN_BEVEL, 5.0f, dash1, 0.0f);
	private final float dash2[] = { 2.0f, 2.0f };
	private final BasicStroke groupstroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT,
			BasicStroke.JOIN_BEVEL, 5.0f, dash2, 0.0f);
	
	public MyVertexStrokeHighlighting(PickedState<BiologicalNodeAbstract> psV,
			PickedState<BiologicalEdgeAbstract> psE, Pathway pw) {
		this.psV = psV;
		this.psE = psE;
		this.pw = pw;
	}

	public Stroke withGraphTheory(BiologicalNodeAbstract v) {
		if (pw.isPetriNet())
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

		boolean petriNet = pw.isPetriNet();
		boolean isContPlace = false;
		
		// mark Environment nodes in hierarchical Nodes.

			if(bna.isEnvironmentNodeOf(pw)){
				return basicstroke;
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
					if (bna.getGroups().size() > 0) {
						return groupstroke;
					}
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
	public Stroke apply(BiologicalNodeAbstract bna) {

		if (bna.isLogical()) {

			return refstroke;

		} else {

			if (!graphTheory) {
				return withoutGraphTheory(bna);
			} else {
				return withGraphTheory(bna);
			}
		}
	}
}
