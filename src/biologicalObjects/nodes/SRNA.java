package biologicalObjects.nodes;

import java.awt.Color;

import biologicalElements.Elementdeclerations;
//import edu.uci.ics.jung.graph.Vertex;


public class SRNA extends RNA{

	private String tarbase_DS="";
	private String tarbase_IS="";
	private String tarbase_ensemble="";
	private String tarbase_accession="";

	
	public SRNA(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.sRNA);
		setDefaultColor(Color.ORANGE);
	}

	public void setTarbase_DS(String tarbase_DS) {
		this.tarbase_DS = tarbase_DS;
	}

	public String getTarbase_DS() {
		return tarbase_DS;
	}

	public void setTarbase_IS(String tarbase_IS) {
		this.tarbase_IS = tarbase_IS;
	}

	public String getTarbase_IS() {
		return tarbase_IS;
	}

	public void setTarbase_ensemble(String tarbase_ensemble) {
		this.tarbase_ensemble = tarbase_ensemble;
	}

	public String getTarbase_ensemble() {
		return tarbase_ensemble;
	}

	public void setTarbase_accession(String tarbase_accession) {
		this.tarbase_accession = tarbase_accession;
	}

	public String getTarbase_accession() {
		return tarbase_accession;
	}



}
