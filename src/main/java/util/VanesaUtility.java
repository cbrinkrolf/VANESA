package util;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Rectangle;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import gui.PopUpDialog;

public class VanesaUtility {
	public static final Color POSITIVE_COLOR = new Color(186, 255, 201);
	public static final Color NEGATIVE_COLOR = new Color(255, 179, 186);
	public static final Color NEUTRAL_COLOR = new Color(48, 70, 116);

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

	public static void openFolderInExplorer(final Path path) {
		if (Desktop.isDesktopSupported()) {
			Desktop desktop = Desktop.getDesktop();
			try {
				desktop.open(path.toFile());
			} catch (IOException e) {
				e.printStackTrace();
				PopUpDialog.getInstance().show("Path Error", e.getMessage());
			}
		}
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

	public static Rectangle offsetRectangle(final Rectangle r, final double offset) {
		return new Rectangle((int) (r.x - offset), (int) (r.y - offset), (int) (r.width + offset * 2),
				(int) (r.height + offset * 2));
	}

	public static Rectangle scaleRectangle(final Rectangle r, final double factor) {
		return new Rectangle((int) (r.x * factor), (int) (r.y * factor), (int) (r.width * factor),
				(int) (r.height * factor));
	}

	public static BigDecimal fixedPrecisionDivide(final BigDecimal a, final BigDecimal b) {
		return a.divide(b, 24, RoundingMode.HALF_UP).stripTrailingZeros();
	}
}
