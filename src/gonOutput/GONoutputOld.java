package gonOutput;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;

/**
 * @author Rafael
 */
public class GONoutputOld {

	private static final boolean debug=false;

	private File file = null;
	private Pathway pw = null;
	private FileWriter fwriter;

	private String places="", transitions="";
	private int addedNodes = 0;
	private int connector=0;
	private double xmin=1000, xmax=-1000, ymin=1000, ymax=-1000;
	private double scale=1;
	private Hashtable<String, String> inEdgesString=new Hashtable<String, String>();
	private Hashtable<String, String> outEdgesString=new Hashtable<String, String>();
	private Hashtable<String, Point2D> nodePositions = new Hashtable<String, Point2D>();
	private Hashtable<String, String> nodeType = new Hashtable<String, String>();
	private HashSet<String> hasInEdges = new HashSet<String>();
	private Hashtable<String, String> nameTable = new Hashtable<String, String>();
	
	public GONoutputOld(File file, Pathway pathway) {
		this.file = file;
		this.pw = pathway;
		try {
			write();
		} catch (IOException e) {e.printStackTrace();}
	}
	
	public void write() throws IOException {

		fwriter=new FileWriter(file);

		//must be in this order
		prepare();
		buildConnections();
		buildNodes();
	
		fwriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<csml:model\r\n xmlns:csml=\"http://www.csml.org/csml/version1\" majorVersion=\"1\" minorVersion=\"9\">\r\n <csml:unitdefs/>\r\n <csml:net>\r\n");
		fwriter.write(places);
		fwriter.write(transitions);
		fwriter.write(" </csml:net>\r\n <csml:simulation enhancedFiring=\"true\" samplingInterval=\"1.0\" simulationTime=\"1000.0\" logUpdateInterval=\"1.0\" plotUpdateInterval=\"1.0\" useContinuousWeakFiring=\"false\" useDiscreteWeakFiring=\"false\" firingAccuracy=\"1.0E-10\"/>\r\n</csml:model>");
		fwriter.close();

	}

	private void prepare() {
		BiologicalNodeAbstract bna;
		Iterator<BiologicalNodeAbstract> it = pw.getAllNodes().iterator();
		while (it.hasNext()) {
			bna = it.next();
			Point2D p = pw.getGraph().getClusteringLayout().getLocation(bna.getVertex());
			String biologicalElement = bna.getBiologicalElement();
			
			nodePositions.put(bna.getVertex().toString(), p);
			nodeType.put(bna.getVertex().toString(), biologicalElement);
			nameTable.put(bna.getVertex().toString(), bna.getLabel());
			if (xmin>p.getX()) xmin=p.getX();
			if (xmax<p.getX()) xmax=p.getX();
			if (ymin>p.getY()) ymin=p.getY();
			if (ymax<p.getY()) ymax=p.getY();
		}
		xmin-=20; ymin-=20;
	}
	
	@SuppressWarnings("unchecked")
	private void buildConnections() {

		Iterator it = pw.getAllEdges().iterator();

		while (it.hasNext()) {
			BiologicalEdgeAbstract bna = (BiologicalEdgeAbstract) it.next();
		
			//get name and position and type
			String fromString=bna.getEdge().getEndpoints().getFirst().toString();
			String toString=bna.getEdge().getEndpoints().getSecond().toString();
			Point2D fromPosition = nodePositions.get(fromString);
			Point2D toPosition = nodePositions.get(toString);
			boolean fromIsEnzyme = nodeType.get(fromString).equals("Enzyme");
			boolean toIsEnzyme = nodeType.get(toString).equals("Enzyme");
			
			if (fromIsEnzyme ^ toIsEnzyme) {
				edge(fromString, toString);
				if (!bna.isDirected()) edge(toString, fromString);
			} else {

				//calculate positions of additional elements
				Point2D m1=(Point2D) fromPosition.clone(), m2=(Point2D) toPosition.clone();
				double xM=(fromPosition.getX()+toPosition.getX())/2;
				double yM=(fromPosition.getY()+toPosition.getY())/2;
				double xDif=fromPosition.getX()-toPosition.getX();
				double yDif=fromPosition.getY()-toPosition.getY();
				
				m1.setLocation(xM+yDif/8, yM-xDif/8);
				if (!bna.isDirected()) m2.setLocation(xM-yDif/8, yM+xDif/8);
				
				//both are enzymes: add place
				if (fromIsEnzyme == toIsEnzyme) {
					nodePositions.put("A"+(++addedNodes),m1);
					nodeType.put("A"+(addedNodes), (fromIsEnzyme?"Protein":"Enzyme"));
					
					edge(fromString, "A"+addedNodes);
					edge("A"+addedNodes, toString);
						
					if (!bna.isDirected()){	//create second place
						nodePositions.put("A"+(++addedNodes),m2);
						nodeType.put("A"+(addedNodes), (fromIsEnzyme?"Protein":"Enzyme"));
							
						edge(toString, "A"+addedNodes);
						edge("A"+addedNodes, fromString);
					}
				}
			}
		}
	}
		
	private void edge(String fromString, String toString) {
		Point2D fromPoint=nodePositions.get(fromString);
		Point2D toPoint=nodePositions.get(toString);
		String connection = getConnectionString(fromString, toString, fromPoint, toPoint);
		boolean fromIsEnzyme = nodeType.get(fromString).equals("Enzyme");
		//boolean toIsEnzyme = nodeType.get(toString).equals("Enzyme");
		hasInEdges.add(toString);
		if(fromIsEnzyme) {
			if (!outEdgesString.containsKey(fromString)) outEdgesString.put(fromString, connection);
			else outEdgesString.put(fromString, outEdgesString.get(fromString)+connection);
		} else {
			if (!inEdgesString.containsKey(toString)) inEdgesString.put(toString, connection);
			else inEdgesString.put(toString, inEdgesString.get(toString)+connection);
		}
	}

	private void buildNodes() {
		Iterator<String> it = nodePositions.keySet().iterator();
		while (it.hasNext()) {
			String nodeName = it.next();
			String type = nodeType.get(nodeName);
			
			if (type.equals("Enzyme"))
				transitions=transitions.concat(getTransitionString(nodeName));
			else
				places=places.concat(getPlaceString(nodeName));


		}
	}
	
	private String getPlaceString(String name){
		Point2D p=nodePositions.get(name);
		return "  <csml:entity label=\""+name+"\" name=\""+nameTable.get(name)+"\" type=\"continuous\" codelinkProbeID=\"\" extension=\"\" accession=\"\">\r\n" +
				"   <csml:parameter label=\"m"+name+"\" type=\"Double\" initialValue=\""+(hasInEdges.contains(name)?"0":"10")+"\" minimumValue=\"0\" maximumValue=\"infinite\" unit=\"unit\"/>\r\n" +
				"   <csml:graphics>\r\n" +
				"    <csml:figure>\r\n" +
				"     <csml:continuousEntity location=\""+
					Math.floor(scale*(p.getX()-xmin))+" "+
					Math.floor(scale*(p.getY()-ymin))+"\"/>\r\n" +
				"    </csml:figure>\r\n" +
				"   </csml:graphics>\r\n" +
				"  </csml:entity>\r\n";
	}
	
	private String getTransitionString(String name){
		return getTransitionHeader(name)
				+(inEdgesString.get(name)==null?"":inEdgesString.get(name))
				+(outEdgesString.get(name)==null?"":outEdgesString.get(name))
				+getTransitionTail(nodePositions.get(name));
	}
	
	private String getTransitionHeader(String name){
		return "  <csml:process label=\""+name+"\" name=\""+(nameTable.get(name)==null?" ":nameTable.get(name))+"\" type=\"continuous\" relationType=\"Type 0\" edgeScore=\"1.0\">\r\n" +
				"   <csml:simulationCondition>\r\n" +
				"    <csml:priority value=\"0\"/>\r\n" +
				"    <csml:firing type=\"Boolean\" value=\"true\" firingStyle=\"and\"/>\r\n" +
				"    <csml:delay type=\"Long\" value=\"0.0\" delayStyle=\"nodelay\"/>\r\n" +
				"    <csml:calc calcStyle=\"speed\"/>\r\n" +
				"    <csml:kinetic type=\"Double\" value=\"1\" kineticStyle=\"custom\">\r\n" +
				"     <csml:parameter name=\"custom\" value=\"1\"/>\r\n" +
				"    </csml:kinetic>\r\n" +
				"   </csml:simulationCondition>\r\n" +
				"   <csml:function>\r\n";
	}
	
	private String getConnectionString(String from, String to, Point2D fromPoint, Point2D toPoint){
		return "    <csml:connector label=\"c"+(++connector)+"\" name=\"c"+connector+"\" type=\"process\" from=\""+from+"\" to=\""+to+"\" linestyle=\"straight\" supportpath=\"true\">\r\n" +
				"     <csml:firing type=\"Double\" value=\"0\" connectorFiringStyle=\"threshold\"/>\r\n" +
				"     <csml:kinetic/>\r\n" +
				"     <csml:graphics>\r\n" +
				"      <csml:figure>\r\n" +
				"       <csml:processConnector points=\""+Math.floor(scale*(fromPoint.getX()-xmin))+" "+Math.floor(scale*(fromPoint.getY()-ymin))+" "+Math.floor(scale*(toPoint.getX()-xmin))+" "+Math.floor(scale*(toPoint.getY()-ymin))+"\"/>\r\n" +
				"      </csml:figure>\r\n" +
				"     </csml:graphics>\r\n" +
				"    </csml:connector>\r\n";
	}
	
	
	private String getTransitionTail(Point2D p){
		return "   </csml:function>\r\n" +
				"   <csml:biological>\r\n" +
				"    <csml:effectList/>\r\n" +
				"   </csml:biological>\r\n" +
				"   <csml:graphics>\r\n" +
				"    <csml:figure>\r\n" +
				"     <csml:continuousProcess location=\"" + Math.floor(scale*(p.getX()-xmin)) + " " + Math.floor(scale*(p.getY()-ymin)) + "\"/>\r\n" +
				"    </csml:figure>\r\n" +
				"   </csml:graphics>\r\n" +
				"  </csml:process>\r\n";

	}

}
