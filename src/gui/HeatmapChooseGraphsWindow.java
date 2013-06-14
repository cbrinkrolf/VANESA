package gui;

import graph.algorithms.HeatmapGraphs;
import java.util.ArrayList;
import biologicalElements.Pathway;

public class HeatmapChooseGraphsWindow extends ChooseGraphsWindow {

	private static final long serialVersionUID = 5807040718662024077L;
	
	public HeatmapChooseGraphsWindow() {
		super("Create Heatgraph");
	}
	
	
	@Override
	public void handleChosenGraphs(ArrayList<Pathway> pathways) {
		new HeatmapGraphs(pathways);
	}
	
}
