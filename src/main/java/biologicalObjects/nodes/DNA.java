package biologicalObjects.nodes;

import java.util.List;

import biologicalElements.ElementDeclarations;
import biologicalElements.Pathway;

public class DNA extends BiologicalNodeAbstract implements NodeWithNTSequence, NodeWithLogFC {
	private String ntSequence = "";
	private Double logFC = 0d;

	public DNA(final String label, final String name, final Pathway pathway) {
		super(label, name, ElementDeclarations.dna, pathway);
		attributeSetter();
	}

	public String getNtSequence() {
		return ntSequence;
	}

	public void setNtSequence(String ntSequence) {
		this.ntSequence = ntSequence;
	}

	public Double getLogFC() {
		return logFC;
	}

	public void setLogFC(Double logFC) {
		this.logFC = logFC;
	}

	@Override
	public List<String> getTransformationParameters() {
		final List<String> list = super.getTransformationParameters();
		list.add("logFC");
		return list;
	}

	public String getTransformationParameterValue(String parameter) {
		if ("logFC".equals(parameter)) {
			return logFC.toString();
		}
		return super.getTransformationParameterValue(parameter);
	}
}
