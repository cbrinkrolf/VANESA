package petriNet;

import java.awt.Color;

import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import graph.jung.graphDrawing.VertexShapes;

public class CovNode extends BiologicalNodeAbstract {
	private CovList tokenList;

	public CovNode(final String label, final String name, final Pathway parent, final int numberPlaces) {
		super(label, name, "", parent);
		setShape(VertexShapes.getEllipse());
		setColor(Color.WHITE);
		tokenList = new CovList(numberPlaces);
	}

	public CovList getTokenList() {
		return this.tokenList;
	}

	public void setTokenList(CovList tokenList) {
		this.tokenList = tokenList;
		super.setLabel(this.tokenList.toString());
	}

	public void addTokens(double[] newTokens) {
		this.tokenList.addTokens(newTokens);
		super.setLabel(this.tokenList.toString());
	}
}
