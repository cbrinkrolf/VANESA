package io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Iterator;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
//import edu.uci.ics.jung.graph.Edge;

public class GraphTextWriter {

	public GraphTextWriter(OutputStream os, Pathway pw) {

		HashSet<String> nodes = new HashSet<String>();
		//int counter = 0;

		try {
			//BufferedWriter out = new BufferedWriter(file);

			StringBuffer buff = new StringBuffer();
			buff.append("#Nodes \n");
			Iterator<BiologicalNodeAbstract> it = pw.getAllGraphNodes().iterator();
			BiologicalNodeAbstract bna;
			while (it.hasNext()) {
				bna = it.next();
				if (!nodes.contains(bna.getLabel() + " # " + bna.getName())) {
					buff.append(bna.getLabel() + " # " + bna.getName() + " \n");
					nodes.add(bna.getLabel() + " # " + bna.getName());
					//counter++;
				}
			}
			
			buff.append("#Edges \n");
			BiologicalEdgeAbstract bioEdge;
			Iterator<BiologicalEdgeAbstract> it2 = pw.getAllEdges().iterator();
			while (it2.hasNext()) {
				bioEdge = it2.next();
				//Edge edge = bioEdge.getEdge();
				boolean isdirected = bioEdge.isDirected();
				String from = bioEdge.getFrom().getLabel();

				String to = bioEdge.getTo().getLabel();
				if (isdirected) {
					buff.append(from + ";" + to + ";" + "true" + " \n");
				}
				else {
					buff.append(from + ";" + to + ";" + "false" + " \n");
				}
				
			}
			
			buff.append("\n");

			// buff.append("#Sequences \n");
			// Iterator it3 = pw.getAllNodes().iterator();
			//
			// while (it3.hasNext()) {
			// Protein bna = (Protein) it3.next();
			// if (bna.getAaSequence().length() > 10) {
			// buff.append(">" + bna.getLabel() + " \n");
			// buff.append(bna.getAaSequence() + " \n");
			// buff.append("\n");
			// }
			// }
			// buff.append("\n");
			os.write(buff.toString().getBytes());

			os.close();
		} catch (IOException e) {
		}
	}
}
