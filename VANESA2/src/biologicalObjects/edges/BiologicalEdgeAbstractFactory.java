package biologicalObjects.edges;

import biologicalElements.Elementdeclerations;
import biologicalObjects.edges.petriNet.PNEdge;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class BiologicalEdgeAbstractFactory {

	// creating new object with attributes of given bna (could be null)
	public static BiologicalEdgeAbstract create(String elementDeclaration, BiologicalEdgeAbstract bea) {
		BiologicalEdgeAbstract newBea;

		String name = "";
		String label = "";
		BiologicalNodeAbstract from = null;
		BiologicalNodeAbstract to = null;
		if (bea != null) {
			name = bea.getName();
			label = bea.getLabel();
			from = bea.getFrom();
			to = bea.getTo();
		}

		switch (elementDeclaration) {
		case Elementdeclerations.activationEdge:
			newBea = new Activation(label, name, from, to);
			break;
		case Elementdeclerations.bindingEdge:
			newBea = new BindingAssociation(label, name, from, to);
			break;
		case Elementdeclerations.compoundEdge:
			newBea = new Compound(label, name, from, to);
			break;
		case Elementdeclerations.dephosphorylationEdge:
			newBea = new Dephosphorylation(label, name, from, to);
			break;
		case Elementdeclerations.dissociationEdge:
			newBea = new Dissociation(label, name, from, to);
			break;
		case Elementdeclerations.expressionEdge:
			newBea = new Expression(label, name, from, to);
			break;
		case Elementdeclerations.glycosylationEdge:
			newBea = new Glycosylation(label, name, from, to);
			break;
		case Elementdeclerations.hiddenCompoundEdge:
			newBea = new HiddenCompound(label, name, from, to);
			break;
		case Elementdeclerations.indirectEffectEdge:
			newBea = new IndirectEffect(label, name, from, to);
			break;
		case Elementdeclerations.inhibitionEdge:
			newBea = new Inhibition(label, name, from, to);
			break;
		case Elementdeclerations.methylationEdge:
			newBea = new Methylation(label, name, from, to);
			break;
		case Elementdeclerations.phosphorylationEdge:
			newBea = new Phosphorylation(label, name, from, to);
			break;
		case Elementdeclerations.physicalInteraction:
			newBea = new PhysicalInteraction(label, name, from, to);
			break;
		case Elementdeclerations.reactionEdge:
			newBea = new ReactionEdge(label, name, from, to);
			break;
		case Elementdeclerations.reactionPairEdge:
			newBea = new ReactionPair(label, name, from, to);
			break;
		case Elementdeclerations.repressionEdge:
			newBea = new Repression(label, name, from, to);
			break;
		case Elementdeclerations.stateChangeEdge:
			newBea = new StateChange(label, name, from, to);
			break;
		case Elementdeclerations.ubiquitinationEdge:
			newBea = new Ubiquitination(label, name, from, to);
			break;
		case "PN Discrete Edge":
			//old description
		case Elementdeclerations.pnEdge:
			newBea = new PNEdge(from, to, label, name, Elementdeclerations.pnEdge, "1");
			fillPNEdge(newBea, newBea);
			break;
		case Elementdeclerations.pnTestEdge:
			newBea = new PNEdge(from, to, label, name, Elementdeclerations.pnTestEdge, "1");
			fillPNEdge(newBea, newBea);
			break;
		case Elementdeclerations.pnInhibitionEdge:
			newBea = new PNEdge(from, to, label, name, Elementdeclerations.pnInhibitionEdge, "1");
			fillPNEdge(newBea, newBea);
			break;
		default:
			newBea = new ReactionEdge(label, name, from, to);
			break;
		}
		return newBea;
	}
	
	private static void fillPNEdge(BiologicalEdgeAbstract newBea, BiologicalEdgeAbstract bea){
		if(newBea instanceof PNEdge && bea instanceof PNEdge){
			((PNEdge)newBea).setActivationProbability(((PNEdge)bea).getActivationProbability());
			((PNEdge)newBea).setFunction(((PNEdge)bea).getFunction());
			((PNEdge)newBea).setLowerBoundary(((PNEdge)bea).getLowerBoundary());
			((PNEdge)newBea).setUpperBoundary(((PNEdge)bea).getUpperBoundary());
		}
	}
}
