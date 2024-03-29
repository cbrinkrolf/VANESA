package petriNet;

import java.util.Collection;
import java.util.Iterator;

import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.petriNet.Place;
import graph.GraphInstance;

public class ReachController {

	private Pathway pw;
	private Collection<BiologicalNodeAbstract> nodes;

	public ReachController() {

		this.pw = GraphInstance.getPathway();
		this.nodes = this.pw.getAllGraphNodes();

		if (this.isBounded()) {
			// Reach r = new Reach();
			// System.out.println("beschraenkt");
		} else {
			// System.out.println("(partiell) unbeschraenkt");

		}

		new Cov();
	}

	private boolean isBounded() {
		// boolean bounded = false;

		Iterator<BiologicalNodeAbstract> it = this.nodes.iterator();
		Place p;
		BiologicalNodeAbstract bna;
		while (it.hasNext()) {
			bna = it.next();
			if (bna instanceof Place) {
				p = (Place) bna;
				// System.out.println(p.getName());
				if (p.getTokenMax() <= 0.0) {
					return false;
				}
			}
		}
		return true;
	}
}
