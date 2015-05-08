package petriNet;

import graph.GraphInstance;
import gui.MainWindowSingleton;
import io.SaveDialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import save.graphPicture.PngFilter;
import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class PlotsPanel extends JPanel implements ActionListener, ItemListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Pathway pw = new GraphInstance().getPathway();
	private int rows = 0; //pw.getPetriNet().getNumberOfPlaces();
	private int cols = pw.getPetriNet().getResultDimension();
	private ArrayList<String> labels = new ArrayList<String>();
	private JPanel p = new JPanel();
	private double min = Double.MAX_VALUE;
	private double max = Double.MIN_VALUE;
	private JCheckBox checkbox;
	private ArrayList<XYSeries> seriesList = new ArrayList<XYSeries>();
	private ArrayList<JFreeChart> charts = new ArrayList<JFreeChart>();

	private HashMap<String, Place> places = new HashMap<String, Place>();

	// ArrayList<String> labels = new ArrayList<String>();

	public PlotsPanel() {
		// System.out.println("cols: "+cols);
		BiologicalNodeAbstract bna;

		Place place;

		if (pw.isPetriNet() && pw.isPetriNetSimulation()) {
			// table = new Object[rows][cols + 1];
			// System.out.println("rowsSize: " + rowsSize);
			// System.out.println("rowsDim: " + rowsDim);
			Collection<BiologicalNodeAbstract> hs = pw.getAllNodes();
			// BiologicalNodeAbstract[] bnas = (BiologicalNodeAbstract[]) hs
			// .toArray(new BiologicalNodeAbstract[0]);

			Iterator<BiologicalNodeAbstract> it = hs.iterator();

			while (it.hasNext()) {
				bna = it.next();
				if (bna instanceof Place && !bna.hasRef()) {
					place = (Place) bna;
					labels.add(place.getName());
					places.put(place.getName(), place);
					rows++;
				}
			}

			Collections.sort(labels, String.CASE_INSENSITIVE_ORDER);

			/*
			 * for (int i = 0; i < bnas.length - 1; i++) { int smallest = i; for
			 * (int j = i + 1; j < bnas.length; j++) if
			 * (bnas[smallest].getLabel().compareTo(bnas[j].getLabel()) > 0)
			 * smallest = j; BiologicalNodeAbstract help = bnas[smallest];
			 * bnas[smallest] = bnas[i]; bnas[i] = help; }
			 */

			// Vector<Double> MAData;
			/*
			 * int k = 0; for (int i = 0; i < rows && k < bnas.length; k++) {
			 * 
			 * bna = places.get(labels.get(k));// bnas[k]; if (bna instanceof
			 * Place) { if (pw.getPetriNet().getPnResult() .containsKey("'" +
			 * bna.getName() + "'.t")) { // System.out.println("drin "+i);
			 * MAData = bna.getPetriNetSimulationData(); table[i][0] =
			 * bna.getLabel(); for (int j = 1; j <= MAData.size(); j++) {
			 * table[i][j] = MAData.get(j - 1); } i++; } else { //
			 * System.out.println("name: "+bna.getName()); table[i][0] =
			 * bna.getLabel(); for (int j = 1; j < cols + 1; j++) { table[i][j]
			 * = -1; } } } }
			 */

			// double min = Double.MAX_VALUE;
			// double max = Double.MIN_VALUE;

			for (int j = 0; j < rows; j++) {
				place = places.get(labels.get(j));
				if (place.getPetriNetSimulationData().size() > 0) {
					// final XYSeriesCollection dataset = new
					// XYSeriesCollection();
					// XYSeries series = new XYSeries(1);
					//System.out.println(place.getName());
					min = Math.min(min, (double) Collections.min(place.getPetriNetSimulationData()));
					max = Math.max(max, (double) Collections.max(place.getPetriNetSimulationData()));
				}
			}

			checkbox = new JCheckBox(
					"Use same scaling for each simulation plot of each place.");
			// this.drawPlots();

			p.removeAll();
			p.setLayout(new GridLayout(0, 3));
			// Place place;
			// Double value;
			for (int j = 0; j < rows; j++) {
				place = places.get(labels.get(j));
				if (place.getPetriNetSimulationData().size() > 0) {
					final XYSeriesCollection dataset = new XYSeriesCollection();

					seriesList.add(new XYSeries(j));
					XYSeries series = seriesList.get(j);
					/*
					 * for (int i = 0; i < cols; i++) { // System.out.println(j
					 * + " " + i); // System.out.println("test: " + table[j][i +
					 * 1]); // value = place.getPetriNetSimulationData().get(i);
					 * 
					 * if (place.getPetriNetSimulationData().size() > i) { value
					 * = place.getPetriNetSimulationData().get(i); } else {
					 * value = 0.0; }
					 * 
					 * // value = Double.parseDouble(table[j][i + //
					 * 1].toString());
					 * series.add(pw.getPetriNet().getPnResult().get("time")
					 * .get(i), value); }
					 */

					dataset.addSeries(series);
					JFreeChart chart = ChartFactory.createXYLineChart(
							labels.get(j), "Timestep", "Token", dataset,
							PlotOrientation.VERTICAL, false, true, false);

					final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
					chart.addSubtitle(new TextTitle("StartToken="
							+ place.getPetriNetSimulationData().get(0)
									.toString()));
					// NumberAxis yAxis = new NumberAxis();
					// xAxis.setTickUnit(new NumberTickUnit(2));
					// xAxis.setRange(0, 50);
					// Assign it to the chart
					if (checkbox.isSelected()) {
						// System.out.println("checked");
						NumberAxis range = (NumberAxis) chart.getXYPlot()
								.getRangeAxis();
						range.setRange(min, max * 1.05);
					}
					ChartPanel pane = new ChartPanel(chart);
					pane.setPreferredSize(new java.awt.Dimension(320, 200));
					renderer.setSeriesPaint(j, Color.BLACK);
					p.add(pane);
					charts.add(chart);
				}
			}
			this.updateData();

			for (int j = rows; j % 3 != 0; j++) {
				//final XYSeriesCollection dataset = new XYSeriesCollection();

				JPanel pane = new JPanel() {
					/**
					 * 
					 */
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
			// add(new JLabel("Simulation Plot of each Place:"),
			// BorderLayout.PAGE_START);
			add(checkbox, BorderLayout.PAGE_START);
			add(sp, BorderLayout.CENTER);
		}
	}

	private void updateData() {
		//System.out.println("update");
		Place place;
		for (int j = 0; j < rows; j++) {
			place = places.get(labels.get(j));
			if (place.getPetriNetSimulationData().size() > 0) {
				// final XYSeriesCollection dataset = new XYSeriesCollection();

				// seriesList.add(new XYSeries(1));
				XYSeries series = seriesList.get(j);
				for (int i = series.getItemCount(); i < cols; i++) {
					// System.out.println(j + " " + i);
					// System.out.println("test: " + table[j][i + 1]);
					// value = place.getPetriNetSimulationData().get(i);

					// value = Double.parseDouble(table[j][i +
					// 1].toString());
					series.add(pw.getPetriNet().getTime().get(i), place.getPetriNetSimulationData().get(i));
				}
				min = Math.min(min, (double) Collections.min(place.getPetriNetSimulationData()));
				max = Math.max(max, (double) Collections.max(place.getPetriNetSimulationData()));
			
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		new SaveDialog(SaveDialog.FORMAT_PNG, p);
		
		
		/*BufferedImage bi = new BufferedImage(p.getWidth(), p.getHeight(),
				BufferedImage.TYPE_INT_BGR);
		Graphics2D graphics = bi.createGraphics();
		p.paint(graphics);
		graphics.dispose();
		graphics.dispose();

		JFileChooser chooser = new JFileChooser();
		chooser.setAcceptAllFileFilterUsed(false);
		PngFilter filter = new PngFilter();
		chooser.addChoosableFileFilter(filter);
		chooser.setFileFilter(filter);
		int option = chooser.showSaveDialog(MainWindowSingleton.getInstance());
		if (option == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			if (!file.getAbsolutePath().endsWith(".png"))
				file = new File(file.getAbsolutePath() + ".png");
			boolean overwrite = true;
			if (file.exists()) {
				int response = JOptionPane.showConfirmDialog(MainWindowSingleton.getInstance(),
						"Overwrite existing file?", "Confirm Overwrite",
						JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE);
				if (response == JOptionPane.CANCEL_OPTION) {
					overwrite = false;
				}
			}

			if (overwrite) {

				try {
					ImageIO.write(bi, "png", file);
				} catch (Exception ex) {
					ex.printStackTrace();
				}

			}
		}*/
	}

	@Override
	public void itemStateChanged(ItemEvent e) {

		// System.out.println(e.getSource());
		this.updateData();

		//System.out.println("checked");
		//System.out.println(min);
		//System.out.println(max);
		//System.out.println(charts.get(0).getXYPlot().getDataset().getItemCount(0));
		JFreeChart chart;
		for (int i = 0; i < charts.size(); i++) {
			//System.out.println(i);
			chart = charts.get(i);
			NumberAxis range = (NumberAxis) chart.getXYPlot().getRangeAxis();
			if (checkbox.isSelected()) {
				range.setRange(min, max * 1.05);
			} else {
				range.setAutoRange(true);
			}
		}
	}
}
