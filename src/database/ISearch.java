package database;

import java.sql.SQLException;
import java.util.ArrayList;

import pojos.DBColumn;

public interface ISearch {

	/**
	 * Processes a search on the specified database 
	 * which is represented by a <code>ISearch</code> instance.
	 */
	public void searchDatabase() throws SQLException;
	
	/**
	 * Builds up a query which will be used to finally search
	 * the database for all necessary information.
	 * @return the query for the final search
	 */
	public String buildQuery() throws SQLException;
	
	/**
	 * Shows the results of  a database search. 
	 * It should be used after the final query has been successfully 
	 * performed.
	 * 
	 * @param dbResult  ArrayList<DBColumn>, the list of received information 
	 * which will be shown in Vanesa.
	 */
	public void showResults(ArrayList<DBColumn> dbResult);
	
	
}
