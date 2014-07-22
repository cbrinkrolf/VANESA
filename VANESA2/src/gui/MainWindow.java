package gui;

import graph.ContainerSingelton;
import graph.GraphContainer;
import graph.eventhandlers.GraphTabListener;
import graph.eventhandlers.GraphWindowListener;
import gui.algorithms.CenterWindow;
import gui.algorithms.ScreenSize;
import gui.eventhandlers.PanelListener;
import gui.images.ImagePath;
import io.SaveDialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.WindowConstants;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.DockingWindowListener;
import net.infonode.docking.OperationAbortedException;
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

import xmlOutput.sbml.VAMLoutput;
import biologicalElements.Pathway;

import com.jhlabs.image.BlurFilter;

import configurations.gui.Settings;

public class MainWindow extends JFrame implements ApplicationListener {
	private static final long serialVersionUID = -8328247684408223577L;

	private HashMap<Integer, View> views = new HashMap<Integer, View>();
	private ViewMap viewMap = new ViewMap();
	private RootWindow rootWindow;
	private ToolBar bar = new ToolBar(false);
	private int maxPanelID = -1;
	private int selectedView = 0;
	
	public void setSelectedView(View v){
		for(int key : views.keySet()){
			if(views.get(key).equals(v)){
				selectedView = key;
			}
		}
	}
	
	public void setSelectedView(TabbedPanel t){
		for(int key : tabbedPanels.keySet()){
			if(tabbedPanels.get(key).equals(t)){
				selectedView = key;
			}
		}
	}
	
	public int getSelectedView(){
		return selectedView;
	}
	
	//MARTIN static Datenbank switcher
	public static boolean useOldDB = false;

	public ToolBar getBar() {
		return bar;
	}

	public void setBar(ToolBar bar) {
		this.bar = bar;
	}

	private JComponent root;

	public JComponent getRoot() {
		return root;
	}

	public void setRoot(JComponent root) {
		this.root = root;
	}

	private JXLayer<JComponent> layer;
	private LockableUI blurUI = new LockableUI(new BufferedImageOpEffect(
			new BlurFilter()));
	private JSplitPane split_pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

	//private TabbedPanel tabbedPanel;
	private HashMap<Integer, TabbedPanel> tabbedPanels = new HashMap<Integer, TabbedPanel>();
	// private TabbedPanel tabbedPanelProperties;
	private final GraphContainer con = ContainerSingelton.getInstance();
	private int addedtabs = 0;
	private final MenuBarClass myMenu;

	public MenuBarClass getmyMenu() {
		return myMenu;
	}

	private OptionPanel optionPanel;

	// private boolean fullScreen = false;

	private Application macOsxHandler;

	public MainWindow() {
		JFrame.setDefaultLookAndFeelDecorated(true);

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
		// // TODO Auto-generated method stub
		// SwingUtilities.updateComponentTreeUI();
		// }
		//
		// });
		// SwingUtilities.updateComponentTreeUI(this);

		setTitle("VANESA 2.0 - Visualization and Analysis of Networks in Systems Biology Applications");
		setVisible(false);

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
		BufferedImage b = new BufferedImage(ii.getIconWidth(),
				ii.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		ii.paintIcon(this, b.createGraphics(), 0, 0);
		application.setApplicationIconImage(b);

		// react to special application menu items on mac osx
		application.addApplicationListener(this);

		//
		// try {
		// InfoNodeLookAndFeelTheme theme =
		// InfoNodeLookAndFeelThemes.getSoftGrayTheme();
		// UIManager.setLookAndFeel(new InfoNodeLookAndFeel(theme));

		// } catch (UnsupportedLookAndFeelException e1) {
		// TODO Auto-generated catch block
		// e1.printStackTrace();
		// }

		ScreenSize screenSize = new ScreenSize();
		int windowWidth = (int) screenSize.getwidth() - 70;
		int windowHeight = (int) screenSize.getheight() - 100;

		if (screenSize.getwidth() > 1024) {
			windowWidth = 1024;
			windowHeight = windowWidth * 2 / 3;
		}

		setEnabled(false);
		setSize(windowWidth, windowHeight);
		new CenterWindow(this).centerWindow(windowWidth, windowHeight);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}

		});

		// create menu
		setJMenuBar(myMenu.returnMenu());
		// toolbar with the buttons on the right
		root = (JComponent) getContentPane();
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
		split_pane.setOneTouchExpandable(true);
		split_pane.addPropertyChangeListener(new PropertyChangeListener() {
//			private final int SP_DIVIDER_MAX_LOCATION = split_pane
//					.getLeftComponent().getPreferredSize().width;
			private final int SP_DIVIDER_MAX_LOCATION = split_pane.getLeftComponent().getMaximumSize().width;
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				final String prop_name = evt.getPropertyName();

				if (prop_name.equals(JSplitPane.LAST_DIVIDER_LOCATION_PROPERTY)
						|| prop_name
								.equals(JSplitPane.DIVIDER_LOCATION_PROPERTY)) {
					if (split_pane.getDividerLocation() > SP_DIVIDER_MAX_LOCATION)
						split_pane.setDividerLocation(SP_DIVIDER_MAX_LOCATION);
				}
			}
		});

		root.add(split_pane, BorderLayout.CENTER);

		layer = new JXLayer<JComponent>(root);
		layer.setUI(blurUI);
		this.setContentPane(layer);

		setVisible(true);
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

//	private RootWindow getRootWindow() {
//
//		TabbedPanel tp = new TabbedPanel();
//		tp.getProperties().setTabAreaOrientation(Direction.DOWN);
//		tp.getProperties().setEnsureSelectedTabVisible(true);
//		tp.getProperties().setHighlightPressedTab(true);
//		tp.addTabListener(new GraphTabListener(this));
//		tp.getProperties().setTabReorderEnabled(true);
//		tp.getProperties().setTabDropDownListVisiblePolicy(
//				TabDropDownListVisiblePolicy.MORE_THAN_ONE_TAB);
//		tp.setBackground(Color.BLACK);
//		View view = new View("Network Modelling", null, tp);
//		view.addListener(new GraphWindowListener());
//		views.put(viewMap.getViewCount(), view);
//
//		viewMap.addView(0, view);
//
//		setWindowProperties(0);
//
//		rootWindow = DockingUtil.createRootWindow(viewMap, true);
//		rootWindow.getWindowBar(Direction.DOWN).setEnabled(true);
//
//		return rootWindow;
//
//	}
	
	
	/**
	 * Adds a view (panel) to the existing window.
	 * @author tloka
	 */
	public void addView(){
		
		//remove rootWindow if exists
		if(rootWindow!=null){
			split_pane.remove(rootWindow);
		}
		
		//create new tabbedPanel
		TabbedPanel tp = new TabbedPanel();
		tp.getProperties().setTabAreaOrientation(Direction.DOWN);
		tp.getProperties().setEnsureSelectedTabVisible(true);
		tp.getProperties().setHighlightPressedTab(true);
		tp.addTabListener(new GraphTabListener(this));
		tp.getProperties().setTabReorderEnabled(true);
		tp.getProperties().setTabDropDownListVisiblePolicy(
				TabDropDownListVisiblePolicy.MORE_THAN_ONE_TAB);
		tp.setBackground(Color.BLACK);
		
		maxPanelID += 1;
		int id = maxPanelID;
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
	}
	
	/**
	 * Removes a view (panel) from the existing window.
	 * @author tloka
	 */
	public void removeView(DockingWindow dw){
		
		//remove rootWindow if exists
//		if(rootWindow!=null){
//			split_pane.remove(rootWindow);
//		}
		
		if(dw instanceof View){
			View view = (View) dw;
			int id = -1;
			for(int key : views.keySet()){
				if(views.get(key).equals(view)){
					id = key;
					break;
				}
			}
			views.remove(id);
			tabbedPanels.remove(id);
			viewMap.removeView(id);
			if(views.keySet().iterator().hasNext()){
				setSelectedView(views.get(views.keySet().iterator().next()));
			}
		}
		

//		rootWindow = DockingUtil.createRootWindow(viewMap, true);
//		
//		split_pane.add(rootWindow);
	}

	public void addTab(TitledTab tab) {
		addedtabs++;
		tabbedPanels.get(getSelectedView()).addTab(tab);
		setSelectedTab(tab);
		myMenu.enableCloseAndSaveFunctions();
	}

	public int getTabCount() {
		return addedtabs;
	}

	public void removeTab(boolean ask) {

		if (tabbedPanels.get(getSelectedView()).getTabCount() == 1) {
			addedtabs = 0;
			myMenu.disableCloseAndSaveFunctions();
			optionPanel.removeAllElements();
		}

		TitledTab removed = (TitledTab) tabbedPanels.get(getSelectedView()).getSelectedTab();

		Pathway pw = con.getPathway(removed.getText());

		if (pw.hasGotAtLeastOneElement() && ask) {

			int n = JOptionPane.showConfirmDialog(null,
					"Would you like to save your network-model?",
					"Save Question", JOptionPane.YES_NO_CANCEL_OPTION);
			if (n == 0) {
				if (pw.getFilename() != null) {
					try {
						new VAMLoutput(pw.getFilename(), pw);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} else {
					new SaveDialog(16);
				}
			}
			if (n == 0 || n ==1) {
				con.removePathway(removed.getText());
				tabbedPanels.get(getSelectedView()).removeTab(removed);
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

	public void removeTab(int index) {

		if (tabbedPanels.get(getSelectedView()).getTabCount() == 1) {
			addedtabs = 0;
			myMenu.disableCloseAndSaveFunctions();
			optionPanel.removeAllElements();
		}
		TitledTab removed = (TitledTab) tabbedPanels.get(getSelectedView()).getTabAt(index);
		Pathway pw = con.getPathway(removed.getText());

		if (pw.hasGotAtLeastOneElement()) {

			// 0: yes, 1: no, 2: cancel, -1: x
			int n = JOptionPane.showConfirmDialog(null,
					"Would you like to save your network-model?",
					"Save Question", JOptionPane.YES_NO_CANCEL_OPTION);
			//System.out.println(n);
			if (n == 0) {
				if (pw.getFilename() != null) {
					try {
						new VAMLoutput(pw.getFilename(), pw);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} else {
					new SaveDialog(16);
				}
			}
			if (n == -1 || n == 2) {
				return;
			}
		}
		con.removePathway(removed.getText());
		tabbedPanels.get(getSelectedView()).removeTab(removed);

	}

	public void removeAllTabs() {

		int tabCount = tabbedPanels.get(getSelectedView()).getTabCount();
		int i;
		addedtabs = 0;
		for (i = 0; i < tabCount; i++) {

			TitledTab removed = (TitledTab) tabbedPanels.get(getSelectedView()).getTabAt(0);
			Pathway pw = con.getPathway(removed.getText());

			if (pw.hasGotAtLeastOneElement()) {

				int n = JOptionPane.showConfirmDialog(null,
						"Would you like to save your network-model?",
						"Save Question", JOptionPane.YES_NO_CANCEL_OPTION);
				
				
				if (n == 0) {
					if (pw.getFilename() != null) {
						try {
							new VAMLoutput(pw.getFilename(), pw);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					} else {
						new SaveDialog(16);
					}
				}
				if (n == 0 || n ==1) {
					tabbedPanels.get(getSelectedView()).removeTab(tabbedPanels.get(getSelectedView()).getTabAt(0));
				}
				
			}

			
		}
		myMenu.disableCloseAndSaveFunctions();
		con.removeAllPathways();
		optionPanel.removeAllElements();
	}

	public void setLockedPane(boolean lock) {
		blurUI.setLocked(lock);
	}

	public String getCurrentPathway() {
		TitledTab t = (TitledTab) tabbedPanels.get(getSelectedView()).getSelectedTab();
		return t.getText();
	}

	public JFrame returnFrame() {
		return this;
	}

	public void setEnable(boolean enable) {
		if (enable) {
			setEnabled(true);
		} else {
			setEnabled(false);
		}
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
	
	public void updateHierarchyView() {
		optionPanel.updatePanel("Hierarchy");
	}

	public void updatePCPView() {
		optionPanel.updatePanel("pcp");
	}

	public void updateFilterView() {
		optionPanel.updatePanel("Filter");
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

	public void updateProjectProperties() {
		optionPanel.updatePanel("project");
	}

	public void updateTheoryProperties() {
		optionPanel.updatePanel("theory");
	}

	public void updateOptionPanel() {
		optionPanel.updatePanel("GraphTree");
		optionPanel.updatePanel("Satellite");
		//optionPanel.updatePanel("Hierarchy");
		optionPanel.updatePanel("Filter");
		optionPanel.updatePanel("theory");
		optionPanel.updatePanel("alignment");
		optionPanel.updatePanel("pcp");
		optionPanel.updatePanel("pathwayTree");
	}

	public void updateAllGuiElements() {
		// System.out.println("update all");
		optionPanel.updatePanel("GraphTree");
		optionPanel.updatePanel("Satellite");
		//optionPanel.updatePanel("Hierarchy");
		optionPanel.updatePanel("Filter");
		optionPanel.updatePanel("theory");
		optionPanel.updatePanel("alignment");
		optionPanel.updatePanel("pcp");
		optionPanel.updatePanel("project");
		optionPanel.updatePanel("Database");
		optionPanel.updatePanel("pathwayTree");
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

	public void openAlignmentGUI(Pathway a, Pathway b) {
		this.optionPanel.openAlignmentPanel(a, b);
	}

	public void updateAlignmentTab() {
		optionPanel.updateAlignmentTab();
	}

	public void checkForAlignmentOptionTab(String oldName, String newName) {
		optionPanel.tryUpdateAlignmentOptionTab(oldName, newName);
	}

	// =============mac osx stuff========================
	@Override
	public void handleAbout(ApplicationEvent event) {
		new AboutWindow();
		event.setHandled(true);
	}

	@Override
	public void handleOpenApplication(ApplicationEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleOpenFile(ApplicationEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handlePreferences(ApplicationEvent event) {
		// new Settings().setSelection(5);
		new Settings(1);
		event.setHandled(true);
	}

	@Override
	public void handlePrintFile(ApplicationEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleQuit(ApplicationEvent arg0) {
		// close application
		this.dispose();
		System.exit(0);
	}

	@Override
	public void handleReopenApplication(ApplicationEvent arg0) {
		// TODO Auto-generated method stub

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
}
