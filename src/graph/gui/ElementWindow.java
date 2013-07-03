package graph.gui;

/*import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.utils.Pair;*/
import graph.GraphInstance;
import graph.jung.classes.MyGraph;
import gui.MainWindow;
import gui.MainWindowSingelton;
import gui.eventhandlers.PropertyWindowListener;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import petriNet.ContinuousTransition;
import petriNet.DiscreteTransition;
import petriNet.PNEdge;
import petriNet.Place;
import petriNet.StochasticTransition;
import petriNet.Transition;
import biologicalElements.Elementdeclerations;
import biologicalElements.GraphElementAbstract;
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.DNA;
import biologicalObjects.nodes.Gene;
import biologicalObjects.nodes.Protein;
import biologicalObjects.nodes.RNA;
import biologicalObjects.nodes.PathwayMap;

public class ElementWindow implements ActionListener, ItemListener {

	JPanel p = new JPanel();
	GraphElementAbstract ab;
	GraphInstance graphInstance;
	boolean emptyPane = true;

	boolean colorChanged = false;
	boolean referenceChanged = false;

	private JCheckBox reference;
	private JButton colorButton;
	private JButton hideNeighbours;
	private JButton showNeighbours;
	boolean vertexElement = false;
	JTabbedPane pane = new JTabbedPane();

	private Object element;

	public ElementWindow() {
System.out.println("gebaut");
	}

	private void updateWindow(Object element) {

		this.element = element;
		this.ab = (GraphElementAbstract) graphInstance
				.getPathwayElement(element);

		PropertyWindowListener pwl = new PropertyWindowListener(element);

		reference = new JCheckBox();
		colorButton = new JButton("Colour");
		hideNeighbours = new JButton("Hide all Neighbours");
		showNeighbours = new JButton("Show all Neighbours");

		colorButton.setBackground(ab.getColor());
		colorButton.setToolTipText("Colour");
		colorButton.setActionCommand("colour");
		colorButton.addActionListener(this);

		JTextField label = new JTextField(20);
		JTextField name = new JTextField(20);

		label.setText(ab.getLabel());
		name.setText(ab.getName());

		// System.out.println("label: "+ab.getLabel());
		// System.out.println("name: "+ab.getName());
		label.setName("label");
		name.setName("name");

		label.addKeyListener(pwl);
		name.addKeyListener(pwl);

		reference.setSelected(ab.isReference());
		reference.setToolTipText("Set this element to a reference");
		reference.setActionCommand("reference");
		reference.addActionListener(this);

		MigLayout headerlayout = new MigLayout("fillx",
				"[right]rel[grow,fill]", "");
		JPanel headerPanel = new JPanel(headerlayout);
		// headerPanel.setBackground(new Color(192, 215, 227));
		headerPanel.add(new JLabel(ab.getBiologicalElement()), "");
		headerPanel.add(new JSeparator(), "gap 10");

		MigLayout layout = new MigLayout("fillx", "[grow,fill]", "");
		p.setLayout(layout);
		p.add(new JLabel("Element"), "gap 5 ");
		p.add(new JLabel(ab.getBiologicalElement()), "wrap,span 3");
		p.add(new JLabel("Label"), "gap 5 ");
		p.add(label, "wrap,span 3");
		p.add(new JLabel("Name"), "gap 5 ");
		p.add(name, "wrap ,span 3");

		JCheckBox transitionfire = new JCheckBox("Should tranistion fire:",
				true);
		JTextField transitionStatement = new JTextField("time>9.8");

		if (ab.isVertex()) {
			JComboBox compartment = new JComboBox();
			addCompartmentItems(compartment);
			AutoCompleteDecorator.decorate(compartment);
			// compartment.setMaximumSize(new Dimension(250,300));
			BiologicalNodeAbstract bna = (BiologicalNodeAbstract) graphInstance
					.getPathwayElement(element);
			compartment.setSelectedItem(bna.getCompartment());
			compartment.addItemListener(this);

			p.add(new JLabel("Compartment"), "gap 5 ");
			p.add(compartment, "wrap ,span 3");

			if (ab instanceof PathwayMap) {
				p.add(new JLabel("Linked to Pathway"), "gap 5 ");
				boolean b = ((PathwayMap) ab).getPathwayLink() == null;
				JCheckBox linked = new JCheckBox("", !b);
				linked.setToolTipText("Shows whether there is a connected Pathway in Memory to this Map (uncheck the Box to delete that Pathway)");
				linked.setActionCommand("pathwayLink");
				linked.addActionListener(this);
				linked.setEnabled(!b);
				p.add(linked, "wrap ,span 3");
			} else if (ab instanceof Protein) {
				Protein protein = (Protein) ab;
				JTextField aaSequence = new JTextField(20);
				aaSequence.setText(protein.getAaSequence());
				aaSequence.setName("protein");
				aaSequence.addKeyListener(pwl);
				p.add(new JLabel("AA-Sequence"), "gap 5 ");
				p.add(aaSequence, "wrap, span 3");
			}

			else if (ab instanceof DNA) {
				DNA dna = (DNA) ab;
				JTextField ntSequence = new JTextField(20);
				ntSequence.setText(dna.getNtSequence());
				ntSequence.setName("dna");
				ntSequence.addKeyListener(pwl);
				p.add(new JLabel("NT-Sequence"), "gap 5 ");
				p.add(ntSequence, "wrap, span 3");
			} else if (ab instanceof Gene) {
				Gene dna = (Gene) ab;
				JTextField ntSequence = new JTextField(20);
				ntSequence.setText(dna.getNtSequence());
				ntSequence.setName("gene");
				ntSequence.addKeyListener(pwl);
				p.add(new JLabel("NT-Sequence"), "gap 5 ");
				p.add(ntSequence, "wrap, span 3");
			}

			else if (ab instanceof RNA) {
				RNA rna = (RNA) ab;
				JTextField ntSequence = new JTextField(20);
				ntSequence.setText(rna.getNtSequence());
				ntSequence.setName("rna");
				ntSequence.addKeyListener(pwl);
				p.add(new JLabel("NT-Sequence"), "gap 5 ");
				p.add(ntSequence, "wrap, span 3");
			} else if (ab instanceof Place) {
				p.add(new JLabel("ID"), "gap 5 ");
				JTextField id = new JTextField(20);
				id.setText("P" + Integer.toString(ab.getID()));
				id.setEditable(false);
				p.add(id, "wrap ,span 3");
				Place place = (Place) ab;

				JLabel lswitchPlace = new JLabel("Place Type");
				JComboBox placeList = new JComboBox(new String[] { "discrete",
						"continuous" });
				if (place.isDiscrete())
					placeList.setSelectedItem("discrete");
				else
					placeList.setSelectedItem("continuous");
				placeList.setName("placeList");
				placeList.addActionListener(pwl);
				p.add(lswitchPlace, "gap 5 ");
				p.add(placeList, "wrap");

				JTextField token = new JTextField(4);
				JLabel lblToken = new JLabel("Token");
				token.setText(place.getToken() + "");
				token.setName("token");
				// token.addKeyListener(pwl);
				token.setEditable(false);

				JTextField tokenStart = new JTextField(4);
				JLabel lblTokenStart = new JLabel("Token Start");
				tokenStart.setText(place.getTokenStart() + "");
				tokenStart.setName("tokenStart");
				tokenStart.addKeyListener(pwl);

				JTextField tokenMin = new JTextField(4);
				tokenMin.setText(place.getTokenMin() + "");
				tokenMin.setName("tokenMin");
				tokenMin.addKeyListener(pwl);
				JLabel lblTokenMin = new JLabel("min Tokens");
				JTextField tokenMax = new JTextField(4);
				tokenMax.setText(place.getTokenMax() + "");
				tokenMax.setName("tokenMax");
				tokenMax.addKeyListener(pwl);
				JLabel lblTokenMax = new JLabel("max Tokens");
				p.add(lblToken, "gap 5 ");
				p.add(token, "wrap");

				p.add(lblTokenStart, "gap 5 ");
				p.add(tokenStart, "wrap");

				p.add(lblTokenMin, "gap 5 ");
				p.add(tokenMin, "wrap");
				p.add(lblTokenMax, "gap 5");
				p.add(tokenMax, "wrap");
			} else if (ab instanceof Transition) {
				JLabel lswitchTrans = new JLabel("Transition Type");
				JComboBox transList = new JComboBox(new String[] {
						DiscreteTransition.class.getName(),
						ContinuousTransition.class.getName(),
						StochasticTransition.class.getName() });
				transList.setSelectedItem(ab.getClass().getCanonicalName());
				transList.setName("transList");
				transList.addActionListener(pwl);
				p.add(lswitchTrans, "gap 5");
				p.add(transList, "wrap");
				if (ab instanceof DiscreteTransition) {
					DiscreteTransition trans = (DiscreteTransition) ab;
					JTextField delay = new JTextField(4);
					JLabel lbldelay = new JLabel("Delay");
					delay.setText(trans.getDelay() + "");
					delay.setName("delay");
					delay.addKeyListener(pwl);

					p.add(lbldelay, "gap 5");
					p.add(delay, "wrap");
				}

				else if (ab instanceof StochasticTransition) {
					StochasticTransition trans = (StochasticTransition) ab;
					String[] disStrings = { "norm", "exp" };
					// Create the combo box, select item at index 4.
					// Indices start at 0, so 4 specifies the pig.
					JComboBox distributionList = new JComboBox(disStrings);
					distributionList.setSelectedItem(trans.getDistribution());
					distributionList.setName("disList");
					distributionList.addActionListener(pwl);
					p.add(new JLabel("Distribution"), "gap 5");
					p.add(distributionList, "wrap");
				}

				else if (ab instanceof ContinuousTransition) {
					ContinuousTransition trans = (ContinuousTransition) ab;
				}
			}
		} else if (ab.isEdge()) {
			// System.out.println("edge");
			if (ab instanceof PNEdge) {

				PNEdge e = (PNEdge) ab;
				JTextField prob = new JTextField(4);
				prob.setText(e.getActivationProbability() + "");
				prob.setName("activationProb");
				prob.addKeyListener(pwl);
				JLabel lblProb = new JLabel("activation Probability");
				JTextField function = new JTextField(5);
				function.setText(e.getFunction());
				function.setName("function");
				function.addKeyListener(pwl);
				JLabel lblpassingTokens = new JLabel("Edge Function");
				JTextField lowBoundary = new JTextField(5);
				lowBoundary.setText(e.getLowerBoundary() + "");
				lowBoundary.setName("lowBoundary");
				lowBoundary.addKeyListener(pwl);
				JLabel lblLow = new JLabel("lower Boundary");
				JTextField upBoundary = new JTextField(5);
				upBoundary.setText(e.getUpperBoundary() + "");
				upBoundary.setName("upBoundary");
				upBoundary.addKeyListener(pwl);
				JLabel lblUp = new JLabel("upper Boundary");

				// String[] types = { "discrete", "continuous", "inhibition" };
				// Create the combo box, select item at index 4.
				// Indices start at 0, so 4 specifies the pig.
				// JLabel typeList = new JComboBox(types);

				p.add(new JLabel("Edge Type"), "gap 5 ");
				p.add(new JLabel(e.getType()), "wrap");

				JButton dirChanger = new JButton("Change Direction");
				dirChanger.setActionCommand("dirChanger");
				dirChanger.addActionListener(this);
				p.add(dirChanger, "wrap");

				p.add(lblProb, "gap 5");
				p.add(prob, "wrap");
				p.add(lblpassingTokens, "gap 5");
				p.add(function, "wrap");
				p.add(lblLow, "gap 5");
				p.add(lowBoundary, "wrap");
				p.add(lblUp, "gap 5");
				p.add(upBoundary, "wrap");

			}
		}

		p.add(new JLabel("Reference"), "gap 5 ");
		p.add(reference, "wrap ,span 3");
		if (ab.isVertex()) {

			hideNeighbours
					.setToolTipText("Sets all Neighbors of the selected Node to Reference");
			hideNeighbours.setActionCommand("hideNeighbours");
			hideNeighbours.addActionListener(this);
			hideNeighbours.setMaximumSize(new Dimension(120, 30));
			showNeighbours
					.setToolTipText("Delete Reference flag of all Neighbours of the current Node");
			showNeighbours.setActionCommand("showNeighbours");
			showNeighbours.addActionListener(this);
			showNeighbours.setMaximumSize(new Dimension(120, 30));
			p.add(showNeighbours);
			p.add(hideNeighbours);
		}
		p.add(colorButton, "gap 5");

	}

	public Color getElementColor() {
		return ab.getColor();
	}

	public JPanel getPanel() {
		p.setVisible(false);
		return p;
	}

	public void revalidateView() {

		graphInstance = new GraphInstance();

		if (emptyPane) {
			updateWindow(graphInstance.getSelectedObject());
			p.setVisible(true);
			p.repaint();
			p.revalidate();
			emptyPane = false;
		} else {
			p.removeAll();
			updateWindow(graphInstance.getSelectedObject());
			p.setVisible(true);
			p.repaint();
			p.revalidate();
		}
	}

	private void updateReferences(boolean reference) {

		if (ab.isVertex()) {

			Pathway pw = graphInstance.getPathway();
			BiologicalNodeAbstract bna = (BiologicalNodeAbstract) pw
					.getElement(element);
			Iterator it = bna.getVertex().getIncidentEdges().iterator();

			while (it.hasNext()) {
				Edge e = (Edge) it.next();
				Pair p = e.getEndpoints();

				BiologicalEdgeAbstract bea = (BiologicalEdgeAbstract) pw
						.getElement(e);
				BiologicalNodeAbstract first = (BiologicalNodeAbstract) pw
						.getElement(p.getFirst());
				BiologicalNodeAbstract second = (BiologicalNodeAbstract) pw
						.getElement(p.getSecond());

				if (first.isReference() == false
						&& second.isReference() == false) {
					bea.setReference(false);
				} else {
					bea.setReference(true);
				}
			}
		} else {
			ab.setReference(reference);
		}
	}

	private void addCompartmentItems(JComboBox compartment) {

		List compartmentList = new Elementdeclerations()
				.getAllCompartmentDeclaration();
		Iterator it = compartmentList.iterator();

		while (it.hasNext()) {
			String element = it.next().toString();
			compartment.addItem(element);
		}
	}

	public void removeAllElements() {
		emptyPane = true;
		p.removeAll();
		p.setVisible(false);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		MainWindow w = MainWindowSingelton.getInstance();
		String event = e.getActionCommand();

		if ("colour".equals(event)) {

			Color newColor = JColorChooser.showDialog(w,
					"Choose Element Colour", getElementColor());
			JButton b = ((JButton) e.getSource());
			b.setBackground(newColor);

			ab.setColor(newColor);
			ab.setReference(false);
			ab.setHidden(false);
			ab.setVisible(true);
			reference.setSelected(false);
			updateReferences(false);

		} else if ("reference".equals(event)) {

			ab.setReference(reference.isSelected());
			if (reference.isSelected()) {
				ab.setHidden(true);
				updateReferences(true);
			} else {
				ab.setHidden(false);
				updateReferences(false);
			}
			colorButton.setBackground(ab.getColor());
		} else if ("pathwayLink".equals(event)) {
			if (JOptionPane
					.showConfirmDialog(
							w,
							"If you delete the PathwayLink the Sub-Pathway (with all eventually made changes within it) will be lost. Do you want to do this?",
							"Delete the Sub-Pathway...",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION && ab instanceof PathwayMap) {
				((PathwayMap)ab).setPathwayLink(null);
				w.updateElementTree();
				w.updatePathwayTree();
				ab.setColor(Color.white);
			}
			w.updateElementProperties();
		} else if (("hideNeighbours".equals(event) || ("showNeighbours"
				.equals(event))) && ab instanceof BiologicalNodeAbstract) {
			Pathway pw = graphInstance.getPathway();
			boolean hide = "hideNeighbours".equals(event);
			BiologicalNodeAbstract bna = (BiologicalNodeAbstract) ab;
			for (BiologicalEdgeAbstract edge : pw.getAllEdges())
				for (Object edge2 : bna.getVertex().getIncidentEdges())
					if (((Edge) edge2).equals(edge.getEdge()))
						edge.setVisible(!hide);

			for (Iterator i = bna.getVertex().getNeighbors().iterator(); i
					.hasNext();) {
				Vertex v = (Vertex) i.next();
				for (Iterator j = pw.getAllNodes().iterator(); j.hasNext();) {
					BiologicalNodeAbstract t = (BiologicalNodeAbstract) j
							.next();
					if (t.getVertex().equals(v))
						t.setVisible(!hide);
				}
			}
		} else if ("dirChanger".equals(event) && ab.isEdge()) {
			Pathway pw = graphInstance.getPathway();
			PNEdge edge = (PNEdge) ab;

			PNEdge newEdge = new PNEdge(pw.getGraph().createEdge(
					(Vertex) edge.getEdge().getEndpoints().getSecond(),
					(Vertex) edge.getEdge().getEndpoints().getFirst(), true),
					edge.getLabel(), edge.getName(),
					edge.getBiologicalElement(), edge.getFunction());
			newEdge.setUpperBoundary(edge.getUpperBoundary());
			newEdge.setLowerBoundary(edge.getLowerBoundary());
			newEdge.setActivationProbability(edge.getActivationProbability());
			newEdge.setDirected(true);
			pw = graphInstance.getPathway();
			MyGraph g = pw.getGraph();
			g.getJungGraph().removeEdge(edge.getEdge());
			g.getEdgeStringer().removeEdge(edge.getEdge());
			pw.removeElement(edge.getEdge());
			newEdge = (PNEdge) pw.addElement(newEdge);
			g.getVisualizationViewer().getPickedState().clearPickedEdges();
			graphInstance.setSelectedObject(newEdge);

			ab = newEdge;
		}
		GraphInstance.getMyGraph().updateGraph();
	}

	@Override
	public void itemStateChanged(ItemEvent event) {

		int state = event.getStateChange();
		String item = (String) event.getItem();
		if (ab.isVertex()) {
			BiologicalNodeAbstract bna = (BiologicalNodeAbstract) graphInstance
					.getPathwayElement(element);
			bna.setCompartment(item);
		}
	}
}
