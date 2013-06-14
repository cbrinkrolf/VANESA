package graph.eventhandlers;

import edu.uci.ics.jung.graph.ArchetypeEdge;
import edu.uci.ics.jung.graph.ArchetypeVertex;
import edu.uci.ics.jung.visualization.PickEventListener;
import graph.GraphInstance;
import gui.MainWindow;
import gui.MainWindowSingelton;

public class PickListener implements PickEventListener {

	public void edgePicked(ArchetypeEdge event) {

		if (event.containsUserDatumKey("alignment")) {

		} else {
			GraphInstance graphInstance = new GraphInstance();
			graphInstance.setSelectedObject(event);

			MainWindow w = MainWindowSingelton.getInstance();
			w.updateElementProperties();
		}

	}

	public void edgeUnpicked(ArchetypeEdge event) {
		MainWindow w = MainWindowSingelton.getInstance();
	}

	public void vertexPicked(ArchetypeVertex event) {

		GraphInstance graphInstance = new GraphInstance();
		graphInstance.setSelectedObject(event);

		MainWindow w = MainWindowSingelton.getInstance();
		w.updateElementProperties();

	}

	public void vertexUnpicked(ArchetypeVertex event) {
		MainWindow w = MainWindowSingelton.getInstance();
	}

}
