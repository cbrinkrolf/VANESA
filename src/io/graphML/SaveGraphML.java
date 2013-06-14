package io.graphML;

import java.io.File;

import edu.uci.ics.jung.io.GraphMLFile;
import graph.GraphInstance;

public class SaveGraphML {

	public SaveGraphML(File file) {

		GraphMLFile writer = new GraphMLFile();
		GraphInstance instance = new GraphInstance();
		writer.save(GraphInstance.getMyGraph().getJungGraph(), file.getAbsolutePath());	
	
	}
}
