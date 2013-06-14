package database.dawis;

import gui.MainWindow;
import gui.ProgressBar;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import pojos.DBColumn;
import configurations.Wrapper;
import database.Connection.DatabaseQueryValidator;
import database.dawis.gui.DAWISSearchResultWindow;

/**
 * 
 * @author Olga
 * 
 */

/*
 * get data of the object of interest
 */
@SuppressWarnings("unchecked")
public class DAWISSearch extends SwingWorker {

	String object, name, id, organism;

	boolean organismusSpecification = false;
	Hashtable<String, String> hasName = new Hashtable<String, String>();
	Vector<String[]> res = new Vector<String[]>();
	String[] organismSynonyms = new String[3];
	String[] pathwayDB = { "KEGG", "Transpath" };
	String[] geneDB = { "KEGG", "Transpath", "Transfac", "EMBL" };
	String[] compoundDB = { "KEGG", "Transpath" };
	String[] diseaseDB = { "OMIM" };
	String[] enzymeDB = { "KEGG" };
	String[] drugDB = { "KEGG" };
	String[] glycanDB = { "KEGG" };
	String[] reactionDB = { "KEGG", "Transpath" };
	String[] reactionPairDB = { "KEGG" };
	String[] proteinDB = { "HPRD", "Transfac", "UniProt" };
	String[] geneOntologyDB = { "GO" };

	String[] db;
	String mode;

	MainWindow w;
	private ProgressBar bar;

	private DatabaseQueryValidator dqv = new DatabaseQueryValidator();
	DAWISSearchResultWindow dsrw;

	public DAWISSearch(String[] input, String mode, boolean spez, MainWindow w,
			ProgressBar bar) {

		object = input[0];
		name = input[1];
		id = input[2];
		organism = input[3];

		if (!organism.equals("")) {
			organismSynonyms = getOrganismSynonyms(organism);
		}

		this.mode = mode;
		this.organismusSpecification = spez;
		this.w = w;
		this.bar = bar;

		chooseDB(object);
	}

	// choose all databases that could contain the object
	private void chooseDB(String obj) {
		if (obj.equals("Pathway Map")) {
			db = pathwayDB;
		} else if (obj.equals("Disease")) {
			db = diseaseDB;
		} else if (obj.equals("Enzyme")) {
			db = enzymeDB;
		} else if (obj.equals("Protein")) {
			db = proteinDB;
		} else if (obj.equals("Gene Ontology")) {
			db = geneOntologyDB;
		} else if (obj.equals("Gene")) {
			db = geneDB;
		} else if (obj.equals("Drug")) {
			db = drugDB;
		} else if (obj.equals("Glycan")) {
			db = glycanDB;
		} else if (obj.equals("Compound")) {
			db = compoundDB;
		} else if (obj.equals("Reaction")) {
			db = reactionDB;
		} else if (obj.equals("Reaction Pair")) {
			db = reactionPairDB;
		}

	}

	/**
	 * get organism synonyms
	 * 
	 * @param organism
	 * @return synonyms [String]
	 */
	private String[] getOrganismSynonyms(String organism) {
		String[] syn = null;
		String synonyms = DAWISQueries.getKEGGOrganismSynonyms;
		String firstQuery = new String();

		firstQuery = synonyms + " t.org = '" + organism + "' OR "
				+ " t.latin_name = '" + organism + "' OR " + " t.name = '"
				+ organism + "'";

		ArrayList<DBColumn> results = new Wrapper().requestDbContent(3,
				firstQuery);

		for (DBColumn column : results) {
			syn = column.getColumn();

			for (int i = 0; i < syn.length; i++) {
				syn[i] = syn[i].toLowerCase();

				if (syn[i].endsWith(".")) {
					syn[i] = syn[i].substring(0, syn[i].length() - 1);
				}
			}
		}

		return syn;
	}

	// /**
	// * get organism synonyms
	// *
	// * @param organism
	// * @return synonyms [String]
	// */
	// private String[] getOrganismSynonyms(String organism) {
	//
	// String[] syn = null;
	// String synonyms = DAWISQueries.getKEGGOrganismSynonyms;
	// String firstQuery = "";
	//
	// firstQuery = synonyms + " t.org = '" + organism + "' OR "
	// + " t.latin_name = '" + organism + "' OR " + " t.name = '"
	// + organism + "'";
	//
	// Vector<String[]> results = new Wrapper().requestDbContent(3, firstQuery);
	//
	// Iterator<String[]> it = results.iterator();
	// while (it.hasNext()) {
	// syn = (String[]) it.next();
	// for (int i = 0; i < syn.length; i++) {
	// syn[i] = syn[i].toLowerCase();
	// if (syn[i].endsWith(".")) {
	// syn[i] = syn[i].substring(0, syn[i].length() - 1);
	// }
	// }
	// }
	//
	// return syn;
	// }

	/**
	 * get start query of the object dependent on database
	 * 
	 * @param object
	 * @return query
	 * @throws SQLException
	 */
	private String getQuery(String object, String db) throws SQLException {

		String queryStart = "";

		if (object.equals("Pathway Map")) {

			if (db.equals("KEGG")) {
				queryStart = DAWISQueries.getKEGGPathwayStartQuery;
				String settings[] = { queryStart, "p.pathway_name", "p.title" };
				if (organismusSpecification) {
					queryStart = prepairQuery(settings, "p.org", db);
				} else {
					queryStart = prepairQuery(settings, "", db);
				}
				queryStart = queryStart + " order by p.pathway_name";
			} else if (db.equals("Transpath")) {
				queryStart = DAWISQueries.getTranspathPathwayStartQuery;
				String settings[] = { queryStart, "tpp.pathway_id",
						"tpp.pathway_name" };
				queryStart = prepairQuery(settings, "", db);
				queryStart = queryStart + " order by tpp.pathway_id";
			}

		} else if (object.equals("Disease")) {

			if (db.equals("OMIM")) {
				queryStart = DAWISQueries.getOMIMDiseaseStartQuery;
				String settings[] = { queryStart, "d.mim", "d.title",
						"os.osynonym" };
				queryStart = prepairQuery(settings, "", db);
				queryStart = queryStart + " order by d.mim;";
			}

		} else if (object.equals("Gene Ontology")) {

			if (db.equals("GO")) {
				queryStart = DAWISQueries.getGOStartQuery;
				String settings[] = { queryStart, "gt.acc", "gt.name",
						"gts.term_synonym" };
				if (organismusSpecification) {
					queryStart = prepairQuery(settings, "gs.species", db);
				} else {
					queryStart = prepairQuery(settings, "", db);
				}
				queryStart = queryStart + " order by gt.acc;";
			}

		} else if (object.equals("Gene")) {

			if (db.equals("KEGG")) {
				queryStart = DAWISQueries.getGeneStartQuery;
				String settings[] = { queryStart, "g.entry", "g.name" };
				if (organismusSpecification) {
					queryStart = prepairQuery(settings, "t.org", db);
				} else {
					queryStart = prepairQuery(settings, "", db);
				}
				queryStart = queryStart + " order by g.entry;";
			} else if (db.equals("Transpath")) {
				queryStart = DAWISQueries.getTranspathGeneStartQuery;
				String settings[] = { queryStart, "tpg.gene_id",
						"tpg.gene_name", "tpgs.gene_synonym " };
				if (organismusSpecification) {
					queryStart = prepairQuery(settings, "tpo.latin_name", db);
				} else {
					queryStart = prepairQuery(settings, "", db);
				}
				queryStart = queryStart + " order by tpg.gene_id;";
			} else if (db.equals("Transfac")) {
				queryStart = DAWISQueries.getTransfacGeneStartQuery;
				String settings[] = { queryStart, "tfg.gene_id",
						"tfg.short_gene_term", "tfgs.gene_synonym" };
				if (organismusSpecification) {
					queryStart = prepairQuery(settings, "tfo.latin_name", db);
				} else {
					queryStart = prepairQuery(settings, "", db);
				}
				queryStart = queryStart + " order by tfg.gene_id;";
			} else if (db.equals("EMBL")) {
				queryStart = DAWISQueries.getEMBLGeneStartQuery;
				String settings[] = { queryStart, "ed.primary_ac",
						"ed.description" };
				if (organismusSpecification) {
					queryStart = prepairQuery(settings, "eo.species", db);
				} else {
					queryStart = prepairQuery(settings, "", db);
				}
				queryStart = queryStart + " order by ed.primary_ac;";
			}

		} else if (object.equals("Protein")) {

			if (db.equals("UniProt")) {
				queryStart = DAWISQueries.getUniProtProteinStartQuery;
				String settings[] = { queryStart, "p.uniprot_id",
						"p.description" };
				if (organismusSpecification) {
					queryStart = prepairQuery(settings, "p.species", db);
				} else {
					queryStart = prepairQuery(settings, "", db);
				}
				queryStart = queryStart
						+ " order by p.uniprot_id limit 0,1000;";
			} else if (db.equals("HPRD")) {
				queryStart = DAWISQueries.getHPRDProteinStartQuery;
				String settings[] = { queryStart, "hps.HPRD_ID",
						"hps.protein_name" };
				queryStart = prepairQuery(settings, "", db);
				queryStart = queryStart + " order by hps.HPRD_ID;";
			} else if (db.equals("Transfac")) {
				queryStart = DAWISQueries.getTransfacProteinStartQuery;
				String settings[] = { queryStart, "tff.factor_id",
						"tff.factor_name" };
				if (organismusSpecification) {
					queryStart = prepairQuery(settings, "tff.organism_Id", db);
				} else {
					queryStart = prepairQuery(settings, "", db);
				}
				queryStart = queryStart + " order by tff.factor_id;";
			}

		} else if (object.equals("Enzyme")) {

			if (db.equals("KEGG")) {
				queryStart = DAWISQueries.getEnzymeStartQuery;
				String settings[] = { queryStart, "ke.entry", "ke.sysname",
						"ken.name", "bs.bsynonym" };
				if (organismusSpecification) {
					queryStart = prepairQuery(settings, "bo.org_name", db);
				} else {
					queryStart = prepairQuery(settings, "", db);
				}
				queryStart = queryStart + " order by ke.entry;";

			}
		} else if (object.equals("Reaction")) {

			if (db.equals("KEGG")) {
				queryStart = DAWISQueries.getReactionStartQuery;
				String settings[] = { queryStart, "kr.entry", "kr.name" };
				if (organismusSpecification) {
					queryStart = prepairQuery(settings, "kpr.organismus", db);
				} else {
					queryStart = prepairQuery(settings, "", db);
				}
				queryStart = queryStart + " order by kr.entry;";

			} else if (db.equals("Transpath")) {
				queryStart = DAWISQueries.getTranspathReactionStartQuery;
				String settings[] = { queryStart, "tpr.reaction_id",
						"tpr.reaction_name" };
				queryStart = prepairQuery(settings, "", db);
				queryStart = queryStart + " order by tpr.reaction_id;";
			}
		} else if (object.equals("Reaction Pair")) {

			if (db.equals("KEGG")) {
				queryStart = DAWISQueries.getReactionPairStartQuery;
				String settings[] = { queryStart, "kr.entry", "kr.name" };
				if (organismusSpecification) {
					queryStart = prepairQuery(settings, "kpr.organismus", db);
				} else {
					queryStart = prepairQuery(settings, "", db);
				}
				queryStart = queryStart + " order by kr.entry;";
			}
		} else if (object.equals("Compound")) {
			if (db.equals("KEGG")) {
				queryStart = DAWISQueries.getCompoundStartQueryOrganismIndependent;
				String settings[] = { queryStart, "kc.entry", "kc.formula",
						"kcn.name" };
				queryStart = prepairQuery(settings, "", db);
				queryStart = queryStart + " order by kc.entry;";
			} else if (db.equalsIgnoreCase("Transpath")) {
				queryStart = DAWISQueries.getTPCompoundStartQuery;
				String settings[] = { queryStart, "molecule_id",
						"molecule_name" };
				queryStart = prepairQuery(settings, "", db);
				queryStart = queryStart + " order by molecule_id;";
			}

		} else if (object.equals("Glycan")) {
			if (db.equals("KEGG")) {
				queryStart = DAWISQueries.getGlycanStartQueryOrganismIndependent;
				String settings[] = { queryStart, "kg.entry", "" };
				queryStart = prepairQuery(settings, "", db);
				queryStart = queryStart + " order by kg.entry;";
			}
		} else if (object.equals("Drug")) {
			if (db.equals("KEGG")) {
				queryStart = DAWISQueries.getDrugStartQuery;
				String settings[] = { queryStart, "kd.entry", "kdn.name" };
				queryStart = prepairQuery(settings, "", db);
				queryStart = queryStart + " order by kd.entry;";
			}
		}

		return queryStart;
	}

	/**
	 * complete query
	 * 
	 * @param settings
	 * @return query
	 */
	private String prepairQuery(String[] settings, String organismus, String db) {

		String queryStart = settings[0];
		String idColumn = settings[1];
		String nameColumn = settings[2];
		String synonymColumn = "";
		String synonymBrenda = "";

		if (settings.length > 3) {
			synonymColumn = settings[3];
		}
		if (settings.length > 4) {
			synonymBrenda = settings[4];
		}

		boolean firstCriteria = false;

		if (!id.equals("")) {

			String temp = dqv.replaceAndValidateString(id);

			if (temp.length() > 0) {
				queryStart = queryStart + dqv.prepareString(id, idColumn, null);
				firstCriteria = true;
			}

		}
		if (!name.equals("")) {

			String temp = dqv.replaceAndValidateString(name);

			if (temp.length() > 0) {

				if (firstCriteria) {
					if (!synonymColumn.equals("")) {
						queryStart = queryStart + " AND ("
								+ dqv.prepareString(name, nameColumn, null)
								+ " OR "
								+ dqv.prepareString(name, synonymColumn, null);
						if (!synonymBrenda.equals("")) {
							queryStart = queryStart
									+ "or "
									+ dqv.prepareString(name, synonymBrenda,
											null) + ")";
						} else {
							queryStart = queryStart + ")";
						}
					} else {
						queryStart = queryStart + " AND "
								+ dqv.prepareString(name, nameColumn, null);
					}
				} else {
					if (!synonymColumn.equals("")) {
						queryStart = queryStart
								+ dqv.prepareString(name, nameColumn, null)
								+ " OR "
								+ dqv.prepareString(name, synonymColumn, null);
						if (!synonymBrenda.equals("")) {
							queryStart = queryStart
									+ "or "
									+ dqv.prepareString(name, synonymBrenda,
											null);
						}
					} else {
						queryStart = queryStart
								+ dqv.prepareString(name, nameColumn, null);
					}
				}

				firstCriteria = true;

			}

		}

		// add organism part of query
		if (organismusSpecification) {
			if (!organismus.equals("")) {
				String organismPart = "";
				boolean first = true;
				if (organismSynonyms != null && !organismSynonyms[0].equals("")) {
					organismPart = "(";
					for (int i = 0; i < organismSynonyms.length; i++) {
						if (first) {
							organismPart = organismPart + organismus
									+ " like '%" + organismSynonyms[i] + "%' ";
							first = false;
						} else {
							organismPart = organismPart + " or " + organismus
									+ " like '%" + organismSynonyms[i] + "%' ";
						}
					}
					organismPart = organismPart + ")";
				} else {
					organismPart = organismus + " like '%" + organism + "%'";
					if (db.equals("KEGG")) {
						if (!object.equals("Enzyme")) {
							organismPart = organismPart
									+ " OR  t.latin_name like '%" + organism
									+ "%' OR  t.name like'%" + organism + "%'";
						}
					}
				}

				queryStart = queryStart + " and (" + organismPart + ")";

			}

		}

		return queryStart;
	}

	/**
	 * get results of the search as vector
	 * 
	 * @param container
	 * @throws SQLException
	 */
	public Vector<String[]> requestDbContentAsVector() throws SQLException {
		Vector<String[]> container = new Vector<String[]>();
		String query = new String();

		for (int i = 0; i < db.length; i++) {
			query = getQuery(object, db[i]);
			ArrayList<DBColumn> results = new Wrapper().requestDbContent(3,
					query);

			if (results.size() > 0) {
				for (DBColumn column : results) {
					String[] entry = column.getColumn();
					String[] row = new String[4];

					for (int a = 0; a < entry.length; a++) {
						if (entry[a] != null) {
							row[a] = entry[a];
						} else {
							row[a] = "";
						}
					}

					if (entry.length < 2) {
						row[1] = "";
					}

					if (entry.length < 3) {
						row[2] = "";
					}

					if (object.equals("Disease")
							|| (object.equals("Protein") && db[i]
									.equals("HPRD"))) {
						row[2] = "h.sapiens";
					}

					row[3] = db[i];
					container.add(row);
				}
			}
		}
		return container;
	}

	// /**
	// * get results of the search as vector
	// *
	// * @param container
	// * @throws SQLException
	// */
	// public Vector<String[]> requestDbContentAsVector() throws SQLException
	// {
	// Vector<String[]> container=new Vector<String[]>();
	// String query=new String();
	//
	// for (int i=0; i<db.length; i++)
	// {
	// query=getQuery(object, db[i]);
	// Vector<String[]> results=new Wrapper().requestDbContent(3, query);
	//
	// if (results.size()>0)
	// {
	// for (Iterator<String[]> it=results.iterator(); it.hasNext();)
	// {
	// String[] entry=(String[])it.next();
	// String[] row=new String[4];
	//
	// for (int a=0; a<entry.length; a++)
	// {
	// if (entry[a]!=null)
	// {
	// row[a]=entry[a];
	// }
	// else
	// {
	// row[a]="";
	// }
	// }
	//
	// if (entry.length<2)
	// {
	// row[1]="";
	// }
	//
	// if (entry.length<3)
	// {
	// row[2]="";
	// }
	//
	// if (object.equals("Disease")
	// ||(object.equals("Protein")&&db[i].equals("HPRD")))
	// {
	// row[2]="h.sapiens";
	// }
	//
	// row[3]=db[i];
	// container.add(row);
	// }
	// }
	// }
	// return container;
	// }

	// /**
	// * get results of the search as vector
	// *
	// * @param container
	// * @throws SQLException
	// */
	// public Vector<String[]> requestDbContentAsVector() throws SQLException {
	//
	// Vector<String[]> container = new Vector<String[]>();
	// String query = "";
	// for (int i = 0; i < db.length; i++) {
	// query = getQuery(object, db[i]);
	// Vector<String[]> results = new Wrapper().requestDbContent(3, query);
	//
	// if (results.size() > 0) {
	// for (Iterator<String[]> it = results.iterator(); it.hasNext();) {
	// String[] entry = (String[]) it.next();
	// String[] row = new String[4];
	//
	// for (int a = 0; a < entry.length; a++) {
	// if (entry[a] != null) {
	// row[a] = entry[a];
	// } else {
	// row[a] = "";
	// }
	// }
	//
	// if (entry.length < 2) {
	// row[1] = "";
	// }
	//
	// if (entry.length < 3) {
	// row[2] = "";
	// }
	//
	// if (object.equals("Disease")
	// || (object.equals("Protein") && db[i]
	// .equals("HPRD"))) {
	// row[2] = "h.sapiens";
	// }
	//
	// row[3] = db[i];
	// container.add(row);
	// }
	// }
	// }
	// return container;
	// }

	public void setSpecification(boolean specification) {
		if (object.equals("Disease")) {
			this.organismusSpecification = true;
		} else {
			this.organismusSpecification = specification;
		}

	}

	protected Object doInBackground() throws Exception {
		w.setLockedPane(true);
		res = requestDbContentAsVector();
		w.setLockedPane(false);

		return null;
	}

	public void done() {

		Boolean continueProgress = false;
		endSearch(w, bar);

		if (res.size() > 0) {
			continueProgress = true;
			dsrw = new DAWISSearchResultWindow(res, this.object, this.mode,
					this.organismusSpecification);
		} else {
			endSearch(w, bar);
			JOptionPane.showMessageDialog(w,
					"Sorry, no entries have been found.");
		}

		if (continueProgress) {
			Vector results = dsrw.getAnswer();
			if (results.size() != 0) {
				final Iterator<String[]> it = results.iterator();
				while (it.hasNext()) {

					String[] element = it.next();

					DAWISConnector con;
					if (mode.equals("Expert")) {
						con = new DAWISConnector(bar, dsrw.getSettings());
					} else {
						con = new DAWISConnector(bar);
					}

					con.setSearchDepth(dsrw.getSerchDeapth());
					con.setOrganismSpecification(organismusSpecification);
					con.setObject(object);
					if (object.equals("Disease")) {
						con.setOrganismSpecification(true);
					}
					try {
						con.createPathway(element);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					con.execute();

				}
			}
		}
		endSearch(w, bar);
	}

	private void endSearch(final MainWindow w, final ProgressBar bar) {
		Runnable run = new Runnable() {
			public void run() {
				bar.closeWindow();
				w.setEnable(true);
			}
		};

		SwingUtilities.invokeLater(run);
	}

}
