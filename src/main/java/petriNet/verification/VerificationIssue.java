package petriNet.verification;

import biologicalElements.Pathway;

public abstract class VerificationIssue {
	protected final Pathway pathway;

	protected VerificationIssue(final Pathway pathway) {
		this.pathway = pathway;
	}

	public abstract String getDescription();

	public abstract boolean isAutoSolvable();

	public abstract void solve();
}
