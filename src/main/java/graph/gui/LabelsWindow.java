package graph.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import biologicalElements.GraphElementAbstract;
import gui.MainWindow;
import net.miginfocom.swing.MigLayout;

public class LabelsWindow implements ActionListener{

	
	private JPanel panel;
	private JOptionPane pane;
	private JTextField label = new JTextField("");
	private JButton add;
	private GraphElementAbstract gea;
	
	private JDialog dialog;
	//private HashMap<JButton, Parameter> parameters = new HashMap<JButton, Parameter>();

	
	public LabelsWindow(GraphElementAbstract gea) {
		this.gea = gea;
		//System.out.println("constr.");
		
		MigLayout layout = new MigLayout("", "[left]");
		
		//DefaultComboBoxModel<String> dcbm = new DefaultComboBoxModel<String>(ElementNamesSingelton.getInstance().getEnzymes());
		//elementNames.setEditable(true);
		//elementNames.setModel(dcbm);
		
		//elementNames.setMaximumSize(new Dimension(250,40));
		//elementNames.setSelectedItem(" ");
		//AutoCompleteDecorator.decorate(elementNames);
		
		panel = new JPanel(layout);
		add = new JButton("Add");
		add.setActionCommand("add");
		add.addActionListener(this);
		label.setPreferredSize(new Dimension(100, 10));
		
		//panel.add(value, "span,wrap 5,growx ,gaptop 2");
		
		//panel.add(new JLabel("Label"), "span 2, gaptop 2 ");
		//panel.add(elementNames, "span,wrap,growx ,gap 10, gaptop 2");
		//panel.add(new JSeparator(), "span, growx, wrap 10, gaptop 7 ");

		
	
		pane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE,
				JOptionPane.OK_CANCEL_OPTION);
		
		dialog = pane.createDialog(MainWindow.getInstance().getFrame(), "Labels");
		this.repaint();
		dialog.pack();
		//dialog.show();
		dialog.setVisible(true);
		
		

	}
	
	private void listParameters(){
		panel.add(new JLabel("Labels:"), "span 2, wrap, gaptop 2");
		Iterator<String> it = gea.getLabelSet().iterator();
		while(it.hasNext()){
			panel.add(new JLabel(it.next()), "wrap");
		}
		
		
		
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
		//System.out.println(e.getActionCommand());
		if("add".equals(e.getActionCommand())){
			gea.addLabel(label.getText());
			label.setText("");
			this.repaint();
		}
	}
	
	private void repaint(){
		panel.removeAll();
		//parameters.clear();
		
		panel.add(new JLabel("Label"));
		panel.add(label, "wrap");
		
		panel.add(add, "wrap");
		this.listParameters();
		panel.repaint();
		//pane.repaint();
		//dialog.repaint();
		dialog.pack();
	}
}
