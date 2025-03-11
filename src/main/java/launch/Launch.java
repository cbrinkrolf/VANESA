package launch;

import biologicalElements.EnzymeNames;
import com.jtattoo.plaf.aluminium.AluminiumLookAndFeel;
import configurations.Workspace;
import database.brenda.MostWantedMolecules;
import gui.IntroScreen;
import gui.MainWindow;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Launch {
	public static void main(String[] args) {
		// set app name for mac osx
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", "VANESA");

		Thread.currentThread().setUncaughtExceptionHandler(
				(t, e) -> Logger.getRootLogger().error("Critical error in thread '" + t.getName() + "'", e));
		try {
			SwingUtilities.invokeAndWait(() -> Thread.currentThread().setUncaughtExceptionHandler(
					(t, e) -> Logger.getRootLogger().error("Error in AWT thread '" + t.getName() + "'", e)));
		} catch (InterruptedException | InvocationTargetException ignored) {
		}

		final Workspace workspace = Workspace.switchToDefaultWorkspace();

		final IntroScreen intro = new IntroScreen();
		intro.openWindow();

		if (!workspace.hasLock()) {
			JOptionPane.showMessageDialog(null, "Another instance of the program is already running!\nExit program.",
					"Exit", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}

		try {
			final Properties props = new Properties();
			props.put("logoString", "VANESA");
			// workaround to get the menu into the main upper menu pane on macOS
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
			Logger.getRootLogger().error("Failed to setup UI", e);
		}
		Logger.getRootLogger().info("VANESA started by " + System.getProperty("user.name"));
		final ExecutorService executorService = Executors.newFixedThreadPool(4);
		executorService.execute(() -> SwingUtilities.invokeLater(() -> {
			intro.setLoadingText("Graphical User Interface");
			final MainWindow w = MainWindow.getInstance();
			intro.closeWindow();
			w.getFrame().setEnabled(true);
			SwingUtilities.updateComponentTreeUI(w.getFrame());
		}));
		executorService.execute(() -> {
			intro.setLoadingText("Cached Database Information");
			MostWantedMolecules.getInstance().fillMoleculeSet();
			EnzymeNames.getInstance().fillEnzymeSet();
		});
		executorService.shutdown();
	}
}
