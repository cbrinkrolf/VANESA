package graph.algorithms.centralities;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

class BetweennessCentralityTest {
	@Test
	public void calcCentrality() throws ExecutionException, InterruptedException {
		final int[] a = {0, 1, 1, 2, 2, 0, 0, 3};
		final int[] b = {1, 0, 2, 1, 0, 2, 3, 0};
		BetweennessCentrality n = new BetweennessCentrality(a, b);
		final var result = n.calcCentrality();
		System.out.println(result.toString());
		assertArrayEquals(new int[]{2, 2, 1, 1, 2, 2, 3, 3}, result.edgesWalkedOver);
	}
}