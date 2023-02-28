package database.gui;

import javax.swing.JTabbedPane;

import database.brenda.gui.BRENDAQueryMask;
import database.mirna.gui.MirnaQueryMask;
import database.ppi.gui.PPIQueryMask;

public class DatabaseWindow {
    private final JTabbedPane tabbedPanel;

    public DatabaseWindow() {
        tabbedPanel = new JTabbedPane();
        // TODO: addQueryTab(new KEGGQueryMask());
        addQueryTab(new PPIQueryMask());
        addQueryTab(new BRENDAQueryMask());
        addQueryTab(new MirnaQueryMask());
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
