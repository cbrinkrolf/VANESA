package petriNet;

import java.awt.Desktop;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;

import biologicalElements.GraphElementAbstract;
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.petriNet.Place;
import biologicalObjects.nodes.petriNet.Transition;
import graph.ChangedFlags;
import graph.gui.Boundary;
import graph.gui.Parameter;
import gui.MainWindow;
import gui.MyPopUp;
import gui.SimMenue;
import moOutput.MOoutput;

public class PetriNetSimulation implements ActionListener {
	private static String pathCompiler = null;
	// final ProgressBar bar = new ProgressBar();
	private static String pathWorkingDirectory = null;
	private static String pathSim = null;
	private boolean stopped = false;
	private SimMenue menue = null;
	private Process simProcess = null;
	Thread compilingThread = null;
	private Process compileProcess = null;
	private boolean buildSuccess = false;
	private Thread allThread = null;

	private BufferedReader outputReader;
	private HashMap<BiologicalEdgeAbstract, String> bea2key;

	private ChangedFlags flags;
	private Server s = null;

	private String simName;

	private File simLib;
	private List<File> simLibs;
	private Pathway pw;

	private String simId;
	private boolean simExePresent;
	private StringBuilder logMessage = null;

	// TODO refactored version of threads for simulation needs to be tested and
	// evaluated. maybe show more hints / error messages
	// TODO log also simulation properties (start / number of intervals, duration
	// etc)
	public PetriNetSimulation(Pathway pw) {
		this.pw = pw;
		MainWindow.getInstance();
		pathWorkingDirectory = MainWindow.pathWorkingDirectory;
		File dir = new File(pathWorkingDirectory);
		this.simLibs = this.getLibs(dir);
	}

	public void showMenue() {
		if (this.menue == null) {
			this.simLibs = this.getLibs(new File(pathWorkingDirectory));
			menue = new SimMenue(pw, this, this.simLibs);
		} else {
			this.simLibs = this.getLibs(new File(pathWorkingDirectory));
			this.menue.setLibs(this.simLibs);
			this.menue.updateSimulationResults();
			this.menue.setState(Frame.NORMAL);
			this.menue.requestFocus();
			menue.setVisible(true);
		}
	}

	private void runOMCIA() {
		stopped = false;
		long zstVorher;
		long zstNachher;
		Double stopTime = menue.getStopValue();
		int intervals = menue.getIntervals();

		System.out.println("simNameOld: " + simName);
		MainWindow w = MainWindow.getInstance();
		flags = pw.getChangedFlags("petriNetSim");
		logAndShow("Simulation properties: stop=" + stopTime + ", intervals=" + intervals + ", integrator="
				+ menue.getIntegrator() + ", forced rebuild=" + menue.isForceRebuild());
		if (!this.checkInstallation()) {
			logAndShow("Installation error. Simulation stopped");
			return;
		} else {
			w.blurrUI();
			zstVorher = System.currentTimeMillis();

			allThread = new Thread() {

				public void run() {
					// while (!stopped) {
					try {
						s = new Server(pw, bea2key, simId);

						s.start();

						System.out.println("building ended");
						pw.getPetriPropertiesNet().setPetriNetSimulation(true);

						System.out.println("stop: " + stopTime);
						Thread simulationThread = new Thread() {

							public void run() {
								try {
									ProcessBuilder pb = new ProcessBuilder();
									boolean noEmmit = !true;

									String override = "";
									if (SystemUtils.IS_OS_WINDOWS) {
										override += "\"";
									}

									override += "-override=outputFormat=ia,stopTime=" + stopTime + ",stepSize="
											+ stopTime / intervals + ",tolerance=0.0001";
									if (flags.isParameterChanged()) {

										Iterator<Parameter> it = pw.getChangedParameters().keySet().iterator();
										GraphElementAbstract gea;
										Parameter param;

										while (it.hasNext()) {
											param = it.next();
											gea = pw.getChangedParameters().get(param);
											BiologicalNodeAbstract bna;
											if (gea instanceof BiologicalNodeAbstract) {
												bna = (BiologicalNodeAbstract) gea;
												override += ",'_" + bna.getName() + "_" + param.getName() + "'="
														+ param.getValue();
											}
										}
									}

									if (flags.isInitialValueChanged()) {
										Iterator<Place> it = pw.getChangedInitialValues().keySet().iterator();
										Place p;
										Double d;
										while (it.hasNext()) {
											p = it.next();
											d = pw.getChangedInitialValues().get(p);
											override += ",'" + p.getName() + "'.startMarks=" + d;
										}
									}

									if (flags.isBoundariesChanged()) {
										// System.out.println("chaaaaanged");
										Iterator<Place> it = pw.getChangedBoundaries().keySet().iterator();
										Place p;
										Boundary b;
										while (it.hasNext()) {
											p = it.next();
											b = pw.getChangedBoundaries().get(p);
											if (b.isLowerBoundarySet()) {
												override += ",'" + p.getName() + "'.minMarks=" + b.getLowerBoundary();
											}
											if (b.isUpperBoundarySet()) {
												override += ",'" + p.getName() + "'.maxMarks=" + b.getUpperBoundary();
											}
										}
									}

									if (SystemUtils.IS_OS_WINDOWS) {
										override += "\"";
									}
									System.out.println("override: " + override);

									// String program = "_omcQuot_556E7469746C6564";
									logAndShow("override statement: " + override);
									if (noEmmit) {
										pb.command(simName, "-s=" + menue.getIntegrator(), override, "-port=11111",
												"-noEventEmit", "-lv=LOG_STATS");
									} else {
										pb.command(simName, "-s=" + menue.getIntegrator(), override, "-port=11111",
												"-lv=LOG_STATS");
									}
									pb.redirectOutput();
									pb.directory(new File(pathSim));
									Map<String, String> env = pb.environment();
									// String envPath = env.get("PATH");
									String envPath = System.getenv("PATH");
									envPath += pathCompiler + "bin;";
									env.put("PATH", envPath);
									System.out.println("working path:" + env.get("PATH"));
									System.out.println(pb.environment().get("PATH"));
									simProcess = pb.start();

									setReader(new InputStreamReader(simProcess.getInputStream()));
								} catch (IOException e1) {
									simProcess.destroy();
									MyPopUp.getInstance().show("Simulation error:", e1.getMessage());
									e1.printStackTrace();
								}
								System.out.println("simulation thread finished");
							}
						};

						Thread redrawGraphThread = new Thread() {
							public void run() {
								pw.getGraph().getVisualizationViewer().requestFocus();
								// w.redrawGraphs();
								// System.out.println(pw.getPetriNet().getSimResController().get().getTime());
								List<Double> v = null;// pw.getPetriNet().getSimResController().get().getTime().getAll();
								// System.out.println("running");
								while (s.isRunning()) {
									if (v == null && pw.getPetriPropertiesNet().getSimResController()
											.getLastActive() != null) {
										v = pw.getPetriPropertiesNet().getSimResController().getLastActive().getTime()
												.getAll();
									}
									// System.out.println("im thread");
									w.redrawGraphs();
									// GraphInstance graphInstance = new
									// GraphInstance();
									// GraphContainer con =
									// ContainerSingelton.getInstance();
									// MainWindow w = MainWindowSingelton.getInstance();

									// double time =
									if (v != null && v.size() > 0) {
										menue.setTime("Time: " + (v.get(v.size() - 1)).toString());
									}
									try {
										sleep(100);
									} catch (InterruptedException e) {
										MyPopUp.getInstance().show("Simulation error:", e.getMessage());
										e.printStackTrace();
									}
								}
								menue.stopped();
								System.out.println("end of simulation");
								w.updatePCPView();
								w.redrawGraphs();
								w.revalidate();
								// w.repaint();
								if (v.size() > 0) {
									menue.setTime((v.get(v.size() - 1)).toString());
								}
								System.out.println("redraw thread finished");
							}
						};

						Thread outputThread = new Thread() {
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
											MyPopUp.getInstance().show("Simulation error:", e.getMessage());
											e.printStackTrace();
										}
									}
									try {
										sleep(100);
									} catch (InterruptedException e) {
										MyPopUp.getInstance().show("Simulation error:", e.getMessage());
										e.printStackTrace();
									}
								}
								try {
									System.out.println("outputReader server stopped");
									line = outputReader.readLine();
									while (line != null && line.length() > 0) {
										menue.addText(line + "\r\n");
										pw.getPetriPropertiesNet().getSimResController().get(simId).getLogMessage()
												.append(line + "\r\n");
										System.out.println(line);
										line = outputReader.readLine();
									}
									outputReader.close();
									outputReader = null;
								} catch (IOException e) {
									e.printStackTrace();
								}
								System.out.println("outputreader thread finished");
								stopped = true;
							}
						};

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
							MyPopUp.getInstance().show("Something went wrong!", "Simulation could not be built!");
							stopAction();
						}

						long zstNachher = System.currentTimeMillis();
						// System.out.println("Zeit benoetigt: " + ((zstNachher -
						// zstVorher) / 1000) + " sec");
						System.out.println("Time for compiling: " + ((zstNachher - zstVorher)) + " millisec");

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
						MyPopUp.getInstance().show("Simulation error:", e.getMessage());
						e.printStackTrace();
						MyPopUp.getInstance().show("Something went wrong", "The model couldn't be simulated!");
						w.unBlurrUI();
						menue.stopped();
						if (simProcess != null) {
							simProcess.destroy();
						}
						return;
					}
					// }
					System.out.println("all thread finished");
					// s = null;
				}
			};// --------end all thread

			boolean simLibChanged = false;
			if (this.simLib != null && !menue.getSimLib().getAbsolutePath().equals(this.simLib.getAbsolutePath())) {
				System.out.println("lib changed");
				simLibChanged = true;
			}

			simExePresent = false;

			if (simName != null && new File(simName).exists()) {
				simExePresent = true;
				logAndShow("simulation executable is already present");
			} else {
				logAndShow("executable needs to be compiled");
			}

			if (flags.isEdgeChanged() || flags.isNodeChanged() || flags.isEdgeWeightChanged()
					|| flags.isPnPropertiesChanged() || !simExePresent || simLibChanged || menue.isForceRebuild()) {
				try {
					logAndShow("(re) compilation due to changed properties");
					this.compile();
				} catch (IOException e) {
					w.unBlurrUI();
					buildSuccess = false;
					e.printStackTrace();
				} catch (InterruptedException e) {
					w.unBlurrUI();
					buildSuccess = false;
					e.printStackTrace();
				}
			} else {
				allThread.start();
			}

			// allThread.start();

			w.unBlurrUI();
		}
	}

	private boolean checkInstallation() {
		Map<String, String> env = System.getenv();
		if (env.containsKey("OPENMODELICAHOME") && new File(env.get("OPENMODELICAHOME")).isDirectory()) {
			pathCompiler = env.get("OPENMODELICAHOME");
			return true;
		} else {
			if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(MainWindow.getInstance(),
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
		}
		return false;
	}

	private void writeMosFile() throws IOException {
		String filter = "variableFilter=\"";

		Iterator<BiologicalNodeAbstract> it = pw.getAllGraphNodes().iterator();
		BiologicalNodeAbstract bna;
		int vars = 0;
		while (it.hasNext()) {
			bna = it.next();
			if (bna instanceof Place && !bna.hasRef()) {
				filter += "'" + bna.getName() + "'.t|";
				vars++;
			} else if (bna instanceof Transition) {
				filter += "'" + bna.getName() + "'.fire|";
				filter += "'" + bna.getName() + "'.actualSpeed|";
				vars += 2;
			}
		}

		Iterator<String> it2 = bea2key.values().iterator();
		String s;
		while (it2.hasNext()) {
			s = it2.next();
			filter += s + "|";
			filter += "der\\\\(" + s + "\\\\)|";
			vars += 2;
		}
		filter = filter.replace("[", "\\\\[");
		filter = filter.replace("]", "\\\\]");
		filter = filter.replace(".", "\\\\.");
		filter = filter.substring(0, filter.length() - 1);
		filter += "\"";
		// filter = "variableFilter=\".*\"";
		System.out.println("Filter: " + filter);
		System.out.println("expected number of output vars: " + vars);
		FileWriter fstream = new FileWriter(pathSim + "simulation.mos");
		BufferedWriter out = new BufferedWriter(fstream);
		pathSim = pathSim.replace('\\', '/');
		out.write("cd(\"" + pathSim + "\"); ");
		out.write("getErrorString();\r\n");
		if (simLib != null) {
			out.write("loadFile(\"" + simLib.getPath().replace("\\", "/") + "/package.mo\"); ");
		} else {
			out.write("loadModel(PNlib);");
		}
		out.write("getErrorString();\r\n");
		out.write("loadFile(\"simulation.mo\"); ");
		out.write("getErrorString();\r\n");
		// out.write("setDebugFlags(\"disableComSubExp\"); ");
		// out.write("getErrorString();\r\n");
		out.write("setCommandLineOptions(\"--preOptModules+=unitChecking\");");
		// out.write("setCommandLineOptions(\"+d=disableComSubExp
		// +unitChecking\");");
		out.write("getErrorString();\r\n");

		// CHRIS improve / correct filter
		out.write("buildModel('" + pw.getName() + "', " + filter + "); ");
		out.write("getErrorString();\r\n");
		out.close();
	}

	private void compile() throws IOException, InterruptedException {

		compilingThread = new Thread() {
			public void run() {
				menue.setTime("Compiling ...");
				try {
					System.out.println("edges changed: " + flags.isEdgeChanged());
					System.out.println("nodes changed: " + flags.isNodeChanged());
					System.out.println("edge weight changed: " + flags.isEdgeWeightChanged());
					System.out.println("pn prop changed " + flags.isPnPropertiesChanged());

					System.out.println("Building new executable");
					if (pathCompiler.charAt(pathCompiler.length() - 1) != File.separatorChar) {
						pathCompiler += File.separator;
					}

					pathSim = pathWorkingDirectory + "simulation" + File.separator;
					File dirSim = new File(pathSim);
					if (dirSim.isDirectory()) {
						FileUtils.cleanDirectory(dirSim);
					} else {
						new File(pathSim).mkdir();
					}

					simLib = menue.getSimLib();
					System.out.println("simulation lib: " + simLib);
					String packageInfo = "";
					if (simLib == null || simLib.getName().equals("PNlib")) {
						// packageInfo = "inner PNlib.Settings settings1;";
					} else {
						packageInfo = "import PNlib = " + simLib.getName() + ";";
					}
					MOoutput mo = new MOoutput(new FileOutputStream(new File(pathSim + "simulation.mo")), pw,
							packageInfo, false);
					bea2key = mo.getBea2resultkey();
					//

					writeMosFile();

					String bin = pathCompiler + "bin" + File.separator + "omc";

					if (SystemUtils.IS_OS_WINDOWS) {
						bin += ".exe";
					}

					compileProcess = new ProcessBuilder(bin, pathSim + "simulation.mos").start();
					InputStream os = compileProcess.getInputStream();

					// System.out.println(s);
					// boolean buildSuccess = true;
					System.out.println(stopped + " " + compileProcess.isAlive());
					compileProcess.waitFor();
					byte[] bytes = new byte[os.available()];
					os.read(bytes);
					String buildOutput = new String(bytes);
					System.out.println("build output: " + buildOutput);

					if (buildOutput.contains(
							"Warning: The following equation is INCONSISTENT due to specified unit information:")) {
						String message = "";
						int number = 0;
						String[] split = buildOutput.split("Warning: ");
						for (int i = 1; i < split.length; i++) {
							if (split[i].startsWith(
									"The following equation is INCONSISTENT due to specified unit information:")) {
								number++;
								message += split[i] + "\r\n";
							}
						}
						MyPopUp.getInstance().show("Warning: " + number + " expression(s) are inconsistent:", message);
					}

					StringTokenizer tokenizer = new StringTokenizer(buildOutput, ",");
					if (tokenizer.hasMoreTokens()) {
						String tmp = tokenizer.nextToken();
						// tmp.indexOf("{");
						simName = tmp.substring(tmp.indexOf("{") + 2, tmp.length() - 1);
						System.out.println("simName: " + simName);

						if (SystemUtils.IS_OS_WINDOWS) {
							simName += ".exe";
						}

						if (new File(simName).exists()) {
							buildSuccess = true;
						}
					}
					if (buildSuccess) {
						logMessage.append("compilation was successful!");
						System.out.println("build success");
						flags.reset();
						pw.getChangedInitialValues().clear();
						pw.getChangedParameters().clear();
						pw.getChangedBoundaries().clear();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				allThread.start();
			}
		};

		compilingThread.start();
		// compileProcess.waitFor();
		// stopped = true;
		// System.out.println("av: " +os.available());

	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getActionCommand().equals("start")) {
			if (allThread != null) {
				//allThread.interrupt();
				//allThread = null;
			}
			this.menue.started();
			// this.runOMC();
			if (!menue.isParameterized()) {
				simId = "simulation_" + pw.getPetriPropertiesNet().getSimResController().size() + "_"
						+ System.nanoTime();
				this.logMessage = pw.getPetriPropertiesNet().getSimResController().get(simId).getLogMessage();
				menue.addText(logMessage.toString());
				this.runOMCIA();
			} else {
				flags = pw.getChangedFlags("petriNetSim");
				BiologicalNodeAbstract bna = menue.getSelectedNode();
				String param = menue.getParameterName();
				List<Double> list = menue.getParameterValues();
				double value;
				Boundary b;
				for (int i = 0; i < list.size(); i++) {
					simId = "simulation_" + pw.getPetriPropertiesNet().getSimResController().size() + "_"
							+ System.nanoTime();
					value = list.get(i);
					System.out.println(value);
					if (bna instanceof Place) {
						switch (param) {
						case "token min":
							flags.setBoundariesChanged(true);
							b = new Boundary();
							b.setLowerBoundary(value);
							pw.getChangedBoundaries().put((Place) bna, b);
							break;
						case "token max":
							flags.setBoundariesChanged(true);
							b = new Boundary();
							b.setUpperBoundary(value);
							pw.getChangedBoundaries().put((Place) bna, b);
							break;
						case "token start":
							flags.setInitialValueChanged(true);
							pw.getChangedInitialValues().put((Place) bna, value);
							break;
						}
					} else if (bna instanceof Transition) {
						if (bna.getParameter(param) != null) {
							Parameter p = bna.getParameter(param);
							flags.setParameterChanged(true);
							pw.getChangedParameters().put(new Parameter(param, value, p.getUnit()), bna);
						} else {
							MyPopUp.getInstance().show("Error",
									"The parameter for parameterized simulation could not be found: " + param);
							i = list.size();
							break;
						}
					}
					// rounding name up to 4 decimals
					pw.getPetriPropertiesNet().getSimResController().get(simId)
							.setName(Math.round(value * 1000) / 1000.0 + "");
					this.logMessage = pw.getPetriPropertiesNet().getSimResController().get(simId).getLogMessage();
					this.runOMCIA();
				}
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
		this.menue.stopped();
		if (s != null && s.isRunning()) {
			s.stop();
		}
		if (compileProcess != null) {
			compileProcess.destroy();
			menue.setTime("compiling stopped!");
		}
		if (simProcess != null) {
			this.simProcess.destroy();
		}

	}

	private List<File> getLibs(File directory) {
		List<File> libs = new ArrayList<File>();
		if (directory.isDirectory()) {
			File[] files = directory.listFiles();
			for (int i = 0; i < files.length; i++) {
				File f = files[i];
				if (f.isDirectory()) {
					// System.out.println("folder: " + f.getName());
					if (new File(f, "package.mo").exists()) {
						libs.add(f);
						// System.out.println("existiert: " + f.getName());
					} else {
						File[] files2 = f.listFiles();
						for (int j = 0; j < files2.length; j++) {
							File f2 = files2[j];
							if (new File(f2, "package.mo").exists()) {
								libs.add(f2);
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
		this.menue.addText(text + "\r\n");
	}
}
