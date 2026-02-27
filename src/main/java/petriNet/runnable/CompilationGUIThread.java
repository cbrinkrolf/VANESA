package petriNet.runnable;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.time.DurationFormatUtils;

import gui.simulation.SimMenu;
import petriNet.CompilationProperties;
import petriNet.SimulationLog;

public class CompilationGUIThread extends CompilationRunnableAbstract {

	public CompilationGUIThread(CompilationProperties properties, SimMenu menu, SimulationLog simLog) {
		this.properties = properties;
		this.menu = menu;
		this.simLog = simLog;
	}

	public Thread getThread() {
		return new Thread(() -> {
			String dateFormat = "HH:mm:ss";
			long start = System.currentTimeMillis();
			SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
			String time = sdf.format(new Date());
			long zstVorher = System.currentTimeMillis();

			while (properties.isCompiling()) {
				menu.setTime("Compiling since " + time + " for: "
						+ DurationFormatUtils.formatDuration(System.currentTimeMillis() - start, dateFormat) + ".");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			long zstNachher = System.currentTimeMillis();
			simLog.addLine("Time for compiling: "
					+ DurationFormatUtils.formatDuration(zstNachher - zstVorher, dateFormat) + " (" + dateFormat + ")");
		});
	}
}
