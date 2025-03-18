package biologicalObjects.nodes;

import java.util.Vector;

import biologicalElements.ElementDeclarations;
import biologicalElements.Pathway;

public class Gene extends BiologicalNodeAbstract implements NodeWithNTSequence {
	private String ntSequence = "";
	private final Vector<String[]> proteins = new Vector<>();
	private final Vector<String[]> enzymes = new Vector<>();
	private boolean hasProteins = false;
	private boolean hasEnzymes = false;

	public Gene(final String label, final String name, final Pathway pathway) {
		super(label, name, ElementDeclarations.gene, pathway);
		attributeSetter();
	}

	public String getNtSequence() {
		return ntSequence;
	}

	public void setNtSequence(String ntSequence) {
		this.ntSequence = ntSequence;
	}

	public Vector<String[]> getProteins() {
		return proteins;
	}

	public Vector<String[]> getEnzymes() {
		return enzymes;
	}

	public boolean hasProteins() {
		return hasProteins;
	}

	public boolean hasEnzymes() {
		return hasEnzymes;
	}

	public void addProtein(String[] proteinID) {
		if (!proteins.contains(proteinID)) {
			proteins.add(proteinID);
		}
		hasProteins = true;
	}

	public void addEnzyme(String[] enzymeID) {
		if (!enzymes.contains(enzymeID)) {
			enzymes.add(enzymeID);
		}
		hasEnzymes = true;
	}
}
