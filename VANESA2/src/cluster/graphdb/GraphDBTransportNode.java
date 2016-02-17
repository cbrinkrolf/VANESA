package cluster.graphdb;

import java.io.Serializable;
import java.util.Arrays;


public class GraphDBTransportNode implements Serializable{
	/**
	 * generated uid
	 */
	private static final long serialVersionUID = 7572280434451895283L;
	public String commonName;
	public String type;
	public String fullName;
	public DatabaseEntry dbIds[];

	public String getCommonName() {
		return commonName;
	}

	public void setCommonName(String commonName) {
		this.commonName = commonName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public DatabaseEntry[] getDbIds() {
		return dbIds;
	}

	public void setDbIds(DatabaseEntry[] dbIds) {
		this.dbIds = dbIds;
	}

	public String[] getBiodata() {
		return biodata;
	}

	public void setBiodata(String[] biodata) {
		this.biodata = biodata;
	}

	public double[] getBiodataEntries() {
		return biodataEntries;
	}

	public void setBiodataEntries(double[] biodataEntries) {
		this.biodataEntries = biodataEntries;
	}

	public String[] getMolecularFunction() {
		return molecularFunction;
	}

	public void setMolecularFunction(String[] molecularFunction) {
		this.molecularFunction = molecularFunction;
	}

	public String[] getBiologicalProcess() {
		return biologicalProcess;
	}

	public void setBiologicalProcess(String[] biologicalProcess) {
		this.biologicalProcess = biologicalProcess;
	}

	public String[] getCellularComponent() {
		return cellularComponent;
	}

	public void setCellularComponent(String[] cellularComponent) {
		this.cellularComponent = cellularComponent;
	}
	public String[] biodata;
	public double[] biodataEntries;

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
		this.dbIds = new DatabaseEntry[0];
		this.biodata = new String[0];
		this.biodataEntries = new double[0];
		this.molecularFunction = new String[0];
		this.biologicalProcess = new String[0];
		this.cellularComponent = new String[0];

	}

	// Constructor with full data input for convenience
	public GraphDBTransportNode(String type, String name, String fullName,
			DatabaseEntry[] dbIds, String[] biodata,
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
	@Override
	public String toString() {
		return "GraphDBTransportNode [commonName=" + commonName + ", type="
				+ type + ", fullName=" + fullName + ", dbIds="
				+ Arrays.toString(dbIds) + "]";
	}
}
