package configurations;

import java.sql.SQLException;

public class NetworkSettingsSingelton extends NetworkSettings {

	private static NetworkSettings settings = null;

	protected NetworkSettingsSingelton(){}

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
	public static NetworkSettings getInstance(){
		if (settings == null) {
			settings = new NetworkSettingsSingelton();
		}
		return settings;
	}

	/**
	 * This function replaces the existing instance by a new one.
	 * 
	 * @param dbsolas
	 */
}

