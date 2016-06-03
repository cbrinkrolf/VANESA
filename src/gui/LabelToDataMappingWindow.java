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
import graph.ContainerSingelton;
import graph.GraphInstance;
import graph.algorithms.NodeAttributeTypes;

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
	private File datafile;
	private HashMap<String, String> datamapping;
	private final String delimiter = "\t";
	private String linecuts[], key, value;
	private int linenumber, successfulmappings;

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
		if (ContainerSingelton.getInstance().getPathwayNumbers() == 0) {
			JOptionPane.showMessageDialog(MainWindowSingleton.getInstance(),
					"Please load or create a network first.",
					"no network found", JOptionPane.WARNING_MESSAGE);
			return;
		}

		filechooser = new JFileChooser();
		filechooser
				.setDialogTitle("please choose your mapping file (label->data)");

		int status = filechooser.showOpenDialog(MainWindowSingleton
				.getInstance());

		switch (status) {
		case JFileChooser.APPROVE_OPTION:
			processFile();
		case JFileChooser.CANCEL_OPTION:
			break;
		case JFileChooser.ERROR_OPTION:
			JOptionPane.showMessageDialog(MainWindowSingleton.getInstance(),
					"Error while loading file.");
			break;

		default:
			System.out.println("Unknown switch case in class:"
					+ this.getClass());
			break;
		}

	}

	/**
	 * 
	 * @return true if successful, false if the file entered was invalid
	 * @throws IOException
	 * @throws InputFormatException
	 */
	private boolean processFile() throws IOException, InputFormatException {

		datafile = filechooser.getSelectedFile();
		System.out.println(datafile.toString());

		datamapping = new HashMap<>();
		FileInputStream fstream;
		try {
			fstream = new FileInputStream(datafile);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					fstream));
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
			JOptionPane.showMessageDialog(MainWindowSingleton.getInstance(),
					"Error while loading file.");
			return false;
		}

		String attributename = JOptionPane.showInputDialog(
				MainWindowSingleton.getInstance(),
				"file accepted.\n please enter a name for the dataset:\n",
				datafile.getName() + "");

		if (attributename.length() > 0) {

			String bnalabel;
			for (BiologicalNodeAbstract bna : GraphInstance.getMyGraph()
					.getAllVertices()) {
				bnalabel = bna.getLabel();
				if (datamapping.containsKey(bnalabel)) {
					bna.addAttribute(NodeAttributeTypes.ANNOTATION,
							attributename, datamapping.get(bnalabel));
					successfulmappings++;
				}
			}
		}

		// MARTIN Stream not working, exception cant be thrown from stream?
		// try (Stream<String> lines = Files.lines(datafile.toPath(),
		// Charset.defaultCharset())) {
		// lines.forEach(line -> processLine(line));
		// }

		JOptionPane.showMessageDialog(MainWindowSingleton.getInstance(),
				successfulmappings + " of " + datamapping.size()
						+ " entries \nhave been mapped and updated.\n\n("
						+ MainWindowSingleton.getInstance().getCurrentPathway()
						+ ")");

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
	 * 
	 */
	private void processLine(String line) throws InputFormatException {
		linecuts = line.split(delimiter);
		if (linecuts.length != 2) {
			throw new InputFormatException("invalid split: (line " + linenumber
					+ ")\n" + line);
		}
		key = linecuts[0];
		value = linecuts[1];

		// allow only unique labels
		if (datamapping.containsKey(key)) {
			throw new InputFormatException("duplicate label (" + key + ",line "
					+ linenumber + ")\n" + line);
		} else {
			datamapping.put(key, value);
		}
	}

	/**
	 * Custom exeption to cancel the current data mapping and provide the user
	 * with information about the error.
	 * 
	 * @author mlewinsk
	 *
	 */
	public class InputFormatException extends Exception {

		/**
		 * generated uid
		 */
		private static final long serialVersionUID = -441053373711813908L;

		public InputFormatException() {
			super();
		}

		public InputFormatException(String message) {
			super(message);
		}
	}

}
