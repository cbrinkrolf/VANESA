package configurations.gui;

import java.awt.Color;
import java.net.URL;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import configurations.ConnectionSettings;
import configurations.ResourceLibrary;
import net.miginfocom.swing.MigLayout;

public class InternetConnectionDialog extends JPanel {
    private static final long serialVersionUID = 1358638152136315177L;

    private final JTextField apiUrl;
    private final JTextField port;
    private final JTextField host;
    private final JLabel status = new JLabel(" ");

    public InternetConnectionDialog() {
        MigLayout layout = new MigLayout("", "[left]");
        apiUrl = new JTextField(20);
        status.setForeground(Color.RED);
        port = new JTextField(20);
        host = new JTextField(20);
        status.setForeground(Color.RED);
        setLayout(layout);
        add(new JLabel("Proxy Settings"), "span 4");
        add(new JSeparator(), "span, growx, wrap 15, gaptop 10, gap 5");
        add(new JLabel("Proxy Port"), "span 2, gap 10, gaptop 2 ");
        add(port, "span,wrap,growx ,gap 10, gaptop 2");
        add(new JLabel("Proxy Host"), "span 2, gap 10, gaptop 2 ");
        add(host, "span,wrap,growx ,gap 10, gaptop 2");

        add(new JSeparator(), "span, growx, wrap 5, gaptop 2, gap 5");
        add(new JLabel("API Settings"), "span 4");
        add(new JSeparator(), "span, growx, wrap 15, gaptop 10, gap 5");
        add(new JLabel("API URL"), "span 2, gap 17, gaptop 2 ");
        add(apiUrl, "span,wrap 15 ,growx ,gap 10, gaptop 2");

        add(new JLabel(" "), "span,wrap,growx ,gap 10, gaptop 2");
        add(new JLabel(" "), "span,wrap,growx ,gap 10, gaptop 2");
        add(status, "span, growx, gap 10 ,gaptop 15,wrap 15");

        apiUrl.setText(ConnectionSettings.getInstance().getApiUrl());
        port.setText(ConnectionSettings.getInstance().getProxyPort());
        host.setText(ConnectionSettings.getInstance().getProxyHost());
    }

    public void enableDisplay(boolean enabled) {
        apiUrl.setEnabled(enabled);
        port.setEnabled(enabled);
        host.setEnabled(enabled);
    }

    public boolean applyDefaults() {
        apiUrl.setText(ResourceLibrary.getSettingsResource("settings.default.api.url"));
        port.setText(ResourceLibrary.getSettingsResource("settings.default.proxy.port"));
        host.setText(ResourceLibrary.getSettingsResource("settings.default.proxy.host"));
        return applyNewSettings();
    }

    public boolean applyNewSettings() {
        ConnectionSettings.getInstance().setApiUrl(apiUrl.getText());
        ConnectionSettings.getInstance().setProxyHost(host.getText());
        ConnectionSettings.getInstance().setProxyPort(port.getText());
        return checkSettings();
    }

    private boolean checkSettings() {
        try {
            // Try parsing the api url to check if it's at least valid
            new URL(apiUrl.getText());
            status.setText("");
            status.setToolTipText("");
            return true;
        } catch (Exception e) {
            status.setText("Settings could not be validated!");
            status.setToolTipText(e.toString());
            return false;
        }
    }
}