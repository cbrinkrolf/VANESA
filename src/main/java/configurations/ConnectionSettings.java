package configurations;

import java.io.File;

import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.io.FileHandler;

import gui.PopUpDialog;
import util.VanesaUtility;

public class ConnectionSettings {
    private static String apiUrl = "";
    private static String proxyHost = "";
    private static String proxyPort = "";

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
        ConnectionSettings.proxyHost = proxy_host;
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
        ConnectionSettings.proxyPort = proxy_port;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        ConnectionSettings.apiUrl = apiUrl;
    }

    private XMLConfiguration getXMLConfiguration() {
        if (xmlConfiguration == null) {
            String settingsFilePath = VanesaUtility.getWorkingDirectoryPath() + File.separator + "settings.xml";
            File f = new File(settingsFilePath);
            if (!f.exists()) {
                System.out.println("There is probably no " + settingsFilePath + " yet.");
                PopUpDialog.getInstance().show("Error configuration file",
                        "Configuration file " + settingsFilePath + " is not valid and got deleted.");
                try {
                    xmlConfiguration = VanesaUtility.getFileBasedXMLConfiguration(settingsFilePath);
                    FileHandler handler = new FileHandler(xmlConfiguration);
                    handler.save(f);
                } catch (ConfigurationException e) {
                    f.delete();
                    System.out.println("Configuration file " + settingsFilePath + " is not valid and got deleted.");
                    PopUpDialog.getInstance().show("Error configuration file",
                            "Configuration file " + settingsFilePath + " is not valid and got deleted.");
                    e.printStackTrace();
                }
            } else {
                try {
                    xmlConfiguration = VanesaUtility.getFileBasedXMLConfiguration(settingsFilePath);
                } catch (ConfigurationException e) {
                    f.delete();
                    System.out.println("Configuration file " + settingsFilePath + " is not valid and got deleted.");
                    PopUpDialog.getInstance().show("Error configuration file",
                            "Configuration file " + settingsFilePath + " is not valid and got deleted.");
                    e.printStackTrace();
                }
            }
        }
        return xmlConfiguration;
    }
}
