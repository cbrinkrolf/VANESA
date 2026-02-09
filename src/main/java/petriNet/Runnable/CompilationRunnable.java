package petriNet.Runnable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.StringTokenizer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;

import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.petriNet.ContinuousTransition;
import biologicalObjects.nodes.petriNet.DiscreteTransition;
import biologicalObjects.nodes.petriNet.Place;
import biologicalObjects.nodes.petriNet.StochasticTransition;
import configurations.Workspace;
import gui.PopUpDialog;
import gui.simulation.SimMenu;
import io.MOoutput;
import petriNet.CompilationProperties;
import petriNet.PetriNetSimulation;

public class CompilationRunnable {

	private CompilationProperties properties;
	private SimMenu menu;
	private Pathway pw;
	private PetriNetSimulation petriNetSim;

	public CompilationRunnable(CompilationProperties properties, SimMenu menu, Pathway pw,
			PetriNetSimulation petriNetSim) {
		this.properties = properties;
		this.menu = menu;
		this.pw = pw;
		this.petriNetSim = petriNetSim;
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

				writeMosFile();

				// TODO faster compilation
				// maybe additional flags for faster compilation or switch to Compile.bat that
				// is also used by OMEdit
				// e.g. "C:/Program Files/OpenModelica1.23.1-64bit/share/omc/scripts/Compile.bat
				// _omcQuot_0c504e5f736d616c6c5f746573742e73626d6c27 gcc ucrt64 parallel dynamic
				// 8 0"
				// compileProcess = new ProcessBuilder(bin, pathSim + "simulation.mos",
				// "--target=gcc", "--linkType=dynamic").start();
				Process compileProcess = new ProcessBuilder(
						properties.getPathCompiler().resolve(properties.getOmcFilePath()).toString(),
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
					while (line != null && line.length() > 0) {
						inputStreamString.append(line);
						line = inputReader.readLine();
					}
					Thread.sleep(200);
				}
				compileProcess.waitFor();

				line = inputReader.readLine();
				while (line != null && line.length() > 0) {
					petriNetSim.logAndShow(line);
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
				e.printStackTrace();
				petriNetSim.logAndShow(e.getMessage());
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

	private void writeMosFile() throws IOException {
		String filterRegEx = "";

		boolean containsPlace = false;
		boolean containsContinuousTransition = false;
		boolean containsDiscreteTransition = false;
		boolean containsStochasticTransition = false;
		int countPlaces = 0;
		int countContinuousTransitions = 0;
		int countDiscreteTransitions = 0;
		int countStochasticTransitions = 0;

		for (final BiologicalNodeAbstract bna : pw.getAllGraphNodes()) {
			if (!bna.isLogical()) {
				if (bna instanceof Place) {
					containsPlace = true;
					countPlaces++;
				} else if (bna instanceof ContinuousTransition) {
					containsContinuousTransition = true;
					countContinuousTransitions++;
				} else if (bna instanceof DiscreteTransition) {
					containsDiscreteTransition = true;
					countDiscreteTransitions++;
				} else if (bna instanceof StochasticTransition) {
					containsStochasticTransition = true;
					countStochasticTransitions++;
				}
			}
		}
		if (containsPlace) {
			filterRegEx += "'.+\\\\.t";
		}
		if (containsContinuousTransition) {
			if (filterRegEx.length() > 0) {
				filterRegEx += "|";
			}
			filterRegEx += ".+\\\\.fire|";
			filterRegEx += ".+\\\\.actualSpeed";
		}
		if (containsDiscreteTransition || containsStochasticTransition) {
			if (filterRegEx.length() > 0) {
				filterRegEx += "|";
			}
			filterRegEx += ".+\\\\.active|";
			filterRegEx += ".+\\\\.fireTime";
		}
		if (containsDiscreteTransition) {
			if (filterRegEx.length() > 0) {
				filterRegEx += "|";
			}
			filterRegEx += ".+\\\\.delay";
		}
		if (containsStochasticTransition) {
			if (filterRegEx.length() > 0) {
				filterRegEx += "|";
			}
			filterRegEx += ".+\\\\.putDelay";
		}
		if (!pw.getAllEdges().isEmpty()) {
			filterRegEx += "|.+\\\\.tokenFlow\\\\.inflow\\\\[\\\\d\\\\]|";
			filterRegEx += ".+der\\\\(.+\\\\.tokenflow\\\\.inflow\\\\[\\\\d\\\\]\\\\)";
			filterRegEx += "|.+\\\\.tokenFlow\\\\.outflow\\\\[\\\\d\\\\]|";
			filterRegEx += ".+der\\\\(.+\\\\.tokenflow\\\\.outflow\\\\[\\\\d\\\\]\\\\)";
		}
		filterRegEx = "variableFilter=\"" + filterRegEx + "\"";

		int vars = countPlaces + 2 * countContinuousTransitions + 3 * countDiscreteTransitions
				+ 3 * countStochasticTransitions + 2 * properties.getBea2key().values().size();

		System.out.println("expected number of output vars: " + vars);
		System.out.println("variableFilter: " + filterRegEx);
		try (final FileWriter fstream = new FileWriter(properties.getPathSim().resolve("simulation.mos").toFile());
				final BufferedWriter out = new BufferedWriter(fstream)) {
			out.write("cd(\"" + properties.getPathSim().toString().replace('\\', '/') + "\"); ");
			out.write("getErrorString();\r\n");
			if (menu.isBuiltInPNlibSelected()) {
				out.write("loadModel(PNlib,{\"" + properties.getSelectedSimLibVersion() + "\"}); ");
			} else {
				out.write("loadFile(\"" + properties.getSelectedSimLib().getPath().replace("\\", "/")
						+ "/package.mo\"); ");
			}
			out.write("getErrorString();\r\n");
			out.write("loadFile(\"simulation.mo\"); ");
			out.write("getErrorString();\r\n");
			// out.write("setDebugFlags(\"disableComSubExp\"); ");
			// out.write("getErrorString();\r\n");
			String commandLineOptions = "--unitChecking";
			if (properties.isOverrideEqPerFile() && properties.getEquationsPerFile() > 0) {
				commandLineOptions += " --equationsPerFile=" + properties.getEquationsPerFile();
			}

			out.write("setCommandLineOptions(\"" + commandLineOptions + "\"); ");
			// out.write("setCommandLineOptions(\"--unitChecking --newBackend
			// -d=mergeComponents\"); ");
			// out.write("setCommandLineOptions(\"+d=disableComSubExp
			// +unitChecking\");");
			out.write("getErrorString();\r\n");

			out.write("buildModel(" + properties.getModelicaModelName() + ", " + filterRegEx + "); ");
			out.write("getErrorString();\r\n");
		}
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
