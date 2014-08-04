package petriNet;

import graph.ContainerSingelton;
import graph.GraphContainer;
import graph.GraphInstance;
import gui.MainWindow;
import gui.MainWindowSingelton;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;

import moOutput.MOoutput;
import biologicalElements.PetriNet;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class PetriNetSimulation {
	private static String pathCompiler = null;
	// final ProgressBar bar = new ProgressBar();
	private static String pathWorkingDirectory = null;
	private static String pathSim = null;
	private boolean stopped = false;

	public PetriNetSimulation() {

		boolean omc = true;

		if (omc) {
			this.runOMC();
			//this.runOMCIA();
		} else {
			this.runDymola();
		}
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
		
		/*Thread t2 = new Thread() {
			public void run() {
				long totalTime = 60000;
				try {
					sleep(2000);
					for (long t = 0; t < totalTime; t += 1000) {
						sleep(500);
						Iterator<BiologicalNodeAbstract> it = graphInstance.getPathway().getAllNodes().iterator();
						BiologicalNodeAbstract bna;
						Place p;
						while(it.hasNext()){
							bna = it.next();
							if(bna instanceof Place){
								p = (Place) bna;
								p.getPetriNetSimulationData().add(Math.random());
								System.out.println(Math.random());
							}
						}
						w.updatePCPView();
					}
					stopped = true;
				} catch (Exception e) {
				}
			}
		};
		t2.start();*/
		
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
				String path2 = pathSim.replace('\\', '/');
				out.write("cd(\"" + path2 + "\");\r\n");
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

				fstream = new FileWriter(pathSim + "simulation.bat");
				out = new BufferedWriter(fstream);
				path2 = pathSim.replace('\\', '/');
				out.write("c:\r\n");
				out.write("cd " + pathSim + "\r\n");
				out.write("simulation.exe -override=outputFormat=ia -port=11111 -lv=LOG_STATS\r\n");
				out.write("exit\r\n");
				// out.write("loadFile(\"../PNlib.mo\");\r\n");
				// out.write("getErrorString();\r\n");
				// out.write("loadFile(\"simulation.mo\");\r\n");
				// out.write("getErrorString();\r\n");
				// out.write("buildModel(simulation, stopTime=" + stopTime
				// + ", method=\"euler\", numberOfIntervals=" + intervals
				// only places
				// +
				// ", outputFormat=\"csv\", variableFilter=\"^[a-zA-Z_0-9]*.t\");\r\n");
				// + ", outputFormat=\"csv\");\r\n");
				// out.write("getErrorString();\r\n");
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

				Server s = new Server();

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
				System.out.println("building ended");
				System.out.println("ps:" + pathSim);
				// final Process pSim = new
				// ProcessBuilder("cmd.exe","/c",pathSim+"simulation.bat").start();
				// final Process pSim = new
				// ProcessBuilder(pathSim+"simulation.exe","-override=outputFormat=ia","-port=11111","-lv=LOG_STATS").start();

				Runtime.getRuntime().exec(
						"cmd /q/c start " + pathSim + "simulation.bat");
				System.out.println("drin");

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
}
