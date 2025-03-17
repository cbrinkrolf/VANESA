package graph.algorithms;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import biologicalElements.Pathway;
import graph.CreatePathway;
import gui.MainWindow;
import gui.PopUpDialog;
import io.vaml.VAMLInput;
import io.vaml.VAMLOutput;

public class MergeGraphs {
	private Pathway pw_new;

	public MergeGraphs(Pathway one, Pathway two, boolean showMessage) {
		pw_new = CreatePathway.create(one.getTitle() + two.getTitle());

		//CHRIS better deep copy of pathway
		File file1 = new File("test");
		try {
			new VAMLOutput(new FileOutputStream(file1), one);
			new VAMLInput(file1, pw_new);
		} catch (IOException e) {
			e.printStackTrace();
		}

		File file2 = new File("test2");
		try {
			new VAMLOutput(new FileOutputStream(file2), two);
			new VAMLInput(file2, pw_new);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		MainWindow.getInstance().enableOptionPanelUpdate(false);
		CompareGraphs.mergeGraph(pw_new);
		MainWindow.getInstance().enableOptionPanelUpdate(true);
		
		MainWindow.getInstance().updateProjectProperties();
		MainWindow.getInstance().updateOptionPanel();

		if (showMessage){
			PopUpDialog.getInstance().show("Finished", "The graphs have been merged and are visualized in a new tab.");
		}
		file1.delete();
		file2.delete();
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
