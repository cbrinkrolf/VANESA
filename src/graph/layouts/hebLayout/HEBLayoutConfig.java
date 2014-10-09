package graph.layouts.hebLayout;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import configurations.gui.ConfigPanel;
import edu.uci.ics.jung.algorithms.layout.Layout;
import graph.layouts.gemLayout.GEMLayoutConfig;

public class HEBLayoutConfig extends ConfigPanel implements ChangeListener{
	
	private static HEBLayoutConfig instance;

	public static int GROUP_DISTANCE_FACTOR = 5;
	
	public HEBLayoutConfig() {
		super(HEBLayout.class);
		// TODO Auto-generated constructor stub
	}
	
	public static HEBLayoutConfig getInstance() {
		if(instance == null){
			instance = new HEBLayoutConfig();
		}
		return instance;
	}
	
	public static double nodeDistance(int groups, int nodes){
		return 2*Math.PI / ((GROUP_DISTANCE_FACTOR - 1)*groups+nodes);
	}

	@Override
	public void stateChanged(ChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
