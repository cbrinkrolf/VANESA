package graph.eventhandlers;

import java.awt.Cursor;

import gui.MainWindow;
import net.infonode.tabbedpanel.TabDragEvent;
import net.infonode.tabbedpanel.TabEvent;
import net.infonode.tabbedpanel.TabAdapter;
import net.infonode.tabbedpanel.TabRemovedEvent;
import net.infonode.tabbedpanel.TabStateChangedEvent;

public class GraphTabListener extends TabAdapter {
	boolean startMoving = false;
	int tabs = 0;
	private final MainWindow window;

	// avoid to fire tabSelected if a new tab was added
	private boolean tabAdded = false;

	public GraphTabListener(MainWindow w) {
		window = w;
	}

	@Override
	public void tabAdded(TabEvent event) {
		tabAdded = true;
		tabs++;
	}

	@Override
	public void tabRemoved(TabRemovedEvent event) {
		tabs--;
	}

	@Override
	public void tabDragged(TabDragEvent event) {
		if (!startMoving) {
			Cursor changedCursor = new Cursor(Cursor.HAND_CURSOR);
			event.getTab().setCursor(changedCursor);
		}
		startMoving = true;
	}

	@Override
	public void tabDropped(TabDragEvent event) {
		Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
		event.getTab().setCursor(normalCursor);
		startMoving = false;
	}

	@Override
	public void tabSelected(TabStateChangedEvent event) {
		if (!tabAdded && window.getTabCount() > 0) {
			window.updateAllGuiElements();
			// window.updateElementTree();
			// window.updatePathwayTree();
			// window.updateProjectProperties();
			// window.updateTheoryProperties();
			// window.initSimResGraphs();
		}
		tabAdded = false;
	}
}
