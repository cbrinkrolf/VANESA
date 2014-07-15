package database.mirna.gui;

import gui.eventhandlers.TextfeldColorChanger;
import gui.images.ImagePath;

import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import database.eventhandlers.DatabaseSearchListener;
import database.gui.DatabaseWindow;

import net.infonode.tabbedpanel.titledtab.TitledTab;
import net.miginfocom.swing.MigLayout;

public class MirnaQueryClass {
	
	private TitledTab tab;
	private JPanel p;
	private JTextField name, acc, sequences, gene;
	
	
	public MirnaQueryClass(DatabaseWindow dw) {
		
		ImagePath imagePath = ImagePath.getInstance();
		MigLayout layout = new MigLayout("", "[right]");
		p = new JPanel(layout);
		
		name = new JTextField(20);
		gene = new JTextField(20);
		acc = new JTextField(20);
		sequences = new JTextField(20);
		
		
		name.setText("hsa-miR-15a");
		gene.setText("");
		acc.setText("");
		sequences.setText("");
		
		name.addFocusListener(new TextfeldColorChanger());
		gene.addFocusListener(new TextfeldColorChanger());
		acc.addFocusListener(new TextfeldColorChanger());
		sequences.addFocusListener(new TextfeldColorChanger());
		
		JButton search = new JButton("search");
		search.setActionCommand("searchDatabase");
		search.addActionListener(new DatabaseSearchListener(dw));
		
		JButton info = new JButton(new ImageIcon(imagePath.getPath("infoButton.png")));
		info.setActionCommand("MIRNAinfo");
		info.addActionListener(new DatabaseSearchListener(dw));
		info.setBorderPainted(false);
		

		JButton reset = new JButton("reset");
		reset.setActionCommand("reset");
		reset.addActionListener(new DatabaseSearchListener(dw));
		
		
		p.add(new JLabel("miRNA Search Window"),"span 4");
		p.add(new JSeparator(),       "span, growx, wrap 15, gaptop 10, gap 5");
		
		p.add(new JLabel(new ImageIcon(imagePath.getPath("dataServer.png"))),"span 2 5");
		
		p.add(new JLabel("miRNA name"), "span 2, gap 5 ");
	    p.add(name,"span,wrap,growx ,gap 10");
	    p.add(new JLabel("Gene name"), "span 2, gap 5 ");
	    p.add(gene,"span,wrap,growx ,gap 10");
	    p.add(new JLabel("Accession"),"span 2, gap 5 ");
		p.add(acc,"span, wrap, growx, gap 10");
		p.add(new JLabel("Sequence"),"span 2, gap 5 ");
		p.add(sequences,"span, wrap, growx, gap 10");
		//p.add(new JSeparator(), "span, growx, wrap 10 ");
		p.add(new JLabel(),"gap 20, span 5");
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(reset);
		buttonPanel.add(search);
		
		p.add(buttonPanel,"span");
			
		tab = new TitledTab("miRNA", null, p, info);
		tab.getProperties().setHighlightedRaised(2);
		tab.getProperties().getHighlightedProperties().getComponentProperties().setBackgroundColor(Color.WHITE);
		tab.getProperties().getNormalProperties().getComponentProperties().setBackgroundColor(Color.LIGHT_GRAY);
		
	}

    public void reset(){
    	name.setText("");
    	gene.setText("");
		acc.setText("");
		sequences.setText(""); 	
	}
	
	
	public TitledTab getTitelTab() {
		return tab;
	}
	
	public String[] getKeyword(){
		
		String[] input = new String[5];
		input[0]=name.getText();
		input[1]=acc.getText();
		input[2]=sequences.getText();
		input[3] = gene.getText();
		return input;
	}
	
	public boolean doSearchCriteriaExist(){
		
		if(name.getText().length()>0 || acc.getText().length()>0 || sequences.getText().length()>0 || gene.getText().length()>0){
			return true;
		}else{
			return false;
		}
	}
	
	public JPanel getPanel(){
		return p;
	}


	
	
}
