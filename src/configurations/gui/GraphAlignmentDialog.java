package configurations.gui;

import graph.algorithms.alignment.StartRserve;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

import blast.AllAgainstAll;
import configurations.GraphAlignmentSettings;

public class GraphAlignmentDialog{

	private JPanel panel;
	private JComboBox<String> chooseBLASTlocation;
	
	private JTextField blastServerURL;
	private JLabel statusBLASTserver;
	
	private JComboBox<String> chooseMNAlignerLocation;
	
	private JTextField rServerURL;
	private JLabel statusRserver;
	
	private boolean statusR;
	private boolean statusBLAST;
	
	
	public GraphAlignmentDialog(){
		
		MigLayout layout = new MigLayout("", "[left]");
		
		panel = new JPanel(layout);
		
		panel.add(new JLabel("Similarity Function"), "span 4");
		panel.add(new JSeparator(), "span, growx, wrap 5, gaptop 10, gap 5");

		panel.add(new JLabel("BLAST Location"), "span 2, gap 10, gaptop 2 ");
		chooseBLASTlocation = new JComboBox<String>(GraphAlignmentSettings.blastLocations);
		chooseBLASTlocation.setSelectedIndex(GraphAlignmentSettings.blastLocation);
		chooseBLASTlocation.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				updateBLASTserverStatus();
				String event = (String) chooseBLASTlocation.getSelectedItem();
				if(event.equals(GraphAlignmentSettings.blastLocations[0]))
					blastServerURL.setEditable(true);
				else
					blastServerURL.setEditable(false);
			}
		});
		panel.add(chooseBLASTlocation, "span,wrap,growx ,gap 10, gaptop 2");
		
		panel.add(new JLabel("BLAST Server URL"), "span 2, gap 10, gaptop 2 ");
		blastServerURL = new JTextField(20);
		blastServerURL.setText(GraphAlignmentSettings.getBlastWebServerURL());
		if (!(GraphAlignmentSettings.blastLocation == 0)) {
			blastServerURL.setEditable(false);
		}
		panel.add(blastServerURL, "span,wrap 35 ,growx ,gap 10, gaptop 2");
		
		panel.add(new JLabel("Algorithm Options"), "span 4");
		panel.add(new JSeparator(), "span, growx, wrap 5, gaptop 10, gap 5");
		
		panel.add(new JLabel("Algorithm Location"), "span 2, gap 10, gaptop 2 ");
		chooseMNAlignerLocation = new JComboBox<String>(GraphAlignmentSettings.mnAlignerLocations);
		chooseMNAlignerLocation.setSelectedIndex(GraphAlignmentSettings.mnalignerLocation);
		chooseMNAlignerLocation.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				updateRserverStatus();
				String event = (String) chooseMNAlignerLocation.getSelectedItem();
				if(event.equals(GraphAlignmentSettings.mnAlignerLocations[0]))
					rServerURL.setEditable(true);
				else
					rServerURL.setEditable(false);
			}
		});
		panel.add(chooseMNAlignerLocation, "span,wrap,growx ,gap 10, gaptop 2");
		
		panel.add(new JLabel("Algorithm Server URL"), "span 2, gap 10, gaptop 2 ");
		rServerURL = new JTextField(20);
		rServerURL.setText(GraphAlignmentSettings.getRWebServerURL());
		if (GraphAlignmentSettings.mnalignerLocation != 0) {
			rServerURL.setEditable(false);
		}
		panel.add(rServerURL, "span,wrap 35 ,growx ,gap 10, gaptop 2");
		
		statusBLASTserver = new JLabel("");
		updateBLASTserverStatus();
		statusBLASTserver.setForeground(Color.RED);
		panel.add(statusBLASTserver, "span, growx, gap 10 ,gaptop 15,wrap 15");
		
		statusRserver = new JLabel("");
		updateRserverStatus();
		statusRserver.setForeground(Color.RED);
		panel.add(statusRserver, "span, growx, gap 10 ,gaptop 15,wrap 15");
		
	}
	
	public boolean applyDefaults(){
		
		chooseBLASTlocation.setSelectedIndex(0);
		GraphAlignmentSettings.setBlastWebServerURL(GraphAlignmentSettings.defaultBlastWebServerURL);
		blastServerURL.setText(GraphAlignmentSettings.getBlastWebServerURL());
		updateBLASTserverStatus();
		
		chooseMNAlignerLocation.setSelectedIndex(0);
		GraphAlignmentSettings.setRWebServerURL(GraphAlignmentSettings.defaultRWebServerURL);
		rServerURL.setText(GraphAlignmentSettings.getRWebServerURL());
		updateRserverStatus();
		
		return checkSettings();
		
	}
	
	public boolean applyNewSettings(){
		
		GraphAlignmentSettings.setBlastLocation(chooseBLASTlocation.getSelectedIndex());
		GraphAlignmentSettings.setBlastWebServerURL(blastServerURL.getText());
		
		GraphAlignmentSettings.setMnalignerLocation(chooseMNAlignerLocation.getSelectedIndex());
		GraphAlignmentSettings.setRWebServerURL(rServerURL.getText());
		
		return checkSettings();
		
	}
	
	private boolean checkSettings() {
		
		return (statusBLAST && statusR);
	}

	private void updateBLASTserverStatus(){
		
		switch (chooseBLASTlocation.getSelectedIndex()) {
		case 0:
			// check BLAST server
			statusBLASTserver.setText("BLAST server not available");
			statusBLAST =  false;
			break;
		case 1:
			if (!AllAgainstAll.checkBLASTinstallation()) {
				statusBLASTserver.setText("No local BLAST detected. Please see help for installation guide");
				statusBLAST =  false;
			}else{
				statusBLASTserver.setText("");
				statusBLAST =  true;
			}
			break;
		}
	}

	private void updateRserverStatus(){
		
		switch (chooseMNAlignerLocation.getSelectedIndex()) {
		case 0:
			// check R server
			statusRserver.setText("No remote algorithm server available");
			statusR = false;
			break;
		case 1:
			statusRserver.setText("");
			new Thread(){
				@Override
				public void run() {
					if(StartRserve.checkLocalRserve()){
						try {
							RConnection c = new RConnection();
							String checkString = "";
							try {
								checkString = c.eval("R.version.string").asString();
							} catch (REXPMismatchException e) {
								e.printStackTrace();
							}
							c.shutdown();
							statusRserver.setText(checkString);
							statusR = true;
						} catch (RserveException e) {
							e.printStackTrace();
							statusRserver.setText("Problems with your local R/Rserve installation detected. Please see help for installation guide");
							statusR = false;
						}
					}else{
						statusRserver.setText("No local R/Rserve detected. Please see help for installation guide");
						statusR = false;
					}
				}
			}.start();
			break;
		case 2:
			statusRserver.setText("Do not use Java Algorithm for large Networks.");
			statusR = true;
			break;
		}
		
	}
	
	public JPanel getPanel(){
		return panel;
	}


}
