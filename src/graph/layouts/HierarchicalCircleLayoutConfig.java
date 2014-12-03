package graph.layouts;

import graph.layouts.hebLayout.HEBLayoutConfig;
import configurations.gui.ConfigPanel;

public abstract class HierarchicalCircleLayoutConfig extends ConfigPanel{

	private static final long serialVersionUID = 83123L;

	private static HEBLayoutConfig instance;

	public static int GROUP_DISTANCE_FACTOR = 5;
	public static int GROUP_DEPTH = 1;
	
	protected boolean showInternalEdges = true;
	protected boolean resetLayout = false;
	protected boolean autoRelayout = true;
	protected boolean moveInGroups = true;
	
	public HierarchicalCircleLayoutConfig(Class<? extends HierarchicalCircleLayout> class1){
		super(class1);
	}
	
	public static HierarchicalCircleLayoutConfig getInstance() {
		if(instance == null){
			instance = new HEBLayoutConfig();
		}
		return instance;
	}
	
	public static double nodeDistance(int groups, int nodes){
		return 2*Math.PI / ((GROUP_DISTANCE_FACTOR - 1)*groups+nodes);
	}
	
	public boolean getShowInternalEdges(){
		return showInternalEdges;
	}
	
	public boolean resetLayout(){
		return resetLayout;
	}
	
	public boolean getAutoRelayout(){
		return autoRelayout;
	}
	
	public boolean getMoveInGroups(){
		return moveInGroups;
	}
}
