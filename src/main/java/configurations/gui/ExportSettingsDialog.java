package configurations.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import configurations.SettingsManager;
import io.image.ComponentImageWriter;
import net.miginfocom.swing.MigLayout;

public class ExportSettingsDialog {
	private final SettingsManager settings = SettingsManager.getInstance();

	private final JPanel panel;
	private final JCheckBox svgClipPath;
	private final JCheckBox pdfClipPath;

	private JComboBox<String> formats;
	private List<String> formatList;

	public ExportSettingsDialog() {
		formatList = new ArrayList<>();
		formatList.add(ComponentImageWriter.IMAGE_TYPE_PNG);
		formatList.add(ComponentImageWriter.IMAGE_TYPE_SVG);
		formatList.add(ComponentImageWriter.IMAGE_TYPE_PDF);

		MigLayout layout = new MigLayout("", "[left]");

		panel = new JPanel(layout);

		panel.add(new JLabel("Specify deletion of clip paths during image export"), "span 2");
		panel.add(new JSeparator(), "span, growx, wrap 5, gaptop 10, gap 5");

		svgClipPath = new JCheckBox();
		svgClipPath.setSelected(settings.isSVGClipPaths());
		svgClipPath.setText("SVG clip paths:");
		svgClipPath.setHorizontalTextPosition(SwingConstants.LEFT);

		panel.add(svgClipPath, "wrap");

		pdfClipPath = new JCheckBox();
		pdfClipPath.setSelected(settings.isPDFClipPaths());
		pdfClipPath.setText("PDF clip paths:");
		pdfClipPath.setHorizontalTextPosition(SwingConstants.LEFT);
		panel.add(pdfClipPath, "wrap");

		panel.add(new JSeparator(), "span 3, growx, wrap 5, gaptop 10, gap 5");

		panel.add(new JLabel("Default file format for image export:"), "span 2");
		// panel.add(new JSeparator(), "span, growx, wrap 5, gaptop 10, gap 5");

		formats = new JComboBox<>();
		for (String s : formatList) {
			formats.addItem(s);
		}
		formats.setSelectedIndex(formatList.indexOf(settings.getDefaultImageExportFormat()));
		panel.add(formats, "wrap");
	}

	public JPanel getPanel() {
		return panel;
	}

	public boolean applyDefaults() {
		svgClipPath.setSelected(false);
		pdfClipPath.setSelected(false);
		formats.setSelectedIndex(0);
		return true;
	}

	public boolean applyNewSettings() {
		settings.setSVGClipPaths(svgClipPath.isSelected());
		settings.setPDFClipPaths(pdfClipPath.isSelected());
		settings.setDefaultImageExportFormat(formatList.get(formats.getSelectedIndex()));
		return true;
	}
}
