package petriNet;

import java.awt.Color;

import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.graph.Vertex;
import graph.jung.graphDrawing.VertexShapes;

public class CovNode extends BiologicalNodeAbstract {

	private CovList tokenList;
	
	public CovNode(String label, String name, Vertex vertex, int numberPlaces) {
		super(label, name, vertex);
		shapes = new VertexShapes();
		setShape(shapes.getEllipse(vertex));
		setColor(Color.WHITE);
		setAbstract(false);
		setReference(false);
		this.tokenList = new CovList(numberPlaces);
		//super.setLabel(this.tokenList.toString());
	}
	public void rebuildShape(VertexShapes vs) {
		setShape(vs.getEllipse(getVertex()));
		//super.setLabel(this.tokenList.toString());
	}
	public CovList getTokenList() {
		return this.tokenList;
	}
	public void setTokenList(CovList tokenList) {
		this.tokenList = tokenList;
		super.setLabel(this.tokenList.toString());
	}
	
	public void addTokens(double[] newTokens){
		this.tokenList.addTokens(newTokens);
		super.setLabel(this.tokenList.toString());
	}
}
