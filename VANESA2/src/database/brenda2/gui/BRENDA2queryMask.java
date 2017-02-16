package database.brenda2.gui;

import javax.swing.ImageIcon;
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

public class BRENDA2queryMask extends QueryMask{
	
	private JPanel p;
	private JTextField ec_number, name, synonym, metabolite, organism;
	
	public BRENDA2queryMask(DatabaseWindow dw){
		super(dw);
		
		ImagePath imagePath = ImagePath.getInstance();
		MigLayout layout = new MigLayout("", "[right]");
		p = new JPanel(layout);
		
		ec_number = new JTextField(20);
		name = new JTextField(20);
		synonym = new JTextField(20);
		metabolite = new JTextField(20);
		organism = new JTextField(20);
		
		name.setText("mutase");
		metabolite.setText("glycerat");
		organism.setText("bacillus");
		
		ec_number.addFocusListener(new TextfeldColorChanger());
		name.addFocusListener(new TextfeldColorChanger());
		synonym.addFocusListener(new TextfeldColorChanger());
		metabolite.addFocusListener(new TextfeldColorChanger());
		organism.addFocusListener(new TextfeldColorChanger());
		
		p.add(new JLabel("BRENDA Search Window"),"span 4");
		p.add(new JSeparator(),       "span, growx, wrap 15, gaptop 10, gap 5");
		
		p.add(new JLabel(new ImageIcon(imagePath.getPath("dataServer.png"))),"span 2 5");
		
		p.add(new JLabel("EC-Number"), "span 2, gap 5 ");
	    p.add(ec_number,"span,wrap,growx ,gap 10");
		p.add(new JLabel("Name"),"span 2, gap 5 ");
		p.add(name,"span, wrap, growx, gap 10");
		p.add(new JLabel("Synonym"),"span 2, gap 5 ");
		p.add(synonym,"span, wrap, growx, gap 10");
		p.add(new JLabel("Metabolite"),"span 2, gap 5 ");
		p.add(metabolite,"span, wrap, growx, gap 10");
		p.add(new JLabel("Organism"),"span 2, gap 5 ");
		p.add(organism,"span, wrap 15, growx, gap 10");
		
		this.addControleButtons(p);
		
		JButton pick = new JButton(new ImageIcon(imagePath.getPath("infoButton.png")));
		pick.addActionListener(new DatabaseSearchListener(dw));
		pick.setActionCommand("BRENDAinfo");
		pick.setBorderPainted(false);
		
	}

	public void reset(){
		
		ec_number.setText("");
		name.setText("");
		synonym.setText("");
		metabolite.setText("");
		organism.setText("");
	}
	
	
	public String[] getKeyword(){
		
		String[] input = new String[5];
		input[0]=ec_number.getText().trim();
		input[1]=name.getText().trim();
		input[2]=synonym.getText().trim();
		input[3]=metabolite.getText().trim();
		input[4]=organism.getText().trim();
		return input;
	}
	
	public boolean doSearchCriteriaExist(){
		
		if(ec_number.getText().trim().length()>0 || name.getText().trim().length()>0 || synonym.getText().trim().length()>0 || metabolite.getText().trim().length()>0 || organism.getText().trim().length()>0){
			return true;
		}else{
			return false;
		}
	}
	
	public JPanel getPanel(){
		return p;
	}
}
