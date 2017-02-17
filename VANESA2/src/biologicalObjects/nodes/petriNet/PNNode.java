package biologicalObjects.nodes.petriNet;

import biologicalObjects.nodes.BiologicalNodeAbstract;

public class PNNode extends BiologicalNodeAbstract{

	public PNNode(String label, String name) {
		super(label, name);
		this.setLabel(label);
		this.setName(name);
	}
	
	@Override
	public void setName(String name){
		super.setName(this.replace(name));
	}
	
	@Override
	public void setLabel(String label){
		super.setLabel(this.replace(label));
	}
	
	
	private String replace(String s){
		s = s.trim();
		//System.out.println(s);
		s = s.replaceAll("\\*", "_star_");
		s = s.replaceAll("\\+", "_plus_");
		s = s.replaceAll("/", "_slash_");
		s = s.replaceAll("-", "_");
		s = s.replaceAll("\\^", "_pow_");
		s = s.replaceAll("\\(", "_");
		s = s.replaceAll("\\)", "_");
		s = s.replaceAll("\\s", "_");
		s = s.replaceAll("_{2,}", "_");
		s = s.replaceAll("\\b_+", "");
		s = s.replaceAll("_+\\b", "");
		
		return s;
	}
}
