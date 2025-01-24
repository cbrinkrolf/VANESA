package configurations.gui;

import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JPanel;

import edu.uci.ics.jung.algorithms.layout.Layout;
//import edu.uci.ics.jung.visualization.Layout;
import graph.GraphInstance;
import graph.jung.classes.MyGraph;

public class ConfigPanel extends JPanel {
	private static final long serialVersionUID = -6475804437343077119L;
	private final String layoutName;

	public ConfigPanel(Class<? extends Layout> layout) {
		layoutName = layout.getSimpleName();
	}

	protected MyGraph getMyGraph() {
		return GraphInstance.getMyGraph();
	}

	public void resetValues() {
	}

	public void setValues() {
	}

	public void applySettings() {
		try {
			Method m = MyGraph.class.getMethod("changeTo" + getLayoutName());
			m.invoke(getMyGraph());
		} catch (Exception ex) {
			ex.printStackTrace();
			Logger.getLogger(ConfigPanel.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public String getLayoutName() {
		return layoutName;
	}
}
