package util;

public abstract class FormularSafety {

	static public String replace(String s) {
		// System.out.println("before: "+s);
		s = s.trim();
		s = s.replaceAll("\\*", "_star_");
		s = s.replaceAll("\\+", "_plus_");
		s = s.replaceAll("/", "_slash_");
		// s = s.replaceAll("-", "_");
		// s = s.replaceAll(",", "_");
		// s = s.replaceAll("\\.", "_");
		s = s.replaceAll("\\^", "_pow_");
		// s = s.replaceAll("\\(", "_");
		// s = s.replaceAll("\\)", "_");
		// s = s.replaceAll("\\s", "_");
		// s = s.replaceAll("_+\\b", "");
		s = s.replaceAll("'", "");
		s = s.replaceAll("\"", "");

		// word characters: [a-z],[A-Z],[0-9],_
		s = s.replaceAll("\\W+", "_"); // replace all non-word characters with underscore
		s = s.replaceAll("\\b_+", ""); // delete all leading and tailing underscores
		s = s.replaceAll("_{2,}", "_"); // replace all mutliple underscores to a single one
		// replace leading digits with "n" in front of them
		s = s.replaceAll("(\\b\\d+)", "n$1");
		// System.out.println("after: "+s);
		return s;
	}
}
