package petriNet.runnable;

import java.io.IOException;

import biologicalElements.Pathway;
import petriNet.CompilationProperties;
import petriNet.Server;
import petriNet.SimulationProperties;

public class StartServerAndWaitForConnection extends SimulationRunnableAbstract {

	private CompilationProperties compilationProperties;
	private Pathway pw;
	private Thread combinedThread;

	public StartServerAndWaitForConnection(CompilationProperties compilationProperties,
			SimulationProperties simulationProperties, Pathway pw, Thread combinedThread) {
		this.compilationProperties = compilationProperties;
		this.properties = simulationProperties;
		this.pw = pw;
		this.combinedThread = combinedThread;
	}

	public Thread getThread(int port) {
		return new Thread(() -> {
			try {
				Server s = new Server(pw, compilationProperties.getBea2key(), properties, port);
				properties.setServer(s);
				s.start();
				System.out.print("wait until servers is ready to connect ");
				int i = 0;
				while (properties.isServerRunning() && !s.isReadyToConnect() && !properties.isFinished()) {
					if (i % 50 == 0) {
						System.out.println(".");
					} else {
						System.out.print(".");
					}
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
						Thread.currentThread().interrupt();
					}
					i++;
				}
				System.out.println();
				if (properties.isServerRunning() && s.isReadyToConnect() && !properties.isFinished()) {
					combinedThread.start();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
}
