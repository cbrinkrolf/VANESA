package petriNet;

import graph.GraphInstance;
import gui.MainWindowSingelton;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import save.graphPicture.JpegFilter;
import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class PlotsPanel extends JPanel implements ActionListener {

	Pathway pw = new GraphInstance().getPathway();
	int rowsSize = pw.getPetriNet().getNumberOfPlaces();
	int rowsDim = pw.getPetriNet().getResultDimension();
	Object[][] rows = new Object[rowsSize][rowsDim + 1];
	ArrayList<String> labels = new ArrayList<String>();
	private BufferedImage bi = null;
	private JPanel p = new JPanel();

	public PlotsPanel() {
		if (pw.isPetriNet() && pw.isPetriNetSimulation()) {
			Collection<BiologicalNodeAbstract> hs = pw.getAllNodes();
			BiologicalNodeAbstract[] bnas = (BiologicalNodeAbstract[]) hs
					.toArray(new BiologicalNodeAbstract[0]);

			for (int i = 0; i < bnas.length - 1; i++) {
				int smallest = i;
				for (int j = i + 1; j < bnas.length; j++)
					if (bnas[smallest].getLabel().compareTo(bnas[j].getLabel()) > 0)
						smallest = j;
				BiologicalNodeAbstract help = bnas[smallest];
				bnas[smallest] = bnas[i];
				bnas[i] = help;
			}

			Vector<Double> MAData;
			int k = 0;
			for (int i = 0; i < rowsDim && k < bnas.length; k++) {
				BiologicalNodeAbstract bna = bnas[k];
				if (bna instanceof Place
						&& pw.getPetriNet().getPnResult()
								.containsKey("P" + ((Place) bna).getID())) {
					MAData = bna.getPetriNetSimulationData();
					rows[i][0] = bna.getLabel();
					for (int j = 1; j <= MAData.size(); j++) {
						rows[i][j] = MAData.get(j - 1);
					}
					i++;
				}
			}
		}
		p.setLayout(new GridLayout(0, 3));

		for (int j = 0; j < rowsSize; j++) {
			final XYSeriesCollection dataset = new XYSeriesCollection();
			XYSeries series = new XYSeries(1);
			for (int i = 1; i < rowsDim; i++) {
				Double value = Double.parseDouble(rows[j][i].toString());
				series.add(i, value);
			}
			dataset.addSeries(series);
			JFreeChart chart = ChartFactory.createXYLineChart(
					(String) rows[j][0], "Timestep", "Token", dataset,
					PlotOrientation.VERTICAL, false, true, false);

			final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
			chart.addSubtitle(new TextTitle("StartToken="
					+ rows[j][1].toString()));
			ChartPanel pane = new ChartPanel(chart);
			pane.setPreferredSize(new java.awt.Dimension(320, 200));
			renderer.setSeriesPaint(j, Color.BLACK);
			p.add(pane);
		}
		for (int j = rowsSize; j % 3 != 0; j++) {
			final XYSeriesCollection dataset = new XYSeriesCollection();

			JPanel pane = new JPanel() {
				public void paintComponent(Graphics g) {
					g.setColor(new Color(255, 255, 255));
					g.fillRect(0, 0, 322, 200);
				}
			};
			p.add(pane);
		}

		JScrollPane sp = new JScrollPane(p);
		sp.setPreferredSize(new java.awt.Dimension(980, 400));
		setLayout(new BorderLayout());
		add(new JLabel("Simulation Plot of each Place:"),
				BorderLayout.PAGE_START);
		add(sp, BorderLayout.CENTER);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		BufferedImage bi = new BufferedImage(p.getWidth(), p.getHeight(),
				BufferedImage.TYPE_INT_BGR);
		Graphics2D graphics = bi.createGraphics();
		p.paint(graphics);
		graphics.dispose();
		graphics.dispose();

		JFileChooser chooser = new JFileChooser();
		chooser.setAcceptAllFileFilterUsed(false);
		JpegFilter filter =new JpegFilter();
		chooser.addChoosableFileFilter(filter);
		chooser.setFileFilter(filter);
		int option = chooser.showSaveDialog(MainWindowSingelton.getInstance());
		if (option == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			if (!file.getAbsolutePath().endsWith(".jpeg")) file=new File(file.getAbsolutePath()+".jpeg");
			boolean overwrite = true;
			if (file.exists()) {
				int response = JOptionPane.showConfirmDialog(null,
						"Overwrite existing file?", "Confirm Overwrite",
						JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE);
				if (response == JOptionPane.CANCEL_OPTION) {
					overwrite = false;
				}
			}

			if (overwrite) {

				try {
					ImageIO.write(bi, "jpeg", file);
				} catch (Exception ex) {
					ex.printStackTrace();
				}

			}
		}
	}
}
