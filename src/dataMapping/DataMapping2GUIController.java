package dataMapping;

import gui.ProgressBar;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.HashSet;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import dataMapping.dataImport.ExcelException;
import database.ppi.PPISearch;


/**
 * This class manage the actions from the GUI and sends data to the DataMappingModelController
 * @author dborck
 * @author bniemann
 *
 */
public class DataMapping2GUIController implements ActionListener, MouseListener{

	private DataMapping2View dataMappingView;
	private DataMappingModelController dataMappingModelController;

	private boolean isSetHeader = false;

	private int identifierColumnIndex = -2;
//	private int valueColumnIndex = -2;
	private HashSet<Integer> valueColumnIndices;
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
	public void addDataMappingView(DataMapping2View dataMappingView) {
		this.dataMappingView = dataMappingView;
		valueColumnIndices = new HashSet<Integer>();
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
		} else if(e.getActionCommand().equals("species")){
			if(!dataMappingView.getSpecies().equals("none")) {
				dataMappingModelController.setSpecies(dataMappingView.getSpecies());
			} else {
				dataMappingModelController.disableCheck(DataMappingModelController.SPECIES);
			}
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
			dataMappingView.dispose();
//		} else if(e.getActionCommand().equals("changeData")) {
//			JTable dmt = dataMappingView.getDataMappingTable();
//			dataMappingModelController.setNewMergeMap(dmt);
		} else if(e.getActionCommand().equals("changeDataAndClose")) {
			JTable dmt = dataMappingView.getDataMappingTable();
			dataMappingModelController.setNewMergeMap(dmt);
			dataMappingView.dispose();
			
		} else if(e.getActionCommand().equals("cancelChange")) {
			dataMappingView.dispose();
		}
		
		
	}

	/**
	 * sets the progress bar on the GUI and invokes the mapping in the ModelController
	 */
	private void doMapping() {
		dataMappingView.setProgressBarBiomart();

		
		//System.out.println("species:"+dataMappingView.getSelectedPathway().getOrganism()); // does only function with KEGG pathways

		SwingWorker<Void, Void> swingworker = new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				//TODO
				long start = System.currentTimeMillis();

				dataMappingModelController.setMultiValues(valueColumnIndices);
				dataMappingModelController.startMapping();
				
				long end = System.currentTimeMillis();
				System.out.println("Zeit fuer Query: " + (end-start));
				
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
		dataMappingModelController.setHeaderIndex(headerRow);
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
		Color old = Color.GRAY;
		if(point == null) { // = reset
			if(!isSetIdentifier && !isSetValue) {
				// nothing needs to be reseted
				return;
			}
			// the table color has to be reseted

			if(isSetIdentifier) {
				dataMappingView.getTable().getColumnModel()
				.getColumn(
						identifierColumnIndex
						).setCellRenderer(new ColorColumnRenderer(old));
				identifierColumnIndex = -2;
				isSetIdentifier = false;
			}

			if(isSetValue) {
				for(int index : valueColumnIndices){
					dataMappingView.getTable().getColumnModel()
					.getColumn(
							index
							).setCellRenderer(new ColorColumnRenderer(old));
				}
				valueColumnIndices.clear();
				isSetValue = false;
			}
		} else { // column is selected
			
			int pick = dataMappingView.getHeader().columnAtPoint(point);
			// a header is clicked
			Color newColor = old;
			if (!isSetIdentifier && !isSetValue){
				// no column has been selected yet
				newColor = identifiereColor;
				isSetIdentifier = true;
				identifierColumnIndex = pick;
				dataMappingView.setIdentifierTF(pick, newColor);
				dataMappingModelController.setIdentifiers(identifierColumnIndex);
			}else if (isSetIdentifier) {
				// one column (for the identifiers) has been selected
				if(pick == identifierColumnIndex){
					JOptionPane.showMessageDialog(
							null,
							"You could not choose the identifier as value,\n"
									+ "please choose a different column or reset if "
									+ "you want a different identifier.",
							"Value has not been set.",
									JOptionPane.INFORMATION_MESSAGE);
					newColor = identifiereColor;
					return;
				}else if(valueColumnIndices.contains(pick)) {
					valueColumnIndices.remove(pick);
					if(valueColumnIndices.isEmpty()){
						isSetValue = false;
						dataMappingModelController.disableCheck(DataMappingModelController.VALUES);
					}
					newColor = old;	
					dataMappingView.setMultiValueTF(valueColumnIndices, valueColor);
				} else {
					newColor = valueColor;
					valueColumnIndices.add(pick);
					dataMappingView.setMultiValueTF(valueColumnIndices, valueColor);
					dataMappingModelController.doValueCheck();
					
					isSetValue = true;
				}
			} 
			// change the color of selected column
			dataMappingView.getTable().clearSelection();
			dataMappingView.getTable().addColumnSelectionInterval (pick, pick);
			dataMappingView.getTable().getColumnModel().getColumn(pick).setCellRenderer(new ColorColumnRenderer(newColor));
		}
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

		PPISearch search = new PPISearch(input, DataMappingView.w, false);
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

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
