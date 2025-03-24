package biologicalObjects.nodes;

import java.util.List;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;

public class DNA extends BiologicalNodeAbstract implements NodeWithNTSequence, NodeWithLogFC {
	private String ntSequence = "";
	private Double logFC = 0.0;

	public DNA(final String label, final String name, final Pathway parent) {
		super(label, name, Elementdeclerations.dna, parent);
		attributeSetter();
	}

	@Override
	public String getNtSequence() {
		return ntSequence;
	}

	@Override
	public void setNtSequence(String ntSequence) {
		this.ntSequence = ntSequence;
	}

	@Override
	public Double getLogFC() {
		return logFC;
	}

	@Override
	public void setLogFC(Double logFC) {
		this.logFC = logFC;
	}

	@Override
	public List<String> getTransformationParameters() {
		List<String> list = super.getTransformationParameters();
		list.add("logFC");
		return list;
	}

	@Override
	public String getTransformationParameterValue(String parameter) {
		switch (parameter) {
		case "logFC":
			return this.getLogFC().toString();
		}
		return super.getTransformationParameterValue(parameter);
	}
}
