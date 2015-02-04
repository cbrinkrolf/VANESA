package graph.layouts.hebLayout;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import graph.layouts.HierarchicalCircleLayoutConfig;

public class HEBLayoutConfig extends HierarchicalCircleLayoutConfig implements ChangeListener{

	private static final long serialVersionUID = 852123;

	private static HEBLayoutConfig instance;

	/**
	 * Higher value means stronger bundling (100% = equivalent subpaths)
	 */
	public static int EDGE_BUNDLING_PERCENTAGE = 90;

	/**
	 * Grouped by roughest level.
	 */
	public static int ROUGHEST_LEVEL = 1;

	/**
	 * Grouped by finest level.
	 */
	public static int FINEST_LEVEL = 0;
	
	/**
	 * Edge Opacity
	 */
	public static int EDGE_OPACITY = 40;
	
	/**
	 * Edge Color
	 */
	public static int EDGE_COLOR = 0;

	public static JCheckBox showInternalEdges;
	public static JCheckBox resetLayout;
	public static JCheckBox autorelayout;
	public static JCheckBox moveInGroups;
	public static JSlider groupSeperationSlider;
	public static JSlider edgeBundlingSlider;
	public static JSlider groupDepthSlider;
	public static JSlider edgeOpacitySlider;
	public static JSlider edgeColorSlider;

	public HEBLayoutConfig() {
		super(HEBLayout.class);

		GridLayout layout = new GridLayout(0, 2);
		setLayout(layout);

		JPanel preferences = new JPanel();
		BoxLayout edgePreferencesLayout = new BoxLayout(preferences, BoxLayout.PAGE_AXIS);
		preferences.setLayout(edgePreferencesLayout);
		preferences.setBorder(BorderFactory.createTitledBorder("Preferences"));

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
		preferences.add(groupSeperationSlider);

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
		preferences.add(edgeBundlingSlider);

		groupDepthSlider = new JSlider();
		groupDepthSlider.setBorder(BorderFactory
				.createTitledBorder("Grouping"));
		groupDepthSlider.setMinimum(FINEST_LEVEL);
		groupDepthSlider.setMaximum(ROUGHEST_LEVEL);
		groupDepthSlider.setValue(ROUGHEST_LEVEL);
		groupDepthSlider.setMajorTickSpacing(1);
		groupDepthSlider.setPaintTicks(true);
		Hashtable<Integer,JLabel> labelTable = new Hashtable<Integer,JLabel>();
		labelTable.put( new Integer( FINEST_LEVEL ), new JLabel("Finest Level") );
		labelTable.put( new Integer( ROUGHEST_LEVEL ), new JLabel("Roughest Level") );
		groupDepthSlider.setLabelTable( labelTable );
		groupDepthSlider.setPaintLabels(true);
		groupDepthSlider.addChangeListener(this);
		preferences.add(groupDepthSlider);
		
		edgeOpacitySlider = new JSlider();
		edgeOpacitySlider.setBorder(BorderFactory
				.createTitledBorder("Edge Opacity"));
		edgeOpacitySlider.setMinimum(0);
		edgeOpacitySlider.setMaximum(100);
		edgeOpacitySlider.setValue(255/40);
		edgeOpacitySlider.setMajorTickSpacing(20);
		edgeOpacitySlider.setMinorTickSpacing(10);
		edgeOpacitySlider.setPaintTicks(true);
		Hashtable<Integer,JLabel> labels = new Hashtable<Integer,JLabel>();
		labels.put( new Integer(0), new JLabel("0%") );
		labels.put( new Integer(20), new JLabel("20%") );
		labels.put( new Integer(40), new JLabel("40%") );
		labels.put( new Integer(60), new JLabel("60%") );
		labels.put( new Integer(80), new JLabel("80%") );
		labels.put( new Integer(100), new JLabel("100%") );
		edgeOpacitySlider.setLabelTable( labels );
		edgeOpacitySlider.setPaintLabels(true);
		edgeOpacitySlider.addChangeListener(this);
		preferences.add(edgeOpacitySlider);
		
		edgeColorSlider = new JSlider();
		edgeColorSlider.setBorder(BorderFactory
				.createTitledBorder("Edge Color"));
		edgeColorSlider.setMinimum(0);
		edgeColorSlider.setMaximum(240);
		edgeColorSlider.setValue(EDGE_COLOR);
		edgeColorSlider.addChangeListener(this);
		edgeColorSlider.setBackground(getColor(edgeColorSlider.getValue()));
		preferences.add(edgeColorSlider);

		showInternalEdges= new JCheckBox("Show group-internal edges");
		showInternalEdges.setSelected(true);
		preferences.add(showInternalEdges);

		moveInGroups = new JCheckBox("Group selection");
		moveInGroups.setToolTipText("Selects the whole group if a node is selected. So, nodes can only be moved groupwise.");
		moveInGroups.setSelected(true);
		preferences.add(moveInGroups);

		autorelayout = new JCheckBox("Automatical relayout");
		autorelayout.setToolTipText("Relayout automatically when moving, coarsing or flatting nodes.");
		autorelayout.setSelected(true);
		preferences.add(autorelayout);

		resetLayout = new JCheckBox("Reset Layout");
		resetLayout.setSelected(false);
		preferences.add(resetLayout);

		add(preferences);		
	}

	/**
	 * @return instance of the layout config
	 */
	public static HEBLayoutConfig getInstance() {
		if(instance == null){
			instance = new HEBLayoutConfig();
		}
		return instance;
	}

	@Override
	public boolean getShowInternalEdges(){
		return showInternalEdges.isSelected();
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

	@Override
	public void stateChanged(ChangeEvent arg0) {
		if (arg0.getSource().equals(HEBLayoutConfig.groupSeperationSlider)) {
			HEBLayoutConfig.GROUP_DISTANCE_FACTOR = HEBLayoutConfig.groupSeperationSlider.getValue();
		}else if (arg0.getSource().equals(HEBLayoutConfig.groupDepthSlider)) {
			HEBLayoutConfig.GROUP_DEPTH = HEBLayoutConfig.groupDepthSlider.getValue();
		} else if (arg0.getSource().equals(HEBLayoutConfig.edgeBundlingSlider)) {
			HEBLayoutConfig.EDGE_BUNDLING_PERCENTAGE = HEBLayoutConfig.edgeBundlingSlider.getValue();
		} else if (arg0.getSource().equals(HEBLayoutConfig.edgeOpacitySlider)) {
			HEBLayoutConfig.EDGE_OPACITY = HEBLayoutConfig.edgeOpacitySlider.getValue()*255/100;
		}  else if (arg0.getSource().equals(HEBLayoutConfig.edgeColorSlider)) {
			HEBLayoutConfig.EDGE_COLOR = HEBLayoutConfig.edgeColorSlider.getValue();
			edgeColorSlider.setBackground(getColor(edgeColorSlider.getValue()));
		}
	}
	
	public static Color getColor(int i){
		
		if(i==0 || i>240){
			return Color.GRAY;
		}
		
		int red;
		if(i%(240/3)<(240/6)){
			red = 240-i%(240/3)*6;
		} else {
			red = ((i%(240/6))*6);
		}
		
		int green;
		if(i%(240/2)<(240/4)){
			green = i%(240/2)*4;
		} else {
			green = 240-((i%(240/4))*4);
		}
		
		int blue;
		if(i<(240/2)){
			blue = i*2;
		} else {
			blue = 240-((i%(240/2))*2);
		}

		return new Color(red,green,blue);	
	}
}
