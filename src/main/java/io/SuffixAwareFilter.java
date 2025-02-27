package io;

import org.apache.commons.io.FilenameUtils;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class SuffixAwareFilter extends FileFilter {
    public static final SuffixAwareFilter PNG = new SuffixAwareFilter("png", "PNG Image (*.png)");
    public static final SuffixAwareFilter SVG = new SuffixAwareFilter("svg", "SVG Image (*.svg)");
    public static final SuffixAwareFilter PDF = new SuffixAwareFilter("pdf", "PDF File (*.pdf)");
    public static final SuffixAwareFilter YAML = new SuffixAwareFilter("yaml", "YAML File (*.yaml)");

    public static final SuffixAwareFilter VAML = new SuffixAwareFilter("vaml", "VANESA Markup Language (*.vaml)");
    public static final SuffixAwareFilter SBML = new SuffixAwareFilter("sbml",
            "System Biology Markup Language (*.sbml)");
    public static final SuffixAwareFilter CSML = new SuffixAwareFilter("csml", "Cell Illustrator (*.csml)");
    public static final SuffixAwareFilter GML = new SuffixAwareFilter("gml", "Graph Markup Language (*.gml)");
    public static final SuffixAwareFilter MO = new SuffixAwareFilter("mo", "Modelica Model (*.mo)");
    public static final SuffixAwareFilter TRANSFORMATION_RULES = new SuffixAwareFilter("yaml",
            "Transformation rules (*.yaml)");
    public static final SuffixAwareFilter NEW_MODELICA_RESULT_DESCRIPTION = new SuffixAwareFilter("csv",
            "New Modelica Simulation Result File (*.csv)");
    public static final SuffixAwareFilter MODELICA_RESULT_DESCRIPTION = new SuffixAwareFilter("plt",
            "Modellica Simulation Result File (*.plt)");
    public static final SuffixAwareFilter VANESA_SIM_RESULT = new SuffixAwareFilter("csv",
            "VANESA Simulation Result File (*.csv)");
    public static final SuffixAwareFilter GRAPH_ML = new SuffixAwareFilter("graphml",
            "Graph Markup Language (*.graphml)");
    public static final SuffixAwareFilter KGML = new SuffixAwareFilter("kgml",
            "KEGG Markup Language (*.kgml)");
    public static final SuffixAwareFilter GRAPH_TEXT_FILE = new SuffixAwareFilter("txt", "Graph Text File (*.txt)");
    public static final SuffixAwareFilter PNML = new SuffixAwareFilter("pnml", "Petri Net Markup Language (*.pnml)");
    public static final SuffixAwareFilter PN_DOC = new SuffixAwareFilter("html", "Petri Net Documentation (*.html)");

    private final String extension;
    private final String description;

    private SuffixAwareFilter(final String extension, final String description) {
        this.extension = extension;
        this.description = description;
    }

    @Override
    public boolean accept(File f) {
    	if(f.getPath().contains("::")){
    		// System.out.println("problem: "+f.getPath());
    		return f.isDirectory();
    	}
        final String suffix = FilenameUtils.getExtension(f.getPath());
        return f.isDirectory() || suffix.equals(extension);
    }

    @Override
    public String getDescription() {
        return description;
    }

    public String getExtension() {
        return extension;
    }

    @Override
    public String toString() {
        return description;
    }
}
