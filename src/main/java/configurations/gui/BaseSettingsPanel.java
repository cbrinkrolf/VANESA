package configurations.gui;

import gui.ImagePath;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public abstract class BaseSettingsPanel extends JPanel {
	protected final ImageIcon infoImage = ImagePath.getInstance().getImageIcon("infoButton.png");
	protected final JPanel contentPanel = new JPanel(new MigLayout("ins 0, fillx, wrap"));

	public BaseSettingsPanel() {
		super(new MigLayout("ins 0, fill, wrap"));
		final JScrollPane scrollPane = new JScrollPane(contentPanel);
		add(scrollPane, "grow");
	}

	protected void addHeader(final String label) {
		final JPanel header = new JPanel(new MigLayout("left"));
		header.setBackground(new Color(164, 164, 164));
		final JLabel headerLabel = new JLabel(label);
		headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD));
		header.add(headerLabel);
		contentPanel.add(header, "growx");
	}

	protected void addSetting(final String label, final String help, final JComponent component) {
		final JPanel row = new JPanel(new MigLayout("fill", "[250:250:250][grow][]"));
		row.setBackground(new Color(200, 200, 200));
		row.add(new JLabel(label), "growx");
		row.add(component, "grow");
		final JLabel helpLabel = new JLabel(infoImage);
		helpLabel.setToolTipText(help);
		row.add(helpLabel);
		contentPanel.add(row, "growx");
	}

	public abstract boolean applyDefaults();

	public abstract boolean applyNewSettings();
}
