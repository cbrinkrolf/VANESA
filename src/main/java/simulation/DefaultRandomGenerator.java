package simulation;

import java.util.Random;

public class DefaultRandomGenerator implements RandomGenerator {
	private final Random random = new Random();

	public DefaultRandomGenerator() {
	}

	public DefaultRandomGenerator(final long seed) {
		random.setSeed(seed);
	}

	@Override
	public void setSeed(final long seed) {
		random.setSeed(seed);
	}

	@Override
	public long nextLong() {
		return random.nextLong();
	}

	@Override
	public double nextDouble() {
		return random.nextDouble();
	}
}
