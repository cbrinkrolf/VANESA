package graph.gui;

import graph.GraphInstance;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import net.miginfocom.swing.MigLayout;
import biologicalElements.GraphElementAbstract;
import biologicalObjects.edges.ReactionPair;
import database.dawis.gui.DAWISRPairEdgeWindow;
import database.dawis.gui.DAWISVertexWindow;
import database.kegg.gui.KEGGEdgeWindow;
import database.kegg.gui.KEGGVertexWindow;

public class ElementInformationWindow {

	JPanel p = new JPanel();
	GraphElementAbstract ab;
	boolean emptyPane = true;
	GraphInstance graphInstance;
	JTabbedPane pane = new JTabbedPane();
	private CommentWindow comments;
	private KEGGVertexWindow keggWindow;
	private DAWISVertexWindow dawisWindow;
	private KEGGEdgeWindow keggEdgeWindow;
	private DAWISRPairEdgeWindow dawisRPairEdgeWindow;

	public ElementInformationWindow() {
		comments = new CommentWindow();
	}

	public JPanel getPanel() {
		p.setVisible(false);
		return p;
	}

	public void removeAllElements() {
		emptyPane = true;
		p.removeAll();
		p.setVisible(false);
	}

	public void revalidateView() {

		graphInstance = new GraphInstance();
		// boolean isDAWISProject = graphInstance.getPathway().isDAWISProject();

		// if (!isDAWISProject){
		if (emptyPane) {
			if (graphInstance.getSelectedObject() != null) {
				updateWindow(graphInstance.getSelectedObject());
				p.setVisible(true);
				p.repaint();
				p.revalidate();
			}
			emptyPane = false;
		} else {
			p.removeAll();
			if (graphInstance.getSelectedObject() != null) {
				updateWindow(graphInstance.getSelectedObject());
				p.setVisible(true);
				p.repaint();
				p.revalidate();
			}
		}
		// } else {
		// this.ab = (GraphElementAbstract) graphInstance
		// .getPathwayElement(graphInstance.getSelectedObject());
		// if (ab.hasDAWISNode()) {
		// dawisWindow = new DAWISVertexWindow();
		// dawisWindow.revalidateView();
		// pane.addTab("DAWIS Info", dawisWindow.getPanel());
		// p.setVisible(true);
		// p.repaint();
		// p.revalidate();
		// }
		//
		// }

	}

	public void revalidateDAWISVertexWindow() {

		p.removeAll();
		p.setVisible(true);
		p.repaint();
		p.revalidate();

	}

	private void updateWindow(Object element) {

		this.ab = (GraphElementAbstract) graphInstance
				.getPathwayElement(element);
		MigLayout layout = new MigLayout("fillx", "[grow,fill]", "[]5[fill]");
		pane.removeAll();

		if (ab.isVertex()) {
			if (ab.hasKEGGNode()) {
				keggWindow = new KEGGVertexWindow();
				keggWindow.revalidateView();
				pane.addTab("KEGG Info", keggWindow.getPanel());
			}
			if (ab.hasDAWISNode()) {
				dawisWindow = new DAWISVertexWindow();
				dawisWindow.revalidateView();
				pane.addTab("DAWIS Info", dawisWindow.getPanel());
			}
		} else {

			if (ab.hasKEGGEdge()) {
				keggEdgeWindow = new KEGGEdgeWindow();
				keggEdgeWindow.revalidateView();
				if (graphInstance.getPathway().isDAWISProject()) {
					pane.addTab("DAWIS Info", keggEdgeWindow.getPanel());
				} else {
					pane.addTab("KEGG Info", keggEdgeWindow.getPanel());
				}
			} else if (ab instanceof ReactionPair) {
				dawisRPairEdgeWindow = new DAWISRPairEdgeWindow();
				dawisRPairEdgeWindow.revalidateView();
				pane.addTab("Feature Info", dawisRPairEdgeWindow.getPanel());
			}

		}

		comments.revalidateView();
		pane.addTab("Comments", comments.getPanel());

		p.setLayout(layout);
		p.add(pane, "");
	}

}
