package gui;

import graph.GraphInstance;
import gui.eventhandlers.MenuListener;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

import org.simplericity.macify.eawt.Application;

public class MenuBarClass {

	private JMenuItem saveNetworkAs;
	private JMenuItem saveNetwork;
	private JMenuItem closeAllNetworks;
	private JMenuItem closeNetwork;
	private JMenuItem savePicture;
	private JMenuItem printPicture;
	private JMenuItem springLayout;
	private JMenuItem kkLayout;
	private JMenuItem frLayout;
	private JMenuItem circleLayout;
	private JMenuItem isomLayout;
	private JMenuItem gemLayout;
	private JMenuItem hebLayout;
	private JMenuItem hctLayout;
	//private JMenuItem centerGraph;
	private JMenuItem databaseItem;
	

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
	//private JMenuItem phosphoImport;

	//private JMenuItem kcore;
	private JMenuItem hamiltonGraph;
	// JMenuItem animations;
	private JMenuItem about;
	private JMenuItem mdLayout;
	//private JMenuItem dbInformation;
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

	public MenuBarClass(Application application) {
		int MENUSHORTCUT = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

		bar = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenu math = new JMenu("Graph");

		JMenu generateGraph = new JMenu("Generate Graph");
		//JMenu visualAnalysis = new JMenu("Visual Analysis");

		//JMenu graph = new JMenu("Graph");
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
		dataMapping.addActionListener(new MenuListener());
		dataMapping.setActionCommand("dataMappingColor");
		tools.add(dataMapping);
		
		
		// items for the dataMapping
		JMenuItem dataMappingDb = new JMenuItem("Data Mapping (DB)");
		dataMappingDb.addActionListener(new MenuListener());
		dataMappingDb.setActionCommand("dataMappingDB");
		tools.add(dataMappingDb);

		JMenuItem phosphoImport = new JMenuItem("PhosphoSite input");
		phosphoImport.addActionListener(new MenuListener());
		phosphoImport.setActionCommand("phospho");

		JMenuItem mirnaTest = new JMenuItem("MirnaTest");
		mirnaTest.addActionListener(new MenuListener());
		mirnaTest.setActionCommand("mirnaTest");
		
		JMenuItem shake = new JMenuItem("Shake Enzymes!");
		shake.addActionListener(new MenuListener());
		shake.setActionCommand("shake");
		
		
		JMenuItem fabricio = new JMenuItem("Patricios Data");
		fabricio.addActionListener(new MenuListener());
		fabricio.setActionCommand("fabricio");

		JMenuItem newNetwork = new JMenuItem("New", KeyEvent.VK_N);
		newNetwork.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
				MENUSHORTCUT));
		newNetwork.addActionListener(new MenuListener());
		newNetwork.setActionCommand("new Network");

		openNetwork = new JMenuItem("Open File", KeyEvent.VK_O);
		openNetwork.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		openNetwork.addActionListener(new MenuListener());
		openNetwork.setActionCommand("open Network");

		export = new JMenuItem("Export Network", KeyEvent.VK_E);
		export.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,
				MENUSHORTCUT));
		export.addActionListener(new MenuListener());
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
		// animations.addActionListener(new MenuListener());
		// animations.setActionCommand("animation");

		closeNetwork = new JMenuItem("Close Network", KeyEvent.VK_C);
		closeNetwork.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
				MENUSHORTCUT));

		closeNetwork.addActionListener(new MenuListener());
		closeNetwork.setActionCommand("close Network");

		closeAllNetworks = new JMenuItem("Close All Networks", KeyEvent.VK_L);
		closeAllNetworks.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L,
				MENUSHORTCUT));
		closeAllNetworks.addActionListener(new MenuListener());
		closeAllNetworks.setActionCommand("close All Networks");

		saveNetwork = new JMenuItem("Save", KeyEvent.VK_S);
		saveNetwork.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				MENUSHORTCUT));
		saveNetwork.addActionListener(new MenuListener());
		saveNetwork.setActionCommand("save");

		saveNetworkAs = new JMenuItem("Save As");
		saveNetworkAs.addActionListener(new MenuListener());
		saveNetworkAs.setActionCommand("save as");

		savePicture = new JMenuItem("Save A Graph-Picture", KeyEvent.VK_G);
		savePicture.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G,
				MENUSHORTCUT));

		savePicture.addActionListener(new MenuListener());
		savePicture.setActionCommand("graphPicture");

		printPicture = new JMenuItem("Print A Graph-Picture", KeyEvent.VK_P);
		printPicture.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,
				MENUSHORTCUT));

		printPicture.addActionListener(new MenuListener());
		printPicture.setActionCommand("printPicture");

		springLayout = new JMenuItem("SpringLayout");
		springLayout.addActionListener(new MenuListener());
		springLayout.setActionCommand("springLayout");

		kkLayout = new JMenuItem("KKLayout");
		kkLayout.addActionListener(new MenuListener());
		kkLayout.setActionCommand("kkLayout");

		frLayout = new JMenuItem("FRLayout");
		frLayout.addActionListener(new MenuListener());
		frLayout.setActionCommand("frLayout");

		circleLayout = new JMenuItem("CircleLayout");
		circleLayout.addActionListener(new MenuListener());
		circleLayout.setActionCommand("circleLayout");
		
		hebLayout = new JMenuItem("Hierarchical Edge Bundling");
		hebLayout.addActionListener(new MenuListener());
		hebLayout.setActionCommand("hebLayout");
		
		hctLayout = new JMenuItem("Hierarchical Circle Tree");
		hctLayout.addActionListener(new MenuListener());
		hctLayout.setActionCommand("hctLayout");
		

		isomLayout = new JMenuItem("ISOMLayout", KeyEvent.VK_S);
		isomLayout.addActionListener(new MenuListener());
		isomLayout.setActionCommand("isomLayout");

		mdLayout = new JMenuItem("MDLayout");
		mdLayout.addActionListener(new MenuListener());
		mdLayout.setActionCommand("MDLayout");

		gemLayout = new JMenuItem("GEMLayout");
		gemLayout.addActionListener(new MenuListener());
		gemLayout.setActionCommand("gemLayout");

		databaseItem = new JMenuItem("Database Connection");
		databaseItem.addActionListener(new MenuListener());
		databaseItem.setActionCommand("database settings");


		// keggItem = new JMenuItem("KEGG Connection");
		// keggItem.addActionListener(new MenuListener());
		// keggItem.setActionCommand("kegg settings");

		// brendaItem = new JMenuItem("BRENDA Connection");
		// brendaItem.addActionListener(new MenuListener());
		// brendaItem.setActionCommand("brenda settings");
		//
		// dawisItem = new JMenuItem("DAWIS Connection");
		// dawisItem.addActionListener(new MenuListener());
		// dawisItem.setActionCommand("dawis settings");
		//
		// ppiItem = new JMenuItem("PPI Connection");
		// ppiItem.addActionListener(new MenuListener());
		// ppiItem.setActionCommand("ppi settings");

		interaction = new JMenuItem("User interaction");
		interaction.addActionListener(new MenuListener());
		interaction.setActionCommand("interaction");

		internet = new JMenuItem("Internet Connection");
		internet.addActionListener(new MenuListener());
		internet.setActionCommand("internet");

		graphAlignment = new JMenuItem("Graph Alignment");
		graphAlignment.addActionListener(new MenuListener());
		graphAlignment.setActionCommand("graphAlignemnt");

		mathLaw = new JMenuItem("Generate Random Graph");
		mathLaw.addActionListener(new MenuListener());
		mathLaw.setActionCommand("mathGraph");

		biGraph = new JMenuItem("Generate Bipartite Graph");
		biGraph.addActionListener(new MenuListener());
		biGraph.setActionCommand("biGraph");

		regularGraph = new JMenuItem("Generate Regular Graph");
		regularGraph.addActionListener(new MenuListener());
		regularGraph.setActionCommand("regularGraph");

		connectedGraph = new JMenuItem("Generate Connected Graph");
		connectedGraph.addActionListener(new MenuListener());
		connectedGraph.setActionCommand("connectedGraph");

		hamiltonGraph = new JMenuItem("Generate Hamilton Graph");
		hamiltonGraph.addActionListener(new MenuListener());
		hamiltonGraph.setActionCommand("hamiltonGraph");

		graphSettings = new JMenuItem("Graph Settings");
		graphSettings.addActionListener(new MenuListener());
		graphSettings.setActionCommand("graphSettings");

		resolveReferences = new JMenuItem("Resolve Reverences");
		resolveReferences.addActionListener(new MenuListener());
		resolveReferences.setActionCommand("resolveReferences");

		help.add(interaction);

		// about item is allready present on mac osx
		if (!application.isMac()) {
			about = new JMenuItem("About");
			about.addActionListener(new MenuListener());
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
		//file.add(sessionID);
		file.add(new JSeparator());
		file.add(saveNetwork);
		file.add(saveNetworkAs);
		file.add(savePicture);
		file.add(printPicture);
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
			exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
					MENUSHORTCUT));
			exit.addActionListener(new MenuListener());
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

		// snapshot.add(savePicture);
		// snapshot.add(printPicture);

		math.add(generateGraph);
		math.add(phosphoImport);
		math.add(mirnaTest);
		math.add(shake);
		//math.add(visualAnalysis);

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
		//graph.add(centerGraph);
		//graph.add(phosphoImport);

		// experiments.add(fabricio);
		// TODO
		testPInvariant = new JMenuItem("Test P Invariante");
		testPInvariant.addActionListener(new MenuListener());
		testPInvariant.setActionCommand("openTestP");
		testTInvariant = new JMenuItem("Test T Invariante");
		testTInvariant.addActionListener(new MenuListener());
		testTInvariant.setActionCommand("openTestT");
		cov = new JMenuItem("Cov");
		cov.addActionListener(new MenuListener());
		cov.setActionCommand("openCov");
		covreach = new JMenuItem("Cov/Reach Graph");
		covreach.addActionListener(new MenuListener());
		covreach.setActionCommand("createCov");
		modellicaResult = new JMenuItem("Load Modellica Result");
		modellicaResult.addActionListener(new MenuListener());
		modellicaResult.setActionCommand("loadModResult");
		simulate = new JMenuItem("Simulate");
		simulate.addActionListener(new MenuListener());
		simulate.setActionCommand("simulate");
		
		createDoc = new JMenuItem("Create Documentation");
		createDoc.addActionListener(new MenuListener());
		createDoc.setActionCommand("createDoc");
		
		editPNelements = new JMenuItem("Edit PN-Elements");
		editPNelements.addActionListener(new MenuListener());
		editPNelements.setActionCommand("editElements");
		convertPetriNet = new JMenuItem("Convert Graph into Petri-Net");
		convertPetriNet.addActionListener(new MenuListener());
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
		//bar.add(graph);
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
		saveNetwork.setEnabled(true);
		openNetwork.setEnabled(true);
		closeAllNetworks.setEnabled(true);
		closeNetwork.setEnabled(true);
		savePicture.setEnabled(true);
		printPicture.setEnabled(true);
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
		saveNetwork.setEnabled(false);
		closeAllNetworks.setEnabled(false);
		closeNetwork.setEnabled(false);
		savePicture.setEnabled(false);
		printPicture.setEnabled(false);
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
