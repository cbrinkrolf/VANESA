package biologicalObjects.edges;

import biologicalElements.Elementdeclerations;
import biologicalObjects.edges.petriNet.PNArc;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import transformation.graphElements.ANYBiologicalEdge;

public class BiologicalEdgeAbstractFactory {
	// creating new object with attributes of given bna (could be null)
	public static BiologicalEdgeAbstract create(final String type, final BiologicalNodeAbstract from,
			final BiologicalNodeAbstract to, final String label, final String name) {
		switch (type) {
		case Elementdeclerations.activationEdge:
			return new Activation(label, name, from, to);
		case Elementdeclerations.bindingEdge:
			return new BindingAssociation(label, name, from, to);
		case Elementdeclerations.compoundEdge:
			return new Compound(label, name, from, to);
		case Elementdeclerations.dephosphorylationEdge:
			return new Dephosphorylation(label, name, from, to);
		case Elementdeclerations.dissociationEdge:
			return new Dissociation(label, name, from, to);
		case Elementdeclerations.expressionEdge:
			return new Expression(label, name, from, to);
		case Elementdeclerations.glycosylationEdge:
			return new Glycosylation(label, name, from, to);
		case Elementdeclerations.hiddenCompoundEdge:
			return new HiddenCompound(label, name, from, to);
		case Elementdeclerations.indirectEffectEdge:
			return new IndirectEffect(label, name, from, to);
		case Elementdeclerations.inhibitionEdge:
			return new Inhibition(label, name, from, to);
		case Elementdeclerations.methylationEdge:
			return new Methylation(label, name, from, to);
		case Elementdeclerations.phosphorylationEdge:
			return new Phosphorylation(label, name, from, to);
		case Elementdeclerations.physicalInteraction:
			return new PhysicalInteraction(label, name, from, to);
		case Elementdeclerations.reactionPairEdge:
			return new ReactionPair(label, name, from, to);
		case Elementdeclerations.repressionEdge:
			return new Repression(label, name, from, to);
		case Elementdeclerations.stateChangeEdge:
			return new StateChange(label, name, from, to);
		case Elementdeclerations.ubiquitinationEdge:
			return new Ubiquitination(label, name, from, to);
		case Elementdeclerations.anyBEA:
			return new ANYBiologicalEdge(label, name, from, to);
		case "PN Discrete Edge": // old description
		case "PN Edge": // old description
		case Elementdeclerations.pnArc:
			return new PNArc(from, to, label, name, Elementdeclerations.pnArc, "1");
		case "PN Test Edge": // old description
		case Elementdeclerations.pnTestArc:
			return new PNArc(from, to, label, name, Elementdeclerations.pnTestArc, "1");
		case "PN Inhibition Edge": // old description
		case Elementdeclerations.pnInhibitorArc:
			return new PNArc(from, to, label, name, Elementdeclerations.pnInhibitorArc, "1");
		case Elementdeclerations.reactionEdge:
		default:
			return new ReactionEdge(label, name, from, to);
		}
	}
}
