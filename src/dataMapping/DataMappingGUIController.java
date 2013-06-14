package dataMapping;

import gui.ProgressBar;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.File;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import dataMapping.dataImport.ExcelException;
import database.ppi.PPISearch;


/**
 * This class manage the actions from the GUI and sends data to the DataMappingModelController
 * @author dborck
 *
 */
public class DataMappingGUIController implements ActionListener, MouseListener, WindowFocusListener{

	private DataMappingView dataMappingView;
	private DataMappingModelController dataMappingModelController;

	private boolean isSetHeader = false;

	private int identifierColumnIndex = -2;
	private int valueColumnIndex = -2;
	private boolean isSetIdentifier = false;
	private boolean isSetValue = false;

	private Color identifiereColor = Color.BLUE;
	private Color valueColor = Color.RED;

	private String searchString;
	boolean searchReady;

	/**
	 * adds the view (GUI) to this DataMappingGUIController
	 * @param dataMappingView - the GUI
	 */
	public void addDataMappingView(DataMappingView dataMappingView) {
		this.dataMappingView = dataMappingView;
	}

	/**
	 * adds the dataMappingModelController to this DataMappingGUIController
	 * @param dataMappingModelController - which controls the data flow between this GUIcontroller and the model 
	 */
	public void addDataMappingModelController(DataMappingModelController dataMappingModelController) {
		this.dataMappingModelController = dataMappingModelController;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("browse")) {
			doBrowse();
		} else if(e.getActionCommand().equals("doMapping")) {
			doMapping();
			dataMappingView.disableOKbutton();
		} else if(e.getActionCommand().equals("identifier")){
			if(!dataMappingView.getIdentifierType().equals("none")) {
				dataMappingModelController.setIdentifierType(dataMappingView.getIdentifierType());
			} else {
				dataMappingModelController.disableCheck(DataMappingModelController.IDENTIFIER_TYPE);
			}
		} else if(e.getActionCommand().equals("pathway")){
			if (dataMappingView.getSelectedPathway()!=null) {// a pathway is selected
				dataMappingModelController.setPathwayLabels(dataMappingView.getSelectedPathway());
			} else {// no pathway is selected
				dataMappingModelController.disableCheck(DataMappingModelController.NETWORK);
			}
		} else if(e.getActionCommand().equals("hprd")) {
			// input matches the search string for an alias search (arg[2])
			String[] input = {"HPRD", "", searchString, ""};
			doPPISearch(input);
		} else if(e.getActionCommand().equals("mint")) {
			// input matches the search string for an alias search (arg[2])
			String[] input = {"MINT", "", searchString, ""};
			doPPISearch(input);
		} else if(e.getActionCommand().equals("intact")) {
			// input matches the search string for an alias search (arg[2])
			String[] input = {"IntAct", "", searchString, ""};
			doPPISearch(input);
		} else if(e.getActionCommand().equals("reset")) {
			reset();
		} else if(e.getActionCommand().equals("cancel")) {
			reset();
			DataMappingView.w.setEnable(true);
			dataMappingView.dispose();
		} else if(e.getActionCommand().equals("changeData")) {
			JTable dmt = dataMappingView.getDataMappingTable();
			dataMappingModelController.setNewMergeMap(dmt);
		}
	}

	/**
	 * sets the progress bar on the GUI and invokes the mapping in the ModelController
	 */
	private void doMapping() {
		dataMappingView.setProgressBarBiomart();
		dataMappingModelController.setPathway(dataMappingView.getSelectedPathway());
		//System.out.println("species:"+dataMappingView.getSelectedPathway().getOrganism()); // does only function with KEGG pathways

		SwingWorker<Void, Void> swingworker = new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				dataMappingModelController.startMapping();
				return null;
			}
		};
		swingworker.execute();
	}

	/**
	 * resets that a header is set, the coloring of the selected columns, and
	 * calls the view to reset the textFields
	 */
	private void reset() {
		isSetHeader = false;
		doColumnColoring(null);
		dataMappingModelController.setChecks();
		dataMappingModelController.resetModel();
		dataMappingView.doReset();
	}

	/**
	 * manage the FileChooser, the progressbar, and the data input
	 */
	private void doBrowse() {
			final File file = dataMappingView.openFileChooser();
			dataMappingView.setProgressBarImport();

			SwingWorker<Void, Void> swingworker = new SwingWorker<Void, Void>() {
				@Override
				protected Void doInBackground() throws Exception {
					try {
						dataMappingModelController.openFile(file);
					} catch (ExcelException em) {
						dataMappingView.openMessage(em);
					} 
					return null;
				}
				@Override
				protected void done() {
					dataMappingView.closeProgressBarImport();
					reset();
				}
			};
			swingworker.execute();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// manage the selection of the columns for the identification of the identifier and values
		if (e.getComponent() instanceof JTableHeader) {
			doColumnColoring(e.getPoint());
		}
		// a cell with header information has been clicked 
		else if (e.getComponent() instanceof JTable) {
			if(!isSetHeader) {
				setHeader(e.getPoint());
				isSetHeader = true;
			}
		}	
	}

	/**
	 * change the default (first) header row to the user selected one
	 * 
	 * @param point - the point of the click event
	 */
	private void setHeader(Point point) {
		JTable table = dataMappingView.getTable();
		int headerRow = table.rowAtPoint(point);
		Vector<String> headerData = dataMappingModelController.getHeaderData(headerRow);
		int index = 0;
		for(String header : headerData) {
			table.getTableHeader().getColumnModel().getColumn(index).setHeaderValue(header);
			index += 1;
		}
		dataMappingView.setHeader();
	}

	/**
	 * highlights the selected column for identifiers and values
	 * or resets the coloring if Point is null
	 * 
	 * @param point - either the Point of the click event or null if this method is called from reset
	 */
	private void doColumnColoring(Point point) {
		if(point == null) {
			if(!isSetIdentifier && !isSetValue) {
				// nothing needs to be reseted
				return;
			}
			// the table color has to be reseted
//			Color old = UIManager.getColor("Table.foreground"); // that is not the right color
//			TODO: get the right color from the L&F			
			Color old = Color.GRAY;
			if(identifierColumnIndex!= -2) {
				dataMappingView.getTable().getColumnModel()
				.getColumn(
						identifierColumnIndex
						).setCellRenderer(new ColorColumnRenderer(old));
				identifierColumnIndex = -2;
				isSetIdentifier = false;
			}

			if(valueColumnIndex!= -2) {
				dataMappingView.getTable().getColumnModel()
				.getColumn(
						valueColumnIndex
						).setCellRenderer(new ColorColumnRenderer(old));
				valueColumnIndex = -2;
				isSetValue = false;
			}
		} else if (point != null) {
			// a header is clicked
			if(!(identifierColumnIndex == -2) && !(valueColumnIndex == -2)) {
				// both columns have been selected
				// TODO: throw Exception
				System.out.println("Please reset first than select new columns, do you want to reset your choise?");
				return;
			} else {
				Color newColor = null;
				if (identifierColumnIndex == -2 && valueColumnIndex == -2){
					// no column has been selected yet
					newColor = identifiereColor;
					isSetIdentifier = true;
				} else if (!(identifierColumnIndex == -2) && valueColumnIndex == -2) {
					// one column (for the identifiers) has been selected
					if(dataMappingView.getHeader().columnAtPoint(point) == identifierColumnIndex) {
						// TODO: throw Exception
						System.out.println("You cannot choose the same column for the values, do you want to reset your choise?");
						return;
					} else {
						newColor = valueColor;
						isSetValue = true;
					}
				} 
				int pick = dataMappingView.getHeader().columnAtPoint(point);
				// save the selected columnIndex
				if(isSetIdentifier && !isSetValue) {
					identifierColumnIndex = pick;
					dataMappingView.setIdentifierTF(pick, newColor);
					dataMappingModelController.setIdentifiers(identifierColumnIndex);
				} else if(isSetValue && isSetIdentifier) {
					valueColumnIndex = pick;
					dataMappingView.setValueTF(pick, newColor);
					dataMappingModelController.setValues(valueColumnIndex);
				}
				// change the color of selected column
				dataMappingView.getTable().clearSelection();
				dataMappingView.getTable().addColumnSelectionInterval (pick, pick);
				dataMappingView.getTable().getColumnModel().getColumn(pick).setCellRenderer(new ColorColumnRenderer(newColor));
			}
		}		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// pops up an menu and sets the search string for the PPISearch
		if (e.getComponent() instanceof JTable && SwingUtilities.isRightMouseButton(e)) {
			int row = ((JTable) e.getComponent()).rowAtPoint(e.getPoint());
			int col = ((JTable) e.getComponent()).columnAtPoint(e.getPoint());
			searchString =(String) ((JTable) e.getComponent()).getModel().getValueAt(row, col);
			if (searchString.contains("_")) {
				searchString = searchString.split("_")[0];
			}
			dataMappingView.maybeShowPopup(e);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getComponent() instanceof JTable && SwingUtilities.isRightMouseButton(e)) {
			dataMappingView.maybeShowPopup(e);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	/**
	 * inner class to manage the new coloring of the selected columns
	 * @author adapted from www
	 *
	 */
	private class ColorColumnRenderer extends DefaultTableCellRenderer 
	{
		private static final long serialVersionUID = 1L;
		Color fgndColor;

		public ColorColumnRenderer(Color fgnd) {
			super(); 
			fgndColor = fgnd;
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int column) {
			Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			cell.setForeground( fgndColor );
			return cell;
		}
	}

	/**
	 * process the PPISearch, and updates the View after a new pathway has been added to the MainWindow 
	 * @param input - the String[] for the PPISearch
	 */
	public void doPPISearch(String[] input) {

		PPISearch search = new PPISearch(input, DataMappingView.w, new ProgressBar());
		search.execute();

		SwingWorker<Void, Void> swingworker = new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground(){
				dataMappingView.inToolSearch(true);
				searchReady = false;
				while (!searchReady){
					searchReady = dataMappingView.isPathwayCBupdated();	
				}
				return null;
			}
			@Override
			protected void done() {
				dataMappingView.updatePathwayCB();
			}
		};
		swingworker.execute();

	}

	@Override
	public void windowGainedFocus(WindowEvent e) {
		dataMappingView.updatePathwayCB();	
	}

	@Override
	public void windowLostFocus(WindowEvent e) {
		dataMappingView.setSelectedPathwayString(dataMappingView.getSelectedPathwayString());
	}
}
