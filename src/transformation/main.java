package transformation;


import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import transformation.Permutator;
import transformation.Tester;

public class main {
	public static void main(String[] args) throws Exception {
		
		Tester t = new Tester();
		t.test();
		
		int[] val = new int[] { 1, 2, 3, 4, 5, 6 };
		List<Collection<String>> all = new ArrayList<Collection<String>>();
		List<String> l1 = new ArrayList<String>();
		List<String> l2 = new ArrayList<String>();
		List<String> l3 = new ArrayList<String>();
		
		l1.add("a");
		l1.add("b");
		
		l2.add("1");
		l2.add("2");
		l2.add("3");
		
		l3.add("x");
		l3.add("y");
		
		all.add(new LinkedList<String>(l1));
		all.add(new LinkedList<String>(l1));
		all.add(l3);
		
		System.out.println(Permutator.permutations(all));
		
		
		
	}
}