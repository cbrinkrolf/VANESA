package configurations;


public class NetworkSettings {

	private Integer nodeLabel;
	private Integer edgeLabel;
	private boolean backgroundColor = false;
	private boolean drawEdges = true;
	private int edgeopacity = 255;

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
	
	public void setDrawEdges(boolean draw){
		this.drawEdges = draw;
	}
	
	public boolean getDrawEdges(){
		return drawEdges;
	}
	
	public int getEdgeOpacity(){
		return edgeopacity;
	}
	
	public void setEdgeOpacity(int newopacity){
		this.edgeopacity = newopacity;
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
