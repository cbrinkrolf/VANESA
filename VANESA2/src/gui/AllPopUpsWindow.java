package gui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class AllPopUpsWindow extends JFrame{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextArea textArea = new JTextArea(20, 80);
	private JScrollPane scrollPane = new JScrollPane(textArea);
	
	public AllPopUpsWindow(){
		this.setTitle("Overview of all Messages");
		
		textArea.setText(MyPopUp.getInstance().getAll());
		this.add(scrollPane, BorderLayout.CENTER);
		this.pack();
		// this.setLocation(w.getLocation());
		this.setLocationRelativeTo(MainWindow.getInstance());
		// this.setLocationRelativeTo(null);
		this.setVisible(true);
		//this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
	}
}
