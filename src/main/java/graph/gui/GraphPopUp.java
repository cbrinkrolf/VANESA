package graph.gui;

import api.payloads.dbBrenda.DBBrendaReaction;
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.PathwayMap;
import configurations.SettingsManager;
import configurations.gui.LayoutConfig;
import copy.CopySelection;
import copy.CopySelectionSingleton;
import database.brenda.BRENDASearch;
import database.brenda.BrendaConnector;
import database.brenda.gui.BrendaSearchResultWindow;
import database.kegg.KEGGConnector;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.algorithms.layout.*;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import graph.GraphContainer;
import graph.GraphInstance;
import graph.jung.classes.MyVisualizationViewer;
import graph.layouts.gemLayout.GEMLayout;
import gui.AsyncTaskExecutor;
import gui.MainWindow;
import gui.PopUpDialog;
import io.SaveDialog;
import io.SuffixAwareFilter;
import io.image.ComponentImageWriter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

public class GraphPopUp {
	private final JPopupMenu popup = new JPopupMenu();

	public GraphPopUp() {
		JLabel title = new JLabel("Graph Layouts");
		JPanel titlePanel = new JPanel();
		titlePanel.setBackground(new Color(200, 200, 250));
		titlePanel.add(title);
		JMenu openPathwayMenu = new JMenu("Open Pathway");
		addMenuItem(openPathwayMenu, "Open Pathway as Sub-Pathway", this::onOpenPathwayClicked);
		addMenuItem(openPathwayMenu, "Open Pathway in new Tab", this::onOpenPathwayTabClicked);
		popup.add(openPathwayMenu);
		addMenuItem(popup, "Return to Parent-Pathway", this::onReturnToParentClicked);
		popup.add(new JSeparator());
		addMenuItem(popup, "Save picture", this::onSavePictureClicked);
		popup.add(new JSeparator());
		addMenuItem(popup, "copy", this::onCopyClicked);
		addMenuItem(popup, "cut", this::onCutClicked);
		addMenuItem(popup, "paste", this::onPasteClicked);
		addMenuItem(popup, "delete", this::onDeleteClicked);
		// popup.add(new JSeparator());
		// addLayoutMenu();
		popup.add(new JSeparator());
		addMenuItem(popup, "KEGG Search", this::onKEGGSearchClicked);
		addMenuItem(popup, "BRENDA Search", this::onBRENDASearchClicked);
		popup.add(new JSeparator());
		popup.add(new JMenuItem("cancel"));
	}

	private void addMenuItem(JPopupMenu parent, String label, Runnable listener) {
		addMenuItem(parent, label, e -> listener.run());
	}

	private void addMenuItem(JPopupMenu parent, String label, ActionListener listener) {
		JMenuItem item = new JMenuItem(label);
		item.addActionListener(listener);
		parent.add(item);
	}

	private void addMenuItem(JMenu parent, String label, Runnable listener) {
		JMenuItem item = new JMenuItem(label);
		item.addActionListener(e -> listener.run());
		parent.add(item);
	}

	private void addLayoutMenu() {
		JMenu layoutMenu = new JMenu("Graph Layouts");
		addMenuItem(layoutMenu, "Center Graph", () -> GraphInstance.getPathway().getGraph().animatedCentering());
		addMenuItem(layoutMenu, "Spring Layout", () -> LayoutConfig.changeToLayout(SpringLayout.class));
		addMenuItem(layoutMenu, "KK Layout", () -> LayoutConfig.changeToLayout(KKLayout.class));
		addMenuItem(layoutMenu, "FR Layout", () -> LayoutConfig.changeToLayout(FRLayout.class));
		addMenuItem(layoutMenu, "Circle Layout", () -> LayoutConfig.changeToLayout(CircleLayout.class));
		addMenuItem(layoutMenu, "GEM Layout", () -> LayoutConfig.changeToLayout(GEMLayout.class));
		addMenuItem(layoutMenu, "ISOM Layout", () -> LayoutConfig.changeToLayout(ISOMLayout.class));
		// addMenuItem(layoutMenu, "MD Layout", () ->
		// LayoutConfig.changeToLayout(MDForceLayout.class));
		popup.add(layoutMenu);
	}

	public JPopupMenu getPopUp() {
		return popup;
	}

	private void onCopyClicked() {
		if (GraphContainer.getInstance().containsPathway()) {
			Pathway pw = GraphInstance.getPathway();
			VisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv = pw.getGraph()
					.getVisualizationViewer();
			Set<BiologicalNodeAbstract> vertices = new HashSet<>(vv.getPickedVertexState().getPicked());
			Set<BiologicalEdgeAbstract> edges = new HashSet<>(vv.getPickedEdgeState().getPicked());
			CopySelectionSingleton.setInstance(new CopySelection(vertices, edges));
		}
	}

	private void onCutClicked() {
		if (GraphContainer.getInstance().containsPathway()) {
			Pathway pw = GraphInstance.getPathway();
			VisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv = pw.getGraph()
					.getVisualizationViewer();
			Set<BiologicalNodeAbstract> vertices = new HashSet<>(vv.getPickedVertexState().getPicked());
			Set<BiologicalEdgeAbstract> edges = new HashSet<>(vv.getPickedEdgeState().getPicked());
			CopySelectionSingleton.setInstance(new CopySelection(vertices, edges));
			pw.removeSelection();
			MainWindow w = MainWindow.getInstance();
			w.updateElementTree();
			w.updatePathwayTree();
			w.updateTheoryProperties();
		}
	}

	private void onPasteClicked() {
		if (GraphContainer.getInstance().containsPathway()) {
			Pathway pw = GraphInstance.getPathway();
			CopySelectionSingleton.getInstance().paste();
			pw.getGraph().restartVisualizationModel();
			pw.getGraph().getVisualizationViewer().repaint();
		}
	}

	private void onDeleteClicked() {
		if (GraphContainer.getInstance().containsPathway()) {
			GraphInstance.getPathway().removeSelection();
			MainWindow w = MainWindow.getInstance();
			w.updateElementTree();
			w.updateTheoryProperties();
		}
	}

	private void onSavePictureClicked(ActionEvent e) {
		JMenuItem item = (JMenuItem) e.getSource();
		JPopupMenu popup = (JPopupMenu) item.getParent();
		@SuppressWarnings("unchecked")
		MyVisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv = (MyVisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract>) popup
				.getInvoker();
		Pathway vvPw = vv.getPathway();// graphInstance.getPathway();
		VisualizationImageServer<BiologicalNodeAbstract, BiologicalEdgeAbstract> wvv = vvPw.prepareGraphToPrint();
		if (GraphContainer.getInstance().containsPathway()) {
			if (vvPw.hasGotAtLeastOneElement()) {
				SuffixAwareFilter[] filters;
				if (SettingsManager.getInstance().getDefaultImageExportFormat()
						.equals(ComponentImageWriter.IMAGE_TYPE_SVG)) {
					filters = new SuffixAwareFilter[] { SuffixAwareFilter.SVG, SuffixAwareFilter.PNG,
							SuffixAwareFilter.PDF };
				} else if (SettingsManager.getInstance().getDefaultImageExportFormat()
						.equals(ComponentImageWriter.IMAGE_TYPE_PDF)) {
					filters = new SuffixAwareFilter[] { SuffixAwareFilter.PDF, SuffixAwareFilter.PNG,
							SuffixAwareFilter.SVG };
				} else {
					filters = new SuffixAwareFilter[] { SuffixAwareFilter.PNG, SuffixAwareFilter.SVG,
							SuffixAwareFilter.PDF };
				}

				new SaveDialog(filters, SaveDialog.DATA_TYPE_GRAPH_PICTURE, wvv);
			} else {
				PopUpDialog.getInstance().show("Error", "Please create a network first.");
			}
		} else {
			PopUpDialog.getInstance().show("Error", "Please create a network first.");
		}
	}

	private void onOpenPathwayClicked() {
		Pathway pw = GraphInstance.getPathway();
		if (pw != null) {
			MainWindow w = MainWindow.getInstance();
			String pwName = w.getCurrentPathway();
			for (BiologicalNodeAbstract bna : pw.getSelectedNodes()) {
				if (bna instanceof PathwayMap) {
					PathwayMap map = (PathwayMap) bna;
					Pathway pwLink = map.getPathwayLink();
					if (pwLink == null) {
						KEGGConnector kc = new KEGGConnector(map.getName(), "", true);
						// kc.setSearchMicroRNAs(JOptionPane.showConfirmDialog(w.getFrame(),
						// "Search also after possibly connected microRNAs in mirBase/tarBase?",
						// "Search parameters...", JOptionPane.YES_NO_OPTION) ==
						// JOptionPane.YES_OPTION);
						kc.addPropertyChangeListener((evt) -> {
							if (evt.getNewValue().equals("finished")) {
								Pathway newPW = kc.getPw();
								newPW.setParent(pw);
								w.removeTab(false);
								w.setCursor(Cursor.WAIT_CURSOR);
								GraphContainer con = GraphContainer.getInstance();
								String newPathwayName = con.addPathway(pwName, newPW);
								newPW = con.getPathway(newPathwayName);
								w.addTab(newPW.getTab());
								w.setCursor(Cursor.DEFAULT_CURSOR);
								map.setPathwayLink(newPW);
								map.setColor(Color.BLUE);
								w.updateAllGuiElements();
							}
						});
						kc.execute();
						MainWindow.getInstance().showProgressBar("KEGG query");
					} else {
						w.removeTab(false);
						w.setCursor(Cursor.WAIT_CURSOR);
						GraphContainer con = GraphContainer.getInstance();
						String newPathwayName = con.addPathway(pwName, pwLink);
						pwLink = con.getPathway(newPathwayName);
						w.addTab(pwLink.getTab());
						w.setCursor(Cursor.DEFAULT_CURSOR);
					}
					w.updateAllGuiElements();
					return;
				}
			}
		}
	}

	private void onOpenPathwayTabClicked() {
		Pathway pw = GraphInstance.getPathway();
		if (pw != null) {
			for (BiologicalNodeAbstract bna : pw.getSelectedNodes()) {
				if (bna instanceof PathwayMap) {
					PathwayMap map = (PathwayMap) bna;
					// Pathway newPW = map.getPathwayLink();
					KEGGConnector kc = new KEGGConnector(map.getName(), "", false);
					// kc.setSearchMicroRNAs(JOptionPane.showConfirmDialog(MainWindow.getInstance().getFrame(),
					// "Search also after possibly connected microRNAs in mirBase/tarBase?",
					// "Search paramaters...", JOptionPane.YES_NO_OPTION) ==
					// JOptionPane.YES_OPTION);
					kc.execute();
					MainWindow.getInstance().showProgressBar("KEGG query");
				}
			}
		}
	}

	private void onReturnToParentClicked() {
		Pathway pw = GraphInstance.getPathway();
		if (pw != null && pw.getParent() != null) {
			MainWindow w = MainWindow.getInstance();
			String pwName = w.getCurrentPathway();
			w.removeTab(false);
			w.setCursor(Cursor.WAIT_CURSOR);
			GraphContainer con = GraphContainer.getInstance();
			String newPathwayName = con.addPathway(pwName, pw.getParent());
			Pathway newPW = con.getPathway(newPathwayName);
			w.addTab(newPW.getTab());
			w.setCursor(Cursor.DEFAULT_CURSOR);
			w.updateAllGuiElements();
		}
	}

	private void onKEGGSearchClicked() {
		/*
		 * TODO: KEGG Pathway pw = GraphInstance.getPathway(); if (pw != null) {
		 * MainWindow w = MainWindow.getInstance(); Set<BiologicalNodeAbstract> vertices
		 * = pw.getSelectedNodes(); if (pw.getSelectedNodes().isEmpty()) {
		 * JOptionPane.showMessageDialog(w.getFrame(),
		 * "Please select a node to search after it in a database!",
		 * "Operation not possible...", JOptionPane.ERROR_MESSAGE); return; }
		 * BiologicalNodeAbstract bna = vertices.iterator().next(); if (vertices.size()
		 * > 1) { String[] possibilities = new String[vertices.size()]; int i = 0; for
		 * (BiologicalNodeAbstract vertex : vertices) { possibilities[i++] =
		 * vertex.getLabel(); } String answer = (String)
		 * JOptionPane.showInputDialog(w.getFrame(),
		 * "Choose one of the selected nodes, which shall be searched in a database",
		 * "Select a node...", JOptionPane.QUESTION_MESSAGE, null, possibilities,
		 * possibilities[0]); if (answer == null) return; bna =
		 * pw.getNodeByLabel(answer); } KeggSearch keggSearch; switch
		 * (bna.getBiologicalElement()) { case
		 * biologicalElements.Elementdeclerations.enzyme: keggSearch = new
		 * KeggSearch(null, null, bna.getLabel(), null, null, pw); break; case
		 * biologicalElements.Elementdeclerations.gene: keggSearch = new
		 * KeggSearch(null, null, null, bna.getLabel(), null, pw); break; case
		 * biologicalElements.Elementdeclerations.pathwayMap: keggSearch = new
		 * KeggSearch(bna.getLabel(), null, null, null, null, pw); break; default:
		 * keggSearch = new KeggSearch(null, null, null, null, bna.getLabel(), pw);
		 * break; } keggSearch.execute();
		 * MainWindow.getInstance().showProgressBar("KEGG query"); }
		 */
	}

	private void onBRENDASearchClicked() {
		Pathway pw = GraphInstance.getPathway();
		if (pw != null) {
			MainWindow w = MainWindow.getInstance();
			Set<BiologicalNodeAbstract> vertices = pw.getSelectedNodes();
			if (pw.getSelectedNodes().isEmpty()) {
				JOptionPane.showMessageDialog(w.getFrame(), "Please select a node to search after it in a database!",
						"Operation not possible...", JOptionPane.ERROR_MESSAGE);
				return;
			}
			BiologicalNodeAbstract bna = vertices.iterator().next();
			if (vertices.size() > 1) {
				String[] possibilities = new String[vertices.size()];
				int i = 0;
				for (BiologicalNodeAbstract vertex : vertices) {
					possibilities[i++] = vertex.getLabel();
				}
				String answer = (String) JOptionPane.showInputDialog(w.getFrame(),
						"Choose one of the selected nodes, which shall be searched in a database", "Select a node...",
						JOptionPane.QUESTION_MESSAGE, null, possibilities, possibilities[0]);
				if (answer == null)
					return;
				bna = pw.getNodeByLabel(answer);
			}
			final BiologicalNodeAbstract selectedBna = bna;
			AsyncTaskExecutor.runUIBlocking("BRENDA Query", () -> {
				String ecNumber = null;
				String metabolite = null;
				if (selectedBna.getBiologicalElement().equals(biologicalElements.Elementdeclerations.enzyme)) {
					ecNumber = selectedBna.getLabel();
				} else {
					metabolite = selectedBna.getLabel();
				}
				MainWindow.getInstance().closeProgressBar();
				DBBrendaReaction[] results = BRENDASearch.searchReactions(ecNumber, null, metabolite, null, null);
				if (results != null && results.length > 0) {
					BrendaSearchResultWindow searchResultWindow = new BrendaSearchResultWindow(results);
					if (!searchResultWindow.show()) {
						return;
					}
					DBBrendaReaction[] selectedResults = searchResultWindow.getSelectedValues();
					if (selectedResults.length != 0) {
						MainWindow.getInstance().showProgressBar("Fetching Network");
						for (DBBrendaReaction res : selectedResults) {
							BrendaConnector bc = new BrendaConnector(res, pw, searchResultWindow.getAutoCoarseDepth(),
									searchResultWindow.getAutoCoarseEnzymeNomenclature(),
									searchResultWindow.getCoFactorsDecision(),
									searchResultWindow.getInhibitorsDecision(), searchResultWindow.getSearchDepth(),
									searchResultWindow.getDisregarded(),
									searchResultWindow.getOrganismSpecificDecision());
							bc.search();
						}
					}
				} else {
					JOptionPane.showMessageDialog(MainWindow.getInstance().getFrame(),
							"Sorry, no entries have been found.");
				}
			});
		}
	}
}
