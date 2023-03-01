package database.Connection;

import java.util.StringTokenizer;

public class DatabaseQueryValidator {
    private boolean notLike = false;
    private boolean orOperator = false;

    public String prepareString(String temp, String attribute, String attribute2) {
        StringTokenizer st = new StringTokenizer(temp, "&|", true);
        StringBuilder buffer = new StringBuilder();
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
        String query;
        boolean not = false;
        if (temp.startsWith("!")) {
            temp = temp.substring(1);
            not = true;
        }
        if (not) {
            if (notLike && orOperator) {
                query = "OR " + attribute + " not like '%" + temp + "%' ";
            } else if (orOperator) {
                query = "OR " + attribute + " not like '%" + temp + "%' ";
            } else {
                query = attribute + " not like '%" + temp + "%' ";
            }
            notLike = true;
        } else {
            if (orOperator) {
                query = "OR " + attribute + " like '%" + temp + "%' ";
            } else {
                query = attribute + " like '%" + temp + "%' ";
            }
            notLike = false;
        }
        return query;
    }
}
