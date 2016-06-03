package graph.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import biologicalElements.Elementdeclerations;
import biologicalElements.GraphElementAbstract;
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract.NodeAttribute;
import biologicalObjects.nodes.DNA;
import biologicalObjects.nodes.Gene;
import biologicalObjects.nodes.PathwayMap;
import biologicalObjects.nodes.Protein;
import biologicalObjects.nodes.RNA;
/*import edu.uci.ics.jung.graph.Edge;
 import edu.uci.ics.jung.graph.Vertex;
 import edu.uci.ics.jung.utils.Pair;*/
import graph.ChangedFlags;
import graph.GraphInstance;
import graph.algorithms.NodeAttributeTypes;
import graph.jung.classes.MyGraph;
import gui.MainWindow;
import gui.MainWindowSingleton;
import gui.eventhandlers.PropertyWindowListener;
import net.miginfocom.swing.MigLayout;
import petriNet.ContinuousTransition;
import petriNet.DiscreteTransition;
import petriNet.PNEdge;
import petriNet.Place;
import petriNet.StochasticTransition;
import petriNet.Transition;
import util.MyJFormattedTextField;
import util.MyNumberFormat;

public class ElementWindow implements ActionListener, ItemListener {

	private JPanel p = new JPanel();
	private GraphElementAbstract ab;
	private GraphInstance graphInstance;
	boolean emptyPane = true;

	private JButton chooseRef;
	private JButton deleteRef;
	private JButton pickOrigin;
	private JButton pickRefs;
	private JButton colorButton;
	private JButton hideNeighbours;
	private JButton showNeighbours;
	private JButton parametersButton;
	private JButton showLabels;

	private JCheckBox constCheck;

	private BiologicalNodeAbstract ref = null;
	private GraphElementAbstract original;

	private JCheckBox knockedOut;
	private MyJFormattedTextField tokenMin;
	private MyJFormattedTextField tokenMax;

	// private Object element;

	public ElementWindow() {

	}

	private void updateWindow(GraphElementAbstract element) {
		p.removeAll();
		// this.element = element;
		// this.ab = (GraphElementAbstract) graphInstance
		// .getPathwayElement(element);
		original = element;
		PropertyWindowListener pwl = new PropertyWindowListener(element);

		JTextField label = new JTextField(20);
		JTextField name = new JTextField(20);

		if (ref != null) {
			this.ab = ref;
			label.setText(ref.getLabel());
			name.setText(ref.getName());
		} else {
			this.ab = original;
			label.setText(ab.getLabel());
			name.setText(ab.getName());
		}

		knockedOut = new JCheckBox();
		colorButton = new JButton("Colour");
		hideNeighbours = new JButton("Hide all Neighbours");
		showNeighbours = new JButton("Show all Neighbours");
		parametersButton = new JButton("Parameters");
		showLabels = new JButton("Show Labels");

		colorButton.setBackground(ab.getColor());
		colorButton.setToolTipText("Colour");
		colorButton.setActionCommand("colour");
		colorButton.addActionListener(this);

		// System.out.println("label: "+ab.getLabel());
		// System.out.println("name: "+ab.getName());
		label.setName("label");
		name.setName("name");

		label.addFocusListener(pwl);
		name.addFocusListener(pwl);

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

		// JCheckBox transitionfire = new JCheckBox("Should transition fire:",
		// true);
		// JTextField transitionStatement = new JTextField("true");

		if (ab.isVertex()) {
			BiologicalNodeAbstract bna = (BiologicalNodeAbstract) original;
			String lbl = "";
			if (bna.hasRef()) {
				lbl = bna.getID() + "_" + bna.getRef().getLabel();
			}
			p.add(new JLabel("Reference:"), "gap 5 ");
			p.add(new JLabel(lbl), "wrap ,span 3");

			if (bna.hasRef()) {
				this.deleteRef = new JButton("Delete Reference");
				deleteRef.setToolTipText("Delete Reference");
				deleteRef.setActionCommand("deleteRef");
				deleteRef.addActionListener(this);
				p.add(deleteRef);
				this.pickOrigin = new JButton("Highlight Origin");
				pickOrigin.setToolTipText("Highlight Origin");
				pickOrigin.setActionCommand("pickOrigin");
				pickOrigin.addActionListener(this);
				p.add(pickOrigin);

			} else {
				this.chooseRef = new JButton("Choose Reference");
				chooseRef.setToolTipText("Choose Reference");
				chooseRef.setActionCommand("chooseRef");
				chooseRef.addActionListener(this);
				p.add(chooseRef);
				if (bna.getRefs().size() > 0) {
					this.pickRefs = new JButton("Highlight References");
					pickRefs.setToolTipText("Highlight References");
					pickRefs.setActionCommand("pickRefs");
					pickRefs.addActionListener(this);
					p.add(pickRefs);
				}
			}

			showLabels.setToolTipText("Show all Labels");
			showLabels.setActionCommand("showLabels");
			showLabels.addActionListener(this);
			p.add(showLabels, "wrap");

			JComboBox<String> compartment = new JComboBox<String>();
			addCompartmentItems(compartment);
			AutoCompleteDecorator.decorate(compartment);

			compartment.setSelectedItem(bna.getCompartment());
			compartment.addItemListener(this);

			p.add(new JLabel("Compartment"), "gap 5 ");
			p.add(compartment, "wrap ,span 3");

			// ADDing Attributes (for all nodes)

			// Show Database IDs
			JTextArea dbids = new JTextArea();
			String dbidstring = new String();
			dbids.setEditable(false);
			dbids.setFont(dbids.getFont().deriveFont(Font.BOLD));
			dbids.setBackground(Color.WHITE);

			p.add(new JLabel("IDs known:"), "gap 5");
			p.add(dbids, "wrap, span 3");

			// Show Experiment names and values
			JTextArea experiments = new JTextArea();
			String experimentstring = new String();
			experiments.setEditable(false);
			experiments.setBackground(Color.WHITE);

			p.add(new JLabel("Dataset:"), "gap 5");
			p.add(experiments, "wrap, span 3");

			// Show GO annotations
			JTextArea goannoations = new JTextArea();
			String annotationstring = new String();
			goannoations.setEditable(false);
			goannoations.setForeground(Color.BLUE);
			goannoations.setBackground(Color.WHITE);

			p.add(new JLabel("Gene Ontology:"), "gap 5");
			p.add(goannoations, "wrap, span 3");

			//Show graph properties (local property)
			JTextArea graphproperties = new JTextArea();
			String propertiesstring = new String();
			graphproperties.setEditable(false);
			graphproperties.setForeground(new Color(255,55,55));
			graphproperties.setBackground(Color.WHITE);

			p.add(new JLabel("Graph properties:"), "gap 5");
			p.add(graphproperties, "wrap, span 3");
			
			
			// JTextField aaSequence = new JTextField(20);
			// aaSequence.setText(protein.getAaSequence());
			// aaSequence.setName("protein");
			// aaSequence.addFocusListener(pwl);
			// p.add(new JLabel("AA-Sequence"), "gap 5 ");
			// p.add(aaSequence, "wrap, span 3");

			String atname, atsvalue;
			double atdvalue;

			ArrayList<String> experimententries = new ArrayList<>(), 
					databaseidentries = new ArrayList<>(), 
					annotationentries = new ArrayList<>(),
					graphpropertiesentries = new ArrayList<>();

			for (NodeAttribute att : bna.getNodeAttributes()) {
				atname = att.getName();
				atsvalue = att.getStringvalue();
				atdvalue = att.getDoublevalue();

				switch (att.getType()) {
				case NodeAttributeTypes.EXPERIMENT:
					experimententries.add(atname + ":\t" + atdvalue + "\n");
					break;

				case NodeAttributeTypes.DATABASE_ID:
					databaseidentries.add(atname + ":\t" + atsvalue + "\n");
					break;

				case NodeAttributeTypes.ANNOTATION:
					annotationentries.add(atname + ":\t" + atsvalue + "\n");
					break;
				
				case NodeAttributeTypes.GRAPH_PROPERTY:
					graphpropertiesentries.add(atname + ":\t" + atdvalue + "\n");
					break;

				default:
					break;
				}
			}

			// Sort for more convenient display
			Collections.sort(experimententries);
			for (String exp : experimententries)
				experimentstring += exp;

			Collections.sort(databaseidentries);
			for (String dbid : databaseidentries)
				dbidstring += dbid;

			Collections.sort(annotationentries);
			for (String ann : annotationentries)
				annotationstring += ann;
			
			Collections.sort(graphpropertiesentries);
			for (String gprop : graphpropertiesentries)
				propertiesstring += gprop;

			experiments.setText(experimentstring);
			dbids.setText(dbidstring);
			goannoations.setText(annotationstring);
			graphproperties.setText(propertiesstring);

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
				aaSequence.addFocusListener(pwl);
				p.add(new JLabel("AA-Sequence"), "gap 5 ");
				p.add(aaSequence, "wrap, span 3");
			}

			else if (ab instanceof DNA) {
				DNA dna = (DNA) ab;
				JTextField ntSequence = new JTextField(20);
				ntSequence.setText(dna.getNtSequence());
				ntSequence.setName("dna");
				ntSequence.addFocusListener(pwl);
				p.add(new JLabel("NT-Sequence"), "gap 5 ");
				p.add(ntSequence, "wrap, span 3");
			} else if (ab instanceof Gene) {
				Gene dna = (Gene) ab;
				JTextField ntSequence = new JTextField(20);
				ntSequence.setText(dna.getNtSequence());
				ntSequence.setName("gene");
				ntSequence.addFocusListener(pwl);
				p.add(new JLabel("NT-Sequence"), "gap 5 ");
				p.add(ntSequence, "wrap, span 3");
			}

			else if (ab instanceof RNA) {
				RNA rna = (RNA) ab;
				JTextField ntSequence = new JTextField(20);
				ntSequence.setText(rna.getNtSequence());
				ntSequence.setName("rna");
				ntSequence.addFocusListener(pwl);
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
				JComboBox<String> placeList = new JComboBox<String>(
						new String[] { "discrete", "continuous" });
				if (place.isDiscrete())
					placeList.setSelectedItem("discrete");
				else
					placeList.setSelectedItem("continuous");
				placeList.setName("placeList");
				placeList.addFocusListener(pwl);
				p.add(lswitchPlace, "gap 5 ");
				p.add(placeList, "wrap");

				MyJFormattedTextField token;
				MyJFormattedTextField tokenStart;

				JLabel lblTokenStart = new JLabel("Token Start");
				if (place.isDiscrete()) {
					token = new MyJFormattedTextField(
							MyNumberFormat.getIntegerFormat());
					tokenStart = new MyJFormattedTextField(
							MyNumberFormat.getIntegerFormat());
					tokenMin = new MyJFormattedTextField(
							MyNumberFormat.getIntegerFormat());
					tokenMax = new MyJFormattedTextField(
							MyNumberFormat.getIntegerFormat());
					token.setText((int) place.getToken() + "");
					tokenStart.setText((int) place.getTokenStart() + "");
					tokenMin.setText((int) place.getTokenMin() + "");
					tokenMax.setText((int) place.getTokenMax() + "");
				} else {
					token = new MyJFormattedTextField(
							MyNumberFormat.getDecimalFormat());
					tokenStart = new MyJFormattedTextField(
							MyNumberFormat.getDecimalFormat());
					tokenMin = new MyJFormattedTextField(
							MyNumberFormat.getDecimalFormat());
					tokenMax = new MyJFormattedTextField(
							MyNumberFormat.getDecimalFormat());
					token.setText(place.getToken() + "");
					tokenStart.setText(place.getTokenStart() + "");
					tokenMin.setText(place.getTokenMin() + "");
					tokenMax.setText(place.getTokenMax() + "");
				}
				JLabel lblToken = new JLabel("Token");

				token.setName("token");
				// token.addFocusListener(pwl);
				token.setEditable(false);
				token.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);

				tokenStart.setName("tokenStart");
				tokenStart.setFocusLostBehavior(JFormattedTextField.COMMIT);
				tokenStart.addFocusListener(pwl);

				tokenMin.setName("tokenMin");
				tokenMin.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
				tokenMin.addFocusListener(pwl);
				JLabel lblTokenMin = new JLabel("min Tokens");

				tokenMax.setName("tokenMax");
				tokenMax.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
				tokenMax.addFocusListener(pwl);
				JLabel lblTokenMax = new JLabel("max Tokens");
				p.add(lblToken, "gap 5 ");
				p.add(token, "wrap");

				p.add(lblTokenStart, "gap 5 ");
				p.add(tokenStart, "");

				constCheck = new JCheckBox("constant");
				constCheck.setActionCommand("constCheck");
				constCheck.addActionListener(this);
				constCheck.setSelected(place.isConstant());
				p.add(constCheck, "wrap");

				if (constCheck.isSelected()) {
					tokenMin.setEnabled(false);
					tokenMax.setEnabled(false);
				}
				p.add(lblTokenMin, "gap 5 ");
				p.add(tokenMin, "wrap");
				p.add(lblTokenMax, "gap 5");
				p.add(tokenMax, "wrap");
			} else if (ab instanceof Transition) {
				JLabel lswitchTrans = new JLabel("Transition Type");
				JComboBox<String> transList = new JComboBox<String>(
						new String[] { DiscreteTransition.class.getName(),
								ContinuousTransition.class.getName(),
								StochasticTransition.class.getName() });
				transList.setSelectedItem(ab.getClass().getCanonicalName());
				transList.setName("transList");
				transList.addFocusListener(pwl);
				p.add(lswitchTrans, "gap 5");
				p.add(transList, "wrap");

				JTextField firingCondition = new JTextField(4);
				JLabel lblFiringCondition = new JLabel("Firing Condition");
				firingCondition.setText(((Transition) ab).getFiringCondition());
				firingCondition.setName("firingCondition");
				firingCondition.addFocusListener(pwl);

				p.add(lblFiringCondition, "gap 5");
				p.add(firingCondition, "wrap");

				if (ab instanceof DiscreteTransition) {
					DiscreteTransition trans = (DiscreteTransition) ab;
					JTextField delay = new JTextField(4);
					JLabel lbldelay = new JLabel("Delay");
					delay.setText(trans.getDelay() + "");
					delay.setName("delay");
					delay.addFocusListener(pwl);

					p.add(lbldelay, "gap 5");
					p.add(delay, "wrap");
				}

				else if (ab instanceof StochasticTransition) {
					StochasticTransition trans = (StochasticTransition) ab;
					String[] disStrings = { "norm", "exp" };
					// Create the combo box, select item at index 4.
					// Indices start at 0, so 4 specifies the pig.
					JComboBox<String> distributionList = new JComboBox<String>(
							disStrings);
					distributionList.setSelectedItem(trans.getDistribution());
					distributionList.setName("disList");
					distributionList.addFocusListener(pwl);
					p.add(new JLabel("Distribution"), "gap 5");
					p.add(distributionList, "wrap");
				}

				else if (ab instanceof ContinuousTransition) {
					ContinuousTransition trans = (ContinuousTransition) ab;
					JTextField maxSpeed = new JTextField(4);
					JLabel lblMaxSpeed = new JLabel("Maximum Speed");
					maxSpeed.setText(trans.getMaximumSpeed());
					maxSpeed.setName("maximumSpeed");
					maxSpeed.addFocusListener(pwl);

					p.add(lblMaxSpeed, "gap 5");
					p.add(maxSpeed, "wrap");

					if (trans.isKnockedOut()) {
						maxSpeed.setEnabled(false);
					}
				}
			}

		} else if (ab.isEdge()) {
			// System.out.println("edge");
			if (ab instanceof PNEdge) {

				PNEdge e = (PNEdge) ab;
				JTextField prob = new JTextField(4);
				prob.setText(e.getActivationProbability() + "");
				prob.setName("activationProb");
				prob.addFocusListener(pwl);
				JLabel lblProb = new JLabel("activation Probability");
				JTextField function = new JTextField(5);
				function.setText(e.getFunction());
				function.setName("function");
				function.addFocusListener(pwl);
				JLabel lblpassingTokens = new JLabel("Edge Function");
				JTextField lowBoundary = new JTextField(5);
				lowBoundary.setText(e.getLowerBoundary() + "");
				lowBoundary.setName("lowBoundary");
				lowBoundary.addFocusListener(pwl);
				JLabel lblLow = new JLabel("lower Boundary");
				JTextField upBoundary = new JTextField(5);
				upBoundary.setText(e.getUpperBoundary() + "");
				upBoundary.setName("upBoundary");
				upBoundary.addFocusListener(pwl);
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

		if (ab instanceof Transition) {
			knockedOut.setSelected(((Transition) ab).isKnockedOut());
			knockedOut.setToolTipText("Knock out");
			knockedOut.setActionCommand("knockedOut");
			knockedOut.addActionListener(this);
			p.add(new JLabel("Knocked out"), "gap 5 ");
			p.add(knockedOut, "wrap ,span 3");
		}

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
		parametersButton.setToolTipText("Show all Parameters");
		parametersButton.setActionCommand("showParameters");
		parametersButton.addActionListener(this);
		p.add(parametersButton);

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
		// System.out.println("revalidate");
		graphInstance = new GraphInstance();

		if (graphInstance.getSelectedObject() instanceof BiologicalNodeAbstract
				&& ((BiologicalNodeAbstract) graphInstance.getSelectedObject())
						.hasRef()) {
			this.ref = ((BiologicalNodeAbstract) graphInstance
					.getSelectedObject()).getRef();
		} else {
			this.ref = null;
		}

		if (emptyPane) {
			updateWindow(graphInstance.getSelectedObject());
			p.setVisible(true);
			p.repaint();
			p.revalidate();
			emptyPane = false;
		} else {
			// try{

			Thread worker = new Thread() {
				public void run() {
					// dirty hack
					try {
						p.removeAll();
					} catch (Exception e) {
						revalidateView();
					}
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							updateWindow(graphInstance.getSelectedObject());
							p.setVisible(true);
							p.repaint();
							p.revalidate();
						}
					});
				}
			};
			worker.start();

		}
	}

	private void addCompartmentItems(JComboBox<String> compartment) {

		List<String> compartmentList = new Elementdeclerations()
				.getAllCompartmentDeclaration();
		Iterator<String> it = compartmentList.iterator();

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
		MainWindow w = MainWindowSingleton.getInstance();
		String event = e.getActionCommand();

		if ("colour".equals(event)) {

			// TODO fail due to old substance API / Bug in API
			Color newColor = JColorChooser.showDialog(w,
					"Choose Element Colour", getElementColor());
			JButton b = ((JButton) e.getSource());
			b.setBackground(newColor);

			ab.setColor(newColor);
			ab.setVisible(true);
			// reference.setSelected(false);
			// updateReferences(false);

		} else if ("pathwayLink".equals(event)) {
			if (JOptionPane
					.showConfirmDialog(
							w,
							"If you delete the PathwayLink the Sub-Pathway (with all eventually made changes within it) will be lost. Do you want to do this?",
							"Delete the Sub-Pathway...",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION
					&& ab instanceof PathwayMap) {
				((PathwayMap) ab).setPathwayLink(null);
				w.updateElementTree();
				w.updatePathwayTree();
				ab.setColor(Color.white);
			}
			w.updateElementProperties();
		} else if (("hideNeighbours".equals(event) || ("showNeighbours"
				.equals(event))) && ab instanceof BiologicalNodeAbstract) {
			// TODO visible wird noch nicht gehandelt in transformators
			Pathway pw = graphInstance.getPathway();
			boolean hide = "hideNeighbours".equals(event);
			BiologicalNodeAbstract bna = (BiologicalNodeAbstract) ab;

			Iterator<BiologicalEdgeAbstract> it = pw.getGraph().getJungGraph()
					.getIncidentEdges(bna).iterator();
			BiologicalEdgeAbstract bea;

			while (it.hasNext()) {
				// System.out.println(!hide);
				bea = it.next();
				bea.setVisible(!hide);
				// bea.setLabel(!hide+"");
			}

			Iterator<BiologicalNodeAbstract> it2 = pw.getGraph().getJungGraph()
					.getNeighbors(bna).iterator();

			BiologicalNodeAbstract node;
			while (it2.hasNext()) {
				// System.out.println("drin");
				node = it2.next();
				node.setVisible(!hide);
			}

		} else if ("dirChanger".equals(event) && ab.isEdge()) {
			Pathway pw = graphInstance.getPathway();
			PNEdge edge = (PNEdge) ab;

			PNEdge newEdge = new PNEdge(edge.getTo(), edge.getFrom(),
					edge.getLabel(), edge.getName(),
					edge.getBiologicalElement(), edge.getFunction());
			newEdge.setUpperBoundary(edge.getUpperBoundary());
			newEdge.setLowerBoundary(edge.getLowerBoundary());
			newEdge.setActivationProbability(edge.getActivationProbability());
			newEdge.setDirected(true);
			// pw = graphInstance.getPathway();
			MyGraph g = pw.getGraph();
			g.getJungGraph().removeEdge(edge);
			// g.getEdgeStringer().removeEdge(edge.getEdge());
			pw.removeElement(edge);
			pw.addEdge(newEdge);
			// g.getVisualizationViewer().getPickedState().clearPickedEdges();
			graphInstance.setSelectedObject(newEdge);

			ab = newEdge;
		} else if ("chooseRef".equals(event)) {
			BiologicalNodeAbstract bna = (BiologicalNodeAbstract) original;
			ReferenceDialog dialog = new ReferenceDialog(bna);
			BiologicalNodeAbstract node = dialog.getAnswer();
			if (node != null) {
				bna.setRef(node);
				this.revalidateView();
				w.updateElementTree();
				// System.out.println("node: "+node.getID());
			}
		} else if ("deleteRef".equals(event)) {
			BiologicalNodeAbstract bna = (BiologicalNodeAbstract) original;
			bna.deleteRef();

			this.revalidateView();
			w.updateElementTree();

		} else if ("pickOrigin".equals(event)) {
			BiologicalNodeAbstract bna = (BiologicalNodeAbstract) original;

			Pathway pw = graphInstance.getPathway();
			pw = graphInstance.getPathway();
			MyGraph g = pw.getGraph();
			g.getVisualizationViewer().getPickedVertexState()
					.pick(bna.getRef(), true);

			this.revalidateView();

		} else if ("pickRefs".equals(event)) {
			BiologicalNodeAbstract bna = (BiologicalNodeAbstract) original;

			Pathway pw = graphInstance.getPathway();
			pw = graphInstance.getPathway();
			MyGraph g = pw.getGraph();
			// System.out.println("c: "+g.getJungGraph().getVertexCount());
			BiologicalNodeAbstract pick;
			Iterator<BiologicalNodeAbstract> it = bna.getRefs().iterator();
			// System.out.println("size: "+bna.getRefs().size());
			while (it.hasNext()) {
				pick = it.next();
				// System.out.println(pick.getLabel());
				// System.out.println(pick);
				g.getVisualizationViewer().getPickedVertexState()
						.pick(pick, true);

			}
			// System.out.println(g.getVisualizationViewer().getPickedVertexState().getPicked().size());
			this.revalidateView();
		}

		else if ("showParameters".equals(event)) {
			// System.out.println("show parameters");
			new ParameterWindow(ab);
			this.updateWindow(ab);
			p.revalidate();
		} else if ("showLabels".equals(event)) {
			// System.out.println("click");
			new LabelsWindow(ab);
		} else if ("knockedOut".equals(event)) {

			((Transition) ab).setKnockedOut(knockedOut.isSelected());
			this.updateWindow(ab);
			p.revalidate();
			// p.repaint();
			Pathway pw = new GraphInstance().getPathway();
			pw.handleChangeFlags(ChangedFlags.PNPROPERTIES_CHANGED);

			// System.out.println("knocked out");
		} else if ("constCheck".equals(event)) {
			if (ab instanceof BiologicalNodeAbstract) {
				BiologicalNodeAbstract bna = (BiologicalNodeAbstract) ab;
				if (constCheck.isSelected()) {
					this.tokenMin.setEnabled(false);
					this.tokenMax.setEnabled(false);
					bna.setConstant(true);
				} else {
					this.tokenMin.setEnabled(true);
					this.tokenMax.setEnabled(true);
					bna.setConstant(false);
				}
				Pathway pw = new GraphInstance().getPathway();
				pw.handleChangeFlags(ChangedFlags.EDGEWEIGHT_CHANGED);
			}
			// System.out.println(this.constCheck.isSelected());

		}

		GraphInstance.getMyGraph().updateGraph();
	}

	@Override
	public void itemStateChanged(ItemEvent event) {
		String item = (String) event.getItem();
		if (ab.isVertex()) {
			((BiologicalNodeAbstract) ab).setCompartment(item);
		}

	}
}
