package graph.algorithms.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;
//import edu.uci.ics.jung.graph.Vertex;
import graph.GraphInstance;
import graph.algorithms.ShortestPath;
import net.miginfocom.swing.MigLayout;

public class ShortestPathGui implements ActionListener {

	private JPanel p = new JPanel();
	private boolean emptyPane = true;
	private JComboBox<String> fromBox = new JComboBox<String>();
	private JComboBox<String> ToBox = new JComboBox<String>();
	private JCheckBox mindMaps = new JCheckBox("avoid pathway maps");
	private Hashtable<String, BiologicalNodeAbstract> table = new Hashtable<String, BiologicalNodeAbstract>();

	private JButton calculate;

	public ShortestPathGui() {

	}

	private void fillBox() {
		mindMaps.setSelected(true);
		Pathway pw = GraphInstance.getPathway();
		Iterator<BiologicalNodeAbstract> it = pw.getAllGraphNodes().iterator();

		Vector<String> w = new Vector<String>();

		BiologicalNodeAbstract bna;
		while (it.hasNext()) {

			bna = it.next();
			w.add(bna.getLabel() + " (Node:" + bna.getID() + ")");
			table.put(bna.getLabel() + " (Node:" + bna.getID() + ")", bna);
			// i++;
		}

		Collections.sort(w);

		fromBox = new JComboBox<String>(w);
		ToBox = new JComboBox<String>(w);

		AutoCompleteDecorator.decorate(fromBox);
		AutoCompleteDecorator.decorate(ToBox);

		fromBox.setMaximumSize(new Dimension(230, 20));
		ToBox.setMaximumSize(new Dimension(230, 20));

	}

	private void updateWindow() {

		calculate = new JButton("OK");

		calculate.setToolTipText("Find shortest path");
		calculate.setActionCommand("path");
		calculate.addActionListener(this);
		table.clear();
		fillBox();

		MigLayout layout = new MigLayout("", "[][grow]", "");
		p.setLayout(layout);

		p.add(new JLabel("Shortest Path"), "span 1");
		p.add(new JSeparator(), "gap 5, span, growx, wrap 10");

		p.add(new JLabel("From"), "span 1");
		p.add(fromBox, "gap 10, span, growx, wrap");

		p.add(new JLabel("To"), "span 1");
		p.add(ToBox, "gap 10, wrap,span, growx");

		p.add(mindMaps, "span, wrap 10, align right");
		p.add(new JSeparator(), "wrap 10, span, growx");
		p.add(calculate, "span, wrap, align right");

	}

	public JPanel getPanel() {
		p.setVisible(false);
		return p;
	}

	public void revalidateView() {
		if (emptyPane) {
			updateWindow();
			p.repaint();
			p.revalidate();
			// p.setVisible(true);
			emptyPane = false;
		} else {
			p.removeAll();
			updateWindow();
			p.repaint();
			p.revalidate();
			// p.setVisible(true);

		}
		// tab.repaint();
		// tab.revalidate();
	}

	public void removeAllElements() {
		emptyPane = true;
	}

	public void actionPerformed(ActionEvent e) {

		String event = e.getActionCommand();
		Pathway pw = GraphInstance.getPathway();
		pw.getGraph().setMouseModeTransform();

		BiologicalNodeAbstract bna1;
		BiologicalNodeAbstract bna2;
		if ("path".equals(event)) {

			pw.getGraph().enableGraphTheory();

			bna1 = table.get(fromBox.getSelectedItem().toString());
			bna2 = table.get(ToBox.getSelectedItem().toString());

			ShortestPath sp = new ShortestPath(bna1, bna2, mindMaps.isSelected());
			Vector<BiologicalNodeAbstract> v = sp.calculateShortestPath();

			Iterator<BiologicalNodeAbstract> i = v.iterator();
			while (i.hasNext()) {

				pw.getGraph().getVisualizationViewer().getPickedVertexState().pick(i.next(), true);
			}
		}
	}

}
