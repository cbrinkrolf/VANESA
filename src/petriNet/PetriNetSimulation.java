package petriNet;

import graph.ContainerSingelton;
import graph.GraphContainer;
import graph.GraphInstance;
import gui.MainWindow;
import gui.MainWindowSingelton;
import gui.ProgressBar;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import moOutput.MOoutput;
import biologicalElements.PetriNet;

public class PetriNetSimulation {
	private static String path = null;
	final ProgressBar bar = new ProgressBar();
	boolean stopped = false;

	public PetriNetSimulation() {
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
			String stopTime = JOptionPane.showInputDialog("Stop Time","500");
			String intervals = JOptionPane.showInputDialog("Intervals","500");
			JFileChooser chooser = new JFileChooser(path);
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.setAcceptAllFileFilterUsed(false);
			if (chooser.showOpenDialog(w) == JFileChooser.APPROVE_OPTION) {
				path = chooser.getSelectedFile().getAbsolutePath();
			} else {
				w.setLockedPane(false);
				return;
			}
			if (path.charAt(path.length() - 1) != '\\')
				path += "\\";
			boolean abort = false;
			String missing = "";
			if (!new File(path + "PNlib_ver1_4.mo").exists()) {
				abort = true;
				missing += "PNlib_ver1_4.mo\n";
			}
			if (!new File(path + "dsmodel.c").exists()) {
				abort = true;
				missing += "dsmodel.c\n";
			}
			if (!new File(path + "myrandom.c").exists()) {
				abort = true;
				missing += "myrandom.c\n";
			}
			if (abort) {
				JOptionPane.showMessageDialog(w, "Following files which are required for simulation are missing in the dymola folder:\n"+missing,"Simulation aborted...",JOptionPane.ERROR_MESSAGE);
				w.setLockedPane(false);
				return;
			}

			new File(path + "simulate.mo").delete();
			new File(path + "simulate.mat").delete();
			new File(path + "simulate.csv").delete();
			new File(path + "simulation.mos").delete();
			new MOoutput(new File(path + "simulation.mo"),
					graphInstance.getPathway());

			FileWriter fstream = new FileWriter(path + "simulation.mos");
			BufferedWriter out = new BufferedWriter(fstream);
			String path2 = path.replace('\\', '/');
			out.write("cd(\"" + path2.substring(0, path2.length() - 1)
					+ "\");\r\n");
			out.write("import PNlib_ver1_4.mo;\r\n");
			out.write("import simulation.mo;\r\n");
			out.write("simulateModel(\"simulation\", stopTime="+stopTime+", numberOfIntervals="+intervals+", method=\"dassl\", resultFile=\"simulate\");\r\n");
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

			final Process p = new ProcessBuilder(path + "bin\\Dymola.exe",
					"/nowindow", path + "simulation.mos").start();
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
			System.out.println("Zeit benötigt: " + ((zstNachher - zstVorher)/1000) + " sec");
			System.out.println("Zeit benötigt: " + ((zstNachher - zstVorher)) + " millisec");
			if (con.containsPathway()
					&& graphInstance.getPathway().hasGotAtLeastOneElement()
					&& !stopped) {
				graphInstance.getPathway().setPetriNet(true);
				PetriNet petrinet = graphInstance.getPathway().getPetriNet();
				petrinet.setPetriNetSimulationFile(path + "simulate.csv");
				petrinet.initializePetriNet();
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
