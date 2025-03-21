package net.infonode.tabbedpanel;

import javax.swing.*;
import java.awt.*;

/**
 * A TabContentPanel is a container for tabs' content components. It listens to a tabbed panel and manages the tabs'
 * content components by showing and hiding the components based upon the selection of tabs in the tabbed panel.
 */
public class TabContentPanel extends JPanel {
	private Component currentComponent;

	/**
	 * Constructs a TabContentPanel
	 *
	 * @param tabbedPanel the TabbedPanel for whom this component is the tabs' content component container
	 */
	public TabContentPanel(TabbedPanel tabbedPanel) {
		super(new BorderLayout());
		setOpaque(false);
		tabbedPanel.addTabListener(new TabAdapter() {
			public void tabSelected(TabStateChangedEvent event) {
				if (currentComponent != null) {
					remove(currentComponent);
				}
				currentComponent = event.getTab() == null ? null : event.getTab().getContentComponent();
				if (currentComponent != null) {
					add(currentComponent, BorderLayout.CENTER, -1);
				}
			}

			public void tabRemoved(TabRemovedEvent event) {
				if (currentComponent == event.getTab().getContentComponent()) {
					remove(currentComponent);
					currentComponent = null;
				}
			}

			public void tabAdded(TabEvent event) {
				if (event.getTab().getContentComponent() != null) {
					if (currentComponent != null) {
						remove(currentComponent);
					}
					currentComponent = event.getTab().getContentComponent();
					add(currentComponent, BorderLayout.CENTER, -1);
				}
			}
		});
	}
}
