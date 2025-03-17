package graph.layouts.gemLayout;

import java.awt.GridLayout;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import biologicalObjects.nodes.BiologicalNodeAbstract;
import configurations.gui.LayoutConfigPanel;
import graph.operations.layout.gem.GEMLayoutOperation;

public class GEMLayoutConfig extends LayoutConfigPanel implements ChangeListener {
	private static final long serialVersionUID = -2467919660667415938L;
	public static final int MAX_ATTRACT = 1048576;
	private static GEMLayoutConfig config;

	public int nodeCount;
	public int edgeLength = 50;
	public int edgeLengthSquared = edgeLength * edgeLength;
	public long iteration;
	public long temperature;
	public int centerX;
	public int centerY;
	public long maxtemp;
	public float oscillation;
	public float rotation;
	public float i_maxtemp = 1.0f;
	public float a_maxtemp = 1.5f;
	public float o_maxtemp = 0.25f;
	public float i_starttemp = 0.3f;
	public float a_starttemp = 1.0f;
	public float o_starttemp = 1.0f;
	public float i_finaltemp = 0.05f;
	public float a_finaltemp = 0.02f;
	public float o_finaltemp = 1.0f;
	public int i_maxiter = 10;
	public int a_maxiter = 3;
	public int o_maxiter = 3;
	public float i_gravity = 0.05f;
	public float i_oscillation = 0.4f;
	public float i_rotation = 0.5f;
	public float i_shake = 0.2f;
	public float a_gravity = 0.1f;
	public float a_oscillation = 0.4f;
	public float a_rotation = 0.9f;
	public float a_shake = 0.3f;
	public float o_gravity = 0.1f;
	public float o_oscillation = 0.4f;
	public float o_rotation = 0.9f;
	public float o_shake = 0.3f;

	// cut-off values, 0 = no cut-off will be applied
	public int minNodesCutOff = 300;

	// each factorCutOffCheck * |nodes|, the cut-off check is performed
	public int factorCutOffCheck = 3;

	// list of properties for each node
	public GemP[] gemProp;

	// inverse map from int id to Vertex
	public BiologicalNodeAbstract[] invmap;

	// adjacent int ids for a given Vertex int id
	public Map<Integer, List<Integer>> adjacent;

	// map from Vertex to int id
	public Map<BiologicalNodeAbstract, Integer> nodeNumbers;

	// randomizer used for node selection
	public Random rand = new Random();

	// map used for current random set of nodes
	public int[] map;

	// priority queue for BFS
	public PriorityQueue<Integer> q;

	public JSlider sliderEdgeLength;
	public JSlider sliderIMaxIter = null;
	public JSlider sliderIGravity = null;
	public JSlider sliderIShake = null;
	public JSlider sliderAMaxIter = null;
	public JSlider sliderAGravity = null;
	public JSlider sliderAShake = null;
	public JSlider sliderOMaxIter = null;
	public JSlider sliderOGravity = null;
	public JSlider sliderOShake = null;

	public JSlider sliderMinNodesCutOff = null;
	public JSlider sliderFactorCutOffCheck = null;

	private GEMLayoutConfig() {
		super();
		GridLayout layout = new GridLayout(0, 2);
		setLayout(layout);

		// global edge length
		sliderEdgeLength = new JSlider();
		sliderEdgeLength.setBorder(BorderFactory.createTitledBorder("Preferred edge length"));
		sliderEdgeLength.setMinimum(10);
		sliderEdgeLength.setMaximum(200);
		sliderEdgeLength.setValue(edgeLength);
		sliderEdgeLength.setMajorTickSpacing(20);
		sliderEdgeLength.setMinorTickSpacing(5);
		sliderEdgeLength.setPaintTicks(true);
		sliderEdgeLength.setPaintLabels(true);
		sliderEdgeLength.addChangeListener(this);

		sliderMinNodesCutOff = new JSlider();
		sliderMinNodesCutOff.setBorder(BorderFactory.createTitledBorder("Number nodes for cut-off: " + minNodesCutOff));
		sliderMinNodesCutOff.setMinimum(0);
		sliderMinNodesCutOff.setMaximum(1000);
		sliderMinNodesCutOff.setValue(minNodesCutOff);
		sliderMinNodesCutOff.setMajorTickSpacing(200);
		// sliderMinNodesCutOff.setMinorTickSpacing(100);
		sliderMinNodesCutOff.setPaintTicks(true);
		sliderMinNodesCutOff.setPaintLabels(true);
		sliderMinNodesCutOff.addChangeListener(this);
		sliderMinNodesCutOff.setToolTipText("Number nodes for cut-off (0 = no cut-off is applied)");

		sliderFactorCutOffCheck = new JSlider();
		sliderFactorCutOffCheck
				.setBorder(BorderFactory.createTitledBorder("Factor to evaluate cut-off: " + factorCutOffCheck));
		sliderFactorCutOffCheck.setMinimum(0);
		sliderFactorCutOffCheck.setMaximum(25);
		sliderFactorCutOffCheck.setValue(factorCutOffCheck);
		sliderFactorCutOffCheck.setMajorTickSpacing(5);
		// sliderFactorCutOffCheck.setMinorTickSpacing(5);
		sliderFactorCutOffCheck.setPaintTicks(true);
		sliderFactorCutOffCheck.setPaintLabels(true);
		sliderFactorCutOffCheck.addChangeListener(this);
		sliderFactorCutOffCheck
				.setToolTipText("Factor how frequent cut-off criteria is checked (0 = no cut-off is applied)");

		// insertion phase options
		JPanel insert = new JPanel();
		BoxLayout insertLayout = new BoxLayout(insert, BoxLayout.PAGE_AXIS);
		insert.setLayout(insertLayout);
		insert.setBorder(BorderFactory.createTitledBorder("Insert phase"));

		sliderIMaxIter = new JSlider();
		sliderIMaxIter.setBorder(BorderFactory.createTitledBorder("max iterations"));
		sliderIMaxIter.setMinimum(0);
		sliderIMaxIter.setMaximum(20);
		sliderIMaxIter.setValue(i_maxiter);
		sliderIMaxIter.setMajorTickSpacing(5);
		sliderIMaxIter.setMinorTickSpacing(1);
		sliderIMaxIter.setPaintTicks(true);
		sliderIMaxIter.setPaintLabels(true);
		sliderIMaxIter.addChangeListener(this);
		insert.add(sliderIMaxIter);

		sliderIGravity = new JSlider();
		sliderIGravity.setBorder(BorderFactory.createTitledBorder("gravity"));
		sliderIGravity.setMinimum(0);
		sliderIGravity.setMaximum(100);
		sliderIGravity.setValue((int) (i_gravity * 100));
		sliderIGravity.setMajorTickSpacing(20);
		sliderIGravity.setMinorTickSpacing(5);
		sliderIGravity.setPaintTicks(true);
		sliderIGravity.setPaintLabels(true);
		sliderIGravity.addChangeListener(this);
		insert.add(sliderIGravity);

		sliderIShake = new JSlider();
		sliderIShake.setBorder(BorderFactory.createTitledBorder("shake"));
		sliderIShake.setMinimum(0);
		sliderIShake.setMaximum(100);
		sliderIShake.setValue((int) (i_shake * 100));
		sliderIShake.setMajorTickSpacing(20);
		sliderIShake.setMinorTickSpacing(5);
		sliderIShake.setPaintTicks(true);
		sliderIShake.setPaintLabels(true);
		sliderIShake.addChangeListener(this);
		insert.add(sliderIShake);

		// arrange phase options
		JPanel arrange = new JPanel();
		BoxLayout arrangeLayout = new BoxLayout(arrange, BoxLayout.PAGE_AXIS);
		arrange.setLayout(arrangeLayout);
		arrange.setBorder(BorderFactory.createTitledBorder("Arrange phase"));

		sliderAMaxIter = new JSlider();
		sliderAMaxIter.setBorder(BorderFactory.createTitledBorder("max iterations"));
		sliderAMaxIter.setMinimum(0);
		sliderAMaxIter.setMaximum(20);
		sliderAMaxIter.setValue(a_maxiter);
		sliderAMaxIter.setMajorTickSpacing(5);
		sliderAMaxIter.setMinorTickSpacing(1);
		sliderAMaxIter.setPaintTicks(true);
		sliderAMaxIter.setPaintLabels(true);
		sliderAMaxIter.addChangeListener(this);
		arrange.add(sliderAMaxIter);

		sliderAGravity = new JSlider();
		sliderAGravity.setBorder(BorderFactory.createTitledBorder("gravity"));
		sliderAGravity.setMinimum(0);
		sliderAGravity.setMaximum(100);
		sliderAGravity.setValue((int) (a_gravity * 100));
		sliderAGravity.setMajorTickSpacing(20);
		sliderAGravity.setMinorTickSpacing(5);
		sliderAGravity.setPaintTicks(true);
		sliderAGravity.setPaintLabels(true);
		sliderAGravity.addChangeListener(this);
		arrange.add(sliderAGravity);

		sliderAShake = new JSlider();
		sliderAShake.setBorder(BorderFactory.createTitledBorder("shake"));
		sliderAShake.setMinimum(0);
		sliderAShake.setMaximum(100);
		sliderAShake.setValue((int) (a_shake * 100));
		sliderAShake.setMajorTickSpacing(20);
		sliderAShake.setMinorTickSpacing(5);
		sliderAShake.setPaintTicks(true);
		sliderAShake.setPaintLabels(true);
		sliderAShake.addChangeListener(this);
		arrange.add(sliderAShake);

		// optimize phase options
		JPanel optimize = new JPanel();
		BoxLayout optimizeLayout = new BoxLayout(optimize, BoxLayout.PAGE_AXIS);
		optimize.setLayout(optimizeLayout);
		optimize.setBorder(BorderFactory.createTitledBorder("Optimize phase"));

		sliderOMaxIter = new JSlider();
		sliderOMaxIter.setBorder(BorderFactory.createTitledBorder("max iterations"));
		sliderOMaxIter.setMinimum(0);
		sliderOMaxIter.setMaximum(20);
		sliderOMaxIter.setValue(o_maxiter);
		sliderOMaxIter.setMajorTickSpacing(5);
		sliderOMaxIter.setMinorTickSpacing(1);
		sliderOMaxIter.setPaintTicks(true);
		sliderOMaxIter.setPaintLabels(true);
		sliderOMaxIter.addChangeListener(this);
		optimize.add(sliderOMaxIter);

		sliderOGravity = new JSlider();
		sliderOGravity.setBorder(BorderFactory.createTitledBorder("gravity"));
		sliderOGravity.setMinimum(0);
		sliderOGravity.setMaximum(100);
		sliderOGravity.setValue((int) (o_gravity * 100));
		sliderOGravity.setMajorTickSpacing(20);
		sliderOGravity.setMinorTickSpacing(5);
		sliderOGravity.setPaintTicks(true);
		sliderOGravity.setPaintLabels(true);
		sliderOGravity.addChangeListener(this);
		optimize.add(sliderOGravity);

		sliderOShake = new JSlider();
		sliderOShake.setBorder(BorderFactory.createTitledBorder("shake"));
		sliderOShake.setMinimum(0);
		sliderOShake.setMaximum(100);
		sliderOShake.setValue((int) (o_shake * 100));
		sliderOShake.setMajorTickSpacing(20);
		sliderOShake.setMinorTickSpacing(5);
		sliderOShake.setPaintTicks(true);
		sliderOShake.setPaintLabels(true);
		sliderOShake.addChangeListener(this);
		optimize.add(sliderOShake);

		JPanel main = new JPanel();
		BoxLayout mainLayout = new BoxLayout(main, BoxLayout.PAGE_AXIS);
		main.setLayout(mainLayout);
		main.setBorder(BorderFactory.createTitledBorder("General properties"));

		main.add(sliderEdgeLength);
		main.add(sliderMinNodesCutOff);
		main.add(sliderFactorCutOffCheck);

		add(main);
		add(insert);
		add(arrange);
		add(optimize);
	}

	public static GEMLayoutConfig getInstance() {
		if (config == null) {
			config = new GEMLayoutConfig();
		}
		return config;
	}

	public void stateChanged(ChangeEvent arg0) {
		if (arg0.getSource().equals(sliderEdgeLength)) {
			edgeLength = sliderEdgeLength.getValue();
			edgeLengthSquared = edgeLength * edgeLength;
		} else if (arg0.getSource().equals(sliderIMaxIter)) {
			i_maxiter = sliderIMaxIter.getValue();
		} else if (arg0.getSource().equals(sliderAMaxIter)) {
			a_maxiter = sliderAMaxIter.getValue();
		} else if (arg0.getSource().equals(sliderOMaxIter)) {
			o_maxiter = sliderOMaxIter.getValue();
		} else if (arg0.getSource().equals(sliderIGravity)) {
			i_gravity = sliderIGravity.getValue() * 0.01f;
		} else if (arg0.getSource().equals(sliderAGravity)) {
			a_gravity = sliderAGravity.getValue() * 0.01f;
		} else if (arg0.getSource().equals(sliderOGravity)) {
			o_gravity = sliderOGravity.getValue() * 0.01f;
		} else if (arg0.getSource().equals(sliderIShake)) {
			i_shake = sliderIShake.getValue() * 0.01f;
		} else if (arg0.getSource().equals(sliderAShake)) {
			a_shake = sliderAShake.getValue() * 0.01f;
		} else if (arg0.getSource().equals(sliderOShake)) {
			o_shake = sliderOShake.getValue() * 0.01f;
		} else if (arg0.getSource().equals(sliderMinNodesCutOff)) {
			minNodesCutOff = sliderMinNodesCutOff.getValue();
			((TitledBorder) sliderMinNodesCutOff.getBorder())
					.setTitle("Number nodes for cut-off: " + sliderMinNodesCutOff.getValue());
		} else if (arg0.getSource().equals(sliderFactorCutOffCheck)) {
			factorCutOffCheck = sliderFactorCutOffCheck.getValue();
			((TitledBorder) sliderFactorCutOffCheck.getBorder())
					.setTitle("Factor to evaluate cut-off: " + sliderFactorCutOffCheck.getValue());
		}
	}

	@Override
	protected void applySettings() {
		getGraph().apply(new GEMLayoutOperation());
	}
}
