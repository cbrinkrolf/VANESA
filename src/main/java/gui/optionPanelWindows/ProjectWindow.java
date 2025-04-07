package gui.optionPanelWindows;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import biologicalElements.Pathway;
import graph.GraphContainer;
import graph.GraphInstance;
import gui.MainWindow;
import net.miginfocom.swing.MigLayout;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class ProjectWindow implements FocusListener, KeyListener {
	private final JPanel panel = new JPanel(new MigLayout("ins 0, fillx, wrap 2", "[]5[grow]"));
	private boolean emptyPane = true;
	private Pathway pw;

	public JPanel getPanel() {
		panel.setVisible(false);
		return panel;
	}

	public void removeAllElements() {
		emptyPane = true;
		panel.removeAll();
		panel.setVisible(false);
	}

	public void revalidateView() {
		if (!emptyPane) {
			panel.removeAll();
		}
		updateWindow();
		panel.setVisible(true);
		panel.repaint();
		panel.revalidate();
		emptyPane = false;
	}

	private void updateWindow() {
		pw = GraphInstance.getPathway();

		panel.add(new JLabel("Pathway"));
		JTextField pathway = new JTextField(pw.getTitle(), 20);
		pathway.setName("pathway");
		pathway.addFocusListener(this);
		pathway.addKeyListener(this);
		panel.add(pathway, "growx");

		panel.add(new JLabel("Organism"));
		JTextField organism = new JTextField(pw.getOrganism(), 20);
		organism.setName("organism");
		organism.addFocusListener(this);
		organism.addKeyListener(this);
		panel.add(organism, "growx");

		panel.add(new JLabel("Author"));
		JTextField author = new JTextField(pw.getAuthor(), 20);
		author.setName("author");
		author.addFocusListener(this);
		author.addKeyListener(this);
		panel.add(author, "growx");

		panel.add(new JLabel("Version"));
		JTextField version = new JTextField(pw.getVersion(), 20);
		version.setName("version");
		version.addFocusListener(this);
		version.addKeyListener(this);
		panel.add(version, "growx");

		panel.add(new JLabel("Date"));
		JTextField date = new JTextField(pw.getDate(), 20);
		date.setName("date");
		date.addFocusListener(this);
		date.addKeyListener(this);
		panel.add(date, "growx");

		panel.add(new JLabel("Description"), "height 20:20:20");
		panel.add(new JSeparator(), "growx");

		JTextArea comment = new JTextArea(15, 5);
		comment.setName("comment");
		comment.setText(pw.getDescription());
		comment.addFocusListener(this);
		comment.addKeyListener(this);
		panel.add(comment, "span 2, growx");

		updateWindowTab(pw.getTitle());
	}

	public void updateWindowTab(final String name) {
		String newName = GraphContainer.getInstance().renamePathway(pw, name);
		pw.setName(newName);
		MainWindow.getInstance().renameSelectedTab(pw.getName());
		GraphContainer.getInstance().setPetriView(pw.isPetriNet());
		Component[] c = MainWindow.getInstance().getFrame().getContentPane().getComponents();
		for (Component component : c) {
			if (component.getClass().getName().equals("javax.swing.JPanel")) {
				MainWindow.getInstance().getBar().updateVisibility();
				MainWindow.getInstance().getMenu().setPetriView(pw.isPetriNet()
						|| pw.getTransformationInformation() != null
						&& pw.getTransformationInformation().getPetriNet() != null);
				break;
			}
		}
	}

	@Override
	public void focusGained(final FocusEvent event) {
		event.getComponent().setBackground(new Color(200, 227, 255));
	}

	@Override
	public void focusLost(final FocusEvent event) {
		event.getComponent().setBackground(Color.WHITE);
		onSaveValue(event.getComponent().getName(), event.getSource());
	}

	private void onSaveValue(final String action, final Object source) {
		final String value;
		if (source instanceof JTextField) {
			value = ((JTextField) source).getText();
		} else if (source instanceof JTextArea) {
			value = ((JTextArea) source).getText();
		} else {
			value = "";
		}
		switch (action) {
		case "pathway":
			String newName = GraphContainer.getInstance().renamePathway(pw, value);
			pw.setTitle(newName);
			MainWindow.getInstance().renameSelectedTab(pw.getName());
			((JTextField) source).setText(newName);
			break;
		case "author":
			pw.setAuthor(value);
			break;
		case "version":
			pw.setVersion(value);
			break;
		case "date":
			pw.setDate(value);
			break;
		case "organism":
			pw.setOrganism(value);
			break;
		case "comment":
			pw.setDescription(value);
			break;
		}
	}

	@Override
	public void keyTyped(final KeyEvent e) {
	}

	@Override
	public void keyPressed(final KeyEvent e) {
	}

	@Override
	public void keyReleased(final KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			onSaveValue(e.getComponent().getName(), e.getSource());
		}
	}
}
