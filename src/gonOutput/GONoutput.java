package gonOutput;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

import petriNet.ContinuousTransition;
import petriNet.DiscreteTransition;
import petriNet.Place;
import petriNet.StochasticTransition;
import petriNet.Transition;
import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;

/**
 * Anpassung an CSML 3.0
 * 
 * @author Rafael, cbrinkro
 */
public class GONoutput {

	//private static final boolean debug = false;

	private File file = null;
	private Pathway pw = null;
	private FileWriter fwriter;

	private String places = "", transitions = "";
	private int connector = 0;
	private double xmin = 1000, xmax = -1000, ymin = 1000, ymax = -1000;
	private double scale = 1;
	private HashMap<BiologicalNodeAbstract, String> edgesString = new HashMap<BiologicalNodeAbstract, String>();
	private Hashtable<BiologicalNodeAbstract, Point2D> placePositions = new Hashtable<BiologicalNodeAbstract, Point2D>();
	private Hashtable<BiologicalNodeAbstract, Point2D> transitionPositions = new Hashtable<BiologicalNodeAbstract, Point2D>();
	//private Hashtable<String, String> nodeType = new Hashtable<String, String>();
	//private Hashtable<String, String> nameTable = new Hashtable<String, String>();
	//private HashMap<String, Object> objectTable = new HashMap<String, Object>();
	//private HashMap<String, String> vertex2name = new HashMap<String, String>();
	//HashSet<BiologicalNodeAbstract> placesHS = new HashSet<BiologicalNodeAbstract>();
	//HashSet<BiologicalNodeAbstract> transitionsHS = new HashSet<BiologicalNodeAbstract>();

	public GONoutput(File file, Pathway pathway) {
		this.file = file;
		this.pw = pathway;
		try {
			write();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void write() throws IOException {

		fwriter = new FileWriter(file);

		// must be in this order
		prepare();
		buildConnections();
		buildNodes();

		fwriter
				.write("<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?>\r\n<csml:project\r\n xmlns:csml=\"http://www.csml.org/csml/version3\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" majorVersion=\"3\" minorVersion=\"0\" projectID=\"local\" projectVersionID=\"undef\">\r\n <csml:model modelID=\"undef\" modelVersionID=\"undef\">\r\n <csml:entitySet>\r\n");
		fwriter.write(places);
		fwriter.write("</csml:entitySet>\r\n<csml:processSet>\r\n");
		fwriter.write(transitions);
		fwriter
				.write("</csml:processSet>\r\n</csml:model>\r\n<csml:viewSet><csml:view name=\"Default View\" refAnimationID=\"default\" refModelID=\"undef\" refPositionID=\"default\" refShapeID=\"default\" viewID=\"default\">\r\n</csml:view>\r\n</csml:viewSet>\r\n</csml:project>\r\n");
		fwriter.close();

	}

	private void prepare() {
		Iterator<BiologicalNodeAbstract> it = pw.getAllNodes().iterator();
		BiologicalNodeAbstract bna;
		while (it.hasNext()) {
			bna = it.next();
			Point2D p = pw.getGraph().getVertexLocation(bna);
			//String biologicalElement = bna.getBiologicalElement();

			if(bna instanceof Transition){
				transitionPositions.put(bna, p);
			}else{
				placePositions.put(bna, p);
			}
			//objectTable.put(bna.getName(), bna);
			
			//nodeType.put(bna.getName(), biologicalElement);
			//nameTable.put(bna.getName(), bna.getLabel());
			//this.vertex2name.put(bna.getVertex().toString(), bna.getName());
			if (xmin > p.getX())
				xmin = p.getX();
			if (xmax < p.getX())
				xmax = p.getX();
			if (ymin > p.getY())
				ymin = p.getY();
			if (ymax < p.getY())
				ymax = p.getY();
		}
		xmin -= 20;
		ymin -= 20;
	}

	private void buildConnections() {

		Iterator<BiologicalEdgeAbstract> it = pw.getAllEdges().iterator();

		BiologicalEdgeAbstract bea;
		BiologicalNodeAbstract from;
		BiologicalNodeAbstract to;
		while (it.hasNext()) {
			bea = it.next();

			// get name and position and type
//			String fromString = this.vertex2name.get(bna.getEdge().getEndpoints().getFirst()
//					.toString());
//			String toString = this.vertex2name.get(bna.getEdge().getEndpoints().getSecond()
//					.toString());
			from = bea.getFrom();
			to = bea.getTo();
			
			String first = from.getBiologicalElement();//this.nodeType.get(fromString);
			String type = "";
			//String transition = "";
			//String place = "";
			if (first.contains("Place")) {
				type = "InputProcessBiological";
				//transition = toString;
				//place = fromString;
				edge(to, from, type, bea);
			} else if (first.contains("Transition")) {
				type = "OutputProcessBiological";
				//transition = fromString;
				//place = toString;
				edge(from, to, type, bea);
			}
			//System.out.println("T: " + transition + " p: " + place + " type: "
				//	+ type);
			//edge(transition, place, type);
			// edge(fromString, "A"+addedNodes, type);

		}
	}

	private void edge(BiologicalNodeAbstract transition, BiologicalNodeAbstract place, String type, BiologicalEdgeAbstract edge) {
		Point2D fromPoint = transitionPositions.get(transition);
		Point2D toPoint = placePositions.get(place);
		String connection = getConnectionString(place, type, fromPoint, toPoint, edge);
		if (!edgesString.containsKey(transition)) {
			edgesString.put(transition, connection);
		} else {
			edgesString.put(transition, edgesString.get(transition) + connection);
		}
	}

	private void buildNodes() {
		Iterator<BiologicalNodeAbstract> it = placePositions.keySet().iterator();
		BiologicalNodeAbstract bna;
		while (it.hasNext()) {
			bna = it.next();
			//String type = nodeType.get(nodeName);
			places = places.concat(getPlaceString(bna));
		}
		
		it = transitionPositions.keySet().iterator();
		while(it.hasNext()){
			bna = it.next();
			transitions = transitions.concat(getTransitionString(bna));
		}
	}

	private String getPlaceString(BiologicalNodeAbstract bna) {
		Point2D p = placePositions.get(bna);
		String type = "";
		Place place = (Place) bna;
		
		//System.out.println(this.nodeType.get(name));
		if(bna.getBiologicalElement().equals(Elementdeclerations.place)){
			type = "Integer";
		}else if(bna.getBiologicalElement().equals(Elementdeclerations.s_place)){
			type = "Double";
		}
		String start = place.getTokenStart()+"";
		String min = place.getTokenMin()+"";
		String max = "";
		if(place.getTokenMax() < 0){
			max = "infinite";
		}else{
			max = place.getTokenMax()+"";
		}
		
		
		return "  <csml:entity id=\""
				+ bna.getID()
				+ "\" name=\""
				+ bna.getName()
				+ "\" type=\"cso:-\">\r\n"
				+

				"<csml:entitySimulationProperty>\r\n"
				+
				" <csml:variable type = \"csml-variable:"+type+"\" variableID=\""
				+ bna.getLabel()
				+ "\">\r\n"
				+
				"  <csml:parameter key=\"csml-variable:parameter:initialValue\" value=\""+start+"\">\r\n"
				+ "  </csml:parameter>\r\n"
				+ "  <csml:parameter key=\"csml-variable:parameter:maximumValue\" value=\""+max+"\">\r\n"
				+ "  </csml:parameter>\r\n"
				+ "  <csml:parameter key=\"csml-variable:parameter:minimumValue\" value=\""+min+"\">\r\n"
				+ "  </csml:parameter>\r\n"
				+ "  <csml:parameter key=\"csml-variable:parameter:unit\" value=\"unit\">\r\n"
				+ "  </csml:parameter>\r\n"
				+ "  <csml:parameter key=\"csml-variable:parameter:global\" value=\"false\">\r\n"
				+ "  </csml:parameter>\r\n"
				+ "  <csml:parameter key=\"csml-variable:parameter:evaluateScriptOnce\" value=\"true\">\r\n"
				+ "  </csml:parameter>\r\n"
				+ " </csml:variable>\r\n"
				+ "</csml:entitySimulationProperty>\r\n"
				+ "<csml:viewProperty>\r\n"
				+ " <csml:position position=\"rotation:0.0\" positionID=\"default\" x=\""
				+ Math.floor(scale * (p.getX() - xmin)) + "\" y=\""
				+ Math.floor(scale * (p.getY() - ymin)) + "\">\r\n"
				+ " </csml:position>\r\n"
				+ " <csml:shape shapeID=\"default\" visible=\"true\">\r\n"
				+ " </csml:shape>\r\n" + "</csml:viewProperty>\r\n"
				+ "<csml:biologicalProperty refCellComponentID=\"-\">"
				+ " <csml:property key=\"extension\" value=\"\">"
				+ " </csml:property>"
				+ " <csml:property key=\"accession\" value=\"\">"
				+ " </csml:property>"
				+ " <csml:property key=\"probeID\" value=\"\">"
				+ " </csml:property>" + "</csml:biologicalProperty>"
				+ "<csml:comments>" + "<csml:comment type=\"text\">"
				+ "</csml:comment>" + "</csml:comments>" +
				"  </csml:entity>\r\n";
	}

	private String getTransitionString(BiologicalNodeAbstract bna) {
		return getTransitionHeader(bna)
				+ (edgesString.get(bna) == null ? "" : edgesString.get(bna))
				// +(outEdgesString.get(name)==null?"":outEdgesString.get(name))
				+ getTransitionTail(transitionPositions.get(bna));
	}

	private String getTransitionHeader(BiologicalNodeAbstract bna) {
		String type = "";
		//Object o = objectTable.get(bna);
		if(bna instanceof DiscreteTransition){
			type = "Discrete";
		}else if (bna instanceof ContinuousTransition){
			type = "Continuous";
		}else if (bna instanceof StochasticTransition){
			type = "Stochastic";
		}
		
		return "  <csml:process id=\"" + bna.getID() + "\" name=\""
				+ (bna.getLabel().equals("") ? " " : bna.getLabel())
				//TODO Transitiontype Discrete vs Continuous
				+ "\" type=\""+type+"\">\r\n";
	}

	private String getConnectionString(BiologicalNodeAbstract place, String type,
			Point2D fromPoint, Point2D toPoint, BiologicalEdgeAbstract edge) {
		//TODO no kinetic information 
		return "<csml:connector id=\"c"
				+ (++connector)
				+ "\" name=\"c"
				+ connector
				+ "\" refID=\""
				+ place.getID()
				+ "\" type=\""
				+ type
				+ "\">\r\n"
				+ "<csml:connectorSimulationProperty>\r\n"
				+ " <csml:connectorFiring connectorFiringStyle=\"csml-connectorFiringStyle:threshold\" value=\"5\">\r\n"
				+ " </csml:connectorFiring>\r\n"
				+ " <csml:connectorKinetic>\r\n"
				+ "  <csml:parameter key=\"stoichiometry\" value=\"3.0\">\r\n"
				+ "  </csml:parameter>\r\n"
				+ "  <csml:parameter key=\"custom\" value=\"2.0\">\r\n"
				+ "  </csml:parameter>\r\n"
				+ " </csml:connectorKinetic>\r\n"
				+ "</csml:connectorSimulationProperty>\r\n"
				+ "<csml:comments>" + " <csml:comment type=\"text\">\r\n"
				+ " </csml:comment>\r\n" + "</csml:comments>\r\n"
				+ "</csml:connector>\r\n";
	}

	private String getTransitionTail(Point2D p) {
		return "   <csml:processSimulationProperty>\r\n"
				+ "    <csml:priority value=\"0\"/>\r\n"
				+ "    <csml:firing firingOnce=\"false\" firingStyle=\"csml-firingStyle:and\" type=\"csml-variable:Boolean\" value=\"true\" />\r\n"
				+ "    <csml:delay delayStyle=\"nodelay\" value=\"0.0\"/>\r\n"
				+ "    <csml:processKinetic calcStyle=\"csml-calcStyle:speed\" fast=\"false\" kineticStyle=\"csml-kineticStyle:custom\">\r\n"
				+
				"     <csml:parameter key=\"custom\" value=\"1.0\">\r\n"
				+ "     </csml:parameter>\r\n"
				+ "    </csml:processKinetic>\r\n"
				+ "    </csml:processSimulationProperty>\r\n"
				+ "    <csml:viewProperty>\r\n"
				+
				"     <csml:position position=\"rotation:0.0\" positionID=\"default\" x=\""+Math.floor(scale * (p.getX() - xmin))+"\" y=\""+Math.floor(scale * (p.getY() - ymin))+"\">\r\n"
				+ "     </csml:position>\r\n"
				+ "     <csml:shape shapeID=\"default\" visible=\"true\">\r\n"
				+ "     </csml:shape>\r\n" + "    </csml:viewProperty>\r\n"
				+ "    <csml:biologicalProperty>\r\n"
				+ "    </csml:biologicalProperty>\r\n"
				+ "    <csml:comments>\r\n"
				+ "     <csml:comment type=\"text\">\r\n"
				+ "     </csml:comment>\r\n" + "    </csml:comments>\r\n" +
				"  </csml:process>\r\n";
	}
}
