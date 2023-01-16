package graph.gui;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import gui.eventhandlers.PopUpListener;

public class GraphPopUp {

	private JPopupMenu popup = new JPopupMenu();

	public GraphPopUp() {

		JLabel title = new JLabel("Graph Layouts");
		JPanel titlePanel = new JPanel();
		titlePanel.setBackground(new Color(200, 200, 250));
		titlePanel.add(title); // adds to center of panel's default
								// BorderLayout.

		//JMenu graph = new JMenu("Graph");
		//JMenu layout = new JMenu("Graph Layouts");

		JMenuItem copySelection = new JMenuItem("copy");
		JMenuItem cutSelection = new JMenuItem("cut");
		JMenuItem pasteselection = new JMenuItem("paste");
		JMenuItem deleteSelection = new JMenuItem("delete");
		
		JMenuItem openPathway = new JMenuItem("Open Pathway as Sub-Pathway");
		JMenuItem openPathwayTab = new JMenuItem("Open Pathway in new Tab");
		JMenu openPathwayMenu = new JMenu("Open Pathway");		
		
		JMenuItem returnToParent =new JMenuItem("Return to Parent-Pathway");
		
		JMenuItem graphPicture = new JMenuItem("Save picture");
		
		JMenuItem centerGraph = new JMenuItem("center graph");
		JMenuItem springLayout = new JMenuItem("Spring Layout");
		JMenuItem kkLayout = new JMenuItem("KK Layout");
		JMenuItem frLayout = new JMenuItem("FR Layout");
		JMenuItem circleLayout = new JMenuItem("Circle Layout");
		JMenuItem gemLayout = new JMenuItem("GEM-Layout");
		JMenuItem isomLayout = new JMenuItem("ISOM Layout");
		JMenuItem mdLayout = new JMenuItem("MDLayout");

		JMenuItem cancel = new JMenuItem("cancel");

		JMenuItem keggSearch = new JMenuItem("Kegg Search");
		JMenuItem brendaSearch = new JMenuItem("Brenda Search");

		copySelection.setActionCommand("copy");
		copySelection.addActionListener(new PopUpListener());

		cutSelection.setActionCommand("cut");
		cutSelection.addActionListener(new PopUpListener());

		pasteselection.setActionCommand("paste");
		pasteselection.addActionListener(new PopUpListener());

		deleteSelection.setActionCommand("delete");
		deleteSelection.addActionListener(new PopUpListener());

		centerGraph.setActionCommand("center");
		centerGraph.addActionListener(new PopUpListener());

		springLayout.setActionCommand("springLayout");
		springLayout.addActionListener(new PopUpListener());

		kkLayout.setActionCommand("kkLayout");
		kkLayout.addActionListener(new PopUpListener());

		frLayout.setActionCommand("frLayout");
		frLayout.addActionListener(new PopUpListener());

		circleLayout.setActionCommand("circleLayout");
		circleLayout.addActionListener(new PopUpListener());

		gemLayout.setActionCommand("gemLayout");
		gemLayout.addActionListener(new PopUpListener());

		isomLayout.setActionCommand("isomLayout");
		isomLayout.addActionListener(new PopUpListener());

		mdLayout.addActionListener(new PopUpListener());

		keggSearch.setActionCommand("keggSearch");
		keggSearch.addActionListener(new PopUpListener());

		brendaSearch.setActionCommand("brendaSearch");
		brendaSearch.addActionListener(new PopUpListener());

		openPathway.setActionCommand("openPathway");
		openPathway.addActionListener(new PopUpListener());
		
		openPathwayTab.setActionCommand("openPathwayTab");
		openPathwayTab.addActionListener(new PopUpListener());
		
		returnToParent.setActionCommand("returnToParent");
		returnToParent.addActionListener(new PopUpListener());
		
		graphPicture.setActionCommand("graphPicture");
		graphPicture.addActionListener(new PopUpListener());
		
		openPathwayMenu.add(openPathway);
		openPathwayMenu.add(openPathwayTab);
		
		// graph.add(centerGraph);

//		popup.add(titlePanel);
//		popup.add(new JSeparator());
//		popup.add(circleLayout);
//		popup.add(gemLayout);
//		popup.add(frLayout);
//		popup.add(isomLayout);
//		popup.add(kkLayout);
//		popup.add(springLayout);
		// popup.add(mdLayout);


		popup.add(openPathwayMenu);
		popup.add(returnToParent);
		popup.add(new JSeparator());
		popup.add(graphPicture);
		popup.add(new JSeparator());
		popup.add(copySelection);
		popup.add(cutSelection);
		popup.add(pasteselection);
		popup.add(deleteSelection);
		
		// popup.add(new JSeparator());
		// popup.add(layout);
		// popup.add(graph);
		popup.add(new JSeparator());
		popup.add(keggSearch);
		popup.add(brendaSearch);
		popup.add(new JSeparator());
		popup.add(cancel);

	}

	public JPopupMenu returnPopUp() {
		return popup;
	}
}
