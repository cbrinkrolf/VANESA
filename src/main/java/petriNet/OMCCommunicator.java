package petriNet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import util.VanesaUtility;

public class OMCCommunicator {

	private String bin;
	private String pathToMos;

	public OMCCommunicator(String bin) {
		this.bin = bin;
		pathToMos = VanesaUtility.getWorkingDirectoryPath() + File.separator + "scripting.mos";

	}

	public boolean isPNLibInstalled() {
		try {
			writeMosFile(getTestPNlibScripting());
			String output = runMosFile(pathToMos);
			// PNlib not installed
			if (output.contains("false")) {
				// PNlib not installed
				return false;
			} else {
				// PNlib installed
				return true;
			}

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean isInstallPNlibSuccessful(String version) {
		try {
			writeMosFile(installPNlib(version));
			String output = runMosFile(pathToMos);
			if (output.contains("false")) {
				return false;
			} else {
				return true;
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean isPackageManagerSupported() {
		try {
			writeMosFile(getOMCVersionScripting());
			String output = runMosFile(pathToMos);
			// System.out.println(output);
			// expected: "OpenModelica v1.19.0 (64-bit)"
			String[] temp = output.split("OpenModelica v");
			String[] tmp = temp[1].split("\\.");
			if (tmp.length > 1) {

				// System.out.println(tmp[0]);
				// System.out.println(tmp[1]);
				int major = Integer.parseInt(tmp[0]);
				int minor = Integer.parseInt(tmp[1]);

				if (major > 1) {
					return true;
				}

				if (major == 1 && minor >= 19) {
					return true;
				}
			} else {
				return false;
			}
		} catch (IOException | InterruptedException | NumberFormatException e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}

	public boolean isPNlibVersionInstalled(String version) {
		try {
			writeMosFile(getInstalledPNVersions());
			String output = runMosFile(pathToMos);
			//System.out.println("output: "+output);
			if (output.contains("PNlib " + version+"\"")) {
				return true;
			} else {
				return false;
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return false;
		}
	}

	public String runMosFile(String pathToMos) throws IOException, InterruptedException {
		Process process = new ProcessBuilder(bin, pathToMos).start();
		InputStream os = process.getInputStream();

		// System.out.println(s);
		// boolean buildSuccess = true;
		//System.out.println("running mos file:  " + process.isAlive());
		process.waitFor();
		// byte[] bytes = new byte[os.available()];
		// os.read(bytes);
		byte[] bytes = os.readAllBytes();
		os.close();
		process.destroy();
		return new String(bytes);
	}

	private void writeMosFile(String content) throws IOException {

		FileWriter fstream = new FileWriter(pathToMos);
		BufferedWriter out = new BufferedWriter(fstream);
		out.write(content);
		out.close();
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
		sb.append("installPackage(PNlib, \"" + version + "\");\r\n");
		sb.append("getErrorString();\r\n");
		return sb.toString();
	}
}
