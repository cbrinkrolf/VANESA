package transformation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import transformation.Permutator;
import transformation.Tester;

public class main {
	public static void main(String[] args) throws Exception {
		// Einige Tests für den Algorithmus
		/*
		 * String[] tests = new String[] { "1+1", "2+4*5", "(2+4)*5", "e", "5",
		 * "(((5)))", "1 +-+-+ .25", "min( 1, 3 )", "sqrt 9/3", "sqrt (9/3)",
		 * "sqrt16" , "av/1"};
		 * 
		 * FunctionParser parser = new FunctionParser(); // parser.parse("");
		 * 
		 * for (String test : tests)
		 * System.out.printf("\"%s\" wird zu \"%s\"\n", test, parser
		 * .parse(test));
		 * 
		 * Map<String, String> map = new TreeMap<String, String>( new
		 * Comparator<String>() {
		 * 
		 * @Override public int compare(String str1, String str2) { return
		 * str2.length() - str1.length(); } });
		 * 
		 * map.put("P1", "1"); map.put("P11", "2"); // map.put("P123", "3");
		 * map.put("P34", "4"); map.put("P", "5");
		 * 
		 * // System.out.println(map.keySet().toArray()); String t =
		 * "P1+P11+P123412"; System.out.println(t); Object[] k =
		 * map.keySet().toArray(); Character c;
		 * System.out.println("mapsize: "+map.size()); for (Object o : k) {
		 * System.out.println("place: "+o); while (t.indexOf(o.toString()) >= 0)
		 * { c = t.charAt(t.indexOf(o.toString()) + o.toString().length());
		 * System.out.println("nachfolgendes c: "+c); if (!Character.isDigit(c))
		 * { //System.out.println(o + " :" + t.indexOf(o.toString())); t =
		 * t.replaceFirst(o.toString(), map.get(o)); System.out.println(t);
		 * }else{ System.out.println("Error"); break; } }
		 * 
		 * } System.out.println(t);
		 * 
		 * }
		 */

		/*
		 * int ergebnis = 2; int zahl = 10; int haelfte = 0; int passt = 0;
		 * //int i; for(int i = 1; i< zahl; i = i+2){ haelfte = (i-1)/2; passt =
		 * 1; for(int k = 3; k<haelfte; k = k+1){ if((i/k)*k == i){ passt = 0; }
		 * } if(passt == 1){ ergebnis = ergebnis + i; System.out.println(i); } }
		 * System.out.println(ergebnis); }
		 */

		/*
		 * int zahl = -5; int ergebnis = 0; if(zahl < 0){ zahl = zahl * -1; }
		 * zahl = Math.abs(zahl); zahl = (int)Math.sqrt(zahl*zahl); while(zahl
		 * != 0){ //if (zahl > 0){ ergebnis = ergebnis + zahl*zahl; zahl =
		 * zahl-1 ; //} } System.out.println(ergebnis);
		 */

		/*System.out.println("hallo");

		int[] liste = new int[5];
		liste[0] = 3;
		liste[1] = 6;
		liste[2] = 1;
		liste[3] = 0;
		liste[4] = 7;

		for (int i = 0; i < 5; i++) {
			// System.out.println(liste[i]);
		}


		Random abc = new Random();
		for (int i = 0; i < 10; i++) {
			//System.out.println(abc.nextInt(10));
		}
		
		int i =0;
		while(i <5){
			
			System.out.println(liste[i]);
			i++;
		}
		
		while(true){
			int a = abc.nextInt(10);
			System.out.println(a);
			if(a == 5){
				break;
			}
		}*/
		
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