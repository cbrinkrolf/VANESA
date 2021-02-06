package petriNet;

import java.awt.Desktop;
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
	private Process process;

	private BufferedReader outputReader;
	private HashMap<BiologicalEdgeAbstract, String> bea2key;

	private ChangedFlags flags;
	private Server s;

	private String simName;

	private File simLib;
	private List<File> simLibs;
	private Pathway pw;

	private String simId;

	public PetriNetSimulation(Pathway pw) {
		this.pw = pw;
		MainWindow.getInstance();
		pathWorkingDirectory = MainWindow.pathWorkingDirectory;
		File dir = new File(pathWorkingDirectory);
		this.simLibs = this.getLibs(dir);
	}

	public void showMenue() {
		// this.simLibs = this.getLibs(new File(pathWorkingDirectory));
		// menue = new SimMenue(this, this.simLibs);

		if (this.menue == null) {
			this.simLibs = this.getLibs(new File(pathWorkingDirectory));
			menue = new SimMenue(pw, this, this.simLibs);
		} else {
			if (!menue.isVisible()) {
				this.simLibs = this.getLibs(new File(pathWorkingDirectory));
				this.menue.setLibs(this.simLibs);
				this.menue.updateSimulationResults();
				menue.setVisible(true);
			} else {
				this.menue.requestFocus();
			}
		}
	}

	private void runOMCIA() {
		System.out.println("simNameOld: " + simName);
		MainWindow w = MainWindow.getInstance();
		w.blurrUI();

		flags = pw.getChangedFlags("petriNetSim");

		Map<String, String> env = System.getenv();

		if (env.containsKey("OPENMODELICAHOME") && new File(env.get("OPENMODELICAHOME")).isDirectory()) {
			pathCompiler = env.get("OPENMODELICAHOME");

			try {

				// String stopTime = JOptionPane
				// .showInputDialog("Stop Time", "20");
				// String intervals = JOptionPane.showInputDialog("Intervals",
				// "20");
				long zstVorher;
				long zstNachher;
				Double stopTime = menue.getStopValue();
				int intervals = menue.getIntervals();
				// this.path = "C:\\OpenModelica1.9.1Nightly\\";
				zstVorher = System.currentTimeMillis();

				boolean simLibChanged = false;
				if (this.simLib != null && !menue.getSimLib().getAbsolutePath().equals(this.simLib.getAbsolutePath())) {
					System.out.println("lib changed");
					simLibChanged = true;
				}

				boolean simExePresent = false;

				if (simName != null && new File(simName).exists()) {
					simExePresent = true;
				}

				if (flags.isEdgeChanged() || flags.isNodeChanged() || flags.isEdgeWeightChanged()
						|| flags.isPnPropertiesChanged() || !simExePresent || simLibChanged || menue.isForceRebuild()) {
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

					/*
					 * if (simLibs.size() < 1) { if(JOptionPane.YES_OPTION ==
					 * JOptionPane.showConfirmDialog(w,
					 * "Cannot find any Modelica Petri net library in the working directory.\n\n" +
					 * "Please put a Modelica Petri net library into \"" + pathWorkingDirectory +
					 * "\".\n" +
					 * "You can download the latest version of PNlib on GitHub (\"https://github.com/modelica-3rdparty/PNlib\")\n\n"
					 * + "Do you want to open the download page in your default web browser?" ,
					 * "Simulation aborted...", JOptionPane.YES_NO_OPTION)) {
					 * if(Desktop.isDesktopSupported()) { Desktop.getDesktop().browse(new
					 * URI("https://github.com/modelica-3rdparty/PNlib/releases" )); } }
					 * w.unBlurrUI(); return; }
					 */
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

					this.writeMosFile();

					stopped = false;

					// simT.run();

					String bin = pathCompiler + "bin" + File.separator + "omc";

					if (SystemUtils.IS_OS_WINDOWS) {
						bin += ".exe";
					}

					final Process p = new ProcessBuilder(bin, pathSim + "simulation.mos").start();
					InputStream os = p.getInputStream();

					// System.out.println(s);
					boolean buildSuccess = true;

					Thread t = new Thread() {
						public void run() {
							long totalTime = 1200000;
							try {
								for (long t = 0; t < totalTime; t += 1000) {
									sleep(1000);
								}
								p.destroy();
								// stopped = true;
							} catch (Exception e) {
							}
						}
					};
					t.start();
					p.waitFor();
					// System.out.println("av: " +os.available());
					byte[] bytes = new byte[os.available()];
					os.read(bytes);
					String buildOutput = new String(bytes);
					System.out.println(buildOutput);

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
					String tmp = tokenizer.nextToken();
					// tmp.indexOf("{");
					simName = tmp.substring(tmp.indexOf("{") + 2, tmp.length() - 1);
					System.out.println("simName: " + simName);

					if (SystemUtils.IS_OS_WINDOWS) {
						simName += ".exe";
					}

					try {
						t.stop();
					} catch (Exception e) {
						buildSuccess = false;
					}

					if (buildSuccess) {

						this.flags.reset();
						pw.getChangedInitialValues().clear();
						pw.getChangedParameters().clear();
						pw.getChangedBoundaries().clear();
					}
				}

				s = new Server(pw, bea2key, simId);

				s.start();
				System.out.println("building ended");
				pw.getPetriPropertiesNet().setPetriNetSimulation(true);

				System.out.println("stop: " + stopTime);
				Thread t1 = new Thread() {
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
							process = pb.start();

							setReader(new InputStreamReader(process.getInputStream()));
						} catch (IOException e1) {
							MyPopUp.getInstance().show("Simulation error:", e1.getMessage());
							e1.printStackTrace();
						}
					}
				};

				Thread t2 = new Thread() {
					public void run() {
						pw.getGraph().getVisualizationViewer().requestFocus();
						//w.redrawGraphs();
						// System.out.println(pw.getPetriNet().getSimResController().get().getTime());
						List<Double> v = null;// pw.getPetriNet().getSimResController().get().getTime().getAll();
						// System.out.println("running");
						while (s.isRunning()) {
							if (v == null && pw.getPetriPropertiesNet().getSimResController().getLastActive() != null) {
								v = pw.getPetriPropertiesNet().getSimResController().getLastActive().getTime().getAll();
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
								menue.setTime((v.get(v.size() - 1)).toString());
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

						w.repaint();
						if (v.size() > 0) {
							menue.setTime((v.get(v.size() - 1)).toString());
						}
						// w.updatePCPView();
					}
				};

				Thread t3 = new Thread() {
					public void run() {
						// System.out.println("running");
						String line;
						while (s.isRunning()) {
							// System.out.println("im thread");
							if (outputReader != null) {
								try {
									line = outputReader.readLine();
									if (line != null && line.length() > 0) {
										menue.addText(line + "\r\n");
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
								System.out.println(line);
								line = outputReader.readLine();

							}
							outputReader.close();
							outputReader = null;
						} catch (IOException e) {
							e.printStackTrace();
						}
						// w.updatePCPView();
					}
				};

				simExePresent = false;

				System.out.println(simName);
				if (new File(simName).exists()) {
					System.out.println("sim exists");
					simExePresent = true;
				}

				if (simExePresent) {
					t2.start();
					t3.start();

					t1.start();
				} else {
					// System.out.println("something wet wrong");
					MyPopUp.getInstance().show("Something went wrong!", "Simulation could not be built!");
					this.stopAction();
				}

				// System.out.println("before sim");
				// w.updatePCPView();
				// System.out.println("ps:" + pathSim);

				// final Process pSim = new
				// ProcessBuilder("cmd.exe","/c",pathSim+"simulation.bat").start();
				// ProcessBuilder pb = new
				// ProcessBuilder(pathSim+"simulation.exe","-override=outputFormat=ia","-port=11111","-lv=LOG_STATS");

				// Process process = pb.start();
				// System.out.println(pb.command());

				// Runtime.getRuntime().exec(new
				// String[]{pathSim+"simulation.exe","-override=outputFormat=ia","-port=11111","-lv=LOG_STATS"});
				/*
				 * Runtime.getRuntime().exec( "cmd /q/c start " + pathSim + "simulation.bat");
				 */
				System.out.println("drin");

				zstNachher = System.currentTimeMillis();
				// System.out.println("Zeit benoetigt: " + ((zstNachher -
				// zstVorher) / 1000) + " sec");
				System.out.println("Time for compiling: " + ((zstNachher - zstVorher)) + " millisec");

				/*
				 * Thread t3 = new Thread() { public void run() { long totalTime = 60000; try {
				 * sleep(2000); for (long t = 0; t < totalTime; t += 200) {
				 *
				 * sleep(200); Iterator<BiologicalNodeAbstract> it =
				 * graphInstance.getPathway().getAllNodes().iterator(); BiologicalNodeAbstract
				 * bna; Place p; graphInstance.getPathway().getPetriNet().addTime(new
				 * Double(graphInstance .getPathway().getPetriNet().getTime().size()+1));
				 * while(it.hasNext()){ bna = it.next(); if(bna instanceof Place){ p = (Place)
				 * bna; p.getPetriNetSimulationData().add(Math.random());
				 * //System.out.println(Math.random()); } }
				 *
				 * } System.out.println("endeeeeee"); stopped = true; } catch (Exception e) {
				 * e.printStackTrace(); } } }; t3.start();
				 */

				if (pw.hasGotAtLeastOneElement() && !stopped) {
					// graphInstance.getPathway().setPetriNet(true);
					// PetriNet petrinet = graphInstance.getPathway()
					// .getPetriNet();
					// petrinet.setPetriNetSimulationFile(pathSim
					// + "simulation_res.csv", true);
					// petrinet.initializePetriNet(bea2key);
				} else
					throw new Exception();
			} catch (Exception e) {
				MyPopUp.getInstance().show("Simulation error:", e.getMessage());
				e.printStackTrace();
				MyPopUp.getInstance().show("Something went wrong", "The model couldn't be simulated!");
				w.unBlurrUI();
				this.menue.stopped();
				this.process.destroy();
				return;
			}
			w.unBlurrUI();
			/*
			 * JOptionPane .showMessageDialog( w,
			 * "Simulation is completed. Select one or more places and click on Petri Net Simulation in the left toolbar to visualize and animate the results!"
			 * , "Simulation done...", JOptionPane.INFORMATION_MESSAGE);
			 */
		} else {
			if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(w,
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
			w.unBlurrUI();
		}
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
		// out.write("buildModel('" + pw.getName() + "'); ");
		out.write("getErrorString();\r\n");

		// out.write("fileName=\"simulate.mat\";\r\n");
		// out.write("CSVfile=\"simulate.csv\";\r\n");
		// out.write("n=readTrajectorySize(fileName);\r\n");
		// out.write("names = readTrajectoryNames(fileName);\r\n");
		// out.write("traj=readTrajectory(fileName,names,n);\r\n");
		// out.write("traj_transposed=transpose(traj);\r\n");
		// out.write("DataFiles.writeCSVmatrix(CSVfile, names,
		// traj_transposed);\r\n");
		// out.write("exit();\r\n");
		out.close();
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getActionCommand().equals("start")) {
			this.menue.started();
			// this.runOMC();
			if (!menue.isParameterized()) {
				simId = "simulation_" + pw.getPetriPropertiesNet().getSimResController().size() + "_"
						+ System.nanoTime();
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
					// roudning name up to 4 decimals
					pw.getPetriPropertiesNet().getSimResController().get(simId)
							.setName(Math.round(value * 1000) / 1000.0 + "");
					this.runOMCIA();

					while (s.isRunning()) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					// System.out.println("durchdurchdurch");
				}
			}

		} else if (event.getActionCommand().equals("stop")) {
			this.stopAction();
		}
	}

	private void setReader(InputStreamReader reader) {
		this.outputReader = new BufferedReader(reader);
	}

	private void stopAction() {
		System.out.println("stop");
		this.menue.stopped();
		if (s != null && s.isRunning()) {
			s.stop();
		}
		if (process != null) {
			this.process.destroy();
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
}
