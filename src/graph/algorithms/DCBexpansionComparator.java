package graph.algorithms;

import java.util.Comparator;

/*
 * Vergleicht DCBexpansion-Objekte anhand ihrer Anzahl von Nachbarn (Summe �ber alle Seeds der Objekte)
 */
public class DCBexpansionComparator implements Comparator<DCBexpansion>{

	public DCBexpansionComparator() {
	}
	
	@Override
	public int compare(DCBexpansion seedList1, DCBexpansion seedList2) {
		return seedList1.getNumOfNeighbours() - seedList2.getNumOfNeighbours();
	}

}
