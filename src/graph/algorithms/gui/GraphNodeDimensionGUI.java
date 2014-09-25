package graph.algorithms.gui;

import graph.GraphInstance;
import graph.algorithms.NetworkProperties;
import graph.algorithms.NodeAttributeNames;

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
	private String[] algorithmNames = { "None","Node Degree", "Neighbor Degree", "Clique", "Assessment*" };
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
				itn = c.getPathway().getAllNodes().iterator();
				while(itn.hasNext()){
					bna = itn.next();
					bna.setNodesize(1.0d);
				}
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
			case 4:
				
				//MARTIN Combination of topological values:
				//Node degree, Neighbor degree, Cycles and Cliques
				ratings = new Hashtable<>();
				
				c = new NetworkProperties();
				itn = c.getPathway().getAllNodes().iterator();
				

				

				double assessment = 0d,
						nodedegree[]= {0d,Double.MAX_VALUE,Double.MIN_VALUE},
						neighbordegree[] = {0d,Double.MAX_VALUE,Double.MIN_VALUE},
						cycles[] = {0d,Double.MAX_VALUE,Double.MIN_VALUE},
						cliques[] = {0d,Double.MAX_VALUE,Double.MIN_VALUE};
				
				//get min and max values
				while(itn.hasNext()){				
					bna = itn.next();
					neighbordegree[0] = bna.getNodeAttributeByName(NodeAttributeNames.NEIGHBOR_DEGREE).getDoublevalue(); 
					nodedegree[0] = bna.getNodeAttributeByName(NodeAttributeNames.NODE_DEGREE).getDoublevalue();
					if(bna.getNodeAttributeByName(NodeAttributeNames.CYCLES)!=null)
						cycles[0] = bna.getNodeAttributeByName(NodeAttributeNames.CYCLES).getDoublevalue();
					if(bna.getNodeAttributeByName(NodeAttributeNames.CLIQUES)!=null)
						cliques[0] = bna.getNodeAttributeByName(NodeAttributeNames.CLIQUES).getDoublevalue();
					
					if(neighbordegree[0]<neighbordegree[1])
						neighbordegree[1]=neighbordegree[0];
					if(neighbordegree[0]>neighbordegree[2])
						neighbordegree[2]=neighbordegree[0];
					
					if(nodedegree[0]<nodedegree[1])
						nodedegree[1]=nodedegree[0];
					if(nodedegree[0]>nodedegree[2])
						nodedegree[2]=nodedegree[0];
					
					if(cycles[0]<cycles[1])
						cycles[1]=cycles[0];
					if(cycles[0]>cycles[2])
						cycles[2]=cycles[0];
					
					if(cliques[0]<cliques[1])
						cliques[1]=cliques[0];
					if(cliques[0]>cliques[2])
						cliques[2]=cliques[0];
					
				}
				
				
				
				
				
				itn = c.getPathway().getAllNodes().iterator();
				while(itn.hasNext()){
					assessment = 1d;
					
					bna = itn.next();
					neighbordegree[0] = bna.getNodeAttributeByName(NodeAttributeNames.NEIGHBOR_DEGREE).getDoublevalue(); 
					nodedegree[0] = bna.getNodeAttributeByName(NodeAttributeNames.NODE_DEGREE).getDoublevalue();
					if(bna.getNodeAttributeByName(NodeAttributeNames.CYCLES)!=null)
						cycles[0] = bna.getNodeAttributeByName(NodeAttributeNames.CYCLES).getDoublevalue();
					if(bna.getNodeAttributeByName(NodeAttributeNames.CLIQUES)!=null)
						cliques[0] = bna.getNodeAttributeByName(NodeAttributeNames.CLIQUES).getDoublevalue();
					
					//Linear normalization:
					// x-min / max-min
					assessment += 10d*(neighbordegree[0]-neighbordegree[1])/ (neighbordegree[2]-neighbordegree[1]);
					assessment += 10d*(nodedegree[0]-nodedegree[1])/ (nodedegree[2]-nodedegree[1]);
					assessment += 40d*(cycles[0]-cycles[1])/ (cycles[2]-cycles[1]);
					assessment += 40d*(cliques[0]-cliques[1])/ (cliques[2]-cliques[1]);
					
					System.out.println(assessment);
					ratings.put(bna, assessment);					
				}
				
				//calclulate Weighting
				transformRatingToWeighting((double)nodesizefromspinner.getValue(),(double)nodesizetospinner.getValue());		
				
				GraphInstance.getMyGraph().getVisualizationViewer().repaint();				
				
				
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



