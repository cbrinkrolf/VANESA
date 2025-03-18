package biologicalObjects.nodes;

import biologicalElements.Pathway;

import java.util.List;

public class RNA extends BiologicalNodeAbstract implements NodeWithNTSequence, NodeWithLogFC {
	private String ntSequence = "";
	private Double logFC = 0d;

	public RNA(final String label, final String name, final Pathway pathway) {
		this(label, name, "", pathway);
	}

	protected RNA(final String label, final String name, final String biologicalElement, final Pathway pathway) {
		super(label, name, biologicalElement, pathway);
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
		final List<String> list = super.getTransformationParameters();
		list.add("logFC");
		return list;
	}

	@Override
	public String getTransformationParameterValue(final String parameter) {
		if ("logFC".equals(parameter)) {
			return logFC.toString();
		}
		return super.getTransformationParameterValue(parameter);
	}
}
