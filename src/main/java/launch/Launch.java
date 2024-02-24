package launch;

import biologicalElements.EnzymeNames;
import com.jtattoo.plaf.aluminium.AluminiumLookAndFeel;
import configurations.SettingsManager;
import configurations.ProgramFileLock;
import configurations.XMLResourceBundle;
import database.brenda.MostWantedMolecules;
import gui.IntroScreen;
import gui.MainWindow;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
        Logger logger = Logger.getRootLogger();
        logger.info("Network editor started by " + System.getProperty("user.name"));
        SettingsManager.getInstance().setApiUrl(XMLResourceBundle.SETTINGS.getString("settings.default.api.url"));
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        executorService.execute(() -> SwingUtilities.invokeLater(() -> {
            intro.setLoadingText("Graphical User Interface");
            MainWindow w = MainWindow.getInstance();
            intro.closeWindow();
            w.getFrame().setEnabled(true);
            SwingUtilities.updateComponentTreeUI(w.getFrame());
        }));
        executorService.execute(() -> {
            intro.setLoadingText("Load Cached Database Information");
            MostWantedMolecules.getInstance().fillMoleculeSet();
            EnzymeNames.getInstance().fillEnzymeSet();
        });
        executorService.shutdown();
    }
}
