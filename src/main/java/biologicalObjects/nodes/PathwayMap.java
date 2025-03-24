package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;

public class PathwayMap extends BiologicalNodeAbstract {
	private boolean specification;
	private Pathway pathwayLink = null;

	public PathwayMap(final String label, final String name, final Pathway parent) {
		super(label, name, Elementdeclerations.pathwayMap, parent);
		attributeSetter();
	}

	public boolean isSpecification() {
		return specification;
	}

	public void setSpecification(boolean specification) {
		this.specification = specification;
	}

	public Pathway getPathwayLink() {
		return pathwayLink;
	}

	public void setPathwayLink(Pathway pathwayLink) {
		this.pathwayLink = pathwayLink;
	}
}
