package graph.algorithms;

import graph.CreatePathway;
import gui.HeatgraphLayer;
import gui.MainWindowSingelton;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import javax.xml.stream.XMLStreamException;

import xmlInput.sbml.VAMLInput;
import xmlOutput.sbml.VAMLoutput;
import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class HeatmapGraphs {
	
	public HeatmapGraphs(Pathway one, Pathway two)  {
		this.buildHeatmap(new Pathway[] { one, two });
	}
	
	
	public HeatmapGraphs(Pathway[] pathways)  {
		this.buildHeatmap(pathways);
	}
	
	
	public HeatmapGraphs(ArrayList<Pathway> pathways) {
		this.buildHeatmap(pathways.toArray(new Pathway[] {}));
	}


	/** append elements from one pathway to another
	 * (will not delete elements, that are allready in $to)
	 * @param from
	 * @param to
	 */
	private void copyElements(Pathway from, Pathway to) {
		File file1 = new File("test");
		try {
			new VAMLoutput(file1, from);
			new VAMLInput(file1, to);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/** build the merged graph and the heatgraph-layer for it */
	private void buildHeatmap(Pathway[] pathways) {
		
		String title = "";
		for(Pathway pw : pathways) {
			title += pw.getTitle();
		}
		
		Pathway pw_new = new CreatePathway(title).getPathway();
		pw_new.setOrganism("");
		pw_new.setLink("");
		pw_new.getGraph().lockVertices();
		pw_new.getGraph().stopVisualizationModel();

		for(Pathway pw : pathways) {
			this.copyElements(pw, pw_new);
		}

		MainWindowSingelton.getInstance().updateProjectProperties();
		MainWindowSingelton.getInstance().updateOptionPanel();
		
		//count all nodes by id
		//elements that are in both graphs will be double in the new graph
		HashMap<String, Integer> countById = new HashMap<String, Integer>();
		Vector<BiologicalNodeAbstract> nodes = pw_new.getAllNodesAsVector();
		for(BiologicalNodeAbstract bna : nodes) {
			String id = bna.getName()+bna.getLabel();
			if (countById.containsKey(id)) {
				countById.put(id, countById.get(id)+1);
				
			}
			else {
				countById.put(id, 1);
			}
		}
		
		//set the form of vertices depending on counts
		for(BiologicalNodeAbstract bna : nodes) {
			String id = bna.getName()+bna.getLabel();
			countById.get(id);
			//bna.setShape(vs.getEllipse(getVertex()))
		}
		
		/*for(String s : countById.keySet()) {
			System.out.println(s+" = "+countById.get(s));
		}*/
		MainWindowSingelton.getInstance().enableOptionPanelUpdate(false);
		CompareGraphs.mergeGraph(pw_new);
		MainWindowSingelton.getInstance().enableOptionPanelUpdate(true);
		
		//pw_new.getGraph().changeToGEMLayout();
		pw_new.getGraph().unlockVertices();
		pw_new.getGraph().restartVisualizationModel();
		/*JOptionPane.showMessageDialog(null,
		"Heatmap ready"+countById.toString());*/
		
		
		
		HeatgraphLayer.getInstance().setCountDataForGraph(pw_new.getGraph(), countById);
		
		//center graph
		if (pw_new.hasGotAtLeastOneElement()) {
			pw_new.getGraph().normalCentering();
		}
		
		
		//pick all element to show the heatmap fully
		pw_new.getGraph().pickAllElements();
		
		pw_new.getGraph().normalCentering();
	}

}