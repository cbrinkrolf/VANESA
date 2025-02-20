package petriNet;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import io.SuffixAwareFilter;

import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.petriNet.ContinuousPlace;
import biologicalObjects.nodes.petriNet.Place;
import graph.GraphInstance;
import io.SaveDialog;
import net.miginfocom.swing.MigLayout;
import org.knowm.xchart.*;
import org.knowm.xchart.internal.chartpart.Chart;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.markers.SeriesMarkers;

import static petriNet.SimulationResultController.SIM_TOKEN;

public class PlotsPanel extends JPanel implements ActionListener, ItemListener {
	private static final long serialVersionUID = -3274771297573731270L;

	private double min = Double.MAX_VALUE;
	private double max = Double.MIN_VALUE;
	private double[] seriesMin;
	private double[] seriesMax;
	private final JCheckBox checkbox = new JCheckBox("Use same scaling for each simulation plot of each place.");
	private final List<XChartPanel<XYChart>> charts = new ArrayList<>();

	public PlotsPanel(String simId) {
		final Pathway pw = GraphInstance.getPathway();
		if (pw == null || !pw.isPetriNet() || !pw.getPetriPropertiesNet().isPetriNetSimulation()) {
			return;
		}
		if (simId == null) {
			simId = pw.getPetriPropertiesNet().getSimResController().getLastActive().getId();
		}
		final Map<String, Place> places = new HashMap<>();
		final List<String> labels = new ArrayList<>();
		int rows = 0;
		for (final BiologicalNodeAbstract bna : pw.getAllGraphNodes()) {
			if (bna instanceof Place && !bna.isLogical()) {
				final Place place = (Place) bna;
				labels.add(place.getName());
				places.put(place.getName(), place);
				rows++;
			}
		}

		labels.sort(String.CASE_INSENSITIVE_ORDER);
		seriesMin = new double[rows];
		seriesMax = new double[rows];
		final SimulationResult simRes = pw.getPetriPropertiesNet().getSimResController().get(simId);
		int cols = simRes.getTime().size();
		for (int j = 0; j < rows; j++) {
			final Place place = places.get(labels.get(j));
			if (simRes.contains(place, SIM_TOKEN) && simRes.get(place, SIM_TOKEN).size() > 0) {
				seriesMin[j] = Collections.min(simRes.get(place, SIM_TOKEN).getAll());
				seriesMax[j] = Collections.max(simRes.get(place, SIM_TOKEN).getAll());
				min = Math.min(min, seriesMin[j]);
				max = Math.max(max, seriesMax[j]);
			}
		}

		final JPanel p = new JPanel();
		p.setLayout(new GridLayout(0, 3));

		final DecimalFormat df = new DecimalFormat("#.#####");
		df.setRoundingMode(RoundingMode.HALF_UP);
		df.setMinimumFractionDigits(1);
		final DecimalFormat intf = new DecimalFormat("#");
		for (int j = 0; j < rows; j++) {
			final Place place = places.get(labels.get(j));
			final NumberFormat format = place instanceof ContinuousPlace ? df : intf;
			if (simRes.contains(place, SIM_TOKEN) && simRes.get(place, SIM_TOKEN).size() > 0) {
				final String start = format.format(simRes.get(place, SIM_TOKEN).get(0));
				final String end = format.format(simRes.get(place, SIM_TOKEN).get(simRes.size() - 1));
				final XYChart chart = new XYChartBuilder().theme(Styler.ChartTheme.Matlab).width(320).height(200)
						.xAxisTitle("Time step").yAxisTitle("Token").title(labels.get(j)).build();
				chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
				chart.getStyler().setLegendVisible(false);
				chart.getStyler().setToolTipsEnabled(true);
				chart.getStyler().setToolTipType(Styler.ToolTipType.xAndYLabels);
				final double[] xValues = new double[cols];
				final double[] yValues = new double[cols];
				for (int i = 0; i < cols; i++) {
					xValues[i] = simRes.getTime().get(i);
					yValues[i] = simRes.get(place, SIM_TOKEN).get(i);
				}
				final XYSeries series = chart.addSeries("series", xValues, yValues);
				series.setLineColor(Color.RED);
				series.setLineWidth(1);
				series.setMarker(SeriesMarkers.NONE);
				chart.addAnnotation(new AnnotationTextPanel("Start=" + start + " End=" + end, 0, 0, true));
				if (checkbox.isSelected()) {
					chart.getStyler().setYAxisMin(min);
					chart.getStyler().setYAxisMax(max * 1.05);
				}
				final XChartPanel<XYChart> chartPanel = new XChartPanel<>(chart);
				p.add(chartPanel);
				charts.add(chartPanel);
			}
		}
		for (int j = rows; j % 3 != 0; j++) {
			p.add(new JPanel() {
				public void paintComponent(final Graphics g) {
					g.setColor(Color.WHITE);
					g.fillRect(0, 0, getWidth(), getHeight());
				}
			});
		}
		p.repaint();
		revalidate();

		final JScrollPane sp = new JScrollPane(p);
		sp.setPreferredSize(new Dimension(980, 400));
		setLayout(new MigLayout("fill, wrap", "", "[][grow]"));
		checkbox.addItemListener(this);
		add(checkbox);
		add(sp, "grow");
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		final List<Chart<?, ?>> charts = new ArrayList<>();
		for (final XChartPanel<XYChart> chartPanel : this.charts) {
			charts.add(chartPanel.getChart());
		}
		new SaveDialog(new SuffixAwareFilter[] { SuffixAwareFilter.PDF, SuffixAwareFilter.PNG, SuffixAwareFilter.SVG },
				charts, this);
	}

	@Override
	public void itemStateChanged(final ItemEvent e) {
		for (int i = 0; i < charts.size(); i++) {
			final XChartPanel<XYChart> chartPanel = charts.get(i);
			final XYChart xyChart = chartPanel.getChart();
			if (checkbox.isSelected()) {
				xyChart.getStyler().setYAxisMin(min);
				xyChart.getStyler().setYAxisMax(max * 1.05);
			} else {
				xyChart.getStyler().setYAxisMin(seriesMin[i]);
				xyChart.getStyler().setYAxisMax(seriesMax[i] * 1.05);
			}
			chartPanel.repaint();
		}
	}
}
