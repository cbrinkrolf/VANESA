package gui.visualization;

import graph.GraphInstance;
import graph.algorithms.NodeAttributeNames;
import gui.LocalBackboardPaintable;
import gui.MainWindowSingleton;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;

import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract.NodeAttribute;
import net.miginfocom.swing.MigLayout;

public class AddRendererDialog extends JFrame implements ActionListener {

	private static final long serialVersionUID = -4314475716107745329L;

	JButton cancelbutton = new JButton("cancel");
	JButton confirmbutton = new JButton("ok");
	JButton[] buttons = { confirmbutton, cancelbutton };
	JOptionPane optionPane;
	JDialog dialog;

	private JComboBox<String> rendertypebox;
	private JComboBox<String> attributetypebox;
	private JComboBox<String> nodeattributesbox;
	private JComboBox<String> valuesbox;

	private JSpinner valuefromspinner;
	private JSpinner valuetospinner;

	private SpinnerNumberModel frommodel;
	private SpinnerNumberModel tomodel;

	private JComboBox<String> shapebox;
	private JSpinner sizespinner;

	private final String[] renderertypes = { "", "LocalBackboard" };
	private final String[] attributetypes = { "", "Annotation", "Color",
			"Experiment", "Graph property" };
	private final String[] nodeattributes_GO = {
			NodeAttributeNames.GO_BIOLOGICAL_PROCESS,
			NodeAttributeNames.GO_CELLULAR_COMPONENT,
			NodeAttributeNames.GO_MOLECULAR_FUNCTION };
	private final String[] nodeattributes_COL = { "Nodecolor" };
	private final String[] nodeattributes_EXP = { NodeAttributeNames.CHOLESTEATOMA };
	private final String[] nodeattributes_GPROP = {
			NodeAttributeNames.NODE_DEGREE, NodeAttributeNames.NEIGHBOR_DEGREE };
	private final String[] shapes = { "oval", "fadeoval", "rect", "roundrect" };

	public AddRendererDialog() {

		MigLayout layout = new MigLayout("", "",
				"[][]40[][]40[][]40[][][][]40[][][][][][][]");

		JPanel mainPanel = new JPanel(layout);

		rendertypebox = new JComboBox<>(renderertypes);
		rendertypebox.addActionListener(this);
		rendertypebox.setActionCommand("rendertype");

		attributetypebox = new JComboBox<>(attributetypes);
		attributetypebox.addActionListener(this);
		attributetypebox.setActionCommand("attributetype");
		attributetypebox.setEnabled(false);

		nodeattributesbox = new JComboBox<>();
		nodeattributesbox.addActionListener(this);
		nodeattributesbox.setActionCommand("nodeattributes");
		nodeattributesbox.setEnabled(false);

		valuesbox = new JComboBox<String>();
		valuesbox.addActionListener(this);
		valuesbox.setActionCommand("values");
		valuesbox.setEnabled(false);

		frommodel = new SpinnerNumberModel(0.0, 0.0, 200, 10.0);
		valuefromspinner = new JSpinner(frommodel);
		valuefromspinner.setEnabled(false);
		tomodel = new SpinnerNumberModel(0.0, 0.0, 200, 10.0);
		valuetospinner = new JSpinner(tomodel);
		valuetospinner.setEnabled(false);

		shapebox = new JComboBox<String>(shapes);
		shapebox.addActionListener(this);

		SpinnerModel sizemodel = new SpinnerNumberModel(30.0, 20.0, 200, 10.0);
		sizespinner = new JSpinner(sizemodel);

		mainPanel.add(new JLabel("Choose the type of renderer:"));
		mainPanel.add(rendertypebox, "wrap, growx");

		mainPanel.add(new JSeparator(), "span, growx");

		mainPanel.add(new JLabel("Choose Attribute type:"));
		mainPanel.add(attributetypebox, "wrap, growx");

		mainPanel.add(new JSeparator(), " span, growx");

		mainPanel.add(new JLabel("Which attribute?"));
		mainPanel.add(nodeattributesbox, "wrap, growx");

		mainPanel.add(new JSeparator(), " span, growx");

		mainPanel.add(new JLabel("Which value?"));
		mainPanel.add(valuesbox, "wrap, growx");
		mainPanel.add(new JLabel("from:"));
		mainPanel.add(valuefromspinner, "grow, wrap");
		mainPanel.add(new JLabel("to:"));
		mainPanel.add(valuetospinner, "grow, wrap");

		mainPanel.add(new JSeparator(), " span, growx");

		mainPanel.add(new JLabel("Please specify renderer characterstics:"),
				"span, wrap");
		mainPanel.add(new JLabel("shape:"));
		mainPanel.add(shapebox, "wrap");

		mainPanel.add(new JLabel("size:"));
		mainPanel.add(sizespinner, "wrap");

		mainPanel.add(new JSeparator(), " span, growx");

		cancelbutton.addActionListener(this);
		cancelbutton.setActionCommand("cancel");

		confirmbutton.addActionListener(this);
		confirmbutton.setActionCommand("ok");

		optionPane = new JOptionPane(mainPanel, JOptionPane.PLAIN_MESSAGE);
		optionPane.setOptions(buttons);

		dialog = new JDialog(this, "Add Renderer", true);

		dialog.setContentPane(optionPane);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		dialog.pack();
		dialog.setLocationRelativeTo(MainWindowSingleton.getInstance());
		dialog.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		String event = e.getActionCommand();

		switch (event) {
		case "cancel":

			this.dispose();
			break;

		case "ok":
			// check for valid input
			// get renderer details
			String shape = null,
			name = null;
			int drawsize = -1;
			shape = shapebox.getSelectedItem().toString();
			drawsize = ((Double) sizespinner.getValue()).intValue();
			HashSet<BiologicalNodeAbstract> bnas = new HashSet<>();

			// STRING variant
			if (rendertypebox.isEnabled() && attributetypebox.isEnabled()
					&& nodeattributesbox.isEnabled() && valuesbox.isEnabled()) {
				

				name = valuesbox.getSelectedItem().toString();
				for (BiologicalNodeAbstract bna : GraphInstance.getMyGraph()
						.getAllVertices()) {
					for (NodeAttribute na : bna.getNodeAttributes()) {
						if (na.getStringvalue().equals(name))
							bnas.add(bna);
					}
				}

				// DOUBLE FROM TO variant
			} else if (rendertypebox.isEnabled()
					&& attributetypebox.isEnabled()
					&& nodeattributesbox.isEnabled()
					&& valuefromspinner.isEnabled()
					&& valuetospinner.isEnabled()) {


				
				double min, max;
				min = (double) valuefromspinner.getValue();
				max = (double) valuetospinner.getValue();
				name = nodeattributesbox.getSelectedItem().toString()+" ("+min+"/"+max+")";
				System.out.println(name+" "+min+"/"+max);
				

				for (BiologicalNodeAbstract bna : GraphInstance.getMyGraph()
						.getAllVertices()) {
					for (NodeAttribute na : bna.getNodeAttributes()) {

						System.out.println(na.getName()+": "+na.getDoublevalue()+" \t"+bna.getLabel());
						if (na.getName().equals( nodeattributesbox.getSelectedItem().toString()) 
								&& na.getDoublevalue() >= min
								&& na.getDoublevalue() <= max){
							bnas.add(bna);
							System.out.println(na.getName()+": "+na.getDoublevalue()+" \t"+bna.getLabel());
						}							
					}
				}
			}

			GraphInstance
					.getMyGraph()
					.getVisualizationViewer()
					.addPreRenderPaintable(
							new LocalBackboardPaintable(bnas, Color.red,
									drawsize, shape, name));
			GraphInstance.getMyGraph().getVisualizationViewer().repaint();

			this.dispose();
			break;

		// comboboxes
		case "rendertype":
			switch ((String) rendertypebox.getSelectedItem()) {
			case "":
				attributetypebox.setEnabled(false);
				nodeattributesbox.setEnabled(false);
				valuesbox.setEnabled(false);
				break;

			default:
				attributetypebox.setEnabled(true);
				break;
			}

			break;

		case "attributetype":

			switch ((String) attributetypebox.getSelectedItem()) {
			case "":
				nodeattributesbox.setEnabled(false);
				valuesbox.setEnabled(false);
				nodeattributesbox.removeAllItems();
				nodeattributesbox.addItem("");
				valuesbox.removeAllItems();
				valuesbox.addItem("");
				break;

			case "Annotation":
				nodeattributesbox.removeAllItems();
				nodeattributesbox.addItem("");
				for (String ann : nodeattributes_GO)
					nodeattributesbox.addItem(ann);
				nodeattributesbox.setEnabled(true);
				break;

			case "Color":
				nodeattributesbox.removeAllItems();
				nodeattributesbox.addItem("");
				for (String ann : nodeattributes_COL)
					nodeattributesbox.addItem(ann);
				nodeattributesbox.setEnabled(true);
				break;

			case "Experiment":
				nodeattributesbox.removeAllItems();
				nodeattributesbox.addItem("");
				for (String ann : nodeattributes_EXP)
					nodeattributesbox.addItem(ann);
				nodeattributesbox.setEnabled(true);
				break;

			case "Graph property":
				nodeattributesbox.removeAllItems();
				nodeattributesbox.addItem("");
				for (String ann : nodeattributes_GPROP)
					nodeattributesbox.addItem(ann);
				nodeattributesbox.setEnabled(true);
				break;

			default:

				break;
			}

			dialog.pack();

			break;

		case "nodeattributes":

			if (nodeattributesbox.isEnabled()
					&& nodeattributesbox.getItemCount() > 0) {

				switch ((String) nodeattributesbox.getSelectedItem()) {
				case "":
					valuesbox.setEnabled(false);
					valuefromspinner.setEnabled(false);
					valuetospinner.setEnabled(false);
					break;

				default:

					if (attributetypebox.getSelectedIndex() == 1
							|| attributetypebox.getSelectedIndex() == 2) {
						String val = nodeattributesbox.getSelectedItem()
								.toString();
						TreeSet<String> choices = new TreeSet<>();
						valuesbox.removeAllItems();
						valuesbox.addItem("");

						for (BiologicalNodeAbstract bna : GraphInstance
								.getMyGraph().getAllVertices()) {
							NodeAttribute na = bna.getNodeAttributeByName(val);
							if (na != null)
								choices.add(na.getStringvalue());
						}

						for (String s : choices)
							valuesbox.addItem(s);

						valuesbox.setEnabled(true);

						dialog.pack();

					} else if (attributetypebox.getSelectedIndex() == 3
							|| attributetypebox.getSelectedIndex() == 4) {
						String val = nodeattributesbox.getSelectedItem()
								.toString();
						double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
						boolean novalues = true;

						for (BiologicalNodeAbstract bna : GraphInstance
								.getMyGraph().getAllVertices()) {
							NodeAttribute na = bna.getNodeAttributeByName(val);
							if (na != null) {
								novalues = false;
								if (na.getDoublevalue() > max)
									max = na.getDoublevalue();
								if (na.getDoublevalue() < min)
									min = na.getDoublevalue();
							}
						}

						if (!novalues) {

							frommodel.setMinimum(min);
							frommodel.setMaximum(max);
							frommodel.setValue(min);
							frommodel.setStepSize(0.01);

							tomodel.setMinimum(min);
							tomodel.setMaximum(max);
							tomodel.setValue(max);
							tomodel.setStepSize(0.01);

							valuefromspinner.setEnabled(true);
							valuetospinner.setEnabled(true);
						}
					}

					break;
				}
			}

			break;

		case "values":
			// set free
			// maybe not needed yet
			break;

		default:

			break;
		}

	}

}
