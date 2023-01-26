package io.graphML;

import biologicalElements.Elementdeclerations;
import biologicalElements.IDAlreadyExistException;
import biologicalElements.Pathway;
import biologicalObjects.edges.*;
import biologicalObjects.nodes.*;
import biologicalObjects.nodes.petriNet.*;
import graph.CreatePathway;
import gui.MainWindow;
import io.BaseReader;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GraphMLReader extends BaseReader<Pathway> {
    private final Logger logger = Logger.getRootLogger();
    private final Elementdeclerations declarations = new Elementdeclerations();
    private boolean hasSeenPositions = false;

    public GraphMLReader(File file) {
        super(file);
    }

    public GraphMLReader(InputStream inputStream) {
        super(inputStream);
    }

    @Override
    public Pathway internalRead(InputStream inputStream) {
        hasSeenPositions = false;
        final XMLEventReader reader;
        try {
            reader = XMLInputFactory.newInstance().createXMLEventReader(inputStream);
        } catch (XMLStreamException e) {
            setHasErrors();
            return null;
        }
        Pathway pw = new CreatePathway().getPathway();
        final List<String> nodeDeclarations = declarations.getAllNodeDeclarations();
        final List<String> edgeDeclarations = declarations.getAllEdgeDeclarations();
        final Map<String, PropertyKey> nodePropertyTypes = new HashMap<>();
        final Map<String, PropertyKey> edgePropertyTypes = new HashMap<>();
        final Map<Integer, BiologicalNodeAbstract> vertexIdMap = new HashMap<>();
        while (reader.hasNext()) {
            final XMLEvent nextEvent = tryNextEvent(reader);
            if (nextEvent != null && nextEvent.isStartElement()) {
                final StartElement element = nextEvent.asStartElement();
                final String key = element.getName().getLocalPart();
                if ("key".equals(key)) {
                    final PropertyKey property = getPropertyKeyFromElement(element);
                    if ("node".equalsIgnoreCase(property.forType)) {
                        nodePropertyTypes.put(property.id, property);
                    } else {
                        edgePropertyTypes.put(property.id, property);
                    }
                } else if ("node".equals(key)) {
                    readNode(reader, element, nodeDeclarations, nodePropertyTypes, pw, vertexIdMap);
                } else if ("edge".equals(key)) {
                    readEdge(reader, element, edgeDeclarations, edgePropertyTypes, pw, vertexIdMap);
                }
            }
        }
        if (!hasSeenPositions) {
            pw.getGraph().changeToCircleLayout();
        }
        pw.getGraph().restartVisualizationModel();
        pw.getGraph().normalCentering();
        MainWindow.getInstance().updateProjectProperties();
        return pw;
    }

    private XMLEvent tryNextEvent(final XMLEventReader reader) {
        try {
            return reader.nextEvent();
        } catch (XMLStreamException e) {
            logger.warn("Failed to read XML event", e);
            return null;
        }
    }

    private PropertyKey getPropertyKeyFromElement(final StartElement element) {
        final String id = getElementAttribute(element, "id");
        final String forType = getElementAttribute(element, "for");
        final String attributeName = getElementAttribute(element, "attr.name");
        final String attributeList = getElementAttribute(element, "attr.list");
        final String attributeType = getElementAttribute(element, "attr.type");
        return new PropertyKey(id, forType, attributeName, attributeType, attributeList);
    }

    private String getElementAttribute(final StartElement element, final String name) {
        final Attribute attribute = element.getAttributeByName(QName.valueOf(name));
        return attribute != null ? attribute.getValue() : null;
    }

    private void readNode(final XMLEventReader reader, final StartElement element, final List<String> nodeDeclarations,
                          final Map<String, PropertyKey> propertyTypes, final Pathway pw,
                          final Map<Integer, BiologicalNodeAbstract> vertexIdMap) {
        String label = StringUtils.stripStart(getElementAttribute(element, "labels"), ":");
        label = transformLabel(label);
        if (label == null || !nodeDeclarations.contains(label)) {
            logger.warn("Ignored node with label '" + label + "'");
            return;
        }
        final String idText = getElementAttribute(element, "id");
        if (idText == null) {
            logger.warn("Ignored node without id");
            return;
        }
        final int id = Integer.parseInt(idText);
        final String nodeX = getElementAttribute(element, "x");
        final String nodeY = getElementAttribute(element, "y");
        Point p = new Point(nodeX != null ? Integer.parseInt(nodeX) : 0, nodeY != null ? Integer.parseInt(nodeY) : 0);
        if (p.x != 0 || p.y != 0) {
            hasSeenPositions = true;
        }
        final Map<String, Object> properties = collectNodeOrEdgeProperties(reader, propertyTypes, "node");
        BiologicalNodeAbstract bna = BiologicalNodeAbstractFactory.create(label, null);
        try {
            bna.setID(id, pw);
        } catch (IDAlreadyExistException e) {
            logger.warn("Ignored node with already existing id " + id);
            return;
        }
        setPropertyIfExists(properties, "_label", bna::setLabel);
        setPropertyIfExists(properties, "name", bna::setName);
        setPropertyIfExists(properties, "concentration", bna::setConcentration);
        setPropertyIfExists(properties, "concentrationMin", bna::setConcentrationMin);
        setPropertyIfExists(properties, "concentrationMax", bna::setConcentrationMax);
        setPropertyIfExists(properties, "concentrationStart", bna::setConcentrationStart);
        setPropertyIfExists(properties, "constant", bna::setConstant);
        setPropertyIfExists(properties, "discrete", bna::setDiscrete);
        setPropertyIfExists(properties, "description", bna::setDescription);
        switch (label) {
            case Elementdeclerations.mRNA:
            case Elementdeclerations.miRNA:
            case Elementdeclerations.lncRNA:
            case Elementdeclerations.sRNA:
                RNA rna = (RNA) bna;
                setPropertyIfExists(properties, "ntSequence", rna::setNtSequence);
                setPropertyIfExists(properties, "logFC", rna::setLogFC);
                break;
            case Elementdeclerations.dna:
                setPropertyIfExists(properties, "ntSequence", ((DNA) bna)::setNtSequence);
                break;
            case Elementdeclerations.gene:
                setPropertyIfExists(properties, "ntSequence", ((Gene) bna)::setNtSequence);
                break;
            case Elementdeclerations.pathwayMap:
                setPropertyIfExists(properties, "specification", ((PathwayMap) bna)::setSpecification);
                break;
            case Elementdeclerations.protein:
                setPropertyIfExists(properties, "aaSequence", ((Protein) bna)::setAaSequence);
                break;
            case Elementdeclerations.reaction:
                setPropertyIfExists(properties, "maximalSpeed", ((Reaction) bna)::setMaximalSpeed);
                setPropertyIfExists(properties, "knockedOut", ((Reaction) bna)::setKnockedOut);
                break;
            case Elementdeclerations.transition:
                setPropertyIfExists(properties, "knockedOut", ((Transition) bna)::setKnockedOut);
                setPropertyIfExists(properties, "firingCondition", ((Transition) bna)::setFiringCondition);
                break;
            case Elementdeclerations.stochasticTransition:
                setPropertyIfExists(properties, "knockedOut", ((Transition) bna)::setKnockedOut);
                setPropertyIfExists(properties, "firingCondition", ((Transition) bna)::setFiringCondition);
                setPropertyIfExists(properties, "distribution", ((StochasticTransition) bna)::setDistribution);
                setPropertyIfExists(properties, "h", ((StochasticTransition) bna)::setH);
                setPropertyIfExists(properties, "a", ((StochasticTransition) bna)::setA);
                setPropertyIfExists(properties, "b", ((StochasticTransition) bna)::setB);
                setPropertyIfExists(properties, "c", ((StochasticTransition) bna)::setC);
                setPropertyIfExists(properties, "mu", ((StochasticTransition) bna)::setMu);
                setPropertyIfExists(properties, "sigma", ((StochasticTransition) bna)::setSigma);
                setPropertyIfExists(properties, "events", ((StochasticTransition) bna)::setEvents);
                setPropertyIfExists(properties, "probabilities", ((StochasticTransition) bna)::setProbabilities);
                break;
            case Elementdeclerations.continuousTransition:
                setPropertyIfExists(properties, "knockedOut", ((Transition) bna)::setKnockedOut);
                setPropertyIfExists(properties, "firingCondition", ((Transition) bna)::setFiringCondition);
                setPropertyIfExists(properties, "maximalSpeed", ((ContinuousTransition) bna)::setMaximalSpeed);
                break;
            case Elementdeclerations.discreteTransition:
                setPropertyIfExists(properties, "knockedOut", ((Transition) bna)::setKnockedOut);
                setPropertyIfExists(properties, "firingCondition", ((Transition) bna)::setFiringCondition);
                setPropertyIfExists(properties, "delay", ((DiscreteTransition) bna)::setDelay);
                break;
            case Elementdeclerations.place:
            case Elementdeclerations.discretePlace:
            case Elementdeclerations.continuousPlace:
                setPropertyIfExists(properties, "token", ((Place) bna)::setToken);
                setPropertyIfExists(properties, "tokenMin", ((Place) bna)::setTokenMin);
                setPropertyIfExists(properties, "tokenMax", ((Place) bna)::setTokenMax);
                setPropertyIfExists(properties, "tokenStart", ((Place) bna)::setTokenStart);
                setPropertyIfExists(properties, "conflictStrategy", ((Place) bna)::setConflictStrategy);
                break;
        }
        pw.addVertex(bna, p);
        vertexIdMap.put(id, bna);
    }

    private String transformLabel(String label) {
        return StringUtils.replace(label, "_", " ");
    }

    private Map<String, Object> collectNodeOrEdgeProperties(final XMLEventReader reader,
                                                            final Map<String, PropertyKey> propertyTypes,
                                                            final String forType) {
        final var properties = new HashMap<String, Object>();
        while (reader.hasNext()) {
            final XMLEvent nextEvent = tryNextEvent(reader);
            if (nextEvent != null && nextEvent.isStartElement()) {
                final StartElement startChildElement = nextEvent.asStartElement();
                final String propertyKey = getElementAttribute(startChildElement, "key");
                if (!propertyTypes.containsKey(propertyKey)) {
                    final PropertyKey property = new PropertyKey(propertyKey, forType, propertyKey, "string", null);
                    propertyTypes.put(propertyKey, property);
                    if (logger.isInfoEnabled())
                        logger.warn("Property '" + propertyKey + "' wasn't defined, fallback to string property");
                }
                final PropertyKey property = propertyTypes.get(propertyKey);
                final String propertyName = property.attributeName;
                if (!propertyName.equals("labels") && !propertyName.equals("label"))
                    properties.put(propertyName, parsePropertyValue(property, reader));
            } else if (nextEvent != null && nextEvent.isEndElement()) {
                final String tagName = nextEvent.asEndElement().getName().getLocalPart();
                if (tagName.equalsIgnoreCase("node") || tagName.equalsIgnoreCase("edge"))
                    break;
            }
        }
        return properties;
    }

    private Object parsePropertyValue(final PropertyKey type, final XMLEventReader reader) {
        String value = tryGetElementText(reader);
        if (value == null)
            return null;
        if (type.attributeList != null) {
            return parsePropertyListValue(type, value);
        } else {
            switch (type.attributeType.toLowerCase(Locale.US)) {
                case "boolean":
                    return Boolean.valueOf(value);
                case "int":
                    return Integer.valueOf(value);
                case "long":
                    return Long.valueOf(value);
                case "float":
                    return Float.valueOf(value);
                case "double":
                    return Double.valueOf(value);
                default:
                    return value;
            }
        }
    }

    private Object parsePropertyListValue(final PropertyKey type, String value) {
        value = StringUtils.strip(value, "[] \t\n\r");
        switch (type.attributeList.toLowerCase(Locale.US)) {
            case "boolean":
                return convertStringToTypeList(value, Boolean::valueOf);
            case "int":
                return convertStringToTypeList(value, Integer::valueOf);
            case "long":
                return convertStringToTypeList(value, Long::valueOf);
            case "float":
                return convertStringToTypeList(value, Float::valueOf);
            case "double":
                return convertStringToTypeList(value, Double::valueOf);
            default:
                boolean insideString = false;
                int start = 0;
                int escapeCount = 0;
                List<String> parts = new ArrayList<>();
                for (int i = 0; i < value.length(); i++) {
                    char currentChar = value.charAt(i);
                    if (currentChar == '"') {
                        if (insideString && escapeCount % 2 == 0) {
                            parts.add(value.substring(start, i).replace("\\\"", "\""));
                            insideString = false;
                        } else if (!insideString) {
                            insideString = true;
                            start = i + 1;
                        }
                    }
                    escapeCount = currentChar == '\\' ? escapeCount + 1 : 0;
                }
                return parts;
        }
    }

    private <R> List<R> convertStringToTypeList(final String value, Function<String, R> mapper) {
        return Arrays.stream(StringUtils.split(value, ',')).map(String::strip).map(mapper).collect(Collectors.toList());
    }

    private String tryGetElementText(final XMLEventReader reader) {
        try {
            return reader.getElementText();
        } catch (XMLStreamException e) {
            logger.warn("Failed to read XML element text", e);
            return null;
        }
    }

    private <T> void setPropertyIfExists(final Map<String, Object> properties, final String key, Consumer<T> setter) {
        setPropertyIfExists(properties, key, setter, null);
    }

    private <T> void setPropertyIfExists(final Map<String, Object> properties, final String key, Consumer<T> setter,
                                         T fallback) {
        Object value = properties.get(key);
        if (value != null) {
            //noinspection unchecked
            setter.accept((T) value);
        } else if (fallback != null) {
            setter.accept(fallback);
        }
    }

    private void readEdge(final XMLEventReader reader, final StartElement element, List<String> edgeDeclarations,
                          final Map<String, PropertyKey> propertyTypes, final Pathway pw,
                          final Map<Integer, BiologicalNodeAbstract> vertexIdMap) {
        String label = getElementAttribute(element, "label");
        label = transformLabel(label);
        if (label == null || !edgeDeclarations.contains(label)) {
            logger.warn("Ignored edge with label '" + label + "'");
            return;
        }
        final String idText = getElementAttribute(element, "id");
        if (idText == null) {
            logger.warn("Ignored edge without id");
            return;
        }
        final int id = Integer.parseInt(idText);
        final String sourceNodeIdText = getElementAttribute(element, "source");
        final String targetNodeIdText = getElementAttribute(element, "target");
        if (sourceNodeIdText == null || targetNodeIdText == null) {
            logger.warn("Ignored edge without source or target node id");
            return;
        }
        BiologicalNodeAbstract source = vertexIdMap.get(Integer.parseInt(sourceNodeIdText));
        BiologicalNodeAbstract target = vertexIdMap.get(Integer.parseInt(targetNodeIdText));
        if (source == null || target == null) {
            logger.warn("Ignored edge without source or target node");
            return;
        }
        final Map<String, Object> properties = collectNodeOrEdgeProperties(reader, propertyTypes, "edge");
        BiologicalEdgeAbstract bea = BiologicalEdgeAbstractFactory.create(label, null);
        try {
            bea.setID(id, pw);
        } catch (IDAlreadyExistException e) {
            logger.warn("Ignored edge with already existing id " + id);
            return;
        }
        bea.setFrom(source);
        bea.setTo(target);
        setPropertyIfExists(properties, "directed", bea::setDirected, true);
        setPropertyIfExists(properties, "_label", bea::setLabel);
        setPropertyIfExists(properties, "name", bea::setName);
        setPropertyIfExists(properties, "function", bea::setFunction);
        setPropertyIfExists(properties, "description", bea::setDescription);
        // TODO: more
        switch (label) {
            case Elementdeclerations.inhibitionEdge:
                setPropertyIfExists(properties, "absoluteInhibition", ((Inhibition) bea)::setAbsoluteInhibition);
                break;
            case Elementdeclerations.reactionPairEdge:
                setPropertyIfExists(properties, "hasReactionPairEdge", ((ReactionPair) bea)::setHasReactionPairEdge);
                if (properties.containsKey("reactionPairEdgeId") || properties.containsKey("reactionPairEdgeName") ||
                    properties.containsKey("reactionPairEdgeType")) {
                    ReactionPairEdge rpe = new ReactionPairEdge();
                    setPropertyIfExists(properties, "reactionPairEdgeId", rpe::setReactionPairID);
                    setPropertyIfExists(properties, "reactionPairEdgeName", rpe::setName);
                    setPropertyIfExists(properties, "reactionPairEdgeType", rpe::setType);
                    ((ReactionPair) bea).setReactionPairEdge(rpe);
                }
                break;
        }
        pw.addEdge(bea);
    }
}
