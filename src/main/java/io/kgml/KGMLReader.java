package io.kgml;

import biologicalElements.ElementDeclarations;
import biologicalElements.Pathway;
import biologicalObjects.edges.*;
import biologicalObjects.nodes.*;
import graph.CreatePathway;
import gui.MainWindow;
import io.BaseReader;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class KGMLReader extends BaseReader<Pathway> {
    private final Logger logger = Logger.getRootLogger();

    public KGMLReader(final File file) {
        super(file);
    }

    public KGMLReader(final InputStream inputStream) {
        super(inputStream);
    }

    @Override
    protected Pathway internalRead(final InputStream inputStream) {
        final XMLEventReader reader;
        try {
            reader = XMLInputFactory.newInstance().createXMLEventReader(inputStream);
        } catch (XMLStreamException e) {
            setHasErrors();
            return null;
        }
        Pathway pw = null;
        String pathwayId = null;
        Entry lastEntry = null;
        Relation lastRelation = null;
        final Map<Integer, Entry> entries = new HashMap<>();
        final List<Relation> relations = new ArrayList<>();
        while (reader.hasNext()) {
            final XMLEvent nextEvent = tryNextEvent(reader);
            if (nextEvent != null && nextEvent.isStartElement()) {
                final StartElement element = nextEvent.asStartElement();
                final String key = element.getName().getLocalPart();
                if ("pathway".equals(key)) {
                    // ignored: number, image
                    pathwayId = getElementAttribute(element, "name");
                    final String org = getElementAttribute(element, "org");
                    final String title = getElementAttribute(element, "title");
                    final String link = getElementAttribute(element, "link");
                    pw = CreatePathway.create(title);
                    pw.setLink(link);
                    pw.setOrganism(org);
                } else if ("entry".equals(key)) {
                    lastEntry = new Entry();
                    final String id = getElementAttribute(element, "id");
                    if (!NumberUtils.isCreatable(id)) {
                        setHasErrors();
                        return null;
                    }
                    lastEntry.Id = Integer.parseInt(id);
                    lastEntry.Name = getElementAttribute(element, "name");
                    lastEntry.Type = getElementAttribute(element, "type");
                    lastEntry.Link = getElementAttribute(element, "link");
                    entries.put(lastEntry.Id, lastEntry);
                } else if ("graphics".equals(key)) {
                    if (lastEntry != null) {
                        // ignored: width, height
                        lastEntry.GraphicsName = getElementAttribute(element, "name");
                        lastEntry.GraphicsFgColor = getElementAttribute(element, "fgcolor");
                        lastEntry.GraphicsBgColor = getElementAttribute(element, "bgcolor");
                        lastEntry.GraphicsType = getElementAttribute(element, "type");
                        final String x = getElementAttribute(element, "x");
                        final String y = getElementAttribute(element, "y");
                        if (!NumberUtils.isCreatable(x) || !NumberUtils.isCreatable(y)) {
                            setHasErrors();
                            return null;
                        }
                        lastEntry.GraphicsX = Integer.parseInt(x);
                        lastEntry.GraphicsY = Integer.parseInt(y);
                    }
                } else if ("component".equals(key)) {
                    if (lastEntry != null) {
                        final String id = getElementAttribute(element, "id");
                        if (!NumberUtils.isCreatable(id)) {
                            setHasErrors();
                            return null;
                        }
                        lastEntry.Components.add(Integer.parseInt(id));
                    }
                } else if ("relation".equals(key)) {
                    lastRelation = new Relation();
                    final String entry1 = getElementAttribute(element, "entry1");
                    final String entry2 = getElementAttribute(element, "entry2");
                    if (!NumberUtils.isCreatable(entry1) || !NumberUtils.isCreatable(entry2)) {
                        setHasErrors();
                        return null;
                    }
                    lastRelation.Entry1 = Integer.parseInt(entry1);
                    lastRelation.Entry2 = Integer.parseInt(entry2);
                    lastRelation.Type = getElementAttribute(element, "type");
                    relations.add(lastRelation);
                } else if ("subtype".equals(key)) {
                    if (lastRelation != null) {
                        RelationSubtype subtype = new RelationSubtype();
                        subtype.Name = getElementAttribute(element, "name");
                        subtype.Value = getElementAttribute(element, "value");
                        lastRelation.Subtypes.add(subtype);
                    }
                }
            }
        }
        if (pw == null) {
            setHasErrors();
            return null;
        }
        populatePathway(pw, pathwayId, entries, relations);
        pw.updateMyGraph();
        pw.getGraphRenderer().zoomAndCenterGraph();
        MainWindow.getInstance().updateAllGuiElements();
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

    private String getElementAttribute(final StartElement element, final String name) {
        final Attribute attribute = element.getAttributeByName(QName.valueOf(name));
        return attribute != null ? attribute.getValue() : null;
    }

    private void populatePathway(final Pathway pw, final String pathwayId, final Map<Integer, Entry> entries,
                                 final List<Relation> relations) {
        final Map<Integer, BiologicalNodeAbstract> entryIdNodeMap = new HashMap<>();
        for (final Entry entry : entries.values()) {
            KEGGNode node = new KEGGNode();
            node.setKEGGentryID(String.valueOf(entry.Id));
            node.setKEGGentryLink(entry.Link);
            node.setKEGGentryType(entry.Type);
            node.setKEGGentryName(entry.GraphicsName);
            node.setKEGGPathway(pathwayId);
            if (!"undefined".equals(entry.Name)) {
                node.setNodeLabel(entry.Name);
            }
            node.setBackgroundColour(entry.GraphicsBgColor);
            node.setForegroundColour(entry.GraphicsFgColor);
            node.setShape(entry.GraphicsType);
            node.setXPos(entry.GraphicsX);
            node.setYPos(entry.GraphicsY);
            BiologicalNodeAbstract bna = null;
            switch (entry.Type) {
                case "gene":
                    String label = entry.GraphicsName.split(",")[0];
                    if (label != null) {
                        node.setNodeLabel(label);
                    }
                    bna = new DNA(node.getNodeLabel(), node.getKEGGentryName(), pw);
                    break;
                case "compound":
                    // TODO: node.setNodeLabel(set[9]);
                    bna = new Metabolite(node.getNodeLabel(), node.getKEGGentryName(), pw);
                    break;
                case "ortholog":
                    bna = new OrthologGroup(node.getNodeLabel(), node.getKEGGentryName(), pw);
                    break;
                case "map":
                    // TODO: node.setNodeLabel(set[10]);
                    bna = new PathwayMap(node.getNodeLabel(), node.getKEGGentryName(), pw);
                    break;
                case "enzyme":
                    bna = new Enzyme(node.getNodeLabel(), node.getKEGGentryName(), pw);
                    break;
                case "other":
                case "undefined":
                    bna = new Other(node.getNodeLabel(), node.getKEGGentryName(), pw);
                    break;
                case "group":
                    bna = new Complex("Complex", "", pw);
                    break;
            }
            if (bna != null) {
                bna.setKEGGnode(node);
                bna.setHasKEGGNode(true);
                pw.addVertex(bna, new Point2D.Double(entry.GraphicsX, entry.GraphicsY));
                entryIdNodeMap.put(entry.Id, bna);
            }
        }
        drawRelations(pw, entryIdNodeMap, relations);
    }

    private void drawRelations(final Pathway pw, final Map<Integer, BiologicalNodeAbstract> entryIdNodeMap,
                               final List<Relation> relations) {
        for (Relation relation : relations) {
            final BiologicalNodeAbstract bna1 = entryIdNodeMap.get(relation.Entry1);
            final BiologicalNodeAbstract bna2 = entryIdNodeMap.get(relation.Entry2);
            if (bna1 == null || bna2 == null) {
                continue;
            }
            for (RelationSubtype subtype : relation.Subtypes) {
                if (!pw.containsEdge(bna1, bna2) && !pw.containsEdge(bna2, bna1)) {
                    BiologicalEdgeAbstract bea;
                    switch (subtype.Name) {
                        case ElementDeclarations.dephosphorylationEdge:
                            bea = new Dephosphorylation("-p", "", bna1, bna2);
                            break;
                        case ElementDeclarations.phosphorylationEdge:
                            bea = new Phosphorylation("+p", "", bna1, bna2);
                            break;
                        case ElementDeclarations.methylationEdge:
                            bea = new Methylation("+m", "", bna1, bna2);
                            break;
                        case ElementDeclarations.ubiquitinationEdge:
                            bea = new Ubiquitination("+u", "", bna1, bna2);
                            break;
                        case ElementDeclarations.glycosylationEdge:
                            bea = new Glycosylation("+g", "", bna1, bna2);
                            break;
                        default:
                            bea = BiologicalEdgeAbstractFactory.create(subtype.Name, "", "", bna1, bna2);
                            break;
                    }
                    bea.setDirected(true);
                    pw.addEdge(bea);
                    pw.updateMyGraph();
                }
            }
        }
    }

    private static class Entry {
        public Integer Id;
        public String Name;
        public String Type;
        public String Link;
        public String GraphicsName;
        public String GraphicsFgColor;
        public String GraphicsBgColor;
        public String GraphicsType;
        public Integer GraphicsX;
        public Integer GraphicsY;
        public final Set<Integer> Components = new HashSet<>();

        @Override
        public String toString() {
            return "Entry{Id='" + Id + "', Name='" + Name + "', Type='" + Type + "', Link='" + Link +
                    "', GraphicsName='" + GraphicsName + "', FgColor='" + GraphicsFgColor + "', BgColor='" +
                    GraphicsBgColor + "', GraphicsType='" + GraphicsType + "', X='" + GraphicsX + "', Y='" +
                    GraphicsY + "', Components=" +
                    Components.stream().map(Object::toString).collect(Collectors.joining(",", "[", "]")) + '}';
        }
    }

    private static class Relation {
        public Integer Entry1;
        public Integer Entry2;
        public String Type;
        public final List<RelationSubtype> Subtypes = new ArrayList<>();

        @Override
        public String toString() {
            return "Relation{Entry1='" + Entry1 + "', Entry2='" + Entry2 + "', Type='" + Type + "', Subtypes=" +
                    Subtypes.stream().map(Object::toString).collect(Collectors.joining(",", "[", "]")) + '}';
        }
    }

    private static class RelationSubtype {
        public String Name;
        public String Value;

        @Override
        public String toString() {
            return "RelationSubtype{Name='" + Name + "', Value='" + Value + "'}";
        }
    }
}
