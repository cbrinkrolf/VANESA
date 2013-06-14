package io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.graph.Edge;

public class PhosphoTextWriter {

	public PhosphoTextWriter(File file, Pathway pw) {

		HashSet nodes = new HashSet();
		int counter = 0;

		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(file));

			StringBuffer buff = new StringBuffer();
			buff.append("#Nodes \n");
			Iterator it = pw.getAllNodes().iterator();

			while (it.hasNext()) {
				BiologicalNodeAbstract bna = (BiologicalNodeAbstract) it.next();
				if (!nodes.contains(bna.getLabel())) {
					buff.append(bna.getLabel() + " \n");
					nodes.add(bna.getLabel());
					counter++;
				}
			}
			buff.append("#Edges \n");

			Iterator it2 = pw.getAllEdges().iterator();
			while (it2.hasNext()) {
				Edge edge = ((BiologicalEdgeAbstract) it2.next()).getEdge();

				String from = ((BiologicalNodeAbstract) pw
						.getNodeByVertexID(edge.getEndpoints().getFirst()
								.toString())).getLabel();

				String to = ((BiologicalNodeAbstract) pw.getNodeByVertexID(edge
						.getEndpoints().getSecond().toString())).getLabel();
				buff.append(from + ";" + to + " \n");
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
			out.write(buff.toString());

			out.close();
		} catch (IOException e) {
		}
	}
}
