package graph.gui;

import graph.ChangedFlags;
import graph.GraphInstance;
import gui.MainWindowSingleton;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import petriNet.ContinuousTransition;
import biologicalElements.GraphElementAbstract;
import biologicalElements.Pathway;

public class ParameterWindow implements ActionListener {

	private JPanel panel;
	private JOptionPane pane;
	private JTextField name = new JTextField("");
	private GraphInstance graphInstance = new GraphInstance();
	private Pathway pw = graphInstance.getPathway();
	private JTextField value = new JTextField("");
	private JTextField unit = new JTextField("");
	private JButton add;
	private GraphElementAbstract gea;
	private FormularPanel fp;

	private JDialog dialog;

	// private HashMap<JButton, Parameter> parameters = new HashMap<JButton,
	// Parameter>();

	public ParameterWindow(GraphElementAbstract gea) {
		this.gea = gea;
		// System.out.println("constr.");

		MigLayout layout = new MigLayout("", "[left]");

		// DefaultComboBoxModel<String> dcbm = new
		// DefaultComboBoxModel<String>(ElementNamesSingelton.getInstance().getEnzymes());
		// elementNames.setEditable(true);
		// elementNames.setModel(dcbm);

		// elementNames.setMaximumSize(new Dimension(250,40));
		// elementNames.setSelectedItem(" ");
		// AutoCompleteDecorator.decorate(elementNames);

		if (gea instanceof ContinuousTransition) {

			fp = new FormularPanel(
					((ContinuousTransition) gea).getMaximumSpeed());
			fp.setVisible(true);

		}
		panel = new JPanel(layout);
		panel.add(fp);

		add = new JButton("Add");
		add.setActionCommand("add");
		add.addActionListener(this);

		// panel.add(value, "span,wrap 5,growx ,gaptop 2");

		// panel.add(new JLabel("Label"), "span 2, gaptop 2 ");
		// panel.add(elementNames, "span,wrap,growx ,gap 10, gaptop 2");
		// panel.add(new JSeparator(), "span, growx, wrap 10, gaptop 7 ");

		pane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE,
				JOptionPane.OK_CANCEL_OPTION);

		dialog = pane.createDialog(null, "Parameters");
		this.repaint();
		dialog.setResizable(true);
		dialog.setLocationRelativeTo(MainWindowSingleton.getInstance());
		dialog.pack();
		// dialog.show();
		dialog.setVisible(true);

	}

	private void listParameters() {
		Parameter p;
		panel.add(new JLabel("Name"), "span 1, gaptop 2");
		panel.add(new JLabel("Value"), "span 1, gapright 4");
		panel.add(new JLabel("Unit"), "span 1, gapright 4, wrap");

		for (int i = 0; i < gea.getParameters().size(); i++) {
			p = gea.getParameters().get(i);
			panel.add(new JLabel(p.getName()), "span 1, gaptop 2");

			panel.add(new JLabel(p.getValue() + ""), "span 1, gapright 4");

			panel.add(new JLabel(p.getUnit()), "span 1, gapright 4");

			JButton del = new JButton("Delete");
			del.setActionCommand("del" + i);

			del.addActionListener(this);

			JButton up = new JButton("u");
			up.setActionCommand("up" + i);
			up.addActionListener(this);

			JButton down = new JButton("d");
			down.setActionCommand("down" + i);
			down.addActionListener(this);
			// parameters.put(del, p);

			panel.add(del, "span 1");
			if (i == 0) {
				panel.add(down, "skip, span 1, wrap");
			} else if (i == gea.getParameters().size() - 1) {
				panel.add(up, "span 1, gapright 4");
			} else {
				panel.add(up, "span 1, gapright 4");
				panel.add(down, "span 1, wrap");
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// System.out.println(e.getActionCommand());
		if ("add".equals(e.getActionCommand())) {
			Parameter p;
			for (int i = 0; i < gea.getParameters().size(); i++) {
				p = gea.getParameters().get(i);
				if (p.getName().equals(name.getText())) {
					// System.out.println("schon vorhanden");
					return;
				}
			}
			p = new Parameter(name.getText(), Double.valueOf(value.getText()),
					unit.getText());
			gea.getParameters().add(p);
			pw.getChangedParameters().put(p, gea);
			pw.handleChangeFlags(ChangedFlags.PARAMETER_CHANGED);
			panel.add(new JLabel(name.getText()), "span 1, gaptop 2 ");
			panel.add(new JLabel(value.getText()), "span 1, gapright 4");
			panel.add(new JLabel(unit.getText()), "span 1, gapright 4, wrap");

			this.repaint();

		} else if (e.getActionCommand().startsWith("del")) {
			pw.handleChangeFlags(ChangedFlags.PARAMETER_CHANGED);
			int idx = Integer.parseInt(e.getActionCommand().substring(3));
			pw.getChangedParameters().remove(gea.getParameters().get(idx));
			this.gea.getParameters().remove(idx);
			this.repaint();
		} else if (e.getActionCommand().startsWith("down")) {
			int idx = Integer.parseInt(e.getActionCommand().substring(4));
			Parameter p = gea.getParameters().get(idx);
			gea.getParameters().set(idx, gea.getParameters().get(idx + 1));
			gea.getParameters().set(idx + 1, p);
			this.repaint();
		} else if (e.getActionCommand().startsWith("up")) {
			int idx = Integer.parseInt(e.getActionCommand().substring(2));
			Parameter p = gea.getParameters().get(idx);
			gea.getParameters().set(idx, gea.getParameters().get(idx - 1));
			gea.getParameters().set(idx - 1, p);

			this.repaint();
		}

	}

	private void repaint() {
		panel.removeAll();
		// parameters.clear();

		if (gea instanceof ContinuousTransition) {
			panel.add(fp, "span 20, wrap");
		}

		panel.add(new JLabel("Name"), "span 1, gaptop 2 ");
		panel.add(name, "span,wrap,growx ,gap 10, gaptop 2");

		panel.add(new JLabel("Value"), "span 1, gapright 4");
		panel.add(value, "span,wrap,growx ,gap 10, gaptop 2");

		panel.add(new JLabel("Unit"), "span 1, gapright 4");
		panel.add(unit, "span,wrap,growx ,gap 10, gaptop 2");

		panel.add(add, "wrap");
		this.listParameters();
		panel.repaint();
		// pane.repaint();
		// dialog.repaint();
		dialog.pack();
		dialog.setLocationRelativeTo(MainWindowSingleton.getInstance());
	}
}
