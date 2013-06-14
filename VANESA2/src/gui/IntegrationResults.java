/**
 * 
 */
package gui;

import gui.algorithms.ScreenSize;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;

import miscalleanous.tables.MyTable;
import miscalleanous.tables.NodePropertyTableModel;
import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;


/**
 * @author Sebastian
 * 
 */
public class IntegrationResults extends JFrame implements ActionListener{

	JPanel panel;

	JOptionPane pane;

	JPanel panel2;

	JButton newButton = new JButton("ok");	
	
	JButton[] buttons = { newButton};

	JOptionPane optionPane;
	JDialog dialog;
	
	private TabelModel model;
	private MyTable table;

	/**
	 * 
	 */

	public IntegrationResults(Vector entries) {
	
		Object[][] rows = new Object[entries.size()][4];
		int iterator_count = 0;
		Iterator it = entries.iterator();
		while (it.hasNext()) {
			
			String[] details = (String[]) it.next();
			rows[iterator_count][0] = details[0];
			rows[iterator_count][1] = details[1];
			rows[iterator_count][2] = details[2];
			rows[iterator_count][3] = details[3];
			
			iterator_count++;
		}

		String[] columNames = { "Name", "BRENDA information", "KEGG information", "DAWIS information" };
		initTable(rows, columNames);
		JScrollPane sp = new JScrollPane(table);
		sp.setPreferredSize(new Dimension(800, 400));
		MigLayout layout = new MigLayout();

		JPanel mainPanel = new JPanel(layout);

		mainPanel
				.add(
						new JLabel(
								entries.size() +" elements have been updated with additional database information."),
						"span 2");
		mainPanel.add(new JSeparator(), "gap 10, wrap 15, growx");
		mainPanel.add(sp, "span 4, growx,wrap 15");
		mainPanel.add(new JLabel("Elements which contain specific database information are marked with an asteriks in the panel 'Graph elements' on the left hand side."),
				"span,grow x,wrap 10");
		mainPanel.add(new JSeparator(), "span, growx, wrap 15, gaptop 10");
	
		newButton.addActionListener(this);
		newButton.setActionCommand("new");

		optionPane = new JOptionPane(mainPanel, JOptionPane.PLAIN_MESSAGE);
		optionPane.setOptions(buttons);

		dialog = new JDialog(this, "Integration report", true);

		dialog.setContentPane(optionPane);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		ScreenSize screen = new ScreenSize();
		int screenHeight = (int) screen.getheight();
		int screenWidth = (int) screen.getwidth();
		
		dialog.pack();
		dialog.setLocation((screenWidth / 2) - dialog.getSize().width / 2,
				(screenHeight / 2) - dialog.getSize().height / 2);
		dialog.setVisible(true);
	}

	
	private void initTable(Object[][] rows, String[] columNames) {

		model = new TabelModel(rows,
				columNames);

		table = new MyTable();
		table.setModel(model);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setColumnControlVisible(false);
		table.setHighlighters(HighlighterFactory.createSimpleStriping());
		table.setFillsViewportHeight(true);
		table.addHighlighter(new ColorHighlighter());
		table.setHorizontalScrollEnabled(true);
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setResizingAllowed(true);
		table.getTableHeader().getColumnModel().getColumn(0).setPreferredWidth(430);
		table.getTableHeader().getColumnModel().getColumn(1).setPreferredWidth(40);
		table.getTableHeader().getColumnModel().getColumn(2).setPreferredWidth(40);
		
	}

	public void actionPerformed(ActionEvent e) {
		String event = e.getActionCommand();

	if ("new".equals(event)) {		
			dialog.setVisible(false);
		}
	}

}

class TabelModel extends NodePropertyTableModel{

	public TabelModel(Object[][] rows, String[] headers) {
		super(rows, headers);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public Class getColumnClass(int c) {
		return String.class;
    }

	 @Override
	public void setValueAt(Object value, int row, int col) {
	}
	
	@Override
	public boolean isCellEditable(int row, int col) {
		    return false;
	
	 }
}
