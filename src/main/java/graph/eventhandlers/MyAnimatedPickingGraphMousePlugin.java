package graph.eventhandlers;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Iterator;

import biologicalElements.Elementdeclerations;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
//import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.visualization.control.PickingGraphMousePlugin;
import graph.GraphInstance;
import graph.jung.classes.MyVisualizationViewer;
import miscalleanous.internet.FollowLink;

public class MyAnimatedPickingGraphMousePlugin extends PickingGraphMousePlugin<BiologicalNodeAbstract, BiologicalEdgeAbstract> {

	private GraphInstance graphInstance = new GraphInstance();
	private boolean inWindow = false;
	
	public void mouseReleased(MouseEvent e){
		super.mouseReleased(e);
		//System.out.println("drin");
		//System.out.println("v: "+vertex);
		
		final VisualizationViewer<BiologicalNodeAbstract,BiologicalEdgeAbstract> vv = graphInstance.getPathway().getGraph().getVisualizationViewer();
	    vv.getPickedVertexState().getPicked().size() ; 
		if (vv.getPickedVertexState().getPicked().size()  == 1) {
	    	  //System.out.println("drin2");
	        Layout<BiologicalNodeAbstract,BiologicalEdgeAbstract> layout = vv.getGraphLayout();
	        Point2D q = layout.apply(vv.getPickedVertexState().getPicked().iterator().next());
	        Point2D lvc = vv.getRenderContext().getMultiLayerTransformer().inverseTransform(vv.getCenter());
	        final double dx = (lvc.getX() - q.getX()) / 10;
	        final double dy = (lvc.getY() - q.getY()) / 10;

	        Runnable animator = new Runnable() {

	          public void run() {
	            for (int i = 0; i < 10; i++) {
	              vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).translate(dx, dy);
	              try {
	                Thread.sleep(100);
	              } catch (InterruptedException ex) {
	              }
	            }
	          }
	        };
	        Thread thread = new Thread(animator);
	        thread.start();
	      }
	    }
		
		
	@Override
	public void mouseClicked(MouseEvent arg0) {

		//System.out.println("Picked E: "+graphInstance.getMyGraph().getVisualizationViewer().getPickedEdgeState().getPicked().size());
		//System.out.println("Picked V: "+graphInstance.getMyGraph().getVisualizationViewer().getPickedVertexState().getPicked().size());
		if (arg0.getClickCount() == 1) {
			super.mouseClicked(arg0);
			//System.out.println("v: "+vertex);

		} else {
			BiologicalNodeAbstract bna;
			String urlString;
			
			Iterator<BiologicalNodeAbstract> it = graphInstance.getPathway().getSelectedNodes().iterator();

			//Iterator<BiologicalNodeAbstract> it = v.iterator();
			while (it.hasNext()) {
				//BiologicalNodeAbstract vertex = (BiologicalNodeAbstract) it.next();
				bna = it.next();//(BiologicalNodeAbstract) graphInstance
//						.getPathway().getNodeByVertexID(vertex.toString());
				if (bna.getBiologicalElement().equals(
						Elementdeclerations.protein)
						|| bna.getBiologicalElement().equals(
								Elementdeclerations.inhibitor)
						|| bna.getBiologicalElement().equals(
								Elementdeclerations.factor)
						|| bna.getBiologicalElement().equals(
								Elementdeclerations.metabolite)) {
					urlString = "https://agbi.techfak.uni-bielefeld.de/DAWISMD/jsp/result/protein_result.jsp?Protein_Id="
							+ bna.getLabel();
					FollowLink.openURL(urlString);
				} else if (bna.getBiologicalElement().equals(
						Elementdeclerations.enzyme)) {
					urlString = "https://agbi.techfak.uni-bielefeld.de/DAWISMD/jsp/result/enzyme_result.jsp?Enzyme_Id="
							+ bna.getLabel();
					FollowLink.openURL(urlString);

				}
			}
		}
	}
	
	public void mouseEntered(MouseEvent e) {
		inWindow = true;
	}

	public void mouseExited(MouseEvent e) {
		inWindow = false;
	}

	public void mouseMoved(MouseEvent e) {
		if (inWindow) {
			@SuppressWarnings("unchecked")
			final MyVisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv = (MyVisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract>) e
					.getSource();
			vv.setMousePoint(vv.getRenderContext().getMultiLayerTransformer().inverseTransform(e.getPoint()));
		}
	}
}
