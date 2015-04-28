package graph.algorithms.gui;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;

import graph.algorithms.NetworkProperties;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.util.Rotation;

public class NodeAttributePieChart extends JFrame {

	private static final long serialVersionUID = 1L;

	public NodeAttributePieChart(String applicationTitle, String chartTitle) {
		super(applicationTitle);
		// This will create the dataset
		PieDataset dataset = createDataset();
		// based on the dataset we create the chart
		JFreeChart chart = createChart(dataset, chartTitle);
		// we put the chart into a panel
		ChartPanel chartPanel = new ChartPanel(chart);
		// default size
		chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));
		// add it to our application
		setContentPane(chartPanel);
		// align elements
		pack();
		// show
		setVisible(true);
		

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

	}

	/**
	 * Creates a sample dataset
	 */

	private PieDataset createDataset() {

		/*
		 * DefaultPieDataset result = new DefaultPieDataset();
		 * result.setValue("Linux", 29); result.setValue("Mac", 20);
		 * result.setValue("Windows", 51); return result;
		 */

		DefaultPieDataset dataset = new DefaultPieDataset();

		NetworkProperties np = new NetworkProperties();

		HashMap<Integer, Integer> degreemap = np.getNodeDegreeDistribution();

		ValueComparator bvc = new ValueComparator(degreemap);
		TreeMap<Integer, Integer> sorted_map = new TreeMap<Integer, Integer>(bvc);
		sorted_map.putAll(degreemap);
		

		for (Entry<Integer, Integer> entry : sorted_map.entrySet()) {
			dataset.setValue(entry.getKey(), entry.getValue());
		}

		return dataset;

	}

	/**
	 * Creates a chart
	 */

	private JFreeChart createChart(PieDataset dataset, String title) {

		JFreeChart chart = ChartFactory.createPieChart3D(title, // chart title
				dataset, // data
				true, // include legend
				true, false);

		PiePlot3D plot = (PiePlot3D) chart.getPlot();
		plot.setStartAngle(290);
		plot.setDirection(Rotation.CLOCKWISE);
		plot.setForegroundAlpha(0.5f);
		return chart;

	}
}