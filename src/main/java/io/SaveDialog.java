package io;

import biologicalElements.Pathway;
import configurations.ConnectionSettings;
import graph.GraphContainer;
import graph.GraphInstance;
import gui.AsyncTaskExecutor;
import gui.MainWindow;
import gui.PopUpDialog;
import io.graphML.GraphMLWriter;
import io.image.ChartImageWriter;
import io.image.ComponentImageWriter;
import io.sbml.JSBMLOutput;
import org.apache.commons.io.IOUtils;
import org.jfree.chart.JFreeChart;
import transformation.RuleManager;
import transformation.YamlRuleWriter;

import javax.swing.*;
import javax.xml.stream.XMLStreamException;
import java.awt.*;
import java.io.*;
import java.util.List;

public class SaveDialog {
	public static final int DATA_TYPE_TRANSFORMATION_RULES = 1;
	public static final int DATA_TYPE_VISUALIZATION_SETTINGS = 2;
	public static final int DATA_TYPE_GRAPH_PICTURE = 3;
	public static final int DATA_TYPE_NETWORK_EXPORT = 4;
	public static final int DATA_TYPE_SIMULATION_RESULTS = 5;

	private SuffixAwareFilter fileFilter;
	private int dataType;

	public SaveDialog(SuffixAwareFilter[] formats, int dataType) {
		this(formats, dataType, null, MainWindow.getInstance().getFrame(), null);
	}

	public SaveDialog(SuffixAwareFilter[] formats, int dataType, Component c) {
		this(formats, dataType, c, MainWindow.getInstance().getFrame(), null);
	}

	public SaveDialog(SuffixAwareFilter[] formats, int dataType, Component c, Component relativeTo, String simId) {
		if (relativeTo == null) {
			relativeTo = MainWindow.getInstance().getFrame();
		}
		JFileChooser chooser = prepare(formats);
		this.dataType = dataType;
		int option = chooser.showSaveDialog(relativeTo);
		if (option == JFileChooser.APPROVE_OPTION) {
			// Save path to settings.xml
			ConnectionSettings.getInstance().setFileSaveDirectory(chooser.getCurrentDirectory().getAbsolutePath());
			fileFilter = (SuffixAwareFilter) chooser.getFileFilter();
			File file = chooser.getSelectedFile();
			if (file.exists()) {
				int response = JOptionPane.showConfirmDialog(relativeTo, "Overwrite existing file?",
						"Confirm Overwrite", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (response != JOptionPane.OK_OPTION) {
					return;
				}
			}
			AsyncTaskExecutor.runUIBlocking("Saving data to file. Please wait a second", () -> {
				write(c, simId, file);
			});
		}
	}

	public SaveDialog(SuffixAwareFilter[] formats, final List<JFreeChart> charts, Component relativeTo) {
		if (relativeTo == null) {
			relativeTo = MainWindow.getInstance().getFrame();
		}
		JFileChooser chooser = prepare(formats);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int option = chooser.showSaveDialog(relativeTo);
		if (option != JFileChooser.APPROVE_OPTION) {
			return;
		}
		// Save path to settings.xml
		ConnectionSettings.getInstance().setFileSaveDirectory(chooser.getCurrentDirectory().getAbsolutePath());
		fileFilter = (SuffixAwareFilter) chooser.getFileFilter();
		String pathSim = chooser.getSelectedFile().getAbsolutePath() + File.separator;
		int width = 320;
		int height = 200;
		boolean anyFileAlreadyExists = false;
		final File[] chartFiles = new File[charts.size()];
		for (int i = 0; i < charts.size(); i++) {
			String name = charts.get(i).getTitle().getText();
			chartFiles[i] = new File(pathSim + name + "." + fileFilter.getExtension());
			if (chartFiles[i].exists()) {
				anyFileAlreadyExists = true;
			}
		}
		if (anyFileAlreadyExists) {
			int response = JOptionPane.showConfirmDialog(relativeTo, "Overwrite all existing files?",
					"Confirm Overwrite", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (response != JOptionPane.OK_OPTION) {
				return;
			}
		}
		AsyncTaskExecutor.runUIBlocking("Saving data to file. Please wait a second", () -> {
			for (int i = 0; i < charts.size(); i++) {
				JFreeChart chart = charts.get(i);
				File file = chartFiles[i];
				if (fileFilter == SuffixAwareFilter.PDF) {
					write(new ChartImageWriter(file, ChartImageWriter.IMAGE_TYPE_PDF, width, height), chart, false);
				} else if (fileFilter == SuffixAwareFilter.PNG) {
					write(new ChartImageWriter(file, ChartImageWriter.IMAGE_TYPE_PNG, width, height), chart, false);
				} else if (fileFilter == SuffixAwareFilter.SVG) {
					write(new ChartImageWriter(file, ChartImageWriter.IMAGE_TYPE_SVG, width, height), chart, false);
				}
			}
			PopUpDialog.getInstance().show("Information", fileFilter + "\nFile saved");
		});
	}

	private JFileChooser prepare(SuffixAwareFilter[] formats) {
		JFileChooser chooser = new JFileChooser(ConnectionSettings.getInstance().getFileSaveDirectory());
		chooser.setAcceptAllFileFilterUsed(false);
		for (final SuffixAwareFilter format : formats) {
			chooser.addChoosableFileFilter(format);
		}
		return chooser;
	}

	private static File ensureExtension(File file, SuffixAwareFilter fileFilter) {
		String filePath = file.getPath();
		if (!filePath.endsWith("." + fileFilter.getExtension())) {
			file = new File(file.getAbsolutePath() + "." + fileFilter.getExtension());
		}
		return file;
	}

	private void write(Component c, String simId, File file) {
		file = ensureExtension(file, fileFilter);
		if (fileFilter == SuffixAwareFilter.SBML) {
			writeSBML(file);
		} else if (fileFilter == SuffixAwareFilter.GRAPH_ML) {
			write(new GraphMLWriter(file), GraphInstance.getPathway());
		} else if (fileFilter == SuffixAwareFilter.MO) {
			writeMO(file);
		} else if (fileFilter == SuffixAwareFilter.GRAPH_TEXT_FILE) {
			write(new GraphTextWriter(file), GraphInstance.getPathway());
		} else if (fileFilter == SuffixAwareFilter.CSV_RESULT) {
			writeCSV(simId, file);
		} else if (fileFilter == SuffixAwareFilter.PNML) {
			write(new PNMLOutput(file), GraphInstance.getPathway());
		} else if (fileFilter == SuffixAwareFilter.CSML) {
			write(new CSMLOutput(file), GraphInstance.getPathway());
		} else if (fileFilter == SuffixAwareFilter.PNG) {
			if (c != null) {
				write(new ComponentImageWriter(file, ComponentImageWriter.IMAGE_TYPE_PNG), c);
			}
		} else if (fileFilter == SuffixAwareFilter.SVG) {
			if (c != null) {
				write(new ComponentImageWriter(file, ComponentImageWriter.IMAGE_TYPE_SVG), c);
			}
		} else if (fileFilter == SuffixAwareFilter.YAML) {
			writeYAML(file);
		}
	}

	private <T> boolean write(BaseWriter<T> writer, T value) {
		return write(writer, value, true);
	}

	private <T> boolean write(BaseWriter<T> writer, T value, boolean printSuccess) {
		writer.write(value);
		if (writer.hasErrors()) {
			PopUpDialog.getInstance().show("Error", fileFilter + "\nAn error occurred: " + writer.getErrors());
			return false;
		} else if (printSuccess) {
			PopUpDialog.getInstance().show("Information", fileFilter + "\nFile saved");
		}
		return true;
	}

	private void writeCSV(String simId, File file) {
		Pathway pw = GraphInstance.getPathway();
		// if BN holds PN
		if (!pw.isPetriNet()) {
			if (pw.getTransformationInformation() == null || pw.getTransformationInformation().getPetriNet() == null) {
				return;
			}
			pw = pw.getTransformationInformation().getPetriNet();
		}
		write(new CSVWriter(file, simId), pw);
	}

	private void writeMO(File file) {
		if (!write(new MOoutput(file, false), GraphInstance.getPathway(), false)) {
			return;
		}
		String pathColored = file.getAbsolutePath();
		if (pathColored.endsWith(".mo")) {
			pathColored = pathColored.substring(0, pathColored.length() - 3);
		}
		if (write(new MOoutput(new File(pathColored + "_colored.mo"), true), GraphInstance.getPathway(), false)) {
			PopUpDialog.getInstance().show("Modelica export", fileFilter + "\nFile saved");
		}
	}

	private void writeSBML(File file) {
		// create a sbmlOutput object
		// SBMLoutputNoWS sbmlOutput = new SBMLoutputNoWS(file, new GraphInstance().getPathway());
		// if (sbmlOutput.generateSBMLDocument())
		// JOptionPane.showMessageDialog(MainWindowSingelton.getInstance(), sbmlDescription + sbmlOutput.generateSBMLDocument());
		if (GraphInstance.getPathway().getFile() == null) {
			GraphInstance.getPathway().setFile(file);
		}
		// TODO creation of FileOutputStream overrides file already, even without call
		// write() method. Document should be generated first, if no errors thrown, then
		// create FOS and write to file.
		String out = "";
		try {
			JSBMLOutput jsbmlOutput = new JSBMLOutput(new FileOutputStream(file), GraphInstance.getPathway());
			out = jsbmlOutput.generateSBMLDocument();
		} catch (XMLStreamException | FileNotFoundException ex) {
			PopUpDialog.getInstance().show("Error", fileFilter + "\nAn error occurred: " + ex.getMessage());
		}
		if (out.length() > 0) {
			PopUpDialog.getInstance().show("Error", fileFilter + "\nAn error occurred: " + out);
		} else {
			GraphContainer.getInstance().renamePathway(GraphInstance.getPathway(), file.getName());
			GraphInstance.getPathway().setName(file.getName());
			GraphInstance.getPathway().setTitle(file.getName());
			MainWindow.getInstance().renameSelectedTab(file.getName());
			PopUpDialog.getInstance().show("Information", fileFilter + "\nFile saved");
		}
	}

	private void writeYAML(File file) {
		if (dataType == DATA_TYPE_VISUALIZATION_SETTINGS) {
			String exportPath = file.getPath();
			if (!exportPath.contains(".yaml")) {
				exportPath = exportPath + ".yaml";
			}
			InputStream internYaml = getClass().getClassLoader().getResourceAsStream("NodeProperties.yaml");
			File exportFile = new File(exportPath);
			try(FileOutputStream exportYaml = new FileOutputStream(exportFile)) {
				IOUtils.copy(internYaml, exportYaml);
				internYaml.close();
			} catch (IOException ex) {
				PopUpDialog.getInstance().show("Error", fileFilter + "\nAn error occurred: " + ex.getMessage());
			}
			PopUpDialog.getInstance().show("Information", fileFilter + "\nFile saved");
			MainWindow.getInstance().setLoadedYaml(exportPath);
		} else if (dataType == DATA_TYPE_TRANSFORMATION_RULES) {
			write(new YamlRuleWriter(file), RuleManager.getInstance().getRules());
		}
	}
}
