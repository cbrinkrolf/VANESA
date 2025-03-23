package biologicalObjects.nodes;

import java.util.List;

import biologicalElements.Elementdeclerations;

public class DNA extends BiologicalNodeAbstract implements NodeWithNTSequence, NodeWithLogFC {
	private String ntSequence = "";
	private Double logFC = 0.0;

	public DNA(final String label, final String name) {
		super(label, name, Elementdeclerations.dna);
		attributeSetter(getClass().getSimpleName(), this);
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
