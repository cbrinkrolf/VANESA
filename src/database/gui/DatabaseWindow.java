package database.gui;

import net.infonode.tabbedpanel.TabDropDownListVisiblePolicy;
import net.infonode.tabbedpanel.TabbedPanel;
import net.infonode.tabbedpanel.titledtab.TitledTab;
import net.infonode.util.Direction;
import database.brenda.gui.BRENDAqueryMask;
import database.kegg.gui.KEGGqueryMask;
import database.mirna.gui.MirnaQueryClass;
import database.ppi.PPIqueryMask;
import database.unid.UNIDQueryMask;

public class DatabaseWindow {

	private TabbedPanel tabbedPanel;
	private KEGGqueryMask kegg;
	private BRENDAqueryMask brenda;
	private QueryInfoWindow info;
	private PPIqueryMask ppi;
	private MirnaQueryClass mirna;
	//MARTIN new DB Search, UNID
	private UNIDQueryMask unid;

	public DatabaseWindow() {

		tabbedPanel = new TabbedPanel();
		tabbedPanel.getProperties().setTabAreaOrientation(Direction.UP);
		tabbedPanel.getProperties().setEnsureSelectedTabVisible(true);
		tabbedPanel.getProperties().setHighlightPressedTab(true);
		tabbedPanel.getProperties().setTabReorderEnabled(true);
		tabbedPanel.getProperties().setTabDropDownListVisiblePolicy(
				TabDropDownListVisiblePolicy.TABS_NOT_VISIBLE);

		kegg = new KEGGqueryMask(this);
		brenda = new BRENDAqueryMask(this);
		info = new QueryInfoWindow();
		ppi = new PPIqueryMask(this);
		mirna = new MirnaQueryClass(this);
		unid = new UNIDQueryMask(this);
		
		
		tabbedPanel.addTab(kegg.getTitelTab());
	//	tabbedPanel.addTab(dawis.getTitelTab());
		tabbedPanel.addTab(ppi.getTitelTab());
		tabbedPanel.addTab(brenda.getTitelTab());
		tabbedPanel.addTab(mirna.getTitelTab());
		tabbedPanel.addTab(unid.getTitelTab());
		

		// tabbedPanel.addTab(info.getTab());

		// tabbedPanel.addTab(info.getTab());

	}

	public TabbedPanel getPanel() {
		return tabbedPanel;
	}

	public boolean somethingTypedIn() {
	
		if (selectedDatabase().equals("KEGG")) {
			return kegg.doSearchCriteriaExist();
		} else if (selectedDatabase().equals("BRENDA")) {
			return brenda.doSearchCriteriaExist();
		
		} else if (selectedDatabase().equals("PPI")) {
			return ppi.doSearchCriteriaExist();
		}else if (selectedDatabase().equals("miRNA")) {
			return mirna.doSearchCriteriaExist();
		}else if (selectedDatabase().equals("UNID")){
			return unid.doSearchCriteriaExist();
		}
		return false;
	}

	public String selectedDatabase() {
		TitledTab t = (TitledTab) tabbedPanel.getSelectedTab();
		return t.getText();
	}
	public String[] getInput() {
		TitledTab t = (TitledTab) tabbedPanel.getSelectedTab();
		if (t.getText().equals("KEGG")) {
			return kegg.getKeyword();
		} else if (t.getText().equals("BRENDA")) {
			return brenda.getKeyword();
		}  else if (t.getText().equals("PPI")) {
			return ppi.getKeyword();
		}else if (t.getText().equals("miRNA")) {
			return mirna.getKeyword();
		}else if (t.getText().equals("UNID")) {
			return unid.getKeyword();
		}
		return null;
	}

	public void reset() {

		TitledTab t = (TitledTab) tabbedPanel.getSelectedTab();
		if (t.getText().equals("KEGG")) {
			kegg.reset();
		} else if (t.getText().equals("BRENDA")) {
			brenda.reset();
		}  else if (t.getText().equals("PPI")) {
			ppi.reset();
		} else if (t.getText().equals("miRNA")) {
			mirna.reset();
		}
	}

	

}
