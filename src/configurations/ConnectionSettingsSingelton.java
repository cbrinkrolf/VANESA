package configurations;

import java.sql.SQLException;

public class ConnectionSettingsSingelton extends ConnectionSettings {

	private static ConnectionSettings settings = null;

	protected ConnectionSettingsSingelton(){}

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
	public static ConnectionSettings getInstance(){
		if (settings == null) {
			settings = new ConnectionSettingsSingelton();
		}
		return settings;
	}

	/**
	 * This function replaces the existing instance by a new one.
	 * 
	 * @param dbsolas
	 */
	public static void setInstance(ConnectionSettings settings) {
		settings = settings;
	}
}

