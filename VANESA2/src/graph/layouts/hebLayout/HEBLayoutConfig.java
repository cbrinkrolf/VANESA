package graph.layouts.hebLayout;

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
	public static int EDGE_BUNDLING_PERCENTAGE = 80;

	/**
	 * Grouped by roughest level.
	 */
	public static int ROUGHEST_LEVEL = 1;

	/**
	 * Grouped by finest level.
	 */
	public static int FINEST_LEVEL = 0;

	public static JCheckBox showInternalEdges;
	public static JCheckBox resetLayout;
	public static JCheckBox autorelayout;
	public static JCheckBox moveInGroups;
	public static JSlider groupSeperationSlider;
	public static JSlider internalEdgeBendingSlider;
	public static JSlider edgeBundlingSlider;
	public static JSlider groupDepthSlider;

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
		}
	}
}
