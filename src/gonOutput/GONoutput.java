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

	private static final boolean debug = false;

	private File file = null;
	private Pathway pw = null;
	private FileWriter fwriter;

	private String places = "", transitions = "";
	private int connector = 0;
	private double xmin = 1000, xmax = -1000, ymin = 1000, ymax = -1000;
	private double scale = 1;
	private HashMap<String, String> edgesString = new HashMap<String, String>();
	private Hashtable<String, Point2D> placePositions = new Hashtable<String, Point2D>();
	private Hashtable<String, Point2D> transitionPositions = new Hashtable<String, Point2D>();
	private Hashtable<String, String> nodeType = new Hashtable<String, String>();
	private Hashtable<String, String> nameTable = new Hashtable<String, String>();
	private HashMap<String, Object> objectTable = new HashMap<String, Object>();
	private HashMap<String, String> vertex2name = new HashMap<String, String>();

	public GONoutput(File file, Pathway pathway) {
		this.file = file;
		this.pw = pathway;
		try {
			write();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void write() throws IOException {

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

	@SuppressWarnings("unchecked")
	private void prepare() {
		Iterator it = pw.getAllNodes().iterator();
		while (it.hasNext()) {
			BiologicalNodeAbstract bna = (BiologicalNodeAbstract) it.next();
			Point2D p = pw.getGraph().getClusteringLayout().getLocation(
					bna.getVertex());
			String biologicalElement = bna.getBiologicalElement();

			if(bna instanceof Transition){
				transitionPositions.put(bna.getName(), p);
			}else{
				placePositions.put(bna.getName(), p);
			}
			objectTable.put(bna.getName(), bna);
			
			nodeType.put(bna.getName(), biologicalElement);
			nameTable.put(bna.getName(), bna.getLabel());
			this.vertex2name.put(bna.getVertex().toString(), bna.getName());
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

	@SuppressWarnings("unchecked")
	private void buildConnections() {

		Iterator it = pw.getAllEdges().iterator();

		while (it.hasNext()) {
			BiologicalEdgeAbstract bna = (BiologicalEdgeAbstract) it.next();

			// get name and position and type
			String fromString = this.vertex2name.get(bna.getEdge().getEndpoints().getFirst()
					.toString());
			String toString = this.vertex2name.get(bna.getEdge().getEndpoints().getSecond()
					.toString());
			String first = this.nodeType.get(fromString);
			String type = "";
			String transition = "";
			String place = "";
			if (first.contains("Place")) {
				type = "InputProcessBiological";
				transition = toString;
				place = fromString;
			} else if (first.contains("Transition")) {
				type = "OutputProcessBiological";
				transition = fromString;
				place = toString;
			}
			//System.out.println("T: " + transition + " p: " + place + " type: "
				//	+ type);
			edge(transition, place, type);
			// edge(fromString, "A"+addedNodes, type);

		}
	}

	private void edge(String transition, String place, String type) {
		Point2D fromPoint = transitionPositions.get(transition);
		Point2D toPoint = placePositions.get(place);
		String connection = getConnectionString(place, type, fromPoint, toPoint);
		if (!edgesString.containsKey(transition)) {
			edgesString.put(transition, connection);
		} else {
			edgesString.put(transition, edgesString.get(transition) + connection);
		}
	}

	private void buildNodes() {
		Iterator<String> it = placePositions.keySet().iterator();
		while (it.hasNext()) {
			String nodeName = it.next();
			//String type = nodeType.get(nodeName);
			places = places.concat(getPlaceString(nodeName));
		}
		
		Iterator<String> it2 = transitionPositions.keySet().iterator();
		while(it2.hasNext()){
			String nodeName = it2.next();
			transitions = transitions.concat(getTransitionString(nodeName));
		}
	}

	private String getPlaceString(String name) {
		Point2D p = placePositions.get(name);
		String type = "";
		Place place = (Place) objectTable.get(name);
		
		//System.out.println(this.nodeType.get(name));
		if(this.nodeType.get(name).equals(Elementdeclerations.place)){
			type = "Integer";
		}else if(this.nodeType.get(name).equals(Elementdeclerations.s_place)){
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
				+ name
				+ "\" name=\""
				+ name
				+ "\" type=\"cso:-\">\r\n"
				+

				"<csml:entitySimulationProperty>\r\n"
				+
				" <csml:variable type = \"csml-variable:"+type+"\" variableID=\""
				+ name
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

	private String getTransitionString(String name) {
		return getTransitionHeader(name)
				+ (edgesString.get(name) == null ? "" : edgesString.get(name))
				// +(outEdgesString.get(name)==null?"":outEdgesString.get(name))
				+ getTransitionTail(transitionPositions.get(name));
	}

	private String getTransitionHeader(String name) {
		String type = "";
		Object o = objectTable.get(name);
		if(o instanceof DiscreteTransition){
			type = "Discrete";
		}else if (o instanceof ContinuousTransition){
			type = "Continuous";
		}else if (o instanceof StochasticTransition){
			type = "Stochastic";
		}
		
		return "  <csml:process id=\"" + name + "\" name=\""
				+ (nameTable.get(name) == null ? " " : nameTable.get(name))
				//TODO Transitiontype Discrete vs Continuous
				+ "\" type=\""+type+"\">\r\n";
	}

	private String getConnectionString(String place, String type,
			Point2D fromPoint, Point2D toPoint) {
		return "<csml:connector id=\"c"
				+ (++connector)
				+ "\" name=\"c"
				+ connector
				+ "\" refID=\""
				+ place
				+ "\" type=\""
				+ type
				+ "\">\r\n"
				+ "<csml:connectorSimulationProperty>\r\n"
				+ " <csml:connectorFiring connectorFiringStyle=\"csml-connectorFiringStyle:threshold\" value=\"0\">\r\n"
				+ " </csml:connectorFiring>\r\n"
				+ " <csml:connectorKinetic>\r\n"
				+ "  <csml:parameter key=\"stoichiometry\" value=\"1.0\">\r\n"
				+ "  </csml:parameter>\r\n"
				+ "  <csml:parameter key=\"custom\" value=\"1.0\">\r\n"
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
