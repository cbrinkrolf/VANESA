package graph.algorithms.gui;

import graph.GraphInstance;
import graph.algorithms.NetworkProperties;

import java.awt.Dimension;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;


import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import net.infonode.tabbedpanel.titledtab.TitledTab;
import net.miginfocom.swing.MigLayout;
import biologicalElements.GraphElementAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class GraphNodeDimensionGUI implements ActionListener {

	JPanel p = new JPanel();
	GraphElementAbstract ab;
	GraphInstance graphInstance;
	boolean emptyPane = true;

	private JComboBox<String> chooseAlgorithm;
	//private JButton weight;
	private String[] algorithmNames = { "None","Node Degree", "Neighbor Degree", "Clique" };
	private int currentalgorithmindex = 0;
	private JSpinner nodesizefromspinner, nodesizetospinner;
	private SpinnerNumberModel frommodel, tomodel;
	private JPanel valuesfromto;	
	
	MigLayout layout;

	//Wighting variables
	private NetworkProperties c;	
	private Iterator<BiologicalNodeAbstract> itn;
	private BiologicalNodeAbstract bna;
	private Hashtable<BiologicalNodeAbstract, Double> ratings;
	private Hashtable<BiologicalNodeAbstract, Double> weightings;
	private Map.Entry<BiologicalNodeAbstract,Double> ratingsentry;
	private Iterator<Map.Entry<BiologicalNodeAbstract,Double>> it;
	private double minvalue, maxvalue, currentvalue;
	private JButton resizebutton;

	private TitledTab tab;
	
	public GraphNodeDimensionGUI() {

	}

	private void updateWindow() {
		
		chooseAlgorithm = new JComboBox<String>(algorithmNames);
		chooseAlgorithm.setActionCommand("algorithm");
		chooseAlgorithm.addActionListener(this);
		
		
		frommodel = new SpinnerNumberModel(1.0d, 1.0d, 8.0d, 1.0d);
		tomodel = new SpinnerNumberModel(4.0d, 1.0d, 8.0d, 1.0d);
		
		nodesizefromspinner = new JSpinner(frommodel);
		nodesizetospinner = new JSpinner(tomodel);
		nodesizefromspinner.setEnabled(false);
		nodesizetospinner.setEnabled(false);
	
		valuesfromto = new JPanel();
		valuesfromto.add(nodesizefromspinner);
		valuesfromto.add(new JLabel(" to "));
		valuesfromto.add(nodesizetospinner);
		
		resizebutton = new JButton("resize");
		resizebutton.addActionListener(this);
		resizebutton.setActionCommand("resize");
		resizebutton.setEnabled(false);
		
		

		layout = new MigLayout("", "[][grow]", "");
		p.setLayout(layout);	
		p.add(new JLabel("Algorithm"), "wrap");
		p.add(chooseAlgorithm, "wrap");
		p.add(valuesfromto);
		p.add(resizebutton);
		
		
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

		tab = new TitledTab("Node Weighting", null, p, null);

		return tab;
	}
	
	
	private void getNodeDegreeRatings() {
		c = new NetworkProperties();
		itn = c.getPathway().getAllNodes().iterator();
		ratings = new Hashtable<BiologicalNodeAbstract,Double>();
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
			ratings.put(bna, degree);
		}
	}
	
	private void transformRatingToWeighting(double minweight , double maxweight) {

		it = ratings.entrySet().iterator();
		weightings = new Hashtable<BiologicalNodeAbstract, Double>();
		while (it.hasNext()) {
			ratingsentry = it.next();
			bna = ratingsentry.getKey();
			currentvalue = ratingsentry.getValue();
			
			//apply formula
			currentvalue = ((maxweight-minweight)/(maxvalue-minvalue))*currentvalue;
			currentvalue+=minweight;
			
			weightings.put(bna, currentvalue);
			
			//set Node Size directly
			bna.setNodesize(currentvalue);
						
		}	
		
	}

	public void actionPerformed(ActionEvent e) {

		String command = e.getActionCommand();
		currentalgorithmindex = chooseAlgorithm.getSelectedIndex();
		if(command.equals("algorithm")){
			resizebutton.setEnabled(true);
			nodesizefromspinner.setEnabled(true);
			nodesizetospinner.setEnabled(true);
			c = new NetworkProperties();
			//do calculations
			switch (currentalgorithmindex) {
			case 0:
				//DEBUG
				//System.out.println("Node Weighting 0: None");
				//Standard Nodesize to 1
				GraphInstance.getMyGraph().getAllVertices().stream().
					forEach(bna -> bna.setNodesize(1.0d));
				nodesizefromspinner.setEnabled(false);
				nodesizetospinner.setEnabled(false);
				resizebutton.setEnabled(false);
				GraphInstance.getMyGraph().getVisualizationViewer().repaint();
				
				break;
			case 1:
				//DEBUG
				//System.out.println("Node Weighting 1: Node Degree");
				//Node Degree rating				
				getNodeDegreeRatings();
				
				//calclulate Weighting
				transformRatingToWeighting((double)nodesizefromspinner.getValue(),(double)nodesizetospinner.getValue());		
				
				GraphInstance.getMyGraph().getVisualizationViewer().repaint();
				
				break;
			case 2:
				//DEBUG
				//System.out.println("Node Weighting 2: Neighbor Degree");
				ratings = c.averageNeighbourDegreeTable();

				//calclulate Weighting
				transformRatingToWeighting((double)nodesizefromspinner.getValue(),(double)nodesizetospinner.getValue());		
				
				GraphInstance.getMyGraph().getVisualizationViewer().repaint();

				break;
			case 3:
				//DEBUG
				//System.out.println("Node Weighting 3: Clique");
				//MARTIN: Clique dimension rating
				break;
				
			default:
				break;
			}
		}	
		else if(command.equals("resize")){
			
			transformRatingToWeighting((double)nodesizefromspinner.getValue(),(double)nodesizetospinner.getValue());		
			
			GraphInstance.getMyGraph().getVisualizationViewer().repaint();
			
		}
	}


}



