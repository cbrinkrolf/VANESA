package graph.algorithms;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import biologicalElements.Pathway;
import graph.CreatePathway;
import gui.MainWindow;
import gui.PopUpDialog;
import io.sbml.JSBMLInput;
import io.sbml.JSBMLOutput;

public class MergeGraphs {
	private Pathway pwNew;

	public MergeGraphs(Pathway pwOne, Pathway pwTwo, boolean showMessage) {
		pwNew = new CreatePathway(pwOne.getTitle() + pwTwo.getTitle()).getPathway();
		pwNew.setOrganism("");
		pwNew.setLink("");

		// CHRIS better deep copy of pathway
		File file1 = new File("test");
		try {
			FileOutputStream fos1 = new FileOutputStream(file1);
			String output1 = new JSBMLOutput(fos1, pwOne).generateSBMLDocument();
			// new VAMLOutput(fos1, one);
			String input1 = new JSBMLInput(pwNew, false).loadSBMLFile(new FileInputStream(file1), file1);
			// new VAMLInput(file1, pw_new);
		} catch (Exception e) {
			e.printStackTrace();
		}

		File file2 = new File("test2");
		try {
			FileOutputStream fos2 = new FileOutputStream(file2);
			String output2 = new JSBMLOutput(fos2, pwTwo).generateSBMLDocument();

			// new VAMLOutput(new FileOutputStream(file2), two);
			// new VAMLInput(file2, pw_new);
			String input2 = new JSBMLInput(pwNew, false).loadSBMLFile(new FileInputStream(file2), file2);
		} catch (Exception e) {
			e.printStackTrace();
		}

		MainWindow.getInstance().enableOptionPanelUpdate(false);
		CompareGraphs.mergeGraph(pwNew);
		MainWindow.getInstance().enableOptionPanelUpdate(true);

		MainWindow.getInstance().updateProjectProperties();
		MainWindow.getInstance().updateOptionPanel();

		if (showMessage) {
			PopUpDialog.getInstance().show("Finished", "The graphs have been merged and are visualized in a new tab.");
		}
		file1.delete();
		file2.delete();
//		pw_new.getGraph().changeToGEMLayout();
//		pw_new.getGraph().unlockVertices();
//		pw_new.getGraph().restartVisualizationModel();
	}

	public void setPw_new(Pathway pw_new) {
		this.pwNew = pw_new;
	}

	public Pathway getPwNew() {
		return pwNew;
	}
}
