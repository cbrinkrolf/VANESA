package simulation;

public interface RandomGenerator {
	void setSeed(long seed);

	long nextLong();

	double nextDouble();
}
