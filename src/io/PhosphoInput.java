package io;

import java.io.File;
import java.io.IOException;

public class PhosphoInput {

	public PhosphoInput(File file) throws IOException {
		
		// CHRIS reimplement if necessary

		/*GraphInstance instance = new GraphInstance();
		Pathway pw = instance.getPathway();
		pw.getGraph().lockVertices();
		pw.getGraph().stopVisualizationModel();

		Hashtable phosphoTabel = new Hashtable<String, Vector>();
		StringBuffer buff = new StringBuffer();
		Iterator<BiologicalNodeAbstract> it = pw.getAllNodes().iterator();

		BiologicalNodeAbstract bna;
		while (it.hasNext()) {
			bna = it.next();
			// System.out.println(bna.getLabel());
			String labelIdentifier = "";
			ArrayList<DBColumn> results = new ArrayList<DBColumn>();
			results = new Wrapper().requestDbContent(3,
					"Select gene_locus from uniprot_locusnames u where uniprot_id='"
							+ bna.getLabel() + "';");

			for (DBColumn column : results) {
				String[] resultDetails = (String[]) column.getColumn();
				labelIdentifier = resultDetails[0];

			}

			BufferedReader in = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = in.readLine()) != null) {

				if (line.startsWith("Y")) {

					int counter_tok = 0;
					StringTokenizer tok = new StringTokenizer(line, "\t");
					boolean check = false;
					while (tok.hasMoreTokens() && counter_tok < 3) {

						String temp_tok = tok.nextToken();
						if (labelIdentifier.equals(temp_tok)) {
							if (check == false) {
								buff.append(bna.getLabel() + "/" + temp_tok);
							}
							check = true;
						} else if (check == true && counter_tok == 2) {
							buff.append(" Site:" + temp_tok + "\n");

							Other p = new Other(temp_tok, temp_tok, pw
									.getGraph().createNewVertex());
							p.setColor(Color.green);
							p.setReference(false);

							pw.addElement(p);
							pw.getGraph().moveVertex(p.getVertex(), 10, 10);

							ReactionEdge reactionedge = new ReactionEdge(pw
									.getGraph().createEdge(bna.getVertex(),
											p.getVertex(), false), "p", "p");
							reactionedge.setColor(Color.green);
							reactionedge.setReference(false);
							pw.addElement(reactionedge);

						}
						counter_tok++;
					}
				}
			}
		}
		//System.out.println(buff.toString());
		pw.getGraph().unlockVertices();
		pw.getGraph().restartVisualizationModel();
		pw.getGraph().normalCentering();
		MainWindowSingelton.getInstance().updateProjectProperties();
		MainWindowSingelton.getInstance().updateOptionPanel();
		*/
	}


}

// SELECT ord_loc_names FROM uniprot_ord_loc_names where
// uniprot_id="RAD52_YEAST"
// DB_UNIPROT