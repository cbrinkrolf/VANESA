package graph.gui;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.petriNet.Place;
import biologicalObjects.nodes.petriNet.Transition;
import graph.GraphInstance;
import gui.MainWindow;
import net.miginfocom.swing.MigLayout;

public class ReferenceDialog {

	private JPanel panel;
	private JOptionPane pane;
	private JComboBox<BiologicalNodeAbstract> elementNames = new javax.swing.JComboBox<BiologicalNodeAbstract>();
	private GraphInstance graphInstance = new GraphInstance();
	private Pathway pw = graphInstance.getPathway();
	private JComboBox<String> box = new JComboBox<String>();
	private ArrayList<BiologicalNodeAbstract> list;
	private BiologicalNodeAbstract self;

	/**
	 * 
	 */
	public ReferenceDialog(BiologicalNodeAbstract bna) {
		self = bna;
		MigLayout layout = new MigLayout("", "[left]");

		// DefaultComboBoxModel<BiologicalNodeAbstract> dcbm = new
		// DefaultComboBoxModel<BiologicalNodeAbstract>((BiologicalNodeAbstract[])
		// pw.getAllNodes().toArray());
		elementNames.setEditable(true);
		// elementNames.setModel(dcbm);
		list = new ArrayList<BiologicalNodeAbstract>();
		elementNames.setMaximumSize(new Dimension(250, 40));
		elementNames.setSelectedItem(" ");
		AutoCompleteDecorator.decorate(elementNames);

		panel = new JPanel(layout);

		panel.add(new JLabel("Element"), "span 4");

		addNodeItems();
		AutoCompleteDecorator.decorate(box);
		box.setSelectedItem("");
		box.setMaximumSize(new Dimension(250, 300));
		panel.add(box, "span,wrap 5,growx ,gaptop 2");

		pane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE,
				JOptionPane.OK_CANCEL_OPTION);

	}

	private void addNodeItems() {

		Iterator<BiologicalNodeAbstract> it = pw.getAllGraphNodes().iterator();

		// sort entries by network label
		HashMap<String, BiologicalNodeAbstract> map = new HashMap<String, BiologicalNodeAbstract>();
		BiologicalNodeAbstract bna;
		while (it.hasNext()) {
			bna = it.next();
			if (bna != self && !bna.hasRef()) {
				if (self instanceof Place) {
					if (bna instanceof Place) {
						map.put(bna.getNetworklabel(), bna);
					}
				} else if (self instanceof Transition) {
					if (bna instanceof Transition) {
						map.put(bna.getNetworklabel(), bna);
					}
				} else {
					map.put(bna.getNetworklabel(), bna);
				}

			}
		}
		
		ArrayList<String> ids = new ArrayList<String>(map.keySet());
		Collections.sort(ids, new Comparator<String>() {
			public int compare(String f1, String f2) {
				return f1.toString().compareTo(f2.toString());
			}
		});

		for (int i = 0; i < ids.size(); i++) {
			list.add(map.get(ids.get(i)));
			box.addItem(map.get(ids.get(i)).getNetworklabel());
		}

		// String element;
		

	}

	public BiologicalNodeAbstract getAnswer() {

		JDialog dialog = pane.createDialog(null, "Select a reference");
		// dialog.show();
		dialog.setLocationRelativeTo(MainWindow.getInstance());
		dialog.setVisible(true);
		Integer value = (Integer) pane.getValue();

		if (value != null && box.getSelectedIndex() > -1) {
			if (value.intValue() == JOptionPane.OK_OPTION) {
				// details[0] = elementNames.getSelectedItem().toString();
				// details[1] = box.getSelectedItem().toString();
				// System.out.println("idx: " + box.getSelectedIndex());
				return list.get(box.getSelectedIndex());
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

}
