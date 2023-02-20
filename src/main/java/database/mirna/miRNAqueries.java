package database.mirna;

public class miRNAqueries {
    // genes which are source for given miRNA
    public static final String miRNA_get_SourceGenes =
            "SELECT DISTINCT OverlappingTranscripts.Name " +
                    "FROM OverlappingTranscripts " +
                    "JOIN Hairpins ON OverlappingTranscripts.hpID = Hairpins.ID " +
                    "JOIN Matures ON Hairpins.ID = Matures.hpID " +
                    "WHERE OverlappingTranscripts.Name = OverlappingTranscripts.Accession AND Matures.Name = ?;";

    // miRNAs are produced by given gene (where given gene is origin for)
    public static final String miRNA_get_SourcingMirnas =
            "SELECT DISTINCT Matures.Name " +
                    "FROM Matures " +
                    "JOIN Hairpins ON Matures.hpID = Hairpins.ID " +
                    "INNER JOIN OverlappingTranscripts ON Matures.hpID = OverlappingTranscripts.hpID " +
                    "WHERE OverlappingTranscripts.Name = OverlappingTranscripts.Accession AND OverlappingTranscripts.Name = ?;";
}
