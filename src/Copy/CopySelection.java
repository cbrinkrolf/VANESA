package Copy;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Vector;

import javax.swing.JOptionPane;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import graph.GraphInstance;
import graph.jung.classes.MyGraph;
import gui.MainWindow;

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

		Pathway pw = new GraphInstance().getPathway();
		if (petriNet ^ pw.isPetriNet()) {
			JOptionPane
					.showMessageDialog(
							MainWindow.getInstance(),
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
		HashMap<BiologicalNodeAbstract, BiologicalNodeAbstract> map = new HashMap<BiologicalNodeAbstract, BiologicalNodeAbstract>();
		
		BiologicalNodeAbstract bna1;
		BiologicalNodeAbstract bna2;
		
		BiologicalEdgeAbstract bea2;
		for (BiologicalEdgeAbstract bea : beas) {
			//System.out.println("bla");
			/*Edge edge = new UndirectedSparseEdge(map.get(bea.getEdge()
					.getEndpoints().getFirst()), map.get(bea.getEdge()
					.getEndpoints().getSecond()));
			Edge oldEdge = bea.getEdge();
			if (oldEdge instanceof DirectedSparseEdge)
				edge = new DirectedSparseEdge(map.get(bea.getEdge()
						.getEndpoints().getFirst()), map.get(bea.getEdge()
						.getEndpoints().getSecond()));
			bea.setEdge(edge);*/
			//System.out.println("bea: "+bea);
			if(!map.containsKey(bea.getFrom())){
				bna1 = bea.getFrom().clone();
				bna1.removeAllConnectionEdges();
				//System.out.println("connedged: "+bna1.getConnectingEdges().size());
				map.put(bea.getFrom(), bna1);
				pw.addVertex(bna1, locations.get(bea.getFrom()));
				
			}else{
				bna1 = map.get(bea.getFrom());
			}
			if(!map.containsKey(bea.getTo())){
				bna2 = bea.getTo().clone();
				bna2.removeAllConnectionEdges();
				map.put(bea.getTo(), bna2);
				pw.addVertex(bna2, locations.get(bea.getTo()));
			}else{
				bna2 = map.get(bea.getTo());
			}
			//System.out.println("bna1: "+bna1.getConnectingEdges().size());
			//System.out.println("bna2: "+bna2.getConnectingEdges().size());
			bea2 = bea.clone();
			bea2.setFrom(bna1);
			bea2.setTo(bna2);
			
			
			pw.addEdge(bea2);
			//System.out.println(bea2.isClone());
			//System.out.println("durch "+pw.getAllEdges().size());
			
			//bea.setEdge(oldEdge);
		}
		
		for (BiologicalNodeAbstract bna : bnas) {
			if(!map.containsKey(bna)){
				bna1 = bna.clone();
				bna1.removeAllConnectionEdges();
				//System.out.println("connedged: "+bna1.getConnectingEdges().size());
				map.put(bna, bna1);
				pw.addVertex(bna1, locations.get(bna));
			}
		}
		pw.updateMyGraph();

		MainWindow.getInstance().updateElementTree();
	}
}