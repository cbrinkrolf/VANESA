package io.graphML;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.io.GraphMLWriter;
import graph.GraphInstance;

public class SaveGraphML {

	public SaveGraphML(File file) {

		GraphMLWriter<BiologicalNodeAbstract, BiologicalEdgeAbstract> writer = new GraphMLWriter<>();
		//GraphMLFile writer = new GraphMLFile();
		//GraphInstance instance = new GraphInstance();
		BufferedWriter out;
		try {
			out = new BufferedWriter(new FileWriter(file));
			writer.save(GraphInstance.getMyGraph().getJungGraph(), out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
	}
}
