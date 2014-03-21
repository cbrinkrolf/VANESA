package graph.algorithms.gui;

import graph.GraphInstance;
import graph.algorithms.DCBresultSet;
import graph.algorithms.DenselyConnectedBiclustering;
import graph.jung.classes.MyGraph;
import gui.MainWindowSingelton;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;

import net.infonode.tabbedpanel.titledtab.TitledTab;
import net.miginfocom.swing.MigLayout;
import biologicalElements.GraphElementAbstract;
import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class DenselyConnectedBiclusteringGUI implements ActionListener {

	JPanel p = new JPanel();
	GraphElementAbstract ab;
	GraphInstance graphInstance;
	boolean emptyPane = true;
	private TitledTab tab;

	private JButton calculate, applyNum, file;
	private JPanel densitypanel, attrdimpanel, attrnumpanel, attrpanel;
	private JSpinner attrdimspinner, attrnumspinner;
	private SpinnerNumberModel attrdimvalues, attrnumvalues;
	private JLabel attrlabel, attrminmax;
	private JFormattedTextField[] rangeField;
	private JFormattedTextField desityField;
	private JDialog dialog;
	private JFileChooser chooser;
	private JTextField path;
	
	MigLayout layout;
	boolean applyNumDone = false;
	boolean chooserDone = false;
	private double DENSITY_MIN = 0d;
	private double DENSITY_MAX = 1d;		
	private double ATTR_MIN = 0d;
	private double ATTR_MAX = 1000d;
	private double ATTRNUM_MIN = 1d;
	private double ATTRNUM_MAX = 20d;
	private double ATTRDIM_MIN = 0d;
	private double ATTRDIM_MAX = 10d;
	
	private JTable table;
	private LinkedList<DCBresultSet> results = new LinkedList<DCBresultSet>();

	public DenselyConnectedBiclusteringGUI() {

	}

	private void updateWindow() {

		calculate = new JButton("OK");

		attrnumpanel = new JPanel(new MigLayout("","[][]",""));
		
		attrnumvalues  = new SpinnerNumberModel(3d, ATTRNUM_MIN, ATTRNUM_MAX, 1d);		

		attrnumspinner = new JSpinner(attrnumvalues);	
		attrnumspinner.setPreferredSize(new Dimension(50, 10));
		
		attrnumpanel.add(attrnumspinner);
		attrnumpanel.add(new JLabel(" Min: " + ATTRNUM_MIN + " Max: "+ ATTRNUM_MAX));
		
		applyNum = new JButton("apply");
		
		applyNum.setToolTipText("Set number of attributes");
		applyNum.setActionCommand("applyNum");
		applyNum.addActionListener(this);

		attrpanel = new JPanel(new MigLayout("","[][]",""));
		
		attrlabel = new JLabel("Attribute ranges:");
		attrminmax = new JLabel("Min: " + ATTR_MIN + " Max: " + ATTR_MAX);
		
		attrpanel.setVisible(false);
		attrlabel.setVisible(false);
		attrminmax.setVisible(false);
		
		densitypanel = new JPanel(new MigLayout("","[][]",""));
		
		desityField = new JFormattedTextField(NumberFormat.getInstance());
		desityField.setPreferredSize(new Dimension(50, 10));
		
		desityField.setValue(0.5);
		
		densitypanel.add(desityField);
		densitypanel.add(new JLabel(" Min: " + DENSITY_MIN + " Max: " + DENSITY_MAX));
		
		
		attrdimpanel = new JPanel(new MigLayout("","[][]",""));
		
		attrdimvalues  = new SpinnerNumberModel(2d, ATTRDIM_MIN, ATTRDIM_MAX, 1d);		

		attrdimspinner = new JSpinner(attrdimvalues);	
		attrdimspinner.setPreferredSize(new Dimension(50, 10));
		
		attrdimpanel.add(attrdimspinner);
		attrdimpanel.add(new JLabel(" Min: " + ATTRDIM_MIN + " Max: " + ATTRDIM_MAX));

		calculate.setToolTipText("Do densely-connected biclustering");
		calculate.setActionCommand("calculate");
		calculate.addActionListener(this);
		calculate.setEnabled(false);
		
		file = new JButton("Add attribute-file");
		file.setToolTipText("File contains vertice attributes");
		file.setActionCommand("file");
		file.addActionListener(this);
		path = new JTextField("", 20);
		
		path.setEditable(false);
		path.setScrollOffset(1);



		layout = new MigLayout("", "[][grow]", "");
		p.setLayout(layout);
		p.add(new JLabel("Densely-connected biclustering:"),"wrap");
		p.add(new JLabel("Number of attributes:"),"wrap");
		p.add(attrnumpanel, "span 2, wrap");
		p.add(applyNum, "span 2, align right, wrap");
		p.add(attrlabel,"wrap");
		p.add(attrpanel, "span 2, wrap");
		p.add(new JLabel("Density:"),"wrap");
		p.add(densitypanel, "span 2, wrap");
		p.add(new JLabel("Number of similar attributes:"),"wrap");
		p.add(attrdimpanel, "span 2, wrap");
		p.add(calculate, "span 2, align right, wrap");
		p.add(path);
		p.add(file, "wrap");

	}

	public JPanel getPanel() {
		p.setVisible(false);
		return p;
	}

	public void revalidateView() {
		
		graphInstance = new GraphInstance();

		if (emptyPane) {
			updateWindow();
			p.repaint();
			p.revalidate();
			emptyPane = false;
		} else {
			p.removeAll();
			updateWindow();
			p.repaint();
			p.revalidate();
			p.setVisible(true);

		}
		tab.repaint();
		tab.revalidate();
	}


	public void actionPerformed(ActionEvent e) {

		String event = e.getActionCommand();
		Pathway pw = graphInstance.getPathway();
		pw.getGraph().setMouseModeTransform();


		if("file".equals(event)){
			chooser = new JFileChooser();
			int state = chooser.showOpenDialog(p);
			if(state == JFileChooser.APPROVE_OPTION){
				path.setText(chooser.getSelectedFile().getPath());
				
			}
			
			
			BufferedReader br = null;
			
			try{
				FileReader fr = new FileReader(chooser.getSelectedFile());
				br = new BufferedReader(fr);
				MyGraph mg = pw.getGraph();
				HashMap<Integer, BiologicalNodeAbstract> idBna = new HashMap<Integer, BiologicalNodeAbstract>();
				
				for(BiologicalNodeAbstract vertex1 : mg.getAllVertices()){
					idBna.put(vertex1.getID(), vertex1);
				}
				br.readLine(); //titel-line is not used
			    while(br.ready()){
			    	String line = br.readLine();
			    	String[] column = line.split("\t");
			    	int numOfVertices = Integer.parseInt(column[0]);
			    	double density = Double.parseDouble(column[1]);
			    	int numOfhomogenAttributes = Integer.parseInt(column[2]);
			    	String labels = column[3];
			    	
			    	String[] ids = column[4].split(" ");
			    	
			    	HashSet<BiologicalNodeAbstract> vertices = new HashSet<BiologicalNodeAbstract>();
			    	for(String id : ids){
			    		if(idBna.get(Integer.parseInt(id)) != null){
			    			vertices.add(idBna.get(Integer.parseInt(id)));
			    		}else{
			    			throw new IllegalArgumentException();
			    		}
			    	}
			    	
			    	DCBresultSet result = new DCBresultSet(numOfVertices, density, numOfhomogenAttributes, labels, vertices);
			    	results.add(result);
			    }
			    
			    table = initTable(results);
				openResultDialog(table);
		    
			}catch (IOException e1) {
				JOptionPane.showMessageDialog(
						null,
						"Couldn't read attribute file.",
						"Error", JOptionPane.ERROR_MESSAGE);
	
				e1.printStackTrace();
			}catch(NumberFormatException nfe){
				JOptionPane.showMessageDialog(
						null,
						"File has wrong format."
								+ "\nPlease use only files that are computed by DCB.",
								"Error", JOptionPane.ERROR_MESSAGE);
	
				nfe.printStackTrace();
			}catch(IllegalArgumentException iae){
				JOptionPane.showMessageDialog(
						null,
						"Attribute file dose not suit graph."
								+ "\nPlease load differend graph or attribute file.",
								"Error", JOptionPane.ERROR_MESSAGE);
	
				iae.printStackTrace();
			}finally{
				results = new LinkedList<DCBresultSet>();
				try {
					br.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		
		if ("applyNum".equals(event)){
			attrpanel.removeAll();
			int num = ((Double) attrnumspinner.getValue()).intValue();
			
			rangeField = new JFormattedTextField[num];	
			
			for(int i=0; i<num; i++){
				
				rangeField[i] = new JFormattedTextField(NumberFormat.getInstance());
				rangeField[i].setPreferredSize(new Dimension(50, 10));
				rangeField[i].setValue(0.5);

				if(i==0){
					attrpanel.add(rangeField[i]);
					attrpanel.add(attrminmax, "wrap");

				}else{
					attrpanel.add(rangeField[i], "wrap");
				}
				
				
			}
			
			applyNumDone = true;
			attrlabel.setVisible(true);
			attrminmax.setVisible(true);
			attrpanel.setVisible(true);
			calculate.setEnabled(true);
			attrpanel.updateUI();
			
		}
		if ("calculate".equals(event)) {
			
			boolean noMinMax = false;
			
			double density = ((Number)desityField.getValue()).doubleValue();
			if(density < DENSITY_MIN || density > DENSITY_MAX){
				noMinMax = true;
			}
			
			double[] ranges = new double[rangeField.length];
			
			for(int i=0; i<ranges.length; i++){
				ranges[i] = ((Number)rangeField[i].getValue()).doubleValue();
				if(ranges[i] < ATTR_MIN || ranges[i] > ATTR_MAX){
					noMinMax = true;
				}
			}
			
			double attrdim = (double) attrdimspinner.getValue();			

			if(noMinMax){
				JOptionPane.showMessageDialog(
						null,
						"Please consider minimum and maximum Values.",
						"Error", JOptionPane.ERROR_MESSAGE);
		
			}else if(attrdim >ranges.length){
				JOptionPane.showMessageDialog(
						null,
						"Number of similar attributes must not be greater than number of attributes.",
						"Error", JOptionPane.ERROR_MESSAGE);
		
			}else{
				DenselyConnectedBiclustering dcb = new DenselyConnectedBiclustering(density, ranges, attrdim);
	
				results = dcb.getResults(); 
	
				table = initTable(results);
				openResultDialog(table);
			
			}

		}
		if("dialog_choose".equals(event)){
	        int numRows = table.getRowCount();
	        javax.swing.table.TableModel model = table.getModel();

	        LinkedList<HashSet<BiologicalNodeAbstract>> selectedClusters = new LinkedList<HashSet<BiologicalNodeAbstract>>();
	        for (int i=0; i < numRows; i++) {
	        	
	        	if((boolean) model.getValueAt(i, 4)){
	        		selectedClusters.add(results.get(i).getVertices());
	        	}
	        }
	        
	    	MyGraph mg = pw.getGraph();

	    	Hashtable<BiologicalNodeAbstract, Double> coloring = new Hashtable<BiologicalNodeAbstract, Double>();
	        for(BiologicalNodeAbstract vertex : mg.getAllVertices()){
	        	coloring.put(vertex, 0.0);
	        }
	        
	        for(HashSet<BiologicalNodeAbstract> cluster : selectedClusters){
	        	for(BiologicalNodeAbstract vertex: cluster){
	        		coloring.put(vertex, coloring.get(vertex)+1);
	        	}
	        }

	        new GraphColorizer(coloring, 0, false);
	        
	        mg.getVisualizationViewer().repaint();
	        results = new LinkedList<DCBresultSet>();
	        dialog.dispose();
		}
		if("dialog_save".equals(event)){
			JFileChooser saver  = new JFileChooser();
			int state = saver.showSaveDialog(p);
			if(state == JFileChooser.APPROVE_OPTION){
				Writer fw = null;

				try{
				  fw = new FileWriter( saver.getSelectedFile() );
				  
				  fw.write("Size");
				  fw.write("\t");
				  fw.write("Density");
				  fw.write("\t");
				  fw.write("Number...");
				  fw.write("\t");
				  fw.write("Graph Labels");
				  fw.write("\t");
				  fw.write("Graph IDs");
				  fw.append( System.getProperty("line.separator") );
  
				  
				  for (DCBresultSet result : results) {

					  	fw.write(((Integer) result.getNumOfVertices()).toString());
					  	fw.write("\t");
					  	fw.write(((Double) ((Math.rint((result.getDensity())*1000))/1000)).toString());
					  	fw.write("\t");
					  	fw.write(((Integer) result.getNumOfhomogenAttributes()).toString());
					  	fw.write("\t");
					  	fw.write(result.getLabels());
					  	fw.write("\t");
					  	for(BiologicalNodeAbstract vertex : result.getVertices()){
					  		fw.write(((Integer) vertex.getID()).toString());
					  		fw.write(" ");
					  	} 
						fw.append( System.getProperty("line.separator") );
						
				  }


				}
				catch ( IOException e1 ) {
					JOptionPane.showMessageDialog(
							null,
							"File could not be saved.",
							"File not saved", JOptionPane.INFORMATION_MESSAGE);
					e1.printStackTrace();
				}
				finally {
				  if ( fw != null )
				    try { fw.close(); } catch ( IOException e2 ) { e2.printStackTrace(); }
				}
			}
			
		}
		if("dialog_cancel".equals(event)){
			results = new LinkedList<DCBresultSet>();
			dialog.dispose();
			
		}
	}

	private void openResultDialog(JTable table) {
		
		if(table != null){
			JScrollPane sp = new JScrollPane(table);
			JButton choose = new JButton("ok");
			choose.setToolTipText("Choose Clusters");
			choose.setActionCommand("dialog_choose");
			choose.addActionListener(this);
			JButton save = new JButton("save");
			save.setToolTipText("Save Clusters");
			save.setActionCommand("dialog_save");
			save.addActionListener(this);
			JButton cancel = new JButton("cancel");
			cancel.setActionCommand("dialog_cancel");
			cancel.addActionListener(this);

			JPanel dialogPane = new JPanel(new MigLayout("","[]",""));

			dialogPane.add(sp, "wrap");
			
			JPanel buttonPane = new JPanel(new MigLayout("","[][][]",""));
			buttonPane.add(choose);
			buttonPane.add(save);
			buttonPane.add(cancel);
			
			dialogPane.add(buttonPane, "align Center");
			choose.setVisible(true);

			JOptionPane optionPane = new JOptionPane(
					dialogPane,
					JOptionPane.PLAIN_MESSAGE,
					JOptionPane.DEFAULT_OPTION,
			        null, new Object[]{}, null);
			
			dialog = optionPane.createDialog( MainWindowSingelton.getInstance(), "Choose Cluster");
			dialog.setVisible(true);

		}else{
			JOptionPane.showMessageDialog(
					null,
					"No Clusters existing for this parameters.",
					"No Results", JOptionPane.INFORMATION_MESSAGE);
		}

	}
	
	
	private JTable initTable(LinkedList<DCBresultSet> results) {
		String[] columNames = { "Size", "Density", "Number of homogen attributes", "Graph", "Check"};
		Object[][] rows = new Object[results.size()][5];
		int iterator_count = 0;

		for (DCBresultSet result : results) {

			rows[iterator_count][0] = ((Integer) result.getNumOfVertices()).toString();
			rows[iterator_count][1] = ((Double) ((Math.rint((result.getDensity())*1000))/1000)).toString();
			rows[iterator_count][2] = ((Integer) result.getNumOfhomogenAttributes()).toString();
			rows[iterator_count][3] = result.getLabels(); 
			rows[iterator_count][4] = false;
			
			iterator_count++;
		}
		
		JTable table = null;

		if(rows.length != 0){
			@SuppressWarnings("serial")
			DefaultTableModel model = new DefaultTableModel(rows, columNames) {
			      @Override public Class<?> getColumnClass(int column) {
			        return getValueAt(0, column).getClass();
			      }
			};

			table = new JTable(model);
			
			table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			table.setRowSelectionAllowed(true);
			table.setColumnSelectionAllowed(false);
		}

		return table;

	}

	public TitledTab getTitledTab() {

		tab = new TitledTab("DCB", null, getPanel(), null);
		return tab;
	}

}
