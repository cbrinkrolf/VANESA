/***************************************************************
 * Copyright (c) Benjamin Kormeier 2006-2010.                  *
 * All rights reserved.                                        *
 ***************************************************************/
package configurations.gui;

import java.awt.Color;
import java.net.InetAddress;
import java.net.URL;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import configurations.ConnectionSettings;
import configurations.ResourceLibrary;
import configurations.asyncWebservice.AsynchroneWebServiceWrapper;
import database.Connection.DBconnection;

/**
 * @author Benjamin Kormeier
 * @version 1.0 21.10.2010
 */
public class ConnectionTab extends JPanel {
	private static final long serialVersionUID = 4607816664303962171L;

	private JTextField user;
	private JPasswordField password;
	private JTextField db;
	private JTextField host;
	private JLabel status = new JLabel(" ");
	private JTextField server_url;
	private ButtonGroup group = new ButtonGroup();
	private JRadioButton server = new JRadioButton("", true);
	private JRadioButton database = new JRadioButton("", true);

	private DBconnection db_connection = ConnectionSettings.getDBConnection();

	private AsynchroneWebServiceWrapper asyn_webservice = AsynchroneWebServiceWrapper.getInstance();
	private JCheckBox withWSAasynchron = new JCheckBox("", asyn_webservice.isWithAddressing());

	public ConnectionTab() {
		MigLayout layout = new MigLayout("", "[left]");

		group.add(server);
		group.add(database);

		user = new JTextField(20);
		user.setText(db_connection.getUser());

		password = new JPasswordField(20);
		password.setText(db_connection.getPassword());

		db = new JTextField(20);
		db.setText(db_connection.getDatabase());

		host = new JTextField(20);
		host.setText(db_connection.getServer());

		server_url = new JTextField(20);
		server_url.setText(ConnectionSettings.getWebServiceUrl());

		status.setForeground(Color.RED);

		this.setLayout(layout);

		this.add(new JLabel("Connection Type"), "span 4");
		this.add(new JSeparator(), "span, growx, wrap 5, gaptop 10, gap 5");

		JPanel connectionPanel = new JPanel();

		connectionPanel.add(new JLabel("Connect via web service"));
		connectionPanel.add(server);
		connectionPanel.add(new JLabel("Connect to database"));
		connectionPanel.add(database);

		this.add(connectionPanel, "span,wrap ,growx ,gap 10, gaptop 2");

		this.add(new JLabel("Database Settings"), "span 4");
		this.add(new JSeparator(), "span, growx, wrap 15, gaptop 10, gap 5");

		this.add(new JLabel("User"), "span 2, gap 17, gaptop 2 ");
		this.add(user, "span,wrap,growx ,gap 10, gaptop 2");

		this.add(new JLabel("Password"), "span 2, gap 17, gaptop 2 ");
		this.add(password, "span,wrap,growx ,gap 10, gaptop 2");

		this.add(new JLabel("Database"), "span 2, gap 17, gaptop 2 ");
		this.add(db, "span,wrap,growx ,gap 10, gaptop 2");

		this.add(new JLabel("Host:Port"), "span 2, gap 17, gaptop 2 ");
		this.add(host, "span,wrap 15 ,growx ,gap 10, gaptop 2");

		this.add(new JLabel("Web Server Settings"), "span 4");
		this.add(new JSeparator(), "span, growx, wrap 15, gaptop 10, gap 5");

		this.add(new JLabel("Server URL"), "span 2, gap 17, gaptop 2 ");
		this.add(server_url, "span,wrap 15 ,growx ,gap 10, gaptop 2");

		this.add(new JLabel("WebService Type"), "span 4");
		this.add(new JSeparator(), "span, growx, wrap 5, gaptop 2, gap 5");
		this.add(new JLabel("With adressing"), "span 2, gap 17, gaptop 2 ");
		this.add(withWSAasynchron, "span,wrap 15 ,growx ,gap 10, gaptop 2");

		this.add(status, "span, growx, gap 10 ,gaptop 15,wrap 15");
	}

	public void enableDispaly(boolean enabled) {
		user.setEnabled(enabled);
		password.setEnabled(enabled);
		db.setEnabled(enabled);
		host.setEnabled(enabled);
		server.setEnabled(enabled);
		database.setEnabled(enabled);
		server_url.setEnabled(enabled);
		withWSAasynchron.setEnabled(enabled);
	}

	public boolean applyNewSettings() {
		db_connection.setPassword(new String(password.getPassword()));
		db_connection.setUser(user.getText());
		db_connection.setDatabase(db.getText());
		db_connection.setServer(host.getText());

		ConnectionSettings.setDBConnection(db_connection);

		if (server.isSelected()) {
			ConnectionSettings.setInternetConnection(true);
		} else {
			ConnectionSettings.setInternetConnection(false);
		}

		asyn_webservice.setAddressing(withWSAasynchron.isSelected());

		ConnectionSettings.setWebServiceUrl(server_url.getText());

		return checkSettings();
	}

	public boolean applyDefaults() {
		String def_user = ResourceLibrary.getSettingsResource("settings.default.user");
		String def_password = ResourceLibrary.getSettingsResource("settings.default.password");
		String def_database = ResourceLibrary.getSettingsResource("settings.default.database.DAWIS");
		String def_server = ResourceLibrary.getSettingsResource("settings.default.server");
		String def_webservice = ResourceLibrary.getSettingsResource("settings.default.webservice.url");

		db_connection.setPassword(def_password);
		db_connection.setUser(def_user);
		db_connection.setDatabase(def_database);
		db_connection.setServer(def_server);

		ConnectionSettings.setDBConnection(db_connection);
		ConnectionSettings.setInternetConnection(true);
		ConnectionSettings.setWebServiceUrl(def_webservice);

		asyn_webservice.setAddressing(true);

		if (checkSettings()) {
			user.setText(def_user);
			password.setText(def_password);
			db.setText(def_database);
			host.setText(def_server);
			server.setSelected(true);

			server_url.setText(def_webservice);

			withWSAasynchron.setSelected(true);

			status.setText(" ");
			return true;
		} else {
			status.setText("Data not reachable - change settings!");
			return false;
		}
	}

	private boolean checkSettings() {
		// -- check database connection --
		try {
			if (database.isSelected()) {
				status.setText("Database not reachable - change settings!");
				ConnectionSettings.getDBConnection().checkConnection();
			}
			if (server.isSelected()) {
				status.setText("Web Server not reachable - change settings!");
				InetAddress.getByName(new URL(ConnectionSettings.getWebServiceUrl()).getHost()).isReachable(2000);
			}

			status.setText("");

			return true;
		} catch (Exception e) {
			System.out.println(e);
			status.setToolTipText(e.toString());
			return false;
		}
	}
}
