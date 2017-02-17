package xmlOutput.sbml;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import biologicalObjects.edges.petriNet.PNEdge;
import biologicalObjects.nodes.petriNet.Place;
import petriNet.Transition;
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
	private File file = null;

	// list with nodes/places from the petri net
	private ArrayList<Place> bnaliste = new ArrayList<Place>();

	// list with transitions from the petri net
	private ArrayList<Transition> btaliste = new ArrayList<Transition>();

	// list with edges from the petri net
	private ArrayList<PNEdge> bealiste = new ArrayList<PNEdge>();

	// For saving Transitionlabel (key) and TransitionHAPLI (value)
	private HashMap<String, TransitionHLAPI> transitionPNML = new HashMap<String, TransitionHLAPI>();

	// For saving Placelabel (key) and PlaceHAPLI (value)
	private HashMap<String, PlaceHLAPI> placePNML = new HashMap<String, PlaceHLAPI>();

	private PetriNetHLAPI net;
	// Transition in pnml
	private TransitionHLAPI t1;

	// Place in pnml
	private PlaceHLAPI p1;

	// Arc in pnml
	private ArcHLAPI arc;

	//private int workspace = 0;

	private int netId = 0;

	private boolean transitionFrom = false;

	private boolean placeFrom = false;

	//private boolean transitionTo = false;

	//private boolean placeTo = false;

	private PetriNetDocHLAPI doc;

	/**
	 * This constructor needs a file to which the output can be written, a list
	 * of edges from the petrinet.
	 * 
	 * @param file
	 *            File, the new PNML file.
	 * @param BiologicalEdgeAbstract
	 *            bealiste, the graph information are received from here.
	 */
	public PNMLOutput(File file, ArrayList<PNEdge> bealiste,
			ArrayList<Place> bnaliste, ArrayList<Transition> btaliste) {
		this.file = file;
		this.bealiste = bealiste;
		this.btaliste = btaliste;
		this.bnaliste = bnaliste;

	}

	/**
	 * Generates a PNML document with PNML Framework.
	 * 
	 * @return boolean true if a document has been written to the specified
	 *         diretory.
	 */
	public String generatePNMLDocument() throws InvalidIDException,
			VoidRepositoryException {

		ModelRepository.getInstance().createDocumentWorkspace("Workspace"+System.currentTimeMillis());
		doc = new PetriNetDocHLAPI();

		net = new PetriNetHLAPI("net" + netId, PNTypeHLAPI.PTNET, doc);

		PageHLAPI page = new PageHLAPI("toppage",
				new NameHLAPI(file.getName()), null, net);

		//int labelid = 0;
		int arcid = 0;
		String placeLabel;
		//String placeLabel2;
		String transitionLabel;

		GraphInstance g = new GraphInstance();
		Point2D location;
		//System.out.println("size: "+bnaliste.size());
		for (int i = 0; i < this.bnaliste.size(); i++) {
			
			// if (this.bnaliste.get(i).getBiologicalElement() ==
			// "Discrete Place" || this.bnaliste.get(i).getBiologicalElement()
			// == "Continuous Place") {
			placeLabel = "P_" + this.bnaliste.get(i).getID();

			if (!placePNML.containsKey(placeLabel)) {
				p1 = new PlaceHLAPI(placeLabel);
				p1.setNameHLAPI(new NameHLAPI(this.bnaliste.get(i).getLabel()));
				placePNML.put(placeLabel, p1);
				/*
				 * Set the Marking of a place.
				 */
				//PTMarkingHLAPI ptMarking = new PTMarkingHLAPI(
					//	(int) this.bnaliste.get(i).getToken(), p1);
				/*
				 * Set the position of a place.
				 */

				location = g.getPathway().getGraph()
						.getVertexLocation(this.bnaliste.get(i));
				NodeGraphicsHLAPI placeGraphics = new NodeGraphicsHLAPI(
						new PositionHLAPI((int) location.getX(),
								(int) location.getY()), null, null, null);

				p1.setNodegraphicsHLAPI(placeGraphics);

				p1.setContainerPageHLAPI(page);
			}

			// }

		}
		for (int i = 0; i < this.btaliste.size(); i++) {

			// if (this.btaliste.get(i).getBiologicalElement() ==
			// "Discrete Transition" ||
			// this.btaliste.get(i).getBiologicalElement() ==
			// "Continuous Transition") {
			if (!transitionPNML
					.containsKey("T_" + this.btaliste.get(i).getID())) {

				transitionLabel = "T_" + this.btaliste.get(i).getID();

				t1 = new TransitionHLAPI(transitionLabel);
				t1.setNameHLAPI(new NameHLAPI(this.btaliste.get(i).getLabel()));

				transitionPNML.put(transitionLabel, t1);

				/*
				 * /* Set the position of a place.
				 */
				location = g.getPathway().getGraph()
						.getVertexLocation(this.btaliste.get(i));
				NodeGraphicsHLAPI placeGraphics = new NodeGraphicsHLAPI(
						new PositionHLAPI((int) location.getX(),
								(int) location.getY()), null, null, null);

				t1.setNodegraphicsHLAPI(placeGraphics);
				t1.setContainerPageHLAPI(page);
			} else {

				t1 = (TransitionHLAPI) transitionPNML.get(this.btaliste.get(i)
						.getID());

			}
			// }

		}
		for (int i = 0; i < this.bealiste.size(); i++) {

			if (this.bealiste.get(i).getFrom() instanceof Place) {
				p1 = (PlaceHLAPI) placePNML.get("P_"
						+ this.bealiste.get(i).getFrom().getID());
				//System.out.println(p1.getId());
				placeFrom = true;

			}
			if (this.bealiste.get(i).getFrom() instanceof Transition) {
				t1 = (TransitionHLAPI) transitionPNML.get("T_"
						+ this.bealiste.get(i).getFrom().getID());
				transitionFrom = true;
			}
			if (this.bealiste.get(i).getTo() instanceof Place) {
				p1 = (PlaceHLAPI) placePNML.get("P_"
						+ this.bealiste.get(i).getTo().getID());
				//placeTo = true;

			}
			if (this.bealiste.get(i).getTo() instanceof Transition) {
				t1 = (TransitionHLAPI) transitionPNML.get("T_"
						+ this.bealiste.get(i).getTo().getID());
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

			/*
			 * Position of arc
			 */
			location = g.getPathway().getGraph()
					.getVertexLocation(this.bealiste.get(i).getFrom());
			final ArcGraphicsHLAPI arcG = new ArcGraphicsHLAPI(arc);
			PositionHLAPI position = new PositionHLAPI((int) location.getX(),
					(int) location.getY());
			arcG.addPositionsHLAPI(position);

			if (this.bealiste.get(i).getBiologicalElement() == "PN Inhibition Edge") {

				t1 = (TransitionHLAPI) transitionPNML.get("T_"
						+ this.bealiste.get(i).getTo().getID());

				t1.setNameHLAPI(new NameHLAPI("Inhibitor;"
						+ this.bealiste.get(i).getTo().getLabel()));

			}
			PNEdge t = (PNEdge) this.bealiste.get(i);

			arc.setNameHLAPI(new NameHLAPI(t.getLabel()));

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
