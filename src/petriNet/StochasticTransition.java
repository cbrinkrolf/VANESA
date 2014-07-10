package petriNet;

import java.awt.Color;

import biologicalElements.Elementdeclerations;
//import edu.uci.ics.jung.graph.Vertex;

public class StochasticTransition extends Transition{

	// oder eine ein anderer Datentyp
	private String distribution = "exp";
	
	public StochasticTransition(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.stochasticTransition);
		setModellicaString("PNlib.TS"); 
		 this.setDefaultColor(Color.DARK_GRAY);
	}

	public String getDistribution() {
		return distribution;
	}

	public void setDistribution(String distribution) {
		this.distribution = distribution;
	}
	
	
	public void changeDistribution(){

		if(distribution.equals("exp")){
			this.distribution = "norm";
		}else this.distribution = "exp";
	}
}
