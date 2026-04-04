package simulation;

public class SimulationException extends RuntimeException {

	private static final long serialVersionUID = -1523305591122557348L;

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
