package database.Connection;

import java.util.StringTokenizer;

public class DatabaseQueryValidator {

	private boolean notLike = false;
	private boolean orOperator = false;

	public DatabaseQueryValidator() {
	}

	private boolean iterationReplacement(String replacement) {

		if (replacement.startsWith("&") || replacement.startsWith("|")
				|| replacement.endsWith("&") || replacement.endsWith("|"))
			return false;
		else
			return true;

	}

	public String replaceAndValidateString(String temp) {

		String newString = temp.trim();

		while (!iterationReplacement(newString)) {

			if (newString.startsWith("&") || newString.startsWith("|")) {
				newString = newString.substring(1);
			}

			if (newString.endsWith("&") || newString.endsWith("|")) {
				newString = newString.substring(0, newString.length() - 1);
			}
			newString = newString.trim();
		}

		return newString;
	}

	public String prepareString(String temp, String attribute, String attribute2) {

		StringTokenizer st = new StringTokenizer(temp, "&|", true);
		StringBuffer buffer = new StringBuffer();
		boolean first = false;

		notLike = false;
		orOperator = false;

		buffer.append("(");
		while (st.hasMoreTokens()) {

			String t = st.nextToken().trim();

			if (!first) {
				if (attribute2 == null) {
					buffer.append(prepareToken(attribute, t));
				} else {
					buffer.append("(");
					buffer.append(prepareToken(attribute, t));
					if (notLike) {
						buffer.append("AND ");
					} else {
						buffer.append("OR ");
					}
					buffer.append(prepareToken(attribute2, t));
					buffer.append(")");
				}
				first = true;
			} else {
				if (t.equals("&")) {
					buffer.append("AND ");
					orOperator = false;
				} else if (t.equals("|")) {
					orOperator = true;
				} else {
					if (attribute2 == null) {
						buffer.append(prepareToken(attribute, t));
					} else {
						if (orOperator) {
							buffer.append(" OR ");
							orOperator = false;
						}
						buffer.append("(");
						buffer.append(prepareToken(attribute, t));
						if (notLike) {
							buffer.append("AND ");
						} else {
							buffer.append("OR ");
						}
						buffer.append(prepareToken(attribute2, t));
						buffer.append(")");
					}
				}
			}
		}
		buffer.append(")");
		return buffer.toString();
	}

	private String prepareToken(String attribute, String temp) {

		String query = "";
		StringBuffer buffer = new StringBuffer();
		boolean not = false;

		if (temp.startsWith("!")) {

			String sub = temp.substring(1, temp.length());
			temp = sub;
			not = true;
		}

		if (not) {

			if (notLike && orOperator) {
				query = "AND " + attribute + " not like '%" + temp + "%' ";
			} else if (orOperator) {
				query = "Or " + attribute + " not like '%" + temp + "%' ";
			} else {
				query = attribute + " not like '%" + temp + "%' ";
			}

			notLike = true;

		} else {

			if (orOperator) {
				query = "Or " + attribute + " like '%" + temp + "%' ";
			} else {
				query = attribute + " like '%" + temp + "%' ";
			}

			notLike = false;
		}

		return query;
	}

}
