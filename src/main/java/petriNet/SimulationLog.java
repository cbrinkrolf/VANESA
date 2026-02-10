package petriNet;

import gui.simulation.SimMenu;

public class SimulationLog {

	private SimMenu menu = null;
	private StringBuilder logMessage;

	public SimulationLog(SimMenu menu) {
		logMessage = new StringBuilder();
		this.menu = menu;
	}

	public void addLine(String line) {
		logMessage.append(line).append("\r\n");
		menu.addText(line + "\r\n");
		System.out.println(line);
	}

}
