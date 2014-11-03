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
	public static int EDGE_BENDING_FACTOR = 4;
	public static int GROUPINTERNAL_EDGE_BENDING_FACTOR = 4;
	public static int GROUP_DEPTH = 1;
	
	public static JCheckBox showInternalEdges;
	public static JSlider groupSeperationSlider;
	public static JSlider edgeBendingSlider;
	public static JSlider internalEdgeBendingSlider;
	public static JSlider groupDepthSlider;
	
	public HEBLayoutConfig() {
		super(HEBLayout.class);

		GridLayout layout = new GridLayout(0, 2);
		setLayout(layout);
		
		JPanel edgePreferences = new JPanel();
		BoxLayout edgePreferencesLayout = new BoxLayout(edgePreferences, BoxLayout.PAGE_AXIS);
		edgePreferences.setLayout(edgePreferencesLayout);
		edgePreferences.setBorder(BorderFactory.createTitledBorder("Edge Preferences"));
		
		JPanel internalEdgePreferences = new JPanel();
		BoxLayout internalEdgePreferencesLayout = new BoxLayout(internalEdgePreferences, BoxLayout.PAGE_AXIS);
		internalEdgePreferences.setLayout(internalEdgePreferencesLayout);
		internalEdgePreferences.setBorder(BorderFactory.createTitledBorder("Internal Edge Preferences"));
		
		 showInternalEdges= new JCheckBox("Show group-internal edges");
		 showInternalEdges.setSelected(true);
		 internalEdgePreferences.add(showInternalEdges);
		 
		 JPanel groupPreferences = new JPanel();
		 BoxLayout groupPreferencesLayout = new BoxLayout(groupPreferences, BoxLayout.PAGE_AXIS);
		 groupPreferences.setLayout(groupPreferencesLayout);
		 groupPreferences.setBorder(BorderFactory.createTitledBorder("Grouping Preferences"));
		 
		 groupSeperationSlider = new JSlider();
		 groupSeperationSlider.setBorder(BorderFactory
					.createTitledBorder("Group seperation factor"));
		 groupSeperationSlider.setMinimum(1);
		 groupSeperationSlider.setMaximum(20);
		 groupSeperationSlider.setValue(GROUP_DISTANCE_FACTOR);
		 groupSeperationSlider.setMajorTickSpacing(5);
		 groupSeperationSlider.setMinorTickSpacing(2);
		 groupSeperationSlider.setPaintTicks(true);
		 groupSeperationSlider.setPaintLabels(true);
		 groupSeperationSlider.addChangeListener(this);
		 groupPreferences.add(groupSeperationSlider);
		 
		 groupDepthSlider = new JSlider();
		 groupDepthSlider.setBorder(BorderFactory
					.createTitledBorder("Grouping Depth"));
		 groupDepthSlider.setMinimum(0);
		 groupDepthSlider.setMaximum(10);
		 groupDepthSlider.setValue(GROUP_DEPTH);
		 groupDepthSlider.setMajorTickSpacing(5);
		 groupDepthSlider.setMinorTickSpacing(2);
		 groupDepthSlider.setPaintTicks(true);
		 groupDepthSlider.setPaintLabels(true);
		 groupDepthSlider.addChangeListener(this);
		 groupPreferences.add(groupDepthSlider);
			
		edgeBendingSlider = new JSlider();
		edgeBendingSlider.setBorder(BorderFactory
				.createTitledBorder("Edge bending quotient"));
		edgeBendingSlider.setMinimum(1);
		edgeBendingSlider.setMaximum(20);
		edgeBendingSlider.setValue(EDGE_BENDING_FACTOR);
		edgeBendingSlider.setMajorTickSpacing(5);
		edgeBendingSlider.setMinorTickSpacing(2);
		edgeBendingSlider.setPaintTicks(true);
		edgeBendingSlider.setPaintLabels(true);
		edgeBendingSlider.addChangeListener(this);
		edgePreferences.add(edgeBendingSlider);
		
		internalEdgeBendingSlider = new JSlider();
		internalEdgeBendingSlider.setBorder(BorderFactory
				.createTitledBorder("Edge bending quotient (group-internal)"));
		internalEdgeBendingSlider.setMinimum(1);
		internalEdgeBendingSlider.setMaximum(20);
		internalEdgeBendingSlider.setValue(GROUPINTERNAL_EDGE_BENDING_FACTOR);
		internalEdgeBendingSlider.setMajorTickSpacing(5);
		internalEdgeBendingSlider.setMinorTickSpacing(2);
		internalEdgeBendingSlider.setPaintTicks(true);
		internalEdgeBendingSlider.setPaintLabels(true);
		internalEdgeBendingSlider.addChangeListener(this);
		internalEdgePreferences.add(internalEdgeBendingSlider);
		
		edgePreferences.add(internalEdgePreferences);
		 add(edgePreferences);
		 add(groupPreferences);
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
		if (arg0.getSource().equals(HEBLayoutConfig.groupSeperationSlider)) {
			HEBLayoutConfig.GROUP_DISTANCE_FACTOR = HEBLayoutConfig.groupSeperationSlider.getValue();
		} else if (arg0.getSource().equals(HEBLayoutConfig.edgeBendingSlider)) {
			HEBLayoutConfig.EDGE_BENDING_FACTOR = HEBLayoutConfig.edgeBendingSlider.getValue();
		} else if (arg0.getSource().equals(HEBLayoutConfig.internalEdgeBendingSlider)) {
			HEBLayoutConfig.GROUPINTERNAL_EDGE_BENDING_FACTOR = HEBLayoutConfig.internalEdgeBendingSlider.getValue();
		} else if (arg0.getSource().equals(HEBLayoutConfig.groupDepthSlider)) {
			HEBLayoutConfig.GROUP_DEPTH = HEBLayoutConfig.groupDepthSlider.getValue();
		}
	}
}
