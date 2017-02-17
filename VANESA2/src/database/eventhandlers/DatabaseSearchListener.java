package database.eventhandlers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import database.brenda.BRENDASearch;
import database.brenda.gui.BrendaInfoWindow;
import database.brenda2.BRENDA2Search;
import database.gui.DatabaseWindow;
import database.kegg.KeggSearch;
import database.kegg.gui.KEGGInfoWindow;
import database.mirna.mirnaSearch;
import database.mirna.gui.MIRNAInfoWindow;
import database.ppi.PPISearch;
import database.ppi.gui.PPIInfoWindow;
import database.unid.UNIDInfoWindow;
import database.unid.UNIDSearch;
import graph.ContainerSingelton;
import graph.GraphContainer;
import graph.jung.classes.MyGraph;
import gui.MainWindow;

public class DatabaseSearchListener implements ActionListener {
	private DatabaseWindow dw;

	public DatabaseSearchListener(DatabaseWindow dw) {
		this.dw = dw;
	}

	private void requestKEGGcontent() {
		//new GetPublications("C:\\Users\\Vallani\\Desktop\\result.txt");

		MainWindow.getInstance().showProgressBar("KEGG query");
		KeggSearch keggSearch = new KeggSearch(dw.getInput(),
				MainWindow.getInstance(),null);
		keggSearch.execute();
	}

	private void requestBrendaContent() {
		MainWindow.getInstance().showProgressBar("BRENDA query");
		BRENDASearch brendaSearch = new BRENDASearch(dw.getInput(),
				MainWindow.getInstance(), null, dw.isHeadless());
		brendaSearch.execute();
	}
	
	private void requestBrenda2Content() {
		MainWindow.getInstance().showProgressBar("BRENDA 2 query");
		BRENDA2Search brenda2Search = new BRENDA2Search(dw.getInput(), null, dw.isHeadless());
		brenda2Search.execute();
	}

	/**
	 * Requests a database to get some content.
	 */


	private void requestPPIcontent() {
		MainWindow.getInstance().showProgressBar("PPI query");
		PPISearch ppiSearch = new PPISearch(dw.getInput(),
				MainWindow.getInstance(),dw.isHeadless());
		ppiSearch.execute();

	}
	
	private void requestMIRNAcontent() {
		MainWindow.getInstance().showProgressBar("miRNA query");		
		mirnaSearch mirnaS = new mirnaSearch(dw.getInput(),
				MainWindow.getInstance(), dw.isHeadless());
		
		mirnaS.execute();   
	}
	
	private void requestUNIDContent(){
		MainWindow.getInstance().showProgressBar("UNID query.");
		UNIDSearch unidS = new UNIDSearch(dw.getInput(), dw.isHeadless());
		unidS.execute();
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String event = e.getActionCommand();
		if ("reset".equals(event)) {
			dw.reset();
		} else if ("KEGGinfo".equals(event)) {
			new KEGGInfoWindow();
		} else if ("PPIinfo".equals(event)) {
			new PPIInfoWindow();
		} else if ("BRENDAinfo".equals(event)) {
			new BrendaInfoWindow();
		} else if ("miRNAinfo".equals(event)) {
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
				}else if(dw.selectedDatabase().equals("BRENDA2")){
					this.requestBrenda2Content();
				}
			} else {
				JOptionPane.showConfirmDialog(MainWindow.getInstance(),
						"Please type something into the search form.");
			}
		} else if ("pickcommons".equals(event)) {
			pickCommons();			
		} else if("pickneighbors".equals(event)){
			pickNeighbors();
		}
	}
	
	private void pickCommons() {
		if (ContainerSingelton.getInstance().containsPathway()) {
			String commonNames[] = dw.getInput()[2].split(",");
			MainWindow w = MainWindow.getInstance();
			GraphContainer con = ContainerSingelton.getInstance();
			Pathway pw = con.getPathway(w.getCurrentPathway());
			MyGraph mg = pw.getGraph();

			for (BiologicalNodeAbstract bna : mg.getAllVertices()) {
				for (int i = 0; i < commonNames.length; i++) {
					if (bna.getLabel().equals(commonNames[i])) {
						mg.getVisualizationViewer().getPickedVertexState()
								.pick(bna, true);
					}
				}
			}
		} else
			JOptionPane.showMessageDialog(null, "please load a network first.");

	}

	private void pickNeighbors() {
		if (ContainerSingelton.getInstance().containsPathway()) {
			MainWindow w = MainWindow.getInstance();
			GraphContainer con = ContainerSingelton.getInstance();
			Pathway pw = con.getPathway(w.getCurrentPathway());
			MyGraph mg = pw.getGraph();

			int amount_picked = mg.getVisualizationViewer()
					.getPickedVertexState().getPicked().size();
			BiologicalNodeAbstract picked[] = new BiologicalNodeAbstract[amount_picked];

			mg.getVisualizationViewer().getPickedVertexState().getPicked()
					.toArray(picked);

			for (BiologicalNodeAbstract source : picked) {
				for (BiologicalNodeAbstract neighbor : mg.getJungGraph()
						.getNeighbors(source)) {
					if (!mg.getVisualizationViewer().getPickedVertexState()
							.isPicked(neighbor)) {
						mg.getVisualizationViewer().getPickedVertexState()
								.pick(neighbor, true);
					}
				}
			}
		} else
			JOptionPane.showMessageDialog(null, "please load a network first.");
	}
}
