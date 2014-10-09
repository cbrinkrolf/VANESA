package graph.layouts.hebLayout;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.graph.Graph;
import graph.GraphInstance;

public class HEBLayout extends CircleLayout<BiologicalNodeAbstract, BiologicalEdgeAbstract>{
	
	private Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract> graph;
	private Set<Set<BiologicalNodeAbstract>> bnaGroups;
	private List<BiologicalNodeAbstract> vertex_ordered_list;
	
	Map<BiologicalNodeAbstract, CircleVertexData> circleVertexDataMap =
           new HashMap<BiologicalNodeAbstract, CircleVertexData>();



	public HEBLayout(Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract> g) {
		super(g);
		graph = g;
		// TODO Auto-generated constructor stub
	}

	/**
	 * Build groups with nodes of the same parent node.
	 */
	public void groupNodes(){
		//Nodes in the graph.
		Set<BiologicalNodeAbstract> graphNodes = new HashSet<BiologicalNodeAbstract>();
		graphNodes.addAll(graph.getVertices());

		if(graphNodes.size()<2){return;}

		bnaGroups = new HashSet<Set<BiologicalNodeAbstract>>();
		Set<BiologicalNodeAbstract> group = new HashSet<BiologicalNodeAbstract>();
		
		for(BiologicalNodeAbstract bna : graphNodes){
			if(group.isEmpty()){
				group.add(bna);
				bnaGroups.add(group);
				continue;
			}
			Set<BiologicalNodeAbstract> bnaGroup = new HashSet<BiologicalNodeAbstract>();
			for(Set<BiologicalNodeAbstract> oldGroup : bnaGroups){
				if(bna.getParentNode() == oldGroup.iterator().next().getParentNode() && bna.getParentNode()!=null){
					bnaGroup = oldGroup;
					break;
				}
			}
			if(bnaGroup.isEmpty()){
				bnaGroup.add(bna);
				bnaGroups.add(bnaGroup);
			} else {
				bnaGroup.add(bna);
			}
		}

		vertex_ordered_list = new Vector<BiologicalNodeAbstract>();
		for(Set<BiologicalNodeAbstract> subSet : bnaGroups){
			for(BiologicalNodeAbstract bna : subSet){
				vertex_ordered_list.add(bna);
			}
		}
		setVertexOrder(vertex_ordered_list);
	}
	
	@Override
	public void initialize()
    {
            Dimension d = getSize();
           
            if (d != null)
            {
                if (vertex_ordered_list == null)
                    setVertexOrder(new Vector<BiologicalNodeAbstract>(getGraph().getVertices()));

                    double height = d.getHeight();
                    double width = d.getWidth();

                    if (getRadius() <= 0) {
                            setRadius(0.45 * (height < width ? height : width));
                    }

                    int group_no = 0;
                    int vertex_no = 0;
                    //distance between two groups (added to small distance between two nodes)
                    final double nodeDistance = 2*Math.PI / (2*bnaGroups.size()+vertex_ordered_list.size());
                    final double groupDistance = (HEBLayoutConfig.GROUP_DISTANCE_FACTOR-1)*nodeDistance;
                    for (Set<BiologicalNodeAbstract> group : bnaGroups)
                    {
                    	for(BiologicalNodeAbstract v : group){
                    		apply(v);

                            double angle = group_no*groupDistance+vertex_no*nodeDistance;

                            GraphInstance.getMyGraph().moveVertex(v, Math.cos(angle) * getRadius() + width / 2,
                                            Math.sin(angle) * getRadius() + height / 2);

                            CircleVertexData data = getCircleData(v);
                            data.setAngle(angle);
                            vertex_no++;
                    	}
                        group_no++;
                    }
            }
    }
	
	public void reset(){
		initialize();
	}
	
	@Override
    protected CircleVertexData getCircleData(BiologicalNodeAbstract v) {
        return circleVertexDataMap.get(v);
	}
	
	private CircleVertexData apply(BiologicalNodeAbstract v){
		CircleVertexData cvData = new CircleVertexData();
		circleVertexDataMap.put(v, cvData);
		return cvData;
	}


protected static class CircleVertexData extends CircleLayout.CircleVertexData{
		
        private double angle;

        @Override
        protected double getAngle() {
                return angle;
        }

        @Override
        protected void setAngle(double angle) {
                this.angle = angle;
        }

        @Override
        public String toString() {
                return "CircleVertexData: angle=" + angle;
        }
}


	
}
