package database.kegg;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class KEGGUtils {
    private static final Pattern PATHWAY_PATTERN = Pattern.compile("([a-z]+):?([0-9]+)");

    private KEGGUtils() {
    }

    public static Integer getPathwayOrg(String id) {
        Matcher matcher = PATHWAY_PATTERN.matcher(id);
        if (matcher.matches())
            return Integer.parseInt(matcher.group(2));
        return null;
    }

    public static String getPathwayNumber(String id) {
        Matcher matcher = PATHWAY_PATTERN.matcher(id);
        if (matcher.matches())
            return matcher.group(1);
        return null;
    }
}
