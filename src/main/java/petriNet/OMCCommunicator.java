package petriNet;

import java.io.*;
import java.nio.file.Path;

import util.VanesaUtility;

public class OMCCommunicator {
	private final Path bin;
	private final Path pathToMos;

	public OMCCommunicator(Path bin) {
		this.bin = bin;
		pathToMos = VanesaUtility.getWorkingDirectoryPath().resolve("scripting.mos");
	}

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
				return major > 1 || (major == 1 && minor >= 19);
			}
			return false;
		} catch (IOException | InterruptedException | NumberFormatException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean isPNlibVersionInstalled(String version) {
		try {
			writeMosFile(getInstalledPNVersions());
			String output = runMosFile(pathToMos);
			tryDeleteMos();
			return output.contains("PNlib " + version + "\"");
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return false;
		}
	}

	public String runMosFile(Path pathToMos) throws IOException, InterruptedException {
		final Process process = new ProcessBuilder(bin.toString(), pathToMos.toString()).start();
		final byte[] bytes;
		try (InputStream os = process.getInputStream()) {
			// boolean buildSuccess = true;
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
		sb.append("loadModel(PNlib);\r\n");
		sb.append("getLoadedLibraries();\r\n");
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
