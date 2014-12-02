/**
 * 
 */
package database.brenda.gui;

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
 * @author Sebastian
 * 
 */
public class BrendaSettingsDialog extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JPanel panel;
	JOptionPane pane;

	JSpinner serchDeapth;
	JCheckBox organismSpecificBox = new JCheckBox();
	JCheckBox locationSpecificBox = new JCheckBox();
	JCheckBox inhibitorBox = new JCheckBox();
	JCheckBox coFactorBox = new JCheckBox();
	
	/**
	 * 
	 */
	public BrendaSettingsDialog() {

		//Container contentPane = getContentPane();
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
		
		panel.add(new JLabel("Get Inhibitors"), "span 2, gap 5, gaptop 2 ");
		panel.add(inhibitorBox, "span 1,wrap,gaptop 2");
		
		panel.add(new JLabel("Get CoFactors"), "span 2, gap 5, gaptop 2 ");
		panel.add(coFactorBox, "span 1,wrap,gaptop 2");
		
		panel.add(new JLabel("Consider element locations"), "span 2, gap 5, gaptop 2 ");
		panel.add(locationSpecificBox, "span 1,wrap,gaptop 2");
		
		panel.add(new JSeparator(), "span, growx, wrap 10, gaptop 7 ");

		pane = new JOptionPane(panel, JOptionPane.QUESTION_MESSAGE,
				JOptionPane.OK_CANCEL_OPTION);
		
		JDialog dialog = pane.createDialog(BrendaSettingsDialog.this, "");
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
	
	public boolean getInhibitorDecision(){
		return inhibitorBox.isSelected();
	}
	
	public boolean getCoFactorDecision(){
		return coFactorBox.isSelected();
	}
}
