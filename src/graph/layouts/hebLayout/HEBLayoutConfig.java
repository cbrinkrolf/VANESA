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
	public static int EDGE_BENDING_PERCENTAGE = 25;
	public static int GROUPINTERNAL_EDGE_BENDING_PERCENTAGE = 25;
	public static int EDGE_BUNDLING_PERCENTAGE = 30;
	public static int GROUP_DEPTH = 1;
	
	public static JCheckBox showInternalEdges;
	public static JSlider groupSeperationSlider;
	public static JSlider edgeBendingSlider;
	public static JSlider internalEdgeBendingSlider;
	public static JSlider edgeBundlingSlider;
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
		 groupSeperationSlider.setMaximum(100);
		 groupSeperationSlider.setValue(GROUP_DISTANCE_FACTOR);
		 groupSeperationSlider.setMajorTickSpacing(20);
		 groupSeperationSlider.setMinorTickSpacing(5);
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
				.createTitledBorder("Edge bending percentage"));
		edgeBendingSlider.setMinimum(0);
		edgeBendingSlider.setMaximum(100);
		edgeBendingSlider.setValue(EDGE_BENDING_PERCENTAGE);
		edgeBendingSlider.setMajorTickSpacing(20);
		edgeBendingSlider.setMinorTickSpacing(5);
		edgeBendingSlider.setPaintTicks(true);
		edgeBendingSlider.setPaintLabels(true);
		edgeBendingSlider.addChangeListener(this);
		edgePreferences.add(edgeBendingSlider);
		
		edgeBundlingSlider = new JSlider();
		edgeBundlingSlider.setBorder(BorderFactory
				.createTitledBorder("Edge bundling percentage"));
		edgeBundlingSlider.setMinimum(0);
		edgeBundlingSlider.setMaximum(100);
		edgeBundlingSlider.setValue(EDGE_BUNDLING_PERCENTAGE);
		edgeBundlingSlider.setMajorTickSpacing(20);
		edgeBundlingSlider.setMinorTickSpacing(5);
		edgeBundlingSlider.setPaintTicks(true);
		edgeBundlingSlider.setPaintLabels(true);
		edgeBundlingSlider.addChangeListener(this);
		edgePreferences.add(edgeBundlingSlider);
		
		internalEdgeBendingSlider = new JSlider();
		internalEdgeBendingSlider.setBorder(BorderFactory
				.createTitledBorder("Edge bending percentage (group-internal)"));
		internalEdgeBendingSlider.setMinimum(-50);
		internalEdgeBendingSlider.setMaximum(50);
		internalEdgeBendingSlider.setValue(GROUPINTERNAL_EDGE_BENDING_PERCENTAGE);
		internalEdgeBendingSlider.setMajorTickSpacing(10);
		internalEdgeBendingSlider.setMinorTickSpacing(5);
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
			HEBLayoutConfig.EDGE_BENDING_PERCENTAGE = HEBLayoutConfig.edgeBendingSlider.getValue();
		} else if (arg0.getSource().equals(HEBLayoutConfig.internalEdgeBendingSlider)) {
			HEBLayoutConfig.GROUPINTERNAL_EDGE_BENDING_PERCENTAGE = HEBLayoutConfig.internalEdgeBendingSlider.getValue();
		} else if (arg0.getSource().equals(HEBLayoutConfig.groupDepthSlider)) {
			HEBLayoutConfig.GROUP_DEPTH = HEBLayoutConfig.groupDepthSlider.getValue();
		} else if (arg0.getSource().equals(HEBLayoutConfig.edgeBundlingSlider)) {
			HEBLayoutConfig.EDGE_BUNDLING_PERCENTAGE = HEBLayoutConfig.edgeBundlingSlider.getValue();
		}
	}
}
