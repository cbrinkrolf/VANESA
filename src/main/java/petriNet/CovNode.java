package petriNet;

import java.awt.Color;

import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import graph.rendering.nodes.CircleShape;

public class CovNode extends BiologicalNodeAbstract {
	private CovList tokenList;

	public CovNode(final String label, final String name, final int numberPlaces, final Pathway pathway) {
		super(label, name, "", pathway);
		setNodeShape(new CircleShape());
		setColor(Color.WHITE);
		tokenList = new CovList(numberPlaces);
	}

	public CovList getTokenList() {
		return tokenList;
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
