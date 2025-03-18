package biologicalObjects.nodes;

import biologicalElements.ElementDeclarations;
import biologicalElements.Pathway;

public class SRNA extends RNA {
	private String tarbaseDS = "";
	private String tarbaseIS = "";
	private String tarbaseEnsemble = "";
	private String tarbaseAccession = "";

	public SRNA(final String label, final String name, final Pathway pathway) {
		super(label, name, ElementDeclarations.sRNA, pathway);
		attributeSetter();
	}

	public String getTarbaseDS() {
		return tarbaseDS;
	}

	public void setTarbaseDS(String tarbaseDS) {
		this.tarbaseDS = tarbaseDS;
	}

	public String getTarbaseIS() {
		return tarbaseIS;
	}

	public void setTarbaseIS(String tarbaseIS) {
		this.tarbaseIS = tarbaseIS;
	}

	public String getTarbaseEnsemble() {
		return tarbaseEnsemble;
	}

	public void setTarbaseEnsemble(String tarbaseEnsemble) {
		this.tarbaseEnsemble = tarbaseEnsemble;
	}

	public String getTarbaseAccession() {
		return tarbaseAccession;
	}

	public void setTarbaseAccession(String tarbaseAccession) {
		this.tarbaseAccession = tarbaseAccession;
	}
}
