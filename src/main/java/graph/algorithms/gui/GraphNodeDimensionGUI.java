package graph.algorithms.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import biologicalObjects.nodes.BiologicalNodeAbstract;
import graph.GraphInstance;
import graph.algorithms.NetworkProperties;
import graph.algorithms.centralities.BetweennessCentrality;
import net.miginfocom.swing.MigLayout;

public class GraphNodeDimensionGUI implements ActionListener {

	private JPanel p = new JPanel();
	private boolean emptyPane = true;

	private JComboBox<String> chooseAlgorithm;
	//private JButton weight;
	private String[] algorithmNames = { "None","Node Degree","Betweenness"};
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

	public GraphNodeDimensionGUI() {

	}

	private void updateWindow() {
		
		chooseAlgorithm = new JComboBox<String>(algorithmNames);
		chooseAlgorithm.setActionCommand("algorithm");
		chooseAlgorithm.addActionListener(this);
		
		
		frommodel = new SpinnerNumberModel(1.0d, 0.0d, 20.0d, 0.1d);
		tomodel = new SpinnerNumberModel(4.0d, 0.0d, 20.0d, 0.1d);
		
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

	private void getNodeDegreeRatings() {
		c = new NetworkProperties();
		itn = c.getPathway().getAllGraphNodes().iterator();
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
	
	private void getNodeBetweennesCentrality(){
		c = new NetworkProperties();
		ratings = new Hashtable<BiologicalNodeAbstract,Double>();
		minvalue = Double.MAX_VALUE;
		maxvalue = Double.MIN_NORMAL;
		int[] nodei, nodej, tmpi, tmpj;
		nodei = c.getNodeI();
		nodej = c.getNodeJ();
		
//		System.out.println(Arrays.toString(nodei));
//
//		System.out.println(Arrays.toString(nodej));
		
		//remove unused element 0 from old data structure and create undirected graph
		//(each edge has to be inserted twice)
		tmpi = new int[(nodei.length-1)*2];
		int tmppos=0;
		for(int i = 1; i<nodei.length; i++){
			tmpi[tmppos] = nodei[i]-1;
			tmppos++;
			tmpi[tmppos] = nodej[i]-1;
			tmppos++;			
		}

		tmpj = new int[(nodej.length-1)*2];
		tmppos=0;
		for(int i = 1; i<nodej.length; i++){
			tmpj[tmppos] = nodej[i]-1;
			tmppos++;
			tmpj[tmppos] = nodei[i]-1;
			tmppos++;			
		}
		
		//overwrite old data structure
		nodei = tmpi;
		nodej = tmpj;
		
//		System.out.println(Arrays.toString(nodei));
//
//		System.out.println(Arrays.toString(nodej));
		
		//invoke betweenness-centrality
		BetweennessCentrality b = new BetweennessCentrality(nodei, nodej);
		try {
			BetweennessCentrality.GraphCentrality g = b.calcCentrality();
			for(BetweennessCentrality.Vertex v : g.vertices){
//				System.out.println(v.id+"  ");
				if(v.timesWalkedOver.doubleValue() < minvalue)
					minvalue=v.timesWalkedOver.doubleValue();
				if(v.timesWalkedOver.doubleValue() > maxvalue)
					maxvalue=v.timesWalkedOver.doubleValue();
				
				ratings.put(c.getNodeAssignmentBackwards(v.id), v.timesWalkedOver.doubleValue());
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		//DEBUG
//		System.out.println(ratings.toString());
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
			bna.setNodeSize(currentvalue);
						
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
				//Standard Nodesize to 1
				GraphInstance.getMyGraph().getAllVertices().stream().
					forEach(bna -> bna.setNodeSize(1.0d));
				nodesizefromspinner.setEnabled(false);
				nodesizetospinner.setEnabled(false);
				resizebutton.setEnabled(false);
				GraphInstance.getMyGraph().getVisualizationViewer().repaint();
				
				break;
			case 1:
				//Node Degree rating				
				getNodeDegreeRatings();
				
				//calclulate Weighting
				transformRatingToWeighting((double)nodesizefromspinner.getValue(),(double)nodesizetospinner.getValue());		
				
				GraphInstance.getMyGraph().getVisualizationViewer().repaint();
				
				break;
			case 2:
				//Betweenness centrality weighting				
				getNodeBetweennesCentrality();			
				
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



