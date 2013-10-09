package graph.algorithms.gui;


import graph.GraphInstance;
import graph.algorithms.Connectness;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import net.infonode.tabbedpanel.titledtab.TitledTab;
import net.miginfocom.swing.MigLayout;
import biologicalElements.GraphElementAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class GraphColoringGUI implements ActionListener {

	JPanel p = new JPanel();
	GraphElementAbstract ab;
	GraphInstance graphInstance;
	boolean emptyPane = true;

	private JComboBox<String> chooseAlgorithm;
	private JButton colorizebutton;

	// private JButton calculate;

	private String[] algorithmNames = { "Node Degree", "Neighbor Degree", "Clique" };
	private int currentalgorithmindex = 0;
	private String[] colorrangenames = {"bluesea","skyline","darkmiddle","darkleftmiddle","rainbow"};
	private String[] iconpaths = {"pictures/icon_colorrange_bluesea.png","pictures/icon_colorrange_skyline.png",
			"pictures/icon_colorrange_darkmiddle.png","pictures/icon_colorrange_dark.png","pictures/icon_colorrange_rainbow.png"};
	private String currentimagepath = iconpaths[0];
	private JRadioButton[] colorrangeradio = new JRadioButton[colorrangenames.length];
	private GraphColorizer gc;
	private JCheckBox logview;
	
	//coloring variables
	private Connectness c;	
	private Iterator<BiologicalNodeAbstract> itn;
	private Hashtable<BiologicalNodeAbstract,Double> coloring;
	private BiologicalNodeAbstract bna;
	
	private TitledTab tab;
	
	public GraphColoringGUI() {

	}

	private void updateWindow() {

		ButtonGroup bg = new ButtonGroup();

		chooseAlgorithm = new JComboBox<String>(algorithmNames);
		chooseAlgorithm.setActionCommand("algorithm");
		chooseAlgorithm.addActionListener(this);
	
		for (int i = 0; i < colorrangenames.length; i++) {
			colorrangeradio[i] = new JRadioButton(colorrangenames[i]);
			colorrangeradio[i].setActionCommand(iconpaths[i]);
			colorrangeradio[i].addActionListener(this);
			bg.add(colorrangeradio[i]);
			
		}				
		colorizebutton = new JButton("color selection");
		colorizebutton.setActionCommand("colorize");
		colorizebutton.addActionListener(this);
		colorizebutton.setEnabled(false);

		MigLayout layout = new MigLayout("", "[][grow]", "");
		p.setLayout(layout);	
		p.add(new JLabel("Algorithm"), "wrap");
		p.add(chooseAlgorithm, "wrap");
		p.add(new JLabel("Color Range"),"wrap");
		for (int i = 0; i < colorrangenames.length; i++) {
			p.add(colorrangeradio[i]);
			p.add(new JLabel(new ImageIcon(iconpaths[i])),"wrap");
		}
		
		logview = new JCheckBox("Data in log(10)");
		logview.setSelected(false);
		logview.setActionCommand("logview");
		logview.addActionListener(this);
		logview.setEnabled(false);
		
		p.add(logview);
		p.add(colorizebutton, "span 2,align right, wrap");
		
	}

	public void recolorGraph() {

		c = new Connectness();	
		itn = c.getPathway().getAllNodes().iterator();
		coloring = new Hashtable<BiologicalNodeAbstract,Double>();
		
		if (currentalgorithmindex == 0) { //node degree
			while (itn.hasNext()) {			
				bna = itn.next();
				coloring.put(bna,(double)c.getNodeDegree(c.getNodeAssignment(bna)));
			}			
		}else if(currentalgorithmindex == 1) {//neighbor degree
			coloring = c.averageNeighbourDegreeTable();
						
		}else if(currentalgorithmindex == 2) {//cliques
			while (itn.hasNext()) {			
				bna = itn.next();
			}			
		}		
		
		
		gc = new GraphColorizer(coloring, currentimagepath, logview.isSelected());	
		//recolor button enable after first Coloring, logview enabled
		colorizebutton.setEnabled(true);
		logview.setEnabled(true);
	}

	
	public void revalidateView() {

		graphInstance = new GraphInstance();

		if (emptyPane) {
			updateWindow();
			p.repaint();
			p.revalidate();
			emptyPane = false;
		} else {
			p.removeAll();
			updateWindow();
			p.repaint();
			p.revalidate();
		 p.setVisible(true);

		}
	}

	public void removeAllElements() {
		emptyPane = true;
	}

	public JPanel getPanel() {
		p.setVisible(false);
		return p;
	}

	public TitledTab getTitledTab() {

		tab = new TitledTab("Coloring", null, p, null);

		return tab;
	}

	public void actionPerformed(ActionEvent e) {

		String command = e.getActionCommand();
	
		if ("colorize".equals(command)) {
			recolorGraph();
			GraphInstance.getMyGraph().getVisualizationViewer().repaint();
		}

		else if ("algorithm".equals(command)){
			currentalgorithmindex = chooseAlgorithm.getSelectedIndex();
			for (int i = 0; i < colorrangeradio.length; i++) {
				if (colorrangeradio[i].isSelected()) {
					recolorGraph();
					break;
				}
			}			
			GraphInstance.getMyGraph().getVisualizationViewer().repaint();
		}
		
		else if("logview".equals(command)){
			recolorGraph();
			GraphInstance.getMyGraph().getVisualizationViewer().repaint();
		}
		//get proper icon path
		for (int i = 0; i < colorrangenames.length; i++) {
			if (iconpaths[i].equals(command)){
				currentimagepath=iconpaths[i];
				recolorGraph();
				//repaint, damit Farben auch angezeigt werden
					GraphInstance.getMyGraph().getVisualizationViewer().repaint();			
				break;
			}
			
		}
	}
}
