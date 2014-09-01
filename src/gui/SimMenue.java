package gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import petriNet.PetriNetSimulation;


public class SimMenue extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JButton start = new JButton("Start");
	private JButton stop = new JButton("Stop");
	private JLabel status = new JLabel("");
	private JLabel time = new JLabel("Time: ");
	private JTextArea textArea = new JTextArea(20,80);
	private PetriNetSimulation sim;
	private JPanel north = new JPanel();
	private JScrollPane scrollPane = new JScrollPane(textArea);

	private ActionListener listener;
	
	
	public SimMenue(ActionListener listener){
		this.listener = listener;
		start.setActionCommand("start");
		start.addActionListener(listener);
		stop.setActionCommand("stop");
		stop.addActionListener(listener);
		
		north.setLayout(new GridLayout(1,10));
		
		this.setLayout(new BorderLayout());
		this.stop.setEnabled(false);
		north.add(start);
		north.add(stop);
		north.add(time);
		north.add(status);
		this.add(north, BorderLayout.NORTH);
		//textArea.setAutoscrolls(true);
		this.add(scrollPane, BorderLayout.SOUTH);
		// this.add(textArea, BorderLayout.SOUTH);
		
		this.pack();
		this.setVisible(true);
		
	}
	
	public void started(){
		start.setEnabled(false);
		stop.setEnabled(true);
	}
	
	public void stopped(){
		start.setEnabled(true);
		stop.setEnabled(false);
	}
	
	public void setTime(double time){
		this.time.setText("Time: "+time);
		this.time.repaint();
	}
	
	public void setStatus(){
		
	}
	
	public void addText(String text){
		this.textArea.setText(textArea.getText()+text);
		this.pack();
	}
	
}
