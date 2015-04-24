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
	protected boolean relayout = false;
	
	public HierarchicalCircleLayoutConfig(Class<? extends HierarchicalCircleLayout> class1){
		super(class1);
	}
	
	public static double nodeDistance(int groups, int nodes){
		return 2*Math.PI / ((GROUP_DISTANCE_FACTOR - 1)*groups+nodes);
	}
	
	public static double groupDistance(double nodeDistance){
		return (GROUP_DISTANCE_FACTOR-1)*nodeDistance;
	}
	
	public boolean getShowInternalEdges(){
		return showInternalEdges;
	}
	
	public boolean resetLayout(){
		return resetLayout;
	}
	
	public void setResetLayout(boolean reset){
		resetLayout = reset;
	}
	
	public boolean getAutoRelayout(){
		return autoRelayout;
	}
	
	public void setAutoRelayout(boolean auto){
		autoRelayout = auto;
	}
	
	public void setRelayout(boolean rl){
		relayout = rl;
	}
	
	public boolean getRelayout(){
		return (relayout || getAutoRelayout());
	}
	
	public boolean getMoveInGroups(){
		return moveInGroups;
	}
}
