package graph.algorithms.gui;

import graph.GraphInstance;
import graph.algorithms.Connectness;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;


import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

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
	
	MigLayout layout;

	//Wighting variables
	private Connectness c;	
	private Iterator<BiologicalNodeAbstract> itn;
	private BiologicalNodeAbstract bna;
	private Hashtable<BiologicalNodeAbstract, Double> weightings;
	private Map.Entry<BiologicalNodeAbstract,Double> weightingsentry;
	private Iterator<Map.Entry<BiologicalNodeAbstract,Double>> it;
	private double minvalue, maxvalue, currentvalue;
	

	private TitledTab tab;
	
	public GraphNodeDimensionGUI() {

	}

	private void updateWindow() {
		
		chooseAlgorithm = new JComboBox<String>(algorithmNames);
		chooseAlgorithm.setActionCommand("algorithm");
		chooseAlgorithm.addActionListener(this);

		layout = new MigLayout("", "[][grow]", "");
		p.setLayout(layout);	
		p.add(new JLabel("Algorithm"), "wrap");
		p.add(chooseAlgorithm, "wrap");
		
		
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
		c = new Connectness();
		itn = c.getPathway().getAllNodes().iterator();
		weightings = new Hashtable<BiologicalNodeAbstract,Double>();
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
			weightings.put(bna, degree);
		}
	}
	
	private void transformRatingToWeighting(double minweight , double maxweight) {

		it = weightings.entrySet().iterator();
		
		while (it.hasNext()) {
			weightingsentry = it.next();
			bna = weightingsentry.getKey();
			currentvalue = weightingsentry.getValue();
			
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
			c = new Connectness();
			//do calculations
			switch (currentalgorithmindex) {
			case 0:
				System.out.println("Node Weighting 0: None");
				//Standard Nodesize to 1
				itn = c.getPathway().getAllNodes().iterator();
				while(itn.hasNext()){
					bna = itn.next();
					bna.setNodesize(1.0d);
				}
				GraphInstance.getMyGraph().getVisualizationViewer().repaint();
				
				break;
			case 1:
				System.out.println("Node Weighting 1: Node Degree");
				//Node Degree rating				
				getNodeDegreeRatings();
				
				//calclulate Weighting
				transformRatingToWeighting(1.0d,4.0d);		
				
				GraphInstance.getMyGraph().getVisualizationViewer().repaint();
				
				break;
			case 2:
				System.out.println("Node Weighting 2: Neighbor Degree");
				//TODO: Neighbor Degree rating
				break;
			case 3:
				System.out.println("Node Weighting 3: Clique");
				//TODO: Clique rating
				break;
				
			default:
				break;
			}
		}		
	}


}



