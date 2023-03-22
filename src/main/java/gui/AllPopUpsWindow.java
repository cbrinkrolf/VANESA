package gui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class AllPopUpsWindow extends JFrame {
	private static final long serialVersionUID = 1L;

	public AllPopUpsWindow() {
		setTitle("Overview of all PopUp messages");
		JTextArea textArea = new JTextArea(20, 80);
		textArea.setText(PopUpDialog.getInstance().getAll());
		JScrollPane scrollPane = new JScrollPane(textArea);
		add(scrollPane, BorderLayout.CENTER);
		pack();
		setIconImages(MainWindow.getInstance().getFrame().getIconImages());
		setLocationRelativeTo(MainWindow.getInstance().getFrame());
		setVisible(true);
	}
}
