package graph.algorithms.gui.smacof;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import biologicalObjects.nodes.BiologicalNodeAbstract;
import graph.ContainerSingelton;
import graph.GraphContainer;
import graph.GraphInstance;
import graph.algorithms.gui.smacof.algorithms.Dissimilarities;
import graph.algorithms.gui.smacof.algorithms.Smacof;
import graph.algorithms.gui.smacof.datastructures.Mat;
import gui.MainWindow;
import gui.MainWindowSingleton;

public class DoSmacof extends Thread {
	
	// Die Daten des BiologicalNodeAbstract koennen ueber die ID der HashMaps
	// identifiziert werden: mapped_nodes und smacof_data_map.
	private final HashMap<Integer, double[]> smacof_data_map;
	private final HashMap<BiologicalNodeAbstract, Integer> mapped_nodes;
	private final Dissimilarities dis;
	private final int maxiter;
	private final double epsilon;
	private final int p;
	private final int resultdim;

	public DoSmacof(HashMap<Integer, double[]> data_map,
			HashMap<BiologicalNodeAbstract, Integer> mapped_nodes,
			String dis,
			int maxiter,
			double epslion,
			int p,
			int resultdim) {
		
		this.smacof_data_map = data_map;
		this.mapped_nodes = mapped_nodes;
		this.maxiter = maxiter;
		this.epsilon = epslion;
		this.p = p;
		this.resultdim = resultdim;
		// look up the dissimilarity measure
		switch(dis) {
        case "NONE":
            this.dis = Dissimilarities.NONE;
            break;
        case "EUCLIDEAN":
            this.dis = Dissimilarities.EUCLIDEAN;
            break;
        case "MAHALANOBIS":
        	this.dis = Dissimilarities.MAHALANOBIS;
            break;
        case "CANBERRA":
        	this.dis = Dissimilarities.CANBERRA;
            break;
        case "DIVERGENCE":
        	this.dis = Dissimilarities.DIVERGENCE;
            break;
        case "BRAY_CURTIS":
        	this.dis = Dissimilarities.BRAY_CURTIS;
            break;
        case "SOERGEL":
        	this.dis = Dissimilarities.SOERGEL;
            break;
        case "BAHATTACHARRYA":
        	this.dis = Dissimilarities.BAHATTACHARYYA;
            break;
        case "WAVE_HEDGES":
        	this.dis = Dissimilarities.WAVE_HEDGES;
            break;
        case "ANGULAR_SEPERATION":
        	this.dis = Dissimilarities.ANGULAR_SEPERATION;
            break;
        case "CORRELATION":
        	this.dis = Dissimilarities.CORRELATION;
            break;
        case "MINKOWSKI":
        	this.dis = Dissimilarities.MINKOWSKI;
            break;
        default:
        	this.dis = Dissimilarities.EUCLIDEAN;
            break;
		}
	}
	
	@Override public void run() {
	   	

	   
	   Smacof smac = new Smacof();
	   Mat result_mat = smac.smacofAlgorithm(this.smacof_data_map,
			   this.maxiter,
			   this.epsilon,
			   this.resultdim,
			   this.dis,
			   this.p,
			   false);
	   
	   System.out.println("Result_Mat:");
	   System.out.println(result_mat);
	   
	   // Skalierung der Ergebnisse um (hoffentlich) besser Darstellung zu erhalten
	   // es muss noch auf sinnvolle Weise der Skalierungsfaktor automatisch bestimmt werden!
	   for (int i = 0; i < result_mat.getFirstDimSize(); i++) {
		   for (int j = 0; j < result_mat.getSecondDimSize(); j++) {
			   result_mat.setElement(i, j, (double) Math.round(Math.scalb(result_mat.getElement(i, j), 10)));
		   }
	   }
		
	   
	   // In result_mat und result_map stehen die Ergebnisse
	   // hier werden die Ergebnisse als neue Koordinaten der Knoten gesetzt
	   GraphContainer con = ContainerSingelton.getInstance();
	   HashMap<BiologicalNodeAbstract, Point2D> vertices = con.getPathway(MainWindowSingleton.getInstance().getCurrentPathway()).getVertices();
	   BiologicalNodeAbstract bna;
	   Point2D point = null;
	   int id;
	   Iterator<BiologicalNodeAbstract> it = con.getPathway(MainWindowSingleton.getInstance().getCurrentPathway()).getAllGraphNodes().iterator();
		while (it.hasNext()) {
			bna = it.next();
			id = mapped_nodes.get(bna);
			if(!vertices.containsKey(bna)){
				for(BiologicalNodeAbstract child : bna.getVertices().keySet()){
					point = vertices.get(child);
					if (result_mat.getSecondDimSize() == 1) {
						point.setLocation(result_mat.getColumn(id)[0], 0);
					} else if (result_mat.getSecondDimSize() == 2) {
						point.setLocation(result_mat.getColumn(id)[0], result_mat.getColumn(id)[1]);
						System.out.println("ID:"+id+" "+vertices.get(bna));
					}
				}
			} else {
				point = vertices.get(bna);
				if (result_mat.getSecondDimSize() == 1) {
					point.setLocation(result_mat.getColumn(id)[0], 0);
				} else if (result_mat.getSecondDimSize() == 2) {
					point.setLocation(result_mat.getColumn(id)[0], result_mat.getColumn(id)[1]);
					System.out.println("ID:"+id+" "+vertices.get(bna));
				}
			}

		}
		
		con.getPathway(MainWindowSingleton.getInstance().getCurrentPathway()).getGraph().getVisualizationViewer().repaint();
		con.getPathway(MainWindowSingleton.getInstance().getCurrentPathway()).updateMyGraph();
	   	   
		MainWindow w = MainWindowSingleton.getInstance();
		w.closeProgressBar();
	  }
}
