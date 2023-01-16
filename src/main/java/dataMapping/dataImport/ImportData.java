package dataMapping.dataImport;

import java.util.Vector;

/**
 * This interface implements the method to get the data from a file
 * to a structure that is readable by a TableModel
 * @author dborck
 *
 */
public interface ImportData {

	/**
	 * @return - a Vector of Objects which store the import data in a row wise manner
	 */
	public Vector<Vector<String>> getDataVector();
	
	/**
	 * @return - a Vector of Strings for the header of the TableModel
	 */
	public Vector<String> getHeaderVector();
	

}
