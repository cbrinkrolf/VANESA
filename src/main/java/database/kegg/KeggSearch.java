package database.kegg;

import api.VanesaApi;
import api.payloads.Response;
import api.payloads.kegg.KeggPathway;
import api.payloads.kegg.PathwaySearchRequestPayload;
import api.payloads.kegg.PathwaySearchResponsePayload;
import com.fasterxml.jackson.core.type.TypeReference;
import database.kegg.gui.KEGGSearchResultWindow;
import gui.MainWindow;
import gui.PopUpDialog;

public class KeggSearch {
	public static void searchPathways(String pathway, String organism, String enzyme, String gene, String compound) {
		PathwaySearchRequestPayload payload = new PathwaySearchRequestPayload();
		payload.pathway = pathway;
		payload.organism = organism;
		payload.enzyme = enzyme;
		payload.gene = gene;
		payload.compound = compound;
		Response<PathwaySearchResponsePayload> response = VanesaApi.postSync("/kegg/pathway/search", payload,
				new TypeReference<>() {
				});
		MainWindow.getInstance().closeProgressBar();
		if (response.hasError()) {
			PopUpDialog.getInstance().show("KEGG search", "Sorry, no entries have been found.\n" + response.error);
			return;
		}
		if (response.payload == null || response.payload.results == null || response.payload.results.length == 0) {
			PopUpDialog.getInstance().show("KEGG search", "Sorry, no entries have been found.");
			return;
		}
		KEGGSearchResultWindow searchResultWindow = new KEGGSearchResultWindow(response.payload.results);
		if (!searchResultWindow.show()) {
			return;
		}
		KeggPathway[] results = searchResultWindow.getSelectedValues();
		if (results.length > 0) {
			// TODO
			/*
				MainWindow.getInstance().showProgressBar("Retrieving PPI Network(s)");
				int depth = searchResultWindow.getSearchDepth();
				boolean autoCoarse = searchResultWindow.getAutoCoarse();
				for (HPRDEntry entry : results) {
					requestHPRDPPI(entry, depth, autoCoarse);
				}
				MainWindow.getInstance().closeProgressBar();
			*/
		}
	}

	/*
	private int answer = JOptionPane.YES_OPTION;
	private ArrayList<DBColumn> results = new ArrayList<>();
	private boolean continueProgress = false;
	private Pathway mergePW;

	public ArrayList<DBColumn> requestDbContent() {
		final DatabaseQueryValidator dqv = new DatabaseQueryValidator();

		String queryStart = "Select pathway.pathway_name from ";
		String queryEnd = "";
		boolean firstEntries = false;
		if (!pathway.equals("")) {
			queryStart += "(SELECT p.pathway_name, p.title, p.org FROM kegg_pathway p WHERE " + dqv.prepareString(pathway, "p.pathway_name", "p.title")
					+ ") as pathway";
			firstEntries = true;
		}
		if (!enzyme.equals("")) {
			if (firstEntries) {
				queryStart += " Inner join (" + KEGGQueries.KEGGEnzymeQuery
						+ dqv.prepareString(enzyme, "n.entry", "n.name") + ") as enzyme ";
				queryEnd = " on pathway.pathway_name=enzyme.pathway_name";
			} else {
				firstEntries = true;
				queryStart += "(" + KEGGQueries.KEGGEnzymeQuery + dqv.prepareString(enzyme, "n.entry", "n.name")
						+ ") as pathway ";
			}
		}
		if (!gene.equals("")) {
			if (firstEntries) {
				queryStart += " Inner join (" + KEGGQueries.KEGGGeneQuery + dqv.prepareString(gene, "n.entry", "n.name")
						+ ") as gene ";
				if (queryEnd.equals("")) {
					queryEnd = " on pathway.pathway_name=gene.pathway_name";
				} else {
					queryEnd = queryEnd + "=gene.pathway_name";
				}
			} else {
				queryStart += "(" + KEGGQueries.KEGGGeneQuery + dqv.prepareString(gene, "n.entry", "n.name")
						+ ") as pathway ";
				firstEntries = true;
			}
		}
		if (!compound.equals("")) {
			if (firstEntries) {
				queryStart += " Inner join (" + KEGGQueries.KEGGCompoundQuery
						+ dqv.prepareString(compound, "c.entry", "c.name") + ") as compound ";
				if (queryEnd.equals("")) {
					queryEnd = " on pathway.pathway_name=compound.pathway_name";
				} else {
					queryEnd = queryEnd + "=compound.pathway_name";
				}
			} else {
				queryStart += "(" + KEGGQueries.KEGGCompoundQuery + dqv.prepareString(compound, "c.entry", "c.name")
						+ ") as pathway ";
				firstEntries = true;
			}
		}
		StringBuilder pathway_names = new StringBuilder("(");
		ArrayList<DBColumn> tempResults = new ArrayList<>();
		if (firstEntries) {
			tempResults = new Wrapper().requestDbContent(queryStart + queryEnd + " LIMIT 0,1000;");
			boolean firstPathwayName = true;
			for (DBColumn column : tempResults) {
				String[] d = column.getColumn();
				if (!firstPathwayName) {
					pathway_names.append(',');
				}
				pathway_names.append('\'').append(d[0]).append("'");
				firstPathwayName = false;
			}
		}
		pathway_names.append(")");
		String lastQuery;
		boolean organism = StringUtils.isNotEmpty(this.organism);
		if (!organism && tempResults.size() > 0) {
			lastQuery = "Select p.pathway_name, p.title, t.name FROM kegg_pathway p LEFT OUTER JOIN kegg_taxonomy as t on p.org=t.org where p.pathway_name In " + pathway_names;
		} else if (organism && pathway_names.length() < 4) {
			lastQuery = "Select p.pathway_name, p.title, t.name FROM kegg_pathway p LEFT OUTER JOIN kegg_taxonomy as t on p.org=t.org where " + dqv.prepareString(this.organism, "t.name", "t.latin_name");
		} else {
			lastQuery = "Select p.pathway_name, p.title, t.name FROM kegg_pathway p LEFT OUTER JOIN kegg_taxonomy as t on p.org=t.org where p.pathway_name In " + pathway_names + " AND " + dqv.prepareString(this.organism, "t.name", "t.latin_name");
		}
		return new Wrapper().requestDbContent(lastQuery + " LIMIT 0,1000;");
	}

	@Override
	public void done() {
		if (results.size() > 1 || mergePW != null)
			answer = JOptionPane.showOptionDialog(w.getFrame(),
					"Shall the selected Pathways be loaded each into a separate tab, be combined into an overview Pathway or merged?",
					"Several Pathways selected...", JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE, null,
					new String[] { "Separate Tabs", "Overview Pathway", "Merge Pathways" }, JOptionPane.YES_OPTION);
		Iterator<String[]> it = results.iterator();
		int OVERVIEW_PW = JOptionPane.NO_OPTION;
		if (answer == OVERVIEW_PW) {
			Pathway newPW = new CreatePathway("Overview Pathway").getPathway();
			ArrayList<BiologicalNodeAbstract> bnas = new ArrayList<>();
			if (!(enzyme == null || enzyme.equals("")))
				bnas.add(new Enzyme(enzyme, enzyme));
			if (!(gene == null || gene.equals("")))
				bnas.add(new Gene(gene, gene));
			if (!(compound == null || compound.equals("")))
				bnas.add(new Metabolite(compound, compound));
			for (String[] s : results) {
				PathwayMap map = new PathwayMap(s[1], s[0]);
				map = (PathwayMap) newPW.addVertex(map, new Point(0, 0));
				for (BiologicalNodeAbstract bna : bnas) {
					bna = newPW.addVertex(bna, new Point(0, 0));
					bna.setColor(Color.red);
					Compound c = new Compound("", "", bna, map);
					c.setDirected(true);
					newPW.addEdge(c);
				}
			}
			newPW.getGraph().restartVisualizationModel();
			w.updateAllGuiElements();
			newPW.getGraph().changeToGEMLayout();
			newPW.getGraph().normalCentering();
		} else {
			String[] details = it.next();
			KEGGConnector kc = new KEGGConnector(details[0], details[2], mergePW != null);
			kc.addPropertyChangeListener(this);
			kc.setSearchMicroRNAs(dsrw.getSearchMirnas());
			kc.setAutoCoarse(dsrw.getAutoCoarse());
			kc.execute();
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getNewValue().equals("finished")) {
			if (answer == JOptionPane.CANCEL_OPTION)
				if (mergePW == null)
					mergePW = kc.getPw();
				else {
					w.removeTab(false);
					mergePW = new MergeGraphs(mergePW, kc.getPw(), false).getPw_new();
				}
			if (it != null && it.hasNext()) {
				String[] details = it.next();
				KEGGConnector kc = new KEGGConnector(details[0], details[2], answer != JOptionPane.YES_OPTION);
				kc.addPropertyChangeListener(this);
				kc.setSearchMicroRNAs(dsrw.getSearchMirnas());
				kc.setAutoCoarse(dsrw.getAutoCoarse());
				kc.execute();
			} else {
				mergePW = GraphInstance.getContainer().getPathway(w.getCurrentPathway());
				MainWindow.getInstance().closeProgressBar();
				w.updateAllGuiElements();
				mergePW.getGraph().getVisualizationViewer().repaint();
				mergePW.getGraph().disableGraphTheory();
				mergePW.getGraph().normalCentering();
			}
		}
	}
	 */
}
