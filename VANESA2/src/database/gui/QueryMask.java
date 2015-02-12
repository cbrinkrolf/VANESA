package database.gui;

import gui.images.ImagePath;

import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import net.miginfocom.swing.MigLayout;
import database.eventhandlers.DatabaseSearchListener;

public class QueryMask implements ItemListener {
	private DatabaseWindow dw;
	private JCheckBox headless;

	public QueryMask(DatabaseWindow dw) {
		this.dw = dw;
		headless = new JCheckBox("headless");
		headless.addItemListener(this);
		dw.setHeadless(headless.isSelected());
	}

	public void addControleButtons(JPanel p) {
		JButton search = new JButton("search");
		search.setActionCommand("searchDatabase");
		search.addActionListener(new DatabaseSearchListener(dw));

		JButton reset = new JButton("reset");
		reset.setActionCommand("reset");
		reset.addActionListener(new DatabaseSearchListener(dw));

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(headless);
		buttonPanel.add(reset);
		buttonPanel.add(search);

		p.add(new JSeparator(), "span, growx, wrap 10 ");
		p.add(new JLabel(), "gap 20, span 5");
		p.add(buttonPanel, "span");
	}

	public boolean isHeadless() {
		return headless.isSelected();
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		// TODO Auto-generated method stub

		if (e.getSource() instanceof JCheckBox) {
		
			JCheckBox box = (JCheckBox) e.getSource();
			dw.setHeadless(box.isSelected());
		}

	}
	
	public JPanel getTitelTab(String db) {
		ImagePath imagePath = ImagePath.getInstance();
		JPanel pan = new JPanel(new MigLayout("ins 0"));
		pan.add(new JLabel(db));
		JButton info = new JButton(new ImageIcon(imagePath.getPath("infoButton.png")));
		info.setMaximumSize(new Dimension(20,20));
		info.setActionCommand(db+"info");
		info.addActionListener(new DatabaseSearchListener(dw));
		info.setBorderPainted(false);
		pan.add(info);
		return pan;
	}

}
