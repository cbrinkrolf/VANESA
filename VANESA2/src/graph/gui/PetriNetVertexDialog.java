/**
 * 
 */
package graph.gui;

//import edu.uci.ics.jung.graph.Vertex;
import graph.GraphContainer;
import graph.ContainerSingelton;
import graph.GraphInstance;
import gui.MainWindow;
import gui.MainWindowSingelton;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import petriNet.ContinuousTransition;
import petriNet.DiscreteTransition;
import petriNet.Place;
import petriNet.StochasticTransition;
import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;

/**
 * @author Sebastian
 * 
 */
public class PetriNetVertexDialog implements ActionListener {

	JPanel panel;
	String[] details = new String[5];
	JOptionPane pane;

	JTextField name = new JTextField();
	JTextField label = new JTextField();

	JComboBox compartment = new JComboBox();
	GraphInstance graphInstance = new GraphInstance();
	Pathway pw = graphInstance.getPathway();
	// JComboBox box = new JComboBox();
	// JSpinner petriValue = new JSpinner();

	// for places
	JTextField token = new JTextField("0");
	JTextField tokenStart = new JTextField("0");
	JTextField tokenMin = new JTextField("0");
	JTextField tokenMax = new JTextField("1000000000");

	// for Transitions
	JTextField delay = new JTextField("1");
	String[] disStrings = { "norm", "exp" };
	JComboBox distributionList = new JComboBox(disStrings);
	JCheckBox transitionfire = new JCheckBox("Should tranistion fire:", true);
	JTextField transitionStatement = new JTextField("time>9.8");

	String petriElement;

	boolean addedNewValues = false;

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
			panel.add(new JLabel("Token"), "span 2, gaptop 2 ");
			panel.add(token, "span,wrap,growx ,gap 10, gaptop 2");
			panel.add(new JLabel("Token Start"), "span 2, gaptop 2 ");
			panel.add(tokenStart, "span,wrap,growx ,gap 10, gaptop 2");
			panel.add(new JLabel("TokenMin"), "span 2, gaptop 2 ");
			panel.add(tokenMin, "span,wrap,growx ,gap 10, gaptop 2");
			panel.add(new JLabel("TokenMax"), "span 2, gaptop 2 ");
			panel.add(tokenMax, "span,wrap,growx ,gap 10, gaptop 2");
		} else if (this.petriElement.equals("continuousPlace")) {
			panel.add(new JLabel("Token"), "span 2, gaptop 2 ");
			panel.add(token, "span,wrap,growx ,gap 10, gaptop 2");
			panel.add(new JLabel("Token Start"), "span 2, gaptop 2 ");
			panel.add(tokenStart, "span,wrap,growx ,gap 10, gaptop 2");
			panel.add(new JLabel("TokenMin"), "span 2, gaptop 2 ");
			panel.add(tokenMin, "span,wrap,growx ,gap 10, gaptop 2");
			panel.add(new JLabel("TokenMax"), "span 2, gaptop 2 ");
			panel.add(tokenMax, "span,wrap,growx ,gap 10, gaptop 2");
		} else if (this.petriElement.equals("discreteTransition")) {
			panel.add(transitionfire, "span 2, gaptop 2 ");
			panel.add(transitionStatement, "span,wrap,growx ,gap 10, gaptop 2");

			panel.add(new JLabel("Delay"), "span 2, gaptop 2 ");
			panel.add(delay, "span,wrap,growx ,gap 10, gaptop 2");

		} else if (this.petriElement.equals("continiousTransition")) {
			panel.add(transitionfire, "span 2, gaptop 2 ");
			panel.add(transitionStatement, "span,wrap,growx ,gap 10, gaptop 2");

		} else if (this.petriElement.equals("stochasticTransition")) {
			panel.add(transitionfire, "span 2, gaptop 2 ");
			panel.add(transitionStatement, "span,wrap,growx ,gap 10, gaptop 2");

			panel.add(new JLabel("Distribution"), "span 2, gaptop 2");
			panel.add(distributionList, "span,wrap,growx ,gap 10, gaptop 2");
		}

		addNodeItems();
		// panel.add(new JLabel("Element"), "span 4");

		pane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE,
				JOptionPane.OK_CANCEL_OPTION);

	}

	private void addNodeItems() {

		// box.addItem("Discrete Place");
		// box.addItem("Continues Place");

		List compartmentList = new Elementdeclerations()
				.getAllCompartmentDeclaration();
		Iterator it2 = compartmentList.iterator();

		while (it2.hasNext()) {
			String element = it2.next().toString();
			compartment.addItem(element);
		}
	}

	public boolean getAnswer(BiologicalNodeAbstract v) {

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
		dialog.show();
		Integer value = (Integer) pane.getValue();

		if (value != null) {
			if (value.intValue() == JOptionPane.OK_OPTION) {
				// Vertex v = pw.getGraph().createNewVertex();

				if (petriElement.equals("discretePlace")) {
					Place p = new Place(label.getText(), name.getText(), v,
							1.0, true);
					p.setToken(Double.parseDouble(token.getText()));
					p.setTokenStart(Double.parseDouble(tokenStart.getText()));
					p.setTokenMin(Double.parseDouble(tokenMin.getText()));
					p.setTokenMax(Double.parseDouble(tokenMax.getText()));
					pw.addElement(p);
				} else if (petriElement.equals("continuousPlace")) {
					Place p = new Place(label.getText(), name.getText(), v,
							1.0, false);
					p.setToken(Double.parseDouble(token.getText()));
					p.setTokenStart(Double.parseDouble(tokenStart.getText()));
					p.setTokenMin(Double.parseDouble(tokenMin.getText()));
					p.setTokenMax(Double.parseDouble(tokenMax.getText()));
					pw.addElement(p);
				} else if (petriElement.equals("discreteTransition")) {
					DiscreteTransition t = new DiscreteTransition(
							label.getText(), name.getText(), v);
					t.setDelay(Double.parseDouble(delay.getText()));

					t.setFireTransition(transitionfire.isSelected());
					t.setFireTransitionStatemanet(transitionStatement.getText());

					pw.addElement(t);
				} else if (petriElement.equals("continiousTransition")) {
					ContinuousTransition t = new ContinuousTransition(
							label.getText(), name.getText(), v);

					t.setFireTransition(transitionfire.isSelected());
					t.setFireTransitionStatemanet(transitionStatement.getText());

					pw.addElement(t);
				} else if (petriElement.equals("stochasticTransition")) {
					StochasticTransition t = new StochasticTransition(
							label.getText(), name.getText(), v);
					t.setDistribution(distributionList.getSelectedItem()
							.toString());

					t.setFireTransition(transitionfire.isSelected());
					t.setFireTransitionStatemanet(transitionStatement.getText());

					pw.addElement(t);
				}
				GraphContainer con = ContainerSingelton.getInstance();
				MainWindow w = MainWindowSingelton.getInstance();
				con.getPathway(w.getCurrentPathway());

				// Graph graph = vv.getGraphLayout().getGraph();

				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}
}
