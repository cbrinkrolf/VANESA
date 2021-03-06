package gui;

import graph.GraphInstance;
import graph.filter.FilterSettings;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JRadioButton;

import biologicalElements.Pathway;

public class GraphProperties implements ActionListener {

	private JPanel p = new JPanel();
	private GraphInstance graphInstance;
	private Pathway pw;
	private HashSet<JRadioButton> nodeFilter = new HashSet<JRadioButton>();
	private FilterSettings filterSettings;
	boolean emptyPane = true;

	public GraphProperties() {
	}

	public void revalidateView() {

		//TODO reimplement if necessary
		/*
		graphInstance = new GraphInstance();
		pw = graphInstance.getPathway();
		filterSettings = pw.getFilterSettings();
		if (emptyPane) {
			p.removeAll();
			Set<String> set = pw.getAllNodeDescriptions();
			if (set.size() > 0) {
				double rows_calc = (set.size() + 1.0) / 2.0;
				double rows = java.lang.Math.ceil(rows_calc);
				p.setLayout(new GridLayout((int) rows, 2));
				addNodeItem(p, set);
				p.setVisible(true);
				p.repaint();
				p.revalidate();
			} else {
				removeAllElements();
			}
		} else {
			p.removeAll();
			Set<String> set = pw.getAllNodeDescriptions();
			if (set.size() > 0) {
				double rows_calc = (set.size() + 1.0) / 2.0;
				double rows = java.lang.Math.ceil(rows_calc);
				p.setLayout(new GridLayout((int) rows, 2));
				addNodeItem(p, set);
				p.setVisible(true);
				p.repaint();
				p.revalidate();
			} else {
				removeAllElements();
			}
		}*/
	}

	public JPanel getPanel() {
		p.setVisible(false);
		return p;
	}

	public void removeAllElements() {
		emptyPane = true;
		p.removeAll();
		p.setVisible(false);
	}

	public void actionPerformed(ActionEvent e) {

		JRadioButton button = (JRadioButton) e.getSource();
		filterSettings.setFilterValue(button.getName(), button.isSelected());
	}

	private void addNodeItem(JPanel panel, Set<String> set) {

		JRadioButton reference = new JRadioButton("show References");
		reference.addActionListener(this);
		reference.setName("references");
		reference.setSelected(filterSettings
				.getFilterValue(reference.getName()));
		reference.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.add(reference);
		Iterator<String> it = set.iterator();

		String element;
		JRadioButton b;
		
		while (it.hasNext()) {

			element = it.next();
			b = new JRadioButton(element + "s");
			b.addActionListener(this);
			b.setName(element);
			b.setSelected(filterSettings.getFilterValue(element));
			b.setAlignmentX(Component.LEFT_ALIGNMENT);
			panel.add(b);
			nodeFilter.add(b);
		}
	}
}
