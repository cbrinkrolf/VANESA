package petriNet.runnable;

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

public class SimulationThread extends SimulationRunnableAbstract {

	private Pathway pw;
	private StringBuilder override;
	private String stepSize;

	public SimulationThread(SimulationProperties simProperties, Pathway pw, SimulationLog simLog) {
		this.properties = simProperties;
		this.pw = pw;
		this.simLog = simLog;
	}

	public Thread getSimulationThread(Runnable onSuccess, Runnable onError) {
		override = new StringBuilder();
		return new Thread(() -> {
			boolean success = true;
			try {
				ProcessBuilder pb = new ProcessBuilder();

				generateOverride();

				final List<String> cmdArguments = new ArrayList<>();
				cmdArguments.add(properties.getSimName());
				cmdArguments.add("-s=" + properties.getSolver());
				cmdArguments.add("-outputFormat=ia");
				cmdArguments.add("-stopTime=" + properties.getStopTime().toPlainString());
				cmdArguments.add("-stepSize=" + stepSize);
				cmdArguments.add("-tolerance=" + properties.getTolerance().toPlainString());
				cmdArguments.add(override.toString());
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

			} catch (Exception e) {
				success = false;
				PopUpDialog.getInstance().show("Simulation error:", e.getMessage());
				e.printStackTrace();
				simLog.addLine(e.getMessage());
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

	private void generateOverride() {
		if (SystemUtils.IS_OS_WINDOWS) {
			override.append("\"");
		}
		final int seed = properties.getSeed();
		stepSize = VanesaUtility
				.fixedPrecisionDivide(properties.getStopTime(), BigDecimal.valueOf(properties.getIntervals()))
				.toPlainString();
		override.append("-override=seed=" + seed + ",placeLocalSeed=" + MOoutput.generateLocalSeed(seed)
				+ ",transitionLocalSeed=" + MOoutput.generateLocalSeed(seed));
		ChangedFlags flags = properties.getFlags();
		System.out.println("parameter changed: " + flags.isParameterChanged());

		if (flags.isParameterChanged()) {
			overrideParameters();
		}

		if (flags.isInitialValueChanged()) {
			overrideInitialValues();
		}

		if (flags.isBoundariesChanged()) {
			overrideBoundaries();
		}

		override.append(properties.getOverrideParameterized());

		if (SystemUtils.IS_OS_WINDOWS) {
			override.append("\"");
		}
		// String program = "_omcQuot_556E7469746C6564";
		simLog.addLine("override statement: " + override);
	}

	private void overrideParameters() {
		for (Parameter param : pw.getChangedParameters().keySet()) {
			GraphElementAbstract gea = pw.getChangedParameters().get(param);
			if (gea instanceof BiologicalNodeAbstract) {
				BiologicalNodeAbstract bna = (BiologicalNodeAbstract) gea;
				override.append(",'_");
				override.append(bna.getName());
				override.append("_");
				override.append(param.getName());
				override.append("'=");
				override.append(param.getValue().toPlainString());
			} else {
				// CHRIS override parameters of edges
			}
		}
	}

	private void overrideInitialValues() {
		for (final Place p : pw.getChangedInitialValues().keySet()) {
			final Double d = pw.getChangedInitialValues().get(p);
			override.append(",'");
			override.append(p.getName());
			override.append("'.start");
			override.append(VanesaUtility.getMarksOrTokens(p));
			override.append("=");
			override.append(d);
		}
	}

	private void overrideBoundaries() {
		for (final Place p : pw.getChangedBoundaries().keySet()) {
			final Boundary b = pw.getChangedBoundaries().get(p);
			if (b.isLowerBoundarySet()) {
				override.append(",'");
				override.append(p.getName());
				override.append("'.min");
				override.append(VanesaUtility.getMarksOrTokens(p));
				override.append("=");
				override.append(b.getLowerBoundary());
			}
			if (b.isUpperBoundarySet()) {
				override.append(",'");
				override.append(p.getName());
				override.append("'.max");
				override.append(VanesaUtility.getMarksOrTokens(p));
				override.append("=");
				override.append(b.getUpperBoundary());
			}
		}
	}
}
