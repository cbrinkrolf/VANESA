package database.dawis.gui;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import net.miginfocom.swing.MigLayout;

/**
 * @author Olga
 * 
 */

/*
 * Die Klasse wurde von Brenda unverändert übernommen und muss an DAWIS angepasst werden
 */
@SuppressWarnings("serial")
public class DAWISSettingsDialog extends JFrame{
	
	JPanel panel;
	JOptionPane pane;
	JSpinner serchDeapth;
	
	JCheckBox organismSpecificBox = new JCheckBox();
	JCheckBox locationSpecificBox = new JCheckBox();
	
	/*
	 * 
	 */
	public DAWISSettingsDialog() {

		MigLayout layout = new MigLayout("", "[right]");

		panel = new JPanel(layout);
		
		panel.add(new JLabel("What kind of Search - Settings are supposed to be applied?"), "span 4, wrap 5");
		panel.add(new JSeparator(), "span, growx, wrap 15, gaptop 10");
	
	    SpinnerNumberModel model1 = new SpinnerNumberModel( 1, 1, 20, 1);
	    serchDeapth = new JSpinner( model1 );

	    panel.add(new JLabel("Search Depth"), "span 2, gap 5, gaptop 2 ");
		panel.add(serchDeapth, "span 1,wrap,gaptop 2");
		
		panel.add(new JLabel("Organism specific calculation"), "span 2, gap 5, gaptop 2 ");
		panel.add(organismSpecificBox, "span 1,wrap,gaptop 2");
		
		panel.add(new JLabel("Consider element locations"), "span 2, gap 5, gaptop 2 ");
		panel.add(locationSpecificBox, "span 1,wrap,gaptop 2");
		
		panel.add(new JSeparator(), "span, growx, wrap 10, gaptop 7 ");

		pane = new JOptionPane(panel, JOptionPane.QUESTION_MESSAGE,
				JOptionPane.OK_CANCEL_OPTION);
		
		JDialog dialog = pane.createDialog(DAWISSettingsDialog.this, "");
		dialog.setVisible(true);
		
	}

	public boolean continueProgress() {
		
		Integer value = (Integer) pane.getValue();

		if (value != null) {
			if (value.intValue() == JOptionPane.OK_OPTION) return true;
		}	
		
		return false;
	}

	public Integer getSerchDeapth() {
		return (Integer)serchDeapth.getValue();
	}
	
	public boolean getOrganismSpecificDecision(){
		return organismSpecificBox.isSelected();
	}
	
	public boolean getLocationSpecificDecision(){
		return locationSpecificBox.isSelected();
	}

}
