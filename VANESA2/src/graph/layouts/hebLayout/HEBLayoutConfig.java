package graph.layouts.hebLayout;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import configurations.gui.ConfigPanel;
import edu.uci.ics.jung.algorithms.layout.Layout;
import graph.layouts.gemLayout.GEMLayoutConfig;

public class HEBLayoutConfig extends ConfigPanel implements ChangeListener{
	
	public static int GROUP_DISTANCE_FACTOR = 3;
	
	public HEBLayoutConfig() {
		super(HEBLayout.class);
		// TODO Auto-generated constructor stub
	}

	private static HEBLayoutConfig instance;
	
	public static HEBLayoutConfig getInstance() {
		if(instance == null){
			instance = new HEBLayoutConfig();
		}
		return instance;
	}

	@Override
	public void stateChanged(ChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
