package gui;

import graph.GraphInstance;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import petriNet.SimulationResult;
import util.MyJFormattedTextField;
import util.MyNumberFormat;
import biologicalElements.Pathway;

public class SimMenue extends JFrame implements ActionListener, ItemListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JButton start = new JButton("Start");
	private JButton stop = new JButton("Stop");
	private JLabel status = new JLabel("");
	private JLabel time = new JLabel("Time: -");
	private JTextArea textArea = new JTextArea(20, 80);
	private JPanel north = new JPanel();
	private JPanel northUp = new JPanel();
	private JPanel northDown = new JPanel();
	private JScrollPane scrollPane = new JScrollPane(textArea);
	private JLabel startLbl = new JLabel("Start:");
	private JLabel stopLbl = new JLabel("Stop:");
	private JLabel intervalsLbl = new JLabel("Intervals:");
	private MyJFormattedTextField startTxt;
	private MyJFormattedTextField stopTxt;
	private MyJFormattedTextField intervalsTxt;
	private JLabel integratorsLbl = new JLabel("Integartor:");
	private JComboBox<String> integrators;
	private JLabel simLibLbl = new JLabel("Simlation library");
	private JComboBox<String> simLibs;
	private JPanel west = new JPanel();
	private JCheckBox forceRebuild = new JCheckBox("force rebuild");

	// private ActionListener listener;

	private List<File> libs;

	public SimMenue(ActionListener listener, List<File> libs) {
		this.libs = libs;
		// this.listener = listener;
		start.setActionCommand("start");
		start.addActionListener(listener);
		stop.setActionCommand("stop");
		stop.addActionListener(listener);

		startTxt = new MyJFormattedTextField(MyNumberFormat.getDecimalFormat());
		startTxt.setText("0.0");
		startTxt.setColumns(5);
		startTxt.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		startTxt.setEnabled(false);

		stopTxt = new MyJFormattedTextField(MyNumberFormat.getDecimalFormat());
		stopTxt.setText("1.0");
		stopTxt.setColumns(5);
		stopTxt.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);

		intervalsTxt = new MyJFormattedTextField(
				MyNumberFormat.getIntegerFormat());
		intervalsTxt.setText("100");
		intervalsTxt.setColumns(5);
		intervalsTxt.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		// intervalsTxt.get
		// nf.parse(source)
		textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

		// northUp.setLayout(new GridLayout(1,5));
		// northDown.setLayout();
		integrators = new JComboBox<String>();
		integrators.setSelectedItem("dassl");
		AutoCompleteDecorator.decorate(integrators);
		integrators.addItem("dassl");
		integrators.addItem("euler");
		integrators.addItem("lobatto2");
		integrators.addItem("lobatto4");
		integrators.addItem("lobatto6");
		integrators.addItem("radau5");
		integrators.addItem("radau3");
		integrators.addItem("radau1");
		integrators.addItem("rungekutta");

		simLibs = new JComboBox<String>();
		Iterator<File> it = libs.iterator();
		while (it.hasNext()) {
			simLibs.addItem(it.next().getName());
		}
		simLibs.setSelectedIndex(0);

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
		northDown.add(integratorsLbl);
		northDown.add(integrators);
		northDown.add(simLibLbl);
		northDown.add(simLibs);
		northDown.add(forceRebuild);

		this.add(north, BorderLayout.NORTH);
		north.setLayout(new GridLayout(2, 1));
		north.add(northUp);
		north.add(northDown);
		west.setLayout(new MigLayout());

		this.updateSimulationResults();

		// textArea.setAutoscrolls(true);
		this.add(scrollPane, BorderLayout.CENTER);
		// this.add(textArea, BorderLayout.SOUTH);
		this.add(west, BorderLayout.WEST);
		this.pack();
		// this.setLocation(w.getLocation());
		this.setLocationRelativeTo(MainWindowSingleton.getInstance());
		// this.setLocationRelativeTo(null);
		this.setVisible(true);

	}

	public void started() {
		start.setEnabled(false);
		stop.setEnabled(true);
		this.setTime("-");
	}

	public void stopped() {
		this.updateSimulationResults();
		start.setEnabled(true);
		stop.setEnabled(false);
	}

	public void setTime(String time) {
		this.time.setText("Time: " + time);
		this.time.repaint();
	}

	public void setStatus() {

	}

	public void addText(String text) {
		this.textArea.setText(textArea.getText() + text);
		this.pack();
	}

	public double getStartValue() {
		Number number = (Number) startTxt.getValue();
		if (number != null) {
			return number.doubleValue();
		}
		return 0.0;
	}

	public double getStopValue() {
		Number number = (Number) stopTxt.getValue();
		if (number != null) {
			return number.doubleValue();
		}
		return 1.0;
	}

	public int getIntervals() {
		Number number = (Number) intervalsTxt.getValue();
		if (number != null) {
			return number.intValue();
		}
		return 100;
	}

	public String getIntegrator() {
		return (String) this.integrators.getSelectedItem();
	}

	public void setLibs(List<File> libs) {
		this.libs = libs;
	}

	public File getSimLib() {
		return this.libs.get(this.simLibs.getSelectedIndex());
	}

	public void updateSimulationResults() {
		west.removeAll();

		west.add(new JSeparator(), "growx, span, wrap");

		JCheckBox all = new JCheckBox();
		all.setActionCommand("-1");
		all.addItemListener(this);

		west.add(all);
		west.add(new JLabel("delete"));
		west.add(new JLabel("Name"), "wrap");
		// west.add(new JButton("click"), "wrap");
		// west.add(new JButton("click"), "wrap");
		// west.add(new JButton("click"), "wrap");

		Pathway pw = new GraphInstance().getPathway();

		List<SimulationResult> results = pw.getSimResController().getAll();

		for (int i = 0; i < results.size(); i++) {
			JCheckBox box = new JCheckBox();
			box.setActionCommand(i + "");
			box.addItemListener(this);
			box.setSelected(results.get(i).isActive());
			west.add(box);
			JButton button = new JButton("del");
			button.addActionListener(this);
			button.setActionCommand(i + "");
			// System.out.println("name: "+results.get(i).getName());
			west.add(button);
			west.add(new JLabel(results.get(i).getName()), "wrap");
		}
		this.pack();
		west.repaint();
		// this.setVisible(true);

	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		// TODO Auto-generated method stub
		if (e.getItem() instanceof JCheckBox) {

			JCheckBox box = (JCheckBox) e.getItem();
			int i = Integer.parseInt(box.getActionCommand());
			// System.out.println(i);
			if (i >= 0) {
				Pathway pw = new GraphInstance().getPathway();
				pw.getSimResController().getAll().get(i)
						.setActive(box.isSelected());
			} else {
				Component[] components = west.getComponents();
				for(int j = 0; j<components.length; j++){
					if(components[j] instanceof JCheckBox){
						((JCheckBox)components[j]).setSelected(box.isSelected());
					}
				}
				List<SimulationResult> resList = new GraphInstance()
						.getPathway().getSimResController().getAll();
				for (int j = 0; j < resList.size(); j++) {
					resList.get(j).setActive(box.isSelected());
				}
				//this.updateSimulationResults();
			}

		}
		// System.out.println(((JCheckBox)e.getItem()).getActionCommand());

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof JButton) {
			int i = Integer.parseInt(e.getActionCommand());
			new GraphInstance().getPathway().getSimResController().remove(i);
			this.updateSimulationResults();
		}

	}
	
	private void updateList(){
		
		
	}
	
	public boolean isForceRebuild(){
		return forceRebuild.isSelected();
	}

}
