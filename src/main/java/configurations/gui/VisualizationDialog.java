package configurations.gui;

import java.awt.Font;
import java.awt.event.ItemEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import io.SuffixAwareFilter;
import org.yaml.snakeyaml.Yaml;

import biologicalElements.Elementdeclerations;
import configurations.ConnectionSettings;
import gui.MainWindow;
import gui.PopUpDialog;
import gui.visualization.YamlToObjectParser;
import io.SaveDialog;
import net.miginfocom.swing.MigLayout;

public class VisualizationDialog {
	public static final String DEFAULTYAML = "defaultYaml";
	private static final String[] shapes = { "ellipse", "triangle", "rectangle", "rounded rectangle", "pentagon",
											 "hexagon", "octagon", "5 star", "6 star", "7 star", "8 star", };
	private static final String[] size = { "0.5", "1.0", "2.0" };

	private JPanel panel;
	private JLabel loadedYamlLabel;
	private final JComboBox<String> biologicalElementsBox;
	private final JComboBox<String> shapeBox;
	private final JComboBox<String> sizeMultiplierBox;
	private final JColorChooser colorChooser;
	private String loadedYaml = null;
	private MainWindow mWindow;

	public VisualizationDialog() {
		Elementdeclerations elementdeclerations = new Elementdeclerations();
		List<String> biologicalElements = elementdeclerations.getNotPNNodeDeclarations();

		MigLayout layout = new MigLayout("", "[][grow]");

		panel = new JPanel();
		panel.setLayout(layout);
		panel.add(new JLabel("Set new defaults:"));
		panel.add(new JSeparator(), "span, growx, wrap");

		panel.add(new JLabel("Element"));
		biologicalElementsBox = new JComboBox<>();
		biologicalElementsBox.setModel(
				new DefaultComboBoxModel<>(biologicalElements.toArray(new String[biologicalElements.size()])));

		panel.add(biologicalElementsBox, "span 2, right, growx, wrap");

		panel.add(new JLabel("Shape"));
		shapeBox = new JComboBox<>(shapes);
		panel.add(shapeBox, "span, wrap, growx");

		panel.add(new JLabel("Size"));
		sizeMultiplierBox = new JComboBox<>(size);
		sizeMultiplierBox.setSelectedIndex(1);
		panel.add(sizeMultiplierBox, "span, growx, wrap");

		panel.add(new JLabel("Color"), "top");
		colorChooser = new JColorChooser();
		AbstractColorChooserPanel[] multipleChooserPanel = colorChooser.getChooserPanels();
		for (AbstractColorChooserPanel accp : multipleChooserPanel) {
			if (accp != multipleChooserPanel[2]) {
				colorChooser.removeChooserPanel(accp);
			}
		}
		AbstractColorChooserPanel[] singleChooserPanel = colorChooser.getChooserPanels();
		AbstractColorChooserPanel finalChooserPanel = singleChooserPanel[0];
		panel.add(finalChooserPanel, "grow, center, wrap");
		panel.add(colorChooser.getPreviewPanel(), "span, right, gapright 20, wrap");

		mWindow = MainWindow.getInstance();

		JLabel labelButton = new JLabel("Configurationfile(.yaml): ");
		labelButton.setFont(labelButton.getFont().deriveFont(Font.BOLD));
		loadedYamlLabel = new JLabel();
		if (mWindow.getLoadedYaml() != null) {
			loadedYamlLabel = new JLabel(mWindow.getLoadedYaml());
		} else {
			loadedYamlLabel = new JLabel("YamlSource Error (isNull)");
			System.out.println("MainWindow YamlSource Null Error");
		}
		JButton loadYamlButton = new JButton("Load Yaml");
		loadYamlButton.addActionListener(e -> {
			if (e.getSource() == loadYamlButton) {
				JFileChooser fileChooser = new JFileChooser("Choose file");
				fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				FileNameExtensionFilter yamlFilter = new FileNameExtensionFilter("Yaml Files", "yml", "yaml");
				fileChooser.setFileFilter(yamlFilter);

				int dataChosen = fileChooser.showOpenDialog(null);

				if (dataChosen == JFileChooser.APPROVE_OPTION) {
					loadedYaml = fileChooser.getSelectedFile().getPath();
					loadedYamlLabel.setText(loadedYaml);
					mWindow.setLoadedYaml(loadedYaml);
					ConnectionSettings.getInstance().setYamlVisualizationFile(loadedYaml);
					PrintWriter pWriter = null;
					try {
						pWriter = new PrintWriter(new BufferedWriter(
								new FileWriter(new File("YamlSourceFile.txt").getAbsolutePath())));
						pWriter.println(loadedYaml);
						pWriter.println(
								"If you want to use your own YAML configuration file, THIS file has to be at the same location as the VANESA jar!");
					} catch (IOException e1) {
						System.out.println("YamlSource Writer Error");
						e1.printStackTrace();
					} finally {
						if (pWriter != null) {
							pWriter.flush();
							pWriter.close();
						}
					}
					shapeBox.setEnabled(true);
					sizeMultiplierBox.setEnabled(true);
					colorChooser.setEnabled(true);
				}
				fileChooser.setVisible(true);
			}
		});
		panel.add(labelButton, "gaptop 50, bot");
		panel.add(loadedYamlLabel, "left, gaptop 50, bot");
		panel.add(loadYamlButton, "span, right, gapleft 10, gaptop 50, bot");
		JLabel exportLabel = new JLabel(
				"To customize press export. The resulting file will take over all future customizations.");
		JLabel spacer = new JLabel("");
		JButton exportButton = new JButton("Export");
		exportButton.addActionListener(e -> {
			if (e.getSource() == exportButton) {
				new SaveDialog(new SuffixAwareFilter[]{SuffixAwareFilter.YAML},
						SaveDialog.DATA_TYPE_VISUALIZATION_SETTINGS);
				loadedYaml = MainWindow.getInstance().getLoadedYaml();
				loadedYamlLabel.setText(MainWindow.getInstance().getLoadedYaml());
				shapeBox.setEnabled(true);
				sizeMultiplierBox.setEnabled(true);
				colorChooser.setEnabled(true);
			}

		});
		panel.add(spacer, "gaptop 10, bot");
		panel.add(exportLabel, "gaptop 10, bot");
		panel.add(exportButton, "span, right, gapleft 10, gaptop 10, bot");

		Yaml yaml = new Yaml();
		if (mWindow.getLoadedYaml() != null) {
			loadedYaml = mWindow.getLoadedYaml();
		} else {
			System.out.println("Dialog NullError");
		}

		if (loadedYaml.equals(VisualizationDialog.DEFAULTYAML)) {
			shapeBox.setEnabled(false);
			sizeMultiplierBox.setEnabled(false);
			colorChooser.setEnabled(false);
		}

		biologicalElementsBox.addItemListener(event -> {
			if (event.getStateChange() == ItemEvent.DESELECTED) {
				yamlWriter(yaml, event.getItem().toString(), false);
			}
		});
	}

	public void yamlWriter(Yaml yaml, String selectedItem, boolean acceptMethodCall) {
		InputStream input;
		BufferedReader reader = null;
		if (loadedYaml != null) {
			if (loadedYaml.equals(VisualizationDialog.DEFAULTYAML)) {
				input = getClass().getClassLoader().getResourceAsStream("NodeProperties.yaml");
				reader = new BufferedReader(new InputStreamReader(input));
			} else {
				try {
					File file = new File(loadedYaml);
					if (file.exists()) {
						input = new FileInputStream(file);
						reader = new BufferedReader(new InputStreamReader(input));
					} else {
						System.out.println("Reading file Error in Dialog");
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}

			List<Object> docs = new LinkedList<>();
			for (Object data : yaml.loadAll(reader)) {
				Map<String, Map<String, Object>> object = (Map<String, Map<String, Object>>) data;
				String preKeyValue = object.keySet().toString();
				String keyValue = preKeyValue.substring(1, preKeyValue.length() - 1);
				if (!acceptMethodCall) {
					if (biologicalElementsBox.getSelectedItem().toString().equals(keyValue)) {
						if (!shapeBox.isEnabled() && !sizeMultiplierBox.isEnabled()) {
							shapeBox.setEnabled(true);
							sizeMultiplierBox.setEnabled(true);
							colorChooser.setEnabled(true);
						}
						shapeBox.setSelectedItem(object.get(keyValue).get("shape"));
						sizeMultiplierBox.setSelectedItem(object.get(keyValue).get("sizefactor").toString());
						colorChooser.setColor((int) object.get(keyValue).get("red"),
								(int) object.get(keyValue).get("green"), (int) object.get(keyValue).get("blue"));
						if (loadedYaml.equals(VisualizationDialog.DEFAULTYAML)) {
							shapeBox.setEnabled(false);
							sizeMultiplierBox.setEnabled(false);
							colorChooser.setEnabled(false);
						}
					}
				}

				if (selectedItem.equals(keyValue)) {
					object.get(selectedItem).put("shape", shapeBox.getSelectedItem());
					object.get(selectedItem).put("sizefactor",
												 Double.parseDouble((String) sizeMultiplierBox.getSelectedItem()));
					object.get(selectedItem).put("red", colorChooser.getColor().getRed());
					object.get(selectedItem).put("green", colorChooser.getColor().getGreen());
					object.get(selectedItem).put("blue", colorChooser.getColor().getBlue());
				}
				docs.add(object);
			}

			Writer writer = null;
			if (loadedYaml != null) {
				if (!loadedYaml.equals(VisualizationDialog.DEFAULTYAML)) {
					try {
						writer = new FileWriter(loadedYaml);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					yaml.dumpAll(docs.iterator(), writer);
				}
			} else {
				PopUpDialog.getInstance().show("Error", "No configuration file is loaded!");
			}
		} else {
			PopUpDialog.getInstance().show("Error", "No configuration file is loaded!");
		}
	}

	public void acceptConfig() {
		Yaml yaml = new Yaml();
		yamlWriter(yaml, biologicalElementsBox.getSelectedItem().toString(), true);
		YamlToObjectParser yamlToObjectParser = new YamlToObjectParser(loadedYaml);
		yamlToObjectParser.acceptConfig();
	}

	public void setDefaultYamlPath() {
		String yamlSourceFile = new File("YamlSourceFile.txt").getAbsolutePath();
		File file = new File(yamlSourceFile);
		if (file.exists()) {
			file.delete();
		}
		mWindow = MainWindow.getInstance();
		mWindow.setLoadedYaml(VisualizationDialog.DEFAULTYAML);
		loadedYaml = VisualizationDialog.DEFAULTYAML;
		loadedYamlLabel.setText(loadedYaml);
		shapeBox.setEnabled(false);
		sizeMultiplierBox.setEnabled(false);
		colorChooser.setEnabled(false);
	}

	public JPanel getPanel() {
		return panel;
	}

	public void setPanel(JPanel panel) {
		this.panel = panel;
	}
}