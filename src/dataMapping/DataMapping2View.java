package dataMapping;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import biologicalElements.Pathway;
import dataMapping.dataImport.ExcelException;
import dataMapping.dataImport.ImportExcelxData;
import graph.GraphContainer;
import gui.MainWindow;
import gui.MyPopUp;
import net.miginfocom.swing.MigLayout;

/**
 * This class manage the GUI elements of the DataMapping
 * @author dborck
 *
 */
public class DataMapping2View extends JDialog implements Observer{

	private static final long serialVersionUID = 1L;
	public static MainWindow w = MainWindow.getInstance();
	private GraphContainer con = GraphContainer.getInstance();

	private JTabbedPane tabbedPane = new JTabbedPane();
	private JPanel mainPanel;
	private JPanel panelSource;
	private JPanel panelData;

	private JComboBox<String> pathwayCB;
	private JTextField dataFile;
	private JButton browse;

	private DefaultTableModel model;
	private JTable table;
	private JTableHeader header;

	private JScrollPane scroll;
	private JTextField headerTF;

	// these are the up to date possible identifiers for the BioMart queries
//	private String[] identifier = {"none", "Agilent [e.g. A_23_P30024] ","Affymetrix [e.g. 209239_at]", "EMBL [e.g. M58603]", "UniProt [e.g. P19838]"};
	private String[] identifier = {"none", "Agilent [e.g. A_23_P30024] "};
	private String[] species = {"none", "Homo sapiens (human)","Mus musculus (mouse)", "Saccharomyces cerevisiae (yeast)"};
	private JTextField identifierTF;
	private JComboBox<String> identifierCB;
	private JComboBox<String> speciesCB;
	
	private JTextField valueTF;

	private final String clickHeader = "Click the header of the column";

	private JProgressBar progressBar;
	private boolean done = false;

	private JButton cancelButton;
	private JButton okButton;
	private JButton resetButton;

	private JProgressBar progressBarBiomart;

	private JPopupMenu popUp;
	private JMenuItem hprd;
	private JMenuItem mint;
	private JMenuItem intact;
	private int pathwayCounts;
	private JPanel panelSep;

	private JPanel dataAdjust;
	private JPanel buttonPanel;
	private JTable dataMappingTable;
	private JScrollPane dataMappingScroll;
//	private JButton changeData;
	private JButton changeDataAndClose;
	private JButton cancel;

	private Object storedPathway;
	private boolean inToolSearch;
	private DefaultTableModel dmModel;

	/**
	 * constructs a new view as a JDialogPanel 
	 */
	public DataMapping2View() {
		super(w,"Data Mapping");
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		mainPanel = new JPanel();

		panelSource = new JPanel(new MigLayout("fillx", "[left]"));

		// data source
		panelSource.add(new JLabel("Source"));
		panelSource.add(new JSeparator(), "span, growx, wrap 5, gaptop 10, gap 5");

		panelSource.add(new JLabel("First Step - Select your data file."), "wrap, span, growx");
		panelSource.add(new JLabel("Data File:"));
		dataFile = new JTextField(15);
		dataFile.setEditable(false);
		panelSource.add(dataFile, "split 2, growx");
		browse = new JButton("Browse ...");
		browse.setActionCommand("browse");
		panelSource.add(browse, "wrap");
		progressBar = new JProgressBar();
		progressBar.setVisible(false);
		panelSource.add(progressBar, "skip 1, wrap, growx");

		panelSep = new JPanel(new MigLayout("fillx", "[left]"));
		panelSep.setMinimumSize(new Dimension(675, 30));
		panelSep.add(new JLabel("Data Preview"));
		panelSep.add(new JSeparator(), "span, growx, wrap 5, gap 5");

		// the data preview and the selection steps
		MigLayout layout2 = new MigLayout("","[left]");
		panelData = new JPanel(layout2);
		//panelData.setBorder(BorderFactory.createTitledBorder("Data Previev"));//does not look nice

		// initialize the table for the data preview
		initTable();
		scroll = new JScrollPane(table);
		scroll.setMinimumSize(new Dimension(300, 400));
		scroll.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		panelData.add(scroll, "west, gapright 10");
		panelData.add(new JLabel("<html>Second Step - <br></br>Select the row with your header labels</html>"), "span, wrap");
		panelData.add(headerTF = new JTextField("Click in one cell with a header label"), "span, growx, wrap 15");
		headerTF.setEditable(false);
		panelData.add(new JLabel("<html>Third Step - <br></br>Select the species-DB which you want to browse</html>"), "span, wrap");
		panelData.add(new JLabel("Species:"), "wrap");
		speciesCB = new JComboBox<String>(species);
		speciesCB.setActionCommand("species");
		panelData.add(speciesCB, "span, growx, wrap 15");
		
		panelData.add(new JLabel("<html>Fourth Step - <br></br>Select the type of your identifiers</html>"), "span, wrap");
		panelData.add(new JLabel("Type of identifier:"), "wrap");
		identifierCB = new JComboBox<String>(identifier);
		identifierCB.setActionCommand("identifier");
		panelData.add(identifierCB, "span, growx, wrap 15");
		panelData.add(new JLabel("<html>Fifth Step - <br></br>Select the column with identifiers</html>"), "span, wrap");
		panelData.add(identifierTF = new JTextField(clickHeader), "span, growx, wrap 15");
		identifierTF.setEditable(false);
		panelData.add(new JLabel("<html>Sixth Step - <br></br>Select the column with data values</html>"), "span, wrap");// to be mapped to the network</html>"), "span, wrap");
		panelData.add(valueTF = new JTextField(clickHeader), "span, growx, wrap 15");
		valueTF.setPreferredSize(identifierTF.getPreferredSize());
		valueTF.setEditable(false);

		//action buttons
		resetButton = new JButton("Reset");
		resetButton.setActionCommand("reset");
		panelData.add(resetButton, "split 3");
		cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("cancel");
		panelData.add(cancelButton);
		okButton = new JButton("OK");
		okButton.setEnabled(false);
		okButton.setActionCommand("doMapping");
		panelData.add(okButton, "growx, wrap 5");

		progressBarBiomart = new JProgressBar();
		progressBarBiomart.setMaximumSize(new Dimension(300, 25));
		progressBarBiomart.setVisible(false);
		panelData.add(progressBarBiomart, "south");

		// popUp menu for database search
		popUp = new JPopupMenu();
		hprd = new JMenuItem("HPRD ...");
		hprd.setActionCommand("hprd");
		mint = new JMenuItem("MINT ...");
		mint.setActionCommand("mint");
		intact = new JMenuItem("IntAct ...");
		intact.setActionCommand("intact");
		popUp.add("Database Search:");
		popUp.addSeparator();
		popUp.add(hprd);
		popUp.add(mint);
		popUp.add(intact);

		// add to main panel
		mainPanel.setLayout(new MigLayout());
		mainPanel.add(panelSource, "wrap");
		mainPanel.add(panelSep, "wrap");
		mainPanel.add(panelData);

		// panel for display mapping data

		dataAdjust = new JPanel(new MigLayout("", "[grow]", ""));
		buttonPanel = new JPanel(new MigLayout("", "[]", ""));
//		changeData = new JButton("Change");
//		changeData.setActionCommand("changeData");
		changeDataAndClose = new JButton("OK");
		changeDataAndClose.setActionCommand("changeDataAndClose");
		cancel = new JButton("Cancel");
		cancel.setActionCommand("cancelChange");

		// panel for the screenshots tutorial

		// combine panels in tabbedPane 
		tabbedPane.addTab("Data Input", mainPanel);
		tabbedPane.addTab("Data adjustment", dataAdjust);
//		tabbedPane.addTab("Help", helpPanel);

		this.add(tabbedPane);
		this.setMinimumSize(new Dimension(700, 700));
		this.setLocationRelativeTo(MainWindow.getInstance());
		this.setResizable(false);
		this.setVisible(true);
	}

	/**
	 * checks whether there is a new pathway in the MainWindow, if after a new search there is one
	 * new pathway than it returns true. Do not call if you have not executed a new search! 
	 * @return true if a new pathway was added to the MainWindow
	 */
	public boolean isPathwayCBupdated() {
		boolean searchReady = false;
		while (!searchReady){
			if (con.getAllPathways().size() >= pathwayCounts+1) {
				searchReady = true;
			}
		}
		return searchReady;	
	}

	/**
	 * after a right click search or a search in the main window, update the pathwayCB
	 */
	public void updatePathwayCB() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				int pathwayAmount = con.getAllPathways().size();
				if (pathwayAmount != pathwayCounts) {
					initPathwayComboBox();
				}
				if(inToolSearch) {
					try {
						pathwayCB.setSelectedItem(w.getCurrentPathway());
					} catch (NullPointerException e) {
						pathwayCB.addItem("none");
					}
				} else if(!inToolSearch){
					pathwayCB.setSelectedItem(storedPathway);
					inToolSearch(false);
				}	
			}
		});
	}

	/**
	 * initialize the pathway ComboBox at the beginning
	 */
	public void initPathwayComboBox() {
		con = GraphContainer.getInstance();
		pathwayCounts = con.getAllPathways().size();
		if(pathwayCB == null) {
			storedPathway = "none";
			List<String> pathwayList = new ArrayList<String>();
			for (Object o : con.getAllPathways()) {
				pathwayList.add(((Pathway) o).getName());
			}
			pathwayCB = new JComboBox<String>(pathwayList.toArray(new String[0]));
			pathwayCB.addItem("none");
		} else {
			pathwayCB.removeAllItems();
			pathwayCB.addItem("none");
			for (Object o : con.getAllPathways()) {
				pathwayCB.addItem(((Pathway) o).getName());
			}
		}
		pathwayCB.setActionCommand("pathway");
		pathwayCB.setMaximumSize(new Dimension(575, 25));
	}

	@Override
	public void update(Observable o, Object arg) {
		if (arg.getClass().equals(ImportExcelxData.class)) {
			final ImportExcelxData importData = (ImportExcelxData) arg;
			// this invokeLater() avoids a java bug when a model is set to the table
			// and the new number of columns differs from the old number
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					model = new DefaultTableModel(importData.getDataVector(), importData.getHeaderVector());
					model.addTableModelListener(new TableModelListener() {
						@Override
						public void tableChanged(TableModelEvent e) {
						}
					});
					table.setModel(model);
					table.setForeground(UIManager.getColor("Table.foreground"));
										
					Enumeration<TableColumn> enumeration = table.getColumnModel().getColumns();
					while(enumeration.hasMoreElements()) {
						enumeration.nextElement().setMinWidth(120);
					}
					model.fireTableDataChanged();
				}
			});
			done = true;
		}
		if(arg.getClass().equals(Boolean.class)) {
			if((Boolean) arg) {
				okButton.setEnabled(true);	
			} else if (!(Boolean) arg) {
				okButton.setEnabled(false);
			}
		}
		if (arg.getClass().equals(String.class) && arg.equals("reset")) {
			okButton.setEnabled(false);
			identifierCB.setSelectedIndex(0);
			speciesCB.setSelectedIndex(0);
		}
		if (arg.getClass().equals(DataMappingModel.class)) {
			final DataMappingModel dmm = (DataMappingModel) arg;
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					closeProgressbarBiomart();
					setDataMappingTableModel(dmm);
					addAdjustmentComponents();
					w.repaint();
				}
			});
		}if(arg.getClass().getSuperclass().equals(Exception.class)){
			
			//Error --> close this dialog
			this.dispose();
			
			

		}
	}

	/**
	 * creates the dataMapping tableModel with the new data from the model after
	 * a search or after some data adjustments have taken place
	 *
	 * @param dmm - the DataMappingModel
	 */
	private void setDataMappingTableModel(DataMappingModel dmm) {
		Map<String, List<String>> mergeMap = dmm.getMergeMap();
		Map<String, List<String>> dups = dmm.getDuplicatedList();
		// the new data for the table
		Vector<Object> allData = new Vector<Object>();
		Vector<String> columnNames = new Vector<String>();

		columnNames.add("UniProt accession");
		columnNames.add("Identifier");
		columnNames.add("Unique/Duplicate");
		for(String headerString : dmm.getHeader()){
			columnNames.add(headerString);
		}
		
		// loop to list all used and unused data for the network
		for(Entry<String, List<String>> entry : mergeMap.entrySet()) {
			Vector<Object> rowDataV = new Vector<Object>();
			rowDataV.add(entry.getKey());
			rowDataV.add(entry.getValue().get(0));
			if(dups.containsKey(entry.getKey())) { // this one is used for upload but there is another one with the same label
				JRadioButton dUsed = new JRadioButton("duplicated", true);
				ButtonGroup group = new ButtonGroup();
				group.add(dUsed);				
				rowDataV.add(dUsed);
				// loop to extract the data of the duplicated
				int numOfDups = (dups.get(entry.getKey()).size())/entry.getValue().size();
				int valueLength = entry.getValue().size();
				
				for(int i = 1; i < entry.getValue().size(); i++){
					rowDataV.add(entry.getValue().get(i));
					
				}
				allData.add(rowDataV);
				for(int i = 0; i<numOfDups; i++) {
					Vector<Object> rowDataD = new Vector<Object>();
					rowDataD.add(entry.getKey());
					rowDataD.add(dups.get(entry.getKey()).get(i*valueLength));
					JRadioButton dunused = new JRadioButton("duplicated", false);
					group.add(dunused);
					rowDataD.add(dunused);
					
					for(int j = 1; j < valueLength; j++){
						rowDataD.add(dups.get(entry.getKey()).get(i*valueLength+j));
					}
					allData.add(rowDataD);
				}
			}
			
			else { // a unique entry
				JRadioButton unique = new JRadioButton("unique", true);
				rowDataV.add(unique);

				for(int i = 1; i < entry.getValue().size(); i++){
					rowDataV.add(entry.getValue().get(i));
					
				}
				allData.add(rowDataV);
			}
			
			

		}
		for(Entry<String, List<String>> dupEntry : dups.entrySet()) {
			Vector<Object> rowDataV = new Vector<Object>();
			if(!mergeMap.containsKey(dupEntry.getKey())) {
				rowDataV.add(dupEntry.getKey());
				for(int i = 1; i < dupEntry.getValue().size(); i++){
					rowDataV.add(dupEntry.getValue().get(i));
				}
				JRadioButton unique = new JRadioButton("unique", false);
				rowDataV.add(unique);
				allData.add(rowDataV);
			}
		}
		
		dmModel = new DefaultTableModel(allData, columnNames);
	}

	/**
	 * add the Components (ColorGradient, MappingTable and ChangeButton to the
	 * dataAdjustment tabbedPane
	 */
	private void addAdjustmentComponents() {
		if(dataMappingScroll != null) {
			dataAdjust.remove(dataMappingScroll);
		}
		dataMappingTable = new JTable(dmModel);

		if(dataMappingTable.getPreferredSize().width > dataMappingTable.getPreferredScrollableViewportSize().width){
			dataMappingTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		}
		else{
			dataMappingTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		}
			
		
		dataMappingScroll = new JScrollPane(dataMappingTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		dataMappingTable.getColumn("Unique/Duplicate").setCellRenderer(new RadioButtonRenderer());
		dataMappingTable.getColumn("Unique/Duplicate").setCellEditor(new RadioButtonEditor(new JCheckBox()));
		dataMappingTable.setAutoCreateRowSorter(true);
		
		dataAdjust.add(dataMappingScroll, "align center, gaptop 15, span, wrap 5");

		buttonPanel.add(changeDataAndClose, "");
//		buttonPanel.add(changeData, "");
		buttonPanel.add(cancel, "");
		
		dataAdjust.add(buttonPanel, "align right, skip 1");
		
		tabbedPane.setSelectedComponent(dataAdjust);

	}

	/**
	 * all Listener for the gui are added in this method
	 * 
	 * @param controller - of the MVC which controls flow between view and model
	 */
	public void addController(DataMapping2GUIController controller){
		header.addMouseListener(controller);
		identifierCB.addActionListener(controller);
		speciesCB.addActionListener(controller);
		browse.addActionListener(controller);
		okButton.addActionListener(controller);
		cancelButton.addActionListener(controller);
		resetButton.addActionListener(controller);
		hprd.addActionListener(controller);
		mint.addActionListener(controller);
		intact.addActionListener(controller);
		table.addMouseListener(controller);
//		changeData.addActionListener(controller);
		changeDataAndClose.addActionListener(controller);
		cancel.addActionListener(controller);
	} 

	/**
	 * the initial the data preview table
	 */
	private void initTable() {	
		if (dataFile.getText().equals("")) {
			table = new JTable();
			table.setForeground(UIManager.getColor("Table.foreground"));
		} else {
			table = new JTable(model);
			table.setForeground(UIManager.getColor("Table.foreground"));
		}
		table.setRowSelectionAllowed(false);
		header = table.getTableHeader();
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setEnabled(false);
		table.setRowHeight(25);
		table.setRowMargin(10);
	}

	/**
	 * some messages to be shown on top of the main Frame and
	 * the actions after the "OK" button is pressed
	 * 
	 * @param e1 - the thrown exception 
	 */
	public void openMessage(Exception e1) {
		if (e1 instanceof ExcelException) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					done = true;
					closeProgressBarImport();
					MyPopUp.getInstance().show("Error", "This is an old Excel file. \n Please convert it to Excel 97+ !");
				}
			});	
		} else {
			e1.printStackTrace();
		}
	}

	/**
	 * simple FileChooser to open the data input file, in Excel format
	 * 
	 * @return File - the user selected file
	 */
	public File openFileChooser() {
		File file = null;
		JFileChooser chooser = new JFileChooser();
		chooser.setAcceptAllFileFilterUsed(false);

		//		TODO: perhaps add an import method for csv files		
		//		// FileFilter for csv files
		//		chooser.setFileFilter(new FileFilter() {
		//			public boolean accept(File f) {
		//				return f.getName().toLowerCase().endsWith(".csv") ||
		//						f.isDirectory();
		//			}
		//			public String getDescription() {
		//				return "comma separated file (.csv)";
		//			}
		//		});

		// FileFilter for Excel files (have to be 97+ or newer!)
		chooser.setFileFilter(new FileFilter() {
			public boolean accept(File f) {
				return f.getName().toLowerCase().endsWith(".xls") ||
						f.getName().toLowerCase().endsWith(".xlsx") ||
						f.isDirectory();
			}
			public String getDescription() {
				return "Excel File (.xls, .xlsx)";
			}
		});
		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			file = chooser.getSelectedFile().getAbsoluteFile();
			dataFile.setText(file.getPath());
		}
		return file;
	}

	/**
	 * opens the ProgressBar at the time when the input data has been read
	 */
	public void setProgressBarImport() {
		if(done == false) {
			progressBar.setVisible(true);  
			progressBar.setString("Open Data File");
			progressBar.setStringPainted(true);
			progressBar.setIndeterminate(true);
		}
	}	

	/**
	 * closes the ProgressBar after the JTable was updated, sets boolean done (whether
	 * the input is finished) to false after all work is done, after that it is possible
	 * to start a new ProgressBar
	 */
	public void closeProgressBarImport(){
		if(done==true) {
			progressBar.setVisible(false);
			done = false;
		}
	}

	/**
	 * @return the chosen identifier type
	 */
	public String getIdentifierType() {
		return (String) identifierCB.getSelectedItem();
	}
	
	/**
	 * @return the chosen identifier type
	 */
	public String getSpecies() {
		return (String) speciesCB.getSelectedItem();
	}

	/**
	 * @return the JTable for the data preview
	 */
	public JTable getTable() {
		return this.table;
	}

	/**
	 * @return the header of the JTable
	 */
	public JTableHeader getHeader() {
		return header;
	}

	/**
	 * is called after the coloring of the identifier column has been set
	 * 
	 * @param pick - the columns index
	 * @param col - the new color for highlighting
	 */
	public void setIdentifierTF(int pick, Color col) {
		identifierTF.setForeground(col);
		String textField = (String) header.getColumnModel().getColumn(pick).getHeaderValue();
		identifierTF.setText("[Col:" + (pick+1) + "] " + textField);
	}
	
	/**
	 * is called after the coloring of the value column has been set
	 * 
	 * @param picks - the columns indieces
	 * @param col - the new color for highlighting
	 */
	public void setMultiValueTF(Set<Integer> picks, Color col) {
		valueTF.setForeground(col);
		String text = "";
		for(int pick: picks){
			text += "[Col:" + (pick+1) + "]; ";
		}
		valueTF.setText(text);
	}

	/**
	 * resets the textFields for the selected identifier and values
	 */
	public void doReset() {
		identifierTF.setForeground(UIManager.getColor("TextField.foreground"));
		identifierTF.setText(clickHeader);
		valueTF.setForeground(UIManager.getColor("TextField.foreground"));
		valueTF.setText(clickHeader);
		if(model != null) {
			model.fireTableDataChanged();
		}
	}

	/**
	 * is called after the header has changed
	 */
	public void setHeader() {
		table.getTableHeader().resizeAndRepaint();
		headerTF.setText("Table header has been set");
	}

	/**
	 * @return - the name of the selected pathway
	 */
	public String getSelectedPathwayString() {
		return (String) pathwayCB.getSelectedItem();
	}

	/**
	 * @return - the pathway which is selected by the combobox and should be used for the mapping
	 */
	public Pathway getSelectedPathway() {
		return con.getPathway((String) pathwayCB.getSelectedItem());
	}

	/**
	 * opens the ProgressBarBiomart at the time when the query is send
	 */
	public void setProgressBarBiomart() {
		progressBarBiomart.setVisible(true);  
		progressBarBiomart.setString("The mapping may take some time!");
		progressBarBiomart.setStringPainted(true);
		progressBarBiomart.setIndeterminate(true);
	}	

	/**
	 * closes the ProgressBarBiomart 
	 */
	public void closeProgressbarBiomart(){
		progressBarBiomart.setVisible(false);
	}

	/**
	 * enables the "OK" button 
	 */
	public void disableOKbutton() {
		okButton.setEnabled(false);

	}

	/**
	 * @return - the JTable with the information about the mapping
	 */
	public JTable getDataMappingTable() {
		return dataMappingTable;
	}

	/**
	 * stores the currently selected pathway in a variable
	 * @param pathwayString - the currently selected one
	 */
	public void setSelectedPathwayString(String pathwayString) {
		storedPathway = pathwayString;

	}

	/**
	 * the boolean inToolSearch variable lets the updating for the pathway
	 * ComboBox decide if a new pathway or the current one should be selected.
	 * @param b - true if the search was doen inside the dataMapping false otherwise
	 */
	public void inToolSearch(boolean b) {
		inToolSearch = b;	
	}
}