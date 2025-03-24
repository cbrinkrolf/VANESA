package graph.gui;

import java.awt.Component;
import java.awt.geom.Point2D;
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
import biologicalObjects.nodes.petriNet.*;
import graph.GraphContainer;
import gui.MainWindow;
import gui.PopUpDialog;
import net.miginfocom.swing.MigLayout;
import transformation.graphElements.ANYPlace;
import transformation.graphElements.ANYTransition;
import util.MyNumberFormat;
import util.StochasticDistribution;

public class PetriNetVertexDialog {
    private final JOptionPane pane;

	private final JTextField name = new JTextField(20);

	private final Pathway pw;
	// JComboBox box = new JComboBox();
	// JSpinner petriValue = new JSpinner();

	// for places
	// private JFormattedTextField token;
	private JFormattedTextField tokenStart;
	private JFormattedTextField tokenMin;
	private JFormattedTextField tokenMax;

	// for Transitions
	private final JTextField delay = new JTextField("1");
	private final JComboBox<String> distributionList;
	// JCheckBox transitionfire = new JCheckBox("Should transition fire:", true);
	private final JTextField firingCondition = new JTextField("true");

    private final JTextField maxSpeed = new JTextField("1");

	private final String petriElement;

	public PetriNetVertexDialog(String petriElement, Pathway pw) {
		this.pw = pw;
		this.petriElement = petriElement;
		distributionList = new JComboBox<>(StochasticDistribution.distributions);

        final JPanel panel = new JPanel(new MigLayout("", "[left]"));
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
            final JLabel lblFiringCondition = new JLabel("Firing Condition");
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
				panel.add(new JLabel("Token Min"), "span 2, gaptop 2 ");
				tokenMin = new JFormattedTextField(MyNumberFormat.getIntegerFormat());
				tokenMin.setText("0");
				tokenMin.setValue(0);
				panel.add(tokenMin, "span,wrap,growx ,gap 10, gaptop 2");
				panel.add(new JLabel("Token Max"), "span 2, gaptop 2 ");
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
				panel.add(new JLabel("Token Min"), "span 2, gaptop 2 ");
				tokenMin = new JFormattedTextField(MyNumberFormat.getDecimalFormat());
				tokenMin.setText("0.0");
				tokenMin.setValue(0.0);
				panel.add(tokenMin, "span,wrap,growx ,gap 10, gaptop 2");
				panel.add(new JLabel("Token Max"), "span 2, gaptop 2 ");
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
				panel.add(new JLabel("Maximal Speed"), "span 2, gaptop 2 ");
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
		final JDialog dialog = pane.createDialog(null, getDialogTitle());
        dialog.setLocationRelativeTo(relativeTo == null ? MainWindow.getInstance().getFrame() : relativeTo);
		dialog.setVisible(true);
		Integer value = (Integer) pane.getValue();
		if (value == null || value != JOptionPane.OK_OPTION) {
			return null;
		}
		if (name.getText().trim().isEmpty() || name.getText().trim().charAt(0) == '_') {
			PopUpDialog.getInstance().show("Adding Node", "Cannot add node! Empty name or name starts with \"_\"");
			return null;
		}

		BiologicalNodeAbstract createdNode = null;
		if (petriElement.equals(Elementdeclerations.discretePlace)) {
			final Place p = new DiscretePlace(name.getText().trim(), name.getText().trim(), pw);
			if (!pw.isHeadless()) {
				// number = (Number) token.getValue();
				// if (number != null) {
				// p.setToken(number.doubleValue());
				// }
				Number number = (Number) tokenStart.getValue();
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
			final Place p = new ContinuousPlace(name.getText().trim(), name.getText().trim(), pw);
			if (!pw.isHeadless()) {
				// number = (Number) token.getValue();
				// if (number != null) {
				// p.setToken(number.doubleValue());
				// }
				Number number = (Number) tokenStart.getValue();
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
			createdNode = new ANYPlace(name.getText().trim(), name.getText().trim(), pw);
		} else if (petriElement.equals(Elementdeclerations.discreteTransition)) {
			DiscreteTransition t = new DiscreteTransition(name.getText().trim(), name.getText().trim(), pw);
			if (!pw.isHeadless()) {
				t.setDelay(delay.getText().trim());
				t.setFiringCondition(firingCondition.getText().trim());
			}
			createdNode = t;
		} else if (petriElement.equals(Elementdeclerations.continuousTransition)) {
			ContinuousTransition t = new ContinuousTransition(name.getText().trim(), name.getText().trim(), pw);
			if (!pw.isHeadless()) {
				t.setFiringCondition(firingCondition.getText().trim());
				t.setMaximalSpeed(maxSpeed.getText().trim());
			}
			createdNode = t;
		} else if (petriElement.equals(Elementdeclerations.stochasticTransition)) {
			StochasticTransition t = new StochasticTransition(name.getText().trim(), name.getText().trim(), pw);
			if (!pw.isHeadless()) {
				t.setDistribution(distributionList.getSelectedItem().toString());
				t.setFiringCondition(firingCondition.getText().trim());
			}
			createdNode = t;
		} else if (petriElement.equals(Elementdeclerations.transition)) {
			createdNode = new ANYTransition(name.getText().trim(), name.getText().trim(), pw);
		}

		if (createdNode != null) {
			boolean createdRef = false;
			for (BiologicalNodeAbstract bna : pw.getAllGraphNodes()) {
				if (bna.getName().equals(name.getText().trim())) {
					if (pw.isHeadless()) {
						PopUpDialog.getInstance().show("Adding Element",
													   "Name already exists. Cannot create Petri net node!");
						return null;
					}
					if (!bna.getBiologicalElement().equals(petriElement)) {
						PopUpDialog.getInstance().show("Adding Element",
													   "Name already exists. Cannot create reference node, because types do not match!\r\n" +
													   "Given type: " + petriElement + "\r\nExisting element: " +
													   bna.getBiologicalElement());
						return null;
					}
					createdNode.setLogicalReference(bna);
					createdRef = true;
				}
			}

			pw.addVertex(createdNode, point);
			if (createdRef) {
				PopUpDialog.getInstance().show("Adding Node", "Name already exists. Created reference node instead.");
			}
		}
		GraphContainer con = GraphContainer.getInstance();
		MainWindow w = MainWindow.getInstance();
		con.getPathway(w.getCurrentPathway());

		// Graph graph = vv.getGraphLayout().getGraph();

		return createdNode;
	}

	private String getDialogTitle() {
		switch (petriElement) {
			case Elementdeclerations.discretePlace:
				return "Create a discrete Place";
			case Elementdeclerations.continuousPlace:
				return "Create a continuous Place";
			case Elementdeclerations.discreteTransition:
				return "Create a discrete Transition";
			case Elementdeclerations.continuousTransition:
				return "Create a continuous Transition";
			case Elementdeclerations.stochasticTransition:
				return "Create a stochastic Transition";
			case Elementdeclerations.place:
				return "Create a generic Place";
			case Elementdeclerations.transition:
				return "Create a generic Transition";
			default:
				return "";
		}
	}
}
