package graph.algorithms;

import edu.uci.ics.jung.graph.Vertex;
import graph.ContainerSingelton;
import graph.GraphContainer;
import graph.GraphInstance;
import gui.MainWindow;
import gui.MainWindowSingelton;

import java.awt.Color;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

import biologicalElements.GraphElementAbstract;
import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class KCoreAnalysis {

	MainWindow w = MainWindowSingelton.getInstance();
	GraphContainer con = ContainerSingelton.getInstance();
	GraphInstance graphInstance = new GraphInstance();
	Pathway p = con.getPathway(w.getCurrentPathway());
	HashMap<Vertex, Integer> vertices = new HashMap<Vertex, Integer>();
	HashMap<Integer, Color> colourMap = new HashMap<Integer, Color>();
	Random numGen = new Random();

	public KCoreAnalysis() {

		Vector v = p.getAllNodesAsVector();
		int maxDegree = 0;

		for (int i = 0; i < v.size(); i++) {
			GraphElementAbstract gea = (GraphElementAbstract) v.get(i);
			Vertex vertex = ((BiologicalNodeAbstract) gea).getVertex();
			vertices.put(vertex, vertex.inDegree());
			if (!colourMap.containsKey(vertex.inDegree())) {
				colourMap.put(vertex.inDegree(), new Color(numGen.nextInt(256),
						numGen.nextInt(256), numGen.nextInt(256)));

			}
			if (vertex.inDegree() > maxDegree)
				maxDegree = vertex.inDegree();

		}

		// Color[] colors = new Color[maxDegree + 1];
		// Color start = Color.red;
		// Color end = Color.blue;
		// for (int i = 1, j = maxDegree + 1; i < maxDegree + 1; i++) {
		// int r = start.getRed() + (end.getRed() - start.getRed()) * i / j;
		// int g = start.getGreen() + (end.getGreen() - start.getGreen()) * i
		// / j;
		// int b = start.getBlue() + (end.getBlue() - start.getBlue()) * i / j;
		// colors[i] = new Color(r, g, b);
		// }

		for (int i = 0; i < v.size(); i++) {
			BiologicalNodeAbstract gea = (BiologicalNodeAbstract) v.get(i);
			Vertex vertex = gea.getVertex();
			if (vertices.get(vertex) == 1)
				gea.setReference(true);
			gea.setColor(colourMap.get(vertices.get(vertex)));
		}
	}
}
