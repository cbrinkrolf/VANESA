package database.kegg.gui;


import graph.GraphInstance;

import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JSeparator;

import miscalleanous.tables.NodePropertyTable;
import net.miginfocom.swing.MigLayout;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.KEGGNode;

public class KEGGVertexWindow {

	JPanel p = new JPanel();
	BiologicalNodeAbstract ab;
	boolean emptyPane = true;
	GraphInstance graphInstance;
	
	public KEGGVertexWindow(){
		
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
			
		 this.ab = (BiologicalNodeAbstract)graphInstance.getPathwayElement(element);
		 KEGGNode node = ab.getKEGGnode();
		 
		// final String link = node.getKEGGentryLink();
		// LinkAction linkAction = new LinkAction("KEGG Internet Link") {
	      //      public void actionPerformed(ActionEvent e) {
	        //        setVisited(true); 
	          //      FollowLink.openURL(link);
	           // }
	     // };
	      
	    // JXHyperlink hyperlink = new JXHyperlink(linkAction);
		 String[] header = {"Attribute","Value"};
		  
		 Object[][] values = node.getKeggDeatails();
		 NodePropertyTable table = new NodePropertyTable(values,header); 
		 MigLayout layout = new MigLayout("fillx", "[right]rel[grow,fill]", "");
		 JPanel headerPanel = new JPanel(layout);
		 headerPanel.setBackground(new Color(192,215,227)); 
		 //headerPanel.add(hyperlink,   "");
		 headerPanel.add(new JSeparator(),          "gap 10, wrap");
		 MigLayout layout2 = new MigLayout("fillx", "[grow,fill]", "[]5[fill]");
		 
		 p.setLayout(layout2);
		 p.add(headerPanel, "wrap");
		 p.add(table.getTable(), "");
	}
	
}


