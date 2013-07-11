package graph;

import gui.MainWindow;
import gui.MainWindowSingelton;

import java.awt.Cursor;

import biologicalElements.Pathway;

public class CreatePathway {

	MainWindow w = MainWindowSingelton.getInstance();
	GraphContainer con = ContainerSingelton.getInstance();
	String pathwayName;
	Pathway pw;
	Pathway parent=null;

	public CreatePathway(String title, Pathway parent) {
		this.parent = parent;
		pathwayName = title;
		buildPathway();
	}

	public CreatePathway(String title) {
		pathwayName = title;
		buildPathway();
	}

	public CreatePathway() {
		pathwayName = "Untitled";
		buildPathway();
	}

	private void buildPathway() {
		w.returnFrame().setCursor(new Cursor(Cursor.WAIT_CURSOR));
		Pathway newPW = null;
		if (parent == null) {
			newPW = new Pathway(pathwayName);
		} else {
			newPW = new Pathway(pathwayName, parent);
		}
		String newPathwayName = con.addPathway(pathwayName, new Pathway(
				pathwayName));
		pw = con.getPathway(newPathwayName);
		w.addTab(pw.getTab().getTitelTab());
		w.returnFrame().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}

	/*public void addVertex(Object node) {
		pw.getGraph().addVertexLabel(node);
	}*/

	public Pathway getPathway() {
		return pw;
	}
}
