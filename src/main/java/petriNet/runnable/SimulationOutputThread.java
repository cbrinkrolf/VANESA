package petriNet.runnable;

import java.io.BufferedReader;
import java.io.IOException;

import gui.PopUpDialog;
import petriNet.SimulationLog;
import petriNet.SimulationProperties;

public class SimulationOutputThread extends SimulationRunnableAbstract {

	public SimulationOutputThread(SimulationProperties properties, SimulationLog simLog) {
		this.properties = properties;
		this.simLog = simLog;
	}

	public Thread getThread() {
		return new Thread(() -> {

			while (properties.isServerRunning()) {
				processWhileRunning();
			}

			try {
				BufferedReader outputReader = properties.getOutputReader();
				// System.out.println("outputReader server stopped");
				if (outputReader != null) {
					// System.out.println("rest content of reader");
					String line = outputReader.readLine();
					while (line != null && !line.isEmpty()) {
						// menue.addText(line + "\r\n");
						// pw.getPetriPropertiesNet().getSimResController().get(simId).getLogMessage()
						// .append(line + "\r\n");
						simLog.addLine(line);
						System.out.println(line);
						line = outputReader.readLine();
					}
					// System.out.println("closing output reader");
					outputReader.close();
				} else {
					System.err.println("outputReader is null!");
				}
			} catch (IOException e) {
				e.printStackTrace();
				simLog.addLine(e.getMessage());
			}
			System.out.println("outputreader thread finished");
			properties.setFinished(true);
		});
	}

	private void processWhileRunning() {
		BufferedReader outputReader = properties.getOutputReader();
		if (outputReader != null) {
			try {
				// System.out.println("is ready: " + outputReader.ready());
				// outputReader.ready();
				String line = outputReader.readLine();
				if (line != null && !line.isEmpty()) {
					simLog.addLine("run: " + line);
				}
			} catch (IOException e) {
				PopUpDialog.getInstance().show("Simulation error:", e.getMessage());
				e.printStackTrace();
			}
		}
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			PopUpDialog.getInstance().show("Simulation error:", e.getMessage());
			e.printStackTrace();
			simLog.addLine(e.getMessage());
			Thread.currentThread().interrupt();
		}
	}
}
