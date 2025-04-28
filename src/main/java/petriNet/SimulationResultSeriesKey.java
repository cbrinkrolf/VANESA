package petriNet;

public enum SimulationResultSeriesKey {
	/**
	 * Number of tokens in a place.
	 */
	PLACE_TOKEN(1),
	// for transitions
	ACTUAL_FIRING_SPEED(0),
	FIRE(2),
	ACTIVE(5),
	PUT_DELAY(6),
	FIRE_TIME(7),
	DELAY(8),
	// for edges
	ARC_SUM_OF_TOKEN(3),
	ARC_ACTUAL_TOKEN_FLOW(4);

	public final int id;

	SimulationResultSeriesKey(final int id) {
		this.id = id;
	}
}
