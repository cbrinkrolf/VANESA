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
import gui.ImagePath;
import net.miginfocom.swing.MigLayout;

public class GraphColoringGUI implements ActionListener {
    private final JPanel panel = new JPanel();
    boolean emptyPane = true;
    private JComboBox<String> chooseAlgorithm;
    private JComboBox<ImageIcon> chooseColorPalette;
    private final String[] algorithmNames = {"Node Degree"};
    private int currentAlgorithmIndex = 0;
    private final ImageIcon[] icons;
    private int currentImageId;
    private JCheckBox logView;

    public GraphColoringGUI() {
        icons = new ImageIcon[]{
                ImagePath.getInstance().getImageIcon("icon_colorrange_bluesea.png"),
                ImagePath.getInstance().getImageIcon("icon_colorrange_skyline.png"),
                ImagePath.getInstance().getImageIcon("icon_colorrange_darkmiddle.png"),
                ImagePath.getInstance().getImageIcon("icon_colorrange_dark.png"), ImagePath.getInstance().getImageIcon(
                "icon_colorrange_rainbow.png")
        };
        currentImageId = 0;
    }

    private void updateWindow() {
        chooseAlgorithm = new JComboBox<>(algorithmNames);
        chooseAlgorithm.setActionCommand("algorithm");
        chooseAlgorithm.addActionListener(this);

        chooseColorPalette = new JComboBox<>(icons);
        chooseColorPalette.setActionCommand("colorpalette");
        chooseColorPalette.addActionListener(this);

        JButton colorizebutton = new JButton("color selection");
        colorizebutton.setActionCommand("colorize");
        colorizebutton.addActionListener(this);

        JButton resetcolorbutton = new JButton("reset colors");
        resetcolorbutton.setActionCommand("resetcolors");
        resetcolorbutton.addActionListener(this);

        MigLayout layout = new MigLayout("", "[][grow]", "");
        panel.setLayout(layout);
        panel.add(new JLabel("Algorithm"), "wrap");
        panel.add(chooseAlgorithm, "wrap");
        panel.add(new JLabel("Color Range"), "wrap");
        panel.add(chooseColorPalette, "wrap");

        logView = new JCheckBox("Data in log(10)");
        logView.setSelected(false);
        logView.setActionCommand("logview");
        logView.addActionListener(this);

        JButton degreedistributionbutton = new JButton("Degree distribution");
        degreedistributionbutton.setActionCommand("degreedistribution");
        degreedistributionbutton.addActionListener(this);

        panel.add(logView, "align left");
        panel.add(colorizebutton, "align right, wrap");
        panel.add(resetcolorbutton, "align left, wrap");
        panel.add(degreedistributionbutton, "span 2, align right, wrap");
    }

    public void recolorGraph() throws IOException {
        // coloring variables
        NetworkProperties np = new NetworkProperties();
        Iterator<BiologicalNodeAbstract> itn = np.getPathway().getAllGraphNodes().iterator();
        Hashtable<BiologicalNodeAbstract, Double> coloring = new Hashtable<BiologicalNodeAbstract, Double>();
        if (currentAlgorithmIndex == 0) { // node degree
            // get current node degree values
            while (itn.hasNext()) {
                BiologicalNodeAbstract bna = itn.next();
                coloring.put(bna, (double) np.getNodeDegree(np.getNodeAssignment(bna)));
                // saving
                bna.addAttribute(NodeAttributeTypes.GRAPH_PROPERTY, NodeAttributeNames.NODE_DEGREE,
                                 np.getNodeDegree(np.getNodeAssignment(bna)));
            }
        }
        new GraphColorizer(coloring, currentImageId, logView.isSelected());
    }

    public void revalidateView() {
        if (!emptyPane) {
            panel.removeAll();
        }
        updateWindow();
        panel.repaint();
        panel.revalidate();
        panel.setVisible(true);
        emptyPane = false;
    }

    public void removeAllElements() {
        emptyPane = true;
    }

    public JPanel getPanel() {
        panel.setVisible(false);
        return panel;
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
                currentAlgorithmIndex = chooseAlgorithm.getSelectedIndex();
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
                // TreeMap<Integer, Integer> sorted_map = new TreeMap<>(bvc);
                // sorted_map.putAll(degreemap);
                // sort by key value
                new NodeAttributeBarChart("Statistics", "Node degree distibution", "Degree", "Count", sorted_map);
            } else if ("colorpalette".equals(command)) {
                currentImageId = chooseColorPalette.getSelectedIndex();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    static class NumbersThenWordsComparator implements Comparator<String> {
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
