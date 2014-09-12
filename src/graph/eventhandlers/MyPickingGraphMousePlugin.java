package graph.eventhandlers;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.swing.JOptionPane;

import miscalleanous.internet.FollowLink;
import biologicalElements.Elementdeclerations;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.PickingGraphMousePlugin;
import graph.GraphInstance;
import gui.MainWindowSingleton;

public class MyPickingGraphMousePlugin extends
		PickingGraphMousePlugin<BiologicalNodeAbstract, BiologicalEdgeAbstract> {
	
	private GraphInstance graphInstance = new GraphInstance();
	private HashMap<BiologicalNodeAbstract, Point2D> oldVertexPositions = new HashMap<BiologicalNodeAbstract, Point2D>();

	
	public void mouseReleased(MouseEvent e) {

		// If mouse was released to change the selection, save vertex positions and return.
		if(!oldVertexPositions.keySet().containsAll(graphInstance.getPathway().getSelectedNodes()) ||
				oldVertexPositions.keySet().size()!=graphInstance.getPathway().getSelectedNodes().size()){
			saveOldVertexPositions(e);
			super.mouseReleased(e);
			return;
		}
		
		Collection<BiologicalNodeAbstract> selectedNodes = graphInstance.getPathway().getSelectedNodes();
		
		// If no nodes were selected, return.
		if(selectedNodes.isEmpty()){
			super.mouseReleased(e);
			return;
		}
		
		// Find coarse nodes in specified environment of the final mouse position.
		final VisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv = (VisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract>) e
				.getSource();
		GraphElementAccessor<BiologicalNodeAbstract, BiologicalEdgeAbstract> pickSupport = vv
				.getPickSupport();
		float width = selectedNodes.iterator().next().getShape().getBounds().width;
		float height = selectedNodes.iterator().next().getShape().getBounds().height;
		Shape shape = new Rectangle.Float(e.getX()-(width/2), e.getY()-(height/2), width, height);
		Collection<BiologicalNodeAbstract> vertices =  pickSupport.getVertices(vv.getGraphLayout(), shape);
		vertices.removeAll(selectedNodes);
		
		// If exactly one node was found, take this node, otherwise return.
		BiologicalNodeAbstract vertex = null;
		if(vertices.size()==1){
			vertex = vertices.iterator().next();
		} else {
			super.mouseReleased(e);
			return;
		}
		
		// If node is a coarse node and not contained in the selection, add the selection to the coarse node.
		if(vertex.isCoarseNode() && !selectedNodes.contains(vertex)){
			// Disallow to add selection to environment coarse nodes.
			if(graphInstance.getPathway().isBNA()){
				if(((BiologicalNodeAbstract)graphInstance.getPathway()).getEnvironment().contains(vertices)){
					JOptionPane.showMessageDialog(MainWindowSingleton.getInstance(), 
							"Not possible to add nodes to environment nodes.", 
							"Coarse node integration Error!", JOptionPane.ERROR_MESSAGE);
					oldVertexPositions.clear();
					super.mouseReleased(e);
					return;
				}
			}
			coarseNodeFusion(vertex);
		}
		oldVertexPositions.clear();
		super.mouseReleased(e);
	}

	private void coarseNodeFusion(BiologicalNodeAbstract vertex){
		Set<BiologicalNodeAbstract> selection = new HashSet<BiologicalNodeAbstract>();
		selection.addAll(graphInstance.getPathway().getSelectedNodes());
		if(vertex.tryAddToCoarseNode(selection)){
			for(BiologicalNodeAbstract node : selection){
				vertex.getGraph().moveVertex(node, oldVertexPositions.get(node).getX(), 
						oldVertexPositions.get(node).getY());
			}
		} else {
			for(BiologicalNodeAbstract node : selection){
				graphInstance.getPathway().getGraph().moveVertex(node, oldVertexPositions.get(node).getX(), 
						oldVertexPositions.get(node).getY());
			}
		}
//		Set<BiologicalNodeAbstract> coarseNodes = new HashSet<BiologicalNodeAbstract>();
//		coarseNodes.addAll(selection);
//		coarseNodes.addAll(vertex.getInnerNodes());
//		vertex.flat();
//		if(vertex.computeCoarseType(coarseNodes)!=null){
//			vertex.tryAddToCoarseNode(coarseNodes);
//			for(BiologicalNodeAbstract node : selection){
//				vertex.getGraph().moveVertex(node, oldVertexPositions.get(node).getX(), oldVertexPositions.get(node).getY());
//			}
//		}
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		
		//oldVertexPosition = graphInstance.getPathway().getGraph().getVertexLocation(pickSupport.getVertex(vv.getGraphLayout(), e.getPoint().getX(), e.getPoint().getY()));
		super.mousePressed(e);
		saveOldVertexPositions(e);
	};
	
	private void saveOldVertexPositions(MouseEvent e){
		oldVertexPositions.clear();
		final VisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv = (VisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract>) e
				.getSource();
		GraphElementAccessor<BiologicalNodeAbstract, BiologicalEdgeAbstract> pickSupport = vv
				.getPickSupport();
		for(BiologicalNodeAbstract vertex : graphInstance.getPathway().getSelectedNodes()){
			oldVertexPositions.put(vertex, graphInstance.getPathway().getGraph().getVertexLocation(vertex));
		}
	}
	
	
	@Override
	public void mouseClicked(MouseEvent arg0) {

		// System.out.println("Picked E: "+graphInstance.getMyGraph().getVisualizationViewer().getPickedEdgeState().getPicked().size());
		// System.out.println("Picked V: "+graphInstance.getMyGraph().getVisualizationViewer().getPickedVertexState().getPicked().size());
		if (arg0.getClickCount() == 1) {
			super.mouseClicked(arg0);
			// System.out.println("v: "+this.vertex);

		} else {

			Vector<BiologicalNodeAbstract> v = graphInstance.getPathway()
					.getSelectedNodes();

			Iterator<BiologicalNodeAbstract> it = v.iterator();
			while (it.hasNext()) {
				// BiologicalNodeAbstract vertex = (BiologicalNodeAbstract)
				// it.next();
				BiologicalNodeAbstract bna = it.next();// (BiologicalNodeAbstract)
														// graphInstance
				// .getPathway().getNodeByVertexID(vertex.toString());
				if (bna.getBiologicalElement().equals(
						Elementdeclerations.protein)
						|| bna.getBiologicalElement().equals(
								Elementdeclerations.inhibitor)
						|| bna.getBiologicalElement().equals(
								Elementdeclerations.factor)
						|| bna.getBiologicalElement().equals(
								Elementdeclerations.smallMolecule)) {
					String urlString = "https://agbi.techfak.uni-bielefeld.de/DAWISMD/jsp/result/protein_result.jsp?Protein_Id="
							+ bna.getLabel();
					new FollowLink().openURL(urlString);
				} else if (bna.getBiologicalElement().equals(
						Elementdeclerations.enzyme)) {
					String urlString = "https://agbi.techfak.uni-bielefeld.de/DAWISMD/jsp/result/enzyme_result.jsp?Enzyme_Id="
							+ bna.getLabel();
					new FollowLink().openURL(urlString);

				}
			}
		}
	}
}
