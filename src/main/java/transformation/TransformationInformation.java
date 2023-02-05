package transformation;

import java.util.List;
import java.util.Map;

import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.petriNet.PNNode;

public class TransformationInformation {

	private List<Match> matches;
	private Map<BiologicalNodeAbstract, PNNode> bnToPnMapping;
	private Pathway petriNet;

	public List<Match> getMatches() {
		return matches;
	}

	public void setMatches(List<Match> matches) {
		this.matches = matches;
	}

	public Map<BiologicalNodeAbstract, PNNode> getBnToPnMapping() {
		return bnToPnMapping;
	}

	public void setBnToPnMapping(Map<BiologicalNodeAbstract, PNNode> bnToPnMapping) {
		this.bnToPnMapping = bnToPnMapping;
	}

	public Pathway getPetriNet() {
		return petriNet;
	}

	public void setPetriNet(Pathway petriNet) {
		this.petriNet = petriNet;
	}
}
