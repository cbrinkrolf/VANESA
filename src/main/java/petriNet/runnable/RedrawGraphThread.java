package petriNet.runnable;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

import biologicalElements.Pathway;
import gui.MainWindow;
import gui.PopUpDialog;
import gui.simulation.SimMenu;
import petriNet.SimulationProperties;

public class RedrawGraphThread extends SimulationRunnableAbstract {

	private final Pathway pw;
	private final SimMenu menu;
	private final MainWindow w;

	private int counter = 0;
	private List<Double> values = null;
	private boolean simAddedToMenu = false;
	private DecimalFormat df = new DecimalFormat("#.#####");

	public RedrawGraphThread(Pathway pw, SimMenu menu, SimulationProperties properties) {
		this.pw = pw;
		this.menu = menu;
		this.properties = properties;
		w = MainWindow.getInstance();
	}

	public Thread getThread() {

		return new Thread(() -> {
			pw.getGraph().getVisualizationViewer().requestFocus();
			// pw.getPetriNet().getSimResController().get().getTime().getAll();

			df.setRoundingMode(RoundingMode.HALF_UP);

			while (properties.isServerRunning()) {
				processWhileRunning();
			}
			menu.stopped();
			System.out.println("end of simulation");
			w.updateSimulationResultView();
			w.redrawGraphs(true);
			w.getFrame().revalidate();
			if (!values.isEmpty()) {
				menu.setTime("Time: " + (values.get(values.size() - 1)).toString());
			}
			System.out.println("redraw thread finished");
		});
	}

	private void processWhileRunning() {
		final String simId = properties.getSimId();
		if (values == null && pw.getPetriPropertiesNet().getSimResController().get(simId) != null) {
			values = pw.getPetriPropertiesNet().getSimResController().get(simId).getTime().getAll();
		}
		if (counter % 5 == 0) {
			w.redrawGraphs(true);
		}
		w.redrawGraphs(false);
		if (values != null && !values.isEmpty()) {
			if (!simAddedToMenu) {
				menu.updateSimulationResults();
				simAddedToMenu = true;
			}
			menu.setTime("Time: " + df.format((values.get(values.size() - 1))));
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			PopUpDialog.getInstance().show("Simulation error:", e.getMessage());
			e.printStackTrace();
			simLog.addLine(e.getMessage());
			Thread.currentThread().interrupt();
		}
		counter++;
	}
}
