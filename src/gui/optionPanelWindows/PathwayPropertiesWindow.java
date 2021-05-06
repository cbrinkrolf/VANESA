package gui.optionPanelWindows;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import graph.GraphInstance;
import graph.Compartment.Compartment;
import graph.jung.classes.MyVisualizationViewer;
import gui.MainWindow;
import gui.MyPopUp;
import net.miginfocom.swing.MigLayout;

public class PathwayPropertiesWindow implements ActionListener, ItemListener {

	private JPanel p = new JPanel();
	private JCheckBox drawCompartments;
	private JButton createDefault;
	private JTextField name;
	private JButton color;

	public PathwayPropertiesWindow() {

	}

	public JPanel getPanel() {
		p.setVisible(false);
		return p;
	}

	public void removeAllElements() {
		p.removeAll();
		p.setVisible(false);
	}

	public void revalidateView() {
		p.removeAll();
		MigLayout layout = new MigLayout("fillx", "[grow,fill]", "");
		p.setLayout(layout);
		drawCompartments = new JCheckBox("draw compartments");
		drawCompartments.setActionCommand("drawCompartments");
		drawCompartments.addItemListener(this);
		p.add(drawCompartments, "wrap");
		// p.add(new JLabel("draw compartments"));

		createDefault = new JButton("create default");
		createDefault.setActionCommand("createDefault");
		createDefault.addActionListener(this);

		p.add(createDefault, "wrap");

		p.add(new JSeparator(), "span, growx, gaptop 7 ");
		p.add(new JLabel("add new compartment:"), "wrap");
		name = new JTextField(10);
		p.add(name);
		color = new JButton("color");
		color.setBackground(new Color(125, 125, 125));
		color.setToolTipText("Select fill color");
		color.setActionCommand("color");
		color.addActionListener(this);
		p.add(color);

		JButton add = new JButton("add");
		add.setActionCommand("add");
		add.addActionListener(this);
		p.add(add, "wrap");

		p.add(new JSeparator(), "span, growx, gaptop 7 ");
		drawList();
		p.revalidate();
		p.setVisible(true);

	}

	private void drawList() {
		Pathway pw = new GraphInstance().getPathway();
		List<Compartment> compartments = pw.getCompartmentManager().getAllCompartmentsAlphabetically();

		for (Compartment c : compartments) {
			p.add(new JLabel(c.getName()));
			JButton color = new JButton("color");
			color.setBackground(c.getColor());
			color.setActionCommand("color_" + c.getName());
			color.addActionListener(this);
			p.add(color);
			JButton del = new JButton("delete");
			del.setActionCommand("del_" + c.getName());
			del.addActionListener(this);
			p.add(del, "wrap");

		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		Pathway pw = new GraphInstance().getPathway();

		if (command.equals("createDefault")) {
			pw.getCompartmentManager().addDefaultCompartments();
			this.revalidateView();
		} else if (command.equals("add")) {
			String cName = name.getText().trim();
			if (cName.length() > 0) {
				if (pw.getCompartmentManager().getCompartment(cName) == null) {
					pw.getCompartmentManager().add(new Compartment(cName, color.getBackground()));
					this.revalidateView();
				} else {
					MyPopUp.getInstance().show("Error", "Name of new compartment is already in use!");
				}
			} else {
				MyPopUp.getInstance().show("Error", "Name of new compartment must not be empty!");
			}

		} else if (command.equals("color")) {
			JButton b = ((JButton) e.getSource());
			Color newColor = JColorChooser.showDialog(MainWindow.getInstance().getFrame(), "Choose color",
					b.getBackground());
			b.setBackground(newColor);
		} else if (command.startsWith("color_")) {
			String cName = command.substring(6);
			Compartment c = pw.getCompartmentManager().getCompartment(cName);
			if (c != null) {
				JButton b = ((JButton) e.getSource());
				Color newColor = JColorChooser.showDialog(MainWindow.getInstance().getFrame(), "Choose new color",
						b.getBackground());

				b.setBackground(newColor);
				c.setColor(newColor);
			}
		} else if (command.startsWith("del_")) {
			String cName = command.substring(4);
			Compartment c = pw.getCompartmentManager().getCompartment(cName);
			if (c != null) {
				pw.getCompartmentManager().remove(c);
				revalidateView();
			}
		}
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		// TODO Auto-generated method stub
		Pathway pw = new GraphInstance().getPathway();
		MyVisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv = pw.getGraph()
				.getVisualizationViewer();
		if (e.getStateChange() == 1) {
			vv.setDrawCompartments(true);
		} else {
			vv.setDrawCompartments(false);
		}

	}
}
