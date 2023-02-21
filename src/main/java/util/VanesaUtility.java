package util;

import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.io.FileHandler;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.util.List;

public class VanesaUtility {
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
}
