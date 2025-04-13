package simulation;

public enum ConflictHandling {
	Probability(0),
	Priority(1),
	// Benefit has ID 3, because probability previously had ID 2.
	Benefit(3);

	private final int id;

	ConflictHandling(final int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public static ConflictHandling fromId(final int id) {
		if (id == Priority.id) {
			return Priority;
		}
		if (id == Benefit.id) {
			return Benefit;
		}
		return Probability;
	}
}
