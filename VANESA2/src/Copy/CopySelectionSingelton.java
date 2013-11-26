package Copy;

import java.util.HashSet;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
//import edu.uci.ics.jung.graph.Edge;
//import edu.uci.ics.jung.graph.Vertex;


public class CopySelectionSingelton extends CopySelection {



	public CopySelectionSingelton(HashSet<BiologicalNodeAbstract> vertices, HashSet<BiologicalEdgeAbstract> edges) {
		super(vertices, edges);
		// TODO Auto-generated constructor stub
	}

	private static CopySelection instance =null;

	public static CopySelection getInstance(){
		if(instance ==null){
			instance =new CopySelectionSingelton(new HashSet<BiologicalNodeAbstract>(), new HashSet<BiologicalEdgeAbstract>());
		}
		return instance;
	}

	public static void setInstance(CopySelection submitted){
		instance=submitted;
	
	}


	
}

