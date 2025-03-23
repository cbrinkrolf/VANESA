package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
import biologicalObjects.nodes.petriNet.ContinuousPlace;
import biologicalObjects.nodes.petriNet.ContinuousTransition;
import biologicalObjects.nodes.petriNet.DiscretePlace;
import biologicalObjects.nodes.petriNet.DiscreteTransition;
import biologicalObjects.nodes.petriNet.Place;
import biologicalObjects.nodes.petriNet.StochasticTransition;
import transformation.graphElements.ANYBiologicalNode;
import transformation.graphElements.ANYPlace;
import transformation.graphElements.ANYTransition;

public class BiologicalNodeAbstractFactory {
	public static BiologicalNodeAbstract create(final String type) {
		return create(type, null);
	}

	// creating new object with attributes of given bna (could be null)
	public static BiologicalNodeAbstract create(final String type, final BiologicalNodeAbstract bna) {
		final String name = bna != null ? bna.getName() : "";
		final String label = bna != null ? bna.getLabel() : "";
		switch (type) {
		case Elementdeclerations.collector:
			return new Collector(label, name);
		case Elementdeclerations.complex:
			return new Complex(label, name);
		case Elementdeclerations.compound:
			return new CompoundNode(label, name);
		case Elementdeclerations.degraded:
			return new Degraded(label, name);
		case Elementdeclerations.disease:
			return new Disease(label, name);
		case Elementdeclerations.dna:
			return fill(new DNA(label, name), bna);
		case Elementdeclerations.domain:
			return new Domain(label, name);
		case Elementdeclerations.drug:
			return new Drug(label, name);
		case Elementdeclerations.enzyme:
			return new Enzyme(label, name);
		case Elementdeclerations.exon:
			return new Exon(label, name);
		case Elementdeclerations.factor:
			return new Factor(label, name);
		case Elementdeclerations.fragment:
			return new Fragment(label, name);
		case Elementdeclerations.gene:
			return new Gene(label, name);
		case Elementdeclerations.glycan:
			return new Glycan(label, name);
		case Elementdeclerations.homodimerFormation:
			return new HomodimerFormation(label, name);
		case Elementdeclerations.inhibitor:
			return new Inhibitor(label, name);
		case Elementdeclerations.ligandBinding:
			return new LigandBinding(label, name);
		case Elementdeclerations.matrix:
			return new Matrix(label, name);
		case Elementdeclerations.membraneChannel:
			return new MembraneChannel(label, name);
		case Elementdeclerations.membraneReceptor:
			return new MembraneReceptor(label, name);
		case Elementdeclerations.mRNA:
			return fill(new MRNA(label, name), bna);
		case Elementdeclerations.miRNA:
			return fill(new MIRNA(label, name), bna);
		case Elementdeclerations.lncRNA:
			return fill(new LNCRNA(label, name), bna);
		case Elementdeclerations.orthologGroup:
			return new OrthologGroup(label, name);
		case Elementdeclerations.pathwayMap:
			return fill(new PathwayMap(label, name), bna);
		case Elementdeclerations.protein:
			return new Protein(label, name);
		case Elementdeclerations.reaction:
			return new Reaction(label, name);
		case Elementdeclerations.receptor:
			return new Receptor(label, name);
		case Elementdeclerations.rna:
			return new RNA(label, name);
		case Elementdeclerations.site:
			return new Site(label, name);
		case Elementdeclerations.smallMolecule: // kept for legacy
		case Elementdeclerations.metabolite:
			return new Metabolite(label, name);
		case Elementdeclerations.solubleReceptor:
			return new SolubleReceptor(label, name);
		case Elementdeclerations.sRNA:
			return fill(new SRNA(label, name), bna);
		case Elementdeclerations.transcriptionFactor:
			return new TranscriptionFactor(label, name);
		case Elementdeclerations.discretePlace:
			return fill(new DiscretePlace(label, name), bna);
		case Elementdeclerations.continuousPlace:
			return fill(new ContinuousPlace(label, name), bna);
		case Elementdeclerations.discreteTransition:
			return fill(new DiscreteTransition(label, name), bna);
		case Elementdeclerations.continuousTransition:
			return fill(new ContinuousTransition(label, name), bna);
		case Elementdeclerations.stochasticTransition:
			return fill(new StochasticTransition(label, name), bna);
		case Elementdeclerations.anyBNA:
			return new ANYBiologicalNode(label, name);
		case Elementdeclerations.place:
			return new ANYPlace(label, name);
		case Elementdeclerations.transition:
			return new ANYTransition(label, name);
		case Elementdeclerations.others:
		default:
			return new Other(label, name);
		}
	}

	private static RNA fill(RNA newBNA, BiologicalNodeAbstract bna) {
		if (bna instanceof RNA) {
			newBNA.setNtSequence(((RNA) bna).getNtSequence());
			newBNA.setLogFC(((RNA) bna).getLogFC());
		}
		return newBNA;
	}

	private static DNA fill(DNA newBNA, BiologicalNodeAbstract bna) {
		if (bna instanceof DNA) {
			newBNA.setNtSequence(((DNA) bna).getNtSequence());
			newBNA.setLogFC(((DNA) bna).getLogFC());
		}
		return newBNA;
	}

	private static PathwayMap fill(PathwayMap newBNA, BiologicalNodeAbstract bna) {
		if (bna instanceof PathwayMap) {
			newBNA.setPathwayLink(((PathwayMap) bna).getPathwayLink());
		}
		return newBNA;
	}

	private static Place fill(Place newBNA, BiologicalNodeAbstract bna) {
		if (bna instanceof Place) {
			newBNA.setToken(((Place) bna).getToken());
			newBNA.setTokenMin(((Place) bna).getTokenMin());
			newBNA.setTokenMax(((Place) bna).getTokenMax());
			newBNA.setTokenStart(((Place) bna).getTokenStart());
		}
		return newBNA;
	}

	private static DiscreteTransition fill(DiscreteTransition newBNA, BiologicalNodeAbstract bna) {
		if (bna instanceof DiscreteTransition) {
			newBNA.setDelay(((DiscreteTransition) bna).getDelay());
			newBNA.setKnockedOut(((DiscreteTransition) bna).isKnockedOut());
		}
		return newBNA;
	}

	private static ContinuousTransition fill(ContinuousTransition newBNA, BiologicalNodeAbstract bna) {
		if (bna instanceof ContinuousTransition) {
			newBNA.setMaximalSpeed(((ContinuousTransition) bna).getMaximalSpeed());
			newBNA.setKnockedOut(((ContinuousTransition) bna).isKnockedOut());
		}
		return newBNA;
	}

	private static StochasticTransition fill(StochasticTransition newBNA, BiologicalNodeAbstract bna) {
		if (bna instanceof StochasticTransition) {
			newBNA.setDistribution(((StochasticTransition) bna).getDistribution());
			newBNA.setKnockedOut(((StochasticTransition) bna).isKnockedOut());
		}
		return newBNA;
	}
}