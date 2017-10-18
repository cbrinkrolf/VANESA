package test;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import gui.algorithms.ScreenSize;
import miscalleanous.tables.MyTable;
import net.miginfocom.swing.MigLayout;



public class pane implements ActionListener {
	
	JPanel panel;
	JOptionPane pane;
	JPanel mainPanel;
	JPanel panel2;
	boolean show = true;
	JCheckBox include = new JCheckBox();
	JButton newButton = new JButton("exit");
	JButton showPanel = new JButton("hide parameters");
	JSlider slider = new JSlider();
	
	JButton add = new JButton("add column");
	JButton delete = new JButton("delete column");
	JButton reset = new JButton("reset all values");
	
	boolean ok = false;
	JOptionPane optionPane;
	JDialog dialog;

	DefaultTableModel model;
	
	private MyTable table;
	
	public pane(){
		
		String[] columNames = { "Element", "t=1"};
		Object[][] rows = {
				{"V1",0},
				{"V2",3},
				
		};
		
		initTable(rows, columNames);
		JScrollPane sp = new JScrollPane(table);
		sp.setPreferredSize(new Dimension(800, 400));
		MigLayout layout = new MigLayout();

		mainPanel = new JPanel(layout);

		mainPanel
				.add(
						new JLabel(
								"Animation"),
						"span 2");
		mainPanel.add(new JSeparator(), "gap 10, wrap 15, growx");
		mainPanel.add(sp, "span 4, growx");

		mainPanel.add(new JSeparator(), "span, growx, wrap 15, gaptop 10");
		
		JPanel selectPanel = new JPanel();
		selectPanel.add(add);
		selectPanel.add(delete);
		selectPanel.add(reset);

		mainPanel.add(selectPanel,"span,gaptop 1,align right,wrap");
		mainPanel.add(new JSeparator(), "span, growx, gaptop 10");

		add.addActionListener(this);
		add.setActionCommand("add");

		delete.addActionListener(this);
		delete.setActionCommand("delete");

		reset.addActionListener(this);
		reset.setActionCommand("reset");

		newButton.addActionListener(this);
		newButton.setActionCommand("ok");
		
		showPanel.addActionListener(this);
		showPanel.setActionCommand("show");
		

		optionPane = new JOptionPane(mainPanel, JOptionPane.PLAIN_MESSAGE);
		slider.setPaintLabels(true);
		slider.setMaximum(1);
		slider.setMajorTickSpacing(1);
		
		
		Object[] options = {slider,showPanel,newButton};
		
		optionPane.setOptions(options);

		dialog = new JDialog();

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

		model = new DefaultTableModel(rows,
				columNames);

		table = new MyTable();
		table.setModel(model);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setColumnControlVisible(false);
		table.setHighlighters(HighlighterFactory.createSimpleStriping());
		table.setFillsViewportHeight(true);
		table.addHighlighter(new ColorHighlighter());
		table.setHorizontalScrollEnabled(true);
		table.getTableHeader().setReorderingAllowed(true);
		table.getTableHeader().setResizingAllowed(true);
		table.getColumn( "Element" ).setPreferredWidth(  100 ); 
		table.getColumn( "t=1" ).setPreferredWidth(  50 ); 
		table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
	}
	
	public void actionPerformed(ActionEvent e) {
		String event = e.getActionCommand();

	if ("ok".equals(event)) {		
			dialog.setVisible(false);
		
	}else if ("add".equals(event)) {	
             
             String header = "t=" + (table.getColumnCount());
             
             int rows = table.getRowCount();
             String[] values = new String[rows];

             for (int j = 0; j < rows; j++)
             {
                 values[j] = "0";
             }
             
             slider.setMaximum(slider.getMaximum()+1);
             model.addColumn( header, values );
             
		}else if ("delete".equals(event)) {	
			
		    int columns = model.getColumnCount();
		    
            if (columns > 2)
            {
                if (!table.getAutoCreateColumnsFromModel())
                {
                    int view = table.convertColumnIndexToView(columns - 1);
                    TableColumn column = table.getColumnModel().getColumn(view);
                    table.getColumnModel().removeColumn( column );
                }
                slider.setMaximum(slider.getMaximum()-1);
                model.setColumnCount( columns - 1 );
            }
			
		}else if ("reset".equals(event)) {		
						
		}else if ("show".equals(event)) {
			if(show){
				show=false;
				mainPanel.setVisible(false);
				showPanel.setText("show parameters");
			}else{
				show=true;
				mainPanel.setVisible(true);
				showPanel.setText("hide parameters");
			}
			dialog.pack();
		}

	}
	
	
	public static void main(String[] args) {
		new pane();
	} 
	
}

