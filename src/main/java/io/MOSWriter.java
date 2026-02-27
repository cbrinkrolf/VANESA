package io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.petriNet.ContinuousTransition;
import biologicalObjects.nodes.petriNet.DiscreteTransition;
import biologicalObjects.nodes.petriNet.Place;
import biologicalObjects.nodes.petriNet.StochasticTransition;
import petriNet.CompilationProperties;

public class MOSWriter {

	private Pathway pw;
	private CompilationProperties properties;

	private boolean containsPlace = false;
	private boolean containsContinuousTransition = false;
	private boolean containsDiscreteTransition = false;
	private boolean containsStochasticTransition = false;
	private int countPlaces = 0;
	private int countContinuousTransitions = 0;
	private int countDiscreteTransitions = 0;
	private int countStochasticTransitions = 0;

	private StringBuilder filterRegEx;

	public MOSWriter(CompilationProperties properties, Pathway pw) {
		this.properties = properties;
		this.pw = pw;
	}

	public void writeMosFile(boolean isBuiltInPNlibSelected) throws IOException {

		countNodesAndEdges();
		createVariableFilter();

		int vars = countPlaces + 2 * countContinuousTransitions + 3 * countDiscreteTransitions
				+ 3 * countStochasticTransitions + 2 * properties.getBea2key().values().size();

		System.out.println("expected number of output vars: " + vars);
		System.out.println("variableFilter: " + filterRegEx);
		try (final FileWriter fstream = new FileWriter(properties.getPathSim().resolve("simulation.mos").toFile());
				final BufferedWriter out = new BufferedWriter(fstream)) {
			out.write("cd(\"" + properties.getPathSim().toString().replace('\\', '/') + "\"); ");
			out.write("getErrorString();\r\n");
			if (isBuiltInPNlibSelected) {
				out.write("loadModel(PNlib,{\"" + properties.getSelectedSimLibVersion() + "\"}); ");
			} else {
				out.write("loadFile(\"" + properties.getSelectedSimLib().getPath().replace("\\", "/")
						+ "/package.mo\"); ");
			}
			out.write("getErrorString();\r\n");
			out.write("loadFile(\"simulation.mo\"); ");
			out.write("getErrorString();\r\n");
			// out.write("setDebugFlags(\"disableComSubExp\"); ");
			// out.write("getErrorString();\r\n");
			String commandLineOptions = "--unitChecking";
			if (properties.isOverrideEqPerFile() && properties.getEquationsPerFile() > 0) {
				commandLineOptions += " --equationsPerFile=" + properties.getEquationsPerFile();
			}

			out.write("setCommandLineOptions(\"" + commandLineOptions + "\"); ");
			// out.write("setCommandLineOptions(\"--unitChecking --newBackend
			// -d=mergeComponents\"); ");
			// out.write("setCommandLineOptions(\"+d=disableComSubExp
			// +unitChecking\");");
			out.write("getErrorString();\r\n");

			out.write("buildModel(" + properties.getModelicaModelName() + ", " + filterRegEx + "); ");
			out.write("getErrorString();\r\n");
		}
	}

	private void countNodesAndEdges() {
		for (final BiologicalNodeAbstract bna : pw.getAllGraphNodes()) {
			if (!bna.isLogical()) {
				if (bna instanceof Place) {
					containsPlace = true;
					countPlaces++;
				} else if (bna instanceof ContinuousTransition) {
					containsContinuousTransition = true;
					countContinuousTransitions++;
				} else if (bna instanceof DiscreteTransition) {
					containsDiscreteTransition = true;
					countDiscreteTransitions++;
				} else if (bna instanceof StochasticTransition) {
					containsStochasticTransition = true;
					countStochasticTransitions++;
				}
			}
		}
	}

	private void createVariableFilter() {
		filterRegEx = new StringBuilder();

		filterRegEx.append("variableFilter=\"");

		if (containsPlace) {
			filterRegEx.append("'.+\\\\.t");
		}
		if (containsContinuousTransition) {
			if (filterRegEx.length() > 0) {
				filterRegEx.append("|");
			}
			filterRegEx.append(".+\\\\.fire|");
			filterRegEx.append(".+\\\\.actualSpeed");
		}
		if (containsDiscreteTransition || containsStochasticTransition) {
			if (filterRegEx.length() > 0) {
				filterRegEx.append("|");
			}
			filterRegEx.append(".+\\\\.active|");
			filterRegEx.append(".+\\\\.fireTime");
		}
		if (containsDiscreteTransition) {
			if (filterRegEx.length() > 0) {
				filterRegEx.append("|");
			}
			filterRegEx.append(".+\\\\.delay");
		}
		if (containsStochasticTransition) {
			if (filterRegEx.length() > 0) {
				filterRegEx.append("|");
			}
			filterRegEx.append(".+\\\\.putDelay");
		}
		if (!pw.getAllEdges().isEmpty()) {
			filterRegEx.append("|.+\\\\.tokenFlow\\\\.inflow\\\\[\\\\d\\\\]|");
			filterRegEx.append(".+der\\\\(.+\\\\.tokenflow\\\\.inflow\\\\[\\\\d\\\\]\\\\)");
			filterRegEx.append("|.+\\\\.tokenFlow\\\\.outflow\\\\[\\\\d\\\\]|");
			filterRegEx.append(".+der\\\\(.+\\\\.tokenflow\\\\.outflow\\\\[\\\\d\\\\]\\\\)");
		}

		filterRegEx.append("\"");
	}
}
