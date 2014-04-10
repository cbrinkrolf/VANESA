package biologicalElements;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Vector;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import pojos.DBColumn;
import configurations.Wrapper;
import database.brenda.BRENDAQueries;


public class ElementNames {

	//private HashSet molecules = new HashSet();
	private HashSet<String> enzymes = new HashSet<String>();
	//private HashSet genes = new HashSet();
	private Vector<String> enzymeVector = new Vector<String>();
	private XMLStreamWriter writer;
	
	
	public ElementNames() {
		
//		try {
//			writeFile();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (XMLStreamException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
	//	fillEnzymeSet();
	}
	
	public void writeFile() throws FileNotFoundException, XMLStreamException
	{
		OutputStream out=new FileOutputStream(new File("EnzymeNames.xml"));

		XMLOutputFactory factory=XMLOutputFactory.newInstance();
		writer=factory.createXMLStreamWriter(out);

		writer.writeStartDocument();
		writer.writeStartElement("Molecules");

		ArrayList<DBColumn> results=new Wrapper().requestDbContent(1, BRENDAQueries.getAllBRENDAenzymeNames);
		

		for (DBColumn column : results)
		{
			String[] details=column.getColumn();
			
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
	
//	public void writeFile() throws FileNotFoundException, XMLStreamException {
//
//		OutputStream out = new FileOutputStream(new File("EnzymeNames.xml"));
//
//		XMLOutputFactory factory = XMLOutputFactory.newInstance();
//		writer = factory.createXMLStreamWriter(out);
//
//		writer.writeStartDocument();
//		writer.writeStartElement("Molecules");
//
//		Vector v = new Wrapper().requestDbContent(1,
//				BRENDAQueries.getAllBRENDAenzymeNames);
//		Iterator it = v.iterator();
//		
//		while (it.hasNext()) {
//			String[] details = (String[]) it.next();
//			writer.writeStartElement("moleculeProperties");
//			writer.writeAttribute("name", details[0]);
//			writer.writeAttribute("ec", details[1]);
//			writer.writeEndElement();
//		}
//
//		writer.writeEndElement();
//		writer.writeEndDocument();
//
//		writer.flush();
//		writer.close();
//	
//	
//	}

	public void fillEnzymeSet() {
		
			File file;
			try {
//				file = new File(new BrendaDataPath().getPath("EnzymeNames.xml").toURI());
//				file=new File(this.getClass().getClassLoader().getResource("resource/EnzymeNames.xml").toURI());
//				System.out.println("DEBUG enzyme name file: "+file);
				
//				InputStream in = new FileInputStream(file);
				InputStream in = this.getClass().getClassLoader().getResource("resource/EnzymeNames.xml").openStream();
				XMLInputFactory factory = XMLInputFactory.newInstance();
				factory.setProperty(XMLInputFactory.IS_VALIDATING, Boolean.TRUE);
				factory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);
				
				javax.xml.stream.XMLEventReader reader = factory.createXMLEventReader(in);
				
				while(reader.hasNext()){
					
					XMLEvent event = reader.nextEvent();		
					switch(event.getEventType()){
					
					case XMLStreamConstants.START_ELEMENT:
										
						StartElement se = (StartElement)event;
						
						QName name = new QName("name");
						QName ec = new QName("ec");
						
						if(se.getName().getLocalPart().equals("moleculeProperties")){
							
							String temp_name = se.getAttributeByName(name).getValue();
							String temp_ec = se.getAttributeByName(ec).getValue();
								
							if (temp_name != null) {
								if (temp_name.length() > 0
										&& !enzymes.contains(temp_name)) {
									enzymes.add(temp_name);			
									enzymeVector.add(temp_name);
								}
							}
							
							if (temp_ec != null) {
								if (temp_ec.length() > 0
										&& !enzymes.contains(temp_ec)) {
									enzymes.add(temp_ec);
									enzymeVector.add(temp_ec);
								}
							}
						}
						
						break;
							
					default: break;	
						
					}
				}	
				
			}  catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XMLStreamException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
	}

	public Vector<String> getEnzymes() {
		return enzymeVector;
	}

	/*public Vector<String> getMoleculeValue(String pattern) {
		Vector<String> v = new Vector<String>();
		Iterator e = molecules.iterator();
		String name;
		while (e.hasNext()) {
			name = e.next().toString();
			if (name.contains(pattern)) {
				v.add(name);
			}
		}
		Collections.sort(v);
		return v;
	}*/

}
