package biologicalObjects.nodes;

import biologicalElements.ElementDeclarations;
import biologicalElements.Pathway;

public class PathwayMap extends BiologicalNodeAbstract {
	private boolean specification;
	private Pathway pathwayLink = null;

	public PathwayMap(final String label, final String name, final Pathway pathway) {
		super(label, name, ElementDeclarations.pathwayMap, pathway);
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
