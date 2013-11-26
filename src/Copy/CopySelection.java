package Copy;

//import edu.uci.ics.jung.graph.Edge;
//import edu.uci.ics.jung.graph.Vertex;
//import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
//import edu.uci.ics.jung.graph.impl.SparseVertex;
//import edu.uci.ics.jung.graph.impl.UndirectedSparseEdge;
//import edu.uci.ics.jung.utils.UserDataContainer.CopyAction;
//import edu.uci.ics.jung.visualization.AbstractLayout;
//import edu.uci.ics.jung.visualization.Coordinates;
//import edu.uci.ics.jung.visualization.DefaultSettableVertexLocationFunction;
//import edu.uci.ics.jung.visualization.Layout;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import graph.GraphInstance;
import graph.jung.classes.MyGraph;
import gui.MainWindowSingelton;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Vector;

import javax.swing.JOptionPane;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
//import edu.uci.ics.jung.visualization.subLayout.SubLayoutDecorator;

public class CopySelection {

	private HashSet<BiologicalNodeAbstract> bnas = new HashSet<BiologicalNodeAbstract>();
	private Vector<BiologicalEdgeAbstract> beas = new Vector<BiologicalEdgeAbstract>();
	private boolean petriNet;
	private MyGraph myGraph = new GraphInstance().getPathway().getGraph();
	private Map<BiologicalNodeAbstract, Point2D> locations = new HashMap<BiologicalNodeAbstract, Point2D>();

	public CopySelection(HashSet<BiologicalNodeAbstract> vertices, HashSet<BiologicalEdgeAbstract> edges) {
		Pathway pw = new GraphInstance().getPathway();
		petriNet = pw.isPetriNet();
		for (BiologicalNodeAbstract v : vertices) {
			locations.put(v, myGraph.getVertexLocation(v));
			bnas.add(v);
		}
		for (BiologicalEdgeAbstract o : pw.getAllEdges()) {
			if(bnas.contains(o.getFrom()) && bnas.contains(o.getTo())){
				beas.add((BiologicalEdgeAbstract) o);
			}
		}
	}

	public void paste() {

		VisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv = GraphInstance.getMyGraph()
				.getVisualizationViewer();
		Pathway pw = new GraphInstance().getPathway();
		if (petriNet ^ pw.isPetriNet()) {
			JOptionPane
					.showMessageDialog(
							MainWindowSingelton.getInstance(),
							"Copy-Paste is not possible from biological graph to petri net and vice versa!",
							"Operation not possible...",
							JOptionPane.ERROR_MESSAGE);
			return;
		}
		//Layout layout = vv.getGraphLayout();
		//for (Iterator iterator = vv.getGraphLayout().getGraph().getVertices()
		//		.iterator(); iterator.hasNext();) {
		//	layout.lockVertex((Vertex) iterator.next());
			//GraphInstance.getMyGraph().lockVertices();
		//}
			
		//HashMap<Vertex, Vertex> map = new HashMap<Vertex, Vertex>();
		
		for (BiologicalNodeAbstract bna : bnas) {
			//Vertex vertex = new SparseVertex();
			//Object key = ((AbstractLayout) ((SubLayoutDecorator) layout)
		//			.getDelegate()).getBaseKey();
			//Object datum = new Coordinates(vv.inverseTransform(
			//		locations.get(bna.getVertex())).getX(), vv
			//		.inverseTransform(locations.get(bna.getVertex())).getY());
			//vertex.setUserDatum(key, datum, new CopyAction.Clone());
			//Vertex oldVertex = bna.getVertex();
			//bna.setVertex(vertex);
			new GraphInstance().getPathway().addVertex(bna, locations.get(bna));
			//vv.getGraphLayout().getGraph().addVertex(bna.getVertex());
			//bna.setVertex(oldVertex);
			//map.put(oldVertex, vertex);
			vv.getPickedVertexState().pick(bna, true);
		}
		for (BiologicalEdgeAbstract bea : beas) {
			/*Edge edge = new UndirectedSparseEdge(map.get(bea.getEdge()
					.getEndpoints().getFirst()), map.get(bea.getEdge()
					.getEndpoints().getSecond()));
			Edge oldEdge = bea.getEdge();
			if (oldEdge instanceof DirectedSparseEdge)
				edge = new DirectedSparseEdge(map.get(bea.getEdge()
						.getEndpoints().getFirst()), map.get(bea.getEdge()
						.getEndpoints().getSecond()));
			bea.setEdge(edge);*/
			pw.addEdge(bea);
			//bea.setEdge(oldEdge);
		}

		MainWindowSingelton.getInstance().updateElementTree();
		MainWindowSingelton.getInstance().updateFilterView();
		
		
		/*pw = new GraphInstance().getPathway();
		for (Iterator iterator = pw.getAllNodes().iterator(); iterator
				.hasNext();) {
			BiologicalNodeAbstract bna = (BiologicalNodeAbstract) iterator
					.next();
			bna.rebuildShape(new VertexShapes());
		}*/
		//for (Iterator iterator = vv.getGraphLayout().getGraph().getVertices()
		//		.iterator(); iterator.hasNext();) {
			//layout.unlockVertex((Vertex) iterator.next());
		//}
	}
}