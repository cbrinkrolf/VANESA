package biologicalElements;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Vector;

import javax.xml.namespace.QName;
import javax.xml.stream.*;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import configurations.Wrapper;
import database.brenda.BRENDAQueries;
import org.apache.commons.lang3.StringUtils;
import pojos.DBColumn;

public class ElementNames {
    private final HashSet<String> enzymes = new HashSet<>();
    private final Vector<String> enzymeVector = new Vector<>();

    public void writeFile() throws FileNotFoundException, XMLStreamException {
        OutputStream out = new FileOutputStream("EnzymeNames.xml");
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        XMLStreamWriter writer = factory.createXMLStreamWriter(out);
        writer.writeStartDocument();
        writer.writeStartElement("Molecules");
        ArrayList<DBColumn> results = new Wrapper().requestDbContent(1, BRENDAQueries.getAllBRENDAenzymeNames);
        for (DBColumn column : results) {
            String[] details = column.getColumn();
            writer.writeStartElement("moleculeProperties");
            writer.writeAttribute("name", details[0]);
            writer.writeAttribute("ec", details[1]);
            writer.writeEndElement();
        }
        writer.writeEndElement();
        writer.writeEndDocument();
        writer.flush();
        writer.close();
    }

    public void fillEnzymeSet() {
        try (InputStream in = this.getClass().getClassLoader().getResourceAsStream("EnzymeNames.xml")) {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            factory.setProperty(XMLInputFactory.IS_VALIDATING, Boolean.TRUE);
            factory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);
            XMLEventReader reader = factory.createXMLEventReader(in);
            while (reader.hasNext()) {
                XMLEvent event = reader.nextEvent();
                if (event.getEventType() == XMLStreamConstants.START_ELEMENT) {
                    StartElement se = (StartElement) event;
                    QName name = new QName("name");
                    QName ec = new QName("ec");
                    if (se.getName().getLocalPart().equals("moleculeProperties")) {
                        String temp_name = se.getAttributeByName(name).getValue();
                        String temp_ec = se.getAttributeByName(ec).getValue();
                        if (StringUtils.isNotEmpty(temp_name) && !enzymes.contains(temp_name)) {
                            enzymes.add(temp_name);
                            enzymeVector.add(temp_name);
                        }
                        if (StringUtils.isNotEmpty(temp_ec) && !enzymes.contains(temp_ec)) {
                            enzymes.add(temp_ec);
                            enzymeVector.add(temp_ec);
                        }
                    }
                }
            }
        } catch (XMLStreamException | IOException e) {
            e.printStackTrace();
        }
    }

    public Vector<String> getEnzymes() {
        return enzymeVector;
    }
}
