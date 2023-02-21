package io;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import biologicalObjects.edges.petriNet.PNArc;
import biologicalObjects.nodes.petriNet.Place;
import biologicalObjects.nodes.petriNet.Transition;
import fr.lip6.move.pnml.framework.general.PnmlExport;
import fr.lip6.move.pnml.framework.utils.ModelRepository;
import fr.lip6.move.pnml.framework.utils.exception.BadFileFormatException;
import fr.lip6.move.pnml.framework.utils.exception.InvalidIDException;
import fr.lip6.move.pnml.framework.utils.exception.OCLValidationFailed;
import fr.lip6.move.pnml.framework.utils.exception.OtherException;
import fr.lip6.move.pnml.framework.utils.exception.UnhandledNetType;
import fr.lip6.move.pnml.framework.utils.exception.ValidationFailedException;
import fr.lip6.move.pnml.framework.utils.exception.VoidRepositoryException;
import fr.lip6.move.pnml.ptnet.hlapi.ArcGraphicsHLAPI;
import fr.lip6.move.pnml.ptnet.hlapi.ArcHLAPI;
import fr.lip6.move.pnml.ptnet.hlapi.NameHLAPI;
import fr.lip6.move.pnml.ptnet.hlapi.NodeGraphicsHLAPI;
import fr.lip6.move.pnml.ptnet.hlapi.PNTypeHLAPI;
import fr.lip6.move.pnml.ptnet.hlapi.PageHLAPI;
import fr.lip6.move.pnml.ptnet.hlapi.PetriNetDocHLAPI;
import fr.lip6.move.pnml.ptnet.hlapi.PetriNetHLAPI;
import fr.lip6.move.pnml.ptnet.hlapi.PlaceHLAPI;
import fr.lip6.move.pnml.ptnet.hlapi.PositionHLAPI;
import fr.lip6.move.pnml.ptnet.hlapi.TransitionHLAPI;
import graph.GraphInstance;

public class PNMLOutput {
	// the pnml document which has to be filled
	private final File file;

	// list with nodes/places from the petri net
	private final ArrayList<Place> bnaliste;

	// list with transitions from the petri net
	private final ArrayList<Transition> btaliste;

	// list with edges from the petri net
	private final ArrayList<PNArc> bealiste;

	// For saving Transitionlabel (key) and TransitionHAPLI (value)
	private final HashMap<String, TransitionHLAPI> transitionPNML = new HashMap<>();

	// For saving Placelabel (key) and PlaceHAPLI (value)
	private final HashMap<String, PlaceHLAPI> placePNML = new HashMap<>();

	// Transition in pnml
	private TransitionHLAPI t1;

	// Place in pnml
	private PlaceHLAPI p1;

	// Arc in pnml
	private ArcHLAPI arc;

	//private int workspace = 0;

	private boolean transitionFrom = false;

	private boolean placeFrom = false;

	//private boolean transitionTo = false;

	//private boolean placeTo = false;

	/**
	 * This constructor needs a file to which the output can be written, a list of edges from the petrinet.
	 */
	public PNMLOutput(File file, ArrayList<PNArc> bealiste, ArrayList<Place> bnaliste, ArrayList<Transition> btaliste) {
		this.file = file;
		this.bealiste = bealiste;
		this.btaliste = btaliste;
		this.bnaliste = bnaliste;
	}

	/**
	 * Generates a PNML document with PNML Framework.
	 * 
	 * @return boolean true if a document has been written to the specified directory.
	 */
	public String generatePNMLDocument() throws InvalidIDException, VoidRepositoryException {
		ModelRepository.getInstance().createDocumentWorkspace("Workspace"+System.currentTimeMillis());
		PetriNetDocHLAPI doc = new PetriNetDocHLAPI();
		int netId = 0;
		PetriNetHLAPI net = new PetriNetHLAPI("net" + netId, PNTypeHLAPI.PTNET, doc);
		PageHLAPI page = new PageHLAPI("toppage", new NameHLAPI(file.getName()), null, net);
		//int labelid = 0;
		int arcid = 0;
		String placeLabel;
		//String placeLabel2;
		String transitionLabel;
		GraphInstance g = new GraphInstance();
		Point2D location;
		for (Place place : this.bnaliste) {
			// if ("Discrete Place".equals(place.getBiologicalElement()) || "Continuous Place".equals(place.getBiologicalElement())) {
			placeLabel = "P_" + place.getID();
			if (!placePNML.containsKey(placeLabel)) {
				p1 = new PlaceHLAPI(placeLabel);
				p1.setNameHLAPI(new NameHLAPI(place.getLabel()));
				placePNML.put(placeLabel, p1);
				// Set the Marking of a place.
				// PTMarkingHLAPI ptMarking = new PTMarkingHLAPI((int) this.bnaliste.get(i).getToken(), p1);
				// Set the position of a place.
				location = g.getPathway().getGraph().getVertexLocation(place);
				NodeGraphicsHLAPI placeGraphics = new NodeGraphicsHLAPI(
						new PositionHLAPI((int) location.getX(), (int) location.getY()), null, null, null);
				p1.setNodegraphicsHLAPI(placeGraphics);
				p1.setContainerPageHLAPI(page);
			}
			// }
		}
		for (Transition transition : this.btaliste) {
			// if ("Discrete Transition".equals(transition.getBiologicalElement()) || "Continuous Transition".equals(transition.getBiologicalElement())) {
			if (!transitionPNML.containsKey("T_" + transition.getID())) {
				transitionLabel = "T_" + transition.getID();
				t1 = new TransitionHLAPI(transitionLabel);
				t1.setNameHLAPI(new NameHLAPI(transition.getLabel()));
				transitionPNML.put(transitionLabel, t1);
				// Set the position of a place.
				location = g.getPathway().getGraph().getVertexLocation(transition);
				NodeGraphicsHLAPI placeGraphics = new NodeGraphicsHLAPI(
						new PositionHLAPI((int) location.getX(), (int) location.getY()), null, null, null);
				t1.setNodegraphicsHLAPI(placeGraphics);
				t1.setContainerPageHLAPI(page);
			} else {
				t1 = transitionPNML.get(transition.getID());
			}
			// }
		}
		for (PNArc pnArc : this.bealiste) {
			if (pnArc.getFrom() instanceof Place) {
				p1 = placePNML.get("P_" + pnArc.getFrom().getID());
				placeFrom = true;
			}
			if (pnArc.getFrom() instanceof Transition) {
				t1 = transitionPNML.get("T_" + pnArc.getFrom().getID());
				transitionFrom = true;
			}
			if (pnArc.getTo() instanceof Place) {
				p1 = placePNML.get("P_" + pnArc.getTo().getID());
				//placeTo = true;
			}
			if (pnArc.getTo() instanceof Transition) {
				t1 = transitionPNML.get("T_" + pnArc.getTo().getID());
				//transitionTo = true;
			}
			if (placeFrom) {
				arc = new ArcHLAPI("arc" + arcid, p1, t1, page);
			}
			if (transitionFrom) {
				arc = new ArcHLAPI("arc" + arcid, t1, p1, page);
			}
			placeFrom = false;
			//transitionTo = false;
			transitionFrom = false;
			//placeTo = false;
			// Position of arc
			location = g.getPathway().getGraph().getVertexLocation(pnArc.getFrom());
			final ArcGraphicsHLAPI arcG = new ArcGraphicsHLAPI(arc);
			PositionHLAPI position = new PositionHLAPI((int) location.getX(), (int) location.getY());
			arcG.addPositionsHLAPI(position);
			if ("PN Inhibition Edge".equals(pnArc.getBiologicalElement())) {
				t1 = transitionPNML.get("T_" + pnArc.getTo().getID());
				t1.setNameHLAPI(new NameHLAPI("Inhibitor;" + pnArc.getTo().getLabel()));
			}
			arc.setNameHLAPI(new NameHLAPI(pnArc.getLabel()));
			arcid++;
		}
		ModelRepository mr = ModelRepository.getInstance();
		mr.setPrettyPrintStatus(true);
		PnmlExport pex = new PnmlExport();
		//pex.
		String result = "";
		try {
			pex.exportObject(doc, file.getAbsolutePath());
		} catch (UnhandledNetType e) {
			e.printStackTrace();
			result+="Error: Unhandled Net Type!\r\n";
		} catch (OCLValidationFailed e) {
			e.printStackTrace();
			result+="Error: OCLValidation failed!\r\n";
		} catch (IOException e) {
			e.printStackTrace();
			result+="Error: IO Exception!\r\n";
		} catch (ValidationFailedException e) {
			e.printStackTrace();
			result+="Error: Validation failed!\r\n";
		} catch (BadFileFormatException e) {
			e.printStackTrace();
			result+="Error: Bad file format!\r\n";
		} catch (OtherException e) {
			e.printStackTrace();
			result+="Error: Other exception!\r\n";
		}
		ModelRepository.getInstance().destroyCurrentWorkspace();
		return result;
	}
}
