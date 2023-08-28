package configurations;

import java.io.File;

import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.io.FileHandler;

import gui.PopUpDialog;
import util.VanesaUtility;

public class SettingsManager {
	private static String apiUrl = "";
	private static String proxyHost = "";
	private static String proxyPort = "";

	private static XMLConfiguration xmlConfiguration = null;

	// directories for opening / saving
	private static String fileSaveDirectory;
	private static String fileOpenDirectory;
	private static String developerMode;

	private static SettingsManager instance = null;

	public static synchronized SettingsManager getInstance() {
		if (SettingsManager.instance == null) {
			SettingsManager.instance = new SettingsManager();
		}
		return SettingsManager.instance;
	}

	private SettingsManager() {
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
		// TODO maker nicer
		// todo workaround because file is not always read after changing one property
		// and reading it again
		xmlConfiguration = null;
		return getXMLConfiguration().getString(property);
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
	 * @return the proxy_host
	 */
	public String getProxyHost() {
		return proxyHost;
	}

	/**
	 * @param proxy_host the proxy_host to set
	 */
	public void setProxyHost(String proxy_host) {
		System.setProperty("http.proxyHost", proxy_host);
		SettingsManager.proxyHost = proxy_host;
	}

	/**
	 * @return the proxy_port
	 */
	public String getProxyPort() {
		return proxyPort;
	}

	/**
	 * @param proxy_port the proxy_port to set
	 */
	public void setProxyPort(String proxy_port) {
		System.setProperty("http.proxyPort", proxy_port);
		SettingsManager.proxyPort = proxy_port;
	}

	public String getApiUrl() {
		return apiUrl;
	}

	public void setApiUrl(String apiUrl) {
		SettingsManager.apiUrl = apiUrl;
	}

	public boolean isDeveloperMode() {
		if (developerMode != null) {
			return developerMode.equals("true");
		}
		String property = getProperty("isDeveloperMode");
		;
		if (property != null && property.length() > 0) {
			return property.equals("true");
		}
		return false;
	}

	public void setDeveloperMode(Boolean developerMode) {
		setProperty("isDeveloperMode", developerMode.toString());
		SettingsManager.developerMode = developerMode.toString();
	}

	public void setOMPath(String path) {
		if (path != null && path.length() > 0 && new File(path).exists() && new File(path).isDirectory()) {
			setProperty("OMPath", path);
		} else {
			PopUpDialog.getInstance().show("Wrong path", "Given path: " + path + " is not valid!");
		}
	}

	public String getOMPath() {
		String property = getProperty("OMPath");
		if (property != null && property.length() > 0 && new File(property).exists()
				&& new File(property).isDirectory()) {
			return property;
		}
		return "";
	}

	public void setPNlibPath(String path) {
		if (path != null && path.length() > 0 && new File(path).exists() && new File(path).isDirectory()) {
			setProperty("PNlibPath", path);
		} else {
			PopUpDialog.getInstance().show("Wrong path", "Given path: " + path + " is not valid!");
		}
	}

	public String getPNlibPath() {
		String property = getProperty("PNlibPath");
		// System.out.println("pnlibpath: "+property);
		if (property != null && property.length() > 0 && new File(property).exists()
				&& new File(property).isDirectory()) {
			return property;
		}
		return "";
	}

	public boolean isOverrideOMPath() {
		String property = getProperty("isOverrideOMPath");
		if (property != null && property.length() > 0) {
			return property.equals("true");
		}
		return false;
	}

	public void setOverrideOMPath(Boolean override) {
		setProperty("isOverrideOMPath", override.toString());
	}

	public boolean isOverridePNlibPath() {
		String property = getProperty("isOverridePNlibPath");
		if (property != null && property.length() > 0) {
			return property.equals("true");
		}
		return false;
	}

	public void setOverridePNlibPath(Boolean override) {
		setProperty("isOverridePNlibPath", override.toString());
	}

	private XMLConfiguration getXMLConfiguration() {
		if (xmlConfiguration == null) {
			String settingsFilePath = VanesaUtility.getWorkingDirectoryPath() + File.separator + "settings.xml";
			File f = new File(settingsFilePath);
			if (!f.exists()) {
				System.out.println("There is probably no " + settingsFilePath + " yet.");
				// This causes an infinite recursion with the MainWindow init! Don't show popup
				// dialogs here
				// PopUpDialog.getInstance().show("Error configuration file",
				// "Configuration file " + settingsFilePath + " is not valid and got deleted.");
				try {
					xmlConfiguration = VanesaUtility.getFileBasedXMLConfiguration(settingsFilePath);
					FileHandler handler = new FileHandler(xmlConfiguration);
					handler.save(f);
				} catch (ConfigurationException e) {
					f.delete();
					System.out.println("Configuration file " + settingsFilePath + " is not valid and got deleted.");
					// PopUpDialog.getInstance().show("Error configuration file",
					// "Configuration file " + settingsFilePath + " is not valid and got deleted.");
					e.printStackTrace();
				}
			} else {
				try {
					xmlConfiguration = VanesaUtility.getFileBasedXMLConfiguration(settingsFilePath);
				} catch (ConfigurationException e) {
					f.delete();
					System.out.println("Configuration file " + settingsFilePath + " is not valid and got deleted.");
					// PopUpDialog.getInstance().show("Error configuration file",
					// "Configuration file " + settingsFilePath + " is not valid and got deleted.");
					e.printStackTrace();
				}
			}
		}
		return xmlConfiguration;
	}
}
