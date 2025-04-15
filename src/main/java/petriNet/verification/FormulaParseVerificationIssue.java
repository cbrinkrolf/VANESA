package petriNet.verification;

import biologicalElements.Pathway;
import biologicalObjects.edges.petriNet.PNArc;
import biologicalObjects.nodes.petriNet.Transition;

public class FormulaParseVerificationIssue extends VerificationIssue {
	private final String entityType;
	private final String entityName;
	private final String formulaName;
	private final String formula;
	private final String error;

	public FormulaParseVerificationIssue(final Pathway pathway, final Transition transition, final String formulaName,
			final String formula, final String error) {
		super(pathway);
		entityType = "transition";
		this.entityName = transition.getName();
		this.formulaName = formulaName;
		this.formula = formula;
		this.error = error;
	}

	public FormulaParseVerificationIssue(final Pathway pathway, final PNArc arc, final String formulaName,
			final String formula, final String error) {
		super(pathway);
		entityType = "arc";
		this.entityName = arc.getName();
		this.formulaName = formulaName;
		this.formula = formula;
		this.error = error;
	}

	@Override
	public String getDescription() {
		return "The " + formulaName + " formula '" + formula + "' of " + entityType + " '" + entityName
				+ "' could not be parsed: " + error;
	}

	@Override
	public boolean isAutoSolvable() {
		return false;
	}

	@Override
	public void solve() {
	}
}
