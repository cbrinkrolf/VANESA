package graph.algorithms.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import gui.MainWindow;

/**
 * Barchart for Nodeattribute visualisation
 * 
 * @author Martin
 * 
 *         April 2015
 *
 */
public class NodeAttributeBarChart extends JFrame {

	/**
	 * generated UID
	 */
	private static final long serialVersionUID = -4206605543774794331L;

	private String charttitle, xaxistext, yaxistext;
	private Map<String, Integer> dataset;

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
	public NodeAttributeBarChart(final String title, final String charttitle,
			final String xaxistext, final String yaxistext,
			Map<String, Integer> dataset) {
		super(title);

		this.charttitle = charttitle;
		this.xaxistext = xaxistext;
		this.yaxistext = yaxistext;
		this.dataset = dataset;

		final CategoryDataset cdataset = createDataset();
		final JFreeChart chart = createChart(cdataset);
		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(800, 600));
		setContentPane(chartPanel);

		pack();
		this.setLocationRelativeTo(MainWindow.getInstance().getFrame());
		this.setIconImages(MainWindow.getInstance().getFrame().getIconImages());
		setVisible(true);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

	}
	
	private CategoryDataset createDataset() {

		final DefaultCategoryDataset bardataset = new DefaultCategoryDataset();

		for (Entry<String, Integer> entry : this.dataset.entrySet()) {
			bardataset.setValue(entry.getValue(), xaxistext, entry.getKey());
//			bardataset.setValue(entry.getKey(), xaxistext, entry.getValue());
		}

		return bardataset;

	}

	private JFreeChart createChart(final CategoryDataset dataset) {

		// create the chart...
		final JFreeChart chart = ChartFactory.createBarChart(charttitle, // chart
																			// title
				xaxistext, // domain axis label
				yaxistext, // range axis label
				dataset, // data
				PlotOrientation.HORIZONTAL, // orientation
				false, // include legend
				true, // tooltips?
				false // URLs?
				);

		// customize plot

		chart.setBackgroundPaint(Color.white);
		final CategoryPlot plot = chart.getCategoryPlot();
		plot.setBackgroundPaint(Color.LIGHT_GRAY);
		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		final BarRenderer renderer = (BarRenderer) plot.getRenderer();
		renderer.setDrawBarOutline(false);
		renderer.setSeriesPaint(0, Color.ORANGE);
//		final CategoryAxis domainAxis = plot.getDomainAxis();
//		domainAxis.setCategoryLabelPositions(CategoryLabelPositions
//				.createUpRotationLabelPositions(Math.PI / 5.0));
		return chart;

	}
}