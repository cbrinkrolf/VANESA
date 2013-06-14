package graph.gui;


import graph.GraphInstance;
import gui.eventhandlers.PropertyWindowListener;

import javax.swing.JPanel;
import javax.swing.JTextArea;

import net.miginfocom.swing.MigLayout;
import biologicalElements.GraphElementAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class CommentWindow {

	JPanel p = new JPanel();
	BiologicalNodeAbstract ab;
	GraphElementAbstract gea;
	boolean emptyPane = true;
	GraphInstance graphInstance;
	
	public CommentWindow(){
		
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
			
		 gea = (GraphElementAbstract)graphInstance.getPathwayElement(element);
		 PropertyWindowListener pwl = new PropertyWindowListener(element);
		 JTextArea comment = new JTextArea(20,5);
		 
		 comment.setText(gea.getComments());
		 comment.setName("comment");
		 comment.addFocusListener(pwl);
		 
		 MigLayout layout = new MigLayout("fillx", "[grow,fill]", "[]5[fill]");
		 
		 p.setLayout(layout);
		 p.add(comment, "");
	}
	
}


