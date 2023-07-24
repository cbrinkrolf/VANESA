/**
 * 
 */
package graph.gui;

import java.awt.Component;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.petriNet.ContinuousPlace;
import biologicalObjects.nodes.petriNet.ContinuousTransition;
import biologicalObjects.nodes.petriNet.DiscretePlace;
import biologicalObjects.nodes.petriNet.DiscreteTransition;
import biologicalObjects.nodes.petriNet.StochasticTransition;
import graph.GraphContainer;
import gui.MainWindow;
import gui.PopUpDialog;
import net.miginfocom.swing.MigLayout;
import transformation.graphElements.ANYPlace;
import transformation.graphElements.ANYTransition;
import util.MyNumberFormat;
import util.StochasticDistribution;

/**
 * @author Sebastian
 * 
 */
public class PetriNetVertexDialog {

	private JPanel panel;
	private JOptionPane pane;

	private JTextField name = new JTextField(20);

	private Pathway pw;
	// JComboBox box = new JComboBox();
	// JSpinner petriValue = new JSpinner();

	// for places
	// private JFormattedTextField token;
	private JFormattedTextField tokenStart;
	private JFormattedTextField tokenMin;
	private JFormattedTextField tokenMax;

	// for Transitions
	private JTextField delay = new JTextField("1");
	private JComboBox<String> distributionList;
	// JCheckBox transitionfire = new JCheckBox("Should transition fire:",
	// true);
	private JTextField firingCondition = new JTextField("true");
	private JLabel lblFiringCondition = new JLabel("Firing Condition");

	private JLabel lblMaxSpeed = new JLabel("Maximal Speed");
	private JTextField maxSpeed = new JTextField("1");

	private String petriElement;

	/**
	 * 
	 */
	public PetriNetVertexDialog(String petriElement, Pathway pw) {
		this.pw = pw;
		this.petriElement = petriElement;

		distributionList = new JComboBox<String>(StochasticDistribution.distributionList.toArray(new String[0]));

		MigLayout layout = new MigLayout("", "[left]");

		panel = new JPanel(layout);

		panel.add(new JLabel("Name"), "span 2, gaptop 2 ");
		panel.add(name, "span,wrap,growx ,gap 10, gaptop 2");

		// avoiding duplicate suggested names
		Set<String> names = pw.getAllNodeNames();
		int i = 1;
		while (names.contains("p" + i)) {
			i++;
		}
		String placeName = "p" + i;
		i = 1;
		while (names.contains("t" + i)) {
			i++;
		}
		String transitionName = "t" + i;

		if (pw.isHeadless()) {
			if (petriElement.toLowerCase().contains("place")) {
				name.setText(placeName);
			} else if (petriElement.toLowerCase().contains("transition")) {
				name.setText(transitionName);
			}
		}

		if (!pw.isHeadless()) {
			panel.add(new JSeparator(), "span, growx, wrap 10, gaptop 7 ");
			if (this.petriElement.equals(Elementdeclerations.discretePlace)) {
				name.setText(placeName);
				// panel.add(new JLabel("Token"), "span 2, gaptop 2 ");
				// token = new JFormattedTextField(MyNumberFormat.getIntegerFormat());
				// token.setText("0");
				// panel.add(token, "span,wrap,growx ,gap 10, gaptop 2");
				panel.add(new JLabel("Token Start"), "span 2, gaptop 2 ");
				tokenStart = new JFormattedTextField(MyNumberFormat.getIntegerFormat());
				tokenStart.setText("0");
				tokenStart.setValue(0);
				panel.add(tokenStart, "span,wrap,growx ,gap 10, gaptop 2");
				panel.add(new JLabel("TokenMin"), "span 2, gaptop 2 ");
				tokenMin = new JFormattedTextField(MyNumberFormat.getIntegerFormat());
				tokenMin.setText("0");
				tokenMin.setValue(0);
				panel.add(tokenMin, "span,wrap,growx ,gap 10, gaptop 2");
				panel.add(new JLabel("TokenMax"), "span 2, gaptop 2 ");
				tokenMax = new JFormattedTextField(MyNumberFormat.getIntegerFormat());
				tokenMax.setText(Integer.MAX_VALUE + "");
				tokenMax.setValue(Integer.MAX_VALUE);
				panel.add(tokenMax, "span,wrap,growx ,gap 10, gaptop 2");
			} else if (this.petriElement.equals(Elementdeclerations.continuousPlace)) {
				name.setText(placeName);
				// panel.add(new JLabel("Token"), "span 2, gaptop 2 ");
				// token = new JFormattedTextField(MyNumberFormat.getDecimalFormat());
				// token.setText("0.0");
				// panel.add(token, "span,wrap,growx ,gap 10, gaptop 2");
				panel.add(new JLabel("Token Start"), "span 2, gaptop 2 ");
				tokenStart = new JFormattedTextField(MyNumberFormat.getDecimalFormat());
				tokenStart.setText("0.0");
				tokenStart.setValue(0.0);
				panel.add(tokenStart, "span,wrap,growx ,gap 10, gaptop 2");
				panel.add(new JLabel("TokenMin"), "span 2, gaptop 2 ");
				tokenMin = new JFormattedTextField(MyNumberFormat.getDecimalFormat());
				tokenMin.setText("0.0");
				tokenMin.setValue(0.0);
				panel.add(tokenMin, "span,wrap,growx ,gap 10, gaptop 2");
				panel.add(new JLabel("TokenMax"), "span 2, gaptop 2 ");
				tokenMax = new JFormattedTextField(MyNumberFormat.getDecimalFormat());
				tokenMax.setText(Double.MAX_VALUE + "");
				panel.add(tokenMax, "span,wrap,growx ,gap 10, gaptop 2");
			} else if (this.petriElement.equals(Elementdeclerations.discreteTransition)) {
				name.setText(transitionName);
				panel.add(lblFiringCondition, "span 2, gaptop 2 ");
				panel.add(firingCondition, "span,wrap,growx ,gap 10, gaptop 2");
				panel.add(new JLabel("Delay"), "span 2, gaptop 2 ");
				panel.add(delay, "span,wrap,growx ,gap 10, gaptop 2");
			} else if (this.petriElement.equals(Elementdeclerations.continuousTransition)) {
				name.setText(transitionName);
				panel.add(lblMaxSpeed, "span 2, gaptop 2 ");
				panel.add(maxSpeed, "span,wrap,growx ,gap 10, gaptop 2");
				panel.add(lblFiringCondition, "span 2, gaptop 2 ");
				panel.add(firingCondition, "span,wrap,growx ,gap 10, gaptop 2");
			} else if (this.petriElement.equals(Elementdeclerations.stochasticTransition)) {
				name.setText(transitionName);
				panel.add(lblFiringCondition, "span 2, gaptop 2 ");
				panel.add(firingCondition, "span,wrap,growx ,gap 10, gaptop 2");
				panel.add(new JLabel("Distribution"), "span 2, gaptop 2");
				panel.add(distributionList, "span,wrap,growx ,gap 10, gaptop 2");
			}
		}
		// panel.add(new JLabel("Element"), "span 4");

		pane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
	}

	public BiologicalNodeAbstract getAnswer(Point2D point, Component relativeTo) {

		String title = "";
		if (petriElement.equals(Elementdeclerations.discretePlace)) {
			title = "Create a discrete Place";
		} else if (petriElement.equals(Elementdeclerations.continuousPlace)) {
			title = "Create a continuous Place";
		} else if (petriElement.equals(Elementdeclerations.discreteTransition)) {
			title = "Create a discrete Transition";
		} else if (petriElement.equals(Elementdeclerations.continuousTransition)) {
			title = "Create a continuous Transition";
		} else if (petriElement.equals(Elementdeclerations.stochasticTransition)) {
			title = "Create a stochastic Transition";
		} else if (petriElement.equals(Elementdeclerations.place)) {
			title = "Create a generic Place";
		} else if (petriElement.equals(Elementdeclerations.transition)) {
			title = "Create a generic Transition";
		}

		JDialog dialog = pane.createDialog(null, title);

		// dialog.show();
		if (relativeTo == null) {
			dialog.setLocationRelativeTo(MainWindow.getInstance().getFrame());
		} else {
			dialog.setLocationRelativeTo(relativeTo);
		}
		dialog.setVisible(true);
		Number number;
		Integer value = (Integer) pane.getValue();
		if (value != null) {
			if (value.intValue() == JOptionPane.OK_OPTION) {

				if (name.getText().trim().length() < 1 || name.getText().trim().charAt(0) == '_') {
					PopUpDialog.getInstance().show("Adding Node",
							"Cannot add node! Empty name or name starts with \"_\"");
					return null;
				}

				BiologicalNodeAbstract createdNode = null;
				if (petriElement.equals(Elementdeclerations.discretePlace)) {
					DiscretePlace p = new DiscretePlace(name.getText().trim(), name.getText().trim());
					if (!pw.isHeadless()) {
						// number = (Number) token.getValue();
						// if (number != null) {
						// p.setToken(number.doubleValue());
						// }
						number = (Number) tokenStart.getValue();
						if (number != null) {
							p.setTokenStart(number.doubleValue());
							p.setToken(number.doubleValue());
						}
						number = (Number) tokenMin.getValue();
						if (number != null) {
							p.setTokenMin(number.doubleValue());
						}
						number = (Number) tokenMax.getValue();
						if (number != null) {
							p.setTokenMax(number.doubleValue());
						}
					}
					createdNode = p;
				} else if (petriElement.equals(Elementdeclerations.continuousPlace)) {
					ContinuousPlace p = new ContinuousPlace(name.getText().trim(), name.getText().trim());
					if (!pw.isHeadless()) {
						// number = (Number) token.getValue();
						// if (number != null) {
						// p.setToken(number.doubleValue());
						// }
						number = (Number) tokenStart.getValue();
						if (number != null) {
							p.setTokenStart(number.doubleValue());
							p.setToken(number.doubleValue());
						}
						number = (Number) tokenMin.getValue();
						if (number != null) {
							p.setTokenMin(number.doubleValue());
						}
						number = (Number) tokenMax.getValue();
						if (number != null) {
							p.setTokenMax(number.doubleValue());
						}
					}
					createdNode = p;
				} else if (petriElement.equals(Elementdeclerations.place)) {
					ANYPlace p = new ANYPlace(name.getText().trim(), name.getText().trim());
					createdNode = p;
				} else if (petriElement.equals(Elementdeclerations.discreteTransition)) {
					DiscreteTransition t = new DiscreteTransition(name.getText().trim(), name.getText().trim());
					if (!pw.isHeadless()) {
						t.setDelay(Double.parseDouble(delay.getText().trim()));
						t.setFiringCondition(firingCondition.getText().trim());
					}
					createdNode = t;
				} else if (petriElement.equals(Elementdeclerations.continuousTransition)) {
					ContinuousTransition t = new ContinuousTransition(name.getText().trim(), name.getText().trim());
					if (!pw.isHeadless()) {
						t.setFiringCondition(firingCondition.getText().trim());
						t.setMaximalSpeed(maxSpeed.getText().trim());
					}
					createdNode = t;
				} else if (petriElement.equals(Elementdeclerations.stochasticTransition)) {
					StochasticTransition t = new StochasticTransition(name.getText().trim(), name.getText().trim());
					if (!pw.isHeadless()) {
						t.setDistribution(distributionList.getSelectedItem().toString());
						t.setFiringCondition(firingCondition.getText().trim());
					}
					createdNode = t;
				} else if (petriElement.equals(Elementdeclerations.transition)) {
					ANYTransition t = new ANYTransition(name.getText().trim(), name.getText().trim());
					createdNode = t;
				}

				if (createdNode != null) {
					Iterator<BiologicalNodeAbstract> it = pw.getAllGraphNodes().iterator();
					BiologicalNodeAbstract bna;

					boolean createdRef = false;

					while (it.hasNext()) {
						bna = it.next();
						if (bna.getName().equals(name.getText().trim())) {
							if (pw.isHeadless()) {
								PopUpDialog.getInstance().show("Adding Element",
										"Name already exists. Cannot create Petri net node!");
								return null;
							} else {
								if (!bna.getBiologicalElement().equals(petriElement)) {
									PopUpDialog.getInstance().show("Adding Element",
											"Name already exists. Cannot create reference node, because types do not match!\r\n"
													+ "Given type: " + petriElement + "\r\n" + "Existing element: "
													+ bna.getBiologicalElement());
									return null;
								}

								createdNode.setLogicalReference(bna);
								createdRef = true;
							}
						}
					}

					pw.addVertex(createdNode, point);
					if (createdRef) {
						PopUpDialog.getInstance().show("Adding Node",
								"Name already exists. Created reference node instead.");
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
