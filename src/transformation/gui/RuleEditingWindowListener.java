package transformation.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;
import graph.GraphContainer;
import graph.jung.classes.MyGraph;
import gui.ToolBarButton;

public class RuleEditingWindowListener implements ActionListener {

	private Pathway bn;
	private Pathway pn;

	public RuleEditingWindowListener(Pathway bn, Pathway pn) {
		this.bn = bn;
		this.pn = pn;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Pathway activePw = bn;

		String panelName = ((ToolBarButton) e.getSource()).getParent().getName();
		if (panelName.equals("buttonBN")) {
			activePw = bn;
		} else if (panelName.equals("buttonPN")) {
			activePw = pn;
		}

		String event = e.getActionCommand();
		GraphContainer con = GraphContainer.getInstance();
		if ("move".equals(event)) {
			if (con.containsPathway()) {
				con.changeMouseFunction("move");
				MyGraph g = activePw.getGraph();
				g.disableGraphTheory();
				// g.getVisualizationViewer().resize(20, 20);
				Dimension d = g.getVisualizationViewer().getPreferredSize();
				d.setSize(d.width * 2, d.height * 2);
				g.getVisualizationViewer().setPreferredSize(d);
				g.getVisualizationViewer().repaint();
			}
		} else if ("pick".equals(event)) {
			if (con.containsPathway()) {
				con.changeMouseFunction("pick");
				activePw.getGraph().disableGraphTheory();
			}
		} else if ("center".equals(event)) {
			if (activePw.hasGotAtLeastOneElement()) {
				activePw.getGraph().normalCentering();
			}
		} else if ("edit".equals(event)) {
			if (con.containsPathway()) {
				con.changeMouseFunction("edit");
				activePw.getGraph().disableGraphTheory();
			}
		} else if ("del".equals(event)) {
			activePw.removeSelection();
		} else if ("discretePlace".equals(event)) {
			con.changeMouseFunction("edit");
			con.setPetriView(true);
			con.setPetriNetEditingMode(Elementdeclerations.discretePlace);
		} else if ("continuousPlace".equals(event)) {
			con.changeMouseFunction("edit");
			con.setPetriView(true);
			con.setPetriNetEditingMode(Elementdeclerations.continuousPlace);
		} else if ("discreteTransition".equals(event)) {
			con.changeMouseFunction("edit");
			con.setPetriView(true);
			con.setPetriNetEditingMode(Elementdeclerations.discreteTransition);
		} else if ("continiousTransition".equals(event)) {
			con.changeMouseFunction("edit");
			con.setPetriView(true);
			con.setPetriNetEditingMode(Elementdeclerations.continuousTransition);
		} else if ("stochasticTransition".equals(event)) {
			con.changeMouseFunction("edit");
			con.setPetriView(true);
			con.setPetriNetEditingMode(Elementdeclerations.stochasticTransition);
		}
	}
}
