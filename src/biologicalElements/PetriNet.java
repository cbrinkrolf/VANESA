package biologicalElements;

import graph.GraphInstance;
import gui.MainWindowSingelton;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JOptionPane;

import petriNet.CSVInputReader;
import petriNet.PNEdge;
import petriNet.PNResultInputReader;
import petriNet.Place;
import petriNet.Transition;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class PetriNet {
	private GraphInstance graphInstance = null;
	private Pathway pw;
	private String petriNetSimulationFile = null;
	private PNResultInputReader pnrir = new PNResultInputReader();
	private CSVInputReader cvsReader = new CSVInputReader();
	private HashMap<String, Vector<Double>> pnResult = null;
	private int places = 0;
	private int transitions = 0;
	private int resultDimension = 0;
	private int currentTimeStep = 0;
	private String covGraph;
	private boolean omc = false;

	public String getCovGraph() {
		return this.covGraph;
	}

	public void setCovGraph(String covGraph) {
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

	public void setPetriNetSimulationFile(String petriNetSimulationFile,
			boolean omc) {
		this.petriNetSimulationFile = petriNetSimulationFile;
		this.omc = omc;
	}

	public HashMap<String, Vector<Double>> getPnResult() {
		return this.pnResult;
	}

	public void initializePetriNet(
			HashMap<BiologicalEdgeAbstract, String> bea2key) {

		graphInstance = new GraphInstance();
		pw = graphInstance.getPathway();
		Collection<BiologicalNodeAbstract> hs = pw.getAllNodes();
		pnResult = pw.getPetriNet().getPnResult();
		ArrayList<String> columns = new ArrayList<String>();
		// rowsSize = 0;
		// Object elem;
		// columns.addAll(bea2key.values());

		Iterator<String> cols = bea2key.values().iterator();
		String col;
		while (cols.hasNext()) {
			col = cols.next();
			columns.add(col);
			columns.add("der(" + col + ")");
		}

		BiologicalNodeAbstract bna;
		if (hs != null) {
			Iterator<BiologicalNodeAbstract> it = hs.iterator();
			while (it.hasNext()) {

				bna = it.next();
				if (bna instanceof Place) {
					columns.add(bna.getName() + ".t");
					// System.out.println(bna.getName());
				} else if (bna instanceof Transition) {
					columns.add(bna.getName() + ".fire");
				}
			}
		}
		columns.add("time");
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
						this.petriNetSimulationFile, columns, omc);

				// Iterator it = this.pnResult.

				this.setDataToNodes(bea2key);

			} else if (ext.equals("plt")) {
				this.pnResult = this.pnrir
						.readResult(getPetriNetSimulationFile());
				this.setDataToNodes(bea2key);
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

	private void setDataToNodes(HashMap<BiologicalEdgeAbstract, String> bea2key)
			throws Exception {
		// System.out.println(pnResult.keySet().size());
		places = 0;

		graphInstance = new GraphInstance();
		Pathway pw = graphInstance.getPathway();
		Collection<BiologicalNodeAbstract> hs = pw.getAllNodes();
		// pnResult = pw.getPetriNet().getPnResult();
		ArrayList<Integer> count = new ArrayList<Integer>();
		// rowsSize = 0;
		// System.out.println("size: "+ pnResult.size());
		Iterator<BiologicalEdgeAbstract> itBea = pw.getAllEdges().iterator();

		BiologicalEdgeAbstract bea;
		PNEdge e;
		Vector<Double> v;
		Vector<Double> v2;
		while (itBea.hasNext()) {
			bea = itBea.next();
			if (bea instanceof PNEdge) {
				e = (PNEdge) bea;

				v = pnResult.get(bea2key.get(bea));
				v2 = pnResult.get("der("+bea2key.get(bea)+")");
				e.setSim_tokensSum(v);
				e.setSim_tokens(v2);
			}
		}

		if (hs != null) {
			Iterator<BiologicalNodeAbstract> it = hs.iterator();
			BiologicalNodeAbstract bna;

			while (it.hasNext()) {
				bna = it.next();
				if (bna instanceof Place) {

					// float f = 1.0f/hs.size()+count.size()*1.0f/hs.size();
					// Color c = Color.getHSBColor(f, 1, 1);

					// int intSimID = bna.getID();
					// if (bna.getPetriNetSimulationData().size() == 0) {
					// System.out.println(intSimID);
					// System.out.println("result: "+pnResult);
					// System.out.println(pnResult.keySet().toString());
					// System.out.println("Hallo erster Test zum abfragen");
					// System.out.println("P"+bna.getID());
					if (pnResult.containsKey("P" + bna.getID() + ".t")) {
						// System.out.println("drin");
					} else {
						// System.out.println("n�");
					}

					// System.out.println();

					v = pnResult.get(bna.getName() + ".t");
					// System.out.println(bna.getName());
					// System.out.println(v.lastElement());
					// System.out.println("size: "+v.size());
					// System.out.println("test2");
					// if (v.get(0).doubleValue() != ((Place)
					// bna).getTokenStart())
					// throw new Exception(
					// "A startToken value in the petri net does not fit the result!");
					bna.setPetriNetSimulationData(v);
					// System.out.println(bna.getName());
					// System.out.println(v.size());

					count.add(new Integer(v.size()));
					// System.out.println("ende");
					this.places++;
				} else if (bna instanceof Transition) {
					// System.out.println("gesetzt");
					v = pnResult.get(bna.getName() + ".fire");
					// System.out.println("size: "+v.size());
					bna.setPetriNetSimulationData(v);
				}
			}

			it = hs.iterator();
			// Place p;
			int i = 0;
			while (it.hasNext()) {
				// System.out.println("drin");
				bna = it.next();
				if (bna instanceof Place) {
					// System.out.println(i*1.0f/(this.places-1));
					((Place) bna).setPlotColor(Color.getHSBColor(i * 1.0f
							/ (this.places), 1, 1));
					// System.out.println(i);
					i++;
					// p = (Place) o;
					// System.out.println(p.getName());
					// System.out.println(p.getPetriNetSimulationData());
				}
			}

			Iterator<Integer> it2 = count.iterator();
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
		Collection<BiologicalNodeAbstract> hs = pw.getAllNodes();
		if (hs != null) {
			Iterator<BiologicalNodeAbstract> it = hs.iterator();
			while (it.hasNext()) {
				BiologicalNodeAbstract bna = it.next();
				if (bna instanceof Place) {
					Place p = (Place) bna;
					// if (p.getPetriNetSimulationData() != null) {
					p.setPetriNetSimulationData(new Vector<Double>());
					// }
					// System.out.println(p.getPetriNetSimulationData().size());
				}

			}
		}
		// pw.setPetriNet(false);
		this.setPetriNetSimulationFile(null, false);
	}

	public int getNumberOfPlaces() {
		return this.places;
	}

	public int getResultDimension() {
		return this.resultDimension;
	}

	public int getPlaces() {
		return places;
	}

	public void setPlaces(int places) {
		this.places = places;
	}

	public int getTransitions() {
		return transitions;
	}

	public void setTransitions(int transitions) {
		this.transitions = transitions;
	}

}
