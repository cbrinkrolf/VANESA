/*
 * Created on 23.04.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package xmlInput.sbml;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Vector;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import database.brenda.MoleculeBox;
import database.brenda.MoleculesPair;




/**
 * @author sebastian
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

public class MoleculesInput {

//	private File file = null;
	
	
	public MoleculesInput(URL url) throws IOException
	{
		try
		{
			getData(url.openStream());
		}
		catch (XMLStreamException e)
		{
			e.printStackTrace();
		}
	}

	
	private void getData(InputStream stream) throws FileNotFoundException, XMLStreamException{
		
		Vector<MoleculesPair> v = new Vector<MoleculesPair>();
		
//		InputStream in = new FileInputStream(file);
		InputStream in = stream;
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
				QName amount = new QName("amount");
				QName disregarded = new QName("disregarded");
				
				if(se.getName().getLocalPart().equals("moleculeProperties")){
					
					String temp_name = se.getAttributeByName(name).getValue();
					int temp_amount = Integer.valueOf(se.getAttributeByName(amount).getValue()).intValue();
					boolean temp_dis = Boolean.valueOf(se.getAttributeByName(disregarded).getValue());
					
					MoleculesPair p = new MoleculesPair(temp_name,temp_amount,temp_dis);
					v.add(p);
				}
				
				break;
					
			default: break;	
				
			}
		}
		
		MoleculeBox box = MoleculeBox.getInstance();
		box.clear();
		box.fillTable(v);
	
	}
}
