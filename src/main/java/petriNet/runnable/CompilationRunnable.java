package petriNet.runnable;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.StringTokenizer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;

import biologicalElements.Pathway;
import configurations.Workspace;
import gui.PopUpDialog;
import gui.simulation.SimMenu;
import io.MOSWriter;
import io.MOoutput;
import petriNet.CompilationProperties;
import petriNet.OMCCommunicator;
import petriNet.SimulationLog;

public class CompilationRunnable extends CompilationRunnableAbstract {

	private Pathway pw;

	public CompilationRunnable(CompilationProperties properties, SimMenu menu, Pathway pw, SimulationLog simLog) {
		this.properties = properties;
		this.menu = menu;
		this.pw = pw;
		this.simLog = simLog;
	}

	public Runnable getRunnable(Runnable onError) {

		return () -> {
			// menu.setTime("Compiling ...");
			boolean buildSuccess = false;
			try {
				System.out.println("edges changed: " + properties.getFlags().isEdgeChanged());
				System.out.println("nodes changed: " + properties.getFlags().isNodeChanged());
				System.out.println("edge weight changed: " + properties.getFlags().isEdgeWeightChanged());
				System.out.println("pn prop changed " + properties.getFlags().isPnPropertiesChanged());
				System.out.println("Building new executable");
				final File dirSim = properties.getPathSim().toFile();
				if (dirSim.isDirectory()) {
					FileUtils.cleanDirectory(dirSim);
				} else {
					dirSim.mkdir();
				}

				String packageInfo = "";
				if (!menu.isBuiltInPNlibSelected()) {
					packageInfo = "import PNlib = " + properties.getSelectedSimLib().getName() + ";";
				}
				MOoutput mo = new MOoutput(properties.getPathSim().resolve("simulation.mo").toFile(),
						properties.getModelicaModelName(), packageInfo, menu.getGlobalSeed(), false);
				mo.write(pw);
				properties.setBea2key(mo.getBea2resultkey());

				new MOSWriter(properties, pw).writeMosFile(menu.isBuiltInPNlibSelected());

				// TODO faster compilation
				// maybe additional flags for faster compilation or switch to Compile.bat that
				// is also used by OMEdit
				// e.g. "C:/Program Files/OpenModelica1.23.1-64bit/share/omc/scripts/Compile.bat
				// _omcQuot_0c504e5f736d616c6c5f746573742e73626d6c27 gcc ucrt64 parallel dynamic
				// 8 0"
				// compileProcess = new ProcessBuilder(bin, pathSim + "simulation.mos",
				// "--target=gcc", "--linkType=dynamic").start();
				Process compileProcess = new ProcessBuilder(
						properties.getPathCompiler().resolve(OMCCommunicator.OMC_FILE_PATH).toString(),
						properties.getPathSim().resolve("simulation.mos").toString()).start();
				properties.setCompileProcess(compileProcess);
				InputStream os = compileProcess.getInputStream();
				InputStream errs = compileProcess.getErrorStream();

				BufferedReader inputReader = new BufferedReader(new InputStreamReader(os));
				System.out.println("Compile process is alive: " + compileProcess.isAlive());
				StringBuilder inputStreamString = new StringBuilder();
				String line;
				while (compileProcess.isAlive()) {
					line = inputReader.readLine();
					while (line != null && !line.isEmpty()) {
						inputStreamString.append(line);
						line = inputReader.readLine();
					}
					Thread.sleep(200);
				}
				compileProcess.waitFor();

				line = inputReader.readLine();
				while (line != null && !line.isEmpty()) {
					simLog.addLine(line);
					inputStreamString.append(line);
					line = inputReader.readLine();
				}
				inputReader.close();
				System.out.println("compile output: " + inputStreamString.toString().stripTrailing());

				if (inputStreamString.toString().contains(
						"Warning: The following equation is INCONSISTENT due to specified unit information:")) {
					String message = "";
					int number = 0;
					String[] split = inputStreamString.toString().split("Warning: ");
					for (int i = 1; i < split.length; i++) {
						if (split[i].startsWith(
								"The following equation is INCONSISTENT due to specified unit information:")) {
							number++;
							message += split[i] + "\r\n";
						}
					}
					PopUpDialog.getInstance().show("Warning: " + number + " expression(s) are inconsistent:", message);
				}

				StringTokenizer tokenizer = new StringTokenizer(inputStreamString.toString(), ",");
				if (tokenizer.hasMoreTokens()) {
					String tmp = tokenizer.nextToken();
					String simName = tmp.substring(tmp.indexOf("{") + 2, tmp.length() - 1);
					if (SystemUtils.IS_OS_WINDOWS) {
						simName += ".exe";
					}
					System.out.println("simName: " + simName);
					properties.setSimName(simName);
					if (new File(simName).exists()) {
						buildSuccess = true;
					} else {
						PopUpDialog.getInstance().show("Something went wrong!", "Simulation could not be built!");
					}
				}
				if (buildSuccess) {
					// onSuccess.run();
				} else {
					// compiling = false;
					onError.run();
				}
			} catch (Exception e) {
				System.out.println("catch exception.....");
				Thread.currentThread().interrupt();
				e.printStackTrace();
				simLog.addLine(e.getMessage());
				onError.run();
			}
			if (Workspace.getCurrentSettings().isCleanWorkingDirAfterCompilation() && buildSuccess) {
				// could be threaded maybe
				System.out.println("cleaning up working directory");
				cleanUpWorkingDirectory();
				System.out.println("finished cleaning up working directory");
			}
		};
	}

	private void cleanUpWorkingDirectory() {
		new Thread(() -> {
			long bytes = 0;
			int deletedFileCount = 0;
			final File dirSim = properties.getPathSim().toFile();
			if (dirSim.isDirectory()) {
				final String[] extensions = { "bat", "c", "h", "o", "json", "intdata", "realdata" };
				final Collection<File> files = FileUtils.listFiles(dirSim, extensions, false);
				for (File f : files) {
					try {
						bytes += FileUtils.sizeOf(f);
						FileUtils.delete(f);
						deletedFileCount++;
					} catch (IOException e) {
						e.printStackTrace();
						if (Workspace.getCurrentSettings().isDeveloperMode()) {
							PopUpDialog.getInstance().show("Error deleting file: " + f.getName(), e.getMessage());
						}
					}
				}
			}
			System.out.println("deleted files: " + deletedFileCount + ", freed disk space: "
					+ FileUtils.byteCountToDisplaySize(bytes));
		}).start();
	}
}
