package graph.layouts.hctLayout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import graph.layouts.ColorSlider;
import graph.layouts.HierarchicalCircleLayoutConfig;

public class HCTLayoutConfig extends HierarchicalCircleLayoutConfig implements ChangeListener, ActionListener {

	private static final long serialVersionUID = 852123;

	private static HCTLayoutConfig instance;

	public static JCheckBox showExternalEdges;
	public static JCheckBox resetLayout;
	public static JCheckBox autorelayout;
	public static JCheckBox moveInGroups;
	public static JSlider groupSeperationSlider;
	public static ColorSlider internalEdgeColorSlider;
	public static ColorSlider externalEdgeColorSlider;
	public static JSlider internalOpacitySlider;
	public static JSlider externalOpacitySlider;
	public static JComboBox<String> groupSelection;
	public static GroupSelection SELECTION;

	public static Color EXTERNAL_EDGE_COLOR = new Color(100, 100, 100);
	public static int EXTERNAL_OPACITY = 255;
	public static Color INTERNAL_EDGE_COLOR = new Color(100, 100, 100);
	public static int INTERNAL_OPACITY = 255;

	public HCTLayoutConfig() {
		super(HCTLayout.class);

		CIRCLE_SIZE = 0.25;
		GridLayout layout = new GridLayout(0, 2);
		setLayout(layout);

		JPanel groupPreferences = new JPanel();
		BoxLayout groupPreferencesLayout = new BoxLayout(groupPreferences, BoxLayout.PAGE_AXIS);
		groupPreferences.setLayout(groupPreferencesLayout);
		groupPreferences.setBorder(BorderFactory.createTitledBorder("Grouping Preferences"));

		JPanel colorPreferences = new JPanel();
		BoxLayout colorPreferencesLayout = new BoxLayout(colorPreferences, BoxLayout.PAGE_AXIS);
		colorPreferences.setLayout(colorPreferencesLayout);
		colorPreferences.setBorder(BorderFactory.createTitledBorder("Color"));

		groupSeperationSlider = new JSlider();
		groupSeperationSlider.setBorder(BorderFactory.createTitledBorder("Group seperation factor"));
		groupSeperationSlider.setMinimum(1);
		groupSeperationSlider.setMaximum(100);
		groupSeperationSlider.setValue(GROUP_DISTANCE_FACTOR);
		groupSeperationSlider.setMajorTickSpacing(20);
		groupSeperationSlider.setMinorTickSpacing(5);
		groupSeperationSlider.setPaintTicks(true);
		Hashtable<Integer, JLabel> sepLabels = new Hashtable<Integer, JLabel>();
		sepLabels.put(1, new JLabel("min"));
		sepLabels.put(100, new JLabel("max"));
		groupSeperationSlider.setLabelTable(sepLabels);
		groupSeperationSlider.setPaintLabels(true);
		groupSeperationSlider.addChangeListener(this);
		groupPreferences.add(groupSeperationSlider);

		JPanel nodeSelection = new JPanel();
		BoxLayout nodeSelectionLayout = new BoxLayout(nodeSelection, BoxLayout.PAGE_AXIS);
		nodeSelection.setLayout(nodeSelectionLayout);
		nodeSelection.setBorder(BorderFactory.createTitledBorder("Node Selection"));

		String[] groupSelectionOptions = { "Single Node", "Node Subpath", "Node Path", "Node Group" };
		groupSelection = new JComboBox<String>(groupSelectionOptions);
		groupSelection.setPreferredSize(new Dimension(200, 30));
		groupSelection.setMaximumSize(groupSelection.getPreferredSize());
		groupSelection.addActionListener(this);
		groupSelection.setSelectedIndex(2);
		nodeSelection.add(groupSelection);

		groupPreferences.add(nodeSelection);

		showExternalEdges = new JCheckBox("Show non-structural edges");
		showExternalEdges.setSelected(true);
		groupPreferences.add(showExternalEdges);

		resetLayout = new JCheckBox("Reset Layout");
		resetLayout.setSelected(false);
		groupPreferences.add(resetLayout);

		autorelayout = new JCheckBox("Automatical relayout");
		autorelayout.setToolTipText("Relayout automatically when moving, coarsing or flatting nodes.");
		autorelayout.setSelected(true);
		groupPreferences.add(autorelayout);

		JPanel internalEdgesPreferences = new JPanel();
		BoxLayout internalEdgesPreferencesLayout = new BoxLayout(internalEdgesPreferences, BoxLayout.PAGE_AXIS);
		internalEdgesPreferences.setLayout(internalEdgesPreferencesLayout);
		internalEdgesPreferences.setBorder(BorderFactory.createTitledBorder("Structural Edges"));

		internalEdgeColorSlider = new ColorSlider("Edge Color", 20);
		internalEdgeColorSlider.addChangeListener(this);
		INTERNAL_EDGE_COLOR = internalEdgeColorSlider.getColor();
		internalEdgesPreferences.add(internalEdgeColorSlider);

		internalOpacitySlider = new JSlider();
		internalOpacitySlider.setBorder(BorderFactory.createTitledBorder("Edge Opacity"));
		internalOpacitySlider.setMinimum(0);
		internalOpacitySlider.setMaximum(100);
		internalOpacitySlider.setValue(100 * INTERNAL_OPACITY / 255);
		internalOpacitySlider.setMajorTickSpacing(20);
		internalOpacitySlider.setMinorTickSpacing(10);
		internalOpacitySlider.setPaintTicks(true);
		Hashtable<Integer, JLabel> labels = new Hashtable<Integer, JLabel>();
		labels.put(0, new JLabel("0%"));
		labels.put(20, new JLabel("20%"));
		labels.put(40, new JLabel("40%"));
		labels.put(60, new JLabel("60%"));
		labels.put(80, new JLabel("80%"));
		labels.put(100, new JLabel("100%"));
		internalOpacitySlider.setLabelTable(labels);
		internalOpacitySlider.setPaintLabels(true);
		internalOpacitySlider.addChangeListener(this);
		internalEdgesPreferences.add(internalOpacitySlider);

		JPanel externalEdgesPreferences = new JPanel();
		BoxLayout externalEdgesPreferencesLayout = new BoxLayout(externalEdgesPreferences, BoxLayout.PAGE_AXIS);
		externalEdgesPreferences.setLayout(externalEdgesPreferencesLayout);
		externalEdgesPreferences.setBorder(BorderFactory.createTitledBorder("Non-Structural Edges"));

		externalEdgeColorSlider = new ColorSlider("Edge Color", 20);
		externalEdgeColorSlider.addChangeListener(this);
		EXTERNAL_EDGE_COLOR = externalEdgeColorSlider.getColor();
		externalEdgesPreferences.add(externalEdgeColorSlider);

		externalOpacitySlider = new JSlider();
		externalOpacitySlider.setBorder(BorderFactory.createTitledBorder("Edge Opacity"));
		externalOpacitySlider.setMinimum(0);
		externalOpacitySlider.setMaximum(100);
		externalOpacitySlider.setValue(100 * EXTERNAL_OPACITY / 255);
		externalOpacitySlider.setMajorTickSpacing(20);
		externalOpacitySlider.setMinorTickSpacing(10);
		externalOpacitySlider.setPaintTicks(true);
		Hashtable<Integer, JLabel> exlabels = new Hashtable<Integer, JLabel>();
		exlabels.put(0, new JLabel("0%"));
		exlabels.put(20, new JLabel("20%"));
		exlabels.put(40, new JLabel("40%"));
		exlabels.put(60, new JLabel("60%"));
		exlabels.put(80, new JLabel("80%"));
		exlabels.put(100, new JLabel("100%"));
		externalOpacitySlider.setLabelTable(exlabels);
		externalOpacitySlider.setPaintLabels(true);
		externalOpacitySlider.addChangeListener(this);
		externalEdgesPreferences.add(externalOpacitySlider);

		colorPreferences.add(internalEdgesPreferences);
		colorPreferences.add(externalEdgesPreferences);

		add(groupPreferences);
		add(colorPreferences);
	}

	public static HCTLayoutConfig getInstance() {
		if (instance == null) {
			instance = new HCTLayoutConfig();
		}
		return instance;
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
		return true;
	}

	public boolean getShowExternalEdges() {
		return showExternalEdges.isSelected();
	}

	@Override
	public void stateChanged(ChangeEvent arg0) {
		if (arg0.getSource().equals(HCTLayoutConfig.groupSeperationSlider)) {
			HCTLayoutConfig.GROUP_DISTANCE_FACTOR = HCTLayoutConfig.groupSeperationSlider.getValue();
		} else if (arg0.getSource().equals(HCTLayoutConfig.internalEdgeColorSlider)) {
			HCTLayoutConfig.INTERNAL_EDGE_COLOR = internalEdgeColorSlider.getColor(INTERNAL_OPACITY);
		} else if (arg0.getSource().equals(HCTLayoutConfig.externalEdgeColorSlider)) {
			HCTLayoutConfig.EXTERNAL_EDGE_COLOR = externalEdgeColorSlider.getColor(EXTERNAL_OPACITY);
		} else if (arg0.getSource().equals(HCTLayoutConfig.externalOpacitySlider)) {
			HCTLayoutConfig.EXTERNAL_OPACITY = HCTLayoutConfig.externalOpacitySlider.getValue() * 255 / 100;
			HCTLayoutConfig.EXTERNAL_EDGE_COLOR = externalEdgeColorSlider.getColor(HCTLayoutConfig.EXTERNAL_OPACITY);
		} else if (arg0.getSource().equals(HCTLayoutConfig.internalOpacitySlider)) {
			HCTLayoutConfig.INTERNAL_OPACITY = HCTLayoutConfig.internalOpacitySlider.getValue() * 255 / 100;
			HCTLayoutConfig.INTERNAL_EDGE_COLOR = internalEdgeColorSlider.getColor(HCTLayoutConfig.INTERNAL_OPACITY);
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource().equals(HCTLayoutConfig.groupSelection)) {
			if (HCTLayoutConfig.groupSelection.getSelectedItem().equals("Single Node")) {
				HCTLayoutConfig.SELECTION = GroupSelection.SINGLE;
			} else if (HCTLayoutConfig.groupSelection.getSelectedItem().equals("Node Path")) {
				HCTLayoutConfig.SELECTION = GroupSelection.PATH;
			} else if (HCTLayoutConfig.groupSelection.getSelectedItem().equals("Node Subpath")) {
				HCTLayoutConfig.SELECTION = GroupSelection.SUBPATH;
			} else if (HCTLayoutConfig.groupSelection.getSelectedItem().equals("Node Group")) {
				HCTLayoutConfig.SELECTION = GroupSelection.ROUGHESTGROUP;
			}
		}
	}

	public enum GroupSelection {
		SINGLE, PATH, SUBPATH, ROUGHESTGROUP
	}
}
