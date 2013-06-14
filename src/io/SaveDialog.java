package io;

import gonOutput.GONoutput;
import graph.GraphInstance;
import gui.MainWindowSingelton;
import io.graphML.SaveGraphML;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import moOutput.MOoutput;
import xmlOutput.sbml.SBMLoutput;
import xmlOutput.sbml.SBMLoutputNoWS;
import xmlOutput.sbml.VAMLoutput;
import configurations.ConnectionSettings;

public class SaveDialog {

	private boolean sbmlBool = true;
	private boolean graphMLBool = true;
	private boolean moBool = true;
	private boolean gonBool = true;
	private boolean vaBool = true;
	private boolean txtBool = true;
	private boolean itxtBool = true;

	private String fileFormat;
	private File file;

	private String sbmlDescription = "System Biology Markup Language (*.sbml)";
	private String sbml = "sbml";

	private String vamlDescription = "VANESA Markup Language (*.vaml)";
	private String vaml = "vaml";

	private String graphMlDescription = "Graph Markup Language (*.graphml)";
	private String graphMl = "graphml";

	private String moDescription = "Modelica Model (*.mo)";
	private String mo = "mo";

	private String gonDescription = "Cell Illustrator (*.gon)";
	private String gon = "gon";

	private String txtDescription = "Graph Text File (*.txt)";
	private String txt = "txt";

	private String irinaDescription = "Irina Export File (*.itxt)";
	private String irinaTxt = "itxt";

	private JFileChooser chooser;

	/*
	 * public SaveDialog(boolean sbml) { this(sbml?1:1); }//
	 */
	// use power of 2
	public static int FORMAT_SBML = 1;
	public static int FORMAT_GRAPHML = 2;
	public static int FORMAT_MO = 4;
	public static int FORMAT_GON = 8;
	public static int FORMAT_VA = 16;
	public static int FORMAT_TXT = 32;
	public static int FORMAT_ITXT = 64;

	public SaveDialog(int format) {

		sbmlBool = (format & FORMAT_SBML) == FORMAT_SBML;
		graphMLBool = (format & FORMAT_GRAPHML) == FORMAT_GRAPHML;
		moBool = (format & FORMAT_MO) == FORMAT_MO;
		gonBool = (format & FORMAT_GON) == FORMAT_GON;
		vaBool = (format & FORMAT_VA) == FORMAT_VA;
		txtBool = (format & FORMAT_TXT) == FORMAT_TXT;
		itxtBool = (format & FORMAT_ITXT) == FORMAT_ITXT;

		if (ConnectionSettings.getFileDirectory() != null) {
			chooser = new JFileChooser(ConnectionSettings.getFileDirectory());
		} else {
			chooser = new JFileChooser();
		}

		chooser.setAcceptAllFileFilterUsed(false);

		if (sbmlBool)
			chooser.addChoosableFileFilter(new MyFileFilter(sbml,
					sbmlDescription));

		/*if (itxtBool)
			chooser.addChoosableFileFilter(new MyFileFilter(irinaTxt,
					irinaDescription));*/

		if (graphMLBool)
			chooser.addChoosableFileFilter(new MyFileFilter(graphMl,
					graphMlDescription));

		if (moBool)
			chooser.addChoosableFileFilter(new MyFileFilter(mo, moDescription));

		if (gonBool)
			chooser
					.addChoosableFileFilter(new MyFileFilter(gon,
							gonDescription));

		if (vaBool)
			chooser.addChoosableFileFilter(new MyFileFilter(vaml,
					vamlDescription));
		if (txtBool)
			chooser
					.addChoosableFileFilter(new MyFileFilter(txt,
							txtDescription));

		int option = chooser.showSaveDialog(null);
		if (option == JFileChooser.APPROVE_OPTION) {

			fileFormat = chooser.getFileFilter().getDescription();
			file = chooser.getSelectedFile();
			boolean overwrite = true;
			if (file.exists()) {
				int response = JOptionPane.showConfirmDialog(null,
						"Overwrite existing file?", "Confirm Overwrite",
						JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE);
				if (response == JOptionPane.CANCEL_OPTION) {
					overwrite = false;
				}
			}
			if (overwrite) {
				write();
			}

		}
	}

	private void getCorrectFile(String ending) {

		String extension = file.getPath();
		int i = extension.lastIndexOf('.');
		if (i > 0 && i < extension.length() - 1) {
		} else {
			file = new File(file.getAbsolutePath() + "." + ending);
		}

	}

	private void write() {

		ConnectionSettings.setFileDirectory(file.getAbsolutePath());
		if (fileFormat.equals(sbmlDescription)) {
			getCorrectFile(sbml);
			// create a sbmlOutput object
			SBMLoutputNoWS sbmlOutput = new SBMLoutputNoWS(file, new GraphInstance()
					.getPathway());
			//if (sbmlOutput.generateSBMLDocument())
				JOptionPane.showMessageDialog(
						MainWindowSingelton.getInstance(), sbmlDescription
								+ sbmlOutput.generateSBMLDocument());
			//else
			//	JOptionPane.showMessageDialog(
			//			MainWindowSingelton.getInstance(), sbmlDescription
			//					+ " File not saved");
		} else if (fileFormat.equals(graphMlDescription)) {
			getCorrectFile(graphMl);
			new SaveGraphML(file);
			JOptionPane.showMessageDialog(MainWindowSingelton.getInstance(),
					graphMlDescription + " File saved");
		} else if (fileFormat.equals(irinaDescription)) {
			getCorrectFile(irinaTxt);
			new IrinaGraphTextWriter(file, new GraphInstance().getPathway());
			JOptionPane.showMessageDialog(MainWindowSingelton.getInstance(),
					irinaDescription + " File saved");

		} else if (fileFormat.equals(moDescription)) {
			getCorrectFile(mo);
			new MOoutput(file, new GraphInstance().getPathway());
			JOptionPane.showMessageDialog(MainWindowSingelton.getInstance(),
					moDescription + " File saved");

		} else if (fileFormat.equals(txtDescription)) {
			getCorrectFile(txt);
			new GraphTextWriter(file, new GraphInstance().getPathway());
			JOptionPane.showMessageDialog(MainWindowSingelton.getInstance(),
					txtDescription + " File saved");

		} else if (fileFormat.equals(gonDescription)) {
			getCorrectFile(gon);
			new GONoutput(file, new GraphInstance().getPathway());
			JOptionPane.showMessageDialog(MainWindowSingelton.getInstance(),
					gonDescription + " File saved");

		} else if (fileFormat.equals(vamlDescription)) {
			getCorrectFile(vaml);
			try {
				new VAMLoutput(file, new GraphInstance().getPathway());
				JOptionPane.showMessageDialog(
						MainWindowSingelton.getInstance(), vamlDescription
								+ " File saved");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
