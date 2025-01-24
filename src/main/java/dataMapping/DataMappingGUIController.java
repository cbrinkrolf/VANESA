package dataMapping;

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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import dataMapping.dataImport.ExcelException;
import database.ppi.PPISearch;
import graph.GraphContainer;
import graph.algorithms.NodeAttributeType;
import graph.jung.classes.MyGraph;
import gui.AsyncTaskExecutor;
import gui.MainWindow;

/**
 * This class manage the actions from the GUI and sends data to the DataMappingModelController
 *
 * @author dborck
 */
public class DataMappingGUIController implements ActionListener, MouseListener, WindowFocusListener {
    private DataMappingView dataMappingView;
    private DataMappingModelController dataMappingModelController;
    private boolean isSetHeader = false;
    private int identifierColumnIndex = -2;
    private HashSet<Integer> valueColumnIndices;
    private boolean isSetIdentifier = false;
    private boolean isSetValue = false;
    private final Color identifiereColor = Color.BLUE;
    private final Color valueColor = Color.RED;
    private String searchString;

    /**
     * adds the view (GUI) to this DataMappingGUIController
     *
     * @param dataMappingView - the GUI
     */
    public void addDataMappingView(DataMappingView dataMappingView) {
        this.dataMappingView = dataMappingView;
        valueColumnIndices = new HashSet<>();
    }

    /**
     * adds the dataMappingModelController to this DataMappingGUIController
     *
     * @param dataMappingModelController - which controls the data flow between this GUIcontroller and the model
     */
    public void addDataMappingModelController(DataMappingModelController dataMappingModelController) {
        this.dataMappingModelController = dataMappingModelController;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("browse")) {
            doBrowse();
        } else if (e.getActionCommand().equals("doMapping")) {
            doMapping();
            dataMappingView.disableOKbutton();
        } else if (e.getActionCommand().equals("identifier")) {
            if (!dataMappingView.getIdentifierType().equals("none")) {
                dataMappingModelController.setIdentifierType(dataMappingView.getIdentifierType());
            } else {
                dataMappingModelController.disableCheck(DataMappingModelController.IDENTIFIER_TYPE);
            }
        } else if (e.getActionCommand().equals("pathway")) {
            if (dataMappingView.getSelectedPathway() != null) {// a pathway is selected
                dataMappingModelController.setPathwayLabels(dataMappingView.getSelectedPathway());
            } else {// no pathway is selected
                dataMappingModelController.disableCheck(DataMappingModelController.NETWORK);
            }
        } else if (e.getActionCommand().equals("hprd")) {
            doPPISearch("HPRD", null, searchString, null);
        } else if (e.getActionCommand().equals("mint")) {
            doPPISearch("MINT", null, searchString, null);
        } else if (e.getActionCommand().equals("intact")) {
            doPPISearch("IntAct", null, searchString, null);
        } else if (e.getActionCommand().equals("reset")) {
            reset();
        } else if (e.getActionCommand().equals("cancel")) {
            reset();
            dataMappingView.dispose();
        } else if (e.getActionCommand().equals("changeData")) {
            JTable dmt = dataMappingView.getDataMappingTable();
            dataMappingModelController.setNewMergeMap(dmt);
        } else if (e.getActionCommand().equals("changeDataAndClose")) {
            JTable dmt = dataMappingView.getDataMappingTable();
            dataMappingModelController.setNewMergeMap(dmt);
            dataMappingView.dispose();
        } else if (e.getActionCommand().equals("save")) {
            String input = JOptionPane.showInputDialog(null, "Enter experiment label", "Savetag");
            if (input != null && input.length() > 0) {
                //get BNAs and mapping
                JTable dmt = dataMappingView.getDataMappingTable();
                HashMap<String, Double> labelsAndValues = new HashMap<>();
                //retrieve Label and corresponding expression value
                for (int i = 0; i < dmt.getRowCount(); i++) {
                    JRadioButton jButton = (JRadioButton) dmt.getValueAt(i, 3);
                    if (jButton.isSelected()) {
                        labelsAndValues.put((String) dmt.getValueAt(i, 0), Double.parseDouble((String) dmt.getValueAt(i, 2)));
                    }
                }
                //MAP from network label to BNA and save attribute
                // get network structure
                MainWindow w = MainWindow.getInstance();
                GraphContainer con = GraphContainer.getInstance();
                Pathway pw = con.getPathway(w.getCurrentPathway());
                MyGraph mg = pw.getGraph();
                for (BiologicalNodeAbstract bna : mg.getAllVertices()) {
                    String label = bna.getLabel();
                    if (labelsAndValues.containsKey(label)) {
                        bna.addAttribute(NodeAttributeType.EXPERIMENT, input, labelsAndValues.get(label));
                    }
                }
            }
        }
    }

    /**
     * sets the progress bar on the GUI and invokes the mapping in the ModelController
     */
    private void doMapping() {
        dataMappingView.setProgressBarBiomart();
        dataMappingModelController.setPathway(dataMappingView.getSelectedPathway());
     // CHRIS avoid SwingWorker
        SwingWorker<Void, Void> swingworker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                dataMappingModelController.setMultiValues(valueColumnIndices);
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
        SwingWorker<Void, Void> swingworker = new SwingWorker<>() {
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
            if (!isSetHeader) {
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
        for (int i = 0; i < headerData.size(); i++) {
            table.getTableHeader().getColumnModel().getColumn(i).setHeaderValue(headerData.get(i));
        }
        dataMappingView.setHeader();
    }

    /**
     * highlights the selected column for identifiers and values or resets the coloring if Point is null
     *
     * @param point - either the Point of the click event or null if this method is called from reset
     */
    private void doColumnColoring(Point point) {
        Color old = Color.GRAY;
        if (point == null) { // = reset
            if (!isSetIdentifier && !isSetValue) {
                // nothing needs to be reset
                return;
            }
            // the table color has to be reset
            if (isSetIdentifier) {
                dataMappingView.getTable().getColumnModel().getColumn(identifierColumnIndex).setCellRenderer(new ColorColumnRenderer(old));
                identifierColumnIndex = -2;
                isSetIdentifier = false;
            }
            if (isSetValue) {
                for (int index : valueColumnIndices) {
                    dataMappingView.getTable().getColumnModel().getColumn(index).setCellRenderer(new ColorColumnRenderer(old));
                }
                valueColumnIndices.clear();
                isSetValue = false;
            }
        } else { // column is selected
            int pick = dataMappingView.getHeader().columnAtPoint(point);
            // a header is clicked
            Color newColor = old;
            if (!isSetIdentifier && !isSetValue) {
                // no column has been selected yet
                newColor = identifiereColor;
                isSetIdentifier = true;
                identifierColumnIndex = pick;
                dataMappingView.setIdentifierTF(pick, newColor);
                dataMappingModelController.setIdentifiers(identifierColumnIndex);
            } else if (isSetIdentifier) {
                // one column (for the identifiers) has been selected
                if (pick == identifierColumnIndex) {
                    JOptionPane.showMessageDialog(null, "You could not choose the identifier as value,\nplease choose a different column or reset if you want a different identifier.", "Value has not been set.", JOptionPane.INFORMATION_MESSAGE);
                    return;
                } else if (valueColumnIndices.isEmpty()) {
                    newColor = valueColor;
                    valueColumnIndices.add(pick);
                    dataMappingView.setValueTF(pick, valueColor);
                    dataMappingModelController.doValueCheck();
                    isSetValue = true;
                } else if (valueColumnIndices.contains(pick)) {
                    valueColumnIndices.remove(pick);
                    if (valueColumnIndices.isEmpty()) {
                        isSetValue = false;
                        dataMappingModelController.disableCheck(DataMappingModelController.VALUES);
                    }
                    newColor = old;
                    dataMappingView.setValueTF(-2, valueColor);
                } else { // valueColumnIndices not empty, dose not contain pick and is unequal to identifierColumnIndex
                    JOptionPane.showMessageDialog(null, "You could not choose a second value,\nplease deselect the value column if you want to choose a different value\nor reset if you want a different identifier.", "No second value could be set.", JOptionPane.INFORMATION_MESSAGE);
                }
            }
            // change the color of selected column
            dataMappingView.getTable().clearSelection();
            dataMappingView.getTable().addColumnSelectionInterval(pick, pick);
            dataMappingView.getTable().getColumnModel().getColumn(pick).setCellRenderer(new ColorColumnRenderer(newColor));
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // pops up a menu and sets the search string for the PPISearch
        if (e.getComponent() instanceof JTable && SwingUtilities.isRightMouseButton(e)) {
            int row = ((JTable) e.getComponent()).rowAtPoint(e.getPoint());
            int col = ((JTable) e.getComponent()).columnAtPoint(e.getPoint());
            searchString = (String) ((JTable) e.getComponent()).getModel().getValueAt(row, col);
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
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    /**
     * inner class to manage the new coloring of the selected columns
     */
    private static class ColorColumnRenderer extends DefaultTableCellRenderer {
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
            cell.setForeground(fgndColor);
            return cell;
        }
    }

    /**
     * process the PPISearch, and updates the View after a new pathway has been added to the MainWindow
     */
    public void doPPISearch(String database, String fullName, String alias, String acNumber) {
        AsyncTaskExecutor.runUIBlocking("PPI search", () -> {
            dataMappingView.inToolSearch(true);
            switch (database) {
                case "HPRD":
                    PPISearch.requestHPRDEntries(fullName, alias, acNumber);
                    break;
                case "MINT":
                    PPISearch.requestMintEntries(fullName, alias, acNumber);
                    break;
                case "IntAct":
                    PPISearch.requestIntActEntries(fullName, alias, acNumber);
                    break;
            }
            dataMappingView.updatePathwayCB();
        });
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
