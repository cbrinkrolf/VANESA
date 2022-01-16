package gui.visualization;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.table.TableColumn;

import edu.uci.ics.jung.visualization.VisualizationServer.Paintable;
import graph.GraphInstance;
import graph.algorithms.gui.clusters.ClusterColorEditor;
import gui.LocalBackboardPaintable;
import gui.MainWindow;
import gui.MyAnnotation;
import miscalleanous.tables.MyTable;
import net.miginfocom.swing.MigLayout;

/**
 * 
 * @author mlewinsk Date July 2015
 *
 *         The PreRenderManager allows dynamic adjustment and/or managing of
 *         JUNG's prerender paintables.
 */
public class PreRenderManager implements ActionListener {

	private static HashMap<String, PreRenderManager> instances = new HashMap<>();

	private final String BACKBOARD_PAINT = "Backboard Paint", JUNG_ANNOTATION = "JUNG annotation";
	public final int ACTIVE = 0, NAME = 1, TYPE = 2, COLOR = 3, SHAPE = 4, SIZE = 5;

	private Object[][] rows;
	private String[] columnnames = { "active", "name", "type", "content", "shape", "size" };
	private HashMap<Integer, Object> tablecontent = new HashMap<>();

	private JButton confirmButton = new JButton("ok");
	private JButton[] buttons = { confirmButton };
	private JButton jungonoff = new JButton("on");
	private JButton backgroundsonoff = new JButton("on");
	private JButton addrenderer = new JButton("add Renderer");
	private JButton delrenderer = new JButton("del Renderer");

	private JOptionPane optionPane;
	private JFrame dialog;

	private JPanel mainPanel;
	private JScrollPane sp;

	private MyTable table;

	private PreRenderManagerTablemodel model;

	private PreRenderManager(String pathwayname) {

		fillTable();

		sp = new JScrollPane(table);
		sp.setPreferredSize(new Dimension(800, 400));
		MigLayout layout = new MigLayout();

		mainPanel = new JPanel(layout);

		mainPanel.add(new JLabel("Please select the renderer of interest"), "span 2");
		mainPanel.add(new JSeparator(), "gap 10, wrap 15, growx");
		mainPanel.add(sp, "span 4, growx");

		mainPanel.add(new JSeparator(), "span, growx, wrap 15, gaptop 10");

		mainPanel.add(new JLabel("JUNG: "));
		mainPanel.add(jungonoff, "wrap");

		mainPanel.add(new JLabel("BGs: "));
		mainPanel.add(backgroundsonoff, "wrap");

		mainPanel.add(new JSeparator(), "span, growx, gaptop 7 ");

		mainPanel.add(addrenderer, "span2, wrap");
		mainPanel.add(delrenderer, " span2, wrap");

		mainPanel.add(new JSeparator(), "span, growx, gaptop 7 ");

		confirmButton.addActionListener(this);
		confirmButton.setActionCommand("ok");

		jungonoff.addActionListener(this);
		jungonoff.setActionCommand("switchjung");

		backgroundsonoff.addActionListener(this);
		backgroundsonoff.setActionCommand("switchbackgrounds");

		addrenderer.addActionListener(this);
		addrenderer.setActionCommand("addrenderer");

		delrenderer.addActionListener(this);
		delrenderer.setActionCommand("delrenderer");

		optionPane = new JOptionPane(mainPanel, JOptionPane.PLAIN_MESSAGE);
		optionPane.setOptions(buttons);

		dialog = new JFrame("Prerender Settings for network: " + pathwayname);

		dialog.setContentPane(optionPane);
		dialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		dialog.setIconImages(MainWindow.getInstance().getFrame().getIconImages());

		// show
		dialog.pack();
		dialog.setLocationRelativeTo(MainWindow.getInstance().getFrame());
		dialog.setVisible(true);
		dialog.setResizable(false);

	}

	public void fillTable() {
		// get Pre renderers from Mygraph
		ArrayList<Paintable> paintables = new ArrayList<>();
		ArrayList<MyAnnotation> annotations = new ArrayList<>();
		for (Paintable p : GraphInstance.getMyGraph().getVisualizationViewer().getPreRenderers()) {
			if (p instanceof LocalBackboardPaintable) {
				paintables.add(p);
			}
		}
		// Get MyAnnotation objects from Annotationmanager
		for (MyAnnotation ma : GraphInstance.getMyGraph().getAnnotationManager().getAnnotations()) {
			annotations.add(ma);
		}

		// initiate table model
		rows = new Object[paintables.size() + annotations.size()][columnnames.length];

		int rowcounter = 0;
		for (Paintable p : paintables) {
			LocalBackboardPaintable lp = (LocalBackboardPaintable) p;
			tablecontent.put(rowcounter, lp);

			rows[rowcounter][ACTIVE] = lp.isActive();
			rows[rowcounter][NAME] = lp.getName();
			rows[rowcounter][TYPE] = BACKBOARD_PAINT;
			rows[rowcounter][COLOR] = lp.getBgcolor();
			rows[rowcounter][SHAPE] = lp.getShape();
			rows[rowcounter][SIZE] = lp.getDrawsize();
			rowcounter++;
		}

		for (MyAnnotation ma : annotations) {
			tablecontent.put(rowcounter, ma);
			rows[rowcounter][ACTIVE] = GraphInstance.getMyGraph().getAnnotationManager().isEnabled(ma);// ;
			rows[rowcounter][NAME] = ma.getName();
			rows[rowcounter][TYPE] = JUNG_ANNOTATION;
			rows[rowcounter][COLOR] = ma.getAnnotation().getPaint();
			rows[rowcounter][SHAPE] = ma.getText().length() > 0 ? "text: " + ma.getText() : "shape";
			rows[rowcounter][SIZE] = (int) ma.getShape().getWidth();
			rowcounter++;
		}

//				if(rowcounter == 0){
//					Object emptyrow = {null,null,null};
//					rows = new Object[1][columnnames.length];
//					rows[0] = emptyrow;
//				}
		initTable(rows, columnnames);

	}

	public void addRow(LocalBackboardPaintable lp) {
		Object newrow[] = new Object[columnnames.length];
		newrow[ACTIVE] = lp.isActive();
		newrow[NAME] = lp.getName();
		newrow[TYPE] = BACKBOARD_PAINT;
		newrow[COLOR] = lp.getBgcolor();
		newrow[SHAPE] = lp.getShape();
		newrow[SIZE] = lp.getDrawsize();

		model.addRow(newrow);
		tablecontent.put(model.getRowCount() - 1, lp);
//		model.addTablecontent(model.getRowCount()-1,lp);

	}

	public static PreRenderManager getInstance() {

		String pathwayname = MainWindow.getInstance().getCurrentPathway();
		PreRenderManager prm = instances.get(pathwayname);

		if (prm != null) {
			prm.dialog.setVisible(true);
		} else
//			try {
			prm = new PreRenderManager(pathwayname);
		instances.put(pathwayname, prm);
//			} catch (IndexOutOfBoundsException ioobe) {
//				//MARTIN Show empty table in PreRender Manager instead of creating a new renderer dialog
//				// show empty table if
//				new AddRendererDialog();
//				// JOptionPane.showMessageDialog(null,"No renderers present.");
//			}
		return prm;
	}

	private void initTable(Object[][] rows, String[] columNames) {

		model = new PreRenderManagerTablemodel(rows, columNames, tablecontent);

		table = new MyTable();
		table.setModel(model);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setColumnControlVisible(false);
		table.setFillsViewportHeight(true);
		table.setHorizontalScrollEnabled(true);
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setResizingAllowed(true);
		table.setDefaultRenderer(Color.class, new PreRenderManagerColorRenderer(true));
		table.setDefaultEditor(Color.class, new ClusterColorEditor());

		// MARTIN sorting an empty table results in nullpointers in
		// (TableSortController.java:120)
		table.setSortable(false);

		TableColumn shapeColumn = table.getColumnModel().getColumn(SHAPE);

		JComboBox<String> comboBox = new JComboBox<String>();
		comboBox.addItem("rect");
		comboBox.addItem("roundrect");
		comboBox.addItem("oval");
		comboBox.addItem("fadeoval");

		shapeColumn.setCellEditor(new DefaultCellEditor(comboBox));

	}

	public void actionPerformed(ActionEvent e) {
		String event = e.getActionCommand();
		switch (event) {
		case "ok":
			dialog.setVisible(false);
			break;
		case "switchjung":

			if (jungonoff.getText().equals("on")) {
				for (int i = 0; i < rows.length; i++) {
					if (rows[i][TYPE].equals(JUNG_ANNOTATION))
						table.setValueAt(false, i, ACTIVE);
				}
				jungonoff.setText("off");
			} else {
				for (int i = 0; i < rows.length; i++) {
					if (rows[i][TYPE].equals(JUNG_ANNOTATION))
						table.setValueAt(true, i, ACTIVE);
				}
				jungonoff.setText("on");
			}

			break;
		case "switchbackgrounds":

			if (backgroundsonoff.getText().equals("on")) {
				for (int i = 0; i < rows.length; i++) {
					if (rows[i][TYPE].equals(BACKBOARD_PAINT))
						table.setValueAt(false, i, ACTIVE);
				}
				backgroundsonoff.setText("off");
			} else {
				for (int i = 0; i < rows.length; i++) {
					if (rows[i][TYPE].equals(BACKBOARD_PAINT))
						table.setValueAt(true, i, ACTIVE);
				}
				backgroundsonoff.setText("on");
			}
			break;
		case "addrenderer":
			new AddRendererDialog(this);

			GraphInstance.getMyGraph().getVisualizationViewer().repaint();
			break;

		case "delrenderer":

			if (tablecontent.get(table.getSelectedRow()) instanceof LocalBackboardPaintable) {
				GraphInstance.getMyGraph().getVisualizationViewer()
						.removePreRenderPaintable((LocalBackboardPaintable) tablecontent.get(table.getSelectedRow()));
			} else if (tablecontent.get(table.getSelectedRow()) instanceof MyAnnotation) {
				GraphInstance.getMyGraph().getAnnotationManager()
						.remove((MyAnnotation) tablecontent.get(table.getSelectedRow()));
			}

			if (model.getRowCount() > 0) {
				int row = table.getSelectedRow();
				model.removeRow(row);
				// update tablecontent mapping (current row id -> paintable)
				HashMap<Integer, Object> contenttmp = new HashMap<>();

				TreeSet<Integer> sortset = new TreeSet<>(tablecontent.keySet());
				for (int id : sortset) {
					if (id < row)
						contenttmp.put(id, tablecontent.get(id));
					else if (id > row)
						contenttmp.put((id - 1), tablecontent.get(id));

				}
				model.updateContent(contenttmp);
				tablecontent = contenttmp;

			}

			GraphInstance.getMyGraph().getVisualizationViewer().repaint();
			break;

		default:
			break;
		}
	}
}
