package petriNet.Runnable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.SystemUtils;

import biologicalElements.GraphElementAbstract;
import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.petriNet.Place;
import graph.ChangedFlags;
import graph.gui.Boundary;
import graph.gui.Parameter;
import gui.PopUpDialog;
import io.MOoutput;
import petriNet.SimulationLog;
import petriNet.SimulationProperties;
import util.VanesaUtility;

public class SimulationThread {

	private SimulationProperties properties;
	private Pathway pw;
	private SimulationLog simLog;

	public SimulationThread(SimulationProperties simProperties, Pathway pw, SimulationLog simLog) {
		this.properties = simProperties;
		this.pw = pw;
		this.simLog = simLog;
	}

	public Thread getSimulationThread(Runnable onSuccess, Runnable onError) {
		return new Thread(() -> {
			boolean success = true;
			try {
				ProcessBuilder pb = new ProcessBuilder();
				String override = "";
				if (SystemUtils.IS_OS_WINDOWS) {
					override += "\"";
				}
				final int seed = properties.getSeed();
				final String stepSize = VanesaUtility
						.fixedPrecisionDivide(properties.getStopTime(), BigDecimal.valueOf(properties.getIntervals()))
						.toPlainString();
				override += "-override=seed=" + seed + ",placeLocalSeed=" + MOoutput.generateLocalSeed(seed)
						+ ",transitionLocalSeed=" + MOoutput.generateLocalSeed(seed);
				ChangedFlags flags = properties.getFlags();
				System.out.println("parameter changed: " + flags.isParameterChanged());
				if (flags.isParameterChanged()) {
					for (Parameter param : pw.getChangedParameters().keySet()) {
						GraphElementAbstract gea = pw.getChangedParameters().get(param);
						if (gea instanceof BiologicalNodeAbstract) {
							BiologicalNodeAbstract bna = (BiologicalNodeAbstract) gea;
							override += ",'_" + bna.getName() + "_" + param.getName() + "'="
									+ param.getValue().toPlainString();
						} else {
							// CHRIS override parameters of edges
						}
					}
				}

				if (flags.isInitialValueChanged()) {
					for (final Place p : pw.getChangedInitialValues().keySet()) {
						final Double d = pw.getChangedInitialValues().get(p);
						override += ",'" + p.getName() + "'.start" + VanesaUtility.getMarksOrTokens(p) + "=" + d;
					}
				}

				if (flags.isBoundariesChanged()) {
					for (final Place p : pw.getChangedBoundaries().keySet()) {
						final Boundary b = pw.getChangedBoundaries().get(p);
						if (b.isLowerBoundarySet()) {
							override += ",'" + p.getName() + "'.min" + VanesaUtility.getMarksOrTokens(p) + "="
									+ b.getLowerBoundary();
						}
						if (b.isUpperBoundarySet()) {
							override += ",'" + p.getName() + "'.max" + VanesaUtility.getMarksOrTokens(p) + "="
									+ b.getUpperBoundary();
						}
					}
				}

				override += properties.getOverrideParameterized();

				if (SystemUtils.IS_OS_WINDOWS) {
					override += "\"";
				}
				// String program = "_omcQuot_556E7469746C6564";
				simLog.addLine("override statement: " + override);
				final List<String> cmdArguments = new ArrayList<>();
				cmdArguments.add(properties.getSimName());
				cmdArguments.add("-s=" + properties.getSolver());
				cmdArguments.add("-outputFormat=ia");
				cmdArguments.add("-stopTime=" + properties.getStopTime().toPlainString());
				cmdArguments.add("-stepSize=" + stepSize);
				cmdArguments.add("-tolerance=" + properties.getTolerance().toPlainString());
				cmdArguments.add(override);
				cmdArguments.add("-port=" + properties.getPort());
				// If we wouldn't want event emission: cmdArguments.add("-noEventEmit");
				cmdArguments.add("-lv=LOG_STATS");
				// for the simulation results export of protected variables, necessary to detect
				// actual firing of stochastic transitions
				cmdArguments.add("-emit_protected");
				pb.command(cmdArguments.toArray(new String[0]));
				pb.redirectOutput();
				pb.directory(properties.getPathSim().toFile());
				Map<String, String> env = pb.environment();
				// String envPath = env.get("PATH");
				String envPath = System.getenv("PATH");
				envPath = properties.getPathCompiler().resolve("bin") + ";" + envPath;
				env.put("PATH", envPath);
				System.out.println("working path:" + env.get("PATH"));
				System.out.println(pb.environment().get("PATH"));
				Process simProcess = pb.start();
				properties.setSimProcess(simProcess);

				properties.setOutputReader(new BufferedReader(new InputStreamReader(simProcess.getInputStream())));

			} catch (Exception e1) {
				success = false;
				PopUpDialog.getInstance().show("Simulation error:", e1.getMessage());
				e1.printStackTrace();
				if (properties.getSimProcess() != null) {
					properties.getSimProcess().destroy();
				}
				System.out.println("crash");
				onError.run();
				// System.out.println("after call");
				// return;
			}
			if (success) {
				onSuccess.run();
			}
			System.out.println("simulation thread finished");
		});
	}

}
