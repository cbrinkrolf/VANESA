/**
 * 
 */
package graph.gui;

import java.util.HashMap;
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

import org.apache.commons.lang3.tuple.Pair;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.petriNet.Place;
import graph.GraphInstance;
import gui.MainWindow;
import net.miginfocom.swing.MigLayout;
import petriNet.Transition;

/**
 * @author Sebastian
 * 
 */
public class EdgeDialog extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel panel;
	private String[] details = new String[3];
	private JOptionPane pane;
	private JTextField name;
	private JRadioButton directed, undirected;

	private GraphInstance graphInstance = new GraphInstance();
	private Pathway pw = graphInstance.getPathway();
	private JComboBox<String> box = new JComboBox<String>();
	private JComboBox<String> fromBox = new JComboBox<String>();
	private HashMap<Integer,BiologicalNodeAbstract> fromMap = new HashMap<Integer, BiologicalNodeAbstract>();
	private JComboBox<String> toBox = new JComboBox<String>();
	private HashMap<Integer,BiologicalNodeAbstract> toMap = new HashMap<Integer, BiologicalNodeAbstract>();



	/**
	 * 
	 */
	public EdgeDialog(BiologicalNodeAbstract from, BiologicalNodeAbstract to) {

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
		
		if(!from.getVertices().isEmpty()){
			panel.add(new JLabel("Select Start Node"), "");
			addAllChildNodes(from, fromBox, fromMap);
			AutoCompleteDecorator.decorate(fromBox);
			panel.add(fromBox, "span,wrap,growx,gap 10");
		} else {
			fromBox.addItem(from.getLabel());
			fromMap.put(fromBox.getItemCount()-1, from);
		}
		if(!to.getVertices().isEmpty()){
			panel.add(new JLabel("Select End Node"), "");
			addAllChildNodes(to, toBox, toMap);
			AutoCompleteDecorator.decorate(toBox);
			panel.add(toBox, "span,wrap,growx,gap 10");
		} else {
			toBox.addItem(to.getLabel());
			toMap.put(toBox.getItemCount()-1, to);
		}

		panel.add(new JLabel("Type of connection"), "");
		// panel.add(new JSeparator(),
		// "span, growx, wrap 15, gaptop 10, gap 5");

		addEdgeItems(panel);

		AutoCompleteDecorator.decorate(box);
		panel.add(box, "span,wrap,growx,gap 10");
		if (graphInstance.getPathway().isPetriNet()) {
			panel.add(new JLabel("Edge weight / function"), "");
			name.setText("1");
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
	
	private void addAllChildNodes(BiologicalNodeAbstract vertex, JComboBox<String> nodeBox, HashMap<Integer, BiologicalNodeAbstract> map){
		for(BiologicalNodeAbstract child : vertex.getVertices().keySet()){
			if(vertex.isPetriNet()){
				if((vertex instanceof Place && child instanceof Transition) |
						(vertex instanceof Transition && child instanceof Place)){
					continue;
				}
			}
			if(!vertex.getEnvironment().contains(child)){
				if(child.getVertices().isEmpty()){
					nodeBox.addItem(child.getLabel());
					map.put(nodeBox.getItemCount()-1, child);
				} else {
					addAllChildNodes(child, nodeBox, map);
				}
			}
		}
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

	public Pair<String[], BiologicalNodeAbstract[]> getAnswer() {

		JDialog dialog = pane.createDialog(EdgeDialog.this, "Create an edge");
		//dialog.show();
		dialog.setLocationRelativeTo(MainWindow.getInstance());
		dialog.setVisible(true);
		Integer value = (Integer) pane.getValue();
		BiologicalNodeAbstract[] bnas = new BiologicalNodeAbstract[2];
		if (value != null) {
			if (value.intValue() == JOptionPane.OK_OPTION) {
				details[0] = name.getText();
				details[2] = box.getSelectedItem().toString();
				bnas[0] = fromMap.get(fromBox.getSelectedIndex());
				bnas[1] = toMap.get(toBox.getSelectedIndex());
				if (directed.isSelected()) {
					details[1] = "directed_edge";
				} else if (undirected.isSelected())
					details[1] = "undirected_edge";
			} else {
				return Pair.of(null,null);
			}
		} else {
			return Pair.of(null,null);
		}
		Pair<String[], BiologicalNodeAbstract[]> ret = Pair.of(details,bnas);
		return ret;
	}
}
