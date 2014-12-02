/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * MDLayoutConfig.java
 *
 * Created on 16.07.2008, 12:31:43
 */
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
/*import edu.uci.ics.jung.visualization.FRLayout;
import edu.uci.ics.jung.visualization.ISOMLayout;
import edu.uci.ics.jung.visualization.Layout;
import edu.uci.ics.jung.visualization.SpringLayout;
import edu.uci.ics.jung.visualization.contrib.CircleLayout;
import edu.uci.ics.jung.visualization.contrib.KKLayout;*/
import graph.GraphInstance;
import graph.layouts.gemLayout.GEMLayoutConfig;
import graph.layouts.hebLayout.HEBLayoutConfig;
import gui.MainWindowSingleton;
import gui.RangeSelector;

/**
 * 
 * @author dao
 */
public class LayoutConfig extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Variables declaration
	private JButton cancel = new JButton("cancel");
	private JButton defaultButton = new JButton("reset");
	private JButton applyButton = new JButton("apply");
	private JButton[] buttons = { applyButton, defaultButton, cancel };
	private JTabbedPane tabbedPane = new JTabbedPane();
	private Map<String, ConfigPanel> tabs = new HashMap<String, ConfigPanel>();
	private JProgressBar progressBar;
	private static LayoutConfig INSTANCE;

	// End of variables declaration
	/** Creates new form MDLayoutConfig */
	public LayoutConfig() {
		initComponents();
	}

	public static LayoutConfig getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new LayoutConfig();
		}
		return INSTANCE;
	}

	public static void changeToLayout(Class<? extends Layout> layout) {
		if (RangeSelector.getInstance().hasRange()) {
			int option = JOptionPane.showConfirmDialog(GraphInstance
					.getMyGraph().getVisualizationViewer(),
					"this graph contains selected clusters,\n"
							+ "all selected clusters maybe out of date\n "
							+ "after new layout!", "continue",
					JOptionPane.YES_NO_OPTION);
			if (option != JOptionPane.YES_OPTION) {
				return;
			}
		}
		getInstance().setSelection(layout.getSimpleName());
	}

	public void addTab(ConfigPanel panel) {
		tabs.put(panel.getLayoutName(), panel);
		tabbedPane.addTab(panel.getLayoutName(), null, panel, panel
				.getLayoutName()
				+ " settings");
	}

	private void initComponents() {
		for (JButton b : this.buttons) {
			b.addActionListener(this);
		}
		//addTab(new MDForceLayoutConfig());
		addTab(new ConfigPanel(CircleLayout.class));
		addTab(HEBLayoutConfig.getInstance());
		addTab(GEMLayoutConfig.getInstance());
		addTab(new ConfigPanel(FRLayout.class));
		addTab(new ConfigPanel(KKLayout.class));
		addTab(new ConfigPanel(SpringLayout.class));
		addTab(new ConfigPanel(ISOMLayout.class));

		progressBar = new JProgressBar();
		this.setLayout(new BorderLayout());
		this.add(tabbedPane, BorderLayout.CENTER);
		this.add(this.progressBar, BorderLayout.SOUTH);
	}

	public void setSelection(String layout) {
		tabbedPane.setSelectedComponent(tabs.get(layout));
		showSettings();
	}

	private void showSettings() {
		int option = JOptionPane.showOptionDialog(MainWindowSingleton.getInstance(), this,
				"Layout settings", JOptionPane.DEFAULT_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, buttons, applyButton);
	}

	public static JOptionPane getOptionPane(Container comp) {
		try {
			JOptionPane pane = (JOptionPane) comp;
			return pane;
		} catch (ClassCastException e) {
			return getOptionPane(comp.getParent());
		}
	}

	public void actionPerformed(final ActionEvent e) {
		final JOptionPane pane = getOptionPane(this);
		String event = e.getActionCommand();
		final ConfigPanel config = (ConfigPanel) tabbedPane
				.getSelectedComponent();
		if ("reset".equals(event)) {
			config.resetValues();
		} else if ("cancel".equals(event)) {
			pane.setValue(e.getSource());
		} else if ("apply".equals(event)) {
			this.progressBar.setIndeterminate(true);
			final Cursor old = LayoutConfig.this.getCursor();
			LayoutConfig.this.setCursor(Cursor
					.getPredefinedCursor(Cursor.WAIT_CURSOR));
			config.setValues();
			new SwingWorker() {

				@Override
				protected Object doInBackground() throws Exception {
					try {
						config.applySettings();
					} finally {
						progressBar.setIndeterminate(false);
						LayoutConfig.this.setCursor(old);
						pane.setValue(e.getSource());
					}
					return null;
				}
			}.execute();
		}
	}

}
