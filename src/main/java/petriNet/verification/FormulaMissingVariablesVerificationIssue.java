package petriNet.verification;

import biologicalElements.Pathway;
import biologicalObjects.edges.petriNet.PNArc;
import biologicalObjects.nodes.petriNet.Transition;

public class FormulaMissingVariablesVerificationIssue extends VerificationIssue {
	private final String entityType;
	private final String entityName;
	private final String formulaName;
	private final String formula;
	private final String[] variables;

	public FormulaMissingVariablesVerificationIssue(final Pathway pathway, final Transition transition,
			final String formulaName, final String formula, final String[] variables) {
		super(pathway);
		entityType = "transition";
		this.entityName = transition.getName();
		this.formulaName = formulaName;
		this.formula = formula;
		this.variables = variables;
	}

	public FormulaMissingVariablesVerificationIssue(final Pathway pathway, final PNArc arc, final String formulaName,
			final String formula, final String[] variables) {
		super(pathway);
		entityType = "arc";
		this.entityName = arc.getName();
		this.formulaName = formulaName;
		this.formula = formula;
		this.variables = variables;
	}

	@Override
	public String getDescription() {
		return "The " + formulaName + " formula '" + formula + "' of " + entityType + " '" + entityName
				+ "' uses undefined variables: '" + String.join("', '", variables)
				+ "'.<br>Make sure they are either defined as parameters or a place exists with that name.";
	}

	@Override
	public boolean isAutoSolvable() {
		return false;
	}

	@Override
	public void solve() {
	}
}
