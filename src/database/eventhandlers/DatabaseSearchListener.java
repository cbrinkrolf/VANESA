package database.eventhandlers;

import graph.ContainerSingelton;
import graph.GraphContainer;
import graph.jung.classes.MyGraph;
import gui.MainWindow;
import gui.MainWindowSingleton;
import gui.ProgressBar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import database.brenda.BRENDASearch;
import database.brenda.gui.BrendaInfoWindow;
import database.gui.DatabaseWindow;
import database.kegg.KeggSearch;
import database.kegg.gui.KEGGInfoWindow;
import database.mirna.mirnaSearch;
import database.mirna.gui.MIRNAInfoWindow;
import database.ppi.PPISearch;
import database.unid.UNIDInfoWindow;
import database.unid.UNIDSearch;

public class DatabaseSearchListener implements ActionListener {
	private DatabaseWindow dw;

	public DatabaseSearchListener(DatabaseWindow dw) {
		this.dw = dw;
	}

	private void requestKEGGcontent() {
		//new GetPublications("C:\\Users\\Vallani\\Desktop\\result.txt");
	
		KeggSearch keggSearch = new KeggSearch(dw.getInput(),
				MainWindowSingleton.getInstance(), new ProgressBar(),null);
		keggSearch.execute();
	}

	private void requestBrendaContent() {
		BRENDASearch brendaSearch = new BRENDASearch(dw.getInput(),
				MainWindowSingleton.getInstance(), new ProgressBar(), null);
		brendaSearch.execute();
	}

	/**
	 * Requests a database to get some content.
	 */


	private void requestPPIcontent() {
		PPISearch ppiSearch = new PPISearch(dw.getInput(),
				MainWindowSingleton.getInstance(), new ProgressBar());
		ppiSearch.execute();

	}
	
	private void requestMIRNAcontent() {
		
		mirnaSearch mirnaS = new mirnaSearch(dw.getInput(),
						MainWindowSingleton.getInstance(), new ProgressBar());
		
		mirnaS.execute();   
	}
	
	private void requestUNIDContent(){
		UNIDSearch unidS = new UNIDSearch(dw.getInput());
		UNIDSearch.progressBar = new ProgressBar();
		UNIDSearch.progressBar.init(100, "UNID", true);
		UNIDSearch.progressBar.setProgressBarString("Getting search results");
		MainWindow mw = MainWindowSingleton.getInstance();
		mw.setEnable(false);
		unidS.execute();
		
	}
	
	private void pickCommons(){
		String commonNames[] = dw.getInput()[2].split(",");
		MainWindow w = MainWindowSingleton.getInstance();
		GraphContainer con = ContainerSingelton.getInstance();
		Pathway pw = con.getPathway(w.getCurrentPathway());
		MyGraph mg = pw.getGraph();
		
		 for(BiologicalNodeAbstract bna : mg.getAllVertices()){
				for (int i = 0; i < commonNames.length; i++) {
					if(bna.getLabel().equals(commonNames[i])){
						mg.getVisualizationViewer().getPickedVertexState().pick(bna, true);
					}
				}
		 }
		
		
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String event = e.getActionCommand();

		if ("reset".equals(event)) {
			dw.reset();
		} else if ("KEGGinfo".equals(event)) {
			new KEGGInfoWindow();
		} else if ("BRENDAinfo".equals(event)) {
			new BrendaInfoWindow();
		} else if ("MIRNAinfo".equals(event)) {
			new MIRNAInfoWindow();
		} else if ("UNIDinfo".equals(event)) {
			new UNIDInfoWindow();
		}else if ("searchDatabase".equals(event)) {
			if (dw.somethingTypedIn()) {
				if (dw.selectedDatabase().equals("KEGG")) {
					this.requestKEGGcontent();
				} else if (dw.selectedDatabase().equals("BRENDA")) {
					this.requestBrendaContent();
				} else if (dw.selectedDatabase().equals("PPI")) {
					this.requestPPIcontent();
				}else if (dw.selectedDatabase().equals("miRNA")) {
					this.requestMIRNAcontent();
				}else if (dw.selectedDatabase().equals("UNID")) {
					this.requestUNIDContent();
				}
			} else {
				JOptionPane.showConfirmDialog(null,
						"Please type something into the search form.");
			}
		} else if ("pickcommons".equals(event)) {
			pickCommons();			
		}
	}
}
