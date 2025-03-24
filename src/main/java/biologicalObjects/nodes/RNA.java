package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;

import java.util.List;

public class RNA extends BiologicalNodeAbstract implements NodeWithNTSequence, NodeWithLogFC {
	private String ntSequence = "";
	private Double logFC = 0.0;

	public RNA(final String label, final String name) {
		super(label, name, Elementdeclerations.rna);
		attributeSetter();
	}

	protected RNA(final String label, final String name, final String biologicalElement) {
		super(label, name, biologicalElement);
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
