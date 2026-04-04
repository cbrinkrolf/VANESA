package gui.visualization;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import configurations.gui.VisualizationDialog;
import gui.PopUpDialog;
import gui.visualization.VisualizationConfigBeans.Bean;

public class YamlToObjectParser {

	private List<Bean> beansList = new ArrayList<>();
	private String loadedYaml;
	private Yaml yaml;
	private VisualizationConfigBeans bean;

	public YamlToObjectParser(String loadedYaml) {
		this.loadedYaml = loadedYaml;
	}

	public void defaultCase() {

		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(getClass().getClassLoader().getResourceAsStream("NodeProperties.yaml")))) {
			Map<String, Map<String, Object>> mapForBeans = new HashMap<>();
			for (Object data : yaml.loadAll(reader)) {
				HashMap<String, Map<String, Object>> object;
				object = (HashMap<String, Map<String, Object>>) data;
				mapForBeans.put(object.keySet().toString().substring(1, object.keySet().toString().length() - 1),
						object.get(object.keySet().toString().substring(1, object.keySet().toString().length() - 1)));
			}
			bean = new VisualizationConfigBeans();
			beansList = bean.parseAndAdjust(mapForBeans, false);
		} catch (IOException e1) {
			System.out.println("Input- or readerstream error in YamlToObjectParser");
			e1.printStackTrace();
		}
	}

	public List<Bean> startConfig() {
		yaml = new Yaml();
		if (loadedYaml.equals(VisualizationDialog.DEFAULTYAML)) {
			defaultCase();
		} else {
			File file = new File(loadedYaml);
			if (file.exists()) {
				System.out.println("Found YamlSource. Default inactive.");
				try (BufferedReader reader = new BufferedReader(
						new InputStreamReader(new FileInputStream(loadedYaml)))) {

					HashMap<String, Map<String, Object>> mapForBeans = new HashMap<>();
					for (Object data : yaml.loadAll(reader)) {
						HashMap<String, Map<String, Object>> object;
						object = (HashMap<String, Map<String, Object>>) data;
						mapForBeans.put(
								object.keySet().toString().substring(1, object.keySet().toString().length() - 1),
								object.get(object.keySet().toString().substring(1,
										object.keySet().toString().length() - 1)));
					}
					bean = new VisualizationConfigBeans();
					beansList = bean.parseAndAdjust(mapForBeans, false);

				} catch (Exception e) {
					System.out.println("Yaml reading error in YamlToObjectParser");
					e.printStackTrace();
					defaultCase();
				}
			} else {
				loadedYaml = VisualizationDialog.DEFAULTYAML;
				defaultCase();
			}
		}
		return beansList;
	}

	public void acceptConfig() {
		yaml = new Yaml();
		InputStream input = null;
		if (loadedYaml != null) {
			if (loadedYaml.equals(VisualizationDialog.DEFAULTYAML)) {
				input = getClass().getClassLoader().getResourceAsStream("NodeProperties.yaml");
			} else {
				try {
					input = new FileInputStream(new File(loadedYaml));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				Map<String, Map<String, Object>> mapForBeans = new HashMap<>();
				for (Object data : yaml.loadAll(input)) {
					HashMap<String, Map<String, Object>> object;
					object = (HashMap<String, Map<String, Object>>) data;
					mapForBeans.put(object.keySet().toString().substring(1, object.keySet().toString().length() - 1),
							object.get(
									object.keySet().toString().substring(1, object.keySet().toString().length() - 1)));
				}
				bean = new VisualizationConfigBeans();
				bean.parseAndAdjust(mapForBeans, true);
			}
		} else {
			PopUpDialog.getInstance().show("Error", "YtOpACCEPT: No configuration file is loaded!");
			// JOptionPane.showMessageDialog(panelOrPane, "YtOpACCEPT: No
			// configuration file is loaded!");
		}
	}
}
