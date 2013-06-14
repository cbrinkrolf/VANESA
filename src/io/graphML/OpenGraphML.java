package io.graphML;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.io.GraphMLFile;
import edu.uci.ics.jung.utils.Pair;
import graph.CreatePathway;
import graph.jung.classes.MyGraph;

import java.io.File;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import biologicalElements.Pathway;
import biologicalObjects.edges.ReactionEdge;
import biologicalObjects.nodes.Other;

public class OpenGraphML {

	private MyGraph myGraph;
	
	public OpenGraphML(File file) {

		GraphMLFile reader = new GraphMLFile();
		Graph g = reader.load(file.getAbsolutePath());	
		Pathway p = new CreatePathway().getPathway();
		myGraph = p.getGraph();
		
		stopVisualizationModel();
		
		Hashtable<Vertex,Vertex> verticesMapping = new Hashtable<Vertex,Vertex>();	
		Set vertices = g.getVertices();
		Iterator it = vertices.iterator();
		
		while(it.hasNext()){
			Vertex v = (Vertex)it.next();
			Vertex newVertex = myGraph.createNewVertex();
			
			Other o = new Other(v.toString(), v.toString(), newVertex);
			p.addElement(o);
			myGraph.moveVertex(newVertex, 10.0, 10.0);
			
			verticesMapping.put(v, newVertex);
			
		}
		
		Set edges = g.getEdges();
		it = edges.iterator();
		while(it.hasNext()){
			
			Edge e = (Edge)it.next();
			Pair pair = e.getEndpoints();
			Vertex one = (Vertex)pair.getFirst();
			Vertex two = (Vertex)pair.getSecond();
			
			Edge newEdge = myGraph.createEdge(verticesMapping.get(one), verticesMapping.get(two), true);
			ReactionEdge r = new ReactionEdge(newEdge,e.toString(), e.toString());
			p.addElement(r);
		}
		updateGraph();
		myGraph.changeToFRLayout();
		startVisualizationModel();
	}
	
	private void updateGraph() {
		myGraph.updateGraph();
	}

	private void stopVisualizationModel() {
		myGraph.lockVertices();
		myGraph.stopVisualizationModel();
	}

	private void startVisualizationModel() {
		myGraph.restartVisualizationModel();
		myGraph.unlockVertices();
	}
}
