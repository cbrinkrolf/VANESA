/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

//import edu.uci.ics.jung.graph.decorators.ConstantVertexAspectRatioFunction;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.collections.Factory;
import org.apache.commons.collections.map.LazyMap;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.visualization.VisualizationServer.Paintable;
import edu.uci.ics.jung.visualization.VisualizationViewer;
//import edu.uci.ics.jung.visualization.VisualizationViewer.Paintable;
import graph.GraphInstance;
import graph.jung.classes.MyGraph;
import heatmap.AdoptedHeatmap;

/**
 * 
 * @author star
 */
public class HeatgraphLayer extends MouseAdapter implements Paintable {

	//private Point2D startDragging;
	//private RangeInfo justCreated;
	private Map<MyGraph, AdoptedHeatmap> items = LazyMap.decorate(
			new HashMap(), new Factory() {

				public Object create() {
					AdoptedHeatmap h = new AdoptedHeatmap();
					/*h.addPoint(10, 10, 100);
					h.addPoint(100, 100, 110);
					h.addPoint(50, 50, 50);
					h.addPoint(120, 150, 30);
					h.updateData();*/
					return h;
				}
			});
	private Map<MyGraph, HashMap<String, Integer>> countData = LazyMap.decorate(
			new HashMap(), new Factory() {
				public Object create() {
					HashMap<String, Integer> h = new HashMap<String, Integer>();
					return h;
				}
			});
	private boolean enabled;
	private static HeatgraphLayer instance;

	private HashMap<String, Integer> lastCountByIdTemp;
	private boolean canChangeVertexShape = true;
	//private ImagePath imagePath = new ImagePath();

	public static HeatgraphLayer getInstance() {
		if (instance == null) {
			instance = new HeatgraphLayer();
		}
		return instance;
	}

	public HeatgraphLayer() {
		
	}

	

	private AdoptedHeatmap getShapes() {
		return getShapes(GraphInstance.getMyGraph());
	}

	private AdoptedHeatmap getShapes(MyGraph mg) {
		return this.items.get(mg);
	}

	public AdoptedHeatmap getHeatmapForGraph(MyGraph mg) {
		return this.items.get(mg);
	}
	
	public AdoptedHeatmap getHeatmapForActiveGraph() {
		return this.getHeatmapForGraph(GraphInstance.getPathwayStatic().getGraph());
	}
	
	public void repaintActiveGraph() {
		AdoptedHeatmap h = this.getHeatmapForActiveGraph();
		h.updateData();
		MainWindowSingleton.getInstance().repaint(300);
	}
	
	public void paint(Graphics g) {
		if(GraphInstance.getMyGraph() == null){
			return;
		}
		Graphics2D g2d = (Graphics2D) g;
		//Color old = g2d.getColor();
		// Font oldFont = g2d.getFont();
		// g2d.setFont(this.font);
		//List<RangeInfo> shapes = this.getShapes();
		
		//set the coordinates transford due to scrolling
		VisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv = GraphInstance.getMyGraph()
				.getVisualizationViewer();
		
		AffineTransform oldXform = g2d.getTransform();
		AffineTransform newXform = new AffineTransform(oldXform);
		//newXform.concatenate(vv.getLayoutTransformer().getTransform());
		g2d.setTransform(newXform);
		
		
		
		//scan again through the nodes and fill the hashmap
		Pathway pw_new = GraphInstance.getPathwayStatic();
		Iterator<BiologicalNodeAbstract> nodes = pw_new.getAllGraphNodes().iterator();
		AdoptedHeatmap h = this.getHeatmapForActiveGraph();
		HashMap<String, Integer> countById = this.countData.get(GraphInstance.getMyGraph());
		
		//search for not active nodes and mark them as 0
		HashMap<String, Integer> countByIdTemp = new HashMap<String, Integer>();
		BiologicalNodeAbstract bna;
		String id;
		while(nodes.hasNext()) {
			bna = nodes.next();
			id = bna.getName()+bna.getLabel();
			if (countById.containsKey(id) && GraphInstance.getMyGraph().getVisualizationViewer().getPickedVertexState().isPicked(bna)) {
				countByIdTemp.put(id, countById.get(id));
			}
		}
		
		//only repaint, if the picked state has changed
		if ((this.lastCountByIdTemp==null) || !this.lastCountByIdTemp.equals(countByIdTemp)) {
			this.lastCountByIdTemp = countByIdTemp;
			h.resetPoints();
			//NodeRankingVertexSizeFunction sf = new NodeRankingVertexSizeFunction("madata",1);
			//VertexShapes vs = new VertexShapes(sf, new ConstantVertexAspectRatioFunction(1.0f));
			nodes = pw_new.getAllGraphNodes().iterator();
			while(nodes.hasNext()) {
				bna = nodes.next();
				id = bna.getName()+bna.getLabel();
				
				if (countByIdTemp.containsKey(id)) {
					//Point2D p = GraphInstance.getMyGraph().getVertexLocations().getLocation(bna.getVertex());
					
					Point2D p = GraphInstance.getMyGraph().getVertexLocation(bna);
					if (this.canChangeVertexShape) {
						bna.setShape(bna.shapes.getRegularStar(Math.max(5, Math.min(10, 4+countByIdTemp.get(id)))));
					//	System.out.println("set shape to regular star");
					}
					else {
						//reset shape to default
						bna.rebuildShape(bna.shapes);
					//	System.out.println("set shape to normal");
					}
					
			//		System.out.println("f�ge hinzu: "+p+" id="+id);
					
					//getGraphVisualization().getLocation((Point) );
					//(int) bna.getShape().getBounds().getCenterX()
					if (countByIdTemp.get(id)>0) {
						h.addPoint(
							(int) p.getX(), (int) p.getY(), 
							countByIdTemp.get(id)*30);
					}
				}
			}
			h.setMaximumBound(vv.getPreferredSize());
			h.updateData();
			
			
		}
		
		//Point pfrom = new Point();
		//g2d.getTransform().transform(new Point(0,0), pfrom);
		//Point pto = new Point();
		//g2d.getTransform().transform(new Point(vv.getBounds().width,vv.getBounds().height), pto);
		g.setColor(new Color(100,100,100));
		//g.fillRect(pfrom.x, pfrom.y, pto.x-pfrom.x, pto.y-pfrom.y);
		//g2d.fillRect(0, 0, vv.getPreferredSize().width, vv.getPreferredSize().height);
		
		//draw the active heatmap 
		g.drawImage(this.getShapes().bufferedImage, (-1)*this.getShapes().getOffsetX(), 
				(-1)*this.getShapes().getOffsetY(), 
				(int) Math.ceil(this.getShapes().bufferedImage.getWidth()/AdoptedHeatmap.scaleFactor), //width
				(int) Math.ceil(this.getShapes().bufferedImage.getHeight()/AdoptedHeatmap.scaleFactor), //height
				
				null);
		
		
		
		/*for (RangeInfo info : shapes) {
			if (info != null) {

				int drawOutline = info.outlineType;
				RectangularShape temp = this.tempRect;
				if (drawOutline == 2) {
					if (info.shape instanceof Ellipse2D) {
						temp = this.tempEllipse;
					}
					temp.setFrameFromDiagonal(info.shape.getMinX() + inset,
							info.shape.getMinY() + inset, info.shape.getMaxX()
									- inset, info.shape.getMaxY() - inset);
				}
				int rgba = info.fillColor.getRGB();
				rgba &= ((info.alpha << 24) + 0x00ffffff);
				Color c = new Color(rgba, true);
				g2d.setColor(c);
				if (drawOutline == 2) {
					g2d.fill(temp);
					g2d.setColor(info.outlineColor);
					g2d.draw(temp);
				} else {
					g2d.fill(info.shape);
				}
				//g2d.dra
				if (drawOutline > 0) {
					g2d.setColor(info.outlineColor);
					g2d.draw(info.shape);
				}
				String s = info.text;
				if (s != null) {
					Rectangle bound = info.shape.getBounds();
					double cx = bound.getCenterX();
					double cy = bound.getCenterY();
					double hw = bound.getWidth() / 2.0, hh = bound.getHeight() / 2.0;
					double oy = hh - yOffset;
					int lr = (info.titlePos & 1) > 0 ? 1 : -1, tb = (info.titlePos & 2) > 0 ? 1
							: -1;
					FontMetrics metrics = g.getFontMetrics();
					Rectangle2D labelBound = metrics.getStringBounds(s, g);
					double sw = labelBound.getWidth(), sh = labelBound
							.getHeight();
					if (info.shape instanceof Ellipse2D) {
						oy = (this.calcYInEllipse(xOffset,
								(Ellipse2D) info.shape));
					}
					float x = (float) (cx + lr * hw - lr * xOffset);
					x += (lr > 0 ? -sw : 0);
					float y = (float) (cy + tb * oy);
					y += (tb > 0 ? 0 : sh);
					g2d.setColor(info.textColor);
					g2d.drawString(s, x, y);
				}
			}
		}
		g2d.setColor(old);*/
		// g2d.setFont(oldFont);
		g2d.setTransform(oldXform);
	}

	/** use coordiantes transformation? */
	public boolean useTransform() {
		return true;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setCountDataForGraph(MyGraph graph,
			HashMap<String, Integer> countById) {
		this.countData.put(graph, countById);
	}

	public void setCanChangeVertexShape(boolean canChangeVertexForm) {
		this.canChangeVertexShape = canChangeVertexForm;
	}

	
}
