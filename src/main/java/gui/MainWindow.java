package gui;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
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
import net.miginfocom.swing.MigLayout;
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
import gui.visualization.VisualizationConfigBeans.Bean;
import gui.visualization.YamlToObjectParser;
import io.SaveDialog;
import io.sbml.JSBMLOutput;
import net.infonode.tabbedpanel.Tab;
import net.infonode.tabbedpanel.TabbedPanel;
import net.infonode.tabbedpanel.TitledTab;

public class MainWindow implements ApplicationListener {
	private final JFrame frame = new JFrame();
	private final TabbedPanel rootWindow;
	private final ToolBar bar;
	private List<Bean> beansList = new ArrayList<>();
	private String loadedYaml = null;

	private final LockableUI blurUI = new LockableUI(new BufferedImageOpEffect(new BlurFilter()));

	private final GraphContainer con = GraphContainer.getInstance();
	private int addedTabs = 0;
	private final MenuBarClass myMenu;

	private final OptionPanel optionPanel;

	private static IndeterminateProgressBar progressbar = null;

	private static MainWindow instance;

	private MainWindow() {
		JFrame.setDefaultLookAndFeelDecorated(true);
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
		root = (JComponent) frame.getContentPane();
		root.setLayout(new MigLayout("ins 0", "[][grow]", "[][][grow]"));
		final JSeparator topSeparator = new JSeparator();
		topSeparator.setBackground(Color.BLACK);
		root.add(topSeparator, "height 2:2:2, span 2, growx, wrap");
		root.add(bar.getToolBar(), "span 2, growx, wrap");
		optionPanel = new OptionPanel();
		root.add(optionPanel.getPanel(), "width 417:417:417, growy");
		rootWindow = new TabbedPanel();
		rootWindow.addTabListener(new GraphTabListener(this));
		root.add(rootWindow, "grow");
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

	public void addTab(final TitledTab tab) {
		addedTabs++;
		rootWindow.addTab(tab);
		setSelectedTab(tab);
		myMenu.enableCloseAndSaveFunctions();
	}

	public int getTabCount() {
		return addedTabs;
	}

	public void removeTab(boolean ask) {
		boolean reallyAsk = ask;
		TitledTab removed = (TitledTab) rootWindow.getSelectedTab();
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
		TitledTab removed = (TitledTab) rootWindow.getTabAt(index);
		Pathway pw = con.getPathway(removed.getText());
		if (pw.isBNA()) {
			if (((BiologicalNodeAbstract) pw).isCoarseNode()) {
				ask = false;
			}
		}
		removeTab(ask, removed, pw);
	}

	public void removeAllTabs() {
		int tabCount = rootWindow.getTabCount();
		addedTabs = 0;
		for (int i = 0; i < tabCount; i++) {
			removeTab(0);
		}
		myMenu.disableCloseAndSaveFunctions();
		con.removeAllPathways();
		optionPanel.removeAllElements();
	}

	public void removeTab(boolean ask, TitledTab remove, Pathway pw) {
		boolean isLastTabOfView = rootWindow.getTabCount() == 1
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
		rootWindow.removeTab(remove);
		Set<Pathway> subPathways = new HashSet<>(con.getAllPathways());
		for (Pathway subPathway : subPathways) {
			if (subPathway.getRootPathway() == pw) {
				removeTab(false, subPathway.getTab(), subPathway);
			}
		}
		if (isLastTabOfView) {
			bar.updateVisibility();
		}
	}

	public void setSelectedTab(final TitledTab tab) {
		rootWindow.setSelectedTab(tab);
		bar.updateVisibility();
	}

	public Tab getSelectedTab() {
		return rootWindow.getSelectedTab();
	}

	public void renameSelectedTab(String name) {
		rootWindow.getSelectedTab().setName(name);
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
		final TitledTab t = (TitledTab) rootWindow.getSelectedTab();
		return t != null ? t.getText() : null;
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
