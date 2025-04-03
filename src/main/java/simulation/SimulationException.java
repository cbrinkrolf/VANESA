package simulation;

public class SimulationException extends RuntimeException {
	public SimulationException(final String message) {
		super(message);
	}

	public SimulationException(final String message, Throwable cause) {
		super(message, cause);
	}

	public SimulationException(final Throwable cause) {
		super(cause);
	}
}
