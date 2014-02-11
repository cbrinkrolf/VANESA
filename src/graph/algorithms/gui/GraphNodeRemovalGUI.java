package graph.algorithms.gui;

import graph.GraphInstance;
import graph.algorithms.NetworkProperties;
import graph.jung.classes.MyGraph;
import gui.MainWindow;
import gui.MainWindowSingelton;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;


import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;

import net.infonode.tabbedpanel.titledtab.TitledTab;
import net.miginfocom.swing.MigLayout;
import biologicalElements.GraphElementAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class GraphNodeRemovalGUI implements ActionListener {

	JPanel p = new JPanel();
	GraphElementAbstract ab;
	GraphInstance graphInstance;
	boolean emptyPane = true;

	private JComboBox<String> chooseAlgorithm;
	private JButton removebutton;
	private String[] algorithmNames = { "Choose Algorithm","Node Degree", "Neighbor Degree", "Clique" };
	private int currentalgorithmindex = 0;
	private JPanel valuesfromto, valuesminmax;
	private JSpinner fromspinner, tospinner;
	private JLabel minvaluelabel, maxvaluelabel;
	private double minvalue, maxvalue, removefrom, removeto, currentvalue;
	
	MigLayout layout;

	//Removal variables
	private NetworkProperties c;	
	private MainWindow mw;
	private MyGraph mg;
	private Iterator<BiologicalNodeAbstract> itn;
	private Iterator<Map.Entry<BiologicalNodeAbstract,Double>> it;
	private Map.Entry<BiologicalNodeAbstract,Double> nodevaluesentry;
	private Hashtable<BiologicalNodeAbstract,Double> nodevalues;
	private HashSet<BiologicalNodeAbstract> removals;
	private BiologicalNodeAbstract bna;
	private SpinnerNumberModel modelremovenodesfrom;
	private SpinnerNumberModel modelremovenodesto;
	
	private TitledTab tab;
	
	public GraphNodeRemovalGUI() {

	}

	private void updateWindow() {
		
		chooseAlgorithm = new JComboBox<String>(algorithmNames);
		chooseAlgorithm.setActionCommand("algorithm");
		chooseAlgorithm.addActionListener(this);

		valuesfromto = new JPanel(new MigLayout("","[][][]",""));
		
		modelremovenodesfrom  = new SpinnerNumberModel(0.0d, 0.0d, 1.0d, 1.0d);		
		modelremovenodesto  = new SpinnerNumberModel(0.0d, 0.0d, 1.0d, 1.0d);
		
		fromspinner = new JSpinner(modelremovenodesfrom);
		tospinner = new JSpinner(modelremovenodesto);
		fromspinner.setEnabled(false);
		tospinner.setEnabled(false);
		
		valuesfromto.add(fromspinner);
		valuesfromto.add(new JLabel(" to "));
		valuesfromto.add(tospinner);
		
		

		
		removebutton = new JButton("remove");
		removebutton.setActionCommand("remove");
		removebutton.addActionListener(this);
		removebutton.setEnabled(false);
		
		valuesminmax = new JPanel(new MigLayout("","[][grow]",""));
		minvaluelabel = new JLabel("NaN");
		maxvaluelabel = new JLabel("NaN");
		valuesminmax.add(new JLabel("Min:\t"));
		valuesminmax.add(minvaluelabel,"wrap");
		valuesminmax.add(new JLabel("Max:\t"));
		valuesminmax.add(maxvaluelabel,"wrap");
		valuesminmax.setVisible(false);

		layout = new MigLayout("", "[][grow]", "");
		p.setLayout(layout);	
		p.add(new JLabel("Algorithm"), "wrap");
		p.add(chooseAlgorithm, "wrap");
		p.add(new JLabel("Remove Nodes with values:"),"wrap");
		p.add(valuesfromto, "span 2, wrap");
		p.add(removebutton,"span 2, align right, wrap");
		p.add(valuesminmax,"span 2, wrap");
		
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

		tab = new TitledTab("Node Removal", null, p, null);

		return tab;
	}
	
	private void resetRemovalInterface() {
		fromspinner.setEnabled(false);
		tospinner.setEnabled(false);
		valuesminmax.setVisible(false);
		removebutton.setEnabled(false);
		chooseAlgorithm.setSelectedIndex(0);
	}

	private void getNodeDegreeRatings() {
		c = new NetworkProperties();
		itn = c.getPathway().getAllNodes().iterator();
		nodevalues = new Hashtable<BiologicalNodeAbstract,Double>();
		minvalue = Double.MAX_VALUE;
		maxvalue = Double.MIN_NORMAL;
		double degree;
		while (itn.hasNext()) {			
			bna = itn.next();
			degree = (double) c.getNodeDegree(c.getNodeAssignment(bna));
			if (degree > maxvalue)
				maxvalue = degree;
			if (degree < minvalue)
				minvalue = degree;
			nodevalues.put(bna, degree);
		}
	}


	public void actionPerformed(ActionEvent e) {

		String command = e.getActionCommand();
		currentalgorithmindex = chooseAlgorithm.getSelectedIndex();
		if(currentalgorithmindex > 0 && command.equals("algorithm")){
			//do calculations
			switch (currentalgorithmindex) {
			case 1:
				System.out.println("Node Removal 1: Node Degree");
				//Node Degree rating
				getNodeDegreeRatings();
				
				//Post min/max  values:
				modelremovenodesfrom = new SpinnerNumberModel(minvalue, minvalue, maxvalue, 1.0d);
				modelremovenodesto = new SpinnerNumberModel(maxvalue, minvalue, maxvalue, 1.0d);				
				
				fromspinner.setModel(modelremovenodesfrom);
				tospinner.setModel(modelremovenodesto);
				
				minvaluelabel.setText(""+minvalue);
				
				maxvaluelabel.setText(""+maxvalue);
				tospinner.setValue(maxvalue);
				
				break;
			case 2:
				System.out.println("Node Removal 2: Neighbor Degree");
				//TODO: Neighbor Degree rating
				break;
			case 3:
				System.out.println("Node Removal 3: Clique");
				//TODO: Clique rating
				break;
				
			default:
				break;
			}
			
			//Enable further Gui elements
			fromspinner.setEnabled(true);
			tospinner.setEnabled(true);
			removebutton.setEnabled(true);
			valuesminmax.setVisible(true);
		}
		else if(command.equals("remove")){
			//get Values from Text fields
			try{
				removefrom = (double) fromspinner.getValue();
				removeto = (double) tospinner.getValue();
		
				//remove specified Nodes 
				removals = new HashSet<BiologicalNodeAbstract>();
				it = nodevalues.entrySet().iterator();
				
				while (it.hasNext()) {
					nodevaluesentry = it.next();
					bna = nodevaluesentry.getKey();
					currentvalue = nodevaluesentry.getValue();
					
					if(currentvalue >= removefrom && currentvalue <=removeto)
						removals.add(bna);
				}		
				
				mg = c.getPathway().getGraph();
				mw = MainWindowSingelton.getInstance();
				mg.lockVertices();
				itn = removals.iterator();
				while (itn.hasNext()) {
					mg.removeVertex(itn.next());				
				}			
				mw.updateElementTree();
				mw.updateFilterView();
				mw.updatePathwayTree();
				mg.unlockVertices();	
				
				
				GraphInstance.getMyGraph().changeToGEMLayout();
				GraphInstance.getMyGraph().getVisualizationViewer().repaint();
				
				resetRemovalInterface();
				System.out.println(" Node Removal: "+removals.size()+" Nodes Removed.");
			}catch(NumberFormatException nfe){
				//TODO BEHANDLUNG von Eingabe, bzw. Formatted text imput
				System.out.println("Input format not allowed");
			}
			
		}
	
		
	}



}
