package database.brenda;

import java.sql.SQLException;

public class MoleculeBoxSingelton extends MoleculeBox {

	private static MoleculeBox settings = null;

	protected MoleculeBoxSingelton(){}

	/**
	 * If an instance of the class 'DBSolas' does not exist an instance
	 * will be generated and returned. Otherwise the existing instance will be
	 * returned.
	 * 
	 * @return DBSolas
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static MoleculeBox getInstance(){
		if (settings == null) {
			settings = new MoleculeBoxSingelton();
		}
		return settings;
	}

	/**
	 * This function replaces the existing instance by a new one.
	 * 
	 * @param dbsolas
	 */
	public static void setInstance(MoleculeBox settings) {
		settings = settings;
	}
}

