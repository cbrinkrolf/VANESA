package gui;

import biologicalElements.PathwayType;
import graph.rendering.VanesaGraphRendererPanel;
import net.infonode.tabbedpanel.TitledTab;

import javax.swing.*;

public class GraphTab extends TitledTab {
	private static final ImageIcon biologicalNetworkIcon = ImagePath.getInstance().getImageIcon(
			"biologicalNetworkTiny.png");
	private static final ImageIcon petriNetworkIcon = ImagePath.getInstance().getImageIcon("petriNetworkTiny.png");

	public GraphTab(final String name, final VanesaGraphRendererPanel viewer) {
		super(name, null, viewer, null);
		setTitleComponent(new CloseButton(this));
	}

	public String getTitle() {
		return getText();
	}

	public void setTitle(final String title) {
		setText(title);
	}

	public void setIcon(final PathwayType type) {
		if (type == PathwayType.BiologicalNetwork) {
			setIcon(biologicalNetworkIcon);
		} else if (type == PathwayType.PetriNet) {
			setIcon(petriNetworkIcon);
		}
	}
}
