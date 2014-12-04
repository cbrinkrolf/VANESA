package graph.layouts.hctLayout;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import graph.layouts.HierarchicalCircleLayoutConfig;

public class HCTLayoutConfig extends HierarchicalCircleLayoutConfig implements ChangeListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 852123;

	private static HCTLayoutConfig instance;

	public static int EDGE_BENDING_PERCENTAGE = 25;
	public static int GROUPINTERNAL_EDGE_BENDING_PERCENTAGE = 25;
	public static int EDGE_BUNDLING_PERCENTAGE = 30;
	
	public static JCheckBox showExternalEdges;
	public static JCheckBox resetLayout;
	public static JCheckBox autorelayout;
	public static JCheckBox moveInGroups;
	public static JSlider groupSeperationSlider;
	
	public HCTLayoutConfig() {
		super(HCTLayout.class);

		GridLayout layout = new GridLayout(0, 2);
		setLayout(layout);
		 
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

		 showExternalEdges= new JCheckBox("Show group-external edges");
		 showExternalEdges.setSelected(true);
		 groupPreferences.add(showExternalEdges);
		 
		resetLayout = new JCheckBox("Reset Layout");
		resetLayout.setSelected(false);
		groupPreferences.add(resetLayout);
		
		autorelayout = new JCheckBox("Automatical relayout");
		autorelayout.setToolTipText("Relayout automatically when moving, coarsing or flatting nodes.");
		autorelayout.setSelected(true);
		groupPreferences.add(autorelayout);
		
		moveInGroups = new JCheckBox("Group selection");
		moveInGroups.setToolTipText("Selects the whole group if a node is selected. So, nodes can only be moved groupwise.");
		moveInGroups.setSelected(true);
		groupPreferences.add(moveInGroups);
	
		 add(groupPreferences);
		
	}
	
	public static HCTLayoutConfig getInstance() {
		if(instance == null){
			instance = new HCTLayoutConfig();
		}
		return instance;
	}
	
	public static double nodeDistance(int groups, int nodes){
		return 2*Math.PI / ((GROUP_DISTANCE_FACTOR - 1)*groups+nodes);
	}
	
	@Override
	public boolean resetLayout(){
		return resetLayout.isSelected();
	}
	
	@Override
	public boolean getAutoRelayout(){
		return autorelayout.isSelected();
	}
	
	@Override
	public boolean getMoveInGroups(){
		return moveInGroups.isSelected();
	}
	
	public boolean getShowExternalEdges() {
		return showExternalEdges.isSelected();
	}

	@Override
	public void stateChanged(ChangeEvent arg0) {
		if (arg0.getSource().equals(HCTLayoutConfig.groupSeperationSlider)) {
			HCTLayoutConfig.GROUP_DISTANCE_FACTOR = HCTLayoutConfig.groupSeperationSlider.getValue();
		}
	}
}
