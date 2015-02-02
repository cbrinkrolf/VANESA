package graph.layouts;

import configurations.gui.ConfigPanel;

public abstract class HierarchicalCircleLayoutConfig extends ConfigPanel{

	private static final long serialVersionUID = 83123L;

	public static int GROUP_DISTANCE_FACTOR = 5;
	public static int GROUP_DEPTH = 1;
	public static double CIRCLE_SIZE = 0.45;
	
	protected boolean showInternalEdges = true;
	protected boolean resetLayout = false;
	protected boolean autoRelayout = true;
	protected boolean moveInGroups = true;
	
	public HierarchicalCircleLayoutConfig(Class<? extends HierarchicalCircleLayout> class1){
		super(class1);
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
