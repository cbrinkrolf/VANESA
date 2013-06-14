package database.kegg.gui;


import graph.GraphInstance;
import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import miscalleanous.internet.FollowLink;
import miscalleanous.tables.NodePropertyTable;
import net.infonode.tabbedpanel.titledtab.TitledTab;
import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.hyperlink.LinkAction;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.edges.KEGGEdge;

public class KEGGEdgeWindow {

	JPanel p = new JPanel();
	BiologicalEdgeAbstract ab;
	TitledTab tab;
	boolean emptyPane = true;
	GraphInstance graphInstance;
	
	public KEGGEdgeWindow(){
			
	}
	
	public JPanel getPanel() {
		p.setVisible(false);
		return p;	
	}

	public void removeAllElements(){
		emptyPane=true;
		p.removeAll();
		p.setVisible(false);
	}
	
	public void revalidateView(){
		
		graphInstance = new GraphInstance();
			
		if(emptyPane){	
			updateWindow(graphInstance.getSelectedObject());
			p.setVisible(true);
			p.repaint();
			p.revalidate();
			emptyPane=false;
		}else{
			p.removeAll();
			updateWindow(graphInstance.getSelectedObject());
			p.setVisible(true);
			p.repaint();
			p.revalidate();
		}
	}	
	
	
	private void updateWindow(Object element){
		
		 String[] header = {"Attribute","Value"};
		 Object[][] values = null;
		 this.ab = (BiologicalEdgeAbstract)graphInstance.getPathwayElement(element);
		 
		 
		 final String link = "http://www.genome.jp/dbget-bin/www_bget?rn+" + ab.getLabel();
			
				LinkAction linkAction = new LinkAction("Original Database Link") {
					public void actionPerformed(ActionEvent e) {
						setVisited(true);
						FollowLink.openURL(link);
					}
				};

				JXHyperlink hyperlink = new JXHyperlink(linkAction);

				final String link2 = "http://agbi.techfak.uni-bielefeld.de/DAWISMD/jsp/detail/reaction_detail.jsp?param0=" + ab.getLabel();
				LinkAction linkAction2 = new LinkAction("DAWIS-M.D. Link") {
					public void actionPerformed(ActionEvent e) {
						setVisited(true);
						FollowLink.openURL(link2);
					}
				};

				JXHyperlink hyperlink2 = new JXHyperlink(linkAction2);
	
			
		 if (ab.hasKEGGEdge()){
			 KEGGEdge edge = ab.getKeggEdge();
			 values = edge.getKeggDeatails();
		 } 
		 
		 NodePropertyTable table = new NodePropertyTable(values,header); 
		 
		 MigLayout layout = new MigLayout("fillx", "[right]rel[grow,fill]", "");
		 JPanel headerPanel = new JPanel(layout);
		 headerPanel.setBackground(new Color(192,215,227)); 
		 if (graphInstance.getPathway().isDAWISProject()){
			 headerPanel.add(hyperlink, "");
			 headerPanel.add(hyperlink2, "dock east");
		 } else {
			 headerPanel.add(new JLabel(ab.getBiologicalElement()),   "");
			 headerPanel.add(hyperlink, "dock east");
		 }

		 headerPanel.add(new JSeparator(),          "gap 10, wrap");
		 MigLayout layout2 = new MigLayout("fillx", "[grow,fill]", "[]5[fill]");
		 
		 p.setLayout(layout2);
		 p.add(headerPanel, "wrap");
		 p.add(table.getTable(), "");
	 
	}
}
