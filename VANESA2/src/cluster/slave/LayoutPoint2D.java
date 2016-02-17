package cluster.slave;

import java.io.Serializable;

/**
 * Decorator for Coordinates, 
 * 
 * @author mlewinsk
 * July 2014
 */

public class LayoutPoint2D implements Serializable{

	private static final long serialVersionUID = 8346840878281856405L;
	private float x = 0.0f;
	private float y = 0.0f;
	
	public LayoutPoint2D(float x, float y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(x);
		result = prime * result + Float.floatToIntBits(y);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LayoutPoint2D other = (LayoutPoint2D) obj;
		if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
			return false;
		if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))
			return false;
		return true;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}
	
	@Override
	public String toString() {
		return "LayoutPoint2D [x=" + x + ", y=" + y + "]";
	}

}
