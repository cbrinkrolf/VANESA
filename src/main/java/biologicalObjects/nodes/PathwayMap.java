package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;

public class PathwayMap extends BiologicalNodeAbstract {
	private boolean specification;
	private Pathway pathwayLink = null;

	public PathwayMap(final String label, final String name) {
		super(label, name, Elementdeclerations.pathwayMap);
		attributeSetter(getClass().getSimpleName(), this);
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
