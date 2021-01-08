package transformation;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import transformation.Permutator;
import transformation.Tester;

public class main {
	public static void main(String[] args) throws Exception {
		
		Tester t = new Tester();
		t.test();
		
		int[] val = new int[] { 1, 2, 3, 4, 5, 6 };
		List<List<Integer>> all = new ArrayList<List<Integer>>();
		List<Integer> l1 = new ArrayList<Integer>();
		List<Integer> l2 = new ArrayList<Integer>();
		List<Integer> l3 = new ArrayList<Integer>();
		
		l1.add(1);
		l1.add(2);
		
		l2.add(1);
		l2.add(2);
		l2.add(3);
		
		l3.add(1);
		l3.add(2);
		
		all.add(l1);
		all.add(l2);
		all.add(l3);
		
		
		List<List<Integer>>perms = Permutator.permutations(all);
		System.out.println(perms.size());
		Iterator<List<Integer>> it = perms.iterator();
		while(it.hasNext()){
			System.out.println(it.next());
		}
		
		List<List<Integer>>perms2 = Permutator.permutations(all, false);
		System.out.println(perms2.size());
		it = perms2.iterator();
		while(it.hasNext()){
			System.out.println(it.next());
		}
		
		
	}
}