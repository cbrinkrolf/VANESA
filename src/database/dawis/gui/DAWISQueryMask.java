package database.dawis.gui;

import gui.eventhandlers.TextfeldColorChanger;
import gui.images.ImagePath;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import net.infonode.tabbedpanel.titledtab.TitledTab;
import net.miginfocom.swing.MigLayout;
import database.dawis.webstart.DAWISWebstartCombiner;
import database.eventhandlers.DatabaseSearchListener;
import database.gui.DatabaseWindow;

/**
 * @author Olga
 * 
 */

/*
 * The query mask to handover the search parameter to DAWIS
 */
public class DAWISQueryMask implements ActionListener {

	DAWISWebstartCombiner comb = null;

	private TitledTab tab;
	JPanel p;
	JComboBox object;
	JRadioButton basic, expert;
	JTextField object1, name, id, organism;
	String[] objects = { "Pathway Map", "Disease", "Gene Ontology", "Gene",
			"Protein", "Enzyme", "Reaction", "Reaction Pair", "Compound",
			"Glycan", "Drug" };

	/*
	 * generates the DAWIS-tab with required fields
	 */
	public DAWISQueryMask(DatabaseWindow dw) {

		ImagePath imagePath = ImagePath.getInstance();
		MigLayout layout = new MigLayout("", "[right]");
		p = new JPanel(layout);

		object = new JComboBox();
		name = new JTextField(20);
		id = new JTextField(20);
		id.setText("00790");
		organism = new JTextField(20);

		for (int i = 0; i < objects.length; i++) {
			object.addItem(objects[i]);
		}

		object.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				JComboBox selectedChoice = (JComboBox) e.getSource();

				if (selectedChoice.getSelectedItem().equals("Pathway Map")) {
					id.setText("00790");
					name.setText("");
					organism.setText("");
				} else if (selectedChoice.getSelectedItem().equals("Disease")) {
					id.setText("");
					name.setText("Phenylketonuria");
					organism.setText("");
				} else if (selectedChoice.getSelectedItem().equals("Protein")) {
					id.setText("PH4H_HUMAN");
					name.setText("");
					organism.setText("homo sapiens");
				} else if (selectedChoice.getSelectedItem().equals("Gene")) {
					id.setText("5860");
					name.setText("");
					organism.setText("homo sapiens");
				} else if (selectedChoice.getSelectedItem().equals(
						"Gene Ontology")) {
					id.setText("0004505");
					name.setText("");
					organism.setText("");
				} else if (selectedChoice.getSelectedItem().equals("Glycan")) {
					id.setText("G00015");
					name.setText("");
					organism.setText("");
				} else if (selectedChoice.getSelectedItem().equals("Reaction")) {
					id.setText("XN000000033");
					name.setText("");
					organism.setText("");
				} else if (selectedChoice.getSelectedItem().equals(
						"Reaction Pair")) {
					id.setText("RP00073");
					name.setText("");
					organism.setText("");
				} else if (selectedChoice.getSelectedItem().equals(
						"Interaction")) {
					id.setText("");
					name.setText("");
					organism.setText("");
				} else if (selectedChoice.getSelectedItem().equals("Drug")) {
					id.setText("D00110");
					name.setText("");
					organism.setText("");
				} else if (selectedChoice.getSelectedItem().equals("Enzyme")) {
					id.setText("1.5.1.34");
					name.setText("");
					organism.setText("");
				} else if (selectedChoice.getSelectedItem().equals("Compound")) {
					id.setText("C00005");
					name.setText("");
					organism.setText("");
				}

			}
		});
		name.addFocusListener(new TextfeldColorChanger());
		id.addFocusListener(new TextfeldColorChanger());
		organism.addFocusListener(new TextfeldColorChanger());

		basic = new JRadioButton("Basic");
		basic.setSelected(true);
		basic.addActionListener(this);
		basic.setActionCommand("basic");

		expert = new JRadioButton("Expert");
		expert.addActionListener(this);
		expert.setActionCommand("expert");

		JButton search = new JButton("search");
		search.setActionCommand("searchDatabase");
		search.addActionListener(new DatabaseSearchListener(dw));

		p.add(new JLabel("DAWIS Search Window"), "span 4");
		p.add(new JSeparator(), "span, growx, wrap 15, gaptop 10, gap 5");

		p.add(new JLabel(new ImageIcon(imagePath.getPath("dataServer.png"))),
				"span 2 5");

		p.add(new JLabel("Object"), "span 2, gap 5 ");
		p.add(object, "span,wrap,growx ,gap 10");
		p.add(new JLabel("Name"), "span 2, gap 5 ");
		p.add(name, "span, wrap, growx, gap 10");
		p.add(new JLabel("Element-id"), "span 2, gap 5 ");
		p.add(id, "span, wrap, growx, gap 10");
		p.add(new JLabel("Organism"), "span 2, gap 5");
		p.add(organism, "span, wrap, growx, gap 10");
		p.add(new JLabel("Mode"), "span 2, gap 5 ");
		p.add(basic, "gapleft 5");
		p.add(expert, "span,wrap,growx ,gap 10");
		p.add(new JSeparator(), "span, growx, wrap 10 ");
		p.add(new JLabel(), "gap 20, span 5");

		JButton reset = new JButton("reset");
		reset.setActionCommand("reset");
		reset.addActionListener(new DatabaseSearchListener(dw));

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(reset);
		buttonPanel.add(search);

		p.add(buttonPanel, "span");

		JButton pick = new JButton(new ImageIcon(imagePath
				.getPath("infoButton.png")));
		pick.addActionListener(new DatabaseSearchListener(dw));
		pick.setActionCommand("DAWISinfo");
		pick.setBorderPainted(false);

		tab = new TitledTab("DAWIS-M.D.", null, p, pick);
		tab.getProperties().setHighlightedRaised(2);
		tab.getProperties().getHighlightedProperties().getComponentProperties()
				.setBackgroundColor(Color.WHITE);
		tab.getProperties().getNormalProperties().getComponentProperties()
				.setBackgroundColor(Color.LIGHT_GRAY);

	}

	/*
	 * gets the TitledTab
	 */
	public TitledTab getTitelTab() {
		return tab;
	}

	/*
	 * gets the parameter to search for as an array of strings
	 */
	public String[] getKeyword() {

		String[] input = new String[4];
		input[0] = object.getSelectedItem().toString();
		input[1] = name.getText();
		input[2] = id.getText();
		input[3] = organism.getText();

		return input;
	}

	/*
	 * tests, if the object and some further parameter to search for are chosen
	 */
	public boolean doSearchCriteriaExist() {

		if (name.getText().length() > 0 || id.getText().length() > 0) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * gets the JPanel
	 */
	public JPanel getPanel() {
		return p;
	}

	/*
	 * gets the boolean for organism specification
	 */
	public boolean getOrganismSpecification() {
		if (!organism.getText().equals("")) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * gets searching mode
	 */
	public String getMode() {
		if (basic.isSelected()) {
			return "Basic";
		} else {
			return "Expert";
		}
	}

	public void reset() {
		//System.out.println("bin drin");
		name.setText("");
		id.setText("");
		organism.setText("");
	}

	// switch the mode
	public void actionPerformed(ActionEvent event) {

		if (event.getActionCommand().equals("basic")) {
			if (basic.isSelected()) {
				expert.setSelected(false);
			} else {
				basic.setSelected(false);
			}
		} else if (event.getActionCommand().equals("expert")) {
			if (expert.isSelected()) {
				basic.setSelected(false);
			} else {
				expert.setSelected(false);
			}
		}

	}
}
