package gui.visualization;

import java.awt.Component;
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
import gui.MyPopUp;
import gui.visualization.VisualizationConfigBeans.Bean;

public class YamlToObjectParser {

	private List<Bean> beansList = new ArrayList<Bean>();
	private String loadedYaml;
	private Yaml yaml;
	private VisualizationConfigBeans bean;

	public YamlToObjectParser(String loadedYaml) {
		this.loadedYaml = loadedYaml;
	}

	public void defaultCase() {
		InputStream input = getClass().getClassLoader().getResourceAsStream("resource/NodeProperties.yaml");
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		HashMap<String, Map<String, Object>> mapForBeans = new HashMap<String, Map<String, Object>>();
		for (Object data : yaml.loadAll(reader)) {
			HashMap<String, Map<String, Object>> object;
			object = (HashMap<String, Map<String, Object>>) data;
			mapForBeans.put(object.keySet().toString().substring(1, object.keySet().toString().length() - 1),
					object.get(object.keySet().toString().substring(1, object.keySet().toString().length() - 1)));
		}
		bean = new VisualizationConfigBeans();
		beansList = bean.parseAndAdjust(mapForBeans, false);

		try {
			reader.close();
			input.close();
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
				InputStream input = null;
				try {
					input = new FileInputStream(loadedYaml);

					BufferedReader reader = new BufferedReader(new InputStreamReader(input));
					HashMap<String, Map<String, Object>> mapForBeans = new HashMap<String, Map<String, Object>>();
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

					try {
						reader.close();
						input.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
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
				input = getClass().getClassLoader().getResourceAsStream("resource/NodeProperties.yaml");
			} else {
				try {
					input = new FileInputStream(new File(loadedYaml));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				HashMap<String, Map<String, Object>> mapForBeans = new HashMap<String, Map<String, Object>>();
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
			MyPopUp.getInstance().show("Error", "YtOpACCEPT: No configuration file is loaded!");
			// JOptionPane.showMessageDialog(panelOrPane, "YtOpACCEPT: No
			// configuration file is loaded!");
		}
	}
}
