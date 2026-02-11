package petriNet;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang3.time.DurationFormatUtils;

import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.petriNet.Place;
import biologicalObjects.nodes.petriNet.Transition;
import configurations.Workspace;
import graph.ChangedFlags;
import gui.MainWindow;
import gui.PopUpDialog;
import gui.simulation.SimMenu;
import io.MOoutput;
import petriNet.Runnable.CompilationRunnable;
import petriNet.Runnable.RedrawGraphThread;
import petriNet.Runnable.SimulationThread;
import util.VanesaUtility;

public class PetriNetSimulation implements ActionListener {

	/**
	 * Supported PNlib versions
	 */
	public static final List<String> SUPPORTED_PNLIB_VERSIONS = List.of("3.0.0", "2.2.0");
	private static Path pathCompiler = null;
	private static Path pathSim = null;

	private boolean stopped = false;
	private SimMenu menu = null;
	// private Process simProcess = null;
	private Process compileProcess = null;
	private Thread allThread = null;
	private boolean compiling = false;
	private Thread waitForServerConnection = null;

	// private Map<BiologicalEdgeAbstract, String> bea2key;

	private ChangedFlags flags;
	private Server s = null;

	// private String simName;

	private File selectedSimLib;
	private String selectedPNlibVersion;

	private List<File> customSimLibs;
	private final Pathway pw;
	private final MainWindow w;

	// private String simId;
	private boolean simExePresent;
	private StringBuilder logMessage = null;
	private boolean installationChecked = false;

	private boolean shortModelName = false;
	private String modelicaModelName = "m";

	private boolean overrideEqPerFile = false;
	private int eqPerFile = -1;

	private CompletableFuture<Void> compilationCompletableFuture;

	private CompilationProperties compilationProperties = new CompilationProperties();
	private SimulationProperties simulationProperties = new SimulationProperties();
	private SimulationLog simLog;

	// CHRIS refactored version of threads for simulation needs to be tested and
	// evaluated. maybe show more hints / error messages
	// CHRIS log also simulation properties (start / number of intervals, duration
	// etc)
	public PetriNetSimulation(final Pathway pw) {
		this.pw = pw;
		final Path pathWorkingDirectory = Workspace.getCurrent().getPath();
		pathSim = pathWorkingDirectory.resolve("simulation");
		customSimLibs = getLibs(pathWorkingDirectory.toFile());
		w = MainWindow.getInstance();
	}

	public void showMenu() {
		if (menu == null) {
			if (Workspace.getCurrentSettings().getPNlibPath().length() > 0) {
				customSimLibs = getLibs(new File(Workspace.getCurrentSettings().getPNlibPath()));
			}
			menu = new SimMenu(pw, this, customSimLibs);
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
		simLog = new SimulationLog(menu);
		if (!installationChecked) {
			installationChecked = checkInstallation();
			if (!installationChecked) {
				simLog.addLine("Installation error. PNlib is not installed. Simulation stopped");
				PopUpDialog.getInstance().show("Installation error!",
						"Installation error. PNlib is not installed. Simulation stopped");
				return;
			}
		}
		stopped = false;
		final BigDecimal stopTime = menu.getStopValue();
		final int intervals = menu.getIntervals();
		final BigDecimal tolerance = menu.getTolerance();

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

		System.out.println("simNameOld: " + compilationProperties.getSimName());
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
				+ menu.isForceRebuild() + ", use short model name=" + shortModelName + ", override equations per file="
				+ overrideEqPerFile;
		if (overrideEqPerFile) {
			message += ", equations per file=" + eqPerFile;
		}
		simLog.addLine(message);

		w.blurUI();

		System.out.println("stop: " + stopTime);
		System.out.println("tolerance: " + tolerance);
		simulationProperties.setFlags(flags);
		simulationProperties.setIntervals(intervals);
		simulationProperties.setOverrideParameterized(overrideParameterized);
		simulationProperties.setPathCompiler(pathCompiler);
		simulationProperties.setPathSim(pathSim);
		simulationProperties.setPort(port);
		simulationProperties.setSeed(seed);
		simulationProperties.setSolver(menu.getSolver());
		simulationProperties.setStopTime(stopTime);
		simulationProperties.setTolerance(tolerance);
		simulationProperties.setUseCustomExecutableSelected(menu.isUseCustomExecutableSelected());

		if (menu.isUseCustomExecutableSelected()) {
			final String customExecutable = menu.getCustomExecutableName();
			simulationProperties.setSimName(pathSim.resolve(customExecutable).toFile().getAbsolutePath());
		} else {
			simulationProperties.setSimName(compilationProperties.getSimName());
		}

		SimulationThread simThread = new SimulationThread(simulationProperties, pw, simLog);

		Thread simulationThread = simThread.getSimulationThread(onSimulationThreadSuccessRunnable(),
				onSimulationThreadErrorRunnable());

		Thread redrawGraphThread = new RedrawGraphThread(pw, menu, simulationProperties).getThread();

		Thread outputThread = getSimulationOutputThread();

		allThread = getCombinedThread(simulationThread, redrawGraphThread, outputThread);

		boolean simLibChanged = false;
		if (menu.isBuiltInPNlibSelected()) {
			selectedSimLib = null;
			if (selectedPNlibVersion == null || !SUPPORTED_PNLIB_VERSIONS.contains(selectedPNlibVersion)
					|| !selectedPNlibVersion.equals(menu.getSelectedBuiltInPNLibVersion())) {
				simLibChanged = true;
				selectedPNlibVersion = menu.getSelectedBuiltInPNLibVersion();
			}
			simLog.addLine("simulation lib: built-in PNlib version " + selectedPNlibVersion);
		} else {
			selectedPNlibVersion = null;
			if (selectedSimLib == null
					|| !selectedSimLib.getAbsolutePath().equals(menu.getCustomPNLib().getAbsolutePath())) {
				simLibChanged = true;
				selectedSimLib = menu.getCustomPNLib();
			}
			simLog.addLine("simulation lib: custom PNlib: " + menu.getCustomPNLib().getAbsolutePath());
		}

		simExePresent = false;
		String simName = compilationProperties.getSimName();
		if (simName != null && new File(simName).exists()) {
			simExePresent = true;
			simLog.addLine("simulation executable is already present");
		} else {
			simLog.addLine("executable needs to be compiled");
		}

		if (menu.isUseCustomExecutableSelected()) {
			compilationProperties.setBea2key(new MOoutput(null, false).getBea2resultkey(pw));
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
				simLog.addLine("(re) compilation due to changed properties");
				this.compile(port);
			} catch (IOException | InterruptedException e) {
				w.unBlurUI();
				e.printStackTrace();
			}
		} else {
			startServerAndSimulation(port);
		}
		w.unBlurUI();
	}

	private void collectDataForCompilation() {

	}

	private void collectDataForSimulation() {

	}

	private void startServerAndSimulation(int port) {
		waitForServerConnection = this.getWaitForServerConnectionThread(port);
		waitForServerConnection.start();
	}

	private Thread getSimulationOutputThread() {
		return new Thread(() -> {
			BufferedReader outputReader = simulationProperties.getOutputReader();
			while (simulationProperties.isServerRunning()) {
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
			stopped = true;
		});
	}

	private Thread getWaitForServerConnectionThread(int port) {
		return new Thread(() -> {
			try {
				s = new Server(pw, compilationProperties.getBea2key(), simulationProperties, port);
				s.start();
				System.out.print("wait until servers is ready to connect ");
				int i = 0;
				while (simulationProperties.isServerRunning() && !s.isReadyToConnect() && !stopped) {
					if (i % 50 == 0) {
						System.out.println(".");
					} else {
						System.out.print(".");
					}
					try {
						Thread.sleep(500);
					} catch (InterruptedException ignored) {
					}
					i++;
				}
				System.out.println();
				if (simulationProperties.isServerRunning() && s.isReadyToConnect() && !stopped) {
					allThread.start();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
	}

	private boolean checkInstallation() {
		final OMCCommunicator omcCommunicator = new OMCCommunicator(simLog);
		if (!omcCommunicator.isOpenModeilicaInstalled()) {
			return false;
		}
		pathCompiler = omcCommunicator.getPathCompiler();
		if (Workspace.getCurrentSettings().isOverridePNlibPath()) {
			return true;
		}
		// CHRIS put those checks in threads for example, so the messages will be shown
		// while checking/installing PNlib

		boolean allInstalledSuccess = true;
		for (String pnLibVersion : SUPPORTED_PNLIB_VERSIONS) {
			System.out.println("test: " + pnLibVersion);
			if (omcCommunicator.isPNlibVersionInstalled(pnLibVersion)) {
				System.out.println(pnLibVersion + " is already installed");
				continue;
			}
			// correct PNlib version not installed
			if (omcCommunicator.isPackageManagerSupported()) {
				String message = "Correct version of PNlib (version " + pnLibVersion
						+ ") is not installed. Trying to install ...";
				simLog.addLine(message);
				PopUpDialog.getInstance().show("PNlib not installed!", message);
				if (omcCommunicator.isInstallPNlibSuccessful(pnLibVersion)) {
					message = "Installation of PNlib (version " + pnLibVersion + ") was successful!";
					simLog.addLine(message);
					PopUpDialog.getInstance().show("PNlib installation successful!", message);
				} else {
					message = "Installation of PNlib (version " + pnLibVersion
							+ ") was not successful! Please install required version of PNlib manually via OpenModelica Connection Editor (OMEdit)!";
					simLog.addLine(message);
					PopUpDialog.getInstance().show("PNlib installation was not successful!", message);
					allInstalledSuccess = false;
				}
			} else {
				String message = "Installation error. PNlib version " + pnLibVersion
						+ " is not installed properly. Please install required version of PNlib manually via OpenModelica Connection Editor (OMEdit)!";
				simLog.addLine(message);
				PopUpDialog.getInstance().show("PNlib installation was not successful!", message);
				allInstalledSuccess = false;
			}
		}
		return allInstalledSuccess;
	}

	private void compile(int port) throws IOException, InterruptedException {
		compiling = true;
		// compilingThread = getCompilingThread();
		compilationProperties = new CompilationProperties();
		compilationProperties.setBuiltInPNlibSelected(menu.isBuiltInPNlibSelected());
		compilationProperties.setEquationsPerFile(menu.getCustomEquationsPerFile());
		compilationProperties.setFlags(flags);
		compilationProperties.setGlobalSeed(menu.getGlobalSeed());
		compilationProperties.setModelicaModelName(modelicaModelName);
		compilationProperties.setOverrideEqPerFile(overrideEqPerFile);
		compilationProperties.setPathCompiler(pathCompiler);
		compilationProperties.setPathSim(pathSim);
		compilationProperties.setSelectedSimLib(selectedSimLib);
		compilationProperties.setSelectedSimLibVersion(selectedPNlibVersion);
		// compilationProperties.setSimName(simName);

		CompilationRunnable compilation = new CompilationRunnable(compilationProperties, menu, pw, simLog);
		runCompilationCompletableFuture(compilation.getRunnable(getOnCompilationErrorRunnable()), port);
		getCompileGUIThread().start();
		// compilingThread.start();
		// compileProcess.waitFor();
		// stopped = true;
	}

	private Thread getCompileGUIThread() {
		return new Thread(() -> {
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
			simLog.addLine("Time for compiling: "
					+ DurationFormatUtils.formatDuration(zstNachher - zstVorher, "HH:mm:ss") + " (HH:mm:ss)");
		});
	}

	private void runCompilationCompletableFuture(Runnable compilationRunnable, int port) {// throws
																							// IllegalArgumentException
		// System.out.println("run comp. future:::");
		compilationCompletableFuture = CompletableFuture.runAsync(compilationRunnable).exceptionally(ex -> {

			ex.printStackTrace();
			System.out.println("in exceptionally");
			stopAction();
			return null;
		}).thenRun(getOnCompilationSuccessRunnable(port));
		// System.out.println("durch von completable future");
		// cf.cancel(true);
		// cf.thenRun(getOnCompilationSuccessRunnable());
		// System.out.println("after then run");
		// cf.cancel(true);

		// Assertions.assertThat(actual).isEqualTo(expected);
	}

	private Runnable getOnCompilationErrorRunnable() {
		return () -> {
			handleCompilationError();
		};
	}

	private void handleCompilationError() {
		simLog.addLine("Compiling was not successful. No executable was generated!");
		stopAction();
	}

	private void handleCompilationSuccess(int port) {
		simLog.addLine("compilation was successful!");
		System.out.println("build success");
		flags.reset();
		pw.getChangedInitialValues().clear();
		pw.getChangedParameters().clear();
		pw.getChangedBoundaries().clear();
		compiling = false;
		simulationProperties.setSimName(compilationProperties.getSimName());
		startServerAndSimulation(port);
	}

	private Runnable getOnCompilationSuccessRunnable(int port) {
		return () -> {
			handleCompilationSuccess(port);
		};
	}

	private Runnable onSimulationThreadSuccessRunnable() {
		return () -> {
			if (simulationProperties.isUseCustomExecutableSelected()) {
				simulationProperties.setSimName(null);
			}
		};
	}

	private Runnable onSimulationThreadErrorRunnable() {
		return () -> {
			stopAction();
		};
	}

	private Thread getCombinedThread(Thread simulationThread, Thread redrawGraphThread, Thread outputThread) {
		return new Thread(() -> {
			try {
				System.out.println("building ended");
				pw.getPetriPropertiesNet().setPetriNetSimulation(true);

				simExePresent = false;
				String simName = compilationProperties.getSimName();
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
					PopUpDialog.getInstance().show("Something went wrong!", "Simulation could not be built!");
					stopAction();
				}
			} catch (Exception e) {
				PopUpDialog.getInstance().show("Simulation error:", e.getMessage());
				e.printStackTrace();
				PopUpDialog.getInstance().show("Something went wrong", "The model couldn't be simulated!");
				w.unBlurUI();
				menu.stopped();
				if (simulationProperties.getSimProcess() != null) {
					simulationProperties.getSimProcess().destroy();
				}
				return;
			}
			System.out.println("all thread finished");
		});
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
				final String simId = "simulation_" + pw.getPetriPropertiesNet().getSimResController().size() + "_"
						+ System.nanoTime();
				simulationProperties.setSimId(simId);
				logMessage = pw.getPetriPropertiesNet().getSimResController().get(simId).getLogMessage();
				menu.clearText();
				menu.addText(logMessage.toString());
				menu.started();
				runOMCIA(port);
			} else {
				// CHRIS needs to be checked again
				flags = pw.getChangedFlags("petriNetSim");
				BiologicalNodeAbstract bna = menu.getSelectedNode();
				String param = menu.getParameterName();
				List<BigDecimal> list = menu.getParameterValues();
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
				this.menu.started();
				for (int i = 0; i < list.size(); i++) {
					String override = "";
					// pw.setChangedBoundaries(boundaries);
					// pw.setChangedInitialValues(initialValues);
					// pw.setChangedParameters(parameters);
					// System.out.println("--------------parameter size: "+parameters.size());

					final BigDecimal value = list.get(i);
					final String simId = "simulation_" + pw.getPetriPropertiesNet().getSimResController().size() + "_"
							+ System.nanoTime() + "_" + value.toPlainString();
					simulationProperties.setSimId(simId);

					System.out.println(value);

					if (bna instanceof Place) {
						final Place p = (Place) bna;
						switch (param) {
						case "token min":
							// flags.setBoundariesChanged(true);
							// b = new Boundary();
							// b.setLowerBoundary(value);
							// pw.getChangedBoundaries().put((Place) bna, b);
							override += ",'" + p.getName() + "'.min" + VanesaUtility.getMarksOrTokens(p) + "="
									+ value.toPlainString();
							break;
						case "token max":
							// flags.setBoundariesChanged(true);
							// b = new Boundary();
							// b.setUpperBoundary(value);
							// pw.getChangedBoundaries().put((Place) bna, b);
							override += ",'" + p.getName() + "'.max" + VanesaUtility.getMarksOrTokens(p) + "="
									+ value.toPlainString();
							break;
						case "token start":
							// flags.setInitialValueChanged(true);
							// pw.getChangedInitialValues().put((Place) bna, value);
							override += ",'" + p.getName() + "'.start" + VanesaUtility.getMarksOrTokens(p) + "="
									+ value.toPlainString();
							break;
						}
					} else if (bna instanceof Transition) {
						if (bna.getParameter(param) != null) {
							// Parameter p = bna.getParameter(param);
							// flags.setParameterChanged(true);
							// pw.getChangedParameters().put(new Parameter(param, value, p.getUnit()), bna);
							override += ",'_" + bna.getName() + "_" + param + "'=" + value.toPlainString();
						} else {
							PopUpDialog.getInstance().show("Error",
									"The parameter for parameterized simulation could not be found: " + param);
							i = list.size();
							break;
						}
					}
					// rounding name up to 4 decimals
					pw.getPetriPropertiesNet().getSimResController().get(simId).setName(
							bna.getName() + "_" + param + "=" + (Math.round(value.doubleValue() * 1000) / 1000.0));
					logMessage = pw.getPetriPropertiesNet().getSimResController().get(simId).getLogMessage();
					menu.clearText();
					menu.addText(logMessage.toString());
					runOMCIA(port++, override);

					try {
						do {
							Thread.sleep(100);
						} while (!stopped);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				System.out.println("eeeeeeend of param sim");
			}
		} else if (event.getActionCommand().equals("stop")) {
			simLog.addLine("Compiling / Simulation stopped by user!");
			stopAction();
		}
	}

	private void stopAction() {
		System.out.println("stop");
		if (compilationCompletableFuture != null) {
			compilationCompletableFuture.cancel(true);
		}
		compiling = false;
		stopped = true;
		menu.stopped();
		System.out.println(simulationProperties.isServerRunning());
		if (s != null && simulationProperties.isServerRunning()) {
			s.stop();
		}
		if (compileProcess != null) {
			compileProcess.destroy();
			menu.setTime("compiling / simulation aborted!");
		}
		if (simulationProperties.getSimProcess() != null) {
			simulationProperties.getSimProcess().destroy();
		}

	}

	private List<File> getLibs(final File directory) {
		final List<File> libs = new ArrayList<>();
		if (directory.isDirectory()) {
			final File[] files = directory.listFiles();
			if (files != null) {
				for (final File f : files) {
					if (f.isDirectory()) {
						if (new File(f, "package.mo").exists()) {
							libs.add(f);
						} else {
							final File[] files2 = f.listFiles();
							if (files2 != null) {
								for (final File f2 : files2) {
									if (new File(f2, "package.mo").exists()) {
										libs.add(f2);
									}
								}
							}
						}
					}
				}
			}
		}
		return libs;
	}

	public SimMenu getMenu() {
		return menu;
	}
}
