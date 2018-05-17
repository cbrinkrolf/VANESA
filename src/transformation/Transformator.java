package transformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class Transformator {

	private Pathway pw;

	List<BiologicalNodeAbstract> nodeList = new ArrayList<BiologicalNodeAbstract>();
	List<BiologicalEdgeAbstract> edgeList = new ArrayList<BiologicalEdgeAbstract>();

	HashMap<String, ArrayList<BiologicalNodeAbstract>> nodeType2bna = new HashMap<String, ArrayList<BiologicalNodeAbstract>>();
	HashMap<String, ArrayList<BiologicalEdgeAbstract>> edgeType2bea = new HashMap<String, ArrayList<BiologicalEdgeAbstract>>();

	public void transform(Pathway pw) {
		this.pw = pw;
	}

	
	
	
	
	
	private void createBuckets() {
		BiologicalNodeAbstract bna;
		BiologicalEdgeAbstract bea;
		String name;
		Class c;
		// all nodes
		Iterator<BiologicalNodeAbstract> it = pw.getAllGraphNodes().iterator();
		while(it.hasNext()) {
			bna = it.next();
			c = bna.getClass();
			name = bna.getClass().getSimpleName();

			while (!name.equals("Object")) {
				//System.out.println(name);

				if (!nodeType2bna.containsKey(name)) {
					nodeType2bna.put(name, new ArrayList<BiologicalNodeAbstract>());
				}
				nodeType2bna.get(name).add(bna);

				c = c.getSuperclass();
				name = c.getSimpleName();
			}
		}
		// all edges
		Iterator<BiologicalEdgeAbstract> it2 = pw.getAllEdges().iterator();
		while(it2.hasNext()) {
			bea = it2.next();
			c = bea.getClass();
			name = bea.getClass().getSimpleName();

			while (!name.equals("Object")) {
				//System.out.println(name);

				if (!edgeType2bea.containsKey(name)) {
					edgeType2bea.put(name, new ArrayList<BiologicalEdgeAbstract>());
				}
				edgeType2bea.get(name).add(bea);

				c = c.getSuperclass();
				name = c.getSimpleName();
			}
		}
	}
	
	
	
}
