package graph.eventhandlers;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import miscalleanous.internet.FollowLink;
import biologicalElements.Elementdeclerations;
import biologicalElements.NodeStateChanged;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AnimatedPickingGraphMousePlugin;
//import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.visualization.control.PickingGraphMousePlugin;
import graph.GraphInstance;
import graph.jung.classes.MyGraph;

public class MyPickingGraphMousePlugin extends
		PickingGraphMousePlugin<BiologicalNodeAbstract, BiologicalEdgeAbstract> {

	private GraphInstance graphInstance = new GraphInstance();

	public void mouseReleased(MouseEvent e) {
		final VisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv = (VisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract>) e
				.getSource();
		final Point2D p = vv.getRenderContext().getMultiLayerTransformer()
				.inverseTransform(e.getPoint());
		GraphElementAccessor<BiologicalNodeAbstract, BiologicalEdgeAbstract> pickSupport = vv
				.getPickSupport();
		BiologicalNodeAbstract vertex = null;
		Collection<BiologicalNodeAbstract> selectedNodes = GraphInstance.getMyGraph().getVisualizationViewer().getPickedVertexState().getPicked();
		vertex = (BiologicalNodeAbstract) pickSupport
				.getVertex(vv.getGraphLayout(), e.getPoint().getX(), e
						.getPoint().getY());
		if(vertex!=null && vertex.isCoarseNode() && !selectedNodes.contains(vertex)){
			coarseNodeFusion(vertex);
		}
		super.mouseReleased(e);

	}

	public void coarseNodeFusion(BiologicalNodeAbstract vertex){
		Set<BiologicalNodeAbstract> selection = new HashSet<BiologicalNodeAbstract>();
		selection.addAll(GraphInstance.getMyGraph().getVisualizationViewer().getPickedVertexState().getPicked());
		Set<BiologicalNodeAbstract> coarseNodes = new HashSet<BiologicalNodeAbstract>();
		coarseNodes.addAll(selection);
		coarseNodes.addAll(vertex.getInnerNodes());
		vertex.flat();
		if(vertex.computeCoarseType(coarseNodes)!=null){
			vertex.coarse(coarseNodes);
		}
	}
	// Iterator it = graphInstance.getMyGraph().getAllvertices().iterator();
	// Iterator it = graphInstance.getMyGraph().getAllEdges().iterator();
	// while(it.hasNext()){
	// System.out.println(graphInstance.getMyGraph().getVisualizationViewer().getPickedEdgeState().isPicked(it.next()));
	// }
	// System.out.println("rel E: "+graphInstance.getMyGraph().getVisualizationViewer().getPickedEdgeState().getSelectedObjects().length);
	// System.out.println("rel V: "+graphInstance.getMyGraph().getVisualizationViewer().getPickedVertexState().getSelectedObjects().length);

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
					String urlString = "http://agbi.techfak.uni-bielefeld.de/DAWISMD/jsp/detail/protein/protein_detail.jsp?param0="
							+ bna.getLabel();
					new FollowLink().openURL(urlString);
				} else if (bna.getBiologicalElement().equals(
						Elementdeclerations.enzyme)) {
					String urlString = "http://agbi.techfak.uni-bielefeld.de/DAWISMD/jsp/detail/enzyme_detail.jsp?param0="
							+ bna.getLabel();
					new FollowLink().openURL(urlString);

				}
			}
		}
	}
}
