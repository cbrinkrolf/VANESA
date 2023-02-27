package database.brenda;

import api.VanesaApi;
import api.payloads.Response;
import api.payloads.dbBrenda.*;
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.edges.ReactionEdge;
import biologicalObjects.nodes.*;
import com.fasterxml.jackson.core.type.TypeReference;
import configurations.Wrapper;
import edu.uci.ics.jung.algorithms.shortestpath.UnweightedShortestPath;
import graph.CreatePathway;
import graph.algorithms.MergeGraphs;
import graph.hierarchies.EnzymeNomenclature;
import graph.hierarchies.HierarchyList;
import graph.hierarchies.HierarchyListComparator;
import graph.jung.classes.MyGraph;
import gui.MainWindow;
import pojos.DBColumn;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.util.*;
import java.util.List;

public class BrendaConnector extends SwingWorker<Object, Object> {
    private MyGraph myGraph;
    private boolean cofactors = false;
    private boolean inhibitors = false;
    private Pathway pw = null;
    private String title = "";
    private String pathwayLink = "";
    private int searchDepth = 4;
    private final BrendaTree tree = new BrendaTree();
    protected String enzymeOrganism = "";
    private boolean organism_specific = false;
    private boolean disregarded = false;
    private final MostWantedMolecules box = MostWantedMolecules.getInstance();
    private final DBBrendaEnzyme enzyme;
    protected Hashtable<String, BiologicalNodeAbstract> enzymes = new Hashtable<>();
    private final Set<BiologicalEdgeAbstract> edges = new HashSet<>();
    private final Pathway mergePW;
    boolean autoCoarseDepth = false;
    boolean autoCoarseEnzymeNomenclature = false;

    public BrendaConnector(DBBrendaEnzyme enzyme, Pathway mergePW) {
        this.enzyme = enzyme;
        this.mergePW = mergePW;
    }

    private void startVisualizationModel() {
        myGraph.restartVisualizationModel();
    }

    private void getPathway() {
        title = "BRENDA Pathway";
        pathwayLink = "";
    }

    private BiologicalNodeAbstract addReactionNodes(String node) {
        String clean = this.cleanString(node);
        if (!enzymes.containsKey(clean)) {
            Metabolite sm = new Metabolite(clean, clean);
            enzymes.put(clean, sm);
            return sm;
        } else {
            return enzymes.get(clean);
        }
    }

    private void searchPossibleEnzymes(BiologicalNodeAbstract node, DefaultMutableTreeNode parentNode) {
        if (parentNode.getLevel() == 0 || (parentNode.getLevel() / 2) < searchDepth + 1) {
            if (!disregarded || !box.getElementValue(node.getLabel())) {
                String queryString = node.getLabel().replaceAll("'", "''").replaceAll("\"", "''");
                if (queryString.contains("?")) {
                    queryString = queryString.replaceAll("\\?", "''");
                }
                ArrayList<DBColumn> results;
                if (organism_specific) {
                    String[] param = {"%" + queryString + "%", "%" + enzymeOrganism + "%"};
                    results = new Wrapper().requestDbContent(1, BRENDAQueries.getPossibleEnzymeDetailsWithOrganism,
                                                             param);
                } else {
                    results = new Wrapper().requestDbContent(1, BRENDAQueries.getPossibleEnzymeDetails + "'%" +
                                                                queryString + "%';");
                }
                String[] left = null;
                String[] right = null;
                String weight = "1";
                for (DBColumn column : results) {
                    String[] resultDetails = column.getColumn();
                    String clean = this.cleanString(resultDetails[0]);
                    if (!enzymes.containsKey(clean)) {
                        resultDetails[3] = resultDetails[3].replace("\uFFFD", "'");
                        String[] gesplittet = resultDetails[3].split("=");
                        if (gesplittet.length == 2) {
                            left = gesplittet[0].split("\\s\\+\\s");
                            right = gesplittet[1].split("\\s\\+\\s");
                        }
                        Enzyme e = new Enzyme(clean, resultDetails[1]);
                        enzymes.put(clean, e);
                        for (String s : left) {
                            String tmp = cleanString(s.trim());
                            String[] split = tmp.split("\\s", 2);
                            if (split[0].matches("\\d+")) {
                                weight = split[0];
                                tmp = split[1];
                            }
                            if (node.getLabel().equals(tmp)) {
                                this.buildEdge(node, e, true, weight);
                                break;
                            }
                        }
                        weight = "1";
                        for (String s : right) {
                            String tmp = cleanString(s.trim());
                            String[] split = tmp.split("\\s", 2);
                            if (split[0].matches("\\d+")) {
                                weight = split[0];
                                tmp = split[1];
                            }
                            if (node.getLabel().equals(tmp)) {
                                this.buildEdge(e, node, true, weight);
                                break;
                            }
                        }
                        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(e.getLabel());
                        tree.addNode(parentNode, newNode, e);
                        if (resultDetails[3] != null) {
                            separateReaction(resultDetails[3], e, newNode);
                        }
                    }
                }
            }
        }
    }

    private void buildEdge(BiologicalNodeAbstract first, BiologicalNodeAbstract second, boolean directed,
                           String weight) {
        if (first != null && second != null) {
            ReactionEdge r = new ReactionEdge(weight, "", first, second);
            r.setFunction(weight);
            r.setDirected(directed);
            r.setVisible(true);
            this.edges.add(r);
        }
    }

    private void drawEdges() {
        for (BiologicalEdgeAbstract bea : edges) {
            if (myGraph.getJungGraph().findEdge(bea.getFrom(), bea.getTo()) == null) {
                pw.addEdge(bea);
            }
        }
    }

    private void separateReaction(String reaction, BiologicalNodeAbstract enzyme, DefaultMutableTreeNode parentNode) {
        int i = 0;
        StringTokenizer tok = new StringTokenizer(reaction, "=");
        int tokenCount = tok.countTokens();
        String[] result = new String[tokenCount];
        while (tok.hasMoreTokens()) {
            result[i++] = tok.nextToken();
        }
        String[] gesplittet = result[0].split("\\s\\+\\s");
        BiologicalNodeAbstract substrate;
        DefaultMutableTreeNode newNode;
        String[] split;
        String weight = "1";
        for (String s : gesplittet) {
            String temp = s.trim();
            // if string begins with number
            split = temp.split("\\s", 2);
            if (split[0].matches("\\d+")) {
                weight = split[0];
                temp = split[1];
            }
            if (!parentNode.getParent().toString().equals(temp)) {
                substrate = addReactionNodes(temp);
                buildEdge(substrate, enzyme, true, weight);
                // buildEdge(substrate.getVertex(), enzyme.getVertex(), true);
                // !!!!!
                newNode = new DefaultMutableTreeNode(substrate.getLabel());
                tree.addNode(parentNode, newNode, substrate);
                searchPossibleEnzymes(substrate, newNode);
            } else {
                substrate = addReactionNodes(temp);
                buildEdge(substrate, enzymes.get(parentNode.toString()), true, weight);
            }
        }
        weight = "1";
        if (tokenCount > 1) {
            String[] gesplittet_b = result[1].split("\\s\\+\\s");
            BiologicalNodeAbstract product;
            String temp;
            for (int j = 0; j < gesplittet_b.length; j++) {
                temp = gesplittet_b[j].trim();
                split = temp.split("\\s", 2);
                if (split[0].matches("\\d+")) {
                    weight = split[0];
                    temp = split[1];
                }
                if (!parentNode.getParent().toString().equals(temp)) {
                    product = addReactionNodes(temp);
                    // buildEdge(enzyme.getVertex(), product.getVertex(), true);
                    buildEdge(enzyme, product, true, weight);
                    newNode = new DefaultMutableTreeNode(product.getLabel());
                    tree.addNode(parentNode, newNode, product);
                    searchPossibleEnzymes(product, newNode);
                } else {
                    buildEdge(enzymes.get(parentNode.toString()), enzymes.get(parentNode.getParent().toString()), true,
                              weight);
                }
            }
        }
    }

    protected void processBrendaElement(String enzyme, DefaultMutableTreeNode node) {
        if (node.getLevel() == 0 || (node.getLevel() / 2) < searchDepth) {
            String[] param = {enzyme};
            ArrayList<DBColumn> results = new Wrapper().requestDbContent(1, BRENDAQueries.getBRENDAenzymeDetails,
                                                                         param);
            Enzyme e;
            DefaultMutableTreeNode newNode;
            String[] resultDetails;
            String clean;
            for (DBColumn column : results) {
                resultDetails = column.getColumn();
                clean = this.cleanString(resultDetails[0]);
                if (!enzymes.containsKey(clean)) {
                    e = new Enzyme(clean, resultDetails[1]);
                    e.setColor(Color.RED);
                    pw.setRootNode(e);
                    e.setHasBrendaNode(true);
                    newNode = new DefaultMutableTreeNode(e.getLabel());
                    tree.addNode(node, newNode, e);
                    enzymes.put(clean, e);
                    if (resultDetails[3] != null && resultDetails[3].length() > 0) {
                        resultDetails[3] = resultDetails[3].replace("\uFFFD", "'");
                        separateReaction(resultDetails[3], e, newNode);
                    }
                }
            }
        }
    }

    private void drawNodes() {
        for (BiologicalNodeAbstract node : enzymes.values()) {
            pw.addVertex(node, new Point(10, 10));
        }
    }

    /**
     * For autocoarsing the resulting network.
     */
    private void autoCoarseDepth() {
        // The parent of each node is the neighbor with the shortest path to the root node.
        class HLC implements HierarchyListComparator<Integer> {
            final Map<BiologicalNodeAbstract, Number> rootDistanceMap;

            public HLC() {
                Pathway newPw = new Pathway("Brenda Search");
                MyGraph searchGraph = new MyGraph(newPw);
                for (BiologicalNodeAbstract nd : myGraph.getAllVertices()) {
                    searchGraph.addVertex(nd, myGraph.getVertexLocation(nd));
                }
                for (BiologicalEdgeAbstract e : myGraph.getAllEdges()) {
                    BiologicalEdgeAbstract reverseEdge = e.clone();
                    reverseEdge.setFrom(e.getTo());
                    reverseEdge.setTo(e.getFrom());
                    searchGraph.addEdge(e);
                    searchGraph.addEdge(reverseEdge);
                }
                UnweightedShortestPath<BiologicalNodeAbstract, BiologicalEdgeAbstract> path = new UnweightedShortestPath<>(
                        searchGraph.getJungGraph());
                rootDistanceMap = path.getDistanceMap(pw.getRootNode());
            }

            public Integer getValue(BiologicalNodeAbstract n) {
                Set<BiologicalNodeAbstract> neighbors = new HashSet<>(getGraph().getJungGraph().getNeighbors(n));
                BiologicalNodeAbstract bestNeighbor = n;
                int bestDistance = rootDistanceMap.get(n) == null ? Integer.MAX_VALUE : rootDistanceMap.get(n)
                                                                                                       .intValue();
                for (BiologicalNodeAbstract neighbor : neighbors) {
                    if (rootDistanceMap.get(neighbor) != null && (bestNeighbor == n || rootDistanceMap.get(neighbor)
                                                                                                      .intValue() <=
                                                                                       bestDistance)) {
                        if (neighbor instanceof Factor || neighbor instanceof Inhibitor) {
                            continue;
                        }
                        bestNeighbor = neighbor;
                        bestDistance = rootDistanceMap.get(neighbor).intValue();
                    }
                }
                return bestNeighbor.getID();
            }

            public Integer getSubValue(BiologicalNodeAbstract n) {
                return n.getID();
            }
        }
        HierarchyList<Integer> l = new HierarchyList<>();
        l.addAll(myGraph.getAllVertices());
        l.sort(new HLC());
        l.coarse();
    }

    /**
     * For autocoarsing the resulting network.
     */
    private void autoCoarseEnzymeNomenclature() {
        EnzymeNomenclature struc = new EnzymeNomenclature();
        // The parent of each node is the neighbor with the shortest path to the root node.
        class HLC implements HierarchyListComparator<String> {
            final EnzymeNomenclature struc;

            public HLC(EnzymeNomenclature struc) {
                this.struc = struc;
            }

            public String getValue(BiologicalNodeAbstract n) {
                return struc.ECtoClass(n.getLabel());
            }

            public String getSubValue(BiologicalNodeAbstract n) {
                return n.getLabel();
            }
        }
        HierarchyList<String> l = new HierarchyList<>();
        for (BiologicalNodeAbstract n : myGraph.getAllVertices()) {
            if (n instanceof Enzyme) {
                l.add(n);
            }
        }
        l.sort(new HLC(struc), struc);
        l.coarse();
    }

    protected String adoptOrganism(String organism) {
        StringTokenizer tok = new StringTokenizer(organism);
        Vector<String> v = new Vector<>();
        String temp;
        int count = 0;
        boolean breakLoop = false;
        while (tok.hasMoreTokens() && !breakLoop) {
            temp = tok.nextToken();
            if (temp.equalsIgnoreCase("SwissProt") || temp.equalsIgnoreCase("GENBANK") || temp.equalsIgnoreCase(
                    "TREMBL") || temp.equalsIgnoreCase("IFO") || temp.equalsIgnoreCase("EMBL") || temp.equalsIgnoreCase(
                    "SRI") || temp.equalsIgnoreCase("NCBI")) {
                breakLoop = true;
                count--;
            } else if (temp.contains("(")) {
                breakLoop = true;
            } else if (temp.contains("sp.")) {
                v.add(temp);
                count++;
                breakLoop = true;
            } else if (temp.contains("\\d")) {
                breakLoop = true;
            } else {
                v.add(temp);
                count++;
            }
        }
        StringBuilder org = new StringBuilder();
        for (int i = 0; i < count; i++) {
            org.append(" ").append(v.elementAt(i));
        }
        return org.toString().trim();
    }

    private void getCofactors() {
        CofactorRequestPayload payload = new CofactorRequestPayload();
        payload.ecs = enzymesInPathway();
        if (organism_specific) {
            payload.organism = enzymeOrganism;
        }
        Response<CofactorResponsePayload> response = VanesaApi.postSync("/db_brenda/enzyme/cofactors", payload,
                                                                        new TypeReference<>() {
                                                                        });
        if (response.payload == null || response.payload.results == null || response.payload.results.length == 0) {
            // TODO: error
            return;
        }
        for (DBBrendaCofactor cofactor : response.payload.results) {
            if (enzymes.containsKey(cofactor.cofactor)) {
                BiologicalNodeAbstract bna = enzymes.get(cofactor.ec);
                BiologicalNodeAbstract bna2 = enzymes.get(cofactor.cofactor);
                buildEdge(bna2, bna, true, "1");
            } else {
                Factor f = new Factor(cofactor.cofactor, cofactor.cofactor);
                f.setColor(Color.cyan);
                enzymes.put(cofactor.cofactor, f);
                Enzyme e = (Enzyme) enzymes.get(cofactor.ec);
                buildEdge(f, e, true, "1");
            }
        }
    }

    private void getInhibitors() {
        InhibitorRequestPayload payload = new InhibitorRequestPayload();
        payload.ecs = enzymesInPathway();
        Response<InhibitorResponsePayload> response = VanesaApi.postSync("/db_brenda/enzyme/inhibitors", payload,
                                                                         new TypeReference<>() {
                                                                         });
        if (response.payload == null || response.payload.results == null || response.payload.results.length == 0) {
            // TODO: error
            return;
        }
        for (DBBrendaInhibitor inhibitor : response.payload.results) {
            if (enzymes.containsKey(inhibitor.inhibitor)) {
                BiologicalNodeAbstract bna = enzymes.get(inhibitor.ec);
                BiologicalNodeAbstract bna2 = enzymes.get(inhibitor.inhibitor);
                bna2.setColor(Color.pink);
                buildEdge(bna2, bna, true, "1");
            } else {
                Inhibitor f = new Inhibitor(inhibitor.inhibitor, inhibitor.inhibitor);
                enzymes.put(inhibitor.inhibitor, f);
                Enzyme e = ((Enzyme) enzymes.get(inhibitor.ec));
                buildEdge(f, e, true, "1");
            }
        }
    }

    private String[] enzymesInPathway() {
        List<String> result = new ArrayList<>();
        for (String enzyme : enzymes.keySet()) {
            if (enzymes.get(enzyme) instanceof Enzyme) {
                Enzyme e = (Enzyme) enzymes.get(enzyme);
                result.add(e.getLabel());
            }
        }
        return result.toArray(new String[0]);
    }

    public void setSearchDepth(int searchDepth) {
        this.searchDepth = searchDepth;
    }

    public void setOrganism_specific(boolean organism_specific) {
        this.organism_specific = organism_specific;
    }

    public MyGraph getGraph() {
        return myGraph;
    }

    public void setDisregarded(boolean disregarded) {
        this.disregarded = disregarded;
    }

    public void setCofactors(boolean cofactors) {
        this.cofactors = cofactors;
    }

    public void setInhibitors(boolean inhibitors) {
        this.inhibitors = inhibitors;
    }

    @Override
    protected Object doInBackground() {
        getPathway();
        box.getDisregardedValues();
        title = enzyme.ec;
        return null;
    }

    @Override
    public void done() {
        int answer = JOptionPane.YES_OPTION;
        if (mergePW != null)
            answer = JOptionPane.showOptionDialog(MainWindow.getInstance().getFrame(),
                                                  "A new tab will be created with the pathway you selected. Shall this tab be a merge between the current pathway and the selected or contain only the selected pathway?",
                                                  "", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                                                  new String[]{"Selected Pathway Only", "Merge Pathways"},
                                                  JOptionPane.CANCEL_OPTION);
        if (answer == JOptionPane.YES_OPTION || answer == JOptionPane.NO_OPTION) {
            if (answer == JOptionPane.NO_OPTION) {
                pw = new Pathway(title);
            } else if (title != null) {
                pw = new CreatePathway("EC: " + title).getPathway();
            } else {
                pw = new CreatePathway("BRENDA").getPathway();
            }
            pw.setOrganism(enzymeOrganism);
            pw.setLink(pathwayLink);
            enzymes.clear();
            // TODO: enzymeOrganism = adoptOrganism(enzyme.organism);
            processBrendaElement(enzyme.ec, tree.getRoot());
            if (cofactors) {
                MainWindow.getInstance().showProgressBar("Getting Cofactors");
                getCofactors();
            }
            if (inhibitors) {
                MainWindow.getInstance().showProgressBar("Getting Inhibitors");
                getInhibitors();
            }
            MainWindow.getInstance().showProgressBar("Drawing network");
            myGraph = pw.getGraph();
            drawNodes();
            drawEdges();
            startVisualizationModel();
            myGraph.changeToGEMLayout();
            myGraph.normalCentering();
            pw.saveVertexLocations();
            if (autoCoarseDepth) {
                autoCoarseDepth();
            }
            if (autoCoarseEnzymeNomenclature) {
                autoCoarseEnzymeNomenclature();
            }
            if (answer == JOptionPane.NO_OPTION) {
                new MergeGraphs(pw, mergePW, true);
            }
            MainWindow.getInstance().closeProgressBar();
        }
        MainWindow.getInstance().updateAllGuiElements();
    }

    public void setAutoCoarseDepth(boolean ac) {
        autoCoarseDepth = ac;
    }

    public void setAutoCoarseEnzymeNomenclature(boolean autoCoarseEnzymeNomenclature) {
        this.autoCoarseEnzymeNomenclature = autoCoarseEnzymeNomenclature;
    }

    private String cleanString(String s) {
        return s.toLowerCase();
    }
}
