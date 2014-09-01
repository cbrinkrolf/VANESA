package petriNet;

import graph.ContainerSingelton;
import graph.GraphContainer;
import graph.GraphInstance;
import gui.MainWindow;
import gui.MainWindowSingelton;
import gui.SimMenue;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;

import moOutput.MOoutput;
import biologicalElements.PetriNet;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class PetriNetSimulation implements ActionListener {
	private static String pathCompiler = null;
	// final ProgressBar bar = new ProgressBar();
	private static String pathWorkingDirectory = null;
	private static String pathSim = null;
	private boolean stopped = false;
	private SimMenue menue;

	private BufferedReader outputReader;

	public PetriNetSimulation() {
		menue = new SimMenue(this);
	}

	private void runOMC() {
		GraphInstance graphInstance = new GraphInstance();
		GraphContainer con = ContainerSingelton.getInstance();
		MainWindow w = MainWindowSingelton.getInstance();
		w.setLockedPane(true);
		Map<String, String> env = System.getenv();

		if (SystemUtils.IS_OS_WINDOWS) {
			pathWorkingDirectory = env.get("APPDATA");
		} else {
			pathWorkingDirectory = env.get("HOME");
		}

		if (env.containsKey("OPENMODELICAHOME")
				&& new File(env.get("OPENMODELICAHOME")).isDirectory()) {
			pathCompiler = env.get("OPENMODELICAHOME");

			try {

				String stopTime = JOptionPane
						.showInputDialog("Stop Time", "20");
				String intervals = JOptionPane.showInputDialog("Intervals",
						"20");
				// this.path = "C:\\OpenModelica1.9.1Nightly\\";

				if (pathCompiler.charAt(pathCompiler.length() - 1) != File.separatorChar) {
					pathCompiler += File.separator;
				}
				if (pathWorkingDirectory
						.charAt(pathWorkingDirectory.length() - 1) != File.separatorChar) {
					pathWorkingDirectory += File.separator;
				}

				pathWorkingDirectory += "vanesa" + File.separator;

				File dir = new File(pathWorkingDirectory);

				if (!dir.isDirectory()) {
					dir.mkdir();
				}

				pathSim = pathWorkingDirectory + "simulation" + File.separator;
				File dirSim = new File(pathSim);
				if (dirSim.isDirectory()) {
					FileUtils.cleanDirectory(dirSim);
				} else {
					new File(pathSim).mkdir();
				}

				boolean abort = false;
				String missing = "";
				if (!new File(pathWorkingDirectory + "PNlib.mo").exists()) {
					abort = true;
					missing += "PNlib.mo\n in " + pathWorkingDirectory;
				}
				/*
				 * if (!new File(path + "dsmodel.c").exists()) { abort = true;
				 * missing += "dsmodel.c\n"; } if (!new File(path +
				 * "myrandom.c").exists()) { abort = true; missing +=
				 * "myrandom.c\n"; }
				 */
				if (abort) {
					JOptionPane
							.showMessageDialog(
									w,
									"Following files which are required for simulation are missing in the dymola folder:\n"
											+ missing, "Simulation aborted...",
									JOptionPane.ERROR_MESSAGE);
					w.setLockedPane(false);
					return;
				}

				MOoutput mo = new MOoutput(new File(pathSim + "simulation.mo"),
						graphInstance.getPathway());
				HashMap<BiologicalEdgeAbstract, String> bea2key = mo
						.getBea2resultkey();
				//
				FileWriter fstream = new FileWriter(pathSim + "simulation.mos");
				BufferedWriter out = new BufferedWriter(fstream);
				String path2 = pathSim.replace('\\', '/');
				out.write("cd(\"" + path2 + "\");\r\n");
				out.write("getErrorString();\r\n");
				out.write("loadFile(\"../PNlib.mo\");\r\n");
				out.write("getErrorString();\r\n");
				out.write("loadFile(\"simulation.mo\");\r\n");
				out.write("getErrorString();\r\n");
				out.write("simulate(simulation, stopTime=" + stopTime
						+ ", method=\"euler\", numberOfIntervals=" + intervals
						// only places
						// +
						// ", outputFormat=\"csv\", variableFilter=\"^[a-zA-Z_0-9]*.t\");\r\n");
						+ ", outputFormat=\"csv\");\r\n");
				out.write("getErrorString();\r\n");
				// variableFilter=\"^[a-zA-Z_0-9]*.t\" only places

				// out.write("fileName=\"simulate.mat\";\r\n");
				// out.write("CSVfile=\"simulate.csv\";\r\n");
				// out.write("n=readTrajectorySize(fileName);\r\n");
				// out.write("names = readTrajectoryNames(fileName);\r\n");
				// out.write("traj=readTrajectory(fileName,names,n);\r\n");
				// out.write("traj_transposed=transpose(traj);\r\n");
				// out.write("DataFiles.writeCSVmatrix(CSVfile, names, traj_transposed);\r\n");
				// out.write("exit();\r\n");
				out.close();
				stopped = false;
				long zstVorher;
				long zstNachher;

				zstVorher = System.currentTimeMillis();

				String bin = null;

				if (SystemUtils.IS_OS_WINDOWS) {
					bin = pathCompiler + "bin" + File.separator + "omc.exe";
				} else {
					bin = pathCompiler + "bin" + File.separator + "omc";
				}
				final Process p = new ProcessBuilder(bin, pathSim
						+ "simulation.mos").start();

				Thread t = new Thread() {
					public void run() {
						long totalTime = 120000;
						try {
							for (long t = 0; t < totalTime; t += 1000) {
								sleep(1000);
							}
							p.destroy();
							stopped = true;
						} catch (Exception e) {
						}
					}
				};
				t.start();
				p.waitFor();
				try {
					t.stop();
				} catch (Exception e) {
				}
				zstNachher = System.currentTimeMillis();
				System.out.println("Zeit benoetigt: "
						+ ((zstNachher - zstVorher) / 1000) + " sec");
				System.out.println("Zeit benoetigt: "
						+ ((zstNachher - zstVorher)) + " millisec");
				if (con.containsPathway()
						&& graphInstance.getPathway().hasGotAtLeastOneElement()
						&& !stopped) {
					graphInstance.getPathway().setPetriNet(true);
					PetriNet petrinet = graphInstance.getPathway()
							.getPetriNet();
					petrinet.setPetriNetSimulationFile(pathSim
							+ "simulation_res.csv", true);
					petrinet.initializePetriNet(bea2key);
					w.updatePCPView();
				} else
					throw new Exception();
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane
						.showMessageDialog(
								MainWindowSingelton.getInstance(),
								"Something went wrong. The model couldn't be simulated!",
								"Error occured...", JOptionPane.ERROR_MESSAGE);
				w.setLockedPane(false);
				return;
			}
			w.setLockedPane(false);
			JOptionPane
					.showMessageDialog(
							w,
							"Simulation is completed. Select one or more places and click on Petri Net Simulation in the left toolbar to visualize and animate the results!",
							"Simulation done...",
							JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(w,
					"Environment variable OPENMODELICAHOME not found.",
					"OPENMODELICA not found...", JOptionPane.QUESTION_MESSAGE);
			w.setLockedPane(false);
		}

		/*
		 * Thread t2 = new Thread() { public void run() { long totalTime =
		 * 60000; try { sleep(2000); for (long t = 0; t < totalTime; t += 10) {
		 * if(t%1000 == 0){ w.redrawGraphs(); } sleep(10);
		 * Iterator<BiologicalNodeAbstract> it =
		 * graphInstance.getPathway().getAllNodes().iterator();
		 * BiologicalNodeAbstract bna; Place p;
		 * graphInstance.getPathway().getPetriNet().addTime(new
		 * Double(graphInstance.getPathway().getPetriNet().getTime().size()+1));
		 * while(it.hasNext()){ bna = it.next(); if(bna instanceof Place){ p =
		 * (Place) bna; p.getPetriNetSimulationData().add(Math.random());
		 * //System.out.println(Math.random()); } }
		 * 
		 * } System.out.println("endeeeeee"); stopped = true; } catch (Exception
		 * e) { e.printStackTrace(); } } }; t2.start();
		 */

	}

	private void runOMCIA() {
		GraphInstance graphInstance = new GraphInstance();
		GraphContainer con = ContainerSingelton.getInstance();
		MainWindow w = MainWindowSingelton.getInstance();
		w.setLockedPane(true);
		Map<String, String> env = System.getenv();

		if (SystemUtils.IS_OS_WINDOWS) {
			pathWorkingDirectory = env.get("APPDATA");
		} else {
			pathWorkingDirectory = env.get("HOME");
		}

		if (env.containsKey("OPENMODELICAHOME")
				&& new File(env.get("OPENMODELICAHOME")).isDirectory()) {
			pathCompiler = env.get("OPENMODELICAHOME");

			try {

				String stopTime = JOptionPane
						.showInputDialog("Stop Time", "20");
				String intervals = JOptionPane.showInputDialog("Intervals",
						"20");
				// this.path = "C:\\OpenModelica1.9.1Nightly\\";

				if (pathCompiler.charAt(pathCompiler.length() - 1) != File.separatorChar) {
					pathCompiler += File.separator;
				}
				if (pathWorkingDirectory
						.charAt(pathWorkingDirectory.length() - 1) != File.separatorChar) {
					pathWorkingDirectory += File.separator;
				}

				pathWorkingDirectory += "vanesa" + File.separator;

				File dir = new File(pathWorkingDirectory);

				if (!dir.isDirectory()) {
					dir.mkdir();
				}

				pathSim = pathWorkingDirectory + "simulation" + File.separator;
				File dirSim = new File(pathSim);
				if (dirSim.isDirectory()) {
					FileUtils.cleanDirectory(dirSim);
				} else {
					new File(pathSim).mkdir();
				}

				boolean abort = false;
				String missing = "";
				if (!new File(pathWorkingDirectory + "PNlib.mo").exists()) {
					abort = true;
					missing += "PNlib.mo\n in " + pathWorkingDirectory;
				}
				/*
				 * if (!new File(path + "dsmodel.c").exists()) { abort = true;
				 * missing += "dsmodel.c\n"; } if (!new File(path +
				 * "myrandom.c").exists()) { abort = true; missing +=
				 * "myrandom.c\n"; }
				 */
				if (abort) {
					JOptionPane
							.showMessageDialog(
									w,
									"Following files which are required for simulation are missing in the dymola folder:\n"
											+ missing, "Simulation aborted...",
									JOptionPane.ERROR_MESSAGE);
					w.setLockedPane(false);
					return;
				}

				MOoutput mo = new MOoutput(new File(pathSim + "simulation.mo"),
						graphInstance.getPathway());
				HashMap<BiologicalEdgeAbstract, String> bea2key = mo
						.getBea2resultkey();
				//
				FileWriter fstream = new FileWriter(pathSim + "simulation.mos");
				BufferedWriter out = new BufferedWriter(fstream);
				pathSim = pathSim.replace('\\', '/');
				out.write("cd(\"" + pathSim + "\");\r\n");
				out.write("getErrorString();\r\n");
				out.write("loadFile(\"../PNlib.mo\");\r\n");
				out.write("getErrorString();\r\n");
				out.write("loadFile(\"simulation.mo\");\r\n");
				out.write("getErrorString();\r\n");
				out.write("buildModel(simulation, stopTime=" + stopTime
						+ ", method=\"euler\", numberOfIntervals=" + intervals
						// only places
						// +
						// ", outputFormat=\"csv\", variableFilter=\"^[a-zA-Z_0-9]*.t\");\r\n");
						+ ", outputFormat=\"csv\");\r\n");
				out.write("getErrorString();\r\n");
				// variableFilter=\"^[a-zA-Z_0-9]*.t\" only places

				// out.write("fileName=\"simulate.mat\";\r\n");
				// out.write("CSVfile=\"simulate.csv\";\r\n");
				// out.write("n=readTrajectorySize(fileName);\r\n");
				// out.write("names = readTrajectoryNames(fileName);\r\n");
				// out.write("traj=readTrajectory(fileName,names,n);\r\n");
				// out.write("traj_transposed=transpose(traj);\r\n");
				// out.write("DataFiles.writeCSVmatrix(CSVfile, names, traj_transposed);\r\n");
				// out.write("exit();\r\n");
				out.close();
				stopped = false;
				long zstVorher;
				long zstNachher;

				// simT.run();

				zstVorher = System.currentTimeMillis();

				String bin = null;

				if (SystemUtils.IS_OS_WINDOWS) {
					bin = pathCompiler + "bin" + File.separator + "omc.exe";
				} else {
					bin = pathCompiler + "bin" + File.separator + "omc";
				}
				final Process p = new ProcessBuilder(bin, pathSim
						+ "simulation.mos").start();

				// simulation.exe -override=outputFormat=ia -port=11111
				// -lv=LOG_STATS

				Server s = new Server(bea2key);

				s.test();

				// System.out.println(s);

				Thread t = new Thread() {
					public void run() {
						long totalTime = 120000;
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
				try {
					t.stop();
				} catch (Exception e) {
				}
				System.out.println("building ended");
				graphInstance.getPathway().setPetriNetSimulation(true);
				w.initPCPGraphs();

				Thread t1 = new Thread() {
					public void run() {

						try {
							ProcessBuilder pb = new ProcessBuilder();
							boolean noEmmit = true;

							if (noEmmit) {
								pb.command(pathSim + "simulation.exe",
										"-override=outputFormat=ia",
										"-port=11111", "-noEventEmit",
										"-lv=LOG_STATS");
							} else {
								pb.command(pathSim + "simulation.exe",
										"-override=outputFormat=ia",
										"-port=11111", "-lv=LOG_STATS");
							}
							pb.redirectOutput();
							pb.directory(new File(pathSim));
							Process process = pb.start();
							setReader(new InputStreamReader(
									process.getInputStream()));

						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				};

				Thread t2 = new Thread() {
					public void run() {
						w.redrawGraphs();
						Vector<Double> v = graphInstance.getPathway()
								.getPetriNet().getTime();
						// System.out.println("running");
						while (s.isRunning()) {
							// System.out.println("im thread");
							w.redrawGraphs();
							// GraphInstance graphInstance = new
							// GraphInstance();
							// GraphContainer con =
							// ContainerSingelton.getInstance();
							// MainWindow w = MainWindowSingelton.getInstance();

							// double time =
							if (v.size() > 0) {
								menue.setTime(v.get(v.size() - 1));
							}
							try {
								sleep(100);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						menue.stopped();
						System.out.println("end of simulation");
						w.redrawGraphs();
						if (v.size() > 0) {
							menue.setTime(v.get(v.size() - 1));
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
									menue.addText(line + "\r\n");
									System.out.println(line);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							try {
								sleep(100);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						try {
							line = outputReader.readLine();
							while (line.length() > 0) {
								menue.addText(line + "\r\n");
								System.out.println(line);
								line = outputReader.readLine();

							}
							outputReader.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						// w.updatePCPView();
					}
				};

				t2.start();
				t3.start();

				t1.start();

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
				 * Runtime.getRuntime().exec( "cmd /q/c start " + pathSim +
				 * "simulation.bat");
				 */
				System.out.println("drin");

				zstNachher = System.currentTimeMillis();
				System.out.println("Zeit benoetigt: "
						+ ((zstNachher - zstVorher) / 1000) + " sec");
				System.out.println("Zeit benoetigt: "
						+ ((zstNachher - zstVorher)) + " millisec");

				/*
				 * Thread t3 = new Thread() { public void run() { long totalTime
				 * = 60000; try { sleep(2000); for (long t = 0; t < totalTime; t
				 * += 200) {
				 * 
				 * sleep(200); Iterator<BiologicalNodeAbstract> it =
				 * graphInstance.getPathway().getAllNodes().iterator();
				 * BiologicalNodeAbstract bna; Place p;
				 * graphInstance.getPathway().getPetriNet().addTime(new
				 * Double(graphInstance
				 * .getPathway().getPetriNet().getTime().size()+1));
				 * while(it.hasNext()){ bna = it.next(); if(bna instanceof
				 * Place){ p = (Place) bna;
				 * p.getPetriNetSimulationData().add(Math.random());
				 * //System.out.println(Math.random()); } }
				 * 
				 * } System.out.println("endeeeeee"); stopped = true; } catch
				 * (Exception e) { e.printStackTrace(); } } }; t3.start();
				 */

				if (con.containsPathway()
						&& graphInstance.getPathway().hasGotAtLeastOneElement()
						&& !stopped) {
					// graphInstance.getPathway().setPetriNet(true);
					// PetriNet petrinet = graphInstance.getPathway()
					// .getPetriNet();
					// petrinet.setPetriNetSimulationFile(pathSim
					// + "simulation_res.csv", true);
					// petrinet.initializePetriNet(bea2key);
				} else
					throw new Exception();
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane
						.showMessageDialog(
								MainWindowSingelton.getInstance(),
								"Something went wrong. The model couldn't be simulated!",
								"Error occured...", JOptionPane.ERROR_MESSAGE);
				w.setLockedPane(false);
				return;
			}
			w.setLockedPane(false);
			JOptionPane
					.showMessageDialog(
							w,
							"Simulation is completed. Select one or more places and click on Petri Net Simulation in the left toolbar to visualize and animate the results!",
							"Simulation done...",
							JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(w,
					"Environment variable OPENMODELICAHOME not found.",
					"OPENMODELICA not found...", JOptionPane.QUESTION_MESSAGE);
			w.setLockedPane(false);
		}
	}

	private void runDymola() {
		GraphInstance graphInstance = new GraphInstance();
		GraphContainer con = ContainerSingelton.getInstance();
		MainWindow w = MainWindowSingelton.getInstance();
		w.setLockedPane(true);
		try {
			JOptionPane
					.showMessageDialog(
							w,
							"To simulate your Petri Net you have to meet some requirements:"
									+ '\n'
									+ "You need a valid Dymola installation (license inclusive). Please specify the path to the Dymola installation folder in the next dialog. Make sure that there also is a PNlib_ver1_4.mo, a myrandom.c and a dsmodel.c file!"
									+ '\n' + '\n'
									+ "Simulation can take some time...",
							"Dymola installation folder required...",
							JOptionPane.QUESTION_MESSAGE);
			String stopTime = JOptionPane.showInputDialog("Stop Time", "500");
			String intervals = JOptionPane.showInputDialog("Intervals", "500");
			JFileChooser chooser = new JFileChooser(pathCompiler);
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.setAcceptAllFileFilterUsed(false);
			if (chooser.showOpenDialog(w) == JFileChooser.APPROVE_OPTION) {
				pathCompiler = chooser.getSelectedFile().getAbsolutePath();
			} else {
				w.setLockedPane(false);
				return;
			}
			if (pathCompiler.charAt(pathCompiler.length() - 1) != '\\')
				pathCompiler += "\\";
			boolean abort = false;
			String missing = "";
			if (!new File(pathCompiler + "PNlib_ver1_4.mo").exists()) {
				abort = true;
				missing += "PNlib_ver1_4.mo\n";
			}
			if (!new File(pathCompiler + "dsmodel.c").exists()) {
				abort = true;
				missing += "dsmodel.c\n";
			}
			if (!new File(pathCompiler + "myrandom.c").exists()) {
				abort = true;
				missing += "myrandom.c\n";
			}
			if (abort) {
				JOptionPane
						.showMessageDialog(
								w,
								"Following files which are required for simulation are missing in the dymola folder:\n"
										+ missing, "Simulation aborted...",
								JOptionPane.ERROR_MESSAGE);
				w.setLockedPane(false);
				return;
			}

			new File(pathCompiler + "simulate.mo").delete();
			new File(pathCompiler + "simulate.mat").delete();
			new File(pathCompiler + "simulate.csv").delete();
			new File(pathCompiler + "simulation.mos").delete();
			new MOoutput(new File(pathCompiler + "simulation.mo"),
					graphInstance.getPathway());

			FileWriter fstream = new FileWriter(pathCompiler + "simulation.mos");
			BufferedWriter out = new BufferedWriter(fstream);
			String path2 = pathCompiler.replace('\\', '/');
			out.write("cd(\"" + path2.substring(0, path2.length() - 1)
					+ "\");\r\n");
			out.write("import PNlib_ver1_4.mo;\r\n");
			out.write("import simulation.mo;\r\n");
			out.write("simulateModel(\"simulation\", stopTime=" + stopTime
					+ ", numberOfIntervals=" + intervals
					+ ", method=\"dassl\", resultFile=\"simulate\");\r\n");
			out.write("fileName=\"simulate.mat\";\r\n");
			out.write("CSVfile=\"simulate.csv\";\r\n");
			out.write("n=readTrajectorySize(fileName);\r\n");
			out.write("names = readTrajectoryNames(fileName);\r\n");
			out.write("traj=readTrajectory(fileName,names,n);\r\n");
			out.write("traj_transposed=transpose(traj);\r\n");
			out.write("DataFiles.writeCSVmatrix(CSVfile, names, traj_transposed);\r\n");
			out.write("exit();\r\n");
			out.close();
			stopped = false;

			long zstVorher;
			long zstNachher;

			zstVorher = System.currentTimeMillis();

			final Process p = new ProcessBuilder(pathCompiler
					+ "bin\\Dymola.exe", "/nowindow", pathCompiler
					+ "simulation.mos").start();
			Thread t = new Thread() {
				public void run() {
					long totalTime = 120000;
					try {
						for (long t = 0; t < totalTime; t += 1000) {
							sleep(1000);
						}
						p.destroy();
						stopped = true;
					} catch (Exception e) {
					}
				}
			};
			t.start();
			p.waitFor();
			try {
				t.stop();
			} catch (Exception e) {
			}
			zstNachher = System.currentTimeMillis();
			System.out.println("Zeit benoetigt: "
					+ ((zstNachher - zstVorher) / 1000) + " sec");
			System.out.println("Zeit benoetigt: " + ((zstNachher - zstVorher))
					+ " millisec");
			if (con.containsPathway()
					&& graphInstance.getPathway().hasGotAtLeastOneElement()
					&& !stopped) {
				graphInstance.getPathway().setPetriNet(true);
				PetriNet petrinet = graphInstance.getPathway().getPetriNet();
				petrinet.setPetriNetSimulationFile(pathCompiler
						+ "simulate.csv", false);
				petrinet.initializePetriNet(new HashMap<BiologicalEdgeAbstract, String>());
			} else
				throw new Exception();
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(MainWindowSingelton.getInstance(),
					"Something went wrong. The model couldn't be simulated!",
					"Error occured...", JOptionPane.ERROR_MESSAGE);
			w.setLockedPane(false);
			return;
		}
		w.setLockedPane(false);
		JOptionPane
				.showMessageDialog(
						w,
						"Simulation is completed. Select one or more places and click on Petri Net Simulation in the left toolbar to visualize and animate the results!",
						"Simulation done...", JOptionPane.INFORMATION_MESSAGE);

	}

	@Override
	public void actionPerformed(ActionEvent event) {
		// TODO Auto-generated method stub
		if (event.getActionCommand().equals("start")) {
			this.menue.started();
			boolean omc = true;

			if (omc) {
				// this.runOMC();
				this.runOMCIA();
			} else {
				this.runDymola();
			}
			// System.out.println("start");
		} else if (event.getActionCommand().equals("stop")) {

		}

	}

	private void setReader(InputStreamReader reader) {
		this.outputReader = new BufferedReader(reader);
	}

}
