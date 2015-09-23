package cluster.slave;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 
 * @author mlewinsk
 * 
 *         Decorator for Clusters with coordinates and integer ids
 * 
 */
public class Cluster implements Serializable {

	private static final long serialVersionUID = -7011416792582435190L;

	public int size;
	public int[] ids;
	public float[] coords;

	public Cluster(int size, int[] ids, float[] coords) {
		this.size = size;
		this.ids = ids;
		this.coords = coords;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(coords);
		result = prime * result + Arrays.hashCode(ids);
		result = prime * result + size;
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
		Cluster other = (Cluster) obj;
		if (!Arrays.equals(coords, other.coords))
			return false;
		if (!Arrays.equals(ids, other.ids))
			return false;
		if (size != other.size)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Cluster [size=" + size + ", ids=" + Arrays.toString(ids)
				+ ", coords=" + Arrays.toString(coords) + "]";
	}

}
