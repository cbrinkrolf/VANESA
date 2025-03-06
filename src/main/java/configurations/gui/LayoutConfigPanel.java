package configurations.gui;

import javax.swing.JPanel;

import graph.GraphInstance;
import graph.jung.classes.MyGraph;

public abstract class LayoutConfigPanel extends JPanel {
	private static final long serialVersionUID = -6475804437343077119L;

	protected MyGraph getMyGraph() {
		return GraphInstance.getMyGraph();
	}

	public void resetValues() {
		// TODO reset to default values not implemented, yet
	}

	public void setValues() {
	}

	protected abstract void applySettings();
}
