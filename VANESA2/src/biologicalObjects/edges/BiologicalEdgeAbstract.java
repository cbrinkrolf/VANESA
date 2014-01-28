package biologicalObjects.edges;

import java.awt.Color;

import biologicalElements.GraphElementAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;

//import edu.uci.ics.jung.graph.Edge;

public abstract class BiologicalEdgeAbstract extends GraphElementAbstract
		implements Cloneable {

	// private Edge edge;
	private KEGGEdge keggEdge;
	ReactionPairEdge reactionPairEdge;
	private boolean isDirected;

	private boolean isWeighted = false;

	public BiologicalNodeAbstract getFrom() {
		return from;
	}

	public void setFrom(BiologicalNodeAbstract from) {
		this.from = from;
	}

	public BiologicalNodeAbstract getTo() {
		return to;
	}

	public void setTo(BiologicalNodeAbstract to) {
		this.to = to;
	}

	private BiologicalNodeAbstract from;
	private BiologicalNodeAbstract to;

	public boolean isWeighted() {
		return isWeighted;
	}

	public void setWeighted(boolean isWeighted) {
		this.isWeighted = isWeighted;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	private int weight = 0;

	private SBMLEdge sbml = new SBMLEdge();

	public SBMLEdge getSbml() {
		sbml.setFrom(this.getFrom().getID() + "");
		sbml.setTo(this.getTo().getID() + "");
		sbml.setEdge(this.getID() + "");
		return sbml;
	}

	public void setSbml(SBMLEdge sbml) {
		this.sbml = sbml;
	}

	public BiologicalEdgeAbstract(String label, String name,
			BiologicalNodeAbstract from, BiologicalNodeAbstract to) {

		// this.edge=edge;
		super(label, name);
		// setName(name.toLowerCase());
		// setLabel(label.toLowerCase());
		setIsEdge(true);
		this.from = from;
		this.to = to;

		/*
		 * sbml.setName(name.toLowerCase()); //sbml.setEdge(edge.toString());
		 * sbml.setLabel(label.toLowerCase()); sbml.setIsAbstract("false");
		 * sbml.setIsDirected("false");
		 * sbml.setFrom(from.getName().toLowerCase());
		 * sbml.setTo(to.getName().toLowerCase());
		 */

	}

	// public Edge getEdge() {
	// return edge;
	// }
	// public void setEdge(Edge edge) {
	// this.edge = edge;
	// }
	public boolean isDirected() {
		return isDirected;
	}

	public void setDirected(boolean isDirected) {
		this.isDirected = isDirected;
	}

	public KEGGEdge getKeggEdge() {
		return keggEdge;
	}

	public void setKeggEdge(KEGGEdge keggEdge) {
		this.keggEdge = keggEdge;
	}

	@Override
	public Color getColor() {

		if (super.isReference()) {
			return Color.LIGHT_GRAY;
		} else {
			return super.getColor();
		}
	}

	public void setReactionPairEdge(ReactionPairEdge reactPEdge) {
		this.reactionPairEdge = reactPEdge;
	}

	public ReactionPairEdge getReactionPairEdge() {
		return reactionPairEdge;
	}

	@Override
	public BiologicalEdgeAbstract clone() {
		return (BiologicalEdgeAbstract) super.clone();
	}
}
