package graph;

import biologicalElements.Pathway;
import gui.MainWindow;

import java.awt.*;

public class CreatePathway {
    private final String name;
    private final Pathway pathway;

    public CreatePathway() {
        this("Untitled");
    }

    public CreatePathway(final String title) {
        name = title;
        pathway = buildPathway();
    }

    private Pathway buildPathway() {
        final MainWindow window = MainWindow.getInstance();
        final GraphContainer con = GraphContainer.getInstance();
        window.setCursor(Cursor.WAIT_CURSOR);
        final String newPathwayName = con.addPathway(name, new Pathway(name));
        final Pathway pw = con.getPathway(newPathwayName);
        window.addTab(pw.getTab());
        window.setCursor(Cursor.DEFAULT_CURSOR);
        return pw;
    }

    public Pathway getPathway() {
        return pathway;
    }

    public static void showPathway(Pathway pw) {
        final MainWindow window = MainWindow.getInstance();
        final GraphContainer con = GraphContainer.getInstance();
        if (con.getAllPathways().contains(pw)) {
            window.setSelectedTab(pw.getTab());
            return;
        }
        final String newPathwayName = con.addPathway(pw.getName(), pw);
        pw = con.getPathway(newPathwayName);
        window.addTab(pw.getTab());
        window.setCursor(Cursor.DEFAULT_CURSOR);
        //pw.updateMyGraph();
    }
}
