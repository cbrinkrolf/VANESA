package database.mirna;

import java.util.ArrayList;

import pojos.DBColumn;

import configurations.Wrapper;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class miRNAqueries {

	public static final String miRNA_onlyName = "select * from mirbase where name like ?";
	
	public static final String miRNA_onlyAccession = "select * from mirbase where accession like ?";
	
	public static final String miRNA_onlySequence = "select * from mirbase where sequence like ?";
	
	public static final String miRNA_all = "select * from mirbase where sequence like ? and accession like ? and name like ?";
	
	public static final String miRNA_acc_name = "select * from mirbase where accession like ? and name like ?";
	
	public static final String miRNA_sequence_acc = "select * from mirbase where sequence like ? and accession like ?";
	
	public static final String miRNA_sequence_name = "select * from mirbase where sequence like ? and name like ?";
	
	public static final String miRNA_get_Genes = "SELECT Gene FROM mirbase_has_tarbase inner join tarbase on tarbase_ID=id where mirbase_ID like ?;";

	public static final String miRNA_get_Pathways = "SELECT kegg_genes_pathway.name,kegg_genes_pathway.name," +
	"kegg_genes_pathway.number,kegg_genes_pathway.org, kegg_genes_name.name FROM " +
	"dawismd.kegg_genes_pathway inner join "+
	"dawismd.kegg_genes_name on kegg_genes_pathway.id=kegg_genes_name.id "+ 
	"where kegg_genes_name.name in ? and kegg_genes_pathway.org='hsa' order by kegg_genes_pathway.name," +
	"kegg_genes_name.name;";
	
	public static ArrayList<DBColumn> getMiRNAsOfPathway(String pathway){
		
		String query="select distinct mirbase.name, tarbase.gene, mirbase.sequence, tarbase.Accession, tarbase.Ensemble, tarbase.IS, tarbase.DS"
		+ " from dawismd.kegg_kgml_pathway pathway" 
		+ " inner join dawismd.kegg_genes_pathway genes_pathway on genes_pathway.number=pathway.number"
		+ " inner join dawismd.kegg_genes genes on genes.id=genes_pathway.id"
		+ " inner join dawismd.kegg_genes_name name on name.id=genes.id"
		+ " inner join db_mirna.tarbase on name.name=tarbase.gene"
		+ " inner join db_mirna.mirbase_has_tarbase  on tarbase_ID=tarbase.ID"
		+ " inner join db_mirna.mirbase on mirbase.ID=mirBase_ID"
		+ " where pathway.name='"+pathway+"';";
		//System.out.println("pw: "+pathway + " "+query);
		return new Wrapper().requestDbContent(2, query);		
	}
	
}
