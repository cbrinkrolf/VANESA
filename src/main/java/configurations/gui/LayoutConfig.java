package configurations.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.SwingWorker;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import graph.GraphInstance;
import graph.layouts.gemLayout.GEMLayoutConfigSingleton;
import graph.layouts.hctLayout.HCTLayoutConfig;
import graph.layouts.hebLayout.HEBLayoutConfig;
import gui.MainWindow;
import gui.RangeSelector;

public class LayoutConfig extends JPanel implements ActionListener {
	private static final long serialVersionUID = -25474744959063431L;
	private static LayoutConfig instance;
	private final JButton cancel = new JButton("cancel");
	private final JButton resetButton = new JButton("reset");
	private final JButton applyButton = new JButton("apply");
	private final JButton[] buttons = {applyButton, resetButton, cancel };
	private final JTabbedPane tabbedPane = new JTabbedPane();
	private final Map<String, ConfigPanel> tabs = new HashMap<>();
	private final JProgressBar progressBar;

	public LayoutConfig() {
		for (JButton b : this.buttons) {
			b.addActionListener(this);
		}
		//addTab(new MDForceLayoutConfig());
		addTab(new ConfigPanel(CircleLayout.class));
		addTab(HEBLayoutConfig.getInstance());
		addTab(HCTLayoutConfig.getInstance());
		addTab(GEMLayoutConfigSingleton.getInstance());
		addTab(new ConfigPanel(FRLayout.class));
		addTab(new ConfigPanel(KKLayout.class));
		addTab(new ConfigPanel(SpringLayout.class));
		addTab(new ConfigPanel(ISOMLayout.class));
		progressBar = new JProgressBar();
		setLayout(new BorderLayout());
		add(tabbedPane, BorderLayout.CENTER);
		add(progressBar, BorderLayout.SOUTH);
	}

	public static LayoutConfig getInstance() {
		if (instance == null) {
			instance = new LayoutConfig();
		}
		return instance;
	}

	public static void changeToLayout(Class<? extends Layout> layout) {
		if (RangeSelector.getInstance().hasRange()) {
			int option = JOptionPane.showConfirmDialog(GraphInstance.getMyGraph().getVisualizationViewer(),
													   "this graph contains selected clusters,\nall selected " +
													   "clusters maybe out of date\nafter new layout!",
													   "continue", JOptionPane.YES_NO_OPTION);
			if (option != JOptionPane.YES_OPTION) {
				return;
			}
		}
		getInstance().setSelection(layout.getSimpleName());
	}

	public void addTab(ConfigPanel panel) {
		tabs.put(panel.getLayoutName(), panel);
		tabbedPane.addTab(panel.getLayoutName(), null, panel, panel.getLayoutName() + " settings");
	}

	public void setSelection(String layout) {
		tabbedPane.setSelectedComponent(tabs.get(layout));
		showSettings();
	}

	private void showSettings() {
		JOptionPane.showOptionDialog(MainWindow.getInstance().getFrame(), this, "Layout settings",
									 JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, buttons, applyButton);
	}

	public static JOptionPane getOptionPane(Container comp) {
		try {
			return (JOptionPane) comp;
		} catch (ClassCastException e) {
			return getOptionPane(comp.getParent());
		}
	}

	public void actionPerformed(final ActionEvent e) {
		final JOptionPane pane = getOptionPane(this);
		String event = e.getActionCommand();
		final ConfigPanel config = (ConfigPanel) tabbedPane.getSelectedComponent();
		if ("reset".equals(event)) {
			config.resetValues();
		} else if ("cancel".equals(event)) {
			pane.setValue(e.getSource());
		} else if ("apply".equals(event)) {
			progressBar.setIndeterminate(true);
			final Cursor old = LayoutConfig.this.getCursor();
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			config.setValues();
			// CHRIS avoid SwingWorker
			new SwingWorker<>() {
				@Override
				protected Object doInBackground() {
					try {
						config.applySettings();
					} finally {
						progressBar.setIndeterminate(false);
						setCursor(old);
						pane.setValue(e.getSource());
					}
					return null;
				}
			}.execute();
		}
	}
}
