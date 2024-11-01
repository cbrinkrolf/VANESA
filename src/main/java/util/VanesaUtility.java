package util;

import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.io.FileHandler;
import org.apache.commons.lang3.SystemUtils;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaderSAX2Factory;
import org.sbml.jsbml.SBMLReader;
import org.xml.sax.InputSource;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class VanesaUtility {
	public static double round(double value, int decimalPlaces) {
		double factor = Math.pow(10, decimalPlaces);
		return Math.round(value * factor) / factor;
	}

	public static Double getMean(List<Double> list) {
		if (list.size() == 0) {
			return 0.0;
		}
		return list.stream().reduce(Double::sum).get() / list.size();
	}

	public static Double getMedian(List<Double> list) {
		double median = 0;
		if (list.size() > 0) {
			if (list.size() % 2 == 0) {
				return (list.get(list.size() / 2) + list.get(list.size() / 2 - 1)) / 2;
			} else {
				return list.get((list.size() - 1) / 2);
			}
		}
		return median;
	}

	public static String getWorkingDirectoryPath() {
		String pathWorkingDirectory;
		if (SystemUtils.IS_OS_WINDOWS) {
			pathWorkingDirectory = System.getenv("APPDATA");
		} else {
			pathWorkingDirectory = System.getenv("HOME");
		}
		pathWorkingDirectory += File.separator + "vanesa";
		File f = new File(pathWorkingDirectory);
		if (f.exists() && f.isDirectory()) {
			return pathWorkingDirectory;
		} else if (!f.exists()) {
			f.mkdir();
			return pathWorkingDirectory;
		} else {
			int i = 0;
			while (f.exists() && !f.isDirectory()) {
				pathWorkingDirectory += i;
				f = new File(pathWorkingDirectory);
				i++;
			}
			f.mkdir();
		}
		return pathWorkingDirectory;
	}

	public static XMLConfiguration getFileBasedXMLConfiguration(final String filePath) throws ConfigurationException {
		File file = new File(filePath);
		if (!file.exists()) {
			XMLConfiguration configuration = new XMLConfiguration();
			FileHandler handler = new FileHandler(configuration);
			handler.save(file);
		}
		Parameters params = new Parameters();
		FileBasedConfigurationBuilder<XMLConfiguration> builder =
				new FileBasedConfigurationBuilder<>(XMLConfiguration.class).configure(
						params.fileBased().setFile(new File(filePath)));
		builder.setAutoSave(true);
		return builder.getConfiguration();
	}

	public static void openURLInBrowser(String url) {
		URI uri = null;
		try {
			uri = new URI(url);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		if (Desktop.isDesktopSupported()) {
			Desktop desktop = Desktop.getDesktop();
			try {
				desktop.browse(uri);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static Document loadXmlDocument(InputStream inputStream) {
		// changed empty constructor SAXBuilder builder = new SAXBuilder();
		// to following, because open JDK got an error with empty constructor
		// see: https://stackoverflow.com/questions/11409025/exceptionininitializererror-while-creating-ant-custom-task
		SAXBuilder builder = new SAXBuilder(new XMLReaderSAX2Factory(false, "org.apache.xerces.parsers.SAXParser"));
		InputSource in = new InputSource(inputStream);
		// SBMLReader.read(inputStream);
		try {
			return builder.build(in);
		} catch (JDOMException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String colorToHex(Color color) {
		return String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
	}

	public static Color colorFromHex(String hex) {
		return Color.decode(hex);
	}
}
