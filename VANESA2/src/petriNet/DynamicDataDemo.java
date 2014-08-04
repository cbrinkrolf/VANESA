package petriNet;

/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2004, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * --------------------
 * DynamicDataDemo.java
 * --------------------
 * (C) Copyright 2002-2004, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited).
 * Contributor(s):   -;
 *
 * $Id: DynamicDataDemo.java,v 1.12 2004/05/07 16:09:03 mungady Exp $
 *
 * Changes
 * -------
 * 28-Mar-2002 : Version 1 (DG);
 *
 */

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * A demonstration application showing a time series chart where you can
 * dynamically add (random) data by clicking on a button.
 * 
 */
public class DynamicDataDemo extends ApplicationFrame implements ActionListener {

	/** The time series data. */
	private XYSeries series;

	/** The most recent value added. */
	private double lastValue = 100.0;

	private Vector<Double> data = new Vector<Double>();

	/**
	 * Constructs a new demonstration application.
	 * 
	 * @param title
	 *            the frame title.
	 */
	public DynamicDataDemo(final String title) {

		super(title);
		this.series = new XYSeries("Random Data");
		final XYSeriesCollection dataset = new XYSeriesCollection(this.series);
		final JFreeChart chart = createChart(dataset);

		final ChartPanel chartPanel = new ChartPanel(chart);
		final JButton button = new JButton("Add New Data Item");
		button.setActionCommand("ADD_DATA");
		button.addActionListener(this);

		final JPanel content = new JPanel(new BorderLayout());
		content.add(chartPanel);
		content.add(button, BorderLayout.SOUTH);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		setContentPane(content);

		
		Runnable r = new Runnable() {
			public void run() {
				while (true) {
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					final double factor = 0.902 + 0.2 * Math.random();
					lastValue = lastValue * factor;
					//System.out.println(lastValue);
					data.add(lastValue);
				}
			}
		};
		Thread t = new Thread(r);
		t.start();
		
		Thread t1 = new Thread() {
			
			public void run(){
				while(true){
				drawData();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}
			}
		};
		t1.start();
		
		//new Thread(r).start();

	}

	/**
	 * Creates a sample chart.
	 * 
	 * @param dataset
	 *            the dataset.
	 * 
	 * @return A sample chart.
	 */
	private JFreeChart createChart(final XYDataset dataset) {
		final JFreeChart result = ChartFactory.createXYLineChart(
				"Dynamic Data Demo", "Time", "Value", dataset);
		final XYPlot plot = result.getXYPlot();
		ValueAxis axis = plot.getDomainAxis();
		axis.setAutoRange(true);
		// axis.setFixedAutoRange(60000.0); // 60 seconds
		axis = plot.getRangeAxis();
		// axis.setRange(0.0, 200.0);
		return result;
	}

	// ****************************************************************************
	// * JFREECHART DEVELOPER GUIDE *
	// * The JFreeChart Developer Guide, written by David Gilbert, is available
	// *
	// * to purchase from Object Refinery Limited: *
	// * *
	// * http://www.object-refinery.com/jfreechart/guide.html *
	// * *
	// * Sales are used to provide funding for the JFreeChart project - please *
	// * support us so that we can continue developing free software. *
	// ****************************************************************************

	/**
	 * Handles a click on the button by adding new (random) data.
	 * 
	 * @param e
	 *            the action event.
	 */
	public void actionPerformed(final ActionEvent e) {
		if (e.getActionCommand().equals("ADD_DATA")) {
			final double factor = 0.90 + 0.2 * Math.random();
			this.lastValue = this.lastValue * factor;
			final Millisecond now = new Millisecond();
			System.out.println("Now = " + now.toString());
			// this.series.add(data.size(), this.lastValue);
			data.add(lastValue);
			// Vector<Double> v = new Vector<Double>();
			this.drawData();
		}
	}

	private void drawData() {
		//series.clear();
//System.out.println(series.getItemCount());
		int count = 0;
		for (int i = series.getItemCount(); i < this.data.size(); i++) {
			series.add(i, data.get(i));
			count++;
		}
		System.out.println(count);

	}

	/**
	 * Starting point for the demonstration application.
	 * 
	 * @param args
	 *            ignored.
	 */
	public static void main(final String[] args) {

		final DynamicDataDemo demo = new DynamicDataDemo("Dynamic Data Demo");
		demo.pack();
		RefineryUtilities.centerFrameOnScreen(demo);
		demo.setVisible(true);

	}

}