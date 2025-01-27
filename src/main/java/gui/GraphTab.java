package gui;

import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import net.infonode.tabbedpanel.titledtab.TitledTab;

public class GraphTab {
    private final TitledTab tab;

    public GraphTab(String name, GraphZoomScrollPane viewer) {
        tab = new TitledTab(name, null, viewer, new CloseButton());
        tab.getProperties().setHighlightedRaised(2);
    }

    public String getTitle() {
        return tab.getText();
    }

    public void setTitle(String title) {
        tab.setText(title);
    }

    public TitledTab getTitleTab() {
        return tab;
    }
}
