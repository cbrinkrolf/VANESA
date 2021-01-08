package transformation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class Tester {

	
	// "binning" of nodes to classes 
	List<BiologicalNodeAbstract> list = new ArrayList<BiologicalNodeAbstract>();
	List<BiologicalEdgeAbstract> es = new ArrayList<BiologicalEdgeAbstract>();

	HashMap<String, ArrayList<BiologicalNodeAbstract>> map = new HashMap<String, ArrayList<BiologicalNodeAbstract>>();

	public Tester() {

		//A a = new A();
		//A a2 = new A();
		//B b = new B();
		//C c = new C();

		//list.add(a);
		//list.add(a2);
		//list.add(b);
		//list.add(c);

		//E1 e1 = new E1(a, a2);
		//E1 e2 = new E1(a, b);
		//E1 e3 = new E1(a, c);

		//es.add(e1);
		//es.add(e2);
		//es.add(e3);

	}

	public void test() {

		for (int i = 0; i < es.size(); i++) {
			BiologicalEdgeAbstract e = es.get(i);
			// System.out.println(e.from.getClass().getName());
			//if (e.from.getClass().getName().equals("A") && (e.to.getClass().getName().equals("B"))) {
			//	System.out.println(e);
				// System.out.println(e.from instanceof A);
				// System.out.println(e.from instanceof General);
			//	put(e.from);

				// System.out.println(e.from.getClass().getSuperclass().getSuperclass().getSimpleName());

				// System.out.println();
			//}

		}

	}

	private void put(BiologicalNodeAbstract bna) {

		String name = "";

		Class c = bna.getClass();
		name = c.getSimpleName();

		while (!name.equals("Object")) {
			System.out.println(name);
			
			if(!map.containsKey(name)){
				map.put(name, new ArrayList<BiologicalNodeAbstract>());
			}
			map.get(name).add(bna);
			
			c = c.getSuperclass();
			name = c.getSimpleName();

		}

	}

}
