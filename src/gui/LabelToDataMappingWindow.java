package gui;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class LabelToDataMappingWindow {

	private JFileChooser filechooser;
	private File datafile;
	
	
	public LabelToDataMappingWindow() {

		filechooser = new JFileChooser();
		filechooser.setDialogTitle("please choose your mapping file (label->data)");
		
		
		
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
			System.out.println("Unknown switch case in class:"+this.getClass());
			break;
		}

	}

	private void processFile() {
		
	}
}
