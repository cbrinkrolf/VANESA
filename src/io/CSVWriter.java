package io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import petriNet.Place;
import petriNet.SimulationResult;
import petriNet.SimulationResultController;
import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class CSVWriter {

	public CSVWriter(OutputStream os, Pathway pw) {

		List<BiologicalNodeAbstract> nodes = pw
				.getAllNodesSortedAlphabetically();
		List<Place> places = new ArrayList<Place>();
		try {

			SimulationResult simRes = pw.getSimResController().get();
			if (simRes != null) {
				// BufferedWriter out = new BufferedWriter(new
				// FileWriter(file));

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

				for (int i = 0; i < simRes.getTime().size(); i++) {
					buff.append(simRes.getTime().get(i) + ";");
					itPlace = places.iterator();
					while (itPlace.hasNext()) {
						bna = itPlace.next();
						// System.out.println(bna.getName());
						buff.append(simRes.get(bna, SimulationResultController.SIM_TOKEN).get(i)
								+ ";");
					}
					buff.append("\r\n");
				}

				os.write(buff.toString().getBytes());

				os.close();
			}
		} catch (IOException e) {
		}
	}
}
