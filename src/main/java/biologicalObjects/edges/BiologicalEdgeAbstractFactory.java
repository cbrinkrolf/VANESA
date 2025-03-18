package biologicalObjects.edges;

import biologicalElements.ElementDeclarations;
import biologicalObjects.edges.petriNet.PNArc;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import transformation.graphElements.ANYBiologicalEdge;

public class BiologicalEdgeAbstractFactory {
	public static BiologicalEdgeAbstract create(final String type, final String label, final String name,
			final BiologicalNodeAbstract from, final BiologicalNodeAbstract to) {
		switch (type) {
		case ElementDeclarations.activationEdge:
			return new Activation(label, name, from, to);
		case ElementDeclarations.bindingEdge:
			return new BindingAssociation(label, name, from, to);
		case ElementDeclarations.compoundEdge:
			return new Compound(label, name, from, to);
		case ElementDeclarations.dephosphorylationEdge:
			return new Dephosphorylation(label, name, from, to);
		case ElementDeclarations.dissociationEdge:
			return new Dissociation(label, name, from, to);
		case ElementDeclarations.expressionEdge:
			return new Expression(label, name, from, to);
		case ElementDeclarations.glycosylationEdge:
			return new Glycosylation(label, name, from, to);
		case ElementDeclarations.hiddenCompoundEdge:
			return new HiddenCompound(label, name, from, to);
		case ElementDeclarations.indirectEffectEdge:
			return new IndirectEffect(label, name, from, to);
		case ElementDeclarations.inhibitionEdge:
			return new Inhibition(label, name, from, to);
		case ElementDeclarations.methylationEdge:
			return new Methylation(label, name, from, to);
		case ElementDeclarations.phosphorylationEdge:
			return new Phosphorylation(label, name, from, to);
		case ElementDeclarations.physicalInteraction:
			return new PhysicalInteraction(label, name, from, to);
		case ElementDeclarations.reactionPairEdge:
			return new ReactionPair(label, name, from, to);
		case ElementDeclarations.repressionEdge:
			return new Repression(label, name, from, to);
		case ElementDeclarations.stateChangeEdge:
			return new StateChange(label, name, from, to);
		case ElementDeclarations.ubiquitinationEdge:
			return new Ubiquitination(label, name, from, to);
		case ElementDeclarations.anyBEA:
			return new ANYBiologicalEdge(label, name, from, to);
		case "PN Discrete Edge":
			//old description
		case "PN Edge":
			//old description
		case ElementDeclarations.pnArc:
			return new PNArc(from, to, label, name, ElementDeclarations.pnArc, "1");
		case "PN Test Edge":
			//old description
		case ElementDeclarations.pnTestArc:
			return new PNArc(from, to, label, name, ElementDeclarations.pnTestArc, "1");
		case "PN Inhibition Edge":
			//old description
		case ElementDeclarations.pnInhibitorArc:
			return new PNArc(from, to, label, name, ElementDeclarations.pnInhibitorArc, "1");
		case ElementDeclarations.reactionEdge:
		default:
			return new ReactionEdge(label, name, from, to);
		}
	}
}
