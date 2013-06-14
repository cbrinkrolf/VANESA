package database.dawis;

import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.CompoundNode;
import biologicalObjects.nodes.Disease;
import biologicalObjects.nodes.Drug;
import biologicalObjects.nodes.Enzyme;
import biologicalObjects.nodes.Factor;
import biologicalObjects.nodes.Fragment;
import biologicalObjects.nodes.Gene;
import biologicalObjects.nodes.GeneOntology;
import biologicalObjects.nodes.Glycan;
import biologicalObjects.nodes.PathwayMap;
import biologicalObjects.nodes.Protein;
import biologicalObjects.nodes.Reaction;
import biologicalObjects.nodes.Site;


public class GetDAWISNode {

	public GetDAWISNode() {

	}

	public void getElementDetails(BiologicalNodeAbstract bna) {
		if (bna instanceof PathwayMap) {
			new GetPathwayDetails((PathwayMap) bna);
		} else if (bna instanceof Disease) {
			new GetDiseaseDetails((Disease) bna);
		} else if (bna instanceof Enzyme) {
			new GetEnzymeDetails((Enzyme) bna);
		} else if (bna instanceof Protein) {
			new GetProteinDetails((Protein) bna);
		} else if (bna instanceof GeneOntology) {
			new GetGODetails((GeneOntology) bna);
		} else if (bna instanceof Gene) {
			new GetGeneDetails((Gene) bna);
		} else if (bna instanceof CompoundNode) {
			new GetCompoundDetails((CompoundNode) bna);
		} else if (bna instanceof Reaction) {
			new GetReactionDetails((Reaction) bna);
		}  else if (bna instanceof Glycan) {
			new GetGlycanDetails((Glycan) bna);
		} else if (bna instanceof Drug) {
			new GetDrugDetails((Drug) bna);
		} else if (bna instanceof Site) {
			new GetTransfacSiteDetails((Site) bna);
		} else if (bna instanceof Factor) {
			new GetTransfacFactorDetails((Factor) bna);
		} else if (bna instanceof Fragment) {
			new GetTransfacFragmentDetails((Fragment) bna);
		} 
	}

}
