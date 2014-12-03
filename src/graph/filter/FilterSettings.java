package graph.filter;

//import edu.uci.ics.jung.graph.Vertex;
import graph.GraphInstance;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import biologicalElements.Elementdeclerations;
import biologicalElements.GraphElementAbstract;
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class FilterSettings {

	LinkedList<Filter> list = new LinkedList<Filter>();

	GraphInstance graphInstance = new GraphInstance();

	Pathway pw = null;

	public FilterSettings() {

		Elementdeclerations dec = new Elementdeclerations();
		list.add(new Filter("references", true));

		Iterator<String> it = dec.getAllNodeDeclarations().iterator();

		while (it.hasNext()) {
			list.add(new Filter(it.next(), true));
		}
	}

	public void addFilter(String element, boolean value) {
		Filter f = new Filter(element, value);
		list.add(f);
	}

	public boolean getFilterValue(String element) {

		Iterator<Filter> it = list.iterator();
		Filter f;
		while (it.hasNext()) {

			 f = it.next();
			if (f.getElement().equals(element)) {
				return f.isValue();
			}
		}
		return false;
	}

	public void setFilterValue(String element, boolean value) {

		Iterator<Filter> it = list.iterator();
		Filter f;
		while (it.hasNext()) {

			f = it.next();
			if (f.getElement().equals(element)) {
				f.setValue(value);
			}
		}
		applySettings(element, value);
	}


	
	private void applySettings(String element, boolean value) {

		pw = graphInstance.getPathway();
		HashMap<String, GraphElementAbstract> map = pw.getBiologicalElements();
		Iterator<GraphElementAbstract> it = map.values().iterator();
		GraphElementAbstract gea;
		if (element.equals("references")) {
			
			while (it.hasNext()) {
				gea = it.next();
				if (gea.isReference()) {
					gea.setVisible(value);
				}
			}
		} else {

			while (it.hasNext()) {
				gea = it.next();
				if (gea.getBiologicalElement().equals(element)) {
					if (value) {
						// gea.setHidden(false);
						gea.setVisible(true);
					} else {
						// gea.setHidden(true);
						gea.setVisible(false);

					}
				}
			}
		}

//		HashSet set_edges = pw.getAllEdges();
		Iterator<BiologicalEdgeAbstract> it2 = pw.getAllEdges().iterator();

		BiologicalEdgeAbstract bea;
		BiologicalNodeAbstract from;
		BiologicalNodeAbstract to;
		while (it2.hasNext()) {
			bea = it2.next();

			/*Vertex one = (Vertex) bea.getEdge().getEndpoints().getFirst();
			Vertex two = (Vertex) bea.getEdge().getEndpoints().getSecond();

			BiologicalNodeAbstract bna_one = (BiologicalNodeAbstract) pw
					.getNodeByVertexID(one.toString());
			BiologicalNodeAbstract bna_two = (BiologicalNodeAbstract) pw
					.getNodeByVertexID(two.toString());*/
			from = bea.getFrom();
			to = bea.getTo();
			if (!from.isVisible() || !to.isVisible()) {
				bea.setVisible(false);
			} else {
				bea.setVisible(true);
			}

		}
		// TODO visibility not set or considered while rendering
		pw.getGraph().updateGraph();
	}
}

class Filter {

	boolean value;

	String element;

	public Filter(String element, boolean value) {
		this.element = element;
		this.value = value;
	}

	public String getElement() {
		return element;
	}

	public void setElement(String element) {
		this.element = element;
	}

	public boolean isValue() {
		return value;
	}

	public void setValue(boolean value) {
		this.value = value;
	}
}