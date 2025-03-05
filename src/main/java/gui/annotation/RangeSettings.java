package gui.annotation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import configurations.gui.LayoutConfig;
import graph.GraphInstance;
import net.miginfocom.swing.MigLayout;
import util.MyColorChooser;

/**
 * @author dao
 */
public class RangeSettings extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	private MyAnnotation rangeInfo;
	private JTextField jTitle = new JTextField();
	private JButton fillColor = new JButton("...");
	private JButton outlineColor = new JButton("...");
	private JButton textColor = new JButton("...");
	private JComboBox<String> titlePos = new JComboBox<String>(
			new String[] { "leftTop", "rightTop", "leftBottom", "rightBottom" });
	private JComboBox<String> outlineType = new JComboBox<String>(
			new String[] { "none", "single line", "double line" });
	private JComboBox<Integer> layerCombo = new JComboBox<Integer>();
	private JButton applyChange = new JButton("OK"), cancel = new JButton("Cancel");
	public int layer;
	private JSpinner alpha = new JSpinner();

	public RangeSettings() {
		applyChange.addActionListener(this);
		cancel.addActionListener(this);
		fillColor.addActionListener(this);
		outlineColor.addActionListener(this);
		textColor.addActionListener(this);
		alpha.setModel(new SpinnerNumberModel(255, 0, 255, 1));
		MigLayout layout = new MigLayout("insets 20", "[left, grow 70]20[left, grow 30, fill]", "");
		this.setLayout(layout);
		this.add(new JLabel("title:"), "cell 0 0");
		this.add(jTitle, "cell 1 0");
		this.add(new JLabel("fillColor:"), "cell 0 1");
		this.add(fillColor, "cell 1 1");
		this.add(new JLabel("alpha:"), "cell 2 1");
		this.add(alpha, "cell 3 1");
		this.add(new JLabel("outlineColor:"), "cell 0 2");
		this.add(outlineColor, "cell 1 2");
		this.add(new JLabel("textColor:"), "cell 0 3");
		this.add(textColor, "cell 1 3");
		this.add(new JLabel("title position:"), "cell 0 4");
		this.add(titlePos, "cell 1 4");
		this.add(new JLabel("outline type:"), "cell 0 5");
		this.add(outlineType, "cell 1 5");
		this.add(new JLabel("layer:"), "cell 0 6");
		this.add(layerCombo, "cell 1 6");
	}

	public int showDialog() {
		int option = JOptionPane.showOptionDialog(GraphInstance.getMyGraph().getVisualizationViewer(), this,
				"Layout settings", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,
				new JButton[] { applyChange, cancel }, applyChange);
		return option;
	}

	public void loadSettings(MyAnnotation info, int atLayer, int layerCount) {
		this.rangeInfo = info;
		jTitle.setText(info.getText());
		alpha.setValue(info.getAlpha());
		fillColor.setBackground(info.getFillColor());
		outlineColor.setBackground(info.getOutlineColor());
		textColor.setBackground(info.getTextColor());
		titlePos.setSelectedIndex(info.getTitlePos());
		outlineType.setSelectedIndex(info.getOutlineType());
		layerCombo.removeAllItems();
		for (int i = 0; i < layerCount; i++) {
			layerCombo.addItem(i);
		}
		layerCombo.setSelectedIndex(atLayer);
	}

	public void updateSettings() {
		rangeInfo.setText(jTitle.getText());
		rangeInfo.setFillColor(fillColor.getBackground());
		rangeInfo.setOutlineColor(outlineColor.getBackground());
		rangeInfo.setTextColor(textColor.getBackground());
		rangeInfo.setOutlineType(outlineType.getSelectedIndex());
		rangeInfo.setTitlePos(titlePos.getSelectedIndex());
		rangeInfo.setAlpha((Integer) alpha.getValue());
		this.layer = layerCombo.getSelectedIndex();
	}

	public void actionPerformed(ActionEvent e) {
		final JOptionPane pane = LayoutConfig.getOptionPane(this);
		if (e.getSource() == this.applyChange) {
			this.updateSettings();
			pane.setValue(e.getSource());
		} else if (e.getSource() == this.cancel) {
			pane.setValue(e.getSource());
		} else if (e.getSource() == this.fillColor || e.getSource() == this.outlineColor
				|| e.getSource() == this.textColor) {
			JButton button = (JButton) e.getSource();
			MyColorChooser mc = new MyColorChooser(GraphInstance.getMyGraph().getVisualizationViewer(), "Choose color",
					true, button.getBackground());
			if (mc.isOkAction()) {
				button.setBackground(mc.getColor());
			}

		}
	}

}
