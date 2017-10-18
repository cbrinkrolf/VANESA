package graph.eventhandlers;

import java.awt.Cursor;

import gui.MainWindow;
import net.infonode.tabbedpanel.TabDragEvent;
import net.infonode.tabbedpanel.TabEvent;
import net.infonode.tabbedpanel.TabListener;
import net.infonode.tabbedpanel.TabRemovedEvent;
import net.infonode.tabbedpanel.TabStateChangedEvent;

public class GraphTabListener implements TabListener {

	boolean startMoving = false;
	int tabs = 0;
	private MainWindow window;
	
	public GraphTabListener(MainWindow w){
		window = w;
	}
	
	public void tabAdded(TabEvent event) {
		tabs ++;
	}

	public void tabRemoved(TabRemovedEvent event) {
		tabs--;
	}

	public void tabDragged(TabDragEvent event) {
		if (!startMoving) {
			Cursor changedCursor = new Cursor(Cursor.HAND_CURSOR);
			event.getTab().setCursor(changedCursor);
		}
		startMoving = true;
	}

	public void tabDropped(TabDragEvent event) {
		Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
		event.getTab().setCursor(normalCursor);
		startMoving = false;
	}

	public void tabDragAborted(TabEvent event) {

	}

	public void tabSelected(TabStateChangedEvent event) {
		
		if(window.getTabCount() > 0){
			window.updateElementTree();
			window.updateSatelliteView();
			window.updatePathwayTree();
			window.updateProjectProperties();
//			window.updateTheoryProperties();
			window.updateAlignmentTab();
			window.initPCPGraphs();
		}
	}

	public void tabDeselected(TabStateChangedEvent event) {
		
	}

	public void tabHighlighted(TabStateChangedEvent event) {

	}

	public void tabDehighlighted(TabStateChangedEvent event) {

	}

	public void tabMoved(TabEvent event) {

	}
}
