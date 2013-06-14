package database.kegg.gui;

import gui.eventhandlers.TextfeldColorChanger;
import gui.images.ImagePath;

import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import net.infonode.tabbedpanel.titledtab.TitledTab;
import net.miginfocom.swing.MigLayout;
import database.eventhandlers.DatabaseSearchListener;
import database.gui.DatabaseWindow;

public class KEGGqueryMask {
	
	private TitledTab tab;
	JPanel p;
	JTextField pathway, organism, enzyme, gene, compound;
	
	public KEGGqueryMask(DatabaseWindow dw){
		ImagePath imagePath = ImagePath.getInstance();
		MigLayout layout = new MigLayout("", "[right]");
		p = new JPanel(layout);
		
		pathway = new JTextField(20);
		organism = new JTextField(20);
		enzyme = new JTextField(20);
		gene = new JTextField(20);
		compound = new JTextField(20);
		
		pathway.setText("Cell Cycle");
		organism.setText("homo sapiens");
		
		pathway.addFocusListener(new TextfeldColorChanger());
		organism.addFocusListener(new TextfeldColorChanger());
		enzyme.addFocusListener(new TextfeldColorChanger());
		gene.addFocusListener(new TextfeldColorChanger());
		compound.addFocusListener(new TextfeldColorChanger());
		
		JButton search = new JButton("search");
		search.setActionCommand("searchDatabase");
		search.addActionListener(new DatabaseSearchListener(dw));
		
		JButton info = new JButton(new ImageIcon(imagePath.getPath("infoButton.png")));
		info.setActionCommand("KEGGinfo");
		info.addActionListener(new DatabaseSearchListener(dw));
		info.setBorderPainted(false);
		

		JButton reset = new JButton("reset");
		reset.setActionCommand("reset");
		reset.addActionListener(new DatabaseSearchListener(dw));
		
		
		p.add(new JLabel("KEGG Search Window"),"span 4");
		p.add(new JSeparator(),       "span, growx, wrap 15, gaptop 10, gap 5");
		
		p.add(new JLabel(new ImageIcon(imagePath.getPath("dataServer.png"))),"span 2 5");
		
		p.add(new JLabel("Pathway"), "span 2, gap 5 ");
	    p.add(pathway,"span,wrap,growx ,gap 10");
	    p.add(new JLabel("Organism"),"span 2, gap 5 ");
		p.add(organism,"span, wrap, growx, gap 10");
		p.add(new JLabel("Enzyme"),"span 2, gap 5 ");
		p.add(enzyme,"span, wrap, growx, gap 10");
		p.add(new JLabel("Gene"),"span 2, gap 5 ");
		p.add(gene,"span, wrap, growx, gap 10");
		p.add(new JLabel("Compound"),"span 2, gap 5 ");
		p.add(compound,"span, wrap 15, growx, gap 10");
		p.add(new JSeparator(), "span, growx, wrap 10 ");
		p.add(new JLabel(),"gap 20, span 5");
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(reset);
		buttonPanel.add(search);
		
		p.add(buttonPanel,"span");
			
		tab = new TitledTab("KEGG", null, p, info);
		tab.getProperties().setHighlightedRaised(2);
		tab.getProperties().getHighlightedProperties().getComponentProperties().setBackgroundColor(Color.WHITE);
		tab.getProperties().getNormalProperties().getComponentProperties().setBackgroundColor(Color.LIGHT_GRAY);
		
	}

    public void reset(){
		
    	pathway.setText("");
		organism.setText("");
		enzyme.setText("");
		gene.setText("");
		compound.setText("");
    	
	}
	
	
	public TitledTab getTitelTab() {
		return tab;
	}
	
	public String[] getKeyword(){
		
		String[] input = new String[5];
		input[0]=pathway.getText();
		input[1]=organism.getText();
		input[2]=enzyme.getText();
		input[3]=gene.getText();
		input[4]=compound.getText();
		return input;
	}
	
	public boolean doSearchCriteriaExist(){
		
		if(pathway.getText().length()>0 || organism.getText().length()>0 || enzyme.getText().length()>0 || gene.getText().length()>0 || compound.getText().length()>0){
			return true;
		}else{
			return false;
		}
	}
	
	public JPanel getPanel(){
		return p;
	}

}
