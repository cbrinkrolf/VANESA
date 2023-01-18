package biologicalElements;

import java.sql.SQLException;

public class ElementNamesSingelton extends ElementNames {

	private static ElementNames settings = null;

	protected ElementNamesSingelton(){}

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
	public static ElementNames getInstance(){
		if (settings == null) {
			settings = new ElementNamesSingelton();
		}
		return settings;
	}

	/**
	 * This function replaces the existing instance by a new one.
	 * 
	 * @param dbsolas
	 */
//	public static void setInstance(ElementNames settings) {
//		settings = settings;
//	}
}

