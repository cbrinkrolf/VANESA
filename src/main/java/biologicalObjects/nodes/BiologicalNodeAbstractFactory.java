package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;
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
	public static BiologicalNodeAbstract create(final Pathway parent, final String type) {
		return create(parent, type, null);
	}

	// creating new object with attributes of given bna (could be null)
	public static BiologicalNodeAbstract create(final Pathway parent, final String type,
			final BiologicalNodeAbstract bna) {
		final String name = bna != null ? bna.getName() : "";
		final String label = bna != null ? bna.getLabel() : "";
		switch (type) {
		case Elementdeclerations.collector:
			return new Collector(label, name, parent);
		case Elementdeclerations.complex:
			return new Complex(label, name, parent);
		case Elementdeclerations.compound:
			return new CompoundNode(label, name, parent);
		case Elementdeclerations.degraded:
			return new Degraded(label, name, parent);
		case Elementdeclerations.disease:
			return new Disease(label, name, parent);
		case Elementdeclerations.dna:
			return fill(new DNA(label, name, parent), bna);
		case Elementdeclerations.domain:
			return new Domain(label, name, parent);
		case Elementdeclerations.drug:
			return new Drug(label, name, parent);
		case Elementdeclerations.enzyme:
			return new Enzyme(label, name, parent);
		case Elementdeclerations.exon:
			return new Exon(label, name, parent);
		case Elementdeclerations.factor:
			return new Factor(label, name, parent);
		case Elementdeclerations.fragment:
			return new Fragment(label, name, parent);
		case Elementdeclerations.gene:
			return new Gene(label, name, parent);
		case Elementdeclerations.glycan:
			return new Glycan(label, name, parent);
		case Elementdeclerations.homodimerFormation:
			return new HomodimerFormation(label, name, parent);
		case Elementdeclerations.inhibitor:
			return new Inhibitor(label, name, parent);
		case Elementdeclerations.ligandBinding:
			return new LigandBinding(label, name, parent);
		case Elementdeclerations.matrix:
			return new Matrix(label, name, parent);
		case Elementdeclerations.membraneChannel:
			return new MembraneChannel(label, name, parent);
		case Elementdeclerations.membraneReceptor:
			return new MembraneReceptor(label, name, parent);
		case Elementdeclerations.mRNA:
			return fill(new MRNA(label, name, parent), bna);
		case Elementdeclerations.miRNA:
			return fill(new MIRNA(label, name, parent), bna);
		case Elementdeclerations.lncRNA:
			return fill(new LNCRNA(label, name, parent), bna);
		case Elementdeclerations.orthologGroup:
			return new OrthologGroup(label, name, parent);
		case Elementdeclerations.pathwayMap:
			return fill(new PathwayMap(label, name, parent), bna);
		case Elementdeclerations.protein:
			return new Protein(label, name, parent);
		case Elementdeclerations.reaction:
			return new Reaction(label, name, parent);
		case Elementdeclerations.receptor:
			return new Receptor(label, name, parent);
		case Elementdeclerations.rna:
			return new RNA(label, name, parent);
		case Elementdeclerations.site:
			return new Site(label, name, parent);
		case Elementdeclerations.smallMolecule: // kept for legacy
		case Elementdeclerations.metabolite:
			return new Metabolite(label, name, parent);
		case Elementdeclerations.solubleReceptor:
			return new SolubleReceptor(label, name, parent);
		case Elementdeclerations.sRNA:
			return fill(new SRNA(label, name, parent), bna);
		case Elementdeclerations.transcriptionFactor:
			return new TranscriptionFactor(label, name, parent);
		case Elementdeclerations.discretePlace:
			return fill(new DiscretePlace(label, name, parent), bna);
		case Elementdeclerations.continuousPlace:
			return fill(new ContinuousPlace(label, name, parent), bna);
		case Elementdeclerations.discreteTransition:
			return fill(new DiscreteTransition(label, name, parent), bna);
		case Elementdeclerations.continuousTransition:
			return fill(new ContinuousTransition(label, name, parent), bna);
		case Elementdeclerations.stochasticTransition:
			return fill(new StochasticTransition(label, name, parent), bna);
		case Elementdeclerations.anyBNA:
			return new ANYBiologicalNode(label, name, parent);
		case Elementdeclerations.place:
			return new ANYPlace(label, name, parent);
		case Elementdeclerations.transition:
			return new ANYTransition(label, name, parent);
		case Elementdeclerations.others:
		default:
			return new Other(label, name, parent);
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