/***************************************************************
 * Copyright (c) Benjamin Kormeier 2006-2009.                  *
 * All rights reserved.                                        *
 ***************************************************************/
package ant;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * @author Benjamin Kormeier
 * @version 1.0 20.11.2008
 */
public class StartScriptGenerator extends Task {
	private String scriptname = new String("start");
	private String target = null;
	private String lib = null;
	private String mainJar = null;
	private String main = null;

	private String xms = new String();
	private String xmx = new String();
	private String CP_FLAG = new String("-cp");

	private boolean windows = false;
	private boolean linux = false;

	private BufferedWriter bf;

	/**
	 * @param target
	 *            the target to set
	 */
	public void setTarget(String target) {
		this.target = target;
	}

	/**
	 * @param lib
	 *            the lib to set
	 */
	public void setLib(String lib) {
		this.lib = lib;
	}

	/**
	 * @param mainJar
	 *            the mainJar to set
	 */
	public void setMainJar(String mainJar) {
		this.mainJar = mainJar;
	}

	/**
	 * @param main
	 *            the main to set
	 */
	public void setMain(String main) {
		this.main = main;
	}

	/**
	 * @param xms
	 *            the xms to set
	 */
	public void setXms(String xms) {
		this.xms = "-Xms" + xms + "m";
	}

	/**
	 * @param xmx
	 *            the xmx to set
	 */
	public void setXmx(String xmx) {
		this.xmx = "-Xmx" + xmx + "m";
	}

	/**
	 * @param windows
	 *            the windows to set
	 */
	public void setWindows(String windows) {
		this.windows = Boolean.valueOf(windows);
	}

	/**
	 * @param linux
	 *            the linux to set
	 */
	public void setLinux(String linux) {
		this.linux = Boolean.valueOf(linux);
	}

	/**
	 * @param scriptname
	 *            the scriptname to set
	 */
	public void setScriptname(String scriptname) {
		this.scriptname = scriptname;
	}

	@Override
	public void execute() {
		if (lib == null)
			throw new BuildException("No library path defined");
		else if (target == null)
			throw new BuildException("No target defined");
		else if (main == null)
			throw new BuildException("No main class defined");
		else if (!new File(target).exists())
			throw new BuildException("Target dir does not exists");

		String libdir = lib.substring(target.length() + 1);
		String separator = new String();
		String suffix = new String();

		if (windows) {
			suffix = new String(".bat");
			separator = new String(";");

			// -- write script file --
			try {
				write(libdir, separator, suffix);
			} catch (IOException e) {
				throw new BuildException("Unable to write script files");
			}
		}

		if (linux) {
			separator = new String(":");
			suffix = new String(".sh");

			// -- write script file --
			try {
				write(libdir, separator, suffix);
			} catch (IOException e) {
				throw new BuildException("Unable to write script files");
			}
		}
	}

	private void write(String libdir, String separator, String suffix)
			throws IOException {
		String[] libraries = new File(lib).list();
		File file = new File(target + File.separator + scriptname + suffix);

		bf = new BufferedWriter(new FileWriter(file));

		// -- write java command with space and classpath definitions --
		bf.write("java " + xms + " " + xmx + " " + CP_FLAG + " ");

		if (mainJar != null)
			bf.write(mainJar + separator);

		for (String l : libraries) {
			if (l.toLowerCase().endsWith(".jar"))
				bf.write(libdir + File.separator + l + separator);
		}

		// -- main file for start --
		bf.write("." + separator + " " + main);

		bf.flush();
		bf.close();

		file.setExecutable(true);
		log("Build " + target + suffix + " successfull");
	}
}
