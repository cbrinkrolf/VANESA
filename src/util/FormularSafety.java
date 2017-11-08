package util;

public abstract class FormularSafety {
	
	static public String replace(String s){
		s = s.trim();
		s = s.replaceAll("\\*", "_star_");
		s = s.replaceAll("\\+", "_plus_");
		s = s.replaceAll("/", "_slash_");
		s = s.replaceAll("-", "_");
		s = s.replaceAll(",", "_");
		s = s.replaceAll("\\.", "_");
		s = s.replaceAll("\\^", "_pow_");
		s = s.replaceAll("\\(", "_");
		s = s.replaceAll("\\)", "_");
		s = s.replaceAll("\\s", "_");
		s = s.replaceAll("_{2,}", "_");
		s = s.replaceAll("\\b_+", "");
		s = s.replaceAll("_+\\b", "");
		s = s.replaceAll("'", "");
		s = s.replaceAll("\"", "");
		//replace leading digits with "n" in front of them
		s = s.replaceAll("(\\b\\d+)", "n$1");
		return s;
	}
}
