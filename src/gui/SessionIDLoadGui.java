package gui;

import gui.algorithms.ScreenSize;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import net.miginfocom.swing.MigLayout;
import pojos.DBColumn;
import configurations.ConnectionSettingsSingelton;
import configurations.Wrapper;
import database.dawis.DAWISQueries;
import database.dawis.gui.DAWISSessionInfoWindow;
import database.dawis.webstart.DAWISBridge;

public class SessionIDLoadGui extends JFrame implements ActionListener {
	private static final long serialVersionUID = -2970818905100226930L;

	private JButton cancel = new JButton("cancel");
	private JButton load = new JButton("load");
	private JButton info = new JButton("info");
	private JButton[] buttons = { info, load, cancel };

	private JTextField id;
	private JPanel p;

	private JOptionPane optionPane;
	private JDialog dialog;

	public SessionIDLoadGui() {

		MigLayout layout = new MigLayout("", "[right]");
		p = new JPanel(layout);

		id = new JTextField(30);
		p.add(new JLabel(
				"Load a Network by a given Dawis Session Identification Number"),
				"span 4");
		p.add(new JSeparator(), "span, growx, wrap 15, gaptop 10, gap 5");

		p.add(new JLabel("Session-ID"), "span 2");
		p.add(id, "span,wrap,growx ,gap 10");
		p.add(new JSeparator(), "span, growx, wrap 10 ");
		p.add(new JLabel(), "gap 20, span 5");

	}

	public void showSettings() {

		optionPane = new JOptionPane(p, JOptionPane.PLAIN_MESSAGE);
		optionPane.setOptions(buttons);

		dialog = new JDialog(this, "Load Network", true);

		dialog.setContentPane(optionPane);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		cancel.addActionListener(this);
		cancel.setActionCommand("cancel");

		load.addActionListener(this);
		load.setActionCommand("load");

		info.addActionListener(this);
		info.setActionCommand("info");

		ScreenSize screen = new ScreenSize();
		int screenHeight = (int) screen.getheight();
		int screenWidth = (int) screen.getwidth();

		dialog.setSize(300, 300);
		dialog.setLocation((screenWidth / 2) - dialog.getSize().width / 2,
				(screenHeight / 2) - dialog.getSize().height / 2);
		dialog.pack();
		dialog.setVisible(true);

	}

	public void actionPerformed(ActionEvent e) {
		String event = e.getActionCommand();

		if ("cancel".equals(event)) {
			dialog.setVisible(false);

		} else if ("load".equals(event)) {
			if (ConnectionSettingsSingelton.useInternetConnection()) {
				if (id.getText().length() != 0) {
					// System.out.println("DEBUG SessionIDLoadGui: id ::: "+id.getText());
					if (testSessionID(id.getText())) {
						// DAWISWebstartConnector dws=new
						// DAWISWebstartConnector(id.getText());
						DAWISBridge dws = new DAWISBridge(id.getText());
						dws.execute();
						dialog.setVisible(false);
					} else {
						JOptionPane.showMessageDialog(
								MainWindowSingelton.getInstance(),
								"Unknown session id.");
					}
				} else {
					JOptionPane.showMessageDialog(
							MainWindowSingelton.getInstance(),
							"Please type something into the search form.");
				}
			} else {
				JOptionPane
						.showMessageDialog(
								MainWindowSingelton.getInstance(),
								"Sorry, no internet connection available. Please check your internet connection.");
			}

		} else if ("info".equals(event)) {
			new DAWISSessionInfoWindow();
		}
	}

	private boolean testSessionID(String sessionID) {

		boolean sessionIdExists = false;

		String[] id = { sessionID };
		// Vector v=new Wrapper().requestDbContent(3,
		// DAWISQueries.getRemoteControlSessionID, id);
		ArrayList<DBColumn> result = new Wrapper().requestDbContent(3,
				DAWISQueries.getRemoteControlSessionID, id);

		// System.out.println("DEBUG SessionIDLoadGui: server ::: "+ConnectionSettings.getDBConnection().getDatabase()+" ::: "+ConnectionSettings.getWebServiceUrl());
		// System.out.println("DEBUG SessionIDLoadGui: result ::: "+result.size());

		if (result.size() > 0) {
			sessionIdExists = true;
		}
		// System.out.println("DEBUG SessionIDLoadGui: sessionIdExists ::: "+sessionIdExists);

		return sessionIdExists;
	}
}
