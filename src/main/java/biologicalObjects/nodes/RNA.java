package biologicalObjects.nodes;

import java.util.List;

public class RNA extends BiologicalNodeAbstract {
	private String ntSequence = "";
	private Double logFC = 0.0;

	public RNA(String label, String name) {
		super(label, name);
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
