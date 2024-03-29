package biologicalElements;

import java.util.ArrayList;
import java.util.List;

public class Elementdeclerations {
	
	public static final String graphdbnode = "GraphDatabase Node";

	public static final String disease = "Disease";

	public static final String compound = "Compound";

	public static final String drug = "Drug";

	public static final String inhibitor = "Inhibitor";

	public static final String others = "Other";

	public static final String complex = "Complex";

	public static final String enzyme = "Enzyme";

	public static final String degraded = "Degraded";

	public static final String dna = "DNA";

	public static final String homodimerFormation = "Homodimer Formation";

	public static final String ligandBinding = "Ligand Binding";

	public static final String membraneChannel = "Membrane Channel";

	public static final String membraneReceptor = "Membrane Receptor";

	public static final String mRNA = "mRNA";

	public static final String miRNA = "miRNA";

	public static final String lncRNA = "lncRNA";

	public static final String orthologGroup = "Ortholog Group";

	public static final String pathwayMap = "Pathway Map";

	public static final String pathwayNode = "Pathway";

	public static final String protein = "Protein";

	public static final String receptor = "Receptor";

	public static final String sRNA = "sRNA";

	// kept for legacy
	public static final String smallMolecule = "Small Molecule";
	
	public static final String metabolite = "Metabolite";

	public static final String glycan = "Glycan";

	public static final String solubleReceptor = "Soluble Receptor";

	public static final String transcriptionFactor = "Transcription Factor";

	public static final String compoundEdge = "compound";

	public static final String hiddenCompoundEdge = "hidden compound";

	public static final String activationEdge = "activation";

	public static final String inhibitionEdge = "inhibition";

	public static final String expressionEdge = "expression";

	public static final String repressionEdge = "repression";

	public static final String indirectEffectEdge = "indirect effect";

	public static final String stateChangeEdge = "state change";

	public static final String bindingEdge = "binding/association";

	public static final String dissociationEdge = "dissociation";

	public static final String phosphorylationEdge = "phosphorylation";

	public static final String dephosphorylationEdge = "dephosphorylation";

	public static final String glycosylationEdge = "glycosylation";

	public static final String ubiquitinationEdge = "ubiquitination";

	public static final String methylationEdge = "methylation";

	public static final String reactionEdge = "reaction";

	public static final String reaction = "Reaction";

	public static final String reactionPair = "Reaction Pair";

	public static final String reactionPairEdge = "Reaction Pair";

	// public static final String interaction = "Interaction";

	public static final String feature = "Feature";

	public static final String gene = "Gene";

	public static final String symptomEdge = "Symptom";

	public static final String domain = "Domain";

	public static final String location = "Location";

	public static final String go = "Gene Ontology";

	public static final String collector = "Collector";

	public static final String rna = "RNA";

	public static final String factor = "Factor";

	public static final String site = "Site";

	public static final String exon = "Exon";

	public static final String fragment = "Fragment";

	public static final String physicalInteraction = "Physical interaction";

	public static final String matrix = "Matrix";
	
	public static final String place = "Place";

	public static final String discretePlace = "Discrete Place";

	public static final String continuousPlace = "Continuous Place";

	public static final String transition = "Transition";

	public static final String stochasticTransition = "Stochastic Transition";

	public static final String discreteTransition = "Discrete Transition";

	public static final String continuousTransition = "Continuous Transition";

	// public static String edge = "Petri Net Edge";

	public static final String pnArc = "PN Arc";
	
	public static final String pnTestArc = "PN Test Arc";

	public static final String pnInhibitorArc = "PN Inhibitor Arc";
	
	//special edges and nodes not in collections
	public static final String anyBEA = "ANY Biological Edge";
	public static final String anyBNA = "ANY Biological Node";

	public List<String> getAllNodeDeclarations() {
		List<String> l = new ArrayList<>();
		l.add(others);
		l.add(complex);
		l.add(degraded);
		l.add(dna);
		l.add(homodimerFormation);
		l.add(ligandBinding);
		l.add(membraneChannel);
		l.add(membraneReceptor);
		l.add(mRNA);
		l.add(miRNA);
		l.add(lncRNA);
		l.add(orthologGroup);
		l.add(pathwayMap);
		l.add(protein);
		l.add(receptor);
		l.add(domain);
		l.add(sRNA);
		l.add(metabolite);
		l.add(solubleReceptor);
		l.add(transcriptionFactor);
		l.add(enzyme);
		l.add(compound);
		l.add(glycan);
		l.add(disease);
		l.add(drug);
		l.add(go);
		l.add(reaction);
		l.add(collector);
		l.add(factor);
		l.add(site);
		l.add(exon);
		l.add(fragment);
		l.add(matrix);
		l.add(discretePlace);
		l.add(continuousPlace);
		l.add(stochasticTransition);
		l.add(discreteTransition);
		l.add(continuousTransition);
		l.add(inhibitor);
		l.sort(String.CASE_INSENSITIVE_ORDER);
		return l;
	}

	public List<String> getNotPNNodeDeclarations() {
		List<String> l = new ArrayList<>();
		l.add(others);
		l.add(complex);
		l.add(degraded);
		l.add(dna);
		l.add(homodimerFormation);
		l.add(ligandBinding);
		l.add(membraneChannel);
		l.add(membraneReceptor);
		l.add(mRNA);
		l.add(miRNA);
		l.add(lncRNA);
		l.add(orthologGroup);
		l.add(pathwayMap);
		l.add(protein);
		l.add(receptor);
		l.add(domain);
		l.add(sRNA);
		l.add(metabolite);
		l.add(solubleReceptor);
		l.add(transcriptionFactor);
		l.add(enzyme);
		l.add(compound);
		l.add(glycan);
		l.add(disease);
		l.add(drug);
		l.add(go);
		l.add(reaction);
		l.add(collector);
		l.add(factor);
		l.add(site);
		l.add(exon);
		l.add(fragment);
		l.add(matrix);
		l.add(inhibitor);
		l.sort(String.CASE_INSENSITIVE_ORDER);
		return l;
	}

	public List<String> getPNEdgeDeclarations() {
		List<String> l = new ArrayList<>();
		l.add(pnArc);
		l.add(pnTestArc);
		l.add(pnInhibitorArc);
		l.sort(String.CASE_INSENSITIVE_ORDER);
		return l;
	}

	public List<String> getNotPNEdgeDeclarations() {
		List<String> l = new ArrayList<>();
		l.add(compoundEdge);
		l.add(hiddenCompoundEdge);
		l.add(activationEdge);
		l.add(inhibitionEdge);
		l.add(expressionEdge);
		l.add(repressionEdge);
		l.add(indirectEffectEdge);
		l.add(stateChangeEdge);
		l.add(bindingEdge);
		l.add(dissociationEdge);
		l.add(phosphorylationEdge);
		l.add(dephosphorylationEdge);
		l.add(glycosylationEdge);
		l.add(ubiquitinationEdge);
		l.add(methylationEdge);
		l.add(reactionEdge);
		l.add(reactionPair);
		l.sort(String.CASE_INSENSITIVE_ORDER);
		return l;
	}

	public List<String> getAllEdgeDeclarations() {
		List<String> l = new ArrayList<>();
		l.add(compoundEdge);
		l.add(hiddenCompoundEdge);
		l.add(activationEdge);
		l.add(inhibitionEdge);
		l.add(expressionEdge);
		l.add(repressionEdge);
		l.add(indirectEffectEdge);
		l.add(stateChangeEdge);
		l.add(bindingEdge);
		l.add(dissociationEdge);
		l.add(phosphorylationEdge);
		l.add(dephosphorylationEdge);
		l.add(glycosylationEdge);
		l.add(ubiquitinationEdge);
		l.add(methylationEdge);
		l.add(reactionEdge);
		l.add(reactionPair);
		l.add(pnArc);
		l.add(pnTestArc);
		l.add(pnInhibitorArc);
		l.sort(String.CASE_INSENSITIVE_ORDER);
		return l;
	}
}
