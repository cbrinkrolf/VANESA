package gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


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
	private JPanel north = new JPanel();
	private JPanel northUp = new JPanel();
	private JPanel northDown = new JPanel();
	private JScrollPane scrollPane = new JScrollPane(textArea);
	private JLabel startLbl = new JLabel("Start:");
	private JLabel stopLbl = new JLabel("Stop:");
	private JLabel intervalsLbl = new JLabel("Intervals:");
	private JFormattedTextField startTxt;
	private JFormattedTextField stopTxt;
	private JFormattedTextField intervalsTxt;

	//private ActionListener listener;
	
	
	public SimMenue(ActionListener listener){
		//this.listener = listener;
		start.setActionCommand("start");
		start.addActionListener(listener);
		stop.setActionCommand("stop");
		stop.addActionListener(listener);

		
		DecimalFormat df = new DecimalFormat();
		
		DecimalFormatSymbols dfs = new DecimalFormatSymbols();
		dfs.setDecimalSeparator('.');
		
		df.setDecimalFormatSymbols(dfs);
		new JFormattedTextField(df);
		startTxt = new JFormattedTextField(df);
		startTxt.setText("0.0");
		startTxt.setColumns(5);
		startTxt.setEnabled(false);
		
		stopTxt = new JFormattedTextField(df);
		stopTxt.setText("1.0");
		stopTxt.setColumns(5);

		
		intervalsTxt = new JFormattedTextField(NumberFormat.getInstance());
		intervalsTxt.setText("100");
		intervalsTxt.setColumns(5);
		//intervalsTxt.get
		//nf.parse(source)
		textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		
		//northUp.setLayout(new GridLayout(1,5));
		//northDown.setLayout();
		
		
		this.setLayout(new BorderLayout());
		this.stop.setEnabled(false);
		northUp.add(start);
		northUp.add(stop);
		northUp.add(time);
		northUp.add(status);
		northDown.add(startLbl);
		northDown.add(startTxt);
		northDown.add(stopLbl);
		northDown.add(stopTxt);
		northDown.add(intervalsLbl);
		northDown.add(intervalsTxt);
		
		this.add(north, BorderLayout.NORTH);
		north.setLayout(new GridLayout(2,1));
		north.add(northUp);
		north.add(northDown);
		
		//textArea.setAutoscrolls(true);
		this.add(scrollPane, BorderLayout.SOUTH);
		// this.add(textArea, BorderLayout.SOUTH);
		
		this.pack();
		this.setLocationRelativeTo(null);
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
	
	public double getStartValue(){
		return Double.parseDouble(startTxt.getText());
	}
	
	public double getStopValue(){
		return Double.parseDouble(stopTxt.getText());
	}
	
	public int getIntervals(){
		
		try {
			return NumberFormat.getInstance().parse(intervalsTxt.getText()).intValue();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 100;
	}
	
}
