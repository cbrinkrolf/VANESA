package database.kegg;

import biologicalElements.Pathway;
import biologicalObjects.edges.Compound;
import biologicalObjects.nodes.*;
import configurations.Wrapper;
import database.Connection.DatabaseQueryValidator;
import database.kegg.gui.KEGGResultWindow;
import graph.CreatePathway;
import graph.GraphInstance;
import graph.algorithms.MergeGraphs;
import gui.MainWindow;
import org.apache.commons.lang3.StringUtils;
import pojos.DBColumn;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class KeggSearch extends SwingWorker<Object, Object> implements PropertyChangeListener {
	private final String pathway;
	private final String enzyme;
	private final String gene;
	private final String compound;
	private final String organism;
	private final int SEPARATE_TABS = JOptionPane.YES_OPTION;
	private Iterator<String[]> it;
	private int answer = SEPARATE_TABS;
	private KEGGConnector kc;
	private final DatabaseQueryValidator dqv = new DatabaseQueryValidator();
	private ArrayList<DBColumn> results = new ArrayList<>();
	private boolean continueProgress = false;
	private KEGGResultWindow dsrw = null;
	private final MainWindow w = MainWindow.getInstance();
	private Pathway mergePW;

	public KeggSearch(String pathway, String organism, String enzyme, String gene, String compound, Pathway mergePW) {
		this.pathway = pathway;
		this.organism = organism;
		this.enzyme = enzyme;
		this.gene = gene;
		this.compound = compound;
		this.mergePW = mergePW;
	}

	public ArrayList<DBColumn> requestDbContent() {
		String queryStart = "Select pathway.pathway_name from ";
		String queryEnd = "";
		boolean firstEntries = false;
		if (!pathway.equals("")) {
			queryStart += "(" + KEGGQueries.KEGGPathwayQuery + dqv.prepareString(pathway, "p.pathway_name", "p.title")
					+ ") as pathway";
			firstEntries = true;
		}
		boolean organism = StringUtils.isNotEmpty(this.organism);
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
		if (!organism && !firstEntries) {
			return new ArrayList<>();
		} else if (!organism && tempResults.size() == 0) {
			return new ArrayList<>();
		} else if (organism && firstEntries && tempResults.size() == 0) {
			return new ArrayList<>();
		} else if (!organism && tempResults.size() > 0) {
			lastQuery = "Select p.pathway_name, p.title, t.name from kegg_pathway p "
					+ "LEFT OUTER JOIN kegg_taxonomy as t on p.org=t.org where p.pathway_name In " + pathway_names;
		} else if (organism && pathway_names.length() < 4) {
			lastQuery = "Select p.pathway_name, p.title, t.name from kegg_pathway p "
					+ "LEFT OUTER JOIN kegg_taxonomy as t on p.org=t.org where "
					+ dqv.prepareString(this.organism, "t.name", "t.latin_name");
		} else {
			lastQuery = "Select p.pathway_name, p.title, t.name from kegg_pathway p "
					+ "LEFT OUTER JOIN kegg_taxonomy as t on p.org=t.org where p.pathway_name In " + pathway_names
					+ " AND " + dqv.prepareString(this.organism, "t.name", "t.latin_name");
		}
		return new Wrapper().requestDbContent(lastQuery + " LIMIT 0,1000;");
	}

	@Override
	protected Void doInBackground() {
		results = KEGGQueries.requestDbContent(pathway, organism, gene, compound, enzyme);
		return null;
	}

	@Override
	public void done() {
		MainWindow.getInstance().closeProgressBar();
		if (results.size() > 0) {
			continueProgress = true;
			dsrw = new KEGGResultWindow(results);
		} else {
			JOptionPane.showMessageDialog(w.getFrame(), "Sorry, no entries have been found.");
		}
		if (continueProgress) {
			List<String[]> results = dsrw.getAnswer();
			if (results == null)
				return;
			if (results.size() != 0) {
				if (results.size() > 1 || mergePW != null)
					answer = JOptionPane.showOptionDialog(w.getFrame(),
							"Shall the selected Pathways be loaded each into a separate tab, be combined into an overview Pathway or merged?",
							"Several Pathways selected...", JOptionPane.YES_NO_CANCEL_OPTION,
							JOptionPane.QUESTION_MESSAGE, null,
							new String[] { "Separate Tabs", "Overview Pathway", "Merge Pathways" }, SEPARATE_TABS);
				it = results.iterator();
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
					kc = new KEGGConnector(details[0], details[2], mergePW != null);
					kc.addPropertyChangeListener(this);
					kc.setSearchMicroRNAs(dsrw.getCheckBox().isSelected());
					kc.setAutoCoarse(dsrw.getAutoCoarse());
					kc.execute();
				}
			}
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
				kc = new KEGGConnector(details[0], details[2], !(answer == SEPARATE_TABS));
				kc.addPropertyChangeListener(this);
				kc.setSearchMicroRNAs(dsrw.getCheckBox().isSelected());
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
}
