package gui;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

import org.simplericity.macify.eawt.Application;

import graph.GraphInstance;
import gui.eventhandlers.MenuListener;

public class MenuBarClass {

	private JMenuItem saveNetworkAs;
	private JMenuItem saveNetwork;
	private JMenuItem saveEdal;
	private JMenuItem openEdal;
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
	private JMenuItem graphAlignment;
	private JMenuItem graphSettings;
	private JMenuItem openNetwork;
	private JMenuItem interaction;
	private JMenuItem mathLaw;
	// private JMenuItem phosphoImport;

	// private JMenuItem kcore;
	private JMenuItem hamiltonGraph;
	// JMenuItem animations;
	private JMenuItem about;
	private JMenuItem mdLayout;
	// private JMenuItem dbInformation;
	private JMenuItem export;

	/*
	 * JMenuItem exportGraphMl; JMenuItem exportMo; JMenuItem exportGon;
	 */
	private JMenuBar bar;
	private JMenuItem regularGraph;

	private JMenuItem testPInvariant;
	private JMenuItem testTInvariant;
	private JMenuItem cov;
	private JMenuItem covreach;
	private JMenuItem modellicaResult;
	private JMenuItem editPNelements;
	private JMenuItem simulate;
	private JMenuItem createDoc;

	private JMenuItem convertPetriNet;

	private JMenuItem resolveReferences;
	private JMenuItem rendererSettings;
	private JMenuItem dataLabelMapping;

	public MenuBarClass(Application application) {
		int MENUSHORTCUT = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

		bar = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenu math = new JMenu("Graph");

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
		dataMapping.setActionCommand("dataMappingColor");
		tools.add(dataMapping);

		// items for the dataMapping
		JMenuItem dataMappingDb = new JMenuItem("Data Mapping (DB)");
		dataMappingDb.addActionListener(MenuListener.getInstance());
		dataMappingDb.setActionCommand("dataMappingDB");
		if(MainWindow.developer)
			tools.add(dataMappingDb);

		// items for datamining, including smacof
		JMenuItem datamining = new JMenuItem("Data Mining (SMACOF)");
		datamining.addActionListener(MenuListener.getInstance());
		datamining.setActionCommand("datamining");
		tools.add(datamining);

		// items for label to data mapping
		dataLabelMapping = new JMenuItem("Label->Data Mapping");
		dataLabelMapping.addActionListener(MenuListener.getInstance());
		dataLabelMapping.setActionCommand("dataLabelMapping");
		tools.add(new JSeparator());
		tools.add(dataLabelMapping);

		JMenuItem phosphoImport = new JMenuItem("PhosphoSite input");
		phosphoImport.addActionListener(MenuListener.getInstance());
		phosphoImport.setActionCommand("phospho");

		JMenuItem mirnaTest = new JMenuItem("MirnaTest");
		mirnaTest.addActionListener(MenuListener.getInstance());
		mirnaTest.setActionCommand("mirnaTest");
		
		JMenuItem mirnaTargets = new JMenuItem("Enrich miRNA targets");
		mirnaTargets.addActionListener(MenuListener.getInstance());
		mirnaTargets.setActionCommand("mirnaTargets");

		JMenuItem shake = new JMenuItem("Shake Enzymes!");
		shake.addActionListener(MenuListener.getInstance());
		shake.setActionCommand("shake");

		JMenuItem fabricio = new JMenuItem("Patricios Data");
		fabricio.addActionListener(MenuListener.getInstance());
		fabricio.setActionCommand("fabricio");

		JMenuItem newNetwork = new JMenuItem("New", KeyEvent.VK_N);
		newNetwork.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, MENUSHORTCUT));
		newNetwork.addActionListener(MenuListener.getInstance());
		newNetwork.setActionCommand("new Network");

		openNetwork = new JMenuItem("Open File", KeyEvent.VK_O);
		openNetwork.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		openNetwork.addActionListener(MenuListener.getInstance());
		openNetwork.setActionCommand("open Network");

		export = new JMenuItem("Export Network", KeyEvent.VK_E);
		export.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, MENUSHORTCUT));
		export.addActionListener(MenuListener.getInstance());
		export.setActionCommand("export Network");

		/*
		 * exportGraphMl = new
		 * JMenuItem("Export Network As GraphML",KeyEvent.VK_E);
		 * exportGraphMl.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,
		 * ActionEvent.CTRL_MASK)); exportGraphMl.addActionListener(new
		 * MenuListener());
		 * exportGraphMl.setActionCommand("export Network Graphml");
		 * 
		 * exportMo = new
		 * JMenuItem("Export Network for Modelica",KeyEvent.VK_M);
		 * exportMo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M,
		 * ActionEvent.CTRL_MASK)); exportMo.addActionListener(new
		 * MenuListener()); exportMo.setActionCommand("export Network Mo");
		 * 
		 * exportGon = new
		 * JMenuItem("Export Network for CellIllustrator",KeyEvent.VK_I);
		 * exportGon.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I,
		 * ActionEvent.CTRL_MASK)); exportGon.addActionListener(new
		 * MenuListener()); exportGon.setActionCommand("export Network Gon");
		 */

		// animations = new JMenuItem("Animation", KeyEvent.VK_O);
		// animations.addActionListener(MenuListener.getInstance());
		// animations.setActionCommand("animation");

		closeNetwork = new JMenuItem("Close Network", KeyEvent.VK_C);
		closeNetwork.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, MENUSHORTCUT));

		closeNetwork.addActionListener(MenuListener.getInstance());
		closeNetwork.setActionCommand("close Network");

		closeAllNetworks = new JMenuItem("Close All Networks", KeyEvent.VK_L);
		closeAllNetworks.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, MENUSHORTCUT));
		closeAllNetworks.addActionListener(MenuListener.getInstance());
		closeAllNetworks.setActionCommand("close All Networks");

		saveNetwork = new JMenuItem("Save", KeyEvent.VK_S);
		saveNetwork.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, MENUSHORTCUT));
		saveNetwork.addActionListener(MenuListener.getInstance());
		saveNetwork.setActionCommand("save");

		saveNetworkAs = new JMenuItem("Save As");
		saveNetworkAs.addActionListener(MenuListener.getInstance());
		saveNetworkAs.setActionCommand("save as");

		saveEdal = new JMenuItem("Save online");
		saveEdal.addActionListener(MenuListener.getInstance());
		saveEdal.setActionCommand("saveEdal");

		openEdal = new JMenuItem("Open online");
		openEdal.addActionListener(MenuListener.getInstance());
		openEdal.setActionCommand("openEdal");

		savePicture = new JMenuItem("Save A Graph-Picture", KeyEvent.VK_G);
		savePicture.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, MENUSHORTCUT));

		savePicture.addActionListener(MenuListener.getInstance());
		savePicture.setActionCommand("graphPicture");

		springLayout = new JMenuItem("SpringLayout");
		springLayout.addActionListener(MenuListener.getInstance());
		springLayout.setActionCommand("springLayout");

		kkLayout = new JMenuItem("KKLayout");
		kkLayout.addActionListener(MenuListener.getInstance());
		kkLayout.setActionCommand("kkLayout");

		frLayout = new JMenuItem("FRLayout");
		frLayout.addActionListener(MenuListener.getInstance());
		frLayout.setActionCommand("frLayout");

		circleLayout = new JMenuItem("CircleLayout");
		circleLayout.addActionListener(MenuListener.getInstance());
		circleLayout.setActionCommand("circleLayout");

		hebLayout = new JMenuItem("Hierarchical Edge Bundling");
		hebLayout.addActionListener(MenuListener.getInstance());
		hebLayout.setActionCommand("hebLayout");

		hctLayout = new JMenuItem("Hierarchical Circle Tree");
		hctLayout.addActionListener(MenuListener.getInstance());
		hctLayout.setActionCommand("hctLayout");

		isomLayout = new JMenuItem("ISOMLayout", KeyEvent.VK_S);
		isomLayout.addActionListener(MenuListener.getInstance());
		isomLayout.setActionCommand("isomLayout");

		mdLayout = new JMenuItem("MDLayout");
		mdLayout.addActionListener(MenuListener.getInstance());
		mdLayout.setActionCommand("MDLayout");

		gemLayout = new JMenuItem("GEMLayout");
		gemLayout.addActionListener(MenuListener.getInstance());
		gemLayout.setActionCommand("gemLayout");

		databaseItem = new JMenuItem("Database Connection");
		databaseItem.addActionListener(MenuListener.getInstance());
		databaseItem.setActionCommand("database settings");

		visualizationSettings = new JMenuItem("Visualization Settings");
		visualizationSettings.addActionListener(MenuListener.getInstance());
		visualizationSettings.setActionCommand("visualizationSettings");

		// keggItem = new JMenuItem("KEGG Connection");
		// keggItem.addActionListener(MenuListener.getInstance());
		// keggItem.setActionCommand("kegg settings");

		// brendaItem = new JMenuItem("BRENDA Connection");
		// brendaItem.addActionListener(MenuListener.getInstance());
		// brendaItem.setActionCommand("brenda settings");
		//
		// dawisItem = new JMenuItem("DAWIS Connection");
		// dawisItem.addActionListener(MenuListener.getInstance());
		// dawisItem.setActionCommand("dawis settings");
		//
		// ppiItem = new JMenuItem("PPI Connection");
		// ppiItem.addActionListener(MenuListener.getInstance());
		// ppiItem.setActionCommand("ppi settings");

		interaction = new JMenuItem("User interaction");
		interaction.addActionListener(MenuListener.getInstance());
		interaction.setActionCommand("interaction");

		internet = new JMenuItem("Internet Connection");
		internet.addActionListener(MenuListener.getInstance());
		internet.setActionCommand("internet");

		graphAlignment = new JMenuItem("Graph Alignment");
		graphAlignment.addActionListener(MenuListener.getInstance());
		graphAlignment.setActionCommand("graphAlignemnt");

		rendererSettings = new JMenuItem("Renderer Settings");
		rendererSettings.addActionListener(MenuListener.getInstance());
		rendererSettings.setActionCommand("rendererSettings");

		mathLaw = new JMenuItem("Generate Random Graph");
		mathLaw.addActionListener(MenuListener.getInstance());
		mathLaw.setActionCommand("mathGraph");

		biGraph = new JMenuItem("Generate Bipartite Graph");
		biGraph.addActionListener(MenuListener.getInstance());
		biGraph.setActionCommand("biGraph");

		regularGraph = new JMenuItem("Generate Regular Graph");
		regularGraph.addActionListener(MenuListener.getInstance());
		regularGraph.setActionCommand("regularGraph");

		connectedGraph = new JMenuItem("Generate Connected Graph");
		connectedGraph.addActionListener(MenuListener.getInstance());
		connectedGraph.setActionCommand("connectedGraph");

		hamiltonGraph = new JMenuItem("Generate Hamilton Graph");
		hamiltonGraph.addActionListener(MenuListener.getInstance());
		hamiltonGraph.setActionCommand("hamiltonGraph");

		graphSettings = new JMenuItem("Graph Settings");
		graphSettings.addActionListener(MenuListener.getInstance());
		graphSettings.setActionCommand("graphSettings");

		resolveReferences = new JMenuItem("Resolve Reverences");
		resolveReferences.addActionListener(MenuListener.getInstance());
		resolveReferences.setActionCommand("resolveReferences");

		help.add(interaction);

		// about item is allready present on mac osx
		if (!application.isMac()) {
			about = new JMenuItem("About");
			about.addActionListener(MenuListener.getInstance());
			about.setActionCommand("about");
			help.add(about);
		}

		file.setMnemonic(KeyEvent.VK_F);
		math.setMnemonic(KeyEvent.VK_G);
		layout.setMnemonic(KeyEvent.VK_L);
		settings.setMnemonic(KeyEvent.VK_S);
		tools.setMnemonic(KeyEvent.VK_T);
		help.setMnemonic(KeyEvent.VK_H);

		file.add(newNetwork);
		file.add(openNetwork);
		// file.add(sessionID);
		if (MainWindow.developer) {
			file.add(openEdal);
		}
		file.add(new JSeparator());
		file.add(saveNetwork);
		file.add(saveNetworkAs);
		if (MainWindow.developer) {
			file.add(saveEdal);
		}
		file.add(savePicture);
		file.add(export);
		/*
		 * file.add(exportGraphMl); file.add(exportMo); file.add(exportGon);
		 */
		file.add(new JSeparator());
		file.add(closeNetwork);
		file.add(closeAllNetworks);

		// Exit is allready present on mac osx
		if (!application.isMac()) {
			JMenuItem exit = new JMenuItem("Exit", KeyEvent.VK_X);
			exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, MENUSHORTCUT));
			exit.addActionListener(MenuListener.getInstance());
			exit.setActionCommand("exit");
			file.add(new JSeparator());
			file.add(exit);
		}

		// settings.add(keggItem);
		// settings.add(brendaItem);
		// settings.add(dawisItem);
		// settings.add(ppiItem);
		settings.add(databaseItem);
		settings.add(internet);
		settings.add(graphAlignment);
		settings.add(graphSettings);
		settings.add(visualizationSettings);
		settings.add(rendererSettings);

		// snapshot.add(savePicture);
		// snapshot.add(printPicture);

		math.add(generateGraph);
		math.add(phosphoImport);

		if (MainWindow.developer) {
			math.add(mirnaTest);
			math.add(mirnaTargets);
			math.add(shake);
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
		// TODO
		testPInvariant = new JMenuItem("Test P Invariante");
		testPInvariant.addActionListener(MenuListener.getInstance());
		testPInvariant.setActionCommand("openTestP");
		testTInvariant = new JMenuItem("Test T Invariante");
		testTInvariant.addActionListener(MenuListener.getInstance());
		testTInvariant.setActionCommand("openTestT");
		cov = new JMenuItem("Cov");
		cov.addActionListener(MenuListener.getInstance());
		cov.setActionCommand("openCov");
		covreach = new JMenuItem("Cov/Reach Graph");
		covreach.addActionListener(MenuListener.getInstance());
		covreach.setActionCommand("createCov");
		modellicaResult = new JMenuItem("Load Simulation Result");
		modellicaResult.addActionListener(MenuListener.getInstance());
		modellicaResult.setActionCommand("loadModResult");
		simulate = new JMenuItem("Simulate");
		simulate.addActionListener(MenuListener.getInstance());
		simulate.setActionCommand("simulate");

		createDoc = new JMenuItem("Create Documentation");
		createDoc.addActionListener(MenuListener.getInstance());
		createDoc.setActionCommand("createDoc");

		editPNelements = new JMenuItem("Edit PN-Elements");
		editPNelements.addActionListener(MenuListener.getInstance());
		editPNelements.setActionCommand("editElements");
		convertPetriNet = new JMenuItem("Convert Graph into Petri-Net");
		convertPetriNet.addActionListener(MenuListener.getInstance());
		convertPetriNet.setActionCommand("convertIntoPetriNet");
		petriNets.add(convertPetriNet);
		petriNets.add(editPNelements);
		petriNets.add(simulate);
		petriNets.add(modellicaResult);
		petriNets.add(testPInvariant);
		petriNets.add(testTInvariant);
		petriNets.add(cov);
		petriNets.add(covreach);
		petriNets.add(createDoc);

		transformation.add(resolveReferences);

		bar.add(file);
		// bar.add(graph);
		bar.add(math);
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
		saveEdal.setEnabled(true);
		saveNetwork.setEnabled(true);
		openNetwork.setEnabled(true);
		openEdal.setEnabled(true);
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
		mdLayout.setEnabled(true);

		if (new GraphInstance().getPathway().isPetriNet()) {
			testPInvariant.setEnabled(true);
			testTInvariant.setEnabled(true);
			cov.setEnabled(true);
			simulate.setEnabled(true);
			covreach.setEnabled(true);
			modellicaResult.setEnabled(true);
			editPNelements.setEnabled(true);
			createDoc.setEnabled(true);
		} else
			convertPetriNet.setEnabled(true);
	}

	public void setPetriView(boolean isPetriNet) {
		testPInvariant.setEnabled(isPetriNet);
		testTInvariant.setEnabled(isPetriNet);
		cov.setEnabled(isPetriNet);
		simulate.setEnabled(isPetriNet);
		covreach.setEnabled(isPetriNet);
		modellicaResult.setEnabled(isPetriNet);
		editPNelements.setEnabled(isPetriNet);
		convertPetriNet.setEnabled(!isPetriNet);
		createDoc.setEnabled(isPetriNet);
	}

	public void disableCloseAndSaveFunctions() {
		saveNetworkAs.setEnabled(false);
		saveEdal.setEnabled(false);
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
		mdLayout.setEnabled(false);

		testPInvariant.setEnabled(false);
		testTInvariant.setEnabled(false);
		cov.setEnabled(false);
		simulate.setEnabled(false);
		covreach.setEnabled(false);
		convertPetriNet.setEnabled(false);
		modellicaResult.setEnabled(false);
		editPNelements.setEnabled(false);
		createDoc.setEnabled(false);
	}
}
