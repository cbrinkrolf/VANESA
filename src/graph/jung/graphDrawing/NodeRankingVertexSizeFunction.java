package graph.jung.graphDrawing;

public class NodeRankingVertexSizeFunction implements VertexSizeFunction {

	private int minDefaultNodeSize = 20;
	private int mDefaultNodeSize = 200;
	private String mSizeKey;
	private double mNodeSizeScale;

	public NodeRankingVertexSizeFunction() {

	}

	public NodeRankingVertexSizeFunction(String key, double scale) {
		this.mSizeKey = key;
		this.mNodeSizeScale = scale;
	}

	public int getSize(Vertex vertex) {
		Number decoratedNodeSize = (Number) vertex.getUserDatum(mSizeKey);

		int nodeSize = minDefaultNodeSize
				+ (int) Math.ceil(100 * decoratedNodeSize.doubleValue()
						* mNodeSizeScale);
		return nodeSize;
	}

}
