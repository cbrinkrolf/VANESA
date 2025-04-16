package petriNet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import configurations.Workspace;
import util.VanesaUtility;

public class OMCCommunicator {
	private final Path bin;
	private final Path pathToMos;
	private Set<String> pnLibVersions;
	private boolean supportedPackageManagerChecked = false;
	private boolean supportPackageManager = false;

	public OMCCommunicator(Path bin) {
		this.bin = bin;
		pathToMos = Workspace.getCurrent().getPath().resolve("scripting.mos");
	}

	/**
	 * 
	 * @return empty string if a PNlib is installed, otherwise returns "false";
	 */
	public boolean isPNLibInstalled() {
		try {
			writeMosFile(getTestPNlibScripting());
			String output = runMosFile(pathToMos);
			tryDeleteMos();
			return !output.contains("false");
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}

	private void tryDeleteMos() {
		final File file = pathToMos.toFile();
		if (file.exists()) {
			file.delete();
		}
	}

	public boolean isInstallPNlibSuccessful(String version) {
		try {
			writeMosFile(installPNlib(version));
			String output = runMosFile(pathToMos);
			tryDeleteMos();
			return !output.contains("false");
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean isPackageManagerSupported() {
		if (!supportedPackageManagerChecked) {
			try {
				writeMosFile(getOMCVersionScripting());
				String output = runMosFile(pathToMos);
				tryDeleteMos();
				// expected: "OpenModelica v1.19.0 (64-bit)"
				String[] temp = output.split("OpenModelica v");
				String[] tmp = temp[1].split("\\.");
				if (tmp.length > 1) {
					int major = Integer.parseInt(tmp[0]);
					int minor = Integer.parseInt(tmp[1]);
					supportPackageManager = major > 1 || (major == 1 && minor >= 19);
				} else {
					supportPackageManager = false;
				}
				supportedPackageManagerChecked = true;
			} catch (IOException | InterruptedException | NumberFormatException e) {
				e.printStackTrace();
				supportPackageManager = false;
				supportedPackageManagerChecked = true;
			}
		}
		return supportPackageManager;
	}

	public boolean isPNlibVersionInstalled(String version) {

		if (pnLibVersions == null) {
			try {
				writeMosFile(getInstalledPNVersions());
				String output = runMosFile(pathToMos);
				tryDeleteMos();
				output = output.replace("\"", "");
				output = output.replace("{", "");
				output = output.replace("}", "");
				output = output.strip();

				if (output.isEmpty() || output.isBlank()) {
					return false;
				}

				String[] tokens = output.split(",");

				pnLibVersions = new HashSet<>(Arrays.asList(tokens));
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
				return false;
			}
		}
		return pnLibVersions.contains(version);
	}

	public String runMosFile(Path pathToMos) throws IOException, InterruptedException {
		final Process process = new ProcessBuilder(bin.toString(), pathToMos.toString()).start();
		final byte[] bytes;
		try (InputStream os = process.getInputStream()) {
			process.waitFor();
			bytes = os.readAllBytes();
		}
		process.destroy();
		return new String(bytes);
	}

	private void writeMosFile(String content) throws IOException {
		try (FileWriter stream = new FileWriter(pathToMos.toFile()); BufferedWriter out = new BufferedWriter(stream)) {
			out.write(content);
		}
	}

	private String getOMCVersionScripting() {
		StringBuilder sb = new StringBuilder();
		sb.append("getVersion();\r\n");
		sb.append("getErrorString();\r\n");
		return sb.toString();
	}

	private String getTestPNlibScripting() {
		StringBuilder sb = new StringBuilder();
		sb.append("loadModel(PNlib);\r\n");
		sb.append("getErrorString();\r\n");
		return sb.toString();
	}

	private String getInstalledPNVersions() {
		StringBuilder sb = new StringBuilder();
		sb.append("getAvailableLibraryVersions(PNlib);\r\n");
		sb.append("getErrorString();\r\n");
		return sb.toString();
	}

	private String installPNlib(String version) {
		StringBuilder sb = new StringBuilder();
		sb.append("updatePackageIndex();\r\n");
		sb.append("installPackage(PNlib, \"").append(version).append("\");\r\n");
		sb.append("getErrorString();\r\n");
		return sb.toString();
	}
}
