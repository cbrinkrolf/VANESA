/***************************************************************
 * Copyright (c) Benjamin Kormeier 2006-2010.                  *
 * All rights reserved.                                        *
 ***************************************************************/
package configurations;

import java.io.File;

import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.io.FileHandler;

import database.Connection.DBconnection;
import gui.MyPopUp;
import util.VanesaUtility;

/**
 * @author Benjamin Kormeier
 * @version 1.0 20.10.2010
 */
public class ConnectionSettings {
	private static DBconnection db_connection = null;
	private static boolean useInternetConnection = true;

	private static boolean localMiRNA = false;
	private static boolean localKegg = false;
	private static boolean localHprd = false;
	private static boolean localMint = false;
	private static boolean localIntact = false;
	private static boolean localBrenda = false;

	private static String web_service_url = new String();

	private static String proxy_host = new String();
	private static String proxy_port = new String();

	private static XMLConfiguration xmlConfiguration = null;

	// directories for opening / saving
	private static String fileSaveDirectory;
	private static String fileOpenDirectory;

	private static ConnectionSettings instance = null;

	public static synchronized ConnectionSettings getInstance() {
		if (ConnectionSettings.instance == null) {
			ConnectionSettings.instance = new ConnectionSettings();
		}
		return ConnectionSettings.instance;
	}

	private ConnectionSettings() {
		getXMLConfiguration();
	}

	public String getFileSaveDirectory() {
		if (fileSaveDirectory != null && fileSaveDirectory.length() > 0) {
			return fileSaveDirectory;
		}
		fileSaveDirectory = getProperty("SaveDialog-Path");

		if (fileSaveDirectory != null && fileSaveDirectory.length() > 0) {
			return fileSaveDirectory;
		}

		if (fileOpenDirectory != null && fileOpenDirectory.length() > 0) {
			return fileOpenDirectory;
		}
		return "";
	}

	public void setFileSaveDirectory(String fileDir) {
		fileSaveDirectory = fileDir;
		setProperty("SaveDialog-Path", fileDir);
	}

	public String getFileOpenDirectory() {
		if (fileOpenDirectory != null && fileOpenDirectory.length() > 0) {
			return fileOpenDirectory;
		}

		fileOpenDirectory = getProperty("OpenDialog-Path");
		if (fileOpenDirectory != null && fileOpenDirectory.length() > 0) {
			return fileOpenDirectory;
		}

		if (fileSaveDirectory != null && fileSaveDirectory.length() > 0) {
			return fileSaveDirectory;
		}
		return "";
	}

	public void setFileOpenDirectory(String fileDir) {
		fileOpenDirectory = fileDir;
		setProperty("OpenDialog-Path", fileDir);
	}

	public void setYamlVisualizationFile(String filePath) {
		setProperty("YamlVisualizationPath", filePath);
	}

	public String getYamlVisualizationFile() {
		return getProperty("YamlVisualizationPath");
	}

	private String getProperty(String property) {
		String value = null;
		value = getXMLConfiguration().getString(property);
		return value;
	}

	private void setProperty(String property, String value) {
		String pathWorkingDirectory = VanesaUtility.getWorkingDirectoryPath();
		File f = new File(pathWorkingDirectory + File.separator + "settings.xml");
		try {
			XMLConfiguration xmlSettings = VanesaUtility
					.getFileBasedXMLConfiguration(pathWorkingDirectory + File.separator + "settings.xml");
			xmlSettings.setProperty(property, value);
			// FileHandler handler = new FileHandler(xmlSettings);
			// handler.save(f);
		} catch (ConfigurationException e) {
			if (f.exists()) {
				f.delete();
			}
			e.printStackTrace();
		}
	}

	/**
	 * @return the db_connection
	 */
	public DBconnection getDBConnection() {
		return db_connection;
	}

	/**
	 * @param db_connection the db_connection to set
	 */
	public void setDBConnection(DBconnection db_connection) {
		ConnectionSettings.db_connection = db_connection;
	}

	/**
	 * @return the useInternetConnection
	 */
	public boolean useInternetConnection() {
		return useInternetConnection;
	}

	/**
	 * @param useInternetConnection the useInternetConnection to set
	 */
	public void setInternetConnection(boolean useInternetConnection) {
		ConnectionSettings.useInternetConnection = useInternetConnection;
	}

	/**
	 * @return the proxy_host
	 */
	public String getProxyHost() {
		return proxy_host;
	}

	/**
	 * @param proxy_host the proxy_host to set
	 */
	public void setProxyHost(String proxy_host) {
		System.setProperty("http.proxyHost", proxy_host);
		ConnectionSettings.proxy_host = proxy_host;
	}

	/**
	 * @return the proxy_port
	 */
	public String getProxyPort() {
		return proxy_port;
	}

	/**
	 * @param proxy_port the proxy_port to set
	 */
	public void setProxyPort(String proxy_port) {
		System.setProperty("http.proxyPort", proxy_port);
		ConnectionSettings.proxy_port = proxy_port;
	}

	/**
	 * @return the web_service_url
	 */
	public String getWebServiceUrl() {
		System.out.println("called: " + web_service_url);
		return web_service_url;
	}

	/**
	 * @param web_service_url the web_service_url to set
	 */
	public void setWebServiceUrl(String web_service_url) {
		ConnectionSettings.web_service_url = web_service_url;
	}

	public boolean isLocalKegg() {
		return localKegg;
	}

	public void setLocalKegg(boolean localKegg) {
		ConnectionSettings.localKegg = localKegg;
	}

	public boolean isLocalHprd() {
		return localHprd;
	}

	public void setLocalHprd(boolean localHprd) {
		ConnectionSettings.localHprd = localHprd;
	}

	public boolean isLocalMint() {
		return localMint;
	}

	public void setLocalMint(boolean localMint) {
		ConnectionSettings.localMint = localMint;
	}

	public boolean isLocalIntact() {
		return localIntact;
	}

	public void setLocalIntact(boolean localIntact) {
		ConnectionSettings.localIntact = localIntact;
	}

	public boolean isLocalBrenda() {
		return localBrenda;
	}

	public void setLocalBrenda(boolean localBrenda) {
		ConnectionSettings.localBrenda = localBrenda;
	}

	public boolean isLocalMiRNA() {
		return localMiRNA;
	}

	public void setLocalMiRNA(boolean localMiRNA) {
		ConnectionSettings.localMiRNA = localMiRNA;
	}

	private XMLConfiguration getXMLConfiguration() {
		if (xmlConfiguration == null) {
			String settingsFilePath = VanesaUtility.getWorkingDirectoryPath() + File.separator + "settings.xml";
			File f = new File(settingsFilePath);
			if (!f.exists()) {
				System.out.println("There is probably no " + settingsFilePath + " yet.");
				MyPopUp.getInstance().show("Error configuration file",
						"Configuration file " + settingsFilePath + " is not valid and got deleted.");
				try {
					xmlConfiguration = VanesaUtility.getFileBasedXMLConfiguration(settingsFilePath);
					FileHandler handler = new FileHandler(xmlConfiguration);
					handler.save(f);
				} catch (ConfigurationException e) {
					f.delete();
					System.out.println("Configuration file " + settingsFilePath + " is not valid and got deleted.");
					MyPopUp.getInstance().show("Error configuration file",
							"Configuration file " + settingsFilePath + " is not valid and got deleted.");
					e.printStackTrace();
				}
			} else {
				try {
					xmlConfiguration = VanesaUtility.getFileBasedXMLConfiguration(settingsFilePath);
				} catch (ConfigurationException e) {
					f.delete();
					System.out.println("Configuration file " + settingsFilePath + " is not valid and got deleted.");
					MyPopUp.getInstance().show("Error configuration file",
							"Configuration file " + settingsFilePath + " is not valid and got deleted.");
					e.printStackTrace();
				}
			}
		}
		return xmlConfiguration;
	}
}
