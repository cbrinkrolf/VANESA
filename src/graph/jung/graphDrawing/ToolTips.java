package graph.jung.graphDrawing;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import petriNet.Place;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import graph.GraphContainer;
import graph.ContainerSingelton;
import graph.GraphInstance;
import gui.MainWindow;
import gui.MainWindowSingelton;

/*public class ToolTips extends DefaultToolTipFunction {

	MainWindow w = MainWindowSingelton.getInstance();
	GraphContainer con = ContainerSingelton.getInstance();
	
	@Override
	public String getToolTipText(Vertex v) {
		String text="<html><h3>Summary</h3>";
		String t = "";
		GraphInstance gi = new GraphInstance();
		int currentTimeStep = gi.getPathway().getPetriNet()
				.getCurrentTimeStep();
		if (con.getPathway(w.getCurrentPathway()).getElement(v).getClass()
				.getName().equals("petriNet.Place")) {
			Place p = (Place) con.getPathway(w.getCurrentPathway()).getElement(
					v);
			text+="Vertex: "+p.getVertex().toString()+"<br>";
			text+="Name: "+p.getName()+"<br>";
			String tmp = "";
			if(p.isDiscrete()){
				tmp = "Discrete";
			}
			else{
				tmp = "Continuous";
			}
			text+="Element Property: "+tmp+" Place<br>";
			text+="Initial Value: "+p.getTokenStart()+"<br>";
			//text+="<li>Tokens now: "+p.getToken()+"</li>";

			String prev = null;
			String current = null;
			String next = null;
			int pre = 0;
			int nex = 0;
			if (p.getPetriNetSimulationData().size() > currentTimeStep) {
				current = ""+p.getMicroArrayValue(currentTimeStep);
				if(currentTimeStep == 0){
					prev = ""+p.getMicroArrayValue(0);
				}
				else{
					prev = ""+p.getMicroArrayValue(currentTimeStep-1);
					pre = currentTimeStep-1;
				}
				if(currentTimeStep == p.getPetriNetSimulationData().size()-1){
					next = ""+p.getMicroArrayValue(currentTimeStep);
					nex = currentTimeStep;
				}
				else{
					next = ""+p.getMicroArrayValue(currentTimeStep+1);
					nex = currentTimeStep+1;
				}
				text+="Timestep: "+currentTimeStep;
				text+="<br><br><table border=1><tr><td>prev<br>TS: "+ pre +"</td><th>now<br>TS: "+currentTimeStep+"</th><td>next<br>TS: "+nex +"</td></tr>";
				//text+="Token at Timestep "+currentTimeStep+": "+current+"</li>";
				//text+="<li>Token at prev. TS: "+prev+"</li>";
				//text+="<li>Token at next TS: "+next+"</table>";
				text+="<tr><td>"+prev+"</td><td>"+current+"</td><td>"+next+"</td></tr>";
			}
			//text = "t_0: " + p.getToken() + t+"</ul></html>";
			text+="</table></html>";

		} else {
			BiologicalNodeAbstract ab = (BiologicalNodeAbstract) con
					.getPathway(w.getCurrentPathway()).getElement(v);
			text = ab.getBiologicalElement() + ": " + ab.getName();
		}
		return text;
	}

	@Override
	public String getToolTipText(Edge edge) {

		String text = "";
		if (edge.containsUserDatumKey("alignment")) {
			DecimalFormat format = new DecimalFormat("#.###");
			format.setRoundingMode(RoundingMode.DOWN);
			text = "Alignment score: "
					+ format.format(edge.getUserDatum("alignment"));

			if (edge.containsUserDatumKey("eValue")) {
				format.setRoundingMode(RoundingMode.DOWN);
				text += " || -log(eValue): "
						+ format.format(edge.getUserDatum("eValue"));
			}

		} else {
			Pair points = edge.getEndpoints();
			Vertex a = (Vertex) points.getFirst();
			Vertex b = (Vertex) points.getSecond();

			BiologicalNodeAbstract ab = (BiologicalNodeAbstract) con
					.getPathway(w.getCurrentPathway()).getElement(a);
			String v_a = ab.getBiologicalElement() + ": " + ab.getName() + "\n";

			BiologicalNodeAbstract ab2 = (BiologicalNodeAbstract) con
					.getPathway(w.getCurrentPathway()).getElement(b);
			String v_b = ab2.getBiologicalElement() + ": " + ab2.getName()
					+ "\n";

			BiologicalEdgeAbstract eb = (BiologicalEdgeAbstract) con
					.getPathway(w.getCurrentPathway()).getElement(edge);
			text = eb.getBiologicalElement() + ": " + eb.getName();

			if (eb.isDirected()) {
				text = text + " From " + v_a;
				text = text + " To " + v_b;
			} else {
				text = text + "Vertex A: " + v_a;
				text = text + "Vertex B: " + v_b;
			}
		}

		return text;
	}

	public String getToolTipText(Place p) {
		return "" + p.getToken();
	}
}*/
