package graph.jung.graphDrawing;

import java.awt.BasicStroke;
import java.awt.Stroke;

import org.apache.commons.collections15.Transformer;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.visualization.picking.PickedState;

public class MyEdgeStrokeHighlighting implements
		Transformer<BiologicalEdgeAbstract, Stroke> {

	protected static final Stroke basic = new BasicStroke(2);

	protected static final Stroke heavy = new BasicStroke(4);
	protected static final float dash2[] = {2.0f};
	protected static final Stroke nogt = new BasicStroke(1.0f,BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER,
            10.0f, dash2, 0.0f);
	
	
	protected static final float log = (float) Math.log(1.5);
	protected static final float pickedFactor = 1.4f;
	
	protected static Stroke hierarchical;

	// protected static final Stroke dotted = new SloppyStroke(2.0f, 3.0f);
	final static float dash1[] = {10.0f};
	protected static final Stroke dotted =new BasicStroke(2.0f,
            BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER,
            10.0f, dash1, 0.0f);;//PluggableRenderContext.DOTTED;
    protected static final Stroke dottedPicked =new BasicStroke(2.0f * pickedFactor,
                    BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER,
                    10.0f, dash1, 0.0f);
            
	protected PickedState<BiologicalNodeAbstract> psV;
	protected PickedState<BiologicalEdgeAbstract> psE;
	protected Pathway pw;

	protected boolean graphTheory = false;

	public MyEdgeStrokeHighlighting(PickedState<BiologicalNodeAbstract> psV,
			PickedState<BiologicalEdgeAbstract> psE, Pathway pw) {
		this.psV = psV;
		this.psE = psE;
		this.pw = pw;
	}

	private Stroke getStrokeWithoutGraphTheory(BiologicalEdgeAbstract bea) {
		if(pw.isBNA()){
			BiologicalNodeAbstract pathway = (BiologicalNodeAbstract) pw;
			if(pathway.getEnvironment().contains(bea.getFrom()) || pathway.getEnvironment().contains(bea.getTo())){
				
				return nogt;
			}
		}
		float strength = (float) Math.log1p(pw.edgeGrade(bea))/log;
		strength = Math.max(strength, 2);
		hierarchical = new BasicStroke(strength);
		Stroke picked = new BasicStroke(pickedFactor*strength);
		if (psV.getPicked().isEmpty()) {
			if (psE.getPicked().isEmpty()) {
				if(bea.getBiologicalElement().equals(Elementdeclerations.pnTestEdge)){
					return dotted;
				}
//				return basic;
				return hierarchical;
			} else {
				if (psE.isPicked(bea)) {
//					return heavy;
					if(bea.getBiologicalElement().equals(Elementdeclerations.pnTestEdge)){
						return dottedPicked;
					}
					return picked;
				} else {
					return dotted;
				}
			}
		} else {

			if (psV.isPicked(bea.getFrom()) || psV.isPicked(bea.getTo())) {
//				return heavy;
				if(bea.getBiologicalElement().equals(Elementdeclerations.pnTestEdge)){
					return dottedPicked;
				}
				return picked;
			} else {
				return dotted;
			}
		}
	}

	private Stroke getStrokeWithGraphTheory(BiologicalEdgeAbstract bea) {

		if (psV.isPicked(bea.getFrom())	&& psV.isPicked(bea.getTo())) {
			return heavy;
		} else {
			return dotted;
		}
	}

	public void setGraphTheory(boolean graphTheory) {
		this.graphTheory = graphTheory;
	}

	@Override
	public Stroke transform(BiologicalEdgeAbstract bea) {
		if (!graphTheory)
			return getStrokeWithoutGraphTheory(bea);
		else
			return getStrokeWithGraphTheory(bea);
	}
}
