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
	public static BiologicalNodeAbstract create(String elementDeclaration, BiologicalNodeAbstract bna) {
		BiologicalNodeAbstract newBNA;

		String name = "";
		String label = "";
		if (bna != null) {
			name = bna.getName();
			label = bna.getLabel();
		}

		switch (elementDeclaration) {
		case Elementdeclerations.collector:
			newBNA = new Collector(name, label);
			break;
		case Elementdeclerations.complex:
			newBNA = new Complex(label, name);
			break;
		case Elementdeclerations.compound:
			newBNA = new CompoundNode(label, name);
			break;
		case Elementdeclerations.degraded:
			newBNA = new Degraded(label, name);
			break;
		case Elementdeclerations.disease:
			newBNA = new Disease(label, name);
			break;
		case Elementdeclerations.dna:
			newBNA = new DNA(label, name);
			fillDNA(newBNA, bna);
			break;
		case Elementdeclerations.domain:
			newBNA = new Domain(label, name);
			break;
		case Elementdeclerations.drug:
			newBNA = new Drug(label, name);
			break;
		case Elementdeclerations.enzyme:
			newBNA = new Enzyme(label, name);
			break;
		case Elementdeclerations.exon:
			newBNA = new Exon(label, name);
			break;
		case Elementdeclerations.factor:
			newBNA = new Factor(label, name);
			break;
		case Elementdeclerations.fragment:
			newBNA = new Fragment(label, name);
			break;
		case Elementdeclerations.gene:
			newBNA = new Gene(label, name);
			break;
		case Elementdeclerations.glycan:
			newBNA = new Glycan(label, name);
			break;
		case Elementdeclerations.homodimerFormation:
			newBNA = new HomodimerFormation(label, name);
			break;
		case Elementdeclerations.inhibitor:
			newBNA = new Inhibitor(label, name);
			break;
		case Elementdeclerations.ligandBinding:
			newBNA = new LigandBinding(label, name);
			break;
		case Elementdeclerations.matrix:
			newBNA = new Matrix(label, name);
			break;
		case Elementdeclerations.membraneChannel:
			newBNA = new MembraneChannel(label, name);
			break;
		case Elementdeclerations.membraneReceptor:
			newBNA = new MembraneReceptor(label, name);
			break;
		case Elementdeclerations.mRNA:
			newBNA = new MRNA(label, name);
			fillRNA(newBNA, bna);
			break;
		case Elementdeclerations.miRNA:
			newBNA = new MIRNA(label, name);
			fillRNA(newBNA, bna);
			break;
		case Elementdeclerations.lncRNA:
			newBNA = new LNCRNA(label, name);
			fillRNA(newBNA, bna);
			break;
		case Elementdeclerations.orthologGroup:
			newBNA = new OrthologGroup(label, name);
			break;
		case Elementdeclerations.others:
			newBNA = new Other(label, name);
			break;
		case Elementdeclerations.pathwayMap:
			newBNA = new PathwayMap(label, name);
			fillPathwayMap(newBNA, bna);
			break;
		case Elementdeclerations.protein:
			newBNA = new Protein(label, name);
			break;
		case Elementdeclerations.reaction:
			newBNA = new Reaction(label, name);
			break;
		case Elementdeclerations.receptor:
			newBNA = new Receptor(label, name);
			break;
		case Elementdeclerations.rna:
			newBNA = new RNA(label, name);
			break;
		case Elementdeclerations.site:
			newBNA = new Site(label, name);
			break;
		// kept for legacy
		case Elementdeclerations.smallMolecule:
			newBNA = new Metabolite(label, name);
			break;
		case Elementdeclerations.metabolite:
			newBNA = new Metabolite(label, name);
			break;
		case Elementdeclerations.solubleReceptor:
			newBNA = new SolubleReceptor(label, name);
			break;
		case Elementdeclerations.sRNA:
			newBNA = new SRNA(label, name);
			fillRNA(newBNA, bna);
			break;
		case Elementdeclerations.transcriptionFactor:
			newBNA = new TranscriptionFactor(label, name);
			break;

		case Elementdeclerations.discretePlace:
			newBNA = new DiscretePlace(label, name);
			fillPlace(newBNA, bna);
			break;
		case Elementdeclerations.continuousPlace:
			newBNA = new ContinuousPlace(label, name);
			fillPlace(newBNA, bna);
			break;
		case Elementdeclerations.discreteTransition:
			newBNA = new DiscreteTransition(label, name);
			fillDiscreteTransition(newBNA, bna);
			break;
		case Elementdeclerations.continuousTransition:
			newBNA = new ContinuousTransition(label, name);
			fillContinuousTransition(newBNA, bna);
			break;
		case Elementdeclerations.stochasticTransition:
			newBNA = new StochasticTransition(label, name);
			fillStochasticTransition(newBNA, bna);
			break;
		case Elementdeclerations.anyBNA:
			newBNA = new ANYBiologicalNode(label, name);
			break;
		case Elementdeclerations.place:
			newBNA = new ANYPlace(label, name);
			break;
		case Elementdeclerations.transition:
			newBNA = new ANYTransition(label, name);
			break;
		default:
			newBNA = new Other(label, name);
		}
		return newBNA;
	}

	private static void fillRNA(BiologicalNodeAbstract newBNA, BiologicalNodeAbstract bna) {
		if (bna != null) {
			if (newBNA instanceof RNA && bna instanceof RNA) {
				((RNA) newBNA).setNtSequence(((RNA) bna).getNtSequence());
				((RNA) newBNA).setLogFC(((RNA) bna).getLogFC());
			}
		}
	}

	private static void fillDNA(BiologicalNodeAbstract newBNA, BiologicalNodeAbstract bna) {
		if (bna != null) {
			if (newBNA instanceof DNA && bna instanceof DNA) {
				((DNA) newBNA).setNtSequence(((DNA) bna).getNtSequence());
				((DNA) newBNA).setLogFC(((DNA) bna).getLogFC());
			}
		}
	}

	private static void fillPathwayMap(BiologicalNodeAbstract newBNA, BiologicalNodeAbstract bna) {
		if (bna != null) {
			if (newBNA instanceof PathwayMap && bna instanceof PathwayMap) {
				((PathwayMap) newBNA).setPathwayLink(((PathwayMap) bna).getPathwayLink());
			}
		}
	}

	private static void fillPlace(BiologicalNodeAbstract newBNA, BiologicalNodeAbstract bna) {
		if (bna != null) {
			if (newBNA instanceof Place && bna instanceof Place) {
				((Place) newBNA).setToken(((Place) bna).getToken());
				((Place) newBNA).setTokenMin(((Place) bna).getTokenMin());
				((Place) newBNA).setTokenMax(((Place) bna).getTokenMax());
				((Place) newBNA).setTokenStart(((Place) bna).getTokenStart());
			}
		}
	}

	private static void fillDiscreteTransition(BiologicalNodeAbstract newBNA, BiologicalNodeAbstract bna) {
		if (bna != null) {
			if (newBNA instanceof DiscreteTransition && bna instanceof DiscreteTransition) {
				((DiscreteTransition) newBNA).setDelay(((DiscreteTransition) bna).getDelay());
				((DiscreteTransition) newBNA).setKnockedOut(((DiscreteTransition) bna).isKnockedOut());
			}
		}
	}

	private static void fillContinuousTransition(BiologicalNodeAbstract newBNA, BiologicalNodeAbstract bna) {
		if (bna != null) {
			if (newBNA instanceof ContinuousTransition && bna instanceof ContinuousTransition) {
				((ContinuousTransition) newBNA).setMaximalSpeed(((ContinuousTransition) bna).getMaximalSpeed());
				((ContinuousTransition) newBNA).setKnockedOut(((ContinuousTransition) bna).isKnockedOut());
			}
		}
	}

	private static void fillStochasticTransition(BiologicalNodeAbstract newBNA, BiologicalNodeAbstract bna) {
		if (bna != null) {
			if (newBNA instanceof StochasticTransition && bna instanceof StochasticTransition) {
				((StochasticTransition) newBNA).setDistribution(((StochasticTransition) bna).getDistribution());
				((StochasticTransition) newBNA).setKnockedOut(((StochasticTransition) bna).isKnockedOut());
			}
		}
	}
}