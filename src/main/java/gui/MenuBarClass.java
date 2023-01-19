package gui;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

import gui.eventhandlers.MenuActionCommands;
import org.simplericity.macify.eawt.Application;

import graph.GraphInstance;
import gui.eventhandlers.MenuListener;

public class MenuBarClass {

	private JMenuItem saveNetworkAs;
	private JMenuItem saveNetwork;
	private JMenuItem closeAllNetworks;
	private JMenuItem closeNetwork;
	private JMenuItem savePicture;
	private JMenuItem springLayout;
	private JMenuItem kkLayout;
	private JMenuItem frLayout;
	private JMenuItem circleLayout;
	private JMenuItem isomLayout;
	private JMenuItem gemLayout;
	private JMenuItem hebLayout;
	private JMenuItem hctLayout;
	// private JMenuItem centerGraph;
	private JMenuItem databaseItem;

	private JMenuItem visualizationSettings;
	// JMenuItem keggItem;
	// JMenuItem brendaItem;
	// JMenuItem dawisItem;
	// JMenuItem ppiItem;
	private JMenuItem biGraph;
	private JMenuItem connectedGraph;
	private JMenuItem internet;
	private JMenuItem graphSettings;
	private JMenuItem openNetwork;
	private JMenuItem interaction;
	private JMenuItem mathLaw;
	// private JMenuItem phosphoImport;

	// private JMenuItem kcore;
	private JMenuItem hamiltonGraph;
	// JMenuItem animations;
	private JMenuItem about;
	//private JMenuItem mdLayout;
	// private JMenuItem dbInformation;
	private JMenuItem export;

	/*
	 * JMenuItem exportGraphMl; JMenuItem exportMo; JMenuItem exportGon;
	 */
	private JMenuBar bar;
	private JMenuItem regularGraph;

	private JMenuItem transform;
	private JMenuItem showPN;

	private JMenuItem testPInvariant;
	private JMenuItem testTInvariant;
	private JMenuItem cov;
	private JMenuItem covreach;
	private JMenuItem modelicaResult;
	private JMenuItem editPNelements;
	private JMenuItem simulate;
	private JMenuItem createDoc;

	private JMenuItem rendererSettings;
	private JMenuItem dataLabelMapping;

	public MenuBarClass(Application application) {
		int MENUSHORTCUT = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();

		bar = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenu graph = new JMenu("Graph");

		JMenu generateGraph = new JMenu("Generate Graph");
		// JMenu visualAnalysis = new JMenu("Visual Analysis");

		// JMenu graph = new JMenu("Graph");
		// JMenu snapshot = new JMenu("Snapshot");
		JMenu layout = new JMenu("Layout");
		JMenu help = new JMenu("Help");
		JMenu settings = new JMenu("Settings");
		// JMenu experiments = new JMenu("Experiments");
		JMenu petriNets = new JMenu("Petri Net");

		// a menu part to collect some tools
		JMenu tools = new JMenu("Tools");

		JMenu transformation = new JMenu("Transformation");

		// items for the dataMapping
		JMenuItem dataMapping = new JMenuItem("Data Mapping (color)");
		dataMapping.addActionListener(MenuListener.getInstance());
		dataMapping.setActionCommand(MenuActionCommands.dataMappingColor.value);
		tools.add(dataMapping);

		// items for datamining, including smacof
		JMenuItem datamining = new JMenuItem("Data Mining (SMACOF)");
		datamining.addActionListener(MenuListener.getInstance());
		datamining.setActionCommand(MenuActionCommands.datamining.value);
		tools.add(datamining);

		// items for label to data mapping
		dataLabelMapping = new JMenuItem("Label->Data Mapping");
		dataLabelMapping.addActionListener(MenuListener.getInstance());
		dataLabelMapping.setActionCommand(MenuActionCommands.dataLabelMapping.value);
		tools.add(new JSeparator());
		tools.add(dataLabelMapping);

		JMenuItem phosphoImport = new JMenuItem("PhosphoSite input");
		phosphoImport.addActionListener(MenuListener.getInstance());
		phosphoImport.setActionCommand(MenuActionCommands.phospho.value);

		JMenuItem mirnaTest = new JMenuItem("MirnaTest");
		mirnaTest.addActionListener(MenuListener.getInstance());
		mirnaTest.setActionCommand(MenuActionCommands.mirnaTest.value);

		JMenuItem enrichGene = new JMenuItem("Enrich genes with miRNA");
		enrichGene.setToolTipText("enriches (selected) genes with miRNA information");
		enrichGene.addActionListener(MenuListener.getInstance());
		enrichGene.setActionCommand(MenuActionCommands.enrichGene.value);

		JMenuItem enrichMirna = new JMenuItem("Enrich miRNA with genes");
		enrichMirna.setToolTipText("enriches (selected) miRNA gene information");
		enrichMirna.addActionListener(MenuListener.getInstance());
		enrichMirna.setActionCommand(MenuActionCommands.enrichMirna.value);

		JMenuItem shake = new JMenuItem("Shake Enzymes!");
		shake.addActionListener(MenuListener.getInstance());
		shake.setActionCommand(MenuActionCommands.shake.value);

		JMenuItem wuff = new JMenuItem("Wuff!");
		wuff.addActionListener(MenuListener.getInstance());
		wuff.setActionCommand(MenuActionCommands.wuff.value);

		JMenuItem newNetwork = new JMenuItem("New", KeyEvent.VK_N);
		newNetwork.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, MENUSHORTCUT));
		newNetwork.addActionListener(MenuListener.getInstance());
		newNetwork.setActionCommand(MenuActionCommands.newNetwork.value);

		openNetwork = new JMenuItem("Open File", KeyEvent.VK_O);
		openNetwork.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		openNetwork.addActionListener(MenuListener.getInstance());
		openNetwork.setActionCommand(MenuActionCommands.openNetwork.value);

		export = new JMenuItem("Export Network", KeyEvent.VK_E);
		export.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, MENUSHORTCUT));
		export.addActionListener(MenuListener.getInstance());
		export.setActionCommand(MenuActionCommands.exportNetwork.value);

		/*
		 * exportGraphMl = new JMenuItem("Export Network As GraphML",KeyEvent.VK_E);
		 * exportGraphMl.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,
		 * ActionEvent.CTRL_MASK)); exportGraphMl.addActionListener(new MenuListener());
		 * exportGraphMl.setActionCommand(MenuActionCommands.exportNetworkGraphml.value);
		 * 
		 * exportMo = new JMenuItem("Export Network for Modelica",KeyEvent.VK_M);
		 * exportMo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M,
		 * ActionEvent.CTRL_MASK)); exportMo.addActionListener(new MenuListener());
		 * exportMo.setActionCommand(MenuActionCommands.exportNetworkMo.value);
		 * 
		 * exportGon = new
		 * JMenuItem("Export Network for CellIllustrator",KeyEvent.VK_I);
		 * exportGon.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I,
		 * ActionEvent.CTRL_MASK)); exportGon.addActionListener(new MenuListener());
		 * exportGon.setActionCommand(MenuActionCommands.exportNetworkGon.value);
		 */

		// animations = new JMenuItem("Animation", KeyEvent.VK_O);
		// animations.addActionListener(MenuListener.getInstance());
		// animations.setActionCommand(MenuActionCommands.animation.value);

		closeNetwork = new JMenuItem("Close Network", KeyEvent.VK_C);
		closeNetwork.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, MENUSHORTCUT));
		closeNetwork.addActionListener(MenuListener.getInstance());
		closeNetwork.setActionCommand(MenuActionCommands.closeNetwork.value);

		closeAllNetworks = new JMenuItem("Close All Networks", KeyEvent.VK_L);
		closeAllNetworks.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, MENUSHORTCUT));
		closeAllNetworks.addActionListener(MenuListener.getInstance());
		closeAllNetworks.setActionCommand(MenuActionCommands.closeAllNetworks.value);

		saveNetwork = new JMenuItem("Save", KeyEvent.VK_S);
		saveNetwork.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, MENUSHORTCUT));
		saveNetwork.addActionListener(MenuListener.getInstance());
		saveNetwork.setActionCommand(MenuActionCommands.save.value);

		saveNetworkAs = new JMenuItem("Save As");
		saveNetworkAs.addActionListener(MenuListener.getInstance());
		saveNetworkAs.setActionCommand(MenuActionCommands.saveAs.value);

		savePicture = new JMenuItem("Save A Graph-Picture", KeyEvent.VK_G);
		savePicture.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, MENUSHORTCUT));

		savePicture.addActionListener(MenuListener.getInstance());
		savePicture.setActionCommand(MenuActionCommands.graphPicture.value);

		springLayout = new JMenuItem("SpringLayout");
		springLayout.addActionListener(MenuListener.getInstance());
		springLayout.setActionCommand(MenuActionCommands.springLayout.value);

		kkLayout = new JMenuItem("KKLayout");
		kkLayout.addActionListener(MenuListener.getInstance());
		kkLayout.setActionCommand(MenuActionCommands.kkLayout.value);

		frLayout = new JMenuItem("FRLayout");
		frLayout.addActionListener(MenuListener.getInstance());
		frLayout.setActionCommand(MenuActionCommands.frLayout.value);

		circleLayout = new JMenuItem("CircleLayout");
		circleLayout.addActionListener(MenuListener.getInstance());
		circleLayout.setActionCommand(MenuActionCommands.circleLayout.value);

		hebLayout = new JMenuItem("Hierarchical Edge Bundling");
		hebLayout.addActionListener(MenuListener.getInstance());
		hebLayout.setActionCommand(MenuActionCommands.hebLayout.value);

		hctLayout = new JMenuItem("Hierarchical Circle Tree");
		hctLayout.addActionListener(MenuListener.getInstance());
		hctLayout.setActionCommand(MenuActionCommands.hctLayout.value);

		isomLayout = new JMenuItem("ISOMLayout", KeyEvent.VK_S);
		isomLayout.addActionListener(MenuListener.getInstance());
		isomLayout.setActionCommand(MenuActionCommands.isomLayout.value);

		gemLayout = new JMenuItem("GEMLayout");
		gemLayout.addActionListener(MenuListener.getInstance());
		gemLayout.setActionCommand(MenuActionCommands.gemLayout.value);

		databaseItem = new JMenuItem("Database Connection");
		databaseItem.addActionListener(MenuListener.getInstance());
		databaseItem.setActionCommand(MenuActionCommands.databaseSettings.value);

		visualizationSettings = new JMenuItem("Visualization Settings");
		visualizationSettings.addActionListener(MenuListener.getInstance());
		visualizationSettings.setActionCommand(MenuActionCommands.visualizationSettings.value);

		// keggItem = new JMenuItem("KEGG Connection");
		// keggItem.addActionListener(MenuListener.getInstance());
		// keggItem.setActionCommand(MenuActionCommands.keggSettings.value);

		// brendaItem = new JMenuItem("BRENDA Connection");
		// brendaItem.addActionListener(MenuListener.getInstance());
		// brendaItem.setActionCommand(MenuActionCommands.brendaSettings.value);
		//
		// dawisItem = new JMenuItem("DAWIS Connection");
		// dawisItem.addActionListener(MenuListener.getInstance());
		// dawisItem.setActionCommand(MenuActionCommands.dawisSettings.value);
		//
		// ppiItem = new JMenuItem("PPI Connection");
		// ppiItem.addActionListener(MenuListener.getInstance());
		// ppiItem.setActionCommand(MenuActionCommands.ppiSettings.value);

		interaction = new JMenuItem("Show network properties");
		interaction.addActionListener(MenuListener.getInstance());
		interaction.setActionCommand(MenuActionCommands.interaction.value);
		
		JMenuItem allPopUps = new JMenuItem("Show all previous PupUp messages");
		allPopUps.addActionListener(MenuListener.getInstance());
		allPopUps.setActionCommand(MenuActionCommands.allPopUps.value);
		
		JMenuItem nodesEdgesTypes = new JMenuItem("Show nodes / edges types");
		nodesEdgesTypes.addActionListener(MenuListener.getInstance());
		nodesEdgesTypes.setActionCommand(MenuActionCommands.nodesEdgesTypes.value);

		internet = new JMenuItem("Internet Connection");
		internet.addActionListener(MenuListener.getInstance());
		internet.setActionCommand(MenuActionCommands.internet.value);

		rendererSettings = new JMenuItem("Renderer Settings");
		rendererSettings.addActionListener(MenuListener.getInstance());
		rendererSettings.setActionCommand(MenuActionCommands.rendererSettings.value);

		mathLaw = new JMenuItem("Generate Random Graph");
		mathLaw.addActionListener(MenuListener.getInstance());
		mathLaw.setActionCommand(MenuActionCommands.mathGraph.value);

		biGraph = new JMenuItem("Generate Bipartite Graph");
		biGraph.addActionListener(MenuListener.getInstance());
		biGraph.setActionCommand(MenuActionCommands.biGraph.value);

		regularGraph = new JMenuItem("Generate Regular Graph");
		regularGraph.addActionListener(MenuListener.getInstance());
		regularGraph.setActionCommand(MenuActionCommands.regularGraph.value);

		connectedGraph = new JMenuItem("Generate Connected Graph");
		connectedGraph.addActionListener(MenuListener.getInstance());
		connectedGraph.setActionCommand(MenuActionCommands.connectedGraph.value);

		hamiltonGraph = new JMenuItem("Generate Hamilton Graph");
		hamiltonGraph.addActionListener(MenuListener.getInstance());
		hamiltonGraph.setActionCommand(MenuActionCommands.hamiltonGraph.value);

		graphSettings = new JMenuItem("Graph Settings");
		graphSettings.addActionListener(MenuListener.getInstance());
		graphSettings.setActionCommand(MenuActionCommands.graphSettings.value);

		transform = new JMenuItem("Transform to Petri net");
		transform.addActionListener(MenuListener.getInstance());
		transform.setActionCommand(MenuActionCommands.transform.value);
		// transform.setEnabled(false);

		showPN = new JMenuItem("Show Petri net");
		showPN.addActionListener(MenuListener.getInstance());
		showPN.setActionCommand(MenuActionCommands.showPN.value);
		// showPN.setEnabled(false);

		JMenuItem ruleManager = new JMenuItem("Rule Management");
		ruleManager.addActionListener(MenuListener.getInstance());
		ruleManager.setActionCommand(MenuActionCommands.ruleManager.value);

		help.add(allPopUps);
		help.add(nodesEdgesTypes);
		help.add(interaction);

		// about item is already present on mac osx
		if (!application.isMac()) {
			about = new JMenuItem("About");
			about.addActionListener(MenuListener.getInstance());
			about.setActionCommand(MenuActionCommands.about.value);
			help.add(about);
		}

		file.setMnemonic(KeyEvent.VK_F);
		graph.setMnemonic(KeyEvent.VK_G);
		layout.setMnemonic(KeyEvent.VK_L);
		settings.setMnemonic(KeyEvent.VK_S);
		tools.setMnemonic(KeyEvent.VK_T);
		help.setMnemonic(KeyEvent.VK_H);

		file.add(newNetwork);
		file.add(openNetwork);
		// file.add(sessionID);
		if (MainWindow.developer) {
			// file.add(openEdal);
		}
		file.add(new JSeparator());
		file.add(saveNetwork);
		file.add(saveNetworkAs);
		if (MainWindow.developer) {
			// file.add(saveEdal);
		}
		file.add(savePicture);
		file.add(export);
		/*
		 * file.add(exportGraphMl); file.add(exportMo); file.add(exportGon);
		 */
		file.add(new JSeparator());
		file.add(closeNetwork);
		file.add(closeAllNetworks);

		// Exit is already present on mac osx
		if (!application.isMac()) {
			JMenuItem exit = new JMenuItem("Exit", KeyEvent.VK_X);
			exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, MENUSHORTCUT));
			exit.addActionListener(MenuListener.getInstance());
			exit.setActionCommand(MenuActionCommands.exit.value);
			file.add(new JSeparator());
			file.add(exit);
		}

		// settings.add(keggItem);
		// settings.add(brendaItem);
		// settings.add(dawisItem);
		// settings.add(ppiItem);
		settings.add(databaseItem);
		settings.add(internet);
		settings.add(graphSettings);
		settings.add(visualizationSettings);
		settings.add(rendererSettings);

		// snapshot.add(savePicture);
		// snapshot.add(printPicture);

		graph.add(generateGraph);
		graph.add(phosphoImport);

		if (MainWindow.developer) {
			graph.add(mirnaTest);
			graph.add(enrichGene);
			graph.add(enrichMirna);
			graph.add(shake);
			graph.add(wuff);
		}
		// math.add(visualAnalysis);

		generateGraph.add(mathLaw);
		generateGraph.add(biGraph);
		generateGraph.add(regularGraph);
		generateGraph.add(connectedGraph);
		generateGraph.add(hamiltonGraph);

		layout.add(circleLayout);
		layout.add(hebLayout);
		layout.add(hctLayout);
		layout.add(frLayout);
		layout.add(isomLayout);
		layout.add(kkLayout);
		layout.add(gemLayout);
		layout.add(springLayout);
		// layout.add(mdLayout);

		// graph.add(layout);
		// graph.add(snapshot);
		// graph.add(animations);
		// graph.add(centerGraph);
		// graph.add(phosphoImport);

		// experiments.add(fabricio);
		testPInvariant = new JMenuItem("Test P Invariant");
		testPInvariant.addActionListener(MenuListener.getInstance());
		testPInvariant.setActionCommand(MenuActionCommands.openTestP.value);
		testTInvariant = new JMenuItem("Test T Invariant");
		testTInvariant.addActionListener(MenuListener.getInstance());
		testTInvariant.setActionCommand(MenuActionCommands.openTestT.value);
		cov = new JMenuItem("Cov");
		cov.addActionListener(MenuListener.getInstance());
		cov.setActionCommand(MenuActionCommands.openCov.value);
		covreach = new JMenuItem("Cov/Reach Graph");
		covreach.addActionListener(MenuListener.getInstance());
		covreach.setActionCommand(MenuActionCommands.createCov.value);
		modelicaResult = new JMenuItem("Load Simulation Result");
		modelicaResult.addActionListener(MenuListener.getInstance());
		modelicaResult.setActionCommand(MenuActionCommands.loadModResult.value);
		simulate = new JMenuItem("Simulate");
		simulate.addActionListener(MenuListener.getInstance());
		simulate.setActionCommand(MenuActionCommands.simulate.value);

		createDoc = new JMenuItem("Create Documentation");
		createDoc.addActionListener(MenuListener.getInstance());
		createDoc.setActionCommand(MenuActionCommands.createDoc.value);

		editPNelements = new JMenuItem("Edit PN-Elements");
		editPNelements.addActionListener(MenuListener.getInstance());
		editPNelements.setActionCommand(MenuActionCommands.editElements.value);

		petriNets.add(editPNelements);
		petriNets.add(simulate);
		petriNets.add(modelicaResult);
		petriNets.add(testPInvariant);
		petriNets.add(testTInvariant);
		petriNets.add(cov);
		petriNets.add(covreach);
		petriNets.add(createDoc);

		if (MainWindow.developer) {
			transformation.add(transform);
			transformation.add(showPN);
			transformation.add(ruleManager);
		}

		bar.add(file);
		// bar.add(graph);
		bar.add(graph);
		bar.add(petriNets);
		bar.add(transformation);
		// bar.add(experiments);
		bar.add(layout);
		bar.add(tools);
		bar.add(settings);
		bar.add(help);

		init();
	}

	private void init() {
		disableCloseAndSaveFunctions();
	}

	public JMenuBar returnMenu() {
		return bar;
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
		// animations.setEnabled(true);
		export.setEnabled(true);
		/*
		 * exportGraphMl.setEnabled(true); exportMo.setEnabled(true);
		 * exportGon.setEnabled(true);
		 */
		//mdLayout.setEnabled(true);
		transform.setEnabled(true);
		showPN.setEnabled(true);
		if (new GraphInstance().getPathway().isPetriNet()) {
			transform.setEnabled(false);
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
		if (new GraphInstance().getPathway().isPetriNet()) {
			transform.setEnabled(false);
			showPN.setEnabled(false);
		}else{
			transform.setEnabled(true);
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
		// animations.setEnabled(false);
		// openNetwork.setEnabled(false);
		export.setEnabled(false);
		/*
		 * exportGraphMl.setEnabled(false); exportMo.setEnabled(false);
		 * exportGon.setEnabled(false);
		 */
		//mdLayout.setEnabled(false);

		testPInvariant.setEnabled(false);
		testTInvariant.setEnabled(false);
		cov.setEnabled(false);
		simulate.setEnabled(false);
		covreach.setEnabled(false);
		modelicaResult.setEnabled(false);
		editPNelements.setEnabled(false);
		createDoc.setEnabled(false);
	}
}
