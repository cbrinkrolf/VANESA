package gui;

import graph.ContainerSingelton;
import gui.algorithms.ScreenSize;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import net.miginfocom.swing.MigLayout;
import biologicalElements.Pathway;

public abstract class ChooseGraphsWindow extends JFrame implements ActionListener {
	private static final long serialVersionUID = -4040410943988968952L;
	private JDialog dialog;
	JOptionPane optionPane;
	JPanel panel = new JPanel();
	
	private ArrayList<JCheckBox> checkboxes = new ArrayList<JCheckBox>();
	
	public ChooseGraphsWindow(String title) {
			dialog = new JDialog(this, title, false);
			dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			
			optionPane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE);
			JButton create = new JButton("OK");
			create.addActionListener(this);
			JButton cancel = new JButton("cancel");
			cancel.addActionListener(this);
			JButton[] buttons = {create, cancel};
			
			optionPane.setOptions(buttons);
			dialog.setContentPane(optionPane);
			
			
			panel.setLayout(new GridLayout(0,1));
			panel.add(new JLabel("Please choose graphs!")
					);
			Collection<Pathway> pathways = ContainerSingelton.getInstance().getAllPathways();
			//Collections.reverse(new ArrayList(pathways));
			for (Object o : pathways) {
				Pathway p = (Pathway) o;
				JCheckBox cb = new JCheckBox(p.getName());
				this.checkboxes.add(cb);
				panel.add(cb);
			}
			
			MigLayout layout = new MigLayout("", "[grow][grow]", "");
			this.setLayout(layout);
			
			dialog.pack();
			ScreenSize screen = new ScreenSize();
			int screenHeight = (int) screen.getheight();
			int screenWidth = (int) screen.getwidth();
			dialog.setLocation((screenWidth / 2) - dialog.getSize().width / 2,
					(screenHeight / 2) - dialog.getSize().height / 2);
			dialog.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String event = e.getActionCommand();
		if ("cancel".equals(event)) {
			dialog.setVisible(false);
		} else if ("OK".equals(event)) {
			//System.out.println("chosen graphs: ");
			//get the names of chosen graphs
			HashSet<String> names = new HashSet<String>();
			for(JCheckBox cb : this.checkboxes) {
				if (cb.isSelected()) {
					//System.out.println(cb.getText());
					names.add(cb.getText());
				}
			}
			
			ArrayList<Pathway> chosen_pathways = new ArrayList<Pathway>();
			Collection<Pathway> pathways = ContainerSingelton.getInstance().getAllPathways();
			for (Object o : pathways) {
				Pathway p = (Pathway) o;
				if (names.contains(p.getName())) {
					chosen_pathways.add(p);
				}
			}
			
			if (chosen_pathways.size()<2) {
				JOptionPane.showMessageDialog(MainWindowSingleton.getInstance(),
				"Please select at least 2 graphs!");
			}
			else {		
				dialog.setVisible(false);
				dialog.dispose();
				this.handleChosenGraphs(chosen_pathways);
				
			}
			
			
		}
	}
	
	public abstract void handleChosenGraphs(ArrayList<Pathway> pathways);
}
