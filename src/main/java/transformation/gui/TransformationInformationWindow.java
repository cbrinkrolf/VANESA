package transformation.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;

import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.visualization.picking.PickedState;
import gui.MainWindow;
import miscalleanous.tables.MyTable;
import miscalleanous.tables.NodePropertyTableModel;
import net.miginfocom.swing.MigLayout;
import transformation.Match;

public class TransformationInformationWindow implements ActionListener {

	private JPanel mainPanel;
	private JPanel buttonPanel;
	private JFrame frame;

	private List<Match> matches;

	private MyTable matchTable;
	private NodePropertyTableModel matchModel;
	private String[] columnNames = { "# Match", "Rule name" };
	private Pathway pw;

	private Collection<BiologicalNodeAbstract> nodesMatched;
	private Collection<BiologicalEdgeAbstract> edgesMatched;
	private Collection<BiologicalNodeAbstract> nodesNotMatched;
	private Collection<BiologicalEdgeAbstract> edgesNotMatched;
	private Collection<BiologicalNodeAbstract> nodesNotTransformed;

	Collection<BiologicalNodeAbstract> allNodes;;
	Collection<BiologicalEdgeAbstract> allEdges;

	public TransformationInformationWindow(Pathway pw) {
		this.pw = pw;
		this.matches = pw.getTransformationInformation().getMatches();

		MigLayout layout = new MigLayout("", "[left]");
		mainPanel = new JPanel(layout);
		buttonPanel = new JPanel(layout);

		createSets();

	}

	// TODO take also logical nodes into account
	private void createSets() {
		nodesMatched = new HashSet<>();
		edgesMatched = new HashSet<>();
		nodesNotMatched = new HashSet<>();
		edgesNotMatched = new HashSet<>();
		nodesNotTransformed = new HashSet<>();

		allNodes = pw.getAllGraphNodes();
		allEdges = pw.getAllEdges();

		nodesNotMatched.addAll(allNodes);
		nodesNotTransformed.addAll(allNodes);
		edgesNotMatched.addAll(allEdges);

		for (Match m : matches) {
			for (BiologicalNodeAbstract bna : m.getMappedNodes()) {
				nodesMatched.add(bna);
				nodesNotMatched.remove(bna);
			}
			for (BiologicalEdgeAbstract bea : m.getMappedEdges()) {
				edgesMatched.add(bea);
				edgesNotMatched.remove(bea);
			}
		}
		for (BiologicalNodeAbstract bna : pw.getTransformationInformation().getBnToPnMapping().keySet()) {
			nodesNotTransformed.remove(bna);
		}
	}

	private void createButtons() {

		JLabel nodesMatched = new JLabel();
		nodesMatched.setText("Matched nodes: " + this.nodesMatched.size() + " out of " + allNodes.size());
		nodesMatched.setToolTipText("Nodes, which are matched at least once by at least one rule");

		JLabel edgesMatched = new JLabel();
		edgesMatched.setText("Matched edges: " + this.edgesMatched.size() + " out of " + allEdges.size());
		edgesMatched.setToolTipText("Edges, which are matched at least once by at least one rule");

		JLabel nodesNotMatched = new JLabel();
		nodesNotMatched.setText("Not matched nodes: " + this.nodesNotMatched.size() + " out of " + allNodes.size());
		nodesNotMatched.setToolTipText("Nodes, which are not matched by any rule");

		JLabel edgesNotMatched = new JLabel();
		edgesNotMatched.setText("Not matched edges: " + this.edgesNotMatched.size() + " out of " + allEdges.size());
		edgesNotMatched.setToolTipText("Edges, which are not matched by any rule");

		JLabel nodesNotTransformed = new JLabel();
		nodesNotTransformed
				.setText("Not transformed nodes: " + this.nodesNotTransformed.size() + " out of " + allNodes.size());
		nodesNotTransformed.setToolTipText(
				"Nodes, which are not transformed to a Petri net node (if matched, no mapping between biological node in the rule pattern and its corresponding Petri net node)");

		JButton highlightAllMatchedNodes = new JButton("highlight");
		highlightAllMatchedNodes.setActionCommand("highlightAllMatchedNodes");
		highlightAllMatchedNodes.addActionListener(this);

		JButton highlightAllMatchedEdges = new JButton("highlight");
		highlightAllMatchedEdges.setActionCommand("highlightAllMatchedEdges");
		highlightAllMatchedEdges.addActionListener(this);

		JButton highlightAllNotMatchedNodes = new JButton("highlight");
		highlightAllNotMatchedNodes.setActionCommand("highlightAllNotMatchedNodes");
		highlightAllNotMatchedNodes.addActionListener(this);

		JButton highlightAllNotMatchedEdges = new JButton("highlight");
		highlightAllNotMatchedEdges.setActionCommand("highlightAllNotMatchedEdges");
		highlightAllNotMatchedEdges.addActionListener(this);

		JButton highlightAllNotTransformedNodes = new JButton("highlight");
		highlightAllNotTransformedNodes.setActionCommand("highlightAllNotTransformedNodes");
		highlightAllNotTransformedNodes.addActionListener(this);

		buttonPanel.add(nodesMatched, "");
		buttonPanel.add(highlightAllMatchedNodes, "wrap");
		buttonPanel.add(edgesMatched, "");
		buttonPanel.add(highlightAllMatchedEdges, "wrap");
		buttonPanel.add(nodesNotMatched, "");
		buttonPanel.add(highlightAllNotMatchedNodes, "wrap");
		buttonPanel.add(edgesNotMatched, "");
		buttonPanel.add(highlightAllNotMatchedEdges, "wrap");
		buttonPanel.add(nodesNotTransformed, "");
		buttonPanel.add(highlightAllNotTransformedNodes, "wrap");
		buttonPanel.add(new JSeparator(), "span, growx, gaptop 7 ");
		
	}

	private void initTable() {

		Object[][] rows = new Object[matches.size()][2];

		for (int i = 0; i < matches.size(); i++) {
			rows[i][0] = i + 1;
			rows[i][1] = matches.get(i).getRule().getName();
		}

		matchModel = new NodePropertyTableModel(rows, columnNames);

		matchTable = new MyTable();
		matchTable.setModel(matchModel);

		matchTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		matchTable.setColumnControlVisible(false);
		matchTable.setHighlighters(HighlighterFactory.createSimpleStriping());
		matchTable.setFillsViewportHeight(true);
		matchTable.addHighlighter(new ColorHighlighter(new Color(192, 215, 227), Color.BLACK));
		matchTable.setHorizontalScrollEnabled(true);
		matchTable.getTableHeader().setReorderingAllowed(false);
		matchTable.getTableHeader().setResizingAllowed(true);
		matchTable.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
				highlightSelectedMatch();
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});

		

		mainPanel.add(new JLabel("List of all matches found and processed during transformation."), "wrap");

		JScrollPane sp = new JScrollPane(matchTable);
		sp.setPreferredSize(new Dimension(400, 400));

		mainPanel.add(sp, "growx, spanx");
		// mainPanel.repaint();
		mainPanel.revalidate();
	}

	public void show() {
		this.createButtons();
		mainPanel.add(buttonPanel, "wrap");
		if (matches.size() > 0) {
			this.initTable();
		}
		mainPanel.revalidate();
		buttonPanel.revalidate();
		
		
		frame = new JFrame("Overview of transformation rules");
		frame.setIconImages(MainWindow.getInstance().getFrame().getIconImages());

		frame.setAlwaysOnTop(false);
		frame.setContentPane(mainPanel);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setIconImages(MainWindow.getInstance().getFrame().getIconImages());
		frame.revalidate();
		frame.setPreferredSize(new Dimension(400, 400));
		frame.pack();
frame.repaint();
		
		frame.setLocationRelativeTo(MainWindow.getInstance().getFrame());
		frame.requestFocus();
		frame.setVisible(true);

		frame.addWindowFocusListener(new WindowFocusListener() {

			@Override
			public void windowLostFocus(WindowEvent e) {
			}

			@Override
			public void windowGainedFocus(WindowEvent e) {
				// repaintPanel();
				frame.pack();
				// revalidate();
				// pack();
				// repaint();
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		PickedState<BiologicalNodeAbstract> vState = pw.getGraph().getVisualizationViewer().getPickedVertexState();
		PickedState<BiologicalEdgeAbstract> eState = pw.getGraph().getVisualizationViewer().getPickedEdgeState();

		vState.clear();
		eState.clear();

		switch (e.getActionCommand()) {

		case "highlightAllMatchedNodes":
			for (BiologicalNodeAbstract bna : nodesMatched) {
				vState.pick(bna, true);
			}
			break;
		case "highlightAllMatchedEdges":
			for (BiologicalEdgeAbstract bea : edgesMatched) {
				eState.pick(bea, true);
			}
			break;
		case "highlightAllNotMatchedNodes":
			for (BiologicalNodeAbstract bna : nodesNotMatched) {
				vState.pick(bna, true);
			}
			break;
		case "highlightAllNotMatchedEdges":
			for (BiologicalEdgeAbstract bea : edgesNotMatched) {
				eState.pick(bea, true);
			}
			break;
		case "highlightAllNotTransformedNodes":
			for (BiologicalNodeAbstract bna : nodesNotTransformed) {
				vState.pick(bna, true);
			}
			break;
		}
	}

	private void highlightSelectedMatch() {
		if (matchTable.getRowCount() < 1) {
			return;
		}
		if (matchTable.getSelectedRowCount() == 0) {
			return;
		}

		Match m = matches.get(((int) matchTable.getValueAt(matchTable.getSelectedRow(), 0)) - 1);

		PickedState<BiologicalNodeAbstract> vState = pw.getGraph().getVisualizationViewer().getPickedVertexState();
		PickedState<BiologicalEdgeAbstract> eState = pw.getGraph().getVisualizationViewer().getPickedEdgeState();

		vState.clear();
		eState.clear();
		for (BiologicalNodeAbstract bna : m.getMappedNodes()) {
			vState.pick(bna, true);
		}

		for (BiologicalEdgeAbstract bea : m.getMappedEdges()) {
			eState.pick(bea, true);
		}
	}
}
