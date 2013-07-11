package graph.filter;

//import edu.uci.ics.jung.graph.Vertex;
import graph.GraphInstance;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import biologicalElements.Elementdeclerations;
import biologicalElements.GraphElementAbstract;
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class FilterSettings {

	LinkedList list = new LinkedList();

	GraphInstance graphInstance = new GraphInstance();

	Pathway pw = null;

	public FilterSettings() {

		Elementdeclerations dec = new Elementdeclerations();
		list.add(new Filter("references", true));

		Iterator it = dec.getAllNodeDeclarations().iterator();

		while (it.hasNext()) {
			list.add(new Filter(it.next().toString(), true));
		}
	}

	public void addFilter(String element, boolean value) {
		Filter f = new Filter(element, value);
		list.add(f);
	}

	public boolean getFilterValue(String element) {

		Iterator it = list.iterator();
		while (it.hasNext()) {

			Filter f = (Filter) it.next();
			if (f.getElement().equals(element)) {
				return f.isValue();
			}
		}
		return false;
	}

	public void setFilterValue(String element, boolean value) {

		Iterator it = list.iterator();
		while (it.hasNext()) {

			Filter f = (Filter) it.next();
			if (f.getElement().equals(element)) {
				f.setValue(value);
			}
		}
		applySettings(element, value);
	}


	
	private void applySettings(String element, boolean value) {

		pw = graphInstance.getPathway();
		HashMap map = pw.getBiologicalElements();
		Iterator it = map.values().iterator();

		if (element.equals("references")) {

			while (it.hasNext()) {
				Object obj = it.next();
				GraphElementAbstract gea = (GraphElementAbstract) obj;
				if (gea.isReference()) {
					gea.setVisible(value);
				}
			}
		} else {

			while (it.hasNext()) {
				GraphElementAbstract gea = (GraphElementAbstract) it.next();
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