/**
 * 
 */
package graph.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import biologicalElements.ElementNamesSingleton;
import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;
import graph.Compartment.Compartment;
import gui.MainWindow;
import net.miginfocom.swing.MigLayout;

/**
 * @author Sebastian
 * 
 */
public class VertexDialog {

	private JPanel panel;
	private JOptionPane pane;
	private JTextField name;
	private JComboBox<String> elementNames = new JComboBox<String>();
	private JComboBox<String> compartment = new JComboBox<String>();
	private JComboBox<String> elementType = new JComboBox<String>();
	private Pathway pw;
	private int lastTypeIdx = -1;
	private DefaultComboBoxModel<String> dcbm;
	private DefaultComboBoxModel<String> dcbmEmpty;

	/**
	 * 
	 */
	public VertexDialog(Pathway pw, int lastTypeidx) {
		this.pw = pw;
		MigLayout layout = new MigLayout("", "[left]");

		dcbm = new DefaultComboBoxModel<String>(ElementNamesSingleton.getInstance().getEnzymes());
		dcbmEmpty = new DefaultComboBoxModel<String>(new String[]{""});
		elementNames.setEditable(true);

		elementNames.setModel(dcbm);

		elementNames.setMaximumSize(new Dimension(250, 40));
		elementNames.setSelectedItem("");

		AutoCompleteDecorator.decorate(elementNames);

		name = new JTextField(20);
		panel = new JPanel(layout);

		panel.add(new JLabel("Element"), "span 4");

		elementType.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (elementType.getSelectedItem().equals(Elementdeclerations.enzyme)) {
					elementNames.setModel(dcbm);
					AutoCompleteDecorator.decorate(elementNames);
				} else {
					elementNames.setModel(dcbmEmpty);
					AutoCompleteDecorator.decorate(elementNames);
				}
			}
		});
		addNodeItems();
		AutoCompleteDecorator.decorate(elementType);
		if (pw.isHeadless()) {
			if (lastTypeidx < 0) {
				elementType.setSelectedItem(Elementdeclerations.anyBNA);
			} else {
				elementType.setSelectedIndex(lastTypeidx);
			}

		} else {
			if (lastTypeidx < 0) {
				elementType.setSelectedItem(Elementdeclerations.enzyme);
			} else {
				elementType.setSelectedIndex(lastTypeidx);
			}
		}
		elementType.setMaximumSize(new Dimension(250, 300));
		panel.add(elementType, "span,wrap 5,growx ,gaptop 2");

		if (!pw.isHeadless()) {
			panel.add(new JLabel("Compartment"), "span 4, gapright 4");

			AutoCompleteDecorator.decorate(compartment);
			compartment.setMaximumSize(new Dimension(250, 300));
			compartment.setSelectedItem("Cytoplasma");
			panel.add(compartment, "span,wrap 5,growx ,gaptop 2");
		}

		if (pw.isHeadless()) {
			panel.add(new JLabel("Name"), "span 2, gaptop 2 ");
			name.setText("N" + (pw.countNodes() + 1));
			panel.add(name, "span,wrap,growx ,gap 10, gaptop 2");
		} else {
			panel.add(new JLabel("Label"), "span 2, gaptop 2 ");
			panel.add(elementNames, "span,wrap,growx ,gap 10, gaptop 2");
		}

		panel.add(new JSeparator(), "span, growx, wrap 10, gaptop 7 ");

		pane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);

	}

	private void addNodeItems() {

		List<String> nodeItems = new Elementdeclerations().getNotPNNodeDeclarations();
		Iterator<String> it = nodeItems.iterator();

		if (pw.isHeadless()) {
			elementType.addItem(Elementdeclerations.anyBNA);
		}

		String element;
		while (it.hasNext()) {
			element = it.next();
			elementType.addItem(element);
		}

		if (!pw.isHeadless()) {
			List<Compartment> compartmentList = pw.getCompartmentManager().getAllCompartmentsAlphabetically();
			Iterator<Compartment> it2 = compartmentList.iterator();
			compartment.addItem("");
			Compartment c;
			while (it2.hasNext()) {
				c = it2.next();
				compartment.addItem(c.getName());
			}
		}
	}

	public Map<String, String> getAnswer(Component relativeTo) {
		Map<String, String> details = new HashMap<String, String>();
		JDialog dialog = pane.createDialog(null, "Create an element");
		// dialog.show();
		if (relativeTo == null) {
			dialog.setLocationRelativeTo(MainWindow.getInstance().getFrame());
		} else {
			dialog.setLocationRelativeTo(relativeTo);
		}
		dialog.setVisible(true);
		Integer value = (Integer) pane.getValue();

		if (value != null) {
			if (value.intValue() == JOptionPane.OK_OPTION) {
				if (pw.isHeadless()) {
					details.put("name", name.getText().toString().trim());
				} else {
					//System.out.println(elementNames.getSelectedIndex());
					details.put("name", elementNames.getSelectedItem().toString().trim());
					details.put("compartment", compartment.getSelectedItem().toString().trim());
				}
				details.put("elementType", elementType.getSelectedItem().toString().trim());
				this.lastTypeIdx = elementType.getSelectedIndex();
			} else {
				return null;
			}
		} else {
			return null;
		}
		return details;
	}

	public int getLastTypeIdx() {
		return this.lastTypeIdx;
	}
}
