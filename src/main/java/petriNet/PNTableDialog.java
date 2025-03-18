package petriNet;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import graph.GraphInstance;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import biologicalElements.ElementDeclarations;
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.edges.petriNet.PNArc;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.petriNet.ContinuousTransition;
import biologicalObjects.nodes.petriNet.DiscreteTransition;
import biologicalObjects.nodes.petriNet.Place;
import biologicalObjects.nodes.petriNet.StochasticTransition;
import biologicalObjects.nodes.petriNet.Transition;
import gui.MainWindow;
import net.miginfocom.swing.MigLayout;

public class PNTableDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = -4872308937435381486L;
	private final JXTable table = new JXTable();
	private final BiologicalNodeAbstract[] bnas;

	public PNTableDialog() {
		super(MainWindow.getInstance().getFrame(), true);
		int i = 0;
		final Pathway pw = GraphInstance.getPathway();
		Object[][] rows = new Object[pw.getAllGraphNodes().toArray().length][9];
		bnas = new BiologicalNodeAbstract[pw.getAllGraphNodes().toArray().length];
		Iterator<BiologicalNodeAbstract> it = pw.getAllGraphNodes().iterator();
		while (it.hasNext()) {
			BiologicalNodeAbstract bna = it.next();
			if (bna instanceof Place || bna instanceof Transition) {
				bnas[i] = bna;
				rows[i][0] = bna.getName();
				rows[i][1] = bna.getLabel();
				rows[i][6] = bna.getBiologicalElement();
				rows[i][2] = "-";
				rows[i][3] = "-";
				rows[i][4] = "-";
				rows[i][5] = "-";
				rows[i][7] = 0.0;
				rows[i][8] = "-";
				if (bna instanceof Place) {
					Place p = (Place) bna;
					rows[i][2] = p.getToken();
					rows[i][3] = p.getTokenMax();
					rows[i][4] = p.getTokenMin();
					rows[i][5] = p.getTokenStart();
					rows[i][7] = "-";
				} else if (bna instanceof DiscreteTransition) {
					DiscreteTransition t = (DiscreteTransition) bna;
					rows[i][7] = t.getDelay();
				} else if (bna instanceof StochasticTransition) {
					StochasticTransition t = (StochasticTransition) bna;
					rows[i][8] = t.getDistribution();
				}
				i++;
			}
		}

		// CHRIS refactor Table
		i = 0;
		Object[][] rows2 = new Object[pw.getAllEdges().toArray().length][9];
		PNArc[] edges = new PNArc[pw.getAllEdges().toArray().length];
		Iterator<BiologicalEdgeAbstract> itEdge = pw.getAllEdges().iterator();
		while (it.hasNext()) {
			PNArc edge = (PNArc) itEdge.next();
			edges[i] = edge;
			rows2[i][0] = edge.getName();
			rows2[i][1] = edge.getLabel();
			for (final BiologicalNodeAbstract bna : pw.getAllGraphNodes()) {
				if (edge.getFrom().equals(bna)) {
					if (bna instanceof Transition) {
						rows2[i][2] = bna.getLabel();
					} else if (bna instanceof Place) {
						rows2[i][2] = "P" + bna.getID() + ":" + bna.getLabel();
					}
				} else {
					if (edge.getTo().equals(bna)) {
						if (bna instanceof Transition) {
							rows2[i][3] = bna.getLabel();
						} else {
							if (bna instanceof Place) {
								rows2[i][3] = "P" + bna.getID() + ":" + bna.getLabel();
							}
						}
					}
				}
			}

			rows2[i][4] = edge.getFunction();
			// rows2[i][5] = edge.getLowerBoundary();
			// rows2[i][6] = edge.getUpperBoundary();
			rows2[i][7] = edge.getProbability();
			i++;
		}

		DefaultTableModel model = new DefaultTableModel(rows,
				new String[] { "Name", "Label", "Token", "TokenMax", "TokenMin", "TokenStart", "Type", "Delay",
						"Distribution" }) {
			private static final long serialVersionUID = 1L;

			public Class<?> getColumnClass(int columnIndex) {
				if (columnIndex == 0 || columnIndex == 1)
					return String.class;
				if (columnIndex == 6 || columnIndex == 8)
					return JComboBox.class;
				return Double.class;
			}

			public boolean isCellEditable(int rowIndex, int columnIndex) {
				if (bnas[rowIndex] instanceof Place && (columnIndex == 7 || columnIndex == 8))
					return false;
				if (bnas[rowIndex] instanceof Transition && (columnIndex == 2 || columnIndex == 3 || columnIndex == 4
						|| columnIndex == 5))
					return false;
				return true;
			}

			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
				if (columnIndex == 6) {
					String s = (String) aValue;
					if (((s.equals(ElementDeclarations.continuousPlace) || s.equals(ElementDeclarations.discretePlace))
							&& !(bnas[rowIndex] instanceof Place)) || ((s.equals(ElementDeclarations.discreteTransition)
							|| s.equals(ElementDeclarations.continuousTransition) || s.equals(
							ElementDeclarations.stochasticTransition)) && !(bnas[rowIndex] instanceof Transition)))
						return;
				}
				super.setValueAt(aValue, rowIndex, columnIndex);
			}

		};

		table.setModel(model);
		TableColumn myColumn = table.getColumnModel().getColumn(6);
		JComboBox<String> comboEditor = new JComboBox<>();
		comboEditor.addItem(ElementDeclarations.discretePlace);
		comboEditor.addItem(ElementDeclarations.continuousPlace);
		comboEditor.addItem(ElementDeclarations.discreteTransition);
		comboEditor.addItem(ElementDeclarations.continuousTransition);
		comboEditor.addItem(ElementDeclarations.stochasticTransition);
		myColumn.setCellEditor(new DefaultCellEditor(comboEditor));
		JComboBox<String> comboEditor2 = new JComboBox<>();
		comboEditor2.addItem("norm");
		comboEditor2.addItem("exp");
		table.getColumnModel().getColumn(8).setCellEditor(new DefaultCellEditor(comboEditor2));

		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setColumnControlVisible(false);
		table.setHighlighters(HighlighterFactory.createSimpleStriping());
		table.setFillsViewportHeight(true);
		table.addHighlighter(new ColorHighlighter(new Color(192, 215, 227), Color.BLACK));
		table.setHorizontalScrollEnabled(true);
		table.getTableHeader().setReorderingAllowed(true);
		table.getColumn("Type").setPreferredWidth(120);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		JScrollPane sp = new JScrollPane(table);
		sp.setPreferredSize(new Dimension(740, 400));
		DefaultTableModel model2 = new DefaultTableModel(rows2,
				new String[] { "Name", "Label", "Start", "End", "Function", "Lower Boundary", "UpperBoundary",
						"Activation Probability" }) {
			private static final long serialVersionUID = 1L;

			public Class<?> getColumnClass(int columnIndex) {
				if (columnIndex == 0 || columnIndex == 1 || columnIndex == 2 || columnIndex == 3 || columnIndex == 4)
					return String.class;
				return Double.class;
			}

			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return columnIndex != 2 && columnIndex != 3;
			}

		};
		JXTable table2 = new JXTable();
		table2.setModel(model2);
		table2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table2.setColumnControlVisible(false);
		table2.setHighlighters(HighlighterFactory.createSimpleStriping());
		table2.setFillsViewportHeight(true);
		table2.addHighlighter(new ColorHighlighter(new Color(192, 215, 227), Color.BLACK));
		table2.setHorizontalScrollEnabled(true);
		table2.getTableHeader().setReorderingAllowed(true);
		table2.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		JScrollPane sp2 = new JScrollPane(table2);
		sp2.setPreferredSize(new Dimension(740, 400));
		table2.setPreferredSize(new Dimension(720, 400));

		JPanel dialogPanel = new JPanel(new MigLayout());

		JPanel p1 = new JPanel();
		p1.setLayout(new MigLayout());
		p1.add(new JLabel("Nodes:"));
		p1.add(new JSeparator(), "span, growx, gaptop 10");
		p1.add(sp);

		JPanel p2 = new JPanel();
		p2.setLayout(new MigLayout());
		p2.add(new JLabel("Edges:"));
		p2.add(new JSeparator(), "span, growx, gaptop 10");
		p2.add(sp2);

		// dialogPanel.add(new JLabel("Nodes:"));
		// dialogPanel.add(new JSeparator());
		// dialogPanel.add(new JLabel("Edges:"));
		// dialogPanel.add(new JSeparator(), "span, growx, gaptop 10");
		// dialogPanel.add(sp, "span 4, growx");
		// dialogPanel.add(sp2, "span 4, growx");

		dialogPanel.add(p1);
		dialogPanel.add(p2);
		// JPanel selectPanel = new JPanel();
		JTabbedPane tp = new JTabbedPane();
		tp.addTab("Nodes", p1);
		tp.addTab("Edges", p2);
		// dialogPanel.add(selectPanel, "span,gaptop 1,align right,wrap");
		// dialogPanel.add(new JSeparator(), "span, growx, gaptop 10");
		dialogPanel.add(tp);
		dialogPanel.add(new JSeparator(), "span, growx, gaptop 10");
		JButton submit = new JButton("Apply Changes");
		submit.addActionListener(this);
		submit.setActionCommand("submit");
		dialogPanel.add(submit);

		setContentPane(dialogPanel);
		setTitle("Edit PN-Elements...");
		setResizable(false);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		pack();
		this.setLocationRelativeTo(MainWindow.getInstance().getFrame());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		final Pathway pw = GraphInstance.getPathway();
		if (e.getActionCommand().equals("submit")) {
			boolean changedAllStates = true;
			for (int i = 0; i < pw.getAllGraphNodes().toArray().length; i++) {
				bnas[i].setName(
						(String) table.getValueAt(table.convertRowIndexToView(i), table.convertColumnIndexToView(0)));
				bnas[i].setLabel(
						(String) table.getValueAt(table.convertRowIndexToView(i), table.convertColumnIndexToView(1)));
				if (bnas[i] instanceof Place) {
					Place p = (Place) bnas[i];
					p.setToken((Double) table.getValueAt(table.convertRowIndexToView(i),
							table.convertColumnIndexToView(2)));
					p.setTokenMax((Double) table.getValueAt(table.convertRowIndexToView(i),
							table.convertColumnIndexToView(3)));
					p.setTokenMin((Double) table.getValueAt(table.convertRowIndexToView(i),
							table.convertColumnIndexToView(4)));
					p.setTokenStart((Double) table.getValueAt(table.convertRowIndexToView(i),
							table.convertColumnIndexToView(5)));

					boolean stateChanged = true;
					for (BiologicalNodeAbstract neighbour : pw.getGraph2().getNeighbors(
							bnas[table.convertRowIndexToView(i)])) {
						for (BiologicalNodeAbstract node : pw.getAllGraphNodes()) {
							if (node.equals(neighbour) && (table.getValueAt(table.convertRowIndexToView(i),
									table.convertColumnIndexToView(6)).equals(ElementDeclarations.discretePlace)
									&& node instanceof ContinuousTransition))
								stateChanged = false;
						}
					}
					changedAllStates &= stateChanged;
					if (stateChanged)
						p.setDiscrete(
								table.getValueAt(table.convertRowIndexToView(i), table.convertColumnIndexToView(6))
										.equals(ElementDeclarations.discretePlace));
					else
						p.setColor(Color.red);

				} else if (bnas[i] instanceof Transition) {
					Transition t = (Transition) bnas[i];
					boolean stateChanged = true;
					for (BiologicalNodeAbstract neighbour : pw.getGraph2().getNeighbors(t)) {
						for (BiologicalNodeAbstract node : pw.getAllGraphNodes()) {
							if (node.equals(neighbour) && (table.getValueAt(table.convertRowIndexToView(i),
									table.convertColumnIndexToView(6)).equals(ElementDeclarations.continuousTransition)
									&& node.getBiologicalElement().equals(ElementDeclarations.discretePlace)))
								stateChanged = false;
						}
					}
					changedAllStates &= stateChanged;

					Transition newT = null;
					if (table.getValueAt(table.convertRowIndexToView(i), table.convertColumnIndexToView(6)).equals(
							ElementDeclarations.discreteTransition) && !(t instanceof DiscreteTransition)) {
						newT = new DiscreteTransition(t.getLabel(), t.getName(), pw);
					} else if (table.getValueAt(table.convertRowIndexToView(i), table.convertColumnIndexToView(6))
							.equals(ElementDeclarations.stochasticTransition) && !(t instanceof StochasticTransition)) {
						newT = new StochasticTransition(t.getLabel(), t.getName(), pw);
					} else if (table.getValueAt(table.convertRowIndexToView(i), table.convertColumnIndexToView(6))
							.equals(ElementDeclarations.continuousTransition) && !(t instanceof ContinuousTransition)) {
						newT = new ContinuousTransition(t.getLabel(), t.getName(), pw);
					}
					if (newT != null) {
						// newT.setCompartment(t.getCompartment());
						pw.addVertex(newT, new Point());
						t = newT;
					}
					if (!stateChanged)
						t.setColor(Color.red);

					if (t instanceof DiscreteTransition) {
						((DiscreteTransition) t).setDelay((String) table.getValueAt(table.convertRowIndexToView(i),
								table.convertColumnIndexToView(7)));
					} else if (t instanceof StochasticTransition) {
						((StochasticTransition) t).setDistribution(
								(String) table.getValueAt(table.convertRowIndexToView(i),
										table.convertColumnIndexToView(8)));
					}
				}
			}

			/*
			 * if (e.getActionCommand().equals("submit")) for (int i = 0; i <
			 * pw.getAllEdges().toArray().length; i++) { edges[i].setName((String)
			 * table2.getValueAt( table2.convertRowIndexToView(i),
			 * table2.convertColumnIndexToView(0))); edges[i].setLabel((String)
			 * table2.getValueAt( table2.convertRowIndexToView(i),
			 * table2.convertColumnIndexToView(1))); edges[i].setFunction((String)
			 * table2.getValueAt( table2.convertRowIndexToView(i),
			 * table2.convertColumnIndexToView(4))); edges[i].setLowerBoundary(((Double)
			 * table2.getValueAt( table2.convertRowIndexToView(i),
			 * table2.convertColumnIndexToView(5)))); edges[i].setUpperBoundary(((Double)
			 * table2.getValueAt( table2.convertRowIndexToView(i),
			 * table2.convertColumnIndexToView(6))));
			 * edges[i].setActivationProbability(((Double) table2
			 * .getValueAt(table2.convertRowIndexToView(i),
			 * table2.convertColumnIndexToView(7))));
			 *
			 * }
			 */

			setVisible(false);
			if (!changedAllStates)
				JOptionPane.showMessageDialog(MainWindow.getInstance().getFrame(),
						"You tried to change the type of your transitions or places in a way, such that there were a relation between a continuous transition and a discrete place! The objects which type was not changed are marked red.",
						"Action could not be fully performed...", JOptionPane.ERROR_MESSAGE);
		}

	}
}
