package Copy;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.SparseVertex;
import edu.uci.ics.jung.graph.impl.UndirectedSparseEdge;
import edu.uci.ics.jung.utils.UserDataContainer.CopyAction;
import edu.uci.ics.jung.visualization.AbstractLayout;
import edu.uci.ics.jung.visualization.Coordinates;
import edu.uci.ics.jung.visualization.DefaultSettableVertexLocationFunction;
import edu.uci.ics.jung.visualization.Layout;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.subLayout.SubLayoutDecorator;
import graph.GraphContainer;
import graph.ContainerSingelton;
import graph.GraphInstance;
import graph.jung.classes.MyGraph;
import graph.jung.graphDrawing.VertexShapes;
import gui.MainWindow;
import gui.MainWindowSingelton;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.SpringLayout;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class CopySelection {

	private Vector<BiologicalNodeAbstract> bnas = new Vector<BiologicalNodeAbstract>();
	private Vector<BiologicalEdgeAbstract> beas = new Vector<BiologicalEdgeAbstract>();
	private boolean petriNet;
	private MyGraph myGraph =new GraphInstance().getPathway().getGraph();
	private Map<Vertex,Point2D> locations=new HashMap<Vertex,Point2D> ();
	
	public CopySelection(Vector<Vertex> vertices) {
		Pathway pw = new GraphInstance().getPathway();
		petriNet=pw.isPetriNet();
		for (Vertex v : vertices){
			locations.put(v, 
		myGraph.getVisualizationViewer().getModel().getGraphLayout().getLocation(v));
			for (Object bna : pw.getAllNodes())
				if (((BiologicalNodeAbstract) bna).getVertex().equals(v))
					bnas.add((BiologicalNodeAbstract) bna);
		}
		for (Object o : pw.getAllEdges()) {
			Edge edge = ((BiologicalEdgeAbstract) o).getEdge();
			boolean firstIn = false;
			boolean secondIn = false;
			for (Vertex v : vertices)
				if (edge.getEndpoints().getFirst().equals(v))
					firstIn = true;
				else if (edge.getEndpoints().getSecond().equals(v))
					secondIn = true;
if (firstIn && secondIn) beas.add((BiologicalEdgeAbstract) o);
		}
	}

	public void paste() {
		
		VisualizationViewer vv = new GraphInstance().getMyGraph()
				.getVisualizationViewer();
		Pathway pw = new GraphInstance().getPathway();
		if (petriNet ^ pw.isPetriNet()){
			JOptionPane.showMessageDialog(MainWindowSingelton.getInstance(), "Copy-Paste is not possible from biological graph to petri net and vice versa!", "Operation not possible...",  JOptionPane.ERROR_MESSAGE);
			return;
		}
		Layout layout = vv.getGraphLayout();
		for (Iterator iterator = vv.getGraphLayout().getGraph().getVertices()
				.iterator(); iterator.hasNext();) {
			layout.lockVertex((Vertex) iterator.next());
		}
		HashMap<Vertex, Vertex> map = new HashMap<Vertex, Vertex>();
		for (BiologicalNodeAbstract bna : bnas) {
			Vertex vertex = new SparseVertex();
			Object key = ((AbstractLayout) ((SubLayoutDecorator) layout)
					.getDelegate()).getBaseKey();
			Object datum = new Coordinates(vv.inverseTransform(
					locations.get(bna.getVertex())).getX(), vv
					.inverseTransform(locations.get(bna.getVertex()))
					.getY());
			vertex.setUserDatum(key, datum, new CopyAction.Clone());
			Vertex oldVertex = bna.getVertex();
			bna.setVertex(vertex);
			new GraphInstance().getPathway().addElement(bna);
			vv.getGraphLayout().getGraph().addVertex(bna.getVertex());
			bna.setVertex(oldVertex);
			map.put(oldVertex, vertex);
		}
		for (BiologicalEdgeAbstract bea : beas) {
			Edge edge = new UndirectedSparseEdge(map.get(bea.getEdge()
					.getEndpoints().getFirst()), map.get(bea.getEdge()
					.getEndpoints().getSecond()));
			Edge oldEdge=bea.getEdge();
			if (oldEdge instanceof DirectedSparseEdge) edge=new DirectedSparseEdge(map.get(bea.getEdge()
					.getEndpoints().getFirst()), map.get(bea.getEdge()
							.getEndpoints().getSecond()));
			bea.setEdge(edge);
			pw.addElement(bea);
			bea.setEdge(oldEdge);
		}

		MainWindowSingelton.getInstance().updateElementTree();
		MainWindowSingelton.getInstance().updateFilterView();
		pw = new GraphInstance().getPathway();
		for (Iterator iterator = pw.getAllNodes().iterator(); iterator
				.hasNext();) {
			BiologicalNodeAbstract bna = (BiologicalNodeAbstract) iterator
					.next();
			bna.rebuildShape(new VertexShapes());
		}
		for (Iterator iterator = vv.getGraphLayout().getGraph().getVertices()
				.iterator(); iterator.hasNext();) {
			layout.unlockVertex((Vertex) iterator.next());
		}
	}
}