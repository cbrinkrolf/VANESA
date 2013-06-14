package biologicalElements;

import graph.GraphInstance;
import gui.MainWindowSingelton;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

import javax.swing.JOptionPane;

import petriNet.CSVInputReader;
import petriNet.PNResultInputReader;
import petriNet.Place;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class PetriNet {
	private GraphInstance graphInstance = null;
	private Pathway pw;
	private String petriNetSimulationFile = null;
	private PNResultInputReader pnrir = new PNResultInputReader();
	private CSVInputReader cvsReader = new CSVInputReader();
	private HashMap<String, Vector<Double>> pnResult = null;
	private int places=0;
	private int resultDimension=0;
	private int currentTimeStep = 0;
	private String covGraph;

	public String getCovGraph() {
		return this.covGraph;
	}
	
	public void setCovGraph(String covGraph){
		this.covGraph = covGraph;
	}

	public int getCurrentTimeStep() {
		return currentTimeStep;
	}

	public void setCurrentTimeStep(int currentTimeStep) {
		this.currentTimeStep = currentTimeStep;
	}

	public PetriNet() {
		// TODO Auto-generated constructor stub
	}

	public String getPetriNetSimulationFile() {
		return petriNetSimulationFile;
	}

	public void setPetriNetSimulationFile(String petriNetSimulationFile) {
		this.petriNetSimulationFile = petriNetSimulationFile;
	}

	public HashMap<String, Vector<Double>> getPnResult() {
		return this.pnResult;
	}

	public void initializePetriNet() {
				
		graphInstance = new GraphInstance();
		pw = graphInstance.getPathway();
		HashSet<GraphElementAbstract> hs = pw.getAllNodes();
		pnResult = pw.getPetriNet().getPnResult();
		ArrayList<String> placeNames = new ArrayList<String>();
		// rowsSize = 0;
		Object elem;
		BiologicalNodeAbstract bna;
		if (hs != null) {
			Iterator<GraphElementAbstract> it = hs.iterator();
			while (it.hasNext()) {
				elem = it.next();
				bna = (BiologicalNodeAbstract) elem;
				if (bna instanceof Place) {
					placeNames.add("P"+bna.getID());
					// System.out.println(bna.getName());
				}
			}
		}
		// System.out.println(placeNames);
		// placeNames.add("t1.activation.minTokens[1]");
		try {
			String ext = "";

			int i = this.petriNetSimulationFile.lastIndexOf('.');

			if (i > 0 && i < this.petriNetSimulationFile.length() - 1) {
				ext = this.petriNetSimulationFile.substring(i + 1)
						.toLowerCase();
			}
			// System.out.println("endung: "+ext);
			if (ext.equals("csv")) {
				this.pnResult = this.cvsReader.readResult(
						this.petriNetSimulationFile, placeNames);
				
				//Iterator it = this.pnResult.
				
				this.setDataToNodes();
				
			} else if (ext.equals("plt")) {
				this.pnResult = this.pnrir
						.readResult(getPetriNetSimulationFile());
				this.setDataToNodes();
			} else if (ext.length() == 0) {
				System.out.println("Dateiname fehlerhaft");
			}

		} catch (Exception e) {
			// System.out.println("da ist was kaputt");
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.deleteDateFromNodes();
			JOptionPane.showMessageDialog(MainWindowSingelton.getInstance(),
					"Result file does not fit on Graph!");

		}

	}

	private void setDataToNodes() throws Exception {
		places=0;
		ArrayList<Color> colors=new ArrayList<Color>();
		colors.add(new Color(0, 0, 0));
		colors.add(new Color(255, 0, 0));
		colors.add(new Color(128, 0, 0));
		colors.add(new Color(0, 255, 0));
		colors.add(new Color(0, 128, 0));
		colors.add(new Color(0, 0, 255));
		colors.add(new Color(0, 0, 128));
		colors.add(new Color(255, 255, 0));
		colors.add(new Color(255, 0, 255));
		colors.add(new Color(0, 255, 255));
		colors.add(new Color(065, 105, 225));
		colors.add(new Color(124, 252, 000));
		colors.add(new Color(178, 034, 034));
		colors.add(new Color(160, 032, 240));
		colors.add(new Color(000, 255, 127));
		colors.add(new Color(255, 127, 000));
		colors.add(new Color(000, 100, 000));
		Random r=new Random();
		
		graphInstance = new GraphInstance();
		Pathway pw = graphInstance.getPathway();
		HashSet<GraphElementAbstract> hs = pw.getAllNodes();
		// pnResult = pw.getPetriNet().getPnResult();
		ArrayList<Integer> count = new ArrayList<Integer>();
		// rowsSize = 0;
		//System.out.println("size: "+ pnResult.size());
		if (hs != null) {
			Iterator<GraphElementAbstract> it = hs.iterator();
			BiologicalNodeAbstract bna;
			while (it.hasNext()) {
				Object elem = it.next();
				bna = (BiologicalNodeAbstract) elem;
				if (bna instanceof Place) {
					((Place)bna).setPlotColor(colors.get(r.nextInt(colors.size())));
					int intSimID = bna.getID();
					// if (bna.getPetriNetSimulationData().size() == 0) {
					// System.out.println(intSimID);
					// System.out.println("result: "+pnResult);
					//System.out.println(pnResult.keySet().toString());
					//System.out.println("Hallo erster Test zum abfragen");
					//System.out.println("P"+bna.getID());
					if(pnResult.containsKey("P"+bna.getID())) {
						//System.out.println("drin");
					}
					else {
						//System.out.println("n�");
					}
					
					System.out.println();
					
					
					Vector<Double> v = pnResult.get("P"+bna.getID());
					// System.out.println(bna.getName());
					// System.out.println("size: "+v.size());
					// System.out.println("test2");
				if (v.get(0).doubleValue()!=((Place)bna).getTokenStart())
						throw new Exception("A startToken value in the petri net does not fit the result!"); 
					bna.setPetriNetSimulationData(v);
					//System.out.println(bna.getName());
					//System.out.println(v.size());
					
					count.add(new Integer(v.size()));
					// System.out.println("ende");
					this.places++;
				}
			}

			it = hs.iterator();
			Place p;
			Object o;
			while (it.hasNext()) {
				o = it.next();
				if (o instanceof Place) {
					p = (Place) o;
					// System.out.println(p.getName());
					// System.out.println(p.getPetriNetSimulationData());
				}
			}

			Iterator it2 = count.iterator();
			int tmp = (Integer) it2.next();
			while (it2.hasNext()) {
				if ((Integer) it2.next() != tmp) {
					// System.out.println("zu wenig");
					throw new Exception();
				} else {
					this.resultDimension = tmp;
				}
			}
			pw.setPetriNetSimulation(true);
		}

	}

	private void deleteDateFromNodes() {
		graphInstance = new GraphInstance();
		Pathway pw = graphInstance.getPathway();
		HashSet<GraphElementAbstract> hs = pw.getAllNodes();
		if (hs != null) {
			Iterator<GraphElementAbstract> it = hs.iterator();
			while (it.hasNext()) {
				Object elem = it.next();
				BiologicalNodeAbstract bna = (BiologicalNodeAbstract) elem;
				if (bna instanceof Place) {
					Place p = (Place) bna;
					// if (p.getPetriNetSimulationData() != null) {
					p.setPetriNetSimulationData(new Vector<Double>());
					// }
					// System.out.println(p.getPetriNetSimulationData().size());
				}

			}
		}
		//pw.setPetriNet(false);
		this.setPetriNetSimulationFile(null);
	}

	public int getNumberOfPlaces() {
		return this.places;
	}

	public int getResultDimension() {
		return this.resultDimension;
	}

}
