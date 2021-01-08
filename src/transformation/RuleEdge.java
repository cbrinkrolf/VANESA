package transformation;

public class RuleEdge {
	
	private String name;
	private String type;
	private RuleNode from;
	private RuleNode to;
	
	public RuleEdge(String name, String type, RuleNode from, RuleNode to){
		this.name = name;
		this.type = type;
		this.from = from;
		this.to = to;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public RuleNode getFrom() {
		return from;
	}
	public void setFrom(RuleNode from) {
		this.from = from;
	}
	public RuleNode getTo() {
		return to;
	}
	public void setTo(RuleNode to) {
		this.to = to;
	}
	
	

}
