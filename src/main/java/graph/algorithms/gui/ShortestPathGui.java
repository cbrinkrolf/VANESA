package graph.algorithms.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Hashtable;
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
import graph.GraphInstance;
import graph.algorithms.ShortestPath;
import net.miginfocom.swing.MigLayout;

public class ShortestPathGui implements ActionListener {
	private final JPanel p = new JPanel();
	private boolean emptyPane = true;
	private JComboBox<String> fromBox = new JComboBox<>();
	private JComboBox<String> ToBox = new JComboBox<>();
	private final JCheckBox mindMaps = new JCheckBox("avoid pathway maps");
	private final Hashtable<String, BiologicalNodeAbstract> table = new Hashtable<>();

	public ShortestPathGui() {
	}

	private void fillBox() {
		mindMaps.setSelected(true);
		final Pathway pw = GraphInstance.getPathway();
		final Vector<String> w = new Vector<>();
		for (final BiologicalNodeAbstract bna : pw.getAllGraphNodes()) {
			w.add(bna.getLabel() + " (Node:" + bna.getID() + ")");
			table.put(bna.getLabel() + " (Node:" + bna.getID() + ")", bna);
		}

		Collections.sort(w);

		fromBox = new JComboBox<>(w);
		ToBox = new JComboBox<>(w);

		AutoCompleteDecorator.decorate(fromBox);
		AutoCompleteDecorator.decorate(ToBox);

		fromBox.setMaximumSize(new Dimension(230, 20));
		ToBox.setMaximumSize(new Dimension(230, 20));
	}

	private void updateWindow() {
		final JButton calculate = new JButton("OK");
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
			emptyPane = false;
		} else {
			p.removeAll();
			updateWindow();
			p.repaint();
			p.revalidate();
		}
	}

	public void removeAllElements() {
		emptyPane = true;
	}

	public void actionPerformed(ActionEvent e) {
		String event = e.getActionCommand();
		Pathway pw = GraphInstance.getPathway();
		pw.getGraph().setMouseModeTransform();
		if ("path".equals(event)) {
			pw.getGraph().enableGraphTheory();
			final BiologicalNodeAbstract bna1 = table.get(fromBox.getSelectedItem().toString());
			final BiologicalNodeAbstract bna2 = table.get(ToBox.getSelectedItem().toString());
			final ShortestPath sp = new ShortestPath(bna1, bna2, mindMaps.isSelected());
			final Vector<BiologicalNodeAbstract> v = sp.calculateShortestPath();
			for (final BiologicalNodeAbstract biologicalNodeAbstract : v) {
				pw.getGraph().getVisualizationViewer().getPickedVertexState().pick(biologicalNodeAbstract, true);
			}
		}
	}
}
