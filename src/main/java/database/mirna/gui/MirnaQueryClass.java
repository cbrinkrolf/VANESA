package database.mirna.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import database.eventhandlers.DatabaseSearchListener;
import database.gui.DatabaseWindow;
import database.gui.QueryMask;
import database.mirna.MirnaStatistics;
import graph.GraphInstance;
import gui.eventhandlers.TextfeldColorChanger;
import gui.images.ImagePath;
import net.miginfocom.swing.MigLayout;

public class MirnaQueryClass extends QueryMask implements ActionListener{
	
	private JPanel p;
	private JTextField name, acc, sequences, gene;
	private JCheckBox hsaOnly;
	private ButtonGroup typeGroup;
	private JRadioButton sources;
	private JRadioButton targets;
	private JRadioButton sourcesAndTargets;
	private JButton enrichGenes;
	private JButton enrichMirnas;
	private JCheckBox mirnaNew;
	
	
	public MirnaQueryClass(DatabaseWindow dw) {
		super(dw);
		
		ImagePath imagePath = ImagePath.getInstance();
		MigLayout layout = new MigLayout("", "[right]");
		p = new JPanel(layout);
		
		name = new JTextField(20);
		gene = new JTextField(20);
		acc = new JTextField(20);
		sequences = new JTextField(20);
		hsaOnly = new JCheckBox("human only");
		hsaOnly.setSelected(true);
		
		mirnaNew = new JCheckBox("new Data");
		mirnaNew.setToolTipText("new and experimental data (2022)");
		
		typeGroup = new ButtonGroup();
		sources = new JRadioButton("sources");
		targets = new JRadioButton("targets");
		sourcesAndTargets = new JRadioButton("both");
		sourcesAndTargets.setSelected(true);
		
		typeGroup.add(sources);
		typeGroup.add(targets);
		typeGroup.add(sourcesAndTargets);
		
		enrichGenes = new JButton("enrich genes");
		enrichGenes.setActionCommand("enrichGenes");
		enrichGenes.addActionListener(this);
		
		enrichMirnas = new JButton("enrich miRNAs");
		enrichMirnas.setActionCommand("enrichMirnas");
		enrichMirnas.addActionListener(this);
		
		
		name.setText("hsa-miR-15a");
		gene.setText("");
		acc.setText("");
		sequences.setText("");
		
		name.addFocusListener(new TextfeldColorChanger());
		gene.addFocusListener(new TextfeldColorChanger());
		acc.addFocusListener(new TextfeldColorChanger());
		sequences.addFocusListener(new TextfeldColorChanger());
		
		JButton info = new JButton(new ImageIcon(imagePath.getPath("infoButton.png")));
		info.setActionCommand("MIRNAinfo");
		info.addActionListener(new DatabaseSearchListener(dw));
		info.setBorderPainted(false);
		
		p.add(new JLabel("miRNA Search Window"),"span 4");
		p.add(new JSeparator(),       "span, growx, wrap 15, gaptop 10, gap 5");
		
		p.add(new JLabel(new ImageIcon(imagePath.getPath("dataServer.png"))),"span 2 5");
		
		p.add(new JLabel("miRNA name"), "span 2, gap 5 ");
	    p.add(name,"span,wrap,growx ,gap 10");
	    p.add(new JLabel("Gene name"), "span 2, gap 5 ");
	    p.add(gene,"span,wrap,growx ,gap 10");
	    p.add(new JLabel("Accession"),"span 2, gap 5 ");
		p.add(acc,"span, wrap, growx, gap 10");
		//p.add(new JLabel("Sequence"),"span 2, gap 5 ");
		//p.add(sequences,"span, wrap, growx, gap 10");
		p.add(hsaOnly, "span 2");
		p.add(sources, "flowx, span, split 3");
		p.add(targets);
		p.add(sourcesAndTargets, "wrap");
		p.add(mirnaNew, "span 1");
		p.add(enrichGenes, "flowx, span, split 3");
		p.add(enrichMirnas, "wrap");

		this.addControleButtons(p);
	}

    public void reset(){
    	name.setText("");
    	gene.setText("");
		acc.setText("");
		sequences.setText("");
		hsaOnly.setSelected(true);
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
	
	public boolean isHsaOnly(){
		return this.hsaOnly.isSelected();
	}
	
	public boolean isSourcesSelected(){
		return (sourcesAndTargets.isSelected() || sources.isSelected());
	}
	
	public boolean isTargetsSelected(){
		return (sourcesAndTargets.isSelected() || targets.isSelected());
	}
	
	public boolean isMirnaNew(){
		return this.mirnaNew.isSelected();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("enrichGenes")){
			MirnaStatistics mirna = new MirnaStatistics(new GraphInstance().getPathway());
			mirna.enrichGenes(isSourcesSelected(), isTargetsSelected(), isHsaOnly());
		}else if (e.getActionCommand().equals("enrichMirnas")){
			MirnaStatistics mirna = new MirnaStatistics(new GraphInstance().getPathway());
			mirna.enrichMirnas(isSourcesSelected(), isTargetsSelected(), isHsaOnly());
		}
	}
}
