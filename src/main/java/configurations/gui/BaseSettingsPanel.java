package configurations.gui;

import configurations.Settings;
import gui.ImagePath;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public abstract class BaseSettingsPanel extends JPanel {
	protected final ImageIcon infoImage = ImagePath.getInstance().getImageIcon("infoButton.png");
	protected final ImageIcon resetImage = ImagePath.getInstance().getImageIcon("reset.svg", 20, 20);
	protected final JPanel contentPanel = new JPanel(new MigLayout("ins 0, fillx, wrap"));

	public BaseSettingsPanel() {
		super(new MigLayout("ins 0, fill, wrap"));
		final JScrollPane scrollPane = new JScrollPane(contentPanel);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		add(scrollPane, "grow");
	}

	/**
	 * Add a header row with a specified label dividing sections of settings
	 */
	protected void addHeader(final String label) {
		final JPanel header = new JPanel(new MigLayout("left"));
		header.setBackground(new Color(164, 164, 164));
		final JLabel headerLabel = new JLabel(label);
		headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD));
		header.add(headerLabel);
		contentPanel.add(header, "growx");
	}

	protected void addSetting(final String label, final String help, final JComponent component,
			final Runnable resetCallback) {
		final JPanel row = new JPanel(new MigLayout("fill", "[250:250:250][grow][][]"));
		row.setBackground(new Color(200, 200, 200));
		row.add(new JLabel(label), "growx");
		row.add(component, "grow");
		final JLabel helpLabel = new JLabel(infoImage);
		helpLabel.setToolTipText(help);
		row.add(helpLabel);
		final JButton resetButton = new JButton(resetImage);
		resetButton.setBackground(new Color(200, 200, 200));
		resetButton.setFocusPainted(false);
		resetButton.setBorder(BorderFactory.createEmptyBorder());
		resetButton.addActionListener((e) -> resetCallback.run());
		resetButton.setToolTipText("Reset to default");
		row.add(resetButton);
		contentPanel.add(row, "growx");
	}

	public abstract boolean applySettings();

	public abstract void updateSettings(final Settings settings);
}
