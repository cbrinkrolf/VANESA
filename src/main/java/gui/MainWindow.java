package gui;

import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import com.jhlabs.image.BlurFilter;
import configurations.XMLResourceBundle;
import configurations.gui.Settings;
import configurations.gui.VisualizationDialog;
import graph.GraphContainer;
import graph.eventhandlers.GraphTabListener;
import graph.eventhandlers.GraphWindowListener;
import graph.jung.graphDrawing.VertexShapes;
import gui.algorithms.CenterWindow;
import gui.algorithms.ScreenSize;
import gui.eventhandlers.PanelListener;
import gui.visualization.VisualizationConfigBeans.Bean;
import gui.visualization.YamlToObjectParser;
import io.SaveDialog;
import io.sbml.JSBMLOutput;
import net.infonode.docking.DockingWindow;
import net.infonode.docking.RootWindow;
import net.infonode.docking.View;
import net.infonode.docking.util.DockingUtil;
import net.infonode.docking.util.ViewMap;
import net.infonode.tabbedpanel.Tab;
import net.infonode.tabbedpanel.TabDropDownListVisiblePolicy;
import net.infonode.tabbedpanel.TabbedPanel;
import net.infonode.tabbedpanel.titledtab.TitledTab;
import net.infonode.util.Direction;
import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.jxlayer.plaf.effect.BufferedImageOpEffect;
import org.jdesktop.jxlayer.plaf.ext.LockableUI;
import org.simplericity.macify.eawt.Application;
import org.simplericity.macify.eawt.ApplicationEvent;
import org.simplericity.macify.eawt.ApplicationListener;
import org.simplericity.macify.eawt.DefaultApplication;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.util.List;
import java.util.*;

public class MainWindow implements ApplicationListener {
	private final JFrame frame = new JFrame();
	private final HashMap<Integer, View> views = new HashMap<>();
	private final ViewMap viewMap = new ViewMap();
	private RootWindow rootWindow;
	private final ToolBar bar;
	private int maxPanelID = -1;
	private int selectedView = 0;
	private List<Bean> beansList = new ArrayList<>();
	private String loadedYaml = null;
	private final VertexShapes vs = new VertexShapes();

	// global constants
	public static boolean developer;

	private final LockableUI blurUI = new LockableUI(new BufferedImageOpEffect(new BlurFilter()));
	private final JSplitPane splitPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

	private final HashMap<Integer, TabbedPanel> tabbedPanels = new HashMap<>();
	private final GraphContainer con = GraphContainer.getInstance();
	private int addedTabs = 0;
	private final MenuBarClass myMenu;

	private final OptionPanel optionPanel;

	private static IndeterminateProgressBar progressbar = null;

	private static MainWindow instance;

	private MainWindow() {
		JFrame.setDefaultLookAndFeelDecorated(true);
		developer = Boolean.parseBoolean(XMLResourceBundle.SETTINGS.getString("settings.default.developer"));
		// try {
		// SubstanceBusinessBlueSteelLookAndFeel lf = new SubstanceBusinessBlueSteelLookAndFeel();
		// lf.setSkin("");
		// UIManager.setLookAndFeel(new SubstanceBusinessBlueSteelLookAndFeel());
		// } catch (Exception e) {
		// }
		// SwingUtilities.invokeLater(new Runnable()
		// {
		// public void run() {
		// SwingUtilities.updateComponentTreeUI();
		// }
		// });
		// SwingUtilities.updateComponentTreeUI(this);

		frame.setTitle("VANESA 2.0 - Visualization and Analysis of Networks in Systems Biology Applications");
		frame.setVisible(false);

		// MacOSX-Look and Feel with http://simplericity.org/macify/
		Application application = new DefaultApplication();
		application.addPreferencesMenuItem();
		application.setEnabledPreferencesMenu(true);

		// create menu bar
		myMenu = new MenuBarClass(application);

		// load dock icon on mac osx
		ImagePath imagePath = ImagePath.getInstance();
		ImageIcon ii = ImagePath.getInstance().getImageIcon("graph2.png");
		BufferedImage b = new BufferedImage(ii.getIconWidth(), ii.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		ii.paintIcon(frame, b.createGraphics(), 0, 0);
		application.setApplicationIconImage(b);

		// react to special application menu items on mac osx
		application.addApplicationListener(this);

		List<Image> iconList = new ArrayList<>();
		iconList.add(imagePath.getImageIcon("logo16.png").getImage());
		iconList.add(imagePath.getImageIcon("logo32.png").getImage());
		iconList.add(imagePath.getImageIcon("logo64.png").getImage());
		iconList.add(imagePath.getImageIcon("logo128.png").getImage());
		frame.setIconImages(iconList);

		// try {
		// InfoNodeLookAndFeelTheme theme = InfoNodeLookAndFeelThemes.getSoftGrayTheme();
		// UIManager.setLookAndFeel(new InfoNodeLookAndFeel(theme));
		// } catch (UnsupportedLookAndFeelException e1) {
		// e1.printStackTrace();
		// }

		ScreenSize screenSize = new ScreenSize();
		int windowWidth = (int) screenSize.getwidth() - 70;
		int windowHeight = (int) screenSize.getheight() - 100;
		if (screenSize.getwidth() > 1024) {
			windowWidth = 1024;
			windowHeight = windowWidth * 2 / 3;
		}
		frame.setEnabled(false);
		frame.setSize(windowWidth, windowHeight);
		new CenterWindow(frame).centerWindow(windowWidth, windowHeight);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}
		});
		bar = new ToolBar(false);
		// create menu
		frame.setJMenuBar(myMenu.returnMenu());
		// toolbar with the buttons on the right
		root = (JComponent) frame.getContentPane();
		// getContentPane().add(new ToolBar(false).getToolBar(),
		// BorderLayout.EAST);
		root.add(bar.getToolBar(), BorderLayout.EAST);
		// main graph view in the middle
		// getContentPane().add(getRootWindow(), BorderLayout.CENTER);
		// root.add(getRootWindow(), BorderLayout.CENTER);

		// option panels on the left
		optionPanel = new OptionPanel();
		// getContentPane().add(optionPanel.getPanel(), BorderLayout.WEST);
		// root.add(optionPanel.getPanel(), BorderLayout.WEST);

		splitPanel.add(optionPanel.getPanel());
		addView();
		splitPanel.setOneTouchExpandable(false);
		splitPanel.addPropertyChangeListener(new PropertyChangeListener() {
			private final int SP_DIVIDER_MAX_LOCATION = splitPanel.getLeftComponent().getMaximumSize().width;

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				final String prop_name = evt.getPropertyName();
				if (prop_name.equals(JSplitPane.LAST_DIVIDER_LOCATION_PROPERTY)
						|| prop_name.equals(JSplitPane.DIVIDER_LOCATION_PROPERTY)) {
					if (splitPanel.getDividerLocation() > SP_DIVIDER_MAX_LOCATION)
						splitPanel.setDividerLocation(SP_DIVIDER_MAX_LOCATION);
				}
			}
		});
		root.add(splitPanel, BorderLayout.CENTER);
		JXLayer<JComponent> layer = new JXLayer<>(root);
		layer.setUI(blurUI);
		frame.setContentPane(layer);
		frame.setVisible(true);
		try {
			askForYaml();
		} catch (FileNotFoundException e1) {
			System.out.println("askForYaml Method Error");
			e1.printStackTrace();
		}
	}

	public static synchronized MainWindow getInstance() {
		if (MainWindow.instance == null) {
			MainWindow.instance = new MainWindow();
		}
		return MainWindow.instance;
	}

	public void nodeAttributeChanger(BiologicalNodeAbstract bna, boolean doResetAppearance) {
		for (Bean bean : beansList) {
			String shapeBean = bean.getShape();
			if (bean.getName().equals(bna.getBiologicalElement())) {
				switch (shapeBean) {
				case "ellipse":
					bna.setDefaultShape(vs.getEllipse());
					break;
				case "rectangle":
					bna.setDefaultShape(vs.getRectangle());
					break;
				case "rounded rectangle":
					bna.setDefaultShape(vs.getRoundRectangle());
					break;
				case "triangle":
					bna.setDefaultShape(vs.getRegularPolygon(3));
					break;
				case "pentagon":
					bna.setDefaultShape(vs.getRegularPolygon(5));
					break;
				case "hexagon":
					bna.setDefaultShape(vs.getRegularPolygon(6));
					break;
				case "octagon":
					bna.setDefaultShape(vs.getRegularPolygon(8));
					break;
				case "5 star":
					bna.setDefaultShape(vs.getRegularStar(5));
					break;
				case "6 star":
					bna.setDefaultShape(vs.getRegularStar(6));
					break;
				case "7 star":
					bna.setDefaultShape(vs.getRegularStar(7));
					break;
				case "8 star":
					bna.setDefaultShape(vs.getRegularStar(8));
					break;
				default:
					System.out.println(bna.getName() + ": No shape defined! Default shape used!");
					bna.setDefaultShape(vs.getEllipse());
				}
				Color colorBean = new Color(bean.getColorRed(), bean.getColorGreen(), bean.getColorBlue());
				bna.setDefaultColor(colorBean);
				double nodeSizeBean = bean.getSizefactor();
				bna.setDefaultNodesize(nodeSizeBean);
			}
		}
		if (doResetAppearance) {
			bna.resetAppearance();
		}
	}

	public void askForYaml() throws FileNotFoundException {
		String yamlSourceFile = new File("YamlSourceFile.txt").getAbsolutePath();
		File file = new File(yamlSourceFile);
		BufferedReader buffReader;
		System.out.println(yamlSourceFile);
		if (file.exists()) {
			try {
				buffReader = new BufferedReader(new FileReader(yamlSourceFile));
				loadedYaml = buffReader.readLine();
			} catch (IOException e) {
				System.out.println("Buffered Reader (YamlSource) Error");
				e.printStackTrace();
			}
			File yamlFile = new File(loadedYaml);
			if (!yamlFile.exists()) {
				loadedYaml = VisualizationDialog.DEFAULTYAML;
			}
		} else {
			loadedYaml = VisualizationDialog.DEFAULTYAML;
		}
		YamlToObjectParser yamlToObject = new YamlToObjectParser(loadedYaml);
		beansList = yamlToObject.startConfig();
	}

	private void setWindowProperties(int viewID) {
		views.get(viewID).getWindowProperties().setCloseEnabled(true);
		views.get(viewID).getWindowProperties().setUndockEnabled(false);
		views.get(viewID).getWindowProperties().setDragEnabled(true);
		views.get(viewID).getWindowProperties().setMaximizeEnabled(false);
		views.get(viewID).getWindowProperties().setMinimizeEnabled(false);
		PanelListener lis = new PanelListener();
		views.get(viewID).addListener(lis);
	}

	public void addView() {
		// remove rootWindow if exists
		if (rootWindow != null) {
			splitPanel.remove(rootWindow);
		}
		// create new tabbedPanel
		TabbedPanel tp = new TabbedPanel();
		tp.getProperties().setTabAreaOrientation(Direction.DOWN);
		tp.getProperties().setEnsureSelectedTabVisible(true);
		tp.getProperties().setHighlightPressedTab(true);
		tp.addTabListener(new GraphTabListener(this));
		tp.getProperties().setTabReorderEnabled(true);
		tp.getProperties().setTabDropDownListVisiblePolicy(TabDropDownListVisiblePolicy.MORE_THAN_ONE_TAB);
		tp.setBackground(Color.BLACK);
		maxPanelID += 1;
		int id = maxPanelID;
		tabbedPanels.put(id, tp);
		View view = new View("Network Modelling", null, tp);
		view.addListener(new GraphWindowListener());
		views.put(id, view);
		setWindowProperties(id);
		viewMap.addView(id, view);
		rootWindow = DockingUtil.createRootWindow(viewMap, true);
		rootWindow.getWindowBar(Direction.DOWN).setEnabled(true);
		splitPanel.add(rootWindow);
		view.makeVisible();
		setSelectedView(view);
	}

	public void removeView(DockingWindow dw) {
		// remove rootWindow if exists
		// if(rootWindow!=null){
		// split_pane.remove(rootWindow);
		// }
		if (dw instanceof View) {
			View view = (View) dw;
			int id = -1;
			for (int key : views.keySet()) {
				if (views.get(key).equals(view)) {
					id = key;
					break;
				}
			}
			views.remove(id);
			tabbedPanels.remove(id);
			viewMap.removeView(id);
			if (views.keySet().iterator().hasNext()) {
				setSelectedView(views.get(views.keySet().iterator().next()));
			}
		}
	}

	public void addTab(TitledTab tab) {
		addedTabs++;
		if (tabbedPanels.isEmpty()) {
			addView();
		}
		tabbedPanels.get(getSelectedView()).addTab(tab);
		setSelectedTab(tab);
		myMenu.enableCloseAndSaveFunctions();
	}

	public int getTabCount() {
		return addedTabs;
	}

	public void removeTab(boolean ask) {
		boolean reallyAsk = ask;
		TitledTab removed = (TitledTab) tabbedPanels.get(getSelectedView()).getSelectedTab();
		Pathway pw = con.getPathway(removed.getText());
		if (pw.isBNA()) {
			if (((BiologicalNodeAbstract) pw).isCoarseNode()) {
				reallyAsk = false;
			}
		}
		removeTab(reallyAsk, removed, pw);
	}

	public void removeTab(int index) {
		boolean ask = true;
		TitledTab removed = (TitledTab) tabbedPanels.get(getSelectedView()).getTabAt(index);
		Pathway pw = con.getPathway(removed.getText());
		if (pw.isBNA()) {
			if (((BiologicalNodeAbstract) pw).isCoarseNode()) {
				ask = false;
			}
		}
		removeTab(ask, removed, pw);
	}

	public void removeAllTabs() {
		int tabCount = tabbedPanels.get(getSelectedView()).getTabCount();
		addedTabs = 0;
		for (int i = 0; i < tabCount; i++) {
			removeTab(0);
		}
		myMenu.disableCloseAndSaveFunctions();
		con.removeAllPathways();
		optionPanel.removeAllElements();
	}

	public void removeTab(boolean ask, TitledTab remove, Pathway pw) {
		if (tabbedPanels.get(getSelectedView()).getTabCount() == 1 && con.getAllPathways().contains(pw)) {
			addedTabs = 0;
			myMenu.disableCloseAndSaveFunctions();
			optionPanel.removeAllElements();
		}
		if (pw.hasGotAtLeastOneElement() && ask) {
			// 0: yes, 1: no, 2: cancel, -1: x
			int n = JOptionPane.showConfirmDialog(frame, "Would you like to save your network-model?", "Save Question",
					JOptionPane.YES_NO_CANCEL_OPTION);
			if (n == 0) {
				if (pw.getFile() != null) {
					try {
						new JSBMLOutput(new FileOutputStream(pw.getFile()), pw);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				} else {
					new SaveDialog(SaveDialog.FORMAT_SBML, SaveDialog.DATA_TYPE_NETWORK_EXPORT);
				}
			}
			if (n == -1 || n == 2) {
				return;
			}
		}
		con.removePathway(remove.getText());
		tabbedPanels.get(getSelectedView()).removeTab(remove);
		Set<Pathway> subPathways = new HashSet<>(con.getAllPathways());
		for (Pathway subPathway : subPathways) {
			if (subPathway.getRootPathway() == pw) {
				removeTab(false, subPathway.getTab().getTitleTab(), subPathway);
			}
		}
	}

	public void setSelectedTab(TitledTab tab) {
		tabbedPanels.get(getSelectedView()).setSelectedTab(tab);
	}

	public Tab getSelectedTab() {
		return tabbedPanels.get(getSelectedView()).getSelectedTab();
	}

	public void renameSelectedTab(String name) {
		tabbedPanels.get(getSelectedView()).getSelectedTab().setName(name);
	}

	public synchronized void showProgressBar(String text) {
		showProgressBar("Please Wait.", text);
	}

	public synchronized void showProgressBar(String title, String text) {
		if (title == null || title.trim().isEmpty()) {
			title = "Please Wait.";
		}
		progressbar = new IndeterminateProgressBar(100, title, text);
		blurUI.setLocked(true);
	}

	public synchronized void closeProgressBar() {
		if (progressbar != null) {
			progressbar.closeWindow();
		}
		blurUI.setLocked(false);
		frame.repaint();
	}

	public synchronized void blurUI() {
		blurUI.setLocked(true);
	}

	public synchronized void unBlurUI() {
		blurUI.setLocked(false);
	}

	public String getCurrentPathway() {
		TitledTab t = (TitledTab) tabbedPanels.get(getSelectedView()).getSelectedTab();
		if (t != null) {
			return t.getText();
		}
		return null;
	}

	public JFrame getFrame() {
		return this.frame;
	}

	public MenuBarClass getMenu() {
		return myMenu;
	}

	public void setFullScreen() {
		if (splitPanel.getDividerLocation() > 0)
			splitPanel.setDividerLocation(0);
		else
			splitPanel.setDividerLocation(splitPanel.getLastDividerLocation());
	}

	public void updateElementTree() {
		optionPanel.updatePanel("GraphTree");
	}

	public void updateSatelliteView() {
		optionPanel.updatePanel("Satellite");
	}

	public void updateSimulationResultView() {
		optionPanel.updatePanel("simulation");
	}

	public void updateDatabaseProperties() {
		optionPanel.updatePanel("Database");
	}

	public void updateElementProperties() {
		optionPanel.updatePanel("element");
	}

	public void updatePathwayTree() {
		optionPanel.updatePanel("pathwayTree");
	}

	public void updateBuildingBlocks() {
		optionPanel.updatePanel("bb");
	}

	public void updateProjectProperties() {
		optionPanel.updatePanel("project");
	}

	public void updateTheoryProperties() {
		optionPanel.updatePanel("theory");
	}

	public void updateOptionPanel() {
		optionPanel.updatePanel("GraphTree");
		optionPanel.updatePanel("Satellite");
		optionPanel.updatePanel("Filter");
		optionPanel.updatePanel("theory");
		optionPanel.updatePanel("alignment");
		optionPanel.updatePanel("simulation");
		optionPanel.updatePanel("pathwayTree");
		optionPanel.updatePanel("bb");
	}

	public void updateAllGuiElements() {
		optionPanel.updatePanel("GraphTree");
		optionPanel.updatePanel("Satellite");
		optionPanel.updatePanel("Filter");
		optionPanel.updatePanel("theory");
		optionPanel.updatePanel("alignment");
		optionPanel.updatePanel("project");
		optionPanel.updatePanel("Database");
		optionPanel.updatePanel("pathwayTree");
		optionPanel.updatePanel("initSimulation");
		// optionPanel.updatePanel("simulation"); seems not necessary
		optionPanel.updatePanel("bb");
		optionPanel.updatePanel("pathwayProperties");
	}

	public void enableOptionPanelUpdate(boolean enable) {
		optionPanel.setUpdatePanels(enable);
	}

	public void redrawGraphs() {
		optionPanel.redrawGraphs();
	}

	public void initSimResGraphs() {
		optionPanel.initSimResGraphs();
	}

	// =============mac osx stuff========================
	@Override
	public void handleAbout(ApplicationEvent event) {
		new AboutWindow();
		event.setHandled(true);
	}

	@Override
	public void handleOpenApplication(ApplicationEvent arg0) {
	}

	@Override
	public void handleOpenFile(ApplicationEvent arg0) {
	}

	@Override
	public void handlePreferences(ApplicationEvent event) {
		new Settings(0);
		event.setHandled(true);
	}

	@Override
	public void handlePrintFile(ApplicationEvent arg0) {
	}

	@Override
	public void handleQuit(ApplicationEvent arg0) {
		frame.dispose();
		System.exit(0);
	}

	@Override
	public void handleReOpenApplication(ApplicationEvent arg0) {
	}

	public void setBeansList(List<Bean> beansList) {
		this.beansList = beansList;
	}

	public String getLoadedYaml() {
		return loadedYaml;
	}

	public void setLoadedYaml(String loadedYaml) {
		this.loadedYaml = loadedYaml;
	}

	public void setSelectedView(View v) {
		for (int key : views.keySet()) {
			if (views.get(key).equals(v)) {
				selectedView = key;
			}
		}
	}

	public void setSelectedView(TabbedPanel t) {
		for (int key : tabbedPanels.keySet()) {
			if (tabbedPanels.get(key).equals(t)) {
				selectedView = key;
			}
		}
	}

	public int getSelectedView() {
		return selectedView;
	}

	public ToolBar getBar() {
		return bar;
	}

	private JComponent root;

	public JComponent getRoot() {
		return root;
	}

	public void setRoot(JComponent root) {
		this.root = root;
	}
}
