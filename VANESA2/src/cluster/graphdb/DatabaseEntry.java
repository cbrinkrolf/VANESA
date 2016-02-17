package cluster.graphdb;

import java.io.Serializable;

/**
 * 
 * @author mlewinsk
 * June 2014
 */
public class DatabaseEntry implements Serializable{
	
	private static final long serialVersionUID = -2161259667490815996L;
	private String database = new String(),
			id = new String();

	public DatabaseEntry(){
		
	}
	
	
	public DatabaseEntry(String database, String id){
		this.database=database;
		this.id=id;	
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
		
	@Override
	public String toString() {
		return "DatabaseEntry [database=" + database + ", id=" + id + "]";
	}

	
	
}
