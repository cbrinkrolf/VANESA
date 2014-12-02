package configurations;


public class NetworkSettings {

	private Integer nodeLabel;
	private Integer edgeLabel;
	private boolean backgroundColor = false;

	public boolean isBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(boolean backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public Integer getNodeLabel() {
		return nodeLabel;
	}

	/*public void setNodeLabel(Integer nodeLabel) {
		this.nodeLabel = nodeLabel;
		GraphInstance.getMyGraph().updateAllNodeLabels();
	}*/

	public Integer getEdgeLabel() {
		return edgeLabel;
	}

	/*public void setEdgeLabel(Integer edgeLabel) {
		this.edgeLabel = edgeLabel;
		GraphInstance.getMyGraph().updateAllEdgeLabels();
	}*/

	public NetworkSettings() {
		nodeLabel = 1;
		edgeLabel = 1;
	}
}
