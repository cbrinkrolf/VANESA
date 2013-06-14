package gui.eventhandlers;

import graph.ContainerSingelton;
import gui.MainWindowSingelton;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextArea;
import javax.swing.JTextField;

import biologicalElements.Pathway;

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
		String newName = ContainerSingelton.getInstance().renamePathway(pw,
				value);
		pw.setName(newName);
		MainWindowSingelton.getInstance().renameSelectedTab(pw.getName());

		ContainerSingelton.getInstance().setPetriView(pw.isPetriNet());
		Component[] c = MainWindowSingelton.getInstance().getContentPane()
				.getComponents();
		for (int i = 0; i < c.length; i++) {
			if (c[i].getClass().getName().equals("javax.swing.JPanel")) {
				MainWindowSingelton.getInstance().getBar()
						.paintToolbar(pw.isPetriNet());
				MainWindowSingelton.getInstance().getmyMenu().setPetriView(pw.isPetriNet());
				break;
			}
		}

	}

	public void focusLost(FocusEvent event) {

		String source = event.getComponent().getName();

		if (source.equals("pathway")) {
			String oldName = pw.getName();
			String value = ((JTextField) event.getSource()).getText();
			String newName = ContainerSingelton.getInstance().renamePathway(pw,
					value);
			MainWindowSingelton.getInstance().checkForAlignmentOptionTab(
					oldName, newName);
			pw.setTitle(newName);
			MainWindowSingelton.getInstance().renameSelectedTab(pw.getName());
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
