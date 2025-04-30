package simulation;

import java.util.concurrent.atomic.AtomicLong;

public class Xorshift128Plus implements RandomGenerator {
	private static final AtomicLong NEXT_SEED = new AtomicLong(8682522807148012L);

	private long state0 = 0;
	private long state1 = 0;

	public Xorshift128Plus() {
		while (true) {
			long current = NEXT_SEED.get();
			long next = current * 1181783497276652981L;
			if (NEXT_SEED.compareAndSet(current, next)) {
				setSeed(next ^ System.nanoTime());
				break;
			}
		}
	}

	public Xorshift128Plus(final long seed) {
		setSeed(seed);
	}

	@Override
	public void setSeed(final long seed) {
		state0 = seed;
		state1 = seed + 1;
		// Generate numbers trying to leave the worst case startup
		for (int i = 0; i < 50; i++) {
			nextLong();
		}
	}

	@Override
	public long nextLong() {
		long x = state0;
		final long y = state1;
		state0 = y;
		x ^= x << 23;
		x ^= x >>> 17;
		x ^= y ^ (y >>> 26);
		state1 = x;
		return x + y;
	}

	@Override
	public double nextDouble() {
		// return Double.longBitsToDouble(nextLong() >>> 12 | 0x3FFL << 52) - 1.0;
		return (double)(nextLong() & 0x1FFFFFFFFFFFFFL) / (0x1L << 53);
	}
}
