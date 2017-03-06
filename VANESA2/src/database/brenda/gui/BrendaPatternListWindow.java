/**
 * 
 */
package database.brenda.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;

import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import database.brenda.MoleculeBox;
import database.brenda.MoleculesPair;
import gui.MainWindow;
import miscalleanous.tables.MyTable;
import net.miginfocom.swing.MigLayout;


/**
 * @author Sebastian
 * 
 */
public class BrendaPatternListWindow extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	JPanel panel;

	JOptionPane pane;

	JPanel panel2;

	JSpinner serchDeapth;

	JCheckBox include = new JCheckBox();
	JButton newButton = new JButton("ok");
	
	JButton select = new JButton("select");
	JButton deselect = new JButton("deselect");
	JButton deselectAll = new JButton("deselect all");
	
	JButton[] buttons = { newButton};
	boolean ok = false;
	JOptionPane optionPane;
	JDialog dialog;

	private MyTable table;

	/**
	 * 
	 */

	public BrendaPatternListWindow() {

		MoleculeBox box = MoleculeBox.getInstance();
		Vector<MoleculesPair> v = box.getAllValues();
		for (int i = 0; i<v.size(); i++){
		//	System.out.println(v.get(i));
		}
		
		
		Object[][] rows = new Object[v.size()][3];
		int iterator_count = 0;
		Iterator<MoleculesPair> it = v.iterator();

		MoleculesPair p;
		while (it.hasNext()) {
			
			p = it.next();
			rows[iterator_count][0] = p.getAmount();
			rows[iterator_count][1] = p.getName();
			rows[iterator_count][2] = p.isDisregard();
			iterator_count++;
		
		}

		String[] columNames = { "# found in reactions", "Name", "Disregarded"};
		initTable(rows, columNames);
		JScrollPane sp = new JScrollPane(table);
		sp.setPreferredSize(new Dimension(800, 400));
		MigLayout layout = new MigLayout();

		JPanel mainPanel = new JPanel(layout);

		mainPanel
				.add(
						new JLabel(
								"Which elements supposed to be disregarded during the calculation?"),
						"span 2");
		mainPanel.add(new JSeparator(), "gap 10, wrap 15, growx");
		mainPanel.add(sp, "span 4, growx");

		mainPanel.add(new JSeparator(), "span, growx, wrap 15, gaptop 10");
		
		JPanel selectPanel = new JPanel();
		selectPanel.add(select);
		selectPanel.add(deselect);
		selectPanel.add(deselectAll);
	
		mainPanel.add(selectPanel,"span,gaptop 1,align right,wrap");
		mainPanel.add(new JSeparator(), "span, growx, gaptop 10");

		select.addActionListener(this);
		select.setActionCommand("select");

		deselect.addActionListener(this);
		deselect.setActionCommand("deselect");

		newButton.addActionListener(this);
		newButton.setActionCommand("new");

		deselectAll.addActionListener(this);
		deselectAll.setActionCommand("deselectAll");

		optionPane = new JOptionPane(mainPanel, JOptionPane.PLAIN_MESSAGE);
		optionPane.setOptions(buttons);

		dialog = new JDialog(this, "Calculation Settings", true);

		dialog.setContentPane(optionPane);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		dialog.pack();
		dialog.setLocationRelativeTo(MainWindow.getInstance());
		dialog.setVisible(true);
	}

	private void initTable(Object[][] rows, String[] columNames) {

		BrendaTabelModel model = new BrendaTabelModel(rows,
				columNames);

		table = new MyTable();
		table.setModel(model);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setColumnControlVisible(false);
		table.setHighlighters(HighlighterFactory.createSimpleStriping());
		table.setFillsViewportHeight(true);
		table.addHighlighter(new ColorHighlighter(new Color(192, 215, 227),
				Color.BLACK));
		table.setHorizontalScrollEnabled(true);
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setResizingAllowed(true);
		table.getTableHeader().getColumnModel().getColumn(0).setPreferredWidth(70);
		table.getTableHeader().getColumnModel().getColumn(1).setPreferredWidth(430);
		table.getTableHeader().getColumnModel().getColumn(2).setPreferredWidth(20);
	}

	public void actionPerformed(ActionEvent e) {
		String event = e.getActionCommand();

	if ("new".equals(event)) {		
			dialog.setVisible(false);
		}else if ("select".equals(event)) {		
			
			int[] selectedRows = table.getSelectedRows();
			for (int i = 0; i < selectedRows.length; i++) {
				table.setValueAt(true,selectedRows[i], 2);			
			}
			
		}else if ("deselect".equals(event)) {	
			
			int[] selectedRows = table.getSelectedRows();
			for (int i = 0; i < selectedRows.length; i++) {
				table.setValueAt(false,selectedRows[i], 2);			
			}
			
		}else if ("deselectAll".equals(event)) {		
						
			for (int i = 0; i < table.getRowCount(); i++) {
				table.setValueAt(false,i, 2);			
			}
		}

	}

}
