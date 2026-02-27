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
			BufferedReader outputReader = properties.getOutputReader();
			while (properties.isServerRunning()) {
				if (outputReader != null) {
					try {
						String line = outputReader.readLine();
						if (line != null && line.length() > 0) {
							simLog.addLine(line);
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
				}
			}
			try {
				System.out.println("outputReader server stopped");
				if (outputReader != null) {
					String line = outputReader.readLine();
					while (line != null && line.length() > 0) {
						// menue.addText(line + "\r\n");
						// pw.getPetriPropertiesNet().getSimResController().get(simId).getLogMessage()
						// .append(line + "\r\n");
						simLog.addLine(line);
						System.out.println(line);
						line = outputReader.readLine();
					}
					outputReader.close();
					outputReader = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("outputreader thread finished");
			properties.setFinished(true);
		});
	}
}
