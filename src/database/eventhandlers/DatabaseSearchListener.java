package database.eventhandlers;

import graph.GraphInstance;
import gui.MainWindowSingelton;
import gui.ProgressBar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import database.brenda.BRENDASearch;
import database.brenda.gui.BrendaInfoWindow;
import database.gui.DatabaseWindow;
import database.kegg.KeggSearch;
import database.kegg.gui.KEGGInfoWindow;
import database.mirna.GetPublications;
import database.mirna.mirnaSearch;
import database.mirna.gui.MIRNAInfoWindow;
import database.ppi.PPISearch;

public class DatabaseSearchListener implements ActionListener {
	private DatabaseWindow dw;

	public DatabaseSearchListener(DatabaseWindow dw) {
		this.dw = dw;
	}

	private void requestKEGGcontent() {
		//new GetPublications("C:\\Users\\Vallani\\Desktop\\result.txt");
	
		KeggSearch keggSearch = new KeggSearch(dw.getInput(),
				MainWindowSingelton.getInstance(), new ProgressBar(),null);
		keggSearch.execute();
	}

	private void requestBrendaContent() {
		BRENDASearch brendaSearch = new BRENDASearch(dw.getInput(),
				MainWindowSingelton.getInstance(), new ProgressBar(), null);
		brendaSearch.execute();
	}

	/**
	 * Requests a database to get some content.
	 */


	private void requestPPIcontent() {
		PPISearch ppiSearch = new PPISearch(dw.getInput(),
				MainWindowSingelton.getInstance(), new ProgressBar());
		ppiSearch.execute();

	}
	
	private void requestMIRNAcontent() {
		
		mirnaSearch mirnaS = new mirnaSearch(dw.getInput(),
						MainWindowSingelton.getInstance(), new ProgressBar());
		
		mirnaS.execute();   
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
		} else {

			if ("searchDatabase".equals(event)) {
				if (dw.somethingTypedIn()) {

					if (dw.selectedDatabase().equals("KEGG")) {
						this.requestKEGGcontent();
					} else if (dw.selectedDatabase().equals("BRENDA")) {
						this.requestBrendaContent();
					} else if (dw.selectedDatabase().equals("PPI")) {
						this.requestPPIcontent();
					}else if (dw.selectedDatabase().equals("miRNA")) {
						this.requestMIRNAcontent();
					}
				} else {
					JOptionPane.showConfirmDialog(null,
							"Please type something into the search form.");
				}
			}
		}
	}
}
