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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import cluster.graphdb.DatabaseEntry;

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
import biologicalObjects.nodes.GraphNode;
import biologicalObjects.nodes.PathwayMap;
import biologicalObjects.nodes.Protein;
import biologicalObjects.nodes.RNA;

public class ElementWindow implements ActionListener, ItemListener {

	JPanel p = new JPanel();
	GraphElementAbstract ab;
	GraphInstance graphInstance;
	boolean emptyPane = true;

	boolean colorChanged = false;
	boolean referenceChanged = false;

	private JCheckBox reference;
	private JLabel lblRef;
	private JButton chooseRef;
	private JButton deleteRef;
	private JButton pickOrigin;
	private JButton pickRefs;
	private JButton colorButton;
	private JButton hideNeighbours;
	private JButton showNeighbours;
	private JButton parametersButton;
	private JButton showLabels;
	boolean vertexElement = false;
	JTabbedPane pane = new JTabbedPane();

	BiologicalNodeAbstract ref = null;

	// private Object element;

	public ElementWindow() {

	}

	private void updateWindow(GraphElementAbstract element) {

		// this.element = element;
		// this.ab = (GraphElementAbstract) graphInstance
		// .getPathwayElement(element);
		ab = element;
		PropertyWindowListener pwl = new PropertyWindowListener(element);

		reference = new JCheckBox();
		colorButton = new JButton("Colour");
		hideNeighbours = new JButton("Hide all Neighbours");
		showNeighbours = new JButton("Show all Neighbours");
		parametersButton = new JButton("Parameters");
		showLabels = new JButton("Show Labels");

		colorButton.setBackground(ab.getColor());
		colorButton.setToolTipText("Colour");
		colorButton.setActionCommand("colour");
		colorButton.addActionListener(this);

		JTextField label = new JTextField(20);
		JTextField name = new JTextField(20);

		if (ref != null) {
			label.setText(ref.getLabel());
			name.setText(ref.getName());
		} else {
			label.setText(ab.getLabel());
			name.setText(ab.getName());
		}

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

		// JCheckBox transitionfire = new JCheckBox("Should transition fire:",
		// true);
		// JTextField transitionStatement = new JTextField("true");

		if (ab.isVertex()) {
			BiologicalNodeAbstract bna = (BiologicalNodeAbstract) ab;
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

			JComboBox compartment = new JComboBox();
			addCompartmentItems(compartment);
			AutoCompleteDecorator.decorate(compartment);
			// compartment.setMaximumSize(new Dimension(250,300));
			// BiologicalNodeAbstract bna = (BiologicalNodeAbstract)
			// graphInstance
			// .getPathwayElement(element);
			// BiologicalNodeAbstract bna = (BiologicalNodeAbstract) element;
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

				JTextField firingCondition = new JTextField(4);
				JLabel lblFiringCondition = new JLabel("Firing Condition");
				firingCondition.setText(((Transition) ab).getFiringCondition());
				firingCondition.setName("firingCondition");
				firingCondition.addKeyListener(pwl);

				p.add(lblFiringCondition, "gap 5");
				p.add(firingCondition, "wrap");

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
					JTextField maxSpeed = new JTextField(4);
					JLabel lblMaxSpeed = new JLabel("Maximum Speed");
					maxSpeed.setText(trans.getMaximumSpeed());
					maxSpeed.setName("maximumSpeed");
					maxSpeed.addKeyListener(pwl);

					p.add(lblMaxSpeed, "gap 5");
					p.add(maxSpeed, "wrap");
				}				
			}
			else if (ab instanceof GraphNode) {	
				GraphNode gnode = (GraphNode) ab;
				
				//Show Database IDs
				JTextArea dbids = new JTextArea();
				String dbidstring = new String();
				for (DatabaseEntry dbid : gnode.getSuperNode().dbIds) {
					dbidstring+=dbid.getDatabase()+":\t"+dbid.getId()+"\n";
				}
				dbids.setText(dbidstring);
				p.add(new JLabel("IDs:"), "gap 5");
				p.add(dbids,"wrap, span 3");
				
				//Show Experiment names and values
				JTextArea experiments = new JTextArea();
				String experimentstring = new String();
				for (int i = 0; i < gnode.getSuperNode().biodata.length; i++) {
					experimentstring+=gnode.getSuperNode().biodata[i]+":\t"+
				(gnode.getSuperNode().biodataEntries[i]+"").substring(0, 4)
				+"\n";
				}
				experiments.setText(experimentstring);
				p.add(new JLabel("Exp."),"gap 5");
				p.add(experiments,"wrap, span 3");
				
				//Show GO annotations
				JTextArea goannoations = new JTextArea();
				String annotationstring = new String();
				
				//check if there are any entries:
				int goentries = 0;
				goentries = gnode.getSuperNode().biologicalProcess.length
						+gnode.getSuperNode().molecularFunction.length
						+gnode.getSuperNode().cellularComponent.length;

				if (goentries > 0) {
					// biological process
					annotationstring += "Biological process:\n";
					for (int i = 0; i < gnode.getSuperNode().biologicalProcess.length; i++) {
						annotationstring += "-"
								+ gnode.getSuperNode().biologicalProcess[i]
								+ "\n";
					}
					// molecular function
					annotationstring += "Molecular function:\n";
					for (int i = 0; i < gnode.getSuperNode().molecularFunction.length; i++) {
						annotationstring += "-"
								+ gnode.getSuperNode().molecularFunction[i]
								+ "\n";
					}
					// cellular compartment
					annotationstring += "Cellular component:\n";
					for (int i = 0; i < gnode.getSuperNode().cellularComponent.length; i++) {
						annotationstring += "-"
								+ gnode.getSuperNode().cellularComponent[i]
								+ "\n";
					}
				}
				
				goannoations.setText(annotationstring);
				
				p.add(new JLabel("GO:"),"gap 5");
				p.add(goannoations,"wrap, span 3");
				
				
	//				JTextField aaSequence = new JTextField(20);
	//				aaSequence.setText(protein.getAaSequence());
	//				aaSequence.setName("protein");
	//				aaSequence.addKeyListener(pwl);
	//				p.add(new JLabel("AA-Sequence"), "gap 5 ");
	//				p.add(aaSequence, "wrap, span 3");
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

	private void updateReferences(boolean reference) {

		// CHRIS reimplement of updateReferences

		/*
		 * if (ab.isVertex()) {
		 * 
		 * Pathway pw = graphInstance.getPathway(); BiologicalNodeAbstract bna =
		 * (BiologicalNodeAbstract) pw .getElement(element); Iterator it =
		 * bna.getVertex().getIncidentEdges().iterator();
		 * 
		 * while (it.hasNext()) { Edge e = (Edge) it.next(); Pair p =
		 * e.getEndpoints();
		 * 
		 * BiologicalEdgeAbstract bea = (BiologicalEdgeAbstract) pw
		 * .getElement(e); BiologicalNodeAbstract first =
		 * (BiologicalNodeAbstract) pw .getElement(p.getFirst());
		 * BiologicalNodeAbstract second = (BiologicalNodeAbstract) pw
		 * .getElement(p.getSecond());
		 * 
		 * if (first.isReference() == false && second.isReference() == false) {
		 * bea.setReference(false); } else { bea.setReference(true); } } } else
		 * { ab.setReference(reference); }
		 */
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

			// TODO fail due to old substance API / Bug in API
			Color newColor = JColorChooser.showDialog(w,
					"Choose Element Colour", getElementColor());
			JButton b = ((JButton) e.getSource());
			b.setBackground(newColor);

			ab.setColor(newColor);
			ab.setReference(false);
			ab.setHidden(false);
			ab.setVisible(true);
			// reference.setSelected(false);
			// updateReferences(false);

		} else if ("reference".equals(event)) {
			// TODO calculate References properly
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
				node.setHidden(hide);
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
			BiologicalNodeAbstract bna = (BiologicalNodeAbstract) ab;
			ReferenceDialog dialog = new ReferenceDialog(bna);
			BiologicalNodeAbstract node = dialog.getAnswer();
			if (node != null) {
				bna.setRef(node);
				this.revalidateView();
				w.updateElementTree();
				// System.out.println("node: "+node.getID());
			}
		} else if ("deleteRef".equals(event)) {
			BiologicalNodeAbstract bna = (BiologicalNodeAbstract) ab;
			bna.deleteRef();

			this.revalidateView();
			w.updateElementTree();

		} else if ("pickOrigin".equals(event)) {
			BiologicalNodeAbstract bna = (BiologicalNodeAbstract) ab;

			Pathway pw = graphInstance.getPathway();
			pw = graphInstance.getPathway();
			MyGraph g = pw.getGraph();
			g.getVisualizationViewer().getPickedVertexState()
					.pick(bna.getRef(), true);

			this.revalidateView();

		} else if ("pickRefs".equals(event)) {
			BiologicalNodeAbstract bna = (BiologicalNodeAbstract) ab;
			
			Pathway pw = graphInstance.getPathway();
			pw = graphInstance.getPathway();
			MyGraph g = pw.getGraph();
			//System.out.println("c: "+g.getJungGraph().getVertexCount());
			BiologicalNodeAbstract pick;
			Iterator<BiologicalNodeAbstract> it = bna.getRefs().iterator();
			//System.out.println("size: "+bna.getRefs().size());
			while (it.hasNext()) {
				pick = it.next();
				//System.out.println(pick.getLabel());
				//System.out.println(pick);
				g.getVisualizationViewer().getPickedVertexState()
						.pick(pick, true);

			}
			//System.out.println(g.getVisualizationViewer().getPickedVertexState().getPicked().size());
			this.revalidateView();
		}

		else if ("showParameters".equals(event)) {
			// System.out.println("show parameters");
			ParameterWindow parameterWindow = new ParameterWindow(ab);
		} else if ("showLabels".equals(event)) {
			// System.out.println("click");
			new LabelsWindow(ab);
		}

		GraphInstance.getMyGraph().updateGraph();
	}

	@Override
	public void itemStateChanged(ItemEvent event) {
		System.out.println("item changed");
		// CHRIS probably never executed

		/*
		 * int state = event.getStateChange(); String item = (String)
		 * event.getItem(); if (ab.isVertex()) { BiologicalNodeAbstract bna =
		 * (BiologicalNodeAbstract) graphInstance .getPathwayElement(element);
		 * bna.setCompartment(item); }
		 */
	}
}
