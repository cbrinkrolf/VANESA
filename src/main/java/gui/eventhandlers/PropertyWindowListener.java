package gui.eventhandlers;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import biologicalElements.Elementdeclerations;
import biologicalElements.GraphElementAbstract;
import biologicalElements.IDAlreadyExistException;
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.edges.petriNet.PNArc;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.DNA;
import biologicalObjects.nodes.DynamicNode;
import biologicalObjects.nodes.Gene;
import biologicalObjects.nodes.Protein;
import biologicalObjects.nodes.RNA;
import biologicalObjects.nodes.petriNet.ContinuousPlace;
import biologicalObjects.nodes.petriNet.ContinuousTransition;
import biologicalObjects.nodes.petriNet.DiscretePlace;
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
import gui.MainWindow;
import gui.MyPopUp;

public class PropertyWindowListener implements FocusListener, ItemListener {

	private GraphElementAbstract geb;
	private GraphInstance graphInstance = new GraphInstance();

	// Object element;

	public PropertyWindowListener(GraphElementAbstract element) {
		if (element instanceof BiologicalNodeAbstract && ((BiologicalNodeAbstract) element).isLogical()) {
			geb = ((BiologicalNodeAbstract) graphInstance.getSelectedObject()).getLogicalReference();
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
		} else if (source.equals("ntSequence")) {
			text = ((JTextField) event.getSource()).getText().trim();
			RNA rna = (RNA) geb;
			if (!text.equals("") && !text.equals(rna.getNtSequence())) {
				rna.setNtSequence(text);
			}
		} else if (source.equals("logFC")) {
			Number n = (Number) ((JFormattedTextField) event.getSource()).getValue();
			RNA rna = (RNA) geb;
			if (n != null && !n.equals(rna.getLogFC())) {
				rna.setLogFC(n.doubleValue());
			}
		} else if (source.equals("concentration")) {
			Number n = (Number) ((JFormattedTextField) event.getSource()).getValue();
			BiologicalNodeAbstract bna = (BiologicalNodeAbstract) geb;
			if (n != null && !n.equals(bna.getConcentration())) {
				bna.setConcentration(n.doubleValue());
			}

		} else if (source.equals("concentrationMin")) {
			Number n = (Number) ((JFormattedTextField) event.getSource()).getValue();
			BiologicalNodeAbstract bna = (BiologicalNodeAbstract) geb;
			if (n != null && !n.equals(bna.getConcentrationMin())) {
				double concentrationMin = n.doubleValue();
				if (concentrationMin <= bna.getConcentrationStart()) {
					bna.setConcentrationMin(concentrationMin);
				} else {
					MyPopUp.getInstance().show("Violation", "Minimum > start");
				}
			}
		} else if (source.equals("concentrationStart")) {
			Number n = (Number) ((JFormattedTextField) event.getSource()).getValue();
			BiologicalNodeAbstract bna = (BiologicalNodeAbstract) geb;
			if (n != null && !n.equals(bna.getConcentrationStart())) {
				double concentrationStart = n.doubleValue();
				if (concentrationStart >= bna.getConcentrationMin()
						&& concentrationStart <= bna.getConcentrationMax()) {
					bna.setConcentrationStart(concentrationStart);
					bna.setConcentration(bna.getConcentrationStart());
				} else {
					MyPopUp.getInstance().show("Violation", "Start > minimum or start < maximum");
				}
			}
		} else if (source.equals("concentrationMax")) {
			Number n = (Number) ((JFormattedTextField) event.getSource()).getValue();
			BiologicalNodeAbstract bna = (BiologicalNodeAbstract) geb;
			if (n != null && !n.equals(bna.getConcentrationMax())) {
				double concentrationMax = n.doubleValue();
				if (concentrationMax >= bna.getConcentrationStart()) {
					bna.setConcentrationMax(concentrationMax);
				} else {
					MyPopUp.getInstance().show("Violation", "Maximum < start");
				}
			}
		}
		// for Places
		else if (source.equals("token")) {
			Number n = (Number) ((JFormattedTextField) event.getSource()).getValue();
			Place p = (Place) geb;
			if (n != null && !n.equals(p.getToken())) {
				double tokens = n.doubleValue();
				p.setToken(tokens);
			}

		} else if (source.equals("tokenMin")) {
			Number n = (Number) ((JFormattedTextField) event.getSource()).getValue();
			Place p = (Place) geb;
			if (n != null && !n.equals(p.getTokenMin())) {
				double tokenMin = n.doubleValue();
				if (tokenMin <= p.getTokenStart()) {
					p.setTokenMin(tokenMin);
					pw.handleChangeFlags(ChangedFlags.BOUNDARIES_CHANGED);
					Boundary b;
					if (pw.getChangedBoundaries().containsKey(p)) {
						b = pw.getChangedBoundaries().get(p);
					} else {
						b = new Boundary();
						pw.getChangedBoundaries().put(p, b);
					}
					b.setLowerBoundary(tokenMin);
				} else {
					MyPopUp.getInstance().show("Violation", "Minimum > start");
				}
			}
		} else if (source.equals("tokenStart")) {
			Number n = (Number) ((JFormattedTextField) event.getSource()).getValue();
			Place p = (Place) geb;
			if (n != null && !n.equals(p.getTokenStart())) {
				double tokenStart = n.doubleValue();
				if (tokenStart >= p.getTokenMin() && tokenStart <= p.getTokenMax()) {
					p.setTokenStart(tokenStart);
					p.setToken(p.getTokenStart());
					pw.handleChangeFlags(ChangedFlags.INITIALVALUE_CHANGED);
					pw.getChangedInitialValues().put(p, tokenStart);
				} else {
					MyPopUp.getInstance().show("Violation", "Start > minimum or start < maximum");
				}
			}
		} else if (source.equals("tokenMax")) {
			Number n = (Number) ((JFormattedTextField) event.getSource()).getValue();
			Place p = (Place) geb;
			if (n != null && !n.equals(p.getTokenMax())) {
				double tokenMax = n.doubleValue();
				if (tokenMax >= p.getTokenStart()) {
					p.setTokenMax(tokenMax);
					pw.handleChangeFlags(ChangedFlags.BOUNDARIES_CHANGED);
					Boundary b;
					if (pw.getChangedBoundaries().containsKey(p)) {
						b = pw.getChangedBoundaries().get(p);
					} else {
						b = new Boundary();
						pw.getChangedBoundaries().put(p, b);
					}
					b.setUpperBoundary(tokenMax);
				} else {
					MyPopUp.getInstance().show("Violation", "Maximum < start");
				}
			}
		}
		// for Transitions
		else if (source.equals("delay")) {
			if (geb instanceof DiscreteTransition) {
				DiscreteTransition p = (DiscreteTransition) geb;
				text = ((JTextField) event.getSource()).getText().trim();
				if (!text.equals("") && !text.equals(p.getDelay() + "")) {
					double delay = Double.parseDouble(text);
					p.setDelay(delay);
					pw.handleChangeFlags(ChangedFlags.PNPROPERTIES_CHANGED);
				}
			}

		} else if (source.equals("firingCondition")) {
			if (geb instanceof Transition) {
				Transition t = (Transition) geb;
				text = ((JTextField) event.getSource()).getText().trim();
				if (!text.equals("") && !text.equals(t.getFiringCondition())) {
					((Transition) geb).setFiringCondition(text.trim());
					pw.handleChangeFlags(ChangedFlags.PNPROPERTIES_CHANGED);
				}
			}

		} else if (source.equals("maximalSpeed")) {
			if (geb instanceof DynamicNode) {
				DynamicNode dn = (DynamicNode) geb;
				text = ((JTextField) event.getSource()).getText().trim();
				if (!text.equals("") && !text.equals(dn.getMaximalSpeed())) {
					dn.setMaximalSpeed(text);
					pw.handleChangeFlags(ChangedFlags.PNPROPERTIES_CHANGED);
				}
			} else if(geb instanceof ContinuousTransition){
				ContinuousTransition ct = (ContinuousTransition) geb;
				text = ((JTextField) event.getSource()).getText().trim();
				if (!text.equals("") && !text.equals(ct.getMaximalSpeed())) {
					ct.setMaximalSpeed(text);
					pw.handleChangeFlags(ChangedFlags.PNPROPERTIES_CHANGED);
				}
			}

		} else if (source.equals("transList")) {
			// System.out.println("translist");
			Transition t = (Transition) geb;
			Transition newT = null;
			Iterator<BiologicalNodeAbstract> k = pw.getGraph().getJungGraph().getNeighbors(t).iterator();
			BiologicalNodeAbstract neighbour;
			BiologicalNodeAbstract node;
			while (k.hasNext()) {
				neighbour = k.next();
				Iterator<BiologicalNodeAbstract> j = pw.getAllGraphNodes().iterator();
				while (j.hasNext()) {
					node = j.next();
					if (node.equals(neighbour) && (((JComboBox<?>) event.getSource()).getSelectedItem()
							.equals(ContinuousTransition.class.getName())
							&& node.getBiologicalElement().equals(Elementdeclerations.discretePlace))) {
						JOptionPane.showMessageDialog(MainWindow.getInstance().getFrame(),
								"Your action would lead to a relation between a discrete place and a continious transition. That is not possible!",
								"Unallowed Operation...", JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
			}

			if (((JComboBox<?>) event.getSource()).getSelectedItem().equals(DiscreteTransition.class.getName()))
				newT = new DiscreteTransition(t.getLabel(), t.getName());
			else if (((JComboBox<?>) event.getSource()).getSelectedItem().equals(ContinuousTransition.class.getName()))
				newT = new ContinuousTransition(t.getLabel(), t.getName());
			else if (((JComboBox<?>) event.getSource()).getSelectedItem().equals(StochasticTransition.class.getName()))
				newT = new StochasticTransition(t.getLabel(), t.getName());
			if (newT != null) {
				// newT.setCompartment(pw.getCompartmentManager().getCompartment(t));
				graphInstance.getPathway().addVertex(newT, new Point());
			}
		} else if (source.equals("placeList")) {
			Place p = (Place) geb;
			Iterator<BiologicalNodeAbstract> k = pw.getGraph().getJungGraph().getNeighbors(p).iterator();
			BiologicalNodeAbstract neighbour;

			BiologicalNodeAbstract node;
			while (k.hasNext()) {
				neighbour = k.next();
				for (Iterator<BiologicalNodeAbstract> j = pw.getAllGraphNodes().iterator(); j.hasNext();) {
					node = j.next();
					if (node.equals(neighbour)
							&& (((JComboBox<?>) event.getSource()).getSelectedItem().equals("discrete")
									&& node.getBiologicalElement().equals(Elementdeclerations.continuousTransition))) {

						JOptionPane.showMessageDialog(MainWindow.getInstance().getFrame(),
								"Your action would lead to a relation between a discrete place and a continious transition. That is not possible!",
								"Unallowed Operation...", JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
			}

			Place newP;
			if (((JComboBox<?>) event.getSource()).getSelectedItem().equals("discrete")) {
				newP = new DiscretePlace(p.getLabel(), p.getName());
			} else {
				newP = new ContinuousPlace(p.getLabel(), p.getName());
			}
			graphInstance.getPathway().addVertex(newP, new Point());
			newP.setToken(p.getToken());
			newP.setTokenMax(p.getTokenMax());
			newP.setTokenMin(p.getTokenMin());
			newP.setTokenStart(p.getTokenStart());
			// newP.setCompartment(p.getCompartment());
			try {
				newP.setID(p.getID(), pw);
			} catch (IDAlreadyExistException ex) {
				newP.setID(pw);
			}

		} else if (source.equals("h")) {
			JFormattedTextField tf = (JFormattedTextField) event.getSource();
			Number n = (Number) tf.getValue();
			StochasticTransition st = (StochasticTransition) geb;
			if (n != null) {
				if (!n.equals(st.getH())) {
					st.setH(n.doubleValue());
					pw.handleChangeFlags(ChangedFlags.PNPROPERTIES_CHANGED);
				}
			} else {
				MyPopUp.getInstance().show("Violation", "h: \"" + tf.getText() + "\" is not a valid decimal number");
			}
		} else if (source.equals("a")) {
			JFormattedTextField tf = (JFormattedTextField) event.getSource();
			Number n = (Number) tf.getValue();
			StochasticTransition st = (StochasticTransition) geb;
			if (n != null) {
				if (!n.equals(st.getA())) {
					st.setA(n.doubleValue());
					pw.handleChangeFlags(ChangedFlags.PNPROPERTIES_CHANGED);
				}
			} else {
				MyPopUp.getInstance().show("Violation", "a: \"" + tf.getText() + "\" is not a valid decimal number");
			}
		} else if (source.equals("b")) {
			JFormattedTextField tf = (JFormattedTextField) event.getSource();
			Number n = (Number) tf.getValue();
			StochasticTransition st = (StochasticTransition) geb;
			if (n != null) {
				if (!n.equals(st.getB())) {
					st.setB(n.doubleValue());
					pw.handleChangeFlags(ChangedFlags.PNPROPERTIES_CHANGED);
				}
			} else {
				MyPopUp.getInstance().show("Violation", "b: \"" + tf.getText() + "\" is not a valid decimal number");
			}
		} else if (source.equals("c")) {
			JFormattedTextField tf = (JFormattedTextField) event.getSource();
			Number n = (Number) tf.getValue();
			StochasticTransition st = (StochasticTransition) geb;
			if (n != null) {
				if (!n.equals(st.getA())) {
					st.setC(n.doubleValue());
					pw.handleChangeFlags(ChangedFlags.PNPROPERTIES_CHANGED);
				}
			} else {
				MyPopUp.getInstance().show("Violation", "c: \"" + tf.getText() + "\" is not a valid decimal number");
			}
		} else if (source.equals("mu")) {
			JFormattedTextField tf = (JFormattedTextField) event.getSource();
			Number n = (Number) tf.getValue();
			StochasticTransition st = (StochasticTransition) geb;
			if (n != null) {
				if (!n.equals(st.getMu())) {
					st.setMu(n.doubleValue());
					pw.handleChangeFlags(ChangedFlags.PNPROPERTIES_CHANGED);
				}
			} else {
				MyPopUp.getInstance().show("Violation", "mu: \"" + tf.getText() + "\" is not a valid decimal number");
			}
		} else if (source.equals("sigma")) {
			JFormattedTextField tf = (JFormattedTextField) event.getSource();
			Number n = (Number) tf.getValue();
			StochasticTransition st = (StochasticTransition) geb;
			if (n != null) {
				if (!n.equals(st.getSigma())) {
					st.setSigma(n.doubleValue());
					pw.handleChangeFlags(ChangedFlags.PNPROPERTIES_CHANGED);
				}
			} else {
				MyPopUp.getInstance().show("Violation", "sigma: \"" + tf.getText() + "\" is not a valid number");
			}
		} else if (source.equals("events")) {
			JTextField tf = (JTextField) event.getSource();
			StochasticTransition st = (StochasticTransition) geb;
			String str = tf.getText();
			str = str.replaceAll("\\[", "").replaceAll("\\]", "");
			String[] tokens = str.split(",");
			ArrayList<Integer> list = new ArrayList<Integer>();
			for (int i = 0; i < tokens.length; i++) {
				try {
					list.add(Integer.parseInt(tokens[i].trim()));
				} catch (Exception e) {
					MyPopUp.getInstance().show("Violation",
							"event item with indes " + i + ": \"" + tokens[i] + "\" is not a valid integer number!");
				}
			}
			if (st.getEvents().size() != list.size()) {
				pw.handleChangeFlags(ChangedFlags.PNPROPERTIES_CHANGED);
				st.setEvents(list);
			} else {
				for (int i = 0; i < list.size(); i++) {
					if (st.getEvents().get(i) != list.get(i)) {
						pw.handleChangeFlags(ChangedFlags.PNPROPERTIES_CHANGED);
						st.setEvents(list);
						break;
					}
				}
			}

			if (st.getEvents().size() != st.getProbabilities().size()) {
				MyPopUp.getInstance().show("Warning", "Number of given events (" + st.getEvents().size()
						+ ") is not equal to number of given probabilities(" + st.getProbabilities().size() + ")!");
			}
		} else if (source.equals("probabilities")) {
			JTextField tf = (JTextField) event.getSource();
			StochasticTransition st = (StochasticTransition) geb;
			String str = tf.getText();
			str = str.replaceAll("\\[", "").replaceAll("\\]", "");
			String[] tokens = str.split(",");
			ArrayList<Double> list = new ArrayList<Double>();
			for (int i = 0; i < tokens.length; i++) {
				try {
					list.add(Double.parseDouble(tokens[i].trim()));
				} catch (Exception e) {
					MyPopUp.getInstance().show("Violation",
							"event item with indes " + i + ": \"" + tokens[i] + "\" is not a valid decimal number!");
				}
			}

			if (st.getProbabilities().size() != list.size()) {
				pw.handleChangeFlags(ChangedFlags.PNPROPERTIES_CHANGED);
				st.setProbabilities(list);
			} else {
				for (int i = 0; i < list.size(); i++) {
					if (st.getProbabilities().get(i) != list.get(i)) {
						pw.handleChangeFlags(ChangedFlags.PNPROPERTIES_CHANGED);
						st.setProbabilities(list);
						break;
					}
				}
			}

			st.setProbabilities(list);
			if (st.getEvents().size() != st.getProbabilities().size()) {
				MyPopUp.getInstance().show("Warning", "Number of given events (" + st.getEvents().size()
						+ ") is not equal to number of given probabilities(" + st.getProbabilities().size() + ")!");
			}
			double sum = 0;
			for (int i = 0; i < list.size(); i++) {
				sum += list.get(i);
			}
			if (sum != 1.0) {
				MyPopUp.getInstance().show("Warning", "Sum of given probabilities (" + sum + ") is not equal to 1.0!");
			}
		}

		// for PetriNet Edges
		else if (source.equals("activationProb")) {
			text = ((JTextField) event.getSource()).getText().trim();
			PNArc e = (PNArc) geb;
			if (!text.equals("") && !text.equals(e.getProbability() + "")) {
				double prob = Double.parseDouble(text);
				e.setProbability(prob);
				pw.handleChangeFlags(ChangedFlags.PNPROPERTIES_CHANGED);
			}
		} else if (source.equals("activationPrio")) {
			text = ((JTextField) event.getSource()).getText().trim();
			PNArc e = (PNArc) geb;
			if (!text.equals("") && !text.equals(e.getPriority() + "")) {
				int prob = Integer.parseInt(text);
				e.setPriority(prob);
				pw.handleChangeFlags(ChangedFlags.PNPROPERTIES_CHANGED);
			}
		} else if (source.equals("function")) {
			text = ((JTextField) event.getSource()).getText().trim();
			if (geb instanceof BiologicalEdgeAbstract) {
				BiologicalEdgeAbstract e = (BiologicalEdgeAbstract) geb;
				if (!text.equals("") && !text.equals(e.getFunction())) {
					e.setFunction(text);
					pw.handleChangeFlags(ChangedFlags.EDGEWEIGHT_CHANGED);
				}
			}else{
				return;
			}
		}
		// ContainerSingelton.getInstance().changeMouseFunction("edit");
		event.getComponent().setBackground(Color.WHITE);
		// GraphInstance.getMyGraph().updateElementLabel(element);
		GraphInstance.getMyGraph().updateGraph();
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		Pathway pw = new GraphInstance().getPathway();
		if (e.getSource() instanceof JComboBox) {
			JComboBox<?> box = (JComboBox<?>) e.getSource();
			if (box.getName().equals("distributionList") && e.getStateChange() == ItemEvent.SELECTED) {
				// System.out.println(e.getItem());
				// System.out.println(box.getSelectedItem());
				StochasticTransition st = (StochasticTransition) geb;
				st.setDistribution(box.getSelectedItem().toString());
				pw.handleChangeFlags(ChangedFlags.PNPROPERTIES_CHANGED);
				MainWindow.getInstance().updateElementProperties();
			}
		}
	}
}
