package biologicalObjects.edges;


public class SBMLEdge {

	private String edge, label, name;
	private String Elementdecleration;
	private String isAbstract ="false";
	private String isDirected = "false";
	private String from, to ;
	public String getEdge() {
		return edge;
	}
	public void setEdge(String edge) {
		this.edge = edge;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getElementdecleration() {
		return Elementdecleration;
	}
	public void setElementdecleration(String elementdecleration) {
		Elementdecleration = elementdecleration;
	}
	public String getIsAbstract() {
		return isAbstract;
	}
	public void setIsAbstract(String isAbstract) {
		this.isAbstract = isAbstract;
	}
	public String getIsDirected() {
		return isDirected;
	}
	public void setIsDirected(String isDirected) {
		this.isDirected = isDirected;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	} 
	
	
}
