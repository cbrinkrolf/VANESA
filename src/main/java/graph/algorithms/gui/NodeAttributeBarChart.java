package graph.algorithms.gui;

import java.awt.Dimension;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.JFrame;

import gui.MainWindow;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.XChartPanel;

/**
 * Barchart for Node attribute visualisation
 */
public class NodeAttributeBarChart extends JFrame {
	private static final long serialVersionUID = -4206605543774794331L;

	/**
	 * Initiates
	 * 
	 * @param title
	 *            - Title of the JFrame
	 * @param charttitle
	 *            - Title of the chart
	 * @param xaxistext
	 *            - X-Axis description
	 * @param yaxistext
	 *            - Y-Axis desciption
	 * @param dataset
	 *            - Map which contains Keys and Values (e.g. count)
	 */
	public NodeAttributeBarChart(final String title, final String charttitle, final String xaxistext,
								 final String yaxistext, final Map<String, Integer> dataset) {
		super(title);
		final CategoryChart chart = new CategoryChartBuilder().xAxisTitle(xaxistext).yAxisTitle(yaxistext).build();
		final List<String> keys = dataset.keySet().stream().sorted().collect(Collectors.toList());
		final List<Integer> values = keys.stream().map(dataset::get).collect(Collectors.toList());;
		chart.addSeries(charttitle, keys, values);
		final XChartPanel<CategoryChart> chartPanel = new XChartPanel<>(chart);
		chartPanel.setPreferredSize(new Dimension(800, 600));
		setContentPane(chartPanel);
		pack();
		setLocationRelativeTo(MainWindow.getInstance().getFrame());
		setIconImages(MainWindow.getInstance().getFrame().getIconImages());
		setVisible(true);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
}