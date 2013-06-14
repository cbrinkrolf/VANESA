package database.dawis.gui;

import graph.GraphInstance;

import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.JPanel;
import javax.swing.JSeparator;

import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.hyperlink.LinkAction;

import miscalleanous.internet.FollowLink;
import miscalleanous.tables.NodePropertyTable;

import net.infonode.tabbedpanel.titledtab.TitledTab;
import net.miginfocom.swing.MigLayout;

import biologicalObjects.edges.ReactionPair;
import biologicalObjects.edges.ReactionPairEdge;


/**
 * 
 * @author Olga
 * 
 */

// builds up the window to show features of a domain
public class DAWISRPairEdgeWindow {

	ReactionPair ab;
	TitledTab tab;
	GraphInstance graphInstance;

	JPanel p = new JPanel();
	boolean emptyPane = true;

	// empty constructor
	public DAWISRPairEdgeWindow() {

	}

	// gets panel
	public JPanel getPanel() {
		p.setVisible(false);
		return p;
	}

	// removes all elements of the window
	public void removeAllElements() {
		emptyPane = true;
		p.removeAll();
		p.setVisible(false);
	}

	// removes old data and updates the window
	public void revalidateView() {

		graphInstance = new GraphInstance();

		if (emptyPane) {
			updateWindow(graphInstance.getSelectedObject());
			p.setVisible(true);
			p.repaint();
			p.revalidate();
			emptyPane = false;
		} else {
			p.removeAll();
			updateWindow(graphInstance.getSelectedObject());
			p.setVisible(true);
			p.repaint();
			p.revalidate();
		}

	}

	// prepares data of the chosen object to show them
	@SuppressWarnings({ "serial", "unchecked" })
	private void updateWindow(Object element) {

		 String[] header = {"Attribute","Value"};
		Object[][] values = null;

		this.ab = (ReactionPair) graphInstance
				.getPathwayElement(element);

		final String link = "http://www.genome.jp/dbget-bin/www_bget?rpair"+ab.getLabel();

		LinkAction linkAction = new LinkAction("Original Database Link") {
				public void actionPerformed(ActionEvent e) {
					setVisited(true);
					FollowLink.openURL(link);
				}
			};

			JXHyperlink hyperlink = new JXHyperlink(linkAction);

			final String link2 = "http://agbi.techfak.uni-bielefeld.de/DAWISMD/jsp/detail/reactionpair_detail.jsp?param0="+ab.getLabel();
			LinkAction linkAction2 = new LinkAction("DAWIS-M.D. Link") {
				public void actionPerformed(ActionEvent e) {
					setVisited(true);
					FollowLink.openURL(link2);
				}
			};

			JXHyperlink  hyperlink2 = new JXHyperlink(linkAction2);

		
			 ReactionPairEdge edge = ab.getReactionPairEdge();
			 values = edge.getRPairDetails();
	
		 
		NodePropertyTable table = new NodePropertyTable(values, header);

		MigLayout layout = new MigLayout("fillx", "[right]rel[grow,fill]", "");
		JPanel headerPanel = new JPanel(layout);
		headerPanel.setBackground(new Color(192, 215, 227));
		headerPanel.add(hyperlink, "");
		headerPanel.add(hyperlink2, "dock east");
//		headerPanel.add(new JLabel(ab.getBiologicalElement()), "");
		headerPanel.add(new JSeparator(), "gap 10, wrap");
		MigLayout layout2 = new MigLayout("fillx", "[grow,fill]", "[]5[fill]");

		p.setLayout(layout2);
		p.add(headerPanel, "wrap");
		p.add(table.getTable(), "");

	}

}
