package simulation;

public enum BenefitStrategy {
	Greedy(0),
	Quotient(1),
	BranchAndBound(2);

	private final int id;

	BenefitStrategy(final int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public static BenefitStrategy fromId(final int id) {
		if (id == Quotient.id) {
			return Quotient;
		}
		if (id == BranchAndBound.id) {
			return BranchAndBound;
		}
		return Greedy;
	}
}
