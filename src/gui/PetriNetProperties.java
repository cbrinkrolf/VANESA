package gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import petriNet.ConvertToPetriNet;
import petriNet.petriNetProperties;

public class PetriNetProperties implements ActionListener{

	private petriNetProperties props;
	
	private JPanel pMain;
	private JPanel pNorth;
	private JPanel pCenter;
	private JPanel pSouth;
	private JPanel p4;
	private JPanel p5;
	
	private JLabel lblDelay;
	private JLabel lblToken;
	private JLabel lblIterations;
	private JTextField txtDelay;
	private JTextField txtIterations;
	
	private JButton btnDelay;
	private JButton btnPlay;
	private JButton btnStop;
	private JButton btnToken;
	private JButton btnSingleStep;
	private JButton btnSingleIteration;
	private JButton btnIterations;
	
	private JTextField txtToken;
	
	public PetriNetProperties(){

		this.pMain = new JPanel();
		this.pNorth = new JPanel();
		this.pCenter = new JPanel();
		this.pSouth = new JPanel();
		this.p4 = new JPanel();
		this.p5 = new JPanel();
		
		this.lblDelay = new JLabel("Delay (ms): ");
		this.lblToken = new JLabel("Token per Node:");
		this.lblIterations = new JLabel("Iterations: ");
		this.btnDelay = new JButton("Apply");
		this.btnDelay.setActionCommand("Apply");
		this.btnDelay.addActionListener(this);
		this.btnSingleStep = new JButton("Perform single Step");
		this.btnSingleIteration = new JButton("Perform single Iteration");
		this.txtDelay = new JTextField();
		this.txtDelay.setText("1000");
		this.btnPlay = new JButton("Play");
		this.btnStop = new JButton("Stop");
		this.txtToken = new JTextField();
		this.txtToken.setText("30");
		this.btnToken = new JButton("Apply");
		this.txtIterations = new JTextField("100");
		this.btnIterations = new JButton("Perform");
		
		this.pNorth.add(this.lblDelay);
		this.pNorth.add(this.txtDelay, BorderLayout.CENTER);
		this.pNorth.add(this.btnDelay, BorderLayout.SOUTH);
		
		this.pCenter.add(this.lblToken);
		this.pCenter.add(this.txtToken);
		this.pCenter.add(this.btnToken);
		
		this.pSouth.add(this.btnPlay);
		this.pSouth.add(this.btnStop);
		
		this.p4.add(this.btnSingleStep);
		this.p4.add(this.btnSingleIteration);
		
		this.p5.add(this.lblIterations);
		this.p5.add(this.txtIterations);
		this.p5.add(this.btnIterations);
		this.pMain.setLayout(new GridLayout(5,1));
		//this.pMain.setLayout(new BorderLayout());
		//this.pMain.add(this.pNorth, BorderLayout.NORTH);
		//this.pMain.add(this.pCenter, BorderLayout.CENTER);
		//this.pMain.add(this.pSouth, BorderLayout.SOUTH);
		this.pMain.add(this.pNorth);
		this.pMain.add(this.pCenter);
		this.pMain.add(this.pSouth);
		this.pMain.add(this.p4);
		this.pMain.add(this.p5);
	}
	
	public JPanel getPanel() {
		this.pMain.setVisible(true);
		return this.pMain;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if(e.getSource() == this.btnDelay){
			this.props = ConvertToPetriNet.getInstance().getProp();
			this.props.setDelay(Integer.parseInt(this.txtDelay.getText()));
	//		System.out.println(this.props.getDelay());
		}
		
	}
}
