package graph.algorithms;

import java.io.File;
import java.io.IOException;

import graph.CreatePathway;
import gui.MainWindowSingelton;

import javax.swing.JOptionPane;
import javax.xml.stream.XMLStreamException;

import xmlInput.sbml.VAMLInput;
import xmlOutput.sbml.VAMLoutput;
import biologicalElements.Pathway;

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

		pw_new.getGraph().lockVertices();
		pw_new.getGraph().stopVisualizationModel();

		//CHRIS better deep copy of pathway
		File file1 = new File("test");
		try {
			new VAMLoutput(file1, one);
			new VAMLInput(file1, pw_new);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}

		File file2 = new File("test2");
		try {
			new VAMLoutput(file2, two);
			new VAMLInput(file2, pw_new);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
		
		MainWindowSingelton.getInstance().enableOptionPanelUpdate(false);
		CompareGraphs.mergeGraph(pw_new);
		MainWindowSingelton.getInstance().enableOptionPanelUpdate(true);
		
		MainWindowSingelton.getInstance().updateProjectProperties();
		MainWindowSingelton.getInstance().updateOptionPanel();

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
