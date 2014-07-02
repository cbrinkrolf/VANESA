package graph.algorithms;

import java.util.Comparator;

/*
 * Vergleicht DCBexpansion-Objekte anhand ihrer Anzahl von Nachbarn (Summe über alle Seeds der Objekte)
 */
public class DCBexpansionComparator implements Comparator<DCBexpansion>{

	public DCBexpansionComparator() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public int compare(DCBexpansion seedList1, DCBexpansion seedList2) {
		return seedList1.getNumOfNeighbours() - seedList2.getNumOfNeighbours();
	}

}
