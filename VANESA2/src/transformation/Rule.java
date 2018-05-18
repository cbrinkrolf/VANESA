package transformation;

import java.util.ArrayList;
import java.util.List;

public class Rule {
	
	private List<String> nodeNames = new ArrayList<String>();
	private List<String> nodeTypes = new ArrayList<String>();
	
	private List<String> edgeNames = new ArrayList<String>();
	private List<String> edgeTypes = new ArrayList<String>();
	private List<String> edgeFrom = new ArrayList<String>();
	private List<String> edgeTo = new ArrayList<String>();
	
	
	
	public List<String> getNodeNames() {
		return nodeNames;
	}
	public List<String> getNodeTypes() {
		return nodeTypes;
	}
	public List<String> getEdgeNames() {
		return edgeNames;
	}
	public List<String> getEdgeTypes() {
		return edgeTypes;
	}
	public List<String> getEdgeFrom() {
		return edgeFrom;
	}
	public List<String> getEdgeTo() {
		return edgeTo;
	}
	
	
	

}
