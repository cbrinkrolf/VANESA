package graph.algorithms;

public enum NodeAttributeType {
	EXPERIMENT(1),
	GRAPH_PROPERTY(2),
	COLOR(3),
	DATABASE_ID(4),
	ANNOTATION(5);

	private final int id;

	NodeAttributeType(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
}