package util;

import java.util.Comparator;

public class StringLengthComparator implements Comparator<String> {
	// compares descending
	public int compare(String s1, String s2) {
		return s2.length() - s1.length();
	}
}
