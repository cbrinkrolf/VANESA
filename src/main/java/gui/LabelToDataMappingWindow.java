package gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import biologicalObjects.nodes.BiologicalNodeAbstract;
import graph.GraphContainer;
import graph.GraphInstance;
import graph.algorithms.NodeAttributeType;

/**
 * Window for mapping custom datasets to a loaded vanesa network. The data will
 * bemapped using the 'label' variable of network nodes (BiologicalNodeAbstract
 * s). For a successful mapping the data has to be tab separated in the pattern
 * of label(tab)value so each line contains a label and a value. Labels
 * specified in the file must be unique.
 *
 * @author mlewinsk
 *
 */
public class LabelToDataMappingWindow {
	private JFileChooser filechooser;
	private HashMap<String, String> datamapping;
	private int linenumber;

	/**
	 * Regular constructor opens a JFileChooser to specify the input file for
	 * the mapping. Check if a network is loaded has to be done prior.
	 *
	 * @throws IOException
	 *             if the filestream fails
	 * @throws InputFormatException
	 *             if the input format is invalid
	 */
	public LabelToDataMappingWindow() throws IOException, InputFormatException {
		// Check for open pathways, if not shoe message
		if (GraphContainer.getInstance().getPathwayNumbers() == 0) {
			PopUpDialog.getInstance().show("Error", "Please create a network before.");
			return;
		}
		filechooser = new JFileChooser();
		filechooser.setDialogTitle("please choose your mapping file (label->data)");
		int status = filechooser.showOpenDialog(MainWindow.getInstance().getFrame());
		switch (status) {
		case JFileChooser.APPROVE_OPTION:
			processFile();
		case JFileChooser.CANCEL_OPTION:
			break;
		case JFileChooser.ERROR_OPTION:
			PopUpDialog.getInstance().show("Error", "Error while loading file.");
			break;
		default:
			System.out.println("Unknown switch case in class:" + this.getClass());
			break;
		}
	}

	/**
	 * @return true if successful, false if the file entered was invalid
	 */
	private boolean processFile() throws IOException, InputFormatException {
		File datafile = filechooser.getSelectedFile();
		System.out.println(datafile.toString());

		datamapping = new HashMap<>();
		FileInputStream fstream;
		int successfulmappings;
		try {
			fstream = new FileInputStream(datafile);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			String strLine;
			linenumber = 1;
			successfulmappings = 0;
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				processLine(strLine);
				linenumber++;
			}

			// Close the input stream
			br.close();
		} catch (FileNotFoundException fnfe) {
			PopUpDialog.getInstance().show("Error", "Error while loading file, file not found.");
			return false;
		}

		String attributename = JOptionPane.showInputDialog(MainWindow.getInstance().getFrame(),
				"file accepted.\n please enter a name for the dataset:\n", datafile.getName());
		if (attributename.length() > 0) {
			String bnalabel;
			for (BiologicalNodeAbstract bna : GraphInstance.getVanesaGraph().getNodes()) {
				bnalabel = bna.getLabel();
				if (datamapping.containsKey(bnalabel)) {
					bna.addAttribute(NodeAttributeType.ANNOTATION, attributename, datamapping.get(bnalabel));
					successfulmappings++;
				}
			}
		}
		PopUpDialog.getInstance().show("Information",
				successfulmappings + " of " + datamapping.size() + " entries \nhave been mapped and updated.\n\n("
						+ MainWindow.getInstance().getCurrentPathway() + ")");
		return true;
	}

	/**
	 * Processing of the Strings of the file reader. Data input format will be
	 * checked and transferred to the 'datamapping' variable.
	 *
	 * @param line
	 *            String from filereader
	 * @throws InputFormatException
	 *             custom exception to deal with wrong input formats. i.e.
	 *             key->value is not splitted correctly or duplicate labels
	 *             exist.
	 */
	private void processLine(String line) throws InputFormatException {
		String[] linecuts = line.split("\t");
		if (linecuts.length != 2) {
			throw new InputFormatException("invalid split: (line " + linenumber + ")\n" + line);
		}
		String key = linecuts[0];
		String value = linecuts[1];
		// allow only unique labels
		if (datamapping.containsKey(key)) {
			throw new InputFormatException("duplicate label (" + key + ",line " + linenumber + ")\n" + line);
		}
		datamapping.put(key, value);
	}

	/**
	 * Custom exception to cancel the current data mapping and provide the user
	 * with information about the error.
	 */
	public static class InputFormatException extends Exception {
		private static final long serialVersionUID = -441053373711813908L;

		public InputFormatException(String message) {
			super(message);
		}
	}

}
