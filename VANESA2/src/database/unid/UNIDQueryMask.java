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
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import net.miginfocom.swing.MigLayout;
import database.eventhandlers.DatabaseSearchListener;
import database.gui.DatabaseWindow;
import database.gui.QueryMask;

/**
 * 
 * @author mlewinsk May 2014
 */
public class UNIDQueryMask extends QueryMask {

	private JPanel p;

	private JComboBox<String> choosedatabase;
	private String[] dbNames = { "UNID" };

	private JTextField fullName, commonName, graphID;
	private JLabel labeldatabase = new JLabel("Database"),
			labeltype = new JLabel("Type"),
			labelfullname = new JLabel("Full Name"),
			labelcommonname = new JLabel("Common Name"),
			labelgraphid = new JLabel("GraphID"),
			labeldepth = new JLabel("Depth"),
			labeldirection = new JLabel("Direction");

	private JComboBox<String> chooseType;
	private String[] typeNames = { "ppi", "cellular_component",
			"biological_process", "molecular_function" };
	
	private JComboBox<String> chooseDirection;
	private String[] directionsNames = {"both","outgoing","incoming"};

	private JSpinner depthspinner;
	private SpinnerNumberModel modeldepthspinner;

	public UNIDQueryMask(DatabaseWindow dw) {
		super(dw);
		ImagePath imagePath = ImagePath.getInstance();
		MigLayout layout = new MigLayout("", "[right]");
		p = new JPanel(layout);

		choosedatabase = new JComboBox<String>(dbNames);
		choosedatabase.setSelectedItem(dbNames[0]);
		choosedatabase.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
			}
		});

		chooseType = new JComboBox<String>(typeNames);
		chooseType.setSelectedIndex(0);
		chooseType.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
			
				switch (chooseType.getSelectedIndex()) {
				case 0: // Protein
					labelcommonname.setEnabled(true);
					commonName.setEnabled(true);					
					labelgraphid.setEnabled(true);
					graphID.setEnabled(true);
					
					labeldirection.setEnabled(false);
					chooseDirection.setEnabled(false);					
					break;

				case 1:// GO stuff
				case 2:
				case 3:
					labelcommonname.setEnabled(false);
					commonName.setEnabled(false);
					labelgraphid.setEnabled(false);
					graphID.setEnabled(false);
					
					labeldirection.setEnabled(true);
					chooseDirection.setEnabled(true);	
					break;
					
				default:
					System.out.println("UNKNOWN CASE IN SWITCH:"+this.getClass());
					break;
				} 
				
				
			}
		});
		
		chooseDirection = new JComboBox<String>(directionsNames);
		chooseDirection.setSelectedIndex(0);
		chooseDirection.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
			}
		});
		
		
		modeldepthspinner = new SpinnerNumberModel(1.0d, 0.0d, 20.0d, 1.0d);
		depthspinner = new JSpinner(modeldepthspinner);

		fullName = new JTextField(20);
		commonName = new JTextField(20);
		graphID = new JTextField(20);

		
		
		fullName.setText("");
		commonName.setText("HMGCR");
		graphID.setText("");

		fullName.addFocusListener(new TextfeldColorChanger());
		commonName.addFocusListener(new TextfeldColorChanger());
		graphID.addFocusListener(new TextfeldColorChanger());

		JButton pickCommons = new JButton("pick commons");
		pickCommons.setActionCommand("pickcommons");
		pickCommons.addActionListener(new DatabaseSearchListener(dw));

		JButton pickNeighbors = new JButton("pick neighbors");
		pickNeighbors.setActionCommand("pickneighbors");
		pickNeighbors.addActionListener(new DatabaseSearchListener(dw));

		p.add(new JLabel("UNID Search Window"), "span 4");
		p.add(new JSeparator(), "span, growx, wrap 15, gaptop 10, gap 5");

		p.add(new JLabel(new ImageIcon(imagePath.getPath("dataServer.png"))),
				"span 2 6");

		p.add(labeldatabase, "span 2, gap 5 ");
		p.add(choosedatabase, "span,wrap,growx ,gap 10");
		p.add(labeltype, "span 2, gap 5 ");
		p.add(chooseType, "span,wrap,growx ,gap 10");
		p.add(labelfullname, "span 2, gap 5 ");
		p.add(fullName, "span,wrap,growx ,gap 10");
		p.add(labelcommonname, "span 2, gap 5 ");
		p.add(commonName, "span,wrap,growx ,gap 10");
		p.add(labelgraphid, "span 2, gap 5 ");
		p.add(graphID, "span, wrap, growx, gap 10");
		p.add(labeldepth, "span 2, gap 5");
		p.add(depthspinner, "span, wrap, growx, gap 10");
		p.add(labeldirection,"span 4, gap 5");
		p.add(chooseDirection, "span, wrap, growx, gap 10");

		//Standard, protein search. show only if GO search is chosen
		labeldirection.setEnabled(false);
		chooseDirection.setEnabled(false);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(pickCommons);
		
		p.add(buttonPanel, "span");
		p.add(pickNeighbors, "span");
		
		this.addControleButtons(p);

		JButton pick = new JButton(new ImageIcon(
				imagePath.getPath("infoButton.png")));
		pick.addActionListener(new DatabaseSearchListener(dw));
		pick.setActionCommand("UNIDinfo");
		pick.setBorderPainted(false);

	}

	public void reset() {

		fullName.setText("");
		commonName.setText("");
		graphID.setText("");
		depthspinner.setValue(1.0d);
		chooseDirection.setSelectedIndex(0);
	}

	public String[] getKeyword() {

		String[] input = new String[7];
		input[0] = (String) choosedatabase.getSelectedItem();
		input[1] = (String) chooseType.getSelectedItem();
		input[2] = fullName.getText();
		input[3] = commonName.getText();
		input[4] = graphID.getText();
		input[5] = depthspinner.getValue() + "";
		input[6] = (String) chooseDirection.getSelectedItem();
		return input;
	}

	public boolean doSearchCriteriaExist() {

		if (fullName.getText().length() > 0
				|| commonName.getText().length() > 0
				|| graphID.getText().length() > 0) {
			return true;
		} else {
			return false;
		}
	}

	public JPanel getPanel() {
		return p;
	}
}
