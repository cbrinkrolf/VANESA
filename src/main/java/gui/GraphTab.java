package gui;

import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import net.infonode.tabbedpanel.TitledTab;

public class GraphTab {
    private final TitledTab tab;

    public GraphTab(String name, GraphZoomScrollPane viewer) {
        tab = new TitledTab(name, null, viewer, new CloseButton());
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
