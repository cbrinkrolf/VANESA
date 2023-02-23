package io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import database.brenda.MoleculeBox;
import database.brenda.MoleculeAmountPair;

public class MoleculesInput {
    public MoleculesInput(InputStream is) throws IOException {
        try {
            getData(is);
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    private void getData(InputStream is) throws XMLStreamException {
        Vector<MoleculeAmountPair> v = new Vector<>();
        XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.IS_VALIDATING, Boolean.TRUE);
        factory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);
        XMLEventReader reader = factory.createXMLEventReader(is);
        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.getEventType() == XMLStreamConstants.START_ELEMENT) {
                StartElement se = (StartElement) event;
                QName name = new QName("name");
                QName amount = new QName("amount");
                QName disregarded = new QName("disregarded");
                if (se.getName().getLocalPart().equals("moleculeProperties")) {
                    String temp_name = se.getAttributeByName(name).getValue();
                    int temp_amount = Integer.parseInt(se.getAttributeByName(amount).getValue());
                    boolean temp_dis = Boolean.parseBoolean(se.getAttributeByName(disregarded).getValue());
                    MoleculeAmountPair p = new MoleculeAmountPair(temp_name, temp_amount, temp_dis);
                    v.add(p);
                }
            }
        }
        MoleculeBox box = MoleculeBox.getInstance();
        box.clear();
        box.fillTable(v);
    }
}
