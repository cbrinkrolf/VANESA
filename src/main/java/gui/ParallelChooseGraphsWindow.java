package gui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import org.mediavirus.parvis.gui.MainFrame;

import biologicalElements.Pathway;
import configurations.Workspace;
import graph.algorithms.NetworkProperties;

public class ParallelChooseGraphsWindow extends ChooseGraphsWindow {

	public ParallelChooseGraphsWindow() {
		super("Create Parallel Coordinate Plot");
	}

	@Override
	public void handleChosenGraphs(ArrayList<Pathway> pathways) {
		// Create datafile in working directory
		// Iterate over Paths
		// Create PARVIS from File

		StringBuilder data = new StringBuilder();
		data.append("13\n");
		data.append("Nodes Integer\n");
		data.append("Edges Integer\n");
		data.append("NodeDegs Integer\n");
		data.append("MinD Integer\n");
		data.append("MaxD Integer\n");
		data.append("AvgDeg real\n");
		data.append("AvgNeDeg real\n");
		data.append("AvgSP real\n");
		data.append("MaxPath Integer\n");
		data.append("Dens real\n");
		data.append("Centr real\n");
		data.append("MI real\n");
		data.append("Name string\n");

		Pathway p;
		NetworkProperties c;
		String name;
		for (int i = 0; i < pathways.size(); i++) {
			p = pathways.get(i);
			c = new NetworkProperties(p);
			name = p.getName();
			// format Name, no spaces, no brackets
			name = name.replace('(', ' ');
			name = name.replace(')', ' ');
			name = name.replace(" ", "");
			// System.out.println(name);

			data.append(p.countNodes());
			data.append("\t");
			data.append(p.countEdges());
			data.append("\t");
			data.append(c.countNodeDegrees());
			data.append("\t");
			data.append(c.getMinDegree());
			data.append("\t");
			data.append(c.getMaxDegree());
			data.append("\t");
			data.append(c.getAvgNodeDegree());
			data.append("\t");
			data.append(c.averageNeighbourDegree());
			data.append("\t");
			data.append(c.averageShortestPathLength());
			data.append("\t");
			data.append(c.maxPathLength());
			data.append("\t");
			data.append(c.getDensity());
			data.append("\t");
			data.append(c.getCentralization());
			data.append("\t");
			data.append(c.getGlobalMatchingIndex());
			data.append("\t");
			data.append(name);
			data.append("\t");

		}

		// WRITE TO LOCAL FILE
		String filename = "paralleldata.stf";
		File file = Workspace.getCurrent().resolve(filename).toFile();
		try (BufferedWriter out = new BufferedWriter(new FileWriter(file))) {
			out.write(data.toString());
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error on writing STFFile: " + e.getMessage());
		}

		// DEBUG
		MainFrame m = new MainFrame();
		m.setVisible(true);

		m.loadNewFile("file:" + file.getAbsolutePath());
	}
}
