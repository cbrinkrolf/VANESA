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
	
	public DatabaseEntry(String database, String id){
		this.database=database;
		this.id=id;	
	}
	
	public String getDatabase(){
		return database;
	}
	public String getId(){
		return id;
	}
}
