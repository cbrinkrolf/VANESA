package gui;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.*;

import biologicalElements.Pathway;
import configurations.gui.LayoutConfigPanel;
import configurations.gui.LayoutConfig;
import graph.GraphContainer;
import graph.layouts.gemLayout.GEMLayoutConfig;
import graph.layouts.hctLayout.HCTLayoutConfig;
import graph.layouts.hebLayout.HEBLayoutConfig;
import org.simplericity.macify.eawt.Application;

import configurations.Workspace;
import graph.GraphInstance;
import gui.eventhandlers.MenuActionCommands;
import gui.eventhandlers.MenuListener;
import util.VanesaUtility;

public class MainMenuBar extends JMenuBar {
	private final JMenuItem saveNetworkAs;
	private final JMenuItem saveNetwork;
	private final JMenuItem closeAllNetworks;
	private final JMenuItem closeNetwork;
	private final JMenuItem savePicture;
	private final JMenuItem springLayout;
	private final JMenuItem kkLayout;
	private final JMenuItem frLayout;
	private final JMenuItem circleLayout;
	private final JMenuItem isomLayout;
	private final JMenuItem gemLayout;
	private final JMenuItem hebLayout;
	private final JMenuItem hctLayout;
	private final JMenuItem openNetwork;
	private final JMenuItem export;
	private final JMenuItem transform;
	private final JMenuItem showTransformResult;
	private final JMenuItem showPN;
	private final JMenuItem testPInvariant;
	private final JMenuItem testTInvariant;
	private final JMenuItem cov;
	private final JMenuItem covreach;
	private final JMenuItem modelicaResult;
	private final JMenuItem editPNelements;
	private final JMenuItem simulate;
	private final JMenuItem createDoc;
	private final JMenuItem devMode;

	public MainMenuBar(Application application) {
		JMenu file = new JMenu("File");
		JMenu graph = new JMenu("Graph");

		JMenu generateGraph = new JMenu("Generate Graph");

		JMenu settings = new JMenu("Settings");

		// a menu part to collect some tools
		JMenu tools = new JMenu("Tools");

		// items for the dataMapping
		JMenuItem dataMapping = createMenuItem("Data Mapping (color)", MenuActionCommands.dataMappingColor);
		tools.add(dataMapping);
		// items for datamining, including smacof
		JMenuItem datamining = createMenuItem("Data Mining (SMACOF)", MenuActionCommands.datamining);
		tools.add(datamining);
		// items for label to data mapping
		JMenuItem dataLabelMapping = createMenuItem("Label->Data Mapping", MenuActionCommands.dataLabelMapping);
		tools.add(new JSeparator());
		tools.add(dataLabelMapping);
		JMenuItem enrichGene = createMenuItem("Enrich genes with miRNA", MenuActionCommands.enrichGene);
		enrichGene.setToolTipText("enriches (selected) genes with miRNA information");
		JMenuItem enrichMirna = createMenuItem("Enrich miRNA with genes", MenuActionCommands.enrichMirna);
		enrichMirna.setToolTipText("enriches (selected) miRNA gene information");
		JMenuItem shake = createMenuItem("Shake Enzymes!", MenuActionCommands.shake);
		JMenuItem wuff = createMenuItem("Wuff!", MenuActionCommands.wuff);
		JMenuItem newNetwork = createMenuItem("New", KeyEvent.VK_N, MenuActionCommands.newNetwork);
		openNetwork = createMenuItem("Open File", KeyEvent.VK_O, MenuActionCommands.openNetwork);
		export = createMenuItem("Export Network", KeyEvent.VK_E, MenuActionCommands.exportNetwork);
		closeNetwork = createMenuItem("Close Network", KeyEvent.VK_C, MenuActionCommands.closeNetwork);
		closeAllNetworks = createMenuItem("Close All Networks", KeyEvent.VK_L, MenuActionCommands.closeAllNetworks);
		saveNetwork = createMenuItem("Save", KeyEvent.VK_S, MenuActionCommands.save);
		saveNetworkAs = createMenuItem("Save As", MenuActionCommands.saveAs);
		savePicture = createMenuItem("Save A Graph-Picture", KeyEvent.VK_G, MenuActionCommands.graphPicture);

		JMenuItem visualizationSettingsMenuItem = createMenuItem("Visualization Settings",
				MenuActionCommands.visualizationSettings);
		JMenuItem generalSettingsMenuItem = createMenuItem("General Settings", MenuActionCommands.settings);
		JMenuItem graphSettingsMenuItem = createMenuItem("Graph Settings", MenuActionCommands.graphSettings);

		JMenuItem generateRandomGraphMenuItem = createMenuItem("Generate Random Graph", MenuActionCommands.mathGraph);
		JMenuItem generateBiGraphMenuItem = createMenuItem("Generate Bipartite Graph", MenuActionCommands.biGraph);
		JMenuItem regularGraph = createMenuItem("Generate Regular Graph", MenuActionCommands.regularGraph);
		JMenuItem connectedGraph = createMenuItem("Generate Connected Graph", MenuActionCommands.connectedGraph);
		JMenuItem hamiltonGraph = createMenuItem("Generate Hamilton Graph", MenuActionCommands.hamiltonGraph);

		// Help menu
		JMenu helpMenu = new JMenu("Help");
		JMenuItem allPopUps = createMenuItem("Show all previous PopUp messages", MenuActionCommands.allPopUps);
		final JMenuItem reportIssue = createMenuItem("Report Issue", this::onReportIssueClicked);
		final String label = Workspace.getCurrentSettings().isDeveloperMode()
				? "Next launch: normal mode"
				: "Next launch: developer mode";
		devMode = createMenuItem(label, MenuActionCommands.devMode);
		helpMenu.add(allPopUps);
		helpMenu.add(reportIssue);
		helpMenu.add(devMode);
		// about item is already present on mac osx
		if (!application.isMac()) {
			JMenuItem about = createMenuItem("About", MenuActionCommands.about);
			helpMenu.add(about);
		}

		file.setMnemonic(KeyEvent.VK_F);
		graph.setMnemonic(KeyEvent.VK_G);
		settings.setMnemonic(KeyEvent.VK_S);
		tools.setMnemonic(KeyEvent.VK_T);
		helpMenu.setMnemonic(KeyEvent.VK_H);

		file.add(newNetwork);
		file.add(openNetwork);

		file.add(new JSeparator());
		file.add(saveNetwork);
		file.add(saveNetworkAs);

		file.add(savePicture);
		file.add(export);
		file.add(new JSeparator());
		file.add(closeNetwork);
		file.add(closeAllNetworks);

		// Exit is already present on mac osx
		if (!application.isMac()) {
			JMenuItem exit = createMenuItem("Exit", KeyEvent.VK_X, MenuActionCommands.exit);
			file.add(new JSeparator());
			file.add(exit);
		}

		settings.add(generalSettingsMenuItem);
		settings.add(graphSettingsMenuItem);
		settings.add(visualizationSettingsMenuItem);
		settings.add(new JSeparator());
		settings.add(createMenuItem("Open Workspace Folder...",
				() -> VanesaUtility.openFolderInExplorer(Workspace.getCurrent().getPath())));

		graph.add(generateGraph);
		if (Workspace.getCurrentSettings().isDeveloperMode()) {
			graph.add(enrichGene);
			graph.add(enrichMirna);
			graph.add(shake);
			graph.add(wuff);
		}

		generateGraph.add(generateRandomGraphMenuItem);
		generateGraph.add(generateBiGraphMenuItem);
		generateGraph.add(regularGraph);
		generateGraph.add(connectedGraph);
		generateGraph.add(hamiltonGraph);

		// Layout menu
		final JMenu layoutMenu = new JMenu("Layout");
		layoutMenu.setMnemonic(KeyEvent.VK_L);
		springLayout = createMenuItem("Spring Layout",
				() -> changeLayoutImmediately(() -> GraphInstance.getMyGraph().changeToSpringLayout()));
		kkLayout = createMenuItem("KK Layout",
				() -> changeLayoutImmediately(() -> GraphInstance.getMyGraph().changeToKKLayout()));
		frLayout = createMenuItem("FR Layout",
				() -> changeLayoutImmediately(() -> GraphInstance.getMyGraph().changeToFRLayout()));
		circleLayout = createMenuItem("Circle Layout",
				() -> changeLayoutImmediately(() -> GraphInstance.getMyGraph().changeToCircleLayout()));
		hebLayout = createMenuItem("HEB Layout (Hierarchical Edge Bundling)...",
				() -> changeLayout(HEBLayoutConfig.getInstance()));
		hctLayout = createMenuItem("HCT Layout (Hierarchical Circle Tree)...",
				() -> changeLayout(HCTLayoutConfig.getInstance()));
		isomLayout = createMenuItem("ISOM Layout", KeyEvent.VK_S,
				() -> changeLayoutImmediately(() -> GraphInstance.getMyGraph().changeToISOMLayout()));
		gemLayout = createMenuItem("GEM Layout...", () -> changeLayout(GEMLayoutConfig.getInstance()));
		layoutMenu.add(circleLayout);
		layoutMenu.add(hebLayout);
		layoutMenu.add(hctLayout);
		layoutMenu.add(frLayout);
		layoutMenu.add(isomLayout);
		layoutMenu.add(kkLayout);
		layoutMenu.add(gemLayout);
		layoutMenu.add(springLayout);

		// Petri Net menu
		final JMenu petriNetMenu = new JMenu("Petri Net");
		editPNelements = createMenuItem("Edit PN-Elements", MenuActionCommands.editElements);
		simulate = createMenuItem("Simulate", MenuActionCommands.simulate);
		modelicaResult = createMenuItem("Load Simulation Result", MenuActionCommands.loadModResult);
		testPInvariant = createMenuItem("Test P Invariant", MenuActionCommands.openTestP);
		testTInvariant = createMenuItem("Test T Invariant", MenuActionCommands.openTestT);
		cov = createMenuItem("Cov", MenuActionCommands.openCov);
		covreach = createMenuItem("Cov/Reach Graph", MenuActionCommands.createCov);
		createDoc = createMenuItem("Create Documentation", MenuActionCommands.createDoc);
		petriNetMenu.add(editPNelements);
		petriNetMenu.add(simulate);
		petriNetMenu.add(modelicaResult);
		petriNetMenu.add(testPInvariant);
		petriNetMenu.add(testTInvariant);
		petriNetMenu.add(cov);
		petriNetMenu.add(covreach);
		petriNetMenu.add(createDoc);

		// Transformation menu
		JMenu transformationMenu = new JMenu("Transformation");
		transform = createMenuItem("Transform to Petri net", MenuActionCommands.transform);
		showTransformResult = createMenuItem("Show Transformation Result", MenuActionCommands.showTransformResult);
		showPN = createMenuItem("Show Petri net", MenuActionCommands.showPN);
		JMenuItem ruleManager = createMenuItem("Rule Management", MenuActionCommands.ruleManager);
		transformationMenu.add(transform);
		transformationMenu.add(showTransformResult);
		transformationMenu.add(showPN);
		transformationMenu.add(ruleManager);

		add(file);
		add(graph);
		add(petriNetMenu);
		add(transformationMenu);
		add(layoutMenu);
		add(tools);
		add(settings);
		add(helpMenu);

		disableCloseAndSaveFunctions();
	}

	private JMenuItem createMenuItem(final String text, final Runnable action) {
		final JMenuItem item = new JMenuItem(text);
		item.addActionListener((e) -> action.run());
		return item;
	}

	private JMenuItem createMenuItem(final String text, final int keyEvent, final Runnable action) {
		final JMenuItem item = new JMenuItem(text, keyEvent);
		item.addActionListener((e) -> action.run());
		return item;
	}

	private JMenuItem createMenuItem(final String text, final MenuActionCommands command) {
		final JMenuItem item = new JMenuItem(text);
		item.addActionListener(MenuListener.getInstance());
		item.setActionCommand(command.value);
		return item;
	}

	private JMenuItem createMenuItem(final String text, final int keyEvent, final MenuActionCommands command) {
		final JMenuItem item = new JMenuItem(text, keyEvent);
		item.addActionListener(MenuListener.getInstance());
		item.setActionCommand(command.value);
		item.setAccelerator(KeyStroke.getKeyStroke(keyEvent, InputEvent.CTRL_DOWN_MASK));
		return item;
	}

	private void onReportIssueClicked() {
		VanesaUtility.openURLInBrowser("https://github.com/cbrinkrolf/VANESA/issues");
	}

	private static void changeLayout(final LayoutConfigPanel layoutConfigPanel) {
		final GraphContainer con = GraphContainer.getInstance();
		final Pathway pw = GraphInstance.getPathway();
		if (con.containsPathway() && pw != null) {
			if (pw.hasGotAtLeastOneElement()) {
				LayoutConfig.show(layoutConfigPanel);
			} else {
				PopUpDialog.getInstance().show("Error", "Please create a network first.");
			}
		} else {
			PopUpDialog.getInstance().show("Error", "Please create a network first.");
		}
	}

	private static void changeLayoutImmediately(final Runnable callback) {
		final GraphContainer con = GraphContainer.getInstance();
		final Pathway pw = GraphInstance.getPathway();
		if (con.containsPathway() && pw != null && pw.hasGotAtLeastOneElement()) {
			if (LayoutConfig.askBeforeLayoutIfClustersPresent()) {
				callback.run();
			}
		} else {
			PopUpDialog.getInstance().show("Error", "Please create a network first.");
		}
	}

	public void enableCloseAndSaveFunctions() {
		saveNetworkAs.setEnabled(true);
		saveNetwork.setEnabled(true);
		openNetwork.setEnabled(true);
		closeAllNetworks.setEnabled(true);
		closeNetwork.setEnabled(true);
		savePicture.setEnabled(true);
		circleLayout.setEnabled(true);
		frLayout.setEnabled(true);
		isomLayout.setEnabled(true);
		kkLayout.setEnabled(true);
		springLayout.setEnabled(true);
		gemLayout.setEnabled(true);
		hebLayout.setEnabled(true);
		hctLayout.setEnabled(true);
		export.setEnabled(true);
		transform.setEnabled(true);
		showTransformResult.setEnabled(true);
		showPN.setEnabled(true);
		if (GraphInstance.getPathway().isPetriNet()) {
			transform.setEnabled(false);
			showTransformResult.setEnabled(false);
			showPN.setEnabled(false);
			testPInvariant.setEnabled(true);
			testTInvariant.setEnabled(true);
			cov.setEnabled(true);
			simulate.setEnabled(true);
			covreach.setEnabled(true);
			modelicaResult.setEnabled(true);
			editPNelements.setEnabled(true);
			createDoc.setEnabled(true);
		}
	}

	public void setPetriView(boolean isPetriNet) {
		if (GraphInstance.getPathway().isPetriNet()) {
			transform.setEnabled(false);
			showTransformResult.setEnabled(false);
			showPN.setEnabled(false);
		} else {
			transform.setEnabled(true);
			showTransformResult.setEnabled(true);
			showPN.setEnabled(true);
		}

		testPInvariant.setEnabled(isPetriNet);
		testTInvariant.setEnabled(isPetriNet);
		cov.setEnabled(isPetriNet);
		simulate.setEnabled(isPetriNet);
		covreach.setEnabled(isPetriNet);
		modelicaResult.setEnabled(isPetriNet);
		editPNelements.setEnabled(isPetriNet);
		createDoc.setEnabled(isPetriNet);
	}

	public void disableCloseAndSaveFunctions() {
		saveNetworkAs.setEnabled(false);
		saveNetwork.setEnabled(false);
		closeAllNetworks.setEnabled(false);
		closeNetwork.setEnabled(false);
		savePicture.setEnabled(false);
		circleLayout.setEnabled(false);
		frLayout.setEnabled(false);
		isomLayout.setEnabled(false);
		kkLayout.setEnabled(false);
		springLayout.setEnabled(false);
		gemLayout.setEnabled(false);
		hebLayout.setEnabled(false);
		hctLayout.setEnabled(false);
		export.setEnabled(false);
		testPInvariant.setEnabled(false);
		testTInvariant.setEnabled(false);
		cov.setEnabled(false);
		simulate.setEnabled(false);
		covreach.setEnabled(false);
		modelicaResult.setEnabled(false);
		editPNelements.setEnabled(false);
		createDoc.setEnabled(false);
	}

	public void setDeveloperLabel(String label) {
		devMode.setText(label);
	}
}
