package transformation.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import biologicalElements.ElementDeclarations;
import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import graph.GraphContainer;
import gui.ToolBarButton;

public class RuleEditingWindowListener implements ActionListener {
	private final Pathway bn;
	private final Pathway pn;

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
				activePw.getGraph2().disableGraphTheory();
			}
		} else if ("pick".equals(event)) {
			if (con.containsPathway()) {
				con.changeMouseFunction("pick");
				activePw.getGraph2().disableGraphTheory();
			}
		} else if ("center".equals(event)) {
			activePw.getGraphRenderer().zoomAndCenterGraph();
		} else if ("edit".equals(event)) {
			if (con.containsPathway()) {
				con.changeMouseFunction("edit");
				activePw.getGraph2().disableGraphTheory();
			}
		} else if ("del".equals(event)) {
			// activePw.removeSelection();
		} else if ("place".equals(event)) {
			con.changeMouseFunction("edit");
			con.setPetriView(true);
			con.setPetriNetEditingMode(ElementDeclarations.place);
		} else if ("discretePlace".equals(event)) {
			con.changeMouseFunction("edit");
			con.setPetriView(true);
			con.setPetriNetEditingMode(ElementDeclarations.discretePlace);
		} else if ("continuousPlace".equals(event)) {
			con.changeMouseFunction("edit");
			con.setPetriView(true);
			con.setPetriNetEditingMode(ElementDeclarations.continuousPlace);
		} else if ("transition".equals(event)) {
			con.changeMouseFunction("edit");
			con.setPetriView(true);
			con.setPetriNetEditingMode(ElementDeclarations.transition);
		} else if ("discreteTransition".equals(event)) {
			con.changeMouseFunction("edit");
			con.setPetriView(true);
			con.setPetriNetEditingMode(ElementDeclarations.discreteTransition);
		} else if ("continuousTransition".equals(event)) {
			con.changeMouseFunction("edit");
			con.setPetriView(true);
			con.setPetriNetEditingMode(ElementDeclarations.continuousTransition);
		} else if ("stochasticTransition".equals(event)) {
			con.changeMouseFunction("edit");
			con.setPetriView(true);
			con.setPetriNetEditingMode(ElementDeclarations.stochasticTransition);
		} else if ("adjustDown".equals(event)) {
			Collection<BiologicalNodeAbstract> nodes = activePw.getSelectedNodes();
			activePw.adjustDown(nodes);
		} else if ("adjustLeft".equals(event)) {
			Collection<BiologicalNodeAbstract> nodes = activePw.getSelectedNodes();
			activePw.adjustLeft(nodes);
		} else if ("adjustHorizontalSpace".equals(event)) {
			Collection<BiologicalNodeAbstract> nodes = activePw.getSelectedNodes();
			activePw.adjustHorizontalSpace(nodes);
		} else if ("adjustVerticalSpace".equals(event)) {
			Collection<BiologicalNodeAbstract> nodes = activePw.getSelectedNodes();
			activePw.adjustVerticalSpace(nodes);
		}
	}
}
