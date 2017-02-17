package database.mirna.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ListSelectionModel;

import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import gui.MainWindow;
import miscalleanous.tables.MyTable;
import miscalleanous.tables.NodePropertyTableModel;
import net.miginfocom.swing.MigLayout;
import pojos.DBColumn;

public class MirnaResultWindow extends JFrame {

		
		/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		private JOptionPane pane;
		private ArrayList<String[]> map = new ArrayList<String[]>();
		private MyTable table;

		/**
		 * 
		 */
		
		public MirnaResultWindow(ArrayList<DBColumn> result)
		{
			int values_count=0;	
			for (DBColumn column : result)
			{
				String[] resultDetails=column.getColumn();

				String mirna_name=new String("Name");
				String mirna_species=new String("Species");
				//String mirna_sequence=new String("Sequence");

				if (resultDetails[0]!=null)
				{
					mirna_name=resultDetails[0];
				}

				if (resultDetails[1]!=null)
				{
					mirna_species=resultDetails[1];
				}

				//if (resultDetails[2]!=null)
				{
					//mirna_sequence=resultDetails[2];
				}

				String[] details={mirna_name, mirna_species};
				map.add(details);
				values_count++;
			}

			
			Object[][] rows=new Object[values_count][2];
			int iterator_count=0;

			for (String[] details : map)
			{
				rows[iterator_count][0]=details[0];
				rows[iterator_count][1]=details[1];
				
				iterator_count++;
			}

			String[] columNames={"Name", "Species"};
			initTable(rows, columNames);
			
			JScrollPane sp=new JScrollPane(table);
			MigLayout layout=new MigLayout();
			JPanel mainPanel=new JPanel(layout);
			
			mainPanel.add(new JLabel("The following microRNAs have been found. Please select the microRNA of interest."),	"");
			mainPanel.add(new JSeparator(), "gap 10, wrap, growx");
			mainPanel.add(sp, "span 2, growx");

			pane=new JOptionPane(mainPanel, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
		}
		

		public Vector<String[]> getAnswer() {

			JDialog dialog = pane.createDialog(MainWindow.getInstance(), "");
			dialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			
			Integer value = (Integer) pane.getValue();
			Vector<String[]> v = new Vector<String[]>();
			if (value != null) {
				if (value.intValue() == JOptionPane.OK_OPTION) {
					
					int[] selectedRows = table.getSelectedRows();
					for(int i = 0; i< selectedRows.length;i++){
					
						String organism = table.getValueAt(selectedRows[i], 1).toString();
						String name = table.getValueAt(selectedRows[i], 0).toString();
						//v.add({name, organism});
						String[] res = {name, organism};
						v.add(res);
						/*
						Iterator it = map.iterator();				
						while(it.hasNext()){
							String[] details = (String[])it.next();
							if (details[1].equals(title) && details[2].equals(organism)){
								v.add(details);
							}	
						}*/
					}
				}
			} 
			return v;
		}

		private void initTable(Object[][] rows, String[] columNames) {
			NodePropertyTableModel model = new NodePropertyTableModel(rows,
					columNames);
			
			table = new MyTable();
			table.setModel(model);
			table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			table.setColumnControlVisible(false);
			table.addHighlighter(HighlighterFactory.createSimpleStriping());
			table.setFillsViewportHeight(true);
			table.addHighlighter(new ColorHighlighter(new Color(192, 215, 227),
					Color.BLACK));
			table.setHorizontalScrollEnabled(true);
			table.getTableHeader().setReorderingAllowed(false);
			table.getTableHeader().setResizingAllowed(false);
			table.setRowSelectionInterval(0, 0);
			
			
			
			

		}
}
