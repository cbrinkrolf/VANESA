package biologicalObjects.nodes;

import java.util.Iterator;
import java.util.Vector;

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

	private String Keggprodukt = "";

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
			
	private Vector<String> allKeggNames =  new Vector<String>();

	private Vector<String> allDBLinks = new Vector<String>();
	
	private Vector<String> allPathways = new Vector<String>();
	
	private Vector<String> allStructures =  new Vector<String>();
	
	private Vector<String> geneMotifs = new Vector<String>();
	
	private Vector<String> involvedWith = new Vector<String>();
	
	private String glycanOrthology = "";
	
	private String glycanBracket="";
	
	private String glycanComposition="";
	
	private String glycanNode="";
	
	private String glycanEdge="";
	
	private String glycanName="";
	
	
	public String getGlycanName() {
		return glycanName;
	}

	public void setGlycanName(String glycanName) {
		this.glycanName = glycanName;
	}

	public String getKeggComment() {
		return KeggComment;
	}

	public void addAlternativeName(String name){
		allKeggNames.add(name);
	}
	
	public String getAllInvolvedElements(){
		
		Iterator<String> it = involvedWith.iterator();
		String results="";
		while(it.hasNext()){
			
			results = results + it.next()+ " ";
		}
		
		return results;
	}
	
	public void addInvolvedElement(String name){
		involvedWith.add(name);
	}
	
	public String getAllNames(){
		
		Iterator<String> it = allKeggNames.iterator();
		String results="";
		while(it.hasNext()){
			
			results = results + it.next()+ " ";
		}
		
		return results;
	}
	
	public Vector<String> getAllNamesAsVector(){
		return allKeggNames;
	}
	
	public void addDBLink(String name){
		allDBLinks.add(name);
	}
	
	public String getAllDBLinks(){
		
		Iterator<String> it = allDBLinks.iterator();
		String results="";
		while(it.hasNext()){
			
			results = results + it.next()+ " ";
		}
		
		return results;
	}
	
	public Vector<String> getAllDBLinksAsVector(){
		return allDBLinks;
	}
	
	public void addGeneMotif(String name){
		geneMotifs.add(name);
	}
	
	public String getAllGeneMotifs(){
		
		Iterator<String> it = geneMotifs.iterator();
		String results="";
		while(it.hasNext()){
			
			results = results + it.next()+ " ";
		}
		
		return results;
	}
	
	public Vector<String> getAllGeneMotifsAsVector(){
		return geneMotifs;
	}
	
	public void addPathwayLink(String name){
		allPathways.add(name);
	}
	
	public String getAllPathwayLinks(){
		
		Iterator<String> it = allPathways.iterator();
		String results="";
		while(it.hasNext()){
			
			results = results + it.next()+ " ";
		}
		
		return results;
	}
	
	public Vector<String> getAllPathwayLinksAsVector(){
		return allPathways;
	}
	
	public void addStructure(String name){
		allStructures.add(name);
	}
	
	public String getAllStructures(){
		
		Iterator<String> it = allStructures.iterator();
		String results="";
		while(it.hasNext()){
			
			results = results + it.next()+ " ";
		}
		
		return results;
	}	
	
	public Vector<String> getAllStructuresAsVector(){
		return allStructures;
	}
	
	public void setKeggComment(String keggComment) {
		KeggComment = keggComment;
	}

	public String getKEGGComponent() {
		return KEGGComponent;
	}

	public void setKEGGComponent(String component) {
		KEGGComponent = component;
	}

	public KEGGNode() {
	}

	public Object[][] getKeggDeatails(){
		
		if(KEGGentryType.equals("enzyme")){
			
			Object[][] values = {
					 {"Name",getKEGGentryName()},
					 {"SysName",getKeggsysName()},
					 {"Alternative Names",getAllNames()},
					 {"Pathway",getKEGGPathway()},
					 {"Class",getKeggenzymeClass()},
					 {"Orthology",getKeggorthology()},
					 {"Reaction",getKeggreaction()},
					 {"Substrate",getKeggsubstrate()},
					 {"Product",getKeggprodukt()},
					 {"Cofactor",getKeggcofactor()},
					 {"Effector",getKeggeffector()},
					 {"PDB-Structure",getAllStructures()},
					 {"DB-Links",getAllDBLinks()},
					 {"DB-Pathways",getAllPathwayLinks()},
					 {"Comment",getKeggComment()},
					 {"References",getKeggreference()
					 },
			};
			return values;
		}
		else if (KEGGentryType.equals("compound")){
			
			if(KEGGentryName.startsWith("G") || KEGGentryName.startsWith("g")){
				
				Object[][] values = {
						 {"Name",getKEGGentryName()},
						 {"Alternative Name",getGlycanName()},
						 {"Pathway",getKEGGPathway()},
						 {"Mass",getCompoundMass()},
						 {"Orthology",getGlycanOrthology()},
						 {"Bracket",getGlycanBracket()},
						 {"Composition",getGlycanComposition()},
						 {"Node",getGlycanNode()},
						 {"Edge",getGlycanEdge()},
						 {"Interaction With",getAllInvolvedElements()},
						 {"Comment",getCompoundComment()},
						 {"Remark",getCompoundRemarks()},
						 {"References",getKeggreference()},
						 {"DB-Pathways",getAllPathwayLinks()},
						 {"DB-Links",getAllDBLinks()}		
				
				};
				
			return values;
				
			}else{
				Object[][] values = {
						 {"Name",getKEGGentryName()},
						 {"Alternative Names",getAllNames()},
						 {"Pathway",getKEGGPathway()},
						 {"Formula",getCompoundFormula()},
						 {"Mass",getCompoundMass()},
						 {"# Atoms",getCompoundAtomsNr()},
						 {"Atoms",getCompoundAtoms()},
						 {"# Bonds",getCompoundBonds()},
						 {"Sequence",getCompoundSequence()},
						 {"Module",getCompoundModule()},
						 {"Organism",getCompoundOrganism()},
						 {"Comment",getCompoundComment()},
						 {"Remark",getCompoundRemarks()},
						 {"DB-Pathways",getAllPathwayLinks()},		
				
				};
				
			return values;
				
			}	
		}else if (KEGGentryType.equals("gene")){
			 
			Object[][] values = {
					 {"Name",getKEGGentryName()},
					 {"Gene Name",getGeneName()},
					 {"Enzyme",getGeneEnzyme()},
					 {"Pathway",getKEGGPathway()},
					 {"Gene Definition",getGeneDefinition()},
					 {"Gene Position",getGenePosition()},
					 {"Gene Motifs",getAllGeneMotifs()},
					 {"Gene Codon Usage",getGeneCodonUsage()},
					 {"# AA",getGeneAAseqNr()},
					 {"AAseq",getGeneAAseq()},
					 {"# Nt",getGeneNtseqNr()},
					 {"Nts",getGeneNtSeq()},
					 {"Gene Orthology",getGeneOrthology() + " " +getGeneOrthologyName()},
					 {"DB-Pathways",getAllPathwayLinks()},	
					 {"DB-Links",getAllDBLinks()}
			};
			
		return values;
		}else {
			Object[][] values = {
					 {"Name",getKEGGentryName()},
					 {"EntryID",getKEGGentryID()},
					 {"Map",getKEGGentryMap()},
					 {"Type",getKEGGentryType()},
					 {"Pathway",getKEGGPathway()}};
			
		return values;		
		}
	}

	public String getKEGGentryID() {
		return KEGGentryID;
	}

	public void setKEGGentryID(String id) {
		KEGGentryID = id;
	}

	public String getKEGGentryLink() {
		return KEGGentryLink;
	}

	public void setKEGGentryLink(String gentryLink) {
		KEGGentryLink = gentryLink;
	}

	public String getKEGGentryMap() {
		return KEGGentryMap;
	}

	public void setKEGGentryMap(String gentryMap) {
		KEGGentryMap = gentryMap;
	}

	public String getKEGGentryName() {
		return KEGGentryName;
	}

	public void setKEGGentryName(String gentryName) {
		KEGGentryName = gentryName;
	}

	public String getKEGGentryReaction() {
		return KEGGentryReaction;
	}

	public void setKEGGentryReaction(String gentryReaction) {
		KEGGentryReaction = gentryReaction;
	}

	public String getKEGGentryType() {
		return KEGGentryType;
	}

	public void setKEGGentryType(String gentryType) {
		KEGGentryType = gentryType;
	}

	public String getKEGGPathway() {
		return KEGGPathway;
	}

	public void setKEGGPathway(String pathway) {
		KEGGPathway = pathway;
	}

	public String getKeggcofactor() {
		return Keggcofactor;
	}

	public void setKeggcofactor(String keggcofactor) {
		Keggcofactor = keggcofactor;
	}

	public String getKeggeffector() {
		return Keggeffector;
	}

	public void setKeggeffector(String keggeffector) {
		Keggeffector = keggeffector;
	}

	public String getKeggenzymeClass() {
		return KeggenzymeClass;
	}

	public void setKeggenzymeClass(String keggenzymeClass) {
		KeggenzymeClass = keggenzymeClass;
	}

	public String getKeggorthology() {
		return Keggorthology;
	}

	public void setKeggorthology(String keggorthology) {
		Keggorthology = keggorthology;
	}

	public String getKeggprodukt() {
		return Keggprodukt;
	}

	public void setKeggprodukt(String keggprodukt) {
		Keggprodukt = keggprodukt;
	}

	public String getKeggreaction() {
		return Keggreaction;
	}

	public void setKeggreaction(String keggreaction) {
		Keggreaction = keggreaction;
	}

	public String getKeggreference() {
		return Keggreference;
	}

	public void setKeggreference(String keggreference) {
		Keggreference = keggreference;
	}

	public String getKeggsubstrate() {
		return Keggsubstrate;
	}

	public void setKeggsubstrate(String keggsubstrate) {
		Keggsubstrate = keggsubstrate;
	}

	public String getKeggsysName() {
		return KeggsysName;
	}

	public void setKeggsysName(String keggsysName) {
		KeggsysName = keggsysName;
	}

	public String getCompoundAtoms() {
		return compoundAtoms;
	}

	public void setCompoundAtoms(String compoundAtoms) {
		this.compoundAtoms = compoundAtoms;
	}

	public String getCompoundAtomsNr() {
		return compoundAtomsNr;
	}

	public void setCompoundAtomsNr(String compoundAtomsNr) {
		this.compoundAtomsNr = compoundAtomsNr;
	}

	public String getCompoundBondNr() {
		return compoundBondNr;
	}

	public void setCompoundBondNr(String compoundBondNr) {
		this.compoundBondNr = compoundBondNr;
	}

	public String getCompoundBonds() {
		return compoundBonds;
	}

	public void setCompoundBonds(String compoundBonds) {
		this.compoundBonds = compoundBonds;
	}

	public String getCompoundComment() {
		return compoundComment;
	}

	public void setCompoundComment(String compoundComment) {
		this.compoundComment = compoundComment;
	}

	public String getCompoundFormula() {
		return compoundFormula;
	}

	public void setCompoundFormula(String compoundFormula) {
		this.compoundFormula = compoundFormula;
	}

	public String getCompoundMass() {
		return compoundMass;
	}

	public void setCompoundMass(String compoundMass) {
		this.compoundMass = compoundMass;
	}

	public String getCompoundModule() {
		return compoundModule;
	}

	public void setCompoundModule(String compoundModule) {
		this.compoundModule = compoundModule;
	}

	public String getCompoundOrganism() {
		return compoundOrganism;
	}

	public void setCompoundOrganism(String compoundOrganism) {
		this.compoundOrganism = compoundOrganism;
	}

	public String getCompoundRemarks() {
		return compoundRemarks;
	}

	public void setCompoundRemarks(String compoundRemarks) {
		this.compoundRemarks = compoundRemarks;
	}

	public String getCompoundSequence() {
		return compoundSequence;
	}

	public void setCompoundSequence(String compoundSequence) {
		this.compoundSequence = compoundSequence;
	}

	public String getGeneAAseq() {
		return geneAAseq;
	}

	public void setGeneAAseq(String geneAAseq) {
		this.geneAAseq = geneAAseq;
	}

	public String getGeneAAseqNr() {
		return geneAAseqNr;
	}

	public void setGeneAAseqNr(String geneAAseqNr) {
		this.geneAAseqNr = geneAAseqNr;
	}

	public String getGeneCodonUsage() {
		return geneCodonUsage;
	}

	public void setGeneCodonUsage(String geneCodonUsage) {
		this.geneCodonUsage = geneCodonUsage;
	}

	public String getGeneDefinition() {
		return geneDefinition;
	}

	public void setGeneDefinition(String geneDefinition) {
		this.geneDefinition = geneDefinition;
	}

	public String getGeneName() {
		return geneName;
	}

	public void setGeneName(String geneName) {
		this.geneName = geneName;
	}

	public String getGeneNtSeq() {
		return geneNtSeq;
	}

	public void setGeneNtSeq(String geneNtSeq) {
		this.geneNtSeq = geneNtSeq;
	}

	public String getGeneNtseqNr() {
		return geneNtseqNr;
	}

	public void setGeneNtseqNr(String geneNtseqNr) {
		this.geneNtseqNr = geneNtseqNr;
	}

	public String getGenePosition() {
		return genePosition;
	}

	public void setGenePosition(String genePosition) {
		this.genePosition = genePosition;
	}

	public String getGeneEnzyme() {
		return geneEnzyme;
	}

	public void setGeneEnzyme(String geneEnzyme) {
		this.geneEnzyme = geneEnzyme;
	}

	public String getGeneOrthology() {
		return geneOrthology;
	}

	public void setGeneOrthology(String geneOrthology) {
		this.geneOrthology = geneOrthology;
	}

	public String getGeneOrthologyName() {
		return geneOrthologyName;
	}

	public void setGeneOrthologyName(String geneOrthologyName) {
		this.geneOrthologyName = geneOrthologyName;
	}

	public String getGlycanBracket() {
		return glycanBracket;
	}

	public void setGlycanBracket(String glycanBracket) {
		this.glycanBracket = glycanBracket;
	}

	public String getGlycanComposition() {
		return glycanComposition;
	}

	public void setGlycanComposition(String glycanComposition) {
		this.glycanComposition = glycanComposition;
	}

	public String getGlycanEdge() {
		return glycanEdge;
	}

	public void setGlycanEdge(String glycanEdge) {
		this.glycanEdge = glycanEdge;
	}

	public String getGlycanNode() {
		return glycanNode;
	}

	public void setGlycanNode(String glycanNode) {
		this.glycanNode = glycanNode;
	}

	public String getGlycanOrthology() {
		return glycanOrthology;
	}

	public void setGlycanOrthology(String glycanOrthology) {
		this.glycanOrthology = glycanOrthology;
	}

}
