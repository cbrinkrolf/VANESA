package io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import petriNet.Place;
import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class CSVWriter {

	public CSVWriter(File file, Pathway pw) {

		List<BiologicalNodeAbstract> nodes = pw
				.getAllNodesSortedAlphabetically();
		List<Place> places = new ArrayList<Place>();
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(file));

			StringBuffer buff = new StringBuffer();
			Iterator<BiologicalNodeAbstract> it = nodes.iterator();
			BiologicalNodeAbstract bna;
			buff.append("Time;");
			while (it.hasNext()) {
				bna = it.next();
				if (bna instanceof Place && !bna.hasRef()) {
					buff.append(bna.getName() + ";");
					places.add((Place) bna);
				}
			}
			buff.append("\r\n");
			Iterator<Place> itPlace;
			for (int i = 0; i < pw.getPetriNet().getTime().size(); i++) {
				buff.append(pw.getPetriNet().getTime().get(i) + ";");
				itPlace = places.iterator();
				while (itPlace.hasNext()) {
					bna = itPlace.next();
					//System.out.println(bna.getName());
					buff.append(bna.getPetriNetSimulationData().get(i) + ";");
				}
				buff.append("\r\n");
			}

			out.write(buff.toString());

			out.close();
		} catch (IOException e) {
		}
	}
}
