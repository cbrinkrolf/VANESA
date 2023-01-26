package io.graphML;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.edges.Inhibition;
import biologicalObjects.edges.ReactionPair;
import biologicalObjects.edges.ReactionPairEdge;
import biologicalObjects.nodes.*;
import biologicalObjects.nodes.petriNet.*;
import io.BaseWriter;
import io.IndentingXMLStreamWriter;
import org.apache.commons.lang3.StringUtils;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.awt.geom.Point2D;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class GraphMLWriter extends BaseWriter<Pathway> {
    private static final String FOR_TYPE_NODE = "node";
    private static final String FOR_TYPE_EDGE = "edge";

    public GraphMLWriter(File file) {
        super(file);
    }

    @Override
    protected void internalWrite(OutputStream outputStream, Pathway pw) {
        try {
            final XMLStreamWriter writer = createXMLStreamWriter(outputStream);
            writer.writeStartDocument();
            writeRootStart(writer);
            writeGraph(writer, pw);
            writer.writeEndElement();
            writer.writeEndDocument();
        } catch (XMLStreamException e) {
            setHasErrors();
        }
    }

    private static XMLStreamWriter createXMLStreamWriter(final OutputStream outputStream) throws XMLStreamException {
        final XMLOutputFactory factory = XMLOutputFactory.newInstance();
        final XMLStreamWriter writer = factory.createXMLStreamWriter(new BufferedOutputStream(outputStream),
                                                                     StandardCharsets.UTF_8.name());
        return new IndentingXMLStreamWriter(writer);
    }

    private void writeRootStart(final XMLStreamWriter writer) throws XMLStreamException {
        writer.writeStartElement("graphml");
        writer.writeAttribute("xmlns", "http://graphml.graphdrawing.org/xmlns");
        writer.writeAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        writer.writeAttribute("xsi:schemaLocation", "http://graphml.graphdrawing.org/xmlns " +
                                                    "http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd");
    }

    private void writeGraph(final XMLStreamWriter writer, final Pathway graph) throws XMLStreamException {
        final Map<String, Property> nodeProperties = writeNodeProperties(writer);
        final Map<String, Property> edgeProperties = writeEdgeProperties(writer);
        writer.writeStartElement("graph");
        writer.writeAttribute("id", "G");
        writer.writeAttribute("edgedefault", "directed");
        for (final Map.Entry<BiologicalNodeAbstract, Point2D> bna : graph.getVertices().entrySet())
            writeNode(writer, bna.getKey(), bna.getValue(), nodeProperties);
        for (final BiologicalEdgeAbstract bea : graph.getEdges())
            writeEdge(writer, bea, edgeProperties);
        writer.writeEndElement();
    }

    private Map<String, Property> writeNodeProperties(final XMLStreamWriter writer) throws XMLStreamException {
        final Map<String, Property> properties = new HashMap<>();
        properties.put("labels", new Property(FOR_TYPE_NODE, "labels", PropertyType.STRING));
        properties.put("_label", new Property(FOR_TYPE_NODE, "label", PropertyType.STRING));
        properties.put("name", new Property(FOR_TYPE_NODE, "name", PropertyType.STRING));
        properties.put("description", new Property(FOR_TYPE_NODE, "description", PropertyType.STRING));
        properties.put("concentration", new Property(FOR_TYPE_NODE, "concentration", PropertyType.DOUBLE));
        properties.put("concentrationMin", new Property(FOR_TYPE_NODE, "concentrationMin", PropertyType.DOUBLE));
        properties.put("concentrationMax", new Property(FOR_TYPE_NODE, "concentrationMax", PropertyType.DOUBLE));
        properties.put("concentrationStart", new Property(FOR_TYPE_NODE, "concentrationStart", PropertyType.DOUBLE));
        properties.put("constant", new Property(FOR_TYPE_NODE, "constant", PropertyType.BOOLEAN));
        properties.put("discrete", new Property(FOR_TYPE_NODE, "discrete", PropertyType.BOOLEAN));
        // RNA
        properties.put("ntSequence", new Property(FOR_TYPE_NODE, "ntSequence", PropertyType.STRING));
        properties.put("logFC", new Property(FOR_TYPE_NODE, "logFC", PropertyType.DOUBLE));
        // PathwayMap
        properties.put("specification", new Property(FOR_TYPE_NODE, "specification", PropertyType.BOOLEAN));
        // Protein
        properties.put("aaSequence", new Property(FOR_TYPE_NODE, "aaSequence", PropertyType.STRING));
        // Reaction & ContinuousTransition
        properties.put("maximalSpeed", new Property(FOR_TYPE_NODE, "maximalSpeed", PropertyType.STRING));
        // DiscreteTransition
        properties.put("delay", new Property(FOR_TYPE_NODE, "delay", PropertyType.DOUBLE));
        // Place
        properties.put("token", new Property(FOR_TYPE_NODE, "token", PropertyType.DOUBLE));
        properties.put("tokenMin", new Property(FOR_TYPE_NODE, "tokenMin", PropertyType.DOUBLE));
        properties.put("tokenMax", new Property(FOR_TYPE_NODE, "tokenMax", PropertyType.DOUBLE));
        properties.put("tokenStart", new Property(FOR_TYPE_NODE, "tokenStart", PropertyType.DOUBLE));
        properties.put("conflictStrategy", new Property(FOR_TYPE_NODE, "conflictStrategy", PropertyType.INT));
        // Transition
        properties.put("firingCondition", new Property(FOR_TYPE_NODE, "firingCondition", PropertyType.STRING));
        // Reaction & Transition
        properties.put("knockedOut", new Property(FOR_TYPE_NODE, "knockedOut", PropertyType.BOOLEAN));
        // StochasticTransition
        properties.put("distribution", new Property(FOR_TYPE_NODE, "distribution", PropertyType.STRING));
        properties.put("h", new Property(FOR_TYPE_NODE, "h", PropertyType.DOUBLE));
        properties.put("a", new Property(FOR_TYPE_NODE, "a", PropertyType.DOUBLE));
        properties.put("b", new Property(FOR_TYPE_NODE, "b", PropertyType.DOUBLE));
        properties.put("c", new Property(FOR_TYPE_NODE, "c", PropertyType.DOUBLE));
        properties.put("mu", new Property(FOR_TYPE_NODE, "mu", PropertyType.DOUBLE));
        properties.put("sigma", new Property(FOR_TYPE_NODE, "sigma", PropertyType.DOUBLE));
        properties.put("events", new Property(FOR_TYPE_NODE, "events", PropertyType.INT_ARRAY));
        properties.put("probabilities", new Property(FOR_TYPE_NODE, "probabilities", PropertyType.DOUBLE_ARRAY));
        for (final Property p : properties.values()) {
            writeProperty(writer, p);
        }
        return properties;
    }

    private static void writeProperty(XMLStreamWriter writer, Property p) throws XMLStreamException {
        writer.writeStartElement("key");
        writer.writeAttribute("id", String.valueOf(p.id));
        writer.writeAttribute("for", p.forType);
        writer.writeAttribute("attr.name", p.name);
        if (p.list != null)
            writer.writeAttribute("attr.list", p.list);
        writer.writeAttribute("attr.type", p.type);
        writer.writeEndElement();
    }

    private Map<String, Property> writeEdgeProperties(final XMLStreamWriter writer) throws XMLStreamException {
        final Map<String, Property> properties = new HashMap<>();
        properties.put("label", new Property(FOR_TYPE_EDGE, "label", PropertyType.STRING));
        properties.put("_label", new Property(FOR_TYPE_EDGE, "_label", PropertyType.STRING));
        properties.put("description", new Property(FOR_TYPE_EDGE, "description", PropertyType.STRING));
        properties.put("function", new Property(FOR_TYPE_EDGE, "function", PropertyType.STRING));
        properties.put("directed", new Property(FOR_TYPE_EDGE, "directed", PropertyType.BOOLEAN));
        // Inhibition
        properties.put("absoluteInhibition", new Property(FOR_TYPE_EDGE, "absoluteInhibition", PropertyType.BOOLEAN));
        // ReactionPair
        properties.put("hasReactionPairEdge", new Property(FOR_TYPE_EDGE, "hasReactionPairEdge", PropertyType.BOOLEAN));
        properties.put("reactionPairEdgeId", new Property(FOR_TYPE_EDGE, "reactionPairEdgeId", PropertyType.STRING));
        properties.put("reactionPairEdgeName",
                       new Property(FOR_TYPE_EDGE, "reactionPairEdgeName", PropertyType.STRING));
        properties.put("reactionPairEdgeType",
                       new Property(FOR_TYPE_EDGE, "reactionPairEdgeType", PropertyType.STRING));
        for (final Property p : properties.values()) {
            writeProperty(writer, p);
        }
        return properties;
    }

    private void writeNode(final XMLStreamWriter writer, final BiologicalNodeAbstract bna, final Point2D p,
                           final Map<String, Property> properties) throws XMLStreamException {
        final String label = ':' + transformLabel(bna.getBiologicalElement());
        writer.writeStartElement("node");
        writer.writeAttribute("id", String.valueOf(bna.getID()));
        writer.writeAttribute("labels", label);
        writer.writeAttribute("x", String.valueOf((int) p.getX()));
        writer.writeAttribute("y", String.valueOf((int) p.getY()));
        writePropertyIfNotNull(writer, properties, "labels", label);
        writePropertyIfNotNull(writer, properties, "_label", bna.getLabel());
        writePropertyIfNotNull(writer, properties, "name", bna.getName());
        writePropertyIfNotNull(writer, properties, "concentration", bna.getConcentration());
        writePropertyIfNotNull(writer, properties, "concentrationMin", bna.getConcentrationMin());
        writePropertyIfNotNull(writer, properties, "concentrationMax", bna.getConcentrationMax());
        writePropertyIfNotNull(writer, properties, "concentrationStart", bna.getConcentrationStart());
        writePropertyIfNotNull(writer, properties, "constant", bna.isConstant());
        writePropertyIfNotNull(writer, properties, "discrete", bna.isDiscrete());
        writePropertyIfNotNull(writer, properties, "description", bna.getDescription());
        /* TODO: more
        KEGGNode KEGGnode;
        double nodesize = 1;
        double defaultNodesize = 1;
        BiologicalNodeAbstract parentNode;
        String organism = "";
        DefaultMutableTreeNode treeNode;
        BiologicalNodeAbstract logicalReference = null;
        Set<BiologicalNodeAbstract> refs = new HashSet<>();
        boolean isVisible = true;
        SortedSet<Integer> set;
        String comments = "";
        Color color = Color.LIGHT_GRAY;
        Color defaultColor = Color.LIGHT_GRAY;
        Shape shape = new VertexShapes().getEllipse();
        Shape defaultShape = new VertexShapes().getEllipse();
        boolean hasKEGGNode = false;
        boolean hasBrendaNode = false;
        HashSet<String> labelSet = new HashSet<>();
        ArrayList<Parameter> parameters = new ArrayList<>();
        Set<BiologicalEdgeAbstract> connectingEdges = new HashSet<>();
        NodeStateChanged nodeStateChanged = NodeStateChanged.UNCHANGED;
        final HashSet<NodeAttribute> nodeAttributes = new HashSet<>();
        boolean markedAsEnvironment = false;
        boolean markedAsCoarseNode = false;
        Point2D parentNodeDistance = new Point2D.Double(0, 0);
        boolean deleted = false;
        boolean inGroup = false;
        ArrayList<Group> groups = new ArrayList<>();
        Color plotColor = null;
        */
        switch (bna.getBiologicalElement()) {
            case Elementdeclerations.mRNA:
            case Elementdeclerations.miRNA:
            case Elementdeclerations.lncRNA:
            case Elementdeclerations.sRNA:
                writePropertyIfNotNull(writer, properties, "ntSequence", ((RNA) bna).getNtSequence());
                writePropertyIfNotNull(writer, properties, "logFC", ((RNA) bna).getLogFC());
                break;
            case Elementdeclerations.dna:
                writePropertyIfNotNull(writer, properties, "ntSequence", ((DNA) bna).getNtSequence());
                break;
            case Elementdeclerations.gene:
                writePropertyIfNotNull(writer, properties, "ntSequence", ((Gene) bna).getNtSequence());
                break;
            case Elementdeclerations.pathwayMap:
                writePropertyIfNotNull(writer, properties, "specification", ((PathwayMap) bna).isSpecification());
                break;
            case Elementdeclerations.protein:
                writePropertyIfNotNull(writer, properties, "aaSequence", ((Protein) bna).getAaSequence());
                break;
            case Elementdeclerations.reaction:
                writePropertyIfNotNull(writer, properties, "maximalSpeed", ((Reaction) bna).getMaximalSpeed());
                writePropertyIfNotNull(writer, properties, "knockedOut", ((Reaction) bna).isKnockedOut());
                break;
            case Elementdeclerations.transition:
                writePropertyIfNotNull(writer, properties, "knockedOut", ((Transition) bna).isKnockedOut());
                writePropertyIfNotNull(writer, properties, "firingCondition", ((Transition) bna).getFiringCondition());
                break;
            case Elementdeclerations.stochasticTransition:
                writePropertyIfNotNull(writer, properties, "knockedOut", ((Transition) bna).isKnockedOut());
                writePropertyIfNotNull(writer, properties, "firingCondition", ((Transition) bna).getFiringCondition());
                writePropertyIfNotNull(writer, properties, "distribution",
                                       ((StochasticTransition) bna).getDistribution());
                writePropertyIfNotNull(writer, properties, "h", ((StochasticTransition) bna).getH());
                writePropertyIfNotNull(writer, properties, "a", ((StochasticTransition) bna).getA());
                writePropertyIfNotNull(writer, properties, "b", ((StochasticTransition) bna).getB());
                writePropertyIfNotNull(writer, properties, "c", ((StochasticTransition) bna).getC());
                writePropertyIfNotNull(writer, properties, "mu", ((StochasticTransition) bna).getMu());
                writePropertyIfNotNull(writer, properties, "sigma", ((StochasticTransition) bna).getSigma());
                writePropertyIfNotNull(writer, properties, "events", ((StochasticTransition) bna).getEvents());
                writePropertyIfNotNull(writer, properties, "probabilities",
                                       ((StochasticTransition) bna).getProbabilities());
                break;
            case Elementdeclerations.continuousTransition:
                writePropertyIfNotNull(writer, properties, "knockedOut", ((Transition) bna).isKnockedOut());
                writePropertyIfNotNull(writer, properties, "firingCondition", ((Transition) bna).getFiringCondition());
                writePropertyIfNotNull(writer, properties, "maximalSpeed",
                                       ((ContinuousTransition) bna).getMaximalSpeed());
                break;
            case Elementdeclerations.discreteTransition:
                writePropertyIfNotNull(writer, properties, "knockedOut", ((Transition) bna).isKnockedOut());
                writePropertyIfNotNull(writer, properties, "firingCondition", ((Transition) bna).getFiringCondition());
                writePropertyIfNotNull(writer, properties, "delay", ((DiscreteTransition) bna).getDelay());
                break;
            case Elementdeclerations.place:
            case Elementdeclerations.continuousPlace:
            case Elementdeclerations.discretePlace:
                writePropertyIfNotNull(writer, properties, "token", ((Place) bna).getToken());
                writePropertyIfNotNull(writer, properties, "tokenMin", ((Place) bna).getTokenMin());
                writePropertyIfNotNull(writer, properties, "tokenMax", ((Place) bna).getTokenMax());
                writePropertyIfNotNull(writer, properties, "tokenStart", ((Place) bna).getTokenStart());
                writePropertyIfNotNull(writer, properties, "conflictStrategy", ((Place) bna).getConflictStrategy());
                break;
        }
        writer.writeEndElement();
    }

    private String transformLabel(String label) {
        return StringUtils.replace(label, " ", "_");
    }

    private void writePropertyIfNotNull(final XMLStreamWriter writer, final Map<String, Property> properties,
                                        final String key, final Object value) throws XMLStreamException {
        if (value != null) {
            writer.writeStartElement("data");
            if (properties.containsKey(key))
                writer.writeAttribute("key", String.valueOf(properties.get(key).id));
            else
                writer.writeAttribute("key", key);
            writer.writeCharacters(GraphMLPropertyFormatter.format(value));
            writer.writeEndElement();
        }
    }

    private void writeEdge(final XMLStreamWriter writer, final BiologicalEdgeAbstract bea,
                           final Map<String, Property> properties) throws XMLStreamException {
        final String label = transformLabel(bea.getBiologicalElement());
        writer.writeStartElement("edge");
        writer.writeAttribute("id", String.valueOf(bea.getID()));
        writer.writeAttribute("source", String.valueOf(bea.getFrom().getID()));
        writer.writeAttribute("target", String.valueOf(bea.getTo().getID()));
        writer.writeAttribute("label", label);
        writePropertyIfNotNull(writer, properties, "label", label);
        writePropertyIfNotNull(writer, properties, "_label", bea.getLabel());
        writePropertyIfNotNull(writer, properties, "name", bea.getName());
        writePropertyIfNotNull(writer, properties, "directed", bea.isDirected());
        writePropertyIfNotNull(writer, properties, "function", bea.getFunction());
        writePropertyIfNotNull(writer, properties, "description", bea.getDescription());
        /* TODO: more
        boolean visible = true;
        SortedSet<Integer> set;
        String comments = "";
        Color defaultColor = Color.GRAY;
        Color color = Color.GRAY;
        Shape shape;
        boolean hasKEGGNode = false;
        boolean hasDAWISNode = false;
        boolean hasBrendaNode = false;
        HashSet<String> labelSet = new HashSet<>();
        ArrayList<Parameter> parameters = new ArrayList<>();
        */
        switch (bea.getBiologicalElement()) {
            case Elementdeclerations.inhibitionEdge:
                writePropertyIfNotNull(writer, properties, "absoluteInhibition",
                                       ((Inhibition) bea).isAbsoluteInhibition());
                break;
            case Elementdeclerations.reactionPairEdge:
                writePropertyIfNotNull(writer, properties, "hasReactionPairEdge",
                                       ((ReactionPair) bea).hasReactionPairEdge());
                final ReactionPairEdge rpe = ((ReactionPair) bea).getReactionPairEdge();
                if (rpe != null) {
                    writePropertyIfNotNull(writer, properties, "reactionPairEdgeId", rpe.getReactionPairID());
                    writePropertyIfNotNull(writer, properties, "reactionPairEdgeName", rpe.getName());
                    writePropertyIfNotNull(writer, properties, "reactionPairEdgeType", rpe.getType());
                }
                break;
        }
        writer.writeEndElement();
    }

    private static class Property {
        private static int idCounter = 1;

        final int id;
        final String forType;
        final String name;
        final String list;
        final String type;

        Property(String forType, String name, PropertyType type) {
            id = idCounter++;
            this.forType = forType;
            this.name = name;
            this.list = type.list;
            this.type = type.type;
        }
    }

    enum PropertyType {
        STRING(null, "string"),
        BOOLEAN(null, "boolean"),
        INT(null, "int"),
        LONG(null, "long"),
        FLOAT(null, "float"),
        DOUBLE(null, "double"),
        STRING_ARRAY("string", "string"),
        BOOLEAN_ARRAY("boolean", "string"),
        INT_ARRAY("int", "string"),
        LONG_ARRAY("long", "string"),
        FLOAT_ARRAY("float", "string"),
        DOUBLE_ARRAY("double", "string");

        private final String list;
        private final String type;

        PropertyType(String list, String type) {
            this.list = list;
            this.type = type;
        }
    }
}
