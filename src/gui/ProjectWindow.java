package gui;

import graph.GraphInstance;
import gui.eventhandlers.ProjectWindowListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import biologicalElements.Pathway;

public class ProjectWindow {

	JPanel p = new JPanel();
	boolean emptyPane = true;
	GraphInstance graphInstance;
	
	public ProjectWindow(){
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
			updateWindow();
			p.setVisible(true);
			p.repaint();
			p.revalidate();
			emptyPane=false;
		}else{
			p.removeAll();
			updateWindow();
			p.setVisible(true);
			p.repaint();
			p.revalidate();
		}
	}	
	
	private void updateWindow(){
		
		Pathway pw = graphInstance.getPathway();
		ProjectWindowListener pwl = new ProjectWindowListener(pw);
		
		JTextField pathway = new JTextField(20);
		JTextField author = new JTextField(20);
		JTextField version = new JTextField(20);
		JTextField date = new JTextField(20);
		JTextField organism = new JTextField(20);
		JTextArea comment = new JTextArea(15,5);
		
		pathway.setText(pw.getTitle());
		author.setText(pw.getAuthor());
		version.setText(pw.getVersion());
		date.setText(pw.getDate());
		organism.setText(pw.getOrganism());
		comment.setText(pw.getDescription());
				
		pathway.setName("pathway");
		author.setName("author");
		version.setName("version");
		date.setName("date");
		organism.setName("organism");
		comment.setName("comment");
		
		pathway.addFocusListener(pwl);
		author.addFocusListener(pwl);
		version.addFocusListener(pwl);
		date.addFocusListener(pwl);
		organism.addFocusListener(pwl);
		comment.addFocusListener(pwl);
		
		
		MigLayout layout = new MigLayout("fillx", "[grow,fill]", "");
		p.setLayout(layout);

		p.add(new JLabel("Pathway"), "gap 5 ");
	    p.add(pathway,"wrap,span 3");
		
	    p.add(new JLabel("Organism"),"gap 5 ");
		p.add(organism,"wrap ,span 3");
	    
	    p.add(new JLabel("Author"),"gap 5 ");
		p.add(author,"wrap ,span 3");
		
		p.add(new JLabel("Version"),"gap 5 ");
		p.add(version,"wrap ,span 3");
		
		p.add(new JLabel("Date"),"gap 5 ");
		p.add(date,"wrap ,span 3");
			
		MigLayout headerlayout = new MigLayout("fillx", "[right]rel[grow,fill]", "");
		JPanel seperatorPanel = new JPanel(headerlayout);
		
		seperatorPanel.add(new JLabel("Description"),"");
		seperatorPanel.add(new JSeparator(),          "gap 10");		
		
		p.add(seperatorPanel,"wrap, span");
		p.add(comment,"span,wrap,growx ,gap 10");
		pwl.updateWindowTab(pw.getTitle());
	//	p.setPreferredSize(new Dimension(100,100));
	}
	
}

