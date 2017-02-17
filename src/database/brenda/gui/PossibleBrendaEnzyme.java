/**
 * 
 */
package database.brenda.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import gui.MainWindow;
import miscalleanous.tables.MyTable;
import miscalleanous.tables.NodePropertyTableModel;
import net.miginfocom.swing.MigLayout;
import pojos.DBColumn;


/**
 * @author Sebastian
 * 
 */
public class PossibleBrendaEnzyme extends JFrame implements ActionListener, TableModelListener 
{
	private static final long serialVersionUID=5817431890806360999L;

	JPanel panel;

	JOptionPane pane;

	JPanel panel2;

	JCheckBox include = new JCheckBox();
	JButton newButton = new JButton("ok");
	JButton skip = new JButton("skip");
	
	
	JButton[] buttons = { newButton,skip };
	boolean ok = false;
	JOptionPane optionPane;
	JDialog dialog;
	
	private String selectedEnzyme;
	private TabelModel model;
	private MyTable table;

	/**
	 * 
	 */
	
	public PossibleBrendaEnzyme(ArrayList<DBColumn> entries, String name)
	{
		Object[][] rows=new Object[entries.size()][3];
		
		int iterator_count=0;
		
		for (DBColumn column : entries)
		{
	    	String[] details=column.getColumn();
	    	
			rows[iterator_count][0]=details[0];
			rows[iterator_count][1]=details[2];
			if (iterator_count==0)
			{
				selectedEnzyme=details[0];
				rows[iterator_count][2]=true;
			}
			else
			{
				rows[iterator_count][2]=false;
			}
			iterator_count++;
		}

		String[] columNames={"EC Number", "Name", "Choose"};
		initTable(rows, columNames);
		JScrollPane sp=new JScrollPane(table);
		sp.setPreferredSize(new Dimension(800, 400));
		MigLayout layout=new MigLayout();

		JPanel mainPanel=new JPanel(layout);

		mainPanel.add(new JLabel(entries.size()+" enzymes that match '"+name+"' have been found. Which element are you looking for?"),"span 2");
		mainPanel.add(new JSeparator(), "gap 10, wrap 15, growx");
		mainPanel.add(sp, "span 4, growx");

		mainPanel.add(new JSeparator(), "span, growx, wrap 15, gaptop 10");

		newButton.addActionListener(this);
		newButton.setActionCommand("new");

		skip.addActionListener(this);
		skip.setActionCommand("skip");

		optionPane=new JOptionPane(mainPanel, JOptionPane.PLAIN_MESSAGE);
		optionPane.setOptions(buttons);

		dialog=new JDialog(this, "Found enzymes out of BRENDA", true);

		dialog.setContentPane(optionPane);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		dialog.pack();
		dialog.setLocationRelativeTo(MainWindow.getInstance());
		dialog.setVisible(true);
	}
	
//	public PossibleBrendaEnzyme(Vector entries, String name) {
//	
//		Object[][] rows = new Object[entries.size()][3];
//		int iterator_count = 0;
//		Iterator it = entries.iterator();
//		while (it.hasNext()) {
//			
//			String[] details = (String[]) it.next();
//			rows[iterator_count][0] = details[0];
//			rows[iterator_count][1] = details[2];
//			if(iterator_count == 0){
//				selectedEnzyme = details[0];
//				rows[iterator_count][2] = true;
//			}else{
//				rows[iterator_count][2] = false;	
//			}
//			iterator_count++;
//		}
//
//		String[] columNames = { "EC Number", "Name", "Choose"};
//		initTable(rows, columNames);
//		JScrollPane sp = new JScrollPane(table);
//		sp.setPreferredSize(new Dimension(800, 400));
//		MigLayout layout = new MigLayout();
//
//		JPanel mainPanel = new JPanel(layout);
//
//		mainPanel
//				.add(
//						new JLabel(
//								entries.size() +" enzymes that match '"+name +"' have been found. Which element are you looking for?"),
//						"span 2");
//		mainPanel.add(new JSeparator(), "gap 10, wrap 15, growx");
//		mainPanel.add(sp, "span 4, growx");
//
//		mainPanel.add(new JSeparator(), "span, growx, wrap 15, gaptop 10");
//	
//		newButton.addActionListener(this);
//		newButton.setActionCommand("new");
//		
//		skip.addActionListener(this);
//		skip.setActionCommand("skip");
//
//		optionPane = new JOptionPane(mainPanel, JOptionPane.PLAIN_MESSAGE);
//		optionPane.setOptions(buttons);
//
//		dialog = new JDialog(this, "Found enzymes out of BRENDA", true);
//
//		dialog.setContentPane(optionPane);
//		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
//		
//		ScreenSize screen = new ScreenSize();
//		int screenHeight = (int) screen.getheight();
//		int screenWidth = (int) screen.getwidth();
//		
//		dialog.pack();
//		dialog.setLocation((screenWidth / 2) - dialog.getSize().width / 2,
//				(screenHeight / 2) - dialog.getSize().height / 2);
//		dialog.setVisible(true);
//	}

	public String getSelectedEnzyme(){
		return selectedEnzyme;
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
		table.addHighlighter(new ColorHighlighter(new Color(192, 215, 227),
				Color.BLACK));
		table.setHorizontalScrollEnabled(true);
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setResizingAllowed(true);
		table.getTableHeader().getColumnModel().getColumn(0).setPreferredWidth(70);
		table.getTableHeader().getColumnModel().getColumn(1).setPreferredWidth(430);
		table.getTableHeader().getColumnModel().getColumn(2).setPreferredWidth(20);
		table.getModel().addTableModelListener(this);
		

	}

	public void actionPerformed(ActionEvent e) {
		String event = e.getActionCommand();

	if ("new".equals(event)) {		
			dialog.setVisible(false);
		}else if("skip".equals(event)) {		
			selectedEnzyme = null;
			dialog.setVisible(false);
		}
	}

	public void tableChanged(TableModelEvent e) {
		table.getModel().removeTableModelListener(this);
		int changedRow = e.getFirstRow();
		int rows = table.getRowCount();
		
		for (int i = 0; i < rows; i++) {
			if(i!=changedRow){
				table.setValueAt(false,i, 2);
			}		
		}
		selectedEnzyme = table.getValueAt(changedRow, 0).toString();
		table.getModel().addTableModelListener(this);
	}

}

class TabelModel extends NodePropertyTableModel
{
	private static final long serialVersionUID=-7930301159955920272L;
	
	// private MoleculeBox box=MoleculeBoxSingelton.getInstance();

	public TabelModel(Object[][] rows, String[] headers)
	{
		super(rows, headers);
	}

	@Override
	public Class<?> getColumnClass(int c)
	{
		if (c==2)
			return Boolean.class;
		else if (c==0)
			return Integer.class;
		return String.class;
	}

	@Override
	public void setValueAt(Object value, int row, int col)
	{
		super.data[row][col]=value;
		fireTableCellUpdated(row, col);
	}

	@Override
	public boolean isCellEditable(int row, int col)
	{
		if (col==2)
		{
			return true;
		}
		else
		{
			return false;
		}

	 }
}
