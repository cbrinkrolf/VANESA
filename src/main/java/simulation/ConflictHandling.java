package simulation;

public enum ConflictHandling {
	Probability(0),
	Priority(1);

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
		return Probability;
	}
}
