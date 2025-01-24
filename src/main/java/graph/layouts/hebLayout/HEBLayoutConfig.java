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

import graph.layouts.ColorSlider;
import graph.layouts.HierarchicalCircleLayoutConfig;

public class HEBLayoutConfig extends HierarchicalCircleLayoutConfig implements ChangeListener {
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
	public static int EDGE_OPACITY = 80;

	/**
	 * Edge Color
	 */
	public static Color EDGE_INCOLOR = new Color(100, 100, 100, EDGE_OPACITY);
	public static Color EDGE_OUTCOLOR = new Color(100, 100, 100, EDGE_OPACITY);

	public static JCheckBox showInternalEdges;
	public static JCheckBox resetLayout;
	public static JCheckBox autorelayout;
	public static JCheckBox moveInGroups;
	public static JCheckBox edgeColorGradient;
	public static JSlider groupSeperationSlider;
	public static JSlider edgeBundlingSlider;
	public static JSlider groupDepthSlider;
	public static JSlider edgeOpacitySlider;
	public static ColorSlider edgeInColorSlider;
	public static ColorSlider edgeOutColorSlider;

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
		groupSeperationSlider.setBorder(BorderFactory.createTitledBorder("Group seperation"));
		groupSeperationSlider.setMinimum(1);
		groupSeperationSlider.setMaximum(100);
		groupSeperationSlider.setValue(GROUP_DISTANCE_FACTOR);
		groupSeperationSlider.setMajorTickSpacing(20);
		groupSeperationSlider.setMinorTickSpacing(5);
		groupSeperationSlider.setPaintTicks(true);
		Hashtable<Integer, JLabel> sepLabels = new Hashtable<>();
		sepLabels.put(1, new JLabel("min"));
		sepLabels.put(100, new JLabel("max"));
		groupSeperationSlider.setLabelTable(sepLabels);
		groupSeperationSlider.setPaintLabels(true);
		groupSeperationSlider.addChangeListener(this);
		preferences.add(groupSeperationSlider);

		edgeBundlingSlider = new JSlider();
		edgeBundlingSlider.setBorder(BorderFactory.createTitledBorder("Edge bundling percentage"));
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
		groupDepthSlider.setBorder(BorderFactory.createTitledBorder("Grouping"));
		groupDepthSlider.setMinimum(FINEST_LEVEL);
		groupDepthSlider.setMaximum(ROUGHEST_LEVEL);
		groupDepthSlider.setValue(ROUGHEST_LEVEL);
		groupDepthSlider.setMajorTickSpacing(1);
		groupDepthSlider.setPaintTicks(true);
		Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
		labelTable.put(FINEST_LEVEL, new JLabel("Finest Level"));
		labelTable.put(ROUGHEST_LEVEL, new JLabel("Roughest Level"));
		groupDepthSlider.setLabelTable(labelTable);
		groupDepthSlider.setPaintLabels(true);
		groupDepthSlider.addChangeListener(this);
		preferences.add(groupDepthSlider);

		edgeOpacitySlider = new JSlider();
		edgeOpacitySlider.setBorder(BorderFactory.createTitledBorder("Edge Opacity"));
		edgeOpacitySlider.setMinimum(0);
		edgeOpacitySlider.setMaximum(100);
		edgeOpacitySlider.setValue(100 * EDGE_OPACITY / 255);
		edgeOpacitySlider.setMajorTickSpacing(20);
		edgeOpacitySlider.setMinorTickSpacing(10);
		edgeOpacitySlider.setPaintTicks(true);
		Hashtable<Integer, JLabel> labels = new Hashtable<>();
		labels.put(0, new JLabel("0%"));
		labels.put(20, new JLabel("20%"));
		labels.put(40, new JLabel("40%"));
		labels.put(60, new JLabel("60%"));
		labels.put(80, new JLabel("80%"));
		labels.put(100, new JLabel("100%"));
		edgeOpacitySlider.setLabelTable(labels);
		edgeOpacitySlider.setPaintLabels(true);
		edgeOpacitySlider.addChangeListener(this);
		colorPreferences.add(edgeOpacitySlider);

		edgeOutColorSlider = new ColorSlider("Outgoing Edge Color", 20);
		edgeOutColorSlider.addChangeListener(this);
		EDGE_OUTCOLOR = edgeOutColorSlider.getColor(EDGE_OPACITY);
		colorPreferences.add(edgeOutColorSlider);

		edgeColorGradient = new JCheckBox("Edge color gradient");
		edgeColorGradient.setSelected(false);
		edgeColorGradient.addChangeListener(this);
		colorPreferences.add(edgeColorGradient);

		edgeInColorSlider = new ColorSlider("Incoming Edge Color", 20);
		edgeInColorSlider.addChangeListener(this);
		edgeInColorSlider.setEnabled(false);
		EDGE_INCOLOR = edgeInColorSlider.getColor(EDGE_OPACITY);
		colorPreferences.add(edgeInColorSlider);

		showInternalEdges = new JCheckBox("Show group-internal edges");
		showInternalEdges.setSelected(true);
		preferences.add(showInternalEdges);

		moveInGroups = new JCheckBox("Group selection");
		moveInGroups.setToolTipText(
				"Selects the whole group if a node is selected. So, nodes can only be moved groupwise.");
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
		if (instance == null) {
			instance = new HEBLayoutConfig();
		}
		return instance;
	}

	@Override
	public boolean getShowInternalEdges() {
		return showInternalEdges.isSelected();
	}

	@Override
	public boolean resetLayout() {
		return resetLayout.isSelected();
	}

	@Override
	public void setResetLayout(boolean reset) {
		resetLayout.setSelected(reset);
	}

	@Override
	public boolean getAutoRelayout() {
		return autorelayout.isSelected();
	}

	@Override
	public void setAutoRelayout(boolean auto) {
		autorelayout.setSelected(auto);
	}

	@Override
	public boolean getMoveInGroups() {
		return moveInGroups.isSelected();
	}

	@Override
	public void stateChanged(ChangeEvent arg0) {
		if (arg0.getSource().equals(HEBLayoutConfig.groupSeperationSlider)) {
			HEBLayoutConfig.GROUP_DISTANCE_FACTOR = HEBLayoutConfig.groupSeperationSlider.getValue();
		} else if (arg0.getSource().equals(HEBLayoutConfig.groupDepthSlider)) {
			HEBLayoutConfig.GROUP_DEPTH = HEBLayoutConfig.groupDepthSlider.getValue();
		} else if (arg0.getSource().equals(HEBLayoutConfig.edgeBundlingSlider)) {
			HEBLayoutConfig.EDGE_BUNDLING_PERCENTAGE = HEBLayoutConfig.edgeBundlingSlider.getValue();
		} else if (arg0.getSource().equals(HEBLayoutConfig.edgeOpacitySlider)) {
			HEBLayoutConfig.EDGE_OPACITY = HEBLayoutConfig.edgeOpacitySlider.getValue() * 255 / 100;
			HEBLayoutConfig.EDGE_INCOLOR = edgeInColorSlider.getColor(HEBLayoutConfig.EDGE_OPACITY);
			HEBLayoutConfig.EDGE_OUTCOLOR = edgeOutColorSlider.getColor(HEBLayoutConfig.EDGE_OPACITY);
		} else if (arg0.getSource().equals(HEBLayoutConfig.edgeInColorSlider)) {
			HEBLayoutConfig.EDGE_INCOLOR = HEBLayoutConfig.edgeInColorSlider.getColor(HEBLayoutConfig.EDGE_OPACITY);
		} else if (arg0.getSource().equals(HEBLayoutConfig.edgeOutColorSlider)) {
			HEBLayoutConfig.EDGE_OUTCOLOR = HEBLayoutConfig.edgeOutColorSlider.getColor(HEBLayoutConfig.EDGE_OPACITY);
			if (!edgeColorGradient.isSelected()) {
				HEBLayoutConfig.edgeInColorSlider.setValue(edgeOutColorSlider.getValue());
			}
		} else if (arg0.getSource().equals(HEBLayoutConfig.edgeColorGradient)) {
			if (edgeColorGradient.isSelected()) {
				edgeInColorSlider.setEnabled(true);
			} else {
				edgeInColorSlider.setValue(edgeOutColorSlider.getValue());
				edgeInColorSlider.setEnabled(false);
			}
		}
	}
}
