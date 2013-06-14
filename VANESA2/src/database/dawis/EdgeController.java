package database.dawis;

import java.awt.Color;

import edu.uci.ics.jung.graph.Vertex;

import graph.jung.classes.MyGraph;

import biologicalElements.Pathway;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.edges.KEGGEdge;
import biologicalObjects.edges.ReactionEdge;
import biologicalObjects.edges.ReactionPair;
import biologicalObjects.edges.ReactionPairEdge;


/**
 * controls edges
 * 
 * @author Olga
 * 
 */
public class EdgeController {

	OrganismController orgController = new OrganismController();
	Pathway pw;
	MyGraph myGraph;

	public EdgeController(Pathway path) {
		this.pw = path;
		myGraph = pw.getGraph();
	}

	/**
	 * build simple edge between two vertices
	 * 
	 * @param one
	 * @param two
	 * @param directed
	 */
	public void buildSimpleEdge(Vertex one, Vertex two) {

		if (!two.getNeighbors().contains(one)) {
			BiologicalEdgeAbstract r = new BiologicalEdgeAbstract(myGraph
					.createEdge(one, two, false), "", "");
			r.setDirected(false);
			r.setReference(true);
			r.setHidden(false);
			r.setVisible(true);

			pw.addElement(r);
		}

	}

	/**
	 * build referenced edge between two vertices
	 * 
	 * @param one
	 * @param two
	 * @param directed
	 */
	public void buildReferencedEdge(Vertex one, Vertex two) {

		BiologicalEdgeAbstract r = new BiologicalEdgeAbstract(myGraph
				.createEdge(one, two, false), "", "");
		r.setDirected(false);
		r.setReference(false);
		r.setColor(Color.blue);
		r.setHidden(false);
		r.setVisible(true);

		pw.addElement(r);

	}

	public ReactionPair createRPairEdge(Vertex from, Vertex to,
			ReactionPairEdge edge) {

		ReactionPair rPairEdge = null;

		rPairEdge = new ReactionPair(myGraph.createEdge(from, to, false), "",
				edge.getName());
		rPairEdge.setReference(false);
		rPairEdge.setDirected(false);
		rPairEdge.setReactionPairEdge(edge);
		rPairEdge.hasReactionPairEdge(true);

		pw.addElement(rPairEdge);

		return rPairEdge;
	}

	/**
	 * create directed edge
	 * 
	 * @param from
	 * @param to
	 * @param edge
	 * @param s
	 * @return
	 */
	public ReactionEdge createReactionEdge(Vertex from, Vertex to, KEGGEdge edge) {

		ReactionEdge reactionEdge = null;

		reactionEdge = new ReactionEdge(myGraph.createEdge(from, to, true),
				edge.getKEEGReactionID(), edge.getName());
		reactionEdge.setReference(false);
		reactionEdge.setDirected(true);
		reactionEdge.hasKEGGEdge(true);
		reactionEdge.setKeggEdge(edge);

		pw.addElement(reactionEdge);

		return reactionEdge;
	}

}
