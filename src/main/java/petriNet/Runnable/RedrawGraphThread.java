package petriNet.Runnable;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

import biologicalElements.Pathway;
import gui.MainWindow;
import gui.PopUpDialog;
import gui.simulation.SimMenu;
import petriNet.SimulationProperties;

public class RedrawGraphThread {

	private final Pathway pw;
	private final SimMenu menu;
	private final MainWindow w;
	private final SimulationProperties properties;

	public RedrawGraphThread(Pathway pw, SimMenu menu, SimulationProperties properties) {
		this.pw = pw;
		this.menu = menu;
		this.properties = properties;
		w = MainWindow.getInstance();
	}

	public Thread getThread() {

		return new Thread(() -> {
			pw.getGraph().getVisualizationViewer().requestFocus();
			List<Double> v = null;// pw.getPetriNet().getSimResController().get().getTime().getAll();
			DecimalFormat df = new DecimalFormat("#.#####");
			df.setRoundingMode(RoundingMode.HALF_UP);
			boolean simAddedToMenu = false;
			int counter = 0;
			while (properties.isServerRunning()) {
				final String simId = properties.getSimId();
				if (v == null && pw.getPetriPropertiesNet().getSimResController().get(simId) != null) {
					v = pw.getPetriPropertiesNet().getSimResController().get(simId).getTime().getAll();
				}
				if (counter % 5 == 0) {
					w.redrawGraphs(true);
				}
				w.redrawGraphs(false);
				if (v != null && v.size() > 0) {
					if (!simAddedToMenu) {
						menu.updateSimulationResults();
						simAddedToMenu = true;
					}
					menu.setTime("Time: " + df.format((v.get(v.size() - 1))));
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					PopUpDialog.getInstance().show("Simulation error:", e.getMessage());
					e.printStackTrace();
				}
				counter++;
			}
			menu.stopped();
			System.out.println("end of simulation");
			w.updateSimulationResultView();
			w.redrawGraphs(true);
			w.getFrame().revalidate();
			if (v.size() > 0) {
				menu.setTime("Time: " + (v.get(v.size() - 1)).toString());
			}
			System.out.println("redraw thread finished");
		});
	}
}
