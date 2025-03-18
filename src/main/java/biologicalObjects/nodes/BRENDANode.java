package biologicalObjects.nodes;

import java.util.Vector;

public class BRENDANode {
	private String name;
	private String ecNumber;
	private String sysName;
	private String enzymeClass;
	private String reaction;
	private String kmValue;
	private String kcat;
	private String substrate;
	private String product;
	private final Vector<String> cofactor = new Vector<>();
	private final Vector<String> inhibitor = new Vector<>();
	private final Vector<String> comment = new Vector<>();

	public BRENDANode() {
		name = "";
		ecNumber = "";
		sysName = "";
		enzymeClass = "";
		reaction = "";
		kmValue = "";
		kcat = "";
		substrate = "";
		product = "";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEcNumber() {
		return ecNumber;
	}

	public void setEcNumber(String ecNumber) {
		this.ecNumber = ecNumber;
	}

	public String getSysName() {
		return sysName;
	}

	public void setSysName(String sysName) {
		this.sysName = sysName;
	}

	public String getEnzymeClass() {
		return enzymeClass;
	}

	public void setEnzymeClass(String enzymeClass) {
		this.enzymeClass = enzymeClass;
	}

	public String getReaction() {
		return reaction;
	}

	public void setReaction(String reaction) {
		this.reaction = reaction;
	}

	public String getKmValue() {
		return kmValue;
	}

	public void setKmValue(String kmValue) {
		this.kmValue = kmValue == null ? "" : kmValue;
	}

	public String getKcat() {
		return kcat;
	}

	public void setKcat(String kcat) {
		this.kcat = kcat == null ? "" : kcat;
	}

	public String getSubstrate() {
		return substrate;
	}

	public void setSubstrate(String substrate) {
		this.substrate = substrate;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getLink() {
        return "https://www.brenda-enzymes.info/enzyme.php?ecno=" + ecNumber;
    }

	public Object[][] getBrendaDetails() {
		return new Object[][]{
				{"Name", getName() }, {"SysName", getSysName() }, {"EC-Number", getEcNumber() },
				{ "Reaction", getReaction() }, { "Km Value", getKmValue() }, { "kcat", getKcat() },
				{ "Substrate", getSubstrate() }, { "Product", getProduct() }, { "Cofactor", getCofactor() },
				{ "Inhibitor", getInhibitor() }, { "Comment", getComment() }
		};
	}

	public String getCofactor() {
		return String.join(" ; ", cofactor);
	}

	public Vector<String> getCofactorsAsVector() {
		return cofactor;
	}

	public void setCofactor(String cofactor) {
		if (!this.cofactor.contains(cofactor)) {
			this.cofactor.add(cofactor);
		}
	}

	public String getComment() {
		return String.join(" ; ", comment);
	}

	public Vector<String> getCommentsAsVector() {
		return comment;
	}

	public void setComment(String comment) {
		if (!this.comment.contains(comment)) {
			this.comment.add(comment);
		}
	}

	public String getInhibitor() {
		return String.join(" ; ", inhibitor);
	}

	public Vector<String> getInhibitorAsVector() {
		return inhibitor;
	}

	public void setInhibitor(String inhibitor) {
		if (!this.inhibitor.contains(inhibitor)) {
			this.inhibitor.add(inhibitor);
		}
	}
}
