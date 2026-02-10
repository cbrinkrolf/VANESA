package petriNet;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import graph.ChangedFlags;

public class CompilationProperties {

	private ChangedFlags flags;
	private Path pathSim;
	private Path pathCompiler;
	private boolean isBuiltInPNlibSelected;
	private String selectedSimLibVersion;
	private File selectedSimLib;
	private String modelicaModelName;

	private int globalSeed;
	private Map<BiologicalEdgeAbstract, String> bea2key;
	private Process compileProcess;
	private boolean buildSuccess;
	private boolean isCompiling;
	private String simName;
	private boolean overrideEqPerFile;
	private int equationsPerFile;

	public ChangedFlags getFlags() {
		return flags;
	}

	public void setFlags(ChangedFlags flags) {
		this.flags = flags;
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

	public boolean isBuiltInPNlibSelected() {
		return isBuiltInPNlibSelected;
	}

	public void setBuiltInPNlibSelected(boolean isBuiltInPNlibSelected) {
		this.isBuiltInPNlibSelected = isBuiltInPNlibSelected;
	}

	public String getModelicaModelName() {
		return modelicaModelName;
	}

	public void setModelicaModelName(String modelicaModelName) {
		this.modelicaModelName = modelicaModelName;
	}

	public int getGlobalSeed() {
		return globalSeed;
	}

	public void setGlobalSeed(int globalSeed) {
		this.globalSeed = globalSeed;
	}

	public Map<BiologicalEdgeAbstract, String> getBea2key() {
		return bea2key;
	}

	public void setBea2key(Map<BiologicalEdgeAbstract, String> bea2key) {
		this.bea2key = bea2key;
	}

	public Process getCompileProcess() {
		return compileProcess;
	}

	public void setCompileProcess(Process compileProcess) {
		this.compileProcess = compileProcess;
	}

	public boolean isBuildSuccess() {
		return buildSuccess;
	}

	public void setBuildSuccess(boolean buildSuccess) {
		this.buildSuccess = buildSuccess;
	}

	public boolean isCompiling() {
		return isCompiling;
	}

	public void setCompiling(boolean isCompiling) {
		this.isCompiling = isCompiling;
	}

	public String getSimName() {
		return simName;
	}

	public void setSimName(String simName) {
		this.simName = simName;
	}

	public String getSelectedSimLibVersion() {
		return selectedSimLibVersion;
	}

	public void setSelectedSimLibVersion(String selectedSimLibVersion) {
		this.selectedSimLibVersion = selectedSimLibVersion;
	}

	public File getSelectedSimLib() {
		return selectedSimLib;
	}

	public void setSelectedSimLib(File selectedSimLib) {
		this.selectedSimLib = selectedSimLib;
	}

	public boolean isOverrideEqPerFile() {
		return overrideEqPerFile;
	}

	public void setOverrideEqPerFile(boolean overrideEqPerFile) {
		this.overrideEqPerFile = overrideEqPerFile;
	}

	public int getEquationsPerFile() {
		return equationsPerFile;
	}

	public void setEquationsPerFile(int equationsPerFile) {
		this.equationsPerFile = equationsPerFile;
	}

}
