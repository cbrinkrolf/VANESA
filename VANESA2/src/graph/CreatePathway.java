package graph;

import java.awt.Cursor;
import java.util.HashMap;
import java.util.Iterator;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import gui.MainWindow;

public class CreatePathway {

	MainWindow w = MainWindow.getInstance();
	GraphContainer con = GraphContainer.getInstance();
	String pathwayName;
	Pathway pw;
	Pathway parent=null;

	public CreatePathway(String title, Pathway parent) {
		this.parent = parent;
		pathwayName = title;
		buildPathway();
	}

	public CreatePathway(String title) {
		pathwayName = title;
		buildPathway();
	}

	public CreatePathway() {
		pathwayName = "Untitled";
		buildPathway();
	}
	
	public CreatePathway(Pathway pathway){
		this.pathwayName = pathway.getName();
		this.buildPathway();
		
		HashMap<BiologicalNodeAbstract, BiologicalNodeAbstract> nodes = new HashMap<BiologicalNodeAbstract, BiologicalNodeAbstract>();
		
		Iterator<BiologicalNodeAbstract> it = pathway.getAllGraphNodes().iterator();
		BiologicalNodeAbstract bna;
		BiologicalNodeAbstract clone;
		while(it.hasNext()){
			bna = it.next();
			clone =(BiologicalNodeAbstract) bna.clone();
			nodes.put(bna, clone);
			pw.addVertex(clone, pathway.getGraph().getVertexLocation(bna));
		}
		
		it = pw.getAllGraphNodes().iterator();
		while(it.hasNext()){
			bna = it.next();
			if(bna.hasRef()){
				bna.setRef(nodes.get(bna.getRef()));
			}
			//pw.addVertex(clone, pathway.getGraph().getVertexLocation(bna));
		}
		
		
		Iterator<BiologicalEdgeAbstract> it2 = pathway.getAllEdges().iterator();
		
		BiologicalEdgeAbstract bea;
		BiologicalEdgeAbstract beaClone;
		while(it2.hasNext()){
			bea = it2.next();
			beaClone = bea.clone();
			beaClone.setFrom(nodes.get(bea.getFrom()));
			beaClone.setTo(nodes.get(bea.getTo()));
			pw.addEdge(beaClone);
		}
		
		
	}

	private void buildPathway() {
		w.returnFrame().setCursor(new Cursor(Cursor.WAIT_CURSOR));
		//Pathway newPW = null;
		if (parent == null) {
			new Pathway(pathwayName);
		} else {
			new Pathway(pathwayName, parent);
		}
		String newPathwayName = con.addPathway(pathwayName, new Pathway(
				pathwayName));
		pw = con.getPathway(newPathwayName);
		w.addTab(pw.getTab().getTitelTab());
		w.returnFrame().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}

	/*public void addVertex(Object node) {
		pw.getGraph().addVertexLabel(node);
	}*/

	public Pathway getPathway() {
		return pw;
	}
	
	
}
