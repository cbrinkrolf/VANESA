package save.graphPicture;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.jibble.epsgraphics.EpsGraphics2D;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import graph.ContainerSingelton;
import graph.GraphContainer;
import graph.jung.classes.MyGraph;
import gui.MainWindow;
import gui.MainWindowSingleton;

public class WriteGraphPicture implements Printable {

	VisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv;

	private String fileFormat;

	public WriteGraphPicture() {

		MainWindow w = MainWindowSingleton.getInstance();
		GraphContainer con = ContainerSingelton.getInstance();
		MyGraph g = con.getPathway(w.getCurrentPathway()).getGraph();
		vv = g.getVisualizationViewer();
	}

	public void writeFile() {
		JFileChooser chooser = new JFileChooser();
		chooser.addChoosableFileFilter(new PngFilter());
		chooser.addChoosableFileFilter(new EPSFilter());
		int option = chooser.showSaveDialog(MainWindowSingleton.getInstance());
		if (option == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			fileFormat = chooser.getFileFilter().getDescription();

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
				write(file);
			}
		}
	}

	private void write(File file) {
		if (fileFormat.equals("PNG Files (*.png)")) {
			String extension = file.getPath();
			int i = extension.lastIndexOf('.');
			if (i > 0 && i < extension.length() - 1) {
				writePNGImage(file);
			} else {
				File withExtension = new File(file.getAbsolutePath() + ".png");
				writePNGImage(withExtension);
			}

		} else if (fileFormat.equals("EPS Files (*.eps)")) {

			String extension = file.getPath();
			int i = extension.lastIndexOf('.');
			if (i > 0 && i < extension.length() - 1) {
				writeEPSImage(file);
			} else {
				File withExtension = new File(file.getAbsolutePath() + ".eps");
				writeEPSImage(withExtension);
			}
		}
		else {
			String extension = file.getPath();
			int i = extension.lastIndexOf('.');
			if (i > 0 && i < extension.length() - 1) {
				writePNGImage(file);
			} else {
				File withExtension = new File(file.getAbsolutePath());
				writePNGImage(withExtension);
			}
		}
	}

	private void writeEPSImage(File file) {

		// use double buffering until now

		// turn it off to capture
		int width = vv.getSize().width;
		int height = vv.getSize().height;
		vv.setDoubleBuffered(false);

		FileOutputStream finalImage = null;
		try {
			finalImage = new FileOutputStream(file);
			EpsGraphics2D g = null;
			try {
				g = new EpsGraphics2D("Biological Network", finalImage, 0, 0,
						width, height);
				g.setBackground(Color.WHITE);
				vv.paint(g);
				g.flush();
				g.close();
				g.dispose();
				finalImage.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		vv.setDoubleBuffered(true);

	}

	private void writePNGImage(File file) {

		int width = vv.getSize().width;
		int height = vv.getSize().height;
		Color bg = vv.getBackground();

		BufferedImage bi = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_BGR);
		Graphics2D graphics = bi.createGraphics();
		graphics.setColor(bg);
		graphics.fillRect(0, 0, width, height);
		vv.paint(graphics);
		graphics.dispose();

		try {
			ImageIO.write(bi, "png", file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void printGraph() {

		PrinterJob printJob = PrinterJob.getPrinterJob();
		printJob.setPrintable(this);

		if (printJob.printDialog()) {
			try {
				printJob.print();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	@Override
	public int print(java.awt.Graphics graphics,
			java.awt.print.PageFormat pageFormat, int pageIndex)
			throws PrinterException {
		if (pageIndex > 0) {
			return (Printable.NO_SUCH_PAGE);
		} else {
			java.awt.Graphics2D g2d = (java.awt.Graphics2D) graphics;
			vv.setDoubleBuffered(false);
			g2d.translate(pageFormat.getImageableX(),
					pageFormat.getImageableY());

			vv.paint(g2d);
			vv.setDoubleBuffered(true);
			return (Printable.PAGE_EXISTS);
		}
	}
}
