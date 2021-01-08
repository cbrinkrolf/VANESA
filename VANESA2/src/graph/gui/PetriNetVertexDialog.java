/**
 * 
 */
package graph.gui;

import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.petriNet.ContinuousPlace;
import biologicalObjects.nodes.petriNet.ContinuousTransition;
import biologicalObjects.nodes.petriNet.DiscretePlace;
import biologicalObjects.nodes.petriNet.DiscreteTransition;
import biologicalObjects.nodes.petriNet.Place;
import biologicalObjects.nodes.petriNet.StochasticTransition;
import graph.GraphContainer;
import graph.GraphInstance;
import gui.MainWindow;
import gui.MyPopUp;
import net.miginfocom.swing.MigLayout;
import util.MyNumberFormat;

/**
 * @author Sebastian
 * 
 */
public class PetriNetVertexDialog {

	private JPanel panel;
	private JOptionPane pane;

	private JTextField name = new JTextField();
	private JTextField label = new JTextField();

	private JComboBox<String> compartment = new JComboBox<String>();
	private GraphInstance graphInstance = new GraphInstance();
	private Pathway pw = graphInstance.getPathway();
	// JComboBox box = new JComboBox();
	// JSpinner petriValue = new JSpinner();

	// for places
	private JFormattedTextField token;
	private JFormattedTextField tokenStart;
	private JFormattedTextField tokenMin;
	private JFormattedTextField tokenMax;

	// for Transitions
	private JTextField delay = new JTextField("1");
	private String[] disStrings = { "norm", "exp" };
	private JComboBox<String> distributionList = new JComboBox<String>(disStrings);
	// JCheckBox transitionfire = new JCheckBox("Should transition fire:",
	// true);
	private JTextField firingCondition = new JTextField("true");
	private JLabel lblFiringCondition = new JLabel("Firing Condition");

	private JLabel lblMaxSpeed = new JLabel("Maximum Speed");
	private JTextField maxSpeed = new JTextField("1");

	private String petriElement;

	/**
	 * 
	 */
	public PetriNetVertexDialog(String petriElement) {
		this.petriElement = petriElement;
		MigLayout layout = new MigLayout("", "[left]");

		panel = new JPanel(layout);
		panel.add(new JLabel("Label"), "span 2, gaptop 2 ");
		panel.add(label, "span,wrap,growx ,gap 10, gaptop 2");
		panel.add(new JLabel("Name"), "span 2, gaptop 2 ");
		panel.add(name, "span,wrap,growx ,gap 10, gaptop 2");
		panel.add(new JLabel("Compartment"), "span 4, gapright 4");
		AutoCompleteDecorator.decorate(compartment);
		compartment.setSelectedItem("Cytoplasma");
		panel.add(compartment, "span,wrap 5,growx ,gaptop 2");
		panel.add(new JSeparator(), "span, growx, wrap 10, gaptop 7 ");

		if (this.petriElement.equals("discretePlace")) {
			label.setText("p" + (pw.getPetriNet().getPlaces() + 1));
			name.setText("p" + (pw.getPetriNet().getPlaces() + 1));
			panel.add(new JLabel("Token"), "span 2, gaptop 2 ");
			token = new JFormattedTextField(MyNumberFormat.getIntegerFormat());
			token.setText("0");
			panel.add(token, "span,wrap,growx ,gap 10, gaptop 2");
			panel.add(new JLabel("Token Start"), "span 2, gaptop 2 ");
			tokenStart = new JFormattedTextField(MyNumberFormat.getIntegerFormat());
			tokenStart.setText("0");
			panel.add(tokenStart, "span,wrap,growx ,gap 10, gaptop 2");
			panel.add(new JLabel("TokenMin"), "span 2, gaptop 2 ");
			tokenMin = new JFormattedTextField(MyNumberFormat.getIntegerFormat());
			tokenMin.setText("0");
			panel.add(tokenMin, "span,wrap,growx ,gap 10, gaptop 2");
			panel.add(new JLabel("TokenMax"), "span 2, gaptop 2 ");
			tokenMax = new JFormattedTextField(MyNumberFormat.getIntegerFormat());
			tokenMax.setText(Integer.MAX_VALUE + "");
			panel.add(tokenMax, "span,wrap,growx ,gap 10, gaptop 2");
		} else if (this.petriElement.equals("continuousPlace")) {
			label.setText("p" + (pw.getPetriNet().getPlaces() + 1));
			name.setText("p" + (pw.getPetriNet().getPlaces() + 1));
			panel.add(new JLabel("Token"), "span 2, gaptop 2 ");
			token = new JFormattedTextField(MyNumberFormat.getDecimalFormat());
			token.setText("0.0");
			panel.add(token, "span,wrap,growx ,gap 10, gaptop 2");
			panel.add(new JLabel("Token Start"), "span 2, gaptop 2 ");
			tokenStart = new JFormattedTextField(MyNumberFormat.getDecimalFormat());
			tokenStart.setText("0.0");
			panel.add(tokenStart, "span,wrap,growx ,gap 10, gaptop 2");
			panel.add(new JLabel("TokenMin"), "span 2, gaptop 2 ");
			tokenMin = new JFormattedTextField(MyNumberFormat.getDecimalFormat());
			tokenMin.setText("0.0");
			panel.add(tokenMin, "span,wrap,growx ,gap 10, gaptop 2");
			panel.add(new JLabel("TokenMax"), "span 2, gaptop 2 ");
			tokenMax = new JFormattedTextField(MyNumberFormat.getDecimalFormat());
			tokenMax.setText(Double.MAX_VALUE + "");
			panel.add(tokenMax, "span,wrap,growx ,gap 10, gaptop 2");
		} else if (this.petriElement.equals("discreteTransition")) {
			label.setText("t" + (pw.getPetriNet().getTransitions() + 1));
			name.setText("t" + (pw.getPetriNet().getTransitions() + 1));
			panel.add(lblFiringCondition, "span 2, gaptop 2 ");
			panel.add(firingCondition, "span,wrap,growx ,gap 10, gaptop 2");

			panel.add(new JLabel("Delay"), "span 2, gaptop 2 ");
			panel.add(delay, "span,wrap,growx ,gap 10, gaptop 2");

		} else if (this.petriElement.equals("continiousTransition")) {
			label.setText("t" + (pw.getPetriNet().getTransitions() + 1));
			name.setText("t" + (pw.getPetriNet().getTransitions() + 1));
			panel.add(lblMaxSpeed, "span 2, gaptop 2 ");
			panel.add(maxSpeed, "span,wrap,growx ,gap 10, gaptop 2");
			panel.add(lblFiringCondition, "span 2, gaptop 2 ");
			panel.add(firingCondition, "span,wrap,growx ,gap 10, gaptop 2");

		} else if (this.petriElement.equals("stochasticTransition")) {
			label.setText("t" + (pw.getPetriNet().getTransitions() + 1));
			name.setText("t" + (pw.getPetriNet().getTransitions() + 1));
			panel.add(lblFiringCondition, "span 2, gaptop 2 ");
			panel.add(firingCondition, "span,wrap,growx ,gap 10, gaptop 2");

			panel.add(new JLabel("Distribution"), "span 2, gaptop 2");
			panel.add(distributionList, "span,wrap,growx ,gap 10, gaptop 2");
		}

		addNodeItems();
		// panel.add(new JLabel("Element"), "span 4");

		pane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
	}

	private void addNodeItems() {

		// box.addItem("Discrete Place");
		// box.addItem("Continues Place");

		List<String> compartmentList = new Elementdeclerations().getAllCompartmentDeclaration();
		Iterator<String> it2 = compartmentList.iterator();

		String element;
		while (it2.hasNext()) {
			element = it2.next();
			compartment.addItem(element);
		}
	}

	public BiologicalNodeAbstract getAnswer(Point2D point) {

		String title = "";
		if (petriElement.equals("discretePlace")) {
			title = "Create a discrete Place";
		} else if (petriElement.equals("continuousPlace")) {
			title = "Create a continuous Place";
		} else if (petriElement.equals("discreteTransition")) {
			title = "Create a discrete Transition";
		} else if (petriElement.equals("continiousTransition")) {
			title = "Create a continious Transition";
		} else if (petriElement.equals("stochasticTransition")) {
			title = "Create a stochastic Transition";
		}

		JDialog dialog = pane.createDialog(null, title);

		// dialog.show();
		dialog.setLocationRelativeTo(MainWindow.getInstance());
		dialog.setVisible(true);
		Number number;
		Integer value = (Integer) pane.getValue();
		if (value != null) {
			if (value.intValue() == JOptionPane.OK_OPTION) {

				if (name.getText().trim().length() < 1 || name.getText().trim().charAt(0) == '_') {
					MyPopUp.getInstance().show("Adding Place", "Cannot add Place! Empty name or name starts with \"_\"");
					return null;
				}

				BiologicalNodeAbstract createdNode = null;
				if (petriElement.equals("discretePlace")) {
					// System.out.println("anfang");

					number = (Number) token.getValue();
					DiscretePlace p = new DiscretePlace(label.getText().trim(), name.getText().trim());

					if (number != null) {
						p.setToken(number.doubleValue());
					}
					number = (Number) tokenStart.getValue();
					if (number != null) {
						p.setTokenStart(number.doubleValue());
					}
					number = (Number) tokenMin.getValue();
					if (number != null) {
						p.setTokenMin(number.doubleValue());
					}
					number = (Number) tokenMin.getValue();
					if (number != null) {
						p.setTokenMax(number.doubleValue());
					}
					createdNode = p;
				} else if (petriElement.equals("continuousPlace")) {

					ContinuousPlace p = new ContinuousPlace(label.getText().trim(), name.getText().trim());
					number = (Number) token.getValue();
					if (number != null) {
						p.setToken(number.doubleValue());
					}
					number = (Number) tokenStart.getValue();
					if (number != null) {
						p.setTokenStart(number.doubleValue());
						p.setToken(number.doubleValue());
					}
					number = (Number) tokenMin.getValue();
					if (number != null) {
						p.setTokenMin(number.doubleValue());
					}
					number = (Number) tokenMin.getValue();
					if (number != null) {
						p.setTokenMax(number.doubleValue());
					}
					createdNode = p;
				} else if (petriElement.equals("discreteTransition")) {
					DiscreteTransition t = new DiscreteTransition(label.getText().trim(), name.getText().trim());
					t.setDelay(Double.parseDouble(delay.getText().trim()));

					// t.setFireTransition(transitionfire.isSelected());
					t.setFiringCondition(firingCondition.getText().trim());
					createdNode = t;
				} else if (petriElement.equals("continiousTransition")) {
					ContinuousTransition t = new ContinuousTransition(label.getText().trim(), name.getText().trim());

					// t.setFireTransition(transitionfire.isSelected());
					t.setFiringCondition(firingCondition.getText().trim());
					t.setMaximumSpeed(maxSpeed.getText().trim());
					createdNode = t;
				} else if (petriElement.equals("stochasticTransition")) {
					StochasticTransition t = new StochasticTransition(label.getText().trim(), name.getText().trim());
					t.setDistribution(distributionList.getSelectedItem().toString());

					// t.setFireTransition(transitionfire.isSelected());
					t.setFiringCondition(firingCondition.getText().trim());
					createdNode = t;
				}

				if (createdNode != null) {
					Iterator<BiologicalNodeAbstract> it = pw.getAllGraphNodes().iterator();
					BiologicalNodeAbstract bna;

					boolean createdRef = false;
					
					while (it.hasNext()) {
						bna = it.next();
						if (bna.getName().equals(name.getText().trim())) {
							createdNode.setRef(bna);
							createdRef = true;
						}
					}
					int i = 1;
					if (createdRef) {
						while (true) {
							if (!pw.getAllNodeNames().contains((name.getText().trim()+"_"+i))) {
								createdNode.setName(name.getText().trim()+"_"+i);
								createdNode.setLabel(name.getText().trim());
								break;
							}
							i++;
						}
					}

					pw.addVertex(createdNode, point);
					if(createdRef){
						MyPopUp.getInstance().show("Adding Place", "Name already exists. Created reference place instead.");
					}
				}
				GraphContainer con = GraphContainer.getInstance();
				MainWindow w = MainWindow.getInstance();
				con.getPathway(w.getCurrentPathway());

				// Graph graph = vv.getGraphLayout().getGraph();

				return createdNode;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
}
