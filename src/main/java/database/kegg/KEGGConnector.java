package database.kegg;

import api.payloads.kegg.KeggPathway;
import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;
import biologicalObjects.edges.*;
import biologicalObjects.nodes.*;
import configurations.Wrapper;
import graph.CreatePathway;
import graph.jung.classes.MyGraph;
import gui.MainWindow;
import pojos.DBColumn;

import javax.swing.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KEGGConnector extends SwingWorker<Object, Object> {
    private String title;
    private final String organism;
    private final String pathwayId;
    private final String pathwayNumber;
    private Pathway pw;

    private List<DBColumn> allOrgElements = new ArrayList<>();
    private List<DBColumn> allEcElements = new ArrayList<>();
    private List<DBColumn> allRnElements = new ArrayList<>();
    private List<DBColumn> allKoElements = new ArrayList<>();

    private List<DBColumn> allOrgRelations = new ArrayList<>();
    private List<DBColumn> allEcRelations = new ArrayList<>();
    private List<DBColumn> allRnRelations = new ArrayList<>();
    private List<DBColumn> allKoRelations = new ArrayList<>();

    private List<DBColumn> allOrgReactions = new ArrayList<>();
    private List<DBColumn> allEcReactions = new ArrayList<>();
    private List<DBColumn> allRnReactions = new ArrayList<>();
    private List<DBColumn> allKoReactions = new ArrayList<>();

    private final boolean dontCreatePathway;

    private static class KeggNodeDescription {
        public String keggPathwayName;
        public String keggEntryId;

        public KeggNodeDescription(String keggPathwayName, String keggEntryId) {
            this.keggEntryId = keggEntryId;
            this.keggPathwayName = keggPathwayName;
        }

        public int hashCode() {
            return keggEntryId.hashCode() ^ keggPathwayName.hashCode();
        }

        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }
            KeggNodeDescription knd = (KeggNodeDescription) o;
            return knd.keggEntryId.equals(keggEntryId) && knd.keggPathwayName.equals(keggPathwayName);
        }
    }

    private final HashMap<KeggNodeDescription, BiologicalNodeAbstract> nodeLowToHighPriorityMap = new HashMap<>();

    public KEGGConnector(String pathwayId, String organism, boolean dontCreatePathway) {
        this.pathwayId = pathwayId;
        this.organism = organism;
        this.dontCreatePathway = dontCreatePathway;
        Pattern pathwayNumberPattern = Pattern.compile(".+([0-9]+)");
        Matcher pathwayNumberMatcher = pathwayNumberPattern.matcher(pathwayId);
        if (pathwayNumberMatcher.matches())
            pathwayNumber = pathwayNumberMatcher.group(1);
        else
            pathwayNumber = pathwayId;
    }

    public Pathway getPw() {
        return pw;
    }

    @Override
    protected Void doInBackground() {
        MainWindow.getInstance().showProgressBar("Loading Data");
        KeggPathway pathway = KeggSearch.getPathway(pathwayId);
        if (pathway != null) {
            title = pathway.name;
        }
        allOrgElements = getPathwayElements(pathwayId);
        allEcElements = getPathwayElements("ec" + pathwayNumber);
        allRnElements = getPathwayElements("rn" + pathwayNumber);
        allKoElements = getPathwayElements("ko" + pathwayNumber);

        allOrgRelations = getRelations(pathwayId);
        allEcRelations = getRelations("ec" + pathwayNumber);
        allRnRelations = getRelations("rn" + pathwayNumber);
        allKoRelations = getRelations("ko" + pathwayNumber);

        allOrgReactions = getAllReactions(pathwayId);
        allEcReactions = getAllReactions("ec" + pathwayNumber);
        allRnReactions = getAllReactions("rn" + pathwayNumber);
        allKoReactions = getAllReactions("ko" + pathwayNumber);
        return null;
    }

    @Override
    public void done() {
        if (dontCreatePathway)
            pw = new Pathway(title);
        else if (title != null)
            pw = new CreatePathway(title).getPathway();
        else
            pw = new CreatePathway().getPathway();

        pw.setOrganism(organism);
        pw.setLink("https://www.genome.jp/pathway/" + pathwayId);

        MyGraph myGraph = pw.getGraph();

        drawNodes(allOrgElements);
        drawNodes(allEcElements);
        drawNodes(allRnElements);
        drawNodes(allKoElements);

        drawReactions(allOrgReactions);
        drawReactions(allEcReactions);
        drawReactions(allRnReactions);
        drawReactions(allKoReactions);

        drawRelations(allOrgRelations);
        drawRelations(allEcRelations);
        drawRelations(allRnRelations);
        drawRelations(allKoRelations);

        myGraph.restartVisualizationModel();
        myGraph.normalCentering();
        pw.saveVertexLocations();
        MainWindow window = MainWindow.getInstance();
        window.updateOptionPanel();
        firePropertyChange("finished", null, "finished");
    }

    private void processKeggElements(String[] set) {
        KEGGNode node = new KEGGNode();
        //  0 - k.id
        //  1 - k.entry_type
        //  2 - n.name
        //  3 - g.bgcolor
        //  4 - g.fgcolor
        //  5 - g.name
        //  6 - g.graphics_type
        //  7 - g.x
        //  8 - g.y
        //  9 - c.name
        // 10 - p.name
        // 11 - k.pathway_name
        node.setKEGGentryID(set[0]);
        node.setKEGGentryLink("http://www.kegg.jp/dbget-bin/www_bget?" + set[2]);
        node.setKEGGentryType(set[1]);
        node.setKEGGentryName(set[2]);
        node.setKEGGPathway(set[11]);
        node.setNodeLabel(set[2]);
        node.setBackgroundColour(set[3]);
        node.setForegroundColour(set[4]);
        node.setShape(set[6]);
        try {
            node.setXPos(Double.parseDouble(set[7]));
            node.setYPos(Double.parseDouble(set[8]));
        } catch (NumberFormatException ignored) {
        }
        BiologicalNodeAbstract bna = null;
        switch (set[1]) {
            case "gene":
                String label = set[5].split(",")[0];
                if (label != null) {
                    node.setNodeLabel(label);
                }
                bna = new DNA(node.getNodeLabel(), node.getKEGGentryName());
                break;
            case "compound":
                node.setNodeLabel(set[9]);
                bna = new Metabolite(node.getNodeLabel(), node.getKEGGentryName());
                break;
            case "ortholog":
                bna = new OrthologGroup(node.getNodeLabel(), node.getKEGGentryName());
                break;
            case "map":
                node.setNodeLabel(set[10]);
                bna = new PathwayMap(node.getNodeLabel(), node.getKEGGentryName());
                break;
            case "enzyme":
                bna = new Enzyme(node.getNodeLabel(), node.getKEGGentryName());
                break;
            case "other":
            case "undefiened": // TODO: typo for a reason?
                bna = new Other(node.getNodeLabel(), node.getKEGGentryName());
                break;
            case "group":
                bna = new Complex("Complex", "");
                break;
        }
        if (bna != null) {
            bna.setKEGGnode(node);
            bna.setHasKEGGNode(true);
            boolean addBNA = true;
            for (BiologicalNodeAbstract old_bna : pw.getVertices().keySet()) {
                KEGGNode oldKeggNode = old_bna.getKEGGnode();
                if (oldKeggNode.getXPos() == node.getXPos() && oldKeggNode.getYPos() == node.getYPos()) {
                    if (keggVisualizationPriority(bna) > keggVisualizationPriority(old_bna)) {
                        pw.removeElement(old_bna);
                        nodeLowToHighPriorityMap.put(
                                new KeggNodeDescription(oldKeggNode.getKEGGPathway(), oldKeggNode.getKEGGentryID()),
                                bna);
                        if (nodeLowToHighPriorityMap.containsValue(old_bna)) {
                            KeggNodeDescription deleteKey = null;
                            for (Entry<KeggNodeDescription, BiologicalNodeAbstract> entry : nodeLowToHighPriorityMap.entrySet()) {
                                if (entry.getValue().equals(old_bna))
                                    deleteKey = entry.getKey();
                            }
                            nodeLowToHighPriorityMap.remove(deleteKey);
                            nodeLowToHighPriorityMap.put(deleteKey, bna);
                        }
                    } else {
                        addBNA = false;
                        nodeLowToHighPriorityMap.put(new KeggNodeDescription(bna.getKEGGnode().getKEGGPathway(),
                                        bna.getKEGGnode().getKEGGentryID()),
                                old_bna);
                    }
                    break;
                }
            }
            if (addBNA) {
                pw.addVertex(bna, new Point2D.Double(bna.getKEGGnode().getXPos(), bna.getKEGGnode().getYPos()));
            }
        }
    }

    private int keggVisualizationPriority(BiologicalNodeAbstract bna) {
        if (bna instanceof Enzyme)
            return 3;
        else if (bna instanceof DNA)
            return 2;
        else if (bna instanceof PathwayMap)
            return 1;
        else if (bna instanceof Metabolite)
            return 0;
        else if (bna instanceof OrthologGroup)
            return -2;
        else if (bna instanceof Complex)
            return -1;
        else if (bna instanceof Other)
            return -3;
        return 0;
    }

    private void drawNodes(List<DBColumn> allElements) {
        for (DBColumn column : allElements) {
            processKeggElements(column.getColumn());
        }
    }

    private void drawRelations(List<DBColumn> allGeneralRelations) {
        for (DBColumn column : allGeneralRelations) {
            // 0 - relation.pathway_name
            // 1 - subtype.name
            // 2 - subtype.subtype_value
            // 3 - relation.entry1
            // 4 - relation.entry2
            String keggPathway = column.getColumn()[0];
            String edgeType = column.getColumn()[1];
            String subtypeValue = column.getColumn()[2];
            String entry1 = column.getColumn()[3];
            String entry2 = column.getColumn()[4];
            BiologicalNodeAbstract bna1 = null;
            BiologicalNodeAbstract subtype = null;
            BiologicalNodeAbstract bna2 = null;
            for (BiologicalNodeAbstract bna : pw.getVertices().keySet()) {
                if (bna.getKEGGnode() != null && bna.getKEGGnode().getKEGGentryID().equals(entry1) &&
                    bna.getKEGGnode().getKEGGPathway().equals(keggPathway))
                    bna1 = bna;
                if (bna.getKEGGnode() != null && bna.getKEGGnode().getKEGGentryID().equals(entry2) &&
                    bna.getKEGGnode().getKEGGPathway().equals(keggPathway))
                    bna2 = bna;
                if (bna.getKEGGnode() != null && bna.getKEGGnode().getKEGGentryID().equals(subtypeValue) &&
                    bna.getKEGGnode().getKEGGPathway().equals(keggPathway))
                    subtype = bna;
            }

            if (!pw.containsVertex(bna1))
                bna1 = nodeLowToHighPriorityMap.get(new KeggNodeDescription(keggPathway, entry1));
            if (!pw.containsVertex(bna2))
                bna2 = nodeLowToHighPriorityMap.get(new KeggNodeDescription(keggPathway, entry2));
            if (!pw.containsVertex(subtype))
                subtype = nodeLowToHighPriorityMap.get(new KeggNodeDescription(keggPathway, subtypeValue));

            if (bna1 != null && bna2 != null) {
                if (subtype != null) {
                    // Vertex subVertex = subtype.getVertex();
                    if (!pw.existEdge(bna1, subtype) && !pw.existEdge(subtype, bna1)) {
                        Compound c = new Compound("", "", bna1, subtype);
                        c.setDirected(true);
                        pw.addEdge(c);
                        pw.updateMyGraph();
                    }

                    if (!pw.existEdge(subtype, bna2) && !pw.existEdge(bna2, subtype)) {
                        Compound c2 = new Compound("", "", subtype, bna2);
                        c2.setDirected(true);
                        pw.addEdge(c2);
                        pw.updateMyGraph();
                    }
                } else {
                    if (!pw.existEdge(bna1, bna2) && !pw.existEdge(bna2, bna1)) {
                        BiologicalEdgeAbstract bea;
                        switch (edgeType) {
                            case Elementdeclerations.dephosphorylationEdge:
                                bea = new Dephosphorylation("-p", "", bna1, bna2);
                                break;
                            case Elementdeclerations.phosphorylationEdge:
                                bea = new Phosphorylation("+p", "", bna1, bna2);
                                break;
                            case Elementdeclerations.methylationEdge:
                                bea = new Methylation("+m", "", bna1, bna2);
                                break;
                            case Elementdeclerations.ubiquitinationEdge:
                                bea = new Ubiquitination("+u", "", bna1, bna2);
                                break;
                            case Elementdeclerations.glycosylationEdge:
                                bea = new Glycosylation("+g", "", bna1, bna2);
                                break;
                            default:
                                bea = BiologicalEdgeAbstractFactory.create(edgeType, null);
                                bea.setFrom(bna1);
                                bea.setTo(bna2);
                                break;
                        }
                        bea.setDirected(true);
                        pw.addEdge(bea);
                        pw.updateMyGraph();
                    }
                }
            }
        }
    }

    private void drawReactions(List<DBColumn> allReactions) {
        for (DBColumn column : allReactions) {
            // 0 - s.id
            // 1 - e.id
            // 2 - p.id
            // 3 - r.reaction_type
            // 4 - e.pathway_name
            String substrateId = column.getColumn()[0];
            String enzymeId = column.getColumn()[1];
            String productId = column.getColumn()[2];
            boolean reversible = column.getColumn()[3].equals("reversible");
            String keggPathway = column.getColumn()[4];
            BiologicalNodeAbstract substrate = null;
            BiologicalNodeAbstract enzyme = null;
            BiologicalNodeAbstract product = null;
            for (BiologicalNodeAbstract bna : pw.getVertices().keySet()) {
                if (bna.getKEGGnode() == null) {
                    continue;
                }
                KEGGNode keggNode = bna.getKEGGnode();
                if (keggNode.getKEGGPathway().equals(keggPathway)) {
                    if (keggNode.getKEGGentryID().equals(substrateId))
                        substrate = bna;
                    if (keggNode.getKEGGentryID().equals(productId))
                        product = bna;
                    if (keggNode.getKEGGentryID().equals(enzymeId))
                        enzyme = bna;
                }
            }
            if (!pw.containsVertex(substrate))
                substrate = nodeLowToHighPriorityMap.get(new KeggNodeDescription(keggPathway, substrateId));
            if (!pw.containsVertex(product))
                product = nodeLowToHighPriorityMap.get(new KeggNodeDescription(keggPathway, productId));
            if (!pw.containsVertex(enzyme))
                enzyme = nodeLowToHighPriorityMap.get(new KeggNodeDescription(keggPathway, enzymeId));

            if (substrate != null && product != null && enzyme != null) {
                if (!pw.existEdge(substrate, enzyme) && !pw.existEdge(enzyme, substrate)) {
                    Compound c;
                    if (reversible) {
                        c = new Compound("", "", enzyme, substrate);
                    } else {
                        c = new Compound("", "", substrate, enzyme);
                    }
                    pw.addEdge(c);
                    c.setDirected(true);
                    pw.updateMyGraph();
                }
                if (!pw.existEdge(enzyme, product) && !pw.existEdge(product, enzyme)) {
                    Compound c2 = new Compound("", "", enzyme, product);
                    pw.addEdge(c2);
                    c2.setDirected(true);
                    pw.updateMyGraph();
                }
            }
        }
    }

    public ArrayList<DBColumn> getPathwayElements(String pathwayID) {
        String query =
                "SELECT k.id, k.entry_type, n.name, g.bgcolor, g.fgcolor, g.name, g.graphics_type, g.x, g.y, c.name, p.name, k.pathway_name " +
                "FROM kegg_kgml_entry k LEFT OUTER JOIN kegg_kgml_entry_name n ON k.entry_id=n.entry_ID " +
                "INNER JOIN kegg_kgml_graphics g ON k.entry_id=g.entry_ID " +
                "LEFT OUTER JOIN kegg_compound_name c ON n.name=c.entry " +
                "LEFT OUTER JOIN kegg_pathway p ON n.name=p.entry " + "WHERE k.pathway_name='" + pathwayID + "' " +
                "AND (length(c.name)=(Select min(length(d.name)) FROM kegg_compound_name d WHERE n.name=d.entry) OR c.name IS NULL) " +
                "GROUP BY k.entry_id;";
        return new Wrapper().requestDbContent(query);
    }

    public ArrayList<DBColumn> getRelations(String pathwayID) {
        String query =
                "SELECT relation.pathway_name, subtype.name,subtype.subtype_value,relation.entry1,relation.entry2 " +
                "FROM kegg_kgml_subtype subtype NATURAL JOIN kegg_kgml_relation relation WHERE pathway_name='" + pathwayID + "'ORDER BY relation_id;";
        return new Wrapper().requestDbContent(query);
    }

    public ArrayList<DBColumn> getAllReactions(String pathwayID) {
        String query = "SELECT s.id, e.id, p.id, r.reaction_type, e.pathway_name FROM kegg_kgml_reaction r " +
                       "INNER JOIN kegg_kgml_substrate s ON r.reaction_id=s.reaction_id " +
                       "INNER JOIN kegg_kgml_product p ON r.reaction_id=p.reaction_id " +
                       "INNER JOIN kegg_kgml_entry_reaction er ON er.reaction=r.name " +
                       "INNER JOIN kegg_kgml_entry e ON er.entry_id=e.entry_id " +
                       "INNER JOIN kegg_kgml_entry_name en ON e.entry_id=en.entry_id WHERE r.pathway_name='" + pathwayID + "' AND e.pathway_name='" + pathwayID + "'; ";
        return new Wrapper().requestDbContent(query);
    }
}