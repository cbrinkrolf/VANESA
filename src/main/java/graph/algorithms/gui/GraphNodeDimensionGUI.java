package graph.algorithms.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
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
	private final JPanel p = new JPanel();
	private boolean emptyPane = true;

	private JComboBox<String> chooseAlgorithm;
	private final String[] algorithmNames = { "None", "Node Degree", "Betweenness" };
	private JSpinner nodeSizeFromSpinner;
	private JSpinner nodeSizeToSpinner;

	private NetworkProperties c;
	private Hashtable<BiologicalNodeAbstract, Double> ratings;
	private double minvalue;
	private double maxvalue;
	private JButton resizeButton;

	private void updateWindow() {
		chooseAlgorithm = new JComboBox<>(algorithmNames);
		chooseAlgorithm.setActionCommand("algorithm");
		chooseAlgorithm.addActionListener(this);

		SpinnerNumberModel fromModel = new SpinnerNumberModel(1.0d, 0.0d, 20.0d, 0.1d);
		SpinnerNumberModel toModel = new SpinnerNumberModel(4.0d, 0.0d, 20.0d, 0.1d);

		nodeSizeFromSpinner = new JSpinner(fromModel);
		nodeSizeToSpinner = new JSpinner(toModel);
		nodeSizeFromSpinner.setEnabled(false);
		nodeSizeToSpinner.setEnabled(false);

		JPanel valuesFromTo = new JPanel();
		valuesFromTo.add(nodeSizeFromSpinner);
		valuesFromTo.add(new JLabel(" to "));
		valuesFromTo.add(nodeSizeToSpinner);

		resizeButton = new JButton("resize");
		resizeButton.addActionListener(this);
		resizeButton.setActionCommand("resize");
		resizeButton.setEnabled(false);

		p.setLayout(new MigLayout("", "[][grow]", ""));
		p.add(new JLabel("Algorithm"), "wrap");
		p.add(chooseAlgorithm, "wrap");
		p.add(valuesFromTo);
		p.add(resizeButton);
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
		ratings = new Hashtable<>();
		minvalue = Double.MAX_VALUE;
		maxvalue = Double.MIN_NORMAL;
		for (BiologicalNodeAbstract bna : c.getPathway().getAllGraphNodes()) {
			double degree = c.getNodeDegree(c.getNodeAssignment(bna));
			if (degree > maxvalue)
				maxvalue = degree;
			if (degree < minvalue)
				minvalue = degree;
			ratings.put(bna, degree);
		}
	}

	private void getNodeBetweennesCentrality() {
		c = new NetworkProperties();
		ratings = new Hashtable<>();
		minvalue = Double.MAX_VALUE;
		maxvalue = Double.MIN_NORMAL;
		int[] nodei, nodej, tmpi, tmpj;
		nodei = c.getNodeI();
		nodej = c.getNodeJ();
		// remove unused element 0 from old data structure and create undirected graph
		// (each edge has to be inserted twice)
		tmpi = new int[(nodei.length - 1) * 2];
		int tmppos = 0;
		for (int i = 1; i < nodei.length; i++) {
			tmpi[tmppos] = nodei[i] - 1;
			tmppos++;
			tmpi[tmppos] = nodej[i] - 1;
			tmppos++;
		}

		tmpj = new int[(nodej.length - 1) * 2];
		tmppos = 0;
		for (int i = 1; i < nodej.length; i++) {
			tmpj[tmppos] = nodej[i] - 1;
			tmppos++;
			tmpj[tmppos] = nodei[i] - 1;
			tmppos++;
		}
		// overwrite old data structure
		nodei = tmpi;
		nodej = tmpj;
		// invoke betweenness-centrality
		final BetweennessCentrality b = new BetweennessCentrality(nodei, nodej);
		try {
			final BetweennessCentrality.GraphCentrality g = b.calcCentrality();
			for (final BetweennessCentrality.Vertex v : g.vertices) {
				if (v.timesWalkedOver.doubleValue() < minvalue) {
					minvalue = v.timesWalkedOver.doubleValue();
				}
				if (v.timesWalkedOver.doubleValue() > maxvalue) {
					maxvalue = v.timesWalkedOver.doubleValue();
				}
				ratings.put(c.getNodeAssignmentBackwards(v.id), v.timesWalkedOver.doubleValue());
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

	private void transformRatingToWeighting(double minWeight, double maxWeight) {
		final Hashtable<BiologicalNodeAbstract, Double> weights = new Hashtable<>();
		for (Map.Entry<BiologicalNodeAbstract, Double> entry : ratings.entrySet()) {
			double value = minWeight + ((maxWeight - minWeight) / (maxvalue - minvalue)) * entry.getValue();
			weights.put(entry.getKey(), value);
			entry.getKey().setSize(value);
		}
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals("algorithm")) {
			resizeButton.setEnabled(true);
			nodeSizeFromSpinner.setEnabled(true);
			nodeSizeToSpinner.setEnabled(true);
			c = new NetworkProperties();
			switch (chooseAlgorithm.getSelectedIndex()) {
			case 0:
				// Reset to standard node size of 1
				GraphInstance.getVanesaGraph().getNodes().forEach(bna -> bna.setSize(1d));
				nodeSizeFromSpinner.setEnabled(false);
				nodeSizeToSpinner.setEnabled(false);
				resizeButton.setEnabled(false);
				break;
			case 1:
				getNodeDegreeRatings();
				transformRatingToWeighting((double) nodeSizeFromSpinner.getValue(),
						(double) nodeSizeToSpinner.getValue());
				break;
			case 2:
				getNodeBetweennesCentrality();
				transformRatingToWeighting((double) nodeSizeFromSpinner.getValue(),
						(double) nodeSizeToSpinner.getValue());
				break;
			}
		} else if (command.equals("resize")) {
			transformRatingToWeighting((double) nodeSizeFromSpinner.getValue(), (double) nodeSizeToSpinner.getValue());
		}
	}
}
