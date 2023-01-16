/***************************************************************
 * Copyright (c) Benjamin Kormeier 2006-2010.                  *
 * All rights reserved.                                        *
 ***************************************************************/
package configurations;

import java.io.File;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.XMLConfiguration;

import database.Connection.DBconnection;
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

	// directories for opening / saving
	private static String fileSaveDirectory;
	private static String fileOpenDirectory;

	public static String getFileSaveDirectory() {
		if (fileSaveDirectory != null && fileSaveDirectory.length() > 0) {
			return fileSaveDirectory;
		}
		File f = new File(VanesaUtility.getWorkingDirectoryPath() + File.separator + "settings.xml");
		if (!f.exists()) {
			System.out.println("There is probably no " + VanesaUtility.getWorkingDirectoryPath() + File.separator
					+ "settings.xml yet.");
		} else {
			try {
				XMLConfiguration xmlSettings = VanesaUtility.getFileBasedXMLConfiguration(
						VanesaUtility.getWorkingDirectoryPath() + File.separator + "settings.xml");
				fileSaveDirectory = xmlSettings.getString("SaveDialog-Path");
				if (fileSaveDirectory != null && fileSaveDirectory.length() > 0) {
					return fileSaveDirectory;
				}
			} catch (ConfigurationException e) {
				f.delete();
				e.printStackTrace();
			}
		}
		if (fileOpenDirectory != null && fileOpenDirectory.length() > 0) {
			return fileOpenDirectory;
		}
		return "";
	}

	public static void setFileSaveDirectory(String fileDir) {
		fileSaveDirectory = fileDir;
		String pathWorkingDirectory = VanesaUtility.getWorkingDirectoryPath();
		File f = new File(pathWorkingDirectory + File.separator + "settings.xml");
		try {
			XMLConfiguration xmlSettings = VanesaUtility.getFileBasedXMLConfiguration(
					pathWorkingDirectory + File.separator + "settings.xml");
			xmlSettings.setProperty("SaveDialog-Path", fileDir);
			// TODO MF: xmlSettings.save();
		} catch (ConfigurationException e) {
			if (f.exists()) {
				f.delete();
			}
			e.printStackTrace();
		}
	}

	public static String getFileOpenDirectory() {
		if (fileOpenDirectory != null && fileOpenDirectory.length() > 0) {
			return fileOpenDirectory;
		}
		File f = new File(VanesaUtility.getWorkingDirectoryPath() + File.separator + "settings.xml");
		if (!f.exists()) {
			System.out.println("There is probably no " + VanesaUtility.getWorkingDirectoryPath() + File.separator
					+ "settings.xml yet.");
		} else {
			try {
				XMLConfiguration xmlSettings = VanesaUtility.getFileBasedXMLConfiguration(
						VanesaUtility.getWorkingDirectoryPath() + File.separator + "settings.xml");
				fileOpenDirectory = xmlSettings.getString("OpenDialog-Path");
				if (fileOpenDirectory != null && fileOpenDirectory.length() > 0) {
					return fileOpenDirectory;
				}
			} catch (ConfigurationException e) {
				f.delete();
				e.printStackTrace();
			}
		}
		if (fileSaveDirectory != null && fileSaveDirectory.length() > 0) {
			return fileSaveDirectory;
		}
		return "";
	}

	public static void setFileOpenDirectory(String fileDir) {
		fileOpenDirectory = fileDir;
		String pathWorkingDirectory = VanesaUtility.getWorkingDirectoryPath();
		File f = new File(pathWorkingDirectory + File.separator + "settings.xml");
		try {
			XMLConfiguration xmlSettings = VanesaUtility.getFileBasedXMLConfiguration(
					pathWorkingDirectory + File.separator + "settings.xml");
			xmlSettings.setProperty("OpenDialog-Path", fileDir);
			// TODO MF: xmlSettings.save();
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
	public static DBconnection getDBConnection() {
		return db_connection;
	}

	/**
	 * @param db_connection the db_connection to set
	 */
	public static void setDBConnection(DBconnection db_connection) {
		ConnectionSettings.db_connection = db_connection;
	}

	/**
	 * @return the useInternetConnection
	 */
	public static boolean useInternetConnection() {
		return useInternetConnection;
	}

	/**
	 * @param useInternetConnection the useInternetConnection to set
	 */
	public static void setInternetConnection(boolean useInternetConnection) {
		ConnectionSettings.useInternetConnection = useInternetConnection;
	}

	/**
	 * @return the proxy_host
	 */
	public static String getProxyHost() {
		return proxy_host;
	}

	/**
	 * @param proxy_host the proxy_host to set
	 */
	public static void setProxyHost(String proxy_host) {
		System.setProperty("http.proxyHost", proxy_host);
		ConnectionSettings.proxy_host = proxy_host;
	}

	/**
	 * @return the proxy_port
	 */
	public static String getProxyPort() {
		return proxy_port;
	}

	/**
	 * @param proxy_port the proxy_port to set
	 */
	public static void setProxyPort(String proxy_port) {
		System.setProperty("http.proxyPort", proxy_port);
		ConnectionSettings.proxy_port = proxy_port;
	}

	/**
	 * @return the web_service_url
	 */
	public static String getWebServiceUrl() {
		return web_service_url;
	}

	/**
	 * @param web_service_url the web_service_url to set
	 */
	public static void setWebServiceUrl(String web_service_url) {
		ConnectionSettings.web_service_url = web_service_url;
	}

	public static boolean isLocalKegg() {
		return localKegg;
	}

	public static void setLocalKegg(boolean localKegg) {
		ConnectionSettings.localKegg = localKegg;
	}

	public static boolean isLocalHprd() {
		return localHprd;
	}

	public static void setLocalHprd(boolean localHprd) {
		ConnectionSettings.localHprd = localHprd;
	}

	public static boolean isLocalMint() {
		return localMint;
	}

	public static void setLocalMint(boolean localMint) {
		ConnectionSettings.localMint = localMint;
	}

	public static boolean isLocalIntact() {
		return localIntact;
	}

	public static void setLocalIntact(boolean localIntact) {
		ConnectionSettings.localIntact = localIntact;
	}

	public static boolean isLocalBrenda() {
		return localBrenda;
	}

	public static void setLocalBrenda(boolean localBrenda) {
		ConnectionSettings.localBrenda = localBrenda;
	}

	public static boolean isLocalMiRNA() {
		return localMiRNA;
	}

	public static void setLocalMiRNA(boolean localMiRNA) {
		ConnectionSettings.localMiRNA = localMiRNA;
	}
}
