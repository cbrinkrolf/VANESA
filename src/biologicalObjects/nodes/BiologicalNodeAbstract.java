package biologicalObjects.nodes;

import graph.jung.graphDrawing.VertexShapes;

import java.awt.Color;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

import biologicalElements.Elementdeclerations;
import biologicalElements.GraphElementAbstract;

public class BiologicalNodeAbstract extends GraphElementAbstract {

	//private Vertex vertex;
	private KEGGNode KEGGnode;
	private DAWISNode dawisNode;
	private String compartment = Elementdeclerations.cytoplasma;
	//private HashMap<Integer, Integer> values = new HashMap<Integer, Integer>();
	private int Nodesize = 20;
	private Vector<CollectorNode> collectorNodes = new Vector<CollectorNode>();
	private BiologicalNodeAbstract parentNode;
	// contains information on microarray data for this element
	private Vector<Double> petriNetSimulationData;
	public VertexShapes shapes;
	private String organism = "";
	String db = "";
	private DefaultMutableTreeNode treeNode;
	private Vector<String> elementsVector;
	private SBMLNode sbml = new SBMLNode();
	public final int GRAPH_ONE = 1;
	public final int GRAPH_TWO = 2;
	public final int GRAPH_BOTH = 3;
	//private int original_graph;
	private MicroArrayAttributes microarrayAttributes = null;
	private boolean hasMicroArrayAttributes = false;
	
	public boolean hasMicroArrayAttributes() {
		return hasMicroArrayAttributes;
	}

	public MicroArrayAttributes getMicroarrayAttributes() {
		return microarrayAttributes;
	}

	public void setMicroarrayAttributes(
			MicroArrayAttributes microarrayAttributes) {
		this.microarrayAttributes = microarrayAttributes;
		hasMicroArrayAttributes = true;
	}

	public SBMLNode getSbml() {
		return sbml;
	}

	public void setSbml(SBMLNode sbml) {
		this.sbml = sbml;
	}

	public BiologicalNodeAbstract(String label, String name) {
		setLabel(label);
		setName(name);
		//setVertex(vertex);
		setIsVertex(true);
		

		
		// values.put(1, 0);
		shapes = new VertexShapes();
		//setShape(shapes.getEllipse(null));

		// initialize microarray data vector
		petriNetSimulationData = new Vector<Double>();

		sbml.setName(name);
		sbml.setLabel(label);
//		if (vertex != null) {
//			sbml.setVertex(vertex.toString());
//		}
		sbml.setBiologicalNodeDescription(Elementdeclerations.transcriptionFactor);

	}

	public KEGGNode getKEGGnode() {
		return KEGGnode;
	}

	public void setKEGGnode(KEGGNode gnode) {
		hasKEGGNode(true);
		KEGGnode = gnode;
	}

	/**
	 * Returns the vector of this biological element's microarray data.
	 * 
	 * @author tschoeni
	 * @return microArrayData
	 */
	public Vector<Double> getPetriNetSimulationData() {
		return petriNetSimulationData;
	}

	/**
	 * Setter for this element's microarray data. Expects a vector containing
	 * Doubles.
	 * 
	 * @author tschoeni
	 * @param newmicroArrayData
	 */
	public void setPetriNetSimulationData(Vector<Double> petriNetSimulationData) {
		this.petriNetSimulationData = petriNetSimulationData;
	}

	/**
	 * This method expects an index and returns the corresponding value of the
	 * microarray data. It's return value is null, when the index exceeds the
	 * vectors size.
	 * 
	 * @author tschoeni
	 * @param index
	 * @return Double
	 */
	public Double getMicroArrayValue(int index) {
		if (index > petriNetSimulationData.size()) {
			return null;
		} else {
			return petriNetSimulationData.get(index);
		}
	}

	/**
	 * Removes a single value from the microarray data vector.
	 * 
	 * @author tschoeni
	 * @param index
	 *            The index to remove
	 */
	public void removeMicroArrayValue(int index) {
		petriNetSimulationData.remove(index);
	}

	/**
	 * Adds a new value to the microarray data vector.
	 * 
	 * @author tschoeni
	 * @param index
	 * @param value
	 */
	public void addMicroArrayValue(int index, Double value) {
		petriNetSimulationData.add(index, value);
	}

	/*public Vertex getVertex() {
		return vertex;
	}

	public void setVertex(Vertex vertex) {
		this.vertex = vertex;
		if (vertex != null) {
			sbml.setVertex(vertex.toString());
		}
	}*/

	@Override
	public Color getColor() {

		if (super.isReference()) {
			return Color.WHITE;
		} else {
			return super.getColor();
		}
	}

	public String getCompartment() {
		return compartment;
	}

	public void setCompartment(String compartment) {
		this.compartment = compartment;
	}

	/*
	 * public int getAnimationValue(int time) { return values.get(time); }
	 * 
	 * public void setAnimationValue(int time, int value) { values.put(time,
	 * value); }
	 * 
	 * public void removeAnimationValue(int time) { values.remove(time); }
	 * 
	 * public int getAnimationSteps() { return values.size(); }
	 */

	/**
	 * get DAWIS OMIM node
	 * 
	 * @return dawisNode
	 */
	public DAWISNode getDAWISNode() {
		return dawisNode;
	}

	/**
	 * set DAWIS OMIM node
	 * 
	 * @param dawisNode
	 */
	public void setDAWISNode(DAWISNode dawisNode) {
		this.dawisNode = dawisNode;
		hasDAWISNode(true);
	}

	public static void setShapes(VertexShapes s) {
		// shapes = s;
	}

	public void rebuildShape(VertexShapes vs) {
		//setShape(vs.getEllipse(getVertex()));
	}

	public int getNodesize() {
		return Nodesize;
	}

	public void setNodesize(int nodesize) {
		Nodesize = nodesize;
	}

	public void setOrganism(String org) {
		this.organism = org;
	}

	public String getOrganism() {
		return this.organism;
	}

	public void setDefaultMutableTreeNode(DefaultMutableTreeNode newNode) {
		this.treeNode = newNode;
	}

	public DefaultMutableTreeNode getDefaultMutableTreeNode() {
		return this.treeNode;
	}

	public void setElementsVector(Vector<String> v) {
		this.elementsVector = v;
	}

	public Vector<String> getElementsVector() {
		return this.elementsVector;
	}

	public void addCollectorNode(CollectorNode collectorNode) {
		collectorNodes.add(collectorNode);
	}

	public Vector<CollectorNode> getCollectorNodes() {
		return collectorNodes;
	}

	public void setParentNode(BiologicalNodeAbstract parent) {
		parentNode = parent;
	}

	public BiologicalNodeAbstract getParentNode() {
		return parentNode;
	}

	public void setDB(String database) {
		this.db = database;
	}

	public String getDB() {
		return this.db;
	}



	// public int getOriginal_graph() {
	// return original_graph;
	// }
	//
	// public void setOriginal_graph(int originalGraph) {
	// original_graph = originalGraph;
	// }
	//

}
