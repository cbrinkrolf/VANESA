package graph.layouts.gemLayout;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayPriorityQueue;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.awt.GridLayout;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import biologicalObjects.nodes.BiologicalNodeAbstract;

import cern.colt.list.IntArrayList;
import configurations.gui.ConfigPanel;
//import edu.uci.ics.jung.graph.Vertex;

public class GEMLayoutConfig extends ConfigPanel implements ChangeListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// number of nodes in the graph
	public static int nodeCount;

	//
	// GEM Constants
	//
	public static int ELEN = 128;

	public static int ELENSQR = ELEN * ELEN;

	public static int MAXATTRACT = 1048576;

	//
	// GEM variables
	//
	public static long iteration;

	public static long temperature;

	public static int centerX, centerY;

	public static long maxtemp;

	public static float oscillation, rotation;

	//
	// GEM Default Parameter Values
	//
	public static float i_maxtemp = (float) 1.0;

	public static float a_maxtemp = (float) 1.5;

	public static float o_maxtemp = (float) 0.25;

	public static float i_starttemp = (float) 0.3;

	public static float a_starttemp = (float) 1.0;

	public static float o_starttemp = (float) 1.0;

	public static float i_finaltemp = (float) 0.05;

	public static float a_finaltemp = (float) 0.02;

	public static float o_finaltemp = (float) 1.0;

	public static int i_maxiter = 10;

	public static int a_maxiter = 3;

	public static int o_maxiter = 3;

	public static float i_gravity = (float) 0.05;

	public static float i_oscillation = (float) 0.4;

	public static float i_rotation = (float) 0.5;

	public static float i_shake = (float) 0.2;

	public static float a_gravity = (float) 0.1;

	public static float a_oscillation = (float) 0.4;

	public static float a_rotation = (float) 0.9;

	public static float a_shake = (float) 0.3;

	public static float o_gravity = (float) 0.1;

	public static float o_oscillation = (float) 0.4;

	public static float o_rotation = (float) 0.9;

	public static float o_shake = (float) 0.3;

	// list of properties for each node
	public static GemP gemProp[];

	// inverse map from int id to Vertex
	public static BiologicalNodeAbstract invmap[];

	// adjacent int ids for a given Vertex int id
	public static Int2ObjectOpenHashMap<IntArrayList> adjacent;

	// map from Vertex to int id
	public static Object2IntOpenHashMap<BiologicalNodeAbstract> nodeNumbers;

	// randomizer used for node selection
	public static Random rand = new Random();

	// map used for current random set of nodes
	public static int map[];

	// priority queue for BFS
	public static IntArrayPriorityQueue q;

	// slider for edge length
	public static JSlider sliderElen = null;

	// slider for i_maxiter
	public static JSlider sliderIMaxIter = null;

	// slider for i_gravity
	public static JSlider sliderIGravity = null;

	// slider for i_shake
	public static JSlider sliderIShake = null;

	// slider for a_maxiter
	public static JSlider sliderAMaxIter = null;

	// slider for a_gravity
	public static JSlider sliderAGravity = null;

	// slider for a_shake
	public static JSlider sliderAShake = null;

	// slider for o_maxiter
	public static JSlider sliderOMaxIter = null;

	// slider for o_gravity
	public static JSlider sliderOGravity = null;

	// slider for o_shake
	public static JSlider sliderOShake = null;

	private static GEMLayoutConfig instance;

	/**
	 * Class containing properties per node.
	 * 
	 * @author taubertj
	 * 
	 */
	public class GemP {

		public int x, y; // position

		public int in;

		public int iX, iY; // impulse

		public float dir; // direction

		public float heat; // heat

		public float mass; // weight = nr edges

		public boolean mark;

		public GemP(int m) {
			x = 0;
			y = 0;
			iX = iY = 0;
			dir = (float) 0.0;
			heat = 0;
			mass = m;
			mark = false;
		}
	}

	
	private GEMLayoutConfig() {
		super(GEMLayout.class);
			//BoxLayout layout = new BoxLayout(this, BoxLayout.PAGE_AXIS);
			GridLayout layout = new GridLayout(0, 2);
			setLayout(layout);

			// global edge length
			sliderElen = new JSlider();
			sliderElen.setBorder(BorderFactory
					.createTitledBorder("Preferred edge length"));
			sliderElen.setMinimum(50);
			sliderElen.setMaximum(200);
			sliderElen.setValue(ELEN);
			sliderElen.setMajorTickSpacing(20);
			sliderElen.setMinorTickSpacing(5);
			sliderElen.setPaintTicks(true);
			sliderElen.setPaintLabels(true);
			sliderElen.addChangeListener(this);

			// insertion phase options
			JPanel insert = new JPanel();
			BoxLayout insertLayout = new BoxLayout(insert, BoxLayout.PAGE_AXIS);
			insert.setLayout(insertLayout);
			insert.setBorder(BorderFactory.createTitledBorder("Insert phase"));

			sliderIMaxIter = new JSlider();
			sliderIMaxIter.setBorder(BorderFactory
					.createTitledBorder("max iterations"));
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
			sliderAMaxIter.setBorder(BorderFactory
					.createTitledBorder("max iterations"));
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
			sliderOMaxIter.setBorder(BorderFactory
					.createTitledBorder("max iterations"));
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

			add(sliderElen);
			add(insert);
			add(arrange);
			add(optimize);

	}
	
	public void stateChanged(ChangeEvent arg0) {
		if (arg0.getSource().equals(GEMLayoutConfig.sliderElen)) {
			GEMLayoutConfig.ELEN = GEMLayoutConfig.sliderElen.getValue();
			GEMLayoutConfig.ELENSQR = GEMLayoutConfig.ELEN * GEMLayoutConfig.ELEN;
		} else if (arg0.getSource().equals(GEMLayoutConfig.sliderIMaxIter)) {
			GEMLayoutConfig.i_maxiter = GEMLayoutConfig.sliderIMaxIter.getValue();
		} else if (arg0.getSource().equals(GEMLayoutConfig.sliderAMaxIter)) {
			GEMLayoutConfig.a_maxiter = GEMLayoutConfig.sliderAMaxIter.getValue();
		} else if (arg0.getSource().equals(GEMLayoutConfig.sliderOMaxIter)) {
			GEMLayoutConfig.o_maxiter = GEMLayoutConfig.sliderOMaxIter.getValue();
		} else if (arg0.getSource().equals(GEMLayoutConfig.sliderIGravity)) {
			GEMLayoutConfig.i_gravity = GEMLayoutConfig.sliderIGravity.getValue() / 100f;
		} else if (arg0.getSource().equals(GEMLayoutConfig.sliderAGravity)) {
			GEMLayoutConfig.a_gravity = GEMLayoutConfig.sliderAGravity.getValue() / 100f;
		} else if (arg0.getSource().equals(GEMLayoutConfig.sliderOGravity)) {
			GEMLayoutConfig.o_gravity = GEMLayoutConfig.sliderOGravity.getValue() / 100f;
		} else if (arg0.getSource().equals(GEMLayoutConfig.sliderIShake)) {
			GEMLayoutConfig.i_shake = GEMLayoutConfig.sliderIShake.getValue() / 100f;
		} else if (arg0.getSource().equals(GEMLayoutConfig.sliderAShake)) {
			GEMLayoutConfig.a_shake = GEMLayoutConfig.sliderAShake.getValue() / 100f;
		} else if (arg0.getSource().equals(GEMLayoutConfig.sliderOShake)) {
			GEMLayoutConfig.o_shake = GEMLayoutConfig.sliderOShake.getValue() / 100f;
		}
	}

	public static GEMLayoutConfig getInstance() {
		if(instance == null){
			instance = new GEMLayoutConfig();
		}
		return instance;
	}
}
