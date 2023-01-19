package database.brenda.gui;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import database.eventhandlers.DatabaseSearchListener;
import database.gui.DatabaseWindow;
import database.gui.QueryMask;
import gui.eventhandlers.TextfeldColorChanger;
import gui.images.ImagePath;
import net.miginfocom.swing.MigLayout;

public class BRENDAqueryMask extends QueryMask{
	
	private JPanel p;
	private JTextField ec_number, name, substrate, product, organism;
	
	public BRENDAqueryMask(DatabaseWindow dw){
		super(dw);
		
		ImagePath imagePath = ImagePath.getInstance();
		MigLayout layout = new MigLayout("", "[right]");
		p = new JPanel(layout);
		
		ec_number = new JTextField(20);
		name = new JTextField(20);
		substrate = new JTextField(20);
		product = new JTextField(20);
		organism = new JTextField(20);
		
		name.setText("mutase");
		product.setText("glycerat");
		organism.setText("bacillus");
		
		ec_number.addFocusListener(new TextfeldColorChanger());
		name.addFocusListener(new TextfeldColorChanger());
		substrate.addFocusListener(new TextfeldColorChanger());
		product.addFocusListener(new TextfeldColorChanger());
		organism.addFocusListener(new TextfeldColorChanger());
		
		p.add(new JLabel("BRENDA Search Window"),"span 4");
		p.add(new JSeparator(),       "span, growx, wrap 15, gaptop 10, gap 5");
		
		p.add(new JLabel(imagePath.getImageIcon("dataServer.png")),"span 2 5");
		
		p.add(new JLabel("EC-Number"), "span 2, gap 5 ");
	    p.add(ec_number,"span,wrap,growx ,gap 10");
		p.add(new JLabel("Name"),"span 2, gap 5 ");
		p.add(name,"span, wrap, growx, gap 10");
		p.add(new JLabel("Substrat"),"span 2, gap 5 ");
		p.add(substrate, "span, wrap, growx, gap 10");
		p.add(new JLabel("Product"),"span 2, gap 5 ");
		p.add(product,"span, wrap, growx, gap 10");
		p.add(new JLabel("Organism"),"span 2, gap 5 ");
		p.add(organism,"span, wrap 15, growx, gap 10");
		
		this.addControleButtons(p);
		
		JButton pick = new JButton(imagePath.getImageIcon("infoButton.png"));
		pick.addActionListener(new DatabaseSearchListener(dw));
		pick.setActionCommand("BRENDAinfo");
		pick.setBorderPainted(false);
		
	}

	public void reset(){
		
		ec_number.setText("");
		name.setText("");
		substrate.setText("");
		product.setText("");
		organism.setText("");
	}
	
	
	public String[] getKeyword(){
		
		String[] input = new String[5];
		input[0]=ec_number.getText();
		input[1]=name.getText();
		input[2]= substrate.getText();
		input[3]=product.getText();
		input[4]=organism.getText();
		return input;
	}
	
	public boolean doSearchCriteriaExist(){
		
		if(ec_number.getText().length()>0 || name.getText().length()>0 || substrate.getText().length() > 0 || product.getText().length() > 0 || organism.getText().length() > 0){
			return true;
		}else{
			return false;
		}
	}
	
	public JPanel getPanel(){
		return p;
	}
}
