package gui;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.List;

import javax.swing.*;

import io.OpenDialog;
import io.SuffixAwareFilter;
import net.infonode.docking.properties.DockingWindowProperties;
import org.apache.commons.io.FilenameUtils;
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
import configurations.SettingsManager;
import configurations.gui.SettingsPanel;
import configurations.gui.VisualizationDialog;
import graph.GraphContainer;
import graph.eventhandlers.GraphTabListener;
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
		// try {
		// SubstanceBusinessBlueSteelLookAndFeel lf = new
		// SubstanceBusinessBlueSteelLookAndFeel();
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

		String title = "VANESA 2.0 - Visualization and Analysis of Networks in Systems Biology Applications";
		if (SettingsManager.getInstance().isDeveloperMode()) {
			title += " (developer mode)";
		}

		frame.setTitle(title);
		frame.setVisible(false);

		// MacOSX-Look and Feel with http://simplericity.org/macify/
		final Application application = new DefaultApplication();
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
		// InfoNodeLookAndFeelTheme theme =
		// InfoNodeLookAndFeelThemes.getSoftGrayTheme();
		// UIManager.setLookAndFeel(new InfoNodeLookAndFeel(theme));
		// } catch (UnsupportedLookAndFeelException e1) {
		// e1.printStackTrace();
		// }

		final ScreenSize screenSize = new ScreenSize();
		int windowWidth = screenSize.width - 70;
		int windowHeight = screenSize.height - 100;
		if (screenSize.width > 1024) {
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
		bar = new ToolBar();
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
		final ComponentAdapter optionPanelResizeListener = new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				final int lastFullWidth = optionPanel.getLastFullWidth();
				final int fullWidth = optionPanel.getFullWidth();
				if (lastFullWidth != -1) {
					if (lastFullWidth == splitPanel.getDividerLocation()) {
						splitPanel.setDividerLocation(fullWidth);
					}
				}
				limitSplitDivider();
			}
		};
		optionPanel.getContentPanel().addComponentListener(optionPanelResizeListener);
		optionPanel.getPanel().addComponentListener(optionPanelResizeListener);
		optionPanel.getPanel().getVerticalScrollBar().addComponentListener(new ComponentAdapter() {
			@Override
			public void componentHidden(ComponentEvent e) {
				if (!optionPanel.getPanel().getVerticalScrollBar().isShowing()) {
					limitSplitDivider();
				}
			}
		});
		splitPanel.add(optionPanel.getPanel());
		addView();
		splitPanel.setOneTouchExpandable(false);
		splitPanel.addPropertyChangeListener(evt -> {
			if (evt.getPropertyName().equals(JSplitPane.LAST_DIVIDER_LOCATION_PROPERTY)
					|| evt.getPropertyName().equals(JSplitPane.DIVIDER_LOCATION_PROPERTY)) {
				limitSplitDivider();
			}
		});
		root.add(splitPanel, BorderLayout.CENTER);
		JXLayer<JComponent> layer = new JXLayer<>(root);
		layer.setUI(blurUI);
		frame.setContentPane(layer);
		frame.setVisible(true);
		initializeDragDrop();
		try {
			askForYaml();
		} catch (FileNotFoundException e1) {
			System.out.println("askForYaml Method Error");
			e1.printStackTrace();
		}
	}

	private void limitSplitDivider() {
		final int dividerMaxLocation = optionPanel.getFullWidth();
		if (splitPanel.getDividerLocation() > dividerMaxLocation) {
			splitPanel.setDividerLocation(dividerMaxLocation);
		}
	}

	private void initializeDragDrop() {
		new DropTarget(frame, new DropTargetAdapter() {
			@SuppressWarnings("unchecked")
			public void drop(final DropTargetDropEvent event) {
				event.acceptDrop(DnDConstants.ACTION_COPY);
				final List<File> droppedFiles;
				try {
					droppedFiles = (List<File>) event.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
				} catch (UnsupportedFlavorException | IOException ignored) {
					return;
				}
				for (final File file : droppedFiles) {
					final String extension = FilenameUtils.getExtension(file.getAbsolutePath())
							.toLowerCase(Locale.ROOT);
					switch (extension) {
					case "graphml":
						OpenDialog.openUIBlocking(SuffixAwareFilter.GRAPH_ML, file);
						break;
					case "vaml":
						OpenDialog.openUIBlocking(SuffixAwareFilter.VAML, file);
						break;
					case "sbml":
						OpenDialog.openUIBlocking(SuffixAwareFilter.SBML, file);
						break;
					case "txt":
						OpenDialog.openUIBlocking(SuffixAwareFilter.GRAPH_TEXT_FILE, file);
						break;
					case "kgml":
						OpenDialog.openUIBlocking(SuffixAwareFilter.KGML, file);
						break;
					case "csv":
						OpenDialog.openUIBlocking(SuffixAwareFilter.VANESA_SIM_RESULT, file);
						break;
					}
				}
			}
		});
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
					bna.setDefaultShape(VertexShapes.getEllipse());
					break;
				case "rectangle":
					bna.setDefaultShape(VertexShapes.getRectangle());
					break;
				case "rounded rectangle":
					bna.setDefaultShape(VertexShapes.getRoundRectangle());
					break;
				case "triangle":
					bna.setDefaultShape(VertexShapes.getRegularPolygon(3));
					break;
				case "pentagon":
					bna.setDefaultShape(VertexShapes.getRegularPolygon(5));
					break;
				case "hexagon":
					bna.setDefaultShape(VertexShapes.getRegularPolygon(6));
					break;
				case "octagon":
					bna.setDefaultShape(VertexShapes.getRegularPolygon(8));
					break;
				case "5 star":
					bna.setDefaultShape(VertexShapes.getRegularStar(5));
					break;
				case "6 star":
					bna.setDefaultShape(VertexShapes.getRegularStar(6));
					break;
				case "7 star":
					bna.setDefaultShape(VertexShapes.getRegularStar(7));
					break;
				case "8 star":
					bna.setDefaultShape(VertexShapes.getRegularStar(8));
					break;
				default:
					System.out.println(bna.getName() + ": No shape defined! Default shape used!");
					bna.setDefaultShape(VertexShapes.getEllipse());
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
		final DockingWindowProperties properties = views.get(viewID).getWindowProperties();
		properties.setCloseEnabled(true);
		properties.setUndockEnabled(false);
		properties.setDragEnabled(true);
		properties.setMaximizeEnabled(false);
		properties.setMinimizeEnabled(false);
		PanelListener lis = new PanelListener();
		views.get(viewID).addListener(lis);
	}

	public void addView() {
		final int previousDividerLocation = splitPanel.getDividerLocation();
		// remove rootWindow if exists
		if (rootWindow != null) {
			splitPanel.remove(rootWindow);
		}
		// create new tabbedPanel
		final TabbedPanel tp = new TabbedPanel();
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
		views.put(id, view);
		setWindowProperties(id);
		viewMap.addView(id, view);
		rootWindow = DockingUtil.createRootWindow(viewMap, true);
		rootWindow.getWindowBar(Direction.DOWN).setEnabled(true);
		splitPanel.add(rootWindow);
		view.makeVisible();
		setSelectedView(view);
		splitPanel.setDividerLocation(previousDividerLocation);
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

		limitSplitDivider();
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
		boolean isLastTabOfView = tabbedPanels.get(getSelectedView()).getTabCount() == 1
				&& con.getAllPathways().contains(pw);
		if (isLastTabOfView) {
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
					new SaveDialog(new SuffixAwareFilter[] { SuffixAwareFilter.SBML },
							SaveDialog.DATA_TYPE_NETWORK_EXPORT);
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
		if (isLastTabOfView) {
			bar.updateVisibility();
		}
		limitSplitDivider();
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
		return frame;
	}

	public MenuBarClass getMenu() {
		return myMenu;
	}

	public void setCursor(int cursor) {
		frame.setCursor(new Cursor(cursor));
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

	public void redrawGraphs(boolean fireSerieState) {
		optionPanel.redrawGraphs(fireSerieState);
	}

	public void initSimResGraphs() {
		optionPanel.initSimResGraphs();
	}

	public void redrawTokens() {
		optionPanel.redrawTokens();
	}

	public void addSimulationResults() {
		optionPanel.addSimulationResults();
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
		new SettingsPanel(0);
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
		bar.updateVisibility();
	}

	public void setSelectedView(TabbedPanel t) {
		for (int key : tabbedPanels.keySet()) {
			if (tabbedPanels.get(key).equals(t)) {
				selectedView = key;
			}
		}
		bar.updateVisibility();
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
