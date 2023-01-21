package configurations.gui;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import configurations.ConnectionSettings;
import configurations.ResourceLibrary;
import net.miginfocom.swing.MigLayout;

/**
 * @author Sebastian
 * 
 */
public class InternetConnectionDialog extends JPanel {
	private static final long serialVersionUID = 1358638152136315177L;

	private JTextField proxy;
	private JTextField host;
	private JLabel status = new JLabel(" ");

	/**
	 * 
	 */
	public InternetConnectionDialog() {
		MigLayout layout = new MigLayout("", "[left]");

		proxy = new JTextField(20);
		host = new JTextField(20);

		status.setForeground(Color.RED);

		this.setLayout(layout);

		this.add(new JLabel("Internet Connection"), "span 4");
		this.add(new JSeparator(), "span, growx, wrap 15, gaptop 10, gap 5");

		this.add(new JLabel("Proxy Port"), "span 2, gap 10, gaptop 2 ");
		this.add(proxy, "span,wrap,growx ,gap 10, gaptop 2");

		this.add(new JLabel("Proxy Host"), "span 2, gap 10, gaptop 2 ");
		this.add(host, "span,wrap,growx ,gap 10, gaptop 2");

		this.add(new JLabel(" "), "span,wrap,growx ,gap 10, gaptop 2");

		this.add(new JLabel(" "), "span,wrap,growx ,gap 10, gaptop 2");

		this.add(status, "span, growx, gap 10 ,gaptop 15,wrap 15");

		proxy.setText(ConnectionSettings.getInstance().getProxyPort());
		host.setText(ConnectionSettings.getInstance().getProxyHost());

	}

	public void enableDispaly(boolean enabled) {
		proxy.setEnabled(enabled);
		host.setEnabled(enabled);
	}

	public boolean applyDefaults() {
		String def_host = ResourceLibrary.getSettingsResource("settings.default.proxy.host");
		String def_port = ResourceLibrary.getSettingsResource("settings.default.proxy.port");

		proxy.setText(def_port);
		host.setText(def_host);

		ConnectionSettings.getInstance().setProxyPort(def_port);
		ConnectionSettings.getInstance().setProxyHost(def_host);

		if (checkSettings()) {
			status.setText(" ");
			return true;
		} else {
			status.setText("Internet Connection Failure - change settings ");
			return false;
		}
	}

	public boolean applyNewSettings() {

		ConnectionSettings.getInstance().setProxyHost(host.getText());
		ConnectionSettings.getInstance().setProxyPort(proxy.getText());

		if (checkSettings()) {
			status.setText("");
			return true;
		} else {
			status.setText("Internet Connection Failure - change settings ");
			return false;
		}
	}

	private boolean checkSettings() {
		return true;
	}
}