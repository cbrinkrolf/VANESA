package biologicalObjects.nodes;

import java.util.Iterator;
import java.util.Vector;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BRENDANode {

	private String name, ec_number, sysName, enzymeClass, reaction;

	@Setter(AccessLevel.NONE)
	private String kmValue;
	@Setter(AccessLevel.NONE)
	private String kcat;
	private String substrate, product;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private Vector<String> cofactor = new Vector<String>();
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private Vector<String> inhibitor = new Vector<String>();
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private Vector<String> comment = new Vector<String>();

	public String getLink() {
		return "http://www.brenda-enzymes.info/php/result_flat.php4?ecno=" + ec_number;
	}

	public BRENDANode() {
		name = "";
		ec_number = "";
		sysName = "";
		enzymeClass = "";
		reaction = "";
		kmValue = "";
		kcat = "";
		substrate = "";
		product = "";
	}

	public Object[][] getBrendaDeatails() {

		Object[][] values = { { "Name", getName() }, { "SysName", getSysName() }, { "EC-Number", getEc_number() },
				{ "Reaction", getReaction() }, { "Km Value", getKmValue() }, { "kcat", getKcat() },
				{ "Substrate", getSubstrate() }, { "Product", getProduct() }, { "Cofactor", getCofactor() },
				{ "Inhibitor", getInhibitor() }, { "Comment", getComment() }

		};
		return values;
	}

	public String getCofactor() {

		String result = "";
		Iterator<String> it = cofactor.iterator();
		boolean first = true;
		while (it.hasNext()) {
			if (first) {
				result = result + it.next();
				first = false;
			} else {
				result = result + " ; " + it.next();
			}
		}
		return result;
	}

	public Vector<String> getCofactorsAsVector() {
		return cofactor;
	}

	public void setCofactor(String cofactor_str) {
		if (!cofactor.contains(cofactor_str)) {
			cofactor.add(cofactor_str);
		}
	}

	public String getComment() {
		String result = "";
		Iterator<String> it = comment.iterator();
		boolean first = true;
		while (it.hasNext()) {
			if (first) {
				result = result + it.next();
				first = false;
			} else {
				result = result + " ; " + it.next();
			}
		}
		return result;
	}

	public Vector<String> getCommentsAsVector() {
		return comment;
	}

	public void setComment(String comment_str) {
		if (!comment.contains(comment_str)) {
			comment.add(comment_str);
		}

	}

	public String getInhibitor() {
		String result = "";
		Iterator<String> it = inhibitor.iterator();
		boolean first = true;
		while (it.hasNext()) {
			if (first) {
				result = result + it.next();
				first = false;
			} else {
				result = result + " ; " + it.next();
			}
		}
		return result;
	}

	public Vector<String> getInhibitorAsVector() {
		return inhibitor;
	}

	public void setInhibitor(String inhibitor_str) {
		if (!inhibitor.contains(inhibitor_str)) {
			inhibitor.add(inhibitor_str);
		}
	}

	public void setKmValue(String kmValue) {
		this.kmValue = (kmValue == null ? "" : kmValue);
	}

	public void setKcat(String kcat) {
		this.kcat = (kcat == null ? "" : kcat);
	}
}
