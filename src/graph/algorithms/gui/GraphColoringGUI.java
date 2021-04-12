package graph.algorithms.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import biologicalObjects.nodes.BiologicalNodeAbstract;
import graph.GraphInstance;
import graph.algorithms.NetworkProperties;
import graph.algorithms.NodeAttributeNames;
import graph.algorithms.NodeAttributeTypes;
import gui.images.ImagePath;
import net.miginfocom.swing.MigLayout;

public class GraphColoringGUI implements ActionListener {

	private JPanel p = new JPanel();
	boolean emptyPane = true;

	private JComboBox<String> chooseAlgorithm;
	private JComboBox<ImageIcon> chooseColorPalette;
	private JButton colorizebutton;
	private JButton resetcolorbutton;
	private JButton degreedistributionbutton;

	private String[] algorithmNames = { "Node Degree" };

	private int currentalgorithmindex = 0;

	private final int NODE_DEGREE = 0;

	private ImageIcon[] icons;

	private int currentimageid;
	private JCheckBox logview;

	// coloring variables
	private NetworkProperties np;
	private Iterator<BiologicalNodeAbstract> itn;
	private Hashtable<BiologicalNodeAbstract, Double> coloring;
	private BiologicalNodeAbstract bna;

	public GraphColoringGUI() {
		// set icon paths
		ImagePath imagepath = ImagePath.getInstance();
		ImageIcon[] tmpset = { new ImageIcon(imagepath.getPath("icon_colorrange_bluesea.png")),
				new ImageIcon(imagepath.getPath("icon_colorrange_skyline.png")),
				new ImageIcon(imagepath.getPath("icon_colorrange_darkmiddle.png")),
				new ImageIcon(imagepath.getPath("icon_colorrange_dark.png")),
				new ImageIcon(imagepath.getPath("icon_colorrange_rainbow.png")) };
		icons = tmpset;
		currentimageid = 0;

	}

	private void updateWindow() {

		chooseAlgorithm = new JComboBox<>(algorithmNames);
		chooseAlgorithm.setActionCommand("algorithm");
		chooseAlgorithm.addActionListener(this);

		chooseColorPalette = new JComboBox<ImageIcon>(icons);
		chooseColorPalette.setActionCommand("colorpalette");
		chooseColorPalette.addActionListener(this);

		colorizebutton = new JButton("color selection");
		colorizebutton.setActionCommand("colorize");
		colorizebutton.addActionListener(this);

		resetcolorbutton = new JButton("reset colors");
		resetcolorbutton.setActionCommand("resetcolors");
		resetcolorbutton.addActionListener(this);

		MigLayout layout = new MigLayout("", "[][grow]", "");
		p.setLayout(layout);
		p.add(new JLabel("Algorithm"), "wrap");
		p.add(chooseAlgorithm, "wrap");
		p.add(new JLabel("Color Range"), "wrap");
		p.add(chooseColorPalette, "wrap");

		logview = new JCheckBox("Data in log(10)");
		logview.setSelected(false);
		logview.setActionCommand("logview");
		logview.addActionListener(this);

		degreedistributionbutton = new JButton("Degree distribution");
		degreedistributionbutton.setActionCommand("degreedistribution");
		degreedistributionbutton.addActionListener(this);

		p.add(logview, "align left");
		p.add(colorizebutton, "align right, wrap");
		p.add(resetcolorbutton, "align left, wrap");
		p.add(degreedistributionbutton, "span 2, align right, wrap");

	}

	public void recolorGraph() throws IOException {

		np = new NetworkProperties();
		itn = np.getPathway().getAllGraphNodes().iterator();
		coloring = new Hashtable<BiologicalNodeAbstract, Double>();

		switch (currentalgorithmindex) {
		case NODE_DEGREE:
			// get current node degree values
			while (itn.hasNext()) {
				bna = itn.next();
				coloring.put(bna, (double) np.getNodeDegree(np.getNodeAssignment(bna)));
				// saving
				bna.addAttribute(NodeAttributeTypes.GRAPH_PROPERTY, NodeAttributeNames.NODE_DEGREE,
						np.getNodeDegree(np.getNodeAssignment(bna)));

			}
			break;
		}
		new GraphColorizer(coloring, currentimageid, logview.isSelected());
	}

	public void revalidateView() {

		if (emptyPane) {
			updateWindow();
			p.repaint();
			p.revalidate();
			emptyPane = false;
		} else {
			p.removeAll();
			updateWindow();
			p.repaint();
			p.revalidate();
			p.setVisible(true);
		}
	}

	public void removeAllElements() {
		emptyPane = true;
	}

	public JPanel getPanel() {
		p.setVisible(false);
		return p;
	}

	public void actionPerformed(ActionEvent e) {

		try {

			String command = e.getActionCommand();

			if ("colorize".equals(command)) {
				recolorGraph();
				GraphInstance.getMyGraph().getVisualizationViewer().repaint();
			} else if ("resetcolors".equals(command)) {
				GraphInstance.getMyGraph().getAllVertices().stream().forEach(bna -> bna.resetAppearance());
				GraphInstance.getMyGraph().getVisualizationViewer().repaint();

			} else if ("algorithm".equals(command)) {
				currentalgorithmindex = chooseAlgorithm.getSelectedIndex();
				GraphInstance.getMyGraph().getVisualizationViewer().repaint();
			} else if ("logview".equals(command)) {
				recolorGraph();
				GraphInstance.getMyGraph().getVisualizationViewer().repaint();
			} else if ("degreedistribution".equals(command)) {
				NetworkProperties np = new NetworkProperties();
				TreeMap<String, Integer> sorted_map = new TreeMap<String, Integer>(new NumbersThenWordsComparator());

				for (Entry<Integer, Integer> entry : np.getNodeDegreeDistribution().entrySet()) {
					sorted_map.put(entry.getKey() + "", entry.getValue());

				}

				// sort by occurrence
				// ValueComparator bvc = new ValueComparator(degreemap);
				// TreeMap<Integer, Integer> sorted_map = new TreeMap<Integer,
				// Integer>(bvc);
				// sorted_map.putAll(degreemap);

				// sort by key value

				new NodeAttributeBarChart("Statistics", "Node degree distibution", "Degree", "Count", sorted_map);

			} else if ("colorpalette".equals(command)) {
				currentimageid = chooseColorPalette.getSelectedIndex();
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	class NumbersThenWordsComparator implements Comparator<String> {
		private Integer intValue(String s) {
			try {
				return Integer.valueOf(s);
			} catch (NumberFormatException e) {
				return null;
			}
		}

		@Override
		public int compare(String s1, String s2) {
			Integer i1 = intValue(s1);
			Integer i2 = intValue(s2);
			if (i1 == null && i2 == null) {
				return s1.compareTo(s2);
			} else if (i1 == null) {
				return -1;
			} else if (i2 == null) {
				return 1;
			} else {
				return i1.compareTo(i2);
			}
		}
	}
}
