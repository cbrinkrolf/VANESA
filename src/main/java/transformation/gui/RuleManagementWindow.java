package transformation.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import gui.MainWindow;
import io.SaveDialog;
import net.miginfocom.swing.MigLayout;
import transformation.Rule;
import transformation.RuleManager;

public class RuleManagementWindow implements ActionListener, ItemListener {

	private JFrame frame;
	private JPanel panel;
	private JButton add;
	private JButton save;
	private JButton load;
	private JButton removeAll;

	private JButton cancel = new JButton("cancel");
	private JButton okButton = new JButton("ok");
	private JButton[] buttons = { okButton, cancel };
	private JOptionPane optionPane;

	private RuleManager rm;
	private List<Rule> rules;

	private Rule newRule = null;
	private Rule modRule = null;

	private HashMap<Rule, RuleEditingWindow> rule2Window = new HashMap<>();

	private static RuleManagementWindow intance = null;
	// private JDialog dialog;

	// private HashMap<JButton, Parameter> parameters = new HashMap<JButton,
	// Parameter>();

	private RuleManagementWindow() {

	}

	public static synchronized RuleManagementWindow getInstance() {
		if (RuleManagementWindow.intance == null) {
			RuleManagementWindow.intance = new RuleManagementWindow();
		}
		return RuleManagementWindow.intance;
	}

	public void show() {
		frame = new JFrame("Rule Management");
		frame.setIconImages(MainWindow.getInstance().getFrame().getIconImages());
		// System.out.println("constr.");
		rm = RuleManager.getInstance();
		rules = rm.getRules();

		MigLayout layout = new MigLayout("", "[left]");

		// DefaultComboBoxModel<String> dcbm = new
		// DefaultComboBoxModel<String>(ElementNamesSingelton.getInstance().getEnzymes());
		// elementNames.setEditable(true);
		// elementNames.setModel(dcbm);

		// elementNames.setMaximumSize(new Dimension(250,40));
		// elementNames.setSelectedItem(" ");
		// AutoCompleteDecorator.decorate(elementNames);

		panel = new JPanel(layout);

		add = new JButton("add new Rule");
		add.setActionCommand("add");
		add.addActionListener(this);

		save = new JButton("Save rules to file");
		save.setActionCommand("saveRules");
		save.addActionListener(this);

		load = new JButton("Load rules from file");
		load.setToolTipText("load and add rules from file");
		load.setActionCommand("loadRules");
		load.addActionListener(this);

		removeAll = new JButton("Remove all rules");
		removeAll.setActionCommand("removeAll");
		removeAll.addActionListener(this);

		// pane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE,
		// JOptionPane.DEFAULT_OPTION);

		cancel.addActionListener(this);
		cancel.setActionCommand("cancel");

		okButton.addActionListener(this);
		okButton.setActionCommand("okButton");

		this.repaintPanel();

		optionPane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE);
		optionPane.setOptions(buttons);

		frame.setAlwaysOnTop(false);
		frame.setContentPane(optionPane);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setIconImages(MainWindow.getInstance().getFrame().getIconImages());
		frame.revalidate();

		frame.pack();

		frame.setLocationRelativeTo(MainWindow.getInstance().getFrame());
		frame.requestFocus();
		frame.setVisible(true);

		frame.addWindowFocusListener(new WindowFocusListener() {

			@Override
			public void windowLostFocus(WindowEvent e) {
			}

			@Override
			public void windowGainedFocus(WindowEvent e) {
				// repaintPanel();
				frame.pack();
				// revalidate();
				// pack();
				// repaint();
			}
		});
	}

	private void listRules() {
		// panel.add(new JLabel("Unit"), "span 1, gapright 4, wrap");

		Rule rule;
		int maxSpaces = (int) Math.floor(Math.log10(rules.size() + 1));
		int spacesUsed;
		String boxLbl;
		JCheckBox box;
		for (int i = 0; i < this.rules.size(); i++) {
			rule = rules.get(i);

			boxLbl = i + 1 + ".";
			spacesUsed = (int) Math.floor(Math.log10(i + 1));

			for (int j = 0; j < maxSpaces - spacesUsed; j++) {
				boxLbl += "  ";
			}

			//System.out.println(i + 1 + "added spaces: " + (maxSpaces - spacesUsed));
			box = new JCheckBox();
			box.setActionCommand(i + "");
			box.addItemListener(this);
			box.setSelected(rule.isActive());
			box.setHorizontalTextPosition(SwingConstants.LEFT);
			box.setText(boxLbl);
			panel.add(box, "span 1, gaptop 2");

			panel.add(new JLabel(rule.getName()), "span 1, gaptop 2");

			// panel.add(new JLabel(p.getValue() + ""), "span 1, gapright 4");

			// panel.add(new JLabel(p.getUnit()), "span 1, gapright 4");

			JButton edit = new JButton("✎");
			edit.setActionCommand("edit" + i);
			edit.addActionListener(this);
			edit.setToolTipText("edit rule");
			edit.setMaximumSize(edit.getMinimumSize());

			JButton del = new JButton("✖");
			del.setBackground(Color.RED);
			del.setActionCommand("del" + i);
			del.setToolTipText("delete rule");

			del.addActionListener(this);

			JButton up = new JButton("↑");
			up.setActionCommand("up" + i);
			up.addActionListener(this);
			up.setToolTipText("move up");

			JButton down = new JButton("↓");
			down.setActionCommand("down" + i);
			down.addActionListener(this);
			down.setToolTipText("move down");

			if (rules.size() > 1) {
				if (i == 0) {
					panel.add(down, "skip, span 1");
				} else if (i == rules.size() - 1) {
					panel.add(up, "span 1, gapright 4");
				} else {
					panel.add(up, "span 1, gapright 4");
					panel.add(down, "span 1");
				}
			}
			if (i == rules.size() - 1) {
				panel.add(edit, "skip, span 1");
			} else {
				panel.add(edit, "span 1");
			}
			if (rules.size() == 1) {
				panel.add(del, "wrap");
			} else {
				panel.add(del, "span 1, wrap");
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// System.out.println(e.getActionCommand());
		if (e.getActionCommand().equals("add")) {
			newRule = new Rule();
			new RuleEditingWindow(newRule, null, this);
		} else if (e.getActionCommand().equals("removeAll")) {
			rules.clear();
			this.repaintPanel();
		} else if (e.getActionCommand().equals("loadRules")) {
			OpenTransformationRules op = new OpenTransformationRules(this);
			op.execute();
		} else if (e.getActionCommand().startsWith("del")) {
			int idx = Integer.parseInt(e.getActionCommand().substring(3));
			rules.remove(idx);
			this.repaintPanel();
		} else if (e.getActionCommand().startsWith("down")) {
			int idx = Integer.parseInt(e.getActionCommand().substring(4));
			Rule r = rules.get(idx);
			rules.set(idx, rules.get(idx + 1));
			rules.set(idx + 1, r);
			this.repaintPanel();
		} else if (e.getActionCommand().startsWith("up")) {
			int idx = Integer.parseInt(e.getActionCommand().substring(2));
			Rule r = rules.get(idx);
			rules.set(idx, rules.get(idx - 1));
			rules.set(idx - 1, r);
			this.repaintPanel();
		} else if (e.getActionCommand().startsWith("edit")) {
			int idx = Integer.parseInt(e.getActionCommand().substring(4));
			// new RuleEditingWindow(this.rules.get(idx));
			modRule = new Rule();
			rule2Window.put(modRule, new RuleEditingWindow(this.rules.get(idx), modRule, this));
		} else if (e.getActionCommand().equals("okButton")) {
			frame.setVisible(false);
		} else if (e.getActionCommand().equals("cancel")) {
			frame.setVisible(false);
		} else if (e.getActionCommand().equals("okButtonRE")) {
			if (newRule != null) {
				rules.add(newRule);
			}
			newRule = null;
			modRule = null;
			this.repaintPanel();
		} else if (e.getActionCommand().equals("saveCopy")) {
			if (modRule != null) {
				rules.add(modRule);
				modRule = null;
			}
			newRule = null;
			this.repaintPanel();
		} else if (e.getActionCommand().equals("cancelRE")) {
			newRule = null;
		} else if (e.getActionCommand().equals("saveRules")) {
			new SaveDialog(SaveDialog.FORMAT_YAML, SaveDialog.DATA_TYPE_TRANSFORMATION_RULES);
		}
	}

	public void repaintPanel() {
		panel.removeAll();

		panel.add(load, "");
		panel.add(save, "");
		panel.add(removeAll, "span,wrap");
		panel.add(add, "span");

		panel.add(new JLabel("List of all loaded rules"), "span,wrap");
		panel.add(new JSeparator(), "span,growx,wrap 5");
		panel.add(new JLabel("Active"), "span 1, gaptop 2");
		panel.add(new JLabel("Name"), "span 1, gapright 4, wrap");
		this.listRules();
		panel.repaint();
		frame.pack();
		// dialog.pack();
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getItem() instanceof JCheckBox) {
			JCheckBox box = (JCheckBox) e.getItem();
			int i = Integer.parseInt(box.getActionCommand());
			// System.out.println(i);
			if (i >= 0) {
				rules.get(i).setActive(box.isSelected());
			}
		}
	}
}
