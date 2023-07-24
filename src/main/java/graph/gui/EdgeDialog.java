package graph.gui;

import java.awt.Component;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import biologicalObjects.nodes.petriNet.Transition;
import gui.MainWindow;
import net.miginfocom.swing.MigLayout;

public class EdgeDialog extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JOptionPane pane;
	private final JTextField name;
	private final JTextField function;
	private final JRadioButton directed;
	private final JRadioButton undirected;

	private final Pathway pw;
	private final JComboBox<String> elementType = new JComboBox<>();
	private final JComboBox<String> fromBox = new JComboBox<>();
	private final HashMap<Integer, BiologicalNodeAbstract> fromMap = new HashMap<>();
	private final JComboBox<String> toBox = new JComboBox<>();
	private final HashMap<Integer, BiologicalNodeAbstract> toMap = new HashMap<>();

	private final BiologicalNodeAbstract from;
	private int lastTypeIdx = -1;
	private boolean lastDirected = true;

	public EdgeDialog(BiologicalNodeAbstract from, BiologicalNodeAbstract to, Pathway pw, int lastTypeIdx,
			boolean lastDirected) {
		this.from = from;
		this.pw = pw;
		MigLayout layout = new MigLayout("", "[left]");

		ButtonGroup group = new ButtonGroup();
		directed = new JRadioButton("directed");
		undirected = new JRadioButton("undirected");
		directed.setSelected(lastDirected);
		undirected.setSelected(!lastDirected);

		group.add(directed);
		group.add(undirected);

		name = new JTextField(20);
		function = new JTextField(20);
		JPanel panel = new JPanel(layout);

		if (!from.getVertices().isEmpty()) {
			panel.add(new JLabel("Select Start Node"), "");
			addAllChildNodes(from, fromBox, fromMap);
			AutoCompleteDecorator.decorate(fromBox);
			panel.add(fromBox, "span,wrap,growx,gap 10");
		} else {
			fromBox.addItem(from.getLabel());
			fromMap.put(fromBox.getItemCount() - 1, from);
		}
		if (!to.getVertices().isEmpty()) {
			panel.add(new JLabel("Select End Node"), "");
			addAllChildNodes(to, toBox, toMap);
			AutoCompleteDecorator.decorate(toBox);
			panel.add(toBox, "span,wrap,growx,gap 10");
		} else {
			toBox.addItem(to.getLabel());
			toMap.put(toBox.getItemCount() - 1, to);
		}

		panel.add(new JLabel("Type of connection"), "");
		// panel.add(new JSeparator(),
		// "span, growx, wrap 15, gaptop 10, gap 5");

		addEdgeItems(panel);

		AutoCompleteDecorator.decorate(elementType);
		panel.add(elementType, "span,wrap,growx,gap 10");
		if (pw.isPetriNet()) {
			if (pw.isHeadless()) {
				panel.add(new JLabel("Name"), "");
				int i = pw.getAllEdges().size() + 1;
				Set<String> names = pw.getAllEdgeNames();
				while (names.contains("PNE" + i)) {
					i++;
				}
				name.setText("PNE" + i);
				panel.add(name, "span,wrap,growx,gap 10");
			} else {
				panel.add(new JLabel("Arc weight / function"), "");
				function.setText("1");
				panel.add(function, "span,wrap,growx,gap 10");
			}
		} else {
			if (pw.isHeadless()) {
				name.setText("E" + (pw.getAllEdges().size() + 1));
				panel.add(new JLabel("Name"), "");
				panel.add(name, "span,wrap,growx,gap 10");
			} else {
				panel.add(new JLabel("Label"), "");
				panel.add(name, "span,wrap,growx,gap 10");
				panel.add(new JLabel("Edge weight / function"), "");
				function.setText("1");
				panel.add(function, "span,wrap,growx,gap 10");
			}
		}

		if (lastTypeIdx < 0 || lastTypeIdx > elementType.getItemCount() - 1) {
			if (pw.isPetriNet()) {
				elementType.setSelectedItem(Elementdeclerations.pnArc);
			} else {
				elementType.setSelectedItem(Elementdeclerations.reactionEdge);
			}
		} else {
			elementType.setSelectedIndex(lastTypeIdx);
		}

		if (!pw.isPetriNet()) {
			panel.add(new JLabel("Edge"), "");
			panel.add(directed, "gap 10");
			panel.add(undirected, "span,wrap,growx");
		}
		// panel.add(new JSeparator(), "span, growx, wrap 10");

		panel.add(new JSeparator(), "span, growx, wrap 10, gaptop 7 ");

		pane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
	}

	private void addAllChildNodes(BiologicalNodeAbstract vertex, JComboBox<String> nodeBox,
			HashMap<Integer, BiologicalNodeAbstract> map) {
		for (BiologicalNodeAbstract child : vertex.getVertices().keySet()) {
			if (vertex.isPetriNet()) {
				if ((vertex instanceof Place && child instanceof Transition)
						| (vertex instanceof Transition && child instanceof Place)) {
					continue;
				}
			}
			if (!vertex.getEnvironment().contains(child)) {
				if (child.getVertices().isEmpty()) {
					nodeBox.addItem(child.getLabel());
					map.put(nodeBox.getItemCount() - 1, child);
				} else {
					addAllChildNodes(child, nodeBox, map);
				}
			}
		}
	}

	private void addEdgeItems(JPanel panel) {
		List<String> edgeItems;
		if (pw.isPetriNet()) {
			edgeItems = new Elementdeclerations().getPNEdgeDeclarations();
		} else {
			edgeItems = new Elementdeclerations().getNotPNEdgeDeclarations();
		}
		if (pw.isHeadless() && !pw.isPetriNet()) {
			elementType.addItem(Elementdeclerations.anyBEA);
		}
		Iterator<String> it = edgeItems.iterator();
		String element;
		while (it.hasNext()) {
			element = it.next();
			// only add special arcs, if arc connects Place->Transition
			if (from instanceof Transition) {
				if (element.equals(Elementdeclerations.pnArc)) {
					elementType.addItem(element);
				}
			} else {
				elementType.addItem(element);
			}
		}
	}

	public Pair<Map<String, String>, BiologicalNodeAbstract[]> getAnswer(Component relativeTo) {
		Map<String, String> details = new HashMap<>();
		String title = "Create an edge";
		if (this.pw.isPetriNet()) {
			title = "Create an arc";
		}
		JDialog dialog = pane.createDialog(null, title);
		// dialog.show();
		if (relativeTo == null) {
			dialog.setLocationRelativeTo(MainWindow.getInstance().getFrame());
		} else {
			dialog.setLocationRelativeTo(relativeTo);
		}
		dialog.setVisible(true);
		Integer value = (Integer) pane.getValue();
		if (value != null && value == JOptionPane.OK_OPTION) {
			details.put("name", name.getText());
			details.put("function", function.getText());
			details.put("element", elementType.getSelectedItem().toString());
			lastTypeIdx = elementType.getSelectedIndex();
			lastDirected = directed.isSelected();
			// details[0] = name.getText();
			// details[2] = box.getSelectedItem().toString();
			BiologicalNodeAbstract[] bnas = new BiologicalNodeAbstract[2];
			bnas[0] = fromMap.get(fromBox.getSelectedIndex());
			bnas[1] = toMap.get(toBox.getSelectedIndex());
			if (directed.isSelected()) {
				details.put("directed", "true");
				// details[1] = "directed_edge";
			} else if (undirected.isSelected()) {
				// details[1] = "undirected_edge";
				details.put("directed", "false");
			}
			return Pair.of(details, bnas);
		}
		return Pair.of(null, null);
	}

	public int getLastTypeIdx() {
		return lastTypeIdx;
	}

	public boolean isLastDirected() {
		return lastDirected;
	}
}
