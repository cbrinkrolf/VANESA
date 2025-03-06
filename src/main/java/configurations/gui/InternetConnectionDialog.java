package configurations.gui;

import java.awt.*;

import javax.swing.*;

import configurations.SettingsManager;
import configurations.XMLResourceBundle;
import gui.ImagePath;
import gui.JIntTextField;
import gui.JValidatedURLTextField;
import net.miginfocom.swing.MigLayout;

public class InternetConnectionDialog extends JPanel {
	private final ImageIcon infoImage = ImagePath.getInstance().getImageIcon("infoButton.png");
	private final JValidatedURLTextField apiUrlTextField = new JValidatedURLTextField();
	private final JIntTextField proxyPortTextField = new JIntTextField();
	private final JTextField proxyHostTextField = new JTextField();

	private final JPanel contentPanel = new JPanel(new MigLayout("ins 0, fillx, wrap"));

	public InternetConnectionDialog() {
		super(new MigLayout("ins 0, fill, wrap"));
		final JScrollPane scrollPane = new JScrollPane(contentPanel);
		add(scrollPane, "grow");
		addHeader("Proxy Settings");
		addSetting("Proxy Host", "Host address of the proxy", proxyHostTextField);
		addSetting("Proxy Port", "Port of the proxy", proxyPortTextField);
		addHeader("API Settings");
		addSetting("API Url", "Base URL of the VANESA API used for database queries", apiUrlTextField);

		apiUrlTextField.setText(SettingsManager.getInstance().getApiUrl());
		proxyPortTextField.setText(SettingsManager.getInstance().getProxyPort());
		proxyHostTextField.setText(SettingsManager.getInstance().getProxyHost());
	}

	private void addHeader(final String label) {
		final JPanel header = new JPanel(new MigLayout("left"));
		header.setBackground(new Color(164, 164, 164));
		final JLabel headerLabel = new JLabel(label);
		headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD));
		header.add(headerLabel);
		contentPanel.add(header, "growx");
	}

	private void addSetting(final String label, final String help, final JComponent component) {
		final JPanel row = new JPanel(new MigLayout("fill", "[200:200:200][grow][]"));
		row.setBackground(new Color(200, 200, 200));
		row.add(new JLabel(label), "growx, top");
		row.add(component, "grow");
		final JLabel helpLabel = new JLabel(infoImage);
		helpLabel.setToolTipText(help);
		row.add(helpLabel, "top");
		contentPanel.add(row, "growx");
	}

	public boolean applyDefaults() {
		apiUrlTextField.setText(XMLResourceBundle.SETTINGS.getString("settings.default.api.url"));
		proxyPortTextField.setText(XMLResourceBundle.SETTINGS.getString("settings.default.proxy.port"));
		proxyHostTextField.setText(XMLResourceBundle.SETTINGS.getString("settings.default.proxy.host"));
		return applyNewSettings();
	}

	public boolean applyNewSettings() {
		SettingsManager.getInstance().setApiUrl(apiUrlTextField.getText());
		SettingsManager.getInstance().setProxyHost(proxyHostTextField.getText());
		SettingsManager.getInstance().setProxyPort(proxyPortTextField.getText());
		return apiUrlTextField.isValid();
	}
}