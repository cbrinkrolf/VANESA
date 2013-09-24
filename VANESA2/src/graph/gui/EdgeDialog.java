/**
 * 
 */
package graph.gui;

import graph.GraphInstance;

import java.util.Iterator;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;

/**
 * @author Sebastian
 * 
 */
public class EdgeDialog extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JPanel panel;
	String[] details = new String[3];
	JOptionPane pane;
	JTextField name;
	JRadioButton directed, undirected;

	GraphInstance graphInstance = new GraphInstance();
	Pathway pw = graphInstance.getPathway();
	JComboBox<String> box = new JComboBox<String>();

	/**
	 * 
	 */
	public EdgeDialog() {

		//Container contentPane = getContentPane();
		MigLayout layout = new MigLayout("", "[left]");

		ButtonGroup group = new ButtonGroup();
		directed = new JRadioButton("directed");
		undirected = new JRadioButton("undirected");
		directed.setSelected(true);

		group.add(directed);
		group.add(undirected);

		name = new JTextField(20);
		panel = new JPanel(layout);

		panel.add(new JLabel("Type of connection"), "");
		// panel.add(new JSeparator(),
		// "span, growx, wrap 15, gaptop 10, gap 5");

		addEdgeItems(panel);

		AutoCompleteDecorator.decorate(box);
		panel.add(box, "span,wrap,growx,gap 10");
		if (graphInstance.getPathway().isPetriNet()) {
			panel.add(new JLabel("Edge weight / function"), "");
		}else{
			panel.add(new JLabel("Label"), "");
		}
		
		panel.add(name, "span,wrap,growx,gap 10");
		if (!pw.isPetriNet()) {
			panel.add(new JLabel("Edge"), "");
			panel.add(directed, "gap 10");
			panel.add(undirected, "span,wrap,growx");
		}
		// panel.add(new JSeparator(), "span, growx, wrap 10");

		panel.add(new JSeparator(), "span, growx, wrap 10, gaptop 7 ");

		pane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE,
				JOptionPane.OK_CANCEL_OPTION);
	}

	private void addEdgeItems(JPanel panel) {
		List<String> edgeItems = new Elementdeclerations().getAllEdgeDeclarations();

		if (pw.isPetriNet())
			edgeItems = new Elementdeclerations().getPNEdgeDeclarations();
		else
			edgeItems = new Elementdeclerations().getNotPNEdgeDeclarations();

		Iterator<String> it = edgeItems.iterator();
		String element;
		while (it.hasNext()) {
			 element = it.next();
			box.addItem(element);
		}
	}

	public String[] getAnswer() {

		JDialog dialog = pane.createDialog(EdgeDialog.this, "Create an edge");
		//dialog.show();
		dialog.setVisible(true);
		Integer value = (Integer) pane.getValue();
		if (value != null) {
			if (value.intValue() == JOptionPane.OK_OPTION) {
				details[0] = name.getText();
				details[2] = box.getSelectedItem().toString();
				if (directed.isSelected()) {
					details[1] = "directed_edge";
				} else if (undirected.isSelected())
					details[1] = "undirected_edge";
			} else {
				return null;
			}
		} else {
			return null;
		}
		return details;
	}
}
