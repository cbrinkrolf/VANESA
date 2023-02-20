package database.gui;

import javax.swing.JTabbedPane;

import database.brenda.gui.BRENDAQueryMask;
import database.brenda2.gui.BRENDA2QueryMask;
import database.kegg.gui.KEGGQueryMask;
import database.mirna.gui.MirnaQueryMask;
import database.ppi.gui.PPIQueryMask;

public class DatabaseWindow {
    private final JTabbedPane tabbedPanel;

    public DatabaseWindow() {
        tabbedPanel = new JTabbedPane();
        addQueryTab(new KEGGQueryMask());
        addQueryTab(new PPIQueryMask());
        addQueryTab(new BRENDAQueryMask());
        addQueryTab(new MirnaQueryMask());
        addQueryTab(new BRENDA2QueryMask());
    }

    private void addQueryTab(QueryMask mask) {
        int index = tabbedPanel.getTabCount();
        tabbedPanel.addTab(mask.getMaskName(), mask.getPanel());
        tabbedPanel.setTabComponentAt(index, mask.getTitleTab());
    }

    public JTabbedPane getPanel() {
        return tabbedPanel;
    }
}
