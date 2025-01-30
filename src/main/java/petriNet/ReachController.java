package petriNet;

import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.petriNet.Place;

public class ReachController {
	private final Pathway pw;

	public ReachController(final Pathway pw) {
		this.pw = pw;
		if (isBounded()) {
			// Reach r = new Reach();
			// System.out.println("beschraenkt");
		} else {
			// System.out.println("(partiell) unbeschraenkt");
		}
		new Cov();
	}

	private boolean isBounded() {
        for (final BiologicalNodeAbstract bna : pw.getAllGraphNodes()) {
            if (bna instanceof Place) {
                final Place p = (Place) bna;
                if (p.getTokenMax() <= 0.0) {
                    return false;
                }
            }
        }
		return true;
	}
}
