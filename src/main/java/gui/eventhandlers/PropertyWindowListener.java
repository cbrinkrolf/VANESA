package gui.eventhandlers;

import biologicalElements.Elementdeclerations;
import biologicalElements.GraphElementAbstract;
import biologicalElements.IDAlreadyExistException;
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.edges.petriNet.PNArc;
import biologicalObjects.nodes.*;
import biologicalObjects.nodes.petriNet.*;
import graph.ChangedFlags;
import graph.GraphInstance;
import graph.gui.Boundary;
import gui.MainWindow;
import gui.PopUpDialog;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

public class PropertyWindowListener implements FocusListener, ItemListener {
	private final GraphElementAbstract geb;
	// Object element;

	public PropertyWindowListener(GraphElementAbstract element) {
		if (element instanceof BiologicalNodeAbstract && ((BiologicalNodeAbstract) element).isLogical()) {
			geb = ((BiologicalNodeAbstract) GraphInstance.getSelectedObject()).getLogicalReference();
		} else {
			geb = element;// graphInstance.getPathwayElement(element);
			// this.element = element;
		}
	}

	@Override
	public void focusGained(FocusEvent event) {
		if (!event.getComponent().getName().equals("comment")) {
			event.getComponent().setBackground(new Color(200, 227, 255));
		}
	}

	@Override
	public void focusLost(FocusEvent event) {
		Pathway pw = GraphInstance.getPathway();
		String source = event.getComponent().getName();
		String text;
		if (source.equals("label")) {
			text = ((JTextField) event.getSource()).getText().trim();
			if (!"".equals(text) && !text.equals(geb.getLabel())) {
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
			if (!"".equals(text) && !text.equals(geb.getName())) {
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
			if (!"".equals(text) && !text.equals(geb.getComments())) {
				geb.setComments(text);
			}
		} else if (source.equals("protein")) {
			text = ((JTextField) event.getSource()).getText().trim();
			Protein protein = (Protein) geb;
			if (!"".equals(text) && !text.equals(protein.getAaSequence())) {
				protein.setAaSequence(text);
			}
		} else if (source.equals("ntSequence")) {
			text = ((JTextField) event.getSource()).getText().trim();
			NodeWithNTSequence node = (NodeWithNTSequence) geb;
			if (!"".equals(text) && !text.equals(node.getNtSequence())) {
				node.setNtSequence(text);
			}
		} else if (source.equals("logFC")) {
			Number n = (Number) ((JFormattedTextField) event.getSource()).getValue();
			NodeWithLogFC node = (NodeWithLogFC) geb;
			if (n != null && !n.equals(node.getLogFC())) {
				node.setLogFC(n.doubleValue());
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
					PopUpDialog.getInstance().show("Violation", "Minimum > start");
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
					PopUpDialog.getInstance().show("Violation", "Start > minimum or start < maximum");
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
					PopUpDialog.getInstance().show("Violation", "Maximum < start");
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
					PopUpDialog.getInstance().show("Violation", "Minimum > start");
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
					PopUpDialog.getInstance().show("Violation", "Start > minimum or start < maximum");
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
					PopUpDialog.getInstance().show("Violation", "Maximum < start");
				}
			}
		}
		// for Transitions
		else if (source.equals("delay")) {
			if (geb instanceof DiscreteTransition) {
				DiscreteTransition p = (DiscreteTransition) geb;
				text = ((JTextField) event.getSource()).getText().trim();
				if (!"".equals(text) && !text.equals(p.getDelay())) {
					p.setDelay(text.trim());
					pw.handleChangeFlags(ChangedFlags.PNPROPERTIES_CHANGED);
				}
			}
		} else if (source.equals("firingCondition")) {
			if (geb instanceof Transition) {
				Transition t = (Transition) geb;
				text = ((JTextField) event.getSource()).getText().trim();
				if (!"".equals(text) && !text.equals(t.getFiringCondition())) {
					((Transition) geb).setFiringCondition(text.trim());
					pw.handleChangeFlags(ChangedFlags.PNPROPERTIES_CHANGED);
				}
			}
		} else if (source.equals("maximalSpeed")) {
			if (geb instanceof DynamicNode) {
				DynamicNode dn = (DynamicNode) geb;
				text = ((JTextField) event.getSource()).getText().trim();
				if (!"".equals(text) && !text.equals(dn.getMaximalSpeed())) {
					dn.setMaximalSpeed(text);
					pw.handleChangeFlags(ChangedFlags.PNPROPERTIES_CHANGED);
				}
			} else if(geb instanceof ContinuousTransition){
				ContinuousTransition ct = (ContinuousTransition) geb;
				text = ((JTextField) event.getSource()).getText().trim();
				if (!"".equals(text) && !text.equals(ct.getMaximalSpeed())) {
					ct.setMaximalSpeed(text);
					pw.handleChangeFlags(ChangedFlags.PNPROPERTIES_CHANGED);
				}
			}

		} else if (source.equals("transList")) {
			Transition t = (Transition) geb;
			Transition newT = null;
			Object selectedItem = ((JComboBox<?>) event.getSource()).getSelectedItem();
			for (BiologicalNodeAbstract neighbour : pw.getGraph2().getNeighbors(t)) {
				for (BiologicalNodeAbstract node : pw.getAllGraphNodes()) {
					if (node.equals(neighbour) &&
						(ContinuousTransition.class.getName().equals(selectedItem) &&
						 node.getBiologicalElement().equals(Elementdeclerations.discretePlace))) {
						JOptionPane.showMessageDialog(MainWindow.getInstance().getFrame(),
													  "Your action would lead to a relation between a discrete place and a continuous transition. That is not possible!",
													  "Unallowed Operation...", JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
			}
			if (DiscreteTransition.class.getName().equals(selectedItem))
				newT = new DiscreteTransition(t.getLabel(), t.getName());
			else if (ContinuousTransition.class.getName().equals(selectedItem))
				newT = new ContinuousTransition(t.getLabel(), t.getName());
			else if (StochasticTransition.class.getName().equals(selectedItem))
				newT = new StochasticTransition(t.getLabel(), t.getName());
			if (newT != null) {
				// newT.setCompartment(pw.getCompartmentManager().getCompartment(t));
				pw.addVertex(newT, new Point());
			}
		} else if (source.equals("placeList")) {
			Place p = (Place) geb;
			for (BiologicalNodeAbstract neighbour : pw.getGraph2().getNeighbors(p)) {
				for (BiologicalNodeAbstract node : pw.getAllGraphNodes()) {
					if (node.equals(neighbour) &&
						("discrete".equals(((JComboBox<?>) event.getSource()).getSelectedItem()) &&
						 node.getBiologicalElement().equals(Elementdeclerations.continuousTransition))) {
						JOptionPane.showMessageDialog(MainWindow.getInstance().getFrame(),
													  "Your action would lead to a relation between a discrete place and a continuous transition. That is not possible!",
													  "Unallowed Operation...", JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
			}

			Place newP;
			if ("discrete".equals(((JComboBox<?>) event.getSource()).getSelectedItem())) {
				newP = new DiscretePlace(p.getLabel(), p.getName());
			} else {
				newP = new ContinuousPlace(p.getLabel(), p.getName());
			}
			pw.addVertex(newP, new Point());
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
				PopUpDialog.getInstance().show("Violation", "h: \"" + tf.getText() + "\" is not a valid decimal number");
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
				PopUpDialog.getInstance().show("Violation", "a: \"" + tf.getText() + "\" is not a valid decimal number");
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
				PopUpDialog.getInstance().show("Violation", "b: \"" + tf.getText() + "\" is not a valid decimal number");
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
				PopUpDialog.getInstance().show("Violation", "c: \"" + tf.getText() + "\" is not a valid decimal number");
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
				PopUpDialog.getInstance().show("Violation", "mu: \"" + tf.getText() + "\" is not a valid decimal number");
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
				PopUpDialog.getInstance().show("Violation", "sigma: \"" + tf.getText() + "\" is not a valid number");
			}
		} else if (source.equals("events")) {
			JTextField tf = (JTextField) event.getSource();
			StochasticTransition st = (StochasticTransition) geb;
			String str = StringUtils.replace(StringUtils.replace(tf.getText(), "[", ""), "]", "");
			String[] tokens = str.split(",");
			ArrayList<Integer> list = new ArrayList<>();
			for (int i = 0; i < tokens.length; i++) {
				try {
					list.add(Integer.parseInt(tokens[i].trim()));
				} catch (Exception e) {
					PopUpDialog.getInstance().show("Violation",
							"event item with indes " + i + ": \"" + tokens[i] + "\" is not a valid integer number!");
				}
			}
			if (st.getEvents().size() != list.size()) {
				pw.handleChangeFlags(ChangedFlags.PNPROPERTIES_CHANGED);
				st.setEvents(list);
			} else {
				for (int i = 0; i < list.size(); i++) {
					if (!st.getEvents().get(i).equals(list.get(i))) {
						pw.handleChangeFlags(ChangedFlags.PNPROPERTIES_CHANGED);
						st.setEvents(list);
						break;
					}
				}
			}
			if (st.getEvents().size() != st.getProbabilities().size()) {
				PopUpDialog.getInstance().show("Warning", "Number of given events (" + st.getEvents().size() +
														  ") is not equal to number of given probabilities(" +
														  st.getProbabilities().size() + ")!");
			}
		} else if (source.equals("probabilities")) {
			JTextField tf = (JTextField) event.getSource();
			StochasticTransition st = (StochasticTransition) geb;
			String str = StringUtils.replace(StringUtils.replace(tf.getText(), "[", ""), "]", "");
			String[] tokens = str.split(",");
			ArrayList<Double> list = new ArrayList<>();
			for (int i = 0; i < tokens.length; i++) {
				try {
					list.add(Double.parseDouble(tokens[i].trim()));
				} catch (Exception e) {
					PopUpDialog.getInstance().show("Violation",
							"event item with indes " + i + ": \"" + tokens[i] + "\" is not a valid decimal number!");
				}
			}
			if (st.getProbabilities().size() != list.size()) {
				pw.handleChangeFlags(ChangedFlags.PNPROPERTIES_CHANGED);
				st.setProbabilities(list);
			} else {
				for (int i = 0; i < list.size(); i++) {
					if (!st.getProbabilities().get(i).equals(list.get(i))) {
						pw.handleChangeFlags(ChangedFlags.PNPROPERTIES_CHANGED);
						st.setProbabilities(list);
						break;
					}
				}
			}

			st.setProbabilities(list);
			if (st.getEvents().size() != st.getProbabilities().size()) {
				PopUpDialog.getInstance().show("Warning", "Number of given events (" + st.getEvents().size() +
														  ") is not equal to number of given probabilities(" +
														  st.getProbabilities().size() + ")!");
			}
			double sum = 0;
			for (Double aDouble : list) {
				sum += aDouble;
			}
			if (sum != 1.0) {
				PopUpDialog.getInstance().show("Warning", "Sum of given probabilities (" + sum + ") is not equal to 1.0!");
			}
		}

		// for PetriNet Edges
		else if (source.equals("activationProb")) {
			text = ((JTextField) event.getSource()).getText().trim();
			PNArc e = (PNArc) geb;
			if (!text.isEmpty() && !text.equals(String.valueOf(e.getProbability()))) {
				double prob = Double.parseDouble(text);
				e.setProbability(prob);
				pw.handleChangeFlags(ChangedFlags.PNPROPERTIES_CHANGED);
			}
		} else if (source.equals("activationPrio")) {
			text = ((JTextField) event.getSource()).getText().trim();
			PNArc e = (PNArc) geb;
			if (!text.isEmpty() && !text.equals(String.valueOf(e.getPriority()))) {
				int prob = Integer.parseInt(text);
				e.setPriority(prob);
				pw.handleChangeFlags(ChangedFlags.PNPROPERTIES_CHANGED);
			}
		} else if (source.equals("function")) {
			text = ((JTextField) event.getSource()).getText().trim();
			if (geb instanceof BiologicalEdgeAbstract) {
				BiologicalEdgeAbstract e = (BiologicalEdgeAbstract) geb;
				if (!text.isEmpty() && !text.equals(e.getFunction())) {
					e.setFunction(text);
					pw.handleChangeFlags(ChangedFlags.EDGEWEIGHT_CHANGED);
				}
			}else{
				return;
			}
		}
		event.getComponent().setBackground(Color.WHITE);
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		Pathway pw = GraphInstance.getPathway();
		if (e.getSource() instanceof JComboBox) {
			JComboBox<?> box = (JComboBox<?>) e.getSource();
			if (box.getName().equals("distributionList") && e.getStateChange() == ItemEvent.SELECTED) {
				StochasticTransition st = (StochasticTransition) geb;
				st.setDistribution(box.getSelectedItem().toString());
				pw.handleChangeFlags(ChangedFlags.PNPROPERTIES_CHANGED);
				MainWindow.getInstance().updateElementProperties();
			}
		}
	}
}
