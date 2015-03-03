package io.graphML;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.io.GraphMLWriter;
import graph.GraphInstance;

public class SaveGraphML {

	public SaveGraphML(OutputStream os) {

		GraphMLWriter<BiologicalNodeAbstract, BiologicalEdgeAbstract> writer = new GraphMLWriter<>();
		//GraphMLFile writer = new GraphMLFile();
		//GraphInstance instance = new GraphInstance();
		//BufferedWriter out;
		try {
			//out = new OutputStreamWriter(out);
			//out = new BufferedWriter(new FileWriter(file));
			writer.save(GraphInstance.getMyGraph().getJungGraph(), new OutputStreamWriter(os));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
	}
}
