package gui.optionPanelWindows;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JCheckBox;
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
import util.MyColorChooser;

public class PathwayPropertiesWindow implements ActionListener, ItemListener {

	private JPanel p = new JPanel();
	private JCheckBox drawCompartments;
	private JCheckBox drawCompartmentsExperimental;
	private JButton createDefault;
	private JTextField name;
	private JButton color;
	private Pathway pw;

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
		this.pw = new GraphInstance().getPathway();
		MigLayout layout = new MigLayout("fillx", "[grow,fill]", "");
		p.setLayout(layout);
		drawCompartments = new JCheckBox("draw compartments");
		if (pw.getCompartmentManager().isDrawCompartments()) {
			drawCompartments.setSelected(true);
		}
		drawCompartments.setActionCommand("drawCompartments");
		drawCompartments.addItemListener(this);
		p.add(drawCompartments, "wrap");

		drawCompartmentsExperimental = new JCheckBox("draw experimental");
		drawCompartmentsExperimental.setActionCommand("drawCompartmentsExperimental");
		drawCompartmentsExperimental.addItemListener(this);
		drawCompartmentsExperimental.setEnabled(false);
		p.add(drawCompartmentsExperimental, "wrap");
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

		if (command.equals("createDefault")) {
			pw.getCompartmentManager().addDefaultCompartments();
			this.revalidateView();
		} else if (command.equals("add")) {
			// TODO check regEx for identifier
			String cName = name.getText().trim();
			if (cName.length() > 0) {
				// compartment name must start with a letter or dash and may only contain
				// letters, digites, and dash symbol (SBML L3V1, reference section 3.1.7)
				Pattern pattern = Pattern.compile("^[a-zA-Z-][a-zA-Z\\d-]*$");
				Matcher matcher = pattern.matcher(cName);
				if (matcher.find()) {
					if (pw.getCompartmentManager().getCompartment(cName) == null) {
						pw.getCompartmentManager().add(new Compartment(cName, color.getBackground()));
						this.revalidateView();
					} else {
						MyPopUp.getInstance().show("Error", "Name of new compartment is already in use!");
					}
				} else {
					MyPopUp.getInstance().show("Error",
							"Name of new compartment may only contain the following characters:\r\n [a-z], [A-Z], [0-9], [-], and must start with a letter or the dash symbol!");
				}
			} else {
				MyPopUp.getInstance().show("Error", "Name of new compartment must not be empty!");
			}

		} else if (command.equals("color")) {
			JButton b = ((JButton) e.getSource());
			// Color newColor =
			// JColorChooser.showDialog(MainWindow.getInstance().getFrame(), "Choose color",
			// b.getBackground());

			MyColorChooser mc = new MyColorChooser(MainWindow.getInstance().getFrame(), "Choose color", true,
					b.getBackground());
			if (mc.isOkAction()) {
				b.setBackground(mc.getColor());
			}

		} else if (command.startsWith("color_")) {
			String cName = command.substring(6);
			Compartment c = pw.getCompartmentManager().getCompartment(cName);
			if (c != null) {
				JButton b = ((JButton) e.getSource());
				// Color newColor =
				// JColorChooser.showDialog(MainWindow.getInstance().getFrame(), "Choose new
				// color",
				// b.getBackground());

				MyColorChooser mc = new MyColorChooser(MainWindow.getInstance().getFrame(), "Choose color", true,
						b.getBackground());
				if (mc.isOkAction()) {
					Color newColor = mc.getColor();
					b.setBackground(newColor);
					c.setColor(newColor);
				}
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
		MyVisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv = pw.getGraph()
				.getVisualizationViewer();
		if (e.getSource().equals(drawCompartments)) {
			if (e.getStateChange() == 1) {
				vv.setDrawCompartments(true);
				pw.getCompartmentManager().setDrawCompartments(true);
			} else {
				vv.setDrawCompartments(false);
				pw.getCompartmentManager().setDrawCompartments(false);
			}
		} else if (e.getSource().equals(drawCompartmentsExperimental)) {
			if (e.getStateChange() == 1) {
				vv.setEsperimentalCompartments(true);
			} else {
				vv.setEsperimentalCompartments(false);
			}
		}
	}
}
