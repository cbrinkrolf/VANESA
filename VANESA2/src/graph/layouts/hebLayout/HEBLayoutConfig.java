package graph.layouts.hebLayout;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import configurations.gui.ConfigPanel;
import edu.uci.ics.jung.algorithms.layout.Layout;
import graph.layouts.gemLayout.GEMLayoutConfig;

public class HEBLayoutConfig extends ConfigPanel implements ChangeListener{
	
	private static HEBLayoutConfig instance;

	public static int GROUP_DISTANCE_FACTOR = 5;
	
	public static JCheckBox showInternalEdges;
	
	public HEBLayoutConfig() {
		super(HEBLayout.class);

		GridLayout layout = new GridLayout(0, 2);
		setLayout(layout);
		
		JPanel edgePreferences = new JPanel();
		BoxLayout edgePreferencesLayout = new BoxLayout(edgePreferences, BoxLayout.PAGE_AXIS);
		edgePreferences.setLayout(edgePreferencesLayout);
		edgePreferences.setBorder(BorderFactory.createTitledBorder("Edge Preferences"));
		
		 showInternalEdges= new JCheckBox("Show group-internal edges");
		 showInternalEdges.setSelected(true);
		 edgePreferences.add(showInternalEdges);
		 
		 
		 add(edgePreferences);
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
	
	public boolean getShowInternalEdges(){
		return showInternalEdges.isSelected();
	}

	@Override
	public void stateChanged(ChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
