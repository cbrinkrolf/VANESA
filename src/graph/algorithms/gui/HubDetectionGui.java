package graph.algorithms.gui;

import edu.uci.ics.jung.algorithms.importance.AbstractRanker;
import edu.uci.ics.jung.algorithms.scoring.DegreeScorer;
import edu.uci.ics.jung.algorithms.scoring.PageRank;
/*import edu.uci.ics.jung.algorithms.importance.DegreeDistributionRanker;
import edu.uci.ics.jung.algorithms.importance.PageRank;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.ConstantVertexAspectRatioFunction;
import edu.uci.ics.jung.graph.decorators.VertexAspectRatioFunction;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.utils.MutableDouble;
import edu.uci.ics.jung.utils.UserData;*/
import graph.GraphInstance;
import graph.algorithms.CastGraphs;
import graph.algorithms.ResultGraph;
import graph.jung.classes.MyGraph;
import graph.jung.graphDrawing.NodeRankingVertexSizeFunction;
import graph.jung.graphDrawing.VertexShapes;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.infonode.tabbedpanel.titledtab.TitledTab;
import net.miginfocom.swing.MigLayout;
import biologicalElements.GraphElementAbstract;
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class HubDetectionGui implements ActionListener {

	JPanel p = new JPanel();
	GraphElementAbstract ab;
	GraphInstance graphInstance;
	boolean emptyPane = true;

	private JSlider mNodeSizePageRankSlider;
	private JComboBox chooseAlgorithm;

	JTextField txtDelay = new JTextField();
	JButton btnPlay = new JButton("Play");
	JButton btnStop = new JButton("Stop");
	// private JButton calculate;

	private String[] algorithmNames = { "DegreeRanker", "PageRank" };
	private String[] keys = { DegreeScorer.class.getName(), PageRank.class.getName() };
	private int sliderDegree;

	private AbstractRanker ranker;
	private boolean degreeRankerUseInDegree = true;
	private double pageRankDampingFactor = 0.2;
	private TitledTab tab;

	public HubDetectionGui() {
		btnPlay.setActionCommand("play");
		btnPlay.addActionListener(this);
	}

	private void updateWindow() {

		this.txtDelay.setText("1000");
		sliderDegree = 0;

		chooseAlgorithm = new JComboBox(algorithmNames);
		chooseAlgorithm.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				// JComboBox selectedChoice = (JComboBox)e.getSource();
				calculateRanking();
			}
		});

		calculateRanking();

		mNodeSizePageRankSlider = new JSlider(0, 100, sliderDegree);
		mNodeSizePageRankSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				sliderDegree = mNodeSizePageRankSlider.getValue();
				rebuildShapes();
			}
		});

		MigLayout layout = new MigLayout("", "[][grow]", "");
		p.setLayout(layout);

		p.add(new JLabel("Node Ranking"), "span 1");
		p.add(new JSeparator(), "gap 5, span, growx, wrap 10");

		p.add(new JLabel("Algorithm"));
		p.add(chooseAlgorithm, "wrap");
		// p.add(fromBox, "gap 10, span, growx, wrap");

		p.add(new JLabel("Size Slider"));
		p.add(mNodeSizePageRankSlider, "wrap");
		// p.add(ToBox, "gap 10, wrap,span, growx");

		// p.add(mindMaps, "span, wrap 10, align right");
		JPanel animation = new JPanel();
		MigLayout layout2 = new MigLayout("", "[][grow]", "");
		animation.setLayout(layout2);
		animation.add(new JLabel("Animation speed:"));
		animation.add(txtDelay);
		animation.add(btnPlay);
		animation.add(btnStop);
		p.add(new JSeparator(), "wrap 10, span, growx");
		p.add(animation, "span, growx");
		// p.add(calculate, "span, wrap, align right");

	}

	public void rebuildShapes() {

		Pathway pw = graphInstance.getPathway();
		NodeRankingVertexSizeFunction sf = new NodeRankingVertexSizeFunction(
				keys[chooseAlgorithm.getSelectedIndex()], sliderDegree);

		VertexAspectRatioFunction rf = new ConstantVertexAspectRatioFunction(
				1.0f);
		VertexShapes vs = new VertexShapes(sf, rf);

		Set verticsSet = pw.getGraph().getAllvertices();
		for (Iterator iterator = verticsSet.iterator(); iterator.hasNext();) {
			Vertex v = (Vertex) iterator.next();
			BiologicalNodeAbstract ba = (BiologicalNodeAbstract) pw
					.getElement(v);
			ba.rebuildShape(vs);
		}
		pw.getGraph().getVisualizationViewer().repaint();
		//pw.getGraph().restartVisualizationModel();
	}

	private void calculateRanking() {

		Pathway pathway = graphInstance.getPathway();
		MyGraph myGraph = pathway.getGraph();
		// Graph g = myGraph.getJungGraph();

		ResultGraph result = CastGraphs.toDirected(pathway);
		DirectedSparseGraph directedGraph = (DirectedSparseGraph) result.graph;
		HashMap mapping = result.mapping;

		switch (chooseAlgorithm.getSelectedIndex()) {
		case 0:
			ranker = new DegreeDistributionRanker(directedGraph,
					degreeRankerUseInDegree);
			break;
		case 1:
			ranker = new PageRank(directedGraph, pageRankDampingFactor);
		default:
			break;
		}

		ranker.setRemoveRankScoresOnFinalize(false);
		ranker.evaluate();

		Set set = directedGraph.getVertices();
		for (Iterator iter = set.iterator(); iter.hasNext();) {
			Vertex v = (Vertex) iter.next();
			Vertex oldV = (Vertex) mapping.get(v);
			oldV.setUserDatum(ranker.getRankScoreKey(), new MutableDouble(
					ranker.getRankScore(v)), UserData.SHARED);
		}

		rebuildShapes();
	}

	public void revalidateView() {

		graphInstance = new GraphInstance();

		if (emptyPane) {
			updateWindow();
			p.repaint();
			p.revalidate();
			// p.setVisible(true);
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
		// p.removeAll();
		// p.setVisible(false);
	}

	public JPanel getPanel() {
		p.setVisible(false);
		return p;
	}

	public TitledTab getTitledTab() {

		tab = new TitledTab("Node Ranking", null, p, null);
		// tab.getProperties().setHighlightedRaised(2);
		// tab.getProperties().getHighlightedProperties().getComponentProperties().setBackgroundColor(Color.WHITE);
		// tab.getProperties().getNormalProperties().getComponentProperties().setBackgroundColor(Color.LIGHT_GRAY);

		return tab;
	}

	public void actionPerformed(ActionEvent e) {

		String event = e.getActionCommand();

		if ("play".equals(event)) {
			calculateRanking();
			sliderDegree = 17;
			rebuildShapes();

			final Hashtable tabel = new Hashtable<Vertex, Double>();
			final Hashtable tabel_edges = new Hashtable<String, BiologicalEdgeAbstract>();
			final ArrayList list = new ArrayList<Vertex>();

			final Pathway pw = graphInstance.getPathway();
			MyGraph myGraph = pw.getGraph();
			ResultGraph result = CastGraphs.toDirected(pw);
			DirectedSparseGraph directedGraph = (DirectedSparseGraph) result.graph;

			Set verticsSet = pw.getGraph().getAllvertices();
			for (Iterator iterator = verticsSet.iterator(); iterator.hasNext();) {
				Vertex v = (Vertex) iterator.next();
				BiologicalNodeAbstract ba = (BiologicalNodeAbstract) pw
						.getElement(v);
				ba.setVisible(false);
				tabel.put(v, ranker.getRankScore(v));
				if (list.isEmpty()) {
					list.add(v);
				} else {
					for (int i = 0; i < list.size(); i++) {
						if ((Double) tabel.get((Vertex) list.get(i)) <= ranker
								.getRankScore(v)) {
							list.add(i, v);
							break;
						}
					}
				}
			}

			HashSet set_edges = pw.getAllEdges();
			Iterator it2 = set_edges.iterator();

			while (it2.hasNext()) {
				BiologicalEdgeAbstract bea = (BiologicalEdgeAbstract) it2
						.next();
				bea.setVisible(false);
				tabel_edges.put(bea.getEdge().getEndpoints().getFirst()
						.toString()
						+ bea.getEdge().getEndpoints().getSecond().toString(),
						bea);
			}

			Runnable animator = new Runnable() {

				@Override
				public void run() {
					// AnimatedPicking picking = new AnimatedPicking();
					//pw.getGraph().restartVisualizationModel();
					pw.getGraph().getVisualizationViewer().repaint();
					for (int i = 0; i < list.size(); i++) {
						Vertex v = (Vertex) list.get(i);
						if (tabel.containsKey(v)) {
							BiologicalNodeAbstract ba = (BiologicalNodeAbstract) pw
									.getNodeByVertexID(v.toString());
							ba.setVisible(true);
							tabel.remove(v);
							// picking.animatePicking(v, true);
							pw.getGraph().getVisualizationViewer().stop();
							//pw.getGraph().getVisualizationViewer().restart();
							pw.getGraph().getVisualizationViewer().repaint();
							try {
								Thread.sleep(Integer
										.valueOf(txtDelay.getText()));
							} catch (InterruptedException er) {
							}

							Set neighbours = v.getNeighbors();
							Iterator it_neighbours = neighbours.iterator();
							while (it_neighbours.hasNext()) {
								Vertex temp = (Vertex) it_neighbours.next();
								// if (tabel.containsKey(temp)) {
								tabel.remove(temp);
								BiologicalNodeAbstract ba2 = (BiologicalNodeAbstract) pw
										.getNodeByVertexID(temp.toString());
								ba2.setVisible(true);
								if (tabel_edges.containsKey(v.toString()
										+ temp.toString())) {
									BiologicalEdgeAbstract bea = (BiologicalEdgeAbstract) tabel_edges
											.get(v.toString() + temp.toString());
									bea.setVisible(true);
								} else if (tabel_edges.containsKey(temp
										.toString()
										+ v.toString())) {
									BiologicalEdgeAbstract bea = (BiologicalEdgeAbstract) tabel_edges
											.get(temp.toString() + v.toString());
									bea.setVisible(true);
								}

								pw.getGraph().getVisualizationViewer().stop();
								//pw.getGraph().getVisualizationViewer()
									//	.restart();
								pw.getGraph().getVisualizationViewer()
										.repaint();
								try {
									Thread.sleep(Integer.valueOf(txtDelay
											.getText()));
								} catch (InterruptedException er) {
								}
								// }
							}
						}
					}
				}
			};
			Thread thread = new Thread(animator);
			thread.start();
		}
	}
}
