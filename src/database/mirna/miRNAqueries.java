package database.mirna;

import java.util.ArrayList;

import pojos.DBColumn;
import configurations.Wrapper;

public class miRNAqueries {

	public static final String miRNA_onlyName = "select distinct Name, Sequence from Matures where Name like ? LIMIT 100";

	public static final String miRNA_onlyGene = "select distinct Accession, DB from TargetGene where Accession like ? LIMIT 100";

	public static final String miRNA_onlyAccession = "select distinct Name, Accession from Matures where Accession like ? LIMIT 100";

	public static final String miRNA_onlySequence = "select distinct Name, Sequence from Matures where Sequence like ?";

	public static final String miRNA_all = "select distinct Name, Sequence from Matures where Sequence like ? and Accession like ? and Name like ?";

	public static final String miRNA_acc_name = "select distinct Name, Accession from Matures where Accession like ? and Name like ?";

	public static final String miRNA_sequence_acc = "select distinct Name, Sequence from Matures where Sequence like ? and Accession like ?";

	public static final String miRNA_sequence_name = "select distinct Name, Sequence from Matures where Sequence like ? and Name like ?";

	public static final String miRNA_get_Genes = "SELECT distinct TargetGene.Accession, TargetGene.DB FROM Matures Matures inner join TargetGenes TargetGenes on Matures.ID=TargetGenes.mID inner join TargetGene TargetGene on TargetGenes.ID=TargetGene.tgsID where Matures.Name = ? AND TargetGene.DB = '?';";

	public static final String miRNA_get_Mirnas = "SELECT distinct Matures.Name FROM Matures Matures inner join TargetGenes TargetGenes on Matures.ID=TargetGenes.mID inner join TargetGene TargetGene on TargetGenes.ID=TargetGene.tgsID where TargetGene.Accession = ?;";

	public static final String miRNA_get_Pathways = "SELECT kegg_genes_pathway.name,kegg_genes_pathway.name,"
			+ "kegg_genes_pathway.number,kegg_genes_pathway.org, kegg_genes_name.name FROM "
			+ "dawismd.kegg_genes_pathway inner join "
			+ "dawismd.kegg_genes_name on kegg_genes_pathway.id=kegg_genes_name.id "
			+ "where kegg_genes_name.name in ? and kegg_genes_pathway.org='hsa' order by kegg_genes_pathway.name,"
			+ "kegg_genes_name.name;";

	public static ArrayList<DBColumn> getMiRNAsOfPathway(String pathway) {

		String query = "select distinct mirbase.name, tarbase.gene, mirbase.sequence, tarbase.Accession, tarbase.Ensemble, tarbase.IS, tarbase.DS"
				+ " from dawismd.kegg_kgml_pathway pathway"
				+ " inner join dawismd.kegg_genes_pathway genes_pathway on genes_pathway.number=pathway.number"
				+ " inner join dawismd.kegg_genes genes on genes.id=genes_pathway.id"
				+ " inner join dawismd.kegg_genes_name name on name.id=genes.id"
				+ " inner join db_mirna.tarbase on name.name=tarbase.gene"
				+ " inner join db_mirna.mirbase_has_tarbase  on tarbase_ID=tarbase.ID"
				+ " inner join db_mirna.mirbase on mirbase.ID=mirBase_ID"
				+ " where pathway.name='" + pathway + "';";
		// System.out.println("pw: "+pathway + " "+query);
		return new Wrapper().requestDbContent(Wrapper.dbtype_KEGG, query);
	}

}
