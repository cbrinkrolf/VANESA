/*
 * Created on 23.04.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package xmlOutput.sbml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import database.brenda.MoleculeBox;
import database.brenda.MoleculeBoxSingelton;
import database.brenda.MoleculesPair;

/**
 * @author sebastian
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

public class Moleculesoutput {

	private File file = null;
	private XMLStreamWriter writer;
	private boolean defaultValues = true;
	MoleculeBox box;

	public Moleculesoutput(boolean defaultValues, File file) throws IOException {
		try {
			
			this.file=file;
			this.defaultValues = defaultValues;
			box = MoleculeBoxSingelton.getInstance();
			write();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private void getMolecules() throws XMLStreamException {
		Iterator it = box.getAllValues().iterator();
		int i = 0;
		while (it.hasNext()) {

			MoleculesPair p = (MoleculesPair) it.next();

			writer.writeStartElement("moleculeProperties");
			writer.writeAttribute("name", p.getName());
			writer.writeAttribute("amount", Integer.toString(p.getAmount()));

			if (defaultValues) {
				if (i < 50) {
					writer.writeAttribute("disregarded", String.valueOf(defaultValues));
				} else {
					writer.writeAttribute("disregarded", String.valueOf(!defaultValues));
				}
			} else {
				writer.writeAttribute("disregarded", String.valueOf(p
						.isDisregard()));
			}

			writer.writeEndElement();
			i++;
		}
	}

	public void write() throws XMLStreamException, IOException {

		OutputStream out = new FileOutputStream(file);

		XMLOutputFactory factory = XMLOutputFactory.newInstance();
		writer = factory.createXMLStreamWriter(out);

		writer.writeStartDocument();
		writer.writeStartElement("Molecules");

		getMolecules();

		writer.writeEndElement();
		writer.writeEndDocument();

		writer.flush();
		writer.close();
		out.close();
	}
}
