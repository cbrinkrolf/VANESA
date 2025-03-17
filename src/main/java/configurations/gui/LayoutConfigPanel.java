package configurations.gui;

import javax.swing.JPanel;

import graph.GraphInstance;
import graph.VanesaGraph;

public abstract class LayoutConfigPanel extends JPanel {
	private static final long serialVersionUID = -6475804437343077119L;

	protected VanesaGraph getGraph() {
		return GraphInstance.getVanesaGraph();
	}

	public void resetValues() {
		// TODO reset to default values not implemented, yet
	}

	public void setValues() {
	}

	protected abstract void applySettings();
}
