package gui.eventhandlers;

//import edu.uci.ics.jung.graph.ArchetypeVertex;
//import edu.uci.ics.jung.graph.Vertex;
//import edu.uci.ics.jung.visualization.Layout;
import graph.GraphInstance;
import graph.jung.graphDrawing.VertexShapes;
import gui.MainWindowSingelton;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Iterator;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import petriNet.ContinuousTransition;
import petriNet.DiscreteTransition;
import petriNet.PNEdge;
import petriNet.Place;
import petriNet.StochasticTransition;
import petriNet.Transition;
import biologicalElements.Elementdeclerations;
import biologicalElements.GraphElementAbstract;
import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.DNA;
import biologicalObjects.nodes.Gene;
import biologicalObjects.nodes.Protein;
import biologicalObjects.nodes.RNA;

public class PropertyWindowListener implements FocusListener, KeyListener,
		ActionListener {

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

		if (source.equals("label")) {
			if (!((JTextField) event.getSource()).getText().equals("")) {
				geb.setLabel(((JTextField) event.getSource()).getText());
				MainWindowSingelton.getInstance().updateElementTree();
			}
		} else if (source.equals("name")) {
			if (!((JTextField) event.getSource()).getText().equals("")) {
				geb.setName(((JTextField) event.getSource()).getText());
				MainWindowSingelton.getInstance().updateElementTree();
			}
		} else if (source.equals("comment")) {
			if (!((JTextArea) event.getSource()).getText().equals("")) {
				geb.setComments(((JTextArea) event.getSource()).getText());
			}
		} else if (source.equals("protein")) {
			if (!((JTextField) event.getSource()).getText().equals("")) {
				Protein protein = (Protein) geb;
				protein.setAaSequence(((JTextField) event.getSource())
						.getText());
			}
		} else if (source.equals("dna")) {
			if (!((JTextField) event.getSource()).getText().equals("")) {
				DNA dna = (DNA) geb;
				dna.setNtSequence(((JTextField) event.getSource()).getText());
			}
		} else if (source.equals("gene")) {
			if (!((JTextField) event.getSource()).getText().equals("")) {
				Gene gene = (Gene) geb;
				gene.setNtSequence(((JTextField) event.getSource()).getText());
			}
		} else if (source.equals("rna")) {
			if (!((JTextField) event.getSource()).getText().equals("")) {
				RNA rna = (RNA) geb;
				rna.setNtSequence(((JTextField) event.getSource()).getText());
			}
		}
		// for Places
		else if (source.equals("token")) {
			if (!((JTextField) event.getSource()).getText().equals("")) {
				Place p = (Place) geb;
				double tokens = Double.parseDouble(((JTextField) event
						.getSource()).getText());
				p.setToken(tokens);
			}

		} else if (source.equals("tokenMin")) {
			if (!((JTextField) event.getSource()).getText().equals("")) {

				Place p = (Place) geb;
				double tokenMin = Double.parseDouble(((JTextField) event
						.getSource()).getText());
				p.setTokenMin(tokenMin);
			}
		} else if (source.equals("tokenStart")) {
			if (!((JTextField) event.getSource()).getText().equals("")) {

				Place p = (Place) geb;
				double tokenStart = Double.parseDouble(((JTextField) event
						.getSource()).getText());
				p.setTokenStart(tokenStart);
			}
		} else if (source.equals("tokenMax")) {
			if (!((JTextField) event.getSource()).getText().equals("")) {

				Place p = (Place) geb;
				double tokenMax = Double.parseDouble(((JTextField) event
						.getSource()).getText());
				p.setTokenMax(tokenMax);
			}
		}
		// for Transitions
		else if (source.equals("delay")) {
			if (!((JTextField) event.getSource()).getText().equals("")) {

				if (geb instanceof DiscreteTransition) {
					DiscreteTransition p = (DiscreteTransition) geb;
					double delay = Double.parseDouble(((JTextField) event
							.getSource()).getText());
					p.setDelay(delay);
				}
			}

		} else if (source.equals("maximumSpeed")) {
			if (!((JTextField) event.getSource()).getText().equals("")) {
				if (geb instanceof ContinuousTransition) {
					ContinuousTransition t = (ContinuousTransition) geb;
					t.setMaximumSpeed(((JTextField) event.getSource())
							.getText());
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
				Iterator<BiologicalNodeAbstract> j = pw.getAllNodes()
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
										MainWindowSingelton.getInstance(),
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
				for (Iterator<BiologicalNodeAbstract> j = pw.getAllNodes()
						.iterator(); j.hasNext();) {
					node = j.next();
					if (node.equals(neighbour)
							&& (((JComboBox) event.getSource())
									.getSelectedItem().equals("discrete") && node
									.getBiologicalElement()
									.equals(Elementdeclerations.continuousTransition))) {

						JOptionPane
								.showMessageDialog(
										MainWindowSingelton.getInstance(),
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
			newP.setID(p.getID());
			newP.rebuildShape(new VertexShapes());

		} else if (source.equals("disList")) {
			StochasticTransition p = (StochasticTransition) geb;
			p.changeDistribution();
		}

		// for PetriNet Edges
		else if (source.equals("activationProb")) {
			if (!((JTextField) event.getSource()).getText().equals("")) {

				PNEdge e = (PNEdge) geb;
				double prob = Double.parseDouble(((JTextField) event
						.getSource()).getText());
				e.setActivationProbability(prob);
			}
		} else if (source.equals("function")) {
			if (!((JTextField) event.getSource()).getText().equals("")) {

				PNEdge e = (PNEdge) geb;
				String function = ((JTextField) event.getSource()).getText();
				e.setFunction(function);
			}
		} else if (source.equals("lowBoundary")) {
			if (!((JTextField) event.getSource()).getText().equals("")) {

				PNEdge e = (PNEdge) geb;
				double lowBoundary = Double.parseDouble(((JTextField) event
						.getSource()).getText());
				e.setLowerBoundary(lowBoundary);
			}
		} else if (source.equals("upBoundary")) {
			if (!((JTextField) event.getSource()).getText().equals("")) {

				PNEdge e = (PNEdge) geb;
				double upperBoundary = Double.parseDouble(((JTextField) event
						.getSource()).getText());
				e.setUpperBoundary(upperBoundary);
			}
		}
		// ContainerSingelton.getInstance().changeMouseFunction("edit");
		event.getComponent().setBackground(Color.WHITE);
		// GraphInstance.getMyGraph().updateElementLabel(element);
		GraphInstance.getMyGraph().updateGraph();
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {

	}

	@Override
	public void keyReleased(KeyEvent e) {
		focusLost(new FocusEvent(((JTextField) e.getSource()), e.getID()));

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		focusLost(new FocusEvent(((JComboBox) e.getSource()), e.getID()));
	}
}
