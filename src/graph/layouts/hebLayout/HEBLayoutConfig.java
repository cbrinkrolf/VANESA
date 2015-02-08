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
	public static int EDGE_INCOLOR = Color.GRAY.getRed()/8;
	public static int EDGE_OUTCOLOR = Color.GRAY.getRed()/8;

	public static JCheckBox showInternalEdges;
	public static JCheckBox resetLayout;
	public static JCheckBox autorelayout;
	public static JCheckBox moveInGroups;
	public static JCheckBox edgeColorGradient;
	public static JSlider groupSeperationSlider;
	public static JSlider edgeBundlingSlider;
	public static JSlider groupDepthSlider;
	public static JSlider edgeOpacitySlider;
	public static JSlider edgeInColorSlider;
	public static JSlider edgeOutColorSlider;


	public HEBLayoutConfig() {
		super(HEBLayout.class);

		GridLayout layout = new GridLayout(0, 2);
		setLayout(layout);

		JPanel preferences = new JPanel();
		BoxLayout edgePreferencesLayout = new BoxLayout(preferences, BoxLayout.PAGE_AXIS);
		preferences.setLayout(edgePreferencesLayout);
		preferences.setBorder(BorderFactory.createTitledBorder("Preferences"));
		
		JPanel colorPreferences = new JPanel();
		BoxLayout colorPreferencesLayout = new BoxLayout(colorPreferences, BoxLayout.PAGE_AXIS);
		colorPreferences.setLayout(colorPreferencesLayout);
		colorPreferences.setBorder(BorderFactory.createTitledBorder("Color"));

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
		colorPreferences.add(edgeOpacitySlider);
		
		edgeOutColorSlider = new JSlider();
		edgeOutColorSlider.setBorder(BorderFactory
				.createTitledBorder("Outgoing Edge Color"));
		edgeOutColorSlider.setMinimum(0);
		edgeOutColorSlider.setMaximum(240);
		edgeOutColorSlider.setValue(EDGE_OUTCOLOR);
		edgeOutColorSlider.addChangeListener(this);
		edgeOutColorSlider.setBackground(getColor(edgeOutColorSlider.getValue()));
		colorPreferences.add(edgeOutColorSlider);
		
		edgeColorGradient = new JCheckBox("Edge color gradient");
		edgeColorGradient.setSelected(false);
		edgeColorGradient.addChangeListener(this);
		colorPreferences.add(edgeColorGradient);
		
		edgeInColorSlider = new JSlider();
		edgeInColorSlider.setBorder(BorderFactory
				.createTitledBorder("Incoming Edge Color"));
		edgeInColorSlider.setMinimum(0);
		edgeInColorSlider.setMaximum(240);
		edgeInColorSlider.setValue(EDGE_OUTCOLOR);
		edgeInColorSlider.addChangeListener(this);
		edgeInColorSlider.setBackground(getColor(edgeOutColorSlider.getValue()));
		edgeInColorSlider.setEnabled(false);
		colorPreferences.add(edgeInColorSlider);

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
		add(colorPreferences);
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
		} else if (arg0.getSource().equals(HEBLayoutConfig.edgeInColorSlider)) {
			HEBLayoutConfig.EDGE_INCOLOR = HEBLayoutConfig.edgeInColorSlider.getValue();
			edgeInColorSlider.setBackground(getColor(edgeInColorSlider.getValue()));
		} else if (arg0.getSource().equals(HEBLayoutConfig.edgeOutColorSlider)) {
			HEBLayoutConfig.EDGE_OUTCOLOR = HEBLayoutConfig.edgeOutColorSlider.getValue();
			edgeOutColorSlider.setBackground(getColor(edgeOutColorSlider.getValue()));
			if(!edgeColorGradient.isSelected()){
				HEBLayoutConfig.edgeInColorSlider.setValue(edgeOutColorSlider.getValue());
			}
		} else if (arg0.getSource().equals(HEBLayoutConfig.edgeColorGradient)){
			if(edgeColorGradient.isSelected()){
				edgeInColorSlider.setEnabled(true);
			} else {
				edgeInColorSlider.setValue(edgeOutColorSlider.getValue());
				edgeInColorSlider.setEnabled(false);
			}
		}
	}
	
	public static Color getInColor(){
		return getColor(EDGE_OUTCOLOR);
	}
	
	public static Color getOutColor(){
		return getColor(EDGE_INCOLOR);
	}
	
	private static Color getColor(int i){
		
		if(i<0){
			return Color.GRAY;
		}
		
		int red;
		
		int green;
		
		int blue;

		// grayscale
		if(i<=30){
			red = 240-i*8;
			blue = 240-i*8;
			green = 240-i*8;
		// colorscale
		} else if(i<=60){
			red = (i-30)*8;
			blue = 0;
			green = 0;
		} else if(i<=90){
			red = 240;
			green = (i-60)*8;
			blue = 0;
		} else if(i<=120){
			red = 240-(i-90)*8;
			green = 240;
			blue = 0;
		} else if(i<=150){
			red = 0;
			green = 240;
			blue = (i-120)*8;
		} else if(i<=180){
			red = 0;
			green = 240-(i-150)*8;
			blue = 240;
		} else if(i<=210){
			red = (i-180)*8;
			green = 0;
			blue = 240;
		} else if(i<=240){
			red = 240;
			green = 0;
			blue = 240-(i-210)*8;
		} else {
			return Color.GRAY;
		}
		

		return new Color(red,green,blue,EDGE_OPACITY);	
	}
}
