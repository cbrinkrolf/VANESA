package util;

import java.awt.Color;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.xml.sax.InputSource;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import gui.PopUpDialog;

public class VanesaUtility {
	public static final Color POSITIVE_BUTTON_COLOR = new Color(186, 255, 201);
	public static final Color NEGATIVE_BUTTON_COLOR = new Color(255, 179, 186);

	public static double round(double value, int decimalPlaces) {
		double factor = Math.pow(10, decimalPlaces);
		return Math.round(value * factor) / factor;
	}

	public static Double getMean(List<Double> list) {
		if (list.isEmpty()) {
			return 0.0;
		}
		return list.stream().reduce(Double::sum).get() / list.size();
	}

	public static Double getMedian(List<Double> list) {
		double median = 0;
		if (!list.isEmpty()) {
			if (list.size() % 2 == 0) {
				return (list.get(list.size() / 2) + list.get(list.size() / 2 - 1)) / 2;
			} else {
				return list.get((list.size() - 1) / 2);
			}
		}
		return median;
	}

	public static Path getWorkingDirectoryPath() {
		final Path basePath = Paths.get(System.getenv(SystemUtils.IS_OS_WINDOWS ? "APPDATA" : "HOME"));
		Path workingDirectory = basePath.resolve("vanesa");
		File f = workingDirectory.toFile();
		if (f.exists() && f.isDirectory()) {
			return workingDirectory;
		}
		if (!f.exists()) {
			f.mkdir();
			return workingDirectory;
		}
		int i = 0;
		while (f.exists() && !f.isDirectory()) {
			workingDirectory = basePath.resolve("vanesa" + i);
			f = workingDirectory.toFile();
			i++;
		}
		f.mkdir();
		return workingDirectory;
	}

	public static XMLConfiguration getFileBasedXMLConfiguration(final Path filePath) throws ConfigurationException {
		File file = filePath.toFile();
		if (!file.exists()) {
			XMLConfiguration configuration = new XMLConfiguration();
			FileHandler handler = new FileHandler(configuration);
			handler.save(file);
		}
		Parameters params = new Parameters();
		FileBasedConfigurationBuilder<XMLConfiguration> builder = new FileBasedConfigurationBuilder<>(
				XMLConfiguration.class).configure(params.fileBased().setFile(file));
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
				PopUpDialog.getInstance().show("URL Error", e.getMessage());
			}
		}
	}

	public static Document loadXmlDocument(InputStream inputStream) {
		// changed empty constructor SAXBuilder builder = new SAXBuilder();
		// to following, because open JDK got an error with empty constructor
		// see:
		// https://stackoverflow.com/questions/11409025/exceptionininitializererror-while-creating-ant-custom-task
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

	public static double[][] createMatrix(int m, int n) {
		double[][] array = new double[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				array[i][j] = 0;
			}
		}
		return array;
	}

	/**
	 * Checks to see if a specific port is available.
	 *
	 * @param port the port to check for availability
	 */
	public static boolean isPortAvailable(int port) {
		// if (port < MIN_PORT_NUMBER || port > MAX_PORT_NUMBER) {
		// throw new IllegalArgumentException("Invalid start port: " + port);
		// }

		ServerSocket ss = null;
		DatagramSocket ds = null;
		try {
			ss = new ServerSocket(port);
			ss.setReuseAddress(true);
			ds = new DatagramSocket(port);
			ds.setReuseAddress(true);
			return true;
		} catch (IOException e) {
		} finally {
			if (ds != null) {
				ds.close();
			}

			if (ss != null) {
				try {
					ss.close();
				} catch (IOException e) {
					/* should not be thrown */
				}
			}
		}

		return false;
	}

	public static List<BiologicalEdgeAbstract> getEdgesSortedByID(Collection<BiologicalEdgeAbstract> edges) {
		Map<Integer, BiologicalEdgeAbstract> map = new HashMap<>();
		for (BiologicalEdgeAbstract bea : edges) {
			map.put(bea.getID(), bea);
		}
		ArrayList<Integer> ids = new ArrayList<>(map.keySet());
		ids.sort(Integer::compare);
		List<BiologicalEdgeAbstract> sortedList = new ArrayList<>();
		for (Integer id : ids) {
			sortedList.add(map.get(id));
		}
		return sortedList;
	}
}
