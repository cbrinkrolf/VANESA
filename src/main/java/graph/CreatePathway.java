package graph;

import biologicalElements.Pathway;
import biologicalElements.PathwayType;
import gui.MainWindow;

import java.awt.*;

public class CreatePathway {
	public static Pathway create() {
		return create("Untitled", PathwayType.BiologicalNetwork);
	}

	public static Pathway create(final PathwayType type) {
		return create("Untitled", type);
	}

	public static Pathway create(final String title) {
		return create(title, PathwayType.BiologicalNetwork);
	}

	public static Pathway create(final String title, final PathwayType type) {
		final MainWindow window = MainWindow.getInstance();
		final GraphContainer con = GraphContainer.getInstance();
		window.setCursor(Cursor.WAIT_CURSOR);
		final Pathway pathway = con.addPathway(title, new Pathway(title, type));
		window.addTab(pathway.getTab());
		window.setCursor(Cursor.DEFAULT_CURSOR);
		return pathway;
	}

	public static void showPathway(final Pathway pw) {
		final MainWindow window = MainWindow.getInstance();
		final GraphContainer con = GraphContainer.getInstance();
		if (con.getAllPathways().contains(pw)) {
			window.setSelectedTab(pw.getTab());
			return;
		}
		con.addPathway(pw.getName(), pw);
		window.addTab(pw.getTab());
		window.setCursor(Cursor.DEFAULT_CURSOR);
	}
}
