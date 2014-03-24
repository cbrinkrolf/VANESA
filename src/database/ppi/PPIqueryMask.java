package database.ppi;

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

public class PPIqueryMask {
	
	private TitledTab tab;
	private JPanel p;
	
	private JComboBox choosePPIdatabase;
	private String[] dbNames =
	{
			"HPRD",
			"MINT",
			"IntAct"
	};
	
	private JTextField fullName, alias, acNumber;
	
//	private JComboBox chooseOrganism;
//	private String[] orgNames =
//	{
//			"all",
//			"Homo sapiens",
//			"Saccharomyces cerevisiae",
//			"Mus musculus"
//	};
	
	public PPIqueryMask(DatabaseWindow dw){
			
		ImagePath imagePath = ImagePath.getInstance();
		MigLayout layout = new MigLayout("", "[right]");
		p = new JPanel(layout);
		
		choosePPIdatabase = new JComboBox(dbNames);
		choosePPIdatabase.setSelectedItem(dbNames[1]);
		choosePPIdatabase.addItemListener( new ItemListener() {
	          public void itemStateChanged( ItemEvent e ) {
//	            JComboBox selectedChoice = (JComboBox)e.getSource();
	          }
	        });
		
		fullName = new JTextField(20);
		alias = new JTextField(20);
		acNumber = new JTextField(20);
		
		fullName.setText("");
		alias.setText("");
		acNumber.setText("P22803");
		
		fullName.addFocusListener(new TextfeldColorChanger());
		alias.addFocusListener(new TextfeldColorChanger());
		acNumber.addFocusListener(new TextfeldColorChanger());
		
		JButton search = new JButton("search");
		search.setActionCommand("searchDatabase");
		search.addActionListener(new DatabaseSearchListener(dw));
		
		JButton reset = new JButton("reset");
		reset.setActionCommand("reset");
		reset.addActionListener(new DatabaseSearchListener(dw));
		
		
		p.add(new JLabel("PPI Search Window"),"span 4");
		p.add(new JSeparator(),       "span, growx, wrap 15, gaptop 10, gap 5");
		
		p.add(new JLabel(new ImageIcon(imagePath.getPath("dataServer.png"))),"span 2 5");
		
		
		p.add(new JLabel("Database"), "span 2, gap 5 ");
	    p.add(choosePPIdatabase,"span,wrap,growx ,gap 10");
		p.add(new JLabel("Name"), "span 2, gap 5 ");
	    p.add(fullName,"span,wrap,growx ,gap 10");
	    p.add(new JLabel("Alias"), "span 2, gap 5 ");
	    p.add(alias,"span,wrap,growx ,gap 10");
		p.add(new JLabel("AC number"),"span 2, gap 5 ");
		p.add(acNumber,"span, wrap, growx, gap 10");
		
		p.add(new JSeparator(), "span, growx, wrap 10 ");
		p.add(new JLabel(),"gap 20, span 5");
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(reset);
		buttonPanel.add(search);
		
		p.add(buttonPanel,"span");
		
		JButton pick = new JButton(new ImageIcon(imagePath.getPath("infoButton.png")));
		pick.addActionListener(new DatabaseSearchListener(dw));
		pick.setActionCommand("PPIinfo");
		pick.setBorderPainted(false);
		
		tab = new TitledTab("PPI", null, p, pick);
		tab.getProperties().setHighlightedRaised(2);
		tab.getProperties().getHighlightedProperties().getComponentProperties().setBackgroundColor(Color.WHITE);
		tab.getProperties().getNormalProperties().getComponentProperties().setBackgroundColor(Color.LIGHT_GRAY);
		
	}

	public void reset(){
		
		fullName.setText("");
		alias.setText("");
		acNumber.setText("");
	}
	
	
	public TitledTab getTitelTab() {
		return tab;
	}
	
	public String[] getKeyword(){
		
		String[] input = new String[4];
		input[0] = (String) choosePPIdatabase.getSelectedItem();
		input[1]=fullName.getText();
		input[2]=alias.getText();
		input[3]=acNumber.getText();
		return input;
	}
	
	public boolean doSearchCriteriaExist(){
		
		if(fullName.getText().length()>0 || alias.getText().length()>0 || acNumber.getText().length()>0){
			return true;
		}else{
			return false;
		}
	}
	
	public JPanel getPanel(){
		return p;
	}
}