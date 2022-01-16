package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;

import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.petriNet.Place;
import graph.animations.RegulationTabelModel;
import io.SaveDialog;
import miscalleanous.tables.MyTable;
import net.miginfocom.swing.MigLayout;
import petriNet.PlotsPanel;
import petriNet.SimulationResult;
import petriNet.SimulationResultController;

public class DetailedSimRes implements ActionListener {

	private MyTable table;
	private JFrame dialog;
	private JPanel dialogPanel;
	private Pathway pw;
	private String simId;

	public DetailedSimRes(Pathway pw, String simId) {
		this.pw = pw;
		if (simId == null) {
			simId = pw.getPetriPropertiesNet().getSimResController().getLastActive().getId();
		}
		this.simId = simId;
		table = new MyTable();
		table.setModel(this.getTableModel(simId));
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setColumnControlVisible(false);
		table.setHighlighters(HighlighterFactory.createSimpleStriping());
		table.setFillsViewportHeight(true);
		table.addHighlighter(new ColorHighlighter(new Color(192, 215, 227), Color.BLACK));
		table.setHorizontalScrollEnabled(true);
		table.getTableHeader().setReorderingAllowed(true);
		table.getTableHeader().setResizingAllowed(true);
		table.getColumn("Label").setPreferredWidth(100);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		JScrollPane sp = new JScrollPane(table);
		sp.setPreferredSize(new Dimension(600, 200));

		MigLayout layout2 = new MigLayout();
		dialogPanel = new JPanel(layout2);
		dialogPanel.add(new JLabel("Results for each Timestep t and for all Places:"), "span 2");
		dialogPanel.add(new JSeparator(), "gap 10, wrap 15, growx");
		dialogPanel.add(sp, "span 4, growx, wrap");

		JButton exportSimResult = new JButton("Export Simulation Result");
		exportSimResult.setActionCommand("exportSimResult");
		exportSimResult.addActionListener(this);

		dialogPanel.add(exportSimResult, "wrap");

		dialogPanel.add(new JSeparator(), "span, growx, wrap 15, gaptop 10");

		// draw a new plot according to the current time step selection

		PlotsPanel pp = new PlotsPanel(simId);
		dialogPanel.add(pp, "wrap");
		JButton button = new JButton("Save results to folder");
		button.addActionListener(pp);

		dialogPanel.add(button);

		// System.out.println("show");
		// show table containing all data
		dialog = new JFrame("Simulation results");
		dialog.setTitle("Simulation Results");
		dialog.setResizable(true);
		dialog.setContentPane(dialogPanel);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.setIconImages(MainWindow.getInstance().getFrame().getIconImages());
		// ScreenSize screen = new ScreenSize();
		// int screenHeight = (int) screen.getheight();
		// int screenWidth = (int) screen.getwidth();
		// dialog.setAlwaysOnTop(false);
		dialog.pack();
		// dialog.setLocation((screenWidth / 2) - dialog.getSize().width /
		// 2,
		// (screenHeight / 2) - dialog.getSize().height / 2);
		dialog.setVisible(true);
		dialog.setLocationRelativeTo(MainWindow.getInstance().getFrame());
	}

	private RegulationTabelModel getTableModel(String simId) {

		BiologicalNodeAbstract bna;
		SimulationResult simRes = pw.getPetriPropertiesNet().getSimResController().get(simId);

		int rowsSize = pw.getPlaceCount();
		int rowsDim = simRes.getTime().size();
		// get Data from all Places
		Iterator<BiologicalNodeAbstract> it = pw.getAllGraphNodes().iterator();
		Object[][] rows = new Object[rowsSize][rowsDim + 1];

		int i = 0;

		while (it.hasNext()) {
			bna = it.next();
			if (bna instanceof Place && !bna.hasRef()) {
				rows[i][0] = bna.getName();
				for (int j = 1; j <= rowsDim; j++) {
					if (simRes.contains(bna, SimulationResultController.SIM_TOKEN)
							&& simRes.get(bna, SimulationResultController.SIM_TOKEN).size() > j - 1) {
						rows[i][j] = Math.max(0, simRes.get(bna, SimulationResultController.SIM_TOKEN).get(j - 1));
					} else {
						rows[i][j] = "-";
					}
				}
				i++;
			}
		}
		// create column labels for table view
		String columNames[] = new String[rowsDim + 1];
		// String selectorValues[] = new String[rowsSize];
		columNames[0] = "Label";
		for (i = 0; i < rowsDim; i++) {
			columNames[i + 1] = "t=" + simRes.getTime().get(i);
		}
		return new RegulationTabelModel(rows, columNames);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String event = e.getActionCommand();
		if (event.equals("exportSimResult")) {
			new SaveDialog(SaveDialog.FORMAT_CSV, null, dialog, this.simId);
		}
	}
}
