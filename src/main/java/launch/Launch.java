package launch;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.log4j.Logger;

import com.jtattoo.plaf.aluminium.AluminiumLookAndFeel;

import biologicalElements.ElementNames;
import biologicalElements.ElementNamesSingleton;
import configurations.ConnectionSettings;
import configurations.ProgramFileLock;
import configurations.ResourceLibrary;
import database.Connection.DBConnection;
import database.brenda.MostWantedMolecules;
import graph.GraphContainer;
import gui.IntroScreen;
import gui.MainWindow;

public class Launch {
	public static void main(String[] args) {
		// avoid strange awt/swing exceptions: Exception in thread "AWT-EventQueue-0"
		// java.lang.IllegalArgumentException: Comparison method violates its general contract!
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
		// set app name for mac osx
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", "VANESA");

		final IntroScreen intro = new IntroScreen();
		intro.openWindow();

		if (!ProgramFileLock.writeLock()) {
			JOptionPane.showMessageDialog(null, "Another instance of the program is already running!\nExit program.",
					"Exit", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}

		try {
			Properties props = new Properties();
			props.put("logoString", "VANESA");
			// workaround to get the menu into the main upper menu pane on mac osx
			// @see http://www.pushing-pixels.org/?p=366
			if (System.getProperty("os.name").startsWith("Mac")) {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				Object MenuBarUI = UIManager.get("MenuBarUI");
				Object MenuUI = UIManager.get("MenuUI");
				Object MenuItemUI = UIManager.get("MenuItemUI");
				Object CheckBoxMenuItemUI = UIManager.get("CheckBoxMenuItemUI");
				Object RadioButtonMenuItemUI = UIManager.get("RadioButtonMenuItemUI");
				Object PopupMenuUI = UIManager.get("PopupMenuUI");
				UIManager.setLookAndFeel(new AluminiumLookAndFeel());
				UIManager.put("MenuBarUI", MenuBarUI);
				UIManager.put("MenuUI", MenuUI);
				UIManager.put("MenuItemUI", MenuItemUI);
				UIManager.put("CheckBoxMenuItemUI", CheckBoxMenuItemUI);
				UIManager.put("RadioButtonMenuItemUI", RadioButtonMenuItemUI);
				UIManager.put("PopupMenuUI", PopupMenuUI);
				props.put("macStyleWindowDecoration", "on");
				props.put("macStyleScrollBar", "on");
			} else {
				UIManager.setLookAndFeel(new AluminiumLookAndFeel());
			}
			AluminiumLookAndFeel.setCurrentTheme(props);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("fail");
		}

		// configure logging
		logConfig();

		// go on with creating the editor window

		// create 3 Runnable instances to run them in separate threads simultaneously
		Runnable programStart = () -> SwingUtilities.invokeLater(() -> {
			intro.setLoadingText("Graphical User Interface");
			MainWindow w = MainWindow.getInstance();
			intro.closeWindow();
			w.getFrame().setEnabled(true);
			SwingUtilities.updateComponentTreeUI(w.getFrame());
		});

		Runnable containerStart = () -> {
			intro.setLoadingText("WebConnection");
			GraphContainer.getInstance();
			intro.setLoadingText("Database Information");
			new MostWantedMolecules();
			ElementNames names = ElementNamesSingleton.getInstance();
			names.fillEnzymeSet();
		};

		Runnable dbStart = () -> {
			String user = ResourceLibrary.getSettingsResource("settings.default.user");
			String password = ResourceLibrary.getSettingsResource("settings.default.password");
			String database = ResourceLibrary.getSettingsResource("settings.default.database.DAWIS");
			String databasePpi = ResourceLibrary.getSettingsResource("settings.default.database.PPI");
			String databaseMirna = ResourceLibrary.getSettingsResource("settings.default.database.mirna");
			String databaseMirnaNew = ResourceLibrary.getSettingsResource("settings.default.database.mirna_New");
			String server = ResourceLibrary.getSettingsResource("settings.default.server");
			ConnectionSettings.getInstance().setDBConnection(new DBConnection(user, password, database, server));
			ConnectionSettings.getInstance().getDBConnection().setDawisDBName(database);
			ConnectionSettings.getInstance().getDBConnection().setPpiDBName(databasePpi);
			ConnectionSettings.getInstance().getDBConnection().setMirnaDBName(databaseMirna);
			ConnectionSettings.getInstance().getDBConnection().setMirnaNewDBName(databaseMirnaNew);
			ConnectionSettings.getInstance().setWebServiceUrl(
					ResourceLibrary.getSettingsResource("settings.default.webservice.url"));
			ConnectionSettings.getInstance().setApiUrl(
					ResourceLibrary.getSettingsResource("settings.default.api.url"));
		};
		ExecutorService executorService = Executors.newFixedThreadPool(4);
		executorService.execute(programStart);
		executorService.execute(dbStart);
		executorService.execute(containerStart);
		executorService.shutdown();
	}

	/**
	 * Default logging for the network editor. Console and file logging is possible.<br>
	 * Configuration method uses programmable configuration class.
	 */
	private static void logConfig() {
		Logger logger = Logger.getRootLogger();
		logger.info("Network editor started by " + System.getProperty("user.name"));
	}
}
