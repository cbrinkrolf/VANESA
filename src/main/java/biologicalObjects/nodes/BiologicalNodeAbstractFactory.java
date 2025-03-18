package biologicalObjects.nodes;

import biologicalElements.ElementDeclarations;
import biologicalElements.Pathway;
import biologicalObjects.nodes.petriNet.*;
import transformation.graphElements.ANYBiologicalNode;
import transformation.graphElements.ANYPlace;
import transformation.graphElements.ANYTransition;

public class BiologicalNodeAbstractFactory {
	public static BiologicalNodeAbstract copy(final BiologicalNodeAbstract bna, final Pathway pathway) {
		return create(bna.getBiologicalElement(), bna, bna.getLabel() != null ? bna.getLabel() : "",
				bna.getName() != null ? bna.getName() : "", pathway);
	}

	public static BiologicalNodeAbstract create(final String type, final String label, final String name,
			final Pathway pathway) {
		return create(type, null, label != null ? label : "", name != null ? name : "", pathway);
	}

	private static BiologicalNodeAbstract create(final String type, final BiologicalNodeAbstract bna,
			final String label, final String name, final Pathway pathway) {
		switch (type) {
		case ElementDeclarations.collector:
			return new Collector(label, name, pathway);
		case ElementDeclarations.complex:
			return new Complex(label, name, pathway);
		case ElementDeclarations.compound:
			return new CompoundNode(label, name, pathway);
		case ElementDeclarations.degraded:
			return new Degraded(label, name, pathway);
		case ElementDeclarations.disease:
			return new Disease(label, name, pathway);
		case ElementDeclarations.dna:
			return copyProperties(new DNA(label, name, pathway), bna);
		case ElementDeclarations.domain:
			return new Domain(label, name, pathway);
		case ElementDeclarations.drug:
			return new Drug(label, name, pathway);
		case ElementDeclarations.enzyme:
			return new Enzyme(label, name, pathway);
		case ElementDeclarations.exon:
			return new Exon(label, name, pathway);
		case ElementDeclarations.factor:
			return new Factor(label, name, pathway);
		case ElementDeclarations.fragment:
			return new Fragment(label, name, pathway);
		case ElementDeclarations.gene:
			return new Gene(label, name, pathway);
		case ElementDeclarations.glycan:
			return new Glycan(label, name, pathway);
		case ElementDeclarations.homodimerFormation:
			return new HomodimerFormation(label, name, pathway);
		case ElementDeclarations.inhibitor:
			return new Inhibitor(label, name, pathway);
		case ElementDeclarations.ligandBinding:
			return new LigandBinding(label, name, pathway);
		case ElementDeclarations.matrix:
			return new Matrix(label, name, pathway);
		case ElementDeclarations.membraneChannel:
			return new MembraneChannel(label, name, pathway);
		case ElementDeclarations.membraneReceptor:
			return new MembraneReceptor(label, name, pathway);
		case ElementDeclarations.mRNA:
			return copyProperties(new MRNA(label, name, pathway), bna);
		case ElementDeclarations.miRNA:
			return copyProperties(new MIRNA(label, name, pathway), bna);
		case ElementDeclarations.lncRNA:
			return copyProperties(new LNCRNA(label, name, pathway), bna);
		case ElementDeclarations.orthologGroup:
			return new OrthologGroup(label, name, pathway);
		case ElementDeclarations.pathwayMap:
			return copyProperties(new PathwayMap(label, name, pathway), bna);
		case ElementDeclarations.protein:
			return new Protein(label, name, pathway);
		case ElementDeclarations.reaction:
			return new Reaction(label, name, pathway);
		case ElementDeclarations.receptor:
			return new Receptor(label, name, pathway);
		case ElementDeclarations.rna:
			return new RNA(label, name, pathway);
		case ElementDeclarations.site:
			return new Site(label, name, pathway);
		case ElementDeclarations.smallMolecule:
		case ElementDeclarations.metabolite:
			return new Metabolite(label, name, pathway);
		case ElementDeclarations.solubleReceptor:
			return new SolubleReceptor(label, name, pathway);
		case ElementDeclarations.sRNA:
			return copyProperties(new SRNA(label, name, pathway), bna);
		case ElementDeclarations.transcriptionFactor:
			return new TranscriptionFactor(label, name, pathway);
		case ElementDeclarations.discretePlace:
			copyProperties(new DiscretePlace(label, name, pathway), bna);
		case ElementDeclarations.continuousPlace:
			return copyProperties(new ContinuousPlace(label, name, pathway), bna);
		case ElementDeclarations.discreteTransition:
			return copyProperties(new DiscreteTransition(label, name, pathway), bna);
		case ElementDeclarations.continuousTransition:
			return copyProperties(new ContinuousTransition(label, name, pathway), bna);
		case ElementDeclarations.stochasticTransition:
			return copyProperties(new StochasticTransition(label, name, pathway), bna);
		case ElementDeclarations.anyBNA:
			return new ANYBiologicalNode(label, name, pathway);
		case ElementDeclarations.place:
			return new ANYPlace(label, name, pathway);
		case ElementDeclarations.transition:
			return new ANYTransition(label, name, pathway);
		case ElementDeclarations.others:
		default:
			return new Other(label, name, pathway);
		}
	}

	private static BiologicalNodeAbstract copyProperties(RNA newBNA, BiologicalNodeAbstract bna) {
		if (bna instanceof RNA) {
			newBNA.setNtSequence(((RNA) bna).getNtSequence());
			newBNA.setLogFC(((RNA) bna).getLogFC());
		}
		return newBNA;
	}

	private static BiologicalNodeAbstract copyProperties(DNA newBNA, BiologicalNodeAbstract bna) {
		if (bna instanceof DNA) {
			newBNA.setNtSequence(((DNA) bna).getNtSequence());
			newBNA.setLogFC(((DNA) bna).getLogFC());
		}
		return newBNA;
	}

	private static BiologicalNodeAbstract copyProperties(PathwayMap newBNA, BiologicalNodeAbstract bna) {
		if (bna instanceof PathwayMap) {
			newBNA.setPathwayLink(((PathwayMap) bna).getPathwayLink());
		}
		return newBNA;
	}

	private static BiologicalNodeAbstract copyProperties(Place newBNA, BiologicalNodeAbstract bna) {
		if (bna instanceof Place) {
			newBNA.setToken(((Place) bna).getToken());
			newBNA.setTokenMin(((Place) bna).getTokenMin());
			newBNA.setTokenMax(((Place) bna).getTokenMax());
			newBNA.setTokenStart(((Place) bna).getTokenStart());
		}
		return newBNA;
	}

	private static BiologicalNodeAbstract copyProperties(DiscreteTransition newBNA, BiologicalNodeAbstract bna) {
		if (bna instanceof DiscreteTransition) {
			newBNA.setDelay(((DiscreteTransition) bna).getDelay());
			newBNA.setKnockedOut(((Transition) bna).isKnockedOut());
		}
		return newBNA;
	}

	private static BiologicalNodeAbstract copyProperties(ContinuousTransition newBNA, BiologicalNodeAbstract bna) {
		if (bna instanceof ContinuousTransition) {
			newBNA.setMaximalSpeed(((ContinuousTransition) bna).getMaximalSpeed());
			newBNA.setKnockedOut(((Transition) bna).isKnockedOut());
		}
		return newBNA;
	}

	private static BiologicalNodeAbstract copyProperties(StochasticTransition newBNA, BiologicalNodeAbstract bna) {
		if (bna instanceof StochasticTransition) {
			newBNA.setDistribution(((StochasticTransition) bna).getDistribution());
			newBNA.setKnockedOut(((Transition) bna).isKnockedOut());
		}
		return newBNA;
	}
}