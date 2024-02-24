package petriNet;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import io.SuffixAwareFilter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.petriNet.ContinuousPlace;
import biologicalObjects.nodes.petriNet.Place;
import graph.GraphInstance;
import io.SaveDialog;

public class PlotsPanel extends JPanel implements ActionListener, ItemListener {
	private static final long serialVersionUID = 1L;

	private final Pathway pw;
	private int rows = 0; // pw.getPetriNet().getNumberOfPlaces();
	private int cols = 0;// pw.getPetriNet().getResultDimension();
	private final ArrayList<String> labels = new ArrayList<>();
	private double min = Double.MAX_VALUE;
	private double max = Double.MIN_VALUE;
	private JCheckBox checkbox;
	private final ArrayList<XYSeries> seriesList = new ArrayList<>();
	private final ArrayList<JFreeChart> charts = new ArrayList<>();
	private final HashMap<String, Place> places = new HashMap<>();

	private static int TOKEN = SimulationResultController.SIM_TOKEN;
	// private static int ACTUAL_FIRING_SPEED = SimulationResultController.SIM_ACTUAL_FIRING_SPEED;
	public static int FIRE = SimulationResultController.SIM_FIRE;
	public static int SUM_OF_TOKEN = SimulationResultController.SIM_SUM_OF_TOKEN;
	public static int ACTUAL_TOKEN_FLOW = SimulationResultController.SIM_ACTUAL_TOKEN_FLOW;

	private final String simId;

	public PlotsPanel(String simId) {
		pw = GraphInstance.getPathway();
		this.simId = simId;
		if (pw.isPetriNet() && pw.getPetriPropertiesNet().isPetriNetSimulation()) {
			if (simId == null) {
				simId = pw.getPetriPropertiesNet().getSimResController().getLastActive().getId();
			}
			// table = new Object[rows][cols + 1];
			Collection<BiologicalNodeAbstract> hs = pw.getAllGraphNodes();
			// BiologicalNodeAbstract[] bnas = (BiologicalNodeAbstract[]) hs.toArray(new BiologicalNodeAbstract[0]);
			for (BiologicalNodeAbstract bna : hs) {
				if (bna instanceof Place && !bna.isLogical()) {
					Place place = (Place) bna;
					labels.add(place.getName());
					places.put(place.getName(), place);
					rows++;
				}
			}

			labels.sort(String.CASE_INSENSITIVE_ORDER);

			SimulationResult simRes = pw.getPetriPropertiesNet().getSimResController().get(simId);
			cols = simRes.getTime().size();
			for (int j = 0; j < rows; j++) {
				Place place = places.get(labels.get(j));
				if (simRes.contains(place, TOKEN) && simRes.get(place, TOKEN).size() > 0) {
					// final XYSeriesCollection dataset = new
					// XYSeriesCollection();
					// XYSeries series = new XYSeries(1);
					// System.out.println(place.getName());
					min = Math.min(min, Collections.min(simRes.get(place, TOKEN).getAll()));
					max = Math.max(max, Collections.max(simRes.get(place, TOKEN).getAll()));
				}
			}

			checkbox = new JCheckBox("Use same scaling for each simulation plot of each place.");
			// this.drawPlots();

			JPanel p = new JPanel();
			p.removeAll();
			p.setLayout(new GridLayout(0, 3));

			DecimalFormat df = new DecimalFormat("#.#####");
			df.setRoundingMode(RoundingMode.HALF_UP);
			df.setMinimumFractionDigits(1);
			DecimalFormat intf = new DecimalFormat("#");
			for (int j = 0; j < rows; j++) {
				Place place = places.get(labels.get(j));
				if (simRes.contains(place, TOKEN) && simRes.get(place, TOKEN).size() > 0) {
					final XYSeriesCollection dataset = new XYSeriesCollection();
					seriesList.add(new XYSeries(j));
					XYSeries series = seriesList.get(j);
					dataset.addSeries(series);
					JFreeChart chart = ChartFactory.createXYLineChart(labels.get(j), "Time step", "Token", dataset,
							PlotOrientation.VERTICAL, false, true, false);

					XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
					String start = "Start=";
					String end = "end=";
					if (place instanceof ContinuousPlace) {
						start += df.format(simRes.get(place, TOKEN).get(0));
						end += df.format(simRes.get(place, TOKEN).get(simRes.size() - 1));
					} else {
						start += intf.format(simRes.get(place, TOKEN).get(0));
						end += intf.format(simRes.get(place, TOKEN).get(simRes.size() - 1));
					}
					chart.addSubtitle(new TextTitle(start + " " + end));
					// NumberAxis yAxis = new NumberAxis();
					// xAxis.setTickUnit(new NumberTickUnit(2));
					// xAxis.setRange(0, 50);
					// Assign it to the chart
					if (checkbox.isSelected()) {
						NumberAxis range = (NumberAxis) chart.getXYPlot().getRangeAxis();
						range.setRange(min, max * 1.05);
					}
					ChartPanel cPane = new ChartPanel(chart);
					cPane.setPreferredSize(new java.awt.Dimension(320, 200));
					renderer.setSeriesPaint(j, Color.BLACK);
					p.add(cPane);
					charts.add(chart);
				}
			}
			updateData();
			for (int j = rows; j % 3 != 0; j++) {
				// final XYSeriesCollection dataset = new XYSeriesCollection();
				JPanel pane = new JPanel() {
					private static final long serialVersionUID = 1L;

					public void paintComponent(Graphics g) {
						g.setColor(new Color(255, 255, 255));
						g.fillRect(0, 0, 322, 200);
					}
				};
				p.add(pane);
			}
			// p.setVisible(true);
			p.repaint();
			// this.repaint();
			this.revalidate();
			// this.setVisible(true);

			JScrollPane sp = new JScrollPane(p);
			sp.setPreferredSize(new java.awt.Dimension(980, 400));
			setLayout(new BorderLayout());
			checkbox.addItemListener(this);
			checkbox.setActionCommand("box");
			// add(new JLabel("Simulation Plot of each Place:"), BorderLayout.PAGE_START);
			add(checkbox, BorderLayout.PAGE_START);
			add(sp, BorderLayout.CENTER);
		}
	}

	private void updateData() {
		SimulationResult simRes = pw.getPetriPropertiesNet().getSimResController().get(simId);
		Place place;
		for (int j = 0; j < rows; j++) {
			place = places.get(labels.get(j));
			if (simRes.contains(place, TOKEN) && simRes.get(place, TOKEN).size() > 0) {
				// final XYSeriesCollection dataset = new XYSeriesCollection();
				// seriesList.add(new XYSeries(1));
				XYSeries series = seriesList.get(j);
				for (int i = series.getItemCount(); i < cols; i++) {
					// value = place.getPetriNetSimulationData().get(i);
					// value = Double.parseDouble(table[j][i + 1].toString());
					series.add(simRes.getTime().get(i), simRes.get(place, TOKEN).get(i));
				}
				min = Math.min(min, Collections.min(simRes.get(place, TOKEN).getAll()));
				max = Math.max(max, Collections.max(simRes.get(place, TOKEN).getAll()));
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		new SaveDialog(new SuffixAwareFilter[]{SuffixAwareFilter.PDF, SuffixAwareFilter.PNG, SuffixAwareFilter.SVG},
				charts, this);
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		updateData();
		for (JFreeChart chart : charts) {
			NumberAxis range = (NumberAxis) chart.getXYPlot().getRangeAxis();
			if (checkbox.isSelected()) {
				range.setRange(min, max * 1.05);
			} else {
				range.setAutoRange(true);
			}
		}
	}
}
