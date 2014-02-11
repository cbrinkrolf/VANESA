package gui;

import graph.ContainerSingelton;
import graph.GraphContainer;
import graph.algorithms.NetworkProperties;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;

import org.mediavirus.parvis.gui.MainFrame;

import biologicalElements.Pathway;

public class ParallelChooseGraphsWindow extends ChooseGraphsWindow {

	private static final long serialVersionUID = -1783016244194648150L;


	public ParallelChooseGraphsWindow() {
		super("Create Parallel Coordinate Plot");
	}
	
	
	@Override
	public void handleChosenGraphs(ArrayList<Pathway> pathways) {
		//Create datafile in working directory
			//Iterate over Paths
		//Create PARVIS from File
		

        String data = "13\n" +
        "Nodes Integer\n" +
        "Edges Integer\n" +
		"NodeDegs Integer\n" +
		"MinD Integer\n" +
		"MaxD Integer\n" +	
		"AvgDeg real\n" +
		"AvgNeDeg real\n" +
		"AvgSP real\n" +
		"MaxPath Integer\n" +
		"Dens real\n" +
		"Centr real\n" +
		"MI real\n" +
		"Name string\n";
		
		Pathway p; 
		NetworkProperties c;
		String name;
		for (int i = 0; i < pathways.size(); i++) {
			p = pathways.get(i);
			c = new NetworkProperties(p.getName());
			name = p.getName();
			//format Name, no spaces, no brackets
			name = name.replace('(', ' ');
			name = name.replace(')', ' ');
			name = name.replace(" ", "");
		//	System.out.println(name);
			
			data += p.countNodes() + "\t" +
			p.countEdges() + "\t" +
			c.countNodeDegrees() + "\t" +
			c.getMinDegree() + "\t" +
			c.getMaxDegree() + "\t" +
			c.getAvgNodeDegree() + "\t" +
			c.averageNeighbourDegree() + "\t" +
			c.averageShortestPathLength() + "\t" +
			c.maxPathLength() + "\t" +
			c.getDensity() + "\t" +
			c.getCentralization() + "\t" +
			c.getGlobalMatchingIndex()+"\t" +
			name+"\n";
			
		}
		
		// WRITE TO LOCAL FILE

		try {
			// Create file
			FileWriter fstream = new FileWriter("paralleldata.stf");
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(data);
			// Close the output stream
			out.close();
			fstream.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error on writing STFFile: " + e.getMessage());
		}

		// DEBUG
		MainFrame m = new MainFrame();
		m.setVisible(true);

		String filename = "paralleldata.stf";

		String curDir = System.getProperty("user.dir");
		m.loadNewFile("file:" + curDir + "/" + filename);

	}
}
