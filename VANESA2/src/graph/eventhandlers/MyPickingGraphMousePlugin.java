package graph.eventhandlers;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JOptionPane;

import miscalleanous.internet.FollowLink;
import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.PickingGraphMousePlugin;
import graph.GraphInstance;
import graph.jung.classes.MyGraph;
import graph.layouts.Circle;
import graph.layouts.HierarchicalCircleLayout;
import gui.MainWindowSingleton;

public class MyPickingGraphMousePlugin extends
		PickingGraphMousePlugin<BiologicalNodeAbstract, BiologicalEdgeAbstract> {
	
	private GraphInstance graphInstance = new GraphInstance();
	private HashMap<BiologicalNodeAbstract, Point2D> oldVertexPositions = new HashMap<BiologicalNodeAbstract, Point2D>();
	private Set<BiologicalNodeAbstract> originalSelection = new HashSet<BiologicalNodeAbstract>();
	private boolean inwindow = false;
	
	public void mouseReleased(MouseEvent e) {
		if(inwindow){
			// If mouse was released to change the selection, save vertex positions and return.
			if(!oldVertexPositions.keySet().containsAll(graphInstance.getPathway().getSelectedNodes()) ||
					oldVertexPositions.keySet().size()!=graphInstance.getPathway().getSelectedNodes().size()){
				saveOldVertexPositions();
				super.mouseReleased(e);
				return;
			}
	
			Collection<BiologicalNodeAbstract> selectedNodes = graphInstance.getPathway().getSelectedNodes();
	
			// If no nodes were selected, return.
			if(selectedNodes.isEmpty()){
				super.mouseReleased(e);
				return;
			}
			
			if(graphInstance.getMyGraph().getLayout() instanceof HierarchicalCircleLayout){
				HierarchicalCircleLayout hclayout = (HierarchicalCircleLayout) graphInstance.getMyGraph().getLayout();
				hclayout.saveCurrentOrder();
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
				Point2D movement = new Point2D.Double();
				MyGraph graph = graphInstance.getMyGraph();
				Pathway pw = graphInstance.getPathwayStatic();
				for(BiologicalNodeAbstract selectedNode : selectedNodes){
					if(selectedNode.isCoarseNode()){
						movement.setLocation(graph.getVertexLocation(selectedNode).getX() - 
								oldVertexPositions.get(selectedNode).getX(), 
								graph.getVertexLocation(selectedNode).getY() - 
								oldVertexPositions.get(selectedNode).getY());
						for(BiologicalNodeAbstract child : selectedNode.getVertices().keySet()){
							pw.getVertices().get(child).setLocation(Circle.addPoints(pw.getVertices().get(child), movement));
						}
					} else {
						if(pw.getVertices().keySet().contains(selectedNode)){
							pw.getVertices().get(selectedNode).setLocation(graph.getVertexLocation(selectedNode));
						}
					}
				}
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
	}

	private void coarseNodeFusion(BiologicalNodeAbstract vertex){
		Set<BiologicalNodeAbstract> selection = new HashSet<BiologicalNodeAbstract>();
		selection.addAll(graphInstance.getPathway().getSelectedNodes());
		if(!vertex.addToCoarseNode(selection, oldVertexPositions)){
			for(BiologicalNodeAbstract node : selection){
				graphInstance.getPathway().getGraph().moveVertex(node, oldVertexPositions.get(node).getX(), 
					oldVertexPositions.get(node).getY());
			}
		} else {
			graphInstance.getPathway().updateMyGraph();
		}
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		if(inwindow){
		
			super.mousePressed(e);
			originalSelection.clear();
			originalSelection.addAll(graphInstance.getPathway().getSelectedNodes());
			if(graphInstance.getMyGraph().getLayout() instanceof HierarchicalCircleLayout){
				HierarchicalCircleLayout hcLayout = (HierarchicalCircleLayout) graphInstance.getMyGraph().getLayout();
				if(hcLayout.getConfig().getMoveInGroups()){
					for(BiologicalNodeAbstract selectedNode : graphInstance.getPathway().getSelectedNodes()){
						for(BiologicalNodeAbstract node : hcLayout.getNodesGroup(selectedNode)){
							graphInstance.getMyGraph().getVisualizationViewer().getPickedVertexState().pick(node, true);
						}
					}
				}
			}
			saveOldVertexPositions();
		}
	}
	
	private void saveOldVertexPositions(){
		oldVertexPositions.clear();
		for(BiologicalNodeAbstract vertex : graphInstance.getPathway().getSelectedNodes()){
			oldVertexPositions.put(vertex, graphInstance.getPathway().getGraph().getVertexLocation(vertex));
		}
	}
	
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		if(inwindow){

			// System.out.println("Picked E: "+graphInstance.getMyGraph().getVisualizationViewer().getPickedEdgeState().getPicked().size());
			// System.out.println("Picked V: "+graphInstance.getMyGraph().getVisualizationViewer().getPickedVertexState().getPicked().size());
			if (arg0.getClickCount() == 1) {
				super.mouseClicked(arg0);
				// System.out.println("v: "+this.vertex);
				
	//			Vector<BiologicalNodeAbstract> v = graphInstance.getPathway()
	//					.getSelectedNodes();
	//
	//			Iterator<BiologicalNodeAbstract> it = v.iterator();
	//			while (it.hasNext()) {
	//				BiologicalNodeAbstract bna = it.next();
	//				bna.printAllHierarchicalAttributes();
	//			}
	
			} else {
	
				Iterator<BiologicalNodeAbstract> it = graphInstance.getPathway()
						.getSelectedNodes().iterator();
				BiologicalNodeAbstract bna;
				String urlString;
				
				while (it.hasNext()) {
					// BiologicalNodeAbstract vertex = (BiologicalNodeAbstract)
					// it.next();
					bna = it.next();// (BiologicalNodeAbstract)
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
						urlString = "https://agbi.techfak.uni-bielefeld.de/DAWISMD/jsp/result/protein_result.jsp?Protein_Id="
								+ bna.getLabel();
						new FollowLink().openURL(urlString);
					} else if (bna.getBiologicalElement().equals(
							Elementdeclerations.enzyme)) {
						urlString = "https://agbi.techfak.uni-bielefeld.de/DAWISMD/jsp/result/enzyme_result.jsp?Enzyme_Id="
								+ bna.getLabel();
						new FollowLink().openURL(urlString);
	
					}
				}
			}
		}
	}
	
	@Override
	public void mouseDragged(MouseEvent e){
		if(inwindow){
			
			if(!(graphInstance.getMyGraph().getLayout() instanceof HierarchicalCircleLayout)){
				super.mouseDragged(e);
			} else {
				if(locked == false) {
		            VisualizationViewer<BiologicalNodeAbstract,BiologicalEdgeAbstract> vv = (VisualizationViewer)e.getSource();
		            if(vertex != null) {
						HierarchicalCircleLayout hcLayout= (HierarchicalCircleLayout) graphInstance.getMyGraph().getLayout();
		            	//mouse position
		                Point p = e.getPoint();
		                
		                //move nodes in layout
		                hcLayout.moveOnCircle(p,down,vv);	                
		                
		                down = p;
	
		            } else {
		                Point2D out = e.getPoint();
		                if(e.getModifiers() == this.addToSelectionModifiers ||
		                        e.getModifiers() == modifiers) {
		                    rect.setFrameFromDiagonal(down,out);
		                }
		            }
		            if(vertex != null) e.consume();
		            vv.repaint();
		        }
			}
		}
	}

	public void mouseEntered(MouseEvent e) {
		if(e.getComponent().toString().contains("MyVisualizationViewer"))
			inwindow = true;
	}

	public void mouseExited(MouseEvent e) {
		if(e.getComponent().toString().contains("MyVisualizationViewer"))
			inwindow = false;
	}

	public void mouseMoved(MouseEvent e) {
	}

}
