package graph.gui;

import graph.GraphInstance;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class ReferenceDialog {

	private JPanel panel;
	private JOptionPane pane;
	private JComboBox<BiologicalNodeAbstract> elementNames = new javax.swing.JComboBox<BiologicalNodeAbstract>();
	private GraphInstance graphInstance = new GraphInstance();
	private Pathway pw = graphInstance.getPathway();
	private JComboBox<String> box = new JComboBox<String>();
	private ArrayList<BiologicalNodeAbstract> list;
	private BiologicalNodeAbstract self;

	boolean addedNewValues = false;

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

		Iterator<BiologicalNodeAbstract> it = pw.getAllNodes().iterator();

		// String element;
		BiologicalNodeAbstract element;
		while (it.hasNext()) {
			element = it.next();
			if (element != self && !element.hasRef()) {
				list.add(element);
				box.addItem(element.getNetworklabel());
			}
		}

	}

	public BiologicalNodeAbstract getAnswer() {

		JDialog dialog = pane.createDialog(null, "Select a reference");
		// dialog.show();
		dialog.setVisible(true);
		Integer value = (Integer) pane.getValue();

		if (value != null && box.getSelectedIndex() > -1) {
			if (value.intValue() == JOptionPane.OK_OPTION) {
				// details[0] = elementNames.getSelectedItem().toString();
				// details[1] = box.getSelectedItem().toString();
				//System.out.println("idx: " + box.getSelectedIndex());
				return list.get(box.getSelectedIndex());
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

}
