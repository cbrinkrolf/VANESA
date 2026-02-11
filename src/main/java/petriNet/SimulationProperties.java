package petriNet;

import java.io.BufferedReader;
import java.math.BigDecimal;
import java.nio.file.Path;

import graph.ChangedFlags;

public class SimulationProperties {

	private boolean isServerRunning = true;
	private String simId = "";
	private ChangedFlags flags;
	private BigDecimal stopTime;
	private int intervals;
	private BigDecimal tolerance;
	private int seed;
	private String overrideParameterized;
	private int port;
	private String simName;
	private String solver;
	private Path pathSim;
	private Path pathCompiler;
	private Process simProcess;
	private boolean useCustomExecutableSelected;
	private BufferedReader outputReader;

	public boolean isServerRunning() {
		return isServerRunning;
	}

	public void setServerRunning(boolean isServerRunning) {
		this.isServerRunning = isServerRunning;
	}

	public String getSimId() {
		return simId;
	}

	public void setSimId(String simId) {
		this.simId = simId;
	}

	public ChangedFlags getFlags() {
		return flags;
	}

	public void setFlags(ChangedFlags flags) {
		this.flags = flags;
	}

	public BigDecimal getStopTime() {
		return stopTime;
	}

	public void setStopTime(BigDecimal stopTime) {
		this.stopTime = stopTime;
	}

	public int getIntervals() {
		return intervals;
	}

	public void setIntervals(int intervals) {
		this.intervals = intervals;
	}

	public BigDecimal getTolerance() {
		return tolerance;
	}

	public void setTolerance(BigDecimal tolerance) {
		this.tolerance = tolerance;
	}

	public int getSeed() {
		return seed;
	}

	public void setSeed(int seed) {
		this.seed = seed;
	}

	public String getOverrideParameterized() {
		return overrideParameterized;
	}

	public void setOverrideParameterized(String overrideParameterized) {
		this.overrideParameterized = overrideParameterized;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getSimName() {
		return simName;
	}

	public void setSimName(String simName) {
		this.simName = simName;
	}

	public String getSolver() {
		return solver;
	}

	public void setSolver(String solver) {
		this.solver = solver;
	}

	public Path getPathSim() {
		return pathSim;
	}

	public void setPathSim(Path pathSim) {
		this.pathSim = pathSim;
	}

	public Path getPathCompiler() {
		return pathCompiler;
	}

	public void setPathCompiler(Path pathCompiler) {
		this.pathCompiler = pathCompiler;
	}

	public Process getSimProcess() {
		return simProcess;
	}

	public void setSimProcess(Process simProcess) {
		this.simProcess = simProcess;
	}

	public boolean isUseCustomExecutableSelected() {
		return useCustomExecutableSelected;
	}

	public void setUseCustomExecutableSelected(boolean useCustomExecutableSelected) {
		this.useCustomExecutableSelected = useCustomExecutableSelected;
	}

	public BufferedReader getOutputReader() {
		return outputReader;
	}

	public void setOutputReader(BufferedReader outputReader) {
		this.outputReader = outputReader;
	}

}
