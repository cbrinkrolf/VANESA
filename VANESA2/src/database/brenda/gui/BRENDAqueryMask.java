package database.brenda.gui;

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
import database.gui.QueryMask;

public class BRENDAqueryMask extends QueryMask{
	
	private TitledTab tab;
	private JPanel p;
	private JTextField ec_number, name, substrat, product, organism;
	
	public BRENDAqueryMask(DatabaseWindow dw){
		super(dw);
		
		ImagePath imagePath = ImagePath.getInstance();
		MigLayout layout = new MigLayout("", "[right]");
		p = new JPanel(layout);
		
		ec_number = new JTextField(20);
		name = new JTextField(20);
		substrat = new JTextField(20);
		product = new JTextField(20);
		organism = new JTextField(20);
		
		name.setText("mutase");
		product.setText("glycerat");
		organism.setText("bacillus");
		
		ec_number.addFocusListener(new TextfeldColorChanger());
		name.addFocusListener(new TextfeldColorChanger());
		substrat.addFocusListener(new TextfeldColorChanger());
		product.addFocusListener(new TextfeldColorChanger());
		organism.addFocusListener(new TextfeldColorChanger());
		
		p.add(new JLabel("BRENDA Search Window"),"span 4");
		p.add(new JSeparator(),       "span, growx, wrap 15, gaptop 10, gap 5");
		
		p.add(new JLabel(new ImageIcon(imagePath.getPath("dataServer.png"))),"span 2 5");
		
		p.add(new JLabel("EC-Number"), "span 2, gap 5 ");
	    p.add(ec_number,"span,wrap,growx ,gap 10");
		p.add(new JLabel("Name"),"span 2, gap 5 ");
		p.add(name,"span, wrap, growx, gap 10");
		p.add(new JLabel("Substrat"),"span 2, gap 5 ");
		p.add(substrat,"span, wrap, growx, gap 10");
		p.add(new JLabel("Product"),"span 2, gap 5 ");
		p.add(product,"span, wrap, growx, gap 10");
		p.add(new JLabel("Organism"),"span 2, gap 5 ");
		p.add(organism,"span, wrap 15, growx, gap 10");
		
		this.addControleButtons(p);
		
		JButton pick = new JButton(new ImageIcon(imagePath.getPath("infoButton.png")));
		pick.addActionListener(new DatabaseSearchListener(dw));
		pick.setActionCommand("BRENDAinfo");
		pick.setBorderPainted(false);
		
		tab = new TitledTab("BRENDA", null, p, pick);
		tab.getProperties().setHighlightedRaised(2);
		tab.getProperties().getHighlightedProperties().getComponentProperties().setBackgroundColor(Color.WHITE);
		tab.getProperties().getNormalProperties().getComponentProperties().setBackgroundColor(Color.LIGHT_GRAY);
		
	}

	public void reset(){
		
		ec_number.setText("");
		name.setText("");
		substrat.setText("");
		product.setText("");
		organism.setText("");
	}
	
	
	public TitledTab getTitelTab() {
		return tab;
	}
	
	public String[] getKeyword(){
		
		String[] input = new String[5];
		input[0]=ec_number.getText();
		input[1]=name.getText();
		input[2]=substrat.getText();
		input[3]=product.getText();
		input[4]=organism.getText();
		return input;
	}
	
	public boolean doSearchCriteriaExist(){
		
		if(ec_number.getText().length()>0 || name.getText().length()>0 || substrat.getText().length()>0 || product.getText().length()>0 || organism.getText().length()>0){
			return true;
		}else{
			return false;
		}
	}
	
	public JPanel getPanel(){
		return p;
	}
}
