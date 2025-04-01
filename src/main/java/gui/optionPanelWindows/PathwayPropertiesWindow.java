package gui.optionPanelWindows;

import java.awt.Color;
import java.awt.event.ActionEvent;
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
import graph.compartment.Compartment;
import graph.jung.classes.MyVisualizationViewer;
import gui.MainWindow;
import gui.PopUpDialog;
import gui.visualization.CompartmentRenderer;
import net.miginfocom.swing.MigLayout;
import util.MyColorChooser;
import util.VanesaUtility;

public class PathwayPropertiesWindow extends JPanel implements ItemListener {
	/**
	 * Regular expression to check for valid compartment names. Compartment name must start with a letter or dash and
	 * may only contain letters, digites, and dash symbol (SBML L3V1, reference section 3.1.7)
	 */
	private static final Pattern COMPARTMENT_NAME_PATTERN = Pattern.compile("^[a-zA-Z-][a-zA-Z\\d-]*$");

	private JCheckBox drawCompartments;
	private JCheckBox drawCompartmentsExperimental;
	private JTextField name;
	private JButton color;
	private Pathway pw;
	private CompartmentRenderer compRenderer = null;

	public PathwayPropertiesWindow() {
		setLayout(new MigLayout("ins 0, wrap 3, fill", "[grow][][]"));
		setVisible(false);
	}

	public void removeAllElements() {
		removeAll();
		setVisible(false);
	}

	public void revalidateView() {
		removeAll();
		pw = GraphInstance.getPathway();
		if (compRenderer == null) {
			compRenderer = new CompartmentRenderer(pw);
		} else {
			compRenderer.setPathway(pw);
		}
		drawCompartments = new JCheckBox("draw compartments");
		if (pw.getCompartmentManager().isDrawCompartments()) {
			drawCompartments.setSelected(true);
		}
		drawCompartments.setActionCommand("drawCompartments");
		drawCompartments.addItemListener(this);
		add(drawCompartments, "growx, span 3");

		drawCompartmentsExperimental = new JCheckBox("draw experimental");
		drawCompartmentsExperimental.setActionCommand("drawCompartmentsExperimental");
		drawCompartmentsExperimental.addItemListener(this);
		drawCompartmentsExperimental.setEnabled(false);
		add(drawCompartmentsExperimental, "growx, span 3");

		final JButton createDefault = new JButton("create default");
		createDefault.addActionListener(e -> createDefaultCompartments());
		add(createDefault, "growx, span 3");

		add(new JSeparator(), "growx, span 3");
		add(new JLabel("Add new compartment:"), "growx, span 3");
		name = new JTextField(10);
		add(name, "growx");
		color = new JButton("color");
		color.setBackground(new Color(125, 125, 125));
		color.setToolTipText("Select fill color");
		color.addActionListener(this::chooseAddCompartmentColor);
		add(color, "width 60:60:60");

		final JButton add = new JButton("add");
		add.setBackground(VanesaUtility.POSITIVE_COLOR);
		add.addActionListener(e -> addCompartment());
		add(add, "width 60:60:60");

		add(new JSeparator(), "growx, span 3");
		drawList();
		revalidate();
		setVisible(true);
	}

	private void drawList() {
		final List<Compartment> compartments = pw.getCompartmentManager().getAllCompartmentsAlphabetically();
		for (final Compartment c : compartments) {
			add(new JLabel(c.getName()), "growx");
			final JButton color = new JButton("color");
			color.setBackground(c.getColor());
			color.addActionListener(e -> {
				final JButton b = ((JButton) e.getSource());
				final MyColorChooser mc = new MyColorChooser(MainWindow.getInstance().getFrame(), "Choose color", true,
						b.getBackground());
				if (mc.isOkAction()) {
					b.setBackground(mc.getColor());
					c.setColor(mc.getColor());
				}
			});
			add(color, "width 60:60:60");
			final JButton del = new JButton("delete");
			del.setBackground(VanesaUtility.NEGATIVE_COLOR);
			del.setActionCommand("del_" + c.getName());
			del.addActionListener(e -> {
				pw.getCompartmentManager().remove(c);
				revalidateView();
			});
			add(del, "width 60:60:60");
		}
	}

	private void createDefaultCompartments() {
		pw.getCompartmentManager().addDefaultCompartments();
		revalidateView();
	}

	private void chooseAddCompartmentColor(final ActionEvent e) {
		final JButton b = ((JButton) e.getSource());
		final MyColorChooser mc = new MyColorChooser(MainWindow.getInstance().getFrame(), "Choose color", true,
				b.getBackground());
		if (mc.isOkAction()) {
			b.setBackground(mc.getColor());
		}
	}

	private void addCompartment() {
		final String cName = name.getText().trim();
		if (cName.isEmpty()) {
			PopUpDialog.getInstance().show("Error", "Name of new compartment must not be empty!");
		} else {
			final Matcher matcher = COMPARTMENT_NAME_PATTERN.matcher(cName);
			if (matcher.find()) {
				if (pw.getCompartmentManager().getCompartment(cName) == null) {
					pw.getCompartmentManager().add(new Compartment(cName, color.getBackground()));
					this.revalidateView();
				} else {
					PopUpDialog.getInstance().show("Error", "Name of new compartment is already in use!");
				}
			} else {
				PopUpDialog.getInstance().show("Error",
						"Name of new compartment may only contain the following characters:\r\n [a-z], [A-Z], [0-9], [-], and must start with a letter or the dash symbol!");
			}
		}
	}

	@Override
	public void itemStateChanged(final ItemEvent e) {
		final MyVisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv = pw.getGraph()
				.getVisualizationViewer();
		if (e.getSource().equals(drawCompartments)) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				vv.addPreRenderPaintable(compRenderer);
				pw.getCompartmentManager().setDrawCompartments(true);
			} else {
				vv.removePreRenderPaintable(compRenderer);
				pw.getCompartmentManager().setDrawCompartments(false);
			}
			vv.repaint();
		} else if (e.getSource().equals(drawCompartmentsExperimental)) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				// vv.setEsperimentalCompartments(true);
			} else {
				// vv.setEsperimentalCompartments(false);
			}
		}
	}
}
