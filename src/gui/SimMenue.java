package gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

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
	private PetriNetSimulation sim;
	private ActionListener listener;
	
	
	public SimMenue(ActionListener listener){
		this.listener = listener;
		start.setActionCommand("start");
		start.addActionListener(listener);
		stop.setActionCommand("stop");
		stop.addActionListener(listener);
		
		this.setLayout(new GridLayout(1,10));
		this.stop.setEnabled(false);
		this.add(start);
		this.add(stop);
		this.add(time);
		this.add(status);
		
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
	}
	
	public void setStatus(){
		
	}
	
}
