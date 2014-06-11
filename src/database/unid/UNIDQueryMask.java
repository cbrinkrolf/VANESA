package database.unid;

import gui.eventhandlers.TextfeldColorChanger;
import gui.images.ImagePath;

import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import net.infonode.tabbedpanel.titledtab.TitledTab;
import net.miginfocom.swing.MigLayout;
import database.eventhandlers.DatabaseSearchListener;
import database.gui.DatabaseWindow;

/**
 * 
 * @author mlewinsk
 * May 2014
 */
public class UNIDQueryMask {
	
	private TitledTab tab;
	private JPanel p;
	
	private JComboBox<String> choosedatabase;
	private String[] dbNames =
	{
			"UNID"
	};
	
	private JTextField fullName, alias, graphID;
	
	private JComboBox<String> chooseOrganism;
	private String[] orgNames =
	{
			"all",
			"Homo sapiens",
			"Saccharomyces cerevisiae",
			"Mus musculus"
	};
	
	public UNIDQueryMask(DatabaseWindow dw){
			
		ImagePath imagePath = ImagePath.getInstance();
		MigLayout layout = new MigLayout("", "[right]");
		p = new JPanel(layout);
		
		choosedatabase = new JComboBox<String>(dbNames);
		choosedatabase.setSelectedItem(dbNames[0]);
		choosedatabase.addItemListener( new ItemListener() {
	          public void itemStateChanged( ItemEvent e ) {
	          }
	        });
		
		chooseOrganism = new JComboBox<String>(orgNames);
		chooseOrganism.setSelectedIndex(0);
		chooseOrganism.addItemListener( new ItemListener() {
	          public void itemStateChanged( ItemEvent e ) {
	          }
	        });
		
		fullName = new JTextField(20);
		alias = new JTextField(20);
		graphID = new JTextField(20);
		
		fullName.setText("HMGCR");
		alias.setText("");
		graphID.setText("");
		
		fullName.addFocusListener(new TextfeldColorChanger());
		alias.addFocusListener(new TextfeldColorChanger());
		graphID.addFocusListener(new TextfeldColorChanger());
		
		JButton search = new JButton("search");
		search.setActionCommand("searchDatabase");
		search.addActionListener(new DatabaseSearchListener(dw));
		
		JButton reset = new JButton("reset");
		reset.setActionCommand("reset");
		reset.addActionListener(new DatabaseSearchListener(dw));
		
		
		p.add(new JLabel("UNID Search Window"),"span 4");
		p.add(new JSeparator(),"span, growx, wrap 15, gaptop 10, gap 5");
		
		p.add(new JLabel(new ImageIcon(imagePath.getPath("dataServer.png"))),"span 2 5");
		
		
		p.add(new JLabel("Database"), "span 2, gap 5 ");
	    p.add(choosedatabase,"span,wrap,growx ,gap 10");
		p.add(new JLabel("Organism"), "span 2, gap 5 ");
	    p.add(chooseOrganism,"span,wrap,growx ,gap 10");	    
		p.add(new JLabel("Name"), "span 2, gap 5 ");
	    p.add(fullName,"span,wrap,growx ,gap 10");
	    p.add(new JLabel("Alias"), "span 2, gap 5 ");
	    p.add(alias,"span,wrap,growx ,gap 10");
		p.add(new JLabel("ID"),"span 2, gap 5 ");
		p.add(graphID,"span, wrap, growx, gap 10");
		
		p.add(new JSeparator(), "span, growx, wrap 10 ");
		p.add(new JLabel(),"gap 20, span 5");
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(reset);
		buttonPanel.add(search);
		
		p.add(buttonPanel,"span");
		
		JButton pick = new JButton(new ImageIcon(imagePath.getPath("infoButton.png")));
		pick.addActionListener(new DatabaseSearchListener(dw));
		pick.setActionCommand("UNIDinfo");
		pick.setBorderPainted(false);
		
		tab = new TitledTab("UNID", null, p, pick);
		tab.getProperties().setHighlightedRaised(2);
		tab.getProperties().getHighlightedProperties().getComponentProperties().setBackgroundColor(Color.WHITE);
		tab.getProperties().getNormalProperties().getComponentProperties().setBackgroundColor(Color.LIGHT_GRAY);
		
	}

	public void reset(){
		
		fullName.setText("");
		alias.setText("");
		graphID.setText("");
	}
	
	
	public TitledTab getTitelTab() {
		return tab;
	}
	
	public String[] getKeyword(){
		
		String[] input = new String[4];
		input[0] = (String) choosedatabase.getSelectedItem();
		input[1]=fullName.getText();
		input[2]=alias.getText();
		input[3]=graphID.getText();
		return input;
	}
	
	public boolean doSearchCriteriaExist(){
		
		if(fullName.getText().length()>0 || alias.getText().length()>0 || graphID.getText().length()>0){
			return true;
		}else{
			return false;
		}
	}
	
	public JPanel getPanel(){
		return p;
	}
}
