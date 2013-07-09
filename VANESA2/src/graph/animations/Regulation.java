package graph.animations;

import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.ConstantVertexAspectRatioFunction;
import edu.uci.ics.jung.utils.UserData;
import graph.GraphInstance;
import graph.jung.graphDrawing.NodeRankingVertexSizeFunction;
import graph.jung.graphDrawing.VertexShapes;
import gui.MainWindow;
import gui.MainWindowSingelton;
import gui.algorithms.ScreenSize;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableColumn;

import miscalleanous.tables.MyTable;
import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import biologicalElements.GraphElementAbstract;
import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class Regulation implements ActionListener, ChangeListener{

	// get instance of main window
	MainWindow w = MainWindowSingelton.getInstance();
	
	// create GUI components
	private JButton newButton = new JButton("exit");
	private JButton showPanel = new JButton("hide parameters");
	private JButton drawPCP = new JButton("Draw Timeseries");
	private JButton add = new JButton("add column");
	private JButton delete = new JButton("delete column");
	private JButton reset = new JButton("reset all values");
	private JSlider slider = new JSlider();
	private JPanel mainPanel;
	private JComboBox stepSelector;
	private JOptionPane optionPane;
	private JDialog dialog;

	private RegulationTabelModel model;
	private MyTable table;
	private boolean show = true;
	
	// prepare data sets
	private Hashtable<Integer,BiologicalNodeAbstract> nodeTabel = new Hashtable<Integer, BiologicalNodeAbstract>();
	private GraphInstance graphInstance;
	
	public Regulation() {
		
		// get pathway and nodes
		graphInstance = new GraphInstance();
		Pathway pw = graphInstance.getPathway();
		Collection<BiologicalNodeAbstract> hs = pw.getAllNodes();
		
		// construct random data set
		// TODO: remove this when "true" data will be stored in the biological nodes
		int rowDim = 5;
		Vector<Double> newmicroArrayData;
		int rowSize = 0;
		BiologicalNodeAbstract bna;
		if(hs != null) {
			Iterator<BiologicalNodeAbstract> it = hs.iterator();
			while(it.hasNext()) {
				newmicroArrayData = new Vector<Double>();
				for(int i = 0; i < rowDim; i++) {
					newmicroArrayData.add(i, Math.random());
				}
				bna = it.next();
				bna.setPetriNetSimulationData(newmicroArrayData);
				rowSize++;
			}
		}
		// end of remove. how to find out rowDim???
		
		
		// get microarray data out of nodes. iterates over BiologicalNodeAbstracts,
		// not JUNG vertices. Each expression value is stored in rows[][].
		Iterator<BiologicalNodeAbstract> it = hs.iterator();
		Object[][] rows = new Object[rowSize][rowDim+1];
		int i = 0;
		Vector<Double> MAData;
		while(it.hasNext()) {
			bna = it.next();
			MAData = bna.getPetriNetSimulationData();
			rows[i][0] = bna.getName();
			for(int j = 1; j <= MAData.size(); j++) {
				rows[i][j] = MAData.get(j-1);
			}
			i++;
		}
		
		// save current dataset in graph instance for further usage in PCP
		//graphInstance.setMADataset(rows, rowSize, rowDim);
		
		// create column labels for table view
		String columNames[] = new String[rowDim+1];
		String selectorValues[] = new String[rowDim];
		columNames[0] = "Element name";
		for(i = 1; i <= rowDim; i++) {
			columNames[i] = "t=" + i;
			selectorValues[i-1] = "t=" + i; 
		}

		// fill table with values and labels
		initTable(rows, columNames);
		
		// build GUI
		stepSelector = new JComboBox(selectorValues);
		
		JScrollPane sp = new JScrollPane(table);
		sp.setPreferredSize(new Dimension(800, 400));
		MigLayout layout = new MigLayout();

		mainPanel = new JPanel(layout);
		mainPanel.add(new JLabel("Animation"),"span 2");
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
		
		drawPCP.addActionListener(this);
		drawPCP.setActionCommand("drawPCP");

		slider.setPaintLabels(true);
		slider.setMinimum(1);
		slider.setMaximum(rowDim);
		slider.setMajorTickSpacing(1);
		slider.addChangeListener(this);
		
		Object[] options = {slider,showPanel,newButton,stepSelector,drawPCP};
		optionPane = new JOptionPane(mainPanel, JOptionPane.PLAIN_MESSAGE);
		optionPane.setOptions(options);

		dialog = new JDialog();
		dialog.setContentPane(optionPane);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		ScreenSize screen = new ScreenSize();
		int screenHeight = (int) screen.getheight();
		int screenWidth  = (int) screen.getwidth();
		
		dialog.pack();
		dialog.setLocation((screenWidth / 2) - dialog.getSize().width / 2,
				(screenHeight / 2) - dialog.getSize().height / 2);
		dialog.setVisible(true);
	}
	
	private void initTable(Object[][] rows, String[] columNames) {

		model = new RegulationTabelModel(rows,columNames,nodeTabel);

		table = new MyTable();
		table.setModel(model);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setColumnControlVisible(false);
		table.setHighlighters(HighlighterFactory.createSimpleStriping());
		table.setFillsViewportHeight(true);
		table.addHighlighter(new ColorHighlighter(new Color(192, 215, 227),Color.BLACK));
		table.setHorizontalScrollEnabled(true);
		table.getTableHeader().setReorderingAllowed(true);
		table.getTableHeader().setResizingAllowed(true);
		table.getColumn( "Element name" ).setPreferredWidth(  100 ); 
		table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
	}
	
	public void actionPerformed(ActionEvent e) {
		String event = e.getActionCommand();

		if ("ok".equals(event)) {		
			dialog.setVisible(false);
		}else if ("add".equals(event)) {	
             String header = "t=" + (table.getColumnCount());
             int rows = table.getRowCount();
             Vector<Double> v = new Vector<Double>();
             for (int j = 0; j < rows; j++) {
                 v.add(0.0);
             }
             
             slider.setMaximum(slider.getMaximum()+1);
             
             Enumeration<BiologicalNodeAbstract> en = nodeTabel.elements();
             while(en.hasMoreElements()){
            	 //en.nextElement().setAnimationValue(slider.getMaximum()+1, 0);
             }
                  
             model.addColumn( header, v );
             
		}else if ("delete".equals(event)) {	
			
		    int columns = model.getColumnCount();
		    
            if (columns > 2) {
                if (!table.getAutoCreateColumnsFromModel()) {
                    int view = table.convertColumnIndexToView(columns - 1);
                    TableColumn column = table.getColumnModel().getColumn(view);
                    table.getColumnModel().removeColumn( column );
                }
                slider.setMaximum(slider.getMaximum()-1);
                
                Enumeration<BiologicalNodeAbstract> en = nodeTabel.elements();
                while(en.hasMoreElements()){
               	 //en.nextElement().removeAnimationValue(slider.getMaximum()-1);
                }
                
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
		}else if(event.equals("drawPCP")) {
			// redraw PCP in side bar
			//w.updatePCPView(graphInstance, stepSelector.getSelectedIndex());
		}
	}

	/**
	 * Handles changes of the timestep slider.
	 * Calculates new size and color for each biological node apparent in 
	 * the pathway. 
	 */
	public void stateChanged(ChangeEvent e) {
		
		// create node color set
		// 0 -> blue -> lower expression
		// 1 -> red  -> higher expression
		// 2 -> gray -> no change in expression
		Vector<Integer> colors = new Vector<Integer>();
		colors.add(0,0x0000ff);
		colors.add(1,0xff0000);
		colors.add(2,0xdedede);
		
		// init loop variables
		Double val;
		Double ref;
		int take = 0;
		Vertex v;
		
		// get pathway and iterate over its JUNG vertices
		Pathway pw = graphInstance.getPathway();
		Set<Vertex> ns = pw.getGraph().getAllVertices();
		if(ns != null) {
			Iterator<Vertex> it = ns.iterator();
			while(it.hasNext()) {
				v = it.next();
				
				// cast to biological node type and retrieve microarray value for current timestep
				BiologicalNodeAbstract bna = (BiologicalNodeAbstract) pw.getElement(v);
				val = bna.getMicroArrayValue(slider.getValue()-1);
				
				// prepare size modification
				v.setUserDatum("madata",0.4,UserData.SHARED);
				NodeRankingVertexSizeFunction sf = new NodeRankingVertexSizeFunction("madata",val);
				VertexShapes vs = new VertexShapes(sf, new ConstantVertexAspectRatioFunction(1.0f));

				// get microarray value for reference timestep and compare it with the current value
				ref = bna.getMicroArrayValue(stepSelector.getSelectedIndex());
				if(val > ref)
					take = 1;
				else if(val < ref)
					take = 0;
				else
					take = 2;
				
				// apply change in color and size
				bna.setColor(new Color(colors.get(take)));
				bna.rebuildShape(vs);
			}
		}
		
		// refresh GUI
		pw.getGraph().restartVisualizationModel();
		w.repaint();
	}

}
