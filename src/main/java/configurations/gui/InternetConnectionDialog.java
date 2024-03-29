package configurations.gui;

import java.awt.Color;
import java.net.URL;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import configurations.SettingsManager;
import configurations.XMLResourceBundle;
import net.miginfocom.swing.MigLayout;

public class InternetConnectionDialog {
	private final JTextField apiUrl;
	private final JTextField port;
	private final JTextField host;
	private final JLabel status = new JLabel(" ");

	private JPanel panel = new JPanel();

	public InternetConnectionDialog() {
		MigLayout layout = new MigLayout("", "[left]");
		apiUrl = new JTextField(20);
		status.setForeground(Color.RED);
		port = new JTextField(20);
		host = new JTextField(20);
		status.setForeground(Color.RED);
		panel.setLayout(layout);
		panel.add(new JLabel("Proxy Settings"), "span 4");
		panel.add(new JSeparator(), "span, growx, wrap 15, gaptop 10, gap 5");
		panel.add(new JLabel("Proxy Port"), "span 2, gap 10, gaptop 2 ");
		panel.add(port, "span,wrap,growx ,gap 10, gaptop 2");
		panel.add(new JLabel("Proxy Host"), "span 2, gap 10, gaptop 2 ");
		panel.add(host, "span,wrap,growx ,gap 10, gaptop 2");

		panel.add(new JSeparator(), "span, growx, wrap 5, gaptop 2, gap 5");
		panel.add(new JLabel("API Settings"), "span 4");
		panel.add(new JSeparator(), "span, growx, wrap 15, gaptop 10, gap 5");
		panel.add(new JLabel("API URL"), "span 2, gap 17, gaptop 2 ");
		panel.add(apiUrl, "span,wrap 15 ,growx ,gap 10, gaptop 2");

		panel.add(new JLabel(" "), "span,wrap,growx ,gap 10, gaptop 2");
		panel.add(new JLabel(" "), "span,wrap,growx ,gap 10, gaptop 2");
		panel.add(status, "span, growx, gap 10 ,gaptop 15,wrap 15");

		apiUrl.setText(SettingsManager.getInstance().getApiUrl());
		port.setText(SettingsManager.getInstance().getProxyPort());
		host.setText(SettingsManager.getInstance().getProxyHost());
	}

	public boolean applyDefaults() {
		apiUrl.setText(XMLResourceBundle.SETTINGS.getString("settings.default.api.url"));
		port.setText(XMLResourceBundle.SETTINGS.getString("settings.default.proxy.port"));
		host.setText(XMLResourceBundle.SETTINGS.getString("settings.default.proxy.host"));
		return applyNewSettings();
	}

	public boolean applyNewSettings() {
		SettingsManager.getInstance().setApiUrl(apiUrl.getText());
		SettingsManager.getInstance().setProxyHost(host.getText());
		SettingsManager.getInstance().setProxyPort(port.getText());
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

	public JPanel getPanel() {
		return panel;
	}
}