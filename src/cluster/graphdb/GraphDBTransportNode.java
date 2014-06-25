package cluster.graphdb;

import java.io.Serializable;
import java.util.HashSet;


public class GraphDBTransportNode implements Serializable{
	/**
	 * generated uid
	 */
	private static final long serialVersionUID = 7572280434451895283L;
	public String commonName;
	public String type;
	public String fullName;
	public HashSet<DatabaseEntry> dbIds;

	public String[] biodata;
	public double[] biodataEntries;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((commonName == null) ? 0 : commonName.hashCode());
		result = prime * result
				+ ((fullName == null) ? 0 : fullName.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		GraphDBTransportNode other = (GraphDBTransportNode) obj;
		if (commonName == null) {
			if (other.commonName != null)
				return false;
		} else if (!commonName.equals(other.commonName))
			return false;
		if (fullName == null) {
			if (other.fullName != null)
				return false;
		} else if (!fullName.equals(other.fullName))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	public String molecularFunction[];
	public String biologicalProcess[];
	public String cellularComponent[];

	// empty constructor needed for Neo
	public GraphDBTransportNode() {
	}

	// Standard Constructor
	public GraphDBTransportNode(String commonName) {
		this.commonName = commonName;
		this.type = "NA";
		this.fullName = "NA";
		this.dbIds = new HashSet<DatabaseEntry>();
		this.biodata = new String[0];
		this.biodataEntries = new double[0];
		this.molecularFunction = new String[0];
		this.biologicalProcess = new String[0];
		this.cellularComponent = new String[0];

	}

	// Constructor with full data input for convenience
	public GraphDBTransportNode(String type, String name, String fullName,
			HashSet<DatabaseEntry> dbIds, String[] biodata,
			double[] biodataEntry, String molecularFunction[],
			String biologicalProcess[], String cellularComponent[]) {
		this.type = type;
		this.commonName = name;
		this.fullName = fullName;
		this.dbIds = dbIds;
		this.biodata = biodata;
		this.biodataEntries = biodataEntry;
		this.molecularFunction = molecularFunction;
		this.biologicalProcess = biologicalProcess;
		this.cellularComponent = cellularComponent;
	}
	
	public String getBiodataString(){
		String result = new String();		
		for (int i = 0; i < biodata.length; i++) {
			result+=biodata[i]+","+biodataEntries[i]+";";
		}
		
		return result;
	}
}
