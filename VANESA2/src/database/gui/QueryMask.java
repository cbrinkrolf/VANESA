package database.gui;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import database.eventhandlers.DatabaseSearchListener;

public class QueryMask {
	private DatabaseWindow dw;
	private JCheckBox headless;

	public QueryMask(DatabaseWindow dw) {
		this.dw = dw;
	}

	public void addControleButtons(JPanel p) {
		JButton search = new JButton("search");
		search.setActionCommand("searchDatabase");
		search.addActionListener(new DatabaseSearchListener(dw));

		JButton reset = new JButton("reset");
		reset.setActionCommand("reset");
		reset.addActionListener(new DatabaseSearchListener(dw));

		headless = new JCheckBox("headless");
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(headless);
		buttonPanel.add(reset);
		buttonPanel.add(search);
		
		p.add(new JSeparator(), "span, growx, wrap 10 ");
		p.add(new JLabel(),"gap 20, span 5");
		p.add(buttonPanel, "span");
	}
	
	public boolean isHeadless(){
		return headless.isSelected();
	}

}
