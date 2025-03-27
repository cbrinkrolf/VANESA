package petriNet;

import java.awt.Desktop;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.RoundingMode;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JOptionPane;

import configurations.Workspace;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;

import biologicalElements.GraphElementAbstract;
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.petriNet.ContinuousTransition;
import biologicalObjects.nodes.petriNet.DiscretePlace;
import biologicalObjects.nodes.petriNet.DiscreteTransition;
import biologicalObjects.nodes.petriNet.Place;
import biologicalObjects.nodes.petriNet.StochasticTransition;
import biologicalObjects.nodes.petriNet.Transition;
import graph.ChangedFlags;
import graph.gui.Boundary;
import graph.gui.Parameter;
import gui.MainWindow;
import gui.PopUpDialog;
import gui.SimMenu;
import io.MOoutput;
import util.VanesaUtility;

public class PetriNetSimulation implements ActionListener {
	private static final Path OMC_FILE_PATH = Paths.get("bin").resolve(SystemUtils.IS_OS_WINDOWS ? "omc.exe" : "omc");
	private static Path pathCompiler = null;
	// final ProgressBar bar = new ProgressBar();
	private final Path pathWorkingDirectory;
	private static Path pathSim = null;
	private boolean stopped = false;
	private SimMenu menu = null;
	private Process simProcess = null;
	private Thread compilingThread = null;
	private Process compileProcess = null;
	private boolean buildSuccess = false;
	private Thread allThread = null;
	private boolean compiling = false;
	private Thread waitForServerConnection = null;

	private BufferedReader outputReader;
	private Map<BiologicalEdgeAbstract, String> bea2key;

	private ChangedFlags flags;
	private Server s = null;

	private String simName;

	private File selectedSimLib;
	private String selectedPNlibVersion;

	private List<File> customSimLibs;
	private final Pathway pw;
	private final MainWindow w;

	private String simId;
	private boolean simExePresent;
	private StringBuilder logMessage = null;
	private boolean installationChecked = false;

	private boolean shortModelName = false;
	private String modelicaModelName = "m";

	private boolean overrideEqPerFile = false;
	private int eqPerFile = -1;

	// private final String pnLibVersion = "3.0.0";
	// supported PNlib versions
	private final List<String> pnLibVersions = new ArrayList<>() {
		private static final long serialVersionUID = 176815129085958L;
		{
			add("3.0.0");
			add("2.2.0");
		}
	};
	// for the simulation results export of protected variables, necessary to detect
	// actual firing of stochastic transitions
	private boolean exportProtectedVariables = true;

	private String customExecutable = "";// "_omcQ_27D15_5FCPM_5FPN_2Esbml_27.exe";

	// CHRIS refactored version of threads for simulation needs to be tested and
	// evaluated. maybe show more hints / error messages
	// CHRIS log also simulation properties (start / number of intervals, duration
	// etc)
	public PetriNetSimulation(Pathway pw) {
		this.pw = pw;
		pathWorkingDirectory = VanesaUtility.getWorkingDirectoryPath();
		pathSim = pathWorkingDirectory.resolve("simulation");
		customSimLibs = getLibs(pathWorkingDirectory.toFile());
		w = MainWindow.getInstance();
	}

	public void showMenu() {
		if (menu == null) {
			if (Workspace.getCurrentSettings().getPNlibPath().length() > 0) {
				customSimLibs = getLibs(new File(Workspace.getCurrentSettings().getPNlibPath()));
			}
			menu = new SimMenu(pw, this, pnLibVersions, customSimLibs);
		} else {
			if (Workspace.getCurrentSettings().getPNlibPath().length() > 0) {
				customSimLibs = getLibs(new File(Workspace.getCurrentSettings().getPNlibPath()));
			}
			menu.setCustomLibs(customSimLibs);
			menu.updateSimulationResults();
			menu.setState(Frame.NORMAL);
			menu.requestFocus();
			menu.setVisible(true);
		}
	}

	private void runOMCIA(int port) {
		runOMCIA(port, "");
	}

	private void runOMCIA(int port, String overrideParameterized) {
		if (!installationChecked) {
			installationChecked = checkInstallation();
			if (!installationChecked) {
				logAndShow("Installation error. PNlib is not installed. Simulation stopped");
				PopUpDialog.getInstance().show("Installation error!",
						"Installation error. PNlib is not installed. Simulation stopped");
				return;
			}
		}
		stopped = false;
		final double stopTime = menu.getStopValue();
		final int intervals = menu.getIntervals();
		final double tolerance = menu.getTolerance();

		if (menu.isUseCustomExecutableSelected()) {
			customExecutable = menu.getCustomExecutableName();
			simName = pathSim.resolve(customExecutable).toFile().getAbsolutePath();
		}

		boolean shortModelNameChanged = !(shortModelName == menu.isUseShortNamesSelected());

		shortModelName = menu.isUseShortNamesSelected();
		if (shortModelName) {
			modelicaModelName = "m";
		} else {
			modelicaModelName = "'" + pw.getName() + "'";
		}

		boolean eqPerFileChanged = !(overrideEqPerFile == menu.isEquationsPerFileSelected());

		System.out.println("selected: " + menu.isEquationsPerFileSelected());
		overrideEqPerFile = menu.isEquationsPerFileSelected();
		if (overrideEqPerFile) {
			if (eqPerFile != menu.getCustomEquationsPerFile()) {
				eqPerFile = menu.getCustomEquationsPerFile();
				eqPerFileChanged = true;
			}
		}

		System.out.println("simNameOld: " + simName);
		System.out.println("port: " + port);
		flags = pw.getChangedFlags("petriNetSim");

		final int seed;
		if (menu.isRandomGlobalSeed()) {
			seed = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
		} else {
			seed = menu.getGlobalSeed();
		}

		String message = "Simulation properties: stop=" + stopTime + ", intervals=" + intervals + ", integrator="
				+ menu.getSolver() + ", tolerance=" + tolerance + ", seed=" + seed + ", forced rebuild="
				+ menu.isForceRebuild() + ", use short model name=" + String.valueOf(shortModelName)
				+ ", override equations per file=" + String.valueOf(overrideEqPerFile);
		if (overrideEqPerFile) {
			message += ", equations per file=" + String.valueOf(eqPerFile);
		}
		logAndShow(message);

		w.blurUI();

		System.out.println("stop: " + stopTime);
		System.out.println("tolerance: " + tolerance);
		Thread simulationThread = getSimulationThread(stopTime, intervals, tolerance, seed, overrideParameterized,
				port);

		Thread redrawGraphThread = getRedrawGraphThread();

		Thread outputThread = getSimulationOutputThread();

		allThread = getCombinedThread(simulationThread, redrawGraphThread, outputThread);

		waitForServerConnection = this.getWaitForServerConnectionThread(port);

		boolean simLibChanged = false;
		if (menu.isBuiltInPNlibSelected()) {
			selectedSimLib = null;
			if (selectedPNlibVersion == null || !pnLibVersions.contains(selectedPNlibVersion)
					|| !selectedPNlibVersion.equals(menu.getSelectedBuiltInPNLibVersion())) {
				simLibChanged = true;
				selectedPNlibVersion = menu.getSelectedBuiltInPNLibVersion();
			}
			logAndShow("simulation lib: built-in PNlib version " + selectedPNlibVersion);
		} else {
			selectedPNlibVersion = null;
			if (selectedSimLib == null
					|| !selectedSimLib.getAbsolutePath().equals(menu.getCustomPNLib().getAbsolutePath())) {
				simLibChanged = true;
				selectedSimLib = menu.getCustomPNLib();
			}
			logAndShow("simulation lib: custom PNlib: " + menu.getCustomPNLib().getAbsolutePath());
		}

		simExePresent = false;

		if (simName != null && new File(simName).exists()) {
			simExePresent = true;
			logAndShow("simulation executable is already present");
		} else {
			logAndShow("executable needs to be compiled");
		}

		if (menu.isUseCustomExecutableSelected()) {
			flags.reset();
			pw.getChangedInitialValues().clear();
			pw.getChangedParameters().clear();
			pw.getChangedBoundaries().clear();
			simLibChanged = false;
		}

		// System.out.println(flags.isEdgeChanged());
		// System.out.println(flags.isNodeChanged());
		// System.out.println(flags.isEdgeWeightChanged());
		// System.out.println(flags.isPnPropertiesChanged());
		// System.out.println(!simExePresent);
		// System.out.println(simLibChanged);
		// System.out.println(menu.isForceRebuild());

		if (flags.isEdgeChanged() || flags.isNodeChanged() || flags.isEdgeWeightChanged()
				|| flags.isPnPropertiesChanged() || !simExePresent || simLibChanged || menu.isForceRebuild()
				|| shortModelNameChanged || eqPerFileChanged) {
			try {
				logAndShow("(re) compilation due to changed properties");
				this.compile();
			} catch (IOException | InterruptedException e) {
				w.unBlurUI();
				buildSuccess = false;
				e.printStackTrace();
			}
		} else {
			waitForServerConnection.start();
		}
		w.unBlurUI();
	}

	private Thread getSimulationThread(double stopTime, int intervals, double tolerance, int seed,
			String overrideParameterized, int port) {
		return new Thread() {

			public void run() {
				try {
					ProcessBuilder pb = new ProcessBuilder();
					boolean noEmmit = !true;

					String override = "";
					if (SystemUtils.IS_OS_WINDOWS) {
						override += "\"";
					}

					override += "-override=outputFormat=ia,stopTime=" + stopTime + ",stepSize=" + stopTime / intervals
							+ ",tolerance=" + tolerance + ",seed=" + seed;
					System.out.println("parameter changed: " + flags.isParameterChanged());
					if (flags.isParameterChanged()) {
						GraphElementAbstract gea;
						BiologicalNodeAbstract bna;
						for (Parameter param : pw.getChangedParameters().keySet()) {
							// System.out.println(param.getName());
							// System.out.println(param.getValue());
							gea = pw.getChangedParameters().get(param);
							// System.out.println(gea.getName());
							if (gea instanceof BiologicalNodeAbstract) {
								bna = (BiologicalNodeAbstract) gea;
								override += ",'_" + bna.getName() + "_" + param.getName() + "'=" +
										param.getValue().toPlainString();
							} else {
								// CHRIS override parameters of edges
							}
						}
					}

					if (flags.isInitialValueChanged()) {
						Double d;
						for (Place p : pw.getChangedInitialValues().keySet()) {
							d = pw.getChangedInitialValues().get(p);
							override += ",'" + p.getName() + "'.start" + getMarksOrTokens(p) + "=" + d;
						}
					}

					if (flags.isBoundariesChanged()) {
						// System.out.println("chaaaaanged");
						Boundary b;
						for (Place p : pw.getChangedBoundaries().keySet()) {
							b = pw.getChangedBoundaries().get(p);
							if (b.isLowerBoundarySet()) {
								override += ",'" + p.getName() + "'.min" + getMarksOrTokens(p) + "="
										+ b.getLowerBoundary();
							}
							if (b.isUpperBoundarySet()) {
								override += ",'" + p.getName() + "'.max" + getMarksOrTokens(p) + "="
										+ b.getUpperBoundary();
							}
						}
					}

					override += overrideParameterized;

					if (SystemUtils.IS_OS_WINDOWS) {
						override += "\"";
					}
					System.out.println("override: " + override);

					// String program = "_omcQuot_556E7469746C6564";
					logAndShow("override statement: " + override);
					if (noEmmit) {
						if (exportProtectedVariables) {
							pb.command(simName, "-s=" + menu.getSolver(), override, "-port=" + port, "-noEventEmit",
									"-lv=LOG_STATS", "-emit_protected");
						} else {
							pb.command(simName, "-s=" + menu.getSolver(), override, "-port=" + port, "-noEventEmit",
									"-lv=LOG_STATS");
						}
					} else {
						if (exportProtectedVariables) {
							pb.command(simName, "-s=" + menu.getSolver(), override, "-port=" + port, "-lv=LOG_STATS",
									"-emit_protected");
						} else {
							pb.command(simName, "-s=" + menu.getSolver(), override, "-port=" + port, "-lv=LOG_STATS");
						}
					}
					pb.redirectOutput();
					pb.directory(pathSim.toFile());
					Map<String, String> env = pb.environment();
					// String envPath = env.get("PATH");
					String envPath = System.getenv("PATH");
					envPath = pathCompiler.resolve("bin") + ";" + envPath;
					env.put("PATH", envPath);
					System.out.println("working path:" + env.get("PATH"));
					System.out.println(pb.environment().get("PATH"));
					simProcess = pb.start();

					setReader(new InputStreamReader(simProcess.getInputStream()));
				} catch (IOException e1) {

					PopUpDialog.getInstance().show("Simulation error:", e1.getMessage());
					e1.printStackTrace();
					if (simProcess != null) {
						simProcess.destroy();
					}
					stopAction();
				}
				if (menu.isUseCustomExecutableSelected()) {
					simName = null;
				}
				System.out.println("simulation thread finished");
			}
		};
	}

	private Thread getRedrawGraphThread() {
		return new Thread() {
			public void run() {
				pw.getGraph().getVisualizationViewer().requestFocus();
				// w.redrawGraphs();
				// System.out.println(pw.getPetriNet().getSimResController().get().getTime());
				List<Double> v = null;// pw.getPetriNet().getSimResController().get().getTime().getAll();
				// System.out.println("running");
				DecimalFormat df = new DecimalFormat("#.#####");
				df.setRoundingMode(RoundingMode.HALF_UP);
				boolean simAddedToMenu = false;
				int counter = 0;
				while (s.isRunning()) {
					// System.out.println("while");

					if (v == null && pw.getPetriPropertiesNet().getSimResController().get(simId) != null) {
						v = pw.getPetriPropertiesNet().getSimResController().get(simId).getTime().getAll();
					}

					if (counter % 5 == 0) {
						w.redrawGraphs(true);
					}
					// System.out.println("before draw");
					w.redrawGraphs(false);
					// System.out.println("after draw");
					// GraphInstance graphInstance = new
					// GraphInstance();
					// GraphContainer con =
					// ContainerSingelton.getInstance();
					// MainWindow w = MainWindowSingelton.getInstance();

					// double time =
					if (v != null && v.size() > 0) {
						if (!simAddedToMenu) {
							menu.updateSimulationResults();
							simAddedToMenu = true;
						}
						menu.setTime("Time: " + df.format((v.get(v.size() - 1))));
					}
					try {
						sleep(1000);
					} catch (InterruptedException e) {
						PopUpDialog.getInstance().show("Simulation error:", e.getMessage());
						e.printStackTrace();
					}
					// System.out.println("end while");
					counter++;
				}
				menu.stopped();
				System.out.println("end of simulation");
				w.updateSimulationResultView();
				w.redrawGraphs(true);
				w.getFrame().revalidate();
				// w.repaint();
				if (v.size() > 0) {
					menu.setTime("Time: " + (v.get(v.size() - 1)).toString());
				}
				System.out.println("redraw thread finished");
			}
		};
	}

	private Thread getSimulationOutputThread() {
		return new Thread() {
			public void run() {
				// System.out.println("running");
				String line;
				while (s.isRunning()) {
					// System.out.println("im thread");
					if (outputReader != null) {
						try {
							line = outputReader.readLine();
							if (line != null && line.length() > 0) {
								logAndShow(line);

								System.out.println(line);
							}
						} catch (IOException e) {
							PopUpDialog.getInstance().show("Simulation error:", e.getMessage());
							e.printStackTrace();
						}
					}
					try {
						sleep(100);
					} catch (InterruptedException e) {
						PopUpDialog.getInstance().show("Simulation error:", e.getMessage());
						e.printStackTrace();
					}
				}
				try {
					System.out.println("outputReader server stopped");

					if (outputReader != null) {
						line = outputReader.readLine();
						while (line != null && line.length() > 0) {
							// menue.addText(line + "\r\n");
							// pw.getPetriPropertiesNet().getSimResController().get(simId).getLogMessage()
							// .append(line + "\r\n");
							logAndShow(line);
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
				stopped = true;
			}
		};
	}

	private Thread getWaitForServerConnectionThread(int port) {
		return new Thread() {

			public void run() {

				try {
					s = new Server(pw, bea2key, simId, port);
					s.start();
					System.out.print("wait until servers is ready to connect ");
					int i = 0;
					while (s.isRunning() && !s.isReadyToConnect() && !stopped) {
						if (i % 50 == 0) {
							System.out.println(".");
						}
						System.out.print(".");
						try {
							sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						i++;
					}
					System.out.println();
					if (s.isRunning() && s.isReadyToConnect() && !stopped) {
						// System.out.println("all threads start");
						allThread.start();
					}

				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		};
	}

	private boolean checkInstallation() {
		if (!checkInstallationOM()) {
			return false;
		}
		if (Workspace.getCurrentSettings().isOverridePNlibPath()) {
			return true;
		}
		// CHRIS put those checks in threads for example, so the messages will be shown
		// while checking/installing PNlib
		final OMCCommunicator omcCommunicator = new OMCCommunicator(pathCompiler.resolve(OMC_FILE_PATH));

		boolean allInstalledSuccess = true;
		for (String pnLibVersion : pnLibVersions) {
			System.out.println("test: " + pnLibVersion);
			if (omcCommunicator.isPNlibVersionInstalled(pnLibVersion)) {
				System.out.println(pnLibVersion + " is already installed");
				continue;
			} else {
				// correct PNlib version not installed
				if (omcCommunicator.isPackageManagerSupported()) {
					String message = "Correct version of PNlib (version " + pnLibVersion
							+ ") is not installed. Trying to install ...";
					logAndShow(message);
					PopUpDialog.getInstance().show("PNlib not installed!", message);
					if (omcCommunicator.isInstallPNlibSuccessful(pnLibVersion)) {
						message = "Installation of PNlib (version " + pnLibVersion + ") was successful!";
						logAndShow(message);
						PopUpDialog.getInstance().show("PNlib installation successful!", message);
					} else {
						message = "Installation of PNlib (version " + pnLibVersion
								+ ") was not successful! Please install required version of PNlib manually via OpenModelica Connection Editor (OMEdit)!";
						logAndShow(message);
						PopUpDialog.getInstance().show("PNlib installation was not successful!", message);
						allInstalledSuccess = false;
					}
				} else {
					String message = "Installation error. PNlib version " + pnLibVersion
							+ " is not installed properly. Please install required version of PNlib manually via OpenModelica Connection Editor (OMEdit)!";
					logAndShow(message);
					PopUpDialog.getInstance().show("PNlib installation was not successful!", message);
					allInstalledSuccess = false;
				}
			}
		}
		return allInstalledSuccess;
	}

	private boolean checkInstallationOM() {
		final String envPath = System.getenv("OPENMODELICAHOME");
		final String overridePath = Workspace.getCurrentSettings().isOverrideOMPath()
				? Workspace.getCurrentSettings().getOMPath().trim()
				: null;
		if (overridePath != null || envPath == null) {
			if (validateOMPath(overridePath)) {
				// noinspection DataFlowIssue
				pathCompiler = Paths.get(overridePath);
				return true;
			}
			logInvalidOMPath(overridePath);
		}
		if (validateOMPath(envPath)) {
			// noinspection DataFlowIssue
			pathCompiler = Paths.get(envPath);
			return true;
		}
		logInvalidOMPath(envPath);
		if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(MainWindow.getInstance().getFrame(),
				"Cannot find OpenModelica installation.\n\n"
						+ "Please install OpenModelica from \"https://openmodelica.org\".\n"
						+ "If OpenModelica is already installed, please set\n"
						+ "environment variable OPENMODELICAHOME to the installation directory.\n\n"
						+ "Do you want to open the OpenModelica homepage in your default web browser?",
				"Simulation aborted...", JOptionPane.YES_NO_OPTION)) {
			try {
				if (Desktop.isDesktopSupported()) {
					Desktop.getDesktop().browse(new URI("https://openmodelica.org"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	private boolean validateOMPath(final String path) {
		if (StringUtils.isBlank(path)) {
			return false;
		}
		final File file = new File(path);
		if (!file.exists() || !file.isDirectory()) {
			return false;
		}
		final File compilerFile = file.toPath().resolve(OMC_FILE_PATH).toFile();
		return compilerFile.exists() && compilerFile.isFile() && compilerFile.canExecute();
	}

	private void logInvalidOMPath(final String path) {
		if (path != null) {
			final File file = new File(path);
			final File compilerFile = file.toPath().resolve(OMC_FILE_PATH).toFile();
			logAndShow("Given path of OpenModelica (" + file.getAbsolutePath() + ") is not a correct path!");
			logAndShow("Path exists: " + file.exists());
			logAndShow("Path is directory: " + file.isDirectory());
			logAndShow("Executable " + compilerFile.getAbsolutePath() + " exists: " + compilerFile.exists());
			logAndShow("Executable is file: " + compilerFile.isFile());
			logAndShow("Executable is can be executed: " + compilerFile.canExecute());
		} else {
			logAndShow("No OpenModelica path available!");
		}
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
				+ 3 * countStochasticTransitions + 2 * bea2key.values().size();

		System.out.println("expected number of output vars: " + vars);
		System.out.println("variableFilter: " + filterRegEx);
		try (final FileWriter fstream = new FileWriter(pathSim.resolve("simulation.mos").toFile());
				final BufferedWriter out = new BufferedWriter(fstream)) {
			out.write("cd(\"" + pathSim.toString().replace('\\', '/') + "\"); ");
			out.write("getErrorString();\r\n");
			if (menu.isBuiltInPNlibSelected()) {
				out.write("loadModel(PNlib,{\"" + selectedPNlibVersion + "\"}); ");
			} else {
				out.write("loadFile(\"" + selectedSimLib.getPath().replace("\\", "/") + "/package.mo\"); ");
			}
			out.write("getErrorString();\r\n");
			out.write("loadFile(\"simulation.mo\"); ");
			out.write("getErrorString();\r\n");
			// out.write("setDebugFlags(\"disableComSubExp\"); ");
			// out.write("getErrorString();\r\n");
			String commandLineOptions = "--unitChecking";
			if (overrideEqPerFile && eqPerFile > 0) {
				commandLineOptions += " --equationsPerFile=" + String.valueOf(eqPerFile);
			}

			out.write("setCommandLineOptions(\"" + commandLineOptions + "\"); ");
			// out.write("setCommandLineOptions(\"--unitChecking --newBackend
			// -d=mergeComponents\"); ");
			// out.write("setCommandLineOptions(\"+d=disableComSubExp
			// +unitChecking\");");
			out.write("getErrorString();\r\n");

			out.write("buildModel(" + this.modelicaModelName + ", " + filterRegEx + "); ");
			out.write("getErrorString();\r\n");
		}
	}

	private void compile() throws IOException, InterruptedException {
		compiling = true;
		compilingThread = getCompilingThread();

		Thread compileGUI = new Thread(() -> {
			long start = System.currentTimeMillis();
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			String time = sdf.format(new Date());
			long zstVorher = System.currentTimeMillis();

			while (compiling) {
				menu.setTime("Compiling since " + time + " for: "
						+ DurationFormatUtils.formatDuration(System.currentTimeMillis() - start, "HH:mm:ss") + ".");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			long zstNachher = System.currentTimeMillis();
			// System.out.println("Zeit benoetigt: " + ((zstNachher -
			// zstVorher) / 1000) + " sec");
			logAndShow("Time for compiling: " + DurationFormatUtils.formatDuration(zstNachher - zstVorher, "HH:mm:ss")
					+ " (HH:mm:ss)");

		});

		compileGUI.start();
		compilingThread.start();
		// compileProcess.waitFor();
		// stopped = true;
		// System.out.println("av: " +os.available());
	}

	private Thread getCompilingThread() {
		return new Thread() {
			public void run() {
				// menu.setTime("Compiling ...");
				try {
					System.out.println("edges changed: " + flags.isEdgeChanged());
					System.out.println("nodes changed: " + flags.isNodeChanged());
					System.out.println("edge weight changed: " + flags.isEdgeWeightChanged());
					System.out.println("pn prop changed " + flags.isPnPropertiesChanged());
					System.out.println("Building new executable");
					final File dirSim = pathSim.toFile();
					if (dirSim.isDirectory()) {
						FileUtils.cleanDirectory(dirSim);
					} else {
						dirSim.mkdir();
					}

					String packageInfo = "";
					if (!menu.isBuiltInPNlibSelected()) {
						packageInfo = "import PNlib = " + selectedSimLib.getName() + ";";
					}
					MOoutput mo = new MOoutput(pathSim.resolve("simulation.mo").toFile(), modelicaModelName,
							packageInfo, menu.getGlobalSeed(), false);
					mo.write(pw);
					bea2key = mo.getBea2resultkey();

					writeMosFile();

					// TODO faster compilation
					// maybe additional flags for faster compilation or switch to Compile.bat that
					// is also used by OMEdit
					// e.g. "C:/Program Files/OpenModelica1.23.1-64bit/share/omc/scripts/Compile.bat
					// _omcQuot_0c504e5f736d616c6c5f746573742e73626d6c27 gcc ucrt64 parallel dynamic
					// 8 0"
					// compileProcess = new ProcessBuilder(bin, pathSim + "simulation.mos",
					// "--target=gcc", "--linkType=dynamic").start();
					compileProcess = new ProcessBuilder(pathCompiler.resolve(OMC_FILE_PATH).toString(),
							pathSim.resolve("simulation.mos").toString()).start();

					InputStream os = compileProcess.getInputStream();
					InputStream errs = compileProcess.getErrorStream();

					BufferedReader inputReader = new BufferedReader(new InputStreamReader(os));
					// System.out.println("input stream");
					// System.out.println(s);
					// boolean buildSuccess = true;
					System.out.println(stopped + " " + compileProcess.isAlive());
					StringBuilder inputStreamString = new StringBuilder();
					String line;
					while (compileProcess.isAlive()) {
						line = inputReader.readLine();
						while (line != null && line.length() > 0) {
							inputStreamString.append(line);
							line = inputReader.readLine();
						}
						sleep(200);
					}
					compileProcess.waitFor();

					line = inputReader.readLine();
					while (line != null && line.length() > 0) {
						logAndShow(line);
						// System.out.println(line);
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
						PopUpDialog.getInstance().show("Warning: " + number + " expression(s) are inconsistent:",
								message);
					}

					StringTokenizer tokenizer = new StringTokenizer(inputStreamString.toString(), ",");
					if (tokenizer.hasMoreTokens()) {
						String tmp = tokenizer.nextToken();
						// tmp.indexOf("{");
						simName = tmp.substring(tmp.indexOf("{") + 2, tmp.length() - 1);

						if (SystemUtils.IS_OS_WINDOWS) {
							simName += ".exe";
						}
						System.out.println("simName: " + simName);
						if (new File(simName).exists()) {
							buildSuccess = true;
						} else {
							PopUpDialog.getInstance().show("Something went wrong!", "Simulation could not be built!");
						}
					}
					if (buildSuccess) {
						logMessage.append("compilation was successful!");
						System.out.println("build success");
						flags.reset();
						pw.getChangedInitialValues().clear();
						pw.getChangedParameters().clear();
						pw.getChangedBoundaries().clear();
						compiling = false;
						waitForServerConnection.start();
					} else {
						logAndShow("Compilen was not successful. No executable was generated!");
						compiling = false;
						stopAction();
					}
				} catch (Exception e) {
					e.printStackTrace();
					logAndShow(e.getMessage());
					compiling = false;
					stopAction();
				}
				compiling = false;
				if (Workspace.getCurrentSettings().isCleanWorkingDirAfterCompilation() && buildSuccess) {
					// could be threaded maybe
					System.out.println("cleaning up working directory");
					cleanUpWorkingDirectory();
					System.out.println("finished cleaning up working directory");
				}
			}
		};
	}

	private Thread getCombinedThread(Thread simulationThread, Thread redrawGraphThread, Thread outputThread) {
		return new Thread() {

			public void run() {
				// while (!stopped) {
				try {
					System.out.println("building ended");
					pw.getPetriPropertiesNet().setPetriNetSimulation(true);

					simExePresent = false;

					System.out.println(simName);
					if (simName != null && new File(simName).exists()) {
						System.out.println("sim exists");
						simExePresent = true;
					}

					if (simExePresent) {
						redrawGraphThread.start();
						outputThread.start();

						simulationThread.start();
					} else {
						// System.out.println("something wet wrong");
						PopUpDialog.getInstance().show("Something went wrong!", "Simulation could not be built!");
						stopAction();
					}

					if (pw.hasGotAtLeastOneElement() && !stopped) {
						// graphInstance.getPathway().setPetriNet(true);
						// PetriNet petrinet = graphInstance.getPathway()
						// .getPetriNet();
						// petrinet.setPetriNetSimulationFile(pathSim
						// + "simulation_res.csv", true);
						// petrinet.initializePetriNet(bea2key);
					} else {
						// throw new Exception();

					}
				} catch (Exception e) {
					PopUpDialog.getInstance().show("Simulation error:", e.getMessage());
					e.printStackTrace();
					PopUpDialog.getInstance().show("Something went wrong", "The model couldn't be simulated!");
					w.unBlurUI();
					menu.stopped();
					if (simProcess != null) {
						simProcess.destroy();
					}
					return;
				}
				// }
				System.out.println("all thread finished");
			}
		};// --------end all thread
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getActionCommand().equals("start")) {
			int port = 11111;

			while (!VanesaUtility.isPortAvailable(port)) {
				port++;
			}
			System.out.println("Port " + port + " will be used.");

			if (allThread != null) {
				// allThread.interrupt();
				// allThread = null;
			}

			// this.runOMC();
			if (!menu.isParameterized()) {
				simId = "simulation_" + pw.getPetriPropertiesNet().getSimResController().size() + "_"
						+ System.nanoTime();
				this.logMessage = pw.getPetriPropertiesNet().getSimResController().get(simId).getLogMessage();
				menu.clearText();
				menu.addText(logMessage.toString());
				this.menu.started();
				this.runOMCIA(port);
			} else {
				// CHRIS needs to be checked again
				flags = pw.getChangedFlags("petriNetSim");
				BiologicalNodeAbstract bna = menu.getSelectedNode();
				String param = menu.getParameterName();
				List<Double> list = menu.getParameterValues();
				double value;
				PopUpDialog.getInstance().show("Parameterized simulation", "Parameters to be simulated:" + list.size());
				if (list.isEmpty()) {
					return;
				}
				// HashMap<Place, Boundary> boundaries = (HashMap<Place, Boundary>)
				// DeepObjectCopy.clone(pw.getChangedBoundaries()); //(HashMap<Place, Boundary>)
				// pw.getChangedBoundaries().clone();
				// HashMap<Place, Double> initialValues = (HashMap<Place, Double>)
				// DeepObjectCopy.clone(pw.getChangedInitialValues());
				// pw.getChangedFlags(param);
				// HashMap<Parameter, GraphElementAbstract> parameters = new HashMap<Parameter,
				// GraphElementAbstract>();
				// for(Parameter p :pw.getChangedParameters().keySet() ){
				// parameters.put(p, pw.getChangedParameters().get(p));
				// }
				// (HashMap<Parameter, GraphElementAbstract>)
				// DeepObjectCopy.clone(pw.getChangedParameters());
				// System.out.println("parmameter size org: "+parameters.size());
				String override;
				Place p;
				this.menu.started();
				for (int i = 0; i < list.size(); i++) {
					override = "";
					// pw.setChangedBoundaries(boundaries);
					// pw.setChangedInitialValues(initialValues);
					// pw.setChangedParameters(parameters);
					// System.out.println("--------------parameter size: "+parameters.size());

					value = list.get(i);
					simId = "simulation_" + pw.getPetriPropertiesNet().getSimResController().size() + "_"
							+ System.nanoTime() + "_" + value;

					System.out.println(value);

					if (bna instanceof Place) {
						p = (Place) bna;
						switch (param) {
						case "token min":
							// flags.setBoundariesChanged(true);
							// b = new Boundary();
							// b.setLowerBoundary(value);
							// pw.getChangedBoundaries().put((Place) bna, b);
							override += ",'" + p.getName() + "'.min" + getMarksOrTokens(p) + "=" + value;
							break;
						case "token max":
							// flags.setBoundariesChanged(true);
							// b = new Boundary();
							// b.setUpperBoundary(value);
							// pw.getChangedBoundaries().put((Place) bna, b);
							override += ",'" + p.getName() + "'.max" + getMarksOrTokens(p) + "=" + value;
							break;
						case "token start":
							// flags.setInitialValueChanged(true);
							// pw.getChangedInitialValues().put((Place) bna, value);
							override += ",'" + p.getName() + "'.start" + getMarksOrTokens(p) + "=" + value;
							break;
						}
					} else if (bna instanceof Transition) {
						if (bna.getParameter(param) != null) {
							// Parameter p = bna.getParameter(param);
							// flags.setParameterChanged(true);
							// pw.getChangedParameters().put(new Parameter(param, value, p.getUnit()), bna);
							override += ",'_" + bna.getName() + "_" + param + "'=" + value;
						} else {
							PopUpDialog.getInstance().show("Error",
									"The parameter for parameterized simulation could not be found: " + param);
							i = list.size();
							break;
						}
					}
					// rounding name up to 4 decimals
					pw.getPetriPropertiesNet().getSimResController().get(simId)
							.setName(bna.getName() + "_" + param + "=" + Math.round(value * 1000) / 1000.0 + "");
					this.logMessage = pw.getPetriPropertiesNet().getSimResController().get(simId).getLogMessage();
					menu.clearText();
					menu.addText(logMessage.toString());
					this.runOMCIA(port++, override);

					try {
						Thread.sleep(100);
						while (!stopped) {
							Thread.sleep(100);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				System.out.println("eeeeeeend of param sim");
			}

		} else if (event.getActionCommand().equals("stop")) {
			System.out.println("stopped by clicking stop");
			logMessage.append("Compiling / Simulation stopped by user!");
			this.stopAction();
		}
	}

	private void setReader(InputStreamReader reader) {
		this.outputReader = new BufferedReader(reader);
	}

	private void stopAction() {
		System.out.println("stop");
		this.buildSuccess = false;
		this.stopped = true;
		this.menu.stopped();
		if (s != null && s.isRunning()) {
			s.stop();
		}
		if (compileProcess != null) {
			compileProcess.destroy();
			menu.setTime("compiling / simulation aborted!");
		}
		if (simProcess != null) {
			this.simProcess.destroy();
		}

	}

	private List<File> getLibs(File directory) {
		// System.out.println("get libs");
		List<File> libs = new ArrayList<>();
		if (directory.isDirectory()) {
			File[] files = directory.listFiles();
			File f;
			File[] files2;
			File f2;
			for (int i = 0; i < files.length; i++) {
				f = files[i];
				if (f.isDirectory()) {
					// System.out.println("folder: " + f.getName());
					if (new File(f, "package.mo").exists()) {
						libs.add(f);
						// System.out.println("existiert1: " + f.getName());
					} else {
						files2 = f.listFiles();
						for (int j = 0; j < files2.length; j++) {
							f2 = files2[j];
							if (new File(f2, "package.mo").exists()) {
								libs.add(f2);
								// System.out.println("existiert2: " + f2.getName()+" -
								// "+f2.getParentFile().getName());
							}
						}
					}
				}
			}
		}
		return libs;
	}

	private void logAndShow(String text) {
		this.logMessage.append(text + "\r\n");
		this.menu.addText(text + "\r\n");
		System.out.println(text);
	}

	private String getMarksOrTokens(Place p) {
		if (p instanceof DiscretePlace) {
			return "Tokens";
		} else {
			return "Marks";
		}
	}

	public SimMenu getMenu() {
		return this.menu;
	}

	private void cleanUpWorkingDirectory() {
		new Thread() {

			@Override
			public void run() {
				long bytes = 0;
				int deletedFileCount = 0;
				final File dirSim = pathSim.toFile();
				if (dirSim.isDirectory()) {
					// FileUtils.cleanDirectory(dirSim);
					String[] extensions = { "bat", "c", "h", "o", "json", "intdata", "realdata" };
					Collection<File> files = FileUtils.listFiles(dirSim, extensions, false);

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
			}
		}.start();
	}
}
