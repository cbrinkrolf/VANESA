package save.graphPicture;

import java.io.File;

public class SuffixAwareFilter extends javax.swing.filechooser.FileFilter {
    public static final SuffixAwareFilter PNG = new SuffixAwareFilter("png", "PNG Files (*.png)");
    public static final SuffixAwareFilter VAML = new SuffixAwareFilter("vaml", "VANESA Markup Language (*.vaml)");
    public static final SuffixAwareFilter SBML = new SuffixAwareFilter("sbml",
                                                                       "System Biology Markup Language (*.sbml)");
    public static final SuffixAwareFilter CSML = new SuffixAwareFilter("csml", "Cell Illustrator (*.csml)");
    public static final SuffixAwareFilter GML = new SuffixAwareFilter("gml", "Graph Markup Language (*.gml)");
    public static final SuffixAwareFilter MO = new SuffixAwareFilter("mo", "Modelica File (*.mo)");

    private final String extension;
    private final String description;

    private SuffixAwareFilter(final String extension, final String description) {
        this.extension = extension;
        this.description = description;
    }

    @Override
    public boolean accept(File f) {
        final String suffix = getSuffix(f);
        return suffix != null && (f.isDirectory() || suffix.equals(extension));
    }

    private String getSuffix(File f) {
        String s = f.getPath(), suffix = null;
        int i = s.lastIndexOf('.');
        if (i > 0 && i < s.length() - 1) {
            suffix = s.substring(i + 1).toLowerCase();
        }
        return suffix;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
