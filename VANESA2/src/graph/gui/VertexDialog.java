/**
 * 
 */
package graph.gui;

import graph.GraphInstance;

import java.awt.Dimension;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import biologicalElements.ElementNamesSingelton;
import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;

/**
 * @author Sebastian
 * 
 */
public class VertexDialog{

	JPanel panel;
	String[] details = new String[3];
	JOptionPane pane;
	JTextField name;
	JComboBox<String> elementNames = new javax.swing.JComboBox<String>();
	JComboBox<String> compartment = new JComboBox<String>();
	GraphInstance graphInstance = new GraphInstance();
	Pathway pw = graphInstance.getPathway();
	JComboBox<String> box = new JComboBox<String>();
	
	boolean addedNewValues = false;
	/**
	 * 
	 */
	public VertexDialog() {
		
		MigLayout layout = new MigLayout("", "[left]");
		
		DefaultComboBoxModel<String> dcbm = new DefaultComboBoxModel<String>(ElementNamesSingelton.getInstance().getEnzymes());
		elementNames.setEditable(true);
		elementNames.setModel(dcbm);
		
		elementNames.setMaximumSize(new Dimension(250,40));
		elementNames.setSelectedItem(" ");
		AutoCompleteDecorator.decorate(elementNames);
		
		panel = new JPanel(layout);
		
		panel.add(new JLabel("Element"), "span 4");
		
		addNodeItems();
		AutoCompleteDecorator.decorate(box);
		box.setSelectedItem("Enzyme");
		box.setMaximumSize(new Dimension(250,300));
		panel.add(box, "span,wrap 5,growx ,gaptop 2");
		
		panel.add(new JLabel("Compartment"), "span 4, gapright 4");
		
		AutoCompleteDecorator.decorate(compartment);
		compartment.setMaximumSize(new Dimension(250,300));
		compartment.setSelectedItem("Cytoplasma");
		panel.add(compartment, "span,wrap 5,growx ,gaptop 2");
		
		panel.add(new JLabel("Label"), "span 2, gaptop 2 ");
		panel.add(elementNames, "span,wrap,growx ,gap 10, gaptop 2");
		panel.add(new JSeparator(), "span, growx, wrap 10, gaptop 7 ");

	
		
		pane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE,
				JOptionPane.OK_CANCEL_OPTION);

	}
	
	private void addNodeItems(){

		List<String> nodeItems = new Elementdeclerations().getNotPNNodeDeclarations();
		Iterator<String> it = nodeItems.iterator();
		
		String element;
		while(it.hasNext()){
			element = it.next();
			box.addItem(element);
		}	
		
		List<String> compartmentList = new Elementdeclerations().getAllCompartmentDeclaration();
		Iterator<String> it2 = compartmentList.iterator();
		
		while(it2.hasNext()){
			element = it2.next();
			compartment.addItem(element);
		}	
	}
	
	public String[] getAnswer() {

		JDialog dialog = pane.createDialog(null, "Create an element");
		//dialog.show();
		dialog.setVisible(true);
		Integer value = (Integer) pane.getValue();
		

		if (value != null) {
			if (value.intValue() == JOptionPane.OK_OPTION) {
				details[0] = elementNames.getSelectedItem().toString();
				details[1] = box.getSelectedItem().toString();
				details[2] = compartment.getSelectedItem().toString();
			} else {
				return null;
			}
		} else {
			return null;
		}
		return details;
	}
}
