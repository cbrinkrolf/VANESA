package gui.eventhandlers;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Iterator;

import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import biologicalElements.Elementdeclerations;
import biologicalElements.GraphElementAbstract;
import biologicalElements.IDAlreadyExistException;
import biologicalElements.Pathway;
import biologicalObjects.edges.petriNet.PNEdge;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.DNA;
import biologicalObjects.nodes.DynamicNode;
import biologicalObjects.nodes.Gene;
import biologicalObjects.nodes.Protein;
import biologicalObjects.nodes.RNA;
import biologicalObjects.nodes.petriNet.ContinuousTransition;
import biologicalObjects.nodes.petriNet.DiscreteTransition;
import biologicalObjects.nodes.petriNet.Place;
import biologicalObjects.nodes.petriNet.StochasticTransition;
import biologicalObjects.nodes.petriNet.Transition;
//import edu.uci.ics.jung.graph.ArchetypeVertex;
//import edu.uci.ics.jung.graph.Vertex;
//import edu.uci.ics.jung.visualization.Layout;
import graph.ChangedFlags;
import graph.GraphInstance;
import graph.gui.Boundary;
import graph.jung.graphDrawing.VertexShapes;
import gui.MainWindow;

public class PropertyWindowListener implements FocusListener {

	private GraphElementAbstract geb;
	private GraphInstance graphInstance = new GraphInstance();

	// Object element;

	public PropertyWindowListener(GraphElementAbstract element) {
		if (element instanceof BiologicalNodeAbstract
				&& ((BiologicalNodeAbstract) element).hasRef()) {
			geb = ((BiologicalNodeAbstract) graphInstance.getSelectedObject())
					.getRef();
		} else {
			geb = element;// graphInstance.getPathwayElement(element);
			// this.element = element;
		}
	}

	@Override
	public void focusGained(FocusEvent event) {
		if (event.getComponent().getName().equals("comment")) {

		} else {
			event.getComponent().setBackground(new Color(200, 227, 255));
		}
	}

	@Override
	public void focusLost(FocusEvent event) {
		Pathway pw = new GraphInstance().getPathway();
		String source = event.getComponent().getName();
		String text;
		if (source.equals("label")) {
			text = ((JTextField) event.getSource()).getText().trim();
			if (!text.equals("") && !text.equals(geb.getLabel())) {
				geb.setLabel(text);
				MainWindow.getInstance().updateElementTree();
				if (geb instanceof BiologicalNodeAbstract) {
					pw.handleChangeFlags(ChangedFlags.NODE_CHANGED);
				} else {
					pw.handleChangeFlags(ChangedFlags.EDGE_CHANGED);
				}
			}
		} else if (source.equals("name")) {
			text = ((JTextField) event.getSource()).getText().trim();
			if (!text.equals("") && !text.equals(geb.getName())) {
				geb.setName(text);
				MainWindow.getInstance().updateElementTree();
				if (geb instanceof BiologicalNodeAbstract) {
					pw.handleChangeFlags(ChangedFlags.NODE_CHANGED);
				} else {
					pw.handleChangeFlags(ChangedFlags.EDGE_CHANGED);
				}
			}
		} else if (source.equals("comment")) {
			text = ((JTextField) event.getSource()).getText().trim();
			if (!text.equals("") && !text.equals(geb.getComments())) {
				geb.setComments(text);
			}
		} else if (source.equals("protein")) {
			text = ((JTextField) event.getSource()).getText().trim();
			Protein protein = (Protein) geb;
			if (!text.equals("") && !text.equals(protein.getAaSequence())) {
				protein.setAaSequence(text);
			}
		} else if (source.equals("dna")) {
			text = ((JTextField) event.getSource()).getText().trim();
			DNA dna = (DNA) geb;
			if (!text.equals("") && !text.equals(dna.getNtSequence())) {
				dna.setNtSequence(text);
			}
		} else if (source.equals("gene")) {
			text = ((JTextField) event.getSource()).getText().trim();
			Gene gene = (Gene) geb;
			if (!text.equals("") && !text.equals(gene.getNtSequence())) {
				gene.setNtSequence(text);
			}
		} else if (source.equals("rna")) {
			text = ((JTextField) event.getSource()).getText().trim();
			RNA rna = (RNA) geb;
			if (!text.equals("") && !text.equals(rna.getNtSequence())) {
				rna.setNtSequence(text);
			}
		}else if (source.equals("concentration")) {
			Number n = (Number) ((JFormattedTextField) event.getSource())
					.getValue();
			BiologicalNodeAbstract bna = (BiologicalNodeAbstract) geb;
			if (n != null && !n.equals("") && !n.equals(bna.getConcentrationStart())) {
				double conc = n.doubleValue();
				bna.setConcentration(conc);
			}

		}else if (source.equals("concentrationMin")) {
			Number n = (Number) ((JFormattedTextField) event.getSource())
					.getValue();
			BiologicalNodeAbstract bna = (BiologicalNodeAbstract) geb;
			if (n != null && !n.equals("") && !n.equals(bna.getConcentrationStart())) {
				double concentrationMin = n.doubleValue();
				bna.setConcentrationMin(concentrationMin);
			}
		} else if (source.equals("concentrationStart")) {
			Number n = (Number) ((JFormattedTextField) event.getSource())
					.getValue();
			BiologicalNodeAbstract bna = (BiologicalNodeAbstract) geb;
			if (n != null && !n.equals("") && !n.equals(bna.getConcentrationStart())) {
				double concentrationStart = n.doubleValue();
				bna.setConcentrationStart(concentrationStart);
				bna.setConcentration(bna.getConcentrationStart());
			}
		} else if (source.equals("concentrationMax")) {
			Number n = (Number) ((JFormattedTextField) event.getSource())
					.getValue();
			BiologicalNodeAbstract bna = (BiologicalNodeAbstract) geb;
			if (n != null && !n.equals("") && !n.equals(bna.getConcentrationStart())) {
				double concentrationMax = n.doubleValue();
				bna.setConcentrationMax(concentrationMax);
			}
		}
		// for Places
		else if (source.equals("token")) {
			Number n = (Number) ((JFormattedTextField) event.getSource())
					.getValue();
			Place p = (Place) geb;
			if (n != null && !n.equals("") && !n.equals(p.getTokenStart())) {
				double tokens = n.doubleValue();
				p.setToken(tokens);
			}

		} else if (source.equals("tokenMin")) {
			Number n = (Number) ((JFormattedTextField) event.getSource())
					.getValue();
			Place p = (Place) geb;
			if (n != null && !n.equals("") && !n.equals(p.getTokenStart())) {
				double tokenMin = n.doubleValue();
				p.setTokenMin(tokenMin);
				pw.handleChangeFlags(ChangedFlags.BOUNDARIES_CHANGED);
				Boundary b;
				if(pw.getChangedBoundaries().containsKey(p)){
					b = pw.getChangedBoundaries().get(p);
				}else{
					b = new Boundary();
					pw.getChangedBoundaries().put(p, b);
				}
				b.setLowerBoundary(tokenMin);

			}
		} else if (source.equals("tokenStart")) {
			Number n = (Number) ((JFormattedTextField) event.getSource())
					.getValue();
			Place p = (Place) geb;
			if (n != null && !n.equals("") && !n.equals(p.getTokenStart())) {

				double tokenStart = n.doubleValue();// Double.parseDouble(((JFormattedTextField)
				// .getSource()).getValue()+"");
				p.setTokenStart(tokenStart);
				p.setToken(p.getTokenStart());
				pw.handleChangeFlags(ChangedFlags.INITIALVALUE_CHANGED);
				pw.getChangedInitialValues().put(p, tokenStart);
			}
		} else if (source.equals("tokenMax")) {
			Number n = (Number) ((JFormattedTextField) event.getSource())
					.getValue();
			Place p = (Place) geb;
			if (n != null && !n.equals("") && !n.equals(p.getTokenStart())) {
				double tokenMax = n.doubleValue();
				p.setTokenMax(tokenMax);
				pw.handleChangeFlags(ChangedFlags.BOUNDARIES_CHANGED);
				Boundary b;
				if(pw.getChangedBoundaries().containsKey(p)){
					b = pw.getChangedBoundaries().get(p);
				}else{
					b = new Boundary();
					pw.getChangedBoundaries().put(p, b);
				}
				b.setUpperBoundary(tokenMax);
			}
		}
		// for Transitions
		else if (source.equals("delay")) {

			if (geb instanceof DiscreteTransition) {
				DiscreteTransition p = (DiscreteTransition) geb;
				text = ((JTextField) event.getSource()).getText().trim();
				if (!text.equals("") && !text.equals(p.getDelay())) {
					double delay = Double.parseDouble(text);
					p.setDelay(delay);
					pw.handleChangeFlags(ChangedFlags.PNPROPERTIES_CHANGED);
				}
			}

		} else if (source.equals("maximumSpeed")) {
			if (geb instanceof DynamicNode) {
				DynamicNode dn = (DynamicNode) geb;
			text = ((JTextField) event.getSource()).getText().trim();
			if (!text.equals("") && !text.equals(dn.getMaximumSpeed())) {
					dn.setMaximumSpeed(text);
					pw.handleChangeFlags(ChangedFlags.PNPROPERTIES_CHANGED);
				}
			}

		} else if (source.equals("transList")) {
			// System.out.println("translist");
			Transition t = (Transition) geb;
			Transition newT = null;
			Iterator<BiologicalNodeAbstract> k = pw.getGraph().getJungGraph()
					.getNeighbors(t).iterator();
			BiologicalNodeAbstract neighbour;
			BiologicalNodeAbstract node;
			while (k.hasNext()) {
				neighbour = k.next();
				Iterator<BiologicalNodeAbstract> j = pw.getAllGraphNodes()
						.iterator();
				while (j.hasNext()) {
					node = j.next();
					if (node.equals(neighbour)
							&& (((JComboBox) event.getSource())
									.getSelectedItem().equals(
											ContinuousTransition.class
													.getName()) && node
									.getBiologicalElement().equals(
											Elementdeclerations.place))) {

						JOptionPane
								.showMessageDialog(
										MainWindow.getInstance(),
										"Your action would lead to a relation between a discrete place and a continious transition. That is not possible!",
										"Unallowed Operation...",
										JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
			}

			if (((JComboBox) event.getSource()).getSelectedItem().equals(
					DiscreteTransition.class.getName()))
				newT = new DiscreteTransition(t.getLabel(), t.getName());
			else if (((JComboBox) event.getSource()).getSelectedItem().equals(
					ContinuousTransition.class.getName()))
				newT = new ContinuousTransition(t.getLabel(), t.getName());
			else if (((JComboBox) event.getSource()).getSelectedItem().equals(
					StochasticTransition.class.getName()))
				newT = new StochasticTransition(t.getLabel(), t.getName());
			if (newT != null) {
				newT.rebuildShape(new VertexShapes());
				newT.setCompartment(t.getCompartment());
				graphInstance.getPathway().addVertex(newT, new Point());
			}
		} else if (source.equals("placeList")) {
			Place p = (Place) geb;
			Iterator<BiologicalNodeAbstract> k = pw.getGraph().getJungGraph()
					.getNeighbors(p).iterator();
			BiologicalNodeAbstract neighbour;

			BiologicalNodeAbstract node;
			while (k.hasNext()) {
				neighbour = k.next();
				for (Iterator<BiologicalNodeAbstract> j = pw.getAllGraphNodes()
						.iterator(); j.hasNext();) {
					node = j.next();
					if (node.equals(neighbour)
							&& (((JComboBox) event.getSource())
									.getSelectedItem().equals("discrete") && node
									.getBiologicalElement()
									.equals(Elementdeclerations.continuousTransition))) {

						JOptionPane
								.showMessageDialog(
										MainWindow.getInstance(),
										"Your action would lead to a relation between a discrete place and a continious transition. That is not possible!",
										"Unallowed Operation...",
										JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
			}

			Place newP = new Place(p.getLabel(), p.getName(), 0,
					((JComboBox) event.getSource()).getSelectedItem().equals(
							"discrete"));
			graphInstance.getPathway().addVertex(newP, new Point());
			newP.setToken(p.getToken());
			newP.setTokenMax(p.getTokenMax());
			newP.setTokenMin(p.getTokenMin());
			newP.setTokenStart(p.getTokenStart());
			newP.setCompartment(p.getCompartment());
			try{
			newP.setID(p.getID());
			} catch(IDAlreadyExistException ex){
				newP.setID();
			}
			newP.rebuildShape(new VertexShapes());

		} else if (source.equals("disList")) {
			StochasticTransition p = (StochasticTransition) geb;
			p.changeDistribution();
		}

		// for PetriNet Edges
		else if (source.equals("activationProb")) {
			text = ((JTextField) event.getSource()).getText().trim();
			PNEdge e = (PNEdge) geb;
			if (!text.equals("") && !text.equals(e.getActivationProbability())) {
				double prob = Double.parseDouble(text);
				e.setActivationProbability(prob);
				pw.handleChangeFlags(ChangedFlags.PNPROPERTIES_CHANGED);
			}
		} else if (source.equals("function")) {
			text = ((JTextField) event.getSource()).getText().trim();
			PNEdge e = (PNEdge) geb;
			if (!text.equals("") && !text.equals(e.getFunction())) {
				e.setFunction(text);
				pw.handleChangeFlags(ChangedFlags.EDGEWEIGHT_CHANGED);
			}
		} else if (source.equals("lowBoundary")) {
			text = ((JTextField) event.getSource()).getText().trim();
			PNEdge e = (PNEdge) geb;
			if (!text.equals("") && !text.equals(e.getLowerBoundary())) {
				double lowBoundary = Double.parseDouble(text);
				e.setLowerBoundary(lowBoundary);
				pw.handleChangeFlags(ChangedFlags.PNPROPERTIES_CHANGED);
			}
		} else if (source.equals("upBoundary")) {
			text = ((JTextField) event.getSource()).getText().trim();
			PNEdge e = (PNEdge) geb;
			if (!text.equals("") && !text.equals(e.getUpperBoundary())) {
				double upperBoundary = Double.parseDouble(text);
				e.setUpperBoundary(upperBoundary);
				pw.handleChangeFlags(ChangedFlags.PNPROPERTIES_CHANGED);
			}
		}
		// ContainerSingelton.getInstance().changeMouseFunction("edit");
		event.getComponent().setBackground(Color.WHITE);
		// GraphInstance.getMyGraph().updateElementLabel(element);
		GraphInstance.getMyGraph().updateGraph();
	}
}
