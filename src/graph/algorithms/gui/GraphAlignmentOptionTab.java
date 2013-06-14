package graph.algorithms.gui;

import graph.GraphInstance;
import graph.algorithms.alignment.AdjacencyMatrix;
import graph.algorithms.alignment.GraphAlignmentAlgorithms;
import graph.algorithms.alignment.MNAlignerJava;
import graph.algorithms.alignment.MNAlignerRunOnR;
import graph.algorithms.alignment.SimilarityMatrix;
import graph.algorithms.alignment.SimpleAlignmentGraph;
import graph.algorithms.alignment.StartRserve;
import gui.ProgressBar;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

import biologicalElements.GraphElementAbstract;
import biologicalElements.Pathway;
import configurations.GraphAlignmentSettings;

public class GraphAlignmentOptionTab implements ActionListener {

	private JPanel p = new JPanel();
	GraphElementAbstract ab;
	GraphInstance graphInstance;
	boolean emptyPane = true;

	private JComboBox chooseSimFunction;
	private String[] simFunctionNames =
	{
			"BLAST Comparison",
			"Node Type Comparison",
			"Loading Similarity Matrix"
	};
	private JButton showSimilarityMatrixButton;
	private final String SIMILARITY_EVENT = "Show Similarity Matrix";
	private int selectedSimFunction;
	
	private double lambdaValue;
	private JSlider lambdaSlider;
	private JButton startAlignmentButton;
	private final String ALIGNMENT_EVENT = "Start Alignment";

	private JSlider edgeThresholdSlider;
	private double edgeThreshold;
	private JButton updateVisualization;
	private final String VISUALISATION_EVENT = "Update Visualization";

	private Pathway pathwayA, pathwayB;
	private SimilarityMatrix similarityMatrix;
	private AdjacencyMatrix graph_A, graph_B;
	
	private GraphAlignmentAlgorithms alignment;
	private ProgressBar bar;
	private SimpleAlignmentGraph alignmentGraph;
	private boolean alignmentReady, simMatrixReady;
	
	private String tabTitle;


	public GraphAlignmentOptionTab(Pathway pwA, Pathway pwB, String title) {

		this.pathwayA = pwA;
		this.pathwayB = pwB;
		this.tabTitle = title;
		this.selectedSimFunction = 0;
		
		this.graph_A = new AdjacencyMatrix(pwA);
		this.graph_B = new AdjacencyMatrix(pwB);
		
		lambdaValue = 0.5;
		edgeThreshold = 0.5;
		
		alignmentReady = simMatrixReady = false;

		similarityMatrix = new SimilarityMatrix(graph_A, graph_B, pathwayA, pathwayB);
		alignmentGraph = new SimpleAlignmentGraph(pathwayA, pathwayB, tabTitle);
		
		updateWindow();
		
		alignmentGraph.drawBasicGraphs();
	}

	public void updateWindow() {

		p.removeAll();
		MigLayout layout = new MigLayout("", "[][grow]", "");
		p.setLayout(layout);
		
		/*
		 * Similarity Options
		 */
		chooseSimFunction = new JComboBox(simFunctionNames);
		chooseSimFunction.setSelectedIndex(selectedSimFunction);
		chooseSimFunction.setActionCommand("simAction");
		chooseSimFunction.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				selectedSimFunction = chooseSimFunction.getSelectedIndex();
				updateSimMatrix();
			}
			
		});
		
		showSimilarityMatrixButton = new JButton(SIMILARITY_EVENT);
		showSimilarityMatrixButton.setToolTipText(SIMILARITY_EVENT);
		showSimilarityMatrixButton.setActionCommand(SIMILARITY_EVENT);
		showSimilarityMatrixButton.addActionListener(this);
		
		/*
		 * Algorithm Options 
		 */
		startAlignmentButton = new JButton(ALIGNMENT_EVENT);
		startAlignmentButton.setToolTipText(ALIGNMENT_EVENT);
		startAlignmentButton.setActionCommand(ALIGNMENT_EVENT);
		startAlignmentButton.addActionListener(this);

		double tmpLambda = lambdaValue * 100;
		lambdaSlider = new JSlider(0, 100, (int) tmpLambda);
		lambdaSlider.setMajorTickSpacing(10);
		lambdaSlider.setMinorTickSpacing(5);
		lambdaSlider.setPaintTicks(true);
		lambdaSlider.setPaintLabels(true);
//		lambdaSlider.setPaintTrack(true);
		lambdaSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				lambdaValue = ((double) lambdaSlider.getValue()) / 100;
			}
		});

		updateVisualization = new JButton(VISUALISATION_EVENT);
		updateVisualization.setToolTipText(VISUALISATION_EVENT);
		updateVisualization.setActionCommand(VISUALISATION_EVENT);
		updateVisualization.addActionListener(this);
		
		double tmpThreshold = edgeThreshold * 100;
		edgeThresholdSlider = new JSlider(0, 100, (int) tmpThreshold);
		edgeThresholdSlider.setMajorTickSpacing(10);
		edgeThresholdSlider.setMinorTickSpacing(5);
		edgeThresholdSlider.setPaintTicks(true);
		edgeThresholdSlider.setPaintLabels(true);
		edgeThresholdSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				edgeThreshold = ((double) edgeThresholdSlider.getValue()) / 100;
			}
		});
		
		class ColorScale extends JPanel {
			private static final long serialVersionUID = 1L;
			private Rectangle2D scale;
			int width, height;
			public ColorScale(){
				this.width = edgeThresholdSlider.getPreferredSize().width;
				this.height = 10;
				scale = new Rectangle(0,0,width, height);
				setPreferredSize(new Dimension(width, height));
			}
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g;
				
				GradientPaint gp = new GradientPaint(0, 0, new Color(0,0,255),
						width, 0, new Color(255,0,0),
						false
				);
				
				g2d.setPaint( gp );
				g2d.fill( scale );
				
				g2d.setPaint(Color.black);
				g2d.drawRect(0, 0, width-1, height-1);
			}
		}
		
		/*
		 * Build panel
		 */
		p.add(new JLabel("Algorithm Options"), "span 1");
		p.add(new JSeparator(), "span, growx, wrap 10");
		p.add(new JLabel("Similarity Function"));
		p.add(chooseSimFunction, "wrap");
		p.add(showSimilarityMatrixButton, "span, wrap 20px, align right");
		
		p.add(new JLabel("Lambda"));
		p.add(lambdaSlider, "wrap");
//		p.add(showSimilarityMatrixButton, "span, split2, align right");
		p.add(startAlignmentButton, "span, align right");
		
		p.add(new JLabel("Visualization Options"), "span 1");
		p.add(new JSeparator(), "span, growx, wrap 10");
		p.add(new JLabel("Edge Threshold"));
		p.add(edgeThresholdSlider, "wrap");
		p.add(new JLabel(""));
		p.add(new ColorScale(), "wrap");
		
//		p.add(showSimilarityMatrixButton, "span, split 2, align left");
//		p.add(new JLabel(""));
		p.add(updateVisualization, "span, align right");
		
		updateSimMatrix();
	}
	
	public void actionPerformed(ActionEvent e) {

		String event = e.getActionCommand();

		if (event.equals(SIMILARITY_EVENT)) {
			
			if (simMatrixReady) {
				similarityMatrix.visualizeSimilarityMatrix();
//				System.out.println("Similarity Matrix:");
//				System.out.println(this.similarityMatrix.getMatrix());
			}
			
		}else if (event.equals(ALIGNMENT_EVENT)) {

			updateVisualization.setEnabled(false);
			startAlignmentButton.setEnabled(false);
			showSimilarityMatrixButton.setEnabled(false);
			alignmentReady = false;
			final AlignmentTask doAlignment = new AlignmentTask();
			
			final Runnable showProgressBar = new Runnable(){
				public void run() {
			    	bar.init(100, "Building Alignment", true);
				}
			};
			Thread appThread = new Thread() {
			     @Override
				public void run() {
			    	 try {
			             SwingUtilities.invokeLater(showProgressBar);
			    		 doAlignment.execute();
			         }
			         catch (Exception e) {
			             e.printStackTrace();
			         }
			     }
			 };
			 bar = new ProgressBar(appThread);
			 bar.run();

		} else if (event.equals(VISUALISATION_EVENT)) {

			updateVisualization.setEnabled(false);
			startAlignmentButton.setEnabled(false);
			showSimilarityMatrixButton.setEnabled(false);
			
			alignmentGraph.setThreshold(edgeThreshold);
			alignmentGraph.removeAlignmentEdges();
			alignmentGraph.drawAlignmentGraph();
			
			updateButtons();
			
		}
		
	}
	
	private void updateButtons(){
		
    	if (simMatrixReady) {
			showSimilarityMatrixButton.setEnabled(true);
			startAlignmentButton.setEnabled(true);
    	}else{
    		showSimilarityMatrixButton.setEnabled(false);
			startAlignmentButton.setEnabled(false);
    	}
    	if (alignmentReady) {
			updateVisualization.setEnabled(true);
		}else{
			updateVisualization.setEnabled(false);
		}
		
	}

	public void revalidateView() {

		graphInstance = new GraphInstance();
		String pwName = graphInstance.getPathway().getName();

		if (pwName.startsWith("Alignment")) {
			if (emptyPane) {
//				updateWindow();
				p.repaint();
				p.revalidate();
				p.setVisible(true);
				emptyPane = false;
			} else {
				p.removeAll();
//				updateWindow();
				p.repaint();
				p.revalidate();
				//		p.setVisible(true);
			}
		}

	}

	public void removeAllElements() {
		emptyPane = true;
		//	p.removeAll();
		//	p.setVisible(false);
	}

	public JPanel getPanel() {
		p.setVisible(true);
		return p;
	}

	public String getTabTitle() {
		return tabTitle;
	}

	public void setTabTitle(String tabTitle) {
		this.tabTitle = tabTitle;
	}
	
	private void updateSimMatrix(){
		
		bar = new ProgressBar();
		bar.init(100, "Building Similarity Matrix", true);
		String buildSim = "Please wait";
		bar.setProgressBarString(buildSim);
		
//		selectedSimFunction = chooseSimFunction.getSelectedIndex();
		switch (selectedSimFunction) {
		case 0:
			if(GraphAlignmentSettings.blastLocation==0){
				// use Web Server
				//..
				simMatrixReady = false;
				break;
			}else if (GraphAlignmentSettings.blastLocation==1){
				similarityMatrix.buildSimilarityMatrixWithBLAST();
				similarityMatrix.normalizeSimilarityMatrix();
				simMatrixReady = true;
				break;
			}else{
				simMatrixReady = false;
			}
		case 1:
			similarityMatrix.buildSimilarityMatrixByType();
//			this.similarityMatrix.normalizeSimilarityMatrix();
			simMatrixReady = true;
			break;
		case 2:
			simMatrixReady = similarityMatrix.buildSimilarityMatrixByFile();
//			this.similarityMatrix.normalizeSimilarityMatrix();
			break;
		default:
			simMatrixReady = false;
			break;
		}
    	
		updateButtons();
		bar.closeWindow();
		
		
	}
	
	
	class AlignmentTask extends SwingWorker<Void,Void>{
		
		private boolean jobDone = false;
//		private String buildSim = "Calculate Similarity Matrix";
		private String buildAli = "  Calculate Alignment  ";
		private String buildGraph = "Building Alignment Graph";
		
        @Override
		public Void doInBackground() {
			
			if (simMatrixReady) {
				bar.setProgressBarString(buildAli);
				
				switch (GraphAlignmentSettings.mnalignerLocation) {
				case 0:
//					System.out.println(GraphAlignmentSettings.mnAlignerLocations[0]);
					// add Web Server call
					jobDone = false;
					break;
				case 1:
					RConnection c = null;
					try {
						StartRserve.checkLocalRserve();
						c = new RConnection();
						alignment = new MNAlignerRunOnR(graph_A, graph_B,
								similarityMatrix, lambdaValue, c);
						alignment.run();
//						c.shutdown();
						jobDone = true;
					} catch (Exception e) {
						System.out.println("ERROR occured!!!");
						e.printStackTrace();
						jobDone = false;
					}finally{
						try {
							c.shutdown();
						} catch (RserveException e) {
							e.printStackTrace();
						}
					}
					break;
				case 2:
//					System.out.println(GraphAlignmentSettings.mnAlignerLocations[2]);
					alignment = new MNAlignerJava(graph_A, graph_B,
							similarityMatrix, lambdaValue);
					try {
							alignment.run();
							jobDone = true;
						} catch (Exception e) {
							jobDone = false;
							e.printStackTrace();
							break;
						}
					break;
				default:
					break;
				}
				
			}else{
				jobDone = false;
				System.err.println("Similarity Matrix not ready!");
			}

        	return null;
        }
        
        @Override
		public void done() {
        	
        	if (jobDone) {
        		
        		bar.setProgressBarString(buildGraph);
        		
        		alignmentGraph.setAlignment(alignment);
        		alignmentGraph.setThreshold(edgeThreshold);
				
        		alignmentGraph.removeAlignmentEdges();
				alignmentGraph.drawAlignmentGraph();
				
				alignmentReady = true;
				
			} else {
				JOptionPane.showMessageDialog(null, "An Error occured during the alignment algorithm.");
				alignmentReady = false;
			}
        	
        	bar.closeWindow();
        	updateButtons();
        }
    }
	
}



