package biologicalObjects.nodes;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import pojos.DBColumn;

/**
 * 
 * @author Olga
 *
 */

/**
 * the node to store data of a special disease
 */
public class DAWISNode {

	private Vector<String[]> collectorElements = new Vector<String[]>();
	private Vector<String> synonyms = new Vector<String>();
	private Vector<String> domains = new Vector<String>();
	private Vector<String> features = new Vector<String>();
	private Vector<String> locations = new Vector<String>();
	private Vector<String> geneNames = new Vector<String>();
	private Vector<String> pDBs = new Vector<String>();
	private Vector<String> accessionnumbers = new Vector<String>();
	private Vector<String> classification = new Vector<String>();
	private Vector<String> substrates = new Vector<String>();
	private Vector<String> substratesName = new Vector<String>();
	private Vector<String> products = new Vector<String>();
	private Vector<String> productsName = new Vector<String>();
	private Vector<String> cofactors = new Vector<String>();
	private Vector<String> cofactorsName = new Vector<String>();
	private Vector<String> inhibitors = new Vector<String>();
	private Vector<String> inhibitorsName = new Vector<String>();
	private Vector<String> effectors = new Vector<String>();
	private Vector<String> effectorsName = new Vector<String>();
	private Vector<String> orthology = new Vector<String>();
	private Vector<String> dbLinks = new Vector<String>();
	private Vector<String> catalysts = new Vector<String>();
	private Vector<String> catalystsNames = new Vector<String>();
	private Vector<String> superfamilies = new Vector<String>();
	private Vector<String> subfamilies = new Vector<String>();
	private Vector<String> expressions = new Vector<String>();
	private Vector<String> prozesses = new Vector<String>();
	private Vector<String> functions = new Vector<String>();
	private Vector<String> reference = new Vector<String>();
	private Vector<String> motifs = new Vector<String>();
	private Vector<String> methods = new Vector<String>();

	private String object = "";
	private String id = "";
	private String name = "";
	private String organism = "";
	private String db = "";
	private String diagnosisType = "";
	private String disorder = "";
	private String pathwayMap = "";
	private String ontology = "";
	private String definition = "";
	private String position = "";
	private String codonUsage = "";
	private String nucleotidSequenceLength = "";
	private String nucleotidSequence = "";
	private String aminoAcidSequenceLength = "";
	private String aminoAcidSequence = "";
	// private String motif = "";
	private String organelle = "";
	private String weight = "";
	private String comment = "";
	private String equation = "";
	private String rDM = "";
	private String formula = "";
	private String atoms = "";
	private String bonds = "";
	private String atomsNumber = "";
	private String module = "";
	private String sequenceSource = "";
	private String remark = "";
	private String bondsNumber = "";
	private String composition = "";
	private String node = "";
	private String edge = "";
	private String target = "";
	private String bracket = "";
	private String original = "";
	private String repeat = "";
	private String activity = "";
	private String type = "";
	private String effect = "";
	private String information = "";
	private String isoelectricPoint = "";
	private String loadedData = "false";
	private String isoformenNumber = "";
	private String transfacGene = "";
	private String cellSpecificityNeg = "";
	private String cellSpecificityPos = "";
	private String factorClass = "";
	private String encodingGene = "";
	private String element = "";
	private String startPoint = "";
	private String endPoint = "";
	private String complexName = "";

	// all labels of the biological node abstract <synonymID, label>
	private Hashtable<String, String> allIDs = new Hashtable<String, String>();
	// labels and their databases <database, label>
	private Hashtable<String, String> allIDDBRelations = new Hashtable<String, String>();
	// all labels of the collector element entries
	private Hashtable<String, Integer> allTableIDs = new Hashtable<String, Integer>();

	boolean dataLoaded = false;

	int countElements;

	Object[][] values;

	/**
	 * construct empty fields to store id, name, diagnosisType and disorder of
	 * the disease
	 */
	public DAWISNode(String obj) {

		this.object = obj;
		id = "";
		name = "";
		organism = "";

	}

	public void setObject(String object) {
		this.object = object;
	}

	public void setDataLoaded() {
		dataLoaded = true;
	}

	public boolean getDataLoaded() {
		return dataLoaded;
	}

	public String getDataLoadedAsString() {
		return dataLoaded + "";
	}

	public String getObject() {
		return this.object;
	}

	/**
	 * get the link for further informations
	 */
	public String getLink(BiologicalNodeAbstract element) {
		if (element instanceof Disease) {
			return "http://www.ncbi.nlm.nih.gov/entrez/dispomim.cgi?id=" + id;
		} else if (element instanceof PathwayMap) {
			String dbID = allIDDBRelations.get("KEGG");
			if (!dbID.equals("")) {
				return "http://www.genome.jp/dbget-bin/www_bget?path:" + dbID;
			}
		} else if (element instanceof Enzyme) {
			return "http://www.genome.jp/dbget-bin/www_bget?ec:" + id;
		} else if (element instanceof Protein) {
			String dbID = allIDDBRelations.get("UniProt");
			if (!dbID.equals("")) {
				return "http://www.expasy.org/uniprot/" + dbID;
			}
		} else if (element instanceof Gene) {
			String dbID = allIDDBRelations.get("KEGG");
			if (!dbID.equals("")) {
				return "http://www.genome.jp/dbget-bin/www_bget?" + organism
						+ "+" + dbID;
			}

		} else if (element instanceof GeneOntology) {
			return "http://amigo.geneontology.org/cgi-bin/amigo/term-details.cgi?term="
					+ id;
		} else if (element instanceof Glycan) {
			return "http://www.genome.jp/dbget-bin/www_bget?gl:" + id;
		} else if (element instanceof Drug) {
			return "http://www.genome.jp/dbget-bin/www_bget?dr:" + id;
		} else if (element instanceof CompoundNode) {
			if (allIDDBRelations.containsKey("KEGG")) {
				String dbID = allIDDBRelations.get("KEGG");
				if (!dbID.equals("")) {
					return "http://www.genome.jp/dbget-bin/www_bget?cpd:"
							+ dbID;
				}
			}

		} else if (element instanceof Reaction) {
			String dbID = allIDDBRelations.get("KEGG");
			if (!dbID.equals("")) {
				return "http://www.genome.jp/dbget-bin/www_bget?rn:" + dbID;
			}
		}

		return null;
	}

	/**
	 * get the link for further informations
	 */
	public String getDAWISLink(BiologicalNodeAbstract element) {
		if (element instanceof Disease) {
			return "https://agbi.techfak.uni-bielefeld.de/DAWISMD/jsp/result/disease_result.jsp?Disease_Id="
					+ id;
		} else if (element instanceof PathwayMap) {
			return "https://agbi.techfak.uni-bielefeld.de/DAWISMD/jsp/result/pathway_result.jsp?Pathway_Id="
					+ id;
		} else if (element instanceof Enzyme) {
			return "https://agbi.techfak.uni-bielefeld.de/DAWISMD/jsp/result/enzyme_result.jsp?Enzyme_Id="
					+ id;
		} else if (element instanceof Protein) {
			return "https://agbi.techfak.uni-bielefeld.de/DAWISMD/jsp/result/protein_result.jsp?Protein_Id="
					+ id;
		} else if (element instanceof Gene) {
			return "https://agbi.techfak.uni-bielefeld.de/DAWISMD/jsp/result/gene_result.jsp?Gene_Id="
					+ id;
		} else if (element instanceof GeneOntology) {
			return "https://agbi.techfak.uni-bielefeld.de/DAWISMD/jsp/result/go_result.jsp?Gene_Ontology_Accession="
					+ id;
		} else if (element instanceof Glycan) {
			return "https://agbi.techfak.uni-bielefeld.de/DAWISMD/jsp/result/glycan_result.jsp?Glycan_Id="
					+ id;
		} else if (element instanceof Drug) {
			return "https://agbi.techfak.uni-bielefeld.de/DAWISMD/jsp/result/drug_result.jsp?Drug_Id="
					+ id;
		} else if (element instanceof CompoundNode) {
			return "https://agbi.techfak.uni-bielefeld.de/DAWISMD/jsp/detail/compound/compound_detail.jsp?param0="
					+ id;
		} else if (element instanceof Reaction) {
			return "https://agbi.techfak.uni-bielefeld.de/DAWISMD/jsp/result/reaction_result.jsp?Reaction_Id="
					+ id;
		} else if (element instanceof Factor) {
			return "http://agbi.techfak.uni-bielefeld.de/DAWISMD/jsp/detail/reaction_detail.jsp?param0="
					+ id;
		} else if (element instanceof Fragment) {
			return "http://agbi.techfak.uni-bielefeld.de/DAWISMD/jsp/detail/reaction_detail.jsp?param0="
					+ id;
		} else if (element instanceof Site) {
			return "http://agbi.techfak.uni-bielefeld.de/DAWISMD/jsp/detail/reaction_detail.jsp?param0="
					+ id;
		}
		return null;
	}

	public Object[][] getDAWISDetailsFor(String object) {

		if (object.equals("Pathway Map")) {
			Object[][] values = { { "Pathway_ID", getID() },
					{ "Name", getName() }, { "Database", getDB() },
					{ "Organism", getOrganism() },
					{ "Synonym", getSynonyms() },
					{ "Pathway map", getPathwayMap() },
					{ "Reference", getReference() },
					{ "Comment", getComment() } };
			return values;
		} else if (object.equals("Disease")) {
			Object[][] values = { { "MIM", getID() }, { "Name", getName() },
					{ "Database", getDB() }, { "Organism", getOrganism() },
					{ "Synonym", getSynonyms() }, { "Domain", getDomain() },
					{ "Features", getFeatures() },
					{ "Locations", getLocations() },
					{ "Disorder", getDisorder() },
					{ "Diagnosistype", getDiagnosisType() } };
			return values;
		} else if (object.equals("Gene Ontology")) {
			Object[][] values = { { "GO_ID", getID() }, { "Name", getName() },
					{ "Database", getDB() }, { "Organism", getOrganism() },
					{ "Synonym", getSynonyms() },
					{ "Ontology", getOntology() },
					{ "Definition", getDefinition() } };
			return values;
		} else if (object.equals("Gene")) {
			Object[][] values = {
					{ "Gene_ID", getID() },
					{ "Name", getName() },
					{ "Database", getDB() },
					{ "Organism", getOrganism() },
					{ "Alternative Names", getSynonyms() },
					{ "Definition", getDefinition() },
					{ "Molecule Type", getType() },
					{ "Position", getPosition() },
					{ "Nucleotid sequence length", getNucleotidSequenceLength() },
					{ "Nucleotid sequence", getNucleotidSequence() },
					{ "AminoAcid sequence length", getAminoAcidSeqLength() },
					{ "AminoAcid sequence", getAminoAcidSeq() },
					{ "Orthology", getOrthology() }, { "Motifs", getMotifs() },
					{ "Classification", getClassification() },
					{ "Organelle", getOrganelle() } };
			return values;
		} else if (object.equals("Protein")) {
			Object[][] values = { { "Protein_ID", getID() },
					{ "Name", getName() }, { "Database", getDB() },
					{ "Organism", getOrganism() },
					{ "Synonym", getSynonyms() },
					{ "Classification", getClassification() },
					{ "Organelle", getOrganelle() },
					{ "Gene name", getGeneName() },
					{ "Molecular weight", getWeigth() },
					{ "Amino acid sequence length", getAminoAcidSeqLength() },
					{ "Amino acid sequence", getAminoAcidSeq() },
					{ "PBD links", getPDBs() },
					{ "#Isoformen", getIsoformenNumber() },
					{ "Tissue expressions", getExpressions() },
					{ "Molecular funktion", getFunktions() },
					{ "Biological prozess", getProzesses() },
					{ "Cell component", getComponent() },
					{ "Subfamily", getSubfamilies() },
					{ "Superfamily", getSuperfamilies() },
					{ "Type", getType() },
					{ "Complex Name", getComplexName() },
					{ "Feature", getFeatures() }, { "Comment", getComment() } };
			return values;
		} else if (object.equals("Enzyme")) {
			Object[][] values = {
					{ "Enzyme_ID", getID() },
					{ "Systematic name", getName() },
					{ "Database", getDB() },
					{ "Organism", getOrganism() },
					{ "Alternative Names", getSynonyms() },
					{ "Class", getClassification() },
					{ "Orthology", getOrthology() },
					{ "Substrate", getSubstrates() + ": " + getSubstratesName() },
					{ "Product", getProducts() + ": " + getProductsName() },
					{ "Cofactor", getCofactors() + ": " + getCofactorsName() },
					{ "Effector", getEffectors() + ": " + getEffectorsName() },
					{ "Inhibitor", getInhibitors() + ": " + getInhibitorsName() },
					{ "PDB-Structure", getPDBs() },
					{ "Comment", getComment() },
					{ "Reference", getReference() },
					{ "DB-Links", getDBLinks() } };
			return values;
		} else if (object.equals("Reaction")) {
			Object[][] values = { { "Reaction_ID", getID() },
					{ "Name", getName() }, { "Database", getDB() },
					{ "Organism", getOrganism() },
					{ "Definition", getDefinition() },
					{ "Orthology", getOrthology() },
					{ "Equation", getEquation() }, { "Comment", getComment() },
					{ "Substrates", getSubstrates() },
					{ "Products", getProducts() } };
			return values;
		} else if (object.equals("Reaction Pair")) {
			Object[][] values = { { "Reaction_Pair_ID", getID() },
					{ "Name", getName() }, { "Database", getDB() },
					{ "Organism", getOrganism() },
					{ "Type", getClassification() }, { "RDM", getRDM() } };
			return values;
		} else if (object.equals("Interaction")) {
			Object[][] values = { { "Interaction_ID", getID() },
					{ "Name", getName() }, { "Database", getDB() },
					{ "Organism", getOrganism() } };
			return values;
		} else if (object.equals("Compound")) {
			Object[][] values = { { "Compound_ID", getID() },
					{ "Name", getName() }, { "Database", getDB() },
					{ "Organism", getOrganism() },
					{ "Alternative Names", getSynonyms() },
					{ "Formula", getFormula() }, { "Mass", getWeigth() },
					{ "Isoelectric point", getIsoelectricPoint() },
					{ "# Atoms", getAtomsNr() }, { "Atoms", getAtoms() },
					{ "# Bonds", getBondsNumber() }, { "Bonds", getBonds() },
					{ "Nucleotid sequence", getNucleotidSequence() },
					{ "Sequence length", getNucleotidSequenceLength() },
					{ "Sequence information", getInformation() },
					{ "Sequence source database", getDBLinks() },
					{ "Molecule type", getType() },
					{ "Molecule classification", getClassification() },
					{ "Features", getFeatures() },
					{ "Molecule subfamily", getSubfamilies() },
					{ "Molecule superfamily", getSuperfamilies() },
					{ "Module", getModule() }, { "Comment", getComment() },
					{ "Remark", getRemarks() } };
			return values;
		} else if (object.equals("Glycan")) {
			Object[][] values = { { "Glycan_ID", getID() },
					{ "Name", getName() }, { "Database", getDB() },
					{ "Organism", getOrganism() },
					{ "Synonyms", getSynonyms() },
					{ "Class", getClassification() }, { "Mass", getWeigth() },
					{ "Remark", getRemarks() },
					{ "Composition", getComposition() },
					{ "Orthology", getOrthology() } };
			return values;
		} else if (object.equals("Drug")) {
			Object[][] values = { { "Drug_ID", getID() },
					{ "Name", getName() }, { "Database", getDB() },
					{ "Organism", getOrganism() },
					{ "Synonyms", getSynonyms() }, { "Formula", getFormula() },
					{ "Mass", getWeigth() }, { "Atom number", getAtomsNr() },
					{ "Atom", getAtoms() },
					{ "Bond number", getBondsNumber() },
					{ "Bond", getBonds() }, { "Target", getTarget() },
					{ "Bracket", getBracket() }, { "Original", getOriginal() },
					{ "Repeat", getRepeat() }, { "Activity", getActivity() },
					{ "Remark", getRemarks() }, { "Comment", getComment() } };

			return values;
		} else if (object.equals("Factor")) {
			Object[][] values = { { "Factor_ID", getID() },
					{ "Name", getName() }, { "Class", getFactorClass() },
					{ "Decimal classification", getClassification() },
					{ "Mass", getWeigth() },
					{ "Cell specificity neg", getSpecificityNeg() },
					{ "Cell specificity pos", getSpecificityPos() },
					{ "Encoding gene", getEncodingGene() },
					{ "Sequence length", getAminoAcidSeqLength() },
					{ "Sequence", getAminoAcidSeq() },
					{ "Sequence source", getDBLinks() },
					{ "Subfamily", getSubfamilies() },
					{ "Superfamily", getSuperfamilies() },
					{ "Type", getType() } };
			return values;
		} else if (object.equals("Site")) {
			Object[][] values = { { "Site_ID", getID() },
					{ "Comments", getComment() },
					{ "Description", getDefinition() },
					{ "Site element", getElement() },
					{ "Gene region", getLocations() },
					{ "Reference point", getReference() },
					{ "Reference point start", getStartPoint() },
					{ "Reference point end", getEndPoint() },
					{ "Sequence type", getType() } };
			return values;
		} else if (object.equals("Collector")) {
			Object[][] values = getList();
			return values;
		} else {
			Object[][] values = { { "Element_ID", getID() },
					{ "Name", getName() }, { "Database", getDB() },
					{ "Organism", getOrganism() } };
			return values;
		}
	}

	public void setDB(String database) {
		this.db = database;
	}

	public String getDB() {
		return this.db;
	}

	public void setCollectorElements(String[] elements) {
		if (!this.collectorElements.contains(elements)) {
			this.collectorElements.add(elements);
		}
		setValue();
	}
	
	/**
	 * Has to be removed/merged same method already exists -> dummy method
	 * by Benny
	 * @see setList(Vector<String[]> vec)
	 * @param list
	 */
	public void setList(ArrayList<DBColumn> list)
	{
		// TODO remove/merge method! - by Benny -
		Vector<String[]> vec=convertArrayListDBColumsn2VectorString(list);
		this.collectorElements=new Vector<String[]>();
		
		if (this.collectorElements.size()>0)
		{
			collectorElements.removeAllElements();
		}
		
		this.collectorElements=vec;
		int size=this.collectorElements.size();
		countElements=size;
		int columnCount=vec.get(0).length;
		
		if (columnCount>1)
		{
			this.values=new Object[size][2];
		}
		else
		{
			this.values=new Object[size][1];
		}
		
		for (int i=0; i<size; i++)
		{
			String[] elemDet=(String[])this.collectorElements.get(i);
			this.values[i][0]=elemDet[0];

			if (this.object.equals("Collector"))
			{
				this.allTableIDs.put(elemDet[0], i);
			}
			
			if (elemDet.length>1)
			{
				this.values[i][1]=elemDet[1];
			}
		}
	}
	
	/**
	 * Converts the DBColumns ArrayList to a Vector of Strngs.
	 * Has to be removed -> dummy method.
	 * by Benny
	 * 
	 * @param list
	 * @return
	 */
	private Vector<String[]> convertArrayListDBColumsn2VectorString(ArrayList<DBColumn> list)
	{
		// TODO remove/merge method! - by Benny -
		Vector<String[]> vector=new Vector<String[]>();
		
		for (DBColumn column : list)
		{
			vector.add(column.getColumn());
		}
		
		return vector;
	}
	
	public void setList(Vector<String[]> vec) {

		this.collectorElements = new Vector<String[]>();
		if (this.collectorElements.size() > 0) {
			collectorElements.removeAllElements();
		}
		this.collectorElements = vec;
		int size = this.collectorElements.size();
		countElements = size;
		int columnCount = vec.get(0).length;
		if (columnCount > 1) {
			this.values = new Object[size][2];
		} else {
			this.values = new Object[size][1];
		}
		for (int i = 0; i < size; i++) {
			String[] elemDet = (String[]) this.collectorElements.get(i);
			this.values[i][0] = elemDet[0];

			if (this.object.equals("Collector")) {
				this.allTableIDs.put(elemDet[0], i);
			}
			if (elemDet.length > 1) {
				this.values[i][1] = elemDet[1];
			}
		}
	}
	

	public Hashtable<String, Integer> getAllTableIDs() {
		return allTableIDs;
	}

	public void setValue() {

		int size = this.collectorElements.size();
		int columnCount = collectorElements.get(0).length;
		countElements = size;

		if (columnCount > 1) {
			this.values = new Object[size][2];
		} else {
			this.values = new Object[size][1];
		}

		for (int i = 0; i < size; i++) {
			String[] elemDet = (String[]) this.collectorElements.get(i);
			this.values[i][0] = elemDet[0];
			if (this.object.equals("Collector")) {
				this.allTableIDs.put(elemDet[0], i);
			}
			if (elemDet.length > 1) {
				this.values[i][1] = elemDet[1];
			}
		}
	}

	private Object[][] getList() {
		if (this.values != null) {
			return this.values;
		} else {
			setValue();
			return this.values;
		}
	}

	public Vector<String[]> getListAsVector() {
		return collectorElements;
	}

	public void setComposition(String comp) {
		composition = comp;
	}

	public String getComposition() {
		return composition;
	}

	public void setNode(String n) {
		node = n;
	}

	public String getNode() {
		return node;
	}

	public void setEdge(String e) {
		edge = e;
	}

	public String getEdge() {
		return edge;
	}

	public void setRemark(String rem) {
		remark = rem;
	}

	public String getRemarks() {
		return remark;
	}

	public void setModule(String mod) {
		module = mod;
	}

	public String getModule() {
		return module;
	}

	public void setSequenceSource(String seq) {
		sequenceSource = seq;
	}

	public String getSequenceSource() {
		return sequenceSource;
	}

	public void setBonds(String b) {
		bonds = b;
	}

	public String getBonds() {
		return bonds;
	}

	public void setBondsNumber(String bonr) {
		bondsNumber = bonr;
	}

	public String getBondsNumber() {
		return bondsNumber;
	}

	public void setAtoms(String a) {
		atoms = a;
	}

	public String getAtoms() {
		return atoms;
	}

	public void setAtomsNumber(String atomsNr) {
		atomsNumber = atomsNr;
	}

	public String getAtomsNr() {
		return atomsNumber;
	}

	public void setFormula(String form) {
		formula = form;
	}

	public String getFormula() {
		return formula;
	}

	public void setRDM(String rdm) {
		rDM = rdm;
	}

	public String getRDM() {
		return rDM;
	}

	public void setEquation(String eq) {
		equation = eq;
	}

	public String getEquation() {
		return equation;
	}

	public void setComment(String com) {
		comment = com;
	}

	public String getComment() {
		return comment;
	}

	public void setSubstrates(String substr) {
		if (substr != null) {
			if (!substrates.contains(substr)) {
				substrates.add(substr);
			}
		}
	}

	/**
	 * get substrates as a vector
	 * 
	 * @return substrates
	 */
	public Vector<String> getSubstratesAsVector() {
		return substrates;
	}

	public String getSubstrates() {
		return createString(substrates);
	}

	public void setSubstratesName(String substrName) {
		if (substrName != null) {
			if (!substratesName.contains(substrName)) {
				substratesName.add(substrName);
			}
		}
	}

	/**
	 * get substrate names as a vector
	 * 
	 * @return substrates names
	 */
	public Vector<String> getSubstrateNamesAsVector() {
		return substratesName;
	}

	public String getSubstratesName() {
		return createString(substratesName);
	}

	public void setProducts(String product) {
		if (product != null) {
			if (!products.contains(product)) {
				products.add(product);
			}
		}
	}

	/**
	 * get products as a vector
	 * 
	 * @return products
	 */
	public Vector<String> getProductsAsVector() {
		return products;
	}

	public String getProducts() {
		return createString(products);
	}

	public void setProductsName(String productName) {
		if (productName != null) {
			if (!productsName.contains(productName)) {
				productsName.add(productName);
			}
		}
	}

	/**
	 * get product names as a vector
	 * 
	 * @return product names
	 */
	public Vector<String> getProductNamesAsVector() {
		return productsName;
	}

	public String getProductsName() {
		return createString(productsName);
	}

	public void setEffectors(String effector) {
		if (effector != null) {
			if (!effectors.contains(effector)) {
				effectors.add(effector);
			}
		}
	}

	/**
	 * get effectors as a vector
	 * 
	 * @return effectors
	 */
	public Vector<String> getEffectorsAsVector() {
		return effectors;
	}

	public String getEffectors() {
		return createString(effectors);
	}

	public void setEffectorsName(String effectorName) {
		if (effectorName != null) {
			if (!effectorsName.contains(effectorName)) {
				effectorsName.add(effectorName);
			}
		}
	}

	/**
	 * get effector names as a vector
	 * 
	 * @return effector names
	 */
	public Vector<String> getEffectorNamesAsVector() {
		return effectorsName;
	}

	public String getEffectorsName() {
		return createString(effectorsName);
	}

	public void setCofactors(String cofactor) {
		if (cofactor != null) {
			if (!cofactors.contains(cofactor)) {
				cofactors.add(cofactor);
			}
		}
	}

	/**
	 * get cofactors as a vector
	 * 
	 * @return cofactors
	 */
	public Vector<String> getCofactorsAsVector() {
		return cofactors;
	}

	public String getCofactors() {
		return createString(cofactors);
	}

	public void setCofactorsName(String cofactorName) {
		if (cofactorName != null) {
			if (!cofactorsName.contains(cofactorName)) {
				cofactorsName.add(cofactorName);
			}
		}
	}

	/**
	 * get cofactor names as a vector
	 * 
	 * @return cofactor names
	 */
	public Vector<String> getCofactorNamesAsVector() {
		return cofactorsName;
	}

	public String getCofactorsName() {
		return createString(cofactorsName);
	}

	public void setInhibitors(String inhibitor) {
		if (inhibitor != null) {
			if (!inhibitors.contains(inhibitor)) {
				inhibitors.add(inhibitor);
			}
		}
	}

	/**
	 * get inhibitors as a vector
	 * 
	 * @return inhibitors
	 */
	public Vector<String> getInhibitorsAsVector() {
		return inhibitors;
	}

	public String getInhibitors() {
		return createString(inhibitors);
	}

	public void setInhibitorsName(String inhibitorName) {
		if (inhibitorName != null) {
			if (!inhibitorsName.contains(inhibitorName)) {
				inhibitorsName.add(inhibitorName);
			}
		}
	}

	/**
	 * get inhibitor names as a vector
	 * 
	 * @return inhibitor names
	 */
	public Vector<String> getInhibitorNamesAsVector() {
		return inhibitorsName;
	}

	public String getInhibitorsName() {
		return createString(inhibitorsName);
	}

	// private Object getBox() {
	// JPanel p = new JPanel();
	// String [] s = {"DFG", "UIU", "TZUI"};
	// JComboBox cb = new JComboBox(s);
	// p.add(cb);
	// return cb;
	// }

	// /**
	// * get all details of the disease as 2-dimensional array
	// *
	// * @return values
	// */
	//
	// public Object[][] getDAWISDetails() {
	//
	// Object[][] values = { { "MIM", getID() }, { "Name", getName() },
	// { "Synonym", getSynonyms() }, { "Domain", getDomain() },
	// { "Features", getFeatures() },
	// { "Locations", getLocations() },
	// { "Disorder", getDisorder() },
	// { "DiagnosisType", getDiagnosisType() }, };
	// return values;
	// }

	/**
	 * set the mim of the disease
	 * 
	 * @param id
	 */
	public void setID(String id) {
		this.id = id;
	}

	/**
	 * get the id of the element
	 * 
	 * @return id
	 */
	public String getID() {
		return id;
	}

	/**
	 * set the name of the element
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * get the name of the element
	 * 
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * set the organism of the object
	 * 
	 * @param organism
	 */
	public void setOrganism(String organism) {
		this.organism = organism;
	}

	/**
	 * get the organism of the object
	 * 
	 * @return organism
	 */
	public String getOrganism() {
		return this.organism;
	}

	/**
	 * get the synonyms of the element
	 * 
	 * @return synonym
	 */
	public String getSynonyms() {
		return createString(synonyms);
	}

	/**
	 * get synonyms of the element as a vector
	 * 
	 * @return synonyms
	 */
	public Vector<String> getSynonymsAsVector() {
		return synonyms;
	}

	/**
	 * add synonyms of the element to a vector
	 * 
	 * @param synonym
	 */
	public void setSynonym(String synonym) {
		if (synonym != null) {
			if (!synonyms.contains(synonym)) {
				synonyms.add(synonym);
			}
		}
	}

	private String createString(Vector<String> vec) {

		String result = "";

		Iterator<String> it = vec.iterator();

		boolean first = true;
		while (it.hasNext()) {
			if (first) {
				result = result + it.next().toString();
				first = false;
			} else {
				result = result + " ; " + it.next().toString();
			}
		}
		return result;
	}

	/**
	 * add gene names to a vector
	 * 
	 * @param gene
	 *            names
	 */
	public void setGeneName(String gn) {
		if (gn != null) {
			if (!geneNames.contains(gn)) {
				geneNames.add(gn);
			}
		}
	}

	/**
	 * get gene names as a vector
	 * 
	 * @return gene names
	 */
	public Vector<String> getGeneNamesAsVector() {
		return geneNames;
	}

	public String getGeneName() {
		return createString(geneNames);
	}

	public void setAccessionnumber(String accessnb) {
		if (accessnb != null) {
			if (!accessionnumbers.contains(accessnb)) {
				accessionnumbers.add(accessnb);
			}
		}
	}

	/**
	 * get accession numbers as a vector
	 * 
	 * @return accession numbers
	 */
	public Vector<String> getAccessionnumbersAsVector() {
		return accessionnumbers;
	}

	public String getAccessionnumber() {
		return createString(accessionnumbers);
	}

	public void setPDBs(String pdb) {
		if (pdb != null) {
			if (!pDBs.contains(pdb)) {
				pDBs.add(pdb);
			}
		}
	}

	/**
	 * get PDBs as a vector
	 * 
	 * @return pdbs
	 */
	public Vector<String> getPDBsAsVector() {
		return pDBs;
	}

	public String getPDBs() {
		return createString(pDBs);
	}

	/**
	 * get domains of the disease
	 * 
	 * @return domain
	 */
	public String getDomain() {

		String result = "";
		Iterator<String> it = domains.iterator();
		boolean first = true;
		while (it.hasNext()) {
			if (first) {
				result = result + it.next().toString();
				first = false;
			} else {
				result = result + " ; " + it.next().toString();
			}
		}
		return result;
	}

	/**
	 * get domains of the disease as a vector
	 * 
	 * @return domains
	 */
	public Vector<String> getDomainsAsVector() {
		return domains;
	}

	/**
	 * add domains of the disease to a vector
	 * 
	 * @param domain
	 */
	public void setDomain(String domain) {
		if (domain != null) {
			if (!domains.contains(domain)) {
				domains.add(domain);
			}
		}
	}

	/**
	 * get features of the disease
	 * 
	 * @return feature
	 */
	public String getFeatures() {

		String result = "";
		Iterator<String> it = features.iterator();
		boolean first = true;
		while (it.hasNext()) {
			if (first) {
				result = result + it.next().toString();
				first = false;
			} else {
				result = result + " ; " + it.next().toString();
			}
		}
		return result;
	}

	/**
	 * get features of the disease as a vector
	 * 
	 * @return features
	 */
	public Vector<String> getFeaturesAsVector() {
		return features;
	}

	/**
	 * add features of the disease to a vector
	 * 
	 * @param feature
	 */
	public void setFeature(String feature) {
		if (feature != null) {
			if (!features.contains(feature)) {
				features.add(feature);
			}
		}
	}

	/**
	 * get locations of the disease
	 * 
	 * @return location
	 */
	public String getLocations() {

		String result = "";
		Iterator<String> it = locations.iterator();
		boolean first = true;
		while (it.hasNext()) {
			if (first) {
				result = result + it.next().toString();
				first = false;
			} else {
				result = result + " ; " + it.next().toString();
			}
		}
		return result;
	}

	/**
	 * get locations of the disease as a vector
	 * 
	 * @return domains
	 */
	public Vector<String> getLocationsAsVector() {
		return locations;
	}

	/**
	 * add locations of the disease to a vector
	 * 
	 * @param domain
	 */
	public void setLocation(String location) {
		if (location != null) {
			if (!locations.contains(location)) {
				locations.add(location);
			}
		}
	}

	/**
	 * set diagnosis type of the disease
	 * 
	 * @param diagnosisType
	 */
	public void setDiagnosisType(String diagnosisType) {
		this.diagnosisType = diagnosisType;
	}

	/**
	 * get diagnosis type of the disease
	 * 
	 * @return diagnosisType
	 */
	public String getDiagnosisType() {
		return diagnosisType;
	}

	/**
	 * set disorder of the disease
	 * 
	 * @param disorder
	 */
	public void setDisorder(String disorder) {
		this.disorder = disorder;
	}

	/**
	 * get disorder of the disease
	 * 
	 * @return disorder
	 */
	public String getDisorder() {
		return disorder;
	}

	/**
	 * set the link for the pathway map
	 * 
	 * @param link
	 */
	public void setPathwayMap(String link) {
		pathwayMap = link;
	}

	/**
	 * get the link for the pathway map
	 * 
	 * @return
	 */
	public String getPathwayMap() {
		return pathwayMap;
	}

	public void setOntology(String ont) {
		ontology = ont;
	}

	public String getOntology() {
		return ontology;
	}

	public void setDefinition(String def) {
		definition = def;
	}

	public String getDefinition() {
		return definition;
	}

	public void setPosition(String pos) {
		position = pos;
	}

	public String getPosition() {
		return position;
	}

	public void setCodonUsage(String codUs) {
		codonUsage = codUs;
	}

	public String getCodonUsage() {
		return codonUsage;
	}

	public void setNucleotidSequenceLength(String nsl) {
		nucleotidSequenceLength = nsl;
	}

	public String getNucleotidSequenceLength() {
		return nucleotidSequenceLength;
	}

	public void setNucleotidSequence(String nucSeq) {
		nucleotidSequence = nucSeq;
	}

	public String getNucleotidSequence() {
		return nucleotidSequence;
	}

	public void setAminoAcidSeqLength(String aminoAcidSeqLength) {
		aminoAcidSequenceLength = aminoAcidSeqLength;
	}

	public String getAminoAcidSeqLength() {
		return aminoAcidSequenceLength;
	}

	public void setAminoAcidSeq(String aminoAcidSeq) {
		aminoAcidSequence = aminoAcidSeq;
	}

	public String getAminoAcidSeq() {
		return aminoAcidSequence;
	}

	public void setOrthology(String orth) {
		if (orth != null) {
			if (!orthology.contains(orth)) {
				orthology.add(orth);
			}
		}
	}

	/**
	 * get orthology as a vector
	 * 
	 * @return orthology
	 */
	public Vector<String> getOrthologyAsVector() {
		return orthology;
	}

	public String getOrthology() {
		return createString(orthology);
	}

	public void setMotif(String mot) {
		if (mot != null) {
			if (!motifs.contains(mot)) {
				motifs.add(mot);
			}
		}
	}

	public String getMotifs() {
		return createString(motifs);
	}

	public Vector<String> getMotifsAsVector() {
		return motifs;
	}

	public void setClassification(String classific) {
		if (classific != null) {
			if (!classification.contains(classific)) {
				classification.add(classific);
			}
		}
	}

	/**
	 * get classifications as a vector
	 * 
	 * @return classifications
	 */
	public Vector<String> getClassificationAsVector() {
		return classification;
	}

	public String getClassification() {
		return createString(classification);
	}

	public void setOrganelle(String organelle) {
		this.organelle = organelle;
	}

	public String getOrganelle() {
		return organelle;
	}

	public void setWeight(String w) {
		weight = w;
	}

	public String getWeigth() {
		return weight;
	}

	public void setTarget(String t) {
		target = t;
	}

	public String getTarget() {
		return target;
	}

	public void setBracket(String br) {
		bracket = br;
	}

	public String getBracket() {
		return bracket;
	}

	public void setOriginal(String or) {
		original = or;
	}

	public String getOriginal() {
		return original;
	}

	public void setRepeat(String rep) {
		repeat = rep;
	}

	public String getRepeat() {
		return repeat;
	}

	public void setActivity(String activ) {
		activity = activ;
	}

	public String getActivity() {
		return activity;
	}

	public void removeElementFromTable(String element) {

		int size = collectorElements.size();
		boolean found = false;

		for (int i = 0; i < size && !found; i++) {

			String[] s = (String[]) collectorElements.get(i);

			if (s[0].equals(element)) {
				collectorElements.remove(i);
				found = true;
			}
		}

		if (!collectorElements.isEmpty()) {
			setList(collectorElements);
		}

	}

	public void setDBLink(String link) {
		if (link != null) {
			if (!dbLinks.contains(link)) {
				dbLinks.add(link);
			}
		}
	}

	/**
	 * get dbLinks as a vector
	 * 
	 * @return dbLinks
	 */
	public Vector<String> getDBLinksAsVector() {
		return dbLinks;
	}

	public Object getDBLinks() {
		return createString(dbLinks);
	}

	public Vector<String[]> getElementsAsVector() {
		return this.collectorElements;
	}

	public int getVectorSize() {
		return countElements;
	}

	public void removeElementsFromTable(int[] rows) {
		Vector<String[]> vec = new Vector<String[]>();
		for (int i = 0; i < rows.length; i++) {
			vec.add(collectorElements.get(rows[i]));
		}
		for (int i = 0; i < vec.size(); i++) {
			collectorElements.remove(vec.get(i));
		}

		if (!collectorElements.isEmpty()) {
			setList(collectorElements);
		}
	}

	public void setType(String t) {
		this.type = t;
	}

	public String getType() {
		return this.type;
	}

	public String getEffect() {
		return effect;
	}

	public void setEffect(String effect) {
		this.effect = effect;

	}

	// public String getSubfamilyIds() {
	// return createString(subfamilyIds);
	// }
	//
	// public void setSubfamilyId(String id) {
	// if (id != null){
	// if (!subfamilyIds.contains(id)) {
	// subfamilyIds.add(id);
	// }
	// }
	// }

	public void setSubfamily(String id) {
		if (id != null) {
			if (!subfamilies.contains(id)) {
				subfamilies.add(id);
			}
		}
	}

	public String getSubfamilies() {
		return createString(subfamilies);
	}

	public Vector<String> getSubfamiliesAsVector() {
		return subfamilies;
	}

	public void setSuperfamily(String id) {
		if (id != null) {
			if (!superfamilies.contains(id)) {
				superfamilies.add(id);
			}
		}
	}

	public String getSuperfamilies() {
		return createString(superfamilies);
	}

	public Vector<String> getSuperfamiliesAsVector() {
		return superfamilies;
	}

	// public Vector <String> getSubfamilyIdsAsVector() {
	// return subfamilyIds;
	// }
	//
	// public String getSubfamilyNames() {
	// return createString(subfamilyNames);
	// }
	//
	// public void setSubfamilyName(String name) {
	// if (name!=null){
	// if (!subfamilyNames.contains(name)) {
	// subfamilyNames.add(name);
	// }
	// }
	// }

	// public Vector <String> getSubfamilyNamesAsVector() {
	// return subfamilyNames;
	// }
	//
	// public String getSuperfamilyIds() {
	// return createString(superfamilyIds);
	// }
	//
	// public void setSuperfamilyId(String id) {
	// if (id != null){
	// if (!superfamilyIds.contains(id)) {
	// superfamilyIds.add(id);
	// }
	// }
	// }

	// public Vector <String> getSuperfamilyIdsAsVector() {
	// return superfamilyIds;
	// }
	//
	// public String getSuperfamilyNames() {
	// return createString(superfamilyNames);
	// }
	//
	// public void setSuperfamilyName(String name) {
	// if (name!=null){
	// if (!superfamilyNames.contains(name)) {
	// superfamilyNames.add(name);
	// }
	// }
	// }

	// public Vector <String> getSuperfamilyNamesAsVector() {
	// return superfamilyNames;
	// }

	public void setInformation(String info) {
		information = info;
	}

	public String getInformation() {
		return information;
	}

	public void setIsoelectricPoint(String point) {
		isoelectricPoint = point;
	}

	public String getIsoelectricPoint() {
		return isoelectricPoint;
	}

	public Object getCatalysts() {
		return createString(catalysts);
	}

	public void setCatalysts(String catalyst) {
		if (catalyst != null) {
			if (!catalysts.contains(catalyst)) {
				catalysts.add(catalyst);
			}
		}
	}

	/**
	 * get catalysts as a vector
	 * 
	 * @return catalysts
	 */
	public Vector<String> getCatalystsAsVector() {
		return catalysts;
	}

	public String getCatalystsNames() {
		return createString(catalystsNames);
	}

	public void setCatalystsName(String catalystName) {
		if (catalystName != null) {
			if (!catalystsNames.contains(catalystName)) {
				catalystsNames.add(catalystName);
			}
		}
	}

	/**
	 * get catalysts names as a vector
	 * 
	 * @return catalysts names
	 */
	public Vector<String> getCatalystNamesAsVector() {
		return catalystsNames;
	}

	public void setDataLoadedString(String text) {
		this.loadedData = text;
	}

	public String getDataLoadedString() {
		return loadedData;
	}

	public void setComponent(String com) {
		this.comment = com;
	}

	public String getComponent() {
		return this.comment;
	}

	public void setIsoformenNumber(String number) {
		this.isoformenNumber = number;
	}

	public String getIsoformenNumber() {
		return this.isoformenNumber;
	}

	public Object getExpressions() {
		return createString(expressions);
	}

	public void setExpression(String expression) {
		if (expression != null) {
			if (!expressions.contains(expression)) {
				expressions.add(expression);
			}
		}
	}

	/**
	 * get catalysts as a vector
	 * 
	 * @return catalysts
	 */
	public Vector<String> getExpressionsAsVector() {
		return expressions;
	}

	public Object getProzesses() {
		return createString(prozesses);
	}

	public void setProzess(String prozess) {
		if (prozess != null) {
			if (!prozesses.contains(prozess)) {
				prozesses.add(prozess);
			}
		}
	}

	/**
	 * get catalysts as a vector
	 * 
	 * @return catalysts
	 */
	public Vector<String> getProzessesAsVector() {
		return prozesses;
	}

	public Object getFunktions() {
		return createString(functions);
	}

	public void setFunction(String function) {
		if (function != null) {
			if (!functions.contains(function)) {
				functions.add(function);
			}
		}
	}

	/**
	 * get catalysts as a vector
	 * 
	 * @return catalysts
	 */
	public Vector<String> getFunctionsAsVector() {
		return functions;
	}

	public void setReference(String ref) {
		if (ref != null) {
			if (!this.reference.contains(ref)) {
				this.reference.add(ref);
			}
		}
	}

	public String getReference() {
		return createString(reference);
	}

	public Vector<String> getReferenceAsVector() {
		return this.reference;
	}

	public void setTransfacGene(String tfGene) {
		this.transfacGene = tfGene;
	}

	public String getTransfacGene() {
		return transfacGene;
	}

	public void setSpecificityNeg(String neg) {
		this.cellSpecificityNeg = neg;
	}

	public String getSpecificityNeg() {
		return this.cellSpecificityNeg;
	}

	public void setSpecificityPos(String pos) {
		this.cellSpecificityPos = pos;
	}

	public String getSpecificityPos() {
		return this.cellSpecificityPos;
	}

	public void setFactorClass(String c) {
		this.factorClass = c;
	}

	public String getFactorClass() {
		return this.factorClass;
	}

	public void setEncodingGene(String gene) {
		this.encodingGene = gene;
	}

	public String getEncodingGene() {
		return this.encodingGene;
	}

	public void setEndPoint(String point) {
		this.endPoint = point;
	}

	public String getEndPoint() {
		return this.endPoint;
	}

	public void setStartPoint(String point) {
		this.startPoint = point;
	}

	public String getStartPoint() {
		return this.startPoint;
	}

	public void setElement(String e) {
		this.element = e;
	}

	public String getElement() {
		return this.element;
	}

	public void addID(String syn, String id) {
		allIDs.put(syn, id);
	}

	public Hashtable<String, String> getAllIDsAsHashtable() {
		return allIDs;
	}

	public void addIDDBRelation(String db, String id) {
		allIDDBRelations.put(db, id);
	}

	// <database, label>
	public Hashtable<String, String> getAllIDDBRelationsAsHashtable() {
		return allIDDBRelations;
	}

	public void setComplexName(String complex) {
		this.complexName = complex;
	}

	public String getComplexName() {
		return this.complexName;
	}

	public void setMethod(String string) {
		if (!methods.contains(string)) {
			methods.add(string);
		}
	}

	public String getMethods() {
		return createString(methods);
	}

	public Vector<String> getMethodsAsSVector() {
		return methods;
	}
}
