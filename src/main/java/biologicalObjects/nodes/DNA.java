package biologicalObjects.nodes;

import java.util.List;

import biologicalElements.Elementdeclerations;

public class DNA extends BiologicalNodeAbstract implements NodeWithNTSequence, NodeWithLogFC {
	private String ntSequence = "";
	private Double logFC = 0.0;

	public DNA(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.dna);
		attributeSetter(this.getClass().getSimpleName(), this);
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
		List<String> list = super.getTransformationParameters();
		list.add("logFC");
		return list;
	}

	public String getTransformationParameterValue(String parameter) {
		switch (parameter) {
		case "logFC":
			return this.getLogFC().toString();
		}
		return super.getTransformationParameterValue(parameter);
	}
}
