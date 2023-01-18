package biologicalObjects.nodes;

import java.util.Iterator;
import java.util.Vector;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KEGGNode extends KEGGNodeGraphicRepresentation {

	private String KEGGentryID = "";
	private String KEGGentryMap = "";
	private String KEGGentryName = "";
	private String KEGGentryType = "";
	private String KEGGentryLink = "";
	private String KEGGentryReaction = "";
	private String KEGGComponent = "";
	private String KEGGPathway = "";
	private String KeggComment = "";
	private String KeggenzymeClass = "";
	private String KeggsysName = "";
	private String Keggreaction = "";
	private String Keggsubstrate = "";
	private String Keggproduct = "";
	private String Keggcofactor = "";
	private String Keggreference = "";
	private String Keggeffector = "";
	private String Keggorthology = "";

	private String compoundFormula = "";
	private String compoundMass = "";
	private String compoundComment = "";
	private String compoundRemarks = "";
	private String compoundAtomsNr = "";
	private String compoundAtoms = "";
	private String compoundBondNr = "";
	private String compoundBonds = "";
	private String compoundSequence = "";
	private String compoundModule = "";
	private String compoundOrganism = "";

	private String geneName = "";
	private String geneDefinition = "";
	private String genePosition = "";
	private String geneCodonUsage = "";
	private String geneAAseqNr = "";
	private String geneAAseq = "";
	private String geneNtseqNr = "";
	private String geneNtSeq = "";
	private String geneOrthology = "";
	private String geneOrthologyName = "";

	private String geneEnzyme = "";
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private Vector<String> allKeggNames = new Vector<String>();
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private Vector<String> allDBLinks = new Vector<String>();
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private Vector<String> allPathways = new Vector<String>();
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private Vector<String> allStructures = new Vector<String>();
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private Vector<String> geneMotifs = new Vector<String>();
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private Vector<String> involvedWith = new Vector<String>();

	private String glycanOrthology = "";
	private String glycanBracket = "";
	private String glycanComposition = "";
	private String glycanNode = "";
	private String glycanEdge = "";
	private String glycanName = "";

	public KEGGNode() {

	}

	public void addAlternativeName(String name) {
		allKeggNames.add(name);
	}

	public String getAllInvolvedElements() {

		Iterator<String> it = involvedWith.iterator();
		String results = "";
		while (it.hasNext()) {

			results = results + it.next() + " ";
		}

		return results;
	}

	public void addInvolvedElement(String name) {
		involvedWith.add(name);
	}

	public String getAllNames() {

		Iterator<String> it = allKeggNames.iterator();
		String results = "";
		while (it.hasNext()) {

			results = results + it.next() + " ";
		}

		return results;
	}

	public Vector<String> getAllNamesAsVector() {
		return allKeggNames;
	}

	public void addDBLink(String name) {
		allDBLinks.add(name);
	}

	public String getAllDBLinks() {

		Iterator<String> it = allDBLinks.iterator();
		String results = "";
		while (it.hasNext()) {

			results = results + it.next() + " ";
		}

		return results;
	}

	public Vector<String> getAllDBLinksAsVector() {
		return allDBLinks;
	}

	public void addGeneMotif(String name) {
		geneMotifs.add(name);
	}

	public String getAllGeneMotifs() {

		Iterator<String> it = geneMotifs.iterator();
		String results = "";
		while (it.hasNext()) {

			results = results + it.next() + " ";
		}

		return results;
	}

	public Vector<String> getAllGeneMotifsAsVector() {
		return geneMotifs;
	}

	public void addPathwayLink(String name) {
		allPathways.add(name);
	}

	public String getAllPathwayLinks() {

		Iterator<String> it = allPathways.iterator();
		String results = "";
		while (it.hasNext()) {

			results = results + it.next() + " ";
		}

		return results;
	}

	public Vector<String> getAllPathwayLinksAsVector() {
		return allPathways;
	}

	public void addStructure(String name) {
		allStructures.add(name);
	}

	public String getAllStructures() {

		Iterator<String> it = allStructures.iterator();
		String results = "";
		while (it.hasNext()) {

			results = results + it.next() + " ";
		}

		return results;
	}

	public Vector<String> getAllStructuresAsVector() {
		return allStructures;
	}

	public Object[][] getKeggDeatails() {

		if (KEGGentryType.equals("enzyme")) {

			Object[][] values = { { "Name", getKEGGentryName() }, { "SysName", getKeggsysName() },
					{ "Alternative Names", getAllNames() }, { "Pathway", getKEGGPathway() },
					{ "Class", getKeggenzymeClass() }, { "Orthology", getKeggorthology() },
					{ "Reaction", getKeggreaction() }, { "Substrate", getKeggsubstrate() },
					{ "Product", getKeggproduct() }, { "Cofactor", getKeggcofactor() },
					{ "Effector", getKeggeffector() }, { "PDB-Structure", getAllStructures() },
					{ "DB-Links", getAllDBLinks() }, { "DB-Pathways", getAllPathwayLinks() },
					{ "Comment", getKeggComment() }, { "References", getKeggreference() }, };
			return values;
		} else if (KEGGentryType.equals("compound")) {

			if (KEGGentryName.startsWith("G") || KEGGentryName.startsWith("g")) {

				Object[][] values = { { "Name", getKEGGentryName() }, { "Alternative Name", getGlycanName() },
						{ "Pathway", getKEGGPathway() }, { "Mass", getCompoundMass() },
						{ "Orthology", getGlycanOrthology() }, { "Bracket", getGlycanBracket() },
						{ "Composition", getGlycanComposition() }, { "Node", getGlycanNode() },
						{ "Edge", getGlycanEdge() }, { "Interaction With", getAllInvolvedElements() },
						{ "Comment", getCompoundComment() }, { "Remark", getCompoundRemarks() },
						{ "References", getKeggreference() }, { "DB-Pathways", getAllPathwayLinks() },
						{ "DB-Links", getAllDBLinks() }

				};

				return values;

			} else {
				Object[][] values = { { "Name", getKEGGentryName() }, { "Alternative Names", getAllNames() },
						{ "Pathway", getKEGGPathway() }, { "Formula", getCompoundFormula() },
						{ "Mass", getCompoundMass() }, { "# Atoms", getCompoundAtomsNr() },
						{ "Atoms", getCompoundAtoms() }, { "# Bonds", getCompoundBonds() },
						{ "Sequence", getCompoundSequence() }, { "Module", getCompoundModule() },
						{ "Organism", getCompoundOrganism() }, { "Comment", getCompoundComment() },
						{ "Remark", getCompoundRemarks() }, { "DB-Pathways", getAllPathwayLinks() },

				};

				return values;

			}
		} else if (KEGGentryType.equals("gene")) {

			Object[][] values = { { "Name", getKEGGentryName() }, { "Gene Name", getGeneName() },
					{ "Enzyme", getGeneEnzyme() }, { "Pathway", getKEGGPathway() },
					{ "Gene Definition", getGeneDefinition() }, { "Gene Position", getGenePosition() },
					{ "Gene Motifs", getAllGeneMotifs() }, { "Gene Codon Usage", getGeneCodonUsage() },
					{ "# AA", getGeneAAseqNr() }, { "AAseq", getGeneAAseq() }, { "# Nt", getGeneNtseqNr() },
					{ "Nts", getGeneNtSeq() }, { "Gene Orthology", getGeneOrthology() + " " + getGeneOrthologyName() },
					{ "DB-Pathways", getAllPathwayLinks() }, { "DB-Links", getAllDBLinks() } };

			return values;
		} else {
			Object[][] values = { { "Name", getKEGGentryName() }, { "EntryID", getKEGGentryID() },
					{ "Map", getKEGGentryMap() }, { "Type", getKEGGentryType() }, { "Pathway", getKEGGPathway() } };

			return values;
		}
	}
}
