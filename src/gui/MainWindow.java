package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.WindowConstants;

import org.apache.commons.lang3.SystemUtils;
import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.jxlayer.plaf.effect.BufferedImageOpEffect;
import org.jdesktop.jxlayer.plaf.ext.LockableUI;
import org.simplericity.macify.eawt.Application;
import org.simplericity.macify.eawt.ApplicationEvent;
import org.simplericity.macify.eawt.ApplicationListener;
import org.simplericity.macify.eawt.DefaultApplication;

import com.jhlabs.image.BlurFilter;

import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import configurations.ResourceLibrary;
import configurations.gui.Settings;
import configurations.gui.VisualizationDialog;
import graph.GraphContainer;
import graph.eventhandlers.GraphTabListener;
import graph.eventhandlers.GraphWindowListener;
import graph.jung.graphDrawing.VertexShapes;
import gui.algorithms.CenterWindow;
import gui.algorithms.ScreenSize;
import gui.eventhandlers.PanelListener;
import gui.images.ImagePath;
import gui.visualization.VisualizationConfigBeans.Bean;
import gui.visualization.YamlToObjectParser;
import io.SaveDialog;
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
import xmlOutput.sbml.JSBMLoutput;

public class MainWindow implements ApplicationListener {

	private JFrame frame = new JFrame();
	private HashMap<Integer, View> views = new HashMap<Integer, View>();
	private ViewMap viewMap = new ViewMap();
	private RootWindow rootWindow;
	private ToolBar bar;
	private int maxPanelID = -1;
	private int selectedView = 0;
	private YamlToObjectParser yamlToObject;
	private List<Bean> beansList = new ArrayList<Bean>();
	private String loadedYaml = null;
	private VertexShapes vs = new VertexShapes();

	// global constants
	public static boolean developer;
	public static String pathWorkingDirectory;

	private JXLayer<JComponent> layer;
	private LockableUI blurUI = new LockableUI(new BufferedImageOpEffect(new BlurFilter()));
	private JSplitPane split_pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

	// private TabbedPanel tabbedPanel;
	private HashMap<Integer, TabbedPanel> tabbedPanels = new HashMap<Integer, TabbedPanel>();
	// private TabbedPanel tabbedPanelProperties;
	private final GraphContainer con = GraphContainer.getInstance();
	private int addedtabs = 0;
	private final MenuBarClass myMenu;

	private OptionPanel optionPanel;

	// private boolean fullScreen = false;

	private Application macOsxHandler;

	private static ProgressBar progressbar = null;

	private static MainWindow instance;

	private MainWindow() {
		JFrame.setDefaultLookAndFeelDecorated(true);

		// Set developer status
		developer = Boolean.parseBoolean(ResourceLibrary.getSettingsResource("settings.default.developer"));
		// System.out.println("main: "+developer);
		// try {
		//
		// //SubstanceBusinessBlueSteelLookAndFeel lf = new
		// SubstanceBusinessBlueSteelLookAndFeel();
		// //lf.setSkin("");
		// UIManager.setLookAndFeel(new
		// SubstanceBusinessBlueSteelLookAndFeel());
		// } catch (Exception e) {
		// System.out.println("fail");
		// }
		// SwingUtilities.invokeLater(new Runnable()
		// {
		//
		// public void run() {
		// SwingUtilities.updateComponentTreeUI();
		// }
		//
		// });
		// SwingUtilities.updateComponentTreeUI(this);

		frame.setTitle("VANESA 2.0 - Visualization and Analysis of Networks in Systems Biology Applications");
		frame.setVisible(false);

		// MacOSX-Look and Feel with http://simplericity.org/macify/
		Application application = new DefaultApplication();
		this.setMacOsxHandler(application);
		application.addPreferencesMenuItem();
		application.setEnabledPreferencesMenu(true);

		// create menu bar
		this.myMenu = new MenuBarClass(application);

		// load dock icon on mac osx
		ImagePath imagePath = ImagePath.getInstance();
		ImageIcon ii = new ImageIcon(imagePath.getPath("graph2.png"));
		BufferedImage b = new BufferedImage(ii.getIconWidth(), ii.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		ii.paintIcon(frame, b.createGraphics(), 0, 0);
		application.setApplicationIconImage(b);

		// react to special application menu items on mac osx
		application.addApplicationListener(this);

		List<Image> iconList = new ArrayList<>();
		Toolkit kit = Toolkit.getDefaultToolkit();
		
		iconList.add(kit.createImage(imagePath.getPath("logo16.png")));
		iconList.add(kit.createImage(imagePath.getPath("logo32.png")));
		iconList.add(kit.createImage(imagePath.getPath("logo64.png")));
		iconList.add(kit.createImage(imagePath.getPath("logo128.png")));
		frame.setIconImages(iconList);
		
		//
		// try {
		// InfoNodeLookAndFeelTheme theme =
		// InfoNodeLookAndFeelThemes.getSoftGrayTheme();
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

		split_pane.add(optionPanel.getPanel());
		addView();
		split_pane.setOneTouchExpandable(false);
		split_pane.addPropertyChangeListener(new PropertyChangeListener() {
			// private final int SP_DIVIDER_MAX_LOCATION = split_pane
			// .getLeftComponent().getPreferredSize().width;
			private final int SP_DIVIDER_MAX_LOCATION = split_pane.getLeftComponent().getMaximumSize().width;

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				final String prop_name = evt.getPropertyName();
				if (prop_name.equals(JSplitPane.LAST_DIVIDER_LOCATION_PROPERTY) || prop_name.equals(JSplitPane.DIVIDER_LOCATION_PROPERTY)) {
					if (split_pane.getDividerLocation() > SP_DIVIDER_MAX_LOCATION)
						split_pane.setDividerLocation(SP_DIVIDER_MAX_LOCATION);
				}
			}
		});

		root.add(split_pane, BorderLayout.CENTER);

		layer = new JXLayer<JComponent>(root);
		layer.setUI(blurUI);
		frame.setContentPane(layer);

		frame.setVisible(true);

		try {
			askForYaml();
		} catch (FileNotFoundException e1) {
			System.out.println("askForYaml Method Error");
			e1.printStackTrace();
		}

		Map<String, String> env = System.getenv();
		if (SystemUtils.IS_OS_WINDOWS) {
			pathWorkingDirectory = env.get("APPDATA");
		} else {
			pathWorkingDirectory = env.get("HOME");
		}

		if (pathWorkingDirectory.charAt(pathWorkingDirectory.length() - 1) != File.separatorChar) {
			pathWorkingDirectory += File.separator;
		}

		pathWorkingDirectory += "vanesa" + File.separator;

		File dir = new File(pathWorkingDirectory);

		if (!dir.isDirectory()) {
			dir.mkdir();
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
		if (doResetAppearance == true) {
			bna.resetAppearance();
		}
	}

	public void askForYaml() throws FileNotFoundException {
		String yamlSourceFile = new File("YamlSourceFile.txt").getAbsolutePath();
		File file = new File(yamlSourceFile);
		BufferedReader buffReader = null;
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
			if (yamlFile.exists()) {
				yamlToObject = new YamlToObjectParser(loadedYaml);
				beansList = yamlToObject.startConfig();
			} else {
				loadedYaml = VisualizationDialog.DEFAULTYAML;
				yamlToObject = new YamlToObjectParser(loadedYaml);
				beansList = yamlToObject.startConfig();
			}
		} else {
			loadedYaml = VisualizationDialog.DEFAULTYAML;
			yamlToObject = new YamlToObjectParser(loadedYaml);
			beansList = yamlToObject.startConfig();
		}
	}

	/*
	 * private DockingWindow setWindowLayout() {
	 * 
	 * DockingWindow myLayout = views[0]; // DockingWindow myLayout = new
	 * SplitWindow(true, views[1], views[0]); return myLayout; }
	 */

	private void setWindowProperties(int viewID) {

		views.get(viewID).getWindowProperties().setCloseEnabled(true);
		views.get(viewID).getWindowProperties().setUndockEnabled(false);
		views.get(viewID).getWindowProperties().setDragEnabled(true);
		views.get(viewID).getWindowProperties().setMaximizeEnabled(false);
		views.get(viewID).getWindowProperties().setMinimizeEnabled(false);

		PanelListener lis = new PanelListener();
		views.get(viewID).addListener(lis);
	}

	// private RootWindow getRootWindow() {
	//
	// TabbedPanel tp = new TabbedPanel();
	// tp.getProperties().setTabAreaOrientation(Direction.DOWN);
	// tp.getProperties().setEnsureSelectedTabVisible(true);
	// tp.getProperties().setHighlightPressedTab(true);
	// tp.addTabListener(new GraphTabListener(this));
	// tp.getProperties().setTabReorderEnabled(true);
	// tp.getProperties().setTabDropDownListVisiblePolicy(
	// TabDropDownListVisiblePolicy.MORE_THAN_ONE_TAB);
	// tp.setBackground(Color.BLACK);
	// View view = new View("Network Modelling", null, tp);
	// view.addListener(new GraphWindowListener());
	// views.put(viewMap.getViewCount(), view);
	//
	// viewMap.addView(0, view);
	//
	// setWindowProperties(0);
	//
	// rootWindow = DockingUtil.createRootWindow(viewMap, true);
	// rootWindow.getWindowBar(Direction.DOWN).setEnabled(true);
	//
	// return rootWindow;
	//
	// }

	/**
	 * Adds a view (panel) to the existing window.
	 * 
	 * @author tloka
	 */
	public void addView() {

		// remove rootWindow if exists
		if (rootWindow != null) {
			split_pane.remove(rootWindow);
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
		//System.out.println("put:"+id);
		tabbedPanels.put(id, tp);

		View view = new View("Network Modelling", null, tp);
		view.addListener(new GraphWindowListener());
		views.put(id, view);

		// set the window properties (close, undock, ...)
		setWindowProperties(id);

		viewMap.addView(id, view);

		rootWindow = DockingUtil.createRootWindow(viewMap, true);
		rootWindow.getWindowBar(Direction.DOWN).setEnabled(true);

		split_pane.add(rootWindow);
		view.makeVisible();
		setSelectedView(view);
	}

	/**
	 * Removes a view (panel) from the existing window.
	 * 
	 * @author tloka
	 */
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
				//System.out.println("set:"+views.get(views.keySet().iterator().next()));
				setSelectedView(views.get(views.keySet().iterator().next()));
			}
		}

		// rootWindow = DockingUtil.createRootWindow(viewMap, true);
		//
		// split_pane.add(rootWindow);
	}

	public void addTab(TitledTab tab) {
		addedtabs++;
		//System.out.println(getSelectedView());
		tabbedPanels.get(getSelectedView()).addTab(tab);
		setSelectedTab(tab);
		myMenu.enableCloseAndSaveFunctions();
	}

	public int getTabCount() {
		return addedtabs;
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
		int i;
		addedtabs = 0;
		for (i = 0; i < tabCount; i++) {
			removeTab(0);
		}
		myMenu.disableCloseAndSaveFunctions();
		con.removeAllPathways();
		optionPanel.removeAllElements();
	}

	public void removeTab(boolean ask, TitledTab remove, Pathway pw) {
		if (tabbedPanels.get(getSelectedView()).getTabCount() == 1 && con.getAllPathways().contains(pw)) {
			addedtabs = 0;
			myMenu.disableCloseAndSaveFunctions();
			optionPanel.removeAllElements();
		}

		if (pw.hasGotAtLeastOneElement() && ask) {

			// 0: yes, 1: no, 2: cancel, -1: x
			int n = JOptionPane.showConfirmDialog(frame, "Would you like to save your network-model?", "Save Question",
					JOptionPane.YES_NO_CANCEL_OPTION);
			// System.out.println(n);
			if (n == 0) {
				if (pw.getFile() != null) {
					try {
						new JSBMLoutput(new FileOutputStream(pw.getFile()), pw);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				} else {
					new SaveDialog(SaveDialog.FORMAT_SBML);
				}
			}
			if (n == -1 || n == 2) {
				return;
			}
		}
		con.removePathway(remove.getText());
		tabbedPanels.get(getSelectedView()).removeTab(remove);
		Set<Pathway> subPathways = new HashSet<Pathway>();
		subPathways.addAll(con.getAllPathways());
		for (Pathway subPathway : subPathways) {
			if (subPathway.getRootPathway() == pw) {
				removeTab(false, subPathway.getTab().getTitelTab(), subPathway);
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
		showProgressBar( "Please Wait.", text);
	}
	
	public synchronized void showProgressBar(String title, String text) {
		progressbar = new ProgressBar();
		if(title == null || title.trim().isEmpty()){
			title = "Please Wait.";
		}
		progressbar.init(100, title, true);
		progressbar.setProgressBarString(text);
		blurUI.setLocked(true);
	}

	public synchronized void closeProgressBar() {
		if (progressbar != null) {
			progressbar.closeWindow();
		}
		blurUI.setLocked(false);
		frame.repaint();
	}

	public synchronized void blurrUI() {
		blurUI.setLocked(true);
	}

	public synchronized void unBlurrUI() {
		blurUI.setLocked(false);
	}

	public String getCurrentPathway() {
		TitledTab t = (TitledTab) tabbedPanels.get(getSelectedView()).getSelectedTab();
		if (t != null) {
			return t.getText();
		} else {
			return null;
		}
	}

	public JFrame getFrame() {
		return this.frame;
	}
	
	public MenuBarClass getmyMenu() {
		return myMenu;
	}

	public void setFullScreen() {
		// if (fullScreen)
		// {
		//
		// optionPanel.getPanel().setVisible(true);
		// getContentPane().add(optionPanel.getPanel(), BorderLayout.WEST);
		// fullScreen=false;
		//
		// }
		// else
		// {
		//
		// optionPanel.getPanel().setVisible(false);
		// fullScreen=true;
		// }
		//
		// this.rootPane.revalidate();

		if (split_pane.getDividerLocation() > 0)
			split_pane.setDividerLocation(0);
		else
			split_pane.setDividerLocation(split_pane.getLastDividerLocation());
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
		// System.out.println(" udate option");
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
		// System.out.println("update all");
		optionPanel.updatePanel("GraphTree");
		optionPanel.updatePanel("Satellite");
		optionPanel.updatePanel("Filter");
		optionPanel.updatePanel("theory");
		optionPanel.updatePanel("alignment");
		optionPanel.updatePanel("project");
		optionPanel.updatePanel("Database");
		optionPanel.updatePanel("pathwayTree");
		optionPanel.updatePanel("initSimulation");
		//optionPanel.updatePanel("simulation"); seems not necessary
		optionPanel.updatePanel("bb");
		optionPanel.updatePanel("pathwayProperties");
	}

	public void updateDAWISVertexWindow() {
		optionPanel.updatePanel("DAWISVertexWindow");
	}

	public void enableDatabaseWindow(boolean enabled) {

		optionPanel.enableDatabaseWindow(enabled);
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
		// new Settings().setSelection(5);
		new Settings(1);
		event.setHandled(true);
	}

	@Override
	public void handlePrintFile(ApplicationEvent arg0) {

	}

	@Override
	public void handleQuit(ApplicationEvent arg0) {
		// close application
		frame.dispose();
		System.exit(0);
	}

	@Override
	public void handleReopenApplication(ApplicationEvent arg0) {
	}

	/**
	 * @param macOsxHandler
	 *            the macOsxHandler to set
	 */
	public void setMacOsxHandler(Application macOsxHandler) {
		this.macOsxHandler = macOsxHandler;
	}

	/**
	 * @return the macOsxHandler
	 */
	public Application getMacOsxHandler() {
		return macOsxHandler;
	}

	public List<Bean> getBeansList() {
		return beansList;
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
