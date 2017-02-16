package util;

import java.util.Comparator;

public class MyDoubleComparable  implements Comparator<Double>{
	
	@Override
    public int compare(Double o1, Double o2) {
        return (o1<o2 ? -1 : (o1==o2 ? 0 : 1));
    }

}
