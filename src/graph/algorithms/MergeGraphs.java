package graph.algorithms;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.xml.stream.XMLStreamException;

import biologicalElements.Pathway;
import graph.CreatePathway;
import gui.MainWindow;
import xmlInput.sbml.VAMLInput;
import xmlOutput.sbml.VAMLoutput;

public class MergeGraphs {

	private Pathway pw_one;
	private Pathway pw_two;
	private Pathway pw_new;

	public MergeGraphs(Pathway one, Pathway two, boolean showMessage) {

		pw_one = one;
		pw_two = two;
		pw_new = new CreatePathway(pw_one.getTitle() + "" + pw_two.getTitle())
				.getPathway();
		pw_new.setOrganism("");
		pw_new.setLink("");

		//CHRIS better deep copy of pathway
		File file1 = new File("test");
		try {
			new VAMLoutput(new FileOutputStream(file1), one);
			new VAMLInput(file1, pw_new);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}

		File file2 = new File("test2");
		try {
			new VAMLoutput(new FileOutputStream(file2), two);
			new VAMLInput(file2, pw_new);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
		
		MainWindow.getInstance().enableOptionPanelUpdate(false);
		CompareGraphs.mergeGraph(pw_new);
		MainWindow.getInstance().enableOptionPanelUpdate(true);
		
		MainWindow.getInstance().updateProjectProperties();
		MainWindow.getInstance().updateOptionPanel();

		if (showMessage) JOptionPane.showMessageDialog(null,
				"The graphs have been merged and are visualized in a new tab.");
		
		
		
//		pw_new.getGraph().changeToGEMLayout();
//		pw_new.getGraph().unlockVertices();
//		pw_new.getGraph().restartVisualizationModel();
	

	}

	public void setPw_new(Pathway pw_new) {
		this.pw_new = pw_new;
	}

	public Pathway getPw_new() {
		return pw_new;
	}

}
