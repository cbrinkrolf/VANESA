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
	// creating new object with attributes of given bna (could be null)
	public static BiologicalNodeAbstract create(final String type, final BiologicalNodeAbstract bna) {
		return create(type, bna, bna != null ? bna.getLabel() : "", bna != null ? bna.getName() : "");
	}

	public static BiologicalNodeAbstract create(final String type, final BiologicalNodeAbstract bna, final String label,
			final String name) {
		switch (type) {
		case Elementdeclerations.collector:
			return new Collector(name, label);
		case Elementdeclerations.complex:
			return new Complex(label, name);
		case Elementdeclerations.compound:
			return new CompoundNode(label, name);
		case Elementdeclerations.degraded:
			return new Degraded(label, name);
		case Elementdeclerations.disease:
			return new Disease(label, name);
		case Elementdeclerations.dna:
			return fillDNA(new DNA(label, name), bna);
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
			return fillRNA(new MRNA(label, name), bna);
		case Elementdeclerations.miRNA:
			return fillRNA(new MIRNA(label, name), bna);
		case Elementdeclerations.lncRNA:
			return fillRNA(new LNCRNA(label, name), bna);
		case Elementdeclerations.orthologGroup:
			return new OrthologGroup(label, name);
		case Elementdeclerations.pathwayMap:
			return fillPathwayMap(new PathwayMap(label, name), bna);
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
		// kept for legacy
		case Elementdeclerations.smallMolecule:
			return new Metabolite(label, name);
		case Elementdeclerations.metabolite:
			return new Metabolite(label, name);
		case Elementdeclerations.solubleReceptor:
			return new SolubleReceptor(label, name);
		case Elementdeclerations.sRNA:
			return fillRNA(new SRNA(label, name), bna);
		case Elementdeclerations.transcriptionFactor:
			return new TranscriptionFactor(label, name);
		case Elementdeclerations.discretePlace:
			fillPlace(new DiscretePlace(label, name), bna);
		case Elementdeclerations.continuousPlace:
			return fillPlace(new ContinuousPlace(label, name), bna);
		case Elementdeclerations.discreteTransition:
			return fillDiscreteTransition(new DiscreteTransition(label, name), bna);
		case Elementdeclerations.continuousTransition:
			return fillContinuousTransition(new ContinuousTransition(label, name), bna);
		case Elementdeclerations.stochasticTransition:
			return fillStochasticTransition(new StochasticTransition(label, name), bna);
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

	private static BiologicalNodeAbstract fillRNA(BiologicalNodeAbstract newBNA, BiologicalNodeAbstract bna) {
		if (bna != null) {
			if (newBNA instanceof RNA && bna instanceof RNA) {
				((RNA) newBNA).setNtSequence(((RNA) bna).getNtSequence());
				((RNA) newBNA).setLogFC(((RNA) bna).getLogFC());
			}
		}
		return newBNA;
	}

	private static BiologicalNodeAbstract fillDNA(BiologicalNodeAbstract newBNA, BiologicalNodeAbstract bna) {
		if (bna != null) {
			if (newBNA instanceof DNA && bna instanceof DNA) {
				((DNA) newBNA).setNtSequence(((DNA) bna).getNtSequence());
				((DNA) newBNA).setLogFC(((DNA) bna).getLogFC());
			}
		}
		return newBNA;
	}

	private static BiologicalNodeAbstract fillPathwayMap(BiologicalNodeAbstract newBNA, BiologicalNodeAbstract bna) {
		if (bna != null) {
			if (newBNA instanceof PathwayMap && bna instanceof PathwayMap) {
				((PathwayMap) newBNA).setPathwayLink(((PathwayMap) bna).getPathwayLink());
			}
		}
		return newBNA;
	}

	private static BiologicalNodeAbstract fillPlace(BiologicalNodeAbstract newBNA, BiologicalNodeAbstract bna) {
		if (bna != null) {
			if (newBNA instanceof Place && bna instanceof Place) {
				((Place) newBNA).setToken(((Place) bna).getToken());
				((Place) newBNA).setTokenMin(((Place) bna).getTokenMin());
				((Place) newBNA).setTokenMax(((Place) bna).getTokenMax());
				((Place) newBNA).setTokenStart(((Place) bna).getTokenStart());
			}
		}
		return newBNA;
	}

	private static BiologicalNodeAbstract fillDiscreteTransition(BiologicalNodeAbstract newBNA,
			BiologicalNodeAbstract bna) {
		if (bna != null) {
			if (newBNA instanceof DiscreteTransition && bna instanceof DiscreteTransition) {
				((DiscreteTransition) newBNA).setDelay(((DiscreteTransition) bna).getDelay());
				((DiscreteTransition) newBNA).setKnockedOut(((DiscreteTransition) bna).isKnockedOut());
			}
		}
		return newBNA;
	}

	private static BiologicalNodeAbstract fillContinuousTransition(BiologicalNodeAbstract newBNA,
			BiologicalNodeAbstract bna) {
		if (bna != null) {
			if (newBNA instanceof ContinuousTransition && bna instanceof ContinuousTransition) {
				((ContinuousTransition) newBNA).setMaximalSpeed(((ContinuousTransition) bna).getMaximalSpeed());
				((ContinuousTransition) newBNA).setKnockedOut(((ContinuousTransition) bna).isKnockedOut());
			}
		}
		return newBNA;
	}

	private static BiologicalNodeAbstract fillStochasticTransition(BiologicalNodeAbstract newBNA,
			BiologicalNodeAbstract bna) {
		if (bna != null) {
			if (newBNA instanceof StochasticTransition && bna instanceof StochasticTransition) {
				((StochasticTransition) newBNA).setDistribution(((StochasticTransition) bna).getDistribution());
				((StochasticTransition) newBNA).setKnockedOut(((StochasticTransition) bna).isKnockedOut());
			}
		}
		return newBNA;
	}
}