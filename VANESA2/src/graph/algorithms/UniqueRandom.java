package graph.algorithms;

import java.util.Random;

/**
 * 
 * Random Class generates new Unique Random numbers. This makes sure that you
 * always get the same random numbers if same starting value is used.
 * 
 * @author Maloche
 * @author Bjoern Sommer bjoern@CELLmicrocosmos.org, Sebastian Rubert
 * 
 */
public class UniqueRandom {

	long seed;
	Random rand;

	/**
	 * Creates a new Unique Random number by using the seed.
	 * 
	 * @param seed
	 */
	public UniqueRandom(long seed) {
		this.seed = seed;
		rand = new Random(seed);
	}

	/**
	 * Creates a new Unique Random number by using the name (only the containing
	 * numbers and multiplied by 1000) of the given node as seed.
	 * 
	 * @param seed
	 */
	public UniqueRandom(BiologicalISOMNode involvedNode) {
		// parse the NodeName to an integer: 1.2.4.1 -> 1241; c0001 -> 1;
		// hsa00010 -> 10
		try {

			String str = involvedNode.getNode().getLabel().trim()
					.replace(".", "");
			char[] chr = str.toCharArray();
			this.seed = 0;
			for (int i = 0; i < chr.length; i++) {
				this.seed = this.seed + (int) chr[i];
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		rand = new Random(seed);
	}

	/**
	 * Returns the next int value in the given range. The absval boolean
	 * indicates if the int is in the range from -scale to +scale or the absolut
	 * value.
	 * 
	 * 
	 * @param scale
	 * @param absval
	 * @return
	 */
	public int getNextInt(int scale, boolean absval) {
		rand.setSeed(seed);
		int i = rand.nextInt(scale);
		seed = rand.nextLong();
		if (absval == false) {

			if (seed % 2 == 0)
				i = -i;
		}

		return i;
	}

	/**
	 * Returns the next float The absval boolean indicates if the float is in
	 * the range from - to + or the absolut value.
	 * 
	 * 
	 * @return
	 */
	public float getNextFloat(boolean absval) {
		rand.setSeed(seed);
		float f = rand.nextFloat();

		seed = rand.nextLong();

		if (absval == false) {
			if (seed % 2 == 0)
				f = -f;
		}

		return f;
	}

	/**
	 * Returns the next double The absval boolean indicates if the double is in
	 * the range from - to + or the absolut value.
	 * 
	 * @return
	 */
	public double getNextDouble(boolean absval) {
		rand.setSeed(seed);
		double d = rand.nextDouble();

		seed = rand.nextLong();

		if (absval == false) {
			if (seed % 2 == 0)
				d = -d;
		}

		return d;
	}

	/**
	 * Get the actual used seed.
	 * 
	 * @return seed
	 */
	public long getSeed() {
		return seed;
	}

	/**
	 * Set the actual used seed.
	 */
	public void setSeed(long seed) {
		this.seed = seed;
	}

}
