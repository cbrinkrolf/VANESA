package xmlOutput.sbml;

/**
 * \file    validateSBML.java
 * \brief   Validates an SBML document using the SBML.org Online Validator
 * \author  Ben Bornstein <sbml-team@caltech.edu>
 * \author  Akiya Jouraku <sbml-team@caltech.edu>
 *
 * $Id$
 * $Source$
 *
 * Copyright (C) 2009-2011 jointly by the following organizations: 
 *     1. California Institute of Technology, Pasadena, CA, USA
 *     2. EMBL European Bioinformatics Institute (EBML-EBI), Hinxton, UK
 *  
 * Copyright (C) 2006-2008 by the California Institute of Technology,
 *     Pasadena, CA, USA 
 *  
 * Copyright (C) 2002-2005 jointly by the following organizations: 
 *     1. California Institute of Technology, Pasadena, CA, USA
 *     2. Japan Science and Technology Agency, Japan
 * 
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation; either version 2.1 of the License, or
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 * documentation provided hereunder is on an "as is" basis, and the
 * copyright holders have no obligations to provide maintenance, support,
 * updates, enhancements or modifications.  In no event shall the copyright
 * holders be liable to any party for direct, indirect, special, incidental
 * or consequential damages, including lost profits, arising out of the use
 * of this software and its documentation, even if the copyright holders
 * have been advised of the possibility of such damage.  See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;

/**
 * SBMLValidator is simply a container for the static method
 * validateSBML(filename, parameters).
 */
public class SBMLValidator {
	public String validatorURL = "http://sbml.org/validator/";

	private InputStream result;

	public int validateSBML(String xml, String filename, Map parameters)
			throws IOException {
		return this.validateSBML(new ByteArrayInputStream(xml.getBytes()),
				filename, parameters);
	}

	/**
	 * Validates the given SBML filename (or http:// URL) by calling the
	 * SBML.org online validator. The results are returned as an InputStream
	 * whose format may be controlled by setting parameters.put("output", ...)
	 * to one of: "xml", "xhtml", "json", "text" (default: xml).
	 * 
	 * @return an InputStream containing the validation results.
	 */
	public int validateSBML(InputStream is, String filename, Map parameters)
			throws IOException {
		if (parameters.get("output") == null) {
			parameters.put("output", "xml");
		}

		MultipartPost post = new MultipartPost(validatorURL);
		this.result = null;

		// if (filename.startsWith("http://")) {
		// post.writeParameter("url", filename);
		// } else {
		post.writeParameter("file", is);
		// }

		try {
			Iterator iter = parameters.keySet().iterator();

			while (iter.hasNext()) {
				String name = (String) iter.next();
				String value = (String) parameters.get(name);

				post.writeParameter(name, value);
			}
		} catch (NoSuchElementException e) {
			e.printStackTrace();
		}

		this.result = post.done();
		return this.getNumberOfErrors();
	}

	private int getNumberOfErrors() throws IOException {
		InputStreamReader is = new InputStreamReader(this.result);

		// create a buffered reader
		BufferedReader bufferedReader = null;
		bufferedReader = new BufferedReader(is);

		// create a xml reader
		XMLStreamReader reader = null;
		try {
			reader = XMLInputFactory.newInstance().createXMLStreamReader(
					bufferedReader);
		} catch (XMLStreamException e) {
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			e.printStackTrace();
		}

		// create a OMElement from SBML file
		StAXOMBuilder axiomBuilder = new StAXOMBuilder(reader);
		OMElement xmlResult = axiomBuilder.getDocumentElement();
		String content = xmlResult.toString();
		System.out.println(content);
		// clean up
		try {
			bufferedReader.close();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XMLStreamException exception) {
			exception.printStackTrace();
		}

		// validation result
		// System.out.println(sb.toString());
		if (xmlResult.toString().indexOf("severity='Error'") > -1) {
			String[] errors = xmlResult.toString().split("severity='Error'");
			// System.out.println(errors.length);
			return errors.length - 1;
		} else {
			return 0;
		}

	}
}
