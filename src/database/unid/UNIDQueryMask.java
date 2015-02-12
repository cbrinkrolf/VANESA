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

	private JComboBox<String> chooseOrganism;
	private String[] orgNames = { "all", "Homo sapiens",
			"Saccharomyces cerevisiae", "Mus musculus" };

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

		chooseOrganism = new JComboBox<String>(orgNames);
		chooseOrganism.setSelectedIndex(0);
		chooseOrganism.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
			}
		});

		modeldepthspinner = new SpinnerNumberModel(1.0d, 0.0d, 6.0d, 1.0d);
		;
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

		p.add(new JLabel("Database"), "span 2, gap 5 ");
		p.add(choosedatabase, "span,wrap,growx ,gap 10");
		p.add(new JLabel("Organism"), "span 2, gap 5 ");
		p.add(chooseOrganism, "span,wrap,growx ,gap 10");
		p.add(new JLabel("Full Name"), "span 2, gap 5 ");
		p.add(fullName, "span,wrap,growx ,gap 10");
		p.add(new JLabel("Common Name"), "span 2, gap 5 ");
		p.add(commonName, "span,wrap,growx ,gap 10");
		p.add(new JLabel("GraphID"), "span 2, gap 5 ");
		p.add(graphID, "span, wrap, growx, gap 10");
		p.add(new JLabel("Depth"), "span 2, gap 5");
		p.add(depthspinner, "span, wrap, growx, gap 10");

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
	}

	public String[] getKeyword() {

		String[] input = new String[5];
		input[0] = (String) choosedatabase.getSelectedItem();
		input[1] = fullName.getText();
		input[2] = commonName.getText();
		input[3] = graphID.getText();
		input[4] = depthspinner.getValue() + "";
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
