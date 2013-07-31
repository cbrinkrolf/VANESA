package gui;

import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.annotations.AnnotatingGraphMousePlugin;
import edu.uci.ics.jung.visualization.annotations.AnnotatingModalGraphMouse;
import edu.uci.ics.jung.visualization.annotations.AnnotationControls;
import edu.uci.ics.jung.visualization.annotations.AnnotationManager;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import graph.GraphInstance;
import gui.images.ImagePath;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RectangularShape;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.thoughtworks.xstream.mapper.AnnotationMapper;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class AnnotationPainter{

	private static AnnotationPainter instance;
	public static final int RECTANGLE = 0, ELLIPSE = 1, POLYGON = 3;
	private int currentRangeType = RECTANGLE;
	private boolean enabled;
	private List<Action> selectShapeActions = new ArrayList();
	private List<Action> selectColorActions = new ArrayList();
	private ImagePath imagePath = ImagePath.getInstance();
	private JMenuItem dropRange;
	private Color fillColor = Color.cyan;
	private Color textColor = Color.black;
	private int alpha = 150;
	private GraphInstance graphInstance;

	public static AnnotationPainter getInstance() {
		if (instance == null) {
			instance = new AnnotationPainter();
		}
		return instance;
	}

	public AnnotationPainter() {
		this.initShapeActions();
		this.initColorActions();
		
		
	}
	
	private void initShapeActions() {
		Action a = new AbstractAction() {

			public void actionPerformed(ActionEvent e) {
				setCurrentRangeType(RECTANGLE);
				//System.out.println("set rect");
			}
		};
		this.initAction(a, "rectangle.png", "rectangle");
		this.selectShapeActions.add(a);
		a = new AbstractAction() {

			public void actionPerformed(ActionEvent e) {
				setCurrentRangeType(ELLIPSE);
				//System.out.println("set ell");
			}
		};
		this.initAction(a, "ellipse.png", "ellipse");
		this.selectShapeActions.add(a);
		
	}
	
	private void initColorActions() {
		Action a = new AbstractAction() {

			public void actionPerformed(ActionEvent e) {
				AnnotationPainter.this.setFillColor(getColor(fillColor));
			}
		};
		this.initAction(a, "comparison.png", "select range color");
		this.selectColorActions.add(a);
		a = new AbstractAction() {

			public void actionPerformed(ActionEvent e) {
				AnnotationPainter.this.setTextColor(getColor(textColor));
			}
		};
		this.initAction(a, "font.png", "select text color");
		this.selectColorActions.add(a);
	}
	
	public List<Action> getSelectShapeActions() {
		return selectShapeActions;
	}
	
	public List<Action> getSelectColorActions() {
		return selectColorActions;
	}
	

	public void setCurrentRangeType(int currentRangeType) {
		this.currentRangeType = currentRangeType;
		setEnabled(true);
		
			//GraphInstance.getMyGraph().setMouseModeSelectRange();
	
		graphInstance = new GraphInstance();
		VisualizationViewer<BiologicalNodeAbstract,BiologicalEdgeAbstract> vv = graphInstance.getPathway().getGraph().getVisualizationViewer();
		RenderContext<BiologicalNodeAbstract,BiologicalEdgeAbstract> rc = vv.getRenderContext();
        MyAnnotatingGraphMousePlugin<BiologicalNodeAbstract,BiologicalEdgeAbstract> annotatingPlugin =
        	new MyAnnotatingGraphMousePlugin<BiologicalNodeAbstract,BiologicalEdgeAbstract>(rc);
        annotatingPlugin.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        annotatingPlugin.setFill(true);
        
        
        if(currentRangeType == RECTANGLE){
        	annotatingPlugin.setRectangularShape(new Rectangle());
        }else if(currentRangeType == ELLIPSE){
        	annotatingPlugin.setRectangularShape(new Ellipse2D.Double());
        }
        
        // create a GraphMouse for the main view
        // 
        
        final AnnotatingModalGraphMouse<BiologicalNodeAbstract,BiologicalEdgeAbstract> graphMouse = 
        	new AnnotatingModalGraphMouse<BiologicalNodeAbstract,BiologicalEdgeAbstract>(rc, annotatingPlugin);
       //AnnotationManager m = new AnnotationManager(rc);
       
       // VisualizationViewer<BiologicalNodeAbstract,BiologicalEdgeAbstract> vv = graphInstance.getPathway().getGraph().getVisualizationViewer();
        vv.setGraphMouse(graphMouse);
        vv.addKeyListener(graphMouse.getModeKeyListener());
        graphMouse.getModeComboBox().setSelectedItem(ModalGraphMouse.Mode.ANNOTATING);
       //vv.addMouseListener(this);
        //String text = JOptionPane
			//	.showInputDialog("Please enter a description!");
        
            

        
        /*JPanel annotationControlPanel = new JPanel();
        annotationControlPanel.setBorder(BorderFactory.createTitledBorder("Annotation Controls"));
        
        AnnotationControls<BiologicalNodeAbstract,BiologicalEdgeAbstract> annotationControls = 
            new AnnotationControls<BiologicalNodeAbstract,BiologicalEdgeAbstract>(annotatingPlugin);
        
        annotationControlPanel.add(annotationControls.getAnnotationsToolBar());
        controls.add(annotationControlPanel);*/
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		//this.rangeShapeEditor.enabled = enabled;
		try {
			GraphInstance.getMyGraph().getVisualizationViewer()
					.getComponentPopupMenu().remove(dropRange);
		} catch (Exception e) {
		}
	}
	
	private void initAction(Action a, String image, String desc) {
		
		// a.putValue(Action.SMALL_ICON, createIcon(imagePath));
		a.putValue(Action.SMALL_ICON, new ImageIcon(imagePath.getPath(image)));
		a.putValue(Action.SHORT_DESCRIPTION, desc);
		

	}
	
	public Color getFillColor() {
		return fillColor;
	}

	public void setFillColor(Color fillColor) {
		try {
			if (!this.fillColor.equals(fillColor)) {
				this.fillColor = new Color(fillColor.getRed(),
						fillColor.getGreen(), fillColor.getBlue(), this.alpha);
			}
		} catch (NullPointerException e) {
		}
	}
	
	private Color getColor(Color oldColor) {
		System.out.println("color: "+oldColor);
		Color newColor = oldColor;
		try{
		newColor = JColorChooser.showDialog(null, "select a new color.",
				oldColor);
		}catch(Exception e){
			
		}
		
		return newColor;
	}
	
	public void setTextColor(Color textColor) {
		this.textColor = textColor;
	}

	


}
