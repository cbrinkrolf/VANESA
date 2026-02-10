package petriNet;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JOptionPane;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import configurations.Workspace;
import gui.MainWindow;

public class OMCCommunicator {
	public static final Path OMC_FILE_PATH = Paths.get("bin").resolve(SystemUtils.IS_OS_WINDOWS ? "omc.exe" : "omc");

	private Path bin;
	private final Path pathToMos;
	private Set<String> pnLibVersions;
	private boolean supportedPackageManagerChecked = false;
	private boolean supportPackageManager = false;
	private SimulationLog simLog;
	private Path pathCompiler;

	public OMCCommunicator(SimulationLog simLog) {
		this.simLog = simLog;
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
				output = output.replaceAll("\"", "");
				output = output.replaceAll("\\{", "");
				output = output.replaceAll("\\}", "");
				output = output.replaceAll("\\s", "");

				if (output.isEmpty()) {
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

	public boolean isOpenModeilicaInstalled() {
		final String envPath = System.getenv("OPENMODELICAHOME");
		final String overridePath = Workspace.getCurrentSettings().isOverrideOMPath()
				? Workspace.getCurrentSettings().getOMPath().trim()
				: null;
		if (overridePath != null || envPath == null) {
			if (validateOMPath(overridePath)) {
				// noinspection DataFlowIssue
				pathCompiler = Paths.get(overridePath);
				bin = pathCompiler.resolve(OMC_FILE_PATH);
				return true;
			}
			logInvalidOMPath(overridePath);
		}
		if (validateOMPath(envPath)) {
			// noinspection DataFlowIssue
			pathCompiler = Paths.get(envPath);
			bin = pathCompiler.resolve(OMC_FILE_PATH);
			return true;
		}
		logInvalidOMPath(envPath);
		if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(MainWindow.getInstance().getFrame(),
				"Cannot find OpenModelica installation.\n\n"
						+ "Please install OpenModelica from \"https://openmodelica.org\".\n"
						+ "If OpenModelica is already installed, please set\n"
						+ "environment variable OPENMODELICAHOME to the installation directory.\n\n"
						+ "Do you want to open the OpenModelica homepage in your default web browser?",
				"Simulation aborted...", JOptionPane.YES_NO_OPTION)) {
			try {
				if (Desktop.isDesktopSupported()) {
					Desktop.getDesktop().browse(new URI("https://openmodelica.org"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	private boolean validateOMPath(final String path) {
		if (StringUtils.isBlank(path)) {
			return false;
		}
		final File file = new File(path);
		if (!file.exists() || !file.isDirectory()) {
			return false;
		}
		final File compilerFile = file.toPath().resolve(OMC_FILE_PATH).toFile();
		return compilerFile.exists() && compilerFile.isFile() && compilerFile.canExecute();
	}

	private void logInvalidOMPath(final String path) {
		if (path != null) {
			final File file = new File(path);
			final File compilerFile = file.toPath().resolve(OMC_FILE_PATH).toFile();
			if (simLog != null) {
				simLog.addLine("Given path of OpenModelica (" + file.getAbsolutePath() + ") is not a correct path!");
				simLog.addLine("Path exists: " + file.exists());
				simLog.addLine("Path is directory: " + file.isDirectory());
				simLog.addLine("Executable " + compilerFile.getAbsolutePath() + " exists: " + compilerFile.exists());
				simLog.addLine("Executable is file: " + compilerFile.isFile());
				simLog.addLine("Executable is can be executed: " + compilerFile.canExecute());
			}
		} else {
			if (simLog != null) {
				simLog.addLine("No OpenModelica path available!");
			}
		}
	}

	public Path getPathCompiler() {
		return pathCompiler;
	}
}
