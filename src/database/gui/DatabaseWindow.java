package database.gui;

import java.util.HashMap;

import javax.swing.JTabbedPane;

import database.brenda.gui.BRENDAqueryMask;
import database.brenda2.gui.BRENDA2queryMask;
import database.kegg.gui.KEGGqueryMask;
import database.mirna.gui.MirnaQueryClass;
import database.ppi.gui.PPIqueryMask;
import gui.MainWindow;

public class DatabaseWindow {

	private JTabbedPane tabbedPanel;
	private KEGGqueryMask kegg;
	private BRENDAqueryMask brenda;
	// private QueryInfoWindow info;
	private PPIqueryMask ppi;
	private MirnaQueryClass mirna;
	//private UNIDQueryMask unid;
	private BRENDA2queryMask brenda2;

	private boolean headless = false;

	private HashMap<Integer, String> tabs = new HashMap<Integer, String>();

	public DatabaseWindow() {

		tabbedPanel = new JTabbedPane();
		// tabbedPanel.getProperties().setTabAreaOrientation(Direction.UP);
		// tabbedPanel.getProperties().setEnsureSelectedTabVisible(true);
		// tabbedPanel.getProperties().setHighlightPressedTab(true);
		// tabbedPanel.getProperties().setTabReorderEnabled(true);
		// tabbedPanel.getProperties().setTabDropDownListVisiblePolicy(
		// TabDropDownListVisiblePolicy.TABS_NOT_VISIBLE);

		kegg = new KEGGqueryMask(this);
		brenda = new BRENDAqueryMask(this);
		// info = new QueryInfoWindow();
		ppi = new PPIqueryMask(this);
		mirna = new MirnaQueryClass(this);
		//unid = new UNIDQueryMask(this);
		brenda2 = new BRENDA2queryMask(this);

		tabbedPanel.addTab("KEGG", kegg.getPanel());
		tabbedPanel.setTabComponentAt(0, kegg.getTitelTab("KEGG"));
		tabs.put(0, "KEGG");

		// tabbedPanel.addTab(dawis.getTitelTab());
		tabbedPanel.addTab("PPI", ppi.getPanel());
		tabbedPanel.setTabComponentAt(1, ppi.getTitelTab("PPI"));
		tabs.put(1, "PPI");
		
		tabbedPanel.addTab("BRENDA", brenda.getPanel());
		tabbedPanel.setTabComponentAt(2, brenda.getTitelTab("BRENDA"));
		tabs.put(2, "BRENDA");
		
		tabbedPanel.addTab("miRNA", mirna.getPanel());
		tabbedPanel.setTabComponentAt(3, mirna.getTitelTab("miRNA"));
		tabs.put(3, "miRNA");

		
		if(MainWindow.developer){
			//tabbedPanel.addTab("UNID", unid.getPanel());
			//tabbedPanel.setTabComponentAt(4, unid.getTitelTab("UNID"));
			//tabs.put(4, "UNID");
			tabbedPanel.addTab("BRENDA2", brenda2.getPanel());
			tabbedPanel.setTabComponentAt(4, brenda2.getTitelTab("B2"));
			tabs.put(4, "BRENDA2");
		}
		
		// tabbedPanel.addTab(unid.getTitelTab());

		// tabbedPanel.addTab(info.getTab());

		// tabbedPanel.addTab(info.getTab());

	}

	public JTabbedPane getPanel() {
		return tabbedPanel;
	}

	public boolean somethingTypedIn() {

		if (selectedDatabase().equals("KEGG")) {
			return kegg.doSearchCriteriaExist();
		} else if (selectedDatabase().equals("BRENDA")) {
			return brenda.doSearchCriteriaExist();

		} else if (selectedDatabase().equals("PPI")) {
			return ppi.doSearchCriteriaExist();
		} else if (selectedDatabase().equals("miRNA")) {
			return mirna.doSearchCriteriaExist();
		} else if (selectedDatabase().equals("UNID")) {
			//return unid.doSearchCriteriaExist();
		} else if(selectedDatabase().equals("BRENDA2")){
			return brenda2.doSearchCriteriaExist();
		}
		return false;
	}

	public String selectedDatabase() {
		// TitledTab t = (TitledTab) tabbedPanel.getSelectedTab();
		return tabs.get(tabbedPanel.getSelectedIndex());
		// return t.getText();
	}

	public String[] getInput() {
		// TitledTab t = (TitledTab) tabbedPanel.getSelectedTab();

		String db = tabs.get(tabbedPanel.getSelectedIndex());
		//System.out.println(db);
		if (db.equals("KEGG")) {
			return kegg.getKeyword();
		} else if (db.equals("BRENDA")) {
			return brenda.getKeyword();
		} else if (db.equals("PPI")) {
			return ppi.getKeyword();
		} else if (db.equals("miRNA")) {
			return mirna.getKeyword();
		} else if (db.equals("UNID")) {
			//return unid.getKeyword();
		}else if (db.equals("BRENDA2")){
			return brenda2.getKeyword();
		}
		return null;
	}

	public void reset() {

		String db = tabs.get(tabbedPanel.getSelectedIndex());
		if (db.equals("KEGG")) {
			kegg.reset();
		} else if (db.equals("BRENDA")) {
			brenda.reset();
		} else if (db.equals("PPI")) {
			ppi.reset();
		} else if (db.equals("miRNA")) {
			mirna.reset();
		} else if (db.equals("UNID")) {
			//unid.reset();
		}else if(db.equals("BRENDA2")){
			brenda2.reset();
		}
	}

	public void setHeadless(boolean headless) {
		this.headless = headless;
	}

	public boolean isHeadless() {
		return this.headless;
	}
	
	public boolean isHsaOnlyMirna(){
		return mirna.isHsaOnly();
	}
}
