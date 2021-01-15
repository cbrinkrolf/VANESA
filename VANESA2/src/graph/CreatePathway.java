package graph;

import java.awt.Cursor;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import javax.xml.stream.XMLStreamException;

import biologicalElements.Pathway;
import gui.MainWindow;
import xmlInput.sbml.JSBMLinput;
import xmlOutput.sbml.JSBMLoutput;

public class CreatePathway {

	MainWindow w = MainWindow.getInstance();
	GraphContainer con = GraphContainer.getInstance();
	String pathwayName;
	Pathway pw;
	Pathway parent = null;

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

	public CreatePathway(Pathway pathway) {

		PipedInputStream in = new PipedInputStream();
		PipedOutputStream out;
		try {
			out = new PipedOutputStream(in);
			new Thread(new Runnable() {
				public void run() {
					try {
						new JSBMLoutput(out, pathway).generateSBMLDocument();
					} catch (XMLStreamException e) {
						e.printStackTrace();
					}
				}
			}).start();
			new JSBMLinput(null).loadSBMLFile(in, pathway.getName());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private void buildPathway() {
		w.returnFrame().setCursor(new Cursor(Cursor.WAIT_CURSOR));
		// Pathway newPW = null;
		if (parent == null) {
			new Pathway(pathwayName);
		} else {
			new Pathway(pathwayName, parent);
		}
		String newPathwayName = con.addPathway(pathwayName, new Pathway(pathwayName));
		pw = con.getPathway(newPathwayName);
		w.addTab(pw.getTab().getTitelTab());
		w.returnFrame().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}

	/*
	 * public void addVertex(Object node) { pw.getGraph().addVertexLabel(node); }
	 */

	public Pathway getPathway() {
		return pw;
	}

}
