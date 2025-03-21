package graph;

public class Rect {
	public static final Rect EMPTY = new Rect(0, 0, 0, 0);

	public final double x;
	public final double y;
	public final double width;
	public final double height;

	public Rect(double x, double y, double width, double height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public double getMaxX() {
		return x + width;
	}

	public double getMaxY() {
		return y + height;
	}

	public boolean contains(final double x, final double y) {
		return x >= this.x && y >= this.y && x <= this.x + width && y <= this.y + height;
	}
}
