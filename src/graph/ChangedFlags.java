package graph;

public class ChangedFlags {

	public static final int NODE_CHANGED = 0;
	public static final int EDGE_CHANGED = 1;
	public static final int PARAMETER_CHANGED = 2;
	public static final int INITIALVALUE_CHANGED = 3;
	public static final int EDGEWEIGHT_CHANGED = 4;
	public static final int PNPROPERTIES_CHANGED = 5;
	public static final int BOUNDARIES_CHANGED = 6;

	// added, deleted, renamed
	private boolean nodeChanged;
	private boolean edgeChanged;
	// parameters of places, transitions
	private boolean parameterChanged;
	private boolean initialValueChanged;
	private boolean edgeWeightChanged;
	// distribution, speed function, activation probability, delay
	private boolean pnPropertiesChanged;
	private boolean boundariesChanged;

	public ChangedFlags() {
		this.nodeChanged = true;
		this.edgeChanged = true;
		this.parameterChanged = true;
		this.initialValueChanged = true;
		this.edgeWeightChanged = true;
		this.boundariesChanged = true;
	}

	public void reset() {
		this.nodeChanged = false;
		this.edgeChanged = false;
		this.parameterChanged = false;
		this.initialValueChanged = false;
		this.edgeWeightChanged = false;
		this.pnPropertiesChanged = false;
		this.boundariesChanged = false;
	}

	public boolean isNodeChanged() {
		return nodeChanged;
	}

	public void setNodeChanged(boolean nodeChanged) {
		this.nodeChanged = nodeChanged;
	}

	public boolean isEdgeChanged() {
		return edgeChanged;
	}

	public void setEdgeChanged(boolean edgeChanged) {
		this.edgeChanged = edgeChanged;
	}

	public boolean isParameterChanged() {
		return parameterChanged;
	}

	public void setParameterChanged(boolean parameterChanged) {
		this.parameterChanged = parameterChanged;
	}

	public boolean isInitialValueChanged() {
		return initialValueChanged;
	}

	public void setInitialValueChanged(boolean initialValueChanged) {
		this.initialValueChanged = initialValueChanged;
	}

	public boolean isEdgeWeightChanged() {
		return edgeWeightChanged;
	}

	public void setEdgeWeightChanged(boolean edgeWeightChanged) {
		this.edgeWeightChanged = edgeWeightChanged;
	}

	public boolean isPnPropertiesChanged() {
		return pnPropertiesChanged;
	}

	public void setPnPropertiesChanged(boolean pnPropertiesChanged) {
		this.pnPropertiesChanged = pnPropertiesChanged;
	}

	public boolean isBoundariesChanged() {
		return boundariesChanged;
	}

	public void setBoundariesChanged(boolean boundariesChanged) {
		this.boundariesChanged = boundariesChanged;
	}
}
