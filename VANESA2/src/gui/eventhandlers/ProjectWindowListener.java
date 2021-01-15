package gui.eventhandlers;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextArea;
import javax.swing.JTextField;

import biologicalElements.Pathway;
import graph.GraphContainer;
import gui.MainWindow;

public class ProjectWindowListener implements FocusListener {

	Pathway pw;

	public ProjectWindowListener(Pathway pw) {
		this.pw = pw;
	}

	public void focusGained(FocusEvent event) {
		event.getComponent().setBackground(new Color(200, 227, 255));
	}

	public void updateWindowTab(String name) {

		String value = name;
		String newName = GraphContainer.getInstance().renamePathway(pw,
				value);
		pw.setName(newName);
		MainWindow.getInstance().renameSelectedTab(pw.getName());

		GraphContainer.getInstance().setPetriView(pw.isPetriNet());
		Component[] c = MainWindow.getInstance().getContentPane()
				.getComponents();
		for (int i = 0; i < c.length; i++) {
			if (c[i].getClass().getName().equals("javax.swing.JPanel")) {
				MainWindow.getInstance().getBar()
						.paintToolbar(pw.isPetriNet());
				MainWindow.getInstance().getmyMenu().setPetriView(pw.isPetriNet());
				break;
			}
		}

	}

	public void focusLost(FocusEvent event) {

		String source = event.getComponent().getName();

		if (source.equals("pathway")) {
			String value = ((JTextField) event.getSource()).getText();
			String newName = GraphContainer.getInstance().renamePathway(pw,
					value);
			pw.setTitle(newName);
			MainWindow.getInstance().renameSelectedTab(pw.getName());
			((JTextField) event.getSource()).setText(newName);

		} else if (source.equals("author")) {
			pw.setAuthor(((JTextField) event.getSource()).getText());
		} else if (source.equals("version")) {
			pw.setVersion(((JTextField) event.getSource()).getText());
		} else if (source.equals("date")) {
			pw.setDate(((JTextField) event.getSource()).getText());
		} else if (source.equals("organism")) {
			pw.setOrganism(((JTextField) event.getSource()).getText());
		} else if (source.equals("comment")) {
			pw.setDescription(((JTextArea) event.getSource()).getText());
		}

		event.getComponent().setBackground(Color.WHITE);
	}
}
