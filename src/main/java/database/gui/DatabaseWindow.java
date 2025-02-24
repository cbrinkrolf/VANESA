package database.gui;

import javax.swing.JTabbedPane;

import database.brenda.gui.BRENDAQueryMask;
import database.kegg.gui.KEGGQueryMask;
import database.mirna.gui.MirnaQueryMask;
import database.ppi.gui.PPIQueryMask;

public class DatabaseWindow extends JTabbedPane {
    public DatabaseWindow() {
        addQueryTab(new KEGGQueryMask());
        addQueryTab(new PPIQueryMask());
        addQueryTab(new BRENDAQueryMask());
        addQueryTab(new MirnaQueryMask());
    }

    private void addQueryTab(final QueryMask mask) {
        int index = getTabCount();
        addTab(mask.getMaskName(), mask.getPanel());
        setTabComponentAt(index, mask.getTitleTab());
    }
}
