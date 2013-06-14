package graph.jung.graphDrawing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.GeneralPath;

import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.VisualizationViewer.Paintable;
import edu.uci.ics.jung.visualization.transform.shape.ShapeTransformer;

public class ViewGrid  implements Paintable {

	  VisualizationViewer master;
      VisualizationViewer vv;
	
	public ViewGrid(VisualizationViewer vv, VisualizationViewer master) {
        this.vv = vv;
        this.master = master;
	}

	public void paint(Graphics g) {
		ShapeTransformer masterViewTransformer = master.getViewTransformer();
        ShapeTransformer masterLayoutTransformer = master.getLayoutTransformer();
        ShapeTransformer vvLayoutTransformer = vv.getLayoutTransformer();

        Rectangle rect = master.getBounds();
        GeneralPath path = new GeneralPath();
        path.moveTo(rect.x, rect.y);
        path.lineTo(rect.width,rect.y);
        path.lineTo(rect.width, rect.height);
        path.lineTo(rect.x, rect.height);
        path.lineTo(rect.x, rect.y);
        
        for(int i=0; i<=rect.width; i+=rect.width/10) {
        		path.moveTo(rect.x+i, rect.y);
        		path.lineTo(rect.x+i, rect.height);
        }
        for(int i=0; i<=rect.height; i+=rect.height/10) {
        		path.moveTo(rect.x, rect.y+i);
        		path.lineTo(rect.width, rect.y+i);
        }
        Shape lens = path;
        lens = masterViewTransformer.inverseTransform(lens);
        lens = masterLayoutTransformer.inverseTransform(lens);
        lens = vvLayoutTransformer.transform(lens);
        Graphics2D g2d = (Graphics2D)g;
        Color old = g.getColor();
        g.setColor(Color.cyan);
        g2d.draw(lens);
        
        path = new GeneralPath();
        path.moveTo((float)rect.getMinX(), (float)rect.getCenterY());
        path.lineTo((float)rect.getMaxX(), (float)rect.getCenterY());
        path.moveTo((float)rect.getCenterX(), (float)rect.getMinY());
        path.lineTo((float)rect.getCenterX(), (float)rect.getMaxY());
        Shape crosshairShape = path;
        crosshairShape = masterViewTransformer.inverseTransform(crosshairShape);
        crosshairShape = masterLayoutTransformer.inverseTransform(crosshairShape);
        crosshairShape = vvLayoutTransformer.transform(crosshairShape);
        g.setColor(Color.black);
        g2d.setStroke(new BasicStroke(3));
        g2d.draw(crosshairShape);
        
        g.setColor(old);
		
	}

	public boolean useTransform() {
		return true;
	}

}
