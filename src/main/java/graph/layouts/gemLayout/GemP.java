package graph.layouts.gemLayout;

/**
 * Class containing properties per node.
 * 
 * @author taubertj
 * 
 */
public class GemP {

	public int x, y; // position

	public int in;

	public int iX, iY; // impulse

	public float dir; // direction

	public float heat; // heat

	public float mass; // weight = nr edges

	public boolean mark;

	public GemP(int m) {
		x = 0;
		y = 0;
		iX = iY = 0;
		dir = (float) 0.0;
		heat = 0;
		mass = m;
		mark = false;
	}
}
