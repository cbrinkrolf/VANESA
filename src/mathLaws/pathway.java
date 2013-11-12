/*package mathLaws;

import java.util.Hashtable;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Vertex;

import graph.CreatePathway;
import graph.jung.classes.MyGraph;
import gui.MainWindow;
import gui.MainWindowSingelton;
import biologicalElements.InternalGraphRepresentation;
import biologicalElements.Pathway;
import biologicalObjects.edges.Expression;
import biologicalObjects.nodes.Enzyme;
import biologicalObjects.nodes.Reaction;

public class pathway {

	private MyGraph myGraph;
	private Pathway pw;
	
	public pathway() {
		
		pw =new CreatePathway("Mathematical Graph").getPathway();
		pw.setOrganism("");
		pw.setLink("");
		pw.setImagePath("");
		pw.setNumber("");
		myGraph = pw.getGraph();
		
		stopVisualizationModel();
		drawNodes();
		startVisualizationModel();
		
		myGraph.normalCentering();
		pw.getGraph().fitScaleOfViewer(pw.getGraph().getSatelliteView());
		pw.getGraph().normalCentering();
		myGraph.fitScaleOfViewer(myGraph.getVisualizationViewer());
		myGraph.fitScaleOfViewer(myGraph.getSatelliteView());
		myGraph.normalCentering();
		
		MainWindow window = MainWindowSingelton.getInstance();
		window.updateOptionPanel();
		window.enable(true);
		
		
	}
	
	private int[][] calculatePathwayMatrix(Integer maxNumber ){
		
		int[][] matrix = new int[maxNumber][maxNumber];
		int firstNumber = 1;
		int secondNumber = 1;
			
		for(int i=0; i< maxNumber; i++){
			firstNumber = i+1;
			
			for(int j =0; j< maxNumber; j++){
				secondNumber = j+1;
				
				if(isEven(firstNumber)){
					if(isOdd(secondNumber) && firstNumber+secondNumber<=maxNumber){
						matrix[i][j]=1;
					}else{
						matrix[i][j]=0;
					}
				}else{
					if(isEven(secondNumber) && firstNumber+secondNumber<=maxNumber){
						matrix[i][j]=1;
					}else{
						matrix[i][j]=0;
					}
				}
			}
		}			
		return matrix;
	}
	
	public static boolean isEven(int n) {
		return (n % 2) == 0;
	}
		
	public static boolean isOdd(int n) {
		return !isEven(n);
	}
	
	private void updateGraph() {
		myGraph.updateGraph();
	}

	private void stopVisualizationModel() {
		myGraph.lockVertices();
		myGraph.stopVisualizationModel();
	}

	private void startVisualizationModel() {
		myGraph.unlockVertices();
		myGraph.restartVisualizationModel();
	}
	
	public void drawNodes(){
		
		int maxnumber = 30;
		InternalGraphRepresentation igp = pw.getGraphRepresentation();
		Hashtable<String,Vertex> mapping = new Hashtable<String, Vertex>();
		
		
		for(int i=1; i<= maxnumber; i++){
			Enzyme e = new Enzyme(""+i,""+i,myGraph.createNewVertex());
			pw.addElement(e);
			myGraph.moveVertex(e.getVertex(), 11, 11);
			mapping.put(""+i, e.getVertex());
		}
		
		int[][] matrix = calculatePathwayMatrix(maxnumber);
		
		for(int i=0; i< maxnumber; i++){	
			for(int j =0; j< maxnumber; j++){
				if(matrix[i][j]==1){
					int first = i+ 1;
					int second = j +1;
					int result = first+second;
				//	System.out.println(first+" " + second+ "=" +result);
					
					if(!igp.doesEdgeExist(mapping.get(""+first), mapping.get(""+result))){
						Edge edge = myGraph.createEdge(mapping.get(""+first), mapping.get(""+result), false);
						Expression e = new Expression(edge, "", "");
						pw.addElement(e);
					}
					
					if(!igp.doesEdgeExist(mapping.get(""+second), mapping.get(""+result))){
						Edge edge = myGraph.createEdge(mapping.get(""+first), mapping.get(""+result), false);
						Expression e = new Expression(edge, "", "");
						pw.addElement(e);
					}
					
				}
			}
		}		
		
	}
}*/
