/**
 * 
 */
package graph.gui;

//import edu.uci.ics.jung.graph.Vertex;
import graph.ContainerSingelton;
import graph.GraphContainer;
import graph.GraphInstance;
import gui.MainWindow;
import gui.MainWindowSingelton;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import petriNet.ContinuousTransition;
import petriNet.DiscreteTransition;
import petriNet.Place;
import petriNet.StochasticTransition;
import util.MyNumberFormat;
import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;

/**
 * @author Sebastian
 * 
 */
public class PetriNetVertexDialog implements ActionListener {

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
	//JCheckBox transitionfire = new JCheckBox("Should transition fire:", true);
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
			label.setText("p"+(pw.getPetriNet().getPlaces()+1));
			name.setText("p"+(pw.getPetriNet().getPlaces()+1));
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
			tokenMax.setText(Integer.MAX_VALUE+"");
			panel.add(tokenMax, "span,wrap,growx ,gap 10, gaptop 2");
		} else if (this.petriElement.equals("continuousPlace")) {
			label.setText("p"+(pw.getPetriNet().getPlaces()+1));
			name.setText("p"+(pw.getPetriNet().getPlaces()+1));
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
			tokenMax.setText(Double.MAX_VALUE+"");
			panel.add(tokenMax, "span,wrap,growx ,gap 10, gaptop 2");
		} else if (this.petriElement.equals("discreteTransition")) {
			label.setText("t"+(pw.getPetriNet().getTransitions()+1));
			name.setText("t"+(pw.getPetriNet().getTransitions()+1));
			panel.add(lblFiringCondition, "span 2, gaptop 2 ");
			panel.add(firingCondition, "span,wrap,growx ,gap 10, gaptop 2");

			panel.add(new JLabel("Delay"), "span 2, gaptop 2 ");
			panel.add(delay, "span,wrap,growx ,gap 10, gaptop 2");

		} else if (this.petriElement.equals("continiousTransition")) {
			label.setText("t"+(pw.getPetriNet().getTransitions()+1));
			name.setText("t"+(pw.getPetriNet().getTransitions()+1));
			panel.add(lblMaxSpeed, "span 2, gaptop 2 ");
			panel.add(maxSpeed, "span,wrap,growx ,gap 10, gaptop 2");
			panel.add(lblFiringCondition, "span 2, gaptop 2 ");
			panel.add(firingCondition, "span,wrap,growx ,gap 10, gaptop 2");

		} else if (this.petriElement.equals("stochasticTransition")) {
			label.setText("t"+(pw.getPetriNet().getTransitions()+1));
			name.setText("t"+(pw.getPetriNet().getTransitions()+1));
			panel.add(lblFiringCondition, "span 2, gaptop 2 ");
			panel.add(firingCondition, "span,wrap,growx ,gap 10, gaptop 2");

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

		List<String> compartmentList = new Elementdeclerations()
				.getAllCompartmentDeclaration();
		Iterator<String> it2 = compartmentList.iterator();

		String element;
		while (it2.hasNext()) {
			element = it2.next();
			compartment.addItem(element);
		}
	}

	public boolean getAnswer(Point2D point) {
		
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
		
		//dialog.show();
		dialog.setVisible(true);
		
		Integer value = (Integer) pane.getValue();
		if (value != null) {
			if (value.intValue() == JOptionPane.OK_OPTION) {
				
				if(name.getText().length() <1 || name.getText().charAt(0) == '_'){
					return false;
				}
				
				Iterator<BiologicalNodeAbstract> it = pw.getAllNodes().iterator();
				BiologicalNodeAbstract bna;
				
				while(it.hasNext()){
					bna = it.next();
					if(bna.getName().equals(name.getText())){
						return false;
					}
				}
				
				
				// Vertex v = pw.getGraph().createNewVertex();

				if (petriElement.equals("discretePlace")) {
					//System.out.println("anfang");
					Place p = new Place(label.getText(), name.getText(), 1.0, true);
					p.setToken(Double.parseDouble(token.getText()));
					p.setTokenStart(Double.parseDouble(tokenStart.getText()));
					p.setTokenMin(Double.parseDouble(tokenMin.getText()));
					p.setTokenMax(Double.parseDouble(tokenMax.getText()));
					pw.addVertex(p, point);
				} else if (petriElement.equals("continuousPlace")) {
					Place p = new Place(label.getText(), name.getText(), 1.0, false);
					p.setToken(Double.parseDouble(token.getText()));
					p.setTokenStart(Double.parseDouble(tokenStart.getText()));
					p.setTokenMin(Double.parseDouble(tokenMin.getText()));
					p.setTokenMax(Double.parseDouble(tokenMax.getText()));
					pw.addVertex(p, point);
				} else if (petriElement.equals("discreteTransition")) {
					DiscreteTransition t = new DiscreteTransition(
							label.getText(), name.getText());
					t.setDelay(Double.parseDouble(delay.getText()));

					//t.setFireTransition(transitionfire.isSelected());
					t.setFiringCondition(firingCondition.getText());

					pw.addVertex(t, point);
				} else if (petriElement.equals("continiousTransition")) {
					ContinuousTransition t = new ContinuousTransition(
							label.getText(), name.getText());

					//t.setFireTransition(transitionfire.isSelected());
					t.setFiringCondition(firingCondition.getText());
					t.setMaximumSpeed(maxSpeed.getText());

					pw.addVertex(t, point);
				} else if (petriElement.equals("stochasticTransition")) {
					StochasticTransition t = new StochasticTransition(
							label.getText(), name.getText());
					t.setDistribution(distributionList.getSelectedItem()
							.toString());

					//t.setFireTransition(transitionfire.isSelected());
					t.setFiringCondition(firingCondition.getText());

					pw.addVertex(t, point);
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
