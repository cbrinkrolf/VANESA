package io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.lang3.SystemUtils;

import biologicalElements.Pathway;
import configurations.ConnectionSettings;
import graph.GraphContainer;
import graph.GraphInstance;
import graph.jung.classes.MyGraph;
import gui.MainWindow;
import gui.MyPopUp;
import gui.ProgressBar;
import xmlInput.sbml.JSBMLinput;
import xmlInput.sbml.VAMLInput;

public class OpenDialog extends SwingWorker<Object, Object> {

	private String fileFormat;
	private File file;

	private final String sbmlDescription = "System Biology Markup Language (*.sbml)";
	private final String sbml = "sbml";

	private final String modellicaResultDescription = "Modellica Simulation Result File (*.plt)";
	private final String modellicaSimulation = "plt";

	private final String modellicaResultDescriptionNew = "New Modelica Simulation Result File (*.csv)";
	private final String modellicaSimulationNew = "csv";

	private final String vamlDescription = "VANESA Markup Language (*.vaml)";
	private final String vaml = "vaml";

	private final String graphMlDescription = "Graph Markup Language (*.gml)";
	private final String moDescription = "Modelica File (*.mo)";

	private final String phosphoMlDescription = "PhosphoGrid Text File (*.txt)";
	private final String poDescription = "txt";

	private String txtDescription = "Graph Text File (*.txt)";
	private String txt = "txt";

	private final int option;
	final ProgressBar bar = new ProgressBar();

	private final String mo = "mo";

	private JFileChooser chooser;

	private GraphContainer con = GraphContainer.getInstance();
	private GraphInstance graphInstance = new GraphInstance();

	private Pathway pathway = null;

	public OpenDialog(Pathway pw){
		this();
		pathway = pw;
	}

	public OpenDialog() {

		String pathWorkingDirectory = null;
		if (SystemUtils.IS_OS_WINDOWS) {
			pathWorkingDirectory = System.getenv("APPDATA");
		} else {
			pathWorkingDirectory = System.getenv("HOME");
		}
		pathWorkingDirectory += File.separator + "vanesa";

		if (ConnectionSettings.getFileDirectory() != null) {
			chooser = new JFileChooser(ConnectionSettings.getFileDirectory());
		} else {
			String path = "";
			try {
				XMLConfiguration xmlSettings = new XMLConfiguration(pathWorkingDirectory + File.separator + "settings.xml");
				path = xmlSettings.getString("OpenDialog-Path");
			}
			catch(ConfigurationException e)
			{
				System.out.println("There is probably no " + pathWorkingDirectory + File.separator + "settings.xml yet.");
			}
			chooser = new JFileChooser(path);
		}

		chooser.setAcceptAllFileFilterUsed(false);
		chooser.addChoosableFileFilter(new MyFileFilter(sbml, sbmlDescription));
		chooser.addChoosableFileFilter(new MyFileFilter(vaml, vamlDescription));
		// chooser.addChoosableFileFilter(new MyFileFilter(mo, moDescription));

//		chooser.addChoosableFileFilter(new MyFileFilter(modellicaSimulation,
//				modellicaResultDescription));
//		chooser.addChoosableFileFilter(new MyFileFilter(modellicaSimulationNew,
//				modellicaResultDescriptionNew));

		chooser.addChoosableFileFilter(new MyFileFilter(poDescription,
				phosphoMlDescription));
		chooser.addChoosableFileFilter(new MyFileFilter(txt, txtDescription));

		option = chooser.showOpenDialog(MainWindow.getInstance());

		if (option == JFileChooser.APPROVE_OPTION)
		{
			File fileDir = chooser.getCurrentDirectory();
			try {
				XMLConfiguration xmlSettings = null;
				File f = new File(pathWorkingDirectory + File.separator + "settings.xml");
				if(f.exists()){
					xmlSettings = new XMLConfiguration(pathWorkingDirectory + File.separator + "settings.xml");
				}else{
					xmlSettings = new XMLConfiguration();
					xmlSettings.setFileName(pathWorkingDirectory + File.separator + "settings.xml");
				}
				xmlSettings.setProperty("OpenDialog-Path", fileDir.getAbsolutePath());
				xmlSettings.save();
			}
			catch(ConfigurationException e)
			{
				e.printStackTrace();
			}
		}
	}

	private void open() {
		if (fileFormat != null) {
		//	System.out.println(fileFormat);
			ConnectionSettings.setFileDirectory(file.getAbsolutePath());

			if (fileFormat.equals(vamlDescription)) {
				try {
					try {
						new VAMLInput(file);
					} catch (XMLStreamException e) {
						MyPopUp.getInstance().show("VAML read error.", "An error occured during the loading. "
								+ "The VAML file is not valid.");
						e.printStackTrace();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}  else if (fileFormat.equals(sbmlDescription)) {

					JSBMLinput jsbmlInput;
					jsbmlInput = pathway==null ? new JSBMLinput() : new JSBMLinput(pathway);
					String result;
					try {
						result = jsbmlInput.loadSBMLFile(new FileInputStream(file), file.getName());
						if(result.length() > 0){
							MyPopUp.getInstance().show("Information", result);
						}
						
					} catch (FileNotFoundException e) {
						try {
							file = new File(file.getAbsolutePath()+".sbml");
							result = jsbmlInput.loadSBMLFile(new FileInputStream(file), file.getName());
							if(result.length() > 0){
								MyPopUp.getInstance().show("Information", result);
							}
							
						} catch (FileNotFoundException ex) {
							ex.printStackTrace();
						}
					}
			} else if (fileFormat.equals(txtDescription)) {
				try {
					new TxtInput(new FileInputStream(file), file.getName());
				} catch (IOException e) {
					e.printStackTrace();
				}

			} else if (fileFormat.equals(phosphoMlDescription)) {
				try {
					new PhosphoInput(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	protected Void doInBackground() throws Exception {

		if (option == JFileChooser.APPROVE_OPTION) {

			fileFormat = chooser.getFileFilter().getDescription();
			file = chooser.getSelectedFile();

			Runnable run = new Runnable() {
				@Override
				public void run() {
					bar.init(100, "  Open ", true);
					bar
							.setProgressBarString("Loading data from file. Please wait a second");
				}
			};
			SwingUtilities.invokeLater(run);

		}
		return null;
	}

	@Override
	public void done() {

		open();
		if (fileFormat != null) {
			bar.closeWindow();

			if (con.containsPathway()) {
				if (graphInstance.getPathway().hasGotAtLeastOneElement()) {

					//GraphInstance.getMyGraph().getVisualizationViewer()
					//		.restart();
					MainWindow.getInstance().updateAllGuiElements();
					MyGraph g = GraphInstance.getMyGraph();
					g.normalCentering();
				}
			}
		}
	}
}
